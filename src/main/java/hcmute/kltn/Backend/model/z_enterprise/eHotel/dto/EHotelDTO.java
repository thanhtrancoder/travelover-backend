package hcmute.kltn.Backend.model.z_enterprise.eHotel.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import hcmute.kltn.Backend.model.base.BaseEntity;
import hcmute.kltn.Backend.model.base.extend.Address;
import hcmute.kltn.Backend.model.base.extend.Image;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Order;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Room;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Room2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EHotelDTO extends BaseEntity{
	@JsonProperty("eHotelId")
	private String eHotelId;
	@JsonProperty("eHotelName")
	private String eHotelName; // not null
	private String description;
	private String phoneNumber;
	private Address address;
	private int numberOfStarRating;
	private List<Image> image;
	private List<Room> room; // not null
	private List<Room2> room2; // not null
	private List<Order> order;
}
