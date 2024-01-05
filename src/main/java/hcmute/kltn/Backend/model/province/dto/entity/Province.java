package hcmute.kltn.Backend.model.province.dto.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import hcmute.kltn.Backend.model.province.dto.extend.District;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "province")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Province {
	@Id
	private String provinceId;
	private String name;
	private String code;
	private String codeName;
	private String divisionType;
	private String phoneCode;
	private List<District> district;
	private int numberOfVisitor;
	private LocalDateTime updatedAt;
}
