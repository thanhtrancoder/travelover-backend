package hcmute.kltn.Backend.model.generatorSequence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneratorSequenceCreate {
	private String collectionName; // not null, unique
	private String prefix; // not null, unique
	private String description;
}
