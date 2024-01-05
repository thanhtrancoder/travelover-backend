package hcmute.kltn.Backend.model.tour.service.impl;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import hcmute.kltn.Backend.exception.CustomException;
import hcmute.kltn.Backend.model.account.dto.entity.Account;
import hcmute.kltn.Backend.model.account.service.IAccountDetailService;
import hcmute.kltn.Backend.model.base.BaseEntity;
import hcmute.kltn.Backend.model.base.extend.Address;
import hcmute.kltn.Backend.model.base.image.service.IImageService;
import hcmute.kltn.Backend.model.base.video.service.IVideoService;
import hcmute.kltn.Backend.model.commission.dto.CommissionDTO;
import hcmute.kltn.Backend.model.commission.service.ICommissionService;
import hcmute.kltn.Backend.model.generatorSequence.dto.GeneratorSequenceDTO;
import hcmute.kltn.Backend.model.generatorSequence.service.IGeneratorSequenceService;
import hcmute.kltn.Backend.model.hotel.dto.HotelSearch;
import hcmute.kltn.Backend.model.hotel.dto.RoomSearch;
import hcmute.kltn.Backend.model.hotel.service.IHotelService;
import hcmute.kltn.Backend.model.order.dto.OrderDTO;
import hcmute.kltn.Backend.model.order.service.IOrderService;
import hcmute.kltn.Backend.model.tour.dto.StatusUpdate;
import hcmute.kltn.Backend.model.tour.dto.TourClone;
import hcmute.kltn.Backend.model.tour.dto.TourCreate;
import hcmute.kltn.Backend.model.tour.dto.TourDTO;
import hcmute.kltn.Backend.model.tour.dto.TourFilter;
import hcmute.kltn.Backend.model.tour.dto.TourSearch;
import hcmute.kltn.Backend.model.tour.dto.TourSearchRes;
import hcmute.kltn.Backend.model.tour.dto.TourSearchRes2;
import hcmute.kltn.Backend.model.tour.dto.TourSort;
import hcmute.kltn.Backend.model.tour.dto.TourUpdate;
import hcmute.kltn.Backend.model.tour.dto.entity.Tour;
import hcmute.kltn.Backend.model.tour.dto.extend.Coach;
import hcmute.kltn.Backend.model.tour.dto.extend.CoachOption;
import hcmute.kltn.Backend.model.tour.dto.extend.Discount;
import hcmute.kltn.Backend.model.tour.dto.extend.Hotel;
import hcmute.kltn.Backend.model.tour.dto.extend.Hotel2;
import hcmute.kltn.Backend.model.tour.dto.extend.OneHotel;
import hcmute.kltn.Backend.model.tour.dto.extend.OneVehicle;
import hcmute.kltn.Backend.model.tour.dto.extend.Option;
import hcmute.kltn.Backend.model.tour.dto.extend.ReasonableTime;
import hcmute.kltn.Backend.model.tour.dto.extend.Reviewer;
import hcmute.kltn.Backend.model.tour.dto.extend.Room;
import hcmute.kltn.Backend.model.tour.dto.extend.Room2;
import hcmute.kltn.Backend.model.tour.dto.extend.Schedule;
import hcmute.kltn.Backend.model.tour.dto.extend.TourDetail;
import hcmute.kltn.Backend.model.tour.dto.extend.Vehicle;
import hcmute.kltn.Backend.model.tour.repository.TourRepository;
import hcmute.kltn.Backend.model.tour.service.ITourService;
import hcmute.kltn.Backend.util.LocalDateTimeUtil;
import hcmute.kltn.Backend.util.LocalDateUtil;
import hcmute.kltn.Backend.util.StringUtil;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelDTOSimple;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.Location;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.RoomSearchRes;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.service.IEHotelService;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.CoachSearch;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.CoachSearchRes;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.EVehicleDTOSimple;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.service.IEVehicleService;

@Service
public class TourService implements ITourService{
	@Autowired
	private TourRepository tourRepository;
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
	private IImageService iImageService;
	@Autowired
	private IVideoService iVideoService;
	@Autowired
	private IEHotelService IEHotelService;
	@Autowired
	private IEVehicleService iEVehicleService;
	@Autowired
	private ICommissionService iCommissionService;
	@Autowired
	private @Lazy IOrderService iOrderService;
	
	private List<TourDetail> deteilToList(String tourDetail) {
		List<TourDetail> tourDetailList = new ArrayList<>();
		
		String[] tourDetailSplit = tourDetail.split("\\n");
		if(tourDetailSplit.length % 2 != 0) {
			throw new CustomException("Tour detail: The number of titles and descriptions does not match");
		}
		
		for(int i = 0; i < tourDetailSplit.length; i += 2) {
			TourDetail tourDetailNew = new TourDetail();
			tourDetailNew.setTitle(tourDetailSplit[i]);
			tourDetailNew.setDescription(tourDetailSplit[i + 1]);
			tourDetailList.add(tourDetailNew);
		}
		
		return tourDetailList;
	}

    private String getCollectionName() {
        String collectionName = mongoTemplate.getCollectionName(Tour.class);
        return collectionName;
    }
    
    private void checkFieldCondition(Tour tour) {
		// check null
		if(tour.getTourTitle() == null || tour.getTourTitle().equals("")) {
			throw new CustomException("Title is not null");
		}
		if(tour.getNumberOfDay() <= 0 ) {
			throw new CustomException("Number Of Day must be greater than 0");
		}
		if(tour.getNumberOfNight() < 0 ) {
			throw new CustomException("Number Of Night must be greater than or equal 0");
		}
		if(tour.getAddress() == null) {
			throw new CustomException("Address is not null");
		}
//		if(tour.getTourDetailList() == null) {
//			throw new CustomException("Tour Detail List Time is not null");
//		}
		if(tour.getReasonableTime() == null) {
			throw new CustomException("Reasonable Time is not null");
		}
		
		// check unique
		if(tour.getTourId() == null || tour.getTourId().equals("")) {
			if(tourRepository.existsByTourTitle(tour.getTourTitle().trim())) {
				throw new CustomException("Title is already");
			}
		} else {
			Tour tourFind = tourRepository.findById(tour.getTourId()).get();
			List<Tour> titleList = tourRepository.findAllByTourTitle(tour.getTourTitle());
			for(Tour item : titleList) {
				if (item.getTourTitle() == tourFind.getTourTitle() && item.getTourId() != tourFind.getTourId()) {
					throw new CustomException("Title is already");
				}
			}
		}
	}
    
    private Tour create(Tour tour) {
    	// check field condition
		checkFieldCondition(tour);
    	
		// Set default value
		String tourId = iGeneratorSequenceService.genId(getCollectionName());
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDate dateNow = LocalDateUtil.getDateNow();
		
		tour.setTourId(tourId);
		tour.setStatus(true);
		tour.setCreatedBy(accountId);
		tour.setCreatedAt(dateNow);
		tour.setLastModifiedBy(accountId);
		tour.setLastModifiedAt(dateNow);
		
		// new date
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		tour.setCreatedAt2(currentDate);
		tour.setLastModifiedAt2(currentDate);
		
		// create tour
		Tour tourNew = new Tour();
		tourNew = tourRepository.save(tour);
		
		return tourNew;
	}
    
    private Tour update(Tour tour) {
    	// Check exists
		if (!tourRepository.existsById(tour.getTourId())) {
			throw new CustomException("Cannot find tour");
		}
		
		// check field condition
		checkFieldCondition(tour);
    	
    	// Set default value
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDate dateNow = LocalDateUtil.getDateNow();
		tour.setLastModifiedBy(accountId);
		tour.setLastModifiedAt(dateNow);
		
		// new date
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		tour.setLastModifiedAt2(currentDate);
		
		// update tour
		Tour tourNew = new Tour();
		tourNew = tourRepository.save(tour);
		
		return tourNew;
    }

	private Tour getDetail(String tourId) {
		// Check exists
		if (!tourRepository.existsById(tourId)) {
			throw new CustomException("Cannot find tour");
		}
		
		// Find tour
		Tour tour = tourRepository.findById(tourId).get();
		
		return tour;
	}

	private List<Tour> getAll() {
		// Find tour
		List<Tour> list = tourRepository.findAll();
		
		return list;
	}

	private void delete(String tourId) {
		// Check exists
		if (tourRepository.existsById(tourId)) {
			tourRepository.deleteById(tourId);
		}
	}
	
	private List<Tour> search(String keyword) {
		// init Tour List
		List<Tour> tourList = new ArrayList<>();
		
		if(keyword == null || keyword.trim().isEmpty()) {
			tourList = getAll();
		} else {
			// create list field name
			List<Criteria> criteriaList = new ArrayList<>();
			for(Field itemField : Tour.class.getDeclaredFields()) {
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
			tourList = mongoTemplate.find(query, Tour.class);
		}
		
		return tourList;
	}
	
	private List<Tour> search2(String keyword) {
		// init tour List
		List<Tour> tourList = new ArrayList<>();
		tourList = tourRepository.findAll();
		if (keyword != null) {
			keyword = keyword.trim();
		}

		if (keyword != null && !keyword.isEmpty()) {
			if (tourList != null) {
				List<Tour> tourListClone = new ArrayList<>();
				tourListClone.addAll(tourList);
				for (Tour itemTour : tourListClone) {
					String keywordNew = StringUtil.getNormalAlphabet(keyword);
					String fieldNew = StringUtil.getNormalAlphabet(getAllValue(itemTour));
					
					System.out.println("\nfieldNew = " + fieldNew + " ");
					
					if (!fieldNew.contains(keywordNew)) {
						tourList.remove(itemTour);
						if (tourList.size() <= 0) {
							break;
						}
					}
				}
			}
		}

		return tourList;
	}
	
	private String getAllValue(Tour tour) {
		String result = new String();
		
		// value of tour
		for (Field itemField : Tour.class.getDeclaredFields()) {
			itemField.setAccessible(true);
			try {
				// check type
				boolean isList = itemField.getType().isAssignableFrom(List.class);
				boolean isAddress = itemField.getType().isAssignableFrom(Address.class);
				boolean isReasonableTime = itemField.getType().isAssignableFrom(ReasonableTime.class);
				boolean isDiscount = itemField.getType().isAssignableFrom(Discount.class);
				Object object = itemField.get(tour);
				if (object != null && !isList && !isAddress && !isReasonableTime && !isDiscount) {
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
				Object object = itemField.get(tour);
				if (object != null) {
					result += String.valueOf(object) + " ";
				} 
			} catch (Exception e) {
				
			}
		}

		return result;
	}
	
	private TourDTO getTourDTO(Tour tour) {
		// mapping tourDTO
		TourDTO tourDTONew = new TourDTO();
		modelMapper.map(tour, tourDTONew);
		
		return tourDTONew;
	}
	
	private List<TourDTO> getTourDTOList(List<Tour> tourList) {
		List<TourDTO> tourDTOList = new ArrayList<>();
		for (Tour itemTour : tourList) {
			tourDTOList.add(getTourDTO(itemTour));
		}
		return tourDTOList;
	}

	@Override
	public TourDTO createTour(TourCreate tourCreate) {
		// mapping tour
		Tour tour = new Tour();
		modelMapper.map(tourCreate, tour);

		// set tour detail list
//		tour.setTourDetailList(deteilToList(tour.getTourDetail()));
		
		// check field condition
		checkFieldCondition(tour);

		// create tour
		Tour tourNew = new Tour();
		tourNew = create(tour);
		
		return getTourDTO(tourNew);
	}

	@Override
	public TourDTO updateTour(TourUpdate tourUpdate) {
		// get tour from database
		Tour tour = getDetail(tourUpdate.getTourId());
		
		// check thumbnail image and delete
		if ((tour.getThumbnailUrl() != null 
				&& !tour.getThumbnailUrl().equals(""))
				&& !tour.getThumbnailUrl().equals(tourUpdate.getThumbnailUrl())) {
			boolean checkDelete = iImageService.deleteImageByUrl(tour.getThumbnailUrl());
			if (checkDelete == false) {
				throw new CustomException("An error occurred during the processing of the old image");
			}
		}
		
		// check image update
		boolean checkExists = false;
		if (tour.getImage() != null) {
			for (String itemString : tour.getImage()) {
				checkExists = false;
				for (String itemStringDTO : tourUpdate.getImage()) {
					if (itemString.equals(itemStringDTO)) {
						checkExists = true;
						break;
					}
				}
				
				if (checkExists == false) {
					boolean checkDelete = false;
					checkDelete = iImageService.deleteImageByUrl(itemString);
					if (checkDelete == false) {
						throw new CustomException("An error occurred during the processing of the old image");
					}
				}
			}
		}
		
		// check video and delete
		if ((tour.getVideoUrl() != null 
				&& !tour.getVideoUrl().equals(""))
				&& !tour.getVideoUrl().equals(tourUpdate.getVideoUrl())) {
			iVideoService.deleteVideoByUrl(tour.getVideoUrl());
		}
		
		// check schedule image
		if (tour.getSchedule() != null) {
			for (Schedule itemSchedule : tour.getSchedule()) {
				if (tourUpdate.getSchedule() != null) {
					boolean checkImageExists = false;
					for (Schedule itemScheduleUpdate : tourUpdate.getSchedule()) {
						// delete old image in schedule
						if (itemSchedule.getImageUrl().equals(itemScheduleUpdate.getImageUrl()) ) {
							checkImageExists = true;
						}
					}
					if (checkImageExists == false) {
						boolean checkDelete = false;
						checkDelete = iImageService.deleteImageByUrl(itemSchedule.getImageUrl());
						if (checkDelete == false) {
							throw new CustomException("An error occurred during the processing of the old image");
						}
						break;
					}
				} else {
					boolean checkDelete = false;
					checkDelete = iImageService.deleteImageByUrl(itemSchedule.getImageUrl());
					if (checkDelete == false) {
						throw new CustomException("An error occurred during the processing of the old image");
					}
					break;
				}
			}
		}
		// mapping schedule
		tour.setSchedule(tourUpdate.getSchedule());
		
		LocalDate updateIsDiscount = null;
		if (tour.getDiscount() != null) {
			updateIsDiscount = tour.getDiscount().getUpdateIsDiscount();
		}
		
		// mapping tour
		modelMapper.map(tourUpdate, tour);
		
		// update image by handle
		tour.setImage(tourUpdate.getImage());
		
		// set tour detail list
//		tour.setTourDetailList(deteilToList(tour.getTourDetail()));
		
		// set Tour.Discount.UpdateIsDiscount
		
		if (tourUpdate.getDiscount() != null) {
			Discount discount = new Discount();
			discount = tourUpdate.getDiscount();
			discount.setUpdateIsDiscount(updateIsDiscount);
			tour.setDiscount(discount);
		}
		
		// update tour
		Tour tourNew = new Tour();
		tourNew = update(tour);
		
		return getTourDTO(tourNew);
	}

	@Override
	public TourDTO getDetailTour(String tourId) {
		Tour tour = getDetail(tourId);

		return getTourDTO(tour);
	}

	@Override
	public List<TourDTO> getAllTour() {
		List<Tour> tourList = new ArrayList<>(getAll());

		return getTourDTOList(tourList);
	}
	
	@Override
	public List<TourSearchRes> searchTour(TourSearch tourSearch) {
		// search with keyword
		List<Tour> tourList = new ArrayList<>();
		List<Tour> tourListClone = new ArrayList<>();
		List<TourSearchRes> tourSearchResList = new ArrayList<>(); 
		int totalPriceNotDiscount = 0;
		int totalPrice = 0;
		
		if(tourSearch.getKeyword() != null) {
			tourList = search(tourSearch.getKeyword());
			
			// filter with status = true
			tourListClone.clear();
			tourListClone.addAll(tourList);
			for (Tour itemTour : tourListClone) {
				if (itemTour.getStatus() == false) {
					tourList.remove(itemTour);
					if (tourList.size() <= 0) {
						break;
					}
				}
			}
			
		} else {
			tourList = tourRepository.findAllByStatus(true);
		}
		
		// search with province
		if(tourSearch.getProvince() != null && !tourSearch.getProvince().equals("")) {
			tourListClone.clear();
			tourListClone.addAll(tourList);
			for(Tour itemTour : tourListClone) {
				if(!itemTour.getAddress().getProvince().equals(tourSearch.getProvince())) {
					tourList.remove(itemTour);
					if(tourList.size() == 0) {
						break;
					}
				}
			}
		}
		
		// search with district
		if(tourSearch.getDistrict() != null && !tourSearch.getDistrict().equals("")) {
			tourListClone.clear();
			tourListClone.addAll(tourList);
			for(Tour itemTour : tourListClone) {
				if(!itemTour.getAddress().getDistrict().equals(tourSearch.getDistrict())) {
					tourList.remove(itemTour);
					if(tourList.size() == 0) {
						break;
					}
				}
			}
		}
		
		// search with commune
		if(tourSearch.getCommune() != null && !tourSearch.getCommune().equals("")) {
			tourListClone.clear();
			tourListClone.addAll(tourList);
			for(Tour itemTour : tourListClone) {
				if(!itemTour.getAddress().getCommune().equals(tourSearch.getCommune())) {
					tourList.remove(itemTour);
					if(tourList.size() == 0) {
						break;
					}
				}
			}
		}
		
		// search with number of day
		
		if(tourSearch.getNumberOfDay() != null && !tourSearch.getNumberOfDay().equals("")) {
			String[] noDaySplit = tourSearch.getNumberOfDay().split("-");
			int startDay = Integer.parseInt(noDaySplit[0]);
			int endDay = Integer.parseInt(noDaySplit[1]);
			tourListClone.clear();
			tourListClone.addAll(tourList);
			for(Tour itemTour : tourListClone) {
				if(itemTour.getNumberOfDay() < startDay || itemTour.getNumberOfDay() > endDay) {
					tourList.remove(itemTour);
					if(tourList.size() == 0) {
						break;
					}
				}
			}
		}
		
		// search with start date
		
		// search with number of people
		
		for(Tour itemTour : tourList) {
			totalPrice = 0;
			TourSearchRes tourSearchRes = new TourSearchRes();
			tourSearchRes.setTour(itemTour);
			totalPrice += (tourSearch.getNumberOfAdult() * itemTour.getPriceOfAdult()) 
					+ (tourSearch.getNumberOfChildren() * itemTour.getPriceOfChildren());
			
			if(tourSearch.getStartDate() != null && tourSearch.getNumberOfAdult() > 0) {
				// search hotel
				HotelSearch hotelSearch = new HotelSearch();
				hotelSearch.setProvince(itemTour.getAddress().getProvince());
				hotelSearch.setDistrict(itemTour.getAddress().getDistrict());
				List<hcmute.kltn.Backend.model.hotel.dto.HotelDTO> hotelDTOList = iHotelService.searchHotel(hotelSearch);
				
				// search room in hotel
				for(hcmute.kltn.Backend.model.hotel.dto.HotelDTO itemHotel : hotelDTOList) {
					RoomSearch roomSearch = new RoomSearch();
					roomSearch.setEHotelId(itemHotel.getEHotelId());
					roomSearch.setStartDate(tourSearch.getStartDate());
					roomSearch.setEndDate(tourSearch.getStartDate().plusDays(itemTour.getNumberOfDay()));
					roomSearch.setNumberOfAdult(tourSearch.getNumberOfAdult());
					roomSearch.setNumberOfChildren(tourSearch.getNumberOfChildren());
					roomSearch.setNumberOfRoom(tourSearch.getNumberOfRoom());
					
					List<hcmute.kltn.Backend.model.hotel.dto.extend.Room> roomList = new ArrayList<>();
					try {
						roomList = iHotelService.searchRoom(roomSearch);
						
						
					} catch (Exception e) {
						
					}
					
					if(roomList.size() > 0) {
						// mapping hotel
						Hotel hotel = new Hotel();
						modelMapper.map(itemHotel, hotel);
						
						// mapping room
						List<Room> roomListRes = new ArrayList<>();
						for(hcmute.kltn.Backend.model.hotel.dto.extend.Room itemRoom : roomList) {
							Room room = new Room();
							modelMapper.map(itemRoom, room);
							roomListRes.add(room);
							totalPrice += room.getPrice() * itemTour.getNumberOfDay();
						}

						hotel.setRoom(roomListRes);
						
						tourSearchRes.setHotel(hotel);

						break;
					}
				}
			}
			totalPriceNotDiscount = totalPrice;
			if (itemTour.getDiscount().getIsDiscount() == true) {
				totalPrice = totalPriceNotDiscount * (100 - itemTour.getDiscount().getDiscountValue()) / 100;
			}
			
			tourSearchRes.setTotalPriceNotDiscount(totalPriceNotDiscount);
			tourSearchRes.setTotalPrice(totalPrice);
			
			if (tourSearchRes.getHotel() != null) {
				tourSearchResList.add(tourSearchRes);
			}
			
		}

		return tourSearchResList;
	}

	@Override
	public List<TourSearchRes> searchFilter(TourFilter tourFilter, List<TourSearchRes> tourSearchResList) {
		try {
			// price filter
			int minPrice = Integer.valueOf(tourFilter.getMinPrice());
			int maxPrice = Integer.valueOf(tourFilter.getMaxPrice());
			if (minPrice >= 0 && maxPrice >= minPrice) {
				List<TourSearchRes> tourSearchResListClone = new ArrayList<>();
				tourSearchResListClone.addAll(tourSearchResList);
				for (TourSearchRes itemTourSearchRes : tourSearchResListClone) {
					if (itemTourSearchRes.getTotalPrice() < minPrice 
							|| itemTourSearchRes.getTotalPrice() > maxPrice) {
						tourSearchResList.remove(itemTourSearchRes);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Tour search filter min max: " + e.getMessage());
		}
		
		try {
			// rating filter
			int ratingFilter = Integer.valueOf(tourFilter.getRatingFilter());
			if (ratingFilter >= 1 && ratingFilter <= 5) {
				List<TourSearchRes> tourSearchResListClone = new ArrayList<>();
				tourSearchResListClone.addAll(tourSearchResList);
				for (TourSearchRes itemTourSearchRes : tourSearchResListClone) {
					if (itemTourSearchRes.getTour().getRate() < ratingFilter) {
						tourSearchResList.remove(itemTourSearchRes);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Tour search filter rating: " + e.getMessage());
		}
		
		return tourSearchResList;
	}

	@Override
	public List<TourSearchRes> searchSort(TourSort tourSort, List<TourSearchRes> tourSearchResList) {
		// sort by popular
		try {
			if (tourSort.getSortBy().equals("popular")) {
				Collections.sort(tourSearchResList, new Comparator<TourSearchRes>() {
		            @Override
		            public int compare(TourSearchRes e1, TourSearchRes e2) {
		            	int result = 0;
		            	if (tourSort.getOrder().equals("asc")) {
		            		result = Integer.compare(e1.getTour().getNumberOfOrdered(), e2.getTour().getNumberOfOrdered());
		            	} else if (tourSort.getOrder().equals("desc")) {
		            		result = Integer.compare(e2.getTour().getNumberOfOrdered(), e1.getTour().getNumberOfOrdered());
		            	} else {
		            		result = 0;
		            	}
		                return result;
		            }
		        });
			}
		} catch (Exception e) {
			System.out.println("Tour search sort by popular: " + e.getMessage());
		}
		
		// sort by price
		try {
			if (tourSort.getSortBy().equals("price")) {
				Collections.sort(tourSearchResList, new Comparator<TourSearchRes>() {
		            @Override
		            public int compare(TourSearchRes e1, TourSearchRes e2) {
		            	int result = 0;
		            	if (tourSort.getOrder().equals("asc")) {
		            		result = Integer.compare(e1.getTotalPrice(), e2.getTotalPrice());
		            	} else if (tourSort.getOrder().equals("desc")) {
		            		result = Integer.compare(e2.getTotalPrice(), e1.getTotalPrice());
		            	} else {
		            		result = 0;
		            	}
		                return result;
		            }
		        });
			}
		} catch (Exception e) {
			System.out.println("Tour search sort by price: " + e.getMessage());
		}
		
		// sort by promotion
		try {	
//		if (tourSort.getSortBy().equals("promotion")) {
//			Collections.sort(tourSearchResList, new Comparator<TourSearchRes>() {
//	            @Override
//	            public int compare(TourSearchRes e1, TourSearchRes e2) {
//	                int result = Integer.compare(e1.getTour().getNumberOfOrdered(), e2.getTour().getNumberOfOrdered());
////		                if (result == 0) {
////		                    result = e1.field2.compareTo(e2.field2);
////		                }
//	                return result;
//	            }
//	        });
//		}	
		} catch (Exception e) {
			System.out.println("Tour search sort by promotion: " + e.getMessage());
		}

		return tourSearchResList;
	}

	@Override
	public void updateIsDiscount() {
		LocalDate dateNow = LocalDateUtil.getDateNow();
		LocalDate yesterday = dateNow.plusDays(-1);
		
		Tour tour = new Tour();
		tour = tourRepository.findFirstBy();
		
		boolean update = false;
		if (tour.getDiscount() == null) {
			update = true;
		} else {
			try {
				if (!tour.getDiscount().getUpdateIsDiscount().equals(dateNow)) {
					update = true;
				}
			} catch (Exception e) {
				update = true;
			}
			
		}	

		// update
		if (update == true) {
			List<Tour> tourList = new ArrayList<>();
			tourList = getAll();
			for (Tour itemTour : tourList) {
				if (itemTour.getDiscount() == null) {
					Discount discount = new Discount();
					discount.setStartDate(null);
					discount.setEndDate(null);
					discount.setDiscountType(null);
					discount.setDiscountValue(0);
					discount.setAuto(false);
					discount.setIsDiscount(false);
					discount.setUpdateIsDiscount(yesterday);
					
					itemTour.setDiscount(discount);
				}
				
				Discount discountNew = new Discount();
				discountNew = itemTour.getDiscount();
				try {
					if ((discountNew.getStartDate().isBefore(dateNow) || discountNew.getStartDate().equals(dateNow))
							&& (discountNew.getEndDate().isAfter(dateNow) || discountNew.getEndDate().equals(dateNow))
							) {
						if (itemTour.getDiscount().getAuto() == true) {
							discountNew.setIsDiscount(true);
						}
					} else {
						discountNew.setIsDiscount(false);
					}	
					
//					MonthDay startDate = MonthDay.from(discountNew.getStartDate());
//					MonthDay endDate = MonthDay.from(discountNew.getEndDate());
//					MonthDay currentDate = MonthDay.from(dateNow);
//					
//					if ((startDate.isBefore(currentDate) || startDate.equals(currentDate))
//							&& (endDate.isAfter(currentDate) || endDate.equals(currentDate))
//							) {
//						if (itemTour.getDiscount().getAuto() == true) {
//							discountNew.setIsDiscount(true);
//						}
//					} else {
//						discountNew.setIsDiscount(false);
//					}
				} catch (Exception e) {
					discountNew.setIsDiscount(false);
				}
				
				discountNew.setUpdateIsDiscount(dateNow);
				
				itemTour.setDiscount(discountNew);
				tourRepository.save(itemTour);
			}
			System.out.println("Update isDiscount successfully");
		}
	}

	@Override
	public void updateIsDiscountNoCheck() {
		LocalDate dateNow = LocalDateUtil.getDateNow();
		LocalDate yesterday = dateNow.plusDays(-1);
		
		List<Tour> tourList = new ArrayList<>();
		tourList = getAll();
		for (Tour itemTour : tourList) {
			if (itemTour.getDiscount() == null) {
				Discount discount = new Discount();
				discount.setStartDate(null);
				discount.setEndDate(null);
				discount.setDiscountType(null);
				discount.setDiscountValue(0);
				discount.setAuto(false);
				discount.setIsDiscount(false);
				discount.setUpdateIsDiscount(yesterday);
				
				itemTour.setDiscount(discount);
			}
			
			Discount discountNew = new Discount();
			discountNew = itemTour.getDiscount();
			
			discountNew.setUpdateIsDiscount(dateNow);
			
			try {
				MonthDay startDate = MonthDay.from(discountNew.getStartDate());
				MonthDay endDate = MonthDay.from(discountNew.getEndDate());
				MonthDay currentDate = MonthDay.from(dateNow);
				
				if ((startDate.isBefore(currentDate) || startDate.equals(currentDate))
						&& (endDate.isAfter(currentDate) || endDate.equals(currentDate))
						) {
					if (itemTour.getDiscount().getAuto() == true) {
						discountNew.setIsDiscount(true);
					}
				} else {
					discountNew.setIsDiscount(false);
				}
			} catch (Exception e) {
				discountNew.setIsDiscount(false);
			}
			
			itemTour.setDiscount(discountNew);
			tourRepository.save(itemTour);
			
		}
		
		System.out.println("Update isDiscount successfully");
	}

	@Override
	public List<TourSearchRes> getAllDiscountTour() {
		// get all tour
		List<Tour> tourList = new ArrayList<>();
		tourList = tourRepository.findAllByStatus(true);
		
		List<Tour> tourListClone = new ArrayList<>();
		
		// get tour is discount
		tourListClone.addAll(tourList);
		for (Tour itemTour : tourListClone) {
			if (itemTour.getDiscount() == null) {
				tourList.remove(itemTour);
				if (tourList.size() <= 0) {
					break;
				}
			}else if (itemTour.getDiscount().getIsDiscount() == false) {
				tourList.remove(itemTour);
				if (tourList.size() <= 0) {
					break;
				}
			}
		}
		
		// sort list tour by discount value
		Collections.sort(tourList, new Comparator<Tour>() {
            @Override
            public int compare(Tour tour1, Tour tour2) {
            	int result = Integer.compare(tour1.getDiscount().getDiscountValue(), tour2.getDiscount().getDiscountValue());
                return result;
            }
        });
		
		// search with 1 people
		List<TourSearchRes> tourSearchResList = new ArrayList<>();
		LocalDate today = LocalDateUtil.getDateNow();
		LocalDate tomorrow = today.plusDays(1);
		
		for(Tour itemTour : tourList) {
			TourSearch tourSearch = new TourSearch();
			tourSearch.setKeyword(itemTour.getTourId());
			tourSearch.setStartDate(tomorrow);
			tourSearch.setNumberOfAdult(1);
			tourSearch.setNumberOfRoom(1);
			
			List<TourSearchRes2> oneTourSearchResList = new ArrayList<>();
			oneTourSearchResList.addAll(searchTour2(tourSearch));
			
			if (!oneTourSearchResList.isEmpty()) {
				// set tour
				TourSearchRes tourSearchRes = new TourSearchRes();
				tourSearchRes.setTour(oneTourSearchResList.get(0).getTour());
				
				// set hotel
				OneHotel hotel2 = new OneHotel();
				modelMapper.map(oneTourSearchResList.get(0).getHotelList().get(0), hotel2);
				hotel2.setRoomList(oneTourSearchResList.get(0).getHotelList().get(0).getOptionList().get(0).getRoomList());
				Option option = new Option();
				modelMapper.map(oneTourSearchResList.get(0).getHotelList().get(0).getOptionList().get(0), option);
				tourSearchRes.setHotel2(hotel2);
				
				// set vehicle
				OneVehicle vehicle = new OneVehicle();
				modelMapper.map(oneTourSearchResList.get(0).getVehicleList().get(0), vehicle);
				vehicle.setCoach(oneTourSearchResList.get(0).getVehicleList().get(0).getOptionList().get(0).getCoachList());
				int vehiclePriceNotDiscount = oneTourSearchResList.get(0).getVehicleList().get(0).getOptionList().get(0).getTotalPriceNotDiscount();
				int vehiclePrice = oneTourSearchResList.get(0).getVehicleList().get(0).getOptionList().get(0).getTotalPrice();
				tourSearchRes.setVehicle(vehicle);
				
				// set price
				tourSearchRes.setTotalPriceNotDiscount(
						option.getTotalPriceNotDiscount() 
						+ oneTourSearchResList.get(0).getTourPriceNotDiscount()
						+ vehiclePriceNotDiscount);
				tourSearchRes.setTotalPrice(
						option.getTotalPrice() 
						+ oneTourSearchResList.get(0).getTourPrice()
						+ vehiclePrice);
				
				tourSearchResList.add(tourSearchRes);
			}
		}

		// get 8 item from tour list
		if (tourSearchResList.size() > 8) {
			List<TourSearchRes> tourSearchResListClone = new ArrayList<>();
			tourSearchResListClone.addAll(tourSearchResList);
			tourListClone.addAll(tourList);
			for (TourSearchRes itemTourSearchRes : tourSearchResListClone) {
				int index = tourSearchResList.indexOf(itemTourSearchRes);
				if (index >= 8) {
					tourSearchResList.remove(itemTourSearchRes);
					if (tourSearchResList.size() <= 0) {
						break;
					}
				}
			}
		}

		
		return tourSearchResList;
	}

	@Override
	public TourDTO updateStatus(StatusUpdate statusUpdate) {
		Tour tour = new Tour();
		tour = getDetail(statusUpdate.getTourId());
		
		// check current status
		if (tour.getStatus() == statusUpdate.getStatus()) {
			return getTourDTO(tour);
		}
		
		tour.setStatus(statusUpdate.getStatus());
		
		Tour tourNew = new Tour();
		tourNew = update(tour);
		
		return getTourDTO(tourNew);
	}

	@Override
	public void updateNumberOfOrdered(String tourId) {
		Tour tour = new Tour();
		tour = getDetail(tourId);
		
		int numberOfOrdered = tour.getNumberOfOrdered();
		tour.setNumberOfOrdered(numberOfOrdered + 1);
		
		tourRepository.save(tour);
	}

	@Override
	public List<TourDTO> listTourSearch(String keyword) {
		List<Tour> tourList = new ArrayList<>();
		tourList = search2(keyword);
		
		return getTourDTOList(tourList);
	}
	
	@Override
	public List<TourDTO> listTourSort(TourSort tourSort, List<TourDTO> tourDTOList) {
		Collections.sort(tourDTOList, new Comparator<TourDTO>() {
            @Override
            public int compare(TourDTO tourDTO1, TourDTO tourDTO2) {
            	int result = 0;
            	
				for (Field itemField : TourDTO.class.getDeclaredFields()) {
					itemField.setAccessible(true);
					// field of tour
					if (tourSort.getSortBy().equals(itemField.getName())) {
						try {
							String string1 = String.valueOf(itemField.get(tourDTO1));
							String string2 = String.valueOf(itemField.get(tourDTO2));
							result = string1.compareTo(string2);
						} catch (Exception e) {
							result = 0;
						}

						break;
					}
    				
    				
    				
    				// field of discount of tour
    				if (itemField.getType().isAssignableFrom(Discount.class)) {
    					try {
    						Discount discount1 = new Discount();
        					discount1 = (Discount) itemField.get(tourDTO1);
        					Discount discount2 = new Discount();
        					discount2 = (Discount) itemField.get(tourDTO2);
        					for (Field itemDiscountField : Discount.class.getDeclaredFields()) {
        						itemDiscountField.setAccessible(true);
        	        			// field of discount
        	    				if (tourSort.getSortBy().equals(itemDiscountField.getName())) {
        	    					try {
        			            		String string1 = String.valueOf(itemDiscountField.get(discount1));
        			            		String string2 = String.valueOf(itemDiscountField.get(discount2));
        			            		result = string1.compareTo(string2);
        			            	} catch (Exception e) {
        			            		result = 0;
        							}
        	    					
        	    					break;
        	    				}
        	    			}
    					} catch (Exception e) {
    						result = 0;
						}
    				}
    			}
				
        		// sort of base class
        		for (Field itemField : BaseEntity.class.getDeclaredFields()) {
        			itemField.setAccessible(true);
        			// field of base
    				if (tourSort.getSortBy().equals(itemField.getName())) {
    					try {
		            		String string1 = String.valueOf(itemField.get(tourDTO1));
		            		String string2 = String.valueOf(itemField.get(tourDTO2));
		            		result = string1.compareTo(string2);
		            	} catch (Exception e) {
		            		result = 0;
						}
    					
    					break;
    				}
    			}
        		
        		if (tourSort.getOrder().equals("asc")) {

            	} else if (tourSort.getOrder().equals("desc")) {
            		result = -result;
            	} else {
            		result = 0;
            	}
            	
            	return result;
            }
        });
		
		return tourDTOList;
	}

	@Override
	public List<TourDTO> listTourFilter(HashMap<String, String> tourFilter, List<TourDTO> tourDTOList) {
		List<TourDTO> tourDTOListClone = new ArrayList<>();
		tourDTOListClone.addAll(tourDTOList);
		for (TourDTO itemTourDTO : tourDTOListClone) {
			tourFilter.forEach((fieldName, fieldValue) -> {
				// filter of tour
				for (Field itemField : TourDTO.class.getDeclaredFields()) {
					itemField.setAccessible(true);
					// field of tour
					if (itemField.getName().equals(fieldName)) {
						try {
							String value = String.valueOf(itemField.get(itemTourDTO));
							String valueNew = StringUtil.getNormalAlphabet(value);
							String fieldValueNew = StringUtil.getNormalAlphabet(fieldValue);
							if (!valueNew.contains(fieldValueNew)) {
								tourDTOList.remove(itemTourDTO);
								break;
							}
						} catch (Exception e) {
							
						}
					}
					
					// field of discount of tour
					if (itemField.getType().isAssignableFrom(Discount.class)) {
						try {
							Discount discount = new Discount();
							discount = (Discount) itemField.get(itemTourDTO);
							for (Field itemDiscountField : Discount.class.getDeclaredFields()) {
								itemDiscountField.setAccessible(true);
								// filter of discount
								if (itemDiscountField.getName().equals(fieldName)) {
									try {
										String value = String.valueOf(itemDiscountField.get(discount));
										String valueNew = StringUtil.getNormalAlphabet(value);
										String fieldValueNew = StringUtil.getNormalAlphabet(fieldValue);
										if (!valueNew.contains(fieldValueNew)) {
											tourDTOList.remove(itemTourDTO);
											break;
										}
									} catch (Exception e) {
										
									}
								}
								
								// filter of discount
								if (itemField.getType().isAssignableFrom(Discount.class)) {
									
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
						
					}
				}
				
				// filter of base class
				for (Field itemField : BaseEntity.class.getDeclaredFields()) {
					itemField.setAccessible(true);
					// field of base
					if (itemField.getName().equals(fieldName)) {
						try {
							String value = String.valueOf(itemField.get(itemTourDTO));
							String valueNew = StringUtil.getNormalAlphabet(value);
							String fieldValueNew = StringUtil.getNormalAlphabet(fieldValue);
							if (!valueNew.contains(fieldValueNew)) {
								tourDTOList.remove(itemTourDTO);
								break;
							}
						} catch (Exception e) {
							
						}
					}
				}
				
			});
			if (tourDTOList.size() <= 0) {
				break;
			}
		}
		
		return tourDTOList;
	}

	@Override
	public void autoUpdateId() {
		List<Tour> tourList = new ArrayList<>();
		tourList.addAll(tourRepository.findAll());

		// update index max into genSeq
		GeneratorSequenceDTO GenSeqDTO = new GeneratorSequenceDTO();
		List<GeneratorSequenceDTO> genSeqDTOList = new ArrayList<>();
		genSeqDTOList.addAll(iGeneratorSequenceService.getAllGenSeq());
		for (GeneratorSequenceDTO itemGenSeqDTO : genSeqDTOList) {
			if (itemGenSeqDTO.getCollectionName().equals(getCollectionName())) {
				// get index max
				int indexMax = 0;
				for (Tour itemTour : tourList) {
					if (itemTour.getTourId().startsWith(itemGenSeqDTO.getPrefix())) {
						int index = 0;
						try {
							index = Integer.valueOf(itemTour.getTourId().substring(2, itemTour.getTourId().length()));
						} catch (Exception e) {
							index = -1;
						}
						
						if (indexMax < index) {
							indexMax = index;
						}
					}
				}
				// update index max
				modelMapper.map(itemGenSeqDTO, GenSeqDTO);
				GenSeqDTO.setNumber(indexMax);
				iGeneratorSequenceService.updateGenSeq(GenSeqDTO);
			}
		}
		
		// clone with true id
		for (Tour itemTour : tourList) {
			if(!itemTour.getTourId().startsWith(GenSeqDTO.getPrefix())) {
				Tour tour = new Tour();
				modelMapper.map(itemTour, tour);
				String tourId = iGeneratorSequenceService.genId(getCollectionName());
				tour.setTourId(tourId);
				tourRepository.save(tour);
			} else {
				try {
					int index = Integer.valueOf(itemTour.getTourId().substring(2, itemTour.getTourId().length()));
				} catch (Exception e) {
					Tour tour = new Tour();
					modelMapper.map(itemTour, tour);
					String tourId = iGeneratorSequenceService.genId(getCollectionName());
					tour.setTourId(tourId);
					tourRepository.save(tour);
				}
			}
		}
		
		// remove id old
		for (Tour itemTour : tourList) {
			if(!itemTour.getTourId().startsWith(GenSeqDTO.getPrefix())) {
				tourRepository.delete(itemTour);
			} else {
				try {
					int index = Integer.valueOf(itemTour.getTourId().substring(2, itemTour.getTourId().length()));
				} catch (Exception e) {
					tourRepository.delete(itemTour);
				}
			}
		}
	}

	@Override
	public TourDTO cloneTour(TourClone tourClone) {
		System.out.println("tourId = " + tourClone.getTourId());
		Tour tour = new Tour();
		tour = getDetail(tourClone.getTourId());
		
		String tourIdNew = iGeneratorSequenceService.genId(getCollectionName());
		tour.setTourId(tourIdNew);
		tourRepository.save(tour);
		
		return getTourDTO(tour);
	}

	@Override
	public List<TourSearchRes2> searchTour2(TourSearch tourSearch) {
		// search with keyword
		List<Tour> tourList = new ArrayList<>();
		List<Tour> tourListClone = new ArrayList<>();
		int totalPriceNotDiscount = 0;
		int totalPrice = 0;
		double priceTemp = 0;
		
		if(tourSearch.getKeyword() != null) {
			tourList = search(tourSearch.getKeyword());
			
			// filter with status = true
			tourListClone.clear();
			tourListClone.addAll(tourList);
			for (Tour itemTour : tourListClone) {
				if (itemTour.getStatus() == false) {
					tourList.remove(itemTour);
					if (tourList.size() <= 0) {
						break;
					}
				}
			}
			
		} else {
			tourList = tourRepository.findAllByStatus(true);
		}
		
		// search with province
		if(tourSearch.getProvince() != null && !tourSearch.getProvince().equals("")) {
			tourListClone.clear();
			tourListClone.addAll(tourList);
			for(Tour itemTour : tourListClone) {
				if(!itemTour.getAddress().getProvince().equals(tourSearch.getProvince())) {
					tourList.remove(itemTour);
					if(tourList.size() == 0) {
						break;
					}
				}
			}
		}
		
		// search with district
		if(tourSearch.getDistrict() != null && !tourSearch.getDistrict().equals("")) {
			tourListClone.clear();
			tourListClone.addAll(tourList);
			for(Tour itemTour : tourListClone) {
				if(!itemTour.getAddress().getDistrict().equals(tourSearch.getDistrict())) {
					tourList.remove(itemTour);
					if(tourList.size() == 0) {
						break;
					}
				}
			}
		}
		
		// search with commune
		if(tourSearch.getCommune() != null && !tourSearch.getCommune().equals("")) {
			tourListClone.clear();
			tourListClone.addAll(tourList);
			for(Tour itemTour : tourListClone) {
				if(!itemTour.getAddress().getCommune().equals(tourSearch.getCommune())) {
					tourList.remove(itemTour);
					if(tourList.size() == 0) {
						break;
					}
				}
			}
		}
		
		// search with number of day
		
		if(tourSearch.getNumberOfDay() != null && !tourSearch.getNumberOfDay().equals("")) {
			String[] noDaySplit = tourSearch.getNumberOfDay().split("-");
			int startDay = Integer.parseInt(noDaySplit[0]);
			int endDay = Integer.parseInt(noDaySplit[1]);
			tourListClone.clear();
			tourListClone.addAll(tourList);
			for(Tour itemTour : tourListClone) {
				if(itemTour.getNumberOfDay() < startDay || itemTour.getNumberOfDay() > endDay) {
					tourList.remove(itemTour);
					if(tourList.size() == 0) {
						break;
					}
				}
			}
		}
		
		// check daily tour limit
		List<OrderDTO> orderDTOList = new ArrayList<>();
		orderDTOList.addAll(iOrderService.getAllOrder2());	
		List<OrderDTO> orderDTOListClone = new ArrayList<>();
		orderDTOListClone.addAll(orderDTOList);
		for (OrderDTO itemOrderDTO : orderDTOListClone) {
			if (
					itemOrderDTO.getOrderStatus().equals("canceled") 
					|| itemOrderDTO.getOrderStatus().equals("finished")
					|| itemOrderDTO.getStatus() == false) {
				orderDTOList.remove(itemOrderDTO);
				if (orderDTOList.size() <= 0) {
					break;
				}
			}
		}
		
		tourListClone.clear();
		tourListClone.addAll(tourList);
		for(Tour itemTour : tourListClone) {
			LocalDate currentDate = tourSearch.getStartDate();

			for (int i = 1; i <= itemTour.getNumberOfDay(); i++) {
				int numberOfTour = 0;
				for (OrderDTO itemOrderDTO : orderDTOList) {
					if (
							itemOrderDTO.getOrderDetail().getTourId().equals(itemTour.getTourId())
							&& (itemOrderDTO.getStartDate().isBefore(currentDate) || itemOrderDTO.getStartDate().equals(currentDate))
							&& (itemOrderDTO.getEndDate().isAfter(currentDate) || itemOrderDTO.getEndDate().equals(currentDate))) {
						numberOfTour += 1;
					}
				}
				if (numberOfTour >= itemTour.getDailyTourLimit()) {
					tourList.remove(itemTour);
					if (tourList.size() <= 0) {
						break;
					}
				}
				currentDate.plusDays(1);
			}
		}
		
		// get commission
		CommissionDTO commissionDTO = new CommissionDTO();
		commissionDTO = iCommissionService.getCurrentCommission();
		
		// create result
		List<TourSearchRes2> tourSearchRes2List = new ArrayList<>(); 
		for(Tour itemTour : tourList) {
			// create 1 tourSearchRes2
			TourSearchRes2 tourSearchRes2 = new TourSearchRes2();
			
			// create tour
			Tour tour = new Tour();
			modelMapper.map(itemTour, tour);
			tourSearchRes2.setTour(tour);
			totalPrice = (tourSearch.getNumberOfAdult() * itemTour.getPriceOfAdult())
							+ (tourSearch.getNumberOfChildren() * itemTour.getPriceOfChildren());
			

			priceTemp = (double)totalPrice * (100 + commissionDTO.getRate()) / 100;
			totalPrice = (int)priceTemp;
			
			tourSearchRes2.setTourPriceNotDiscount(totalPrice);
			if (itemTour.getDiscount().getIsDiscount() == true) {
				priceTemp = (double)totalPrice * (100 - itemTour.getDiscount().getDiscountValue()) / 100;
				tourSearchRes2.setTourPrice((int)priceTemp);
			} else {
				tourSearchRes2.setTourPrice(totalPrice);
			};
			
			// create hotel2 list
			List<Hotel2> hotel2List = new ArrayList<>();
			// search ehotel location
			Location location = new Location();
			location.setProvince(itemTour.getAddress().getProvince());
			location.setDistrict(itemTour.getAddress().getDistrict());
			List<EHotelDTOSimple> eHotelList = new ArrayList<>();
			eHotelList.addAll(IEHotelService.searchEHotelByLocation(location));
			for (EHotelDTOSimple itemEHotelDTOSimple : eHotelList) {
				// create hotel2
				Hotel2 hotel2 = new Hotel2();
				modelMapper.map(itemEHotelDTOSimple, hotel2);
				
				// create optionRoom List
				List<Option> optionRoomList = new ArrayList<>();
				// search room2
				hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.RoomSearch roomSearch 
				= new hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.RoomSearch();
				roomSearch.setEHotelId(itemEHotelDTOSimple.getEHotelId());
				roomSearch.setStartDate(tourSearch.getStartDate());
				roomSearch.setEndDate(tourSearch.getStartDate().plusDays(itemTour.getNumberOfDay() - 1));
				roomSearch.setNumberOfAdult(tourSearch.getNumberOfAdult());
				roomSearch.setNumberOfChildren(tourSearch.getNumberOfChildren());
				roomSearch.setNumberOfRoom(tourSearch.getNumberOfRoom());
				
				List<RoomSearchRes> RoomSearchResList = new ArrayList<>();
				try {
					RoomSearchResList.addAll(IEHotelService.searchRoom2(roomSearch));
				} catch (Exception e) {
					
				}
				if (!RoomSearchResList.isEmpty()) {
					for (RoomSearchRes itemRoomSearchRes : RoomSearchResList) {
						// create optionRoom
						Option option = new Option();
						// create room2 list
						List<Room2> room2List = new ArrayList<>();
						for (hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Room2 itemRoom2 : itemRoomSearchRes.getRoom()) {
							Room2 room2 = new Room2();
							modelMapper.map(itemRoom2, room2);
							room2List.add(room2);
						}
						option.setRoomList(room2List);
						
						priceTemp = (double)itemRoomSearchRes.getTotalPrice() * (100 + commissionDTO.getRate()) / 100;
						totalPrice = (int)priceTemp;
						
						option.setTotalPriceNotDiscount(totalPrice);
						if (itemTour.getDiscount().getIsDiscount() == true) {
							priceTemp = (double)totalPrice * (100 - itemTour.getDiscount().getDiscountValue()) / 100;
							option.setTotalPrice((int)priceTemp);
						} else {
							option.setTotalPrice(totalPrice);
						}
						optionRoomList.add(option);
					}
				}

				hotel2.setOptionList(optionRoomList);
				
				if (!optionRoomList.isEmpty()) {
					hotel2List.add(hotel2);
				}
				
			}
			// sort hotel2 list by price
			Collections.sort(hotel2List, new Comparator<Hotel2>() {
	            @Override
	            public int compare(Hotel2 hotel1, Hotel2 hotel2) {
	            	int result = Integer.compare(hotel1.getOptionList().get(0).getTotalPrice(), hotel2.getOptionList().get(0).getTotalPrice());
	                return result;
	            }
	        });
			
			tourSearchRes2.setHotelList(hotel2List);
			
			// vehicle
			// create hotel2 list
			List<Vehicle> vehicleList = new ArrayList<>();
			// search eVehicle location
			hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.Location locationVehicle 
				= new hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.Location();
			
			if (tourSearch.getStartLocation() != null && !tourSearch.getStartLocation().isEmpty()) {
				locationVehicle.setStartLocation(tourSearch.getStartLocation());
			} else {
				locationVehicle.setStartLocation(itemTour.getAddress().getProvince());
			}
			locationVehicle.setEndLocation(itemTour.getAddress().getProvince());
			List<EVehicleDTOSimple> eVehicleList = new ArrayList<>();
			eVehicleList.addAll(iEVehicleService.searchEVehicleByLocation(locationVehicle));
			
			for (EVehicleDTOSimple itemEVehicleDTOSimple : eVehicleList) {
				// create vehicle
				Vehicle vehicle = new Vehicle();
				modelMapper.map(itemEVehicleDTOSimple, vehicle);
				
				// create option list
				List<CoachOption> optionCoachList = new ArrayList<>();
				// search coach
				CoachSearch coachSearch = new CoachSearch();
				coachSearch.setEVehicleId(itemEVehicleDTOSimple.getEVehicleId());
				coachSearch.setStartDate(tourSearch.getStartDate());
				coachSearch.setEndDate(tourSearch.getStartDate().plusDays(itemTour.getNumberOfDay() - 1));
				coachSearch.setNumberOfPeople(tourSearch.getNumberOfAdult() + tourSearch.getNumberOfChildren());
				coachSearch.setNumberOfCoach(1);
				
				List<CoachSearchRes> coachSearchResList = new ArrayList<>();
				try {
					coachSearchResList.addAll(iEVehicleService.searchCoach(coachSearch));
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				if (!coachSearchResList.isEmpty()) {
					for (CoachSearchRes itemCoachSearchRes : coachSearchResList) {
						// create optionCoach
						CoachOption coachOption = new CoachOption();
						
						// create coach list
						List<Coach> coachList = new ArrayList<>();
						for (hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.extend.Coach itemCoach : itemCoachSearchRes.getCoachList()) {
							Coach coach = new Coach();
							modelMapper.map(itemCoach, coach);
							coachList.add(coach);
						}
						coachOption.setCoachList(coachList);
						
						priceTemp = (double)itemCoachSearchRes.getTotalPrice() * (100 + commissionDTO.getRate()) / 100;
						totalPrice = (int)priceTemp;
						
						coachOption.setTotalPriceNotDiscount(totalPrice);
						if (itemTour.getDiscount().getIsDiscount() == true) {
							priceTemp = (double)totalPrice * (100 - itemTour.getDiscount().getDiscountValue()) / 100;
							coachOption.setTotalPrice((int)priceTemp);
						} else {
							coachOption.setTotalPrice(totalPrice);
						}
						
						optionCoachList.add(coachOption);
					}
				}
				
				vehicle.setOptionList(optionCoachList);
				
				if (!optionCoachList.isEmpty()) {
					vehicleList.add(vehicle);
				}
			}
			// sort vehicle list by price
			Collections.sort(vehicleList, new Comparator<Vehicle>() {
	            @Override
	            public int compare(Vehicle vehicle1, Vehicle vehicle2) {
	            	int result = Integer.compare(vehicle1.getOptionList().get(0).getTotalPrice(), vehicle2.getOptionList().get(0).getTotalPrice());
	                return result;
	            }
	        });
			
			tourSearchRes2.setVehicleList(vehicleList);
			
			// add tourSearchRes2
			if (!hotel2List.isEmpty() && !vehicleList.isEmpty()) {
				tourSearchRes2List.add(tourSearchRes2);
			}
		}
		
		// sort tour search list by price
//		Collections.sort(tourSearchRes2List, new Comparator<TourSearchRes2>() {
//            @Override
//            public int compare(TourSearchRes2 tourSearchRes1, TourSearchRes2 tourSearchRes2) {
//            	int result = Integer.compare(tourSearchRes1.getTourPrice(), tourSearchRes2.getTourPrice());
//                return result;
//            }
//        });

		return tourSearchRes2List;
	}

	@Override
	public List<TourSearchRes2> searchFilter2(TourFilter tourFilter, List<TourSearchRes2> tourSearchRes2List) {
		try {
			// price filter
			int minPrice = Integer.valueOf(tourFilter.getMinPrice());
			int maxPrice = Integer.valueOf(tourFilter.getMaxPrice());
			if (minPrice >= 0 && maxPrice >= minPrice) {
				List<TourSearchRes2> tourSearchResListClone = new ArrayList<>();
				tourSearchResListClone.addAll(tourSearchRes2List);
				for (TourSearchRes2 itemTourSearchRes2 : tourSearchResListClone) {
					int totalPrice = 
							itemTourSearchRes2.getTourPrice() 
							+ itemTourSearchRes2.getHotelList().get(0).getOptionList().get(0).getTotalPrice()
							+ itemTourSearchRes2.getVehicleList().get(0).getOptionList().get(0).getTotalPrice();
					if (totalPrice < minPrice || totalPrice > maxPrice) {
						tourSearchRes2List.remove(itemTourSearchRes2);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Tour search filter min max: " + e.getMessage());
		}
		
		try {
			// rating filter
			int ratingFilter = Integer.valueOf(tourFilter.getRatingFilter());
			if (ratingFilter >= 1 && ratingFilter <= 5) {
				List<TourSearchRes2> tourSearchResListClone = new ArrayList<>();
				tourSearchResListClone.addAll(tourSearchRes2List);
				for (TourSearchRes2 itemTourSearchRes2 : tourSearchResListClone) {
					if (itemTourSearchRes2.getTour().getRate() < ratingFilter) {
						tourSearchRes2List.remove(itemTourSearchRes2);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Tour search filter rating: " + e.getMessage());
		}
		
		return tourSearchRes2List;
	}

	@Override
	public List<TourSearchRes2> searchSort2(TourSort tourSort, List<TourSearchRes2> tourSearchRes2List) {
		// sort by popular
		try {
			if (tourSort.getSortBy().equals("popular")) {
				Collections.sort(tourSearchRes2List, new Comparator<TourSearchRes2>() {
		            @Override
		            public int compare(TourSearchRes2 e1, TourSearchRes2 e2) {
		            	int result = 0;
		            	if (tourSort.getOrder().equals("asc")) {
		            		result = Integer.compare(e1.getTour().getNumberOfOrdered(), e2.getTour().getNumberOfOrdered());
		            	} else if (tourSort.getOrder().equals("desc")) {
		            		result = Integer.compare(e2.getTour().getNumberOfOrdered(), e1.getTour().getNumberOfOrdered());
		            	} else {
		            		result = 0;
		            	}
		                return result;
		            }
		        });
			}
		} catch (Exception e) {
			System.out.println("Tour search sort by popular: " + e.getMessage());
		}
		
		// sort by price
		try {
			if (tourSort.getSortBy().equals("price")) {
				Collections.sort(tourSearchRes2List, new Comparator<TourSearchRes2>() {
		            @Override
		            public int compare(TourSearchRes2 e1, TourSearchRes2 e2) {
						int totalPrice1 = 
								e1.getTourPrice() 
								+ e1.getHotelList().get(0).getOptionList().get(0).getTotalPrice()
								+ e1.getVehicleList().get(0).getOptionList().get(0).getTotalPrice();
						
						int totalPrice2 = 
								e2.getTourPrice() 
								+ e2.getHotelList().get(0).getOptionList().get(0).getTotalPrice()
								+ e2.getVehicleList().get(0).getOptionList().get(0).getTotalPrice();
		            	
		            	int result = 0;
		            	if (tourSort.getOrder().equals("asc")) {
		            		result = Integer.compare(totalPrice1, totalPrice2);
		            	} else if (tourSort.getOrder().equals("desc")) {
		            		result = Integer.compare(totalPrice2, totalPrice1);
		            	} else {
		            		result = 0;
		            	}
		                return result;
		            }
		        });
			}
		} catch (Exception e) {
			System.out.println("Tour search sort by price: " + e.getMessage());
		}
		
		// sort by promotion
		try {	
//		if (tourSort.getSortBy().equals("promotion")) {
//			Collections.sort(tourSearchResList, new Comparator<TourSearchRes>() {
//	            @Override
//	            public int compare(TourSearchRes e1, TourSearchRes e2) {
//	                int result = Integer.compare(e1.getTour().getNumberOfOrdered(), e2.getTour().getNumberOfOrdered());
////		                if (result == 0) {
////		                    result = e1.field2.compareTo(e2.field2);
////		                }
//	                return result;
//	            }
//	        });
//		}	
		} catch (Exception e) {
			System.out.println("Tour search sort by promotion: " + e.getMessage());
		}

		return tourSearchRes2List;
	}

	@Override
	public void updateReviewer(String tourId, Reviewer reviewer) {
		Tour tour = new Tour();
		tour = getDetail(tourId);
		
		List<Reviewer> reviewerList = new ArrayList<>();
		if (tour.getReviewer() != null) {
			reviewerList.addAll(tour.getReviewer());
		}
		
		reviewerList.add(reviewer);
		tour.setReviewer(reviewerList);
		
		int numberOfReviewer = 0;
		int totalRate = 0;
		for (Reviewer itemReviewer : reviewerList) {
			totalRate += itemReviewer.getRate();
			numberOfReviewer += 1;
		}
		
		tour.setNumberOfReviewer(numberOfReviewer);
		tour.setRate(((double)(double)totalRate / (double)numberOfReviewer));
		
		tourRepository.save(tour);		
	}

	@Override
	public void updateDailyTourLimit(String tourId, int dailyTourLimit) {
		if (tourId != null && !tourId.isEmpty()) {
			Tour tour = new Tour();
			tour = getDetail(tourId);
			tour.setDailyTourLimit(dailyTourLimit);
			tourRepository.save(tour);
		} else {
			List<Tour> tourList = new ArrayList<>();
			tourList.addAll(getAll());
			for (Tour itemTour : tourList) {
				itemTour.setDailyTourLimit(dailyTourLimit);
				tourRepository.save(itemTour);
			}
		}
	}
}