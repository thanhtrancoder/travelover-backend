package hcmute.kltn.Backend.model.tour.service;

import java.util.HashMap;
import java.util.List;

import hcmute.kltn.Backend.model.province.dto.LocationDTO;
import hcmute.kltn.Backend.model.tour.dto.StatusUpdate;
import hcmute.kltn.Backend.model.tour.dto.TourClone;
import hcmute.kltn.Backend.model.tour.dto.TourCreate;
import hcmute.kltn.Backend.model.tour.dto.TourDTO;
import hcmute.kltn.Backend.model.tour.dto.TourFilter;
import hcmute.kltn.Backend.model.tour.dto.TourSearch;
import hcmute.kltn.Backend.model.tour.dto.TourSearchRes;
import hcmute.kltn.Backend.model.tour.dto.TourSearchRes2;
import hcmute.kltn.Backend.model.tour.dto.TourSort;
import hcmute.kltn.Backend.model.tour.dto.TourUpdate;
import hcmute.kltn.Backend.model.tour.dto.entity.Tour;
import hcmute.kltn.Backend.model.tour.dto.extend.Reviewer;

public interface ITourService {
	public TourDTO createTour(TourCreate tourCreate);
	public TourDTO updateTour(TourUpdate tourUpdate);
	public TourDTO getDetailTour(String tourId);
	public TourDTO updateStatus(StatusUpdate statusUpdate);
	
	public List<TourDTO> getAllTour();
	public List<TourDTO> listTourSearch(String keyword);
	public List<TourDTO> listTourFilter(HashMap<String, String> tourFilter, List<TourDTO> tourDTOList);
	public List<TourDTO> listTourSort(TourSort tourSort, List<TourDTO> tourDTOList);
	
	public List<TourSearchRes> searchTour(TourSearch tourSearch);
	public List<TourSearchRes> searchFilter(TourFilter tourFilter, List<TourSearchRes> tourSearchResList);
	public List<TourSearchRes> searchSort(TourSort tourSort, List<TourSearchRes> tourSearchResList);
	public List<TourSearchRes> getAllDiscountTour();
	
	public List<TourSearchRes2> searchTour2(TourSearch tourSearch);
	public List<TourSearchRes2> searchFilter2(TourFilter tourFilter, List<TourSearchRes2> tourSearchRes2List);
	public List<TourSearchRes2> searchSort2(TourSort tourSort, List<TourSearchRes2> tourSearchRes2List);
	
	public void updateNumberOfOrdered(String tourId);
	public void updateIsDiscount();
	public void updateIsDiscountNoCheck();
	public void updateReviewer(String tourId, Reviewer reviewer);
	
	public void updateDailyTourLimit(String tourId, int dailyTourLimit);
	public void autoUpdateId();
	public TourDTO cloneTour(TourClone tourClone);
}