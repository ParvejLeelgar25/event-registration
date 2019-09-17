package com.axelor.event.registration.db.repo;

import java.util.ArrayList;
import java.util.List;

import com.axelor.event.registration.db.Event;
import com.axelor.event.registration.db.EventRegistration;
import com.axelor.inject.Beans;

public class EventRegistrationManageRepository extends EventRegistrationRepository {

	@Override
	public EventRegistration save(EventRegistration entity) {
		
		if(entity.getEvent() != null) {
			Event event = entity.getEvent();
			EventRepository eventRepository = Beans.get(EventRepository.class);
			List<EventRegistration> eventRegistrationList = event.getEventRegistrationList();
			eventRegistrationList.add(entity);
			event.setAmountCollected(entity.getAmount().add(event.getAmountCollected()));
			event.setTotalDiscount(event.getTotalDiscount().add(event.getEventFees().subtract(entity.getAmount())));
			event.setTotalEntry(event.getTotalEntry() + 1);
			event.setEventRegistrationList(eventRegistrationList);
			eventRepository.save(event);
		}
		return super.save(entity);
	}

	@Override
	public void remove(EventRegistration entity) {
		
		if (entity.getEvent() != null) {
			Event event = entity.getEvent();
			EventRepository eventRepository = Beans.get(EventRepository.class);
			int count = 0;
			List<EventRegistration> eventRegistrationList = event.getEventRegistrationList();
			for (EventRegistration eventRegistration : eventRegistrationList) {
				if (eventRegistration.getId() == entity.getId()) {
					eventRegistrationList.remove(count);
					break;
				}
				count++;
			}if(event.getTotalEntry() > 0) {
				event.setTotalEntry(event.getTotalEntry() - 1);
			}
			
			event.setEventRegistrationList(eventRegistrationList);
			
			eventRepository.save(event);
		}

		super.remove(entity);
	}
}
