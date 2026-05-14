<template>
  <div class="warehouse-3d-container">
    <!-- 顶部导航栏 -->
    <header class="modern-header">
      <div class="header-left">
        <div class="logo-area">
          <i class="el-icon-box"></i>
          <span class="app-title">智能仓储可视化系统</span>
        </div>
      </div>
      
      <div class="header-center">
        <el-select 
          v-model="selectedWarehouse" 
          placeholder="选择仓库" 
          size="small"
          class="warehouse-selector"
          @change="loadWarehouseData"
        >
          <el-option
            v-for="wh in warehouseList"
            :key="wh.id"
            :label="wh.name"
            :value="wh.id"
          />
        </el-select>
        
        <div class="quick-stats">
          <div class="stat-item">
            <span class="stat-label">货架总数</span>
            <span class="stat-value">{{ totalShelves }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">仓位总数</span>
            <span class="stat-value">{{ totalPositions }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">利用率</span>
            <span class="stat-value usage-rate">{{ usageRate }}%</span>
          </div>
        </div>
      </div>
      
      <div class="header-right">
        <el-button-group size="small">
          <el-button 
            :type="viewMode === 'solid' ? 'primary' : 'info'" 
            icon="el-icon-crop"
            @click="setViewMode('solid')"
          >实体</el-button>
          <el-button 
            :type="viewMode === 'wireframe' ? 'primary' : 'info'" 
            icon="el-icon-grid"
            @click="setViewMode('wireframe')"
          >线框</el-button>
        </el-button-group>
        
        <el-button 
          size="small" 
          type="success" 
          icon="el-icon-refresh"
          @click="resetCamera"
        >重置视角</el-button>
        
        <el-button 
          size="small" 
          type="warning" 
          icon="el-icon-download"
          @click="exportData"
        >导出</el-button>
      </div>
    </header>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 左侧控制面板 -->
      <aside class="control-panel left-panel">
        <div class="panel-section">
          <h3 class="panel-title">
            <i class="el-icon-filter"></i>
            状态筛选
          </h3>
          <div class="filter-group">
            <el-checkbox-group v-model="statusFilters" @change="applyFilters">
              <el-checkbox 
                label="1" 
                class="status-checkbox available"
              >
                <span class="status-dot available"></span>
                空闲
              </el-checkbox>
              <el-checkbox 
                label="2" 
                class="status-checkbox partial"
              >
                <span class="status-dot partial"></span>
                部分占用
              </el-checkbox>
              <el-checkbox 
                label="3" 
                class="status-checkbox full"
              >
                <span class="status-dot full"></span>
                已满
              </el-checkbox>
              <el-checkbox 
                label="0" 
                class="status-checkbox disabled"
              >
                <span class="status-dot disabled"></span>
                不可用
              </el-checkbox>
            </el-checkbox-group>
          </div>
        </div>

        <div class="panel-section">
          <h3 class="panel-title">
            <i class="el-icon-sort"></i>
            排序方式
          </h3>
          <el-radio-group v-model="sortOption" size="small" @change="applySort">
            <el-radio-button label="default">默认</el-radio-button>
            <el-radio-button label="usage">利用率</el-radio-button>
            <el-radio-button label="capacity">容量</el-radio-button>
          </el-radio-group>
        </div>

        <div class="panel-section">
          <h3 class="panel-title">
            <i class="el-icon-data-analysis"></i>
            统计图表
          </h3>
          <div class="chart-container">
            <div class="mini-chart">
              <div class="chart-bar" :style="{ width: usageRate + '%' }"></div>
            </div>
            <div class="chart-legend">
              <span>仓位使用率：{{ usageRate }}%</span>
            </div>
          </div>
        </div>
      </aside>

      <!-- 中央3D画布 -->
      <main class="canvas-container">
        <div ref="threeContainer" class="three-canvas"></div>
        
        <!-- 悬浮操作提示 -->
        <div class="canvas-overlay">
          <div class="overlay-tip">
            <i class="el-icon-info"></i>
            <span>左键旋转 · 右键平移 · 滚轮缩放 · 点击查看详情</span>
          </div>
        </div>
      </main>

      <!-- 右侧详情面板 -->
      <aside class="control-panel right-panel">
        <div v-if="selectedItem" class="detail-panel">
          <div class="detail-header">
            <h3 class="detail-title">
              <i :class="selectedItem.type === 'shelf' ? 'el-icon-s-grid' : 'el-icon-box'"></i>
              {{ selectedItem.type === 'shelf' ? '货架详情' : '仓位详情' }}
            </h3>
            <el-button 
              size="mini" 
              icon="el-icon-close" 
              circle 
              @click="clearSelection"
            ></el-button>
          </div>
          
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="编号">
              {{ selectedItem.code }}
            </el-descriptions-item>
            <el-descriptions-item label="名称">
              {{ selectedItem.name }}
            </el-descriptions-item>
            <el-descriptions-item 
              v-if="selectedItem.type === 'shelf'" 
              label="位置"
            >
              第{{ selectedItem.row_no }}排 - 第{{ selectedItem.column_no }}列
            </el-descriptions-item>
            <el-descriptions-item 
              v-if="selectedItem.type === 'position'" 
              label="位置"
            >
              货架{{ selectedItem.shelf_no }} - 
              层{{ selectedItem.row_no }} - 
              列{{ selectedItem.column_no }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(selectedItem.status)" size="small">
                {{ getStatusText(selectedItem.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item 
              v-if="selectedItem.type === 'position'" 
              label="容量"
            >
              {{ selectedItem.current_load || 0 }} / {{ selectedItem.max_capacity }}
            </el-descriptions-item>
            <el-descriptions-item 
              v-if="selectedItem.type === 'position'" 
              label="使用率"
            >
              <el-progress 
                :percentage="calculateUsageRate(selectedItem)" 
                :status="getProgressStatus(selectedItem)"
                :stroke-width="8"
              ></el-progress>
            </el-descriptions-item>
          </el-descriptions>
          
          <div class="detail-actions">
            <el-button 
              type="primary" 
              size="small" 
              block
              icon="el-icon-view"
            >
              查看库存
            </el-button>
            <el-button 
              type="success" 
              size="small" 
              block
              icon="el-icon-edit"
            >
              编辑信息
            </el-button>
          </div>
        </div>
        
        <div v-else class="empty-state">
          <i class="el-icon-pointer"></i>
          <p>点击货架或仓位查看详情</p>
        </div>
      </aside>
    </div>

    <!-- 底部状态栏 -->
    <footer class="status-bar">
      <div class="status-left">
        <span class="status-indicator">
          <i class="el-icon-connection"></i>
          系统运行正常
        </span>
        <span class="divider">|</span>
        <span>最后更新：{{ lastUpdateTime }}</span>
      </div>
      <div class="status-right">
        <span>渲染模式：{{ viewMode === 'solid' ? '实体' : '线框' }}</span>
        <span class="divider">|</span>
        <span>FPS: {{ fps }}</span>
      </div>
    </footer>
  </div>
</template>

<script>
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';

export default {
  name: 'Warehouse3D',
  data() {
    return {
      // 数据相关
      warehouseList: [],
      selectedWarehouse: null,
      shelves: [],
      positions: [],
      
      // 筛选和排序
      statusFilters: ['1', '2', '3', '0'],
      sortOption: 'default',
      
      // 视图控制
      viewMode: 'solid',
      
      // 选中项
      selectedItem: null,
      
      // Three.js相关
      scene: null,
      camera: null,
      renderer: null,
      controls: null,
      raycaster: null,
      mouse: null,
      highlightedObject: null,
      shelfMeshes: [],
      positionMeshes: [],
      
      // 统计信息
      totalShelves: 0,
      totalPositions: 0,
      usageRate: 0,
      
      // 其他
      lastUpdateTime: '',
      fps: 60,
      animationId: null
    };
  },
  
  mounted() {
    this.initThreeJS();
    this.loadWarehouses();
    this.updateTime();
    setInterval(this.updateTime, 60000);
    window.addEventListener('resize', this.onWindowResize);
  },
  
  beforeDestroy() {
    if (this.animationId) {
      cancelAnimationFrame(this.animationId);
    }
    window.removeEventListener('resize', this.onWindowResize);
    if (this.renderer) {
      this.renderer.dispose();
    }
  },
  
  methods: {
    // 初始化Three.js场景
    initThreeJS() {
      const container = this.$refs.threeContainer;
      
      // 创建场景
      this.scene = new THREE.Scene();
      this.scene.background = new THREE.Color(0x0a0a1a);
      this.scene.fog = new THREE.Fog(0x0a0a1a, 50, 200);
      
      // 创建相机
      this.camera = new THREE.PerspectiveCamera(
        60,
        container.clientWidth / container.clientHeight,
        0.1,
        1000
      );
      this.camera.position.set(30, 25, 30);
      this.camera.lookAt(0, 0, 0);
      
      // 创建渲染器
      this.renderer = new THREE.WebGLRenderer({ 
        antialias: true,
        alpha: true 
      });
      this.renderer.setSize(container.clientWidth, container.clientHeight);
      this.renderer.setPixelRatio(window.devicePixelRatio);
      this.renderer.shadowMap.enabled = true;
      this.renderer.shadowMap.type = THREE.PCFSoftShadowMap;
      container.appendChild(this.renderer.domElement);
      
      // 添加轨道控制器
      this.controls = new OrbitControls(this.camera, this.renderer.domElement);
      this.controls.enableDamping = true;
      this.controls.dampingFactor = 0.05;
      this.controls.minDistance = 10;
      this.controls.maxDistance = 100;
      this.controls.maxPolarAngle = Math.PI / 2.2;
      
      // 添加光源
      this.addLights();
      
      // 添加地面
      this.addGround();
      
      // 添加坐标轴辅助
      const axesHelper = new THREE.AxesHelper(5);
      this.scene.add(axesHelper);
      
      // 初始化射线检测
      this.raycaster = new THREE.Raycaster();
      this.mouse = new THREE.Vector2();
      
      // 添加事件监听
      this.renderer.domElement.addEventListener('mousemove', this.onMouseMove);
      this.renderer.domElement.addEventListener('click', this.onClick);
      
      // 开始动画循环
      this.animate();
    },
    
    // 添加光源
    addLights() {
      // 环境光
      const ambientLight = new THREE.AmbientLight(0x404040, 0.6);
      this.scene.add(ambientLight);
      
      // 方向光（主光源）
      const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
      directionalLight.position.set(50, 50, 50);
      directionalLight.castShadow = true;
      directionalLight.shadow.mapSize.width = 2048;
      directionalLight.shadow.mapSize.height = 2048;
      directionalLight.shadow.camera.near = 0.5;
      directionalLight.shadow.camera.far = 200;
      directionalLight.shadow.camera.left = -50;
      directionalLight.shadow.camera.right = 50;
      directionalLight.shadow.camera.top = 50;
      directionalLight.shadow.camera.bottom = -50;
      this.scene.add(directionalLight);
      
      // 补光
      const fillLight = new THREE.DirectionalLight(0x4040ff, 0.3);
      fillLight.position.set(-30, 20, -30);
      this.scene.add(fillLight);
      
      // 背光
      const backLight = new THREE.DirectionalLight(0xff4040, 0.2);
      backLight.position.set(30, 20, -30);
      this.scene.add(backLight);
    },
    
    // 添加地面
    addGround() {
      const groundGeometry = new THREE.PlaneGeometry(100, 100);
      const groundMaterial = new THREE.MeshStandardMaterial({
        color: 0x1a1a2e,
        roughness: 0.8,
        metalness: 0.2,
        side: THREE.DoubleSide
      });
      const ground = new THREE.Mesh(groundGeometry, groundMaterial);
      ground.rotation.x = -Math.PI / 2;
      ground.position.y = -0.1;
      ground.receiveShadow = true;
      this.scene.add(ground);
      
      // 添加网格辅助线
      const gridHelper = new THREE.GridHelper(100, 20, 0x444466, 0x222244);
      this.scene.add(gridHelper);
    },
    
    // 加载仓库列表
    async loadWarehouses() {
      try {
        // TODO: 替换为真实API调用
        // const response = await this.$api.get('/erp_ct_warehouse');
        // this.warehouseList = response.data;
        
        // 模拟数据
        this.warehouseList = [
          { id: 1, name: '一号仓库', code: 'WH001' },
          { id: 2, name: '二号仓库', code: 'WH002' },
          { id: 3, name: '三号仓库', code: 'WH003' }
        ];
        
        if (this.warehouseList.length > 0) {
          this.selectedWarehouse = this.warehouseList[0].id;
          this.loadWarehouseData();
        }
      } catch (error) {
        this.$message.error('加载仓库列表失败');
        console.error(error);
      }
    },
    
    // 加载仓库数据
    async loadWarehouseData() {
      try {
        // TODO: 替换为真实API调用
        // const shelvesResponse = await this.$api.get('/erp_ct_shelves', {
        //   params: { warehouse_id: this.selectedWarehouse }
        // });
        // const positionsResponse = await this.$api.get('/erp_ct_position', {
        //   params: { warehouse_id: this.selectedWarehouse }
        // });
        // this.shelves = shelvesResponse.data;
        // this.positions = positionsResponse.data;
        
        // 模拟数据生成
        this.generateMockData();
        
        // 清空现有模型
        this.clearScene();
        
        // 渲染货架和仓位
        this.renderShelves();
        this.renderPositions();
        
        // 更新统计信息
        this.updateStatistics();
        
        this.$message.success('数据加载成功');
      } catch (error) {
        this.$message.error('加载仓库数据失败');
        console.error(error);
      }
    },
    
    // 生成模拟数据
    generateMockData() {
      this.shelves = [];
      this.positions = [];
      
      // 生成货架数据（5排 x 4列）
      for (let row = 1; row <= 5; row++) {
        for (let col = 1; col <= 4; col++) {
          const shelf = {
            id: this.shelves.length + 1,
            code: `SH-${row.toString().padStart(2, '0')}-${col.toString().padStart(2, '0')}`,
            name: `货架${row}-${col}`,
            warehouse_id: this.selectedWarehouse,
            row_no: row,
            column_no: col,
            layers: 4,
            positions_of_layer: 3,
            status: 1
          };
          this.shelves.push(shelf);
          
          // 为每个货架生成仓位
          for (let layer = 1; layer <= shelf.layers; layer++) {
            for (let pos = 1; pos <= shelf.positions_of_layer; pos++) {
              const statusRand = Math.random();
              let status = 1;
              if (statusRand > 0.8) status = 3;
              else if (statusRand > 0.6) status = 2;
              else if (statusRand > 0.95) status = 0;
              
              const position = {
                id: this.positions.length + 1,
                code: `POS-${shelf.code}-${layer}-${pos}`,
                name: `仓位${layer}-${pos}`,
                warehouse_id: this.selectedWarehouse,
                area: `A${row}`,
                shelf_no: shelf.id,
                row_no: layer,
                column_no: pos,
                status: status,
                max_capacity: 100,
                current_load: status === 3 ? 100 : (status === 2 ? 50 : 0),
                order_no: this.positions.length + 1
              };
              this.positions.push(position);
            }
          }
        }
      }
    },
    
    // 清空场景
    clearScene() {
      // 移除所有货架网格
      this.shelfMeshes.forEach(mesh => {
        this.scene.remove(mesh);
        if (mesh.geometry) mesh.geometry.dispose();
        if (mesh.material) mesh.material.dispose();
      });
      this.shelfMeshes = [];
      
      // 移除所有仓位网格
      this.positionMeshes.forEach(mesh => {
        this.scene.remove(mesh);
        if (mesh.geometry) mesh.geometry.dispose();
        if (mesh.material) mesh.material.dispose();
      });
      this.positionMeshes = [];
    },
    
    // 渲染货架
    renderShelves() {
      const shelfWidth = 2;
      const shelfDepth = 1.5;
      const shelfHeight = 0.3;
      const layerHeight = 1.2;
      const gapBetweenShelves = 1.5;
      
      this.shelves.forEach((shelf, index) => {
        const totalHeight = shelf.layers * layerHeight + shelfHeight;
        const geometry = new THREE.BoxGeometry(shelfWidth, totalHeight, shelfDepth);
        
        const material = new THREE.MeshStandardMaterial({
          color: 0x4a5568,
          roughness: 0.7,
          metalness: 0.3,
          transparent: true,
          opacity: 0.9
        });
        
        const shelfMesh = new THREE.Mesh(geometry, material);
        
        // 计算位置
        const x = (shelf.column_no - 1) * (shelfWidth + gapBetweenShelves) - 5;
        const z = (shelf.row_no - 1) * (shelfDepth + gapBetweenShelves) - 5;
        const y = totalHeight / 2 - 0.5;
        
        shelfMesh.position.set(x, y, z);
        shelfMesh.castShadow = true;
        shelfMesh.receiveShadow = true;
        shelfMesh.userData = {
          type: 'shelf',
          data: shelf
        };
        
        this.scene.add(shelfMesh);
        this.shelfMeshes.push(shelfMesh);
        
        // 添加层板
        for (let i = 0; i <= shelf.layers; i++) {
          const layerGeo = new THREE.BoxGeometry(shelfWidth + 0.1, 0.05, shelfDepth + 0.1);
          const layerMat = new THREE.MeshStandardMaterial({
            color: 0x718096,
            roughness: 0.5,
            metalness: 0.4
          });
          const layerMesh = new THREE.Mesh(layerGeo, layerMat);
          layerMesh.position.set(0, i * layerHeight - totalHeight / 2 + shelfHeight / 2, 0);
          shelfMesh.add(layerMesh);
        }
      });
    },
    
    // 渲染仓位
    renderPositions() {
      const shelfWidth = 2;
      const shelfDepth = 1.5;
      const layerHeight = 1.2;
      const gapBetweenShelves = 1.5;
      
      this.positions.forEach((position, index) => {
        const shelf = this.shelves.find(s => s.id === position.shelf_no);
        if (!shelf) return;
        
        const positionsPerLayer = shelf.positions_of_layer;
        const slotWidth = shelfWidth / positionsPerLayer;
        const slotDepth = shelfDepth * 0.8;
        const slotHeight = layerHeight * 0.7;
        
        const geometry = new THREE.BoxGeometry(slotWidth * 0.9, slotHeight, slotDepth);
        
        const color = this.getStatusColor(position.status);
        const material = new THREE.MeshStandardMaterial({
          color: color,
          roughness: 0.6,
          metalness: 0.2,
          transparent: true,
          opacity: 0.85,
          emissive: color,
          emissiveIntensity: 0.2
        });
        
        const positionMesh = new THREE.Mesh(geometry, material);
        
        // 计算位置
        const shelfX = (shelf.column_no - 1) * (shelfWidth + gapBetweenShelves) - 5;
        const shelfZ = (shelf.row_no - 1) * (shelfDepth + gapBetweenShelves) - 5;
        const totalShelfHeight = shelf.layers * layerHeight + 0.3;
        
        const x = shelfX + (position.column_no - 0.5) * slotWidth - shelfWidth / 2;
        const z = shelfZ;
        const y = (position.row_no - 1) * layerHeight + slotHeight / 2 - totalShelfHeight / 2 + 0.3;
        
        positionMesh.position.set(x, y, z);
        positionMesh.castShadow = true;
        positionMesh.receiveShadow = true;
        positionMesh.userData = {
          type: 'position',
          data: position
        };
        
        this.scene.add(positionMesh);
        this.positionMeshes.push(positionMesh);
      });
    },
    
    // 获取状态颜色
    getStatusColor(status) {
      const colors = {
        '0': 0x718096, // 不可用 - 灰色
        '1': 0x4299e1, // 空闲 - 蓝色
        '2': 0x48bb78, // 部分占用 - 绿色
        '3': 0xf56565  // 已满 - 红色
      };
      return colors[status] || 0x718096;
    },
    
    // 获取状态文本
    getStatusText(status) {
      const texts = {
        '0': '不可用',
        '1': '空闲',
        '2': '部分占用',
        '3': '已满'
      };
      return texts[status] || '未知';
    },
    
    // 获取状态类型（用于Element UI标签）
    getStatusType(status) {
      const types = {
        '0': 'info',
        '1': 'success',
        '2': 'warning',
        '3': 'danger'
      };
      return types[status] || 'info';
    },
    
    // 计算使用率
    calculateUsageRate(position) {
      if (!position.max_capacity || position.max_capacity === 0) return 0;
      return Math.round((position.current_load / position.max_capacity) * 100);
    },
    
    // 获取进度条状态
    getProgressStatus(position) {
      const rate = this.calculateUsageRate(position);
      if (rate >= 100) return 'exception';
      if (rate >= 80) return 'warning';
      return 'success';
    },
    
    // 更新统计信息
    updateStatistics() {
      this.totalShelves = this.shelves.length;
      this.totalPositions = this.positions.length;
      
      const usedPositions = this.positions.filter(p => p.status === '2' || p.status === '3').length;
      this.usageRate = this.totalPositions > 0 
        ? Math.round((usedPositions / this.totalPositions) * 100) 
        : 0;
    },
    
    // 应用筛选
    applyFilters() {
      this.positionMeshes.forEach(mesh => {
        const position = mesh.userData.data;
        const isVisible = this.statusFilters.includes(String(position.status));
        mesh.visible = isVisible;
      });
    },
    
    // 应用排序
    applySort() {
      // TODO: 实现排序逻辑
      this.$message.info('排序功能开发中');
    },
    
    // 设置视图模式
    setViewMode(mode) {
      this.viewMode = mode;
      
      const isWireframe = mode === 'wireframe';
      
      this.positionMeshes.forEach(mesh => {
        if (mesh.material) {
          mesh.material.wireframe = isWireframe;
        }
      });
      
      this.shelfMeshes.forEach(mesh => {
        if (mesh.material) {
          mesh.material.wireframe = isWireframe;
        }
      });
    },
    
    // 重置相机
    resetCamera() {
      this.camera.position.set(30, 25, 30);
      this.camera.lookAt(0, 0, 0);
      this.controls.reset();
      this.$message.success('视角已重置');
    },
    
    // 导出数据
    exportData() {
      const data = {
        warehouse: this.warehouseList.find(w => w.id === this.selectedWarehouse),
        shelves: this.shelves,
        positions: this.positions,
        statistics: {
          totalShelves: this.totalShelves,
          totalPositions: this.totalPositions,
          usageRate: this.usageRate
        }
      };
      
      const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `warehouse_${this.selectedWarehouse}_${Date.now()}.json`;
      a.click();
      URL.revokeObjectURL(url);
      
      this.$message.success('数据导出成功');
    },
    
    // 鼠标移动事件
    onMouseMove(event) {
      const container = this.$refs.threeContainer;
      const rect = container.getBoundingClientRect();
      
      this.mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
      this.mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;
      
      this.raycaster.setFromCamera(this.mouse, this.camera);
      
      const allMeshes = [...this.shelfMeshes, ...this.positionMeshes];
      const intersects = this.raycaster.intersectObjects(allMeshes);
      
      if (intersects.length > 0) {
        const object = intersects[0].object;
        
        if (this.highlightedObject && this.highlightedObject !== object) {
          this.resetHighlight(this.highlightedObject);
        }
        
        if (object !== this.highlightedObject) {
          this.highlightObject(object);
          this.highlightedObject = object;
          document.body.style.cursor = 'pointer';
        }
      } else {
        if (this.highlightedObject) {
          this.resetHighlight(this.highlightedObject);
          this.highlightedObject = null;
        }
        document.body.style.cursor = 'default';
      }
    },
    
    // 高亮对象
    highlightObject(object) {
      if (object.material) {
        object.material.emissiveIntensity = 0.5;
      }
    },
    
    // 重置高亮
    resetHighlight(object) {
      if (object.material) {
        object.material.emissiveIntensity = 0.2;
      }
    },
    
    // 点击事件
    onClick(event) {
      if (!this.highlightedObject) return;
      
      const userData = this.highlightedObject.userData;
      if (userData && userData.data) {
        this.selectedItem = {
          type: userData.type,
          ...userData.data
        };
      }
    },
    
    // 清除选择
    clearSelection() {
      this.selectedItem = null;
      if (this.highlightedObject) {
        this.resetHighlight(this.highlightedObject);
        this.highlightedObject = null;
      }
    },
    
    // 窗口大小变化
    onWindowResize() {
      const container = this.$refs.threeContainer;
      if (!container) return;
      
      this.camera.aspect = container.clientWidth / container.clientHeight;
      this.camera.updateProjectionMatrix();
      this.renderer.setSize(container.clientWidth, container.clientHeight);
    },
    
    // 动画循环
    animate() {
      this.animationId = requestAnimationFrame(this.animate);
      
      this.controls.update();
      this.renderer.render(this.scene, this.camera);
      
      // 简单FPS计算
      this.fps = Math.round(1000 / 16.67);
    },
    
    // 更新时间
    updateTime() {
      const now = new Date();
      this.lastUpdateTime = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    }
  }
};
</script>

<style scoped lang="scss">
.warehouse-3d-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a2e 100%);
  color: #e2e8f0;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  overflow: hidden;
}

// 现代化头部
.modern-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 64px;
  background: rgba(26, 26, 46, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.3);
  z-index: 100;
}

.header-left {
  .logo-area {
    display: flex;
    align-items: center;
    gap: 12px;
    
    i {
      font-size: 28px;
      color: #63b3ed;
      filter: drop-shadow(0 0 10px rgba(99, 179, 237, 0.5));
    }
    
    .app-title {
      font-size: 20px;
      font-weight: 700;
      background: linear-gradient(135deg, #63b3ed, #4299e1);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      letter-spacing: 0.5px;
    }
  }
}

.header-center {
  display: flex;
  align-items: center;
  gap: 24px;
  
  .warehouse-selector {
    width: 200px;
    
    ::v-deep .el-input__inner {
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.2);
      color: #e2e8f0;
      border-radius: 8px;
      
      &:hover {
        border-color: #63b3ed;
      }
    }
  }
  
  .quick-stats {
    display: flex;
    gap: 20px;
    
    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 8px 16px;
      background: rgba(255, 255, 255, 0.05);
      border-radius: 12px;
      border: 1px solid rgba(255, 255, 255, 0.1);
      transition: all 0.3s ease;
      
      &:hover {
        background: rgba(255, 255, 255, 0.1);
        transform: translateY(-2px);
      }
      
      .stat-label {
        font-size: 11px;
        color: #a0aec0;
        margin-bottom: 4px;
      }
      
      .stat-value {
        font-size: 18px;
        font-weight: 700;
        color: #e2e8f0;
        
        &.usage-rate {
          color: #48bb78;
        }
      }
    }
  }
}

.header-right {
  display: flex;
  gap: 8px;
  
  ::v-deep .el-button {
    border-radius: 8px;
    font-weight: 500;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    }
  }
}

// 主内容区
.main-content {
  display: flex;
  flex: 1;
  overflow: hidden;
}

// 控制面板
.control-panel {
  width: 320px;
  background: rgba(26, 26, 46, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow-y: auto;
  transition: all 0.3s ease;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.05);
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.2);
    border-radius: 3px;
    
    &:hover {
      background: rgba(255, 255, 255, 0.3);
    }
  }
}

.left-panel {
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  padding: 20px;
}

.right-panel {
  border-left: 1px solid rgba(255, 255, 255, 0.1);
  padding: 20px;
}

.panel-section {
  margin-bottom: 24px;
  
  .panel-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    font-weight: 600;
    color: #e2e8f0;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 2px solid rgba(99, 179, 237, 0.3);
    
    i {
      color: #63b3ed;
    }
  }
}

.filter-group {
  ::v-deep .el-checkbox-group {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
  
  .status-checkbox {
    display: flex;
    align-items: center;
    padding: 10px 12px;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 8px;
    border: 1px solid rgba(255, 255, 255, 0.1);
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(255, 255, 255, 0.1);
      transform: translateX(4px);
    }
    
    .status-dot {
      width: 12px;
      height: 12px;
      border-radius: 50%;
      margin-right: 10px;
      box-shadow: 0 0 8px currentColor;
      
      &.available {
        background: #4299e1;
        color: #4299e1;
      }
      
      &.partial {
        background: #48bb78;
        color: #48bb78;
      }
      
      &.full {
        background: #f56565;
        color: #f56565;
      }
      
      &.disabled {
        background: #718096;
        color: #718096;
      }
    }
    
    ::v-deep .el-checkbox__label {
      color: #e2e8f0;
      font-size: 13px;
    }
    
    ::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
      color: #63b3ed;
    }
  }
}

.chart-container {
  .mini-chart {
    height: 8px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 4px;
    overflow: hidden;
    margin-bottom: 8px;
    
    .chart-bar {
      height: 100%;
      background: linear-gradient(90deg, #4299e1, #48bb78);
      border-radius: 4px;
      transition: width 0.5s ease;
      box-shadow: 0 0 10px rgba(72, 187, 120, 0.5);
    }
  }
  
  .chart-legend {
    font-size: 12px;
    color: #a0aec0;
  }
}

// 画布容器
.canvas-container {
  flex: 1;
  position: relative;
  overflow: hidden;
  
  .three-canvas {
    width: 100%;
    height: 100%;
  }
  
  .canvas-overlay {
    position: absolute;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
    
    .overlay-tip {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 20px;
      background: rgba(26, 26, 46, 0.9);
      backdrop-filter: blur(10px);
      border-radius: 20px;
      border: 1px solid rgba(255, 255, 255, 0.2);
      font-size: 12px;
      color: #a0aec0;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
      
      i {
        color: #63b3ed;
      }
    }
  }
}

// 详情面板
.detail-panel {
  .detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid rgba(99, 179, 237, 0.3);
    
    .detail-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: #e2e8f0;
      
      i {
        color: #63b3ed;
      }
    }
    
    ::v-deep .el-button--circle {
      width: 28px;
      height: 28px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      border: none;
      color: #a0aec0;
      
      &:hover {
        background: rgba(245, 101, 101, 0.2);
        color: #f56565;
      }
    }
  }
  
  ::v-deep .el-descriptions {
    .el-descriptions__label {
      background: rgba(255, 255, 255, 0.05);
      color: #a0aec0;
      font-size: 12px;
    }
    
    .el-descriptions__content {
      color: #e2e8f0;
      font-size: 13px;
    }
    
    .el-descriptions__body {
      border-color: rgba(255, 255, 255, 0.1);
    }
  }
  
  .detail-actions {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    gap: 12px;
    
    ::v-deep .el-button {
      border-radius: 8px;
      font-weight: 500;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
      }
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #718096;
  
  i {
    font-size: 64px;
    margin-bottom: 16px;
    opacity: 0.5;
  }
  
  p {
    font-size: 14px;
    text-align: center;
  }
}

// 底部状态栏
.status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 40px;
  background: rgba(26, 26, 46, 0.95);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 12px;
  color: #718096;
  
  .status-left,
  .status-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .status-indicator {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #48bb78;
    
    i {
      font-size: 14px;
    }
  }
  
  .divider {
    color: rgba(255, 255, 255, 0.2);
  }
}

// Element UI 深度定制
::v-deep {
  .el-select-dropdown {
    background: rgba(26, 26, 46, 0.98);
    border: 1px solid rgba(255, 255, 255, 0.1);
    
    .el-select-dropdown__item {
      color: #e2e8f0;
      
      &:hover {
        background: rgba(99, 179, 237, 0.2);
      }
      
      &.selected {
        color: #63b3ed;
      }
    }
  }
  
  .el-radio-button__inner {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: #e2e8f0;
    border-radius: 6px;
    
    &:hover {
      color: #63b3ed;
    }
  }
  
  .el-radio-button__orig-radio:checked + .el-radio-button__inner {
    background: #63b3ed;
    border-color: #63b3ed;
    color: #fff;
  }
  
  .el-progress__text {
    color: #e2e8f0 !important;
    font-size: 11px !important;
  }
}
</style>
