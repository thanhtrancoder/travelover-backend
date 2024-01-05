package hcmute.kltn.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import hcmute.kltn.Backend.model.base.response.dto.Response;
import hcmute.kltn.Backend.model.base.response.dto.ResponseObject;
import hcmute.kltn.Backend.model.base.response.service.IResponseObjectService;
import hcmute.kltn.Backend.model.dev.service.IDevService;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/devs")
@Tag(
		name = "Devs", 
		description = "APIs for managing devs\n\n",
		externalDocs = @ExternalDocumentation(
				description = "Update Api History", 
				url = "https://drive.google.com/file/d/176XCXAx04e66xHKIOnL2V1smcR2fiEGo/view?usp=sharing")
		)
@SecurityRequirement(name = "Bearer Authentication")
public class DevController {
	@Autowired
	private IDevService iDevService;
	@Autowired
	private IResponseObjectService iResponseObjectService;
	
	private final String updateNewDateTimeDesc = "Chạy để cập nhật giá trị date-time cho biến date-time mới "
			+ "(biến mới bao gồm ngày và giờ, tên biến kèm thêm số 2 ở cuối)";
	@RequestMapping(value = "/update-new-date-time", method = RequestMethod.GET)
	@Operation(summary = "Update New DateTime - ADMIN / STAFF", description = updateNewDateTimeDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> updateNewDateTime() {
		iDevService.updateNewDateTime();
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Update New DateTime successfully");
			}
		});
	}
}
