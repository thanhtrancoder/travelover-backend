package hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room2 {
	private String roomId;
	private String name;
	private String type;
	private List<String> bed;
	private int standardNumberOfAdult;
	private int maximumNumberOfChildren;
	private String actualNumberOfAdult;
	private int price;
	private boolean status;
	
	public boolean getStatus() {
		return this.status;
	}
}
