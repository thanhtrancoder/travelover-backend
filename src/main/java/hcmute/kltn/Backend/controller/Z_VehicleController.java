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

import hcmute.kltn.Backend.model.base.response.dto.Response;
import hcmute.kltn.Backend.model.base.response.dto.ResponseObject;
import hcmute.kltn.Backend.model.base.response.service.IResponseObjectService;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.CoachSearch;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.CoachSearchRes;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.EVehicleDTO;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.EVehicleDTOSimple;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.Location;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.service.IEVehicleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(path = "/api/v1/evehicles")
@SecurityRequirement(name = "Bearer Authentication")
public class Z_VehicleController {
	@Autowired
	private IResponseObjectService iResponseObjectService;
	@Autowired
	private IEVehicleService iEVehicleService;
	
	private final String createEVehicleDesc = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'eVehicleName': ''\n"
			+ "- 'description': ''\n"
			+ "- 'phoneNumber': ''\n"
			+ "- 'address': ''\n"
			+ "- 'route': '' (list\\<String\\> các province mà nhà xe hỗ trợ chạy, "
			+ "province nào không có trong list này thì nhà xe không chạy ở tỉnh đó)\n"
			+ "- 'numberOfStarRating': '' (giá trị từ 1 đến 5)\n\n"
			+ "Khi gọi create sẽ tự tạo ra danh sách xe dựa trên số sao đánh giá";
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@Operation(tags = "Z Enterprise Vehicles", summary = "Create Vehicle - ENTERPRISE", description = createEVehicleDesc)
	@PreAuthorize("hasAnyRole('ROLE_ENTERPRISE')")
	ResponseEntity<ResponseObject> createEVehicle(
			@RequestBody EVehicleDTO eVehicleDTO) {
		
		EVehicleDTO eVehicleDTONew = iEVehicleService.createEVehicle(eVehicleDTO);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Create Enterprise Vehicle successfully");
				setData(eVehicleDTONew);
			}
		});
	}
	
	private final String updateEVehicleDesc = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'eVehicleId': ''\n"
			+ "- 'eVehicleName': ''\n"
			+ "- 'phoneNumber': ''\n"
			+ "- 'address': ''\n";
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@Operation(tags = "Z Enterprise Vehicles", summary = "Update Vehicle - ENTERPRISE", description = updateEVehicleDesc)
	@PreAuthorize("hasAnyRole('ROLE_ENTERPRISE')")
	ResponseEntity<ResponseObject> updateEVehicle(
			@RequestBody EVehicleDTO eVehicleDTO) {
		
		EVehicleDTO eVehicleDTONew = iEVehicleService.updateEVehicle(eVehicleDTO);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Update Enterprise Vehicle successfully");
				setData(eVehicleDTONew);
			}
		});
	}
	
	private final String getDetailEVehicleDesc = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'eVehicleId': ''\n";
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	@Operation(tags = "Z Enterprise Vehicles", summary = "Get Detail Vehicle - ENTERPRISE", description = getDetailEVehicleDesc)
	@PreAuthorize("hasAnyRole('ROLE_ENTERPRISE')")
	ResponseEntity<ResponseObject> getDetailEVehicle(
			@RequestParam String eVehicleId) {
		
		EVehicleDTO eVehicleDTONew = iEVehicleService.getDetailEVehicle(eVehicleId);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get Detail Enterprise Vehicle successfully");
				setData(eVehicleDTONew);
			}
		});
	}
	
	private final String getAllEVehicleDesc = "";
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@Operation(tags = "Z Enterprise Vehicles", summary = "Get All Vehicle - ENTERPRISE", description = getAllEVehicleDesc)
	@PreAuthorize("hasAnyRole('ROLE_ENTERPRISE')")
	ResponseEntity<ResponseObject> getAllEVehicle() {
		
		List<EVehicleDTO> eVehicleDTOList = iEVehicleService.getAllEVehicle();
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get All Enterprise Vehicle successfully");
				setData(eVehicleDTOList);
			}
		});
	}
	
	private final String searchEVehicleByLocationDesc = "";
	@RequestMapping(value = "/location/search", method = RequestMethod.GET)
	@Operation(tags = "Z Enterprise Vehicles", summary = "Search Vehicle by location - ENTERPRISE", description = searchEVehicleByLocationDesc)
	@PreAuthorize("hasAnyRole('ROLE_ENTERPRISE')")
	ResponseEntity<ResponseObject> searchEVehicleByLocation(
			@ModelAttribute Location location) {
		
		List<EVehicleDTOSimple> eVehicleDTOSimpleList = iEVehicleService.searchEVehicleByLocation(location);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Search Vehicle by location successfully");
				setData(eVehicleDTOSimpleList);
			}
		});
	}
	
	private final String searchCoachDesc = "";
	@RequestMapping(value = "/coach/search", method = RequestMethod.GET)
	@Operation(tags = "Z Enterprise Vehicles - Coach", summary = "Search Coach", description = searchCoachDesc)
	ResponseEntity<ResponseObject> searchCoach(
			@ModelAttribute CoachSearch coachSearch) {
		
		List<CoachSearchRes> coachSearchResList = iEVehicleService.searchCoach(coachSearch);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Search Coach successfully");
				setData(coachSearchResList);
			}
		});
	}
}
