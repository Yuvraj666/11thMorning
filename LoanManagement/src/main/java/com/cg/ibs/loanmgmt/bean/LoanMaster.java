package com.cg.ibs.loanmgmt.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "Loan")
@SequenceGenerator(name = "appseq", initialValue = 1000, allocationSize = 1)
public class LoanMaster implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appseq")
	@Column(name = "application_num", nullable = false)
	private BigInteger applicationNumber;
	@Column(nullable = false)
	private BigInteger uci;
	@Column(name = "loan_amount", nullable = false)
	private BigDecimal loanAmount;
	@Column(name = "loan_tenure", nullable = false)
	private int loanTenure;
	@Column(nullable = false)
	private BigDecimal balance;
	@Column(name = "applied_date", nullable = false)
	private LocalDate appliedDate;
	@Column(name = "total_num_of_emis", nullable = true)
	private Integer totalNumOfEmis;
	@Column(name = "num_of_emis_paid", nullable = true)
	private Integer numOfEmisPaid;
	@Column(name = "type_id", nullable = false)
	private Integer typeId;
	@Column(name = "emi_amount", nullable = false)
	private BigDecimal emiAmount;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LoanStatus status;
	@Column(name = "next_emi_date")
	private LocalDate nextEmiDate;
	@Column(name = "loan_account_num", unique = true)
	private BigInteger loanAccountNumber;
	@Column(name = "approved_date")
	private LocalDate approvedDate;
	@Column(name = "loan_closed_date")
	private LocalDate loanClosededDate;
	@Column(name = "preclosure_applied_date")
	private LocalDate preclosureAppliedDate;
	@Column(name = "preclosure_approved_date")
	private LocalDate preclosureApprovedDate;
	@Column(name = "top_Up_count")
	private BigDecimal topUpCount;
	@OneToOne
	private Account savingsAccount;

	public LocalDate getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(LocalDate approvedDate) {
		this.approvedDate = approvedDate;
	}

	public LocalDate getLoanClosededDate() {
		return loanClosededDate;
	}

	public void setLoanClosededDate(LocalDate loanClosededDate) {
		this.loanClosededDate = loanClosededDate;
	}

	public LocalDate getPreclosureAppliedDate() {
		return preclosureAppliedDate;
	}

	public void setPreclosureAppliedDate(LocalDate preclosureAppliedDate) {
		this.preclosureAppliedDate = preclosureAppliedDate;
	}

	public LocalDate getPreclosureApprovedDate() {
		return preclosureApprovedDate;
	}

	public void setPreclosureApprovedDate(LocalDate preclosureApprovedDate) {
		this.preclosureApprovedDate = preclosureApprovedDate;
	}

	public BigDecimal getTopUpCount() {
		return topUpCount;
	}

	public void setTopUpCount(BigDecimal topUpCount) {
		this.topUpCount = topUpCount;
	}

	public Account getSavingsAccount() {
		return savingsAccount;
	}

	public void setSavingsAccount(Account savingsAccount) {
		this.savingsAccount = savingsAccount;
	}

	public LoanMaster() {
		super();
	}

	public BigInteger getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(BigInteger applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public BigInteger getUci() {
		return uci;
	}

	public void setUci(BigInteger uci) {
		this.uci = uci;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}

	public int getLoanTenure() {
		return loanTenure;
	}

	public void setLoanTenure(int loanTenure) {
		this.loanTenure = loanTenure;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public LocalDate getAppliedDate() {
		return appliedDate;
	}

	public void setAppliedDate(LocalDate appliedDate) {
		this.appliedDate = appliedDate;
	}

	public Integer getTotalNumOfEmis() {
		return totalNumOfEmis;
	}

	public void setTotalNumOfEmis(Integer totalNumOfEmis) {
		this.totalNumOfEmis = totalNumOfEmis;
	}

	public Integer getNumOfEmisPaid() {
		return numOfEmisPaid;
	}

	public void setNumOfEmisPaid(Integer numOfEmisPaid) {
		this.numOfEmisPaid = numOfEmisPaid;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public BigDecimal getEmiAmount() {
		return emiAmount;
	}

	public void setEmiAmount(BigDecimal emiAmount) {
		this.emiAmount = emiAmount;
	}

	public LoanStatus getStatus() {
		return status;
	}

	public void setStatus(LoanStatus status) {
		this.status = status;
	}

	public LocalDate getNextEmiDate() {
		return nextEmiDate;
	}

	public void setNextEmiDate(LocalDate nextEmiDate) {
		this.nextEmiDate = nextEmiDate;
	}

	public BigInteger getLoanAccountNumber() {
		return loanAccountNumber;
	}

	public void setLoanAccountNumber(BigInteger loanAccountNumber) {
		this.loanAccountNumber = loanAccountNumber;
	}

}
