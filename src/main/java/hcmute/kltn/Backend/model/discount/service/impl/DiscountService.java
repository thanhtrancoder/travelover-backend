package hcmute.kltn.Backend.model.discount.service.impl;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
import hcmute.kltn.Backend.model.base.image.service.IImageService;
import hcmute.kltn.Backend.model.discount.dto.DiscountCreate;
import hcmute.kltn.Backend.model.discount.dto.DiscountDTO;
import hcmute.kltn.Backend.model.discount.dto.DiscountUpdate;
import hcmute.kltn.Backend.model.discount.dto.entity.Discount;
import hcmute.kltn.Backend.model.discount.repository.DiscountRepository;
import hcmute.kltn.Backend.model.discount.service.IDiscountService;
import hcmute.kltn.Backend.model.generatorSequence.service.IGeneratorSequenceService;
import hcmute.kltn.Backend.util.LocalDateTimeUtil;
import hcmute.kltn.Backend.util.LocalDateUtil;
import hcmute.kltn.Backend.util.StringUtil;

@Service
public class DiscountService implements IDiscountService{
	@Autowired
	private DiscountRepository discountRepository;
	@Autowired
	private IGeneratorSequenceService iGeneratorSequenceService;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private IAccountDetailService iAccountDetailService;
	@Autowired
    private MongoTemplate mongoTemplate;
	@Autowired
	private IImageService iImageService;
	
	private String getCollectionName() {
        String collectionName = mongoTemplate.getCollectionName(Discount.class);
        return collectionName;
    }
	
	private void checkFieldCondition(Discount discount) {
		LocalDate currentDate = LocalDateUtil.getDateNow();
		
		// check null
		if(discount.getDiscountTitle() == null || discount.getDiscountTitle().equals("")) {
			throw new CustomException("Title is not null");
		}
		if(discount.getDiscountCode() == null || discount.getDiscountCode().equals("")) {
			throw new CustomException("Discount Code is not null");
		}
		if(discount.getDiscountValue() <= 0) {
			throw new CustomException("Discount Value must be greater than 0");
		}
		if(discount.getStartDate() == null || discount.getStartDate().isBefore(currentDate)) {
			throw new CustomException("Start Date must be greater or equal than current date");
		}
		if(discount.getEndDate() == null || discount.getEndDate().isBefore(discount.getStartDate())) {
			throw new CustomException("End Date must be greater or equal than start date");
		}
		if(discount.getMinOrder() < 0) {
			throw new CustomException("Minium order must be greater or equal than 0");
		}
		if(discount.getMaxDiscount() <= 0) {
			throw new CustomException("Maxium order must be greater than 0");
		}
		if(discount.getIsQuantityLimit() == true) {
			if(discount.getNumberOfCode() <= 0) {
				
				throw new CustomException("Number Of Code must be greater than 0");
			}
		}
		
		// check unique
		if(discount.getDiscountId() == null || discount.getDiscountId().equals("")) {
			if(discountRepository.existsByDiscountCode(discount.getDiscountCode().trim())) {
				throw new CustomException("Discount code is already");
			}
		} else {
			Discount discountFind = discountRepository.findById(discount.getDiscountId()).get();
			List<Discount> discountCodeList = discountRepository.findAllByDiscountCode(discount.getDiscountId());
			for(Discount itemDiscount : discountCodeList) {
				if (itemDiscount.getDiscountCode() == discountFind.getDiscountCode() 
						&& itemDiscount.getDiscountId() != discountFind.getDiscountId()) {
					throw new CustomException("Discount code is already");
				}
			}
		}
	}
	
	private Discount create(Discount discount) {
		// check field condition
		checkFieldCondition(discount);
    	
		// Set default value
		String discountId = iGeneratorSequenceService.genId(getCollectionName());
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDate currentDate = LocalDateUtil.getDateNow();
		
		discount.setNumberOfCodeUsed(0);
		discount.setDiscountId(discountId);
		discount.setStatus(true);
		discount.setCreatedBy(accountId);
		discount.setCreatedAt(currentDate);
		discount.setLastModifiedBy(accountId);
		discount.setLastModifiedAt(currentDate);
		
		// new date
		LocalDateTime currentDate2 = LocalDateTimeUtil.getCurentDate();
		discount.setCreatedAt2(currentDate2);
		discount.setLastModifiedAt2(currentDate2);
		
		// create discount
		Discount discountNew = new Discount();
		discountNew = discountRepository.save(discount);
		
		return discountNew;
	}
	
	private Discount update(Discount discount) {
    	// Check exists
		if (!discountRepository.existsById(discount.getDiscountId())) {
			throw new CustomException("Cannot find discount");
		}
		
		// check field condition
		checkFieldCondition(discount);
    	
    	// Set default value
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDate currentDate = LocalDateUtil.getDateNow();
		discount.setLastModifiedBy(accountId);
		discount.setLastModifiedAt(currentDate);
		
		// new date
		LocalDateTime currentDate2 = LocalDateTimeUtil.getCurentDate();
		discount.setLastModifiedAt2(currentDate2);
		
		// update discount
		Discount discountNew = new Discount();
		discountNew = discountRepository.save(discount);
		
		return discountNew;
    }
	
	private Discount getDetail(String discountId) {
		// Check exists
		if (!discountRepository.existsById(discountId)) {
			throw new CustomException("Cannot find discount");
		}
		
		// Find discount
		Discount discount = discountRepository.findById(discountId).get();
		
		return discount;
	}
	
	private List<Discount> getAll() {
		// Find discount
		List<Discount> list = discountRepository.findAll();
		
		return list;
	}
	
	private void delete(String discountId) {
		// Check exists
		if (discountRepository.existsById(discountId)) {
			discountRepository.deleteById(discountId);
		}
	}
	
	private String genDiscountCode() {
		List<Discount> discountList = new ArrayList<>(getAll());
		
		String discountCode = new String();
		boolean checkExists = true;
		while (checkExists == true) {
			checkExists = false;
			discountCode = StringUtil.genRandom(10);
			for (Discount itemDiscount : discountList) {
				if (itemDiscount.getDiscountCode().equals(discountCode)) {
					checkExists = true;
				}
			}
		}
		
		return discountCode;
	}
	
	private List<Discount> search(String keyword) {
		// init discount List
		List<Discount> discountList = new ArrayList<>();
		
		if(keyword == null || keyword.trim().isEmpty()) {
			discountList = getAll();
		} else {
			// create list field name
			List<Criteria> criteriaList = new ArrayList<>();
			for(Field itemField : Discount.class.getDeclaredFields()) {
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
	        discountList = mongoTemplate.find(query, Discount.class);
		}
		
		return discountList;
	}
	
	private String getAllValue(Discount discount) {
		String result = new String();
		
		// value of account
		for (Field itemField : Discount.class.getDeclaredFields()) {
			itemField.setAccessible(true);
			try {
				// check type
				boolean isList = itemField.getType().isAssignableFrom(List.class);
				Object object = itemField.get(discount);
				if (object != null && !isList) {
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
				Object object = itemField.get(discount);
				if (object != null) {
					result += String.valueOf(object) + " ";
				} 
			} catch (Exception e) {
				
			}
		}

		return result;
	}
	
	private List<Discount> search2(String keyword) {
		// init tour List
		List<Discount> discountList = new ArrayList<>();
		discountList = discountRepository.findAll();
		if (keyword != null) {
			keyword = keyword.trim();
		}

		if (keyword != null && !keyword.isEmpty()) {
			if (discountList != null) {
				List<Discount> discountListClone = new ArrayList<>();
				discountListClone.addAll(discountList);
				for (Discount itemDiscount : discountListClone) {
					String keywordNew = StringUtil.getNormalAlphabet(keyword);
					String fieldNew = StringUtil.getNormalAlphabet(getAllValue(itemDiscount));
					
					if (!fieldNew.contains(keywordNew)) {
						discountList.remove(itemDiscount);
						if (discountList.size() <= 0) {
							break;
						}
					}
				}
			}
		}

		return discountList;
	}
	
	private DiscountDTO getDiscountDTO(Discount discount) {
		// mapping discountDTO
		DiscountDTO discountDTONew = new DiscountDTO();
		modelMapper.map(discount, discountDTONew);
		
		return discountDTONew;
	}
	
	private List<DiscountDTO> getDiscountDTOList(List<Discount> discountList) {
		List<DiscountDTO> discountDTOList = new ArrayList<>();
		for (Discount itemDiscount : discountList) {
			discountDTOList.add(getDiscountDTO(itemDiscount));
		}
		return discountDTOList;
	}
	
	private boolean checkValid(Discount discount) {
		LocalDate currentDate = LocalDateUtil.getDateNow();
		
		if (discount.getStatus() == false) {
			return false;
		}
		
		if (discount.getStartDate().isAfter(currentDate)) {
			return false;
		}
		
		if (discount.getEndDate().isBefore(currentDate)) {
			return false;
		}
		
		if (discount.getIsQuantityLimit() == true) {
			if (discount.getNumberOfCodeUsed() >= discount.getNumberOfCode()) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public DiscountDTO createDiscount(DiscountCreate discountCreate) {
		// mapping discount
		Discount discount = new Discount();
		modelMapper.map(discountCreate, discount);
		
		// check discount code
		if (discount.getDiscountCode() != null && !discount.getDiscountCode().trim().isEmpty()) {
			if (discount.getDiscountCode().length() < 6 || 20 < discount.getDiscountCode().length()) {
				throw new CustomException("Discount code must be between 6 and 20 in length");
			}
			
			List<Discount> discountList = new ArrayList<>();
			discountList.addAll(getAll());
			for (Discount itemDiscount : discountList) {
				if (itemDiscount.getDiscountCode().equals(discount.getDiscountCode())) {
					throw new CustomException("Discount code is already");
				}
			}
			discount.setDiscountCode(discountCreate.getDiscountCode().toUpperCase());
		} else {
			// gen discountCode
			String discountCode = genDiscountCode();
			discount.setDiscountCode(discountCode);
		}
		
		// check field condition
		checkFieldCondition(discount);

		// create discount
		Discount discountNew = new Discount();
		discountNew = create(discount);
		
		return getDiscountDTO(discountNew);
	}

	@Override
	public DiscountDTO updateDiscount(DiscountUpdate discountUpdate) {
		// get discount from database
		Discount discount = getDetail(discountUpdate.getDiscountId());
		
		// check current date
		LocalDate currentDate = LocalDateUtil.getDateNow();
		if (!currentDate.isBefore(discount.getStartDate())) {
			throw new CustomException("Only update discounts before the use date "
					+ "(the current date must be less than the start date)");
		}
		
		// check image and delete
		if ((discount.getImageUrl() != null 
				&& !discount.getImageUrl().equals(""))
				&& !discount.getImageUrl().equals(discountUpdate.getImageUrl())) {
			boolean checkDelete = iImageService.deleteImageByUrl(discount.getImageUrl());
			if (checkDelete == false) {
				throw new CustomException("An error occurred during the processing of the old image");
			}
		}
		
		// check discount code
		if (discountUpdate.getDiscountCode() != null && !discountUpdate.getDiscountCode().trim().isEmpty()) {
			if (!discountUpdate.getDiscountCode().equals(discount.getDiscountCode())) {
				if (discountUpdate.getDiscountCode().length() < 6 || 20 < discountUpdate.getDiscountCode().length()) {
					throw new CustomException("Discount code must be between 6 and 20 in length");
				}
				
				List<Discount> discountList = new ArrayList<>();
				discountList.addAll(getAll());
				for (Discount itemDiscount : discountList) {
					if (itemDiscount.getDiscountCode().equals(discountUpdate.getDiscountCode()) 
							&& !itemDiscount.getDiscountId().equals(discount.getDiscountId())) {
						throw new CustomException("Discount code is already");
					}
				}
				discountUpdate.setDiscountCode(discountUpdate.getDiscountCode().toUpperCase());
			}
		} else {
			discountUpdate.setDiscountCode(discount.getDiscountCode());
		}
		
		// mapping discount
		modelMapper.map(discountUpdate, discount);
		
		// update discount
		Discount discountNew = new Discount();
		discountNew = update(discount);
		
		return getDiscountDTO(discountNew);
	}

	@Override
	public DiscountDTO getDetailDiscount(String discountId) {
		Discount discount = getDetail(discountId);

		return getDiscountDTO(discount);
	}

	@Override
	public List<DiscountDTO> getAllDiscount() {
		List<Discount> discountList = new ArrayList<>(getAll());

		return getDiscountDTOList(discountList);
	}

	@Override
	public List<DiscountDTO> searchDiscount(String keyword) {
		// search account with keyword
		List<Discount> discountList = search(keyword);

		return getDiscountDTOList(discountList);
	}

	@Override
	public DiscountDTO getDiscountByCode(String discountCode) {
		// Check exists
		if (!discountRepository.existsByDiscountCode(discountCode)) {
			throw new CustomException("Cannot find discount");
		}
		
		// Find discount
		Discount discount = discountRepository.findByDiscountCode(discountCode).get();
		
		return getDiscountDTO(discount);
	}

	@Override
	public int getActualDiscountValue(String discountCode,int totalPrice) {
		// find discount
		Optional<Discount> discountFind = discountRepository.findByDiscountCode(discountCode);
		if (discountFind.isEmpty()) {
			throw new CustomException("Cannot find discount");
		}
		Discount discount = discountFind.get();
		
		// check valid
		boolean checkValid = true;
		checkValid = checkValid(discount);
		if (checkValid == false) {
			throw new CustomException("Expired discount code");
		}
		
		// check min order
		if (discount.getMinOrder() > totalPrice) {
			throw new CustomException("The order value has not reached the minimum order value");
		}
		
		// get actual discount value
		int actualDiscountValue = 0;
		actualDiscountValue = totalPrice * discount.getDiscountValue() / 100;
		
		// check max discount
		if (actualDiscountValue > discount.getMaxDiscount()) {
			actualDiscountValue = discount.getMaxDiscount();
		}
		
		return actualDiscountValue;
	}

	@Override
	public void usedDiscount(String discountCode) {
		// find discount
		Optional<Discount> discountFind = discountRepository.findByDiscountCode(discountCode);
		if (discountFind.isEmpty()) {
			throw new CustomException("Cannot find discount");
		}
		Discount discount = new Discount();
		discount = discountFind.get();
		
		// update number of code used
		int numberOfCodeUsed = discount.getNumberOfCodeUsed() + 1;
		discount.setNumberOfCodeUsed(numberOfCodeUsed);
		
		// update discount
		
		// update discount
		Discount discountNew = new Discount();
		discountNew = discountRepository.save(discount);
	}

	@Override
	public List<DiscountDTO> listDiscountSearch(String keyword) {
		List<Discount> discountList = new ArrayList<>();
		discountList.addAll(search2(keyword));
		
		return getDiscountDTOList(discountList);
	}

	@Override
	public List<DiscountDTO> listDiscountFilter(HashMap<String, String> discountFilter,
			List<DiscountDTO> discountDTOList) {
		List<DiscountDTO> discountDTOListClone = new ArrayList<>();
		discountDTOListClone.addAll(discountDTOList);
		for (DiscountDTO itemDiscountDTO : discountDTOListClone) {
			discountFilter.forEach((fieldName, fieldValue) -> {
				// filter of account class
				for (Field itemField : DiscountDTO.class.getDeclaredFields()) {
					itemField.setAccessible(true);
					// field of account
					if (itemField.getName().equals(fieldName)) {
						try {
							String value = String.valueOf(itemField.get(itemDiscountDTO));
							String valueNew = StringUtil.getNormalAlphabet(value);
							String fieldValueNew = StringUtil.getNormalAlphabet(fieldValue);
							if (!valueNew.contains(fieldValueNew)) {
								discountDTOList.remove(itemDiscountDTO);
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
							String value = String.valueOf(itemField.get(itemDiscountDTO));
							String valueNew = StringUtil.getNormalAlphabet(value);
							String fieldValueNew = StringUtil.getNormalAlphabet(fieldValue);
							if (!valueNew.contains(fieldValueNew)) {
								discountDTOList.remove(itemDiscountDTO);
								break;
							}
						} catch (Exception e) {
							
						}
					}
				}
				
			});
			if (discountDTOList.size() <= 0) {
				break;
			}
		}
		
		return discountDTOList;
	}

	@Override
	public List<DiscountDTO> listDiscountSort(Sort sort, List<DiscountDTO> discountDTOList) {
		Collections.sort(discountDTOList, new Comparator<DiscountDTO>() {
            @Override
            public int compare(DiscountDTO discountDTO1, DiscountDTO discountDTO2) {
            	int result = 0;
            	
            	// sort of account class
        		for (Field itemField : DiscountDTO.class.getDeclaredFields()) {
        			itemField.setAccessible(true);
        			// field of account
    				if (sort.getSortBy().equals(itemField.getName())) {
    					try {
		            		String string1 = String.valueOf(itemField.get(discountDTO1));
		            		String string2 = String.valueOf(itemField.get(discountDTO2));
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
		            		String string1 = String.valueOf(itemField.get(discountDTO1));
		            		String string2 = String.valueOf(itemField.get(discountDTO2));
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
		
		return discountDTOList;
	}
}
