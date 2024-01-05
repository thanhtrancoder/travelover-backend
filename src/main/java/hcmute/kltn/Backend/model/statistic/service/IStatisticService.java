package hcmute.kltn.Backend.model.statistic.service;

import hcmute.kltn.Backend.model.statistic.dto.StatisticDTO;
import hcmute.kltn.Backend.model.statistic.dto.Time;

public interface IStatisticService {
	public StatisticDTO getTurnover(Time time);
	public StatisticDTO getProfit(Time time);
}
