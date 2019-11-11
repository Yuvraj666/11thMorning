package com.cg.ibs.loanmgmt.bean;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "document")
@SequenceGenerator(name = "docseq", initialValue = 6000, allocationSize = 1)
public class DocumentBean implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "docseq")
	@Column(name = "document_Id", nullable = false)
	private long documentId;
	@Column(name = "application_num", nullable = false)
	private BigInteger applicationNumber;
	@Lob
	@Column(name = "aadhar_card", nullable = true)
	private byte[] aadharCard;
	@Lob
	@Column(name = "admission_letter", nullable = true)
	private byte[] admissionLetter;
	@Lob
	@Column(name = "property_collateral", nullable = true)
	private byte[] property_collateral;
	@Lob
	@Column(name = "vehicle_rc", nullable = true)
	private byte[] vehicleRc;
	@Lob
	@Column(name = "pan_card", nullable = true)
	private byte[] panCard;

	public DocumentBean() {
		super();
	}

	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	public BigInteger getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(BigInteger applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public byte[] getAadharCard() {
		return aadharCard;
	}

	public void setAadharCard(byte[] aadharCard) {
		this.aadharCard = aadharCard;
	}

	public byte[] getAdmissionLetter() {
		return admissionLetter;
	}

	public void setAdmissionLetter(byte[] admissionLetter) {
		this.admissionLetter = admissionLetter;
	}

	public byte[] getProperty_collateral() {
		return property_collateral;
	}

	public void setProperty_collateral(byte[] property_collateral) {
		this.property_collateral = property_collateral;
	}

	public byte[] getVehicleRc() {
		return vehicleRc;
	}

	public void setVehicleRc(byte[] vehicleRc) {
		this.vehicleRc = vehicleRc;
	}

	public byte[] getPanCard() {
		return panCard;
	}

	public void setPanCard(byte[] panCard) {
		this.panCard = panCard;
	}
}
