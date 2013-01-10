package com.teamkn.model;

import java.io.Serializable;

public class QRCodeResult implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public Class<?> result_activity;
	public String symbology;
	public String barcode;

	public QRCodeResult(Class<?> result_activity) {
		super();
		this.result_activity = result_activity;
	}

	public QRCodeResult(String symbology, String barcode) {
		super();
		this.symbology = symbology;
		this.barcode = barcode;
	}
	
}
