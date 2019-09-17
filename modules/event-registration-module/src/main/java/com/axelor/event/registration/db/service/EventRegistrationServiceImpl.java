package com.axelor.event.registration.db.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.axelor.event.registration.db.Discount;
import com.axelor.event.registration.db.Event;
import com.axelor.event.registration.db.EventRegistration;

public class EventRegistrationServiceImpl implements EventRegistrationService {

	@Override
	public void calculation(EventRegistration eventRegistration, Event event) {
		if (event.getDiscountList() != null) {
			LocalDate registrationDate = eventRegistration.getRegistrationDate().toLocalDate();
			List<Discount> discountList = event.getDiscountList();
			long days = ChronoUnit.DAYS.between(registrationDate, event.getRegistrationClose());
			BigDecimal discountAmount = BigDecimal.ZERO;
			for (Discount discount : discountList) {
				if (discount.getBeforeDays() <= days && discount.getDiscountAmount().compareTo(discountAmount) == 1) {
					discountAmount = discount.getDiscountAmount();
				}
			}
			eventRegistration.setAmount(event.getEventFees().subtract(discountAmount));
		} else {
			eventRegistration.setAmount(event.getEventFees());
		}

	}

}
