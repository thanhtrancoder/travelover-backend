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
import hcmute.kltn.Backend.model.discount.dto.DiscountCreate;
import hcmute.kltn.Backend.model.discount.dto.DiscountDTO;
import hcmute.kltn.Backend.model.discount.dto.DiscountUpdate;
import hcmute.kltn.Backend.model.discount.service.IDiscountService;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/discounts")
@Tag(
		name = "Discounts", 
		description = "APIs for managing discounts\n\n",
		externalDocs = @ExternalDocumentation(
				description = "Update Api History", 
				url = "https://drive.google.com/file/d/1K0hAFY-8JF1Az9ocQlKalb9W5TT003vN/view?usp=sharing")
		)
@SecurityRequirement(name = "Bearer Authentication")
public class DiscountController {
	@Autowired
	private IResponseObjectService iResponseObjectService;
	@Autowired
	private IDiscountService iDiscountService;
	
	private final String createDiscountDescription = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'discountCode': '' (nhập theo ý muốn hoặc bỏ trống để tự tạo)\n"
			+ "- 'discountTitle': '' \n"
			+ "- 'discountValue': '' (giá trị giảm giá, đơn vị là %, chỉ cần nhập số)\n"
			+ "- 'startDate': ''\n"
			+ "- 'endDate': ''\n"
			+ "- 'minOrder': '' (giá trị đơn hàng tối thiểu)\n"
			+ "- 'maxDiscount': '' (giảm giá tối đa)\n"
			+ "- 'isQuantityLimit': '' (giới hạn số lượng)\n"
			+ "- 'numberOfCode': '' (nhập nếu isQuantityLimit = true)\n";
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@Operation(summary = "Create discount - ADMIN / STAFF", description = createDiscountDescription)
	@PreAuthorize("hasAnyRole('ROLE_STAFF')")
	ResponseEntity<ResponseObject> createDiscount(
			@RequestBody DiscountCreate discountCreate) {
		DiscountDTO discountDTO = iDiscountService.createDiscount(discountCreate);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Create Discount successfully");
				setData(discountDTO);
			}
		});
	}
	
	private final String updateDiscountDescription = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'discountCode': '' (nhập theo ý muốn hoặc bỏ trống để dùng mã cũ hoặc nhập mã cũ để dùng mã cũ)\n"
			+ "- 'discountTitle': '' \n"
			+ "- 'discountValue': '' (giá trị giảm giá, đơn vị là %, chỉ cần nhập số)\n"
			+ "- 'startDate': ''\n"
			+ "- 'endDate': ''\n"
			+ "- 'minOrder': '' (giá trị đơn hàng tối thiểu)\n"
			+ "- 'maxDiscount': '' (giảm giá tối đa)\n"
			+ "- 'isQuantityLimit': '' (giới hạn số lượng)\n"
			+ "- 'numberOfCode': '' (nhập nếu isQuantityLimit = true)\n";
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@Operation(summary = "Update Discount - ADMIN / STAFF", description = updateDiscountDescription)
	@PreAuthorize("hasAnyRole('ROLE_STAFF')")
	ResponseEntity<ResponseObject> updateDiscount(
			@RequestBody DiscountUpdate discountUpdate) {
		DiscountDTO discountDTO = iDiscountService.updateDiscount(discountUpdate);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Update Discount successfully");
				setData(discountDTO);
			}
		});
	}
	
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	@Operation(summary = "Get detail discount - ADMIN / STAFF")
	@PreAuthorize("hasAnyRole('ROLE_STAFF')")
	ResponseEntity<ResponseObject> getDetailDiscount(
			@RequestParam String discountId) {
		DiscountDTO discountDTO = iDiscountService.getDetailDiscount(discountId);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get Detail Discount successfully");
				setData(discountDTO);
			}
		});
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@Operation(summary = "Get all discount - ADMIN / STAFF")
	@PreAuthorize("hasAnyRole('ROLE_STAFF')")
	ResponseEntity<ResponseObject> getAlllDiscount(
			@ModelAttribute Pagination pagination) {
		List<DiscountDTO> discountDTOList = iDiscountService.getAllDiscount();
		
		// default sort
		Sort sort = new Sort();
		sort.setSortBy("createdAt2");
		sort.setOrder("desc");
		List<DiscountDTO> discountDTOListNew = new ArrayList<>();
		discountDTOListNew.addAll(iDiscountService.listDiscountSort(sort, discountDTOList));
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get All Discount successfully");
				setPageSize(pagination.getPageSize());
				setPageNumber(pagination.getPageNumber());
				setData(discountDTOListNew);
			}
		});
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@Operation(summary = "Search discount by keyword - ADMIN / STAFF")
	@PreAuthorize("hasAnyRole('ROLE_STAFF')")
	ResponseEntity<ResponseObject> searchDiscount(
			@RequestParam String keyword) {
		List<DiscountDTO> discountDTOList = iDiscountService.searchDiscount(keyword);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Search Discount successfully");
				setData(discountDTOList);
			}
		});
	}
	
	private final String listDiscountSearchDesc = "Search discount bằng keyword, search trên bảng discount "
			+ "- discountFilter: nhập dạng 'tên field': 'giá trị' (thêm bao nhiêu field tùy ý, "
			+ "khi filter sẽ tìm đúng tên field và xem giá trị từ account có chứa giá trị nhập vào)\n\n"
			+ "- - 'minOrder': '0',\n\n"
			+ "- - 'createdAt2': '2023-12'\n\n"
			+ "- sort: nhập tên field và kiểu sort có 2 kiểu là asc hoặc desc (chỉ sort theo 1 cột)";
	@RequestMapping(value = "/list/search", method = RequestMethod.GET)
	@Operation(summary = "Search discount for admin page - ADMIN / STAFF", description = listDiscountSearchDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> listDiscountSearch(
			@RequestParam(required = false) String keyword,
			@RequestParam HashMap<String, String> discountFilter,
			@ModelAttribute Sort sort,
			@ModelAttribute Pagination pagination) {
		List<DiscountDTO> discountDTOList = iDiscountService.listDiscountSearch(keyword);
		List<DiscountDTO> discountDTOFilterList = iDiscountService.listDiscountFilter(discountFilter, discountDTOList);
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
		List<DiscountDTO> discountDTOSortList = iDiscountService.listDiscountSort(sort, discountDTOFilterList);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Search discount for admin page successfully");
				setPageSize(pagination.getPageSize());
				setPageNumber(pagination.getPageNumber());
				setData(discountDTOSortList);
			}
		});
	}
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	@Operation(summary = "Get discount by discountCode")
	ResponseEntity<ResponseObject> getDiscountByCode(
			@RequestParam String discountCode) {
		DiscountDTO discountDTO = iDiscountService.getDiscountByCode(discountCode);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get Discount successfully");
				setData(discountDTO);
			}
		});
	}
	
	private final String getActualDiscountValue = "Nhập discount code và total price để nhận về số tiền được giảm, trả về lỗi "
			+ "nếu mã áp dụng không thành công";
	@RequestMapping(value = "/actual-discount-value", method = RequestMethod.GET)
	@Operation(summary = "Get actual discount value", description = getActualDiscountValue)
	ResponseEntity<ResponseObject> getActualDiscountValue(
			@RequestParam String discountCode,
			@RequestParam int totalPrice) {
		int actualDiscountValue = iDiscountService.getActualDiscountValue(discountCode, totalPrice);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get actual discount value successfully");
				setData(actualDiscountValue);
			}
		});
	}
}
