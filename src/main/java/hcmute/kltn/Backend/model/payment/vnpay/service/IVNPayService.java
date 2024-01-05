package hcmute.kltn.Backend.model.payment.vnpay.service;

import javax.servlet.http.HttpServletRequest;

import hcmute.kltn.Backend.model.payment.vnpay.dto.VNPayCreate;
import hcmute.kltn.Backend.model.payment.vnpay.dto.VNPayRefund;
import hcmute.kltn.Backend.model.payment.vnpay.dto.VNPaymentDTO;

public interface IVNPayService {
	public VNPaymentDTO createVNPayment(VNPaymentDTO vnpaymentDTO);
//	public VNPaymentDTO updateHotel(HotelUpdate hotelUpdate);
	public VNPaymentDTO getDetailHotel(String vnPaymentId);
	
	public String createPayment(VNPayCreate vnPayCreate, String ipAddress);
	 
	public void refundPayment(VNPayRefund vnPayRefund, String ipAddress);
	 
	public int checkPayment(HttpServletRequest request);
}
