const { Sequelize } = require('sequelize')
const log = require('electron-log/main')

log.initialize({ preload: true })
log.transports.console.level = 'silly';

const dbFile = "D:/kmfmcdb/kmf.db"// path.join(process.resourcesPath, '/src/db/kmf.db')
log.info('数据库文件位置:'+dbFile) 
const sequelize =  new Sequelize({
    dialect: 'sqlite',
    storage: dbFile,
    host: 'localhost',
    logging: false
})

function SequelizeConfig(opts) {
    this.getSequelizeInstance = function(){
        return sequelize
    }
}
module.exports = SequelizeConfig;