# Transaction_Home_Task

## 实现功能
* 增删改查（带批量）的交易系统API
* 领域驱动设计风格的项目分包
* 统一API返回
* 统一异常处理 (业务异常 + 校验异常)
    * 使用resource bundle处理错误信息
    * 统一的错误码管理枚举
* 内存数据库 (H2) 与缓存 (Caffeine)
* 深分页优化
* 集成Spring Data JPA
* 数据对象转换器
* 集成 OpenAPI v3 / Swagger UI
    * http://localhost:8000/swagger-ui/index.html#/
* 单元测试

## API列表
也请查看 [Swagger-UI](http://localhost:8000/swagger-ui/index.html#/) API文档。
* 创建交易
* 查询交易
* 修改交易
* 删除交易
* 批量创建交易
* 分页查询交易
* 批量修改交易
* 批量删除交易

## 单元测试
### 单交易操作接口
### 批量操作接口

## 单元测试
* 运行环境：
    * AMD Ryzen 7 3800X @ 4.10GHz
    * Crucial 4 * 8GB DRAM @ 3200 MHz (18-18-41-59)
    * ROG Strix X570-F Gaming
    * TOSHIBA HDWD130

## 可以改进的点？
* 使用 @SQLDelete 或者自己实现 ORM 层面的软删除处理
    * 其实，软删除更多时候需要结合业务去考虑。
* 自动处理集合的缓存添加与释放
    * 需要修改 Spring Cache 的一些类, 可能要自己实现 ``CacheManager``
    * 或者从头到尾自己实现 - 使用长得类似 ``@CollectionCacheable`` 这样的注解？

## 没时间写了
* 国际化处理 + resource bundle国际化处理
* LogBack 异步日志
* 切面日志