package hcmute.kltn.Backend.model.hotel.dto.extend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomInfro {
	private String roomId_Name;
	private String name_Name;
	private String bed_Name;
	private String numberOfAdult;
	private String numberOfChildren;
	private String price_Name;
}
