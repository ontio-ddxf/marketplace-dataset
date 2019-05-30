package com.ontology;

import com.alibaba.fastjson.JSON;
import com.ontology.utils.ElasticsearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
//@RunWith(SpringRunner.class)
//@SpringBootTest
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
		map.put("dataId","qwert");
		map.put("name","suibianyige");
		map.put("keyword","关键字");
		Map<String,Object> data = new HashMap<>();
		data.put("coin","ong");
		data.put("ontid","did:ont:AUJjTER6xUkfSwh2GApyrgxFRZn7ib8cix");
		data.put("price","100");
		data.put("tag0", JSON.toJSONString(map));
		String s = ElasticsearchUtil.addData(data, "dataset_index", "dataTag");

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
