<template>
  <div>
    <el-dialog title="生产数据选择"  :fullscreen="true" :close-on-click-modal="false" :append-to-body="true" :visible.sync="visible">
      <el-form ref="queryParams" :rules="queryParamsRules" :model="queryParams" class="search-form">
        <el-row :gutter="24">
          <el-col :span="3" style="padding:0px;padding-left:12px;">
            <el-select v-model="queryParams.manufacturingCenter" placeholder="选择制造中心" clearable filterable>
              <el-option v-for="item in manufacturingCenters" :key="item.mkNo" :label="`${(item.mkName)}[${(item.mkNo)}]`"
                :value="item.mkNo"></el-option>
            </el-select>
          </el-col>
          <el-col :span="4" style="padding:0px;"><el-input v-model="queryParams.sale_no" placeholder="销售订单号" clearable></el-input></el-col>
          <el-col :span="4" style="padding:0px;"><el-input v-model="queryParams.plan_no" placeholder="制令单号" clearable></el-input></el-col>
          <el-col :span="4" style="padding:0px;"><el-input v-model="queryParams.bill_no" placeholder="工艺单号" clearable></el-input></el-col>
          <el-col :span="3" style="padding:0px;"> <el-input v-model="queryParams.piece_name" placeholder="产品名" clearable></el-input></el-col>
          <el-col :span="5" style="padding:0px;">
            <el-button icon="el-icon-search" @click="handleQuery" style="margin-left:2px;">查询</el-button>
            <el-button type="primary" @click="doSubmit" style="margin-left:2px;">确认</el-button>
          </el-col>
        </el-row>
      </el-form>

      <div style="position:relative;background:#efefef;height:40px; width:98%;margin-left:8px; margin-bottom:-11px; transform: perspective(800px) rotateX(65deg); z-index:1"></div>
 
        <el-row>
          <el-col :span="24" :xs="24">
            <el-table max-height="470" v-loading="loading" :data="produceInfoList" @select="handleSelectionChange"
              @sort-change="sortChangeHandler" :cell-style="styleProcess">
              <el-table-column width="35">
                <!-- slot-scope="scope"这个不能少,不然checkbox会存在问题 -->
                <template slot="header" slot-scope="scope">
                  <el-checkbox class="custom-checkbox" v-model="checkAll" @change="handleAllChange"
                    :indeterminate="isIndeterminate">&nbsp;</el-checkbox>
                </template>
                <template slot-scope="{ row }">
                  <el-checkbox class="custom-checkbox" @change="handleSelectChange($event, row)" :key="row.id" :ref="row.id" :disabled="checkReadOnly(row)" :value="checkIds.includes(row.id)"></el-checkbox>
                </template>
              </el-table-column>
              <el-table-column label="制令单" align="center" prop="plan_no" sortable="custom"  width="130"/>
              <el-table-column label="销售单" align="center" prop="so_no" sortable="custom" width="130"/>
              <el-table-column label="工序号" align="center" prop="process_no" sortable="custom" width="90"/>
              <el-table-column label="工序名称" align="left" prop="piece_name" sortable="custom" width="230"/>
              <el-table-column label="规格" align="left" prop="piece_stand" :show-overflow-tooltip="true" sortable width="90"/>
              <el-table-column label="生产量" align="center" prop="production_quantity" :show-overflow-tooltip="true" width="60"/>
              <el-table-column label="已产量" align="center" prop="total_produced_quantity" :show-overflow-tooltip="true" width="60"/>
              <el-table-column label="接收量" prop="received_quantity" align="center" width="80">
                <template slot-scope="{ row }">
                  <el-input type="number" v-model="row.received_quantity" placeholder="输入接收量" class="recieveNumber"
                    @input="changeReceiveQuanitity($event, row)"></el-input>
                </template>
              </el-table-column>
              <el-table-column label="成型"  sortable="custom" prop="remark" align="center" width="80">
                <template slot-scope="{ row }">
                  {{ getFormulaInfo(row)}}
                </template>
              </el-table-column>
              <el-table-column label="工艺说明" prop="tech_remark" align="left"  width="100vh" :show-overflow-tooltip="true"/>
              <el-table-column label="备注" prop="remark" align="left"  width="200vh" :show-overflow-tooltip="true"/>
            </el-table>
            <!--分页组件-->
            <pagination v-show="total > 0" :total="total" :page.sync="pageParam.pageNum" :limit.sync="pageParam.pageSize"
              @pagination="doQuery"  layout="total, prev, pager, next"/>
          </el-col>
        </el-row>
 
    </el-dialog>
  </div>
</template>

<script>
import { fetchProductInfo, findNeedToProductInfo, findManufactureCenter } from '@/api/ProductInfo'
import remoteService from '../utils/remoteRequest'

// 默认的排序属性.
const DEFAULT_ORDER = 'piece_name desc,piece_stand desc,line_no'
// K03 时显示 MOS_PLANA中的mdec_5(支撑条长度)字段的值
const SUPPORT_STRIP_PROCESS_NO = "K03"
// K01-边框 时显示打孔边距
const FRAME_PROCESS_NO = "K01"
export default {
  data() {
    return {
      hasDoSearch: false,
      checkAll: false,
      isIndeterminate: false,
      checkIds: [],
      // 总条数
      total: 0,
      pageParam: {
        pageNum: 1,
        pageSize: 300,
        orderByColumn: DEFAULT_ORDER,
        orderColumnMap: {},
        isAsc: 'asc',
        sortTypeMap: {
          descending: 'desc',                                             
          ascending: 'asc'
        }
      },
      queryParams: {
        manufacturingCenter: '',
        sale_no: '',
        plan_no: '',
        piece_name: '',
      },
      produceInfoList: [],
      visible: false,
      dataListAllSelections: [], // 所有选中的数据包含跨页数据
      selectData: [],
      loading: true,
      products: [],
      manufacturingCenters: [],
      queryParamsRules: {
        manufacturingCenter: [
          { required: true, trigger: "blur", message: "制造中心不能为空" }
        ]
      },
    }
  },
  created() {
    this.initData() 
  },  
  updated(){
    let that = this  
  },
  methods: {
    init() {
      this.visible = true
      this.loading = false
      let that = this
      this.$nextTick(() => {
        // this.dataListAllSelections = JSON.parse(JSON.stringify(this.selectData));
        // this.resetSearch(); 
        if(this.hasDoSearch){
        this.doQuery()
        }
      });
    },
    initData() {
      // 从后台加载制造中心的数据
      this.manufacturingCenters = []
      let that = this
      findManufactureCenter({}).
        then((res) => {
          // console.log(res)
          if (!!res.data && res.data.length > 0) {
            this.manufacturingCenters = res.data
          }  
        }).
        catch((err) => {
          console.log(err)
          this.msgError(`获得制造中心数据出错:${err}`)
        })
    },
    // 选中数据
    handleSelectionChange(selection, row) {
      this.dataListAllSelections = [row]
    },
    doSubmit() {
      const selectedList = this.produceInfoList.filter((e) => {
        let receiveQuanitity = e.received_quantity
        if (this.checkIds.includes(e.id) && (isNaN(receiveQuanitity) || receiveQuanitity <= 0)) {
          return false
        }
        return this.checkIds.includes(e.id)
      })
      if (this.checkIds.length != selectedList.length) {
        this.msgError("选择的数据必须输入接收量")
        return
      }
      this.visible = false;
      if (!selectedList || selectedList.length <= 0) {
        this.msgInfo("未选择任何生产数据")
      } else { 
        this.$emit("doSelected", selectedList)
      }
      //console.log(selectedList)
      this.checkIds = []
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      // 清空所选择的数据
      this.checkIds = [] 
      this.doQuery()
    },
    doQuery() {
      this.hasDoSearch = true
      this.$refs.queryParams.validate(valid => {
        if (valid) {
          this.loading = true;
          this.produceInfoList = []
          let allParams = { ...this.queryParams, ...this.pageParam }

          findNeedToProductInfo(allParams)
            .then((res) => {
              this.loading = false;
              console.log(res)
              this.produceInfoList = res.rows
              this.total = res.total;
            })
            .catch((er) => {
              this.loading = false;
              //console.log(`查询需要生产的数据失败${er}`)
            });
        }
      });
    }, 
    sortChangeHandler(sortFiled) {
      console.log(sortFiled)
      let sortFieldName = sortFiled.prop
      // 如果是制令单号，需改为制令单的表前缀，因为与工艺卡有同名字段
      if (sortFieldName == 'plan_no') {
        sortFieldName = 'mosPlanA.plan_no'
      }
      if("remark" == sortFieldName){
        const device = this.$store.getters.device
        const processNo = device.processNoList[0]
        // 默认为K02 叶片长度mos_plan_a.mdec_3
        sortFieldName = "blade_meterial_long"
        // 增加成型列可排序，因为此列根据工序号显示不同字段值，因此在排序时也要相应的处理。变更 UCH20240110-01
        // K03支撑条
        if(processNo == SUPPORT_STRIP_PROCESS_NO){
           // 支撑条为 支撑条长度 mos_plan_a.mdec_5
          sortFieldName = "st_long"
        }else if(processNo == FRAME_PROCESS_NO){
          // K01 边框
          // 边框为 边框打孔距离 goodina.mdec_2
          sortFieldName = "side_frame_hole_margins"
        }
      }
      this.pageParam.orderByColumn = ''
      this.pageParam.isAsc = ''
      let sortType = this.pageParam.sortTypeMap[sortFiled.order]
      if (!!sortType && typeof sortType != undefined) {
        this.pageParam.orderColumnMap[sortFieldName] = sortType
      } else {
        delete this.pageParam.orderColumnMap[sortFieldName]
      }
      
      let orderColumns = Object.keys(this.pageParam.orderColumnMap)
      console.log(orderColumns)
      for (let i = 0, L = orderColumns.length; i < L; i++) {
        let orderColumn = orderColumns[i]
        let orderType = this.pageParam.orderColumnMap[orderColumn]

        this.pageParam.orderByColumn += orderColumn
        this.pageParam.isAsc = orderType
        if (i < L - 1) {
          this.pageParam.orderByColumn += ' ' + orderType + ','
        }
      }
      console.log(this.pageParam.orderByColumn)
      if (!this.pageParam.orderByColumn || this.pageParam.orderByColumn == '') {
        this.pageParam.orderByColumn = DEFAULT_ORDER
        this.pageParam.isAsc = 'asc'
      }
      console.log(this.pageParam.orderByColumn + ' ' + this.pageParam.isAsc)
      //console.log(this.pageParam)
      this.doQuery()
    },
    // 行内单选选择
    handleSelectChange(e, row) {
      if (e == true) {
        // 从未选中==>选中
        this.checkIds.push(row.id);
      } else {
        let index = this.checkIds.findIndex((e) => e == row.id);
        this.checkIds.splice(index, 1);
      }
      const currentTableIds = this.produceInfoList.map((e) => e.id);
      const currentTableCheckedIds = currentTableIds.filter((e) =>
        this.checkIds.includes(e)
      );
      this.checkAll = currentTableCheckedIds.length == currentTableIds.length;
      this.isIndeterminate =
        currentTableCheckedIds.length > 0 &&
        currentTableCheckedIds.length < currentTableIds.length
    },
    // 页内全选选择
    handleAllChange(e) {
      const currentTableIds = this.produceInfoList.map((e) => e.id)
      this.isIndeterminate = false;
      if (e == true) {
        // 从未选中==>选中
        let ids = this.checkIds.concat(currentTableIds)
        // id需去重
        this.checkIds = [...new Set(ids)]
      } else {
        // 取消勾选,从选中id列表里把当前页的所有id均移除
        this.checkIds = this.checkIds.filter((e) => {
          return !currentTableIds.includes(e);
        });
      }
    },
    // 输入接收量限制处理
    changeReceiveQuanitity(inputValue, produceInfo) {
      let maxValue = parseInt(produceInfo.production_quantity)
      if (!!produceInfo.total_produced_quantity) {
        maxValue = maxValue - parseInt(produceInfo.total_produced_quantity)
      }
      if (inputValue > maxValue) {
        inputValue = maxValue
      }
      this.$nextTick(() => {
        produceInfo.received_quantity = inputValue
        this.produceInfoList.forEach(element => {
          if (element.id == produceInfo.id) {
            element.received_quantity = inputValue
          }
        })
      })
    },
    getFormulaInfo(row){
      if(! row.formulaCalculateResult){
        return ''
      }
      console.log(row)
      let processNo = row.process_no  
      let contentInfo = `${row.formulaCalculateResult.shape_g}x${row.formulaCalculateResult.shape_w}`
      if(processNo == SUPPORT_STRIP_PROCESS_NO){
        contentInfo = `支撑条长度:`
        if(typeof row.formulaCalculateResult.st_long != undefined){
            contentInfo += row.formulaCalculateResult.st_long
        }
      }
      // K01 时显示 关联ft_sys.dbo.SYS_GOODInA 的字段mdec_2（边框打孔距离）
      if(processNo == FRAME_PROCESS_NO){
        contentInfo =  `边框打孔距离:`
        if(typeof row.formulaCalculateResult.side_frame_hole_margins != undefined){
            contentInfo += row.formulaCalculateResult.side_frame_hole_margins
        }
      }
      return contentInfo
    },
    checkReadOnly(row, callback){
      return this.$parent.isInSelectedList(row,isIn=>{ });
    },
    styleProcess({row,column,rowIndex,columnIndex}){
      const that = this
      let isInSlected = this.$parent.isInSelectedList(row,isIn=>{ });
      console.log("检查结果:"+isInSlected)
      if(isInSlected){
        return {color:"red", backgroundColor:"#c0c0c0",cursor:'not-allowed'}
      }else {
        return { }
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.userDialog {
  .el-dialog__body {
    padding: 0px 0px 0px 0px;
    color: #606266;
    font-size: 14px;
    word-break: break-all;
  }

  .el-main {
    padding: 0px 0px 0px 0px;

    .el-pagination {
      margin-top: 5px;
    }
  }
}

.recieveNumber input.el-input__inner {
  padding: 0px !important;
  border: 1px solid blue !important;
}
.pagination-container{
  padding: 0px 0px !important;
}
.custom-checkbox{
  transform: scale(1.6);
}
.custom-checkbox .el-checkbox__inner {
  width: 30px; /* 设置宽度 */
  height: 30px; /* 设置高度 */
  border-radius: 4px; /* 如果你想要圆角可以设置这个 */
}

.custom-checkbox .el-checkbox__inner::after {
  width: 10px; /* 设置对号的宽度 */
  height: 14px; /* 设置对号的高度 */
}
.search-form{
 z-index:2;
 position: relative; 
  margin-bottom:-20px;
}
</style>
<style>
.recieveNumber .el-input__inner {
  padding: 2px !important;
}
</style>
