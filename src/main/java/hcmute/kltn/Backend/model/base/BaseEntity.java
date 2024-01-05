package hcmute.kltn.Backend.model.base;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity {
	private boolean status; // not null
	private String createdBy; // not null
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate createdAt; // not null
	private LocalDateTime createdAt2;
	private String lastModifiedBy; // not null
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate lastModifiedAt; // not null
	private LocalDateTime lastModifiedAt2;
	
	public boolean getStatus() {
		return this.status;
	}
}
