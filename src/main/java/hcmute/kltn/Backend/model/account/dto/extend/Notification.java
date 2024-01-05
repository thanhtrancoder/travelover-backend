package hcmute.kltn.Backend.model.account.dto.extend;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
	private String title;
	private String description;
	private String link;
	private String imageUrl;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate createAt;
	private boolean status;
}
