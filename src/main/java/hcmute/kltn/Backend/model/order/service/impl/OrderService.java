package hcmute.kltn.Backend.model.order.service.impl;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import hcmute.kltn.Backend.exception.CustomException;
import hcmute.kltn.Backend.model.account.dto.AccountDTO;
import hcmute.kltn.Backend.model.account.dto.entity.Account;
import hcmute.kltn.Backend.model.account.service.IAccountDetailService;
import hcmute.kltn.Backend.model.account.service.IAccountService;
import hcmute.kltn.Backend.model.base.BaseEntity;
import hcmute.kltn.Backend.model.base.EOrderStatus;
import hcmute.kltn.Backend.model.base.Sort;
import hcmute.kltn.Backend.model.commission.dto.CommissionDTO;
import hcmute.kltn.Backend.model.commission.service.ICommissionService;
import hcmute.kltn.Backend.model.discount.dto.DiscountDTO;
import hcmute.kltn.Backend.model.discount.dto.DiscountUpdate;
import hcmute.kltn.Backend.model.discount.service.IDiscountService;
import hcmute.kltn.Backend.model.email.dto.EmailDTO;
import hcmute.kltn.Backend.model.email.service.IEmailService;
import hcmute.kltn.Backend.model.generatorSequence.service.IGeneratorSequenceService;
import hcmute.kltn.Backend.model.hotel.dto.HotelDTO;
import hcmute.kltn.Backend.model.hotel.dto.extend.Room;
import hcmute.kltn.Backend.model.hotel.service.IHotelService;
import hcmute.kltn.Backend.model.order.dto.Commission;
import hcmute.kltn.Backend.model.order.dto.OrderCreate;
import hcmute.kltn.Backend.model.order.dto.OrderDTO;
import hcmute.kltn.Backend.model.order.dto.OrderPaymentUpdate;
import hcmute.kltn.Backend.model.order.dto.OrderStatusUpdate;
import hcmute.kltn.Backend.model.order.dto.OrderUpdate;
import hcmute.kltn.Backend.model.order.dto.Rating;
import hcmute.kltn.Backend.model.order.dto.entity.Order;
import hcmute.kltn.Backend.model.order.dto.extend.CustomerInformation;
import hcmute.kltn.Backend.model.order.dto.extend.Discount;
import hcmute.kltn.Backend.model.order.dto.extend.HotelDetail;
import hcmute.kltn.Backend.model.order.dto.extend.Member;
import hcmute.kltn.Backend.model.order.dto.extend.OrderDetail;
import hcmute.kltn.Backend.model.order.dto.extend.Payment;
import hcmute.kltn.Backend.model.order.dto.extend.VOTourDetail;
import hcmute.kltn.Backend.model.order.dto.extend.VehicleDetail;
import hcmute.kltn.Backend.model.order.repository.OrderRepository;
import hcmute.kltn.Backend.model.order.service.IOrderService;
import hcmute.kltn.Backend.model.payment.vnpay.dto.VNPayRefund;
import hcmute.kltn.Backend.model.payment.vnpay.service.IVNPayService;
import hcmute.kltn.Backend.model.tour.dto.TourDTO;
import hcmute.kltn.Backend.model.tour.dto.entity.Tour;
import hcmute.kltn.Backend.model.tour.dto.extend.Reviewer;
import hcmute.kltn.Backend.model.tour.service.ITourService;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelDTO;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.entity.EHotel;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Room2;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.service.IEHotelService;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.EVehicleDTO;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.extend.Coach;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.service.IEVehicleService;
import hcmute.kltn.Backend.util.LocalDateTimeUtil;
import hcmute.kltn.Backend.util.LocalDateUtil;
import hcmute.kltn.Backend.util.StringUtil;

@Service
public class OrderService implements IOrderService{
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private IGeneratorSequenceService iGeneratorSequenceService;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private IAccountDetailService iAccountDetailService;
	@Autowired
    private MongoTemplate mongoTemplate;
	@Autowired
	private IHotelService iHotelService;
	@Autowired
	private IDiscountService iDiscountService;
	@Autowired
	private IVNPayService iVNPayService;
	@Autowired
	private IEmailService iEmailService;
	@Autowired
	private IEVehicleService iEVehicleService;
	@Autowired
	private IEHotelService iEHotelService;
	@Autowired
	private ICommissionService iCommissionService;
	@Autowired
	private IAccountService iAccountService;
	@Autowired
	private ITourService iTourService;

	
	private String getOrderStatus(String orderStatus) {
		int index = Integer.valueOf(orderStatus);
		
		List<String> orderStatusList = new ArrayList<>();
		for (EOrderStatus value : EOrderStatus.values()) {
			orderStatusList.add(String.valueOf(value));
		}
		
		if (index < 0 || index >= orderStatusList.size()) {
			return null;
		} 
		
		return orderStatusList.get(index);
	}

    private String getCollectionName() {
        String collectionName = mongoTemplate.getCollectionName(Order.class);
        return collectionName;
    }
    
    private void checkFieldCondition(Order order) {
		// check null
		LocalDate dateNow = LocalDateUtil.getDateNow();
		if(order.getStartDate() == null || order.getStartDate().isEqual(dateNow) || order.getStartDate().isBefore(dateNow)) {
			throw new CustomException("The start date must greater than the current date");
		}
//		if(order.getEndDate() == null || order.getEndDate().isEqual(dateNow) 
//				|| orderDTO.getEndDate().isBefore(dateNow) || orderDTO.getEndDate().isBefore(orderDTO.getStartDate())) {
//			throw new CustomException("The end date must greater or equal than the start date");
//		}
		if(order.getCustomerInformation() == null) {
			throw new CustomException("Customer Information is not null");
		}
		if(order.getNumberOfChildren() < 0) {
			throw new CustomException("Number of children must greater or equal than 0");
		}
		if(order.getNumberOfAdult() <= 0) {
			throw new CustomException("Number of adult must greate than 0");
		}
//		if(order.getPrice() <= 0) {
//			throw new CustomException("Price must greate than 0");
//		}
		if(order.getTotalPrice() <= 0) {
			throw new CustomException("Total price must greate than 0");
		}
		
		// check unique
	}
    
    private Order create(Order order) {
		// check field condition
		checkFieldCondition(order);
		
		// set default value
		String orderId = iGeneratorSequenceService.genId(getCollectionName());
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDate dateNow = LocalDateUtil.getDateNow();
		order.setOrderId(orderId);
		order.setStatus(true);
		order.setCreatedBy(accountId);
		order.setCreatedAt(dateNow);
		order.setLastModifiedBy(accountId);
		order.setLastModifiedAt(dateNow);
		
		// new date
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		order.setCreatedAt2(currentDate);
		order.setLastModifiedAt2(currentDate);
		
		// create order
		Order orderNew = new Order();
		orderNew = orderRepository.save(order);
		
		return orderNew;
	}
    
    private Order update(Order order) {
		// check exists
		if(!orderRepository.existsById(order.getOrderId())) {
			throw new CustomException("Cannot find order");
		}
		
		// check field condition
		checkFieldCondition(order);
		
		// set default value
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDate dateNow = LocalDateUtil.getDateNow();
		order.setLastModifiedBy(accountId);
		order.setLastModifiedAt(dateNow);
		
		// new date
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		order.setLastModifiedAt2(currentDate);
		
		// update order
		Order orderNew = new Order();
		orderNew = orderRepository.save(order);

		return orderNew;
	}

	private Order getDetail(String orderId) {
		// check exists
		if(!orderRepository.existsById(orderId)) {
			throw new CustomException("Cannot find order");
		}
		
		// get order from db
		Order order = orderRepository.findById(orderId).get();
		
		return order;
	}

	private List<Order> getAll() {
		// get all order from db
		List<Order> orderList = orderRepository.findAll();
		
		return orderList;
	}

	private void delete(String orderId) {
		if (orderRepository.existsById(orderId)) {
			orderRepository.deleteById(orderId);
		}
	}

	private List<Order> search(String keyword) {
		// init Order List
		List<Order> orderList = new ArrayList<>();
		
		if(keyword == null || keyword.equals("")) {
			orderList = getAll();
		} else {
			// create list field name
			List<Criteria> criteriaList = new ArrayList<>();
			for(Field itemField : Order.class.getDeclaredFields()) {
				if(itemField.getType() == String.class) {
					criteriaList.add(Criteria.where(itemField.getName()).regex(keyword, "i"));
				}
			}
			criteriaList.add(Criteria.where("_id").is(keyword));
			
			// create criteria
			Criteria criteria = new Criteria();
			criteria.orOperator(criteriaList.toArray(new Criteria[0]));
			
			// create query
			Query query = new Query();
			query.addCriteria(criteria);
			
			// search
			orderList = mongoTemplate.find(query, Order.class);
		}

		return orderList;
	}
	
	private String getAllValue(Order order) {
		System.out.println("order value = " + order.toString());
		String result = new String();
		
		// value of account
		for (Field itemField : Order.class.getDeclaredFields()) {
			itemField.setAccessible(true);
			try {
				// check type
				boolean isList = itemField.getType().isAssignableFrom(List.class);
				boolean isOrderDetail = itemField.getType().isAssignableFrom(OrderDetail.class);
				boolean isCustomerInformation = itemField.getType().isAssignableFrom(CustomerInformation.class);
				boolean isDiscount = itemField.getType().isAssignableFrom(Discount.class);
				Object object = itemField.get(order);
				if (object != null && !isList && !isOrderDetail && !isCustomerInformation && !isDiscount) {
					result += String.valueOf(object) + " ";
				} 
			} catch (Exception e) {
				
			}
		}
		
		// value of base
		for (Field itemField : BaseEntity.class.getDeclaredFields()) {
			itemField.setAccessible(true);
			try {
				// check type
				Object object = itemField.get(order);
				if (object != null) {
					result += String.valueOf(object) + " ";
				} 
			} catch (Exception e) {
				
			}
		}

		return result;
	}
	
	private List<Order> search2(String keyword) {
		// init tour List
		List<Order> orderList = new ArrayList<>();
		orderList = orderRepository.findAll();
		if (keyword != null) {
			keyword = keyword.trim();
		}

		if (keyword != null && !keyword.isEmpty()) {
			if (orderList != null) {
				List<Order> orderListClone = new ArrayList<>();
				orderListClone.addAll(orderList);
				for (Order itemOrder : orderListClone) {
					String keywordNew = StringUtil.getNormalAlphabet(keyword);
					String fieldNew = StringUtil.getNormalAlphabet(getAllValue(itemOrder));
					
					if (!fieldNew.contains(keywordNew)) {
						orderList.remove(itemOrder);
						if (orderList.size() <= 0) {
							break;
						}
					}
				}
			}
		}

		return orderList;
	}
	
	private List<Order> hideOrderUnpaid(List<Order> orderList) {
		List<Order> orderListClone = new ArrayList<>();
		orderListClone.addAll(orderList);
		
		if (orderListClone != null) {
			for (Order itemOrder : orderListClone) {
				if (itemOrder.getOrderStatus() != null) {
					if (itemOrder.getOrderStatus().equals("create")) {
						orderList.remove(itemOrder);
						if (orderList.size() <= 0) {
							break;
						}
					}
				}
			}
		}
		
		return orderList;
	}
	
	private OrderDTO getOrderDTO(Order order) {
		OrderDTO orderDTONew = new OrderDTO();
		modelMapper.map(order, orderDTONew);
		return orderDTONew;
	}
	
	private List<OrderDTO> getOrderDTOList(List<Order> orderList) {
		List<OrderDTO> orderDTOList = new ArrayList<>();
		for (Order itemOrder : orderList) {
			orderDTOList.add(getOrderDTO(itemOrder));
		}
		return orderDTOList;
	}
	
	private void defaultSort(List<Order> orderList) {
		Collections.sort(orderList, new Comparator<Order>() {
            @Override
            public int compare(Order order1, Order order2) {
            	int result = order2.getLastModifiedAt().compareTo(order1.getLastModifiedAt());

                return result;
            }
        });
	}

	@Override
	public OrderDTO createOrder(OrderCreate orderCreate) {
		int totalPrice = 0;
		
		// mapping order
		Order order = new Order();
		modelMapper.map(orderCreate, order);
		
		// init order detail
		OrderDetail orderDetail = new OrderDetail();
		
		// get tour information
		orderDetail.setTourId(orderCreate.getTourId());
		
		TourDTO tourDTO = new TourDTO();
		tourDTO = iTourService.getDetailTour(orderCreate.getTourId());
		VOTourDetail vOTourDetail = new VOTourDetail();
		vOTourDetail.setTourTitle(tourDTO.getTourTitle());
		vOTourDetail.setThumbnailUrl(tourDTO.getThumbnailUrl());
		vOTourDetail.setNumberOfDay(tourDTO.getNumberOfDay());
		vOTourDetail.setNumberOfNight(tourDTO.getNumberOfNight());
		vOTourDetail.setPriceOfAdult(tourDTO.getPriceOfAdult());
		vOTourDetail.setPriceOfChildren(tourDTO.getPriceOfChildren());
		
		orderDetail.setTourDetail(vOTourDetail);
		
		// update total price
		totalPrice += vOTourDetail.getPriceOfAdult() * orderCreate.getNumberOfAdult();
		totalPrice += vOTourDetail.getPriceOfChildren() * orderCreate.getNumberOfChildren();
		
		// update endDate
		LocalDate endDate = order.getStartDate().plusDays((long) (tourDTO.getNumberOfDay() - 1));
		order.setEndDate(endDate);
		
		long numberOfDay = Math.abs(ChronoUnit.DAYS.between(endDate, orderCreate.getStartDate()));
		
		// get commission
		CommissionDTO commissionDTO = new CommissionDTO();
		commissionDTO = iCommissionService.getCurrentCommission();
		Commission commission = new Commission();
		modelMapper.map(commissionDTO, commission);
		
		// get hotel information
		EHotel eHotel = new EHotel();
		eHotel = iEHotelService.getDetailEHotel(orderCreate.getHotelId());
		List<HotelDetail> hotelDetailList = new ArrayList<>();
		for (String itemEHotelId : orderCreate.getRoomIdList()) {
			boolean checkExists = false;
			
			for (Room2 itemRoom : eHotel.getRoom2()) {
				if (itemRoom.getRoomId().equals(itemEHotelId)) {
					checkExists = true;
					
					HotelDetail hotelDetail = new HotelDetail();
					modelMapper.map(itemRoom, hotelDetail);
					hotelDetailList.add(hotelDetail);	
					
					int totalPriceTemp = (int)(itemRoom.getPrice() * ((double)numberOfDay + 0.5));
	
					totalPrice += totalPriceTemp;
				}
			}
			if (checkExists == false) {
				throw new CustomException("Cannot find room in hotel " + itemEHotelId);
			}
		}
		orderDetail.setHotelId(orderCreate.getHotelId());
		orderDetail.setHotelDetail(hotelDetailList);
		
		// get vehicle information
		EVehicleDTO eVehicleDTO = new EVehicleDTO();
		eVehicleDTO = iEVehicleService.getDetailEVehicle(orderCreate.getVehivleId());
		List<VehicleDetail> vehicleDetailList = new ArrayList<>();
		for (String itemCoachId : orderCreate.getCoachIdList()) {
			boolean checkExists = false;
			for (Coach itemCoach : eVehicleDTO.getCoachList()) {
				if (itemCoachId.equals(itemCoach.getCoachId())) {
					checkExists = true;
					
					VehicleDetail vehicleDetail = new VehicleDetail();
					modelMapper.map(itemCoach, vehicleDetail);
					vehicleDetailList.add(vehicleDetail);
					
					totalPrice += itemCoach.getPricePerDay() * tourDTO.getNumberOfDay();
				}
			}
		}

		orderDetail.setVehicleId(orderCreate.getVehivleId());
		orderDetail.setVehicleDetail(vehicleDetailList);		
		
		// get guider information
		
		System.out.println("totalPrice = " + totalPrice);
		
		// get commission detail
		commission.setOriginalPrice(totalPrice);
		commission.setProfit(totalPrice * commissionDTO.getRate() / 100);

		double priceTemp = (double)totalPrice * (100 + commissionDTO.getRate()) / 100;
		totalPrice = (int)priceTemp;
		
		order.setTotalPrice(totalPrice);
		
		// update discount
		Discount discount = new Discount();
		discount.setDiscountTour(null);
		discount.setDiscountTourValue(0);
		discount.setDiscountCode(null);
		discount.setDiscountCodeValue(0);
		// discount tour
		if (tourDTO.getDiscount().getIsDiscount() == true) {
			int actualDiscountValue = totalPrice * tourDTO.getDiscount().getDiscountValue() / 100;
			totalPrice = totalPrice - actualDiscountValue;
			discount.setDiscountTour(tourDTO.getTourId());
			discount.setDiscountTourValue(actualDiscountValue);
		}
		// discount add
		if (orderCreate.getDiscountCode() != null && !orderCreate.getDiscountCode().equals("")) {
			int actualDiscountValue = iDiscountService.getActualDiscountValue(orderCreate.getDiscountCode(), totalPrice);
			
			totalPrice = totalPrice - actualDiscountValue;
			discount.setDiscountCode(orderCreate.getDiscountCode());
			discount.setDiscountCodeValue(actualDiscountValue);
		}
		
		if (orderCreate.getFinalPrice() != totalPrice) {
			int uneven = Math.abs(totalPrice - orderCreate.getFinalPrice());
			if (uneven < 1000) {
				totalPrice = orderCreate.getFinalPrice();
			}
		}

		order.setDiscount(discount);
		order.setOrderDetail(orderDetail);
		order.setFinalPrice(totalPrice);
		order.setCommission(commission);
		order.setOrderStatus("create");
		order.setIsRated(false);
//		order.setOrderStatus(getOrderStatus("1"));
		
		// create order
		Order orderNew = new Order();
		orderNew = create(order);

		return getOrderDTO(orderNew);
	}

	@Override
	public OrderDTO updateOrder(OrderUpdate orderUpdate) {
		// mapping order
		Order order = new Order();
		order = getDetail(orderUpdate.getOrderId());
		modelMapper.map(orderUpdate, order);
		
		// set default value
		String orderStatus = getOrderStatus(orderUpdate.getOrderStatus());
		if (orderStatus == null) {
			throw new CustomException("Order status does not exist");
		}
		order.setOrderStatus(orderStatus);
		
		// update order
		Order orderNew = new Order();
		orderNew = update(order);

		return getOrderDTO(orderNew);
	}

	@Override
	public OrderDTO getDetailOrder(String orderId) {
		Order order = getDetail(orderId);
		
		if (order.getOrderStatus() != null) {
			if (order.getOrderStatus().equals("create")) {
				throw new CustomException("Cannot find order");
			}
		}

		return getOrderDTO(order);
	}

	@Override
	public List<OrderDTO> getAllOrder() {
		List<Order> orderList = new ArrayList<>(getAll());
		defaultSort(orderList);
		
		Account currentAccount = iAccountDetailService.getCurrentAccount();
		if (currentAccount.getRole().equals("CUSTOMER")) {
			List<Order> orderListClone = new ArrayList<>(getAll());
			for (Order itemOrder : orderListClone) {
				if (!itemOrder.getCreatedBy().equals(currentAccount.getAccountId())) {
					orderList.remove(itemOrder);
					if (orderList.size() <= 0) {
						break;
					}
				}
			}
		}

		return getOrderDTOList(hideOrderUnpaid(orderList));
	}

	@Override
	public List<OrderDTO> searchOrder(String keyword) {
		List<Order> orderList = search(keyword);

		return getOrderDTOList(hideOrderUnpaid(orderList));
	}

	@Override
	public OrderDTO updateOrderStatus(OrderStatusUpdate orderStatusUpdate) {

		
		// get order from database
		Order order = new Order();
		order = getDetail(orderStatusUpdate.getOrderId());

		String orderStatus = getOrderStatus(orderStatusUpdate.getStatus());
		if (orderStatus == null) {
			throw new CustomException("Order status does not exist");
		}
		
		// check cancel status
		if (order.getOrderStatus().equals("canceled")) {
			throw new CustomException("Can't update status for canceled orders");
		}
		// check finished status
		if (order.getOrderStatus().equals("finished")) {
			throw new CustomException("Can't update status for finished orders");
		}
		
		Account account = iAccountDetailService.getCurrentAccount();
		if (account.getRole().equals("CUSTOMER") && !orderStatus.equals("canceled")) {
			throw new CustomException("Customers can only cancel");
		}
		
		// check follow status
		int orderStatusOld = 0;
		if (!orderStatus.equals("canceled")) {
			for (EOrderStatus value : EOrderStatus.values()) {
				if (order.getOrderStatus().equals(String.valueOf(value))) {
					break;
				}
				orderStatusOld += 1;
			}
			int orderStatusNew = 0;
			for (EOrderStatus value : EOrderStatus.values()) {
				if (orderStatus.equals(String.valueOf(value))) {
					break;
				}
				orderStatusNew += 1;
			}
			if (orderStatusNew <= orderStatusOld) {
				throw new CustomException("Order status update failed, the new status must be greater than the current status");
			}
		}
		
		// update tour numberOfOrdered
		if (orderStatus.equals("finished")) {
			iTourService.updateNumberOfOrdered(order.getOrderDetail().getTourId());
		}

		order.setOrderStatus(orderStatus);
		
		// set last modify
		LocalDate today = LocalDateUtil.getDateNow();
		
		order.setLastModifiedBy(account.getAccountId());
		order.setLastModifiedAt(today);
		
		// check cancel status
		if (orderStatus.equals("canceled")) {
			// refund
			try {
				VNPayRefund VNPayRefund = new VNPayRefund();
				VNPayRefund.setVnPaymentId(order.getPayment().get(0).getVnPaymentId());
				VNPayRefund.setOrderInfo("Hoàn tiền cho đơn hàng bị hủy: " + orderStatusUpdate.getMessage());
				VNPayRefund.setCreateBy(account.getAccountId());
				iVNPayService.refundPayment(VNPayRefund, "127.0.0.0");
			} catch (Exception e) {
				// TODO: handle exception
			}

			
			// send mail
			AccountDTO accountSendMail = new AccountDTO();
			accountSendMail = iAccountService.getDetailAccount(order.getCreatedBy());
			if (account.getRole().equals("CUSTOMER")) {
				EmailDTO emailDTO = new EmailDTO();
				emailDTO.setTo(accountSendMail.getEmail());
				emailDTO.setSubject("Thông báo về việc hủy đơn hàng");
				emailDTO.setContent("Kính gửi Quý khách,<br>\r\n"
						+ "<br>\r\n"
						+ "Chúng tôi rất tiếc phải thông báo rằng đơn hàng của Quý khách đã bị hủy "
						+ "theo yêu cầu của Quý khách. Chúng tôi rất mong được phục vụ Quý khách trong tương lai.<br>\r\n"
						+ "<br>\r\n"
						+ "Trân trọng,<br>\r\n"
						+ "Đội ngũ Travelover.");
				iEmailService.sendMail(emailDTO);
			} else {
				String discountString = "Để bù đắp, chúng tôi xin gửi tặng Quý khách mã giảm giá: "
						+ orderStatusUpdate.getDiscountCode() + " cho lần mua hàng tiếp theo. ";
				
				EmailDTO emailDTO = new EmailDTO();
				emailDTO.setTo(accountSendMail.getEmail());
				emailDTO.setSubject("Thông báo về việc hủy đơn hàng");
				emailDTO.setContent("Chào Kính gửi Quý khách,<br>\r\n"
						+ "<br>\r\n"
						+ "Chúng tôi rất tiếc phải thông báo rằng đơn hàng tour du lịch của Quý khách "
						+ "(Mã đơn hàng: " + order.getOrderId() + ") đã bị hủy vì lý do: " + orderStatusUpdate.getMessage() + "<br>\r\n"
						+ "<br>\r\n"
						+ "Chúng tôi hiểu rằng việc này có thể gây ra sự bất tiện cho Quý khách và chúng tôi xin lỗi vì điều này. "
						+ discountString
						+ "Nếu Quý khách có bất kỳ câu hỏi hoặc cần hỗ trợ thêm, "
						+ "vui lòng liên hệ với chúng tôi qua email này.<br>\r\n"
						+ "<br>\r\n"
						+ "Chúng tôi rất mong được phục vụ Quý khách trong tương lai gần.<br>\r\n"
						+ "<br>\r\n"
						+ "Trân trọng,<br>\r\n"
						+ "Đội ngũ Travelover.");
				iEmailService.sendMail(emailDTO);
			}
			
			order.setReasonCancel(orderStatusUpdate.getMessage());
		}

		// update order
		Order orderNew = new Order();
		orderNew = orderRepository.save(order);
		
		return getOrderDTO(orderNew);
	}

	@Override
	public OrderDTO updateOrderPayment(OrderPaymentUpdate orderPaymentUpdate) {
		// get order from database
		Order order = new Order();
		order = getDetail(orderPaymentUpdate.getOrderId());
		
		// get payment
		if (order.getPayment() != null) {
			List<Payment> paymentList = new ArrayList<>(order.getPayment());
			
			// update payment
			Payment payment = new Payment();
			payment.setVnPaymentId(orderPaymentUpdate.getVnPaymentId());
			payment.setMethod(orderPaymentUpdate.getMethod());
			payment.setAmount(orderPaymentUpdate.getAmount());
			payment.setCreateAt(LocalDateTimeUtil.getCurentDate());
			paymentList.add(payment);
			order.setPayment(paymentList);
		} else {
			List<Payment> paymentList = new ArrayList<>();
			
			// update payment
			Payment payment = new Payment();
			payment.setVnPaymentId(orderPaymentUpdate.getVnPaymentId());
			payment.setMethod(orderPaymentUpdate.getMethod());
			payment.setAmount(orderPaymentUpdate.getAmount());
			payment.setCreateAt(LocalDateTimeUtil.getCurentDate());
			paymentList.add(payment);
			order.setPayment(paymentList);
		}
		
		if (order.getDiscount().getDiscountCode() != null && !order.getDiscount().getDiscountCode().equals("")) {
			// update number of code used in discount
			DiscountDTO discountDTO = new DiscountDTO();
			discountDTO = iDiscountService.getDiscountByCode(order.getDiscount().getDiscountCode());
			if (discountDTO.getIsQuantityLimit() == true) {
				iDiscountService.usedDiscount(order.getDiscount().getDiscountCode());
			}
		}
		
		// update orderStatus
		order.setOrderStatus(getOrderStatus("1"));
		
		// update order
		Order orderNew = new Order();
		orderNew = orderRepository.save(order);
		
		return getOrderDTO(orderNew);
	}

	@Override
	public boolean paymentCheck(String orderId) {
		Order order = new Order();
		order = getDetail(orderId);
		
		if (order.getPayment() != null) {
			if (order.getPayment().size() > 0) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void deleteUnpaidOrder() {
		// get current date
		LocalDate currentDate = LocalDateUtil.getDateNow();
		
		// get all order
		List<Order> orderList = new ArrayList<>();
		orderList.addAll(getAll());
		
		// delete order unPaid
		if (orderList != null) {
			for (Order itemOrder : orderList) {
				if (itemOrder.getOrderStatus() != null) {
					if (itemOrder.getOrderStatus().equals("create")) {
						if (itemOrder.getCreatedAt().isBefore(currentDate)) {
							orderRepository.delete(itemOrder);
						}
					}
				} else {
					orderRepository.delete(itemOrder);
				}	
			}
		}
		System.out.println("Delete Unpaid Order Successfully");
	}

	@Override
	public List<OrderDTO> listOrderSearch(String keyword) {
		List<Order> orderList = new ArrayList<>();
		orderList.addAll(search2(keyword));
		
		return getOrderDTOList(orderList);
	}

	@Override
	public List<OrderDTO> listOrderFilter(HashMap<String, String> filter, List<OrderDTO> orderDTOList) {
		List<OrderDTO> orderDTOListClone = new ArrayList<>();
		orderDTOListClone.addAll(orderDTOList);
		for (OrderDTO itemOrderDTO : orderDTOListClone) {
			filter.forEach((fieldName, fieldValue) -> {
				// filter of account class
				for (Field itemField : HotelDTO.class.getDeclaredFields()) {
					itemField.setAccessible(true);
					// field of account
					if (itemField.getName().equals(fieldName)) {
						try {
							String value = String.valueOf(itemField.get(itemOrderDTO));
							String valueNew = StringUtil.getNormalAlphabet(value);
							String fieldValueNew = StringUtil.getNormalAlphabet(fieldValue);
							if (!valueNew.contains(fieldValueNew)) {
								orderDTOList.remove(itemOrderDTO);
								break;
							}
						} catch (Exception e) {
							
						}
					}
				}
				
				// filter of base class
				for (Field itemField : BaseEntity.class.getDeclaredFields()) {
					itemField.setAccessible(true);
					// field of base
					if (itemField.getName().equals(fieldName)) {
						try {
							String value = String.valueOf(itemField.get(itemOrderDTO));
							String valueNew = StringUtil.getNormalAlphabet(value);
							String fieldValueNew = StringUtil.getNormalAlphabet(fieldValue);
							if (!valueNew.contains(fieldValueNew)) {
								orderDTOList.remove(itemOrderDTO);
								break;
							}
						} catch (Exception e) {
							
						}
					}
				}
				
			});
			if (orderDTOList.size() <= 0) {
				break;
			}
		}
		
		return orderDTOList;
	}

	@Override
	public List<OrderDTO> listOrderSort(Sort sort, List<OrderDTO> orderDTOList) {
		Collections.sort(orderDTOList, new Comparator<OrderDTO>() {
            @Override
            public int compare(OrderDTO orderDTO1, OrderDTO orderDTO2) {
            	int result = 0;
            	
            	// sort of account class
        		for (Field itemField : HotelDTO.class.getDeclaredFields()) {
        			itemField.setAccessible(true);
        			// field of account
    				if (sort.getSortBy().equals(itemField.getName())) {
    					try {
		            		String string1 = String.valueOf(itemField.get(orderDTO1));
		            		String string2 = String.valueOf(itemField.get(orderDTO2));
		            		result = string1.compareTo(string2);
		            	} catch (Exception e) {
		            		result = 0;
						}
    					
    					break;
    				}
    			}
        		
        		// sort of base class
        		for (Field itemField : BaseEntity.class.getDeclaredFields()) {
        			itemField.setAccessible(true);
        			// field of base
    				if (sort.getSortBy().equals(itemField.getName())) {
    					try {
		            		String string1 = String.valueOf(itemField.get(orderDTO1));
		            		String string2 = String.valueOf(itemField.get(orderDTO2));
		            		result = string1.compareTo(string2);
		            	} catch (Exception e) {
		            		result = 0;
						}
    					
    					break;
    				}
    			}
        		
        		if (sort.getOrder().equals("asc")) {

            	} else if (sort.getOrder().equals("desc")) {
            		result = -result;
            	} else {
            		result = 0;
            	}
            	
            	return result;
            }
        });
		
		return orderDTOList;
	}

	@Override
	public OrderDTO getDetailOrderNotCheckCreate(String orderId) {
		Order order = getDetail(orderId);

		return getOrderDTO(order);
	}

	@Override
	public void rating(Rating rating) {
		// get order
		Order order = new Order();
		order = getDetail(rating.getOrderId());
		
		// check fail
		if (!order.getOrderStatus().equals("finished") || order.getStatus() != true) {
			throw new CustomException("Incomplete orders can't be rated");
		}
		
		if (order.getIsRated() == true) {
			throw new CustomException("Order can only be rated 1 time");
		}
		
		Account account = new Account();
		account = iAccountDetailService.getCurrentAccount();
		
		if (!account.getAccountId().equals(order.getCreatedBy())) {
			throw new CustomException("Only rated orders placed by the current account");
		}
		
		if (rating.getRate() < 1 || rating.getRate() > 5) {
			throw new CustomException("The rate must be between 1 and 5");
		}
		
		// update isRated
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		
		// update review tour
		Reviewer reviewer = new Reviewer();
		reviewer.setAccountId(account.getAccountId());
		reviewer.setFirstName(account.getFirstName());
		reviewer.setLastName(account.getLastName());
		reviewer.setAvatar(account.getAvatar());
		reviewer.setRate(rating.getRate());
		reviewer.setComment(rating.getReview());
		reviewer.setCreateAt(currentDate);
		reviewer.setLastModifiedAt(currentDate);;
		
		iTourService.updateReviewer(order.getOrderDetail().getTourId(), reviewer);
		
		order.setIsRated(true);
		order.setLastModifiedBy(account.getAccountId());
		order.setLastModifiedAt2(currentDate);
		
		orderRepository.save(order);
		
	}

	@Override
	public List<OrderDTO> getAllOrder2() {
		List<Order> orderList = new ArrayList<>(getAll());
		defaultSort(orderList);

		return getOrderDTOList(hideOrderUnpaid(orderList));
	}
}
