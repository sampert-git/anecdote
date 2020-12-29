# Anecdote名人轶事网

## 项目简介
`anecdote`旨在为用户提供了解及分享古今中外各路名人之奇闻轶事的web平台，功能包括：

- 邮箱注册/登录
- 首页列表展示
- 帖子搜索
- 帖子发表
- 评论及评论点赞
- 后台管理

## 项目演示
[视频演示地址](https://v.youku.com/v_show/id_XNDkxMDI4NDMyNA==.html)

## 技术选型

### 后端
| 元素 | 说明 |
| ---- | ---- |
| Springboot | 容器+MVC框架 |
| Mybatis | ORM框架 |
| PageHelper | 分页插件 |
| Redis | 数据缓存 |
| MongoDB | NoSQL数据库 |
| Druid | 数据库连接池 |
| Slf4j+logback | 日志收集 |

### 前端
| 元素 | 说明 |
| ---- | ---- |
| Bootstrap | UI框架 |
| Thymeleaf | 模板引擎 |

## 开发环境

| 工具 | 版本 |
| ---- | ---- |
| JDK | 1.8 |
| MySQL | 8.0.19 |
| Redis | 6.0.5 |
| MongoDB | 4.4.0 |

## 项目部署

- 确保机器已安装MySQL、Redis、MongoDB等数据库服务，并正常运行；
- 根据实际情况修改yml配置文件参数（邮箱、数据库ip、端口等）；
- 运行`anecdote.sql`文件的中SQL语句初始化MySQL数据库（Redis、MongoDB不需要事先建库建表）；
- 克隆本项目导入IDEA编译运行即可。
