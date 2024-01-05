package hcmute.kltn.Backend.model.order.dto.extend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Discount {
	private String discountCode;
	private int discountCodeValue;
	private String discountTour;
	private int discountTourValue;
}
