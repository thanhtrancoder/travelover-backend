package hcmute.kltn.Backend.model.province.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.gson.internal.LinkedTreeMap;

import hcmute.kltn.Backend.exception.CustomException;
import hcmute.kltn.Backend.model.base.externalAPI.dto.ApiCallResponse;
import hcmute.kltn.Backend.model.base.externalAPI.service.IExternalAPIService;
import hcmute.kltn.Backend.model.province.dto.DistrictDTO;
import hcmute.kltn.Backend.model.province.dto.LocationDTO;
import hcmute.kltn.Backend.model.province.dto.ProvinceDTO;
import hcmute.kltn.Backend.model.province.dto.UpdateVisitor;
import hcmute.kltn.Backend.model.province.dto.WardDTO;
import hcmute.kltn.Backend.model.province.dto.entity.Province;
import hcmute.kltn.Backend.model.province.dto.extend.District;
import hcmute.kltn.Backend.model.province.dto.extend.Ward;
import hcmute.kltn.Backend.model.province.repository.ProvinceRepository;
import hcmute.kltn.Backend.model.province.service.IProvinceService;
import hcmute.kltn.Backend.util.LocalDateTimeUtil;
import hcmute.kltn.Backend.util.StringUtil;

@Service
public class ProvinceService implements IProvinceService{
	@Autowired
	private ProvinceRepository provinceRepository;
	@Autowired
	private IExternalAPIService iExternalAPIService;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
    private MongoTemplate mongoTemplate;
	
	@Value("${provinces.open-api.vn.url}") 
	private String url;
	@Value("${provinces.open-api.vn.param.name}") 
	private String paramName;
	@Value("${provinces.open-api.vn.param.value}") 
	private String paramValue;
	
	private List<Ward> searchWard(String keyword) {
		// init District List
		List<Province> provinceList = new ArrayList<>();
		provinceList = provinceRepository.findAll();
		
		List<Ward> wardList = new ArrayList<>();
		if (provinceList != null) {
			for (Province itemProvince : provinceList) {
				if (itemProvince.getDistrict() != null) {
					for (District itemDistrict : itemProvince.getDistrict()) {
						if (itemDistrict.getWard() != null) {
							for (Ward itemWard : itemDistrict.getWard()) {
								wardList.add(itemWard);
							}
						}
					}
				}
			}
		}
		

		keyword = keyword.trim();
		
		if(keyword != null && !keyword.isEmpty()) {
			if (wardList != null) {
				List<Ward> wardListClone = new ArrayList<>();
				wardListClone.addAll(wardList);
				for (Ward itemWard : wardListClone) {
					String keywordNew = StringUtil.getNormalAlphabet(keyword);
					String nameNew = StringUtil.getNormalAlphabet(itemWard.getName());
					String divisionTypeNew = StringUtil.getNormalAlphabet(itemWard.getDivisionType());
					
					if (!nameNew.contains(keywordNew) && !divisionTypeNew.contains(keywordNew)) {
						wardList.remove(itemWard);
						if (wardList.size() <= 0) {
							break;
						}
					}
				}
			}
		} 
		
		return wardList;
	}

	private List<District> searchDistrict(String keyword) {
		// init District List
		List<Province> provinceList = new ArrayList<>();
		provinceList = provinceRepository.findAll();
		
		List<District> districtList = new ArrayList<>();
		if (provinceList != null) {
			for (Province itemProvince : provinceList) {
				if (itemProvince.getDistrict() != null) {
					for (District itemDistrict : itemProvince.getDistrict()) {
						districtList.add(itemDistrict);
					}
				}	
			}
		}
		

		keyword = keyword.trim();
		
		if(keyword != null && !keyword.isEmpty()) {
			if (districtList != null) {
				List<District> districtListClone = new ArrayList<>();
				districtListClone.addAll(districtList);
				for (District itemDistrict : districtListClone) {
					String keywordNew = StringUtil.getNormalAlphabet(keyword);
					String nameNew = StringUtil.getNormalAlphabet(itemDistrict.getName());
					String divisionTypeNew = StringUtil.getNormalAlphabet(itemDistrict.getDivisionType());
					
					if (!nameNew.contains(keywordNew) && !divisionTypeNew.contains(keywordNew)) {
						districtList.remove(itemDistrict);
						if (districtList.size() <= 0) {
							break;
						}
					}
				}
			}
		} 
		
		return districtList;
	}
	
	private List<Province> searchProvince(String keyword) {
		// init Province List
		List<Province> provinceList = new ArrayList<>();
		provinceList = provinceRepository.findAll();
		keyword = keyword.trim();
		
		if(keyword != null && !keyword.isEmpty()) {
			if (provinceList != null) {
				List<Province> provinceListClone = new ArrayList<>();
				provinceListClone.addAll(provinceList);
				for (Province itemProvince : provinceListClone) {
					String keywordNew = StringUtil.getNormalAlphabet(keyword);
					String nameNew = StringUtil.getNormalAlphabet(itemProvince.getName());
					String divisionTypeNew = StringUtil.getNormalAlphabet(itemProvince.getDivisionType());
					
					if (!nameNew.contains(keywordNew) && !divisionTypeNew.contains(keywordNew)) {
						provinceList.remove(itemProvince);
						if (provinceList.size() <= 0) {
							break;
						}
					}
				}
			}
		} 
		
		return provinceList;
	}
	
	private List<LocationDTO> search(String keyword) {
		List<LocationDTO> locationDTOList = new ArrayList<>();
		
		// search Province with keyword
		List<Province> provinceList = new ArrayList<>();
		provinceList = searchProvince(keyword);
		for (Province itemProvince : provinceList) {
			LocationDTO locationDTO = new LocationDTO();
			modelMapper.map(itemProvince, locationDTO);
			locationDTO.setLocationType("province");	
			locationDTOList.add(locationDTO);
		}
		
		// search District with keyword
		List<District> districtList = new ArrayList<>();
		districtList = searchDistrict(keyword);
		for (District itemDistrict : districtList) {
			LocationDTO locationDTO = new LocationDTO();
			modelMapper.map(itemDistrict, locationDTO);
			locationDTO.setLocationType("district");	
			locationDTOList.add(locationDTO);
		}
		

		// search Ward with keyword
		List<Ward> wardList = new ArrayList<>();
		wardList = searchWard(keyword);
		for (Ward itemWard : wardList) {
			LocationDTO locationDTO = new LocationDTO();
			modelMapper.map(itemWard, locationDTO);
			locationDTO.setLocationType("ward");	
			locationDTOList.add(locationDTO);
		}
		
		// default sort
		Map<String, Integer> sortOrder = new HashMap<>();
		sortOrder.put("province", 1);
		sortOrder.put("district", 2);
		sortOrder.put("ward", 3);
		
		Collections.sort(locationDTOList, new Comparator<LocationDTO>() {
            @Override
            public int compare(LocationDTO locationDTO1, LocationDTO locationDTO2) {
//                Integer order1 = sortOrder.get(locationDTO1.getLocationType());
//                Integer order2 = sortOrder.get(locationDTO2.getLocationType());
//                int result = order1.compareTo(order2);
//                
//                if (result == 0) {
//                    result = locationDTO1.getCodeName().compareTo(locationDTO2.getCodeName());
//                }
                
                int result = locationDTO1.getCodeName().compareTo(locationDTO2.getCodeName());
                
                return result;
            }
        });
		
		return locationDTOList;
	}
	
	private Province getDetailProvince(String provinceCode) {
		Optional<Province> provinceFind = provinceRepository.findByCode(provinceCode);
		if (provinceFind.isEmpty()) {
			throw new CustomException("Cannot find province by code");
		}
		Province province = new Province();
		province = provinceFind.get();
		
		return province;
	}
	
	private District getDetailDistrict(String provinceCode, String districtCode) {
		Province province = new Province();
		province = getDetailProvince(provinceCode);
		if (province.getDistrict() != null) {
			for (District itemDistrict : province.getDistrict()) {
				if (itemDistrict.getCode().equals(districtCode)) {
					return itemDistrict;
				}
			}
		}
		
		throw new CustomException("annot find district by province code and district code");
	}
	
	private Ward getDetailWard(String provinceCode, String districtCode, String wardCode) {
		District district = new District();
		district = getDetailDistrict(provinceCode, districtCode);
		if (district.getWard() != null) {
			for (Ward itemWard : district.getWard()) {
				if (itemWard.getCode().equals(wardCode)) {
					return itemWard;
				}
			}
		}
		
		throw new CustomException("annot find ward by province code and district code and ward code");
	}
	
	@Override
	public void autoUpdate() {
		List<Province> provinceOldList = new ArrayList<>();
		provinceOldList = provinceRepository.findAll();
		
		HashMap<String, String> param = new HashMap<>();
		param.put(paramName, paramValue);
		
		ApiCallResponse apiCallResponse = new ApiCallResponse();
		apiCallResponse = iExternalAPIService.get(url, null, param);
		
		if (apiCallResponse.getStatus() != HttpStatus.OK) {
			throw new CustomException("An error occurred during the call of the province api");
		}
		
		// get province
		List<Object> provinceList = new ArrayList<>();
		provinceList = (List<Object>) apiCallResponse.getBody();
		if (provinceList.size() > 0) {
			for (Object itemProvince : provinceList) {
				Province province = new Province();
				HashMap<String, Object> hashMapProvince = new HashMap<>();
				
				// check hashMap
				if (itemProvince.getClass() == HashMap.class) {
					HashMap<String, Object> hashMap = (HashMap<String, Object>) itemProvince;
					hashMapProvince.putAll(hashMap);
				}

				// check LinkedHashMap
				if (itemProvince.getClass() == LinkedTreeMap.class) {
					HashMap<String, Object> hashMap = new HashMap<>((LinkedTreeMap<String, Object>) itemProvince);
					hashMapProvince.putAll(hashMap);
				}
				
				province.setProvinceId(StringUtil.getIntegerString(hashMapProvince.get("code").toString()));
				province.setName(hashMapProvince.get("name").toString());
				province.setCode(StringUtil.getIntegerString(hashMapProvince.get("code").toString()));
				province.setCodeName(hashMapProvince.get("codename").toString());
				province.setDivisionType(hashMapProvince.get("division_type").toString());
				province.setPhoneCode(StringUtil.getIntegerString(hashMapProvince.get("phone_code").toString()));
				province.setNumberOfVisitor(0);
				// get number of visitor old
				if (provinceOldList != null) {
					for (Province itemProvinceOld : provinceOldList) {
						if (itemProvinceOld.getCode().equals(province.getCode())) {
							province.setNumberOfVisitor(itemProvinceOld.getNumberOfVisitor());
							break;
						}
					}
						
				}
				province.setUpdatedAt(LocalDateTimeUtil.getCurentDate());
				
				// get district
				List<District> districtSave = new ArrayList<>();
				List<Object> districtList = new ArrayList<>();
				districtList = (List<Object>) hashMapProvince.get("districts");
				if (districtList.size() > 0) {
					for (Object itemDistrict : districtList) {
						District district = new District();
						HashMap<String, Object> hashMapDistrict = new HashMap<>();
						
						// check hashMap
						if (itemDistrict.getClass() == HashMap.class) {
							HashMap<String, Object> hashMap = (HashMap<String, Object>) itemDistrict;
							hashMapDistrict.putAll(hashMap);
						}

						// check LinkedHashMap
						if (itemDistrict.getClass() == LinkedTreeMap.class) {
							HashMap<String, Object> hashMap = new HashMap<>((LinkedTreeMap<String, Object>) itemDistrict);
							hashMapDistrict.putAll(hashMap);
						}

						district.setName(hashMapDistrict.get("name").toString());
						district.setCode(StringUtil.getIntegerString(hashMapDistrict.get("code").toString()));
						district.setCodeName(hashMapDistrict.get("codename").toString());
						district.setDivisionType(hashMapDistrict.get("division_type").toString());
						district.setShortCodeName(hashMapDistrict.get("short_codename").toString());
						district.setNumberOfVisitor(0);
						// get number of visitor old
						if (provinceOldList != null) {
							for (Province itemProvinceOld : provinceOldList) {
								if (itemProvinceOld.getDistrict() != null) {
									for (District itemDistrictOld : itemProvinceOld.getDistrict()) {
										if (itemDistrictOld.getCode().equals(district.getCode())) {
											district.setNumberOfVisitor(itemDistrictOld.getNumberOfVisitor());
											break;
										}
									}
								}
								
							}
								
						}
						district.setUpdatedAt(LocalDateTimeUtil.getCurentDate());
						
						// get commune
						List<Ward> wardSave = new ArrayList<>();
						List<Object> wardList = new ArrayList<>();
						wardList = (List<Object>) hashMapDistrict.get("wards");
						if (wardList.size() > 0) {
							for (Object itemWard : wardList) {
								Ward ward = new Ward();
								HashMap<String, Object> hashMapWard = new HashMap<>();
								
								// check hashMap
								if (itemWard.getClass() == HashMap.class) {
									HashMap<String, Object> hashMap = (HashMap<String, Object>) itemWard;
									hashMapWard.putAll(hashMap);
								}

								// check LinkedHashMap
								if (itemWard.getClass() == LinkedTreeMap.class) {
									HashMap<String, Object> hashMap = new HashMap<>((LinkedTreeMap<String, Object>) itemWard);
									hashMapWard.putAll(hashMap);
								}

								ward.setName(hashMapWard.get("name").toString());
								ward.setCode(StringUtil.getIntegerString(hashMapWard.get("code").toString()));
								ward.setCodeName(hashMapWard.get("codename").toString());
								ward.setDivisionType(hashMapWard.get("division_type").toString());
								ward.setShortCodeName(hashMapWard.get("short_codename").toString());
								ward.setNumberOfVisitor(0);
								// get number of visitor old
								if (provinceOldList != null) {
									for (Province itemProvinceOld : provinceOldList) {
										if (itemProvinceOld.getDistrict() != null) {
											for (District itemDistrictOld : itemProvinceOld.getDistrict()) {
												if (itemDistrictOld.getWard() != null) {
													for (Ward itemWardOld : itemDistrictOld.getWard()) {
														if (itemWardOld.getCode().equals(ward.getCode())) {
															ward.setNumberOfVisitor(itemWardOld.getNumberOfVisitor());
															break;
														}
													}
												}
												
											}
										}
										
									}
										
								}
								ward.setUpdatedAt(LocalDateTimeUtil.getCurentDate());
								
								wardSave.add(ward);
							}
							district.setWard(wardSave);
						}
						
						districtSave.add(district);
					}
					
					province.setDistrict(districtSave);
				}
				
				provinceRepository.save(province);
	        }
		}
	}

	@Override
	public List<ProvinceDTO> getAllProvince() {
		List<Province> provinceList = new ArrayList<>();
		provinceList.addAll(provinceRepository.findAll());
		
		List<ProvinceDTO> provinceDTOList = new ArrayList<>();
		for (Province itemProvince : provinceList) {
			ProvinceDTO provinceDTO = new ProvinceDTO();
			modelMapper.map(itemProvince, provinceDTO);
			provinceDTOList.add(provinceDTO);
		}
		
		return provinceDTOList;
	}

	@Override
	public List<Province> getAllLocation() {
		List<Province> provinceList = new ArrayList<>();
		provinceList.addAll(provinceRepository.findAll());

		return provinceList;
	}

	@Override
	public List<DistrictDTO> getAllDistrict(String provinceCode) {
		Optional<Province> provinceFind = provinceRepository.findByCode(provinceCode);
		if (provinceFind.isEmpty()) {
			throw new CustomException("Cannot find district by province code");
		} else {
			Province province = provinceFind.get();
			List<DistrictDTO> districtDTOList = new ArrayList<>();
			for (District itemDistrict : province.getDistrict()) {
				DistrictDTO districtDTO = new DistrictDTO();
				modelMapper.map(itemDistrict, districtDTO);
				districtDTOList.add(districtDTO);
			}
			return districtDTOList;
		}
	}

	@Override
	public List<WardDTO> getAllWard(String provinceCode, String districtCode) {
		Optional<Province> provinceFind = provinceRepository.findByCode(provinceCode);
		if (provinceFind.isEmpty()) {
			throw new CustomException("Cannot find ward by province code and district code");
		} else {
			Province province = provinceFind.get();
			for (District itemDistrict : province.getDistrict()) {
				if (itemDistrict.getCode().equals(districtCode)) {
					List<WardDTO> wardDTOList = new ArrayList<>();
					for (Ward itemWard : itemDistrict.getWard()) {
						WardDTO wardDTO = new WardDTO();
						modelMapper.map(itemWard, wardDTO);
						wardDTOList.add(wardDTO);
					}
					return wardDTOList;
				}
			}
			throw new CustomException("Cannot find ward by province code and district code");
		}

	}

	@Override
	public List<LocationDTO> searchLocation(String keyword) {
		List<LocationDTO> locationDTOList = new ArrayList<>();
		locationDTOList = search(keyword);
		
		return locationDTOList;
	}

	@Override
	public void updateNumberOfVisitor(UpdateVisitor updateVisitor) {
		Province province = new Province();
		province = getDetailProvince(updateVisitor.getProvinceCode());
		// update for province
		province.setNumberOfVisitor(province.getNumberOfVisitor() + updateVisitor.getNumberOfVisitor());
		province.setUpdatedAt(LocalDateTimeUtil.getCurentDate());
		
		// update for district
		if (province.getDistrict() != null) {
			List<District> districtList = new ArrayList<>();
			districtList.addAll(province.getDistrict());
			for (District itemDistrict : province.getDistrict()) {
				if (itemDistrict.getCode().equals(updateVisitor.getDistrictCode())) {
					District district = new District();
					modelMapper.map(itemDistrict, district);
					district.setNumberOfVisitor(district.getNumberOfVisitor() + updateVisitor.getNumberOfVisitor());
					district.setUpdatedAt(LocalDateTimeUtil.getCurentDate());
					
					// update ward
					if (district.getWard() != null) {
						List<Ward> wardList = new ArrayList<>();
						wardList.addAll(district.getWard());
						for (Ward itemWard : district.getWard()) {
							if (itemWard.getCode().equals(updateVisitor.getWardCode())) {
								Ward ward = new Ward();
								modelMapper.map(itemWard, ward);
								ward.setNumberOfVisitor(ward.getNumberOfVisitor() + updateVisitor.getNumberOfVisitor());
								ward.setUpdatedAt(LocalDateTimeUtil.getCurentDate());
								
								int wardIndex = wardList.indexOf(itemWard);
								wardList.set(wardIndex, ward);
								district.setWard(wardList);
							}
						}
					}
					
					int districtIndex = districtList.indexOf(itemDistrict);
					districtList.set(districtIndex, district);
					province.setDistrict(districtList);
				}
			}
		}
		
		provinceRepository.save(province);
	}

	@Override
	public List<LocationDTO> getHotLocation() {
		// get location
		List<Province> provinceList = new ArrayList<>();
		provinceList = provinceRepository.findAll();
		
		// sort by numberOfVisitor
		Collections.sort(provinceList, new Comparator<Province>() {
            @Override
            public int compare(Province province1, Province province2) {
                int result = Integer.compare(province1.getNumberOfVisitor(), province2.getNumberOfVisitor());
                
                return -result;
            }
        });
		
		// get 10 item from tour list
		if (provinceList.size() > 10) {
			List<Province> provinceListClone = new ArrayList<>();
			provinceListClone.addAll(provinceList);
			for (Province itemProvince : provinceListClone) {
				int index = provinceList.indexOf(itemProvince);
				if (index >= 10) {
					provinceList.remove(itemProvince);
					if (provinceList.size() <= 0) {
						break;
					}
				}
			}
		}
		
		// get location
		List<LocationDTO> locationDTOList = new ArrayList<>();
		for (Province itemProvince : provinceList) {
			LocationDTO locationDTO = new LocationDTO();
			modelMapper.map(itemProvince, locationDTO);
			locationDTO.setLocationType("province");
			locationDTOList.add(locationDTO);
		}
		return locationDTOList;
	}


	
}
