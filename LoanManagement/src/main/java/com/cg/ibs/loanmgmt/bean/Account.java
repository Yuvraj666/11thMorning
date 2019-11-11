package com.cg.ibs.loanmgmt.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "Account")
@NamedQueries({
		@NamedQuery(name = "ACC_BY_UCI", query = "Select a from Account a INNER JOIN a.accountHoldings h where h.customer.uci= :UCI") })
public class Account implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACC_SQ")
	@SequenceGenerator(sequenceName = "ACCOUNT_SEQUENCE", allocationSize = 1, name = "ACC_SQ")
	@Column(name = "account_number", nullable = false, length = 11)
	private BigInteger accNo;
	@Column(name = "balance", nullable = false, length = 20)
	private BigDecimal balance;
	@Column(name = "transac_pass", nullable = false, length = 15)
	private String trans_Pwd;
	@Column(name = "acc_creation_date", nullable = false)
	private LocalDate accCreationDate;
	@Column(name = "open_balance", nullable = false, length = 20)
	private BigDecimal openBalance;
	@Column(name = "acc_status", nullable = false, length = 7)
	@Enumerated(EnumType.STRING)
	private AccountStatus accStatus;
	@Column(name = "account_type", nullable = false, length = 17)
	@Enumerated(EnumType.STRING)
	private AccountType accType;
	@Column(name = "tenure", length = 7)
	private double tenure = -1;
	@Column(name = "maturity_amt", length = 20)
	private BigDecimal maturityAmt = new BigDecimal(-1);
	@OneToMany(mappedBy = "account", cascade = CascadeType.PERSIST)
	private Set<AccountHolding> accountHoldings;

	public Account() {
		super();
	}

	public BigInteger getAccNo() {
		return accNo;
	}

	public void setAccNo(BigInteger accNo) {
		this.accNo = accNo;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getTrans_Pwd() {
		return trans_Pwd;
	}

	public void setTrans_Pwd(String trans_Pwd) {
		this.trans_Pwd = trans_Pwd;
	}

	public LocalDate getAccCreationDate() {
		return accCreationDate;
	}

	public void setAccCreationDate(LocalDate accCreationDate) {
		this.accCreationDate = accCreationDate;
	}

	public BigDecimal getOpenBalance() {
		return openBalance;
	}

	public void setOpenBalance(BigDecimal openBalance) {
		this.openBalance = openBalance;
	}

	public AccountStatus getAccStatus() {
		return accStatus;
	}

	public void setAccStatus(AccountStatus accStatus) {
		this.accStatus = accStatus;
	}

	public AccountType getAccType() {
		return accType;
	}

	public void setAccType(AccountType accType) {
		this.accType = accType;
	}

	public double getTenure() {
		return tenure;
	}

	public void setTenure(double tenure) {
		this.tenure = tenure;
	}

	public BigDecimal getMaturityAmt() {
		return maturityAmt;
	}

	public void setMaturityAmt(BigDecimal maturityAmt) {
		this.maturityAmt = maturityAmt;
	}

	public Set<AccountHolding> getAccountHoldings() {
		return accountHoldings;
	}

	public void setAccountHoldings(Set<AccountHolding> accountHoldings) {
		this.accountHoldings = accountHoldings;
	}

}