package hcmute.kltn.Backend.model.statistic.dto;

import java.util.List;

import hcmute.kltn.Backend.model.statistic.dto.extend.StatisticValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticDTO {
	private String labelX;
	private String unitX;
	private String labelY;
	private String unitY;
	private String description;
	private List<StatisticValue> valueList;
}
