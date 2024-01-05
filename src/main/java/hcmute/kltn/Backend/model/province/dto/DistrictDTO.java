package hcmute.kltn.Backend.model.province.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistrictDTO {
	private String name;
	private String code;
	private String codeName;
	private String divisionType;
	private String shortCodeName;
}
