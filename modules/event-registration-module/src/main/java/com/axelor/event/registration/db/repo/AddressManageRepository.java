package com.axelor.event.registration.db.repo;

import com.axelor.event.registration.db.Address;
import com.axelor.event.registration.db.service.AddressService;
import com.google.inject.Inject;

public class AddressManageRepository extends AddressRepository{

	@Inject private AddressService addressService;
	
	 @Override
	  public Address save(Address entity) {

	    entity.setFullName(addressService.computeFullName(entity));
	    return super.save(entity);
	  }
}
