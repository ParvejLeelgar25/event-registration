package com.axelor.event.registration.db.web;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
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
	@Inject
	private EventService eventService;
	@Inject
	private MessageService messageService;

	public void validation(ActionRequest request, ActionResponse response) {
		Event event = request.getContext().asType(Event.class);
		if(event.getEventRegistrationList() != null) {
			if (event.getCapacity() >= event.getEventRegistrationList().size()) {
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
			} else {
				response.setError(I18n.get(ITranslation.CAPACITY_EXCEED));
			}
		}
	}

	public void dateValidation(ActionRequest request, ActionResponse response) {
		Event event = request.getContext().asType(Event.class);
		LocalDateTime startDateTime = event.getStartDate();
		LocalDateTime endDateTime = event.getEndDate();
		LocalDate registrationOpen = event.getRegistrationOpen();
		LocalDate registrationClose = event.getRegistrationClose();
		LocalDate startDate = null;
		LocalDate endDate = null;

		if (startDateTime != null) {
			startDate = startDateTime.toLocalDate();
		}

		if (event.getStartDate() != null && event.getEndDate() != null) {
			endDate = event.getEndDate().toLocalDate();
			if (startDate.isAfter(endDate)) {
				response.setError(I18n.get(ITranslation.START_DATE));
			}
		}

		if (registrationOpen != null && startDateTime != null) {
			if (registrationOpen.isAfter(startDate) || registrationOpen.isEqual(startDate)) {
				response.setError(I18n.get(ITranslation.START_DATE_BEFORE));
			}
		}

		if (registrationClose != null && startDateTime != null) {
			if (registrationClose.isAfter(startDate) || registrationClose.isEqual(startDate)) {
				response.setError(I18n.get(ITranslation.REGISTRATION_CLOSE));
			}
		}

		if (registrationOpen != null && registrationClose != null) {
			if (registrationOpen.isAfter(registrationClose)) {
				response.setError(I18n.get(ITranslation.REGISTRATION_OPEN));
			}
		}
	}

	public void validationOnDiscountList(ActionRequest request, ActionResponse response) {
		Event event = request.getContext().asType(Event.class);
		LocalDate registrationOpen = event.getRegistrationOpen();
		LocalDate registrationClose = event.getRegistrationClose();
		int days = Period.between(registrationOpen, registrationClose).getDays();

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

	public void validationOnList(ActionRequest request, ActionResponse response) {
		Event event = request.getContext().asType(Event.class);
		if (event.getEventRegistrationList() != null) {
			List<EventRegistration> eventRegistrationsList = event.getEventRegistrationList();
			if (event.getCapacity() >= event.getEventRegistrationList().size()) {
				LocalDate registrationOpen = event.getRegistrationOpen();
				LocalDate registrationClose = event.getRegistrationClose();
				if (eventRegistrationsList.size() > 0) {
					LocalDateTime registrationDateTime = event.getEventRegistrationList()
							.get(eventRegistrationsList.size() - 1).getRegistrationDate();
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
			} else {
				response.setError(I18n.get(ITranslation.CAPACITY_EXCEED));
			}
		}
	}

	public void eventCalculation(ActionRequest request, ActionResponse response) {

		Event event = request.getContext().asType(Event.class);
		eventService.eventCalculation(event);
		response.setValue("amountCollected", event.getAmountCollected());
		response.setValue("totalDiscount", event.getTotalDiscount());
	}

	public void sendEmail(ActionRequest request, ActionResponse response)
			throws MessagingException, IOException, AxelorException {

		Event event = request.getContext().asType(Event.class);
		if(Beans.get(EmailAccountRepository.class).all().filter("self.isValid = ?1", true).fetchOne() != null){
			Boolean checkEmailList = eventService.sendEmail(event);
			if(checkEmailList) {
				response.setFlash("Emails are sending");
			} else {
				response.setFlash("No recieptant found");
			}
		} else {
			response.setError("Please configure mail account for send mail");
		}
	}

	public void importRegistrationData(ActionRequest request, ActionResponse response) throws IOException {

		Integer eventId = (Integer) request.getContext().get("_event_id");
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) request.getContext().get("metaFile");
		MetaFile dataFile = Beans.get(MetaFileRepository.class).find(((Integer) map.get("id")).longValue());
		File file = MetaFiles.getPath(dataFile).toFile();
		String dataFileArray[] = dataFile.getFileName().split("\\.");
		String dataFileType = dataFileArray[dataFileArray.length - 1];

		if (dataFileType.equals("csv")) {
			eventService.importRegistrationData(eventId, dataFile);
			response.setFlash("Data Imported");
		} else {
			response.setError("Please Select CSV file");
		}
		response.setCanClose(true);
	}
}
