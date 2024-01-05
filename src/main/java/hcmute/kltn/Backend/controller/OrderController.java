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
import hcmute.kltn.Backend.model.order.dto.OrderCreate;
import hcmute.kltn.Backend.model.order.dto.OrderDTO;
import hcmute.kltn.Backend.model.order.dto.OrderStatusUpdate;
import hcmute.kltn.Backend.model.order.dto.Rating;
import hcmute.kltn.Backend.model.order.service.IOrderService;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/orders")
@Tag(
		name = "Orders", 
		description = "APIs for managing orders\n\n"
				+ "__04/01/2024__\n\n"
				+ "__10:20AM__\n\n"
				+ "Tạo mới: api rating để đánh giá đơn hàng đã đặt",
		externalDocs = @ExternalDocumentation(
				description = "Update Api History", 
				url = "https://drive.google.com/file/d/1G8DN3460uuAVgkwhOTvseSdWPT_4nAP3/view?usp=sharing")
		)
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {
	@Autowired
	private IOrderService iOrderService;
	@Autowired
	private IResponseObjectService iResponseObjectService;
	
	private final String createOrderDescription = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'startDate': ''\n"
			+ "- 'customerInformation': ''\n"
			+ "- 'numberOfChildren': ''\n"
			+ "- 'numberOfAdult': ''\n";
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@Operation(summary = "Create order - CUSTOMER", description = createOrderDescription)
	@PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
	ResponseEntity<ResponseObject> createOrder(
			@RequestBody OrderCreate orderCreate) {
		OrderDTO orderDTO = iOrderService.createOrder(orderCreate);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Create order successfully");
				setData(orderDTO);
			}
		});
	}
	
//	private final String updateOrderDescription = "Các field bắt buộc phải nhập:\n\n"
//			+ "- 'startDate': ''\n"
//			+ "- 'endDate': ''\n"
//			+ "- 'customerInformation': ''\n"
//			+ "- 'numberOfChildren': ''\n"
//			+ "- 'numberOfAdult': ''\n";
//	@RequestMapping(value = "/update", method = RequestMethod.PUT)
//	@Operation(summary = "Update order - STAFF", description = updateOrderDescription)
//	@PreAuthorize("hasAnyRole('ROLE_STAFF')")
//	ResponseEntity<ResponseObject> updateOrder(
//			@RequestBody OrderUpdate orderUpdate) {
//		Order order = iOrderService.updateOrder(orderUpdate);
//		
//		return iResponseObjectService.success(new Response() {
//			{
//				setMessage("Update order successfully");
//				setData(order);
//			}
//		});
//	}
	
	private final String updateOrderStatus = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'orderId': ''\n"
			+ "- 'status': ''\n\n"
			+ "status Nhập số tương ứng như sau:\n\n"
			+ "- 0 : canceled\n"
			+ "- 1 : pending\n"
			+ "- 2 : confirmed\n"
			+ "- 3 : underway\n"
			+ "- 4 : finished\n";
	@RequestMapping(value = "/status/update", method = RequestMethod.PUT)
	@Operation(summary = "Update order status - ADMIN / STAFF / CUSTOMER", description = updateOrderStatus)
//	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	@PreAuthorize("isAuthenticated()")
	ResponseEntity<ResponseObject> updateOrderStatus(
			@RequestBody OrderStatusUpdate orderStatusUpdate) {
		OrderDTO orderDTO = iOrderService.updateOrderStatus(orderStatusUpdate);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Update order status successfully");
				setData(orderDTO);
			}
		});
	}
	
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	@Operation(summary = "Get order - ADMIN / STAFF / CUSTOMER")
	@PreAuthorize("isAuthenticated()")
	ResponseEntity<ResponseObject> updateOrder(
			@RequestParam String orderId) {
		OrderDTO orderDTO = iOrderService.getDetailOrder(orderId);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get detail order successfully");
				setData(orderDTO);
			}
		});
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@Operation(summary = "Get all order - ADMIN / STAFF / CUSTOMER")
	@PreAuthorize("isAuthenticated()")
	ResponseEntity<ResponseObject> getAllOrder(
			@ModelAttribute Pagination pagination) {
		// delete order unpaid
		iOrderService.deleteUnpaidOrder();
		
		List<OrderDTO> orderDTOList = iOrderService.getAllOrder();
		
		// default sort
		Sort sort = new Sort();
		sort.setSortBy("createdAt2");
		sort.setOrder("desc");
		List<OrderDTO> orderDTOListNew = new ArrayList<>();
		orderDTOListNew.addAll(iOrderService.listOrderSort(sort, orderDTOList));
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get all order successfully");
				setPageSize(pagination.getPageSize());
				setPageNumber(pagination.getPageNumber());
				setData(orderDTOListNew);
			}
		});
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@Operation(summary = "Search order by keyword - ADMIN / STAFF / CUSTOMER")
	@PreAuthorize("isAuthenticated()")
	ResponseEntity<ResponseObject> searchOrder(
			@RequestParam String keyword) {
		List<OrderDTO> orderDTOList = iOrderService.searchOrder(keyword);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Search order successfully");
				setData(orderDTOList);
			}
		});
	} 
	
	private final String listOrderSearchDesc = "Search hotel bằng keyword, search trên bảng hotel "
			+ "- filter: nhập dạng 'tên field': 'giá trị' (thêm bao nhiêu field tùy ý, "
			+ "khi filter sẽ tìm đúng tên field và xem giá trị từ account có chứa giá trị nhập vào)\n\n"
			+ "- - 'hotelDescription': 'Khách sạn',\n\n"
			+ "- - 'createdAt2': '2023-12'\n\n"
			+ "- sort: nhập tên field và kiểu sort có 2 kiểu là asc hoặc desc (chỉ sort theo 1 cột)";
	@RequestMapping(value = "/list/search", method = RequestMethod.GET)
	@Operation(summary = "Search order for admin page - ADMIN / STAFF", description = listOrderSearchDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> listOrderSearch(
			@RequestParam(required = false) String keyword,
			@RequestParam HashMap<String, String> filter,
			@ModelAttribute Sort sort,
			@ModelAttribute Pagination pagination) {
		List<OrderDTO> orderDTOList = iOrderService.listOrderSearch(keyword);
		List<OrderDTO> orderDTOFilterList = iOrderService.listOrderFilter(filter, orderDTOList);
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
		List<OrderDTO> orderDTOSortList = iOrderService.listOrderSort(sort, orderDTOFilterList);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Search order for admin page successfully");
				setPageSize(pagination.getPageSize());
				setPageNumber(pagination.getPageNumber());
				setData(orderDTOSortList);
			}
		});
	}
	
	@RequestMapping(value = "/payment/check", method = RequestMethod.GET)
	@Operation(summary = "Payment check")
//	@PreAuthorize("isAuthenticated()")
	ResponseEntity<ResponseObject> paymentCheck(
			@RequestParam String orderId) {
		boolean paymentCheck = iOrderService.paymentCheck(orderId);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Payment check successfully");
				setData(paymentCheck);
			}
		});
	}
	
	private final String ratingDesc = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'orderId': ''\n"
			+ "- 'rate': '' (1 <= rate <= 5)\n"
			+ "- 'review': ''\n\n"
			+ "Chỉ được đánh giá đơn hàng do tài khoản đang đang nhập tạo\n\n"
			+ "Chỉ được đánh giá đơn hàng đã hoàn thành\n\n"
			+ "Mỗi đơn hàng chỉ được đánh giá 1 lần";
	@RequestMapping(value = "/rating", method = RequestMethod.POST)
	@Operation(summary = "Rating order - CUSTOMER", description = ratingDesc)
	@PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
	ResponseEntity<ResponseObject> rating(
			@RequestBody Rating rating) {
		iOrderService.rating(rating);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Rating order successfully");
			}
		});
	}
}
