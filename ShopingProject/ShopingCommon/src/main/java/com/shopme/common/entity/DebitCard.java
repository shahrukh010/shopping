package com.shopme.common.entity;

import java.sql.Date;

public class DebitCard {

	private final long CARD_NUMBER;
	private final String CARD_HOLDER_NAME;
	private final Date EXPIRE_DATE;
	private final int CVV;

	public DebitCard(long cARD_NUMBER, String cARD_HOLDER_NAME, Date eXPIRE_DATE, int cVV) {
		super();
		CARD_NUMBER = cARD_NUMBER;
		CARD_HOLDER_NAME = cARD_HOLDER_NAME;
		EXPIRE_DATE = eXPIRE_DATE;
		CVV = cVV;
	}

}
