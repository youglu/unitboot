<template>
    <div class="index" style="height: 100%;display: flex;flex-direction: column;padding:10px;">
        <TopInfo></TopInfo>
        <div style="height: calc(100% - 0px); display: flex;flex-direction: column;"> 
            <div style="height 200px; margin-top:10px;">
                <div style="font-size: 26px;padding-left:20px; margin-bottom:-23px;position:relative;z-index:2;color:green;">当前在生产</div>
                <div style="position:relative;background:#efefef;height:40px; width:98%;margin-left:8px; margin-bottom:-11px; transform: perspective(800px) rotateX(65deg); z-index:1"></div>
                <el-row type="flex" justify="center">
                    <el-col :span="24" :xs="24">
                        <el-table v-loading="loading" :data="productionInfoList">
                            <el-table-column label="工序号" align="center" prop="process_no" width="60"/>
                            <el-table-column label="产品名称" align="left" prop="piece_name" width="160"/>
                            <el-table-column label="规格" align="center" prop="piece_stand" :show-overflow-tooltip="true" width="100"/>
                            <el-table-column label="已产量" align="center" prop="produced_quantity" :show-overflow-tooltip="true" width="60"/>
                            <el-table-column label="订单量" align="center" prop="production_quantity"  :show-overflow-tooltip="true" width="60"/>
                            <el-table-column label="接收量" align="center" prop="received_quantity" width="60"/>
                            <el-table-column label="备注" prop="remark" align="left" :show-overflow-tooltip="true"/>
                            <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="80">
                                <template slot-scope="scope">
                                    <el-button size="mini" type="text" icon="el-icon-edit"
                                        @click="breakProduction(scope.row)">中断</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </el-col>
                </el-row>
            </div>

            <div style="margin-top:20px;padding-bottom:100px;">
                <div style="font-size: 26px;padding-left:20px; margin-bottom:-23px;position:relative;z-index:2">即将生产</div>
                <div style="position:relative;background:#efefef;height:40px; width:98%;margin-left:8px; margin-bottom:-11px; transform: perspective(800px) rotateX(65deg); z-index:1"></div>
                <el-row type="flex" justify="center">
                    <el-col :span="24" :xs="24">
                        <el-table v-loading="loading" :data="willProductInfoList">
                            <el-table-column label="工序号" align="center" prop="process_no" width="60"/>
                            <el-table-column label="产品名称" align="left" prop="piece_name" width="160"/>
                            <el-table-column label="规格" align="center" prop="piece_stand" :show-overflow-tooltip="true" width="100"/>
                            <el-table-column label="订单量" align="center" prop="production_quantity" :show-overflow-tooltip="true" width="60"/>
                            <el-table-column label="接收量" align="center" prop="received_quantity" width="60"/>
                            <el-table-column label="备注" prop="remark" align="left"  :show-overflow-tooltip="true"/>
                            <el-table-column label="操作" align="center" width="80" class-name="small-padding fixed-width">
                                <template slot-scope="scope">
                                    <el-button size="mini" type="text" icon="el-icon-edit"
                                        @click="cancelWillProduction(scope.row)">取消</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </el-col>
                </el-row>
            </div>

            <div style="text-align: center;margin:30px 0px 0px 0px;position: fixed;bottom:90px;left:47%;z-index:3" >
                <el-button id="chooseProductButton" @click="showNeedProductionInfo" :loading="loading" size="medium" type="primary"
                    style="font-size:50px;width:80px;height:80px;border-radius: 50%;position: absolute;box-shadow: 0 0 20px rgba(16, 33, 37, 0.825);">
                    <span style="position: relative;top:-5px;">+</span>
                </el-button>
                <NeedProductionInfoDialog ref="needProductionInfo" @doSelected="submitSelectedProduceInfo"  @checkIsInSelectedList="isInSelectedList">
                </NeedProductionInfoDialog>
            </div>
        </div>
    </div>
</template>
<script>
import { snowflakeGenerator } from 'snowflake-id-js'
import constInfo from '@/utils/constInfo'
import {
    findAll, fetchCurrentProductionInfo, findCurrentProductInfo, updateProductInfoState
    , product_state, createProductInfo, saveBreakProductionInfo, saveCancelProductionInfo
    , confirmReceiveQuantity
} from '@/api/ProductInfo'
import TopInfo from '@/components/TopInfo'
import NeedProductionInfoDialog from '@/components/NeedProductionInfoDialog'

const generator = snowflakeGenerator(512)

export default {
    name: "index",
    data() {
        return {
            productionInfoList: [],
            willProductInfoList: [],
            constInfo: constInfo,
            loading: false,
            // 监听当前在产信息定时对象
            monitorIterval: null
        };
    },
    components: {
        TopInfo, NeedProductionInfoDialog
    },
    watch: {
    },
    created() {
        this.getCurrentDeviceInfo()
        this.findProductions()  
    },
    mounted(){
        let animateTeyp = 1
         window.setInterval(function(){
            if(animateTeyp == 1){
                chooseProductButton.classList.remove("animate2");
                chooseProductButton.classList.add("animate1");
                animateTeyp = 2
            }else{
                animateTeyp = 1 
                chooseProductButton.classList.remove("animate1");
                chooseProductButton.classList.add("animate2");
            }
         },800)
    },
    methods: {
        // 从本地获得当前在产数据
        findProductions() {
            this.productionInfoList = []
            this.willProductInfoList = []
            findAll().then(res => {
                console.log(res)
                let inProductionList = []
                let willProductList = []
                res.data.forEach((productInfo) => {
                    if (productInfo.production_state == product_state.IN_PRODUCTION) {
                        inProductionList[inProductionList.length] = productInfo
                    } else if (productInfo.production_state == product_state.WILL_PRODUCTION) {
                        willProductList[willProductList.length] = productInfo
                    }
                })
                this.$nextTick(() => {
                    this.productionInfoList = inProductionList
                    this.willProductInfoList = willProductList
                });
            }).catch(error => {
                console.log(`获得当前设备生产数据发生异常:${error}`)
            })
        },
        // 获得当前设备信息
        getCurrentDeviceInfo() {
            this.$store.dispatch("getCurrentDeviceInfo").then(deviceInfo => {
                this.startMonitorProductioin()
            })
        },
        // 显示列表
        showNeedProductionInfo() {
            this.$refs.needProductionInfo.init() 
        },
        // 获得选择的待生的数据
        submitSelectedProduceInfo(selectedProduceInfoList) {
            this.loading = true
            // console.log(selectedProduceInfoList)
            const currentWillProcessIds = this.willProductInfoList.map((e) => { return e.process_id })
            const currentInProcessIds = this.productionInfoList.map((e) => { return e.process_id })
            const that = this
            const needSaveList = []
            // 增加接收量局部函数
            let appendReceiveQuantity = function (oldItem, newItem) {
                if (oldItem.process_id === newItem.process_id) {
                    let newReciveQuantity = parseInt(oldItem.received_quantity) + parseInt(newItem.received_quantity)
                    // 如果本次相加的接收量大于应生产量，则接收量为应生产量
                    if (newReciveQuantity > oldItem.production_quantity) {
                        newReciveQuantity = oldItem.production_quantity
                    }
                    oldItem.received_quantity = newReciveQuantity 
                    needSaveList[needSaveList.length] = oldItem
                }
            }
          
            // 将选择的数据检查是否在已选 并做相应的处理
            selectedProduceInfoList.filter((productItem) => {
                // 设置用户信息
                productItem.userId = that.$store.getters.userId
                productItem.orgId = that.$store.getters.orgId
                if (!currentWillProcessIds.includes(productItem.process_id) && !currentInProcessIds.includes(productItem.process_id)) {
                    // 更新ID为新生成的ID，所以本地后台就不再生成ID
                    productItem.id = generator.next().value
                    // 设置选 中生产数据的所属设备
                    productItem.deviceNo = that.$store.getters.deviceNo 
                    needSaveList[needSaveList.length] = productItem 
                } else {
                    // 如果存在，則更新接收量，需检查接收是否大于应产量
                    this.willProductInfoList.forEach((item) => {
                        appendReceiveQuantity(item, productItem)
                    })
                    this.productionInfoList.forEach((item) => {
                        appendReceiveQuantity(item, productItem)
                    })
                }
            })
            console.log(needSaveList) 
            
            // 提交数据到服务端，用于服务端创建工序作业单
            confirmReceiveQuantity(needSaveList)
                .then((res) => {
                    console.log('上报结果')
                    console.log(res) 
                   
                    if(res.code == 200){
                        needSaveList.forEach((productItem) => {
                            that.$nextTick(() => {
                                that.willProductInfoList.push(productItem)
                            })
                        })
                        let processWorkNos = res.data
                        that.willProductInfoList.forEach((productItem) => {
                            let processWorkNo = processWorkNos[productItem.process_id];
                            if (!!processWorkNo) {
                                productItem.work_no = processWorkNo
                            }
                        })
                        needSaveList.forEach((productItem) => {
                            let processWorkNo = processWorkNos[productItem.process_id];
                            if (!!processWorkNo) {
                                productItem.work_no = processWorkNo
                            }
                        })
                        console.log(that.willProductInfoList)
                        that.msgInfo("上报成功")

                        // 保存到本地后台
                        createProductInfo(needSaveList)
                            .then((res) => {
                                this.loading = false
                            })
                            .catch((err) => {
                                this.msgInfo('保存选择的生产数据到本地发生错误')
                                console.log(err)
                                this.loading = false
                            })
                    }else{
                        that.msgInfo("上报数据发生错误")
                        that.loading = false
                    }
                }).catch((err) => {
                    that.msgInfo("上报数据发生错误")
                    console.log(err)
                    that.loading = false
                })


        },
        isInSelectedList(checkProduct,callback){
            let isIn = false
            // 检查是否在将产列表
            this.willProductInfoList.filter((productItem) => {
                if(checkProduct.plan_no == productItem.plan_no
                    && checkProduct.so_no == productItem.so_no
                    && checkProduct.piece_name == productItem.piece_name
                    && checkProduct.wst_id == productItem.wst_id){
                        isIn = true
                    }
            });
            if(!isIn){
                // 如果不存在，再检查是否在在产
                this.productionInfoList.filter((productItem) => {
                    if(checkProduct.plan_no == productItem.plan_no
                        && checkProduct.so_no == productItem.so_no
                        && checkProduct.piece_name == productItem.piece_name
                        && checkProduct.wst_id == productItem.wst_id){
                            isIn = true
                        }
                });
            }
         
           callback(isIn) 
           return isIn
        },
        //  中断在产
        breakProduction(rowObj) {
            this.$confirm('您确定要中止吗?', {
                // confirmButtonText: '确定',
                // cancelButtonText: '取消'
            }).then(() => {
                this.doBreakProduction(rowObj)
            }).catch(() => {
                this.msgInfo('取消中断')
            })
        },
        // 执行中断在产
        doBreakProduction(rowObj) {
            this.loading = true
            // 保存数据
            saveBreakProductionInfo(rowObj.id)
                .then((res) => {
                    if (!!res && !!res.data && res.data == 'success') {
                        // 先清空在产
                        this.productionInfoList = []
                        // 这里行不做处理，由自动检查处理，不然得在这里保存加入在产的状态更新
                        // 从将产列表获得最后一个，并从将产列表删除
                        //const addToProductionData = this.willProductInfoList.shift()
                        //if (!!addToProductionData) {
                        //this.productionInfoList = [addToProductionData]
                        //}
                        this.msgSuccess('中断完成')
                    } else {
                        this.msgError(res.data)
                    }
                    this.loading = false
                })
                .catch((err) => {
                    this.msgError('中断发生错误')
                    this.loading = false
                })
        },
        //  取消将产
        cancelWillProduction(rowObj) {
            this.$confirm('您确定要取消吗?', {
                // confirmButtonText: '确定',
                // cancelButtonText: '取消'
            }).then(() => {
                this.docancelWillProduction(rowObj)
            }).catch(() => {
            })
        },
        // 执行取消将产
        docancelWillProduction(rowObj) {
            if (!this.willProductInfoList || this.willProductInfoList.length <= 0) {
                return
            }
            this.loading = true
            // 保存数据
            saveCancelProductionInfo(rowObj.id)
                .then((res) => {
                    if (!!res && !!res.data && res.data == 'success') {
                        let toDeleteIndex = -1
                        this.willProductInfoList.forEach((item, index) => {
                            if (rowObj.id == item.id) {
                                toDeleteIndex = index
                            }
                        })
                        // 删除将产指定位置数据
                        this.willProductInfoList.splice(toDeleteIndex, 1)
                        this.msgSuccess('取消完成')
                    } else {
                        this.msgError(res.data)
                    }
                    this.loading = false
                })
                .catch((err) => {
                    this.msgError('取消发生错误')
                    this.loading = false
                })
        },
        // 定时监视生产情况
        startMonitorProductioin() {
            const that = this
            this.monitorIterval = window.setInterval(function () {
                // 定时更新当前在产，此API在本地后台会先更新 在产=接收量 的数据状态为已产，然后返回在产（如果在产没达到接收量）
                let param = { deviceNo: that.$store.getters.deviceNo }
                findCurrentProductInfo(param).
                    then((res) => {
                        console.log(res.data)
                        that.productionInfoList = []
                        that.willProductInfoList = []
                        if (!!res && !!res.data && res.data.length > 0) {
                            that.$nextTick(() => {
                                res.data.forEach((productItem,index,arr)=>{
                                    if(productItem.production_state === product_state.IN_PRODUCTION){
                                        that.productionInfoList.push(productItem)
                                    }else{
                                        that.willProductInfoList.push(productItem)
                                    }
                                })
                            });
                        }
                    }).
                    catch((err) => {
                        console.log(err)
                        that.productionInfoList = []
                    })
            }, 5000)

        }
    },
    beforeDestroy() {
        window.clearInterval(this.monitorIterval)
        console.log("注销操作....")
    }

};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
    margin: 40px 0 0;
}

ul {
    list-style-type: none;
    padding: 0;
}

li {
    display: inline-block;
    margin: 0 10px;
}

a {
    color: #42b983;
}

.udate {
    border: 1px solid red;
    font-size: 120px;
    font-weight: bold;
}
.animate1{
    width:90px !important;
    height:90px !important;
    transition: all 0.2s; 
    margin-left: -5px;
    margin-top: -5px;
    font-size: 60px !important;
}
.animate2{
    width:80px !important;
    height:80px !important;
    transition: all 0.5s; 
    margin-left: 0px;
    margin-top: 0px;
} 
</style>
