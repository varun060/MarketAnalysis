package com.currencymarket.currencymarket;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CurrencymarketApplicationTests {

	@Autowired
    private MockMvc mvc;
	@Test
	public void contextLoads() {
		
		
	}

}
