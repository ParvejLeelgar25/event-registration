package com.axelor.event.registration.db.repo;

import java.util.List;

import com.axelor.event.registration.db.Event;
import com.axelor.event.registration.db.EventRegistration;
import com.axelor.inject.Beans;

public class EventRegistrationManageRepository extends EventRegistrationRepository{

	@Override
	public EventRegistration save(EventRegistration entity) {
		Event event = entity.getEvent();
		EventRepository eventRepository = Beans.get(EventRepository.class);
		
		List<EventRegistration> eventRegistrationList = event.getEventRegistrationList();
		eventRegistrationList.add(entity);
		event.setTotalEntry(event.getTotalEntry() + 1);
		event.setEventRegistrationList(eventRegistrationList);
		eventRepository.save(event);
		return super.save(entity);
	}
}
