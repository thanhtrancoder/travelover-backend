package hcmute.kltn.Backend.model.url.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class URLDTO {
	private String urlId;
	private String name;
	private String url;
}