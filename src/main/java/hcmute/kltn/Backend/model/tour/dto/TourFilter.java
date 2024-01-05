package hcmute.kltn.Backend.model.tour.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourFilter {
	private String minPrice;
	private String maxPrice;
	private String ratingFilter;
}
