package hcmute.kltn.Backend.model.tour.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import hcmute.kltn.Backend.model.tour.dto.entity.Tour;

public interface TourRepository extends MongoRepository<Tour, String>{
	boolean existsByTourTitle(String tourTitle);
	List<Tour> findAllByTourTitle(String tourTitle);
	List<Tour> findAllByStatus(boolean status);
	Tour findFirstBy();
}