package hcmute.kltn.Backend.model.commission.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import hcmute.kltn.Backend.model.commission.dto.entity.Commission;

public interface CommissionRepository extends MongoRepository<Commission, String>{
	boolean existsByName(String name);
	List<Commission> findAllByName(String name);
}
