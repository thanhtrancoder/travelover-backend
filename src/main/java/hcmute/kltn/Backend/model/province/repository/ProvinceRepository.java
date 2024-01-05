package hcmute.kltn.Backend.model.province.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import hcmute.kltn.Backend.model.province.dto.entity.Province;

public interface ProvinceRepository extends MongoRepository<Province, String>{
	Optional<Province> findByCode(String code);
}
