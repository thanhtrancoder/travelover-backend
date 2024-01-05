package hcmute.kltn.Backend.model.base.externalAPI.service.impl;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import hcmute.kltn.Backend.model.base.externalAPI.dto.ApiCallResponse;
import hcmute.kltn.Backend.model.base.externalAPI.service.IExternalAPIService;

@Service
public class ExternalAPIService implements IExternalAPIService{
	
	private final RestTemplate restTemplate;
	
	@Autowired
	public ExternalAPIService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}
	
	@Override
	public Object getJsonObject(String json) {
		Gson gson = new Gson();
		JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
		
		// convert to HashMap with jsonObject
	    if (jsonElement.isJsonObject()) {
	    	JsonObject jsonObject = jsonElement.getAsJsonObject();
		    Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
		    HashMap<String, Object> map = gson.fromJson(jsonObject, type);
			
			return map;
	    }
	    
	    // convert to HashMap with jsonArray
	    if (jsonElement.isJsonArray()) {
	        JsonArray jsonArray = jsonElement.getAsJsonArray();
	        List<HashMap<String, Object>> list = new ArrayList<>();
	        for (JsonElement element : jsonArray) {
	            Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
	            HashMap<String, Object> map = gson.fromJson(element, type);
	            list.add(map);
	        }

	        return list;
	    }
	    
	    // convert to HashMap with jsonArray
	    if (jsonElement.isJsonPrimitive()) {
	    	JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
	        if (jsonPrimitive.isNumber()) {
	            Number number = jsonPrimitive.getAsNumber();
	            return number;
	        } 
	        	
	        if (jsonPrimitive.isBoolean()) {
	            boolean bool = jsonPrimitive.getAsBoolean();
	            return bool;
	        }
	        
	        if (jsonPrimitive.isString()) {
	            String string = jsonPrimitive.getAsString();
	            return string;
	        }

	        return null;
	    }
	    
	    // convert to HashMap with jsonArray
	    if (jsonElement.isJsonNull()) {
	        return null;
	    }
	    
	    return null;
	}

	@Override
	public ApiCallResponse get(String url, HashMap<String, String> header, HashMap<String, String> param) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			// set header
			if (header != null) {
				for (String key : header.keySet()) {
					String value = header.get(key);
					headers.set(key, value);
				}
			} 
			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
			
			
			// set param
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
			if (param != null) {
				for (String key : param.keySet()) {
					String value = param.get(key);
					builder.queryParam(key, value);
				}
			}

			// call api
			ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

		    ApiCallResponse res = new ApiCallResponse();
		    res.setStatus(response.getStatusCode());
		    res.setBody(getJsonObject(response.getBody()));

			return res;
		} catch (Exception e) {
			System.out.println("ExternalAPIService get error = " + e.getMessage());
			// get error
			String[] eSplit = e.getMessage().split("\"", 2);
			String error = eSplit[1];
			int errorLength = error.length();
			error = error.substring(0, errorLength - 1);
		    
		    String status = e.getMessage().substring(0, 3);
		    ApiCallResponse res = new ApiCallResponse();
		    res.setStatus(status);
		    res.setBody(getJsonObject(error));

			return res;
		}
	}

	@Override
	public ApiCallResponse post(String url, HashMap<String, String> header, HashMap<String, String> param) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			// set header
			if (header != null) {
				for (String key : header.keySet()) {
					String value = header.get(key);
					headers.set(key, value);
				}
			} 
			String jsonParam = new ObjectMapper().writeValueAsString(param);
			HttpEntity<String> entity = new HttpEntity<String>(jsonParam, headers);

			// call api
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		    ApiCallResponse res = new ApiCallResponse();
		    res.setStatus(response.getStatusCode());
		    res.setBody(getJsonObject(response.getBody()));

			return res;
		} catch (Exception e) {
			System.out.println("ExternalAPIService get error = " + e.getMessage());
			// get error
			String[] eSplit = e.getMessage().split("\"", 2);
			String error = eSplit[1];
			int errorLength = error.length();
			error = error.substring(0, errorLength - 1);
		    
		    String status = e.getMessage().substring(0, 3);
		    ApiCallResponse res = new ApiCallResponse();
		    res.setStatus(status);
		    res.setBody(getJsonObject(error));

			return res;
		}
	}
}
