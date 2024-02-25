
const { Sequelize, DataTypes, QueryTypes, Op, where } = require('sequelize')
const SequelizerConfig = require('../db/SequelizeConfig.js');
const schedule = require('node-schedule');
const axios = require('axios');
const log = require('electron-log/main')
const {snowflakeGenerator} = require('snowflake-id-js') 
const CacheBase = require('cache-base');
 
const idGenerator = snowflakeGenerator(512) 
const sequelizeConfig = new SequelizerConfig()
const sequelizer = sequelizeConfig.getSequelizeInstance()  
const mapCache = new CacheBase();

const API_SERVER_URL = process.env.API_SERVER_URL || 'http://14.18.154.234:9999/'  
log.info(`连接后台服务端url:${API_SERVER_URL}`)
axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios中间件远程后台服务实例
const remoteService = axios.create({
    baseURL: API_SERVER_URL,
    // 超时
    timeout: 10000
})
var isInProcess = false;
// 默认同时在产数量
const DEFAULT_PRODUCTION_COUNT = 5;

/**
 * 生产数据模型
 */
class ProductionInfo {
    // 状态
    // 将产
    WILL_PRODUCTION = 0;
    // 在产
    IN_PRODUCTION = 1;
    // 已产
    PRODUCTED = 2;
    // 中断
    BREAKED = 10;
    // 取消
    CANCEL = 20;
    // 已报送
    SENDED = 30;
    // 已中断报送
    BREAK_SENDED = 31;
    // 删除
    DEL = -1;

    productionEntity = sequelizer.define('ProductionInfo', {
        id: {
            primaryKey: true,
            type: DataTypes.STRING,
            defaultValue: Sequelize.UUIDV4,
            allowNull: false,
        },
        plan_no: {
            type: DataTypes.STRING,
            allowNull: false,
            comment: '制令单号'
        },
        so_no: {
            type: DataTypes.STRING,
            allowNull: true,
            comment: '销售单号'
        },
        piece_name: {
            type: DataTypes.STRING,
            allowNull: false,
            comment: '产品名称'
        },
        piece_stand: {
            type: DataTypes.STRING,
            allowNull: false,
            comment: '规格'
        },
        produced_quantity: {
            type: DataTypes.STRING,
            allowNull: true,
            comment: '已产量'
        },
        production_quantity: {
            type: DataTypes.STRING,
            allowNull: true,
            comment: '订单量'
        },
        received_quantity: {
            type: DataTypes.STRING,
            allowNull: true,
            comment: '接收量'
        },
        process_no: {
            type: DataTypes.STRING,
            allowNull: true,
            comment: '制程工序编号,'
        },
        wst_id: {
            type: DataTypes.STRING,
            allowNull: true,
            comment: '制程工序ID'
        },
        work_no: {
            type: DataTypes.STRING,
            allowNull: true,
            comment: '确认后报工单号'
        },
        process_id: {
            type: DataTypes.STRING,
            allowNull: false,
            comment: '工艺工序Id'
        },
        process_order_no: {
            type: DataTypes.STRING,
            allowNull: true,
            comment: '工序顺序号'
        },
        production_state: {
            type: DataTypes.NUMBER,
            defaultValue: 0,
            allowNull: true,
            comment: '生产状态，0：即将生产，1：在生产，2：完成生产'
        },
        remark: {
            type: DataTypes.STRING,
            allowNull: true,
            comment: '备注'
        },
        deviceNo: {
            type: DataTypes.STRING,
            allowNull: true,
            comment: '生产设备编号'
        },
        userId: {
            type: DataTypes.STRING,
            allowNull: true,
            comment: '创建人ID'
        },
        orgId: {
            type: DataTypes.STRING,
            allowNull: true,
            comment: '创建人所属部门ID'
        },
        createdAt: {
            type: DataTypes.DATE,
            get() {
                let dateValue = this.getDataValue('createdAt')
                if (!!dateValue) {
                    return dateValue.toString().substr(0, 20)
                }
            }
        },
        updatedAt: {
            type: DataTypes.DATE,
            get() {
                let dateValue = this.getDataValue('updatedAt')
                if (!!dateValue) {
                    return dateValue.toString().substr(0, 20)
                }
            }
        },
        formulaCalculateResult: {
            type: DataTypes.JSON,
            allowNull: true,
            comment: '公式计算结果JSON数据'
        },
        side_frame_hole_margins: {
            type: DataTypes.STRING,
            allowNull: true,
            comment: '边框打孔距离'
        },
        angle_steel: {
            type: DataTypes.STRING,
            allowNull: true,
            comment: '角钢或共板'
        }
    },
        { tableName: 'ProductionInfo' }
    );
    /** 构造函数 */
    constructor() {
        this.init()
    }
    /** 初始化方法 */
    init = async function () {
        const that = this
        await sequelizer.sync({ alter: true })
        function scheduleCronstyle() {
            schedule.scheduleJob('0/10 * * * * ?', async function () {
                log.info('scheduleCronstyle:' + new Date());
                if (isInProcess) { 
                    log.info("还在处理中...")
                    return
                }
                isInProcess = true
                await that.checkNeedSendToServer();
                // 清除无效数据
                that.clearInvalidData();
                isInProcess = false
            });
        }
        scheduleCronstyle();
    }
    /**
     * 创建生产数据
     * @param {生产数据} produceInfo 
     * @returns 
     */
    create = async function (produceInfo) {
        // 验证数据有效性
        // 这里是操作代码
        try {
            if(!!produceInfo.id){
                let existEntity = await this.findByPk(produceInfo.id) 
                log.info(`根据id:${produceInfo.id} 查询是否存在数据：${existEntity}`)
                if(!!existEntity && null != existEntity){
                    return this.updateByPk(produceInfo)
                }
            }
            //produceInfo.id = idGenerator.next().value
            const newEntity = await this.productionEntity.create(produceInfo)
            return newEntity;
        } catch (err) { 
            log.info('创建生产数据发生错误')
            log.info(err)
        }
    };
    /**
     * 更新生产数据
     * @param {生产数据} produceInfo 
     * @returns 
     */
    updateByPk = async function (produceInfo) {
        // 验证数据有效性
        // 这里是操作代码
        try { 
            const newEntity = await this.productionEntity.update(produceInfo,
                {  where: { id: produceInfo.id }  })
            return newEntity;
        } catch (err) { 
            log.info('按ID更新生产数据发生错误')
            log.info(err)
        }
    };
    /**
     * 查询当前设备所有在产与将产数据
     * @returns 生产数据集合
     */
    findByPk = async function (primaryKey) {
        // 这里是操作代码
        const entity = await this.productionEntity.findByPk(primaryKey)
        return entity;
    };
    /**
     * 查询当前设备所有在产与将产数据
     * @returns 生产数据集合
     */
    findAll = async function () {
        // 这里是操作代码
        const entityList = await this.productionEntity.findAll({
            raw: true,
            // attributes: ['id',['piece_name','pieceName'],['plan_no','plan_no']]
            where: {
                production_state: { [Op.in]: [this.IN_PRODUCTION, this.WILL_PRODUCTION] }
            }
        })
        return entityList;
    };
    /**
     * 查询当前设备所有在产数据
     * @returns 生产数据集合
     */
    findCurrentProductionInfo = async function (params) {
        // 先检查更新
        await this.autoUpdateProductionInfoState()
        // 再查询
        let whereParam = { production_state: this.IN_PRODUCTION}
        if(typeof params != "undefined" && !!params.deviceNo){ 
            whereParam['deviceNo'] =  params.deviceNo 
        }
        

        const entityList = await this.productionEntity.findAll({
            where: whereParam
        })
        return entityList;
    };
    /**
     * 查询当前设备所有在产与将产数据
     * @returns 生产数据集合
     */
     findCurrentProductInfo = async function (params) {
        // 先检查更新
        await this.autoUpdateProductionInfoState()
        // 查询在产总数
        let whereParam = { production_state: this.IN_PRODUCTION}
        if(typeof params != "undefined" && !!params.deviceNo){ 
            whereParam['deviceNo'] =  params.deviceNo 
        }
        let inProductionCount = await this.productionEntity.count({
            where: whereParam
        })
        // 检查是否小于默认的同时在产量
        if(inProductionCount < DEFAULT_PRODUCTION_COUNT){
            let updateCount = DEFAULT_PRODUCTION_COUNT - inProductionCount;
            // 更新最近的将产为在产，条数为默认的在产量 - 当前在产量
            await sequelizer.query("update `ProductionInfo` set production_state=?  where id in(select id from `ProductionInfo` where production_state=? order by createdAt asc limit ?) "
                , {
                    replacements: [this.IN_PRODUCTION, this.WILL_PRODUCTION, updateCount],
                    type: QueryTypes.UPDATE,
                    raw: true
                });
        }
        // 再查询
        whereParam = { production_state: { [Op.in]: [this.IN_PRODUCTION, this.WILL_PRODUCTION] }}
        if(typeof params != "undefined" && !!params.deviceNo){ 
            whereParam['deviceNo'] =  params.deviceNo 
        }
        const entityList = await this.productionEntity.findAll({
            where: whereParam
        })
        return entityList;
    };
    /**
     * 更新当前在产数据的状态，根据已产量与接收量是否相等进行检查
     * 这里可能会锁表异常，如果董工与客户端后台同时在更新同一行，可能
     * 就会发生异常，这里要注意
     * @returns void
     */
    autoUpdateProductionInfoState = async function (params) {
        // 检查是否有上报处理，如果是则暂不更新处理
        log.info(`是否在处理：${isInProcess}`)
        if (!isInProcess) {
            let that = this
            let whereParam = {
                where: {
                    [Op.eq]: sequelizer.where(sequelizer.col('produced_quantity'), sequelizer.col('received_quantity')),
                    production_state: { [Op.notIn]: [that.SENDED, that.BREAK_SENDED] }
                }
            }
            // 这个没用
            // whereParam.where['produced_quantity'] =  {[Op.not]:null}
            await this.productionEntity.update(
                { production_state: this.PRODUCTED },
                whereParam
            )
        }
    };
    /**
     * 更新生产数状态
     * @returns void
     */
    updateProductInfoState = async function (params) { 
        log.info(`更新状态.......${isInProcess}`)
        isInProcess = true
        try {
            log.info(params)
            let whereParam = {
                where: { id: params.id }
            }
            params.production_state = parseInt(params.production_state)
            const updatedEntity = await this.productionEntity.update(
                { production_state: params.production_state },
                whereParam
            )
            if (params.production_state === this.BREAKED) {
                log.info('保存中断记录.......')
                await this.saveBreakProductionInfo(params)
            } else if (params.production_state === this.CANCEL) {
                log.info('保存取消记录.......')
                await this.saveCancelProductionInfo(params)
            }
            isInProcess = false
        } catch (error) {
            isInProcess = false
        }
    };
    /**
     * 执行中断，并保存中断记录
     * @returns void
     */
    saveBreakProductionInfo = async function (params) {
        let whereParam = {
            where: { id: params.id }
        }
        const updatedEntity = await this.productionEntity.update(
            { production_state: this.BREAKED },
            whereParam
        )
        const entity = await this.productionEntity.findByPk(params.id);
        if (entity === null) {
            log.info('saveBreakProductionInfo Not found product by id ：'+params.id);
        } else {
            // 同时发送此中断的生产数据到服务端进行报工
            this.doSendToServer([entity], this.BREAK_SENDED)
        }
    };
    /**
     * 执行取消，并保存取消记录
     * @returns void
     */
    saveCancelProductionInfo = async function (params) { 
        const entity = await this.productionEntity.findByPk(params.id);
        if (entity === null) {
            log.info('saveCancelProductionInfo Not found product by id ：'+params.id);
        } else {
            // 发送取消操作到后台进行相关操作
            this.doCancelToServer([entity], this.CANCEL)
        }
    };
    /**
     * 保存生产数据操作日志
     */
    saveProductionOperationLog = async function (entityInfo) {

    }
    /**
     * 检查生产数据是否需要同步到服务端进行ERP报工.
     * 
     */
    checkNeedSendToServer = async function () {
        try {
            const that = this 
            // 获得接收量 = 已产量 & 状态 <> 已报送的数据 并且 <> 已中断报送
            let productionInfoList = await sequelizer.query("SELECT * FROM `ProductionInfo` where produced_quantity = received_quantity and production_state = ? and production_state <> ? and production_state <> ?"
                , {
                    replacements: [that.PRODUCTED, that.SENDED, that.BREAK_SENDED],
                    type: QueryTypes.SELECT,
                    mapToModel: true,
                    model: this.productionEntity,
                    raw: true
                }); 
                log.info(mapCache)
            if (!productionInfoList || productionInfoList.length <= 0) {
                // 再查询当前在产数据的已产是否有更新（与上次保存是否一致），如果则需发到服务端进行更新
                productionInfoList = await this.findCurrentProductionInfo()
                if (!!productionInfoList && productionInfoList.length > 0) {
                    const productInfo = productionInfoList[0]
                    let produced_quantity = productInfo.produced_quantity
                    if(!produced_quantity || null == produced_quantity){
                        produced_quantity = 0
                    }
                    log.info(`当前在产产量:${produced_quantity},缓存中的产量:${mapCache.get(productInfo.id)}`)
                    // 检查缓存是否有值
                    if(mapCache.has(productInfo.id)){
                        let cachedProducedQuantity = mapCache.get(productInfo.id)
                        // 如果当前已产大于上次保存的已产，则发到服务端
                        if(parseInt(produced_quantity) > cachedProducedQuantity){ 
                            mapCache.set(productInfo.id, produced_quantity)
                            this.doSendToServer(productionInfoList, this.IN_PRODUCTION)
                        }
                    }else{ 
                        mapCache.set(productInfo.id, produced_quantity)
                    }
                }else{
                    // 如果没有在产数据，是清空缓存
                     mapCache.clear()
                }
            }else{
                this.doSendToServer(productionInfoList, this.SENDED)
            }
            
        } catch (error) {
            log.info(error)
        }
    }
    /**
     * 发送完成生产的数据（接收量=已产量）到服务端.
     * @param {完成生产的数据} productionInfoList 
     * @param {发送报工时的状态，SEND：正常完成报工，BREAK_SEND:中断完成报工} sendState
     */
    doSendToServer = async function (productionInfoList, sendState) {
        // log.info(productionInfoList)
        if (!!productionInfoList && productionInfoList.length > 0) {
            let that = this
            // 发送生产数据到服务端进行ERP报工
            let sendProductionIdArray = []
            productionInfoList.forEach((item) => {
                let dateValue = item.createdAt
                if (!!dateValue) {
                    item.createdAt = dateValue.toString().substr(0, 19)
                }
                dateValue = item.updatedAt
                if (!!dateValue) {
                    item.updatedAt = dateValue.toString().substr(0, 19)
                }
                // 不发送计算的公式json数据到服务端
                delete item.formulaCalculateResult
                
                sendProductionIdArray[sendProductionIdArray.length] = item.id
            })
            //log.info(productionInfoList)
            log.info('发送到服务端进行报工处理')
            await remoteService({
                url: '/erp/reportProcessOver',
                method: 'post',
                data: productionInfoList
            }).then(async (res) => {
                log.info(res.data)
                if(res.data.code == 200){
                    // 更新本次发送的数据状态为已报工
                    await that.productionEntity.update(
                        { production_state: sendState },
                        { where: { id: sendProductionIdArray } }
                    )
                }
            }).catch((err) => {
                log.info(err)
            })
        }
    }
    /**
     * 发送取消操作到服务端.
     * @param {完成生产的数据} productionInfoList
     */
     doCancelToServer = async function (productionInfoList) {
        // log.info(productionInfoList)
        if (!!productionInfoList && productionInfoList.length > 0) {
            let that = this
            let sendProductionIdArray = []
            productionInfoList.forEach((item) => {
                let dateValue = item.createdAt
                if (!!dateValue) {
                    item.createdAt = dateValue.toString().substr(0, 19)
                }
                dateValue = item.updatedAt
                if (!!dateValue) {
                    item.updatedAt = dateValue.toString().substr(0, 19)
                }
                // 不发送计算的公式json数据到服务端
                delete item.formulaCalculateResult
                
                sendProductionIdArray[sendProductionIdArray.length] = item.id
            })
            //log.info(productionInfoList)
            log.info('发送到服务端进行取消处理')
            await remoteService({
                url: '/erp/cancelWillProduct',
                method: 'post',
                data: productionInfoList
            }).then(async (res) => {
                log.info(res.data)
                if(res.data.code == 200){
                    log.info("取消将产数据"+sendProductionIdArray+"完成")
                }
            }).catch((err) => {
                log.info(err)
            })
        }
    }
    /**
     * 模拟生产数据，将在产已产产量增加1
     */
    mockProduct = async function (queryParams) {
        let produced_quantity = queryParams.quantity
        log.info(`模拟更新在产产量${produced_quantity}`)
        const updatedEntity = await this.productionEntity.update(
            { produced_quantity: produced_quantity },
            {
                where: { production_state: this.IN_PRODUCTION }
            }
        )
    }
    /**
     * 定时清理取消或中断后无用的数据，需在中断或取消操作后进行删除会更好.
     * 
     */
    clearInvalidData = async function () {
        try {
            const that = this 
            sequelizer.query("delete FROM `ProductionInfo` where production_state in(?,?)"
                , {
                    replacements: [that.SENDED, that.DEL],/*that.BREAKED, that.CANCEL,*/
                    type: QueryTypes.DELETE,
                    mapToModel: true,
                    model: this.productionEntity,
                    raw: true
                }); 
        } catch (error) {
            log.info(error)
        }
    }
}
module.exports = ProductionInfo;