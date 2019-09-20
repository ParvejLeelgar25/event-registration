package com.axelor.event.registration.db.report;

public interface ITranslation {

	public static final String CAPACITY_EXCEED = /*$$(*/ "Registration Can't Exceed Capacity"; /*)*/
	
	public static final String DATE_BETWEEN = /*$$(*/ "Registration Date must between Event registration open and registration close"; /*)*/
	
	public static final String MISSING_FIELD = /*$$(*/ "Please fill Registration Date and Event field"; /*)*/
	
	public static final String MISSING_REGISTRATION_DATE = /*$$(*/ "Please fill Registration Date "; /*)*/
	
	public static final String START_DATE = /*$$(*/ "start date should not be ahead of endDate"; /*)*/
	
	public static final String REGISTRATION_OPEN = /*$$(*/ "Registration Open should not be ahead of registration close"; /*)*/
	
	public static final String REGISTRATION_CLOSE = /*$$(*/ "Registration close should not be ahead or equal to start date"; /*)*/
	
	public static final String BEFORE_DAYS = /*$$(*/ "Exceed duration between open and close registration dates"; /*)*/
	
	public static final String START_DATE_BEFORE = /*$$(*/ "Registration Open should not be ahead or equal to start date"; /*)*/
	
}
