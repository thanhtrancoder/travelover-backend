package hcmute.kltn.Backend.model.tour.dto;

import java.util.List;

import hcmute.kltn.Backend.model.base.BaseEntity;
import hcmute.kltn.Backend.model.base.extend.Address;
import hcmute.kltn.Backend.model.tour.dto.extend.Discount;
import hcmute.kltn.Backend.model.tour.dto.extend.ReasonableTime;
import hcmute.kltn.Backend.model.tour.dto.extend.Reviewer;
import hcmute.kltn.Backend.model.tour.dto.extend.Schedule;
import hcmute.kltn.Backend.model.tour.dto.extend.TourDetail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourDTO extends BaseEntity{
	private String tourId;
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
	private String tourDetail;
	private List<TourDetail> tourDetailList; // not null
	private ReasonableTime reasonableTime; // not null
	private String suitablePerson;
	private String termAndCondition;
	private int numberOfOrdered;
	private int numberOfReviewer;
	private double rate;
	private List<Reviewer> reviewer;
	private Discount discount;
	private int dailyTourLimit;
}
