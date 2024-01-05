package hcmute.kltn.Backend.model.order.dto.extend;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInformation {
	private String fullName;
	private int age;
	private String gender;
	private String email;
	private String phoneNumber;
	private String frontIDImageUrl; 
	private String backIDImageUrl; 
	private List<Member> member;
}
