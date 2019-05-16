/**
 * All services for controllers will be listed under this package
 */
package com.currencymarket.currencymarket.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.currencymarket.currencymarket.constant.CuurencyConst;
import com.currencymarket.currencymarket.controller.CurrencyController;
import com.currencymarket.currencymarket.model.Currency;
import com.currencymarket.currencymarket.model.MarketDetails;
import com.currencymarket.currencymarket.model.OutputModel;
import com.currencymarket.currencymarket.model.ResponseModel;
import com.currencymarket.currencymarket.repository.CurrencyRepository;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
// mongo db drivers
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

/**
 * Market ANalyzer will do the logic of identifying 
 * uses Mongo repositories for getting data
 * 
 * profitable time
 * @author Varun  K R
 *
 */

@Service
public class MarketAnlyser {

	// repository to get data
	@Autowired
	private CurrencyRepository repository;
	
	 private static final Logger logger = LogManager.getLogger(CurrencyController.class);

	public ResponseModel getProfitTimes(Date fromDate, Date toDate, String currency) {

		try {

			logger.info("in Market analyzer service");
			/**** Connect to MongoDB ****/
			// Since 2.10.0, uses MongoClient
			@SuppressWarnings("resource")
			MongoClient mongo = new MongoClient("localhost", 27017);


			// if database doesn't exists, MongoDB will create it for you
			//@SuppressWarnings("deprecation")
			DB db = mongo.getDB("stockmarcket");

			/**** Get collection / table from 'testdb' ****/
			// if collection doesn't exists, MongoDB will create it for you
			DBCollection table = db.getCollection("currencymarket");

			List<Currency> currencies=getCurrencyType(currency);
			logger.info("currencies "+currencies);
			logger.info("111");
			Map<String,List<OutputModel>>outputModelMap=new HashMap<String, List<OutputModel>>();
			List<OutputModel> outputModels=new ArrayList<OutputModel>();
			/**** Get database ****/
			ResponseModel result=new ResponseModel();
			logger.info("list length is"+currencies.size());
			for(Currency currencyIter:currencies) {
				logger.info("inside for"+currencyIter.getCurrency());
				// get cursor for querying
				BasicDBObject searchQuery = new BasicDBObject("date",
						new BasicDBObject("$gte",fromDate).append("$lt",toDate ));
				searchQuery.put("currency", currencyIter.getCurrency());
				DBCursor cursor = table.find(searchQuery);
				while (cursor.hasNext()) {
					DBObject json =cursor.next();
					logger.info("json obtained is "+json.toString());

					Gson gson = new Gson();

					//  convert JSON file to Java object
					MarketDetails status = gson.fromJson(json.toString(), MarketDetails.class);					

					Collections.sort(status.getQuotes());
					// declare iterator values to 0
					int iterFirst=0;
					float defaultDif=0;
					OutputModel model = new OutputModel();
					/**** logic to identify highest profit ****/
					while(iterFirst<status.getQuotes().size()) {
						float oldPrice=status.getQuotes().get(iterFirst).getPrice();
						// comparison is only required with future values
						int iterSecond=iterFirst+1;
						/**** Compare each value difference ***/
						while(iterSecond<status.getQuotes().size()) {
							float newPrice=status.getQuotes().get(iterSecond).getPrice();
							if((newPrice-oldPrice)>defaultDif) {
								defaultDif=newPrice-oldPrice;
								//model details which will be sent as a result 
								model.setCurrency(status.getCurrency());
								model.setFromTime(status.getQuotes().get(iterFirst).getTime());
								model.setToTime(status.getQuotes().get(iterSecond).getTime());
								model.setValueProfit(defaultDif);
								model.setFrmPrice(status.getQuotes().get(iterFirst).getPrice());
								model.setToPrice(status.getQuotes().get(iterSecond).getPrice());

							}
							iterSecond++;
						}
						iterFirst++;

					}
					outputModels.add(model);

					if(null==outputModelMap.get(model.getCurrency())) {
						List<OutputModel> outputModelss=new ArrayList<OutputModel>();
						outputModelMap.put(model.getCurrency(), outputModelss);

					}
					//outputModelMap.get(model.getCurrency());
					outputModelMap.get(model.getCurrency()).add(model);



					//return result;
				}

			}
			result.setResults(outputModels);
			logger.info("********result is"+result.getResults().size());
			return result;


		} catch (MongoException e) {

			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Method to return currencies according to input type
	 * @param currency
	 * @return
	 */
	private List<Currency> getCurrencyType(String currency) {

		// if all get all currencies and return
		List<Currency> currencies;
		if((CuurencyConst.All.equals(currency))) {
			currencies= repository.findAll();
		}
		else {
			// else get specific currency object and return
			currencies=new ArrayList<Currency>();
			Currency cur=new Currency();
			cur.setCurrency(currency);

			currencies.add(cur);
		}
		return currencies;
	}


}
