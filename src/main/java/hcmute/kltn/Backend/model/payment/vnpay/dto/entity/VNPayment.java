package hcmute.kltn.Backend.model.payment.vnpay.dto.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import hcmute.kltn.Backend.model.base.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Document(collection = "vnpayment")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class VNPayment extends BaseEntity{
	@Id
	private String vnPaymentId;
	private String name;
	private String orderId;
	private int amount;
	private String description;
	private String vnp_BankCode;
	private String vnp_TransactionNo;
	private String vnp_TmnCode;
	private String vnp_TxnRef;
	private String vnp_OrderInfo;
	private String vnp_Amount;
	private String vnp_ResponseCode;
	private String vnp_ResponseId;
	private String vnp_Command;
	private String vnp_PayDate;
	private String vnp_TransactionType;
	private String vnp_SecureHash;
	private String vnp_TransactionStatus;
	private String vnp_Message;
}
