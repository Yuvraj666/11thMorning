package com.cg.ibs.loanmgmt.bean;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "Loan_type")
public class LoanTypeBean {
	@Override
	public String toString() {
		return "LoanTypeBean [loanType=" + loanType + "]";
	}
	@Id @Column(name = "type_Id", nullable = false)
	private Integer typeId;
	@Column(name = "loan_type", nullable = false)
	private String loanType;
	@Column(name = "interest_rate", nullable = false)
	private Float interestRate;
	@Column(name = "maximum_limit", nullable = false)
	private BigDecimal maximumLimit;
	@Column(name = "minimum_limit", nullable = false)
	private BigDecimal minimumLimit;
	public Integer getTypeId() {
		return typeId;
	}
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}
	public String getLoanType() {
		return loanType;
	}
	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}
	public Float getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(Float interestRate) {
		this.interestRate = interestRate;
	}
	public BigDecimal getMaximumLimit() {
		return maximumLimit;
	}
	public void setMaximumLimit(BigDecimal maximumLimit) {
		this.maximumLimit = maximumLimit;
	}
	public BigDecimal getMinimumLimit() {
		return minimumLimit;
	}
	public void setMinimumLimit(BigDecimal minimumLimit) {
		this.minimumLimit = minimumLimit;
	}
	
}
	