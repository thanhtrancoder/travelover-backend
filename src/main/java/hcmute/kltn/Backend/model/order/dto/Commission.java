package hcmute.kltn.Backend.model.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Commission {
	private String commissionId;
	private String name; // not null
	private int rate;
	private int originalPrice;
	private int profit;
}
