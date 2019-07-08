* 1. [DDXF合约调用说明文档](#DDXF合约调用说明文档)
* 2. [ONS合约](#ONS合约)
	* 2.1. [注册ONS](#注册ONS)
	* 2.2. [查询ONS归属](#查询ONS归属)
* 3. [Ontid合约](#Ontid合约)
	* 3.1. [注册dataId](#注册dataId)
* 4. [DataToken合约](#DataToken合约)
	* 4.1. [生成dToken](#生成dToken)
	* 4.2. [消费token](#消费token)
	* 4.2. [查询token剩余访问次数](#查询token剩余访问次数)
	* 4.2. [查询token剩余流转次数](#查询token剩余流转次数)
	* 4.2. [查询token过期时间](#查询token过期时间)
* 5. [交易合约](#交易合约)
	* 5.1. [挂单](#挂单)
	* 5.2. [下单](#下单)
	* 5.3. [申请仲裁](#申请仲裁)
	* 5.4. [仲裁判决](#仲裁判决)
	* 5.5. [确认交易](#确认交易)
* 6. [授权Marketplace生成token的交易合约](#授权Marketplace生成token的交易合约)
	* 5.1. [授权](#授权)
	* 5.2. [下单并生成token](#下单并生成token)


## DDXF合约调用说明文档
此文档说明了DDXF应用所涉及合约的调用参数(params)构造，参数为Json格式的字符串。

构造好的参数使用java sdk的ontSdk.makeTransactionByJson(params)可以构造相应的交易。 

对交易签名，发送即可正常调用合约。


## ONS合约

###  注册ONS

格式：[subdomain].<二级域名>.<一级域名>.<顶级域名>

合约调用方为上级domain的owner，顶级域名为合约admin，最低为二级域名owner

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "fb12993d6f13a2ec911f3bbfe534be90e4deeca4",
			"functions": [{
				"operation": "registerDomain",
				"args": [{
					"name": "fulldomain",
					"value": "String:abc.on.ont"
				}, {
					"name": "registerdid",
					"value": "String:did:ont:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4"
				}, {
					"name": "idx",
					"value": 1
				}, {
					"name": "validto",
					"value": -1
				}]
			}],
			"payer": "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby",
			"gasLimit": 80000,
			"gasPrice": 500
		}
	}
}
```

| Field Name    | Type  | Description                |
|---            |---    |---                         |
|action         |String |标识为调用合约，必须是invoke|
|contractHash   |String |合约地址                    |
|operation      |String |合约方法名                  |
|args           |List   |合约参数                    |
|payer          |String |手续费支付方                |
|gasLimit       |Long   |gasLimit                    |
|gasPrice       |Long   |gasPrice                    |
|fulldomain     |String |全域名                      |
|registerdid    |Strin  |注册者ontid                 |
|idx            |Integer|wallet idx                  |
|validto        |Long   |有效期，-1为永久            |




###  查询ONS归属

预执行交易

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "fb12993d6f13a2ec911f3bbfe534be90e4deeca4",
			"functions": [{
				"operation": "ownerOf",
				"args": [{
					"name": "fulldomain",
					"value": "String:abc.on.ont"
				}]
			}],
			"payer": "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby",
			"gasLimit": 80000,
			"gasPrice": 500
		}
	}
}
```

| Field Name    | Type  | Description                |
|---            |---    |---                         |
|action         |String |标识为调用合约，必须是invoke|
|contractHash   |String |合约地址                    |
|operation      |String |合约方法名                  |
|args           |List   |合约参数                    |
|payer          |String |手续费支付方                |
|gasLimit       |Long   |gasLimit                    |
|gasPrice       |Long   |gasPrice                    |
|fulldomain     |String |全域名                      |




## Ontid合约

###  注册dataId

Ontid合约为native合约，需要用sdk的native合约方式构造交易
```
String dataId = "did:ont:AVePfF6AtTtnk4kB8HTPevTWU8FsXr2DrG";
String ontid = "did:ont:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4";
Integer idx = 1;
String contractHash = "0000000000000000000000000000000000000003";
String  = "regIDWithController";
String payer = "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby";
Long gasLimit = 20000;
Long gasPrice = 500;

List list = new ArrayList();
list.add(new Struct().add(dataId,ontid,idx));

arg = NativeBuildParams.createCodeParamsScript(list);
Transaction tx = ontSdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractHash)),method,arg,payer,gasLimit,gasPrice);

```

| Field Name    | Type  | Description                |
|---            |---    |---                         |
|dataId         |String |data的ontid                 |
|ontid          |String |代理控制人                  |
|idx            |Integer|签名公钥编号                |
|contractHash   |String |合约地址                    |
|method         |String |合约方法                    |
|payer          |String |手续费支付方                |
|gasLimit       |Long   |gasLimit                    |
|gasPrice       |Long   |gasPrice                    |




## DataToken合约

###  生成dToken

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "06633f64506fbf7fd4b65b422224905d362d1f55",
			"functions": [{
				"operation": "createTokenWithController",
				"args": [{
					"name": "account",
					"value": "Address:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4"
				}, {
					"name": "dataId",
					"value": "String:did:ont:Aac8jSxyF81hxFEyRuiXSp5TvzN9MVqAoT"
				}, {
					"name": "ontid",
					"value": "String:did:ont:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4"
				}, {
					"name": "index",
					"value": 1
				}, {
					"name": "symbol",
					"value": "String:test"
				}, {
					"name": "name",
					"value": "String:testName"
				}, {
					"name": "amount",
					"value": 10
				}, {
					"name": "transferCount",
					"value": 10
				}, {
					"name": "accessCount",
					"value": 10
				}, {
					"name": "expireTime",
					"value": 0
				}]
			}],
			"payer": "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby",
			"gasLimit": 80000,
			"gasPrice": 500
		}
	}
}
```

| Field Name    | Type  | Description                |
|---            |---    |---                         |
|action         |String |标识为调用合约，必须是invoke|
|contractHash   |String |合约地址                    |
|operation      |String |合约方法名                  |
|args           |List   |合约参数                    |
|payer          |String |手续费支付方                |
|gasLimit       |Long   |gasLimit                    |
|gasPrice       |Long   |gasPrice                    |
|account        |String |生成dataToken的接收地址， 此地址必然绑定在传入的ontid下|
|dataId         |Strin  |已注册的dataId              |
|ontid          |String |控制人ontid                 |
|index          |Integer|控制人ontid index           |
|symbol         |String |符号                        |
|name           |String |名称                        |
|amount         |Integer|生成token的总量             |
|transferCount  |Integer|生成token的流转次数         |
|accessCount    |Integer|生成token的访问次数         |
|expireTime     |Integer|过期时间(时间戳,单位:秒,0为永久)|



###  消费token

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "06633f64506fbf7fd4b65b422224905d362d1f55",
			"functions": [{
				"operation": "consumeToken",
				"args": [{
					"name": "tokenId",
					"value": 1
				}]
			}],
			"payer": "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby",
			"gasLimit": 80000,
			"gasPrice": 500
		}
	}
}
```

| Field Name    | Type  | Description                |
|---            |---    |---                         |
|action         |String |标识为调用合约，必须是invoke|
|contractHash   |String |合约地址                    |
|operation      |String |合约方法名                  |
|args           |List   |合约参数                    |
|payer          |String |手续费支付方                |
|gasLimit       |Long   |gasLimit                    |
|gasPrice       |Long   |gasPrice                    |
|tokenId        |Integer|生成token的id               |


###  查询token剩余访问次数

预执行交易

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "06633f64506fbf7fd4b65b422224905d362d1f55",
			"functions": [{
				"operation": "getAccessCount",
				"args": [{
					"name": "tokenId",
					"value": 1
				}]
			}],
			"payer": "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby",
			"gasLimit": 80000,
			"gasPrice": 500
		}
	}
}
```

| Field Name    | Type  | Description                |
|---            |---    |---                         |
|action         |String |标识为调用合约，必须是invoke|
|contractHash   |String |合约地址                    |
|operation      |String |合约方法名                  |
|args           |List   |合约参数                    |
|payer          |String |手续费支付方                |
|gasLimit       |Long   |gasLimit                    |
|gasPrice       |Long   |gasPrice                    |
|tokenId        |Integer|生成token的id               |


###  查询token剩余流转次数

预执行交易

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "06633f64506fbf7fd4b65b422224905d362d1f55",
			"functions": [{
				"operation": "getTransferCount",
				"args": [{
					"name": "tokenId",
					"value": 1
				}]
			}],
			"payer": "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby",
			"gasLimit": 80000,
			"gasPrice": 500
		}
	}
}
```

| Field Name    | Type  | Description                |
|---            |---    |---                         |
|action         |String |标识为调用合约，必须是invoke|
|contractHash   |String |合约地址                    |
|operation      |String |合约方法名                  |
|args           |List   |合约参数                    |
|payer          |String |手续费支付方                |
|gasLimit       |Long   |gasLimit                    |
|gasPrice       |Long   |gasPrice                    |
|tokenId        |Integer|生成token的id               |


###  查询token过期时间

预执行交易

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "06633f64506fbf7fd4b65b422224905d362d1f55",
			"functions": [{
				"operation": "getExpireTime",
				"args": [{
					"name": "tokenId",
					"value": 1
				}]
			}],
			"payer": "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby",
			"gasLimit": 80000,
			"gasPrice": 500
		}
	}
}
```

| Field Name    | Type  | Description                |
|---            |---    |---                         |
|action         |String |标识为调用合约，必须是invoke|
|contractHash   |String |合约地址                    |
|operation      |String |合约方法名                  |
|args           |List   |合约参数                    |
|payer          |String |手续费支付方                |
|gasLimit       |Long   |gasLimit                    |
|gasPrice       |Long   |gasPrice                    |
|tokenId        |Integer|生成token的id               |




## 交易合约

###  挂单

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "3da0998e1e759aaed78b41ce1f92151d7b3f1083",
			"functions": [{
				"operation": "makeOrder",
				"args": [{
					"name": "makerTokenHash",
					"value": "ByteArray:0f0929b514ddf62522a8a335b588321b2e7725bc"
				}, {
					"name": "makerTokenId",
					"value": 17
				}, {
					"name": "makerReceiveAddress",
					"value": "Address:ARCESVnP8Lbf6S7FuTei3smA35EQYog4LR"
				}, {
					"name": "takerPaymentTokenAmount",
					"value": 100000000000
				}, {
					"name": "mpReceiveAddress",
					"value": "Address:AaKE7cfZkiuG6yMpCKjQQaQJDB3u5L5rn4"
				}, {
					"name": "OJList",
					"value": ["Address:Ac7iRytbVPnphyJKojefCgQCsjzmJzvbWs", "Address:AQA9HufmEYuK6Y4vXHHvKCFw7THGx1nze1"]
				}]
			}],
			"payer": "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby",
			"gasLimit": 80000,
			"gasPrice": 500
		}
	}
}
```

| Field Name            | Type  | Description                |
|---                    |---    |---                         |
|action                 |String |标识为调用合约，必须是invoke|
|contractHash           |String |合约地址                    |
|operation              |String |合约方法名                  |
|args                   |List   |合约参数                    |
|payer                  |String |手续费支付方                |
|gasLimit               |Long   |gasLimit                    |
|gasPrice               |Long   |gasPrice                    |
|makerTokenHash         |String |dataToken的合约地址         |
|makerTokenId           |Integer|对应持有的tokenId           |
|makerReceiveAddress    |String |提供方收款钱包地址          |
|takerPaymentTokenAmount|Long   |提供方对token的定价         |
|mpReceiveAddress       |String |marketplace收款钱包地址     |
|OJList                 |List   |仲裁者钱包地址列表          |


###  下单

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "3da0998e1e759aaed78b41ce1f92151d7b3f1083",
			"functions": [{
				"operation": "takeOrder",
				"args": [{
					"name": "orderId",
					"value": "ByteArray:d9fe5a7c0cafa18e746498f051fb048cbf206d8d"
				}, {
					"name": "takerReceiveAddress",
					"value": "Address:AHgtXRopCzzzSBtcLhv7YydRuLb4nwCuqr"
				}, {
					"name": "txFeeAmount",
					"value": 10000000000
				}, {
					"name": "OJ",
					"value": "Address:Ac7iRytbVPnphyJKojefCgQCsjzmJzvbWs"
				}]
			}],
			"payer": "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby",
			"gasLimit": 80000,
			"gasPrice": 500
		}
	}
}
```

| Field Name            | Type  | Description                |
|---                    |---    |---                         |
|action                 |String |标识为调用合约，必须是invoke|
|contractHash           |String |合约地址                    |
|operation              |String |合约方法名                  |
|args                   |List   |合约参数                    |
|payer                  |String |手续费支付方                |
|gasLimit               |Long   |gasLimit                    |
|gasPrice               |Long   |gasPrice                    |
|orderId                |String |订单的id                    |
|takerReceiveAddress    |Integer|需求方的钱包地址            |
|txFeeAmount            |String |手续费数量                  |
|OJ                     |String |需求方选择的唯一仲裁者      |


###  申请仲裁

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "3da0998e1e759aaed78b41ce1f92151d7b3f1083",
			"functions": [{
				"operation": "applyArbitrage",
				"args": [{
					"name": "orderId",
					"value": "ByteArray:d9fe5a7c0cafa18e746498f051fb048cbf206d8d"
				}]
			}],
			"payer": "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby",
			"gasLimit": 80000,
			"gasPrice": 500
		}
	}
}
```

| Field Name            | Type  | Description                |
|---                    |---    |---                         |
|action                 |String |标识为调用合约，必须是invoke|
|contractHash           |String |合约地址                    |
|operation              |String |合约方法名                  |
|args                   |List   |合约参数                    |
|payer                  |String |手续费支付方                |
|gasLimit               |Long   |gasLimit                    |
|gasPrice               |Long   |gasPrice                    |
|orderId                |String |订单的id                    |


###  仲裁判决

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "3da0998e1e759aaed78b41ce1f92151d7b3f1083",
			"functions": [{
				"operation": "arbitrage",
				"args": [{
					"name": "orderId",
					"value": "ByteArray:d9fe5a7c0cafa18e746498f051fb048cbf206d8d"
				}, {
					"name": "isWin",
					"value": true
				}, {
					"name": "makerReceiveAmount",
					"value": 50000000
				}, {
					"name": "takerReceiveAmount",
					"value": 1150000000
				}]
			}],
			"payer": "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby",
			"gasLimit": 80000,
			"gasPrice": 500
		}
	}
}
```

| Field Name            | Type  | Description                |
|---                    |---    |---                         |
|action                 |String |标识为调用合约，必须是invoke|
|contractHash           |String |合约地址                    |
|operation              |String |合约方法名                  |
|args                   |List   |合约参数                    |
|payer                  |String |手续费支付方                |
|gasLimit               |Long   |gasLimit                    |
|gasPrice               |Long   |gasPrice                    |
|orderId                |String |订单的id                    |
|isWin                  |Boolean|仲裁结果                    |
|makerReceiveAmount     |Long   |卖家收到的钱                |
|takerReceiveAmount     |Long   |买家收到的钱                |



###  确认交易

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "3da0998e1e759aaed78b41ce1f92151d7b3f1083",
			"functions": [{
				"operation": "confirm",
				"args": [{
					"name": "orderId",
					"value": "ByteArray:d9fe5a7c0cafa18e746498f051fb048cbf206d8d"
				}]
			}],
			"payer": "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby",
			"gasLimit": 80000,
			"gasPrice": 500
		}
	}
}
```

| Field Name            | Type  | Description                |
|---                    |---    |---                         |
|action                 |String |标识为调用合约，必须是invoke|
|contractHash           |String |合约地址                    |
|operation              |String |合约方法名                  |
|args                   |List   |合约参数                    |
|payer                  |String |手续费支付方                |
|gasLimit               |Long   |gasLimit                    |
|gasPrice               |Long   |gasPrice                    |
|orderId                |String |订单的id                    |


## 授权Marketplace生成token的交易合约

###  授权

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "3da0998e1e759aaed78b41ce1f92151d7b3f1083",
			"functions": [{
					"operation": "authOrder",
					"args": [{
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
						"value": 5
					}, {
						"name": "price",
						"value": 1000000000
					}, {
						"name": "transferCount",
						"value": 10
					}, {
						"name": "accessCount",
						"value": 10
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
						"value": ["Address:Ac7iRytbVPnphyJKojefCgQCsjzmJzvbWs", "Address:AQA9HufmEYuK6Y4vXHHvKCFw7THGx1nze1"]
					}],
					"payer": "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby",
					"gasLimit": 80000,
					"gasPrice": 500
				}
			}
		}
```

| Field Name            | Type  | Description                |
|---                    |---    |---                         |
|action                 |String |标识为调用合约，必须是invoke|
|contractHash           |String |合约地址                    |
|operation              |String |合约方法名                  |
|args                   |List   |合约参数                    |
|payer                  |String |手续费支付方                |
|gasLimit               |Long   |gasLimit                    |
|gasPrice               |Long   |gasPrice                    |
|dataId                 |String |授权数据的dataId            |
|index                  |Integer|dataId控制人的公钥编号      |
|symbol                 |String |token的符号                 |
|name                   |Long   |token的名称                 |
|authAmount             |Integer|授权marketplace生成的数量   |
|price                  |Long   |单个token的价格             |
|transferCount          |Integer|允许token流转的次数         |
|accessCount            |Integer|允许token访问的次数         |
|expireTime             |Long   |token的过期时间，0为永久    |
|makerTokenHash         |String |dataToken的合约地址         |
|makerReceiveAddress    |String |卖家的收款钱包地址          |
|mpReceiveAddress       |String |Marketplace的收款钱包地址   |
|OJList                 |List   |仲裁者钱包地址列表          |


###  下单并生成token

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "3da0998e1e759aaed78b41ce1f92151d7b3f1083",
			"functions": [{
				"operation": "takeOrder",
				"args": [{
					"name": "authId",
					"value": "ByteArray:cdf603cc119a00820aae41d5566c3404e3741ad3"
				}, {
					"name": "takerReceiveAddress",
					"value": "Address:AHgtXRopCzzzSBtcLhv7YydRuLb4nwCuqr"
				}, {
					"name": "tokenAmount",
					"value": 1
				}, {
					"name": "OJ",
					"value": "Address:Ac7iRytbVPnphyJKojefCgQCsjzmJzvbWs"
				}]
			}],
			"payer": "AeCRx2oYR4GL2djtnMLmtwezPXT1GPNTby",
			"gasLimit": 80000,
			"gasPrice": 500
		}
	}
}
```

| Field Name            | Type  | Description                |
|---                    |---    |---                         |
|action                 |String |标识为调用合约，必须是invoke|
|contractHash           |String |合约地址                    |
|operation              |String |合约方法名                  |
|args                   |List   |合约参数                    |
|payer                  |String |手续费支付方                |
|gasLimit               |Long   |gasLimit                    |
|gasPrice               |Long   |gasPrice                    |
|authId                 |String |授权的id                    |
|takerReceiveAddress    |Integer|需求方的钱包地址            |
|tokenAmount            |Integer|购买并生成token的数量       |
|OJ                     |String |需求方选择的唯一仲裁者      |
