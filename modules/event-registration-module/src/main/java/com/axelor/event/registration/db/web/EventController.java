package com.axelor.event.registration.db.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import com.axelor.event.registration.db.Discount;
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
			if (event.getEventRegistrationList() != null) {
				for (EventRegistration eventRegistration : event.getEventRegistrationList()) {
					if (eventRegistration.getRegistrationDate() != null) {
						LocalDate registrationDate = eventRegistration.getRegistrationDate().toLocalDate();
						if (registrationOpen != null && registrationClose != null && registrationDate != null
								&& registrationDate.isBefore(registrationOpen)
								|| registrationDate.isAfter(registrationClose)) {
							response.setError(I18n.get(ITranslation.DATE_BETWEEN));
						}
					} else {
						response.setError(I18n.get(ITranslation.MISSING_REGISTRATION_DATE));
					}
				}
			}
		}
	}

	public void dateValidation(ActionRequest request, ActionResponse response) {
		Event event = request.getContext().asType(Event.class);
		LocalDate startDate = null;
		LocalDate endDate = null;

		if (event.getStartDate() != null) {
			startDate = event.getStartDate().toLocalDate();
			if (event.getEndDate() != null) {
				endDate = event.getEndDate().toLocalDate();
				if (startDate.isAfter(endDate)) {
					response.setFlash(I18n.get(ITranslation.START_DATE));
					response.setValue("startDate", null);
					response.setValue("endDate", null);
				}
			}
		}

		LocalDate registrationOpen = event.getRegistrationOpen();
		LocalDate registrationClose = event.getRegistrationClose();
		if (registrationOpen != null && registrationClose != null) {
			if (registrationOpen.isAfter(registrationClose)) {
				response.setFlash(I18n.get(ITranslation.REGISTRATION_OPEN));
				response.setValue("registrationOpen", null);
				response.setValue("registrationClose", null);
			}
		}

		if (registrationClose != null && startDate != null) {
			if (registrationClose.isAfter(startDate)) {
				response.setFlash(I18n.get(ITranslation.REGISTRATION_CLOSE));
				response.setValue("startDate", null);
				response.setValue("registrationClose", null);
			}
		}
	}

	public void validationOnDiscountList(ActionRequest request, ActionResponse response) {
		Event event = request.getContext().asType(Event.class);
		LocalDate registrationOpen = event.getRegistrationOpen();
		LocalDate registrationClose = event.getRegistrationClose();
		long days = ChronoUnit.DAYS.between(registrationOpen, registrationClose) + 1;

		if (event.getDiscountList() != null) {
			List<Discount> discountList = event.getDiscountList();
			int count = discountList.size() - 1;
			for (Discount discount : discountList) {
				long beforeDays = discount.getBeforeDays();
				if (beforeDays > days) {
					response.setFlash(I18n.get(ITranslation.BEFORE_DAYS));
					discountList.remove(count);
					event.setDiscountList(event.getDiscountList());
					response.setValue("discountList", event.getDiscountList());
					break;
				}
			}
		}
	}

	public void setAmount(ActionRequest request, ActionResponse response) {
		Event event = request.getContext().asType(Event.class);
		if (event.getEventRegistrationList() != null) {
			List<EventRegistration> eventRegistrationsList = event.getEventRegistrationList();
			int count = eventRegistrationsList.size() - 1;
			if (event.getCapacity() < event.getTotalEntry() + 1) {
				response.setFlash(I18n.get(ITranslation.CAPACITY_EXCEED));
				eventRegistrationsList.remove(count);
				event.setEventRegistrationList(eventRegistrationsList);
				response.setValue("eventRegistrationList", event.getEventRegistrationList());
			}
		}

	}
}
