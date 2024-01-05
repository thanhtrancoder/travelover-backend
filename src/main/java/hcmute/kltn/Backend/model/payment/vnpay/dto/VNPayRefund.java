package hcmute.kltn.Backend.model.payment.vnpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VNPayRefund {
	private String vnPaymentId;
	private String orderInfo;
	private String createBy;
}
