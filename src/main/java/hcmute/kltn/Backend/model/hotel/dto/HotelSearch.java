package hcmute.kltn.Backend.model.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelSearch {
	private String keyword;
	private String province;
	private String district;
	private String commune;
}
