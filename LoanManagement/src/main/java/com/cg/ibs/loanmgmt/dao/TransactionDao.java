package com.cg.ibs.loanmgmt.dao;

import com.cg.ibs.loanmgmt.bean.LoanMaster;
import com.cg.ibs.loanmgmt.bean.TransactionBean;

public interface TransactionDao {
	TransactionBean createDebitTransaction(LoanMaster loanMaster, TransactionBean transaction);

	TransactionBean createCreditTransaction(LoanMaster loanMaster, TransactionBean transaction);

	TransactionBean createLoanDebitTransaction(LoanMaster loanMaster, TransactionBean transaction);

	TransactionBean createLoanCreditTransaction(LoanMaster loanMaster, TransactionBean transaction);

	TransactionBean createDebitTransactionForPreClosure(LoanMaster loanMaster, TransactionBean transaction);

	TransactionBean createCreditTransactionForPreClosure(LoanMaster loanMaster, TransactionBean transaction);

	TransactionBean createCreditTransactionForDeclinedPreClosure(LoanMaster loanMaster, TransactionBean transaction);
	
}
