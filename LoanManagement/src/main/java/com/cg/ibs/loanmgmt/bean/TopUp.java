package com.cg.ibs.loanmgmt.bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Top_Up")
public class TopUp {
	@Id
	@Column(name = "top_Up_Id")
	private BigInteger topUpId;
	@Column(name = "application_num", nullable = false)
	private BigInteger applicationNumber;
	@Column(name = "top_Up_Amount", nullable = false)
	private BigDecimal topUpAmount;
	@Column(name = "top_Up_tenure", nullable = false)
	private Integer topUpTenure;
	@Column(name = "top_Up_Emi", nullable = false)
	private BigDecimal topUpEmi;
	@Column(name = "top_Up_Balance", nullable = false)
	private BigDecimal topUpBalance;
	@Enumerated(EnumType.STRING)
	@Column(name = "top_up_status", nullable = false)
	private TopUpStatus topUpStatus;
	@Column(name = "topUp_applied_date")
	private LocalDate topUpAppliedDate;
	@Column(name = "topUp_approved_date")
	private LocalDate topUpApprovedDate;
	@Column(name = "num_of_topup", nullable = false)
	private Integer numOftopUps = 0;

	public BigInteger getTopUpId() {
		return topUpId;
	}

	public void setTopUpId(BigInteger topUpId) {
		this.topUpId = topUpId;
	}

	public BigInteger getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(BigInteger applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public BigDecimal getTopUpAmount() {
		return topUpAmount;
	}

	public void setTopUpAmount(BigDecimal topUpAmount) {
		this.topUpAmount = topUpAmount;
	}

	public Integer getTopUpTenure() {
		return topUpTenure;
	}

	public void setTopUpTenure(Integer topUpTenure) {
		this.topUpTenure = topUpTenure;
	}

	public BigDecimal getTopUpEmi() {
		return topUpEmi;
	}

	public void setTopUpEmi(BigDecimal topUpEmi) {
		this.topUpEmi = topUpEmi;
	}

	public BigDecimal getTopUpBalance() {
		return topUpBalance;
	}

	public void setTopUpBalance(BigDecimal topUpBalance) {
		this.topUpBalance = topUpBalance;
	}

	public TopUpStatus getTopUpStatus() {
		return topUpStatus;
	}

	public void setTopUpStatus(TopUpStatus topUpStatus) {
		this.topUpStatus = topUpStatus;
	}

	public LocalDate getTopUpAppliedDate() {
		return topUpAppliedDate;
	}

	public void setTopUpAppliedDate(LocalDate topUpAppliedDate) {
		this.topUpAppliedDate = topUpAppliedDate;
	}

	public LocalDate getTopUpApprovedDate() {
		return topUpApprovedDate;
	}

	public void setTopUpApprovedDate(LocalDate topUpApprovedDate) {
		this.topUpApprovedDate = topUpApprovedDate;
	}

	public Integer getNumOftopUps() {
		return numOftopUps;
	}

	public void setNumOftopUps(Integer numOftopUps) {
		this.numOftopUps = numOftopUps;
	}

	public TopUp() {
		super();
	}

}