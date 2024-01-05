package hcmute.kltn.Backend.model.tour.dto.extend;

import java.util.List;

import hcmute.kltn.Backend.model.base.extend.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {
	private String hotelId;
	private String hotelName;
	private String hotelDescription;
	private Address address;
	private List<Room> room;
}
