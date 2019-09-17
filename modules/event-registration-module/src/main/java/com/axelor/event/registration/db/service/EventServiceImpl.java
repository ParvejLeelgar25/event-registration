package com.axelor.event.registration.db.service;

import java.math.BigDecimal;
import java.util.List;

import com.axelor.event.registration.db.Event;
import com.axelor.event.registration.db.EventRegistration;

public class EventServiceImpl implements EventService{

	@Override
	public void eventCalculation(Event event) {
		BigDecimal amountCollected = BigDecimal.ZERO;
		BigDecimal totalDiscount = BigDecimal.ZERO;
		int temp;
		
		if(event.getEventRegistrationList() != null) {
			List<EventRegistration> eventRegistrationsList = event.getEventRegistrationList();
			for(EventRegistration eventRegistration : eventRegistrationsList) {
				amountCollected = amountCollected.add(eventRegistration.getAmount());
			}
			
			temp = event.getEventRegistrationList().size();
			totalDiscount = (event.getEventFees().multiply(BigDecimal.valueOf(temp))).subtract(amountCollected);
			event.setAmountCollected(amountCollected);
			event.setTotalDiscount(totalDiscount);
		}		
	}

}
