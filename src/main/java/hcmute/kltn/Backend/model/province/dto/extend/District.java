package hcmute.kltn.Backend.model.province.dto.extend;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class District {
	private String name;
	private String code;
	private String codeName;
	private String divisionType;
	private String shortCodeName;
	private List<Ward> ward;
	private int numberOfVisitor;
	private LocalDateTime updatedAt;
}
