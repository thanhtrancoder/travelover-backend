package hcmute.kltn.Backend.model.discount.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import hcmute.kltn.Backend.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountDTO extends BaseEntity{
	private String discountId;
	private String discountTitle; // not null
	private String description;
	private String imageUrl;
	private String discountCode; // not null, unique
	private String discountType;
	private int discountValue; // not null
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate startDate; // not null
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate endDate; // not null
	private int minOrder; // not null
	private int maxDiscount; // not null
	@JsonProperty("isQuantityLimit")
	private boolean isQuantityLimit; // not null
	private int numberOfCode;
	private int numberOfCodeUsed;
	
	public boolean getIsQuantityLimit() {
		return this.isQuantityLimit;
	}
	public void setIsQuantityLimit(boolean isQuantityLimit) {
		this.isQuantityLimit = isQuantityLimit;
	}
}
