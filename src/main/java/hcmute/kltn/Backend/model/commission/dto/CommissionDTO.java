package hcmute.kltn.Backend.model.commission.dto;

import java.time.LocalDateTime;

import hcmute.kltn.Backend.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommissionDTO extends BaseEntity{
	private String commissionId;
	private String name; // not null
	private String description;
	private int rate;
	private LocalDateTime startDate; // not null
	private LocalDateTime endDate; // not null
}
