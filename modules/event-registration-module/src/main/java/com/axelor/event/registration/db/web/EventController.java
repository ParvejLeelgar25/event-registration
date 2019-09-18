package com.axelor.event.registration.db.web;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.hslf.record.Sound;

import com.axelor.apps.message.db.EmailAddress;
import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.db.repo.EmailAccountRepository;
import com.axelor.apps.message.db.repo.EmailAddressRepository;
import com.axelor.apps.message.service.MessageService;
import com.axelor.data.Importer;
import com.axelor.data.csv.CSVImporter;
import com.axelor.event.registration.db.Discount;
import com.axelor.event.registration.db.Event;
import com.axelor.event.registration.db.EventRegistration;
import com.axelor.event.registration.db.report.ITranslation;
import com.axelor.event.registration.db.service.EventService;
import com.axelor.exception.AxelorException;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.common.io.Files;
import com.google.inject.Inject;


public class EventController {
	@Inject private EventService eventService;
	@Inject private MessageService messageService;
	
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
		
		if(registrationOpen != null && startDate != null) {
			if(registrationOpen.isAfter(startDate)) {
				response.setFlash(I18n.get(ITranslation.START_DATE_BEFORE));
				response.setValue("startDate", null);
				response.setValue("registrationOpen", null);
			}
		}
	}

	public void validationOnDiscountList(ActionRequest request, ActionResponse response) {
		Event event = request.getContext().asType(Event.class);
		LocalDate registrationOpen = event.getRegistrationOpen();
		LocalDate registrationClose = event.getRegistrationClose();
		long days = ChronoUnit.DAYS.between(registrationOpen, registrationClose);

		if (event.getDiscountList() != null) {
			List<Discount> discountList = event.getDiscountList();
			int count = discountList.size()-1;
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

	public void validationOnList(ActionRequest request, ActionResponse response) {
		Event event = request.getContext().asType(Event.class);
		if (event.getEventRegistrationList() != null) {
			List<EventRegistration> eventRegistrationsList = event.getEventRegistrationList();
			if (event.getCapacity() < event.getTotalEntry() + 1) {
				response.setError(I18n.get(ITranslation.CAPACITY_EXCEED));
			} else {
				LocalDate registrationOpen = event.getRegistrationOpen();
				LocalDate registrationClose = event.getRegistrationClose();
				if(eventRegistrationsList.size() > 0) {
					LocalDateTime registrationDateTime = event.getEventRegistrationList().get(eventRegistrationsList.size()-1).getRegistrationDate();
					if (registrationDateTime != null) {
						LocalDate registrationDate = registrationDateTime.toLocalDate();
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
	
	public void eventCalculation(ActionRequest request, ActionResponse response) {
		
		Event event = request.getContext().asType(Event.class);
		eventService.eventCalculation(event);
		response.setValue("amountCollected", event.getAmountCollected());
		response.setValue("totalDiscount", event.getTotalDiscount());
	}
	
	public void sendEmail(ActionRequest request, ActionResponse response) throws MessagingException, IOException, AxelorException {
		
		Event event = request.getContext().asType(Event.class);
		eventService.sendEmail(event);
	}
	
	public void importRegistration(ActionRequest request, ActionResponse response) {
	    Event event = request.getContext().asType(Event.class);
	    response.setView(
                ActionView.define("Invoice")
                    .model(Event.class.getName())
                    .add("form", "import-registration-form")
                    .context("event_id", event.getId())
                    .param("popup", "true")
                    .param("show-toolbar", "false")
                    .param("show-confirm", "false")
                    .param("popup-save", "false")
                    .param("forceEdit", "true")
                    .map());
	}
	
	public void importRegistrationData(ActionRequest request, ActionResponse response) throws IOException {
		
		Integer event_id = (Integer) request.getContext().get("event_id");
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) request.getContext().get("metaFile");
		MetaFile dataFile = Beans.get(MetaFileRepository.class).find(((Integer) map.get("id")).longValue());
		File file = MetaFiles.getPath(dataFile).toFile();
		String dataFileArray[] = dataFile.getFileName().split("\\.");
	    String dataFileType = dataFileArray[dataFileArray.length - 1];
	    
	     if(dataFileType.equals("csv")) {
	    	eventService.importRegistrationData(event_id,dataFile);
	    } else {
	    	response.setError("Please Select CSV file");
	    }
	     response.setCanClose(true);
	}
}

