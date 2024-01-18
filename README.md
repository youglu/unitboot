# unitboot
将springboot项目在osgi中运行，实现多个springboot项目可以合在一起部署运行，同时具备微内核、热部署等OSGI特性，
配合docker一起部署，可实现单元级的更新，比docker更新更快速.有兴趣的朋友可以一起参与持续改进。

运行及更新步骤

### 1：下载所有文件

### 2：运行
进入unitboot-1.0\bin,windows直接运行start.bat

### 3：更新springboot单元
以unitboot-demo1为例，先构建打包好unitboot-demo1,然后将包替换到unitboot-1.0\spring中，
然后在第2步启动的控制台中执行以下命令

|命令|说明|
|---|:---|
|ss |查询出所有单元|
|update 单元编号|更新指定编号的单元，单元编号就是通过ss命令查出来最前面的数字编号。|

技术讨论或发邮件到:<a href='mail:329900041@qq.com'>329900041@qq.com<a/>


### 其它
#### 无法push 到github时，可尝试设置github代理，命令好下：
git config --global http.proxy 127.0.0.1:7890
其中 127.0.0.1:7890为代理的IP与端口
