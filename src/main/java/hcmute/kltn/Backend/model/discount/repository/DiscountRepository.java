package hcmute.kltn.Backend.model.discount.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import hcmute.kltn.Backend.model.discount.dto.entity.Discount;

public interface DiscountRepository extends MongoRepository<Discount, String>{
	boolean existsByDiscountCode(String discountCode);
	List<Discount> findAllByDiscountCode(String discountCode);
	Optional<Discount> findByDiscountCode(String discountCode);
}
