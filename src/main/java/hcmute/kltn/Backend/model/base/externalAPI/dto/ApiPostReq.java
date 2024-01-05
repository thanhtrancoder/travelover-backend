package hcmute.kltn.Backend.model.base.externalAPI.dto;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiPostReq {
	private String url;
	private HashMap<String, String> header;
	private HashMap<String, String> param;
}
