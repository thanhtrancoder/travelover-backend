package hcmute.kltn.Backend.model.z_enterprise.eHotel.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EHotelOrderUpdate {
	@JsonProperty("eHotelId")
	private String eHotelId;
	private String orderId;
	private String companyId;
	private List<OrderDetail> orderDetail;
	private String paymentStatus;
	private String orderStatus;
}
