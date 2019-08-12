* 0. [Restful Api 需求](#RestfulApi)
* 1. [注册接口](#注册接口)
	* 1.1. [注册ons](#注册ons)
	* 1.2. [获取注册ons交易参数](#获取注册ons交易参数)
	* 1.3. [发送注册交易](#发送注册交易)
	* 1.4. [查询注册是否成功](#查询注册是否成功)
* 2. [登录接口](#登录接口)
	* 2.1. [获取message](#获取message)
	* 2.2. [根据ontid和主域名获取ons列表](#根据ontid和主域名获取ons列表)
	* 2.3. [回调验证](#回调验证)
	* 2.4. [查询登录是否成功](#查询登录是否成功)
* 3. [商品接口](#商品接口)
	* 3.1. [插入或根据id更新数据到ElasticSearch](#插入或根据id更新数据到ElasticSearch)
	* 3.2. [根据ID返回数据](#根据ID返回数据)
	* 3.3. [根据卖家ontid返回数据](#根据卖家ontid返回数据)
	* 3.4. [注册dataId](#注册dataId)
	* 3.5. [回调返回交易签名数据并发送交易](#回调返回交易签名数据并发送交易)
	* 3.6. [根据dataId查询数据](#根据dataId查询数据)
* 4. [认证接口](#认证接口)
	* 4.1. [获取认证方列表](#获取认证方列表)
	* 4.2. [认证人获取待认证列表](#认证人获取待认证列表)
	* 4.3. [认证数据](#认证数据)
* 5. [订单接口](#订单接口)
	* 5.1. [挂单授权MP生成token](#挂单授权MP生成token)
	* 5.2. [查询所有挂单](#查询所有挂单)
	* 5.3. [查询自己的订单](#查询自己的订单)
	* 5.4. [购买数据](#购买数据)
    * 5.5. [查看数据](#查看数据)
    * 5.6. [查询当前tokenId](#查询当前tokenId)
    * 5.7. [查询token剩余流转次数和访问次数](#查询token剩余流转次数和访问次数)
    * 5.8. [二次挂单创建order](#二次挂单创建order)
    * 5.9. [查询二手商品](#查询二手商品)
    * 5.10. [购买二手商品](#购买二手商品)
* 6. [仲裁接口](#仲裁接口)
	* 6.1. [获取仲裁方列表](#获取仲裁方列表)
	* 6.2. [获取待仲裁列表](#获取待仲裁列表)
	* 6.3. [发送仲裁结果](#发送仲裁结果)
* 7. [合约调用接口](#合约调用接口)
	* 7.1. [构造交易](#构造交易)
	* 7.2. [发送交易](#发送交易)
	* 7.3. [注册dataId](#注册dataId)

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

## 注册接口

###  注册ons

```
url：/api/v1/ons/{ons}
method：Get
```

请求：
| Field Name | Type | Description |
|---|---|---|
|ons         |String|需要注册的域名|

响应：

```source-json
{
    "action": "registerDomain",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "callback": "http://192.168.3.121:7878/api/v1/ons/invoke",
        "id": "a0308abd-d57e-41fe-9554-5fe6435db2fe",
        "qrcodeUrl": "http://192.168.3.121:7878/api/v1/ons/qrcode/a0308abd-d57e-41fe-9554-5fe6435db2fe"
    },
    "version": "v1"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action    | String | 动作标志                      |
| error      | int    | 错误码                        |
| desc       | String | 成功为SUCCESS，失败为错误描述 |
| result    | String | 成功返回记录信息，失败返回""  |
| callback  | String | 回调url地址                   |
| id        | String | 记录的id                      |
| qrcodeUrl | String | 获取交易参数的地址            |
| version   | String | 版本号                        |


###  获取注册ons交易参数

```
url：/api/v1/ons/qrcode/{id}
method：Get
```

请求：
| Field Name | Type | Description |
|---|---|---|
|id         |String|记录的id|

响应：

```source-json
{
	"action": "signTransaction",
	"id": "80edaf95-4706-41f1-a25b-57447e4e3094",
	"params": {
		"invokeConfig": {
			"gasLimit": 40000,
			"contractHash": "fb12993d6f13a2ec911f3bbfe534be90e4deeca4",
			"functions": [{
				"args": [{
					"name": "fulldomain",
					"value": "String:ning.on.ont"
				}, {
					"name": "registerdid",
					"value": "String:%ontid"
				}, {
					"name": "idx",
					"value": 1
				}, {
					"name": "validto",
					"value": -1
				}],
				"operation": "registerDomain"
			}],
			"payer": "AcdBfqe7SG8xn4wfGrtUbbBDxw2x1e8UKm",
			"gasPrice": 500
		},
		"ontidSign": true,
		"callback": "http://192.168.3.121:7878/api/v1/ons/invoke"
	},
	"version": "v1.0.0"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action    | String | 动作标志                      |
| id        | String | 记录的id                      |
| params    | Object | 交易参数                      |
| version   | String | 版本号                        |


###  发送注册交易

```
url：/api/v1/ons/invoke
method：Post
```

请求：
```source-json
{
	"action": "invoke",
	"id": "10ba038e-48da-487b-96e8-8d3b99b6d18a",
	"version": "v1.0.0",
	"params": {
		"signedTx": "00d1ed6aa95cf401000000000000409c000000000000f5f7b705b03ae46e48f89c2b99e79fa4391536fe6e0360ea00016f51c10331313151c114000000000000000000000000000000000000000214010b5816b180ffb41e3889b6f42aeaf31fd63209143fc9fa9491df7e93b94db2df99e6af2d67ad34b756c10973656e64546f6b656e67bae44577a468b5bfd00ebbaba7d91204204828470000"
	}
}
```
| Field Name | Type | Description |
|---|---|---|
| action    | String | 动作标志                      |
| id        | String | 记录的id                      |
| version   | String | 版本号                        |
| params    | String | 参数                          |
| signedTx  | String | 签名后的交易hash              |


###  查询注册是否成功

```
url：/api/v1/ons/result/{id}
method：Get
```

请求：
| Field Name | Type | Description |
|---|---|---|
|id          |String|记录的id     |

响应：

```source-json
{
    "action": "registerResult",
    "error": 0,
    "desc": "SUCCESS",
    "result": "1",
    "version": "v1"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action    | String | 动作标志                      |
| error      | int    | 错误码                        |
| desc       | String | 成功为SUCCESS，失败为错误描述 |
| result    | String | 成功返回"1"，失败返回"0"      |
| version   | String | 版本号                        |


## 登录接口

###  获取message

```
url：/api/v1/ons/login
method：Get
```

响应：

```source-json
{
    "action": "getMessage",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "callback": "http://192.168.3.121:7878/api/v1/login/callback",
        "id": "e1471264-b2d1-45fa-9eb5-1a8ad6ce2b6c",
        "message": "hello 1561537241660"
    },
    "version": "v1"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action    | String | 动作标志                      |
| error      | int    | 错误码                        |
| desc       | String | 成功为SUCCESS，失败为错误描述 |
| result    | String | 成功返回记录信息，失败返回""  |
| callback  | String | 回调url地址                   |
| id        | String | 记录的id                      |
| message   | String | 随机消息                      |
| version   | String | 版本号                        |

###  根据ontid和主域名获取ons列表

```
url：/api/v1/ons/list?domain=on.ont&ontid=did:ont:AGWYQHd4bzyhrbpeYCMsxXYQcJo95VtR5q
method：Get
```

请求：

| Field Name | Type | Description |
|---|---|---|
| ontid    | String | 用户ontid                      |
| domain   | String | 网站主域名                     |

响应：

```source-json
{
    "action": "getOnsList",
    "error": 0,
    "desc": "SUCCESS",
    "result": [
        "test.ont.io",
        "2222.ont.io",
        "1111.ont.io"
    ],
    "version": "v1"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action    | String | 动作标志                      |
| error      | int    | 错误码                        |
| desc       | String | 成功为SUCCESS，失败为错误描述 |
| result    | String | 成功返回域名列表，失败返回""  |
| version   | String | 版本号                        |


###  回调验证

```
url：/api/v1/ons/login/callback
method：Post
```

请求：
```source-json
{
	"action": "login",
	"version": "v1.0.0",
	"id": "10ba038e-48da-487b-96e8-8d3b99b6d18a",
	"params": {
		"type": "ontid",
		"user": "did:ont:AGWYQHd4bzyhrbpeYCMsxXYQcJo95VtR5q",
		"domain": "test.ont.io",
		"message": "helloworld",
		"publickey": "0205c8fff4b1d21f4b2ec3b48cf88004e38402933d7e914b2a0eda0de15e73ba61",
		"signature": "01abd7ea9d79c857cd838cabbbaad3efb44a6fc4f5a5ef52ea8461d6c055b8a7cf324d1a58962988709705cefe40df5b26e88af3ca387ec5036ec7f5e6640a1754"
	}
}
```
| Field Name | Type | Description |
|---|---|---|
| action    | String | 动作标志                      |
| version   | String | 版本号                        |
| id        | String | 记录的id                      |
| params    | Object | 回调验证参数                  |
| type      | String | 类型                          |
| user      | String | 用户ontid                     |
| domain    | String | 用户域名                      |
| message   | String | 验签消息                      |
| publickey | String | 公钥                          |
| signature | String | 签名数据                      |

响应：

```source-json
{
    "result": true,
    "action": "login",
    "id": "10ba038e-48da-487b-96e8-8d3b99b6d18a",
    "error": 0,
    "desc": "SUCCESS"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action    | String | 动作标志                      |
| error     | int    | 错误码                        |
| desc      | String | 成功为SUCCESS，失败为错误描述 |
| result    | String | 成功返回true，失败返回""      |
| version   | String | 版本号                        |


###  查询登录是否成功

```
url：/api/v1/ons/login/result/{id}
method：Get
```

请求：
| Field Name | Type | Description |
|---|---|---|
|id          |String|记录的id     |

响应：

```source-json
{
    "action": "loginResult",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "result": "1",
        "ons": "test.ont.io",
        "ontid": "did:ont:AGWYQHd4bzyhrbpeYCMsxXYQcJo95VtR5q"
    },
    "version": "v1"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action    | String | 动作标志                      |
| error      | int    | 错误码                        |
| desc       | String | 成功为SUCCESS，失败为错误描述 |
| result    | String | 成功返回"1"，失败返回"0",没有查到信息返回null(需要继续请求)      |
| ons       | String | 用户域名                      |
| ontid     | String | 用户ontid                     |
| version   | String | 版本号                        |


## 商品接口

###  插入或根据id更新数据到ElasticSearch

```
url：/api/v1/dataset
method：PUT
```

请求：

```source-json
{
	"id": "",
	"ontid": "did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8",
	"data": {
		"desc": "descrption for data",
		"img": "http://image.image.com/",
		"keywords": ["keyword1", "keyword2"],
		"metadata": "metadata",
		"name": "data name"
	},
	"certifier": "did:ont:AMbABKSWfcwCvHWuJ3XbyHAPNLsTvP6q8w",
	"dataSource": "www.datasource.com"
}
```

| Field Name | Type | Description |
|---|---|---|
|id         |String|标识一条数据，查询系统生成|
|ontid      |String|数据所有者的ontid         |
|data       |Map   |数据属性                  |
|desc       |String|数据属描述                |
|img        |String|数据图片                  |
|keywords   |List  |数据标签                  |
|metadata   |String|元数据                    |
|name       |String|数据名称                  |
|certifier  |String|数据认证方                |
|dataSource |String|数据源                    |

响应：

```source-json
{
    "action": "addOrUpdate",
    "error": 0,
    "desc": "SUCCESS",
    "result": "fa20c972950f46cdba99ca521f0c49fa",
    "version": "v1"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action    | String | 动作标志                      |
| error      | int    | 错误码                        |
| desc       | String | 成功为SUCCESS，失败为错误描述 |
| result    | String | 成功返回数据ID，失败返回""    |
| version   | String | 版本号                        |


###  根据ID返回数据

```
url:/api/v1/dataset/{id}
method:GET
```
| Field Name | Type | Description |
|---|---|---|
|id|String|数据id|

响应：

```source-json
{
    "action": "getData",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "isCertificated": 0,
        "dataId": "",
        "tokenRange": "",
        "data": {
            "desc": "descrption for data",
            "img": "http://image.image.com/",
            "keywords": ["keyword1","keyword2"],
            "metadata": "metadata",
            "name": "data name"
        },
        "createTime": "2019-06-03 10:59:55",
        "certifier": "did:ont:AMbABKSWfcwCvHWuJ3XbyHAPNLsTvP6q8w",
        "id": "fa20c972950f46cdba99ca521f0c49fa",
        "state": 0,
        "ontid": "did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8"
    },
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | Object | 返回数据 |
| version   | String | 版本号                        |

### 根据卖家ontid返回数据
```
url：/api/v1/dataset/provider/{ontid}?pageNum=&pageSize=
method：GET
```

| Field Name | Type | Description |
|---|---|---|
|ontid|String|卖家ontid|
|pageNum|Integer|起始页|
|pageSize|Integer|每页记录数|


响应：

```source-json
{
    "action": "getDataByProvider",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "currentPage": 0,
        "pageSize": 5,
        "recordCount": 1,
        "recordList": [
            {
                "isCertificated": 0,
                "dataId": "",
                "tokenRange": "",
                "data": {
                    "desc": "descrption for data",
                    "img": "http://image.image.com/",
                    "keywords": ["keyword1","keyword2"],
                    "metadata": "metadata",
                    "name": "data name"
                },
                "createTime": "2019-06-03 10:59:55",
                "certifier": "did:ont:AMbABKSWfcwCvHWuJ3XbyHAPNLsTvP6q8w",
                "id": "fa20c972950f46cdba99ca521f0c49fa",
                "state": 0,
                "dataSource": "www.datasource.com",
                "ontid": "did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8"
            }
        ],
        "pageCount": 1,
        "beginPageIndex": 1,
        "endPageIndex": 1
    },
    "version": "v1"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回分页数据列表，失败返回"" |
| version   | String | 版本号                        |


###  注册dataId

```
url：/api/v1/dataset/dataId
method：POST
```

请求：

```source-json
{
{
	"dataIdVo": {
		"dataId": "did:ont:ANYEUuWmPFrHM8XPYLJf2Z1PGAKSyBJ2G9",
		"id": "b7a6d295f5e74b34a4911d5db132f3e6",
		"ontid": "did:ont:ARCESVnP8Lbf6S7FuTei3smA35EQYog4LR",
		"pubKey": 1
	},
	"orderVo": {
		"id": "b7a6d295f5e74b34a4911d5db132f3e6",
		"amount": 9999,
		"contractVo": {
			"argsList": [{
				"name": "dataId",
				"value": "String:did:ont:ANYEUuWmPFrHM8XPYLJf2Z1PGAKSyBJ2G9"
			}, {
				"name": "index",
				"value": 1
			}, {
				"name": "symbol",
				"value": "String:NTF"
			}, {
				"name": "name",
				"value": "String:newCon"
			}, {
				"name": "authAmount ",
				"value": 9999
			}, {
				"name": "price",
				"value": 1
			}, {
				"name": "transferCount",
				"value": 999
			}, {
				"name": "accessCount",
				"value": 999
			}, {
				"name": "expireTime",
				"value": 0
			}, {
				"name": "makerTokenHash",
				"value": "ByteArray:3e7d3d82df5e1f951610ffa605af76846802fbae"
			}, {
				"name": "makerReceiveAddress",
				"value": "Address:ARCESVnP8Lbf6S7FuTei3smA35EQYog4LR"
			}, {
				"name": "mpReceiveAddress",
				"value": "Address:AR9NDnK3iMSZodbENnt7eX5TJ2s27fnHra"
			}, {
				"name": "OJList",
				"value": ["Address:ARCESVnP8Lbf6S7FuTei3smA35EQYog4LR"]
			}],
			"contractHash": "f261464e2cd21c2ab9c06fa3e627ce03c7715ec9",
			"method": "authOrder"
		},

		"ojList": [
			"ARCESVnP8Lbf6S7FuTei3smA35EQYog4LR"
		],
		"price": "1",
		"token": "ong"
	}
}
}
```

| Field Name | Type | Description |
|---|---|---|
|id|String|数据id|
|dataId|String|为数据生成的dataId|
|ontid|String|用户ontid|
|pubKeys|String|签名公钥,默认1|
|amount|int|授权数量|
|ojList|List|仲裁者列表|
|price|String|价格|
|token|String|默认ong|
|orderVo|Obj|授权上架参数|

响应：

```source-json
{
    "action": "createDataId",
    "error": 0,
    "desc": "SUCCESS",
    "result": ["7d7c4f01e0fa3c3203424644697b8d2266f337fb25b3ae89bc9575194a5d5ce7","7d7c4f01e0fa3c3203424644697b8d2266f337fb25b3ae89bc9575194a5d5ce7"],
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 注册dataId和授权上架的交易hex |
| version   | String | 版本号                        |


###  回调返回交易签名数据并发送交易

```
url：/api/v1/dataset/dataId/invoke
method：Post
```
请求
````json
{
	"action": "string",
	"id": "string",
	"params": [{
		"type": "ontid or address",
		"user": "did:ont:AUEKhXNsoAT27HJwwqFGbpRy8QLHUMBMPz or AUEKhXNsoAT27HJwwqFGbpRy8QLHUMBMPz",
		"message": "helloworld",
		"publickey": "0205c8fff4b1d21f4b2ec3b48cf88004e38402933d7e914b2a0eda0de15e73ba61",
		"signature": "01abd7ea9d79c857cd838cabbbaad3efb44a6fc4f5a5ef52ea8461d6c055b8a7cf324d1a58962988709705cefe40df5b26e88af3ca387ec5036ec7f5e6640a1754"
	}, {
		"type": "ontid or address",
		"user": "did:ont:AUEKhXNsoAT27HJwwqFGbpRy8QLHUMBMPz or AUEKhXNsoAT27HJwwqFGbpRy8QLHUMBMPz",
		"message": "helloworld",
		"publickey": "0205c8fff4b1d21f4b2ec3b48cf88004e38402933d7e914b2a0eda0de15e73ba61",
		"signature": "01abd7ea9d79c857cd838cabbbaad3efb44a6fc4f5a5ef52ea8461d6c055b8a7cf324d1a58962988709705cefe40df5b26e88af3ca387ec5036ec7f5e6640a1754"
	}],
	"version": "string"
}
````
| Field Name | Type | Description |
|---|---|---|
|publickey|String|ontid公钥|
|signature|String|签名数据|

响应：

```source-json
{
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
|  | obj | 成功返回空{}，失败返回错误信息 |


###  根据dataId查询数据

```
url：/api/v1/dataset/data/{dataId}
method：GET
```

| Field Name | Type | Description |
|---|---|---|
|dataId|String|数据的dataId|

响应：

```source-json
{
    "action": "getDatabyDataId",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "isCertificated": 0,
        "dataId": "did:ont:AL2pVs2zwogCvBD4GdsZD6woCkJdzPZJZ5",
        "tokenRange": "15,16",
        "data": {
            "desc": "descrption for data",
            "img": "http://image.image.com/",
            "keywords": ["keyword1","keyword2"],
            "metadata": "metadata",
            "name": "data name"
        },
        "createTime": "2019-06-03 10:59:55",
        "certifier": "did:ont:AMbABKSWfcwCvHWuJ3XbyHAPNLsTvP6q8w",
        "id": "fa20c972950f46cdba99ca521f0c49fa",
        "state": 1,
        "ontid": "did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8"
    },
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | Object | 返回数据 |
| version   | String | 版本号        |


## 认证接口

###  获取认证方列表
```
url：/api/v1/certifier
method：GET
```

响应：

```source-json
{
    "error": 0,
    "desc": "SUCCESS",
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
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回数据认证方列表，失败返回"" |


###  认证人获取待认证列表
```
url：/api/v1/certifier/{certifier}?pageNum=&pageSize=
method：GET
```
| Field Name | Type | Description |
|---|---|---|
|certifier|String|认证人ontid|
|pageNum|Integer|起始页|
|pageSize|Integer|每页记录数|


响应：

```source-json
{
    "action": "getToBeCertificated",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "currentPage": 0,
        "pageSize": 5,
        "recordCount": 1,
        "recordList": [
            {
                "isCertificated": 0,
                "dataId": "",
                "tokenRange": "",
                "data": {
                    "desc": "descrption for data",
                    "img": "http://image.image.com/",
                    "keywords": ["keyword1","keyword2"],
                    "metadata": "metadata",
                    "name": "data name"
                },
                "createTime": "2019-06-03 10:59:55",
                "certifier": "did:ont:AMbABKSWfcwCvHWuJ3XbyHAPNLsTvP6q8w",
                "id": "fa20c972950f46cdba99ca521f0c49fa",
                "state": 0,
                "dataSource": "www.datasource.com",
                "ontid": "did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8"
            }
        ],
        "pageCount": 1,
        "beginPageIndex": 1,
        "endPageIndex": 1
    },
    "version": "v1"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回待认证分页列表，失败返回"" |
| version   | String | 版本号                        |


### 认证数据
```
url：/api/v1/certifier
method：POST
```
请求：
```source-json
{
	"id": "135dfa98d52846f581d3aff19b9f6804",
	"certifier": "did:ont:AcdBfqe7SG8xn4wfGrtUbbBDxw2x1e8UKm"
}
```

| Field Name | Type | Description |
|---|---|---|
|id|String|数据id|
|certifier|String|认证人ontid|


响应：

```source-json
{
    "action": "certificate",
    "error": 0,
    "desc": "SUCCESS",
    "result": "SUCCESS",
    "version": "v1"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | String | 成功返回"SUCCESS"，失败返回"" |
| version   | String | 版本号                        |


## 订单接口

###  挂单授权MP生成token
```
url：/api/v1/order
method：POST
```

请求：
```source-json
{
	"id": "d0cc3f3e0855447990dde11e2ad85d88",
	"token": "ong",
	"price": "100",
	"amount":10,
	"ojList": ["did:ont:AJYEUcQi9jp157QXNWpKybwkCVSTuTNsh1","did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8"],
	"sigVo": {
		"txHex": "00d1ed6aa95cf401000000000000409c000000000000f5f7b705b03ae46e48f89c2b99e79fa4391536fe6e0360ea00016f51c10331313151c114000000000000000000000000000000000000000214010b5816b180ffb41e3889b6f42aeaf31fd63209143fc9fa9491df7e93b94db2df99e6af2d67ad34b756c10973656e64546f6b656e67bae44577a468b5bfd00ebbaba7d91204204828470000",
		"pubKeys": "03edaa022ce0f2020ec92e68ce47de932a804b4a5f240989fb612b63685d1bc8da",
		"sigData": "01e42dbefd28087bb42ad8667e6ed3a56e23cec70b0289c7d40e22948d7985bbc0713c1f5f19d92b706b6fe57a7ceaa23fc2eba99b0673160d271ee43ad55ece19"
	}
}
```
| Field Name | Type | Description |
|---|---|---|
|id|String|数据的id标识|
|token|String|售卖币种|
|price|String|售卖价格|
|amount|int|售卖数量|
|ojList|List|仲裁方备选列表|
|sigVo|Map|挂单交易签名信息|

响应：

```source-json
{
    "action": "createOrder",
    "error": 0,
    "desc": "SUCCESS",
    "result": "96a5dc65ec4e6efebcae8d2a802d759c3b33ddd398c3921702949b564a33923a",
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回挂单交易hash，失败返回"" |
| version   | String | 版本号                        |


###  查询所有挂单
```
url：/api/v1/order/all
method：POST
```

请求：
```source-json
{
	"pageNum": 0,
	"pageSize": 5,
	"queryParams": [{
		"text": "keyword1",
		"columnIndex": 0,
		"percent": 100
	}, {
		"text": "keyword2",
		"columnIndex": 1,
		"percent": 100
	}]
}
```
| Field Name | Type | Description |
|---|---|---|
|pageNum|Integer|起始页|
|pageSize|Integer|每页记录数|
|queryParams|List|查询条件|

响应：

```source-json
{
    "action": "getAllOrder",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "currentPage": 0,
        "pageSize": 5,
        "recordCount": 1,
        "recordList": [
            {
                "keywords":["keyword1","keyword2"],
                "tokenId": "1",
                "confirmTime": "",
                "token": "ong",
                "boughtTime": "",
                "expireTime": "",
                "dataId": "did:ont:Aac8jSxyF81hxFEyRuiXSp5TvzN9MVqAoT",
                "createTime": "2019-06-03 15:10:28",
                "cancelTime": "",
                "price": "100",
                "demanderOntid": "",
                "judger": [
                    "did:ont:AJYEUcQi9jp157QXNWpKybwkCVSTuTNsh1",
                    "did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8"
                ],
                "providerOntid": "did:ont:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4",
                "state": "1",
                "id": "D4D1FA099FD140519AA71F942465CBF9"
            }
        ],
        "pageCount": 1,
        "beginPageIndex": 1,
        "endPageIndex": 1
    },
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回已挂单数据，失败返回"" |
| version   | String | 版本号                        |


###  查询自己的订单
```
url：/api/v1/order/self
method：POST
```

请求：
```source-json
{
	"pageNum": 0,
	"pageSize": 5,
	"type":2,
	"ontid":"did:ont:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4"
}
```
| Field Name | Type | Description |
|---|---|---|
|pageNum|Integer|起始页|
|pageSize|Integer|每页记录数|
|type|Integer|用户类型：1-买家；2-卖家|
|ontid|String|用户ontid|

响应：

```source-json
{
    "action": "findSelfOrder",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "currentPage": 0,
        "pageSize": 5,
        "recordCount": 1,
        "recordList": [
            {
                "keywords":["keyword1","keyword2"],
                "tokenId": "1",
                "orderId": "ba24bb9d3dee415e9c9c12acaf78c973",
                "confirmTime": "",
                "tokenHash": "0000000000000000000000000000000000000002",
                "boughtTime": "",
                "expireTime": "",
                "dataId": "did:ont:Aac8jSxyF81hxFEyRuiXSp5TvzN9MVqAoT",
                "createTime": "2019-06-03 15:10:28",
                "cancelTime": "",
                "price": "100",
                "demanderOntid": "did:ont:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4",
                "judger": ["did:ont:AJYEUcQi9jp157QXNWpKybwkCVSTuTNsh1","did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8"],
                "providerOntid": "did:ont:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4",
                "state": "1",
                "id": "D4D1FA099FD140519AA71F942465CBF9",
                "isExpired": "0"
            }
        ],
        "pageCount": 1,
        "beginPageIndex": 1,
        "endPageIndex": 1
    },
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回自己的订单，失败返回"" |
| version   | String | 版本号                        |


###  购买数据
```
url：/api/v1/order/purchase
method：POST
```

请求：
```source-json
{
	"id": "D4D1FA099FD140519AA71F942465CBF9",
	"demanderOntid": "did:ont:AMZvjuJNxD21uVgJ5c8VDdGUiT4TudtLFU",
	"demanderAddress": "AMZvjuJNxD21uVgJ5c8VDdGUiT4TudtLFU",
	"judger": "did:ont:AJYEUcQi9jp157QXNWpKybwkCVSTuTNsh1",
	"name": "name for order",
    "desc": "descrption for order",
    "img": "http://image.image.com/",
    "keywords": ["keyword1","keyword2"],
	"sigVo": {
		"txHex": "00d1ed6aa95cf401000000000000409c000000000000f5f7b705b03ae46e48f89c2b99e79fa4391536fe6e0360ea00016f51c10331313151c114000000000000000000000000000000000000000214010b5816b180ffb41e3889b6f42aeaf31fd63209143fc9fa9491df7e93b94db2df99e6af2d67ad34b756c10973656e64546f6b656e67bae44577a468b5bfd00ebbaba7d91204204828470000",
		"pubKeys": "03edaa022ce0f2020ec92e68ce47de932a804b4a5f240989fb612b63685d1bc8da",
		"sigData": "01e42dbefd28087bb42ad8667e6ed3a56e23cec70b0289c7d40e22948d7985bbc0713c1f5f19d92b706b6fe57a7ceaa23fc2eba99b0673160d271ee43ad55ece19"
	}
}
```
| Field Name | Type | Description |
|---|---|---|
|id|String|标识数据的id|
|demanderOntid|String|买家ontid|
|judger|String|选取的仲裁方|
|expireTime|Integer|订单超时天数|
|sigVo|Map|购买交易的签名信息|
|name|String|订单名称|
|desc|String|订单描述|
|img|String|图片链接|
|keywords|List|数据标签|

响应：

```source-json
{
    "action": "purchase",
    "error": 0,
    "desc": "SUCCESS",
    "result": "85f50a7c1c25632bdf1ae6708e9233c7ea1169336bcf5f14b8d926c3e99a76ec",
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回交易hash，失败返回"" |
| version   | String | 版本号                        |


###  查看数据
```
url：/api/v1/order/data
method：POST
```

请求：
```source-json
{
	"id": "D4D1FA099FD140519AA71F942465CBF9",
	"ontid": "did:ont:AMZvjuJNxD21uVgJ5c8VDdGUiT4TudtLFU",
	"sigVo": {
		"txHex": "00d1ed6aa95cf401000000000000409c000000000000f5f7b705b03ae46e48f89c2b99e79fa4391536fe6e0360ea00016f51c10331313151c114000000000000000000000000000000000000000214010b5816b180ffb41e3889b6f42aeaf31fd63209143fc9fa9491df7e93b94db2df99e6af2d67ad34b756c10973656e64546f6b656e67bae44577a468b5bfd00ebbaba7d91204204828470000",
		"pubKeys": "03edaa022ce0f2020ec92e68ce47de932a804b4a5f240989fb612b63685d1bc8da",
		"sigData": "01e42dbefd28087bb42ad8667e6ed3a56e23cec70b0289c7d40e22948d7985bbc0713c1f5f19d92b706b6fe57a7ceaa23fc2eba99b0673160d271ee43ad55ece19"
	}
}
```
| Field Name | Type | Description |
|---|---|---|
|id|String|标识订单id，非orderId|
|ontid|String|买家ontid|
|sigVo|Map|查看数据消费token的交易签名|

响应：

```source-json
{
    "action": "getData",
    "error": 0,
    "desc": "SUCCESS",
    "result": "www.data.com",
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回数据，失败返回"" |
| version   | String | 版本号                        |


###  查询当前tokenId
```
url：/api/v1/order/token/{id}
method：Get
```
| Field Name | Type | Description |
|---|---|---|
|id|String|数据的id,非dataId|

响应：

```source-json
{
    "action": "getCurrentTokenId",
    "error": 0,
    "desc": "SUCCESS",
    "result": 15,
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | int | 成功返回当前数据的tokenId，失败返回"" |
| version   | String | 版本号                        |


###  查询token剩余流转次数和访问次数
```
url：/api/v1/order/token/balance/{tokenId}
method：Get
```
| Field Name | Type | Description |
|---|---|---|
|tokenId|String|tokenId|

响应：

```source-json
{
    "action": "getTokenBalance",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "accessCount": 12,
        "transferCount": 11,
        "expireTimeCount": 1561564800
    },
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | int | 成功返回当前token的剩余流转次数和访问次数，失败返回"" |
| version   | String | 版本号                        |


###  二次挂单创建order
```
url：/api/v1/order/second
method：POST
```

请求：
```source-json
{
	"id": "d0cc3f3e0855447990dde11e2ad85d88",
	"dataId": "did:ont:Aac8jSxyF81hxFEyRuiXSp5TvzN9MVqAoT",
	"tokenId": 1,
	"name": "name for order",
	"desc": "descrption for order",
	"img": "http://image.image.com/",
	"token": "ong",
	"price": "100",
	"providerOntid": "did:ont:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4",
	"ojList": ["did:ont:AJYEUcQi9jp157QXNWpKybwkCVSTuTNsh1","did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8"],
	"keywords": ["keyword1","keyword2"],
	"sigVo": {
		"txHex": "00d1ed6aa95cf401000000000000409c000000000000f5f7b705b03ae46e48f89c2b99e79fa4391536fe6e0360ea00016f51c10331313151c114000000000000000000000000000000000000000214010b5816b180ffb41e3889b6f42aeaf31fd63209143fc9fa9491df7e93b94db2df99e6af2d67ad34b756c10973656e64546f6b656e67bae44577a468b5bfd00ebbaba7d91204204828470000",
		"pubKeys": "03edaa022ce0f2020ec92e68ce47de932a804b4a5f240989fb612b63685d1bc8da",
		"sigData": "01e42dbefd28087bb42ad8667e6ed3a56e23cec70b0289c7d40e22948d7985bbc0713c1f5f19d92b706b6fe57a7ceaa23fc2eba99b0673160d271ee43ad55ece19"
	}
}
```
| Field Name | Type | Description |
|---|---|---|
|id|String|当前订单的id标识|
|dataId|String|数据的dataId|
|tokenId|String|tokenId|
|name|String|订单名称|
|desc|String|订单描述|
|img|String|图片链接|
|token|String|售卖币种|
|price|String|售卖价格|
|providerOntid|String|卖家ontid|
|ojList|List|仲裁方备选列表|
|keywords|List|数据标签|
|sigVo|Map|挂单交易签名信息|

响应：

```source-json
{
    "action": "createSecondOrder",
    "error": 0,
    "desc": "SUCCESS",
    "result": "96a5dc65ec4e6efebcae8d2a802d759c3b33ddd398c3921702949b564a33923a",
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回挂单交易hash，失败返回"" |
| version   | String | 版本号                        |

###  查询二手商品
```
url：/api/v1/order/all/second
method：POST
```

请求：
```source-json
{
	"pageNum": 0,
	"pageSize": 5,
	"queryParams": [{
		"text": "keyword1",
		"columnIndex": 0,
		"percent": 100
	}, {
		"text": "keyword2",
		"columnIndex": 1,
		"percent": 100
	}]
}
```
| Field Name | Type | Description |
|---|---|---|
|pageNum|Integer|起始页|
|pageSize|Integer|每页记录数|
|queryParams|List|查询条件|

响应：

```source-json
{
    "action": "findSecondOrder",
    "error": 0,
    "desc": "SUCCESS",
    "result": {
        "currentPage": 0,
        "pageSize": 5,
        "recordCount": 1,
        "recordList": [
            {
                "keywords":["keyword1","keyword2"],
                "tokenId": "1",
                "orderId": "ba24bb9d3dee415e9c9c12acaf78c973",
                "confirmTime": "",
                "token": "ong",
                "boughtTime": "",
                "expireTime": "",
                "dataId": "did:ont:Aac8jSxyF81hxFEyRuiXSp5TvzN9MVqAoT",
                "createTime": "2019-06-03 15:10:28",
                "cancelTime": "",
                "price": "100",
                "demanderOntid": "",
                "judger": [
                    "did:ont:AJYEUcQi9jp157QXNWpKybwkCVSTuTNsh1",
                    "did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8"
                ],
                "providerOntid": "did:ont:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4",
                "state": "1",
                "id": "D4D1FA099FD140519AA71F942465CBF9"
            }
        ],
        "pageCount": 1,
        "beginPageIndex": 1,
        "endPageIndex": 1
    },
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回已挂单数据，失败返回"" |
| version   | String | 版本号                        |

###  购买二手商品
```
url：/api/v1/order/purchase/second
method：POST
```

请求：
```source-json
{
	"id": "D4D1FA099FD140519AA71F942465CBF9",
	"demanderOntid": "did:ont:AMZvjuJNxD21uVgJ5c8VDdGUiT4TudtLFU",
	"demanderAddress": "AMZvjuJNxD21uVgJ5c8VDdGUiT4TudtLFU",
	"judger": "did:ont:AJYEUcQi9jp157QXNWpKybwkCVSTuTNsh1",
	"sigVo": {
		"txHex": "00d1ed6aa95cf401000000000000409c000000000000f5f7b705b03ae46e48f89c2b99e79fa4391536fe6e0360ea00016f51c10331313151c114000000000000000000000000000000000000000214010b5816b180ffb41e3889b6f42aeaf31fd63209143fc9fa9491df7e93b94db2df99e6af2d67ad34b756c10973656e64546f6b656e67bae44577a468b5bfd00ebbaba7d91204204828470000",
		"pubKeys": "03edaa022ce0f2020ec92e68ce47de932a804b4a5f240989fb612b63685d1bc8da",
		"sigData": "01e42dbefd28087bb42ad8667e6ed3a56e23cec70b0289c7d40e22948d7985bbc0713c1f5f19d92b706b6fe57a7ceaa23fc2eba99b0673160d271ee43ad55ece19"
	}
}
```
| Field Name | Type | Description |
|---|---|---|
|id|String|标识数据的id|
|demanderOntid|String|买家ontid|
|judger|String|选取的仲裁方|
|expireTime|Integer|订单超时天数|
|sigVo|Map|购买交易的签名信息|

响应：

```source-json
{
    "action": "purchaseSecondOrder",
    "error": 0,
    "desc": "SUCCESS",
    "result": "85f50a7c1c25632bdf1ae6708e9233c7ea1169336bcf5f14b8d926c3e99a76ec",
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回交易hash，失败返回"" |
| version   | String | 版本号                        |


## 仲裁接口

###  获取仲裁方列表
```
url：/api/v1/judger
method：GET
```

响应：

```source-json
{
    "action": "getJudger",
    "error": 0,
    "desc": "SUCCESS",
    "result": [
        {
            "id": "1",
            "ontid": "did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8"
        }
    ],
    "version": "v1"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回数据仲裁方列表，失败返回"" |
| version   | String | 版本号                        |


###  获取待仲裁列表
```
url：/api/v1/judger/{ontid}?pageNum=&pageSize=
method：GET
```

| Field Name | Type | Description |
|---|---|---|
|ontid|String|仲裁者ontid|
|pageNum|Integer|起始页|
|pageSize|Integer|每页记录数|

响应：

```source-json
{
	"action": "getTobeJudged",
	"error": 0,
	"desc": "SUCCESS",
	"result": [{
		"keywords": ["keyword1","keyword2"],
		"tokenId": "1",
		"orderId": "ba24bb9d3dee415e9c9c12acaf78c973",
		"confirmTime": "",
		"tokenHash": "0000000000000000000000000000000000000002",
		"boughtTime": "2019-06-03 18:10:30",
		"expireTime": "2019-06-18 18:10:30",
		"dataId": "did:ont:Aac8jSxyF81hxFEyRuiXSp5TvzN9MVqAoT",
		"createTime": "2019-06-03 15:10:28",
		"cancelTime": "",
		"price": "100",
		"demanderOntid": "did:ont:AMZvjuJNxD21uVgJ5c8VDdGUiT4TudtLFU",
		"judger": "did:ont:AJYEUcQi9jp157QXNWpKybwkCVSTuTNsh1",
		"providerOntid": "did:ont:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4",
		"state": "4",
		"id": "D4D1FA099FD140519AA71F942465CBF9",
		"isExpired": "0"
	}],
	"pageCount": 1,
	"beginPageIndex": 1,
	"endPageIndex": 1
},
"version": "v1"
}

```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回待仲裁分页列表，失败返回"" |
| version   | String | 版本号                        |


###  发送仲裁结果
```
url：/api/v1/judger/result
method：POST
```

请求：
```
{
	"id": "D4D1FA099FD140519AA71F942465CBF9",
	"winOrLose": true,
	"sigVo": {
		"txHex": "00d1ed6aa95cf401000000000000409c000000000000f5f7b705b03ae46e48f89c2b99e79fa4391536fe6e0360ea00016f51c10331313151c114000000000000000000000000000000000000000214010b5816b180ffb41e3889b6f42aeaf31fd63209143fc9fa9491df7e93b94db2df99e6af2d67ad34b756c10973656e64546f6b656e67bae44577a468b5bfd00ebbaba7d91204204828470000",
		"pubKeys": "03edaa022ce0f2020ec92e68ce47de932a804b4a5f240989fb612b63685d1bc8da",
		"sigData": "01e42dbefd28087bb42ad8667e6ed3a56e23cec70b0289c7d40e22948d7985bbc0713c1f5f19d92b706b6fe57a7ceaa23fc2eba99b0673160d271ee43ad55ece19"
	}
}
```
| Field Name | Type | Description |
|---|---|---|
|id|String|标识订单id，非orderId|
|winOrLose|Boolean|仲裁结果|
|sigVo|Map|仲裁的签名信息|

响应：

```source-json
{
    "action": "judgeResult",
    "error": 0,
    "desc": "SUCCESS",
    "result": "85f50a7c1c25632bdf1ae6708e9233c7ea1169336bcf5f14b8d926c3e99a76ec",
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回交易hash，失败返回"" |
| version   | String | 版本号                        |


## 合约调用接口

###  构造交易
```
url：/api/v1/contract/transaction
method：POST
```
请求：
```source-json
{
    "argsList": [
    {"name":"account","value":"Address:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4"},{"name":"dataId","value":"String:did:ont:Aac8jSxyF81hxFEyRuiXSp5TvzN9MVqAoT"},{"name":"ontid","value":"String:did:ont:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4"},{"name":"index","value":1},{"name":"symbol","value":"String:test"},{"name":"name","value":"String:testName"},{"name":"totalAmount","value":12}
    ],
    "contractHash": "01d6e6bdc03efe68d5754956ed182e4381b7a9d9",
    "method": "createTokenWithController"
}
```
| Field Name | Type | Description |
|---|---|---|
|contractHash|String|合约地址|
|method|String|合约方法|
|argsList|List|合约参数|

响应：

```source-json
{
    "action": "makeTransaction",
    "error": 0,
    "desc": "SUCCESS",
    "result": "00d14be68c14f401000000000000409c000000000000f5f7b705b03ae46e48f89c2b99e79fa4391536fe6e0360ea00016f5
               1c10331313151c114000000000000000000000000000000000000000214010b5816b180ffb41e3889b6f42aeaf31fd63209
               143fc9fa9491df7e93b94db2df99e6af2d67ad34b756c10973656e64546f6b656e67bae44577a468b5bfd00ebbaba7d9120
               4204828470000"
    ],
    "version": "v1"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回交易hex，失败返回"" |
| version   | String | 版本号                        |


###  发送交易
```
url：/api/v1/contract/send
method：POST
```
请求：
```source-json
{
	"txHex": "00d1ed6aa95cf401000000000000409c000000000000f5f7b705b03ae46e48f89c2b99e79fa4391536fe6e0360ea00016f51c10331313151c114000000000000000000000000000000000000000214010b5816b180ffb41e3889b6f42aeaf31fd63209143fc9fa9491df7e93b94db2df99e6af2d67ad34b756c10973656e64546f6b656e67bae44577a468b5bfd00ebbaba7d91204204828470000",
	"pubKeys": "03edaa022ce0f2020ec92e68ce47de932a804b4a5f240989fb612b63685d1bc8da",
	"sigData": "01e42dbefd28087bb42ad8667e6ed3a56e23cec70b0289c7d40e22948d7985bbc0713c1f5f19d92b706b6fe57a7ceaa23fc2eba99b0673160d271ee43ad55ece19"
}
```

| Field Name | Type | Description |
|---|---|---|
|txHex|String|交易hex|
|pubKeys|String|签名公钥|
|sigData|String|签名数据|

响应：

```source-json
{
	"action": "sendTransaction",
	"error": 0,
	"desc": "SUCCESS",
	"result": "cc5dacf2d6a1f49444e7feee20c2a59c60a624e5662157300d9f6a9e953260c2",
	"version": "v1"
}

```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回交易hash，失败返回"" |
| version   | String | 版本号                        |


###  注册dataId
```
url：/api/v1/contract/dataid
method：POST
```
请求：
```source-json
{
	"dataId": "did:ont:Aac8jSxyF81hxFEyRuiXSp5TvzN9MVqAoT",
	"ontid": "did:ont:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4",
	"pubKey": 1
}
```

| Field Name | Type | Description |
|---|---|---|
|dataId|String|dataId|
|ontid|String|数据所属者ontid|
|pubKey|Integer|签名的公钥编号|

响应：

```source-json
{
	"action": "registerDataId",
	"error": 0,
	"desc": "SUCCESS",
	"result": "00d1ed6aa95cf401000000000000409c000000000000f5f7b705b03ae46e48f89c2b99e79fa4391536fe6e0360ea00016f51c10331313151c114000000000000000000000000000000000000000214010b5816b180ffb41e3889b6f42aeaf31fd63209143fc9fa9491df7e93b94db2df99e6af2d67ad34b756c10973656e64546f6b656e67bae44577a468b5bfd00ebbaba7d91204204828470000",
	"version": "v1"
}

```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| error | int | 错误码 |
| desc | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回交易hex，失败返回"" |
| version   | String | 版本号                        |