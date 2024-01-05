package hcmute.kltn.Backend.model.dev.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hcmute.kltn.Backend.model.account.dto.entity.Account;
import hcmute.kltn.Backend.model.account.repository.AccountRepository;
import hcmute.kltn.Backend.model.dev.service.IDevService;
import hcmute.kltn.Backend.model.discount.dto.entity.Discount;
import hcmute.kltn.Backend.model.discount.repository.DiscountRepository;
import hcmute.kltn.Backend.model.hotel.dto.entity.Hotel;
import hcmute.kltn.Backend.model.hotel.repository.HotelRepository;
import hcmute.kltn.Backend.model.order.dto.entity.Order;
import hcmute.kltn.Backend.model.order.dto.extend.Payment;
import hcmute.kltn.Backend.model.order.repository.OrderRepository;
import hcmute.kltn.Backend.model.tour.dto.entity.Tour;
import hcmute.kltn.Backend.model.tour.repository.TourRepository;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.entity.EHotel;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.repository.EHotelRepository;

@Service
public class DevService implements IDevService{
	@Autowired
    private ModelMapper modelMapper;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private DiscountRepository discountRepository;
	@Autowired
	private HotelRepository hotelRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private TourRepository tourRepository;
	@Autowired
	private EHotelRepository eHotelRepository;

	@Override
	public void updateNewDateTime() {
		// for account
		List<Account> accountList = new ArrayList<>();
		accountList.addAll(accountRepository.findAll());
		for (Account itemAccount : accountList) {
			Account account = new Account();
			modelMapper.map(itemAccount, account);
			if (itemAccount.getCreatedAt2() == null) {
				account.setCreatedAt2(itemAccount.getCreatedAt().atStartOfDay());
			}
			if (itemAccount.getLastModifiedAt2() == null) {
				account.setLastModifiedAt2(itemAccount.getLastModifiedAt().atStartOfDay());
			}
			accountRepository.save(account);
		}
		System.out.println("Update new date time for account successfully");
		
		// for discount
		List<Discount> discountList = new ArrayList<>();
		discountList.addAll(discountRepository.findAll());
		for (Discount itemDiscount : discountList) {
			Discount discount = new Discount();
			modelMapper.map(itemDiscount, discount);
			if (itemDiscount.getCreatedAt2() == null) {
				discount.setCreatedAt2(itemDiscount.getCreatedAt().atStartOfDay());
			}
			if (itemDiscount.getLastModifiedAt2() == null) {
				discount.setLastModifiedAt2(itemDiscount.getLastModifiedAt().atStartOfDay());
			}
			discountRepository.save(discount);
		}
		System.out.println("Update new date time for discount successfully");
		
		// for hotel
		List<Hotel> hotelList = new ArrayList<>();
		hotelList.addAll(hotelRepository.findAll());
		for (Hotel itemHotel : hotelList) {
			Hotel hotel = new Hotel();
			modelMapper.map(itemHotel, hotel);
			if (itemHotel.getCreatedAt2() == null) {
				hotel.setCreatedAt2(itemHotel.getCreatedAt().atStartOfDay());
			}
			if (itemHotel.getLastModifiedAt2() == null) {
				hotel.setLastModifiedAt2(itemHotel.getLastModifiedAt().atStartOfDay());
			}
			hotelRepository.save(hotel);
		}
		System.out.println("Update new date time for hotel successfully");
		
		// for order
		List<Order> orderList = new ArrayList<>();
		orderList.addAll(orderRepository.findAll());
		for (Order itemOrder : orderList) {
			Order order = new Order();
			modelMapper.map(itemOrder, order);
			if (itemOrder.getCreatedAt2() == null) {
				order.setCreatedAt2(itemOrder.getCreatedAt().atStartOfDay());
			}
			if (itemOrder.getLastModifiedAt2() == null) {
				order.setLastModifiedAt2(itemOrder.getLastModifiedAt().atStartOfDay());
			}
			
			// payment createAt
			if (itemOrder.getPayment() != null) {
				List<Payment> paymentList = new ArrayList<>();
				for (Payment itemPayment : itemOrder.getPayment()) {
					Payment payment = new Payment();
					modelMapper.map(itemPayment, payment);
					if (itemPayment.getCreateAt() == null) {
						payment.setCreateAt(itemPayment.getDate().atStartOfDay());
					}
					paymentList.add(payment);
				}
				order.setPayment(paymentList);
			}
						
			orderRepository.save(order);
		}
		System.out.println("Update new date time for order successfully");
		
		// for tour
		List<Tour> tourList = new ArrayList<>();
		tourList.addAll(tourRepository.findAll());
		for (Tour itemTour : tourList) {
			Tour tour = new Tour();
			modelMapper.map(itemTour, tour);
			if (itemTour.getCreatedAt2() == null) {
				tour.setCreatedAt2(itemTour.getCreatedAt().atStartOfDay());
			}
			if (itemTour.getLastModifiedAt2() == null) {
				tour.setLastModifiedAt2(itemTour.getLastModifiedAt().atStartOfDay());
			}
			tourRepository.save(tour);
		}
		System.out.println("Update new date time for tour successfully");
		
		// for eHotel
		List<EHotel> eHotelList = new ArrayList<>();
		eHotelList.addAll(eHotelRepository.findAll());
		for (EHotel itemEHotel : eHotelList) {
			EHotel eHotel = new EHotel();
			modelMapper.map(itemEHotel, eHotel);
			if (itemEHotel.getCreatedAt2() == null) {
				eHotel.setCreatedAt2(itemEHotel.getCreatedAt().atStartOfDay());
			}
			if (itemEHotel.getLastModifiedAt2() == null) {
				eHotel.setLastModifiedAt2(itemEHotel.getLastModifiedAt().atStartOfDay());
			}
			eHotelRepository.save(eHotel);
		}
		System.out.println("Update new date time for eHotel successfully");
	}

}
