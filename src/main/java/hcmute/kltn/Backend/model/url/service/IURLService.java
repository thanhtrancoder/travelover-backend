package hcmute.kltn.Backend.model.url.service;

import java.util.List;

import hcmute.kltn.Backend.model.url.dto.URLDTO;

public interface IURLService {
	public URLDTO createURL(URLDTO urlDTO);
	public URLDTO updateURL(URLDTO urlDTO);
	public URLDTO getURLDetail(String urlId);
	public List<URLDTO> getAllURL();
	public List<URLDTO> searchURL(String keyword);
	public void deleteURL(String urlId);
}
