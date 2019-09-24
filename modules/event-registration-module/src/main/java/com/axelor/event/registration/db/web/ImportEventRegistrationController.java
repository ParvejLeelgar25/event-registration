package com.axelor.event.registration.db.web;

import java.io.File;
import java.util.LinkedHashMap;

import com.axelor.event.registration.db.service.ImportEventRegistrationService;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class ImportEventRegistrationController {

	@Inject
	ImportEventRegistrationService importEventRegistrationService;

	public void importRegistrationData(ActionRequest request, ActionResponse response) {

		Integer eventId = (Integer) request.getContext().get("_id");
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) request.getContext().get("metaFile");
		MetaFile dataFile = Beans.get(MetaFileRepository.class).find(((Integer) map.get("id")).longValue());
		File file = MetaFiles.getPath(dataFile).toFile();
		String dataFileArray[] = dataFile.getFileName().split("\\.");
		String dataFileType = dataFileArray[dataFileArray.length - 1];

		if (dataFileType.equals("csv")) {
			importEventRegistrationService.importEventRegistration(eventId, dataFile);
			response.setFlash("Data Imported");
		} else {
			response.setError("Please Select CSV file");
		}
		response.setCanClose(true);

	}

}
