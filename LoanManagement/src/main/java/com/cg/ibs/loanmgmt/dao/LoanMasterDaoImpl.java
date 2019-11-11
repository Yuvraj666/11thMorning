package com.cg.ibs.loanmgmt.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.cg.ibs.loanmgmt.bean.CustomerBean;
import com.cg.ibs.loanmgmt.bean.LoanMaster;
import com.cg.ibs.loanmgmt.bean.LoanStatus;
import com.cg.ibs.loanmgmt.util.JpaUtil;

@Repository("LoanMasterDao")
public class LoanMasterDaoImpl implements LoanMasterDao {
	private static Logger LOGGER = Logger.getLogger(LoanMasterDaoImpl.class);
	private EntityManager entityManager;
	LoanMaster loanMaster = new LoanMaster();

	public LoanMasterDaoImpl() {
		entityManager = JpaUtil.getEntityManger();
	}

	@Override
	public LoanMaster applyLoan(LoanMaster loanMaster) {
		LOGGER.info("Applied loan is being saved");
		entityManager.persist(loanMaster);
		return loanMaster;
	}

	public LoanMaster updateLoanApprovalDao(LoanMaster loanMasterTemp, BigInteger loanNumber) {
		LOGGER.info("Loan details are being updated after approval of loan");
		loanMasterTemp.setStatus(LoanStatus.APPROVED);
		loanMasterTemp.setLoanAccountNumber(loanNumber);
		loanMasterTemp.setTotalNumOfEmis(loanMasterTemp.getLoanTenure());
		loanMasterTemp.setNumOfEmisPaid(0);
		loanMasterTemp.setApprovedDate(LocalDate.now());
		loanMasterTemp.setNextEmiDate(loanMasterTemp.getApprovedDate().plusMonths(1));
		loanMasterTemp.getSavingsAccount().setBalance(loanMasterTemp.getLoanAmount());
		;
		return entityManager.merge(loanMasterTemp);

	}

	public void updateLoanDenialDao(LoanMaster loanMasterTemp) {
		LOGGER.info("Loan details are being updated after denial of loan");
		loanMasterTemp.setStatus(LoanStatus.DENIED);
		entityManager.merge(loanMasterTemp);
	}

	public List<LoanMaster> getSentForVerificationLoans() {
		LOGGER.info("Pending loans are being fetched from database.");
		TypedQuery<LoanMaster> query = entityManager
				.createQuery("Select l from LoanMaster l where l.status ='SENT_FOR_VERIFICATION'", LoanMaster.class);
		List<LoanMaster> pendingLoans = query.getResultList();
		return sortLoanMasterListByAppNum(pendingLoans);

	}

	public List<LoanMaster> getPendingPreClosures() {
		LOGGER.info("Pending Pre-Closures are being fetched from database.");
		TypedQuery<LoanMaster> query = entityManager
				.createQuery("Select l from LoanMaster l where l.status ='PRE_CLOSURE_VERIFICATION'", LoanMaster.class);
		List<LoanMaster> pendingPreClosures = query.getResultList();
		return sortLoanMasterListByAppNum(pendingPreClosures);
	}

	private List<LoanMaster> sortLoanMasterListByAppNum(List<LoanMaster> loanMasterList) {
		LOGGER.info("Loan details comparator is being defined.");
		Comparator<LoanMaster> loanApplicantNumComparator = new Comparator<LoanMaster>() {
			public int compare(LoanMaster o1, LoanMaster o2) {
				return o1.getApplicationNumber().compareTo(o2.getApplicationNumber());
			}
		};
		Collections.sort(loanMasterList, loanApplicantNumComparator);
		return loanMasterList;
	}

	public LoanMaster updatePreClosureApprovalDao(LoanMaster loanMasterTemp) {
		LOGGER.info("Loan details are being updated after approval of pre-closure");
		loanMasterTemp.setStatus(LoanStatus.PRE_CLOSED);
		loanMasterTemp.setBalance(new BigDecimal("0.00"));
		loanMasterTemp.setNextEmiDate(null);
		return entityManager.merge(loanMasterTemp);
	}

	@Override
	public List<LoanMaster> getLoanListByUci(CustomerBean customer) {
		LOGGER.info("List of loans is being fetched using customer uci");
		TypedQuery<LoanMaster> query = entityManager.createQuery("select c from LoanMaster c where c.uci=?1",
				LoanMaster.class);
		query.setParameter(1, customer.getUci());
		List<LoanMaster> listTemp = new ArrayList<LoanMaster>();
		listTemp = query.getResultList();
		return listTemp;
	}

	public LoanMaster updateEmiDao(LoanMaster loanMaster) {
		LOGGER.info("EMI is being updated in database.");
		loanMaster.setNumOfEmisPaid(loanMaster.getNumOfEmisPaid() + 1);
		loanMaster.setNextEmiDate(loanMaster.getNextEmiDate().plusMonths(1));
		return entityManager.merge(loanMaster);
	}

	public LoanMaster updateBalanceDao(LoanMaster loanMaster, BigDecimal balance) {
		LOGGER.info("Balance is being updated in database.");
		loanMaster.setBalance(balance);
		return entityManager.merge(loanMaster);
	}

	public LoanMaster getLoanByLoanNumber(BigInteger loanAccNum) {
		LOGGER.info("Loan number is input to obtain complete loan details.");
		TypedQuery<LoanMaster> query = entityManager
				.createQuery("Select l from LoanMaster l where loanAccountNumber=?1", LoanMaster.class);
		query.setParameter(1, loanAccNum);
		LoanMaster loanMaster = query.getSingleResult();
		return loanMaster;
	}

	public void updatePreClosureDao(LoanMaster loanMaster) {
		LOGGER.info("PreClosure is being updated in database.");
		loanMaster.setStatus(LoanStatus.PRE_CLOSURE_VERIFICATION);
		loanMaster.getSavingsAccount()
				.setBalance(loanMaster.getSavingsAccount().getBalance().subtract(loanMaster.getBalance()));
		entityManager.merge(loanMaster);
	}

	@Override
	public List<LoanMaster> getApprovedLoanListByUci(CustomerBean customer) {
		LOGGER.info("List of loans is being fetched using customer uci");
		TypedQuery<LoanMaster> query = entityManager
				.createQuery("select c from LoanMaster c where c.status='APPROVED' AND c.uci=?1", LoanMaster.class);
		query.setParameter(1, customer.getUci());
		List<LoanMaster> listTemp = new ArrayList<LoanMaster>();
		listTemp = query.getResultList();
		return listTemp;
	}

	@Override
	public LoanMaster applyingLoan(LoanMaster loanMaster) {
		LOGGER.info("Loan Uploaded with Documents");
		return entityManager.merge(loanMaster);
	}

	@Override
	public LoanMaster getLoanByApplicantNumber(BigInteger applicantNum) {
		return entityManager.find(LoanMaster.class, applicantNum);
	}

	@Override
	public LoanMaster updatePreClosureDenialDao(LoanMaster loanMasterTemp) {
		LOGGER.info("Loan details are being updated after approval of pre-closure");
		loanMasterTemp.setStatus(LoanStatus.APPROVED);
		loanMasterTemp.getSavingsAccount()
				.setBalance(loanMasterTemp.getSavingsAccount().getBalance().add(loanMasterTemp.getBalance()));
		loanMasterTemp.setNextEmiDate(null);
		return entityManager.merge(loanMasterTemp);
	}
}
