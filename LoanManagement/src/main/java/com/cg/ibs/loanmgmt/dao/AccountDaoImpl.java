package com.cg.ibs.loanmgmt.dao;

import java.math.BigInteger;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.cg.ibs.loanmgmt.bean.Account;
import com.cg.ibs.loanmgmt.bean.LoanMaster;
import com.cg.ibs.loanmgmt.util.JpaUtil;

@Repository("AccountDao")
public class AccountDaoImpl implements AccountDao {
	private EntityManager entityManager;

	public AccountDaoImpl() {
		entityManager = JpaUtil.getEntityManger();
	}
	@Override
	public Account getAccount(BigInteger accountNumber) {
		return entityManager.find(Account.class, accountNumber);
	}
	@Override
	public Account addLoanAmount(LoanMaster loanMaster) {
		Account account = new Account();
		loanMaster.getSavingsAccount().setBalance(loanMaster.getLoanAmount());
		return account;
	}
	
}
