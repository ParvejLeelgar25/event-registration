package com.axelor.event.registration.db.web;

import java.time.LocalDate;

import com.axelor.event.registration.db.Event;
import com.axelor.event.registration.db.EventRegistration;
import com.axelor.event.registration.db.report.ITranslation;
import com.axelor.i18n.I18n;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class EventRegistrationController {

	public void validation(ActionRequest request, ActionResponse response) {
		EventRegistration eventRegistration = request.getContext().asType(EventRegistration.class);		
		if(eventRegistration.getRegistrationDate() != null && eventRegistration.getEvent() != null) {
			Event event = eventRegistration.getEvent();
			LocalDate registrationDate = eventRegistration.getRegistrationDate().toLocalDate();
			if (event.getCapacity() < (event.getTotalEntry() + 1)) {
				response.setError(I18n.get(ITranslation.CAPACITY_EXCEED));
			} else {
				LocalDate registrationOpen = event.getRegistrationOpen();
				LocalDate registrationClose = event.getRegistrationClose();
				if (registrationOpen != null && registrationClose != null && registrationDate != null
						&& registrationDate.isBefore(registrationClose) && registrationDate.isAfter(registrationOpen)) {
					response.setError(I18n.get(ITranslation.DATE_BETWEEN));
				}
			}
		} else {
			response.setError(I18n.get(ITranslation.MISSING_FIELD));
		}
	}
}
