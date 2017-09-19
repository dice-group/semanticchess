package de.daug.semanticchess.Annotation;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;






//TODO: 			work in progress
@Deprecated
public class FoxAnnotationsWebApi{
	public static void main(String[] args) throws UnsupportedEncodingException, IOException{
		
		JsonObject obj = new JsonObject();
		
		obj.addProperty("input", "Show me games between Carlsen and Aronian");
		obj.addProperty("type", "text");
		obj.addProperty("task", "ner");
		obj.addProperty("output", "jsonld");
		obj.addProperty("foxlight", "org.aksw.fox.tools.ner.en.IllinoisExtendedEN");
		

		
		System.out.println(obj.toString());
		
		HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead 

		try {

		    HttpPost request = new HttpPost("http://fox.cs.uni-paderborn.de:4444/fox");
		    StringEntity params =new StringEntity("{\"input\" :\"The philosopher and mathematician Leibniz was born in Leipzig in 1646 and attended the University of Leipzig from 1661-1666.\",\"type\":\"text\",\"task\":\"ner\",\"output\":\"jsonld\",\"lang\":\"en\",\"foxlight\":\"org.aksw.fox.tools.ner.en.IllinoisExtendedEN\"}");
		    request.addHeader("content-type", "application/json");
		    
		    request.setEntity(params);
		    HttpResponse response = httpClient.execute(request);

		    //handle response here...

		}catch (Exception ex) {

		    //handle exception here

		} finally {
		    //Deprecated
		    //httpClient.getConnectionManager().shutdown(); 
		}
		
	}

}