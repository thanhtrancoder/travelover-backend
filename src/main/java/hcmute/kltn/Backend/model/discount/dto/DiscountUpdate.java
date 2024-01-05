package hcmute.kltn.Backend.model.discount.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountUpdate {
	private String discountId;
	private String discountCode; // not null, unique
	private String discountTitle;
	private String description;
	private String imageUrl;
	private int discountValue;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate startDate;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate endDate;
	private int minOrder;
	private int maxDiscount;
	@JsonProperty("isQuantityLimit")
	private boolean isQuantityLimit;
	private int numberOfCode;
	
	public boolean getIsQuantityLimit() {
		return this.isQuantityLimit;
	}
	public void setIsQuantityLimit(boolean isQuantityLimit) {
		this.isQuantityLimit = isQuantityLimit;
	}
}
