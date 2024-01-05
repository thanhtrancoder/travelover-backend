package hcmute.kltn.Backend.model.order.dto.extend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDetail {
	private String coachId;
	private String name; // xe 4 chỗ. 7 chỗ
	private int capacity; // xác định kiểu xe
	private String type; // xác định kiểu xe
	private String manufacturerAndModel; // tên hãng sản xuất và model: Kia Morning, Hyundai Grand i10, Vinfast Fadil
	private int pricePerDay; // giá thuê theo ngày
	private int pricePerKilometer; // giá thuê theo quãng đường phát sinh
	private boolean driverIncluded; // có bao gồm tài xế chưa
	private String additionalServices; // các dịch vụ đi kèm
}
