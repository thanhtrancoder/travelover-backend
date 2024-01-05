package hcmute.kltn.Backend.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeUtil {
	public static LocalDateTime getCurentDate() {
		ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
		LocalDateTime currentDate = LocalDateTime.now(zoneId);
		
		return currentDate;
	}
}
