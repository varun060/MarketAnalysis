package com.currencymarket.currencymarket.model;



import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Document Mapper class 
 * between java and mongo
 * @author varun
 *
 */
//@Document
public class MarketDetails {

@SerializedName("_id")
@Expose
private Id id;
@SerializedName("currency")
@Expose
private String currency;

@SerializedName("quotes")
@Expose
//private Map<Integer,Float> quotes;
private List<Quote> quotes = null;

public Id getId() {
return id;
}

public void setId(Id id) {
this.id = id;
}

public String getCurrency() {
return currency;
}

public void setCurrency(String currency) {
this.currency = currency;
}

	 public List<Quote> getQuotes() { return quotes; }
	
	 public void setQuotes(List<Quote> quotes) { this.quotes = quotes; }
	

}
