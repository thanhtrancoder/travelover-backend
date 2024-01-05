package hcmute.kltn.Backend.model.base.response.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import hcmute.kltn.Backend.model.base.response.dto.Response;
import hcmute.kltn.Backend.model.base.response.dto.ResponseObject;
import hcmute.kltn.Backend.model.base.response.service.IResponseObjectService;

@Service
public class ResponseObjectService implements IResponseObjectService{
	@Override
	public ResponseEntity<ResponseObject> success(Response response) {
		ResponseObject responseObject = new ResponseObject();
		responseObject.setStatus("ok");
		responseObject.setMessage(response.getMessage() != null ? response.getMessage() : "ok");
		responseObject.setTotalPages(1);
		responseObject.setTotalData(1);
		responseObject.setCurrentPage(1);
		if(response.getData() != null) {
			if(response.getData() instanceof List<?>) {
				List<?> dataList = (List<?>) response.getData();
				responseObject.setTotalData(dataList.size());
				responseObject.setCountData(dataList.size());
			} else {
				responseObject.setTotalData(1);
				responseObject.setCountData(1);
			}
		} else {
			responseObject.setTotalData(0);
			responseObject.setCountData(0);
		}
		responseObject.setData(response.getData());
		
		// pagination with list
		List<?> objectList = new ArrayList<>();
		if (response.getPageSize() > 0 && response.getData() instanceof List<?>) {
			List<?> dataList = (List<?>) response.getData();
			int totalPage = Integer.valueOf(dataList.size() / response.getPageSize());
			if ((dataList.size() % response.getPageSize()) > 0) {
				totalPage++;
			}
			
			int fromIndex = 0;
			int toIndex = 0;
			
			responseObject.setTotalData(dataList.size());
			
			if (response.getPageNumber() > 0) {
				// pagination
				if (response.getPageNumber() > totalPage) {
					responseObject.setTotalPages(totalPage);
					responseObject.setCurrentPage(response.getPageNumber());
					responseObject.setCountData(0);
					responseObject.setData(null);
				} else {
					fromIndex = response.getPageSize() * (response.getPageNumber() - 1);
					toIndex = fromIndex + response.getPageSize();
					if (toIndex >= dataList.size()) {
						toIndex = dataList.size();
					}	
					
					objectList = dataList.subList(fromIndex, toIndex);

					responseObject.setTotalPages(totalPage);
					responseObject.setCurrentPage(response.getPageNumber());
					responseObject.setCountData(objectList.size());
					responseObject.setData(objectList);
				}
				
				
			} else if (response.getPageNumber() == -1) {
				// get last page
				fromIndex = response.getPageSize() * (totalPage - 1);
				toIndex = fromIndex + response.getPageSize();
				if (toIndex >= dataList.size()) {
					toIndex = dataList.size();
				}	
				
				objectList = dataList.subList(fromIndex, toIndex);

				responseObject.setTotalPages(totalPage);
				responseObject.setCurrentPage(totalPage);
				responseObject.setCountData(objectList.size());
				responseObject.setData(objectList);
			} 
			
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(responseObject);
	}

	@Override
	public ResponseEntity<ResponseObject> failed(Response response) {
		ResponseObject responseObject = new ResponseObject();
		responseObject.setStatus("failed");
		responseObject.setMessage(response.getMessage() != null ? response.getMessage() : "failed");
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObject);
	}
}
