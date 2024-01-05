package hcmute.kltn.Backend.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class LocalDateUtil {
	public static LocalDate getDateNow() {
		ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
		LocalDate dateNow = LocalDate.now(vietnamZone);
		
		return dateNow;
	}
	
	public static int numberOfDayBetween(LocalDate startDate, LocalDate endDate) {
		Period period = Period.between(startDate, endDate);
		int numberOfDay = period.getDays();
		
		return numberOfDay;
	}
}
