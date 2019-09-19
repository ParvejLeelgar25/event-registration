package com.axelor.event.registration.db.module;

import com.axelor.app.AxelorModule;
import com.axelor.event.registration.db.Event;
import com.axelor.event.registration.db.repo.AddressManageRepository;
import com.axelor.event.registration.db.repo.AddressRepository;
import com.axelor.event.registration.db.repo.EventRegistrationManageRepository;
import com.axelor.event.registration.db.repo.EventRegistrationRepository;
import com.axelor.event.registration.db.repo.EventRepository;
import com.axelor.event.registration.db.service.AddressService;
import com.axelor.event.registration.db.service.AddressServiceImpl;
import com.axelor.event.registration.db.service.EventRegistrationService;
import com.axelor.event.registration.db.service.EventRegistrationServiceImpl;
import com.axelor.event.registration.db.service.EventService;
import com.axelor.event.registration.db.service.EventServiceImpl;

public class EventRegistrationModule extends AxelorModule{

	@Override
	protected void configure() {
		bind(AddressService.class).to(AddressServiceImpl.class);
		bind(AddressRepository.class).to(AddressManageRepository.class);
		bind(EventRegistrationRepository.class).to(EventRegistrationManageRepository.class);
		bind(EventService.class).to(EventServiceImpl.class);
		bind(EventRegistrationService.class).to(EventRegistrationServiceImpl.class);
		
	}

}
