package hcmute.kltn.Backend.model.order.service;

import java.util.HashMap;
import java.util.List;

import hcmute.kltn.Backend.model.base.Sort;
import hcmute.kltn.Backend.model.order.dto.OrderCreate;
import hcmute.kltn.Backend.model.order.dto.OrderDTO;
import hcmute.kltn.Backend.model.order.dto.OrderPaymentUpdate;
import hcmute.kltn.Backend.model.order.dto.OrderStatusUpdate;
import hcmute.kltn.Backend.model.order.dto.OrderUpdate;
import hcmute.kltn.Backend.model.order.dto.Rating;
import hcmute.kltn.Backend.model.order.dto.entity.Order;

public interface IOrderService {
	public OrderDTO createOrder(OrderCreate orderCreate);
	public OrderDTO updateOrder(OrderUpdate orderUpdate);
	public OrderDTO updateOrderPayment(OrderPaymentUpdate orderPaymentUpdate);
	public OrderDTO updateOrderStatus(OrderStatusUpdate orderStatusUpdate);
	public OrderDTO getDetailOrder(String orderId);
	public OrderDTO getDetailOrderNotCheckCreate(String orderId);
	
	public List<OrderDTO> getAllOrder();
	public List<OrderDTO> getAllOrder2();
	public List<OrderDTO> searchOrder(String keyword);
	public List<OrderDTO> listOrderSearch(String keyword);
	public List<OrderDTO> listOrderFilter(HashMap<String, String> filter, List<OrderDTO> orderDTOList);
	public List<OrderDTO> listOrderSort(Sort sort, List<OrderDTO> orderDTOList);
	
	public boolean paymentCheck(String orderId);
	
	public void deleteUnpaidOrder();
	public void rating(Rating rating);
} 