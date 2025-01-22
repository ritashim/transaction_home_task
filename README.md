# Transaction_Home_Task

[中文版](README_zh_CN.md)

## Features
* Transaction Service with CRUD (w/ Batch) APIs
* Domain-driven-design style structured project 
* Unified API response
* Unified exception handling / validation handling
    * With error messages handled by resource bundle
    * With unified error codes management
* Memory DB (H2) with caching (Caffeine)
* Pagination optimized for deep pagination
* Spring Data JPA integration
* Data Object Conversions
* OpenAPI v3 / Swagger UI Integration
  * http://localhost:8000/swagger-ui/index.html#/
* Unit Tests

## APIs
Please also check out [Swagger-UI](http://localhost:8000/swagger-ui/index.html#/) API documentation.
* Create transaction
* Query transaction
* Update transaction
* Delete transaction
* Batch create transaction
* Paged query transaction
* Batch update transaction
* Batch delete transaction

## Deploy
### Dependency
* Docker

### Packaging
```sh
mvn install
```

### Run
```sh
docker run -p 8000:8000 transaction:0.0.1-SNAPSHOT
```

## Dependency
* Spring
  * Spring Boot Starter Web
  * Validation
  * Cache
  * JPA
  * Test
* Persistence
  * H2
  * QueryDsl
* Caching
  * Caffeine
* Data Object Convert
  * MapStruct
* Helpers / Utils
  * Lombok
  * Hutool
* API UI
  * OpenAPI v3

## Unit Tests
### Single Transaction APIs / Batch Transaction APIs
![Unit Test 01](/document_resources/unit_test_1.png)

## Stress Tests
* Running on:
  * AMD Ryzen 7 3800X @ 4.10GHz
  * Crucial 4 * 8GB DRAM @ 3200 MHz (18-18-41-59)
  * ROG Strix X570-F Gaming
  * TOSHIBA HDWD130
* Docker Resource Config
  * 8C16T, 16G Memory
### Batch Data Insert
![Stress Test 01](/document_resources/stress_test_1.png)
### Data Insert (50 Threads, 2500 qps)
![Stress Test 02](/document_resources/stress_test_2.png)

## What could be improved
* Deal with soft deletion: Using @SQLDelete or implement by self
  * Soft delete should be discussed case-by-case on specific business/background.
* Deal with cache: Implement automatic, batch caching / evict 
  * Customize Spring Cache infrastructure, may need customize ``CacheManager``
  * Or implement by self (Something like ``@CollectionCacheable``)

## What is not completed
* i18n @ resource bundle
* Async Log (LogBack)
* API Logging via Spring AOP
