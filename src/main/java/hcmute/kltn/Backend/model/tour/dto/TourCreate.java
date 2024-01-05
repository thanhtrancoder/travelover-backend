package hcmute.kltn.Backend.model.tour.dto;

import java.util.List;

import hcmute.kltn.Backend.model.base.extend.Address;
import hcmute.kltn.Backend.model.tour.dto.extend.Discount;
import hcmute.kltn.Backend.model.tour.dto.extend.ReasonableTime;
import hcmute.kltn.Backend.model.tour.dto.extend.Schedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourCreate {
	private String tourTitle; // not null, unique
	private String thumbnailUrl;
	private List<String> image;
	private String videoUrl;
	private int numberOfDay; // not null
	private int numberOfNight; // not null
	private Address address; // not null
	private String tourDescription;
	private int priceOfAdult; // not null
	private int priceOfChildren; // not null
	private List<Schedule> schedule;
	private String tourDetail; // not null
	private ReasonableTime reasonableTime; // not null
	private String suitablePerson;
	private String termAndCondition;
	private Discount discount;
	private int dailyTourLimit;
}
