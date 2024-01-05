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
import hcmute.kltn.Backend.model.tour.dto.StatusUpdate;
import hcmute.kltn.Backend.model.tour.dto.TourClone;
import hcmute.kltn.Backend.model.tour.dto.TourCreate;
import hcmute.kltn.Backend.model.tour.dto.TourDTO;
import hcmute.kltn.Backend.model.tour.dto.TourFilter;
import hcmute.kltn.Backend.model.tour.dto.TourSearch;
import hcmute.kltn.Backend.model.tour.dto.TourSearchRes;
import hcmute.kltn.Backend.model.tour.dto.TourSearchRes2;
import hcmute.kltn.Backend.model.tour.dto.TourSort;
import hcmute.kltn.Backend.model.tour.dto.TourUpdate;
import hcmute.kltn.Backend.model.tour.service.ITourService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/tours")
@Tag(
		name = "Tours", 
		description = "APIs for managing tours\n\n"
				+ "__04/01/2024__\n\n"
				+ "__10:20AM__\n\n"
				+ "Cập nhật: hiển thị đánh giá cho từng tour\n\n"
				+ "- field numberOfReviewer là số lượng người đã đánh giá\n\n"
				+ "- field rate là số sao đánh giá (từ 1 đến 5, chưa ai đánh giá là 0)\n\n"
				+ "- field reviewer là danh sách các đánh giá",
		externalDocs = @ExternalDocumentation(
				description = "Update Api History", 
				url = "https://drive.google.com/file/d/1jrATNUoOWUdZ64oVM93gr9x_sDQnMvmX/view?usp=sharing")
		)
@SecurityRequirement(name = "Bearer Authentication")
public class TourController {
	@Autowired
	private ITourService iTourService;
	@Autowired
	private IResponseObjectService iResponseObjectService;
	
	private final String createTourDescription = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'tourTitle': ''\n"
			+ "- 'numberOfDay': ''\n"
			+ "- 'address': ''\n"
			+ "- 'TourDetail': ''\n"
			+ "- 'reasonableTime': ''\n";
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@Operation(summary = "Create tour - ADMIN / STAFF", description = createTourDescription)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> createTour(
			@RequestBody TourCreate tourCreate) {
		iTourService.updateIsDiscount();
		TourDTO tourDTO = iTourService.createTour(tourCreate);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Create Tour successfully");
				setData(tourDTO);
			}
		});
	}

	private final String updateTourDescription = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'tourId': ''\n"
			+ "- 'tourTitle': ''\n"
			+ "- 'numberOfDay': ''\n"
			+ "- 'address': ''\n"
			+ "- 'TourDetail': ''\n"
			+ "- 'reasonableTime': ''\n";
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@Operation(summary = "Update tour - ADMIN / STAFF", description = updateTourDescription)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> updateTour(
			@RequestBody TourUpdate tourUpdate) {
		iTourService.updateIsDiscount();
		TourDTO tourDTO = iTourService.updateTour(tourUpdate);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Update tour successfully");
				setData(tourDTO);
			}
		});
	}
	
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	@Operation(summary = "Get tour detail")
	ResponseEntity<ResponseObject> getDetail(@RequestParam String tourId) {
		iTourService.updateIsDiscount();
		TourDTO tourDTO = iTourService.getDetailTour(tourId);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get detail tour successfully");
				setData(tourDTO);
			}
		});
	}
	
	private final String getAllTourDesc = "Các field bắt buộc phải nhập:\n"
			+ "- 'pageSize': ''\n"
			+ "- 'pageNumber': ''\n\n"
			+ "pageSize: Số lượng item có trong 1 trang\n\n"
			+ "pageNumber: Trang hiện tại\n\n"
			+ "Các trường hợp sử dụng\n"
			+ "- Không phân trang: pageSize = 0, pageNumber = 0\n"
			+ "- - pageSize = 0\n"
			+ "- - pageNumber = 0\n"
			+ "- Có phân trang: \n"
			+ "- - pageSize > 0\n"
			+ "- - pageNumber > 0\n"
			+ "- Lấy trang cuối cùng: \n"
			+ "- - pageSize > 0\n"
			+ "- - pageNumber = -1\n";
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@Operation(summary = "Get all tour - ADMIN / STAFF", description = getAllTourDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> getAllTour(
			@ModelAttribute Pagination pagination) {
		iTourService.updateIsDiscount();
		List<TourDTO> tourDTOList = iTourService.getAllTour();
		
		// default sort
		TourSort tourSort = new TourSort();
		tourSort.setSortBy("createdAt2");
		tourSort.setOrder("desc");
		List<TourDTO> tourDTOListNew = new ArrayList<>();
		tourDTOListNew.addAll(iTourService.listTourSort(tourSort, tourDTOList));
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get all tour successfully");
				setPageSize(pagination.getPageSize());
				setPageNumber(pagination.getPageNumber());
				setData(tourDTOListNew);
			}
		});
	}
	
	@RequestMapping(value = "/discount/update", method = RequestMethod.GET)
	@Operation(summary = "Manual update isDiscount - ADMIN / STAFF")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> updateIsDiscountNoCheck() {
		iTourService.updateIsDiscountNoCheck();
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("update isDiscount successfully");
			}
		});
	}
	
	private final String searchTourDesc = "Các field bắt buộc phải nhập (áp dụng cho Pagination):\n"
			+ "- 'pageSize': ''\n"
			+ "- 'pageNumber': ''\n\n"
			+ "pageSize: Số lượng item có trong 1 trang\n\n"
			+ "pageNumber: Trang hiện tại\n\n"
			+ "Các trường hợp sử dụng\n"
			+ "- Không phân trang: pageSize = 0, pageNumber = 0\n"
			+ "- - pageSize = 0\n"
			+ "- - pageNumber = 0\n"
			+ "- Có phân trang: \n"
			+ "- - pageSize > 0\n"
			+ "- - pageNumber > 0\n"
			+ "- Lấy trang cuối cùng: \n"
			+ "- - pageSize > 0\n"
			+ "- - pageNumber = -1\n\n"
			+ "tourFilter: không dùng thì không truyền gì hết\n"
			+ "- - minPrice >= 0\n"
			+ "- - maxPrice >= minPrice\n"
			+ "- - 1 <= ratingFilter <= 5\n\n"
			+ "tourSort: không dùng thì không truyền gì hết (chưa xử lý promotion)\n"
			+ "- - sortBy = 1 trong 3 loại là popular, price, promotion\n"
			+ "- - order = 1 trong 2 loại là asc, desc\n";
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@Operation(summary = "Search tour", description = searchTourDesc)
//	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> searchTour(
			@ModelAttribute TourSearch tourSearch,
			@ModelAttribute TourFilter tourFilter,
			@ModelAttribute TourSort tourSort,
			@ModelAttribute Pagination Pagination) {
		iTourService.updateIsDiscount();
		List<TourSearchRes> tourList = iTourService.searchTour(tourSearch);
		
		List<TourSearchRes> tourFilterList = iTourService.searchFilter(tourFilter, tourList);
		
		List<TourSearchRes> tourSortList = iTourService.searchSort(tourSort, tourFilterList);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Search tour successfully");
				setPageSize(Pagination.getPageSize());
				setPageNumber(Pagination.getPageNumber());
				setData(tourSortList);
			}
		});
	}
	
	private final String getAllDiscountTourDes = "Lấy ra 8 tour giảm giá nhiều nhất cho 1 người lớn và 1 phòng";
	@RequestMapping(value = "/list-discount-tour", method = RequestMethod.GET)
	@Operation(summary = "Get 8 discount tour", description = getAllDiscountTourDes)
	ResponseEntity<ResponseObject> getAllDiscountTour() {
		iTourService.updateIsDiscount();
		List<TourSearchRes> tourSearchResList = iTourService.getAllDiscountTour();

		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get 8 discount tour successfully");
				setData(tourSearchResList);
			}
		});
	}
	
	private final String updateStatusDesc = "Tính năng xóa tour:\n\n"
			+ "- status = true -> enable\n\n"
			+ "- status = false -> disable";
	@RequestMapping(value = "/status/update", method = RequestMethod.PUT)
	@Operation(summary = "Update tour status - ADMIN / STAFF", description = updateStatusDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> updateStatus(
			@RequestBody StatusUpdate statusUpdate) {
		iTourService.updateIsDiscount();
		TourDTO tourDTO = iTourService.updateStatus(statusUpdate);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Update tour status successfully");
				setData(tourDTO);
			}
		});
	}
	
	private final String listSearchDesc = "Search tour bằng keyword, search trên bảng tour "
			+ "(chỉ search với tour, không liên quan đến các bảng khác)\n\n"
			+ "- tourFilter: nhập dạng 'tên field': 'giá trị' (thêm bao nhiêu field tùy ý, "
			+ "khi filter sẽ tìm đúng tên field và xem giá trị từ tour có chứa giá trị nhập vào)\n\n"
			+ "- - 'numberOfDay': '1',\n\n"
			+ "- - 'numberOfNight': '2'\n\n"
			+ "- tourSort: nhập tên field và kiểu sort có 2 kiểu là asc hoặc desc (chỉ sort theo 1 cột)";
	@RequestMapping(value = "/list/search", method = RequestMethod.GET)
	@Operation(summary = "Search tour for admin page - ADMIN / STAFF", description = listSearchDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> listTourSearch(
			@RequestParam(required = false) String keyword,
			@RequestParam HashMap<String, String> tourFilter,
			@ModelAttribute TourSort tourSort,
			@ModelAttribute Pagination pagination) {
		iTourService.updateIsDiscount();
		List<TourDTO> tourDTOList = iTourService.listTourSearch(keyword);
		List<TourDTO> tourDTOFilterList = iTourService.listTourFilter(tourFilter, tourDTOList);
		if (tourSort == null) {
			tourSort = new TourSort();
			tourSort.setSortBy("createdAt2");
			tourSort.setOrder("desc");
		} else if (tourSort.getSortBy() == null) {
			tourSort.setSortBy("createdAt2");
			tourSort.setOrder("desc");
		} else if (tourSort.getSortBy().isEmpty()) {
			tourSort.setSortBy("createdAt2");
			tourSort.setOrder("desc");
		}
		List<TourDTO> tourDTOSortList = iTourService.listTourSort(tourSort, tourDTOFilterList);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Search tour for admin page successfully");
				setPageSize(pagination.getPageSize());
				setPageNumber(pagination.getPageNumber());
				setData(tourDTOSortList);
			}
		});
	}
	
	private final String autoUpdateIdDesc = "Cập nhật id của các tour clone bị id lum la, không theo chuẩn";
	@RequestMapping(value = "/auto-update-id", method = RequestMethod.GET)
	@Operation(summary = "Auto Update Id - ADMIN / STAFF", description = autoUpdateIdDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> autoUpdateId() {
		iTourService.autoUpdateId();
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Auto Update Id successfully");
			}
		});
	}
	
	private final String cloneTourDesc = "Clone tour bằng id, tour clone có id theo chuẩn, không bị lỗi";
	@RequestMapping(value = "/clone", method = RequestMethod.POST)
	@Operation(summary = "Clone tour - ADMIN / STAFF", description = autoUpdateIdDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> cloneTour(
			@RequestBody TourClone tourClone) {
		TourDTO tourDTO = iTourService.cloneTour(tourClone);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Clone tour successfully");
				setData(tourDTO);
			}
		});
	}
	
	private final String searchTour2Desc = "Các field bắt buộc phải nhập (áp dụng cho Pagination):\n"
			+ "- 'pageSize': ''\n"
			+ "- 'pageNumber': ''\n\n"
			+ "pageSize: Số lượng item có trong 1 trang\n\n"
			+ "pageNumber: Trang hiện tại\n\n"
			+ "Các trường hợp sử dụng\n"
			+ "- Không phân trang: pageSize = 0, pageNumber = 0\n"
			+ "- - pageSize = 0\n"
			+ "- - pageNumber = 0\n"
			+ "- Có phân trang: \n"
			+ "- - pageSize > 0\n"
			+ "- - pageNumber > 0\n"
			+ "- Lấy trang cuối cùng: \n"
			+ "- - pageSize > 0\n"
			+ "- - pageNumber = -1\n\n"
			+ "tourFilter: không dùng thì không truyền gì hết\n"
			+ "- - minPrice >= 0\n"
			+ "- - maxPrice >= minPrice\n"
			+ "- - 1 <= ratingFilter <= 5\n\n"
			+ "tourSort: không dùng thì không truyền gì hết (chưa xử lý promotion)\n"
			+ "- - sortBy = 1 trong 3 loại là popular, price, promotion\n"
			+ "- - order = 1 trong 2 loại là asc, desc\n";
	@RequestMapping(value = "/search2", method = RequestMethod.GET)
	@Operation(summary = "Search tour", description = searchTour2Desc)
//	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> searchTour2(
			@ModelAttribute TourSearch tourSearch,
			@ModelAttribute TourFilter tourFilter,
			@ModelAttribute TourSort tourSort,
			@ModelAttribute Pagination Pagination) {
		iTourService.updateIsDiscount();
		List<TourSearchRes2> tourList = iTourService.searchTour2(tourSearch);
		
		List<TourSearchRes2> tourFilterList = iTourService.searchFilter2(tourFilter, tourList);
		
		List<TourSearchRes2> tourSortList = iTourService.searchSort2(tourSort, tourFilterList);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Search tour successfully");
				setPageSize(Pagination.getPageSize());
				setPageNumber(Pagination.getPageNumber());
				setData(tourSortList);
			}
		});
	}
	
	private final String updateDailyTourLimitDesc = "Bỏ trống tourId để update cho toàn bộ tour";
	@RequestMapping(value = "/update-daily-tour-limit", method = RequestMethod.GET)
	@Operation(summary = "Update Daily Tour Limit - ADMIN / STAFF", description = updateDailyTourLimitDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> updateDailyTourLimit(
			@RequestParam(required = false) String tourId,
			@RequestParam int dailyTourLimit) {
		iTourService.updateDailyTourLimit(tourId, dailyTourLimit);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Update Daily Tour Limit successfully");
			}
		});
	}
}