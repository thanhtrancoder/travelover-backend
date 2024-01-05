package hcmute.kltn.Backend.model.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class testOrderSuccess {
	private String to;
	private String orderId;
}
