package com.axelor.event.registration.db.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.axelor.event.registration.db.Discount;
import com.axelor.event.registration.db.Event;
import com.axelor.event.registration.db.EventRegistration;
import com.axelor.event.registration.db.report.ITranslation;
import com.axelor.i18n.I18n;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class EventRegistrationController {

	public void validation(ActionRequest request, ActionResponse response) {
		EventRegistration eventRegistration = request.getContext().asType(EventRegistration.class);
		if (eventRegistration.getRegistrationDate() != null && eventRegistration.getEvent() != null) {
			Event event = eventRegistration.getEvent();
			LocalDate registrationDate = eventRegistration.getRegistrationDate().toLocalDate();
			if (event.getCapacity() < (event.getTotalEntry() + 1)) {
				response.setError(I18n.get(ITranslation.CAPACITY_EXCEED));
			} else {
				LocalDate registrationOpen = event.getRegistrationOpen();
				LocalDate registrationClose = event.getRegistrationClose();
				if (registrationOpen != null && registrationClose != null && registrationDate != null
						&& registrationDate.isBefore(registrationOpen) || registrationDate.isAfter(registrationClose)) {
					response.setError(I18n.get(ITranslation.DATE_BETWEEN));
				}
			}
		} else {
			response.setError(I18n.get(ITranslation.MISSING_FIELD));
		}
	}

	public void calculation(ActionRequest request, ActionResponse response) {
		EventRegistration eventRegistration = request.getContext().asType(EventRegistration.class);
		Event event = request.getContext().getParent().asType(Event.class);

		LocalDate registrationOpen = event.getRegistrationOpen();
		LocalDate registrationClose = event.getRegistrationClose();
		LocalDateTime registrationDateTime = eventRegistration.getRegistrationDate();
		if (registrationDateTime != null) {

			LocalDate registrationDate = registrationDateTime.toLocalDate();
			if (registrationOpen != null && registrationClose != null && registrationDate != null
					&& registrationDate.isBefore(registrationClose) && registrationDate.isAfter(registrationOpen)) {
				if (event.getDiscountList() != null) {
					List<Discount> discountList = event.getDiscountList();
					long days = ChronoUnit.DAYS.between(registrationDate, registrationClose);
					BigDecimal discountAmount = BigDecimal.ZERO;
					for (Discount discount : discountList) {
						if (discount.getBeforeDays() <= days
								&& discount.getDiscountAmount().compareTo(discountAmount) == 1) {
							discountAmount = discount.getDiscountAmount();
						}
					}
					response.setValue("amount", event.getEventFees().subtract(discountAmount));

				} else {
					response.setValue("amount", event.getEventFees());
				}
			} else {
				response.setError(I18n.get(ITranslation.DATE_BETWEEN));
			}
		} else {
			response.setError(I18n.get(ITranslation.MISSING_REGISTRATION_DATE));
		}
	}

}
