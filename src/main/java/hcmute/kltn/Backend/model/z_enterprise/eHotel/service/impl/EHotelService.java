package hcmute.kltn.Backend.model.z_enterprise.eHotel.service.impl;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import hcmute.kltn.Backend.exception.CustomException;
import hcmute.kltn.Backend.model.account.service.IAccountDetailService;
import hcmute.kltn.Backend.model.base.BaseEntity;
import hcmute.kltn.Backend.model.base.Sort;
import hcmute.kltn.Backend.model.base.extend.Address;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelCreate;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelDTO;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelDTOSimple;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelOrderCreate;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelOrderStatusUpdate;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelOrderUpdate;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelUpdate;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.Location;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.RoomSearch;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.RoomSearchRes;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.entity.EHotel;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Order;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.OrderDetail;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Room;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Room2;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.repository.EHotelRepository;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.service.IEHotelService;
import hcmute.kltn.Backend.util.IntegerUtil;
import hcmute.kltn.Backend.util.LocalDateTimeUtil;
import hcmute.kltn.Backend.util.LocalDateUtil;
import hcmute.kltn.Backend.util.StringUtil;

@Service
public class EHotelService implements IEHotelService{
	@Autowired
	private EHotelRepository eHotelRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private IAccountDetailService iAccountDetailService;
	@Autowired
    private MongoTemplate mongoTemplate;

    private String getCollectionName() {
        String collectionName = mongoTemplate.getCollectionName(EHotel.class);
        return collectionName;
    }
    
    private void checkFieldCondition(EHotelDTO eHotelDTO) {
		// check null
		if(eHotelDTO.getEHotelName() == null || eHotelDTO.getEHotelName().equals("")) {
			throw new CustomException("Enterprise Hotel Name is not null");
		}
		if(eHotelDTO.getAddress() == null) {
			throw new CustomException("Address is not null");
		}
		if(eHotelDTO.getPhoneNumber() == null || eHotelDTO.getPhoneNumber().equals("")) {
			throw new CustomException("Phone number is not null");
		}
		if(eHotelDTO.getNumberOfStarRating() < 1 || eHotelDTO.getNumberOfStarRating() > 5) {
			throw new CustomException("Number Of Star Rating must be between 1 and 5");
		}
		
		// check unique
	}

	private EHotel create(EHotelDTO eHotelDTO) {
		// check field condition
		checkFieldCondition(eHotelDTO);
		
		// mapping
		EHotel eHotel = new EHotel();
		modelMapper.map(eHotelDTO, eHotel);
		
		// set roomId
//		for(Room itemRoom : eHotel.getRoom()) {
//			itemRoom.setRoomId(String.valueOf(eHotel.getRoom().indexOf(itemRoom) + 1));
//		}
		
		// set default value
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDate dateNow = LocalDateUtil.getDateNow();
		eHotel.setStatus(true);
		eHotel.setCreatedBy(accountId);
		eHotel.setCreatedAt(dateNow);
		eHotel.setLastModifiedBy(accountId);
		eHotel.setLastModifiedAt(dateNow);
		
		// new date
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		eHotel.setCreatedAt2(currentDate);
		eHotel.setLastModifiedAt2(currentDate);
		
		// create hotel
		eHotel = eHotelRepository.save(eHotel);
		
		return eHotel;
	}

	private EHotel update(EHotelDTO eHotelDTO) {
		// check exists
		if(!eHotelRepository.existsById(eHotelDTO.getEHotelId())) {
			throw new CustomException("Cannot find enterprise hotel");
		}
		
		// check field condition
		checkFieldCondition(eHotelDTO);
		
		// get hotel from db
		EHotel eHotel = eHotelRepository.findById(eHotelDTO.getEHotelId()).get();
		
		// mapping
		modelMapper.map(eHotelDTO, eHotel);
		
		// set default value
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDate dateNow = LocalDateUtil.getDateNow();
		eHotel.setLastModifiedBy(accountId);
		eHotel.setLastModifiedAt(dateNow);
		
		// new date
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		eHotel.setLastModifiedAt2(currentDate);
		
		// update hotel
		eHotel = eHotelRepository.save(eHotel);

		return eHotel;
	}

	private EHotel getDetail(String eHotelId) {
		// check exists
		if(!eHotelRepository.existsById(eHotelId)) {
			throw new CustomException("Cannot find enterprise hotel");
		}
		
		// get hotel from db
		EHotel eHotel = eHotelRepository.findById(eHotelId).get();
		
		return eHotel;
	}

	private List<EHotel> getAll() {
		// get all hotel from db
		List<EHotel> eHotelList = eHotelRepository.findAll();

		return eHotelList;
	}

	private boolean delete(String eHotelId) {
		// check exists
		if(!eHotelRepository.existsById(eHotelId)) {
			throw new CustomException("Cannot find enterprise hotel");
		}
		
		// delete hotel
		eHotelRepository.deleteById(eHotelId);
		
		return true;
	}
	
	private List<EHotel> search(String keyword) {
		// init EHotel List
		List<EHotel> eHotelList = new ArrayList<>();
		
		if(keyword == null || keyword.equals("")) {
			eHotelList = getAll();
		} else {
			// create list field name
			List<Criteria> criteriaList = new ArrayList<>();
			for(Field itemField : EHotel.class.getDeclaredFields()) {
				 if (itemField.getType() == String.class) {
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
	        eHotelList = mongoTemplate.find(query, EHotel.class);
		}

		return eHotelList;
	}
	
	private String getAllValue(EHotel eHotel) {
		String result = new String();
		
		// value of account
		for (Field itemField : EHotel.class.getDeclaredFields()) {
			itemField.setAccessible(true);
			try {
				// check type
				boolean isList = itemField.getType().isAssignableFrom(List.class);
				boolean isAddress = itemField.getType().isAssignableFrom(Address.class);
				Object object = itemField.get(eHotel);
				if (object != null && !isList && !isAddress) {
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
				Object object = itemField.get(eHotel);
				if (object != null) {
					result += String.valueOf(object) + " ";
				} 
			} catch (Exception e) {
				
			}
		}

		return result;
	}
	
	private List<EHotel> search2(String keyword) {
		// init tour List
		List<EHotel> eHotelList = new ArrayList<>();
		eHotelList = eHotelRepository.findAll();
		if (keyword != null) {
			keyword = keyword.trim();
		}

		if (keyword != null && !keyword.isEmpty()) {
			if (eHotelList != null) {
				List<EHotel> eHotelListClone = new ArrayList<>();
				eHotelListClone.addAll(eHotelList);
				for (EHotel itemEHotel : eHotelListClone) {
					String keywordNew = StringUtil.getNormalAlphabet(keyword);
					String fieldNew = StringUtil.getNormalAlphabet(getAllValue(itemEHotel));
					
					if (!fieldNew.contains(keywordNew)) {
						eHotelList.remove(itemEHotel);
						if (eHotelList.size() <= 0) {
							break;
						}
					}
				}
			}
		}

		return eHotelList;
	}
	
	private Room2 genRoom(String type, int rating) {
		Map<String, String> nameMap = new HashMap<>();
		nameMap.put("1", "Phòng tiêu chuẩn giường đơn");
		nameMap.put("2", "Phòng tiêu chuẩn giường đôi");
		nameMap.put("3", "Phòng tiện nghi giường đôi");
		nameMap.put("4", "Phòng rộng rãi giường đôi");
		nameMap.put("5", "Phòng tiêu chuẩn 2 giường đơn");
		nameMap.put("6", "Phòng tiêu chuẩn 2 giường");
		nameMap.put("7", "Phòng tiện nghi 2 giường");
		nameMap.put("8", "Phòng rộng rãi 2 giường");
		nameMap.put("9", "Phòng tiêu chuẩn 3 giường đơn");
		nameMap.put("10", "Phòng tiêu chuẩn gia đình");
		nameMap.put("11", "Phòng tiện nghi gia đình");
		nameMap.put("12", "Phòng rộng rãi gia đình");
		
		Map<String, String> typeMap = new HashMap<>();
		typeMap.put("1", "1");
		typeMap.put("2", "2");
		typeMap.put("3", "3");
		typeMap.put("4", "4");
		typeMap.put("5", "5");
		typeMap.put("6", "6");
		typeMap.put("7", "7");
		typeMap.put("8", "8");
		typeMap.put("9", "9");
		typeMap.put("10", "10");
		typeMap.put("11", "11");
		typeMap.put("12", "1");
		
		Map<String, List<String>> bedMap = new HashMap<>();
		bedMap.put("1", Arrays.asList("1 Giường đơn"));
		bedMap.put("2", Arrays.asList("1 Giường đôi"));
		bedMap.put("3", Arrays.asList("1 Giường đôi lớn"));
		bedMap.put("4", Arrays.asList("1 Giường lớn"));
		bedMap.put("5", Arrays.asList("2 Giường đơn"));
		bedMap.put("6", Arrays.asList("1 Giường đơn", "1 Giường đôi"));
		bedMap.put("7", Arrays.asList("1 Giường đơn", "1 Giường đôi lớn"));
		bedMap.put("8", Arrays.asList("1 Giường đơn", "1 Giường lớn"));
		bedMap.put("9", Arrays.asList("3 Giường đơn"));
		bedMap.put("10", Arrays.asList("2 Giường đôi"));
		bedMap.put("11", Arrays.asList("2 Giường đôi lớn"));
		bedMap.put("12", Arrays.asList("2 Giường lớn"));
		
		Map<String, Integer> standardNumberOfAdultMap = new HashMap<>();
		standardNumberOfAdultMap.put("1", 1);
		standardNumberOfAdultMap.put("2", 2);
		standardNumberOfAdultMap.put("3", 2);
		standardNumberOfAdultMap.put("4", 2);
		standardNumberOfAdultMap.put("5", 2);
		standardNumberOfAdultMap.put("6", 3);
		standardNumberOfAdultMap.put("7", 3);
		standardNumberOfAdultMap.put("8", 3);
		standardNumberOfAdultMap.put("9", 3);
		standardNumberOfAdultMap.put("10", 4);
		standardNumberOfAdultMap.put("11", 4);
		standardNumberOfAdultMap.put("12", 4);
		
		Map<String, Integer> maximumNumberOfChildrenMap = new HashMap<>();
		maximumNumberOfChildrenMap.put("1", 1);
		maximumNumberOfChildrenMap.put("2", 2);
		maximumNumberOfChildrenMap.put("3", 2);
		maximumNumberOfChildrenMap.put("4", 2);
		maximumNumberOfChildrenMap.put("5", 2);
		maximumNumberOfChildrenMap.put("6", 3);
		maximumNumberOfChildrenMap.put("7", 3);
		maximumNumberOfChildrenMap.put("8", 3);
		maximumNumberOfChildrenMap.put("9", 3);
		maximumNumberOfChildrenMap.put("10", 4);
		maximumNumberOfChildrenMap.put("11", 4);
		maximumNumberOfChildrenMap.put("12", 4);
		
		Map<String, String> actualNumberOfAdultMap = new HashMap<>();
		actualNumberOfAdultMap.put("1", "1-1");
		actualNumberOfAdultMap.put("2", "1-2");
		actualNumberOfAdultMap.put("3", "1-2");
		actualNumberOfAdultMap.put("4", "1-2");
		actualNumberOfAdultMap.put("5", "2-3");
		actualNumberOfAdultMap.put("6", "2-4");
		actualNumberOfAdultMap.put("7", "2-4");
		actualNumberOfAdultMap.put("8", "2-4");
		actualNumberOfAdultMap.put("9", "3-5");
		actualNumberOfAdultMap.put("10", "3-5");
		actualNumberOfAdultMap.put("11", "3-5");
		actualNumberOfAdultMap.put("12", "3-5");
		
		Map<Integer, Integer> priceRandomMap = new HashMap<>();
		priceRandomMap.put(1, IntegerUtil.randomRange(-50, 50) * 1000);
		priceRandomMap.put(2, IntegerUtil.randomRange(-100, 100) * 1000);
		priceRandomMap.put(3, IntegerUtil.randomRange(-200, 200) * 1000);
		priceRandomMap.put(4, IntegerUtil.randomRange(-300, 300) * 1000);
		priceRandomMap.put(5, IntegerUtil.randomRange(-400, 400) * 1000);
		
		Map<String, Integer> priceMap = new HashMap<>();
		priceMap.put("1", 200000 * rating + priceRandomMap.get(rating));
		priceMap.put("2", 300000 * rating + priceRandomMap.get(rating));
		priceMap.put("3", 400000 * rating + priceRandomMap.get(rating));
		priceMap.put("4", 500000 * rating + priceRandomMap.get(rating));
		priceMap.put("5", 600000 * rating + priceRandomMap.get(rating));
		priceMap.put("6", 700000 * rating + priceRandomMap.get(rating));
		priceMap.put("7", 800000 * rating + priceRandomMap.get(rating));
		priceMap.put("8", 900000 * rating + priceRandomMap.get(rating));
		priceMap.put("9", 1000000 * rating + priceRandomMap.get(rating));
		priceMap.put("10", 1100000 * rating + priceRandomMap.get(rating));
		priceMap.put("11", 1200000 * rating + priceRandomMap.get(rating));
		priceMap.put("12", 1300000 * rating + priceRandomMap.get(rating));
		
		Room2 room = new Room2();
		room.setName(nameMap.get(type));
		room.setType(typeMap.get(type));
		room.setBed(bedMap.get(type));
		room.setStandardNumberOfAdult(standardNumberOfAdultMap.get(type));
		room.setMaximumNumberOfChildren(maximumNumberOfChildrenMap.get(type));
		room.setActualNumberOfAdult(actualNumberOfAdultMap.get(type));
		room.setPrice(priceMap.get(type));
		room.setStatus(true);
		
		return room;
	}
	
	private String getOrderStatus(String index) {
		HashMap<String, String> orderStatusMap = new HashMap<>();
		orderStatusMap.put("0", "cancel");
		orderStatusMap.put("1", "pending");
		orderStatusMap.put("2", "confirmed");
		orderStatusMap.put("3", "finished");
		
		String orderStatus = orderStatusMap.get(index);
		if (orderStatus == null || orderStatus.equals("")) {
			throw new CustomException("Cannot find order status " + index);
		}
		return orderStatus;
	}
	
	@Override
	public EHotel createEHotel(EHotelCreate eHotelCreate) {
		// mapping hotelDTO
		EHotelDTO eHotelDTO = new EHotelDTO();
		modelMapper.map(eHotelCreate, eHotelDTO);
		
		List<Room2> roomList = new ArrayList<>();
		int index = 1;
		for (int type = 1; type <= 12; type++) {
			Room2 room2 = new Room2();
			room2 = genRoom(String.valueOf(type), eHotelCreate.getNumberOfStarRating());
			
			for (int rating = 1; rating <= (eHotelCreate.getNumberOfStarRating() * 2); rating++) {
				Room2 room = new Room2();
				modelMapper.map(room2, room);
				room.setRoomId(String.valueOf(index));
				roomList.add(room);
				index++;
			}
		}
		eHotelDTO.setRoom2(roomList);
		
		// create hotel
		EHotel eHotel = create(eHotelDTO);

		return eHotel;
	}

	@Override
	public EHotel updateEHotel(EHotelUpdate eHotelUpdate) {
		// mapping DTO
		EHotelDTO eHotelDTO = new EHotelDTO();
		modelMapper.map(eHotelUpdate, eHotelDTO);
		
		// update hotel
		EHotel eHotel = update(eHotelDTO);

		return eHotel;
	}
	
	@Override
	public EHotel getDetailEHotel(String eHotelId) {
		EHotel eHotel = getDetail(eHotelId);
		
		return eHotel;
	}

	@Override
	public List<EHotel> getAllEHotel() {
		List<EHotel> eHotelList = getAll();

		return eHotelList;
	}
	
	@Override
	public List<EHotel> searchEHotel(String keyword) {
		List<EHotel> eHotelList = search(keyword);

		return eHotelList;
	}

	@Override
	public Order createOrder(EHotelOrderCreate eHotelOrder) {
		// get order from db with hotel
		EHotel eHotel = getDetail(eHotelOrder.getEHotelId());
		List<Order> orderList = eHotel.getOrder();
		if (orderList == null) {
			orderList = new ArrayList<Order>();
		}
		
		Order order = new Order();
		modelMapper.map(eHotelOrder, order);
		
		// insert order detail into order
		List<OrderDetail> orderDetailList = new ArrayList();
		
		for(String itemRoomId : eHotelOrder.getRoomId()) {
			OrderDetail orderDetail = new OrderDetail();
			for(Room2 itemRoom2 : eHotel.getRoom2()) {
				if(itemRoomId.equals(itemRoom2.getRoomId())) {
					modelMapper.map(itemRoom2, orderDetail);
					orderDetailList.add(orderDetail);
					break;
				}
			}
			if(!itemRoomId.equals(orderDetail.getRoomId())) {
				throw new CustomException("Cannot find room id " + itemRoomId);
			}
		}
		order.setOrderDetail(orderDetailList);
		
		// set default value
//		int indexOrderList = 1;
//		if (orderList != null) {
//			indexOrderList = orderList.size() + 1;
//		} 
		int indexOrderList = orderList.size() + 1;

		order.setOrderId(String.valueOf(indexOrderList));
		int numberOfDay = LocalDateUtil.numberOfDayBetween(order.getStartDate(), order.getEndDate());
		int totalPrice = 0;
		for(OrderDetail itemOrderDetail : order.getOrderDetail()) {
			totalPrice += itemOrderDetail.getPrice() * numberOfDay;
		}
		order.setTotalPrice(totalPrice);
		order.setOrderStatus("pending");
		
		// create order
		orderList.add(order);
		eHotel.setOrder(orderList);
		eHotelRepository.save(eHotel);

		return order;
	}

	@Override
	public Order updateOrder(EHotelOrderUpdate eHotelOrderUpdate) {
		// get hotel from data
		EHotel eHotel = getDetail(eHotelOrderUpdate.getEHotelId());
		
		// check exists order
		boolean checkOrder = false;
		for(Order itemOrder : eHotel.getOrder()) {
			if(itemOrder.getOrderId().equals(eHotelOrderUpdate.getOrderId())) {
				checkOrder = true;
				break;
			}
		}
		if(!checkOrder) {
			throw new CustomException("Cannot find order");
		}
		
		// mapping
		Order order = new Order();
		modelMapper.map(eHotelOrderUpdate, order);
		
		// set default value
		int totalPrice = 0;
		for(OrderDetail itemOrderDetail : order.getOrderDetail()) {
			totalPrice += itemOrderDetail.getPrice();
		}
		order.setTotalPrice(totalPrice);
		
		// update order
		List<Order> orderList = eHotel.getOrder();
		for(Order itemOrder : eHotel.getOrder()) {
			if(itemOrder.getOrderId().equals(eHotelOrderUpdate.getOrderId())) {
				orderList.set(eHotel.getOrder().indexOf(itemOrder), order);
				break;
			}
		}
		eHotel.setOrder(orderList);
		eHotelRepository.save(eHotel);

		return null;
	}

	@Override
	public Order getOneOrder(String eHotelId, String orderId) {
		// get hotel from data
		EHotel eHotel = getDetail(eHotelId);
		
		// check exists order
		boolean checkOrder = false;
		for(Order itemOrder : eHotel.getOrder()) {
			if(itemOrder.getOrderId().equals(orderId)) {
				checkOrder = true;
				break;
			}
		}
		if(!checkOrder) {
			throw new CustomException("Cannot find order");
		}
		
		// get order
		Order order = new Order();
		for(Order itemOrder : eHotel.getOrder()) {
			if(itemOrder.getOrderId().equals(orderId)) {
				order = itemOrder;
				break;
			}
		}
		return order;
	}

	@Override
	public List<Order> getAllOrder(String eHotelId) {
		// get hotel
		EHotel eHotel = getDetail(eHotelId);
		
		return eHotel.getOrder();
	}

	@Override
	public List<Room> searchRoom(RoomSearch roomSearch) {
		// 1. Chia số người lớn theo số phòng = A
		// 2. Tìm phòng chứa được A hoặc phòng to nhất chứa được A
		// 3. Nếu còn dư người, tìm 1 phòng chứa đủ người còn dư hoặc phòng to nhất chứa đủ người còn dư
		// 4. nếu còn dư người, lặp lại bước 3
		
		// check number of adult than number of room
		if (roomSearch.getNumberOfAdult() < roomSearch.getNumberOfRoom()) {
			throw new CustomException("The number of adults must be greater than or equal to the number of rooms");
		}
		
		// get hotel from db
		EHotel eHotel = getDetail(roomSearch.getEHotelId());
		
		// get all room with hotelId from db
		List<Room> roomList = eHotel.getRoom();
		List<Room> roomListClone = new ArrayList<>();
		
		// check order
		if(eHotel.getOrder() != null) {
			// get all Order with startDate and endDate
			List<Order> orderList = new ArrayList<>();
			for(Order itemOrder : eHotel.getOrder()) {
				if((itemOrder.getStartDate().isBefore(roomSearch.getEndDate()) || itemOrder.getStartDate().isEqual(roomSearch.getEndDate())) 
						&& (itemOrder.getEndDate().isAfter(roomSearch.getStartDate()) || itemOrder.getEndDate().isEqual(roomSearch.getStartDate()))) {
					orderList.add(itemOrder);
				}
			}
			
			// get all roomId with startDate and endDate
			List<String> roomIdList = new ArrayList<>();
			for(Order itemOrder : orderList) {
				for(OrderDetail itemOrderDetail : itemOrder.getOrderDetail()) {
					roomIdList.add(itemOrderDetail.getRoomId());
				}
			}
			
			// search with startDate
			roomListClone.clear();
			roomListClone.addAll(roomList);
			for(Room itemRoom : roomListClone) {
				for(String itemRoomId : roomIdList) {
					if(itemRoomId.equals(itemRoom.getRoomId())) {
						roomList.remove(itemRoom);
						break;
					}
				}
				if(roomList.size() == 0) {
					break;
				}
			}
		}
		
		// sort roomList by capacity asc
		Collections.sort(roomList, new Comparator<Room>() {
            @Override
            public int compare(Room e1, Room e2) {
            	int result = 0;
            	result = Integer.compare(e1.getCapacity(), e2.getCapacity());
                return result;
            }
        });

		// search with numberOfPeople
		List<Room> roomListResult = new ArrayList<>();
		int numberOfPeople = 0;
		numberOfPeople = roomSearch.getNumberOfAdult();
		int currentCaptity = 0;
		int numberOfPeoplePerRoom = 0;
		
		// Find with number of room
		numberOfPeoplePerRoom = roomSearch.getNumberOfAdult() / roomSearch.getNumberOfRoom();
		for (int i = 0; i < roomSearch.getNumberOfRoom(); i++) {
			Room roomMaxCapacity = new Room();
			roomMaxCapacity.setCapacity(0);
			roomListClone.clear();
			roomListClone.addAll(roomList);
			for (Room itemRoom : roomListClone) {
				if (itemRoom.getCapacity() >= numberOfPeoplePerRoom) {
					currentCaptity += itemRoom.getCapacity();
					roomListResult.add(itemRoom);
					roomList.remove(itemRoom);
					roomMaxCapacity.setCapacity(0);
					break;
				}
				if (roomMaxCapacity.getCapacity() < itemRoom.getCapacity()) {
					modelMapper.map(itemRoom, roomMaxCapacity);
				}
			}
			if (roomMaxCapacity.getCapacity() > 0) {
				currentCaptity += roomMaxCapacity.getCapacity();
				roomListResult.add(roomMaxCapacity);
				roomList.remove(roomMaxCapacity);
			}
		}
		
		// Continue find if there are more people left
		while (currentCaptity < numberOfPeople) {
			if(roomList.size() == 0) {
				throw new CustomException("There are not enough available rooms");
			}
			
			Room roomMaxCapacity = new Room();
			roomMaxCapacity.setCapacity(0);
			roomListClone.clear();
			roomListClone.addAll(roomList);
			for (Room itemRoom : roomListClone) {
				if (itemRoom.getCapacity() >= numberOfPeople) {
					currentCaptity += itemRoom.getCapacity();
					roomListResult.add(itemRoom);
					roomList.remove(itemRoom);
					roomMaxCapacity.setCapacity(0);
					break;
				}
				if (roomMaxCapacity.getCapacity() < itemRoom.getCapacity()) {
					modelMapper.map(itemRoom, roomMaxCapacity);
				}
			}
			if (roomMaxCapacity.getCapacity() > 0) {
				currentCaptity += roomMaxCapacity.getCapacity();
				roomListResult.add(roomMaxCapacity);
				roomList.remove(roomMaxCapacity);
			}
		}

		return roomListResult;
	}

	@Override
	public List<RoomSearchRes> searchRoom2(RoomSearch roomSearch) {		
		long numberOfDay = Math.abs(ChronoUnit.DAYS.between(roomSearch.getEndDate(), roomSearch.getStartDate()));
		// A = số người lớn
		// R = số phòng
		// Thực hiện tìm R phòng cho A người
		// 1. A / R = P (lấy số nguyên)
		// 2. Tìm 1 phòng chứa được P người, (nếu không có thì không tìm được phòng nào --> dừng)
		// 3. A - P = A1, R - 1 = R1 (nếu A1 = 0 thì dừng và trả kết quả)
		// 4. Thực hiện tìm R1 phòng cho A1 người (tới đây thì lặp lại cho tới khi hết thì thôi)
		
		// check number of adult and number of room
		if (roomSearch.getNumberOfAdult() < roomSearch.getNumberOfRoom()) {
			throw new CustomException("The number of adults must be greater than or equal to the number of rooms");
		}
		
		// get room list
		EHotel eHotel = new EHotel();
		eHotel = getDetail(roomSearch.getEHotelId());
		List<Room2> roomList = new ArrayList<>();
		roomList.addAll(eHotel.getRoom2());
		if (roomList.isEmpty()) {
			throw new CustomException("No suitable room found");
		}
		
		// check order
		if(eHotel.getOrder() != null) {
			// get all Order with startDate and endDate
			List<Order> orderList = new ArrayList<>();
			for(Order itemOrder : eHotel.getOrder()) {
				if((itemOrder.getStartDate().isBefore(roomSearch.getEndDate()) || itemOrder.getStartDate().isEqual(roomSearch.getEndDate())) 
						&& (itemOrder.getEndDate().isAfter(roomSearch.getStartDate()) || itemOrder.getEndDate().isEqual(roomSearch.getStartDate()))
						&& (!itemOrder.getOrderStatus().equals("cancel"))) {
					orderList.add(itemOrder);
				}
			}
			
			// get all roomId with startDate and endDate
			List<String> roomIdList = new ArrayList<>();
			for(Order itemOrder : orderList) {
				for(OrderDetail itemOrderDetail : itemOrder.getOrderDetail()) {
					roomIdList.add(itemOrderDetail.getRoomId());
				}
			}
			
			// search with startDate
			List<Room2> roomListClone = new ArrayList<>();
			roomListClone.clear();
			roomListClone.addAll(roomList);
			for(Room2 itemRoom2 : roomListClone) {
				for(String itemRoomId : roomIdList) {
					if(itemRoomId.equals(itemRoom2.getRoomId())) {
						roomList.remove(itemRoom2);
						break;
					}
				}
				if(roomList.size() == 0) {
					break;
				}
			}
		}
		
		Map<List<String>, Map<Room2, List<String>>> roomMap = new HashMap<>();
		
		// find room
		int numberOfAdult = roomSearch.getNumberOfAdult();
		int numberOfRoom = roomSearch.getNumberOfRoom();
		int personPerRoom = (int) numberOfAdult / numberOfRoom;
		int index = 1;
		while (numberOfAdult > 0) {
			Map<Room2, List<String>> roomIdMap = new HashMap<>();

			for (Room2 itemRoom : roomList) {
				String[] actualNumberOfAdultSplit = itemRoom.getActualNumberOfAdult().split("-");
				int minPerson = Integer.valueOf(actualNumberOfAdultSplit[0]);
				int maxPerson = Integer.valueOf(actualNumberOfAdultSplit[1]);
				if (minPerson <= personPerRoom && personPerRoom <= maxPerson && itemRoom.getStatus() == true) {
					// check exists in roomIdMap
					boolean check = false;
					Map<Room2, List<String>> roomIdMapClone = new HashMap<>();
					roomIdMapClone.putAll(roomIdMap);
					for (Map.Entry<Room2, List<String>> itemRoomId : roomIdMapClone.entrySet()) {
						Room2 key = itemRoomId.getKey ();
						List<String> value = itemRoomId.getValue ();
						
						if (key.getType().equals(itemRoom.getType())) {
							List<String> roomIdList = new ArrayList<>();
							roomIdList.addAll(value);
							roomIdList.add(itemRoom.getRoomId());
							roomIdMap.replace(key, roomIdList);
							check = true;
							break;
						}
					}
					
					// create new in roomIdMap
					if (check == false) {
						List<String> roomIdList = new ArrayList<>();
						roomIdList.add(itemRoom.getRoomId());
						roomIdMap.put(itemRoom, roomIdList);
					}
				}
			}
			if (roomIdMap.isEmpty()) {
				throw new CustomException("No suitable room found");
			}
			List<String> key = new ArrayList<>();
			key.add(String.valueOf(index));
			key.add(String.valueOf(personPerRoom));
			roomMap.put(key, roomIdMap);
			index++;
			
			numberOfAdult -= personPerRoom;
			if (numberOfAdult <= 0) {
				break;
			}
			numberOfRoom -= 1;
			personPerRoom = (int) numberOfAdult / numberOfRoom;
		}
		
		// get standard room = person per room
		Map<List<String>, Map<Room2, List<String>>> roomMapClone = new HashMap<>();
		roomMapClone.putAll(roomMap);
		for (Map.Entry<List<String>, Map<Room2, List<String>>> itemRoom : roomMapClone.entrySet()) {
			List<String> key = itemRoom.getKey ();
			Map<Room2, List<String>> value = itemRoom.getValue ();
			
			Map<Room2, List<String>> valueNew = new HashMap<>();
			valueNew.putAll(value);
			
			for (Map.Entry<Room2, List<String>> itemValue : value.entrySet()) {
				Room2 keyOfValue = itemValue.getKey ();
				List<String> valueOfValue = itemValue.getValue ();
				
				if (keyOfValue.getStandardNumberOfAdult() != Integer.valueOf(key.get(1))) {
					valueNew.remove(keyOfValue);
					
				}
			}
			roomMap.replace(key, valueNew);
		}
		
		// get key
		Map<String, List<String>> keyMap = new HashMap<>();
		index = 1;
		for (Map.Entry<List<String>, Map<Room2, List<String>>> itemRoom : roomMap.entrySet()) {
			List<String> key = itemRoom.getKey ();
			keyMap.put(String.valueOf(index), key);
			index += 1;
		}
		
		// check empty
		if (roomMap.get(keyMap.get(String.valueOf(1))).isEmpty()) {
			throw new CustomException("No suitable room found");
		}
		
		// create options room
		Map<String, List<Room2>> roomResultMap = new HashMap<>();
		index = 1;
		for (Map.Entry<Room2, List<String>> itemRoom : roomMap.get(keyMap.get(String.valueOf(1))).entrySet()) {
			Room2 key = itemRoom.getKey();
			List<String> value = itemRoom.getValue();
			
			List<Room2> roomResultList = new ArrayList<>();
			roomResultList.add(key);
			
			roomResultMap.put(String.valueOf(index), roomResultList);
			index += 1;
		}
		if (roomSearch.getNumberOfRoom() > 1) {
			for (int i = 2; i <= roomSearch.getNumberOfRoom(); i ++) {
				index = 1;
				Map<String, List<Room2>> roomResultMapClone = new HashMap<>();
				roomResultMapClone.putAll(roomResultMap);
				for (Map.Entry<String, List<Room2>> itemRoomResult : roomResultMapClone.entrySet()) {
					String keyResult = itemRoomResult.getKey();
					List<Room2> valueResult = itemRoomResult.getValue();
					
					// check empty
					if (roomMap.get(keyMap.get(String.valueOf(i))).isEmpty()) {
						throw new CustomException("No suitable room found");
					}
					
					for (Map.Entry<Room2, List<String>> itemRoom : roomMap.get(keyMap.get(String.valueOf(i))).entrySet()) {
						Room2 keyRoom = itemRoom.getKey();
						List<String> valueRoom = itemRoom.getValue();

						List<Room2> roomResultList = new ArrayList<>();
						roomResultList.addAll(valueResult);
						
						// get roomId
						String roomId = null;
						for (String itemString : valueRoom) {
							roomId = itemString;
							for (Room2 itemRoom2 : roomResultList) {
								if (itemRoom2.getRoomId().equals(itemString)) {
									roomId = null;
									break;
								} 
							}
							if (roomId != null) {
								break;
							}
						}
						if (roomId == null) {
							throw new CustomException("No suitable room found");
						}
						
						Room2 room2 = new Room2();
						modelMapper.map(keyRoom, room2);
						room2.setRoomId(roomId);
						roomResultList.add(room2);
						
						roomResultMap.put(String.valueOf(index), roomResultList);
						index += 1;
					}
				}
			}
		}
		
		// delete exists room
		Map<String, List<Room2>> roomResultMapClone = new HashMap<>();
		roomResultMapClone.putAll(roomResultMap);
		int index1 = 1;
		for (Map.Entry<String, List<Room2>> itemRoomResult : roomResultMapClone.entrySet()) {
			String keyRoomResult = itemRoomResult.getKey();
			List<Room2> valueRoomResult = itemRoomResult.getValue();
			
			int index2 = 1;
			for (Map.Entry<String, List<Room2>> itemRoomResult2 : roomResultMapClone.entrySet()) {
				String keyRoomResult2 = itemRoomResult2.getKey();
				List<Room2> valueRoomResult2 = itemRoomResult2.getValue();
				
				if (index2 > index1) {
					int RoomResultSize = valueRoomResult.size();
					for (Room2 itemRoom : valueRoomResult) {
						for (Room2 itemRoom2 : valueRoomResult2) {
							if (itemRoom.getRoomId().equals(itemRoom2.getRoomId())) {
								RoomResultSize -= 1;
							}
						}
					}
					if (RoomResultSize == 0) {
						roomResultMap.remove(keyRoomResult2);
					}
				}
				index2 += 1;
			}
			index1 += 1;
		}
		
		List<RoomSearchRes> roomSearchResList = new ArrayList<>();
		for (Map.Entry<String, List<Room2>> itemRoomResult : roomResultMap.entrySet()) {
			String keyRoomResult = itemRoomResult.getKey();
			List<Room2> valueRoomResult = itemRoomResult.getValue();
			
			int totalPrice = 0;
			for (Room2 itemRoom: valueRoomResult) {
				totalPrice += itemRoom.getPrice();
			}
			
			RoomSearchRes roomSearchRes = new RoomSearchRes();
			roomSearchRes.setRoom(valueRoomResult);
			totalPrice = (int)(totalPrice * ((double)numberOfDay + 0.5));
//			if (totalPrice % 1000 != 0) {
//				totalPrice += 500;
//			}
			roomSearchRes.setTotalPrice(totalPrice);
			roomSearchResList.add(roomSearchRes);
		}
		
		// sort list tour by total price value
		Collections.sort(roomSearchResList, new Comparator<RoomSearchRes>() {
            @Override
            public int compare(RoomSearchRes roomSearchRes1, RoomSearchRes roomSearchRes2) {
            	int result = Integer.compare(roomSearchRes1.getTotalPrice(), roomSearchRes2.getTotalPrice());
                return result;
            }
        });
		
		return roomSearchResList;
	}

	@Override
	public List<EHotel> listEHotelSearch(String keyword) {
		List<EHotel> eHotelList = new ArrayList<>();
		eHotelList.addAll(search2(keyword));
		
		return eHotelList;
	}

	@Override
	public List<EHotel> listEHotelFilter(HashMap<String, String> filter, List<EHotel> eHotelList) {
		List<EHotel> eHotelListClone = new ArrayList<>();
		eHotelListClone.addAll(eHotelList);
		for (EHotel itemEHotel : eHotelListClone) {
			filter.forEach((fieldName, fieldValue) -> {
				// filter of account class
				for (Field itemField : EHotel.class.getDeclaredFields()) {
					itemField.setAccessible(true);
					// field of account
					if (itemField.getName().equals(fieldName)) {
						try {
							String value = String.valueOf(itemField.get(itemEHotel));
							String valueNew = StringUtil.getNormalAlphabet(value);
							String fieldValueNew = StringUtil.getNormalAlphabet(fieldValue);
							if (!valueNew.contains(fieldValueNew)) {
								eHotelList.remove(itemEHotel);
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
							String value = String.valueOf(itemField.get(itemEHotel));
							String valueNew = StringUtil.getNormalAlphabet(value);
							String fieldValueNew = StringUtil.getNormalAlphabet(fieldValue);
							if (!valueNew.contains(fieldValueNew)) {
								eHotelList.remove(itemEHotel);
								break;
							}
						} catch (Exception e) {
							
						}
					}
				}
				
			});
			if (eHotelList.size() <= 0) {
				break;
			}
		}
		
		return eHotelList;
	}

	@Override
	public List<EHotel> listEHotelSort(Sort sort, List<EHotel> eHotelList) {
		Collections.sort(eHotelList, new Comparator<EHotel>() {
            @Override
            public int compare(EHotel eHotel1, EHotel eHotel2) {
            	int result = 0;
            	
            	// sort of account class
        		for (Field itemField : EHotel.class.getDeclaredFields()) {
        			itemField.setAccessible(true);
        			// field of account
    				if (sort.getSortBy().equals(itemField.getName())) {
    					try {
		            		String string1 = String.valueOf(itemField.get(eHotel1));
		            		String string2 = String.valueOf(itemField.get(eHotel2));
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
		            		String string1 = String.valueOf(itemField.get(eHotel1));
		            		String string2 = String.valueOf(itemField.get(eHotel2));
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
		
		return eHotelList;
	}

	@Override
	public Order updateOrderStatus(EHotelOrderStatusUpdate eHotelOrderStatusUpdate) {
		// get ehotel
		EHotel eHotel = new EHotel();
		eHotel = getDetail(eHotelOrderStatusUpdate.getEHotelId());
		
		// get order list
		List<Order> orderList = new ArrayList<>();
		orderList.addAll(eHotel.getOrder());
		
		// get order status
		String orderStatus = getOrderStatus(eHotelOrderStatusUpdate.getOrderStatus());
		
		if (orderStatus.equals("cancel")) {
			throw new CustomException("Can't update status for canceled orders");
		}
		if (orderStatus.equals("finished")) {
			throw new CustomException("Can't update status for finished orders");
		}
		
		for (Order itemOrder : eHotel.getOrder()) {
			if (itemOrder.getOrderId().equals(eHotelOrderStatusUpdate.getOrderId())) {
				Order order = new Order();
				modelMapper.map(itemOrder, order);
				order.setOrderStatus(orderStatus);
				orderList.set(eHotel.getOrder().indexOf(itemOrder), order);
				eHotel.setOrder(orderList);
				
				// set default value
				String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
				LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
				eHotel.setLastModifiedBy(accountId);
				eHotel.setLastModifiedAt2(currentDate);
				
				eHotelRepository.save(eHotel);
				
				return order;
			}
		}
		
		throw new CustomException("Cannot find order " + eHotelOrderStatusUpdate.getOrderId());
	}

	@Override
	public List<EHotelDTOSimple> searchEHotelByLocation(Location location) {
		List<EHotel> eHotelList = new ArrayList<>();
		eHotelList.addAll(getAll());
		
		List<EHotel> eHotelListClone = new ArrayList<>();
		eHotelListClone.addAll(eHotelList);
		
		for (EHotel itemEHotel : eHotelListClone) {
			if (itemEHotel.getAddress() != null) {
				if (location.getProvince() != null && !location.getProvince().equals("")) {
					if (!itemEHotel.getAddress().getProvince().equals(location.getProvince())) {
						eHotelList.remove(itemEHotel);
						if (eHotelList.size() <= 0) {
							break;
						}
					} else if (location.getDistrict() != null && !location.getDistrict().equals("")) {
						if (!itemEHotel.getAddress().getDistrict().equals(location.getDistrict())) {
							eHotelList.remove(itemEHotel);
							if (eHotelList.size() <= 0) {
								break;
							}
						} else if (location.getCommune() != null && !location.getCommune().equals("")) {
							if (!itemEHotel.getAddress().getCommune().equals(location.getCommune())) {
								eHotelList.remove(itemEHotel);
								if (eHotelList.size() <= 0) {
									break;
								}
							} 
						}
					}
				}
			} else {
				eHotelList.remove(itemEHotel);
				if (eHotelList.size() <= 0) {
					break;
				}
			}

		}
		
		List<EHotelDTOSimple> eHotelDTOSimpleList = new ArrayList<>();
		for (EHotel itemEHotel : eHotelList) {
			EHotelDTOSimple eHotelDTOSimple = new EHotelDTOSimple();
			modelMapper.map(itemEHotel, eHotelDTOSimple);
			eHotelDTOSimpleList.add(eHotelDTOSimple);
		}
		
		
		return eHotelDTOSimpleList;
	}
}
