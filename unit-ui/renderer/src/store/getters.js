const getters = {
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  userObj: state => state.user,
  userId: state => state.user.userId,
  orgId: state => state.user.orgId,
  nickName: state => state.user.nickName,
  introduction: state => state.user.introduction,
  roles: state => state.user.roles,
  permissions: state => state.user.permissions,
  deviceNo: state=> state.DeviceInfo.deviceNo,
  device: state=> state.DeviceInfo
}
export default getters
