package hcmute.kltn.Backend.model.order.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import hcmute.kltn.Backend.model.order.dto.entity.Order;

public interface OrderRepository extends MongoRepository<Order, String>{

}
