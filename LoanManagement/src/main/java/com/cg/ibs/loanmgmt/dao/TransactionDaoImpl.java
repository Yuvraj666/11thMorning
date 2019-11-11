package com.cg.ibs.loanmgmt.dao;

import java.math.BigInteger;
import java.time.LocalDateTime;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.cg.ibs.loanmgmt.bean.LoanMaster;
import com.cg.ibs.loanmgmt.bean.TransactionBean;
import com.cg.ibs.loanmgmt.bean.TransactionMode;
import com.cg.ibs.loanmgmt.bean.TransactionType;
import com.cg.ibs.loanmgmt.util.JpaUtil;

@Repository("TransactionDao")
public class TransactionDaoImpl implements TransactionDao {
	private EntityManager entityManager;
	BigInteger bankAccountNumber = new BigInteger("652339604622");

	public TransactionDaoImpl() {
		entityManager = JpaUtil.getEntityManger();
	}

	@Override
	public TransactionBean createDebitTransaction(LoanMaster loanMaster, TransactionBean transaction) {
		transaction.setAccountNumber(loanMaster.getSavingsAccount().getAccNo());
		transaction.setTransactionAmount(loanMaster.getEmiAmount());
		transaction.setTransactionDate(LocalDateTime.now());
		transaction.setTransactionDescription(
				"Emi Payment: " + loanMaster.getLoanAccountNumber() + " Emi Number: " + loanMaster.getNumOfEmisPaid());
		transaction.setTransactionMode(TransactionMode.ONLINE);
		transaction.setTransactionType(TransactionType.DEBIT);
		entityManager.persist(transaction);
		return transaction;
	}

	@Override
	public TransactionBean createCreditTransaction(LoanMaster loanMaster, TransactionBean transaction) {
		transaction.setAccountNumber(loanMaster.getLoanAccountNumber());
		transaction.setTransactionAmount(loanMaster.getEmiAmount());
		transaction.setTransactionDate(LocalDateTime.now());
		transaction.setTransactionDescription(
				"Emi Payment: " + loanMaster.getLoanAccountNumber() + " Emi Number: " + loanMaster.getNumOfEmisPaid());
		transaction.setTransactionMode(TransactionMode.ONLINE);
		transaction.setTransactionType(TransactionType.CREDIT);
		entityManager.persist(transaction);
		return transaction;
	}

	@Override
	public TransactionBean createLoanDebitTransaction(LoanMaster loanMaster, TransactionBean transaction) {
		transaction.setAccountNumber(bankAccountNumber);
		transaction.setTransactionAmount(loanMaster.getLoanAmount());
		transaction.setTransactionDate(LocalDateTime.now());
		transaction.setTransactionDescription("Loan Payment for :" + loanMaster.getLoanAccountNumber());
		transaction.setTransactionMode(TransactionMode.ONLINE);
		transaction.setTransactionType(TransactionType.DEBIT);
		entityManager.persist(transaction);
		return transaction;
	}

	@Override
	public TransactionBean createLoanCreditTransaction(LoanMaster loanMaster, TransactionBean transaction) {
		transaction.setAccountNumber(loanMaster.getSavingsAccount().getAccNo());
		transaction.setTransactionAmount(loanMaster.getLoanAmount());
		transaction.setTransactionDate(LocalDateTime.now());
		transaction.setTransactionDescription("Loan Payment for :" + loanMaster.getLoanAccountNumber());
		transaction.setTransactionMode(TransactionMode.ONLINE);
		transaction.setTransactionType(TransactionType.CREDIT);
		entityManager.persist(transaction);
		return transaction;
	}

}
