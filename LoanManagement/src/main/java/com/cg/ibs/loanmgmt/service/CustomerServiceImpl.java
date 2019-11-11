package com.cg.ibs.loanmgmt.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.ibs.loanmgmt.bean.Account;
import com.cg.ibs.loanmgmt.bean.AccountHolding;
import com.cg.ibs.loanmgmt.bean.CustomerBean;
import com.cg.ibs.loanmgmt.bean.DocumentBean;
import com.cg.ibs.loanmgmt.bean.LoanMaster;
import com.cg.ibs.loanmgmt.bean.LoanStatus;
import com.cg.ibs.loanmgmt.bean.LoanTypeBean;
import com.cg.ibs.loanmgmt.bean.TopUp;
import com.cg.ibs.loanmgmt.bean.TopUpStatus;
import com.cg.ibs.loanmgmt.bean.TransactionBean;
import com.cg.ibs.loanmgmt.dao.AccountDao;
import com.cg.ibs.loanmgmt.dao.AccountHoldingDao;
import com.cg.ibs.loanmgmt.dao.CustomerDao;
import com.cg.ibs.loanmgmt.dao.DocumentDao;
import com.cg.ibs.loanmgmt.dao.LoanMasterDao;
import com.cg.ibs.loanmgmt.dao.LoanTypeDao;
import com.cg.ibs.loanmgmt.dao.TopUpDao;
import com.cg.ibs.loanmgmt.dao.TransactionDao;
import com.cg.ibs.loanmgmt.util.JpaUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

@Service("CustomerService")
public class CustomerServiceImpl implements CustomerService {
	private static Logger LOGGER = Logger.getLogger(CustomerServiceImpl.class);
	@Autowired
	private LoanTypeDao loanTypeDao;
	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private LoanMasterDao loanMasterDao;
	@Autowired
	private TransactionDao transactionDao;
	@Autowired
	private AccountHoldingDao accountHoldingDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private DocumentDao documentDao;
	private LoanMaster loanMaster = new LoanMaster();
	private static DocumentBean document;
	@Autowired
	private TopUpDao topUpDao;

	@Override
	public LoanTypeBean getLoanTypeByTypeId(Integer typeId) {
		LOGGER.info("Fetching loan type details");
		return loanTypeDao.getLoanTypeByTypeID(typeId);
	}

	@Override
	public boolean verifyLoanAmount(BigDecimal loanAmount, BigDecimal maximumLimit, BigDecimal minimumLimit) {
		LOGGER.info("Verifying loan amount");
		boolean verify = false;
		if (loanAmount.compareTo(maximumLimit) < 0 && loanAmount.compareTo(minimumLimit) > 0) {
			verify = true;
		}
		return verify;
	}

	@Override
	public boolean verifyLoanTenure(int tenure) {
		LOGGER.info("Verifying loan tenure");
		boolean verify = false;
		if (tenure > 0) {
			verify = true;
		}
		return verify;
	}

	@Override
	public LoanMaster calculateEmi(LoanMaster loanMaster) {
		LOGGER.info("Calculating EMI amount");
		Float rate = loanTypeDao.getLoanTypeByTypeID(loanMaster.getTypeId()).getInterestRate() / (12 * 100);
		Double onePlusRPoweredN = Math.pow((rate + 1), loanMaster.getLoanTenure());
		BigDecimal principal = loanMaster.getLoanAmount();
		BigDecimal emi = principal.multiply(BigDecimal.valueOf((rate * onePlusRPoweredN) / (onePlusRPoweredN - 1)));
		loanMaster.setEmiAmount(emi.setScale(2, BigDecimal.ROUND_UP));
		return loanMaster;
	}

	@Override
	public boolean verifyCustomerLogin(String userId, String password) {
		LOGGER.info("Verifying customer login details");
		boolean login = false;
		CustomerBean customer = customerDao.getCustomerByUserId(userId);
		if (null != customer && password.equals(customer.getPassword())) {
			login = true;
		}
		return login;
	}

	@Override
	public CustomerBean getCustomer(String userId) {
		LOGGER.info("Fetching customer details");
		return customerDao.getCustomerByUserId(userId);
	}

	@Override
	public LoanMaster applyLoan(CustomerBean customer, LoanMaster loanMaster) throws IOException {
		LOGGER.info("Loan is being applied by the customer");
		EntityTransaction transaction = JpaUtil.getTransaction();
		loanMaster.setUci(customer.getUci());
		loanMaster.setAppliedDate(LocalDate.now());
		loanMaster.setBalance(loanMaster.getLoanAmount());
		loanMaster.setStatus(LoanStatus.PENDING);
		transaction.begin();
		LoanMaster appliedLoan = loanMasterDao.applyLoan(loanMaster);
		transaction.commit();
		return appliedLoan;
	}

	public DocumentBean uploadDocument(LoanMaster loanMaster, String path, Integer docId) throws IOException {
		LOGGER.info("Document's fields are being set.");
		document = new DocumentBean();
		EntityTransaction transaction = JpaUtil.getTransaction();
		if (docId == 0) {
			document.setApplicationNumber(loanMaster.getApplicationNumber());
			document.setAadharCard(getDocumentViaPath(path));
		}
		if (docId == 1) {
			document.setApplicationNumber(loanMaster.getApplicationNumber());
			document.setProperty_collateral(getDocumentViaPath(path));
		}
		if (docId == 2) {
			document.setApplicationNumber(loanMaster.getApplicationNumber());
			document.setAdmissionLetter(getDocumentViaPath(path));
		}
		if (docId == 3) {
			document.setApplicationNumber(loanMaster.getApplicationNumber());
			document.setPanCard(getDocumentViaPath(path));
		}
		if (docId == 4) {
			document.setApplicationNumber(loanMaster.getApplicationNumber());
			document.setVehicleRc(getDocumentViaPath(path));
		}
		transaction.begin();
		document = documentDao.uploadDocuments(document);
		transaction.commit();
		return document;

	}

	private static byte[] getDocumentViaPath(String path) throws IOException {
		LOGGER.info("Document is being uploaded for the applied loan via the pdf");
		byte[] content = null;
		FileInputStream inStream = new FileInputStream(path);
		content = new byte[(int) inStream.available()];
		inStream.read(content);
		inStream.close();
		return content;
	}

	public List<LoanMaster> getLoanListByUci(CustomerBean customer) {
		LOGGER.info("Loan details are being fetched using uci");
		List<LoanMaster> loanList = new ArrayList<>();
		loanList = loanMasterDao.getLoanListByUci(customer);
//		for (LoanMaster loanMaster : loanList) {
//			if (loanMaster.getStatus().equals(LoanStatus.PENDING)) {
//				loanMaster.setLoanAccountNumber(BigInteger.valueOf(""));
//				loanMaster.setNumOfEmisPaid(Integer.parseInt("0"));
//			}
//		}
		return loanList;
	}

	public LoanTypeBean getLoanTypeByTypeID(Integer typeId) {
		LOGGER.info("Loan type details are being fetched using type ID");
		return loanTypeDao.getLoanTypeByTypeID(typeId);
	}

	public BigDecimal calculateBalance(LoanMaster loanMaster) {
		LOGGER.info("Balance is being calculated");
		BigDecimal initialBalance = loanMaster.getBalance();
		Float rate = getLoanTypeByTypeID(loanMaster.getTypeId()).getInterestRate();
		BigDecimal paidInterest = (initialBalance
				.multiply(BigDecimal.valueOf((Math.pow(1 + rate / 100.0, 1.0 / 12.0)))))
						.subtract(loanMaster.getBalance());
		BigDecimal paidPrincipal = loanMaster.getEmiAmount().subtract(paidInterest);
		BigDecimal newBalance = loanMaster.getBalance().subtract(paidPrincipal);
		return newBalance;
	}

	public LoanMaster updateEMI(LoanMaster loanMaster) {
		LOGGER.info("Loan details are being updated after paying EMI");
		EntityTransaction transaction = JpaUtil.getTransaction();
		loanMaster.setBalance(calculateBalance(loanMaster).setScale(2, RoundingMode.HALF_UP));
		loanMaster.getSavingsAccount()
				.setBalance(loanMaster.getSavingsAccount().getBalance().subtract(loanMaster.getEmiAmount()));
		transaction.begin();
		LoanMaster loanMasterTemp = loanMasterDao.updateEmiDao(loanMaster);
		transaction.commit();
		if (closeLoan(loanMaster)) {
			loanMaster.setStatus(LoanStatus.CLOSED);
		}
		if (loanMaster.getNumOfEmisPaid() == loanMaster.getTotalNumOfEmis() - 1) {
			loanMaster.setBalance(loanMaster.getEmiAmount());
		}
		return loanMasterTemp;
	}

	private boolean closeLoan(LoanMaster loanMaster) {
		LOGGER.info("Loan status is being updated to closed");
		boolean close = false;
		if (loanMaster.getNumOfEmisPaid() == loanMaster.getTotalNumOfEmis()) {
			close = true;
		}
		return close;
	}

	public LoanMaster updateBalance(LoanMaster loanMaster) {
		LOGGER.info("Balance is being updated");
		EntityTransaction transaction = JpaUtil.getTransaction();
		BigDecimal balance = calculateBalance(loanMaster);
		transaction.begin();
		LoanMaster loanMasterTemp = loanMasterDao.updateBalanceDao(loanMaster, balance);
		transaction.commit();
		return loanMasterTemp;
	}

	@Override
	public TransactionBean createDebitTransaction(LoanMaster loanMaster) {
		LOGGER.info("Transaction has been created.");

		TransactionBean transaction = new TransactionBean();
		EntityTransaction txn = JpaUtil.getTransaction();
		txn.begin();
		transaction = transactionDao.createDebitTransaction(loanMaster, transaction);
		txn.commit();
		return transaction;
	}

	public boolean verifyLoanExist(BigInteger loanAccNumber) {
		LOGGER.info("Verifying if the loan exist.");
		boolean check = false;
		loanMaster = loanMasterDao.getLoanByLoanNumber(loanAccNumber);
		if (null != loanMaster) {
			check = true;
		}
		return check;
	}

	public boolean verifyPreClosureApplicable(BigInteger loanAccNum) {
		LOGGER.info("Verifying the preClosure applicability.");
		boolean check = false;
		loanMaster = loanMasterDao.getLoanByLoanNumber(loanAccNum);
		if (loanMaster.getNumOfEmisPaid() >= 0.25 * loanMaster.getTotalNumOfEmis()) {
			check = true;
		}
		return check;

	}

	public void updatePreClosure(LoanMaster loanMaster) {
		LOGGER.info("Updating the preClosure amount.");
		EntityTransaction transaction = JpaUtil.getTransaction();
		transaction.begin();
		loanMasterDao.updatePreClosureDao(loanMaster);
		transaction.commit();
	}

	public LoanMaster getLoanDetails(BigInteger loanAccNumber) {
		LOGGER.info("Loan Details are being fetched using loan number");
		return loanMasterDao.getLoanByLoanNumber(loanAccNumber);
	}

	public boolean debitReceiptGenerator(LoanMaster loanMaster, TransactionBean transaction)
			throws DocumentException, FileNotFoundException {
		LOGGER.info("Receipt is being generated");
		boolean check = false;
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document,
				new FileOutputStream("Receipt_" + loanMaster.getLoanAccountNumber() + "savings_"
						+ loanMaster.getSavingsAccount().getAccNo() + "_debit_" + loanMaster.getNumOfEmisPaid()
						+ "  _Emi.pdf"));
		document.open();

		document.add(new Paragraph("                        **********Acknowledgment Receipt**********"));
		document.add(new Paragraph("      "));
		document.add(new Paragraph("      "));
		document.add(new Paragraph("Transaction ID:      " + transaction.getTransactionId()));
		document.add(new Paragraph("Transaction Date:      " + transaction.getTransactionDate()));
		document.add(new Paragraph("Transaction Amount:      " + transaction.getTransactionAmount()));
		document.add(new Paragraph("Transaction Description:      " + transaction.getTransactionDescription()));
		document.add(new Paragraph("Transaction Mode:      " + transaction.getTransactionMode()));
		document.add(new Paragraph("Transaction Type:      " + transaction.getTransactionType()));
		document.close();
		writer.close();
		check = true;
		return check;
	}

	public boolean creditReceiptGenerator(LoanMaster loanMaster, TransactionBean transaction)
			throws DocumentException, FileNotFoundException {
		LOGGER.info("Receipt is being generated");
		boolean check = false;
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("Receipt_"
				+ loanMaster.getLoanAccountNumber() + "_credit_" + loanMaster.getNumOfEmisPaid() + "  _Emi.pdf"));
		document.open();

		document.add(new Paragraph("                        **********Acknowledgment Receipt**********"));
		document.add(new Paragraph("      "));
		document.add(new Paragraph("      "));
		document.add(new Paragraph("Transaction ID:      " + transaction.getTransactionId()));
		document.add(new Paragraph("Transaction Date:      " + transaction.getTransactionDate()));
		document.add(new Paragraph("Transaction Amount:      " + transaction.getTransactionAmount()));
		document.add(new Paragraph("Transaction Description:      " + transaction.getTransactionDescription()));
		document.add(new Paragraph("Transaction Mode:      " + transaction.getTransactionMode()));
		document.add(new Paragraph("Transaction Type:      " + transaction.getTransactionType()));
		document.close();
		writer.close();
		check = true;
		return check;
	}

	@Override
	public List<LoanMaster> getApprovedLoanListByUci(CustomerBean customer) {
		return loanMasterDao.getApprovedLoanListByUci(customer);
	}

	@Override
	public List<AccountHolding> getSavingAccountListByUci(CustomerBean customer) {
		return accountHoldingDao.getSavingAccountListByUci(customer);
	}

	@Override
	public Account getAccount(BigInteger accountNumber) {
		return accountDao.getAccount(accountNumber);
	}

	@Override
	public LoanMaster applyingLoan(LoanMaster loanMaster) throws IOException {
		LOGGER.info("Loan is being applied by the customer");
		EntityTransaction transaction = JpaUtil.getTransaction();
		loanMaster.setStatus(LoanStatus.SENT_FOR_VERIFICATION);
		transaction.begin();
		LoanMaster appliedLoan = loanMasterDao.applyLoan(loanMaster);
		transaction.commit();
		return appliedLoan;
	}

	@Override
	public boolean verifyTopupAmount(LoanMaster loanMasterTemp, BigDecimal topUpAmount) {
		boolean verify = false;
		if ((loanMasterTemp.getBalance().add(topUpAmount)).compareTo(loanMasterTemp.getLoanAmount()) >= 0) {
			verify = true;
		}
		return verify;
	}

	@Override
	public boolean verifyTopUpTenure(LoanMaster loanMasterTemp, Integer topUpTenure) {
		boolean verify = false;
		if (topUpTenure <= loanMasterTemp.getTotalNumOfEmis()) {
			verify = true;
		}
		return verify;
	}

	@Override
	public TopUp applyTopUp(CustomerBean customer, LoanMaster loanMaster, TopUp topUp) throws IOException {
		LOGGER.info("TopUp is being applied by the customer");
		EntityTransaction transaction = JpaUtil.getTransaction();
		topUp.setTopUpBalance(topUp.getTopUpAmount());
		topUp.setApplicationNumber(loanMaster.getApplicationNumber());
		topUp.setTopUpAppliedDate(LocalDate.now());
		topUp.setTopUpStatus(TopUpStatus.PENDING);
		transaction.begin();
		TopUp appliedTopUp = topUpDao.applyTopUp(topUp);
		transaction.commit();
		return appliedTopUp;

	}

	@Override
	public TransactionBean createCreditTransaction(LoanMaster loanMaster) {
		TransactionBean transaction = new TransactionBean();
		EntityTransaction txn = JpaUtil.getTransaction();
		txn.begin();
		transaction = transactionDao.createCreditTransaction(loanMaster, transaction);
		txn.commit();
		return transaction;
	}
}
