package hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import hcmute.kltn.Backend.model.base.BaseEntity;
import hcmute.kltn.Backend.model.base.extend.Address;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.extend.Coach;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EVehicleDTO extends BaseEntity{
	@JsonProperty("eVehicleId")
	private String eVehicleId;
	@JsonProperty("eVehicleName")
	private String eVehicleName;
	private String description;
	private String phoneNumber;
	private Address address;
	private List<String> route; // province list
	private int numberOfStarRating;
	private List<Coach> coachList;
}
