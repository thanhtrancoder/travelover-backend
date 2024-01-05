package hcmute.kltn.Backend.model.z_enterprise.eHotel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EHotelOrderStatusUpdate {
	@JsonProperty("eHotelId")
	private String eHotelId;
	private String orderId;
	private String orderStatus;
}
