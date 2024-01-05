package hcmute.kltn.Backend.model.base.video.service;

import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import hcmute.kltn.Backend.model.base.video.dto.Video;

public interface IVideoService {
	public Video createVideo(MultipartFile file);
	
	public UrlResource getVideo(String videoName);
	
	public boolean deleteVideoByUrl(String videoUrl);
}
