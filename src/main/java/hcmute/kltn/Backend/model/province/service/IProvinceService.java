package hcmute.kltn.Backend.model.province.service;

import java.util.List;

import hcmute.kltn.Backend.model.province.dto.DistrictDTO;
import hcmute.kltn.Backend.model.province.dto.LocationDTO;
import hcmute.kltn.Backend.model.province.dto.ProvinceDTO;
import hcmute.kltn.Backend.model.province.dto.UpdateVisitor;
import hcmute.kltn.Backend.model.province.dto.WardDTO;
import hcmute.kltn.Backend.model.province.dto.entity.Province;

public interface IProvinceService {
	public void autoUpdate();
	public List<ProvinceDTO> getAllProvince();
	public List<DistrictDTO> getAllDistrict(String provinceCode);
	public List<WardDTO> getAllWard(String provinceCode, String districtCode);
	public List<Province> getAllLocation();
	public List<LocationDTO> searchLocation(String keyword);
	public List<LocationDTO> getHotLocation();
	
	public void updateNumberOfVisitor(UpdateVisitor updateVisitor);
}
