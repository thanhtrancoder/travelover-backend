package hcmute.kltn.Backend.model.generatorSequence.service;

import java.util.List;

import hcmute.kltn.Backend.model.generatorSequence.dto.GeneratorSequenceCreate;
import hcmute.kltn.Backend.model.generatorSequence.dto.GeneratorSequenceDTO;
import hcmute.kltn.Backend.model.generatorSequence.dto.entity.GeneratorSequence;

public interface IGeneratorSequenceService {
	public GeneratorSequenceDTO createGenSeq(GeneratorSequenceCreate generatorSequenceCreate);
	public GeneratorSequenceDTO updateGenSeq(GeneratorSequenceDTO generatorSequenceDTO);
	public GeneratorSequenceDTO getDetailGenSeq(String id);
	public List<GeneratorSequenceDTO> getAllGenSeq();
	public List<GeneratorSequenceDTO> searchGenSeq(String keyword);
	
	public String genId(String collectionName);
	public boolean initData(GeneratorSequenceDTO generatorSequenceDTO);
	
}
