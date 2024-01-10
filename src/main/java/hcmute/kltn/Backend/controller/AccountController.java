package hcmute.kltn.Backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hcmute.kltn.Backend.model.account.dto.AccountDTO;
import hcmute.kltn.Backend.model.account.dto.AccountSetRole;
import hcmute.kltn.Backend.model.account.dto.AccountSort;
import hcmute.kltn.Backend.model.account.dto.AccountUpdateProfile;
import hcmute.kltn.Backend.model.account.dto.AuthRequest;
import hcmute.kltn.Backend.model.account.dto.AuthResponse;
import hcmute.kltn.Backend.model.account.dto.ChangePassword;
import hcmute.kltn.Backend.model.account.dto.RegisterRequest;
import hcmute.kltn.Backend.model.account.dto.ResetPasswordReq;
import hcmute.kltn.Backend.model.account.service.IAccountService;
import hcmute.kltn.Backend.model.base.Pagination;
import hcmute.kltn.Backend.model.base.response.dto.Response;
import hcmute.kltn.Backend.model.base.response.dto.ResponseObject;
import hcmute.kltn.Backend.model.base.response.service.IResponseObjectService;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/accounts")
@Tag(
		name = "Accounts", 
		description = "APIs for managing accounts\n\n",
		externalDocs = @ExternalDocumentation(
				description = "Update Api History", 
				url = "https://drive.google.com/file/d/1XJgZ6J5RRIIl2k17FIpC890iZYTD6mGk/view?usp=sharing")
		)
@SecurityRequirement(name = "Bearer Authentication")
public class AccountController {
	@Autowired
	private IAccountService iAccountService;
	@Autowired
	private IResponseObjectService iResponseObjectService;
	
	@CrossOrigin(origins = "http://travelover-api.up.railway.app")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@Operation(summary = "Login Account")
	ResponseEntity<ResponseObject> login(@RequestBody AuthRequest request) {
		AuthResponse response = iAccountService.login(request);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Login successfully");
				setData(response);
			}
		});
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@Operation(summary = "Register Account")
	ResponseEntity<ResponseObject> register(@RequestBody RegisterRequest registerRequest) {
		AccountDTO accountDTO =  iAccountService.register(registerRequest);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Create Account successfully");
				setData(accountDTO);
			}
		});
	}
	
	@RequestMapping(value = "/profile/update", method = RequestMethod.PUT)
	@Operation(summary = "Update Account - LOGIN")
	@PreAuthorize("isAuthenticated()")
	ResponseEntity<ResponseObject> updateProfile(
			@RequestBody AccountUpdateProfile updateAccountRequest) {
		AccountDTO accountDTO = iAccountService.updateProfile(updateAccountRequest);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Update Account successfully");
				setData(accountDTO);
			}
		});
	}
	
	@RequestMapping(value = "/profile/detail", method = RequestMethod.GET)
	@Operation(summary = "Get profile Account - LOGIN")
	@PreAuthorize("isAuthenticated()")
	ResponseEntity<ResponseObject> getProfile() {
		AccountDTO accountDTO = iAccountService.getProfile();
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Query Account successfully");
				setData(accountDTO);
			}
		});
	}

//	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
//	@Operation(summary = "Delete Account")
//	ResponseEntity<ResponseObject> deleteAccount(@PathVariable String id) {
//		boolean delete = iUserService.delete(id);
//		
//		return iResponseObjectService.success(new Response() {
//			{
//				setMessage("Delete Account successfully");
//			}});
//	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@Operation(summary = "Get all Account - ADMIN")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	ResponseEntity<ResponseObject> getAllAccounts(
			@ModelAttribute Pagination pagination) {
		List<AccountDTO> accountDTOList = iAccountService.getAllAccount();
		
		// default sort
		AccountSort accountSort = new AccountSort();
		accountSort.setSortBy("createdAt2");
		accountSort.setOrder("desc");
		List<AccountDTO> accountDTOListNew = new ArrayList<>();
		accountDTOListNew.addAll(iAccountService.listAccountSort(accountSort, accountDTOList));
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get all Accounts");
				setPageSize(pagination.getPageSize());
				setPageNumber(pagination.getPageNumber());
				setData(accountDTOListNew);
			}
		});
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@Operation(summary = "Search Account by keyword - ADMIN")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	ResponseEntity<ResponseObject> getProfile(@RequestParam String keyword) {
		List<AccountDTO> accountDTOList = iAccountService.searchAccount(keyword);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Search Account successfully");
				setData(accountDTOList);
			}
		});
	}
	
	private final String listAccountSearchDesc = "Search account bằng keyword, search trên bảng account "
			+ "- accountFilter: nhập dạng 'tên field': 'giá trị' (thêm bao nhiêu field tùy ý, "
			+ "khi filter sẽ tìm đúng tên field và xem giá trị từ account có chứa giá trị nhập vào)\n\n"
			+ "- - 'role': 'CUSTOMER',\n\n"
			+ "- - 'createdAt2': '2023-12'\n\n"
			+ "- accountSort: nhập tên field và kiểu sort có 2 kiểu là asc hoặc desc (chỉ sort theo 1 cột)";
	@RequestMapping(value = "/list/search", method = RequestMethod.GET)
	@Operation(summary = "Search account for admin page - ADMIN / STAFF", description = listAccountSearchDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
	ResponseEntity<ResponseObject> listAccountSearch(
			@RequestParam(required = false) String keyword,
			@RequestParam HashMap<String, String> accountFilter,
			@ModelAttribute AccountSort accountSort,
			@ModelAttribute Pagination pagination) {
		List<AccountDTO> accountDTOList = iAccountService.listAccountSearch(keyword);
		List<AccountDTO> accountDTOFilterList = iAccountService.listAccountFilter(accountFilter, accountDTOList);
		if (accountSort == null) {
			accountSort = new AccountSort();
			accountSort.setSortBy("createdAt2");
			accountSort.setOrder("desc");
		} else if (accountSort.getSortBy() == null) {
			accountSort.setSortBy("createdAt2");
			accountSort.setOrder("desc");
		} else if (accountSort.getSortBy().isEmpty()) {
			accountSort.setSortBy("createdAt2");
			accountSort.setOrder("desc");
		}
		List<AccountDTO> accountDTOSortList = iAccountService.listAccountSort(accountSort, accountDTOFilterList);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Search account for admin page successfully");
				setPageSize(pagination.getPageSize());
				setPageNumber(pagination.getPageNumber());
				setData(accountDTOSortList);
			}
		});
	}
	
	private final String setRoleDesc = "Cập nhật role cho tài khoản với giá trị tương ứng:\n\n"
			+ "- 1: ADMIN\n\n"
			+ "- 2: STAFF\n\n"
			+ "- 3: CUSTOMER";
	@RequestMapping(value = "/set-role", method = RequestMethod.PUT)
	@Operation(summary = "Set account role - ADMIN", description = setRoleDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	ResponseEntity<ResponseObject> setRole(
			@RequestBody AccountSetRole accountSetRole) {
		iAccountService.setRole(accountSetRole);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Set account role successfully");
			}
		});
	}
	
	private final String changePasswordDesc = "Thay đổi password khi đang đăng nhặp:\n\n"
			+ "- api chỉ có password và newPassword nên FE tự check cconfirmPassword và độ dài "
			+ "(hoặc các yêu cầu về password như lúc đăng ký tài khoản)\n\n"
			+ "- api chỉ trả về lỗi khi password không đúng với password hiện tại của account";
	@RequestMapping(value = "/password/change", method = RequestMethod.PUT)
	@Operation(summary = "Change password - LOGIN", description = changePasswordDesc)
	@PreAuthorize("isAuthenticated()")
	ResponseEntity<ResponseObject> changePassword(
			@RequestBody ChangePassword changePassword) {
		iAccountService.changePassword(changePassword);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Change password successfully");
			}
		});
	}
	
	private final String requestResetPasswordDesc = "Yêu cầu đặt lại mật khẩu:\n\n"
			+ "- Mã xác nhận sẽ được gửi qua email\n\n"
			+ "- Trả về lỗi nếu email chưa đuọc đăng ký tài khoản";
	@RequestMapping(value = "/password/request-reset", method = RequestMethod.GET)
	@Operation(summary = "Request Reset password", description = requestResetPasswordDesc)
	ResponseEntity<ResponseObject> requestResetPassword(
			@RequestParam String email) {
		iAccountService.requestResetPassword(email);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Request Reset password successfully");
			}
		});
	}
	
	private final String resetPasswordDesc = "Đặt lại mật khẩu:\n\n"
			+ "- Các field bắt buộc phải nhập\n\n"
			+ "- - email: '' (lấy email từ bước yêu cầu reset password)\n\n"
			+ "- - code: '' (lấy code được gửi trong email yêu cầu reset password)\n\n"
			+ "- - newPassword: '' (trên FE thêm 1 bước kiểm tra confirm password và các yêu cầu về password nếu có)\n\n"
			+ "- Trả về lỗi 'Invalid verification code' nếu email chưa đuọc đăng ký tài khoản "
			+ "hoặc code không hợp lệ hoặc tài khoản không thực hiện reset password";
	@RequestMapping(value = "/password/reset", method = RequestMethod.PUT)
	@Operation(summary = "Reset password", description = resetPasswordDesc)
	ResponseEntity<ResponseObject> resetPassword(
			@RequestBody ResetPasswordReq resetPasswordReq) {
		iAccountService.resetPassword(resetPasswordReq);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Reset password successfully");
			}
		});
	}
}
