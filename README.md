* 1. [Restful Api 需求](#RestfulApi)
* 2. [商品接口](#商品接口)
	* 2.1. [插入或根据id更新数据到ElasticSearch](#ElasticSearch-1)
	* 2.2. [查询数据，分页返回](#)
	* 2.3. [根据ID返回数据](#ID)
	* 2.4. [权限](#-1)
* 3. [认证接口](#认证接口)
	* 3.1. [获取认证方列表](#获取认证方列表)
* 4. [仲裁接口](#仲裁接口)
	* 4.1. [获取仲裁方列表](#获取仲裁方列表)

<!-- vscode-markdown-toc-config
	numbering=true
	autoSave=true
	/vscode-markdown-toc-config -->
<!-- /vscode-markdown-toc -->

# dataset

dataset是一个数据集合，数据提供方可以插入数据到搜索引擎，数据需求方可以查询数据。


##  1. <a name='RestfulApi'></a>Restful Api 需求

暴露给用户的是 `Restful Api`。
有以下场景：

1. 数据提供方插入数据或根据id更新数据。
2. 根据Tag查询并分页展示。
3. 根据ID查询数据详细信息。

## 商品接口

###  2.1. <a name='ElasticSearch-1'></a>插入或根据id更新数据到ElasticSearch

```
url：/api/v1/dataset
method：PUT
```

请求：

```source-json
{
	"id":"fa20c972950f46cdba99ca521f0c49fa",
	"ontid":"did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8",
	"data":["青少年疾病","骨科","骷髅腿"],
	"price":"1",
	"coin":"ong"
}
```

| Field Name | Type | Description |
|---|---|---|
|id|String|标识一条数据，查询系统生成|
|ontid|String|数据所有者的ontid|
|data|List|数据属性|
|Price|String|价格|
|coin|String|货币种类|

响应：

```source-json
{
  "code":0,
  "msg":"SUCCESS",
  "result": "ID"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | String | 成功返回数据ID，失败返回"" |


###  2.2. <a name=''></a>查询数据，分页返回

```
url：/api/v1/dataset
method：POST
```
根据存储数据的属性以及匹配百分比查询并返回分页数据。

请求：

```source-json
{
	"pageIndex": 0,
	"pageSize": 10,
	"queryParams": [{
			"percent": 100,
			"text": "上海"
		},
		{
			"percent": 100,
			"text": "青少年"
		}
	]
}

```

| Field Name | Type | Description |
|---|---|---|
|pageIndex|Integer|起始页|
|pageSize|Integer|每页数据条数|
|percent|Integer|匹配度|
|text|String|数据属性|

响应：

```source-json
{
  "code": 0,
  "msg": "SUCCESS",
  "result": {
    "currentPage": 0,
    "pageSize": 10,
    "recordCount": 1,
    "recordList": [
      {
              "id": "fa20c972950f46cdba99ca521f0c49fa",
              "ontid": "did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8",
              "tag0": "上海第九人民医院",
              "tag1": "青少年疾病",
              "tag2": "骨科",
              "createTime": "2019-04-01 11:58:09",
              "price": "1",
              "coin": "ont",
              "tagValue":["上海第九人民医院","青少年疾病","骨科"]
            }
          ],
    "pageCount": 1,
    "beginPageIndex": 1,
    "endPageIndex": 1
        }
      }
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | Object | 返回分页数据 |
|records|Array|Array里面每个数据和插入的数据一个格式|
|currentPage|int|当前页|
|pageSize|int|每页数据条数|
|recordCount|int|总数据条数|
|pageCount|int|总页数|
|beginPageIndex|int|起始页|
|endPageIndex|int|结束页|

###  2.3. <a name='ID'></a>根据ID返回数据

```
url:/api/v1/dataset/{id}
method:Get
```

响应：

```source-json
{
  "code": 0,
  "msg": "SUCCESS",
  "result": {
    "id": "fa20c972950f46cdba99ca521f0c49fa",
    "ontid": "did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8",
    "tag0": "上海第九人民医院",
    "tag1": "青少年疾病",
    "tag2": "骨科",
    "createTime": "2019-04-01 11:58:09",
    "price": "1",
    "coin": "ont",
    "tagValue":["上海第九人民医院","青少年疾病","骨科"]
  }
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | Object | 返回数据 |

###  2.4. <a name='-1'></a>权限

目前Restful API没有设计权限系统，由使用代码的第三方自己实现

## 认证接口

###  获取认证方列表
```
url：/api/v1/certifier
method：GET
```

响应：

```source-json
{
    "code": 0,
    "msg": "SUCCESS",
    "result": [
        {
            "id": "1",
            "ontid": "did:ont:AcdBfqe7SG8xn4wfGrtUbbBDxw2x1e8UKm"
        }
    ]
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回数据认证方列表，失败返回"" |

## 仲裁接口

###  获取仲裁方列表
```
url：/api/v1/judger
method：GET
```

响应：

```source-json
{
    "code": 0,
    "msg": "SUCCESS",
    "result": [
        {
            "id": "1",
            "ontid": "did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8"
        }
    ]
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回数据仲裁方列表，失败返回"" |