package hcmute.kltn.Backend.model.hotel.dto.extend;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchAPI {
	private String url;
	private List<Param> pram;
	private String directListOption;
	private String test;
}
