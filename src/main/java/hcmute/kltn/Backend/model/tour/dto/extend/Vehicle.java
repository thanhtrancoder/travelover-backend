package hcmute.kltn.Backend.model.tour.dto.extend;

import java.util.List;

import hcmute.kltn.Backend.model.base.extend.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
	private String eVehicleId;
	private String eVehicleName;
	private String description;
	private String phoneNumber;
	private Address address;
	private int numberOfStarRating;
	private List<CoachOption> optionList;
}
