package hcmute.kltn.Backend.model.tour.dto;

import java.util.List;

import hcmute.kltn.Backend.model.tour.dto.entity.Tour;
import hcmute.kltn.Backend.model.tour.dto.extend.Hotel2;
import hcmute.kltn.Backend.model.tour.dto.extend.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourSearchRes2 {
	private Tour tour;
	private int tourPriceNotDiscount;
	private int tourPrice;
	private List<Hotel2> hotelList;
	private List<Vehicle> vehicleList;
}
