package hcmute.kltn.Backend.model.tour.dto.extend;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Option {
	private List<Room2> roomList;
	private int totalPriceNotDiscount;
	private int totalPrice;
}
