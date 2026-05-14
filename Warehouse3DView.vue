<template>
  <div class="warehouse-3d-container">
    <!-- 顶部导航栏 -->
    <header class="modern-header">
      <div class="header-left">
        <div class="logo-area">
          <i class="el-icon-box"></i>
          <span class="app-title">智能仓储可视化系统</span>
        </div>
        <el-select 
          v-model="selectedWarehouseId" 
          placeholder="选择仓库" 
          size="small"
          class="warehouse-select"
          @change="loadWarehouseData"
        >
          <el-option
            v-for="wh in warehouseList"
            :key="wh.id"
            :label="wh.name"
            :value="wh.id"
          />
        </el-select>
      </div>
      
      <div class="header-center">
        <div class="stats-bar">
          <div class="stat-item">
            <span class="stat-label">总货架</span>
            <span class="stat-value">{{ totalShelves }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">总仓位</span>
            <span class="stat-value">{{ totalPositions }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">利用率</span>
            <span class="stat-value usage">{{ utilizationRate }}%</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">空闲</span>
            <span class="stat-value available">{{ availableCount }}</span>
          </div>
        </div>
      </div>

      <div class="header-right">
        <el-button-group size="small">
          <el-tooltip content="重置视角" placement="bottom">
            <el-button icon="el-icon-refresh-left" @click="resetCamera"></el-button>
          </el-tooltip>
          <el-tooltip content="自动旋转" placement="bottom">
            <el-button 
              :type="autoRotate ? 'primary' : ''" 
              icon="el-icon-c-scale-to-original" 
              @click="toggleAutoRotate"
            ></el-button>
          </el-tooltip>
          <el-tooltip content="线框模式" placement="bottom">
            <el-button 
              :type="wireframeMode ? 'primary' : ''" 
              icon="el-icon-grid" 
              @click="toggleWireframe"
            ></el-button>
          </el-tooltip>
          <el-tooltip content="导出视图" placement="bottom">
            <el-button icon="el-icon-download" @click="exportView"></el-button>
          </el-tooltip>
        </el-button-group>
      </div>
    </header>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 左侧控制面板 -->
      <aside class="control-panel left-panel">
        <div class="panel-section">
          <h3 class="panel-title">
            <i class="el-icon-s-data"></i>
            状态筛选
          </h3>
          <div class="filter-group">
            <el-checkbox-group v-model="statusFilters" @change="applyFilters">
              <el-checkbox label="1" class="status-checkbox available">
                <span class="status-dot"></span> 空闲
              </el-checkbox>
              <el-checkbox label="2" class="status-checkbox partial">
                <span class="status-dot"></span> 部分占用
              </el-checkbox>
              <el-checkbox label="3" class="status-checkbox full">
                <span class="status-dot"></span> 已满
              </el-checkbox>
              <el-checkbox label="0" class="status-checkbox unavailable">
                <span class="status-dot"></span> 不可用
              </el-checkbox>
            </el-checkbox-group>
          </div>
        </div>

        <div class="panel-section">
          <h3 class="panel-title">
            <i class="el-icon-s-operation"></i>
            显示选项
          </h3>
          <div class="option-group">
            <el-switch
              v-model="showLabels"
              active-text="显示标签"
              inactive-text="隐藏标签"
              @change="toggleLabels"
            />
          </div>
          <div class="option-group">
            <el-slider
              v-model="opacityValue"
              :min="0.3"
              :max="1"
              :step="0.1"
              marks
              @input="updateOpacity"
            >
              <template slot="marks">
                <span :style="{ left: '0%' }">透明</span>
                <span :style="{ left: '100%' }">不透明</span>
              </template>
            </el-slider>
          </div>
        </div>

        <div class="panel-section legend-section">
          <h3 class="panel-title">
            <i class="el-icon-guide"></i>
            图例说明
          </h3>
          <div class="legend-item">
            <span class="legend-color available"></span>
            <span class="legend-text">空闲 (0%)</span>
          </div>
          <div class="legend-item">
            <span class="legend-color partial"></span>
            <span class="legend-text">部分占用 (1-99%)</span>
          </div>
          <div class="legend-item">
            <span class="legend-color full"></span>
            <span class="legend-text">已满 (100%)</span>
          </div>
          <div class="legend-item">
            <span class="legend-color unavailable"></span>
            <span class="legend-text">不可用</span>
          </div>
          <div class="legend-item">
            <span class="legend-color selected"></span>
            <span class="legend-text">选中状态</span>
          </div>
        </div>
      </aside>

      <!-- 3D 画布区域 -->
      <div class="canvas-wrapper">
        <div ref="canvasContainer" class="canvas-container"></div>
        
        <!-- 悬浮操作提示 -->
        <div class="canvas-hints">
          <div class="hint-item">
            <i class="el-icon-mouse"></i>
            <span>左键旋转 · 右键平移 · 滚轮缩放</span>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-overlay">
          <div class="loading-spinner">
            <i class="el-icon-loading"></i>
            <span>加载仓库数据...</span>
          </div>
        </div>
      </div>

      <!-- 右侧详情面板 -->
      <aside class="control-panel right-panel" :class="{ 'expanded': selectedObject }">
        <div v-if="!selectedObject" class="empty-state">
          <i class="el-icon-pointer"></i>
          <p>点击货架或仓位查看详情</p>
        </div>
        
        <div v-else class="detail-content">
          <div class="detail-header">
            <h3 class="detail-title">
              <i :class="selectedObject.type === 'shelf' ? 'el-icon-s-order' : 'el-icon-box'"></i>
              {{ selectedObject.type === 'shelf' ? '货架详情' : '仓位详情' }}
            </h3>
            <el-button size="mini" icon="el-icon-close" circle @click="clearSelection"></el-button>
          </div>

          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="编号">
              <el-tag size="mini" effect="plain">{{ selectedObject.code }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="名称">
              {{ selectedObject.name }}
            </el-descriptions-item>
            
            <template v-if="selectedObject.type === 'position'">
              <el-descriptions-item label="所属货架">
                {{ selectedObject.shelfNo }}
              </el-descriptions-item>
              <el-descriptions-item label="层号">
                {{ selectedObject.rowNo }}
              </el-descriptions-item>
              <el-descriptions-item label="列号">
                {{ selectedObject.columnNo }}
              </el-descriptions-item>
              <el-descriptions-item label="最大容量">
                {{ selectedObject.maxCapacity }}
              </el-descriptions-item>
              <el-descriptions-item label="当前状态">
                <el-tag :type="getStatusType(selectedObject.status)" size="mini">
                  {{ getStatusText(selectedObject.status) }}
                </el-tag>
              </el-descriptions-item>
            </template>

            <template v-else>
              <el-descriptions-item label="排号">
                {{ selectedObject.rowNo }}
              </el-descriptions-item>
              <el-descriptions-item label="列号">
                {{ selectedObject.columnNo }}
              </el-descriptions-item>
              <el-descriptions-item label="层数">
                {{ selectedObject.layers }}
              </el-descriptions-item>
              <el-descriptions-item label="每层位数">
                {{ selectedObject.positionsOfLayer }}
              </el-descriptions-item>
              <el-descriptions-item label="总仓位数">
                {{ selectedObject.totalPositions }}
              </el-descriptions-item>
            </template>

            <el-descriptions-item label="账套">
              {{ selectedObject.dbno }}
            </el-descriptions-item>
          </el-descriptions>

          <div class="action-buttons">
            <el-button type="primary" size="small" block>
              <i class="el-icon-view"></i>
              定位视角
            </el-button>
            <el-button size="small" block>
              <i class="el-icon-document"></i>
              查看库存
            </el-button>
          </div>
        </div>
      </aside>
    </div>

    <!-- 底部状态栏 -->
    <footer class="modern-footer">
      <div class="footer-info">
        <span><i class="el-icon-coordinate"></i> 视角：{{ cameraPosition }}</span>
        <span><i class="el-icon-time"></i> 最后更新：{{ lastUpdateTime }}</span>
      </div>
      <div class="footer-version">
        <el-tag size="mini" type="info">v2.0.0</el-tag>
      </div>
    </footer>
  </div>
</template>

<script>
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';

export default {
  name: 'Warehouse3DView',
  data() {
    return {
      // Three.js 核心对象
      scene: null,
      camera: null,
      renderer: null,
      controls: null,
      raycaster: null,
      mouse: new THREE.Vector2(),
      
      // 数据存储
      warehouseList: [],
      shelvesData: [],
      positionsData: [],
      
      // 状态管理
      selectedWarehouseId: null,
      selectedObject: null,
      statusFilters: ['0', '1', '2', '3'],
      showLabels: true,
      wireframeMode: false,
      autoRotate: true,
      opacityValue: 0.9,
      loading: false,
      
      // 渲染对象组
      shelfGroup: null,
      positionGroup: null,
      labelsGroup: null,
      highlightedObjects: [],
      
      // 统计信息
      totalShelves: 0,
      totalPositions: 0,
      utilizationRate: 0,
      availableCount: 0,
      
      // 其他
      lastUpdateTime: '',
      animationId: null,
    };
  },
  
  computed: {
    cameraPosition() {
      if (!this.camera) return 'N/A';
      const pos = this.camera.position;
      return `(${pos.x.toFixed(1)}, ${pos.y.toFixed(1)}, ${pos.z.toFixed(1)})`;
    }
  },
  
  mounted() {
    this.initThreeJS();
    this.loadWarehouseList();
    this.setupEventListeners();
    this.updateTime();
    setInterval(this.updateTime, 60000);
  },
  
  beforeDestroy() {
    this.cleanupThreeJS();
  },
  
  methods: {
    // 初始化 Three.js
    initThreeJS() {
      const container = this.$refs.canvasContainer;
      const width = container.clientWidth;
      const height = container.clientHeight;
      
      // 创建场景
      this.scene = new THREE.Scene();
      this.scene.background = new THREE.Color('#0f172a');
      this.scene.fog = new THREE.Fog('#0f172a', 50, 200);
      
      // 创建相机
      this.camera = new THREE.PerspectiveCamera(
        60,
        width / height,
        0.1,
        1000
      );
      this.camera.position.set(30, 25, 30);
      this.camera.lookAt(0, 5, 0);
      
      // 创建渲染器
      this.renderer = new THREE.WebGLRenderer({ 
        antialias: true,
        alpha: true 
      });
      this.renderer.setSize(width, height);
      this.renderer.setPixelRatio(window.devicePixelRatio);
      this.renderer.shadowMap.enabled = true;
      this.renderer.shadowMap.type = THREE.PCFSoftShadowMap;
      container.appendChild(this.renderer.domElement);
      
      // 添加控制器
      this.controls = new OrbitControls(this.camera, this.renderer.domElement);
      this.controls.enableDamping = true;
      this.controls.dampingFactor = 0.05;
      this.controls.autoRotate = this.autoRotate;
      this.controls.autoRotateSpeed = 1.0;
      this.controls.maxPolarAngle = Math.PI / 2.2;
      this.controls.minDistance = 10;
      this.controls.maxDistance = 100;
      
      // 添加光线追踪器
      this.raycaster = new THREE.Raycaster();
      
      // 创建灯光
      this.setupLights();
      
      // 创建地面网格
      this.createGroundGrid();
      
      // 创建对象组
      this.shelfGroup = new THREE.Group();
      this.positionGroup = new THREE.Group();
      this.labelsGroup = new THREE.Group();
      this.scene.add(this.shelfGroup);
      this.scene.add(this.positionGroup);
      this.scene.add(this.labelsGroup);
      
      // 开始渲染循环
      this.animate();
      
      // 窗口大小变化处理
      window.addEventListener('resize', this.handleResize);
    },
    
    setupLights() {
      // 环境光
      const ambientLight = new THREE.AmbientLight(0xffffff, 0.6);
      this.scene.add(ambientLight);
      
      // 主方向光
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
      const fillLight = new THREE.DirectionalLight(0x93c5fd, 0.3);
      fillLight.position.set(-30, 20, -30);
      this.scene.add(fillLight);
      
      // 底光
      const bottomLight = new THREE.DirectionalLight(0xfbbf24, 0.2);
      bottomLight.position.set(0, -20, 0);
      this.scene.add(bottomLight);
    },
    
    createGroundGrid() {
      // 创建网格地面
      const gridHelper = new THREE.GridHelper(100, 50, 0x334155, 0x1e293b);
      gridHelper.position.y = -0.1;
      this.scene.add(gridHelper);
      
      // 创建发光地面
      const planeGeometry = new THREE.PlaneGeometry(100, 100);
      const planeMaterial = new THREE.MeshStandardMaterial({
        color: 0x0f172a,
        roughness: 0.8,
        metalness: 0.2,
        transparent: true,
        opacity: 0.9
      });
      const plane = new THREE.Mesh(planeGeometry, planeMaterial);
      plane.rotation.x = -Math.PI / 2;
      plane.position.y = -0.2;
      plane.receiveShadow = true;
      this.scene.add(plane);
    },
    
    // 加载仓库列表
    async loadWarehouseList() {
      try {
        // TODO: 替换为真实 API
        // const response = await this.$api.get('/erp_ct_warehouse/list');
        // this.warehouseList = response.data;
        
        // Mock 数据
        this.warehouseList = [
          { id: 1, name: '华东一号仓', code: 'WH001', warehouse_id: 'WH001', status: 1, dbno: 'DB001' },
          { id: 2, name: '华南二号仓', code: 'WH002', warehouse_id: 'WH002', status: 1, dbno: 'DB001' },
          { id: 3, name: '华北三号仓', code: 'WH003', warehouse_id: 'WH003', status: 1, dbno: 'DB001' }
        ];
        
        if (this.warehouseList.length > 0) {
          this.selectedWarehouseId = this.warehouseList[0].id;
          this.loadWarehouseData();
        }
      } catch (error) {
        this.$message.error('加载仓库列表失败');
        console.error(error);
      }
    },
    
    // 加载仓库数据
    async loadWarehouseData() {
      if (!this.selectedWarehouseId) return;
      
      this.loading = true;
      
      try {
        // 清空现有数据
        this.clearScene();
        
        // TODO: 替换为真实 API
        // const [shelvesRes, positionsRes] = await Promise.all([
        //   this.$api.get('/erp_ct_shelves/list', { params: { warehouse_id: this.selectedWarehouseId } }),
        //   this.$api.get('/erp_ct_position/list', { params: { warehouse_id: this.selectedWarehouseId } })
        // ]);
        // this.shelvesData = shelvesRes.data;
        // this.positionsData = positionsRes.data;
        
        // Mock 数据生成
        this.generateMockData();
        
        // 渲染货架和仓位
        this.renderShelves();
        this.renderPositions();
        
        // 更新统计信息
        this.updateStatistics();
        
        this.loading = false;
      } catch (error) {
        this.loading = false;
        this.$message.error('加载仓库数据失败');
        console.error(error);
      }
    },
    
    // 生成模拟数据
    generateMockData() {
      this.shelvesData = [];
      this.positionsData = [];
      
      const shelfRows = 5;
      const shelfCols = 4;
      const layersPerShelf = 4;
      const positionsPerLayer = 3;
      
      let shelfId = 1;
      for (let row = 0; row < shelfRows; row++) {
        for (let col = 0; col < shelfCols; col++) {
          const shelf = {
            id: shelfId,
            name: `货架-${String.fromCharCode(65 + row)}${col + 1}`,
            code: `SH-${String.fromCharCode(65 + row)}${String(col + 1).padStart(2, '0')}`,
            warehouse_id: String(this.selectedWarehouseId),
            row_no: row,
            column_no: col,
            layers: layersPerShelf,
            positions_of_layer: positionsPerLayer,
            status: 1,
            dbno: 'DB001'
          };
          this.shelvesData.push(shelf);
          
          // 为每个货架生成仓位
          for (let layer = 0; layer < layersPerShelf; layer++) {
            for (let pos = 0; pos < positionsPerLayer; pos++) {
              const statusRoll = Math.random();
              let status = 1;
              if (statusRoll < 0.1) status = 0;
              else if (statusRoll < 0.4) status = 2;
              else if (statusRoll < 0.7) status = 3;
              
              const position = {
                id: this.positionsData.length + 1,
                code: `POS-${String.fromCharCode(65 + row)}${String(col + 1).padStart(2, '0')}-${layer + 1}-${pos + 1}`,
                warehouse_id: this.selectedWarehouseId,
                name: `仓位-${layer + 1}-${pos + 1}`,
                area: `区域-${String.fromCharCode(65 + row)}`,
                shelf_no: shelfId,
                row_no: layer,
                column_no: pos,
                status: status,
                max_capacity: 100,
                order_no: this.positionsData.length + 1,
                dbno: 'DB001'
              };
              this.positionsData.push(position);
            }
          }
          
          shelfId++;
        }
      }
    },
    
    // 渲染货架
    renderShelves() {
      const shelfSpacing = 6;
      const shelfWidth = 2;
      const shelfDepth = 1.5;
      
      this.shelvesData.forEach((shelf, index) => {
        const x = (shelf.row_no - this.shelvesData.length / 2) * shelfSpacing;
        const z = (shelf.column_no - this.shelvesData.length / 2) * shelfSpacing;
        const height = shelf.layers * 1.2;
        
        // 创建货架主体
        const geometry = new THREE.BoxGeometry(shelfWidth, height, shelfDepth);
        const material = new THREE.MeshStandardMaterial({
          color: 0x64748b,
          roughness: 0.7,
          metalness: 0.3,
          transparent: true,
          opacity: this.opacityValue,
          wireframe: this.wireframeMode
        });
        
        const mesh = new THREE.Mesh(geometry, material);
        mesh.position.set(x, height / 2, z);
        mesh.castShadow = true;
        mesh.receiveShadow = true;
        
        // 存储元数据
        mesh.userData = {
          type: 'shelf',
          id: shelf.id,
          ...shelf,
          totalPositions: shelf.layers * shelf.positions_of_layer
        };
        
        // 添加边框
        const edges = new THREE.EdgesGeometry(geometry);
        const lineMaterial = new THREE.LineBasicMaterial({ 
          color: 0x94a3b8,
          linewidth: 2
        });
        const lineSegments = new THREE.LineSegments(edges, lineMaterial);
        mesh.add(lineSegments);
        
        this.shelfGroup.add(mesh);
        
        // 添加层分隔线
        for (let i = 1; i < shelf.layers; i++) {
          const dividerGeometry = new THREE.PlaneGeometry(shelfWidth + 0.1, shelfDepth + 0.1);
          const dividerMaterial = new THREE.MeshBasicMaterial({
            color: 0x475569,
            side: THREE.DoubleSide,
            transparent: true,
            opacity: 0.5
          });
          const divider = new THREE.Mesh(dividerGeometry, dividerMaterial);
          divider.rotation.x = Math.PI / 2;
          divider.position.set(0, i * 1.2, 0);
          mesh.add(divider);
        }
      });
    },
    
    // 渲染仓位
    renderPositions() {
      const shelfSpacing = 6;
      const shelfWidth = 2;
      const shelfDepth = 1.5;
      const positionWidth = 0.6;
      const positionHeight = 0.8;
      const positionDepth = 1.2;
      
      this.positionsData.forEach((position, index) => {
        const shelf = this.shelvesData.find(s => s.id === position.shelf_no);
        if (!shelf) return;
        
        const shelfX = (shelf.row_no - this.shelvesData.length / 2) * shelfSpacing;
        const shelfZ = (shelf.column_no - this.shelvesData.length / 2) * shelfSpacing;
        
        // 计算仓位位置
        const offsetX = (position.column_no - (shelf.positions_of_layer - 1) / 2) * positionWidth;
        const offsetY = 0.4 + position.row_no * 1.2;
        const offsetZ = 0;
        
        const x = shelfX + offsetX;
        const y = offsetY;
        const z = shelfZ + offsetZ;
        
        // 根据状态设置颜色
        const color = this.getPositionColor(position.status);
        
        // 创建仓位几何体
        const geometry = new THREE.BoxGeometry(positionWidth * 0.9, positionHeight * 0.8, positionDepth * 0.9);
        const material = new THREE.MeshStandardMaterial({
          color: color,
          roughness: 0.5,
          metalness: 0.4,
          transparent: true,
          opacity: this.opacityValue,
          wireframe: this.wireframeMode,
          emissive: color,
          emissiveIntensity: 0.2
        });
        
        const mesh = new THREE.Mesh(geometry, material);
        mesh.position.set(x, y, z);
        mesh.castShadow = true;
        mesh.receiveShadow = true;
        
        // 存储元数据
        mesh.userData = {
          type: 'position',
          id: position.id,
          ...position
        };
        
        // 添加高亮效果
        mesh.onBeforeRender = () => {
          if (mesh.userData.highlighted) {
            material.emissiveIntensity = 0.6;
          }
        };
        
        this.positionGroup.add(mesh);
        
        // 添加标签（可选）
        if (this.showLabels) {
          this.createLabel(position.code, x, y + 0.6, z);
        }
      });
    },
    
    // 创建文本标签
    createLabel(text, x, y, z) {
      const canvas = document.createElement('canvas');
      const context = canvas.getContext('2d');
      canvas.width = 256;
      canvas.height = 64;
      
      context.fillStyle = 'rgba(15, 23, 42, 0.8)';
      context.fillRect(0, 0, canvas.width, canvas.height);
      
      context.font = 'Bold 20px Arial';
      context.fillStyle = '#ffffff';
      context.textAlign = 'center';
      context.textBaseline = 'middle';
      context.fillText(text, canvas.width / 2, canvas.height / 2);
      
      const texture = new THREE.CanvasTexture(canvas);
      const material = new THREE.SpriteMaterial({ 
        map: texture,
        transparent: true,
        opacity: 0.9
      });
      
      const sprite = new THREE.Sprite(material);
      sprite.position.set(x, y, z);
      sprite.scale.set(2, 0.5, 1);
      
      this.labelsGroup.add(sprite);
    },
    
    // 获取仓位颜色
    getPositionColor(status) {
      const colors = {
        0: 0x64748b, // 不可用 - 灰色
        1: 0x3b82f6, // 空闲 - 蓝色
        2: 0x22c55e, // 部分占用 - 绿色
        3: 0xef4444  // 已满 - 红色
      };
      return colors[status] || 0x64748b;
    },
    
    // 更新统计信息
    updateStatistics() {
      this.totalShelves = this.shelvesData.length;
      this.totalPositions = this.positionsData.length;
      
      const statusCount = { 0: 0, 1: 0, 2: 0, 3: 0 };
      this.positionsData.forEach(pos => {
        statusCount[pos.status]++;
      });
      
      this.availableCount = statusCount[1];
      const usedCount = statusCount[2] + statusCount[3];
      this.utilizationRate = this.totalPositions > 0 
        ? Math.round((usedCount / this.totalPositions) * 100) 
        : 0;
    },
    
    // 应用筛选
    applyFilters() {
      this.positionGroup.children.forEach(mesh => {
        const isVisible = this.statusFilters.includes(String(mesh.userData.status));
        mesh.visible = isVisible;
      });
      
      this.labelsGroup.children.forEach(sprite => {
        // 简单处理：标签跟随仓位可见性
        sprite.visible = true; // 实际项目中需要更精确的关联
      });
    },
    
    // 切换标签显示
    toggleLabels() {
      this.labelsGroup.visible = this.showLabels;
    },
    
    // 更新透明度
    updateOpacity() {
      this.positionGroup.children.forEach(mesh => {
        mesh.material.opacity = this.opacityValue;
      });
      this.shelfGroup.children.forEach(mesh => {
        mesh.material.opacity = this.opacityValue;
      });
    },
    
    // 切换线框模式
    toggleWireframe() {
      this.wireframeMode = !this.wireframeMode;
      
      this.positionGroup.children.forEach(mesh => {
        mesh.material.wireframe = this.wireframeMode;
      });
      this.shelfGroup.children.forEach(mesh => {
        mesh.material.wireframe = this.wireframeMode;
      });
    },
    
    // 切换自动旋转
    toggleAutoRotate() {
      this.autoRotate = !this.autoRotate;
      this.controls.autoRotate = this.autoRotate;
    },
    
    // 重置相机
    resetCamera() {
      this.camera.position.set(30, 25, 30);
      this.camera.lookAt(0, 5, 0);
      this.controls.reset();
    },
    
    // 清空场景
    clearScene() {
      // 移除所有子对象
      while(this.shelfGroup.children.length > 0) {
        const obj = this.shelfGroup.children[0];
        this.shelfGroup.remove(obj);
        if (obj.geometry) obj.geometry.dispose();
        if (obj.material) {
          if (Array.isArray(obj.material)) {
            obj.material.forEach(m => m.dispose());
          } else {
            obj.material.dispose();
          }
        }
      }
      
      while(this.positionGroup.children.length > 0) {
        const obj = this.positionGroup.children[0];
        this.positionGroup.remove(obj);
        if (obj.geometry) obj.geometry.dispose();
        if (obj.material) {
          if (Array.isArray(obj.material)) {
            obj.material.forEach(m => m.dispose());
          } else {
            obj.material.dispose();
          }
        }
      }
      
      while(this.labelsGroup.children.length > 0) {
        const obj = this.labelsGroup.children[0];
        this.labelsGroup.remove(obj);
        if (obj.material && obj.material.map) {
          obj.material.map.dispose();
        }
      }
      
      this.selectedObject = null;
    },
    
    // 设置事件监听
    setupEventListeners() {
      const container = this.$refs.canvasContainer;
      
      container.addEventListener('mousemove', this.onMouseMove);
      container.addEventListener('click', this.onClick);
      container.addEventListener('dblclick', this.onDoubleClick);
    },
    
    // 鼠标移动处理
    onMouseMove(event) {
      const rect = this.$refs.canvasContainer.getBoundingClientRect();
      this.mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
      this.mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;
      
      // 射线检测
      this.raycaster.setFromCamera(this.mouse, this.camera);
      
      const intersects = this.raycaster.intersectObjects(
        [...this.positionGroup.children, ...this.shelfGroup.children],
        true
      );
      
      // 清除之前的高亮
      this.highlightedObjects.forEach(obj => {
        if (obj.material) {
          obj.material.emissiveIntensity = 0.2;
        }
      });
      this.highlightedObjects = [];
      
      if (intersects.length > 0) {
        const firstIntersect = intersects[0];
        const object = firstIntersect.object;
        
        if (object.userData.type) {
          document.body.style.cursor = 'pointer';
          
          if (object.material) {
            object.material.emissiveIntensity = 0.6;
            this.highlightedObjects.push(object);
          }
        }
      } else {
        document.body.style.cursor = 'default';
      }
    },
    
    // 点击处理
    onClick(event) {
      this.raycaster.setFromCamera(this.mouse, this.camera);
      
      const intersects = this.raycaster.intersectObjects(
        [...this.positionGroup.children, ...this.shelfGroup.children],
        true
      );
      
      if (intersects.length > 0) {
        const object = intersects[0].object;
        
        if (object.userData.type) {
          this.selectObject(object.userData);
        }
      }
    },
    
    // 双击处理
    onDoubleClick(event) {
      this.raycaster.setFromCamera(this.mouse, this.camera);
      
      const intersects = this.raycaster.intersectObjects(
        [...this.positionGroup.children, ...this.shelfGroup.children],
        true
      );
      
      if (intersects.length > 0) {
        const object = intersects[0].object;
        
        if (object.userData.type) {
          this.focusOnObject(object);
        }
      }
    },
    
    // 选择对象
    selectObject(userData) {
      this.selectedObject = userData;
      
      // 高亮选中的对象
      this.highlightSelectedObject();
    },
    
    // 高亮选中对象
    highlightSelectedObject() {
      // 清除之前的高亮
      this.positionGroup.children.forEach(mesh => {
        if (mesh.material) {
          mesh.material.emissiveIntensity = 0.2;
        }
      });
      
      if (!this.selectedObject) return;
      
      // 找到并高亮选中的对象
      const targetGroup = this.selectedObject.type === 'shelf' 
        ? this.shelfGroup 
        : this.positionGroup;
      
      targetGroup.children.forEach(mesh => {
        if (mesh.userData.id === this.selectedObject.id) {
          if (mesh.material) {
            mesh.material.emissiveIntensity = 0.8;
          }
        }
      });
    },
    
    // 聚焦到对象
    focusOnObject(object) {
      const position = object.position.clone();
      const offset = 5;
      
      this.camera.position.set(
        position.x + offset,
        position.y + offset,
        position.z + offset
      );
      this.camera.lookAt(position);
      this.controls.target.copy(position);
    },
    
    // 清除选择
    clearSelection() {
      this.selectedObject = null;
      this.highlightSelectedObject();
    },
    
    // 获取状态类型
    getStatusType(status) {
      const types = {
        0: 'info',
        1: 'success',
        2: 'warning',
        3: 'danger'
      };
      return types[status] || 'info';
    },
    
    // 获取状态文本
    getStatusText(status) {
      const texts = {
        0: '不可用',
        1: '空闲',
        2: '部分占用',
        3: '已满'
      };
      return texts[status] || '未知';
    },
    
    // 导出视图
    exportView() {
      this.renderer.render(this.scene, this.camera);
      
      const link = document.createElement('a');
      link.download = `warehouse-view-${Date.now()}.png`;
      link.href = this.renderer.domElement.toDataURL('image/png');
      link.click();
      
      this.$message.success('视图已导出');
    },
    
    // 处理窗口大小变化
    handleResize() {
      const container = this.$refs.canvasContainer;
      const width = container.clientWidth;
      const height = container.clientHeight;
      
      this.camera.aspect = width / height;
      this.camera.updateProjectionMatrix();
      this.renderer.setSize(width, height);
    },
    
    // 动画循环
    animate() {
      this.animationId = requestAnimationFrame(this.animate);
      
      this.controls.update();
      this.renderer.render(this.scene, this.camera);
    },
    
    // 清理 Three.js
    cleanupThreeJS() {
      if (this.animationId) {
        cancelAnimationFrame(this.animationId);
      }
      
      window.removeEventListener('resize', this.handleResize);
      
      const container = this.$refs.canvasContainer;
      if (container && this.renderer) {
        container.removeChild(this.renderer.domElement);
      }
      
      if (this.renderer) {
        this.renderer.dispose();
      }
      
      // 清理几何体和材质
      this.scene.traverse(object => {
        if (object.geometry) {
          object.geometry.dispose();
        }
        if (object.material) {
          if (Array.isArray(object.material)) {
            object.material.forEach(m => m.dispose());
          } else {
            object.material.dispose();
          }
        }
      });
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
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  overflow: hidden;
}

// 现代化顶部导航栏
.modern-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background: rgba(30, 41, 59, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(148, 163, 184, 0.1);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  z-index: 100;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .logo-area {
      display: flex;
      align-items: center;
      gap: 10px;
      
      i {
        font-size: 28px;
        color: #3b82f6;
        background: linear-gradient(135deg, #3b82f6, #8b5cf6);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
      
      .app-title {
        font-size: 20px;
        font-weight: 700;
        color: #f1f5f9;
        letter-spacing: 0.5px;
      }
    }
    
    .warehouse-select {
      width: 200px;
      
      ::v-deep .el-input__inner {
        background: rgba(51, 65, 85, 0.5);
        border: 1px solid rgba(148, 163, 184, 0.3);
        color: #f1f5f9;
        border-radius: 8px;
        
        &:hover {
          border-color: #3b82f6;
        }
        
        &:focus {
          border-color: #3b82f6;
          box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
        }
      }
    }
  }
  
  .header-center {
    .stats-bar {
      display: flex;
      gap: 30px;
      
      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 4px;
        
        .stat-label {
          font-size: 12px;
          color: #94a3b8;
          text-transform: uppercase;
          letter-spacing: 0.5px;
        }
        
        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: #f1f5f9;
          
          &.usage {
            color: #fbbf24;
          }
          
          &.available {
            color: #22c55e;
          }
        }
      }
    }
  }
  
  .header-right {
    ::v-deep .el-button-group {
      .el-button {
        background: rgba(51, 65, 85, 0.5);
        border: 1px solid rgba(148, 163, 184, 0.3);
        color: #f1f5f9;
        
        &:hover {
          background: rgba(59, 130, 246, 0.2);
          border-color: #3b82f6;
          color: #3b82f6;
        }
        
        &.is-active,
        &--primary {
          background: #3b82f6;
          border-color: #3b82f6;
          color: white;
        }
      }
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
  width: 280px;
  background: rgba(30, 41, 59, 0.6);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(148, 163, 184, 0.1);
  padding: 20px;
  overflow-y: auto;
  transition: all 0.3s ease;
  
  &.left-panel {
    border-right: 1px solid rgba(148, 163, 184, 0.1);
  }
  
  &.right-panel {
    border-left: 1px solid rgba(148, 163, 184, 0.1);
    
    &.expanded {
      width: 320px;
    }
  }
  
  .panel-section {
    margin-bottom: 24px;
    
    .panel-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 600;
      color: #f1f5f9;
      margin-bottom: 16px;
      padding-bottom: 8px;
      border-bottom: 1px solid rgba(148, 163, 184, 0.2);
      
      i {
        color: #3b82f6;
      }
    }
  }
  
  // 筛选组
  .filter-group {
    ::v-deep .el-checkbox-group {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
    
    .status-checkbox {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #cbd5e1;
      
      .status-dot {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        display: inline-block;
        
        &::before {
          content: '';
          display: block;
          width: 100%;
          height: 100%;
          border-radius: 50%;
        }
      }
      
      &.available .status-dot::before {
        background: #3b82f6;
      }
      
      &.partial .status-dot::before {
        background: #22c55e;
      }
      
      &.full .status-dot::before {
        background: #ef4444;
      }
      
      &.unavailable .status-dot::before {
        background: #64748b;
      }
      
      ::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
        color: #f1f5f9;
      }
    }
  }
  
  // 选项组
  .option-group {
    margin-bottom: 16px;
    
    ::v-deep .el-switch__label {
      color: #cbd5e1;
      
      &.is-active {
        color: #3b82f6;
      }
    }
    
    ::v-deep .el-slider {
      margin-top: 12px;
      
      .el-slider__runway {
        background: rgba(51, 65, 85, 0.5);
      }
      
      .el-slider__bar {
        background: #3b82f6;
      }
      
      .el-slider__button {
        border-color: #3b82f6;
      }
      
      .el-slider__marks-text {
        color: #94a3b8;
        font-size: 12px;
      }
    }
  }
  
  // 图例
  .legend-section {
    .legend-item {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;
      
      .legend-color {
        width: 20px;
        height: 20px;
        border-radius: 4px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
        
        &.available {
          background: #3b82f6;
        }
        
        &.partial {
          background: #22c55e;
        }
        
        &.full {
          background: #ef4444;
        }
        
        &.unavailable {
          background: #64748b;
        }
        
        &.selected {
          background: #fbbf24;
          box-shadow: 0 0 10px #fbbf24;
        }
      }
      
      .legend-text {
        font-size: 13px;
        color: #cbd5e1;
      }
    }
  }
  
  // 空状态
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 300px;
    color: #64748b;
    
    i {
      font-size: 48px;
      margin-bottom: 16px;
      opacity: 0.5;
    }
    
    p {
      font-size: 14px;
      text-align: center;
    }
  }
  
  // 详情内容
  .detail-content {
    .detail-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      
      .detail-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 16px;
        font-weight: 600;
        color: #f1f5f9;
        
        i {
          color: #3b82f6;
        }
      }
      
      ::v-deep .el-button {
        background: rgba(51, 65, 85, 0.5);
        border: 1px solid rgba(148, 163, 184, 0.3);
        color: #f1f5f9;
        
        &:hover {
          background: rgba(239, 68, 68, 0.2);
          border-color: #ef4444;
          color: #ef4444;
        }
      }
    }
    
    ::v-deep .el-descriptions {
      .el-descriptions__label {
        background: rgba(51, 65, 85, 0.3);
        color: #94a3b8;
        font-weight: 500;
      }
      
      .el-descriptions__content {
        color: #f1f5f9;
      }
      
      .el-descriptions__body {
        background: rgba(30, 41, 59, 0.5);
        border-radius: 8px;
      }
    }
    
    .action-buttons {
      margin-top: 20px;
      display: flex;
      flex-direction: column;
      gap: 12px;
      
      ::v-deep .el-button {
        background: rgba(59, 130, 246, 0.1);
        border: 1px solid rgba(59, 130, 246, 0.3);
        color: #3b82f6;
        
        &:hover {
          background: rgba(59, 130, 246, 0.2);
          border-color: #3b82f6;
        }
        
        &--primary {
          background: #3b82f6;
          border-color: #3b82f6;
          color: white;
          
          &:hover {
            background: #2563eb;
            border-color: #2563eb;
          }
        }
      }
    }
  }
}

// 画布区域
.canvas-wrapper {
  flex: 1;
  position: relative;
  overflow: hidden;
  
  .canvas-container {
    width: 100%;
    height: 100%;
  }
  
  .canvas-hints {
    position: absolute;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
    background: rgba(30, 41, 59, 0.8);
    backdrop-filter: blur(10px);
    padding: 8px 16px;
    border-radius: 20px;
    border: 1px solid rgba(148, 163, 184, 0.2);
    
    .hint-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 12px;
      color: #94a3b8;
      
      i {
        color: #3b82f6;
      }
    }
  }
  
  .loading-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(15, 23, 42, 0.8);
    backdrop-filter: blur(5px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 50;
    
    .loading-spinner {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12px;
      color: #f1f5f9;
      
      i {
        font-size: 40px;
        color: #3b82f6;
      }
    }
  }
}

// 底部状态栏
.modern-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 24px;
  background: rgba(30, 41, 59, 0.8);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(148, 163, 184, 0.1);
  
  .footer-info {
    display: flex;
    gap: 24px;
    font-size: 12px;
    color: #94a3b8;
    
    span {
      display: flex;
      align-items: center;
      gap: 6px;
      
      i {
        color: #3b82f6;
      }
    }
  }
  
  .footer-version {
    ::v-deep .el-tag {
      background: rgba(59, 130, 246, 0.1);
      border-color: rgba(59, 130, 246, 0.3);
      color: #3b82f6;
    }
  }
}

// 滚动条美化
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(30, 41, 59, 0.3);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: rgba(148, 163, 184, 0.3);
  border-radius: 3px;
  
  &:hover {
    background: rgba(148, 163, 184, 0.5);
  }
}

// Element UI 覆盖样式
::v-deep .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: #3b82f6;
  border-color: #3b82f6;
}

::v-deep .el-checkbox__inner:hover {
  border-color: #3b82f6;
}
</style>
