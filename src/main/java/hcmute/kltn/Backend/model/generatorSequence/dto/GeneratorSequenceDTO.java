package hcmute.kltn.Backend.model.generatorSequence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneratorSequenceDTO {
	private String id;
	private String collectionName;
	private String prefix;
	private long number; 
	private String description;
}
