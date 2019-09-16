package com.axelor.event.registration.db.repo;

import java.util.List;

import com.axelor.event.registration.db.Event;
import com.axelor.event.registration.db.EventRegistration;

public class EventManageRepository extends EventRepository{

	@Override
	public Event save(Event entity) {
		
		if(entity.getEventRegistrationList() != null) {
			List<EventRegistration> eventRegistrationList = entity.getEventRegistrationList();
			for(EventRegistration eventRegistration : eventRegistrationList) {
				eventRegistration.setEvent(entity);
			}
		}
		return super.save(entity);
	}

	
}
