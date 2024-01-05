package hcmute.kltn.Backend.model.z_enterprise.eHotel.service;

import java.util.HashMap;
import java.util.List;

import hcmute.kltn.Backend.model.base.Sort;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelCreate;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelDTOSimple;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelOrderCreate;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelOrderStatusUpdate;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelOrderUpdate;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.EHotelUpdate;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.Location;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.RoomSearch;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.RoomSearchRes;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.entity.EHotel;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Order;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Room;
import hcmute.kltn.Backend.model.z_enterprise.eHotel.dto.extend.Room2;

public interface IEHotelService {
	public EHotel createEHotel(EHotelCreate eHotelCreate);
	public EHotel updateEHotel(EHotelUpdate eHotelUpdate);
	public EHotel getDetailEHotel(String eHotelId);
	
	public Order createOrder(EHotelOrderCreate eHotelOrderCreate);
	public Order updateOrder(EHotelOrderUpdate eHotelOrderUpdate);
	public Order getOneOrder(String eHotelId, String orderId);
	public Order updateOrderStatus(EHotelOrderStatusUpdate eHotelOrderStatusUpdate);
	
	public List<EHotel> getAllEHotel();
	public List<EHotel> searchEHotel(String keyword);
	public List<EHotelDTOSimple> searchEHotelByLocation(Location location);
	public List<EHotel> listEHotelSearch(String keyword);
	public List<EHotel> listEHotelFilter(HashMap<String, String> filter, List<EHotel> eHotelList);
	public List<EHotel> listEHotelSort(Sort sort, List<EHotel> eHotelList);
	
	public List<Room> searchRoom(RoomSearch roomSearch);
	
	public List<RoomSearchRes> searchRoom2(RoomSearch roomSearch);
	
	public List<Order> getAllOrder(String eHotelId);
}
