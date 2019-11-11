package com.cg.ibs.loanmgmt.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "account_Holdings")
public class AccountHolding implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long aHId;
	@ManyToOne@JoinColumn
	private CustomerBean customer;
	@ManyToOne@JoinColumn
	private Account account;
	@Enumerated(EnumType.STRING)
	private AccountHoldingType type;

	public AccountHolding() {
		super();
	}

	public Long getaHId() {
		return aHId;
	}

	public void setaHId(Long aHId) {
		this.aHId = aHId;
	}

	public CustomerBean getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerBean customer) {
		this.customer = customer;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public AccountHoldingType getType() {
		return type;
	}

	public void setType(AccountHoldingType type) {
		this.type = type;
	}
}
