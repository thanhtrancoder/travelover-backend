package hcmute.kltn.Backend.model.order.dto.extend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuiderDetail {
	private String personId;
	private String personName;
	private String gender;
	private String phoneNumber;
	private int price;
}
