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

- 安装相关运行环境，运行`anecdote.sql`文件的中SQL语句初始化MySQL，克隆本项目导入IDEA编译，编译前根据实际情况修改yml配置文件。
- 其中邮箱配置除了需要在配置文件中修改为自己的邮箱各项参数，  还需要到`UserServiceImpl.getVerifCode()`方法修改邮箱发出地址相关参数。
- 项目上传图片默认保存位置`E:\anecdote-upload\imgs\anec_imgs`，若要修改到`AnecdoteServiceImpl.addAnecdote()`方法找到相关位置修改即可。
