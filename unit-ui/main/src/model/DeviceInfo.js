
const { Sequelize, DataTypes } = require('sequelize')
const SequelizerConfig = require('../db/SequelizeConfig.js');
const log = require('electron-log/main')

const sequelizeConfig = new SequelizerConfig()
const sequelizer = sequelizeConfig.getSequelizeInstance()

/**
 * 设备模型
 */
class DeviceInfo {
    deviceEntity = sequelizer.define('DeviceInfo', {
        id: {
            primaryKey: true,
            type: DataTypes.UUID,
            defaultValue: Sequelize.UUIDV4,
            allowNull: false,
        },
        // 设备编号
        device_no: {
            type: DataTypes.STRING,
            allowNull: false,
        },
        // 能生产的工序号
        process_no: {
            type: DataTypes.STRING,
            allowNull: true,
        },
        // 料宽
        material_width: {
            type: DataTypes.STRING,
            allowNull: true,
        }
    });
    constructor() {
        this.init()
    }
    init = async function () {
        // try { await sequelizer.sync({ alter: true }) } catch (error) { console.log(error) } 
    } 
    findAll = async function () {
        const deviceInfoList = await this.deviceEntity.findAll({ raw: true });
        return deviceInfoList;
    };
    /**
     * 获得当前设备信息
     * @returns deviceInfoMap
     */
    findDeviceInfo = async function () {
        let deviceMap = {};
        let deviceInfo = await this.findAll();
        if (!deviceInfo || deviceInfo.length <= 0) {
            return deviceMap;
        }
        deviceInfo.forEach(row => {
            let device = deviceMap[row.device_no];
            if (!device || typeof device == undefined) {
                device = row;
                device['processNoList'] = [device.process_no];
            } else {
                let processNoList = device['processNoList'].concat(row.process_no);
                device['processNoList'] = processNoList;
            }
            deviceMap[device.device_no] = device;
        });
        return deviceMap;
    };
}
module.exports = DeviceInfo;