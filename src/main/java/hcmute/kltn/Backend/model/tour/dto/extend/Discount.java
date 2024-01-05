package hcmute.kltn.Backend.model.tour.dto.extend;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Discount {
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate startDate;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate endDate;
	private String discountType;
	private int discountValue;
	private boolean auto;
	@JsonProperty("isDiscount")
	private boolean isDiscount;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate updateIsDiscount;
	
	public void setIsDiscount(boolean isDiscount) {
		this.isDiscount = isDiscount;
	}
	public boolean getIsDiscount() {
		return this.isDiscount;
	}
	public boolean getAuto() {
		return this.auto;
	}
}
