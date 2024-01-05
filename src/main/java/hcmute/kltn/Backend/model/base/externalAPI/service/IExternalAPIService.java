package hcmute.kltn.Backend.model.base.externalAPI.service;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import hcmute.kltn.Backend.model.base.externalAPI.dto.Header;
import hcmute.kltn.Backend.model.base.externalAPI.dto.ApiCallResponse;

public interface IExternalAPIService {
	public ApiCallResponse get(String url, HashMap<String, String> header, HashMap<String, String> param);
	public ApiCallResponse post(String url, HashMap<String, String> header, HashMap<String, String> param);
	
	public Object getJsonObject(String json);
}
