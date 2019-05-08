package com.ontology;

import com.ontology.utils.ElasticsearchUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void deleteIndex(){
		ElasticsearchUtil.deleteIndex("test_index");
	}

}
