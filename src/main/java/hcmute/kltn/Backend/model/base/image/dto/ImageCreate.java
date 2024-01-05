package hcmute.kltn.Backend.model.base.image.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageCreate {
	private String targetId;
	private MultipartFile imageFile;
}
