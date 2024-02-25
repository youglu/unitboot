const { request } = require('http')

const electronInstance = require('electron')
// const portscanner = require('portscanner');
const log = require('electron-log/main')
const path = require('path')
const url = require('url')

log.initialize({ preload: true })
log.transports.console.level = 'silly';

// 关闭electron安全警告提示
process.env['ELECTRON_DISABLE_SECURITY_WARNINGS'] = 'true'

log.info(`当前环境:${process.env.NODE_ENV},${process.version}`) 

// 设置服务端地址
process.env.API_SERVER_URL =  process.env.NODE_ENV === 'development'
? `http://192.168.31.125:9999/`
: `http://14.18.154.234:9999/`
// process.env.SERVER_PORT = process.env.SERVER_PORT || 8999;
global.API_SERVER_URL_PREFIX = '/';
global.API_SERVER_URL = process.env.API_SERVER_URL //`http://localhost:${process.env.SERVER_PORT}${global.API_SERVER_URL_PREFIX}`;
log.info(`服务端地址地址:${global.API_SERVER_URL }`)

// 设置渲染视图地址
const indexUrl = process.env.NODE_ENV === 'development'
  ? `http://localhost:1680`
  : `file://${__dirname}/view/index.html`

log.info(`当前渲染地址地址:${indexUrl}`)

// 设置本地对外提供的服务端口
// process.env.NODE_ENV = 'prod' //'development' //'prod'
process.env.PORT = 8889
//if (process.env.NODE_ENV == 'development') {
global.__static = path.join(__dirname, '/static').replace(/\\/g, '\\\\')
// 后台随选择一个端口，如果没有指定的话
// portscanner.findAPortNotInUse(8088, 8888)
  // .then(port => {
    // process.env.PORT = process.env.P②6ORT // || port;
    console.log("客户端后台端口:" + process.env.PORT)
    global.API_URL_PREFIX = !process.env.PORT ? `http://localhost:${process.env.PORT}/api` : '/api'
    require('./web/server.js');
  // });
//} 

let mainWindow
/**
 * 创建渲染窗体
 */
function createWindow() {
  mainWindow = new electronInstance.BrowserWindow({
    width: 1500,
    height: 800,
    useContentSize: true,
    icon:  path.join(__dirname, 'icon/kmf-256.png'),
    autoHideMenuBar: true,
    webPreferences: {
      nodeIntegration: true,
      nodeIntegrationInWorker: true,
      webSecurity: false,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload.js')
    }
  })
  //mainWindow.loadURL(
    //url.format({
      //pathname: path.join(__dirname, 'view/index.html'),
      //protocol: 'file:',
      //slashes: true
    //}))
  mainWindow.loadURL(indexUrl)
  mainWindow.on('closed', () => {
    mainWindow = null
  })
  const enableDebug = false;
  mainWindow.webContents.on("did-frame-finish-load", () => { 
    if (enableDebug === true) {
      mainWindow.webContents.once("devtools-opened", () => {  mainWindow.focus();  })
      mainWindow.webContents.openDevTools(); 
    }
  })
}


/**
 * Auto Updater
 */ 
const { autoUpdater, dialog  } = require('electron')

autoUpdater.logger = log;
// 更新服务器地址
const server = 'http://www.gddshl.com'; 
autoUpdater.setFeedURL({ 
  provider: "generic",
  url: `${server}/download/kmf/latest/${process.platform}` 
});
console.log(`${server}/download/kmf/latest/${process.platform}` )
 
// 需要确保在更新时通知用户
autoUpdater.on('update-downloaded', (event, releaseNotes, releaseName) => {
  const dialogOpts = {
    type: 'info',
    buttons: ['重启', '稍后'],
    title: '应用更新',
    message: process.platform === 'win32' ? releaseNotes : releaseName,
    detail: '应用已经更新了，请重启'
  }
  dialog.showMessageBox(dialogOpts).then((returnValue) =>{
    if(returnValue.response === 0){ 
      autoUpdater.quitAnd()
    }
  })
})
autoUpdater.on('error', (err) => {
 log.info('自动更新错误：' + err);
})
// 已准备事件调用创建渲染窗体
electronInstance.app.on('ready', ()=>{
  createWindow()
  autoUpdater.checkForUpdates()
})

// 所有窗体关闭后退出
electronInstance.app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    electronInstance.app.quit()
  }
})

electronInstance.app.on('activate', () => {
  if (mainWindow === null) {
    createWindow()
  }
}) 