const Koa = require('koa')
const { koaBody } = require('koa-body');
const Router = require('koa-router')
const cros = require('koa-cors')
const log = require('electron-log/main') 

const ProductionInfo = require('../model/ProductionInfo')
const DeviceInfo = require('../model/DeviceInfo')



log.transports.console.level = 'silly';
 
const app = new Koa();
const router = new Router();
const deviceInfo = new DeviceInfo();
const productionInfo = new ProductionInfo();


//--------------------设备信息路由-------------------
router.get('/api/currentDeviceInfo', async function (ctx, next) {
  let productionDevice = await deviceInfo.findDeviceInfo()
  log.info(productionDevice)
  ctx.body = {
    success: true,
    data: productionDevice
  };
});
router.get('/api/createDevice', async function (ctx, next) {
  let newDevice = await deviceInfo.create()
  ctx.body = {
    success: true,
    data: newDevice
  }
})
//--------------------设备信息路由 end-------------------

//--------------------生产数据路由-------------------
router.post('/api/product/createProductInfo', koaBody(), async function (ctx, next) {
  let newProduceInfoList = ctx.request.body
  log.info("待保存的生产数据")
  log.info(newProduceInfoList)
  newProduceInfoList.forEach(async (element) => {
    await productionInfo.create(element) 
  });
  ctx.body = {
    success: true,
    data: 'success'
  }
})
// 查询指定产品的当前生产情况
router.get('/api/product/findCurrentProductionInfo', async function (ctx, next) {
  let queryParams = ctx.request.query
  let currentProductionInfoList = await productionInfo.findCurrentProductionInfo(queryParams)
  ctx.body = {
    success: true,
    data: currentProductionInfoList
  }
});
// 查询指定产品的当前生产情况
router.get('/api/product/findCurrentProductInfo', async function (ctx, next) {
  let queryParams = ctx.request.query
  let currentProductionInfoList = await productionInfo.findCurrentProductInfo(queryParams)
  ctx.body = {
    success: true,
    data: currentProductionInfoList
  }
});
router.get('/api/product/findAll', async function (ctx, next) {
  let allproductionInfoList = await productionInfo.findAll()
  ctx.body = {
    success: true,
    data: allproductionInfoList
  }
})
// 更新生产数状态
router.get('/api/product/updateProductInfoState', async function (ctx, next) {
  let queryParams = ctx.request.query
  if (!queryParams || !queryParams.id || typeof queryParams.production_state == undefined) {
    ctx.body = {
      success: true,
      data: '参数有误'
    }
  } else {
    await productionInfo.updateProductInfoState(queryParams)
    ctx.body = {
      success: true,
      data: 'success'
    }
  }
});
// 请求模拟增加当前在产的产量
router.get('/api/product/mockProduct', async function (ctx, next) {
  let queryParams = ctx.request.query
  await productionInfo.mockProduct(queryParams)
  ctx.body = {
    success: true,
    data: 'success'
  }
});
//--------------------生产数据路由 end-------------------
app
  .use(cros())
  .use(router.routes())
  .use(koaBody())
  .use(router.allowedMethods());

const PORT = process.env.PORT || 8889;

log.info(`KOA运行端口:${PORT}`)
app.listen(PORT)