package com.axelor.event.registration.db.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axelor.app.AppSettings;
import com.axelor.data.Importer;
import com.axelor.data.csv.CSVImporter;
import com.axelor.event.registration.db.Event;
import com.axelor.event.registration.db.EventRegistration;
import com.axelor.exception.AxelorException;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.common.io.Files;
import com.google.inject.Inject;

public class ImportEventRegistrationServiceImpl implements ImportEventRegistrationService {

	private final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Inject
	EventRegistrationService eventRegistrationService;
	@Inject
	EventService eventService;

	@Override
	public void importEventRegistration(Integer eventId, MetaFile dataFile) {

		File configXmlFile = this.getConfigXmlFile();
		File dataCsvFile = this.getDataCsvFile(dataFile);

		Map<String, Object> importContext = new HashMap<String, Object>();
		importContext.put("_eventId", eventId);
		CSVImporter importer = new CSVImporter(configXmlFile.getAbsolutePath(), dataCsvFile.getParent());
		importer.setContext(importContext);
		importer.run();
	}

	private File getDataCsvFile(MetaFile dataFile) {

		String csvFilePath = AppSettings.get().get("file.upload.dir");
		File csvFile = new File(csvFilePath, "ImportRegistration.csv");
		try {
			if (dataFile != null) {
				Files.copy(MetaFiles.getPath(dataFile).toFile(), csvFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return csvFile;
	}

	private File getConfigXmlFile() {

		File configFile = null;
		try {
			configFile = File.createTempFile("input-config", ".xml");

			InputStream bindFileInputStream = this.getClass()
					.getResourceAsStream("/data-init/event-reg-input-config.xml");

			if (bindFileInputStream == null) {
				throw new AxelorException();
			}

			FileOutputStream outputStream = new FileOutputStream(configFile);

			IOUtils.copy(bindFileInputStream, outputStream);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return configFile;
	}

	@Override
	public Object importRegistration(Object bean, Map<String, Object> values) {

		assert bean instanceof EventRegistration;

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
					LOG.error("Error when importing registration");
				}
			} else {
				eventRegistration = null;
				LOG.error("Error when importing registration");
			}
		} else {
			eventRegistration = null;
			LOG.error("Error when importing registration");
		}
		return eventRegistration;
	}

}
