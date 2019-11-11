package com.cg.ibs.loanmgmt.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
	@Table(name = "Transaction")
	public class TransactionBean implements Serializable {
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		@Column(name = "TRANS_ID", nullable = false, length = 10)
		private BigInteger transactionId;

		@Column(name = "TRANS_TYPE", nullable = false, length = 20)
		@Enumerated(EnumType.STRING)
		private TransactionType transactionType;
		
		@Column(name = "TRANS_DATE_TIME", nullable = false, length = 20)
		private LocalDateTime transactionDate;
		
		@Column(name = "AMOUNT", nullable = false, length = 10)
		private BigDecimal transactionAmount;
		
		@Column(name = "TRANS_MODE", nullable = false, length = 20)
		@Enumerated(EnumType.STRING)
		private TransactionMode transactionMode;
		
		@Column(name = "ACC_NO", nullable = false, length = 11)
		private BigInteger accountNumber;
		
		@Column(name = "TRANS_DESC", nullable = false, length = 40)
		private String transactionDescription;
		
		public BigInteger getTransactionId() {
			return transactionId;
		}

		public void setTransactionId(BigInteger transactionId) {
			this.transactionId = transactionId;
		}

		public TransactionType getTransactionType() {
			return transactionType;
		}

		public void setTransactionType(TransactionType transactionType) {
			this.transactionType = transactionType;
		}

		public LocalDateTime getTransactionDate() {
			return transactionDate;
		}

		public void setTransactionDate(LocalDateTime transactionDate) {
			this.transactionDate = transactionDate;
		}

		public BigDecimal getTransactionAmount() {
			return transactionAmount;
		}

		public void setTransactionAmount(BigDecimal transactionAmount) {
			this.transactionAmount = transactionAmount;
		}

		public TransactionMode getTransactionMode() {
			return transactionMode;
		}

		public void setTransactionMode(TransactionMode transactionMode) {
			this.transactionMode = transactionMode;
		}

		public BigInteger getAccountNumber() {
			return accountNumber;
		}

		public void setAccountNumber(BigInteger accountNumber) {
			this.accountNumber = accountNumber;
		}

		public String getTransactionDescription() {
			return transactionDescription;
		}

		public void setTransactionDescription(String transactionDescription) {
			this.transactionDescription = transactionDescription;
		}
}
