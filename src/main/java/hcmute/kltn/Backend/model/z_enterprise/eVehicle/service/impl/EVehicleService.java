package hcmute.kltn.Backend.model.z_enterprise.eVehicle.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hcmute.kltn.Backend.exception.CustomException;
import hcmute.kltn.Backend.exception.TryCatchException;
import hcmute.kltn.Backend.model.account.service.IAccountDetailService;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.CoachSearch;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.CoachSearchRes;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.EVehicleDTO;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.EVehicleDTOSimple;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.Location;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.entity.EVehicle;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.extend.Coach;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.repository.EVehicleRepository;
import hcmute.kltn.Backend.model.z_enterprise.eVehicle.service.IEVehicleService;
import hcmute.kltn.Backend.util.EntityUtil;
import hcmute.kltn.Backend.util.IntegerUtil;
import hcmute.kltn.Backend.util.LocalDateTimeUtil;
import hcmute.kltn.Backend.util.StringUtil;

@Service
public class EVehicleService implements IEVehicleService{
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private IAccountDetailService iAccountDetailService;
	@Autowired
	private EVehicleRepository eVehicleRepository;
	
	private void checkFieldCondition(EVehicle eVehicle) {
		// check null
		if(eVehicle.getEVehicleName() == null || eVehicle.getEVehicleName().equals("")) {
			throw new CustomException("Enterprise Vehicle Name is not null");
		}
		if(eVehicle.getAddress() == null) {
			throw new CustomException("Address is not null");
		}
		if(eVehicle.getPhoneNumber() == null || eVehicle.getPhoneNumber().equals("")) {
			throw new CustomException("Phone number is not null");
		}
		if(eVehicle.getNumberOfStarRating() < 1 || eVehicle.getNumberOfStarRating() > 5) {
			throw new CustomException("Number Of Star Rating must be between 1 and 5");
		}
		
		// check unique
	}
	
	private EVehicle create(EVehicle eVehicle) {
		// check field condition
		checkFieldCondition(eVehicle);
    	
		// Set default value
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();

		eVehicle.setStatus(true);
		eVehicle.setCreatedBy(accountId);
		eVehicle.setCreatedAt2(currentDate);
		eVehicle.setLastModifiedBy(accountId);
		eVehicle.setLastModifiedAt2(currentDate);
		
		// create discount
		EVehicle eVehicleNew = new EVehicle();
		eVehicleNew = eVehicleRepository.save(eVehicle);
		
		return eVehicleNew;
	}

	private EVehicle update(EVehicle eVehicle) {
    	// Check exists
		if (!eVehicleRepository.existsById(eVehicle.getEVehicleId())) {
			throw new CustomException("Cannot find Enterprise Vehicle");
		}
		
		// check field condition
		checkFieldCondition(eVehicle);
    	
    	// Set default value
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		eVehicle.setLastModifiedBy(accountId);
		eVehicle.setLastModifiedAt2(currentDate);
		
		// update discount
		EVehicle eVehicleNew = new EVehicle();
		eVehicleNew = eVehicleRepository.save(eVehicle);
		
		return eVehicleNew;
    }
	
	private EVehicle getDetail(String eVehicleId) {
		// Check exists
		if (!eVehicleRepository.existsById(eVehicleId)) {
			throw new CustomException("Cannot find Enterprise Vehicle");
		}
		
		// Find discount
		EVehicle eVehicle = eVehicleRepository.findById(eVehicleId).get();
		
		return eVehicle;
	}
	
	private List<EVehicle> getAll() {
		// Find discount
		List<EVehicle> list = eVehicleRepository.findAll();
		
		return list;
	}
	
	private void delete(String eVehicleId) {
		// Check exists
		if (eVehicleRepository.existsById(eVehicleId)) {
			eVehicleRepository.deleteById(eVehicleId);
		}
	}
	
	private List<EVehicle> search(String keyword) {
		// init tour List
		List<EVehicle> eVehicleList = new ArrayList<>();
		eVehicleList = eVehicleRepository.findAll();
		if (keyword != null) {
			keyword = keyword.trim();
		}

		if (keyword != null && !keyword.isEmpty()) {
			if (eVehicleList != null) {
				List<EVehicle> eVehicleListClone = new ArrayList<>();
				eVehicleListClone.addAll(eVehicleList);
				for (EVehicle itemEVehicle : eVehicleListClone) {
					String keywordNew = StringUtil.getNormalAlphabet(keyword);
					String allValue = "";
					try {
						allValue = EntityUtil.getAllValue(itemEVehicle);
					} catch (Exception e) {
						throw new TryCatchException(e);
					}
					String fieldNew = StringUtil.getNormalAlphabet(allValue);
					
					System.out.println("\nfieldNew = " + fieldNew + " ");
					
					if (!fieldNew.contains(keywordNew)) {
						eVehicleList.remove(itemEVehicle);
						if (eVehicleList.size() <= 0) {
							break;
						}
					}
				}
			}
		}

		return eVehicleList;
	}
	
	private EVehicleDTO getEVehicleDTO(EVehicle eVehicle) {
		// mapping discountDTO
		EVehicleDTO eVehicleDTONew = new EVehicleDTO();
		modelMapper.map(eVehicle, eVehicleDTONew);
		
		return eVehicleDTONew;
	}
	
	private List<EVehicleDTO> getEVehicleDTOList(List<EVehicle> eVehicleList) {
		List<EVehicleDTO> eVehicleDTOList = new ArrayList<>();
		for (EVehicle itemEVehicle : eVehicleList) {
			eVehicleDTOList.add(getEVehicleDTO(itemEVehicle));
		}
		return eVehicleDTOList;
	}

	private Coach genCoach(String type, int rating) {
		HashMap<String, String> nameMap = new HashMap<>();
		nameMap.put("1", "Xe 4 chỗ");
		nameMap.put("2", "Xe 7 chỗ");
		nameMap.put("3", "Xe 9 chỗ");
		nameMap.put("4", "Xe 12 chỗ");
		nameMap.put("5", "Xe 16 chỗ");
		nameMap.put("6", "Xe 19 chỗ");
		nameMap.put("7", "Xe 29 chỗ");
		nameMap.put("8", "Xe 35 chỗ");
		nameMap.put("9", "Xe 45 chỗ");
		
		HashMap<String, Integer> capacityMap = new HashMap<>();
		capacityMap.put("1", 4);
		capacityMap.put("2", 7);
		capacityMap.put("3", 9);
		capacityMap.put("4", 12);
		capacityMap.put("5", 16);
		capacityMap.put("6", 19);
		capacityMap.put("7", 29);
		capacityMap.put("8", 35);
		capacityMap.put("9", 45);
		
		HashMap<String, List<String>> brandMap = new HashMap<>();
		brandMap.put("1, 1", Arrays.asList("Kia Morning", "Hyundai Grand i10", "Vinfast Fadil"));
		brandMap.put("1, 2", Arrays.asList("Kia Morning", "Toyota Wigo", "Honda Brio"));
		brandMap.put("1, 3", Arrays.asList("Hyundai Grand i10", "Toyota Wigo", "Suzuki Celerio"));
		brandMap.put("1, 4", Arrays.asList("Vinfast Fadil", "Honda Brio", "Suzuki Celerio"));
		brandMap.put("1, 5", Arrays.asList("Toyota Wigo", "Honda Brio", "Suzuki Celerio"));
		
		brandMap.put("2, 1", Arrays.asList("Mitsubishi Xpander", "Toyota Rush", "Honda CR-V"));
		brandMap.put("2, 2", Arrays.asList("Toyota Fortuner", "Mazda CX-8", "Suzuki Ertiga"));
		brandMap.put("2, 3", Arrays.asList("Toyota Avanza", "Kia Rondo", "Kia Carens"));
		brandMap.put("2, 4", Arrays.asList("Kia Sedona", "Mercedes Benz GLC", "Ford Everest"));
		brandMap.put("2, 5", Arrays.asList("Mitsubishi Pajero Sport", "Toyota Innova", "Kia Carnival"));
		
		brandMap.put("3, 1", Arrays.asList("Toyota Proace", "Hyundai Starex", "Toyota Hiace"));
		brandMap.put("3, 2", Arrays.asList("Toyota Granvia", "Ford Tourneo Custom", "Ford Transit Dcar"));
		brandMap.put("3, 3", Arrays.asList("Mercedes-Benz MB", "GMC Yukon", "Toyota Innova"));
		brandMap.put("3, 4", Arrays.asList("Ford Everest", "Hyundai Starex", "Honda Odyssey"));
		brandMap.put("3, 5", Arrays.asList("Toyota Proace", "Hyundai Starex", "Toyota Hiace"));
		
		brandMap.put("4, 1", Arrays.asList("Hyundai Solati", "Gaz", "Vinfast"));
		brandMap.put("4, 2", Arrays.asList("Hyundai Solati", "Gaz", "Vinfast"));
		brandMap.put("4, 3", Arrays.asList("Hyundai Solati", "Gaz", "Vinfast"));
		brandMap.put("4, 4", Arrays.asList("Hyundai Solati", "Gaz", "Vinfast"));
		brandMap.put("4, 5", Arrays.asList("Hyundai Solati", "Gaz", "Vinfast"));
		
		brandMap.put("5, 1", Arrays.asList("Ford Transit", "Toyota Hiace", "Hyundai Solati"));
		brandMap.put("5, 2", Arrays.asList("Ford Transit", "Toyota Hiace", "Gaz"));
		brandMap.put("5, 3", Arrays.asList("Ford Transit", "Toyota Hiace", "Gaz"));
		brandMap.put("5, 4", Arrays.asList("Ford Transit", "Hyundai Solati", "Mercedes Sprinter"));
		brandMap.put("5, 5", Arrays.asList("Ford Transit", "Hyundai Solati", "Mercedes Sprinter"));
		
		brandMap.put("6, 1", Arrays.asList("Dcar", "Iveco", "Fuso"));
		brandMap.put("6, 2", Arrays.asList("Dcar", "Iveco", "Fuso"));
		brandMap.put("6, 3", Arrays.asList("Dcar", "Iveco", "Fuso"));
		brandMap.put("6, 4", Arrays.asList("Dcar", "Iveco", "Fuso"));
		brandMap.put("6, 5", Arrays.asList("Dcar", "Iveco", "Fuso"));
		
		brandMap.put("7, 1", Arrays.asList("Hyundai County", "Samco Felix", "Thaco Meadow TB85s"));
		brandMap.put("7, 2", Arrays.asList("Hyundai County", "Samco Felix", "Thaco Meadow TB85s"));
		brandMap.put("7, 3", Arrays.asList("Hyundai County", "Samco Felix", "Thaco Meadow TB85s"));
		brandMap.put("7, 4", Arrays.asList("Hyundai County", "Samco Felix", "Thaco Meadow TB85s"));
		brandMap.put("7, 5", Arrays.asList("Hyundai County", "Samco Felix", "Thaco Meadow TB85s"));
		
		brandMap.put("8, 1", Arrays.asList("Isuzu Samco", "Thaco Kinglong", "Hyundai Aero Town"));
		brandMap.put("8, 2", Arrays.asList("Isuzu Samco", "Thaco Kinglong", "Hyundai Universe"));
		brandMap.put("8, 3", Arrays.asList("Isuzu Samco", "Thaco Kinglong", "Hyundai Universe"));
		brandMap.put("8, 4", Arrays.asList("Isuzu Samco", "Thaco Kinglong", "Hyundai Universe"));
		brandMap.put("8, 5", Arrays.asList("Isuzu Samco", "Thaco Kinglong", "Hyundai Universe"));
		
		brandMap.put("9, 1", Arrays.asList("Hyundai Aero Space", "Hyundai Hi Class", "Universe Haeco"));
		brandMap.put("9, 2", Arrays.asList("Hyundai Aero Space", "Hyundai Hi Class", "Universe Haeco"));
		brandMap.put("9, 3", Arrays.asList("Hyundai Aero Space", "Universe Haeco", "Samco Universe"));
		brandMap.put("9, 4", Arrays.asList("Hyundai Hi Class", "Universe Haeco", "Samco Universe"));
		brandMap.put("9, 5", Arrays.asList("Hyundai Hi Class", "Universe Haeco", "Samco Universe"));
		
		HashMap<String, Integer> pricePerDayMap = new HashMap<>();
		pricePerDayMap.put("1", 800000);
		pricePerDayMap.put("2", 1000000);
		pricePerDayMap.put("3", 1200000);
		pricePerDayMap.put("4", 1400000);
		pricePerDayMap.put("5", 1600000);
		pricePerDayMap.put("6", 1800000);
		pricePerDayMap.put("7", 2300000);
		pricePerDayMap.put("8", 2500000);
		pricePerDayMap.put("9", 3000000);
		
		HashMap<String, Integer> pricePerDayPlusMap = new HashMap<>();
		pricePerDayPlusMap.put("1", 200000);
		pricePerDayPlusMap.put("2", 250000);
		pricePerDayPlusMap.put("3", 300000);
		pricePerDayPlusMap.put("4", 350000);
		pricePerDayPlusMap.put("5", 400000);
		pricePerDayPlusMap.put("6", 450000);
		pricePerDayPlusMap.put("7", 575000);
		pricePerDayPlusMap.put("8", 625000);
		pricePerDayPlusMap.put("9", 750000);
		
		HashMap<String, Integer> pricePerDayRandomMap = new HashMap<>();
		pricePerDayRandomMap.put("1", IntegerUtil.randomRange(0, 150) * 1000);
		pricePerDayRandomMap.put("2", IntegerUtil.randomRange(-200, 200) * 1000);
		pricePerDayRandomMap.put("3", IntegerUtil.randomRange(-250, 250) * 1000);
		pricePerDayRandomMap.put("4", IntegerUtil.randomRange(-300, 300) * 1000);
		pricePerDayRandomMap.put("5", IntegerUtil.randomRange(-350, 350) * 1000);
		pricePerDayRandomMap.put("6", IntegerUtil.randomRange(-400, 400) * 1000);
		pricePerDayRandomMap.put("7", IntegerUtil.randomRange(-525, 525) * 1000);
		pricePerDayRandomMap.put("8", IntegerUtil.randomRange(-575, 575) * 1000);
		pricePerDayRandomMap.put("9", IntegerUtil.randomRange(-700, 700) * 1000);
		
		HashMap<String, Integer> pricePerKilometerMap = new HashMap<>();
		pricePerKilometerMap.put("1", 5000);
		pricePerKilometerMap.put("2", 6000);
		pricePerKilometerMap.put("3", 7000);
		pricePerKilometerMap.put("4", 9000);
		pricePerKilometerMap.put("5", 11000);
		pricePerKilometerMap.put("6", 13000);
		pricePerKilometerMap.put("7", 18000);
		pricePerKilometerMap.put("8", 21000);
		pricePerKilometerMap.put("9", 25000);
		
		HashMap<String, Integer> pricePerKilometerPlusMap = new HashMap<>();
		pricePerKilometerPlusMap.put("1", 1250);
		pricePerKilometerPlusMap.put("2", 1500);
		pricePerKilometerPlusMap.put("3", 1750);
		pricePerKilometerPlusMap.put("4", 2250);
		pricePerKilometerPlusMap.put("5", 2750);
		pricePerKilometerPlusMap.put("6", 3250);
		pricePerKilometerPlusMap.put("7", 4500);
		pricePerKilometerPlusMap.put("8", 5250);
		pricePerKilometerPlusMap.put("9", 6250);
		
		HashMap<String, String> serviceMap = new HashMap<>();
		serviceMap.put("1", "Hệ thống điều hòa");
		serviceMap.put("2", "Hệ thống điều hòa, Hệ thống âm thanh");
		serviceMap.put("3", "Hệ thống điều hòa, Màn hình trung tâm, Hệ thống âm thanh");
		serviceMap.put("4", "Hệ thống điều hòa, Màn hình trung tâm cỡ lớn, Hệ thống âm thanh cao cấp");
		serviceMap.put("5", "Hệ thống điều hòa, Màn hình trung tâm cỡ lớn, Hệ thống âm thanh cao cấp");

		Coach coach = new Coach();
		coach.setName(nameMap.get(type));
		coach.setCapacity(capacityMap.get(type));
		coach.setType(type);
		coach.setManufacturerAndModel(brandMap.get(type + ", " + rating).get(IntegerUtil.randomRange(0, 2)));
		coach.setPricePerDay(
				pricePerDayMap.get(type) 
				+ (pricePerDayPlusMap.get(type) * (rating - 1)) 
				+ pricePerDayRandomMap.get(type));
		coach.setPricePerKilometer(
				pricePerKilometerMap.get(type) 
				+ (pricePerKilometerPlusMap.get(type) * (rating - 1))); 
		coach.setDriverIncluded(true);
		coach.setAdditionalServices(serviceMap.get(String.valueOf(rating)));
		
		return coach;
	}
	
	@Override
	public EVehicleDTO createEVehicle(EVehicleDTO eVehicleDTO) {
		// mapping discount
		EVehicle eVehicle = new EVehicle();
		modelMapper.map(eVehicleDTO, eVehicle);
		
		// check field condition
		checkFieldCondition(eVehicle);
		
		// gen coach
		List<Coach> coachList = new ArrayList<>();
		int index = 1;
		// gen 
		for (int i = 0; i <= 2; i++) {
			for (int j = 0; j <= 2; j++) {
				Coach coach = new Coach();
				coach = genCoach(String.valueOf(i * 3 + 1 + j), eVehicleDTO.getNumberOfStarRating());
				
				for (int k = 1; k <= (eVehicleDTO.getNumberOfStarRating() + 2 - i); k++) {
					Coach coachNew = new Coach();
					modelMapper.map(coach, coachNew);
					coachNew.setCoachId(String.valueOf(index));
					coachList.add(coachNew);
					
					index += 1;
				}
			}
		}
		eVehicle.setCoachList(coachList);

		// create discount
		EVehicle eVehicleNew = new EVehicle();
		eVehicleNew = create(eVehicle);
		
		return getEVehicleDTO(eVehicleNew);
	}
	

	@Override
	public EVehicleDTO updateEVehicle(EVehicleDTO eVehicleDTO) {
		// get discount from database
		EVehicle eVehicle = getDetail(eVehicleDTO.getEVehicleId());
		
		// mapping discount
		int numberOfStarRating = eVehicle.getNumberOfStarRating();
		modelMapper.map(eVehicleDTO, eVehicle);
		eVehicle.setNumberOfStarRating(numberOfStarRating);
		
		// update discount
		EVehicle eVehicleNew = new EVehicle();
		eVehicleNew = update(eVehicle);
		
		return getEVehicleDTO(eVehicleNew);
	}
	

	@Override
	public EVehicleDTO getDetailEVehicle(String eVehicleId) {
		EVehicle eVehicle = getDetail(eVehicleId);

		return getEVehicleDTO(eVehicle);
	}
	

	@Override
	public List<EVehicleDTO> getAllEVehicle() {
		List<EVehicle> eVehicleList = new ArrayList<>(getAll());

		return getEVehicleDTOList(eVehicleList);
	}
	

	@Override
	public List<EVehicleDTO> searchEVehicle(String keyword) {
		List<EVehicle> eVehicleList = search(keyword);

		return getEVehicleDTOList(eVehicleList);
	}

	
	@Override
	public List<EVehicleDTOSimple> searchEVehicleByLocation(Location location) {
		List<EVehicleDTOSimple> eVehicleDTOSimpleList = new ArrayList<>();
		
		List<EVehicle> eVehicleList = new ArrayList<>();
		eVehicleList.addAll(getAll());
		
		for (EVehicle itemEVehicle : eVehicleList) {
			int existsLocation = 0;
			if (itemEVehicle.getRoute() != null) {
				for (String itemRoute : itemEVehicle.getRoute()) {
					if (itemRoute.equals(location.getStartLocation())) {
						existsLocation += 1;
					}
					if (itemRoute.equals(location.getEndLocation())) {
						existsLocation += 1;
					}
				}
				if (existsLocation >= 2) {
					EVehicleDTOSimple eVehicleDTOSimple = new EVehicleDTOSimple();
					modelMapper.map(itemEVehicle, eVehicleDTOSimple);
					eVehicleDTOSimpleList.add(eVehicleDTOSimple);
				}
			}
		}

		return eVehicleDTOSimpleList;
	}

	@Override
	public List<CoachSearchRes> searchCoach(CoachSearch coachSearch) {
		// số người trên 1 xe hiện tại = tổng số người  / số xe
		// tìm xe có sức chứa >= số người
		// Số người trên 1 xe hiện tại = (tổng số người - sức chứa xe vừa tìm) / (số xe - 1)
		// nếu số người trên 1 xe vẫn còn thì thực hiện tìm tiêp
		
		long numberOfDay = Math.abs(ChronoUnit.DAYS.between(coachSearch.getEndDate(), coachSearch.getStartDate()));
		
		List<CoachSearchRes> coachSearchResList = new ArrayList<>();
		
		EVehicle eVehicle = new EVehicle();
		eVehicle = getDetail(coachSearch.getEVehicleId());
		
		List<Coach> coachList = new ArrayList<>();
		coachList.addAll(eVehicle.getCoachList());
		
		// sort coachList by capacity asc
		Collections.sort(coachList, new Comparator<Coach>() {
            @Override
            public int compare(Coach coach1, Coach coach2) {
            	int result = 0;
            	result = Integer.compare(coach1.getCapacity(), coach2.getCapacity());
                return result;
            }
        });
		
		
		CoachSearchRes coachSearchRes = new CoachSearchRes();
		int currentCapacity = 0;
		int currentNumberOfCoach = coachSearch.getNumberOfCoach();
		List<Coach> coachResList = new ArrayList<>();
		int totalPrice = 0;
		while (currentCapacity < coachSearch.getNumberOfPeople()) {
			if (currentNumberOfCoach <= 0) {
				throw new CustomException("No suitable coach found");
			}
			int peoplePerCoach = (coachSearch.getNumberOfPeople() - currentCapacity) / currentNumberOfCoach;
			boolean findCheck = false;
			for (Coach itemCoach : coachList) {
				if (itemCoach.getCapacity() >= peoplePerCoach) {
					boolean idCheck = false;
					for (Coach itemCoachId : coachResList) {
						if (itemCoachId.getCoachId().equals(itemCoach.getCoachId())) {
							idCheck = true;
						}
					}
					if (idCheck == false) {
						Coach coach = new Coach();
						modelMapper.map(itemCoach, coach);
						
						coachResList.add(coach);
						totalPrice += coach.getPricePerDay();
						
//						currentCapacity += coach.getCapacity();
						currentCapacity += peoplePerCoach;
						currentNumberOfCoach -= 1;
						
						findCheck = true;
						
						break;
					}
				}
			}
			if (findCheck == false) {
				throw new CustomException("No suitable coach found");
			}
		}
		coachSearchRes.setCoachList(coachResList);
		coachSearchRes.setTotalPrice((int)(totalPrice * ((double)numberOfDay + 1)));
		coachSearchResList.add(coachSearchRes);
		
//		for (int i = 1; i <= coachSearch.getNumberOfCoach(); i++) {
//			List<Coach> coachResListClone = new ArrayList<>();
//			coachResListClone.addAll(coachResList);
//			
//			Coach coach = new Coach();
//			coach = coachResList.get(i - 1);
//			
//			for (Coach itemCoach : coachList) {
//				if (itemCoach.getCapacity() > coach.getCapacity()) {
//					boolean idCheck = false;
//					for (Coach itemCoachId : coachResList) {
//						if (itemCoachId.getCoachId().equals(itemCoach.getCoachId())) {
//							idCheck = true;
//						}
//					}
//					if (idCheck == false) {
//						Coach coachNew = new Coach();
//						modelMapper.map(itemCoach, coachNew);
//						
//						coachResList.set(i - 1, coachNew);
//						totalPrice -= coach.getPricePerDay();
//						totalPrice += coachNew.getPricePerDay();
//						
//						break;
//					}
//				}
//			}
//			
//			CoachSearchRes coachSearchResNew = new CoachSearchRes();
//			coachSearchResNew.setCoachList(coachList);
//			coachSearchResNew.setTotalPrice(totalPrice);
//			coachSearchResList.add(coachSearchResNew);
//		}
		
		return coachSearchResList;
	}
}
