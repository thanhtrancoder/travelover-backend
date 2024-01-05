package hcmute.kltn.Backend.model.base.response.service;

import org.springframework.http.ResponseEntity;

import hcmute.kltn.Backend.model.base.response.dto.Response;
import hcmute.kltn.Backend.model.base.response.dto.ResponseObject;

public interface IResponseObjectService {
	public ResponseEntity<ResponseObject> success(Response response);
	public ResponseEntity<ResponseObject> failed(Response response);
}
