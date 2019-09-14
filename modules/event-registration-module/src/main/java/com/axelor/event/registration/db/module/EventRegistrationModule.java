package com.axelor.event.registration.db.module;

import com.axelor.app.AxelorModule;
import com.axelor.event.registration.db.repo.AddressManageRepository;
import com.axelor.event.registration.db.repo.AddressRepository;
import com.axelor.event.registration.db.service.AddressService;
import com.axelor.event.registration.db.service.AddressServiceImpl;

public class EventRegistrationModule extends AxelorModule{

	@Override
	protected void configure() {
		bind(AddressService.class).to(AddressServiceImpl.class);
		bind(AddressRepository.class).to(AddressManageRepository.class);
	}

}
