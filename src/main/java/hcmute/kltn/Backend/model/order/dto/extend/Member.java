package hcmute.kltn.Backend.model.order.dto.extend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {
	private String fullName;
	private int age;
	private String gender;
	private String frontIDImageUrl; 
	private String backIDImageUrl; 
}
