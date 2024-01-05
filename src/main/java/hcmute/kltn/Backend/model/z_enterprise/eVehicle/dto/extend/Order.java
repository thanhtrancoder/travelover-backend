package hcmute.kltn.Backend.model.z_enterprise.eVehicle.dto.extend;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
	private String orderId;
	private String companyId;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate startDate;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate endDate;
	private List<Coach> coachList;
	private int totalPrice;
	private String paymentStatus;
	private String orderStatus;
}
