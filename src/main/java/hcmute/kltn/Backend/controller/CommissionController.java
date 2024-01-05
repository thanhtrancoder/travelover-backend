package hcmute.kltn.Backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hcmute.kltn.Backend.model.base.response.dto.Response;
import hcmute.kltn.Backend.model.base.response.dto.ResponseObject;
import hcmute.kltn.Backend.model.base.response.service.IResponseObjectService;
import hcmute.kltn.Backend.model.commission.dto.CommissionDTO;
import hcmute.kltn.Backend.model.commission.service.ICommissionService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/commissions")
@Tag(
		name = "Commissions", 
		description = "APIs for managing commissions\n\n",
		externalDocs = @ExternalDocumentation(
				description = "Update Api History", 
				url = "https://drive.google.com/file/d/1dA0UtJCpz34hBvNURQfUxnuOsa-i_lfG/view?usp=sharing")
		)
@SecurityRequirement(name = "Bearer Authentication")
public class CommissionController {
	@Autowired
	private IResponseObjectService iResponseObjectService;
	@Autowired
	private ICommissionService iCommissionService;
	
	private final String createCommissionDesc = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'name': '' (Không được trùng)\n\n"
			+ "- 'rate': '' (đơn vị %, 0 <= rate <= 100)";
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@Operation(summary = "Create commission - ADMIN", description = createCommissionDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	ResponseEntity<ResponseObject> createCommission(
			@RequestBody CommissionDTO commissionDTO) {
		CommissionDTO commissionDTONew = iCommissionService.createCommission(commissionDTO);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Create commission successfully");
				setData(commissionDTONew);
			}
		});
	}
	
	private final String updateCommissionDesc = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'commissionId': ''\n\n"
			+ "- 'name': '' (Không được trùng)\n\n"
			+ "- 'rate': '' (đơn vị %, 0 <= rate <= 100)\n\n"
			+ "(Chỉ được chỉnh sửa hoa hồng chưa được áp dụng)";
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@Operation(summary = "Update commission - ADMIN", description = updateCommissionDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	ResponseEntity<ResponseObject> updateCommission(
			@RequestBody CommissionDTO commissionDTO) {
		CommissionDTO commissionDTONew = iCommissionService.updateCommission(commissionDTO);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Update commission successfully");
				setData(commissionDTONew);
			}
		});
	}
	
	private final String getCommissionDetailDesc = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'commissionId': ''\n\n";
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	@Operation(summary = "Get commission detail- ADMIN", description = getCommissionDetailDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	ResponseEntity<ResponseObject> getCommissionDetail(
			@RequestParam String commissionId) {
		CommissionDTO commissionDTONew = iCommissionService.getDetailCommission(commissionId);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get commission detail successfully");
				setData(commissionDTONew);
			}
		});
	}
	
	private final String getAllCommissionDesc = "";
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@Operation(summary = "Get all commission - ADMIN", description = getAllCommissionDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	ResponseEntity<ResponseObject> getAllCommission() {
		List<CommissionDTO> commissionDTOList = iCommissionService.getAllCommission();
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Get all commission successfully");
				setData(commissionDTOList);
			}
		});
	}
	
	private final String enableCommissionDesc = "Các field bắt buộc phải nhập:\n\n"
			+ "- 'commissionId': ''\n\n";
	@RequestMapping(value = "/enable", method = RequestMethod.GET)
	@Operation(summary = "Enable commission - ADMIN", description = enableCommissionDesc)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	ResponseEntity<ResponseObject> enableCommission(
			@RequestParam String commissionId) {
		CommissionDTO commissionDTO = iCommissionService.enableCommission(commissionId);
		
		return iResponseObjectService.success(new Response() {
			{
				setMessage("Enable commission successfully");
				setData(commissionDTO);
			}
		});
	}
}
