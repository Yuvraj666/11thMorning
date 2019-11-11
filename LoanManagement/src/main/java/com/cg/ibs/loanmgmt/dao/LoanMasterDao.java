package com.cg.ibs.loanmgmt.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.cg.ibs.loanmgmt.bean.CustomerBean;
import com.cg.ibs.loanmgmt.bean.LoanMaster;

public interface LoanMasterDao {
	LoanMaster applyLoan(LoanMaster loanMaster);
	LoanMaster applyingLoan(LoanMaster loanMaster);
	public LoanMaster updateEmiDao(LoanMaster loanMaster);

	public LoanMaster updateBalanceDao(LoanMaster loanMaster, BigDecimal balance);

	List<LoanMaster> getSentForVerificationLoans();

	LoanMaster updateLoanApprovalDao(LoanMaster loanMasterTemp, BigInteger loanNumber);

	void updateLoanDenialDao(LoanMaster loanMasterTemp);

	List<LoanMaster> getPendingPreClosures();

	LoanMaster updatePreClosureApprovalDao(LoanMaster loanMasterTemp);

	List<LoanMaster> getLoanListByUci(CustomerBean customer);

	List<LoanMaster> getApprovedLoanListByUci(CustomerBean customer);

	public LoanMaster getLoanByLoanNumber(BigInteger loanAccNum);

	public void updatePreClosureDao(LoanMaster loanMaster);

	LoanMaster getLoanByApplicantNumber(BigInteger applicantNum);

	// LoanMaster payEmi(CustomerBean customer, LoanMaster loanMaster);

}
