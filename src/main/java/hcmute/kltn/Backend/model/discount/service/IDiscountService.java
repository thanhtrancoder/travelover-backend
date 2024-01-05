package hcmute.kltn.Backend.model.discount.service;

import java.util.HashMap;
import java.util.List;

import hcmute.kltn.Backend.model.base.Sort;
import hcmute.kltn.Backend.model.discount.dto.DiscountCreate;
import hcmute.kltn.Backend.model.discount.dto.DiscountDTO;
import hcmute.kltn.Backend.model.discount.dto.DiscountUpdate;

public interface IDiscountService {
	public DiscountDTO createDiscount(DiscountCreate discountCreate);
	public DiscountDTO updateDiscount(DiscountUpdate discountUpdate);
	public DiscountDTO getDetailDiscount(String discountId);
	public DiscountDTO getDiscountByCode(String discountCode);
	
	public List<DiscountDTO> getAllDiscount();
	public List<DiscountDTO> searchDiscount(String keyword);
	public List<DiscountDTO> listDiscountSearch(String keyword);
	public List<DiscountDTO> listDiscountFilter(HashMap<String, String> discountFilter, List<DiscountDTO> discountDTOList);
	public List<DiscountDTO> listDiscountSort(Sort sort, List<DiscountDTO> discountDTOList);
	
	public int getActualDiscountValue(String discountCode,int totalPrice);
	
	public void usedDiscount(String discountCode);
}
