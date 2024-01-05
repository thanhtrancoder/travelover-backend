package hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomCreate {
	private int capacity;
	private int price;
	private boolean status;
}