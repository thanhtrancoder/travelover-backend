package hcmute.kltn.Backend.model.z_enterprise.eHotel.dto;

import java.util.List;

import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Room2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomSearchRes {
	private List<Room2> room;
	private int totalPrice;
}
