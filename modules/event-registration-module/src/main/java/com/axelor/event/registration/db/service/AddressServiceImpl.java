package com.axelor.event.registration.db.service;

import com.axelor.event.registration.db.Address;
import com.google.common.base.Strings;

public class AddressServiceImpl implements AddressService{

	@Override
	public String computeFullName(Address address) {
		String flat = address.getFlat();
	    String street = address.getStreet();
	    String landMark = address.getLandMark();
	    String city = address.getCity();
	    String country = address.getCountry();

	    return (!Strings.isNullOrEmpty(flat) ? flat : "")
	        + (!Strings.isNullOrEmpty(street) ? " " + street : "")
	        + (!Strings.isNullOrEmpty(landMark) ? " " + landMark : "")
	        + (!Strings.isNullOrEmpty(city) ? " " + city : "")
	        + (!Strings.isNullOrEmpty(country) ? " " + country : "");
	}

}
