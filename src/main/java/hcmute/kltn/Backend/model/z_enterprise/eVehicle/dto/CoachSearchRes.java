package hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto;

import java.util.List;

import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.extend.Coach;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoachSearchRes {
	private List<Coach> coachList;
	private int totalPrice;
}
