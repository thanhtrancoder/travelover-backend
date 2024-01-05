package hcmute.kltn.Backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import hcmute.kltn.Backend.model.base.image.dto.Image;
import hcmute.kltn.Backend.model.base.image.service.IImageService;
import hcmute.kltn.Backend.model.base.response.dto.Response;
import hcmute.kltn.Backend.model.base.response.dto.ResponseObject;
import hcmute.kltn.Backend.model.base.response.service.IResponseObjectService;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/images")
@Tag(
		name = "Images", 
		description = "APIs for managing images\n\n",
		externalDocs = @ExternalDocumentation(
				description = "Update Api History", 
				url = "https://drive.google.com/file/d/1I-WwINoVR1vnfv-1H3rs5sw4uvDPsTWc/view?usp=sharing")
		)
@SecurityRequirement(name = "Bearer Authentication")
public class ImageController {
	@Autowired
	private IImageService iImageService;
	@Autowired
	private IResponseObjectService iResponseObjectService;
	
	private final String createImageDesc = "Tải file có type = image, size <= 2MB";
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Create image - LOGIN", description = createImageDesc)
	@PreAuthorize("isAuthenticated()")
	ResponseEntity<ResponseObject> createImage(
			@ModelAttribute MultipartFile file) {
		Image image = iImageService.createImage(file);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Create Image successfully");
				setData(image);
			}
		});
	}
	
	private final String createMultipleImageDesc = "Tải nhiều hình cùng lúc, nếu có lỗi xảy ra sẽ trả về lỗi và không "
			+ "có hình nào được lưu cả\n\n"
			+ "Tải file có type = image, size <= 2MB";
	@RequestMapping(value = "/multiple-create", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Create multiple image - LOGIN", description = createMultipleImageDesc)
	@PreAuthorize("isAuthenticated()")
	ResponseEntity<ResponseObject> createMultipleImage(
			@ModelAttribute List<MultipartFile> fileList) {
		List<String> imageUrlList = new ArrayList<>();
		imageUrlList.addAll(iImageService.createMultipleImage(fileList));

		return iResponseObjectService.success(new Response() {
			{
				setMessage("Create multiple image successfully");
				setData(imageUrlList);
			}
		});
	}
	
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	@Operation(summary = "Get image detail - LOGIN")
	@PreAuthorize("isAuthenticated()")
	ResponseEntity<ResponseObject> getDetail(@RequestParam String imageId) {
		Image image = iImageService.getImageDetail(imageId);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get image detail successfully");
				setData(image);
			}
		});
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@Operation(summary = "Delete image - LOGIN")
	@PreAuthorize("isAuthenticated()")
	ResponseEntity<ResponseObject> deleteImage(
			@RequestParam String imageId) {
		boolean checkDelete = iImageService.deleteImage(imageId);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Delete image successfully");
			}
		});
	}
}