package hcmute.kltn.Backend.model.account.dto;

import java.util.List;

import hcmute.kltn.Backend.model.account.dto.extend.Notification;
import hcmute.kltn.Backend.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO extends BaseEntity{
	private String accountId;
	private String firstName;
	private String lastName;
	private String email;
	private String role;
	private String avatar; 
	private String address;
	private String phoneNumber;
	private String rank;
	private List<Notification> notification;
}
