package hcmute.kltn.Backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hcmute.kltn.Backend.model.base.Pagination;
import hcmute.kltn.Backend.model.base.response.dto.Response;
import hcmute.kltn.Backend.model.base.response.dto.ResponseObject;
import hcmute.kltn.Backend.model.base.response.service.IResponseObjectService;
import hcmute.kltn.Backend.model.province.dto.DistrictDTO;
import hcmute.kltn.Backend.model.province.dto.LocationDTO;
import hcmute.kltn.Backend.model.province.dto.ProvinceDTO;
import hcmute.kltn.Backend.model.province.dto.UpdateVisitor;
import hcmute.kltn.Backend.model.province.dto.WardDTO;
import hcmute.kltn.Backend.model.province.dto.entity.Province;
import hcmute.kltn.Backend.model.province.service.IProvinceService;
import hcmute.kltn.Backend.util.StringUtil;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/location")
@Tag(
		name = "Provinces", 
		description = "APIs for managing provinces",
		externalDocs = @ExternalDocumentation(
				description = "Update Api History", 
				url = "https://drive.google.com/file/d/1xGhB6RBMA6YNQbcNBDPcLuASCuVy3fQ_/view?usp=sharing")
		)
@SecurityRequirement(name = "Bearer Authentication")
public class LocationController {
	@Autowired
	private IProvinceService iProvinceService;
	@Autowired
	private IResponseObjectService iResponseObjectService;
	
	private final String updateTourDesc = "Cập nhật dữ liệu location mới nhất theo trang web https://provinces.open-api.vn/";
	@RequestMapping(value = "/auto-update", method = RequestMethod.PUT)
	@Operation(summary = "Auto update province - ADMIN / STAFF (time-consuming)", description = updateTourDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> updateTour() {
		iProvinceService.autoUpdate();
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Auto update province successfully");
			}
		});
	}
	
	private final String getAllLocationDesc = "Copy link bỏ lên trình duyệt để test cho đỡ lag, "
			+ "trên swagger cứ bấm chạy là lag nha\n\n"
			+ "link: http://localhost:8080/api/v1/location/list";
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@Operation(summary = "Get all location (LAG)", description = getAllLocationDesc)
	ResponseEntity<ResponseObject> getAllLocation() {
		List<Province> provinceList = iProvinceService.getAllLocation();		
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get all location successfully");
				setData(provinceList);
			}
		});
	}
	
	private final String getAllProvinceDesc = "Lấy danh sách tất cả province";
	@RequestMapping(value = "/province/list", method = RequestMethod.GET)
	@Operation(summary = "Get all province", description = getAllProvinceDesc)
	ResponseEntity<ResponseObject> getAllProvince() {
		List<ProvinceDTO> provinceDTOList = iProvinceService.getAllProvince();		
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get all province successfully");
				setData(provinceDTOList);
			}
		});
	}
	
	private final String getAllDistrictDesc = "Lấy danh sách tất cả destrict thuộc province";
	@RequestMapping(value = "/province/district/list", method = RequestMethod.GET)
	@Operation(summary = "Get all district", description = getAllDistrictDesc)
	ResponseEntity<ResponseObject> getAllDistrict(@RequestParam String provinceCode) {
		List<DistrictDTO> districtDTOList = iProvinceService.getAllDistrict(provinceCode);		
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get all district successfully");
				setData(districtDTOList);
			}
		});
	}
	
	private final String getAllWardDesc = "Lấy danh sách tất cả ward thuộc district thuộc province";
	@RequestMapping(value = "/province/district/ward/list", method = RequestMethod.GET)
	@Operation(summary = "Get all ward", description = getAllWardDesc)
	ResponseEntity<ResponseObject> getAllWard(
			@RequestParam String provinceCode,
			@RequestParam String districtCode) {
		List<WardDTO> wardDTOList = iProvinceService.getAllWard(provinceCode, districtCode);		
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get all ward successfully");
				setData(wardDTOList);
			}
		});
	}
	
	private final String searchDesc = "Search theo name và divisionType\n\n"
			+ "Sắp xếp mặc định bằng codeName theo bảng chữ cái";
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@Operation(summary = "Search location by keyword", description = searchDesc)
	ResponseEntity<ResponseObject> search(
			@RequestParam String keyword,
			@ModelAttribute Pagination Pagination) {
		List<LocationDTO> locationDTOList = iProvinceService.searchLocation(keyword);		
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get all ward successfully");
				setPageSize(Pagination.getPageSize());
				setPageNumber(Pagination.getPageNumber());
				setData(locationDTOList);
			}
		});
	}
	
	@RequestMapping(value = "/visitor-update", method = RequestMethod.PUT)
	@Operation(summary = "TEST - Update number of visitor - FOR DEV")
	ResponseEntity<ResponseObject> updateNumberOfVisitor(
			@RequestBody UpdateVisitor updateVisitor) {
		System.out.println("updateVisitor = " + updateVisitor);
		iProvinceService.updateNumberOfVisitor(updateVisitor);		
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Update number of visitor successfully");
			}
		});
	}
	
	private final String getHotLocationDesc = "Lấy ra 10 địa điểm có nhiều khách đến du lịch nhất";
	@RequestMapping(value = "/hot-list", method = RequestMethod.GET)
	@Operation(summary = "Get 10 hot location", description = getHotLocationDesc)
	ResponseEntity<ResponseObject> getHotLocation() {
		List<LocationDTO> locationDTOList = iProvinceService.getHotLocation();	
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get 10 hot location successfully");
				setData(locationDTOList);
			}
		});
	}
	
}
