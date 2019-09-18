package com.ontology;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.ontio.common.Address;
import com.ontology.controller.vo.SigVo;
import com.ontology.utils.Constant;
import com.ontology.utils.ElasticsearchUtil;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootApplicationTests {

	@Autowired
	private SDKUtil sdkUtil;

	@Test
	public void testSend() throws Exception {
		SigVo sigVo = new SigVo();
		sigVo.setTxHex("00d168a46458f401000000000000204e000000000000675478ea7368fd9579c00a8a749d29c2b82f2aefa600c66b2a6469643a6f6e743a41615a364b354b704473456f7a4832326b784a58624d476e375461736e355a616f736a7cc82a6469643a6f6e743a4152434553566e50384c6266365337467554656933736d4133354551596f67344c526a7cc8516a7cc86c13726567494457697468436f6e74726f6c6c65721400000000000000000000000000000000000000030068164f6e746f6c6f67792e4e61746976652e496e766f6b650000");
		sigVo.setPubKeys("02ee173141c4965d8bfa1f88d326e083cef23b9191afdca53657f3ab74f99845b0");
		sigVo.setSigData("01992e8da518ab77e8fb47a53b4c6a12b8cd0a4e52a94ea79bead30637b4aeb1532bfb2a1048725910ce174f7fb54c1fa7ebdbab3a044a87577bbc6c105c552208");
		sdkUtil.sendTransaction(sigVo);
	}

	@Test
	public void testver() throws Exception {
		String pubKey = "02ee173141c4965d8bfa1f88d326e083cef23b9191afdca53657f3ab74f99845b0";
		String data = "hello 1563977526455";
		String signature = "014a72e4b30702400b9d7bafff491d7d93abf0ad4acdc76db6b5c01829a1535238027c26f4efdf94bf7ab7ae2f8407df6aab7f6c4a3abddca60a3ead0df0be047c";
		Object verified = sdkUtil.verified(pubKey, data, signature);
		log.info("{}",verified);
	}

	@Test
	public void contextLoads() {
//		String tokenRange = "1,2,,,3";  [1, 2, , , 3]  5
//		String tokenRange = "1,2,,3"; [1, 2, , 3]  4
//		String tokenRange = "1,2,,";[1, 2]  2
		String tokenRange = "1,2,,,,,";
		String[] split = tokenRange.split(",");
		log.info("{}",Arrays.toString(split));
		log.info("{}",split.length);
	}

	@Test
	public void deleteIndex(){
		ElasticsearchUtil.deleteIndex("test_index");
	}

	@Test
	public void testCatch(){
		try {
			String s = null;
			s.equals("1");
		} catch (Exception e) {
			log.error("catch exception:",e);
		}
	}

	@Test
	public void testSearchES2() {
		Map<String,Object> map = new HashMap<>();
		map.put("orderId","feb68d3b8e6a284fb961032985f5543e530e9eb1");
		map.put("state","1");

		ElasticsearchUtil.updateDataById(map, "order_index", "order","FC182E0385D043B5A5C6D7C79B3E0D9D");

	}

	@Test
	public void updateData() {
		Map<String,Object> map = new HashMap<>();
		map.put("createdTime","2019-07-17 15:41:19");

		ElasticsearchUtil.updateDataById(map, "event_index", "events","82A6820220CE4ADE8AAB80B4AD5EF2D1");

	}

	@Test
	public void updateData2() {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		MatchQueryBuilder querydata = QueryBuilders.matchQuery("dataId", "did:ont:ANoc4WHoPSv6bZTu5UGfrJ7pxnC1ySfJEH");
		boolQuery.must(querydata);
		List<Map<String, Object>> list = ElasticsearchUtil.searchListData(Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET, boolQuery, null, null, null, null);
		log.info("",list.size());

	}

	@Test
	public void searchOrder() {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		MatchQueryBuilder order = QueryBuilders.matchQuery("orderId.keyword", "");
		boolQuery.must(order);
		List<Map<String, Object>> list = ElasticsearchUtil.searchListData(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, boolQuery, null, null, null, null);
		log.info("{}",list);

	}

	@Test
	public void deleteOrder() {
		ElasticsearchUtil.deleteDataById(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER,"70A9AE313BBF45298898EEF7469B4E7B");

	}

	@Test
	public void sendTransaction() throws Exception {
		SigVo sigVo = new SigVo();
		sigVo.setSigData("01c280bdfc3353b385013e9478764aabd7a3b67f280cd02163b92c9c68725e5d796fb189fb9788edeb38d14d987e3bf8eaea52871d1e91b41ce83925a23d7c84e4");
		sigVo.setPubKeys("03b8a3156d16896976827d257444a6cd305e864bc83c6351e57cbaa64f29ef7d08");
		sigVo.setTxHex("00d18d70320cf401000000000000409c000000000000675478ea7368fd9579c00a8a749d29c2b82f2aef6101ff01012a6469643a6f6e743a4150653479543542364b6e7652374c656e6b5a4436655147684735325172646a756f0b6c7266632e6f6e2e6f6e7454c10e7265676973746572446f6d61696e678f8dba5f2a6974f800bea817b1f1828647ab11b60000");
		sdkUtil.sendTransaction(sigVo);
	}

	@Test
	public void testVersion() {
		GetResponse getResponse = ElasticsearchUtil.searchVersionById(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, "3D4DB44F9F44449992CF76CB9B462E5F", null);
		long version = getResponse.getVersion();
		log.info("version:{}",version);
//		Map<String,Object> map = new HashMap<>();
//		map.put("dataId","");
//		ElasticsearchUtil.updateDataByIdAndVersion(map,Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET,"ef4952d865764871abf205d272cbbb7d",3L);

	}

	@Test
	public void uupdateOrderId() {
		JSONArray objects = JSONArray.parseArray("[\"did:ont:AFsPutgDdVujxQe7KBqfK9Jom8AFMGB2x8\",\"did:ont:AacQn34p97jdtt95ftfJTTfz6wpm9nZ4j4\"]");
//		List<String> ojList = new ArrayList<>();
//		for (Object o : objects) {
//			String addr = Address.parse((String) o).toBase58();
//			ojList.add(addr);
//		}
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		MatchQueryBuilder queryToken = QueryBuilders.matchQuery("tokenId", 16);
		MatchQueryBuilder queryAmount = QueryBuilders.matchQuery("amount", 4);
		MatchQueryBuilder queryPrice = QueryBuilders.matchQuery("price", 2000000000);
		MatchQueryBuilder queryJudger = QueryBuilders.matchQuery("judger", JSON.toJSONString(objects));
		boolQuery.must(queryToken);
		boolQuery.must(queryAmount);
		boolQuery.must(queryPrice);
		boolQuery.must(queryJudger);
		List<Map<String, Object>> list = ElasticsearchUtil.searchListData(Constant.ES_INDEX_ORDER, Constant.ES_TYPE_ORDER, boolQuery, null, null, null, null);
		log.info("{}",list.get(0));
	}

	@Test
	public void testMapRemove() {
		Map<String,Object> map = new HashMap<>();
		map.put("dataId","qwert");
		map.put("column0","column1");
		map.put("column1","column2");
		map.put("column2","column3");
		map.put("column3","column4");
		map.put("column4","column5");
		int size = map.size();
		for (int i = 0; i< size; i++) {
			if (!map.containsKey("column"+i)) {
				break;
			} else {
				map.remove("column"+i);
			}
		}
		log.info("{}",map);
	}

}
