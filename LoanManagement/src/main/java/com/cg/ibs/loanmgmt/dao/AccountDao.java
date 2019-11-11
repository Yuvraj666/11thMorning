package com.cg.ibs.loanmgmt.dao;

import java.math.BigInteger;

import com.cg.ibs.loanmgmt.bean.Account;
import com.cg.ibs.loanmgmt.bean.LoanMaster;

public interface AccountDao {
	Account getAccount(BigInteger accountNumber);
	Account addLoanAmount(LoanMaster loanMaster);
}
