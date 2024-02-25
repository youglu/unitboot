// Preload Script
const { contextBridge } = require('electron');

// 将主线程变量设置到渲染线程
contextBridge.exposeInMainWorld('mainEnv', {
  PORT: process.env.PORT
});