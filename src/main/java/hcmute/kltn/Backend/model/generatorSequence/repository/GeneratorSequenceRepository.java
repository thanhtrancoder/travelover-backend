package hcmute.kltn.Backend.model.generatorSequence.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import hcmute.kltn.Backend.model.generatorSequence.dto.entity.GeneratorSequence;

public interface GeneratorSequenceRepository extends MongoRepository<GeneratorSequence, String>{
	GeneratorSequence findByCollectionName(String collectionName);
	boolean existsByCollectionName(String collectionName);
	boolean existsByPrefix(String prefix);
	List<GeneratorSequence> findAllByCollectionName(String collectionName);
	List<GeneratorSequence> findAllByPrefix(String prefix);
}
