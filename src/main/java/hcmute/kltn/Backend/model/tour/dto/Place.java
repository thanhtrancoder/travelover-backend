package hcmute.kltn.Backend.model.tour.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Place {
	private String province;
	private String district;
	private int visitor;
	private String imageUrl;
}
