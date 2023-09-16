## 🌍项目介绍

一个丰富的 API 开放调用平台。

一般来说，API网关是运行于外部请求与内部服务之间的一个流量入口，实现对外部请求的协议转换、鉴权、流控、参数校验、监控等通用功能。API平台有很多，但是不够统一，本平台帮助企业、个人统一开放接口，统一提供鉴权、限流、熔断等非业务基础能力。 减少沟通成本，避免重复造轮子，为业务高效赋能。力求给用户提供简洁、实用的接口调用体验，并且使用数字签名校验保障接口调用的安全性。

![img](https://bugstack.cn/images/article/assembly/api-gateway/api-gateway-220809-01.png?raw=true) 

对于公司来说，原本一个庞大的单体应用（All in one）业务系统被拆分成许多微服务（Microservice）系统进行独立的维护和部署，服务拆分带来的变化是API的规模成倍增长，API的管理难度也在日益增加，使用API网关发布和管理API逐渐成为一种趋势。 

此外，本人在美团实习过程中发现公司有调用第三方接口的业务，并且在学习过程中也有调用第三方接口的尝试，比如手机短信接口，视频点播接口，支付接口等等，一直好奇第三方接口的服务提供方是如何实现安全，方便，快捷的接口的调用体验，遂萌生了一个自己写一个API接口平台的想法，于是该项目诞生了。

 

## ✨ 高可用

### 稳定性保障

提供了一些常规的稳定性保障手段，来保证自身和后端服务的可用性。如下图所示：

![图 12](https://p0.meituan.net/travelcube/5d5f296cd05fbba6c73904c6b65ecc88199503.png)

- **流量管控**：从用户自定义UUID限流、App限流、IP限流、集群限流等多个维度提供流量保护。
- **请求缓存**：对于一些幂等的、查询频繁的、数据及时性不敏感的请求，业务研发人员可开启请求缓存功能。
- **超时管理**：每个API都设置了处理超时时间，对于超时的请求，进行快速失败的处理，避免资源占用。
- **熔断降级**：支持熔断降级功能，实时监控请求的统计信息，达到配置的失败阈值后，自动熔断，返回默认值。

### 请求安全

请求安全是API网关非常重要的能力，目前已接入请求签名。 

### 监控告警

**立体化监控** 

提供360度的立体化监控，从业务指标、机器指标、JVM指标提供7x24小时的专业守护，如下表： 

|      | 监控模块 | 主要功能                                                     |
| ---- | -------- | ------------------------------------------------------------ |
| 1    | 统一监控 | 实时上报请求调用信息、系统指标，负责应用层（JVM）监控、系统层（CPU、IO、网络）监控 |
| 2    | 链路追踪 | 负责全链路参数透传、全链路追踪监控                           |
| 3    | 日志监控 | 监控本地日志异常关键字：如5xx状态码、空指针异常等            |

**多维度告警**

有了全面的监控体系，自然少不了配套的告警机制，主要的告警能力包括： 

|      | 监控模块     | 主要功能                                  |
| ---- | ------------ | ----------------------------------------- |
| 1    | 限流告警     | API请求达到限流规则阈值触发限流告警       |
| 2    | 请求失败告警 | 请求超时、后端服务异常等触发请求失败告警  |
| 3    | API异常告警  | API发布失败、API检查异常时触发API异常告警 |



## 🎨 架构

### 控制面

使用API网关的控制面，业务研发人员可以轻松的完成API的全生命周期管理，如下图所示（部分功能暂未实现）： 

![img](https://p0.meituan.net/travelcube/70ebfcddcb50909c120b091db3835098216890.png) 

业务研发人员从创建API开始，完成参数录入；接着可以通过文档和MOCK功能进行API测试；API测试完成后，为了保证上线稳定性，提供了发布审批的安全保证措施；API运行期间会监控API的调用失败情况、记录请求日志，一旦发现异常及时发出告警；最后，对于不再使用的API进行下线操作后，会回收API所占用的各类资源并等待重新启用。

整个生命周期，全部通过配置化、流程化的方式，由业务研发人员全自助管理，极大地提升了研发效率。

### 配置中心

用于向API网关的数据面下发API的路由、规则、组件等配置变更。

配置中心的设计上使用Nacos统一配置管理和本地缓存结合的方式，实现动态配置，不停机发布。 

### 数据面

当请求流量命中API请求路径进入服务端，具体处理逻辑由一系列功能组件完成。网关提供了丰富的功能组件集成，包括链路追踪、实时监控、访问日志、参数校验、鉴权、限流、熔断降级等，如下图所示（部分功能暂未实现）：

![图 6](https://p1.meituan.net/travelcube/8d9184431f36f8e2b1f4cd396fbc71ae221623.png) 抽象模型层和业务层代码为公共模块，使用 Dubbo RPC 框架实现子系统间的接口调用 

客户端 SDK 尽量用最少的依赖，基于 Spring Boot Starter 自主设计 SDK ，保证 SDK 的精简、避免依赖冲突 

![流程图](https://ace-file.oss-cn-shenzhen.aliyuncs.com/articles/%E6%B5%81%E7%A8%8B%E5%9B%BE.png)

- api-common ：公共封装类（如公共实体、公共常量，统一响应实体，统一异常处理）
- api-backend ：接口管理平台，主要包括用户、接口相关的功能
- api-gateway ：网关服务，涉及到接口数据一致性处理 ，网关限流，统一鉴权，统一日志处理，流量染色，接口统计等等
- api-interface：接口服务，提供可供调用的接口
- api-client-sdk：提供给开发者的 SDK



## 🌟 模块

###  数据mock模块

- 快速生成建表语句、模拟数据和代码，便于快速测试接口
- 支持多种生成模拟数据的规则，比如固定值、随机值、正则表达式、递增值

### 接口模块

- 接口列表，具体接口出入参，示例等展示
- 浏览接口及在线调试，🚀实时统计调用次数并离线保存
- 🚀数字签名校验接口调用权限，支持SDK调用接口 
- 支持调用量统计，如调用量TOP3排行饼状图
- 目前已对接AI，标签聚合等第三方能力，还在持续接入中
- 🚀支持对接口打标，进行相似接口推荐（类比大数据的检索=>召回=粗排=>精排=>重排）

### 管理员模块

- 接口实时上下线，信息变更，🚀流量保护
- 用户列表管理，数据权限变更，🚀后续支持量化人群等
- 接口充值，即修改用户的接口调用量

### 用户模块

- 注册，登录（包括账号密码和扫码等方式）
- 🚀对接个人公众号，提供获取动态码、关注回复等功能
- 🚀更改开发者密钥（ak，sk），下载开放客户端sdk
- 个人主页，设置个人基本信息，上传头像等

### 其他

- 数据分析：导入数据集并输入分析诉求，即可自动生成可视化图标以及分析结论，🚀后续支持接口调用分析
- 聚合搜索：集中搜索不同来源的接口文档和示例，提升用户接入效率
- 🚀第三方支付接入中，增加会员模块



## ✨ 技术亮点


### 聚合搜索
- 为提高聚合搜索接口的通用性，首先通过定义数据源接口来实现统一的数据源接入标准(比如新数据源必须支持分页)；当新数据源(比如视频)要接入时，只需使用适配器模式对其数据查询接口进行封装、以适配数据源接口，无须修改原有代码，提高了系统的可扩展性。
- 为减少代码的圈复杂度，使用注册器模式代替if else来管理多个数据源对象，调用方可根据名称轻松获取对象。

### mock数据生成和分析

- 将每种生成类型定义为一个 Builder：对于 Java代码生成器（JavaCodeBuilder、FrontendCodeBuilder），使用 FreeMarker 模板引擎来生成，自定义模板引擎配置Bean。
- 每种生成规则定义为一个 Generator，使用 DataGeneratorFactory（工厂模式）对多个 Generator 实例进行统一的创建和管理。使用单例模式节省每次调用时创建生成器的开销。
- 使用门面模式聚合各种生成类型，提供统一的生成调用和校验方法，减少重复请求。
- 于AIGC的输入Token限制，使用Easy Excel解析用户上传的XLSX表格数据文件并压缩为CSV，实测提高了20%的单次输入数据量。

### 接口调用

- 自定义本地缓存加载器，用于热点接口列表的缓存预热和变更等

- 为防止接口被恶意调用，设计API签名认证算法，为用户分配唯一ak/ sk以鉴权，保障调用的安全性、可溯源性（便于统计接口调用次数）。

- 为解决开发者调用成本过高的问题（须自己使用HTTP +封装签名去调用接口，平均20行左右代码），基于Spring Boot Starter开发了客户端SDK，一行代码即可调用接口，提高开发体验。

- 选用Spring Cloud Gateway作为API网关，实现了路由转发、访问控制、流量染色，并集中处理签名校验、请求参数校验、接口调用统计等业务逻辑，提高安全性的同时、便于系统开发维护。

- 统一全局异常，接口能力收敛：
  ![image-20230803231722291](https://ace-file.oss-cn-shenzhen.aliyuncs.com/articles/image-20230803222319726.png)

  ![image-20230803222319726](https://ace-file.oss-cn-shenzhen.aliyuncs.com/articles/image-20230803231722291.png)



### 其他

- 为了提高开发效率，选用Ant Design Pro脚手架快速搭建基础页面,并对原始模板进行瘦身、抽象为可复用的公共模板，便于后续同类项目的快速研发。
- 为了明确接口的返回，自定义统一的错误码,并封装了全局异常处理器，从而规范了异常返回、屏蔽了项目冗余的报错细节。
- 使用编辑距离算法实现了根据标签匹配最相似用户的功能，并通过优先队列来减少TOP N运算过程中的内存占用。
- 使用WxJava SDK代替HttpClient 方式实现OAuth2微信授权登录、模板消息推送，节省开发时间。



## 🔧 技术栈

### 前端

- 开发框架：React、Umi
- 脚手架：Ant Design Pro
- 组件库：Ant Design、Ant Design Components
- 语法扩展：TypeScript类型控制、Less
- 打包工具：Webpack
- 代码规范：ESLint、StyleLint、Prettier
- 图表：ECharts

- 依赖：monaco-editor 代码编辑器、copy-to-clipboard 剪切板复制

### 后端

- 语言：Java 8
- 开发框架：SpringBoot 2.x，Dubbo 分布式（RPC、Nacos）、Spring Cloud Gateway 微服务网关
- 数据访问：MyBatis + MyBatis Plus
- 项目管理：Maven
- 接口文档：Swagger + Knife4j
- 中间件：Redis、Elasticsearch、RabbitMQ
- 对象存储：阿里云COS
- web 服务：Nginx前端部署，宝塔 Linux运维
- 其他：Spring AOP、API 签名认证（Http 调用）、Spring Boot Starter（SDK 开发）
- 依赖：FreeMarker：模板引擎、Druid：SQL 解析、datafaker：模拟数据、Easy Excel：Excel 导入导出、Hutool、Apache Common Utils、Gson 等工具库、Redisson 分布式锁 、dataFaker 



## 🌈 平台接入

基于 Spring Boot Starter 开发，只需一行代码，使用强大的第三方接口能力！

### 快速开始

#### 0、引入 sdk

```xml
<dependency>
    <groupId>com.pc</groupId>
    <artifactId>api-client-sdk</artifactId>
    <version>0.0.3</version>
</dependency>
```

#### 1、注册后获取开发者密钥对

#### 2、初始化 ApiClient对象

方法 1：自主 new 对象

```java
String accessKey = "你的 accessKey";
String secretKey = "你的 secretKey";
ApiClient client = new ApiClient(accessKey, secretKey);
```

方法 2：通过配置注入对象

修改配置：

```yaml
api:
  client:
    access-key: 你的 access-key
    secret-key: 你的 secret-key
```

使用客户端对象：

```java
@Resource
private ApiClient client;
```

#### 3、构造请求参数

```java
User user = new User();
user.username("pengchang");
```

#### 4、获取响应结果

```java
String response = client.getUsernameByPost(devChatRequest);
System.out.println(response);
```



### API 文档

#### AI 对话

方法名：doChat

请求参数：

- message：要发送的消息，不超过 1024 字

响应结果：

- code：响应状态码
- data： 
  - content：对话结果内容
- message：响应信息

示例代码：

```java
// 构造请求
AIRequest request = new AIRequest();
devChatRequest.setModelId(1651468516836098050L);
request.setText("你好");

// 获取响应
String response = client.doChat(devChatRequest);
System.out.println(response;
```



### Http 接入

1. 获取 **AccessKey** 和 **SecretKey**

2. 请求信息

   URL ：[https://120.25.220.64:8090/api/ai/chat]()
   请求方法：POST
   请求头：

   | 请求头名称 | 值                                        | 示例值                                                       |
   | ---------- | ----------------------------------------- | ------------------------------------------------------------ |
   | accessKey  | accessKey                                 | 9vtw6bx8eh65uu5q62a8nhqdjf2asapp                             |
   | nonce      | 四位随机整数                              | 1234                                                         |
   | body       | 请求参数的 md5 加密值，请求参数详情见下方 | cf2623df4bf9d0485b0ab9392cbcef11                             |
   | timestamp  | 当前时间戳（单位秒）                      | 1687775370                                                   |
   | sign       | 签名，详情见下方                          | d10ce4n429778060c0af1d5f3f388d39953e74d996022e6182094fa3a84adbe9 |

3. 请求参数

   | text | 消息（问题） |
   | ---- | ------------ |
   | text | 消息（问题） |

4. 签名认证 

   签名的值是 **请求参数的 md5 加密值 + . + SecretKey 的值进行 SHA256 加密**

5. 示例代码（ Python ）

   ```python
   import hashlib
   import time
   import random
   import requests
   import json
   
   
   class DevChatRequest:
       def __init__(self, text: str):
           self.text = message
   
       def to_dict(self):
           return {
               "text" : self.text
           }
   
   class YuCongMingClient:
       HOST = 'https://120.25.220.64:8090/api/ai/chat'
   
       def __init__(self, access_key, secret_key):
           self.access_key = access_key
           self.secret_key = secret_key
   
       def do_chat(self, dev_chat_request: DevChatRequest):
           url = self.HOST
           json_data = json.dumps(dev_chat_request.to_dict())
           headers = self.get_headers(json_data)
           response = requests.post(url, headers=headers, data=json_data)
           return response.json()
   
       def get_headers(self, json_data):
           encode_body = self.encode_body(json_data)
           headers = {
               'Content-Type': 'application/json',
               'accessKey': self.access_key,
               'nonce': self.generate_nonce(),
               'body': encode_body,
               'timestamp': str(int(time.time())),
               'sign': self.generate_sign(encode_body)
           }
           return headers
   
       def generate_nonce(self):
           return str(random.randint(1000, 9999))
   
       def encode_body(self, json_data):
           md5 = hashlib.md5()
           md5.update(json_data.encode('utf-8'))
           return md5.hexdigest()
   
       def generate_sign(self, json_data):
           sha256 = hashlib.sha256()
           sign_str = json_data + "." +self.secret_key
           sha256.update(sign_str.encode('utf-8'))
           return sha256.hexdigest()
   
   
   if __name__ == '__main__':
       accessKey = "你的 accessKey";
       secretKey = "你的 secretKey";
       client = AIClient(accessKey, secretKey)
       print(client.do_chat(DevChatRequest("你好")))
   
   ```

6. 返回状态码

   | Code | Message | 解释 |
   | ---- | ------- | ---- |
   | 0    | ok      |      |

   | 40000 | 请求参数错误 | 检查参数名是否匹配、长度是否符合要求等 |
   | 40100 | 未登录 |
   | 40101 | 无权限 | 可能是由nonce、timestamp、sign 异常引起 |
   | 40400 | 请求数据不存在 | ak、模型等不存在 |
   | 40300 | 禁止访问 | 账号状态异常等 |
   | 50000 | 系统内部异常 | 网络抖动，数据库操作失败等 |
   | 50001 | 操作失败 | 限流等 |



## 🔧 愿景

- 支持业务研发人员通过开发自定义组件的方式扩展API网关能力。
- 业务研发人员配置好API，可以自动生成API的前后端交互文档和客户端SDK，方便前后端开发人员进行交互、联调。
- 提供加载自定义组件能力，支持业务完成一些自定义逻辑的扩展。 





## 🔗 相关链接

- [开源](https://github.com/pcpengchan)
- 对于一些 Bug 修复和细节优化，欢迎直接提交 PR 🌹 或联系作者,tel：18128332989
