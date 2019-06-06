* 1. [Restful Api 需求](#RestfulApi)
* 2. [商品接口](#商品接口)
	* 2.1. [插入或根据id更新数据到ElasticSearch](#插入或根据id更新数据到ElasticSearch)
	* 2.2. [根据ID返回数据](#根据ID返回数据)
	* 2.3. [根据卖家ontid返回数据](#根据卖家ontid返回数据)
	* 2.4. [生成dataId和dataToken](#生成dataId和dataToken)
	* 2.5. [查询token余额](#查询token余额)
* 3. [认证接口](#认证接口)
	* 3.1. [获取认证方列表](#获取认证方列表)
	* 3.2. [认证人获取待认证列表](#认证人获取待认证列表)
	* 3.3. [认证数据](#认证数据)
* 4. [订单接口](#订单接口)
	* 4.1. [挂单创建order](#挂单创建order)
	* 4.2. [查询所有挂单](#查询所有挂单)
	* 4.3. [查询自己的订单](#查询自己的订单)
	* 4.4. [购买数据](#购买数据)
    * 4.4. [查验数据](#查验数据)
* 5. [仲裁接口](#仲裁接口)
	* 5.1. [获取仲裁方列表](#获取仲裁方列表)
	* 5.2. [获取待仲裁列表](#获取待仲裁列表)
* 6. [合约调用接口](#合约调用接口)
	* 6.1. [构造交易](#构造交易)
	* 6.2. [发送交易](#发送交易)
	* 6.3. [注册dataId和tokenId](#注册dataId和tokenId)

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
		"keywords": ["word1", "word2"],
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
    "code": 0,
    "msg": "SUCCESS",
    "result": "fa20c972950f46cdba99ca521f0c49fa",
    "version": "v1"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action    | String | 动作标志                      |
| code      | int    | 错误码                        |
| msg       | String | 成功为SUCCESS，失败为错误描述 |
| result    | String | 成功返回数据ID，失败返回""    |
| version   | String | 版本号                        |


###  根据ID返回数据

```
url:/api/v1/dataset/{id}
method:Get
```
| Field Name | Type | Description |
|---|---|---|
|id|String|数据id|

响应：

```source-json
{
    "action": "getData",
    "code": 0,
    "msg": "SUCCESS",
    "result": {
        "isCertificated": 0,
        "dataId": "",
        "tokenId": "",
        "data": {
            "desc": "descrption for data",
            "img": "http://image.image.com/",
            "keywords": [
                "word1",
                "word2"
            ],
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
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | Object | 返回数据 |
| version   | String | 版本号                        |

### 根据卖家ontid返回数据
```
url：/api/v1/dataset/provider/{ontid}?{pageNum}&{pageSize}
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
    "code": 0,
    "msg": "SUCCESS",
    "result": {
        "currentPage": 0,
        "pageSize": 5,
        "recordCount": 1,
        "recordList": [
            {
                "isCertificated": 0,
                "dataId": "",
                "tokenId": "",
                "data": {
                    "desc": "descrption for data",
                    "img": "http://image.image.com/",
                    "keywords": [
                        "word1",
                        "word2"
                    ],
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
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回分页数据列表，失败返回"" |
| version   | String | 版本号                        |


###  生成dataId和dataToken

```
url：/api/v1/dataset/tokenId
method：POST
```

请求：

```source-json
{
	"id": "fa20c972950f46cdba99ca521f0c49fa",
	"dataId": "did:ont:Aac8jSxyF81hxFEyRuiXSp5TvzN9MVqAoT",
	"sigDataVo": {
		"txHex": "00d1ed6aa95cf401000000000000409c000000000000f5f7b705b03ae46e48f89c2b99e79fa4391536fe6e0360ea00016f51c10331313151c114000000000000000000000000000000000000000214010b5816b180ffb41e3889b6f42aeaf31fd63209143fc9fa9491df7e93b94db2df99e6af2d67ad34b756c10973656e64546f6b656e67bae44577a468b5bfd00ebbaba7d91204204828470000",
		"pubKeys": "03edaa022ce0f2020ec92e68ce47de932a804b4a5f240989fb612b63685d1bc8da",
		"sigData": "01e42dbefd28087bb42ad8667e6ed3a56e23cec70b0289c7d40e22948d7985bbc0713c1f5f19d92b706b6fe57a7ceaa23fc2eba99b0673160d271ee43ad55ece19"
	},
	"sigTokenVo": {
		"txHex": "00d1ed6aa95cf401000000000000409c000000000000f5f7b705b03ae46e48f89c2b99e79fa4391536fe6e0360ea00016f51c10331313151c114000000000000000000000000000000000000000214010b5816b180ffb41e3889b6f42aeaf31fd63209143fc9fa9491df7e93b94db2df99e6af2d67ad34b756c10973656e64546f6b656e67bae44577a468b5bfd00ebbaba7d91204204828470000",
		"pubKeys": "03edaa022ce0f2020ec92e68ce47de932a804b4a5f240989fb612b63685d1bc8da",
		"sigData": "01e42dbefd28087bb42ad8667e6ed3a56e23cec70b0289c7d40e22948d7985bbc0713c1f5f19d92b706b6fe57a7ceaa23fc2eba99b0673160d271ee43ad55ece19"
	}
}

```

| Field Name | Type | Description |
|---|---|---|
|id|String|数据id|
|dataId|String|为数据生成的dataId|
|sigDataVo|Map|注册dataId交易签名|
|sigTokenVo|Map|生成tokenId交易签名|
|txHex|String|交易hex|
|pubKeys|String|签名公钥|
|sigData|String|签名数据|

响应：

```source-json
{
    "action": "createDataIdAndTokenId",
    "code": 0,
    "msg": "SUCCESS",
    "result": ["7d7c4f01e0fa3c3203424644697b8d2266f337fb25b3ae89bc9575194a5d5ce7","5e359e5b5ca2e47bbca4f3c4d10596dcd0af5852ecf16d2d5d1ff45b51b842a2"],
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 注册dataId和生成tokenId的交易hash |
| version   | String | 版本号                        |


###  查询token余额

```
url：/token/balance/{address}/{tokenId}
method：Get
```

| Field Name | Type | Description |
|---|---|---|
|address|String|查询的ontid地址|
|tokenId|Long|tokenId|

响应：

```source-json
{
    "action": "getBalanceOfToken",
    "code": 0,
    "msg": "SUCCESS",
    "result": 999,
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 该地址下token的余额 |
| version   | String | 版本号                        |


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


###  认证人获取待认证列表
```
url：/api/v1/certifier/{certifier}?{pageNum}&{pageSize}
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
    "code": 0,
    "msg": "SUCCESS",
    "result": {
        "currentPage": 0,
        "pageSize": 5,
        "recordCount": 1,
        "recordList": [
            {
                "isCertificated": 0,
                "dataId": "",
                "tokenId": "",
                "data": {
                    "desc": "descrption for data",
                    "img": "http://image.image.com/",
                    "keywords": [
                        "word1",
                        "word2"
                    ],
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
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
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
    "code": 0,
    "msg": "SUCCESS",
    "result": "SUCCESS",
    "version": "v1"
}
```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | String | 成功返回"SUCCESS"，失败返回"" |
| version   | String | 版本号                        |


## 订单接口

###  挂单创建order
```
url：/api/v1/order
method：POST
```

请求：
```source-json
{
	"dataId": "did:ont:Aac8jSxyF81hxFEyRuiXSp5TvzN9MVqAoT",
	"tokenId": 1,
	"tokenHash": "0000000000000000000000000000000000000002",
	"price": "100",
	"providerOntid": "did: ont: AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4",
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
|dataId|String|数据的dataId|
|tokenId|String|tokenId|
|tokenHash|String|售卖币种hash|
|price|String|售卖价格|
|providerOntid|String|卖家ontid|
|ojList|List|仲裁方备选|
|keywords|List|数据标签|
|sigVo|Map|挂单交易签名信息|

响应：

```source-json
{
    "action": "createOrder",
    "code": 0,
    "msg": "SUCCESS",
    "result": "96a5dc65ec4e6efebcae8d2a802d759c3b33ddd398c3921702949b564a33923a",
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
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
    "code": 0,
    "msg": "SUCCESS",
    "result": {
        "currentPage": 0,
        "pageSize": 5,
        "recordCount": 1,
        "recordList": [
            {
                "column1": "keyword2",
                "column0": "keyword1",
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
                "demanderOntid": "",
                "judger": [
                    "did:ont:AJYEUcQi9jp157QXNWpKybwkCVSTuTNsh1",
                    "did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8"
                ],
                "providerOntid": "did: ont: AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4",
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
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
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
	"ontid":"did: ont: AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4"
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
    "code": 0,
    "msg": "SUCCESS",
    "result": {
        "currentPage": 0,
        "pageSize": 5,
        "recordCount": 1,
        "recordList": [
            {
                "column1": "keyword2",
                "column0": "keyword1",
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
                "demanderOntid": "",
                "judger": "[\"did:ont:AJYEUcQi9jp157QXNWpKybwkCVSTuTNsh1\",\"did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8\"]",
                "providerOntid": "did: ont: AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4",
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
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
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
     	"judger": "did:ont:AJYEUcQi9jp157QXNWpKybwkCVSTuTNsh1",
     	"expireTime":15,
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
|demanderOntid|String|买家ontid|
|judger|String|选取的仲裁方|
|expireTime|Integer|订单超时天数|
|sigVo|Map|购买交易的签名信息|

响应：

```source-json
{
    "action": "findSelfOrder",
    "code": 0,
    "msg": "SUCCESS",
    "result": "85f50a7c1c25632bdf1ae6708e9233c7ea1169336bcf5f14b8d926c3e99a76ec",
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回交易hash，失败返回"" |
| version   | String | 版本号                        |


###  查验数据
```
url：/api/v1/order/data
method：POST
```

请求：
```source-json
{
	"id": "D4D1FA099FD140519AA71F942465CBF9",
	"ontid": "did:ont:AMZvjuJNxD21uVgJ5c8VDdGUiT4TudtLFU"
}
```
| Field Name | Type | Description |
|---|---|---|
|id|String|标识订单id，非orderId|
|ontid|String|买家ontid|

响应：

```source-json
{
    "action": "findSelfOrder",
    "code": 0,
    "msg": "SUCCESS",
    "result": "www.data.com",
    "version": "v1"
}
```

| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回数据，失败返回"" |
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
    "code": 0,
    "msg": "SUCCESS",
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
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回数据仲裁方列表，失败返回"" |
| version   | String | 版本号                        |


###  获取待仲裁列表
```
url：/api/v1/judger/{ontid}?{pageNum}&{pageSize}
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
	"code": 0,
	"msg": "SUCCESS",
	"result": [{
		"column1": "keyword2",
		"column0": "keyword1",
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
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回待仲裁分页列表，失败返回"" |
| version   | String | 版本号                        |


## 合约调用接口

###  构造交易
```
url：/api/v1/contract
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
    "code": 0,
    "msg": "SUCCESS",
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
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
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
	"code": 0,
	"msg": "SUCCESS",
	"result": "cc5dacf2d6a1f49444e7feee20c2a59c60a624e5662157300d9f6a9e953260c2",
	"version": "v1"
}

```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回交易hash，失败返回"" |
| version   | String | 版本号                        |


###  注册dataId和tokenId
```
url：/api/v1/contract/dataid
method：POST
```
请求：
```source-json
{
	"dataId": "did:ont:Aac8jSxyF81hxFEyRuiXSp5TvzN9MVqAoT",
	"ontid": "did:ont:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4",
	"pubKey": 1,
	"contractVo": {
		{
			"argsList": [{
				"name": "account",
				"value": "Address:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4"
			}, {
				"name": "dataId",
				"value": "String:did:ont:AVePfF6AtTtnk4kB8HTPevTWU8FsXr2DrG"
			}, {
				"name": "ontid",
				"value": "String:did:ont:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4"
			}, {
				"name": "index",
				"value": 1
			}, {
				"name": "symbol",
				"value": "String:DNF"
			}, {
				"name": "name",
				"value": "String:123"
			}, {
				"name": "totalAmount",
				"value": 100
			}],
			"contractHash": "0f0929b514ddf62522a8a335b588321b2e7725bc",
			"method": "createTokenWithController",
		}
	}
```

| Field Name | Type | Description |
|---|---|---|
|dataId|String|dataId|
|ontid|String|数据所属者ontid|
|pubKey|Integer|签名的公钥编号|
|contractVo|Map|生成tokenId的合约参数|

响应：

```source-json
{
	"action": "dataid",
	"code": 0,
	"msg": "SUCCESS",
	"result": ["00d1ed6aa95cf401000000000000409c000000000000f5f7b705b03ae46e48f89c2b99e79fa4391536fe6e0360ea00016f51c10331313151c114000000000000000000000000000000000000000214010b5816b180ffb41e3889b6f42aeaf31fd63209143fc9fa9491df7e93b94db2df99e6af2d67ad34b756c10973656e64546f6b656e67bae44577a468b5bfd00ebbaba7d91204204828470000":"00d17bb1432df401000000000000409c000000000000f5f7b705b03ae46e48f89c2b99e79fa4391536fe6e0360ea00016f51c10331313151c114000000000000000000000000000000000000000214010b5816b180ffb41e3889b6f42aeaf31fd63209143fc9fa9491df7e93b94db2df99e6af2d67ad34b756c10973656e64546f6b656e67bae44577a468b5bfd00ebbaba7d91204204828470000"],
	"version": "v1"
}

```
| Field Name | Type | Description |
| :-- | :-- | :-- |
| action | String | 动作标志 |
| code | int | 错误码 |
| msg | String | 成功为SUCCESS，失败为错误描述 |
| result | List | 成功返回交易hex，失败返回"" |
| version   | String | 版本号                        |