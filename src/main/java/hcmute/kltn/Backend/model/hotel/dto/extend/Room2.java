package hcmute.kltn.Backend.model.hotel.dto.extend;

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
}
