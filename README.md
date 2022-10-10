# unitboot
将springboot项目在osgi中运行，实现多个springboot项目可以合在一起部署运行，同时具备微内核、热部署等OSGI特性，有兴趣的朋友可以一起参与持续改进。

运行步骤

1：下载所有文件

2：进入unitboot-1.0\bin,windows直接运行start.bat

3：构建demo,然后将包替换到unitboot-1.0\spring中，然后在第2步启动的控制台执行以下命令
 命令               说明
 ss                 查询出所有单元
 update 单元编号     更新指定编号的单元，单元编号就是通过ss命令查出来最前面的数字编号。
