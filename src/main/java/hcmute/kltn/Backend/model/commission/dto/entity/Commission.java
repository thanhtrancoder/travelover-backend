package hcmute.kltn.Backend.model.commission.dto.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import hcmute.kltn.Backend.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Document(collection = "commission")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Commission extends BaseEntity{
	@Id
	private String commissionId;
	private String name; // not null
	private String description;
	private int rate;
	private LocalDateTime startDate; // not null
	private LocalDateTime endDate; // not null
}
