package hcmute.kltn.Backend.model.hotel.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import hcmute.kltn.Backend.model.hotel.dto.entity.Hotel;

public interface HotelRepository extends MongoRepository<Hotel, String>{

}
