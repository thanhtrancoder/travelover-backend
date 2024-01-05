package hcmute.kltn.Backend.model.tour.dto.extend;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reviewer {
	private String accountId;
	private String firstName;
	private String lastName;
	private String avatar;
	private int rate;
	private String comment;
	private LocalDateTime createAt;
	private LocalDateTime lastModifiedAt;
}
