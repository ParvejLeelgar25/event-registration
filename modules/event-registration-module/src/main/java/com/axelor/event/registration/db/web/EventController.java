package com.axelor.event.registration.db.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.axelor.event.registration.db.Event;
import com.axelor.event.registration.db.EventRegistration;
import com.axelor.event.registration.db.report.ITranslation;
import com.axelor.i18n.I18n;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class EventController {

	public void validation(ActionRequest request, ActionResponse response) {
		Event event = request.getContext().asType(Event.class);

		if (event.getCapacity() < event.getTotalEntry()) {
			response.setError(I18n.get(ITranslation.CAPACITY_EXCEED));
		} else {
			LocalDate registrationOpen = event.getRegistrationOpen();
			LocalDate registrationClose = event.getRegistrationClose();

			for (EventRegistration eventRegistration : event.getEventRegistrationList()) {
				LocalDate registrationDate = eventRegistration.getRegistrationDate().toLocalDate();
				System.out.println("sfsdf");
				if (registrationOpen != null && registrationClose != null && registrationDate != null
						&& registrationDate.isBefore(registrationClose) && registrationDate.isAfter(registrationOpen)) {
					response.setError(I18n.get(ITranslation.DATE_BETWEEN));
				}
			}
		}
	}
}
