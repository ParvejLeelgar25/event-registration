package com.axelor.event.registration.db.service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axelor.event.registration.db.Event;
import com.axelor.event.registration.db.EventRegistration;
import com.google.inject.Inject;

public class ImportRegistration {

	private final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Inject
	EventRegistrationService eventRegistrationService;
	@Inject
	EventService eventService;

	public Object importRegistration(Object bean, Map<String, Object> values) {

		/* assert bean instanceof Event; */
		assert bean instanceof EventRegistration;
		/* Event event = (Event) bean; */
		EventRegistration eventRegistration = (EventRegistration) bean;
		Event event = eventRegistration.getEvent();
		if (event.getCapacity() > event.getTotalEntry()) {
			LocalDate registrationOpen = event.getRegistrationOpen();
			LocalDate registrationClose = event.getRegistrationClose();
			LocalDateTime registrationDateTime = eventRegistration.getRegistrationDate();
			if (registrationDateTime != null) {
				LocalDate registrationDate = registrationDateTime.toLocalDate();
				if (registrationOpen != null && registrationClose != null && registrationDate != null
						&& registrationDate.isBefore(registrationClose) && registrationDate.isAfter(registrationOpen)) {
					eventRegistrationService.calculation(eventRegistration, event);
					event.setTotalEntry(event.getTotalEntry() + 1);
					event.setAmountCollected(event.getAmountCollected().add(eventRegistration.getAmount()));
					event.setTotalDiscount(
							event.getTotalDiscount().add(event.getEventFees().subtract(eventRegistration.getAmount())));
				} else {
					eventRegistration = null;
					LOG.error("Error when importing registration :");
				}
			} else {
				eventRegistration = null;
				LOG.error("Error when importing registration :");
			}
		} else {
			eventRegistration = null;
			LOG.error("Error when importing registration :");
		}
		return eventRegistration;
	}
}
