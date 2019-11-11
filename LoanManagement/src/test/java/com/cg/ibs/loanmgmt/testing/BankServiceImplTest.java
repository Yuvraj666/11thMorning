package com.cg.ibs.loanmgmt.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Test;

import com.cg.ibs.loanmgmt.bean.BankAdmins;
import com.cg.ibs.loanmgmt.bean.CustomerBean;
import com.cg.ibs.loanmgmt.bean.LoanMaster;
import com.cg.ibs.loanmgmt.bean.LoanTypeBean;
import com.cg.ibs.loanmgmt.service.BankServiceImpl;

public class BankServiceImplTest {
	private static BankServiceImpl bankService = new BankServiceImpl();
	private static LoanMaster loanMasterTemp = new LoanMaster();
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("LoanMgmt");
	EntityManager entityManager = emf.createEntityManager();

	@Test
	public void bankLoginTest() {
		boolean actual = bankService.verifyBankLogin("admin1", "pass1");
		assertTrue(actual);
	}

	@Test
	public void wrongBankLoginTest() {
		boolean actual = bankService.verifyBankLogin("admin1", "lol123");
		assertFalse(actual);
	}

	@Test
	public void fetchLoanTypeTest() {
		LoanTypeBean actual = bankService.getLoanTypeByTypeID(1);
		assertEquals("Home Loan", actual.getLoanType());
	}

	@Test
	public void nullLoanTypeTest() {
		LoanTypeBean actual = bankService.getLoanTypeByTypeID(6);
		assertNull(actual);
	}

	@Test
	public void customerDetailsTest() {
		CustomerBean actual = bankService.getCustomerDetailsByUci(new BigInteger("5555111151511001"));
		assertNotNull(actual);
	}

	@Test
	public void loanNumTest() {
		loanMasterTemp.setAppliedDate(LocalDate.now());
		loanMasterTemp.setApplicationNumber(new BigInteger("12345"));
		BigInteger actual = bankService.generateLoanNumber(loanMasterTemp);
		assertEquals(new BigInteger("20191012345"), actual);
	}

	@Test
	public void falseLoanNumTest() {
		loanMasterTemp.setAppliedDate(LocalDate.now());
		loanMasterTemp.setApplicationNumber(new BigInteger("12345"));
		BigInteger actual = bankService.generateLoanNumber(loanMasterTemp);
		assertNotEquals(new BigInteger("20191112345"), actual);
	}

	@Test
	public void fetchBankAdminTest() {
		BankAdmins actual = bankService.getBankAdmin("admin1");
		assertNotNull(actual);
	}

	@Test
	public void pendingLoanTest() {
		List<LoanMaster> actual = bankService.getPendingLoans();
		assertNotNull(actual);
	}

	@Test
	public void getLoansTest() {
		List<LoanMaster> actual = bankService.getPendingLoans();
		assertNotNull(actual);
	}

	@Test
	public void getPendingPreClosureTest() {
		List<LoanMaster> actual = bankService.getPendingPreClosures();
		assertNotNull(actual);
	}

}
