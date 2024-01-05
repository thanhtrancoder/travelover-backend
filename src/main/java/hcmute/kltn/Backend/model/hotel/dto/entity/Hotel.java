package hcmute.kltn.Backend.model.hotel.dto.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import hcmute.kltn.Backend.model.base.BaseEntity;
import hcmute.kltn.Backend.model.base.extend.Address;
import hcmute.kltn.Backend.model.base.extend.Contact;
import hcmute.kltn.Backend.model.base.extend.Image;
import hcmute.kltn.Backend.model.hotel.dto.extend.SearchAPI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Document(collection = "hotel")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Hotel extends BaseEntity {
	@Id
	private String hotelId;
	@JsonProperty("eHotelId")
	private String eHotelId;
	private String hotelName; // not null
	private String hotelDescription;
	private Contact contact; // not null
	private List<Image> image;
	private Address address; // not null
	private SearchAPI searchAPI;
}
