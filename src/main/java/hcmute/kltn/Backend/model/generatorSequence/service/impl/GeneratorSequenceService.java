package hcmute.kltn.Backend.model.generatorSequence.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import hcmute.kltn.Backend.exception.CustomException;
import hcmute.kltn.Backend.model.generatorSequence.dto.GeneratorSequenceCreate;
import hcmute.kltn.Backend.model.generatorSequence.dto.GeneratorSequenceDTO;
import hcmute.kltn.Backend.model.generatorSequence.dto.entity.GeneratorSequence;
import hcmute.kltn.Backend.model.generatorSequence.repository.GeneratorSequenceRepository;
import hcmute.kltn.Backend.model.generatorSequence.service.IGeneratorSequenceService;

@Service
public class GeneratorSequenceService implements IGeneratorSequenceService {
	@Autowired
	private GeneratorSequenceRepository generatorSequenceRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
    private MongoTemplate mongoTemplate;
	
	private void checkFieldCondition(GeneratorSequence generatorSequence) {
		// check null
		if(generatorSequence.getCollectionName() == null || generatorSequence.getCollectionName().equals("")) {
			throw new CustomException("Collection Name is not null");
		}
		if(generatorSequence.getPrefix() == null || generatorSequence.getPrefix().equals("")) {
			throw new CustomException("Prefix is not null");
		}
//		if(generatorSequenceDTO.getNumber() < 0) {
//			throw new CustomException("Number must be greater than or equal to 0");
//		}
		
		// check unique
		if(generatorSequence.getId() == null || generatorSequence.getId().equals("")) {
			if(generatorSequenceRepository.existsByCollectionName(generatorSequence.getCollectionName())) {
				throw new CustomException("Collection Name is already");
			}
			if(generatorSequenceRepository.existsByPrefix(generatorSequence.getPrefix())) {
				throw new CustomException("Prefix is already");
			}
		} else {
			GeneratorSequence generatorSequenceFind = generatorSequenceRepository.findById(generatorSequence.getId()).get();
			List<GeneratorSequence> generatorSequencesCltNList = generatorSequenceRepository.findAllByCollectionName(generatorSequence.getCollectionName());
			for(GeneratorSequence item : generatorSequencesCltNList) {
				if(item.getCollectionName() == generatorSequenceFind.getCollectionName() && item.getId() != generatorSequenceFind.getId()) {
					throw new CustomException("Collection Name is already");
				}
			}

			List<GeneratorSequence> generatorSequencesPrfNList = generatorSequenceRepository.findAllByPrefix(generatorSequence.getPrefix());
			for(GeneratorSequence item : generatorSequencesPrfNList) {
				if(item.getPrefix() == generatorSequenceFind.getPrefix() && item.getId() != generatorSequenceFind.getId()) {
					throw new CustomException("Prefix is already");
				}
			}
		}
	}
	
	private GeneratorSequence create(GeneratorSequence generatorSequence) {
		// check field condition
		checkFieldCondition(generatorSequence);
		
		// create generator sequence
		GeneratorSequence genSeqNew = new GeneratorSequence();
		genSeqNew = generatorSequenceRepository.save(generatorSequence);
		
		return genSeqNew;
	}

//	private GeneratorSequence create(GeneratorSequenceDTO generatorSequenceDTO) {
//		// check field condition
//		checkFieldCondition(generatorSequenceDTO);
//		
//		// Mapping
//		GeneratorSequence generatorSequence = new GeneratorSequence();
//		modelMapper.map(generatorSequenceDTO, generatorSequence);
//
//		// Set default value
//		generatorSequence.setNumber(0);
//		
//		// create generator sequence
//		generatorSequence = generatorSequenceRepository.save(generatorSequence);
//		
//		return generatorSequence;
//	}
	
	private GeneratorSequence update(GeneratorSequence generatorSequence) {
		// Check exists
		if (!generatorSequenceRepository.existsById(generatorSequence.getId())) {
			throw new CustomException("Cannot find generator sequence");
		}
		
		// check field condition
		checkFieldCondition(generatorSequence);
		
		// update
		GeneratorSequence genSeqNew = new GeneratorSequence();
		genSeqNew = generatorSequenceRepository.save(generatorSequence);
		
		return genSeqNew;
	}

//	private GeneratorSequence update(GeneratorSequenceDTO generatorSequenceDTO) {
//		// Check exists
//		if (!generatorSequenceRepository.existsById(generatorSequenceDTO.getId())) {
//			throw new CustomException("Cannot find generator sequence");
//		}
//		
//		// check field condition
//		checkFieldCondition(generatorSequenceDTO);
//		
//		// get GeneratorSequence from db
//		GeneratorSequence generatorSequence = generatorSequenceRepository.findById(generatorSequenceDTO.getId()).get();
//		
//		// Mapping
//		modelMapper.map(generatorSequenceDTO, generatorSequence);
//		
//		// set default value
//		
//		// update
//		generatorSequence = generatorSequenceRepository.save(generatorSequence);
//		
//		return generatorSequence;
//	}

	private GeneratorSequence getDetail(String generatorSequenceId) {
		// Check exists
		if (!generatorSequenceRepository.existsById(generatorSequenceId)) {
			throw new CustomException("Cannot find generator sequence");
		}
		
		// get GeneratorSequence from db
		GeneratorSequence generatorSequence = generatorSequenceRepository.findById(generatorSequenceId).get();
		
		return generatorSequence;
	}

	private List<GeneratorSequence> getAll() {
		// get all GeneratorSequence from db
		List<GeneratorSequence> listGeneratorSequence = generatorSequenceRepository.findAll();

		return listGeneratorSequence;
		
	}

	private void delete(String generatorSequenceId) {
		// Check exists
		if (generatorSequenceRepository.existsById(generatorSequenceId)) {
			generatorSequenceRepository.deleteById(generatorSequenceId);
		} 
	}
	
	private List<GeneratorSequence> search(String keyword) {
		// init generatorSequence List
		List<GeneratorSequence> genSeqList = new ArrayList<>();
		
		if(keyword == null || keyword.equals("")) {
			genSeqList = getAll();
		} else {
			// create list field name
			List<Criteria> criteriaList = new ArrayList<>();
			for(Field itemField : GeneratorSequence.class.getDeclaredFields()) {
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
			genSeqList = mongoTemplate.find(query, GeneratorSequence.class);
		}
		
		return genSeqList;
	}
	
	private GeneratorSequenceDTO getGenSeqDTO (GeneratorSequence genSeq) {
		GeneratorSequenceDTO genSeqDTONew = new GeneratorSequenceDTO();
		modelMapper.map(genSeq, genSeqDTONew);
		return genSeqDTONew;
	}
	
	private List<GeneratorSequenceDTO> getGenSeqDTOList(List<GeneratorSequence> genSeqList) {
		List<GeneratorSequenceDTO> genSeqDTOList = new ArrayList<>();
		for (GeneratorSequence itemGenSeq : genSeqList) {
			genSeqDTOList.add(getGenSeqDTO(itemGenSeq));
		}
		return genSeqDTOList;
	}

	@Override
	public GeneratorSequenceDTO createGenSeq(GeneratorSequenceCreate generatorSequenceCreate) {
		// mapping
		GeneratorSequence generatorSequence = new GeneratorSequence();
		modelMapper.map(generatorSequenceCreate, generatorSequence);
		
		// set default value
		generatorSequence.setNumber(0);
		
		// create generatorSequence
		GeneratorSequence generatorSequenceNew = new GeneratorSequence();
		generatorSequenceNew = create(generatorSequence);
		
		return getGenSeqDTO(generatorSequenceNew);
	}

	@Override
	public GeneratorSequenceDTO updateGenSeq(GeneratorSequenceDTO generatorSequenceDTO) {
		// mapping
		GeneratorSequence generatorSequence = new GeneratorSequence();
		modelMapper.map(generatorSequenceDTO, generatorSequence);
				
		GeneratorSequence genSeqNew = new GeneratorSequence();
		genSeqNew =	update(generatorSequence);
		
		return getGenSeqDTO(genSeqNew);
	}

	@Override
	public GeneratorSequenceDTO getDetailGenSeq(String id) {
		GeneratorSequence genSeq = getDetail(id);

		return getGenSeqDTO(genSeq);
	}

	@Override
	public List<GeneratorSequenceDTO> getAllGenSeq() {
		List<GeneratorSequence> genSeqList = getAll();

		return getGenSeqDTOList(genSeqList);
	}

	@Override
	public List<GeneratorSequenceDTO> searchGenSeq(String keyword) {
		List<GeneratorSequence> genSeqList = search(keyword);

		return getGenSeqDTOList(genSeqList);
	}
	
	@Override
	public String genId(String collectionName) {
		GeneratorSequence generatorSequence = generatorSequenceRepository.findByCollectionName(collectionName);
		if (generatorSequence != null) {
			String id = generatorSequence.getPrefix() + String.format("%012d", generatorSequence.getNumber() + 1);
			generatorSequence.setNumber(generatorSequence.getNumber() + 1);
			generatorSequenceRepository.save(generatorSequence);
			return id;
		} else {
			return null;
		}
	}

	@Override
	public boolean initData(GeneratorSequenceDTO generatorSequenceDTO) {
		// Check Already
		boolean existsTableName = generatorSequenceRepository.existsByCollectionName(generatorSequenceDTO.getCollectionName());
		boolean existsPrefix = generatorSequenceRepository.existsByPrefix(generatorSequenceDTO.getPrefix());
		if(existsTableName || existsPrefix) {
			return false;
		}
		
		// Mapping
		GeneratorSequence generatorSequence = new GeneratorSequence();
		modelMapper.map(generatorSequenceDTO, generatorSequence);

		// Set default value
		
		generatorSequence = generatorSequenceRepository.save(generatorSequence);
		
		return true;
	}
}
