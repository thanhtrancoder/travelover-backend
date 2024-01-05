package hcmute.kltn.Backend.model.commission.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import hcmute.kltn.Backend.exception.CustomException;
import hcmute.kltn.Backend.model.account.service.IAccountDetailService;
import hcmute.kltn.Backend.model.commission.dto.CommissionDTO;
import hcmute.kltn.Backend.model.commission.dto.entity.Commission;
import hcmute.kltn.Backend.model.commission.repository.CommissionRepository;
import hcmute.kltn.Backend.model.commission.service.ICommissionService;
import hcmute.kltn.Backend.model.generatorSequence.service.IGeneratorSequenceService;
import hcmute.kltn.Backend.util.EntityUtil;
import hcmute.kltn.Backend.util.LocalDateTimeUtil;
import hcmute.kltn.Backend.util.StringUtil;

@Service
public class CommissionService implements ICommissionService{
	@Autowired
	private IGeneratorSequenceService iGeneratorSequenceService;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private IAccountDetailService iAccountDetailService;
	@Autowired
    private MongoTemplate mongoTemplate;
	@Autowired
	private CommissionRepository commissionRepository;
	
	private String getCollectionName() {
        String collectionName = mongoTemplate.getCollectionName(Commission.class);
        return collectionName;
    }
	
	private void checkFieldCondition(Commission commission) {
		
		// check null
		if(commission.getName() == null || commission.getName().equals("")) {
			throw new CustomException("Name is not null");
		}
		if(commission.getRate() < 0 || commission.getRate() > 100) {
			throw new CustomException("The rate should be between 0 and 100");
		}
		
		// check unique
		if(commission.getCommissionId() == null || commission.getCommissionId().equals("")) {
			if(commissionRepository.existsByName(commission.getName().trim())) {
				throw new CustomException("Name is already");
			}
		} else {
			List<Commission> commissionNameList = commissionRepository.findAll();

			System.out.println("commission = " + commission);
			
			
			for(Commission itemCommission : commissionNameList) {
				
				System.out.println("itemCommission = " + itemCommission);
				
				if (itemCommission.getName().equals(commission.getName())
						&& !itemCommission.getCommissionId().equals(commission.getCommissionId())) {
					throw new CustomException("Name is already");
				}
			}
		}
	}
	
	private Commission create(Commission commission) {
		// check field condition
		checkFieldCondition(commission);
    	
		// Set default value
		String commissionId = iGeneratorSequenceService.genId(getCollectionName());
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		
		commission.setCommissionId(commissionId);
		commission.setStatus(false);
		commission.setCreatedBy(accountId);
		commission.setCreatedAt2(currentDate);
		commission.setLastModifiedBy(accountId);
		commission.setLastModifiedAt2(currentDate);
		
		// create discount
		Commission commissionNew = new Commission();
		commissionNew = commissionRepository.save(commission);
		
		return commissionNew;
	}
	
	private Commission update(Commission commission) {
    	// Check exists
		if (!commissionRepository.existsById(commission.getCommissionId())) {
			throw new CustomException("Cannot find commission");
		}
		
		// check field condition
		checkFieldCondition(commission);
    	
    	// Set default value
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		commission.setLastModifiedBy(accountId);
		commission.setLastModifiedAt2(currentDate);
		
		// update discount
		Commission commissionNew = new Commission();
		commissionNew = commissionRepository.save(commission);
		
		return commissionNew;
    }
	
	private Commission getDetail(String commissionId) {
		// Check exists
		if (!commissionRepository.existsById(commissionId)) {
			throw new CustomException("Cannot find commission");
		}
		
		// Find discount
		Commission commission = commissionRepository.findById(commissionId).get();
		
		return commission;
	}
	
	private List<Commission> getAll() {
		// Find discount
		List<Commission> list = commissionRepository.findAll();
		
		return list;
	}
	
	private void delete(String commissionId) {
		// Check exists
		if (commissionRepository.existsById(commissionId)) {
			commissionRepository.deleteById(commissionId);
		}
	}
	
	private List<Commission> search(String keyword) {
		// init tour List
		List<Commission> commissionList = new ArrayList<>();
		commissionList.addAll(commissionRepository.findAll());
		if (keyword != null) {
			keyword = keyword.trim();
		}

		if (keyword != null && !keyword.isEmpty()) {
			if (commissionList != null) {
				List<Commission> commissionListClone = new ArrayList<>();
				commissionListClone.addAll(commissionList);
				for (Commission itemCommission : commissionListClone) {
					String keywordNew = StringUtil.getNormalAlphabet(keyword);
					String fieldNew = "";
					try {
						fieldNew = StringUtil.getNormalAlphabet(EntityUtil.getAllValue(itemCommission));
					} catch (Exception e) {
						// TODO: handle exception
					}
					
					
					if (!fieldNew.contains(keywordNew)) {
						commissionList.remove(itemCommission);
						if (commissionList.size() <= 0) {
							break;
						}
					}
				}
			}
		}

		return commissionList;
	}
	
	private CommissionDTO getCommissionDTO(Commission commission) {
		// mapping discountDTO
		CommissionDTO commissionDTONew = new CommissionDTO();
		modelMapper.map(commission, commissionDTONew);
		
		return commissionDTONew;
	}
	
	private List<CommissionDTO> getCommissionDTOList(List<Commission> commissionList) {
		List<CommissionDTO> commissionDTOList = new ArrayList<>();
		for (Commission itemCommission : commissionList) {
			commissionDTOList.add(getCommissionDTO(itemCommission));
		}
		return commissionDTOList;
	}
	
	@Override
	public CommissionDTO createCommission(CommissionDTO commissionDTO) {
		// mapping discount
		Commission commission = new Commission();
		modelMapper.map(commissionDTO, commission);
		
		// check field condition
		checkFieldCondition(commission);

		// create discount
		Commission commissionNew = new Commission();
		commissionNew = create(commission);
		
		return getCommissionDTO(commissionNew);
	}

	@Override
	public CommissionDTO updateCommission(CommissionDTO commissionDTO) {
		// get discount from database
		Commission commission = getDetail(commissionDTO.getCommissionId());
		
		if (commission.getStatus() == true) {
			throw new CustomException("Unable to update commision that is applied to the system");
		} else if (commission.getEndDate() != null) {
			throw new CustomException("Can't update used commissions");
		}

		commission.setName(commissionDTO.getName());
		commission.setDescription(commissionDTO.getDescription());
		commission.setRate(commissionDTO.getRate());
		
		// update discount
		Commission commissionNew = new Commission();
		commissionNew = update(commission);
		
		return getCommissionDTO(commissionNew);
	}

	@Override
	public CommissionDTO getDetailCommission(String commissionId) {
		Commission commission = getDetail(commissionId);

		return getCommissionDTO(commission);
	}

	@Override
	public List<CommissionDTO> getAllCommission() {
		List<Commission> commissionList = new ArrayList<>(getAll());

		return getCommissionDTOList(commissionList);
	}

	@Override
	public List<CommissionDTO> searchCommission(String keyword) {
		// search account with keyword
		List<Commission> commissionList = search(keyword);

		return getCommissionDTOList(commissionList);
	}

	@Override
	public CommissionDTO enableCommission(String commissionId) {
		Commission commission = new Commission();
		commission = getDetail(commissionId);
		
		if (commission.getStatus() == true) {
			getCommissionDTO(commission);
		}
		
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		commission.setStatus(true);
		commission.setStartDate(currentDate);
		
		Commission commissionNew = new Commission();
		commissionNew = update(commission);
		
		List<Commission> commissionList = new ArrayList<>();
		commissionList.addAll(getAll());
		for (Commission itemCommission : commissionList) {
			if (!itemCommission.getCommissionId().equals(commissionId) && itemCommission.getStatus() == true) {
				itemCommission.setStatus(false);
				itemCommission.setEndDate(currentDate);
				update(itemCommission);
			}
		}
		
		return getCommissionDTO(commissionNew);
	}

	@Override
	public void initData(CommissionDTO commissionDTO) {
		// mapping discount
		Commission commission = new Commission();
		modelMapper.map(commissionDTO, commission);
		
		// Set default value
		String commissionId = iGeneratorSequenceService.genId(getCollectionName());
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		
		commission.setCommissionId(commissionId);
		commission.setStartDate(currentDate);
		commission.setStatus(true);
		commission.setCreatedBy("system");
		commission.setCreatedAt2(currentDate);
		commission.setLastModifiedBy("system");
		commission.setLastModifiedAt2(currentDate);
		
		// create discount
		commissionRepository.save(commission);
	}

	
	@Override
	public CommissionDTO getCurrentCommission() {
		List<Commission> commissionList = new ArrayList<>();
		commissionList.addAll(getAll());
		for (Commission itemCommission : commissionList) {
			if (itemCommission.getStatus() == true) {
				return getCommissionDTO(itemCommission);
			}
		}
		
		return null;
	}

}
