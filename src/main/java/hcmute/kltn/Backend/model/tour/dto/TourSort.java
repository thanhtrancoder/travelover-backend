package hcmute.kltn.Backend.model.tour.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourSort {
	private String sortBy;
	private String order;
}
