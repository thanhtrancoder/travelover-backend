package hcmute.kltn.Backend.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import hcmute.kltn.Backend.exception.TryCatchException;
import hcmute.kltn.Backend.model.base.video.service.IVideoService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "")
@Tag(
		name = "Resource", 
		description = "APIs for managing videos resource\n\n",
		externalDocs = @ExternalDocumentation(
				description = "Update Api History", 
				url = "")
		)
@SecurityRequirement(name = "Bearer Authentication")
public class ResourceController {
	@Autowired
	private IVideoService iVideoService;
	
	private final String getVideoDesc = "Nhận dữ liệu video để chơi trên FE";
	@RequestMapping(value = "/videos/play/{id}", method = RequestMethod.GET)
	@Operation(summary = "Get resource video", description = getVideoDesc)
	ResponseEntity<UrlResource> getVideo (
			@PathVariable String id) {
		UrlResource videoResource = iVideoService.getVideo(id);
		
		return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaType.parseMediaType("video/mp4"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + videoResource.getFilename() + "\"")
                .body(videoResource);
	}
}
