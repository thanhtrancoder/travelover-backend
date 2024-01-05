package hcmute.kltn.Backend.model.tour.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdate {
	private String tourId;
	private boolean status;
	
	public boolean getStatus() {
		return this.status;
	}
}
