package hcmute.kltn.Backend.model.payment.vnpay.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import hcmute.kltn.Backend.model.payment.vnpay.dto.entity.VNPayment;

public interface VNPaymentRepository extends MongoRepository<VNPayment, String>{

}
