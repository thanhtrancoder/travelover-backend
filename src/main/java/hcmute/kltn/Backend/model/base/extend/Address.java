package hcmute.kltn.Backend.model.base.extend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
	private String province;
	private String district;
	private String commune;
	private String moreLocation;
}
