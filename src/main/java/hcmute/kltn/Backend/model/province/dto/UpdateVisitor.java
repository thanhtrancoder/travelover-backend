package hcmute.kltn.Backend.model.province.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVisitor {
	private String provinceCode;
	private String districtCode;
	private String wardCode;
	private int numberOfVisitor;
}
