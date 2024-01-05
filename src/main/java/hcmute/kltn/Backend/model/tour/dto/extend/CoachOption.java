package hcmute.kltn.Backend.model.tour.dto.extend;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoachOption {
	private List<Coach> coachList;
	private int totalPriceNotDiscount;
	private int totalPrice;
}
