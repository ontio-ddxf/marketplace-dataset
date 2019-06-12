* 1. [DDXF合约调用说明文档](#DDXF合约调用说明文档)
* 2. [ONS合约](#ONS合约)
	* 2.1. [注册ONS](#注册ONS)
	* 2.2. [查询ONS归属](#查询ONS归属)
* 3. [Ontid合约](#Ontid合约)
	* 3.1. [注册dataId](#注册dataId)
	* 3.2. [查询token余额](#查询token余额)
* 4. [DataToken合约](#DataToken合约)
	* 4.1. [生成dToken](#生成dToken)
	* 4.2. [查询token余额](#查询token余额)
* 5. [交易合约](#交易合约)
	* 5.1. [提供方挂单](#提供方挂单)
	* 5.2. [下单](#下单)
	* 5.3. [申请仲裁](#申请仲裁)
	* 5.4. [仲裁判决](#仲裁判决)
	* 5.5. [确认交易](#确认交易)



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
			"contractHash": "0f0929b514ddf62522a8a335b588321b2e7725bc",
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
					"name": "totalAmount",
					"value": 100
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
|totalAmount    |Integer|生成token的总量             |



###  查询token余额

预执行交易

构造交易参数：
```
{
	"action": "invoke",
	"params": {
		"invokeConfig": {
			"contractHash": "0f0929b514ddf62522a8a335b588321b2e7725bc",
			"functions": [{
				"operation": "balanceOf",
				"args": [{
					"name": "account",
					"value": "Address:AYYABY37JqzNZ8Pe8ebRvLMtc46qvX7tg4"
				}, {
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
|account        |String |持有token的地址             |
|tokenId        |Integer|生成token的id               |





## 交易合约

###  提供方挂单

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
					"name": "makerTokenAmount",
					"value": 1
				}, {
					"name": "makerReceiveAddress",
					"value": "Address:ARCESVnP8Lbf6S7FuTei3smA35EQYog4LR"
				}, {
					"name": "makerMortgageTokenHash",
					"value": "ByteArray:0000000000000000000000000000000000000002"
				}, {
					"name": "takerPaymentTokenHash",
					"value": "ByteArray:0000000000000000000000000000000000000002"
				}, {
					"name": "takerPaymentTokenAmount",
					"value": 100000000000
				}, {
					"name": "mpReceiveAddress",
					"value": "Address:AaKE7cfZkiuG6yMpCKjQQaQJDB3u5L5rn4"
				}, {
					"name": "txFeeTokenHash",
					"value": "ByteArray:0000000000000000000000000000000000000002"
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
|makerTokenAmount       |Integer|挂单token数量               |
|makerReceiveAddress    |String |提供方收款钱包地址          |
|makerMortgageTokenHash |String |抵押的tokenHash，默认为ONG  |
|takerPaymentTokenHash  |String |收款的tokenHash，默认为ONG  |
|takerPaymentTokenAmount|Long   |提供方对token的定价         |
|mpReceiveAddress       |String |marketplace收款钱包地址     |
|txFeeTokenHash         |String |手续费的tokenHash，默认为ONG|
|OJList                 |List   |仲裁者钱包地址列表          |


###  需求方下单

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
					"name": "txFeeTokenHash",
					"value": "ByteArray:0000000000000000000000000000000000000002"
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
|txFeeTokenHash         |Integer|手续费的tokenHash，默认为ONG|
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
				}, {
					"name": "arbitrageFee",
					"value": 50000000
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
|arbitrageFee           |Integer|仲裁费数量                  |




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