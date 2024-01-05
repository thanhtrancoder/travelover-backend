package hcmute.kltn.Backend.model.z_enterprise.eHotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
	private String province;
	private String district;
	private String commune;
}
