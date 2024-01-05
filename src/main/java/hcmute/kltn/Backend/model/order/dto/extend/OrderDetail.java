package hcmute.kltn.Backend.model.order.dto.extend;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {
	private String tourId;
	private VOTourDetail tourDetail;
	private String hotelId;
	private List<HotelDetail> hotelDetail;
	private String vehicleId;
	private List<VehicleDetail> vehicleDetail;
	private String guiderId;
	private List<GuiderDetail> guiderDetail;
}
