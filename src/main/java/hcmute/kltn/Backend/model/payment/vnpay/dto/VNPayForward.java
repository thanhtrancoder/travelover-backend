package hcmute.kltn.Backend.model.payment.vnpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VNPayForward {
	private String vnp_OrderInfo;
	private String vnp_ResponseCode;
	private String vnp_TransactionStatus;
	private String vnp_SecureHash;
}
