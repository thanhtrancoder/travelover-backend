//package hcmute.kltn.Backend.component;
//
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.cloudinary.Cloudinary;
//import com.cloudinary.utils.ObjectUtils;
//
//import hcmute.kltn.Backend.exception.CustomException;
//
//@Component
//public class CloudinaryUtil {
//	@Autowired
//	private Cloudinary cloudinary;
//	
//	public Map<?, ?> create(String folder, MultipartFile file) {
//		try {
//			Map<?, ?> params = ObjectUtils.asMap(
//					"folder", folder,
//				    "use_filename", false,
//				    "unique_filename", true,
//				    "overwrite", false
//			);
//			Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), params);
//			
//			return result;
//		} catch (Exception ex) {
//    		throw new CustomException("Failed to upload image");
//    	}
//	}
//	
//	public Map<?, ?> update(String publicId, MultipartFile file) {
//		try {
//    		Map<?, ?> params = ObjectUtils.asMap(
//    				"public_id", publicId,
//    			    "use_filename", false,
//    			    "unique_filename", true,
//    			    "overwrite", true
//    		);
//    		Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), params);
//    		
//    		return result;
//
//    	} catch (Exception ex) {
//    		throw new CustomException("Failed to update image");
//    	}
//	}
//	
//	public boolean delete(String publicId) {
//		try {
//    		cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
//    		return true;
//    	} catch (Exception ex) {
//    		throw new CustomException("Failed to delete image");
//    	}
//	}
//}
