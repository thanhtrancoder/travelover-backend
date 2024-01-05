package hcmute.kltn.Backend.model.hotel.service;

import java.util.HashMap;
import java.util.List;

import hcmute.kltn.Backend.model.base.Sort;
import hcmute.kltn.Backend.model.hotel.dto.HotelCreate;
import hcmute.kltn.Backend.model.hotel.dto.HotelDTO;
import hcmute.kltn.Backend.model.hotel.dto.HotelSearch;
import hcmute.kltn.Backend.model.hotel.dto.HotelUpdate;
import hcmute.kltn.Backend.model.hotel.dto.RoomSearch;
import hcmute.kltn.Backend.model.hotel.dto.RoomSearch2;
import hcmute.kltn.Backend.model.hotel.dto.entity.Hotel;
import hcmute.kltn.Backend.model.hotel.dto.extend.Room;
import hcmute.kltn.Backend.model.hotel.dto.extend.Room2;

public interface IHotelService {
	public HotelDTO createHotel(HotelCreate hotelCreate);
	public HotelDTO updateHotel(HotelUpdate hotelUpdate);
	public HotelDTO getDetailHotel(String hotelId);
	
	public List<HotelDTO> getAllHotel();
	public List<HotelDTO> searchHotel(HotelSearch hotelSearch);
	public List<HotelDTO> listHotelSearch(String keyword);
	public List<HotelDTO> listHotelFilter(HashMap<String, String> filter, List<HotelDTO> hotelDTOList);
	public List<HotelDTO> listHotelSort(Sort sort, List<HotelDTO> hotelDTOList);
	
	public Room getRoomDetail(String hotelId, String roomId);
	
	public List<Room> searchRoom(RoomSearch roomSearch);
	
	public List<Room2> searchRoom2(RoomSearch2 roomSearch);
}
