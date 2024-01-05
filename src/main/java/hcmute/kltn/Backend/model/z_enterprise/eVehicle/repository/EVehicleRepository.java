package hcmute.kltn.Backend.model.z_enterprise.eVehicle.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.entity.EVehicle;

public interface EVehicleRepository extends MongoRepository<EVehicle, String>{

}
