package hcmute.kltn.Backend.model.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountUpdateProfile {
	private String firstName;
	private String lastName;
	private String avatar; // Foreign key
	private String address;
	private String phoneNumber;
}
