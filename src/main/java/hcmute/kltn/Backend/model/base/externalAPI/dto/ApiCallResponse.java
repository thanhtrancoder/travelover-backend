package hcmute.kltn.Backend.model.base.externalAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiCallResponse {
	private Object status;
	private Object body;
}
