package hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import hcmute.kltn.Backend.model.base.BaseEntity;
import hcmute.kltn.Backend.model.base.extend.Address;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.extend.Coach;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Document(collection = "z_enterprise_vehicle")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class EVehicle extends BaseEntity{
	@Id
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
