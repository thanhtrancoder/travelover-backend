package hcmute.kltn.Backend.model.account.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import hcmute.kltn.Backend.model.account.dto.AccountDTO;
import hcmute.kltn.Backend.model.account.dto.AccountSetRole;
import hcmute.kltn.Backend.model.account.dto.AccountSort;
import hcmute.kltn.Backend.model.account.dto.AccountUpdateProfile;
import hcmute.kltn.Backend.model.account.dto.AuthRequest;
import hcmute.kltn.Backend.model.account.dto.AuthResponse;
import hcmute.kltn.Backend.model.account.dto.ChangePassword;
import hcmute.kltn.Backend.model.account.dto.RegisterRequest;
import hcmute.kltn.Backend.model.account.dto.ResetPasswordReq;
import hcmute.kltn.Backend.model.account.dto.entity.Account;

public interface IAccountService {
	public AccountDTO register(RegisterRequest registerRequest);
	public AccountDTO updateProfile(AccountUpdateProfile accountUpdateProfile);
	public AccountDTO getProfile();
	public AccountDTO getDetailAccount(String accountId);
	
	public AuthResponse login(AuthRequest request);
	
	public List<AccountDTO> getAllAccount();
	public List<AccountDTO> searchAccount(String keyword);
	public List<AccountDTO> listAccountSearch(String keyword);
	public List<AccountDTO> listAccountFilter(HashMap<String, String> accountFilter, List<AccountDTO> accountDTOList);
	public List<AccountDTO> listAccountSort(AccountSort accountSort, List<AccountDTO> accountDTOList);

	
	public boolean initData(AccountDTO accountDTO);
	
	public void setRole(AccountSetRole accountSetRole);
	public void changePassword(ChangePassword changePassword);
	public void requestResetPassword(String email);
	public void resetPassword(ResetPasswordReq resetPasswordReq);
}
