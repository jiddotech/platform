package com.jiddo.platform.maps;

import org.springframework.stereotype.Service;

@Service
public interface RevGeoCodingService {

	public RevGeoCodeAddressDTO getAddress(Double lat, Double lon);
	
	public RevGeoCodeAddressDTO getAddressIgnoreError(Double lat, Double lon);

}
