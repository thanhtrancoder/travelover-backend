package hcmute.kltn.Backend.model.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordReq {
	private String email;
	private String code;
	private String newPassword;
}
