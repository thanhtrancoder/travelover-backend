package hcmute.kltn.Backend.model.z_enterprise.eVehicle.service;

import java.util.List;

import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.CoachSearch;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.CoachSearchRes;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.EVehicleDTO;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.EVehicleDTOSimple;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.Location;

public interface IEVehicleService {
	public EVehicleDTO createEVehicle(EVehicleDTO eVehicleDTO);
	public EVehicleDTO updateEVehicle(EVehicleDTO eVehicleDTO);
	public EVehicleDTO getDetailEVehicle(String eVehicleId);

	public List<EVehicleDTO> getAllEVehicle();
	public List<EVehicleDTO> searchEVehicle(String keyword);
	
	public List<EVehicleDTOSimple> searchEVehicleByLocation(Location location);
	
	public List<CoachSearchRes> searchCoach(CoachSearch coachSearch);
}
