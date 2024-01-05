package hcmute.kltn.Backend.model.order.dto.extend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VOTourDetail {
	private String tourTitle;
	private String thumbnailUrl;
	private int numberOfDay; 
	private int numberOfNight; 
	private int priceOfAdult; 
	private int priceOfChildren; 
}
