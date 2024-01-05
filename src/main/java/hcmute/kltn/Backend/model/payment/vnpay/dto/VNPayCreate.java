package hcmute.kltn.Backend.model.payment.vnpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VNPayCreate {
	private int amount;
	private String orderType;
	private String orderInfo;
	private String returnUrl;
}
