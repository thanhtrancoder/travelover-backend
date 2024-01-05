package hcmute.kltn.Backend.controller;

import java.util.HashMap;

import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import hcmute.kltn.Backend.exception.CustomException;
import hcmute.kltn.Backend.model.base.externalAPI.dto.ApiCallResponse;
import hcmute.kltn.Backend.model.base.externalAPI.dto.ApiPostReq;
import hcmute.kltn.Backend.model.base.externalAPI.dto.Header;
import hcmute.kltn.Backend.model.base.externalAPI.service.IExternalAPIService;
import hcmute.kltn.Backend.model.base.response.dto.Response;
import hcmute.kltn.Backend.model.base.response.dto.ResponseObject;
import hcmute.kltn.Backend.model.base.response.service.IResponseObjectService;
import hcmute.kltn.Backend.util.HashMapUtil;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/external-api")
@Tag(
		name = "External API", 
		description = "APIs for managing external API",
		externalDocs = @ExternalDocumentation(
				description = "Update Api History", 
				url = "https://drive.google.com/file/d/1vfq5tT7UN-PDVVSaU2H1f8xHBAkC-5MX/view?usp=sharing")
		)
@SecurityRequirement(name = "Bearer Authentication")
public class ExternalAPIController {
	@Autowired
	private IExternalAPIService iExternalAPIService;
	@Autowired
	private IResponseObjectService iResponseObjectService;
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	@Operation(summary = "Call external api with GET method")
	ResponseEntity<ResponseObject> get(
			@RequestParam String url, 
			@RequestParam HashMap<String, String> header,
			@RequestParam HashMap<String, String> param) {
		ApiCallResponse object = iExternalAPIService.get(url, header, param);
		
		if (object.getStatus() == HttpStatus.OK) {
			return iResponseObjectService.success(new Response() {
				{
					setMessage("Call external api with GET method successfully");
					setData(object.getBody());
				}
			});
		}

		// handle fail
		ResponseObject responseObject = new ResponseObject();
		responseObject.setStatus("failed");
		responseObject.setMessage("There is an error occurring during API call");
		responseObject.setData(object.getBody());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObject);
	}
	
	@RequestMapping(value = "/post", method = RequestMethod.POST)
	@Operation(summary = "Call external api with POST method")
	ResponseEntity<ResponseObject> post(
			@RequestBody ApiPostReq ApiPostReq) {
		ApiCallResponse object = iExternalAPIService.post(ApiPostReq.getUrl(), ApiPostReq.getHeader(), ApiPostReq.getParam());
		
		if (object.getStatus() == HttpStatus.OK) {
			return iResponseObjectService.success(new Response() {
				{
					setMessage("Call external api with GET method successfully");
					setData(object.getBody());
				}
			});
		}

		// handle fail
		ResponseObject responseObject = new ResponseObject();
		responseObject.setStatus("failed");
		responseObject.setMessage("There is an error occurring during API call");
		responseObject.setData(object.getBody());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObject);
	}
}
