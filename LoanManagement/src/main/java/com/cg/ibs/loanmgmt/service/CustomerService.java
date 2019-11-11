package com.cg.ibs.loanmgmt.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.cg.ibs.loanmgmt.bean.Account;
import com.cg.ibs.loanmgmt.bean.AccountHolding;
import com.cg.ibs.loanmgmt.bean.CustomerBean;
import com.cg.ibs.loanmgmt.bean.DocumentBean;
import com.cg.ibs.loanmgmt.bean.LoanMaster;
import com.cg.ibs.loanmgmt.bean.LoanTypeBean;
import com.cg.ibs.loanmgmt.bean.TopUp;
import com.cg.ibs.loanmgmt.bean.TransactionBean;
import com.itextpdf.text.DocumentException;

public interface CustomerService {
	LoanTypeBean getLoanTypeByTypeId(Integer typeId);

	boolean verifyLoanAmount(BigDecimal loanAmount, BigDecimal maximumLimit, BigDecimal minimumLimit);

	public boolean verifyLoanTenure(int tenure);

	LoanMaster calculateEmi(LoanMaster loanMaster);

	boolean verifyCustomerLogin(String userId, String password);

	LoanMaster applyLoan(CustomerBean customer, LoanMaster loanMaster) throws IOException;

	LoanMaster applyingLoan(LoanMaster loanMaster) throws IOException;

	CustomerBean getCustomer(String userId);

	List<AccountHolding> getSavingAccountListByUci(CustomerBean customer);

	Account getAccount(BigInteger accountNumber);

	List<LoanMaster> getLoanListByUci(CustomerBean customer);

	List<LoanMaster> getApprovedLoanListByUci(CustomerBean customer);

	TransactionBean createDebitTransaction(LoanMaster loanMaster);

	TransactionBean createCreditTransaction(LoanMaster loanMaster);

	public BigDecimal calculateBalance(LoanMaster loanMaster);

	public LoanMaster updateEMI(LoanMaster loanMaster);

	public LoanMaster updateBalance(LoanMaster loanMaster);

	public LoanTypeBean getLoanTypeByTypeID(Integer typeId);

	public boolean verifyLoanExist(BigInteger loanAccNumber);

	public boolean verifyPreClosureApplicable(BigInteger loanAccNum);

	public void updatePreClosure(LoanMaster loanMaster);

	public LoanMaster getLoanDetails(BigInteger loanAccNumber);

	public boolean debitReceiptGenerator(LoanMaster loanMaster, TransactionBean transaction)
			throws DocumentException, FileNotFoundException;

	public DocumentBean uploadDocument(LoanMaster loanMaster, String path, Integer docId) throws IOException;

	public boolean verifyTopupAmount(LoanMaster loanMasterTemp, BigDecimal topUpAmount);

	public boolean verifyTopUpTenure(LoanMaster loanMasterTemp, Integer topUpTenure);

	TopUp applyTopUp(CustomerBean customer, LoanMaster loanMaster, TopUp topUp) throws IOException;

	boolean creditReceiptGenerator(LoanMaster loanMaster, TransactionBean transaction) throws DocumentException, FileNotFoundException;

	TransactionBean createDebitTransactionForPreClosure(LoanMaster loanMaster);
}
