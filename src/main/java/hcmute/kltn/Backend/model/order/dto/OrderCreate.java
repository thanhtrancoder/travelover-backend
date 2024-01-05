package hcmute.kltn.Backend.model.order.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import hcmute.kltn.Backend.model.order.dto.extend.CustomerInformation;
import hcmute.kltn.Backend.model.order.dto.extend.Discount;
import hcmute.kltn.Backend.model.order.dto.extend.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreate {
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate startDate; // not null
	private String tourId;
	private String hotelId;
	private List<String> roomIdList;
	private String vehivleId;
	private List<String> coachIdList;
	private String guiderId;
	private List<String> personIdList;
	private int numberOfChildren; // not null
	private int numberOfAdult; // not null
	private String discountCode;
	private CustomerInformation customerInformation; // not null
	private String note;
	private int finalPrice;
}
