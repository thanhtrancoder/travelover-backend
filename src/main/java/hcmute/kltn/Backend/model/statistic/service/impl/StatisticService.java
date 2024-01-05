package hcmute.kltn.Backend.model.statistic.service.impl;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hcmute.kltn.Backend.exception.CustomException;
import hcmute.kltn.Backend.model.commission.dto.CommissionDTO;
import hcmute.kltn.Backend.model.commission.service.ICommissionService;
import hcmute.kltn.Backend.model.order.dto.OrderDTO;
import hcmute.kltn.Backend.model.order.service.IOrderService;
import hcmute.kltn.Backend.model.statistic.dto.StatisticDTO;
import hcmute.kltn.Backend.model.statistic.dto.Time;
import hcmute.kltn.Backend.model.statistic.dto.extend.StatisticValue;
import hcmute.kltn.Backend.model.statistic.service.IStatisticService;
import hcmute.kltn.Backend.util.LocalDateTimeUtil;

@Service
public class StatisticService implements IStatisticService{
	@Autowired
	private IOrderService iOrderService;
	@Autowired
	private ICommissionService iCommissionService;
	
	private void checkCondition(Time time) {
		if (time != null) {
			if (time.getYear() != null && !time.getYear().isEmpty()) {
				
				int year = Integer.valueOf(time.getYear());
				if (year < 2023) {
					throw new CustomException("Years must be greater than or equal to 2023");
				}
				if (time.getMonth() != null && !time.getMonth().isEmpty()) {
					int month = Integer.valueOf(time.getMonth());
					if (month < 1 || 12 < month) {
						throw new CustomException("The month must be between 1 and 12");
					}
				}
			}
		}
	}
	
	@Override
	public StatisticDTO getTurnover(Time time) {
		checkCondition(time);
		
		List<OrderDTO> orderDTOList = new ArrayList<>();
		orderDTOList.addAll(iOrderService.getAllOrder());
		
		StatisticDTO statisticDTO = new StatisticDTO();
		List<StatisticValue> statisticValueList = new ArrayList<>();
		
		HashMap<List<Integer>, Integer> turnoverPerDay = new HashMap<>();
		
		for (OrderDTO itemOrderDTO : orderDTOList) {
			if (itemOrderDTO.getStatus() == true && itemOrderDTO.getOrderStatus().equals("finished")) {
				int createYear = itemOrderDTO.getCreatedAt2().getYear();
				int createMonth = itemOrderDTO.getCreatedAt2().getMonthValue();
				int createDay = itemOrderDTO.getCreatedAt2().getDayOfMonth();
				
				List<Integer> keyTurnoverPerDay = Arrays.asList(createYear, createMonth, createDay);
				
				try {
					int valueNew = turnoverPerDay.get(keyTurnoverPerDay) + itemOrderDTO.getFinalPrice();
					turnoverPerDay.replace(keyTurnoverPerDay, valueNew);
				} catch (Exception e) {
					turnoverPerDay.put(keyTurnoverPerDay, itemOrderDTO.getFinalPrice());
				}
			}
		}

		if (time != null) {
			if (time.getYear() != null && !time.getYear().isEmpty()) {
				int year = Integer.valueOf(time.getYear());
				if (time.getMonth() != null && !time.getMonth().isEmpty()) {
					// Turnover by day
					int month = Integer.valueOf(time.getMonth());
					
				    YearMonth yearMonth = YearMonth.of(year, month); 
				    int daysInMonth = yearMonth.lengthOfMonth(); 
					for (int day = 1; day <= daysInMonth; day++) {
						StatisticValue statisticValue = new StatisticValue();
						statisticValue.setValueX(day);
						
						List<Integer> keyTurnoverPerDay = Arrays.asList(year, month, day);
						int turnoverValue = 0;
						try {
							turnoverValue = turnoverPerDay.get(keyTurnoverPerDay);
						} catch (Exception e) {
						}
						statisticValue.setValueY(turnoverValue);
						
						statisticValueList.add(statisticValue);
					}
					
					statisticDTO.setLabelX("Ngày");
					statisticDTO.setUnitX(null);
					statisticDTO.setLabelY("Doanh thu");
					statisticDTO.setUnitY("đ");
					statisticDTO.setDescription("Tính doanh thu theo ngày trong tháng " + month);
					statisticDTO.setValueList(statisticValueList);
				} else {
					// Turnover by month
					for (int month = 1; month <= 12; month++) {
						StatisticValue statisticValue = new StatisticValue();
						statisticValue.setValueX(month);
						
						int turnoverValue = 0;
						
					    YearMonth yearMonth = YearMonth.of(year, month); 
					    int daysInMonth = yearMonth.lengthOfMonth(); 
						for (int day = 1; day <= daysInMonth; day++) {
							List<Integer> keyTurnoverPerDay = Arrays.asList(year, month, day);
							try {
								turnoverValue += turnoverPerDay.get(keyTurnoverPerDay);
							} catch (Exception e) {
								
							}
						}
						
						statisticValue.setValueY(turnoverValue);
						
						statisticValueList.add(statisticValue);
					}
					
					statisticDTO.setLabelX("Tháng");
					statisticDTO.setUnitX(null);
					statisticDTO.setLabelY("Doanh thu");
					statisticDTO.setUnitY("đ");
					statisticDTO.setDescription("Tính doanh thu theo tháng trong năm " + year);
					statisticDTO.setValueList(statisticValueList);
				}
			} else {
				// Turnover by year
				LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
				
				for (int year = 2023; year <= currentDate.getYear(); year++) {
					StatisticValue statisticValue = new StatisticValue();
					statisticValue.setValueX(year);
					
					int turnoverValue = 0;
					

				    for (int month = 1; month <= 12; month++) {
					    YearMonth yearMonth = YearMonth.of(year, month); 
					    int daysInMonth = yearMonth.lengthOfMonth(); 
						for (int day = 1; day <= daysInMonth; day++) {
							List<Integer> keyTurnoverPerDay = Arrays.asList(year, month, day);
							try {
								turnoverValue += turnoverPerDay.get(keyTurnoverPerDay);
							} catch (Exception e) {
								
							}
						}
				    }
					
					statisticValue.setValueY(turnoverValue);
					
					statisticValueList.add(statisticValue);
				}
				
				statisticDTO.setLabelX("Năm");
				statisticDTO.setUnitX(null);
				statisticDTO.setLabelY("Doanh thu");
				statisticDTO.setUnitY("đ");
				statisticDTO.setDescription("Tính doanh thu theo năm");
				statisticDTO.setValueList(statisticValueList);
			}
		}
		
		return statisticDTO;
	}

	@Override
	public StatisticDTO getProfit(Time time) {
checkCondition(time);
		
		List<OrderDTO> orderDTOList = new ArrayList<>();
		orderDTOList.addAll(iOrderService.getAllOrder());
		
		StatisticDTO statisticDTO = new StatisticDTO();
		List<StatisticValue> statisticValueList = new ArrayList<>();
		
		HashMap<List<Integer>, Integer> turnoverPerDay = new HashMap<>();
		
		for (OrderDTO itemOrderDTO : orderDTOList) {
			if (itemOrderDTO.getStatus() == true && itemOrderDTO.getOrderStatus().equals("finished")) {
				int createYear = itemOrderDTO.getCreatedAt2().getYear();
				int createMonth = itemOrderDTO.getCreatedAt2().getMonthValue();
				int createDay = itemOrderDTO.getCreatedAt2().getDayOfMonth();
				
				List<Integer> keyTurnoverPerDay = Arrays.asList(createYear, createMonth, createDay);
				
				int profit = 0;
				try {
					profit = itemOrderDTO.getCommission().getProfit();
				} catch (Exception e) {
					CommissionDTO commissionDTO = new CommissionDTO();
					commissionDTO = iCommissionService.getCurrentCommission();
					profit = itemOrderDTO.getFinalPrice() * commissionDTO.getRate() / 100;
				}
				
				try {
					int valueNew = turnoverPerDay.get(keyTurnoverPerDay) + profit;
					turnoverPerDay.replace(keyTurnoverPerDay, valueNew);
				} catch (Exception e) {
					turnoverPerDay.put(keyTurnoverPerDay, profit);
				}
			}
		}

		if (time != null) {
			if (time.getYear() != null && !time.getYear().isEmpty()) {
				int year = Integer.valueOf(time.getYear());
				if (time.getMonth() != null && !time.getMonth().isEmpty()) {
					// Turnover by day
					int month = Integer.valueOf(time.getMonth());
					
				    YearMonth yearMonth = YearMonth.of(year, month); 
				    int daysInMonth = yearMonth.lengthOfMonth(); 
					for (int day = 1; day <= daysInMonth; day++) {
						StatisticValue statisticValue = new StatisticValue();
						statisticValue.setValueX(day);
						
						List<Integer> keyTurnoverPerDay = Arrays.asList(year, month, day);
						int turnoverValue = 0;
						try {
							turnoverValue = turnoverPerDay.get(keyTurnoverPerDay);
						} catch (Exception e) {
						}
						statisticValue.setValueY(turnoverValue);
						
						statisticValueList.add(statisticValue);
					}
					
					statisticDTO.setLabelX("Ngày");
					statisticDTO.setUnitX(null);
					statisticDTO.setLabelY("Doanh thu");
					statisticDTO.setUnitY("đ");
					statisticDTO.setDescription("Tính doanh thu theo ngày trong tháng " + month);
					statisticDTO.setValueList(statisticValueList);
				} else {
					// Turnover by month
					for (int month = 1; month <= 12; month++) {
						StatisticValue statisticValue = new StatisticValue();
						statisticValue.setValueX(month);
						
						int turnoverValue = 0;
						
					    YearMonth yearMonth = YearMonth.of(year, month); 
					    int daysInMonth = yearMonth.lengthOfMonth(); 
						for (int day = 1; day <= daysInMonth; day++) {
							List<Integer> keyTurnoverPerDay = Arrays.asList(year, month, day);
							try {
								turnoverValue += turnoverPerDay.get(keyTurnoverPerDay);
							} catch (Exception e) {
								
							}
						}
						
						statisticValue.setValueY(turnoverValue);
						
						statisticValueList.add(statisticValue);
					}
					
					statisticDTO.setLabelX("Tháng");
					statisticDTO.setUnitX(null);
					statisticDTO.setLabelY("Doanh thu");
					statisticDTO.setUnitY("đ");
					statisticDTO.setDescription("Tính doanh thu theo tháng trong năm " + year);
					statisticDTO.setValueList(statisticValueList);
				}
			} else {
				// Turnover by year
				LocalDateTime currentDate = LocalDateTimeUtil.getCurentDate();
				
				for (int year = 2023; year <= currentDate.getYear(); year++) {
					StatisticValue statisticValue = new StatisticValue();
					statisticValue.setValueX(year);
					
					int turnoverValue = 0;
					

				    for (int month = 1; month <= 12; month++) {
					    YearMonth yearMonth = YearMonth.of(year, month); 
					    int daysInMonth = yearMonth.lengthOfMonth(); 
						for (int day = 1; day <= daysInMonth; day++) {
							List<Integer> keyTurnoverPerDay = Arrays.asList(year, month, day);
							try {
								turnoverValue += turnoverPerDay.get(keyTurnoverPerDay);
							} catch (Exception e) {
								
							}
						}
				    }
					
					statisticValue.setValueY(turnoverValue);
					
					statisticValueList.add(statisticValue);
				}
				
				statisticDTO.setLabelX("Năm");
				statisticDTO.setUnitX(null);
				statisticDTO.setLabelY("Doanh thu");
				statisticDTO.setUnitY("đ");
				statisticDTO.setDescription("Tính doanh thu theo năm");
				statisticDTO.setValueList(statisticValueList);
			}
		}
		
		return statisticDTO;
	}

}
