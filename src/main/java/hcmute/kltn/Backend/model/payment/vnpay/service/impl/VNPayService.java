package hcmute.kltn.Backend.model.payment.vnpay.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import hcmute.kltn.Backend.exception.CustomException;
import hcmute.kltn.Backend.model.base.externalAPI.dto.ApiCallResponse;
import hcmute.kltn.Backend.model.base.externalAPI.service.IExternalAPIService;
import hcmute.kltn.Backend.model.generatorSequence.service.IGeneratorSequenceService;
import hcmute.kltn.Backend.model.order.dto.OrderDTO;
import hcmute.kltn.Backend.model.order.service.IOrderService;
import hcmute.kltn.Backend.model.payment.vnpay.dto.VNPayCreate;
import hcmute.kltn.Backend.model.payment.vnpay.dto.VNPayRefund;
import hcmute.kltn.Backend.model.payment.vnpay.dto.VNPaymentDTO;
import hcmute.kltn.Backend.model.payment.vnpay.dto.entity.VNPayment;
import hcmute.kltn.Backend.model.payment.vnpay.repository.VNPaymentRepository;
import hcmute.kltn.Backend.model.payment.vnpay.service.IVNPayService;
import hcmute.kltn.Backend.util.LocalDateTimeUtil;
import hcmute.kltn.Backend.util.StringUtil;

@Service
public class VNPayService implements IVNPayService{
	@Value("${vnpay.PayUrl}")
    private String vnpayPayUrl;
	@Value("${vnpay.TmnCode}")
    private String vnpayTmnCode;
	@Value("${vnpay.secretKey}")
    private String vnpaySecretKey;
	@Value("${vnpay.ApiUrl}")
    private String vnpayAPIUrl;
	@Value("${backend.dev.domain}")
    private String backendDomain;
	
	@Autowired
	private VNPaymentRepository vnPaymentRepository;
	@Autowired
	private IGeneratorSequenceService iGeneratorSequenceService;
	@Autowired
	private IExternalAPIService iExternalAPIService;
	@Autowired
    private MongoTemplate mongoTemplate;
	@Autowired
	private ModelMapper modelMapper;
//	@Autowired
//	private IOrderService iOrderService;
	
	private String md5(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException ex) {
            digest = "";
        } catch (NoSuchAlgorithmException ex) {
            digest = "";
        }
        return digest;
    }

    private String Sha256(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException ex) {
            digest = "";
        } catch (NoSuchAlgorithmException ex) {
            digest = "";
        }
        return digest;
    }

    //Util for VNPAY
    private String hashAllFields(Map fields) {
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return hmacSHA512(vnpaySecretKey,sb.toString());
    }

    private String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    private String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
	private String getCollectionName() {
        String collectionName = mongoTemplate.getCollectionName(VNPayment.class);
        return collectionName;
    }
    
	private VNPayment create(VNPayment vnPayment) {
		// check field condition
//		checkFieldCondition(hotel);
		
		// set default value
		String vnPaymentId = iGeneratorSequenceService.genId(getCollectionName());
//		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		vnPayment.setVnPaymentId(vnPaymentId);
		vnPayment.setStatus(true);
//		vnPayment.setCreatedBy();
		vnPayment.setCreatedAt2(currentDate);
//		vnPayment.setLastModifiedBy();
		vnPayment.setLastModifiedAt2(currentDate);
		
		// create hotel
		VNPayment vnPaymentNew = new VNPayment();
	
		vnPaymentNew = vnPaymentRepository.save(vnPayment);

		return vnPaymentNew;
	}
	
	private VNPayment getDetail(String vnPaymentId) {
		// check exists
		if(!vnPaymentRepository.existsById(vnPaymentId)) {
			throw new CustomException("Cannot find hotel");
		}
		
		// get hotel from database
		VNPayment vnPayment = vnPaymentRepository.findById(vnPaymentId).get();

		return vnPayment;
	}
	
	private VNPaymentDTO getVNPaymentDTO(VNPayment vnPayment) {
		VNPaymentDTO vnPaymentDTONew = new VNPaymentDTO();
		modelMapper.map(vnPayment, vnPaymentDTONew);
		return vnPaymentDTONew;
	}

	@Override
	public String createPayment(VNPayCreate vnPayCreate, String ipAddress) {
		String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = getRandomNumber(8);
        String vnp_TmnCode = vnpayTmnCode;
        String returnUrl = backendDomain + "/api/v1/payments/vnpay/forward";
        String orderInfo = vnPayCreate.getOrderInfo() + "/" + vnPayCreate.getReturnUrl();
        
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(vnPayCreate.getAmount()*100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", vnPayCreate.getOrderType());
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", ipAddress);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(vnpaySecretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnpayPayUrl + "?" + queryUrl;
        return paymentUrl;
	}
	
	@Override
	public int checkPayment(HttpServletRequest request){
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = null;
            String fieldValue = null;
            try {
                fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }

	@Override
	public void refundPayment(VNPayRefund vnPayRefund, String ipAddress) {
		String vnp_RequestId = StringUtil.genRandom(10);
		String vnp_Version = "2.1.0";
        String vnp_Command = "refund";
        String vnp_TmnCode = vnpayTmnCode;
        
        VNPayment vnPayment = getDetail(vnPayRefund.getVnPaymentId());
        System.out.println("vnPayment = " + vnPayment);
        
        LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
        String date = String.valueOf(currentDate);
        String[] dateSplit = date.split("\\.");
        date = dateSplit[0];
        date = date.replaceAll("-", "");
        date = date.replaceAll("T", "");
        date = date.replaceAll(":", "");
        System.out.println("date = " + date);
        
        String data = vnp_RequestId
        		+ "|" + vnp_Version
        		+ "|" + vnp_Command
        		+ "|" + vnp_TmnCode
        		+ "|" + "02"
        		+ "|" + vnPayment.getVnp_TxnRef()
        		+ "|" + String.valueOf(vnPayment.getAmount()*100)
        		+ "|" + vnPayment.getVnp_TransactionNo()
        		+ "|" + vnPayment.getVnp_PayDate()
        		+ "|" + vnPayRefund.getCreateBy()
        		+ "|" + date
        		+ "|" + ipAddress
        		+ "|" + vnPayRefund.getOrderInfo();
        String vnp_SecureHash = hmacSHA512(vnpaySecretKey, data);
        
        HashMap<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_RequestId", vnp_RequestId);
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_TransactionType", "02");
        vnp_Params.put("vnp_TxnRef", vnPayment.getVnp_TxnRef());
        vnp_Params.put("vnp_Amount", String.valueOf(vnPayment.getAmount()*100));
        vnp_Params.put("vnp_OrderInfo", vnPayRefund.getOrderInfo());
        vnp_Params.put("vnp_TransactionNo", vnPayment.getVnp_TransactionNo());
        vnp_Params.put("vnp_TransactionDate", vnPayment.getVnp_PayDate());
        vnp_Params.put("vnp_CreateBy", vnPayRefund.getCreateBy());
        vnp_Params.put("vnp_CreateDate", date);
        vnp_Params.put("vnp_IpAddr", ipAddress);
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);
        
        ApiCallResponse ApiCallResponse = new ApiCallResponse();
        ApiCallResponse = iExternalAPIService.post(vnpayAPIUrl, null, vnp_Params);
        
        if (ApiCallResponse.getStatus() != HttpStatus.OK) {
        	System.out.println("ApiCallResponse = " + ApiCallResponse.getBody());
        	throw new CustomException("There was an error during the refund process");
        }
		
        VNPayment vnPaymentRefund = new VNPayment();
        HashMap<String, String> respone = new HashMap<>();
        respone = (HashMap<String, String>) ApiCallResponse.getBody();
        
        vnPaymentRefund.setVnp_BankCode(respone.get("vnp_BankCode"));
        vnPaymentRefund.setVnp_TransactionNo(respone.get("vnp_TransactionNo")); 
        vnPaymentRefund.setVnp_TmnCode(respone.get("vnp_TmnCode")); 
        vnPaymentRefund.setVnp_TxnRef(respone.get("vnp_TxnRef")); 
        vnPaymentRefund.setVnp_OrderInfo(respone.get("vnp_OrderInfo")); 
        vnPaymentRefund.setVnp_Amount(respone.get("vnp_Amount")); 
        vnPaymentRefund.setVnp_ResponseCode(respone.get("vnp_ResponseCode")); 
        vnPaymentRefund.setVnp_ResponseId(respone.get("vnp_ResponseId")); 
        vnPaymentRefund.setVnp_Command(respone.get("vnp_Command")); 
        vnPaymentRefund.setVnp_PayDate(respone.get("vnp_PayDate")); 
        vnPaymentRefund.setVnp_TransactionType(respone.get("vnp_TransactionType")); 
        vnPaymentRefund.setVnp_SecureHash(respone.get("vnp_SecureHash")); 
        vnPaymentRefund.setVnp_TransactionStatus(respone.get("vnp_TransactionStatus")); 
        vnPaymentRefund.setVnp_Message(respone.get("vnp_Message")); 
		vnPaymentRefund.setName("Hoàn tiền cho khách hàng");
		vnPaymentRefund.setDescription("");
		vnPaymentRefund.setCreatedBy(vnPayRefund.getCreateBy());
		vnPaymentRefund.setLastModifiedBy(vnPayRefund.getCreateBy());
		
		if (!vnPaymentRefund.getVnp_ResponseCode().equals("00")) {
			throw new CustomException(
					"There was an error during the refund process: " 
					+ vnPaymentRefund.getVnp_ResponseCode() + " " 
					+ vnPaymentRefund.getVnp_Message());
		}
		
		VNPayment vnPaymentRefundNew = new VNPayment();
		vnPaymentRefundNew = create(vnPaymentRefund);
	}

	@Override
	public VNPaymentDTO createVNPayment(VNPaymentDTO vnpaymentDTO) {
		// mapping hotel
		VNPayment vnPayment = new VNPayment();
		modelMapper.map(vnpaymentDTO, vnPayment);
		
		// get accountId
//		OrderDTO orderDTO = new OrderDTO();
//		orderDTO = iOrderService.getDetailOrder(vnpaymentDTO.getOrderId());
//		vnPayment.setCreatedBy(orderDTO.getCreatedBy());
//		vnPayment.setLastModifiedBy(orderDTO.getCreatedBy());
		
		// set default value
		vnPayment.setAmount(Integer.valueOf(vnpaymentDTO.getVnp_Amount()) / 100);
		
		// create hotel
		VNPayment vnPaymentNew = new VNPayment();
		vnPaymentNew = create(vnPayment);
		
		return getVNPaymentDTO(vnPaymentNew);
	}

	@Override
	public VNPaymentDTO getDetailHotel(String vnPaymentId) {
		// get hotel 
		VNPayment vnPayment = getDetail(vnPaymentId);

		return getVNPaymentDTO(vnPayment);
	}
}
