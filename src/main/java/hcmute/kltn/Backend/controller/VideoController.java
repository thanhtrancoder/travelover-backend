package hcmute.kltn.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import hcmute.kltn.Backend.model.base.response.dto.Response;
import hcmute.kltn.Backend.model.base.response.dto.ResponseObject;
import hcmute.kltn.Backend.model.base.response.service.IResponseObjectService;
import hcmute.kltn.Backend.model.base.video.dto.Video;
import hcmute.kltn.Backend.model.base.video.service.IVideoService;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/videos")
@Tag(
		name = "Videos", 
		description = "APIs for managing videos\n\n",
		externalDocs = @ExternalDocumentation(
				description = "Update Api History", 
				url = "https://drive.google.com/file/d/1680C0xg0Ny--NNspQ7eG7rDPEHqmcQio/view?usp=sharing")
		)
@SecurityRequirement(name = "Bearer Authentication")
public class VideoController {
	@Autowired
	private IVideoService iVideoService;
	@Autowired
	private IResponseObjectService iResponseObjectService;
	
	private final String createVideoDesc = "Tải video lên và nhận url\n\n"
			+ "- Dung lượng max là 100MB\n\n"
			+ "- Trên FE dùng url này bỏ vào src của thẻ viedeo và type là video/mp4 hoặc tương tự nếu có "
			+ "(cái type t không rõ lắm nha)";
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Create video - ADMIN / STAFF", description = createVideoDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> createVideo(
			@ModelAttribute MultipartFile file) {
		Video video =	iVideoService.createVideo(file);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Create video successfully");
				setData(video);
			}
		});
	}
}
