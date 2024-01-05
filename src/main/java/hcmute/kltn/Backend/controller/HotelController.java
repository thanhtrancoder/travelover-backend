package hcmute.kltn.Backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
import hcmute.kltn.Backend.model.base.Sort;
import hcmute.kltn.Backend.model.base.response.dto.Response;
import hcmute.kltn.Backend.model.base.response.dto.ResponseObject;
import hcmute.kltn.Backend.model.base.response.service.IResponseObjectService;
import hcmute.kltn.Backend.model.hotel.dto.HotelCreate;
import hcmute.kltn.Backend.model.hotel.dto.HotelDTO;
import hcmute.kltn.Backend.model.hotel.dto.HotelSearch;
import hcmute.kltn.Backend.model.hotel.dto.HotelUpdate;
import hcmute.kltn.Backend.model.hotel.service.IHotelService;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/hotels")
@Tag(
		name = "Hotels", 
		description = "APIs for managing hotels\n\n",
		externalDocs = @ExternalDocumentation(
				description = "Update Api History", 
				url = "https://drive.google.com/file/d/1luZ6dxUn-_lnFdEqee2fSk0vFVKPrvtg/view?usp=sharing")
		)
@SecurityRequirement(name = "Bearer Authentication")
public class HotelController {
	@Autowired
	private IHotelService iHotelService;
	@Autowired
	private IResponseObjectService iResObjService;
	
	private final String createHotelDesc = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'eHotelId': ''\n"
			+ "- 'hotelName': ''\n"
			+ "- 'contact': ''\n"
			+ "- 'address': ''\n";
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@Operation(summary = "Create hotel - ADMIN", description = createHotelDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	ResponseEntity<ResponseObject> createHotel(@RequestBody HotelCreate hotelCreate) {
		HotelDTO hotelDTO = iHotelService.createHotel(hotelCreate);
		
		return iResObjService.success(new Response() {
			{
				setMessage("Create hotel successfully");
				setData(hotelDTO);
			}
		});
	}
	
	private final String updateHotelDesc = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'hotelName': ''\n"
			+ "- 'contact': ''\n"
			+ "- 'address': ''\n";
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@Operation(summary = "Update hotel - ADMIN", description = updateHotelDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	ResponseEntity<ResponseObject> updateHotel(@RequestBody HotelUpdate hotelUpdate) {
		HotelDTO hotelDTO = iHotelService.updateHotel(hotelUpdate);
		
		return iResObjService.success(new Response() {
			{
				setMessage("Update hotel successfully");
				setData(hotelDTO);
			}
		});
	}
	
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	@Operation(summary = "Get hotel detail - ADMIN")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	ResponseEntity<ResponseObject> getDetailHotel(@RequestParam String hotelId) {
		HotelDTO hotelDTO = iHotelService.getDetailHotel(hotelId);
		
		return iResObjService.success(new Response() {
			{
				setMessage("Get hotel detail successfully");
				setData(hotelDTO);
			}
		});
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@Operation(summary = "Get all hotel - ADMIN")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	ResponseEntity<ResponseObject> getAllHotel(
			@ModelAttribute Pagination pagination) {
		List<HotelDTO> hotelDTOList = iHotelService.getAllHotel();
		
		// default sort
		Sort sort = new Sort();
		sort.setSortBy("createdAt2");
		sort.setOrder("desc");
		List<HotelDTO> hotelDTOListNew = new ArrayList<>();
		hotelDTOListNew.addAll(iHotelService.listHotelSort(sort, hotelDTOList));
		
		return iResObjService.success(new Response() {
			{
				setMessage("Get all hotel successfully");
				setPageSize(pagination.getPageSize());
				setPageNumber(pagination.getPageNumber());
				setData(hotelDTOListNew);
			}
		});
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@Operation(summary = "Search hotel - ADMIN")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	ResponseEntity<ResponseObject> searchHotel(@ModelAttribute HotelSearch hotelSearch) {
		List<HotelDTO> hotelDTOList = iHotelService.searchHotel(hotelSearch);
		
		return iResObjService.success(new Response() {
			{
				setMessage("Search hotel successfully");
				setData(hotelDTOList);
			}
		});
	}
	
	private final String listHotelSearchDesc = "Search hotel bằng keyword, search trên bảng hotel "
			+ "- filter: nhập dạng 'tên field': 'giá trị' (thêm bao nhiêu field tùy ý, "
			+ "khi filter sẽ tìm đúng tên field và xem giá trị từ account có chứa giá trị nhập vào)\n\n"
			+ "- - 'hotelDescription': 'Khách sạn',\n\n"
			+ "- - 'createdAt2': '2023-12'\n\n"
			+ "- sort: nhập tên field và kiểu sort có 2 kiểu là asc hoặc desc (chỉ sort theo 1 cột)";
	@RequestMapping(value = "/list/search", method = RequestMethod.GET)
	@Operation(summary = "Search hotel for admin page - ADMIN / STAFF", description = listHotelSearchDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> listHotelSearch(
			@RequestParam(required = false) String keyword,
			@RequestParam HashMap<String, String> filter,
			@ModelAttribute Sort sort,
			@ModelAttribute Pagination pagination) {
		List<HotelDTO> hotelDTOList = iHotelService.listHotelSearch(keyword);
		List<HotelDTO> hotelDTOFilterList = iHotelService.listHotelFilter(filter, hotelDTOList);
		if (sort == null) {
			sort = new Sort();
			sort.setSortBy("createdAt2");
			sort.setOrder("desc");
		} else if (sort.getSortBy() == null) {
			sort.setSortBy("createdAt2");
			sort.setOrder("desc");
		} else if (sort.getSortBy().isEmpty()) {
			sort.setSortBy("createdAt2");
			sort.setOrder("desc");
		}
		List<HotelDTO> hotelDTOSortList = iHotelService.listHotelSort(sort, hotelDTOFilterList);
		
		return iResObjService.success(new Response() {
			{
				setMessage("Search hotel for admin page successfully");
				setPageSize(pagination.getPageSize());
				setPageNumber(pagination.getPageNumber());
				setData(hotelDTOSortList);
			}
		});
	}
}
