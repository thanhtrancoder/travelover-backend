package hcmute.kltn.Backend.model.url.service.impl;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import hcmute.kltn.Backend.exception.CustomException;
import hcmute.kltn.Backend.model.account.service.IAccountDetailService;
import hcmute.kltn.Backend.model.generatorSequence.service.IGeneratorSequenceService;
import hcmute.kltn.Backend.model.url.dto.URLDTO;
import hcmute.kltn.Backend.model.url.dto.entity.URL;
import hcmute.kltn.Backend.model.url.repository.URLRepository;
import hcmute.kltn.Backend.model.url.service.IURLService;
import hcmute.kltn.Backend.util.LocalDateTimeUtil;
import hcmute.kltn.Backend.util.StringUtil;

public class URLService implements IURLService{
	@Autowired
	private IGeneratorSequenceService iGeneratorSequenceService;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private IAccountDetailService iAccountDetailService;
	@Autowired
    private MongoTemplate mongoTemplate;
	@Autowired
	private URLRepository urlRepository;
	
    private String getCollectionName() {
        String collectionName = mongoTemplate.getCollectionName(URL.class);
        return collectionName;
    }
	
    private void checkFieldCondition(URL url) {
		// check null
		if(url.getName() == null || url.getName().equals("")) {
			throw new CustomException("Name is not null");
		}
		if(url.getUrl() == null || url.getUrl().equals("")) {
			throw new CustomException("URL is not null");
		}
		
		// check unique
		if(url.getUrlId() == null || url.getUrlId().equals("")) {
			if(urlRepository.existsByName(url.getName().trim())) {
				throw new CustomException("Name is already");
			}
		} else {
			URL urlFind = urlRepository.findById(url.getUrlId()).get();
			List<URL> nameList = urlRepository.findAllByName(url.getName());
			for(URL itemURL : nameList) {
				if (itemURL.getName() == urlFind.getName() && itemURL.getUrlId() != urlFind.getUrlId()) {
					throw new CustomException("Name is already");
				}
			}
		}
	}
    
	private URL create(URL url) {
    	// check field condition
		checkFieldCondition(url);
    	
		// Set default value
		String urlId = iGeneratorSequenceService.genId(getCollectionName());
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		
		url.setUrlId(urlId);
		url.setStatus(true);
		url.setCreatedBy(accountId);
		url.setCreatedAt2(currentDate);
		url.setLastModifiedBy(accountId);
		url.setLastModifiedAt2(currentDate);
		
		// create url
		URL urlNew = new URL();
		urlNew = urlRepository.save(url);
		
		return urlNew;
	}

    private URL update(URL url) {
    	// Check exists
		if (!urlRepository.existsById(url.getUrlId())) {
			throw new CustomException("Cannot find url");
		}
		
		// check field condition
		checkFieldCondition(url);
    	
    	// Set default value
		String accountId = iAccountDetailService.getCurrentAccount().getAccountId();
		LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
		url.setLastModifiedBy(accountId);
		url.setLastModifiedAt2(currentDate);
		
		// update tour
		URL urlNew = new URL();
		urlNew = urlRepository.save(url);
		
		return urlNew;
    }
    
	private URL getDetail(String urlId) {
		// Check exists
		if (!urlRepository.existsById(urlId)) {
			throw new CustomException("Cannot find url");
		}
		
		// Find tour
		URL url = urlRepository.findById(urlId).get();
		
		return url;
	}
	
	private List<URL> getAll() {
		// Find tour
		List<URL> urlTist = urlRepository.findAll();
		
		return urlTist;
	}

	private void delete(String urlId) {
		// Check exists
		if (urlRepository.existsById(urlId)) {
			urlRepository.deleteById(urlId);
		}
	}
	
	private String getAllValue(URL url) {
		String result = new String();
		for (Field itemField : URL.class.getDeclaredFields()) {
			itemField.setAccessible(true);
			try {
				// check type
				
				Object object = itemField.get(url);
				result += String.valueOf(object) + " "; 
			} catch (Exception e) {
				
			}
		}

		return result;
	}
	
	private List<URL> search(String keyword) {
		// init tour List
		List<URL> urlList = new ArrayList<>();
		urlList = urlRepository.findAll();
		if (keyword != null) {
			keyword = keyword.trim();
		}

		if (keyword != null && !keyword.isEmpty()) {
			if (urlList != null) {
				List<URL> urlListClone = new ArrayList<>();
				urlListClone.addAll(urlList);
				for (URL itemUrl : urlListClone) {
					String keywordNew = StringUtil.getNormalAlphabet(keyword);
					String fieldNew = StringUtil.getNormalAlphabet(getAllValue(itemUrl));
					
					System.out.println("\nfieldNew = " + fieldNew + " ");
					
					if (!fieldNew.contains(keywordNew)) {
						urlList.remove(itemUrl);
						if (urlList.size() <= 0) {
							break;
						}
					}
				}
			}
		}

		return urlList;
	}
	
	private URLDTO getURLDTO(URL url) {
		// mapping tourDTO
		URLDTO urlDTONew = new URLDTO();
		modelMapper.map(url, urlDTONew);
		
		return urlDTONew;
	}
	
	private List<URLDTO> getURLDTOList(List<URL> urlList) {
		List<URLDTO> urlDTOList = new ArrayList<>();
		for (URL itemURL : urlList) {
			urlDTOList.add(getURLDTO(itemURL));
		}
		return urlDTOList;
	}

	@Override
	public URLDTO createURL(URLDTO urlDTO) {
		// mapping url
		URL url = new URL();
		modelMapper.map(urlDTO, url);
		
		// check field condition
		checkFieldCondition(url);

		// create tour
		URL urlNew = new URL();
		urlNew = create(url);
		
		return getURLDTO(urlNew);
	}

	@Override
	public URLDTO updateURL(URLDTO urlDTO) {
		// get tour from database
		URL url = getDetail(urlDTO.getUrlId());
		
		// mapping tour
		modelMapper.map(urlDTO, url);
		
		// update tour
		URL urlNew = new URL();
		urlNew = update(url);
		
		return getURLDTO(urlNew);
	}
	
	@Override
	public URLDTO getURLDetail(String urlId) {
		URL url = getDetail(urlId);

		return getURLDTO(url);
	}

	@Override
	public List<URLDTO> getAllURL() {
		List<URL> urlList = new ArrayList<>(getAll());

		return getURLDTOList(urlList);
	}

	@Override
	public List<URLDTO> searchURL(String keyword) {
		List<URL> urlList = new ArrayList<>();
		urlList.addAll(search(keyword));
		
		return getURLDTOList(urlList);
	}

	@Override
	public void deleteURL(String urlId) {
		delete(urlId);
	}
}
