package hcmute.kltn.Backend.model.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaymentUpdate {
	private String vnPaymentId;
	private String orderId;
	private String method;
	private int amount;
}
