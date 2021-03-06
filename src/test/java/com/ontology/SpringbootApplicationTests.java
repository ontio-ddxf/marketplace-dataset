package com.ontology;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.ontio.common.Address;
import com.ontology.utils.Constant;
import com.ontology.utils.ElasticsearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootApplicationTests {

	@Test
	public void contextLoads() {
		String expireTime = JSON.toJSONStringWithDateFormat(new Date(),"yyyy-MM-dd HH:mm:ss");
		log.info("{}",expireTime);
		Date expireDate = JSON.parseObject(expireTime, Date.class);
		log.info("{}",expireDate);
	}

	@Test
	public void deleteIndex(){
		ElasticsearchUtil.deleteIndex("test_index");
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
		map.put("state","5");

		ElasticsearchUtil.updateDataById(map, "order_index", "order","8E38579484324634BEE1D2DE0F888644");

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
	public void testVersion() {
		Map<String,Object> map = new HashMap<>();
		map.put("dataId","");
		ElasticsearchUtil.updateDataByIdAndVersion(map,Constant.ES_INDEX_DATASET, Constant.ES_TYPE_DATASET,"ef4952d865764871abf205d272cbbb7d",3L);

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
