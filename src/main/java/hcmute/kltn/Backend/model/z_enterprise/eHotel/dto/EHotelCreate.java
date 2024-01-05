package hcmute.kltn.Backend.model.z_enterprise.eHotel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import hcmute.kltn.Backend.model.base.extend.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EHotelCreate {
	@JsonProperty("eHotelName")
	private String eHotelName; // not null
	private String description;
	private String phoneNumber;
	private Address address;
	private int numberOfStarRating;
}
