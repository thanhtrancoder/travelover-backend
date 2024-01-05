package hcmute.kltn.Backend.model.url.dto.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import hcmute.kltn.Backend.model.base.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Document(collection = "url")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class URL extends BaseEntity{
	@Id
	private String urlId;
	private String name; // not null, unique
	private String url; // not null
}
