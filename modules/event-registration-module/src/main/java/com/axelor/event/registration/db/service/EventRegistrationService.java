package com.axelor.event.registration.db.service;

import com.axelor.event.registration.db.Event;
import com.axelor.event.registration.db.EventRegistration;

public interface EventRegistrationService {

	public void calculation(EventRegistration eventRegistration, Event event);

}
