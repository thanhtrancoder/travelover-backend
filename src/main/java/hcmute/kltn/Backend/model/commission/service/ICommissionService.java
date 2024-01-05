package hcmute.kltn.Backend.model.commission.service;

import java.util.List;

import hcmute.kltn.Backend.model.commission.dto.CommissionDTO;

public interface ICommissionService {
	public CommissionDTO createCommission(CommissionDTO commissionDTO);
	public CommissionDTO updateCommission(CommissionDTO commissionDTO);
	public CommissionDTO getDetailCommission(String commissionId);
	public CommissionDTO enableCommission(String commissionId);
	public CommissionDTO getCurrentCommission();
	
	public void initData(CommissionDTO commissionDTO);
	
	public List<CommissionDTO> getAllCommission();
	public List<CommissionDTO> searchCommission(String keyword);
}
