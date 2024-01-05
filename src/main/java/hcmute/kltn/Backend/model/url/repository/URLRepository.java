package hcmute.kltn.Backend.model.url.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import hcmute.kltn.Backend.model.url.dto.entity.URL;

public interface URLRepository extends MongoRepository<URL, String>{
	boolean existsByName(String name);
	
	List<URL> findAllByName(String name);
}
