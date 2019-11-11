package com.cg.ibs.loanmgmt.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.ibs.loanmgmt.bean.BankAdmins;
import com.cg.ibs.loanmgmt.bean.CustomerBean;
import com.cg.ibs.loanmgmt.bean.DocumentBean;
import com.cg.ibs.loanmgmt.bean.LoanMaster;
import com.cg.ibs.loanmgmt.bean.LoanTypeBean;
import com.cg.ibs.loanmgmt.bean.TopUp;
import com.cg.ibs.loanmgmt.bean.TopUpStatus;
import com.cg.ibs.loanmgmt.bean.TransactionBean;
import com.cg.ibs.loanmgmt.dao.BankAdminsDao;
import com.cg.ibs.loanmgmt.dao.CustomerDao;
import com.cg.ibs.loanmgmt.dao.DocumentDao;
import com.cg.ibs.loanmgmt.dao.LoanMasterDao;
import com.cg.ibs.loanmgmt.dao.LoanTypeDao;
import com.cg.ibs.loanmgmt.dao.TopUpDao;
import com.cg.ibs.loanmgmt.dao.TransactionDao;
import com.cg.ibs.loanmgmt.util.JpaUtil;

@Service("BankService")
public class BankServiceImpl implements BankService {
	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private LoanMasterDao loanMasterDao;
	@Autowired
	private BankAdminsDao bankAdminsDao;
	@Autowired
	private LoanTypeDao loanTypeDao;
	@Autowired
	private DocumentDao documentDao;
	@Autowired
	private TopUpDao topUpDao;
	@Autowired
	private TransactionDao transactionDao;
	private TopUp topUp = new TopUp();

	private static Logger LOGGER = Logger.getLogger(BankServiceImpl.class);
	private LoanMaster loanMaster = new LoanMaster();

	@Override
	public boolean verifyBankLogin(String userId, String password) {
		LOGGER.info("Verifying customer login details");
		boolean login = false;
		BankAdmins admin = bankAdminsDao.getAdminByUserId(userId);
		if (null != admin && password.equals(admin.getPassword())) {
			login = true;
		}
		return login;
	}

	public LoanTypeBean getLoanTypeByTypeID(Integer typeId) {
		return loanTypeDao.getLoanTypeByTypeID(typeId);
	}

	public CustomerBean getCustomerDetailsByUci(BigInteger uci) {
		return customerDao.getCustomerDetailsByUci(uci);
	}

	public BigInteger generateLoanNumber(LoanMaster loanMaster) {
		StringBuilder sb = new StringBuilder();
		sb.append(loanMaster.getAppliedDate().getYear()).append(loanMaster.getAppliedDate().getMonthValue())
				.append(loanMaster.getApplicationNumber());
		BigInteger bigInteger = new BigInteger(sb.toString());
		return bigInteger;

	}

	public void updateLoanDenial(LoanMaster loanMasterTemp) {
		EntityTransaction transaction = JpaUtil.getTransaction();
		transaction.begin();
		loanMasterDao.updateLoanDenialDao(loanMasterTemp);
		transaction.commit();

	}

	public LoanMaster updateLoanApproval(LoanMaster loanMasterTemp) {
		EntityTransaction transaction = JpaUtil.getTransaction();
		transaction.begin();
		loanMaster = loanMasterDao.updateLoanApprovalDao(loanMasterTemp, generateLoanNumber(loanMasterTemp));
		transaction.commit();
		return loanMaster;
	}

	@Override
	public BankAdmins getBankAdmin(String userId) {
		LOGGER.info("Fetching customer details");
		return bankAdminsDao.getAdminByUserId(userId);
	}

	public List<LoanMaster> getSentForVerificationLoans() {
		List<LoanMaster> listTemp = null;
		listTemp = loanMasterDao.getSentForVerificationLoans();
		return listTemp;
	}

	public List<LoanMaster> getPendingPreClosures() {
		List<LoanMaster> listTemp = null;
		listTemp = loanMasterDao.getPendingPreClosures();
		return listTemp;
	}

	public LoanMaster updatePreClosureApproval(LoanMaster loanMasterTemp) {
		EntityTransaction transaction = JpaUtil.getTransaction();
		transaction.begin();
		loanMaster = loanMasterDao.updatePreClosureApprovalDao(loanMasterTemp);
		transaction.commit();
		return loanMaster;
	}

	@Override
	public void downloadDocument(LoanMaster loanMaster) {
		List<DocumentBean> documents = documentDao.getDocumentByApplicantNum(loanMaster.getApplicationNumber());
		byte[] aadhar = null;
		byte[] loanSpecificDocument = null;
		if (loanMaster.getTypeId().equals(1)) {
			for (DocumentBean documentBean : documents) {
				if (!(documentBean.getAadharCard() == null)) {
					aadhar = documentBean.getAadharCard();
					System.out.println(documentBean.getDocumentId());
				}
				if (!(documentBean.getProperty_collateral() == null)) {
					loanSpecificDocument = documentBean.getProperty_collateral();
					System.out.println(documentBean.getDocumentId());
				}
			}
		}
		if (loanMaster.getTypeId().equals(2)) {
			for (DocumentBean documentBean : documents) {
				if (!(documentBean.getAadharCard() == null)) {
					aadhar = documentBean.getAadharCard();
				}
				if (!(documentBean.getAdmissionLetter() == null)) {
					loanSpecificDocument = documentBean.getAdmissionLetter();
				}
			}
		}
		if (loanMaster.getTypeId().equals(3)) {
			for (DocumentBean documentBean : documents) {
				if (!(documentBean.getAadharCard() == null)) {
					aadhar = documentBean.getAadharCard();
				}
				if (!(documentBean.getPanCard() == null)) {
					loanSpecificDocument = documentBean.getPanCard();
				}
			}
		}
		if (loanMaster.getTypeId().equals(4)) {
			for (DocumentBean documentBean : documents) {
				if (!(documentBean.getAadharCard() == null)) {
					aadhar = documentBean.getAadharCard();
				}
				if ((!(documentBean.getVehicleRc() == null))) {
					loanSpecificDocument = documentBean.getVehicleRc();
				}
			}
		}
		File dir = new File("./downloads");
		if (!dir.exists()) {
			dir.mkdir();
		}
		try (FileOutputStream outputStream = new FileOutputStream(
				dir.getPath() + "/" + "Aadhar_" + loanMaster.getApplicationNumber() + ".pdf");
				FileOutputStream outputStream2 = new FileOutputStream(
						dir.getPath() + "/" + "loan_" + loanMaster.getApplicationNumber() + ".pdf")) {
			outputStream.write(aadhar);
			outputStream2.write(loanSpecificDocument);
			outputStream.flush();
			outputStream2.flush();
			outputStream.close();
			outputStream.close();
		} catch (FileNotFoundException exp) {
			System.out.println(exp.getMessage());
		} catch (IOException exp1) {
			System.out.println(exp1.getMessage());
		}
	}

	@Override
	public TopUp setTopUp(TopUp topUpTemp) {
		EntityTransaction transaction = JpaUtil.getTransaction();
		transaction.begin();
		topUpTemp.setTopUpStatus(TopUpStatus.APPROVED);
		topUpTemp.setNumOftopUps(topUp.getNumOftopUps() + 1);
		generateTopUpId(topUpTemp);
		topUpDao.updateTopUpApprovalDao(topUpTemp);
		transaction.commit();
		return topUp;
	}

	public BigInteger generateTopUpId(TopUp topUpTemp) {
		StringBuilder sb = new StringBuilder();
		sb.append(topUpTemp.getApplicationNumber()).append("-").append(topUpTemp.getNumOftopUps());
		BigInteger topUpId = new BigInteger(sb.toString());
		return topUpId;
	}

	@Override
	public void updateTopUpDenial(TopUp topUp) {
		EntityTransaction transaction = JpaUtil.getTransaction();
		transaction.begin();
		topUp.setTopUpStatus(TopUpStatus.DENIED);
		topUpDao.updateTopUpDenialDao(topUp);
		transaction.commit();

	}

	@Override
	public LoanMaster getLoanByApplicantNum(BigInteger applicantNum) {
		EntityTransaction transaction = JpaUtil.getTransaction();
		transaction.begin();
		loanMaster = loanMasterDao.getLoanByApplicantNumber(applicantNum);
		transaction.commit();
		return null;
	}

	@Override
	public List<TopUp> getPendingTopUp() {
		List<TopUp> listPendingTopUp = null;
		listPendingTopUp = topUpDao.getPendingTopUp();
		return listPendingTopUp;
	}

	@Override
	public TransactionBean createLoanDebitTransaction(LoanMaster loanMaster) {
		LOGGER.info("Loan approval Transaction has been created.");
		TransactionBean transaction = new TransactionBean();
		EntityTransaction txn = JpaUtil.getTransaction();
		txn.begin();
		transaction = transactionDao.createLoanDebitTransaction(loanMaster, transaction);
		txn.commit();
		return transaction;
	}

	@Override
	public TransactionBean createLoanCreditTransaction(LoanMaster loanMaster) {
		LOGGER.info("Loan approval Credit Transaction has been created.");
		TransactionBean transaction = new TransactionBean();
		EntityTransaction txn = JpaUtil.getTransaction();
		txn.begin();
		transaction = transactionDao.createLoanCreditTransaction(loanMaster, transaction);
		txn.commit();
		return transaction;
	}

	@Override
	public TransactionBean createCreditTransactionForPreClosure(LoanMaster loanMasterTemp) {
		TransactionBean transaction = new TransactionBean();
		EntityTransaction txn = JpaUtil.getTransaction();
		txn.begin();
		transaction = transactionDao.createCreditTransactionForPreClosure(loanMasterTemp, transaction);
		txn.commit();
		return transaction;
	}

	@Override
	public TransactionBean createCreditTransactionForDeclinedPreClosure(LoanMaster loanMasterTemp) {
		TransactionBean transaction = new TransactionBean();
		EntityTransaction txn = JpaUtil.getTransaction();
		txn.begin();
		transaction = transactionDao.createCreditTransactionForDeclinedPreClosure(loanMaster, transaction);
		txn.commit();
		return transaction;
	}

	@Override
	public LoanMaster updatePreClosureDenial(LoanMaster loanMasterTemp) {
		EntityTransaction transaction = JpaUtil.getTransaction();
		transaction.begin();
		loanMaster = loanMasterDao.updatePreClosureDenialDao(loanMasterTemp);
		transaction.commit();
		return loanMaster;
	}
}
