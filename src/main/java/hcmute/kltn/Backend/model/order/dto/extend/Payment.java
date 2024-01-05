package hcmute.kltn.Backend.model.order.dto.extend;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
	private String vnPaymentId;
	private String method;
	private int amount;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate date;
	private LocalDateTime createAt;

}