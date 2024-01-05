package hcmute.kltn.Backend.model.hotel.service.impl;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import hcmute.kltn.Backend.model.base.extend.Contact;
import hcmute.kltn.Backend.model.base.externalAPI.dto.ApiCallResponse;
import hcmute.kltn.Backend.model.base.externalAPI.service.IExternalAPIService;
import hcmute.kltn.Backend.model.generatorSequence.service.IGeneratorSequenceService;
import hcmute.kltn.Backend.model.hotel.dto.HotelCreate;
import hcmute.kltn.Backend.model.hotel.dto.HotelDTO;
import hcmute.kltn.Backend.model.hotel.dto.HotelSearch;
import hcmute.kltn.Backend.model.hotel.dto.HotelUpdate;
import hcmute.kltn.Backend.model.hotel.dto.RoomSearch;
import hcmute.kltn.Backend.model.hotel.dto.RoomSearch2;
import hcmute.kltn.Backend.model.hotel.dto.entity.Hotel;
import hcmute.kltn.Backend.model.hotel.dto.extend.Room;
import hcmute.kltn.Backend.model.hotel.dto.extend.Room2;
import hcmute.kltn.Backend.model.hotel.dto.extend.SearchAPI;
import hcmute.kltn.Backend.model.hotel.repository.HotelRepository;
import hcmute.kltn.Backend.model.hotel.service.IHotelService;
import hcmute.kltn.Backend.model.tour.dto.entity.Tour;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.service.IEHotelService;
import hcmute.kltn.Backend.util.LocalDateTimeUtil;
import hcmute.kltn.Backend.util.LocalDateUtil;
import hcmute.kltn.Backend.util.StringUtil;

@Service
public class HotelService implements IHotelService{
	@Autowired
	private HotelRepository hotelRepository;
	@Autowired
	private IGeneratorSequenceService iGeneratorSequenceService;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private IAccountDetailService iAccountDetailService;
	@Autowired
    private MongoTemplate mongoTemplate;
	@Autowired
	private IEHotelService iEHotelService;
	@Autowired
	private IExternalAPIService iExternalAPIService;
	
	private String getCollectionName() {
        String collectionName = mongoTemplate.getCollectionName(Hotel.class);
        return collectionName;
    }
	
	private void checkFieldCondition(Hotel hotel) {
		// check null
		if(hotel.getHotelName() == null || hotel.getHotelName().equals("")) {
			throw new CustomException("Hotel Name is not null");
		}
		if(hotel.getContact() == null) {
			throw new CustomException("Contact is not null");
		}
		if(hotel.getAddress() == null) {
			throw new CustomException("Address is not null");
		}
		
		// check unique
	}
	
	private Hotel create(Hotel hotel) {
		// check field condition
		checkFieldCondition(hotel);
		
		// set default value
		String hotelId = iGeneratorSequenceService.genId(getCollectionName());
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDate dateNow = LocalDateUtil.getDateNow();
		hotel.setHotelId(hotelId);
		hotel.setStatus(true);
		hotel.setCreatedBy(accountId);
		hotel.setCreatedAt(dateNow);
		hotel.setLastModifiedBy(accountId);
		hotel.setLastModifiedAt(dateNow);
		
		// new date
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		hotel.setCreatedAt2(currentDate);
		hotel.setLastModifiedAt2(currentDate);
		
		// create hotel
		Hotel hotelNew = new Hotel();
		hotelNew = hotelRepository.save(hotel);

		return hotelNew;
	}
	
	private Hotel update(Hotel hotel) {
		// check exists
		if(!hotelRepository.existsById(hotel.getHotelId())) {
			throw new CustomException("Cannot find hotel");
		}
		
		// check field condition
		checkFieldCondition(hotel);
		
		// set default value
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDate dateNow = LocalDateUtil.getDateNow();
		hotel.setLastModifiedBy(accountId);
		hotel.setLastModifiedAt(dateNow);
		
		// new date
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		hotel.setLastModifiedAt2(currentDate);
		
		// update hotel
		Hotel hotelNew = new Hotel();
		hotelNew = hotelRepository.save(hotel);

		return hotelNew;
	}

//	private Hotel update(HotelDTO hotelDTO) {
//		// check exists
//		if(!hotelRepository.existsById(hotelDTO.getHotelId())) {
//			throw new CustomException("Cannot find hotel");
//		}
//		
//		// check field condition
//		checkFieldCondition(hotelDTO);
//		
//		// get hotel from database
//		Hotel hotel = hotelRepository.findById(hotelDTO.getHotelId()).get();
//		
//		// mapping
//		modelMapper.map(hotelDTO, hotel);
//		
//		// set default value
//		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
//		LocalDate dateNow = LocalDateUtil.getDateNow();
//		hotel.setLastModifiedBy(accountId);
//		hotel.setLastModifiedAt(dateNow);
//		
//		// update hotel
//		hotel = hotelRepository.save(hotel);
//
//		return hotel;
//	}

	private Hotel getDetail(String hotelId) {
		// check exists
		if(!hotelRepository.existsById(hotelId)) {
			throw new CustomException("Cannot find hotel");
		}
		
		// get hotel from database
		Hotel hotel = hotelRepository.findById(hotelId).get();

		return hotel;
	}

	private List<Hotel> getAll() {
		// find all hotel
		List<Hotel> hotelList = hotelRepository.findAll();
		
		return hotelList;
	}

	private void delete(String hotelId) {
		// check exists
		if(hotelRepository.existsById(hotelId)) {
			hotelRepository.deleteById(hotelId);
		}
	}
	
	private List<Hotel> search(String keyword) {
		// init hotel list
		List<Hotel> hotelList = new ArrayList<>();
		
		if(keyword == null || keyword.equals("")) {
			hotelList = getAll();
		} else {
			// create list field name
			List<Criteria> criteriaList = new ArrayList<>();
			for(Field itemField : Hotel.class.getDeclaredFields()) {
				if(itemField.getType() == String.class) {
					criteriaList.add(Criteria.where(itemField.getName()).regex(keyword, "i"));
				}
			}
			criteriaList.add(Criteria.where("_id").is(keyword));
			
			Criteria criteria = new Criteria();
			criteria.orOperator(criteriaList.toArray(new Criteria[0]));
			
			// create query
			Query query = new Query();
			query.addCriteria(criteria);
			
			// search
			hotelList = mongoTemplate.find(query, Hotel.class);
		}

		return hotelList;
	}
	
	private String getAllValue(Hotel hotel) {
		String result = new String();
		
		// value of account
		for (Field itemField : Hotel.class.getDeclaredFields()) {
			itemField.setAccessible(true);
			try {
				// check type
				boolean isList = itemField.getType().isAssignableFrom(List.class);
				boolean isContact = itemField.getType().isAssignableFrom(Contact.class);
				boolean isAddress = itemField.getType().isAssignableFrom(Address.class);
				boolean isSearchAPI = itemField.getType().isAssignableFrom(SearchAPI.class);
				Object object = itemField.get(hotel);
				if (object != null && !isList && !isContact && !isAddress && !isSearchAPI) {
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
				Object object = itemField.get(hotel);
				if (object != null) {
					result += String.valueOf(object) + " ";
				} 
			} catch (Exception e) {
				
			}
		}

		return result;
	}
	
	private List<Hotel> search2(String keyword) {
		// init tour List
		List<Hotel> hotelList = new ArrayList<>();
		hotelList = hotelRepository.findAll();
		if (keyword != null) {
			keyword = keyword.trim();
		}

		if (keyword != null && !keyword.isEmpty()) {
			if (hotelList != null) {
				List<Hotel> hotelListClone = new ArrayList<>();
				hotelListClone.addAll(hotelList);
				for (Hotel itemHotel : hotelListClone) {
					String keywordNew = StringUtil.getNormalAlphabet(keyword);
					String fieldNew = StringUtil.getNormalAlphabet(getAllValue(itemHotel));
					
					if (!fieldNew.contains(keywordNew)) {
						hotelList.remove(itemHotel);
						if (hotelList.size() <= 0) {
							break;
						}
					}
				}
			}
		}

		return hotelList;
	}
	
	private HotelDTO getHotelDTO(Hotel hotel) {
		HotelDTO hotelDTONew = new HotelDTO();
		modelMapper.map(hotel, hotelDTONew);
		return hotelDTONew;
	}
	
	private List<HotelDTO> getHotelDTOList(List<Hotel> hotelList) {
		List<HotelDTO> hotelDTOList = new ArrayList<>();
		for (Hotel itemHotel : hotelList) {
			hotelDTOList.add(getHotelDTO(itemHotel));
		}
		return hotelDTOList;
	}

	@Override
	public HotelDTO createHotel(HotelCreate hotelCreate) {
		// mapping hotel
		Hotel hotel = new Hotel();
		modelMapper.map(hotelCreate, hotel);
		
		// create hotel
		Hotel hotelNew = new Hotel();
		hotelNew = create(hotel);
		
		return getHotelDTO(hotelNew);
	}

	@Override
	public HotelDTO updateHotel(HotelUpdate hotelUpdate) {
		// mapping hotel
		Hotel hotel = new Hotel();
		hotel = getDetail(hotelUpdate.getHotelId());
		modelMapper.map(hotelUpdate, hotel);
		
		// update hotel
		Hotel hotelNew = new Hotel();
		hotelNew = update(hotel);

		return getHotelDTO(hotelNew);
	}

	@Override
	public HotelDTO getDetailHotel(String hotelId) {
		// get hotel 
		Hotel hotel = getDetail(hotelId);

		return getHotelDTO(hotel);
	}

	@Override
	public List<HotelDTO> getAllHotel() {
		// get all hotel
		List<Hotel> hotelList = getAll();

		return getHotelDTOList(hotelList);
	}

	@Override
	public List<HotelDTO> searchHotel(HotelSearch hotelSearch) {
		// search with keyword
		List<Hotel> hotelList = new ArrayList<>();
		List<Hotel> hotelListClone = new ArrayList<>();
		if(hotelSearch.getKeyword() != null) {
			hotelList = search(hotelSearch.getKeyword());
		} else {
			hotelList = getAll();
		}
		
		// search with province
		if(hotelSearch.getProvince() != null && !hotelSearch.getProvince().equals("")) {
			hotelListClone.clear();
			hotelListClone.addAll(hotelList);
			for(Hotel itemHotel : hotelListClone) {
				if(!itemHotel.getAddress().getProvince().equals(hotelSearch.getProvince())) {
					hotelList.remove(itemHotel);
					if(hotelList.size() == 0) {
						break;
					}
				}
			}
		}
		
		// search with district
		if(hotelSearch.getDistrict() != null && !hotelSearch.getDistrict().equals("")) {
			hotelListClone.clear();
			hotelListClone.addAll(hotelList);
			for(Hotel itemHotel : hotelListClone) {
				if(!itemHotel.getAddress().getDistrict().equals(hotelSearch.getDistrict())) {
					hotelList.remove(itemHotel);
					if(hotelList.size() == 0) {
						break;
					}
				}
			}
		}
		
		// search with commune
		if(hotelSearch.getCommune() != null && !hotelSearch.getCommune().equals("")) {
			hotelListClone.clear();
			hotelListClone.addAll(hotelList);
			for(Hotel itemHotel : hotelListClone) {
				if(!itemHotel.getAddress().getCommune().equals(hotelSearch.getCommune())) {
					hotelList.remove(itemHotel);
					if(hotelList.size() == 0) {
						break;
					}
				}
			}
		}

		return getHotelDTOList(hotelList);
	}

	@Override
	public List<Room> searchRoom(RoomSearch roomSearch) {
		// mapping field in enterprise
		hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.RoomSearch roomSearchEnterprise = new hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.RoomSearch();
		modelMapper.map(roomSearch, roomSearchEnterprise);
		
		// search room in enterprise
		List<hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Room> roomListEnterprise = new ArrayList<>();
		roomListEnterprise = iEHotelService.searchRoom(roomSearchEnterprise);
		
		// mapping room list
		List<Room> roomList = new ArrayList<>();
		for(hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Room itemRoomEnterprise : roomListEnterprise) {
			Room room = new Room();
			modelMapper.map(itemRoomEnterprise, room);
			roomList.add(room);
		}
		
		return roomList;
	}

	@Override
	public Room getRoomDetail(String hotelId, String roomId) {
		Room room = new Room();
		
		hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.entity.EHotel eHotel = new hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.entity.EHotel();		
		eHotel = iEHotelService.getDetailEHotel(hotelId);
		for (hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Room itemRoomEnterprise : eHotel.getRoom()) {
			if (itemRoomEnterprise.getRoomId().equals(roomId)) {
				room.setRoomId(itemRoomEnterprise.getRoomId());
				room.setCapacity(itemRoomEnterprise.getCapacity());
				room.setPrice(itemRoomEnterprise.getPrice());
				room.setStatus(itemRoomEnterprise.getStatus());
				
				break;
			}
		}
		return room;
	}

	@Override
	public List<Room2> searchRoom2(RoomSearch2 roomSearch) {
		Hotel hotel = new Hotel();
		hotel = getDetail(roomSearch.getHotelId());
		
		String url = hotel.getSearchAPI().getUrl();
		HashMap<String, String> header = new HashMap<>();
		HashMap<String, String> param = new HashMap<>();
		ApiCallResponse apiCallResponse = new ApiCallResponse();
		apiCallResponse = iExternalAPIService.get(url, header, param);

		return null;
	}

	@Override
	public List<HotelDTO> listHotelSearch(String keyword) {
		List<Hotel> hotelList = new ArrayList<>();
		hotelList.addAll(search2(keyword));
		
		return getHotelDTOList(hotelList);
	}

	@Override
	public List<HotelDTO> listHotelFilter(HashMap<String, String> filter, List<HotelDTO> hotelDTOList) {
		List<HotelDTO> hotelDTOListClone = new ArrayList<>();
		hotelDTOListClone.addAll(hotelDTOList);
		for (HotelDTO itemHotelDTO : hotelDTOListClone) {
			filter.forEach((fieldName, fieldValue) -> {
				// filter of account class
				for (Field itemField : HotelDTO.class.getDeclaredFields()) {
					itemField.setAccessible(true);
					// field of account
					if (itemField.getName().equals(fieldName)) {
						try {
							String value = String.valueOf(itemField.get(itemHotelDTO));
							String valueNew = StringUtil.getNormalAlphabet(value);
							String fieldValueNew = StringUtil.getNormalAlphabet(fieldValue);
							if (!valueNew.contains(fieldValueNew)) {
								hotelDTOList.remove(itemHotelDTO);
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
							String value = String.valueOf(itemField.get(itemHotelDTO));
							String valueNew = StringUtil.getNormalAlphabet(value);
							String fieldValueNew = StringUtil.getNormalAlphabet(fieldValue);
							if (!valueNew.contains(fieldValueNew)) {
								hotelDTOList.remove(itemHotelDTO);
								break;
							}
						} catch (Exception e) {
							
						}
					}
				}
				
			});
			if (hotelDTOList.size() <= 0) {
				break;
			}
		}
		
		return hotelDTOList;
	}

	@Override
	public List<HotelDTO> listHotelSort(Sort sort, List<HotelDTO> hotelDTOList) {
		Collections.sort(hotelDTOList, new Comparator<HotelDTO>() {
            @Override
            public int compare(HotelDTO hotelDTO1, HotelDTO hotelDTO2) {
            	int result = 0;
            	
            	// sort of account class
        		for (Field itemField : HotelDTO.class.getDeclaredFields()) {
        			itemField.setAccessible(true);
        			// field of account
    				if (sort.getSortBy().equals(itemField.getName())) {
    					try {
		            		String string1 = String.valueOf(itemField.get(hotelDTO1));
		            		String string2 = String.valueOf(itemField.get(hotelDTO2));
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
		            		String string1 = String.valueOf(itemField.get(hotelDTO1));
		            		String string2 = String.valueOf(itemField.get(hotelDTO2));
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
		
		return hotelDTOList;
	}

}
