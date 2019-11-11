package com.cg.ibs.loanmgmt.bean;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Customers")
public class CustomerBean implements Serializable {

	@Id
	@Column(name = "UCI", length = 16)
	private BigInteger uci;
	@Column(name = "USER_ID", nullable = false, length = 15)
	private String userId;
	@Column(name = "PASSWORD", nullable = true, length = 15)
	private String password;
	@Column(name = "applicant_id", nullable = false, length = 5)
	private long applicantId;
	@Column(name = "first_name", nullable = false, length = 20)
	private String firstName;
	@Column(name = "last_name", nullable = false, length = 20)
	private String lastName;
	@Column(name = "father_name", nullable = false, length = 40)
	private String fatherName;
	@Column(name = "mother_name", nullable = false, length = 40)
	private String motherName;
	@Column(name = "DOB", nullable = false)
	private LocalDate dateOfBirth;
	@Enumerated(EnumType.STRING)
	@Column(name = "GENDER", nullable = false, length = 20)
	private Gender gender;
	@Column(name = "mobile_number", nullable = false, length = 10)
	private String mobileNumber;
	@Column(name = "alternate_mobile_number", nullable = false, length = 10)
	private String alternateMobileNumber;
	@Column(name = "email_id", nullable = false, length = 35)
	private String emailId;
	@Column(name = "aadhar_number", nullable = false, length = 12)
	private String aadharNumber;
	@Column(name = "pan_number", nullable = false, length = 10)
	private String panNumber;
	 @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
	    private Set<AccountHolding> accountHoldings;
	
	public CustomerBean() {
		super();
	}

	public BigInteger getUci() {
		return uci;
	}

	public void setUci(BigInteger uci) {
		this.uci = uci;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getApplicantId() {
		return applicantId;
	}

	public void setApplicantId(long applicantId) {
		this.applicantId = applicantId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getAlternateMobileNumber() {
		return alternateMobileNumber;
	}

	public void setAlternateMobileNumber(String alternateMobileNumber) {
		this.alternateMobileNumber = alternateMobileNumber;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

}
