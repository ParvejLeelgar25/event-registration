package com.axelor.event.registration.db.service;

import java.util.Map;

import com.axelor.meta.db.MetaFile;

public interface ImportEventRegistrationService {

	public void importEventRegistration(Integer eventId, MetaFile dataFile);

	public Object importRegistration(Object bean, Map<String, Object> values);
}
