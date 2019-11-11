package com.cg.ibs.loanmgmt.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.NoResultException;

import org.junit.jupiter.api.Test;

import com.cg.ibs.loanmgmt.bean.CustomerBean;
import com.cg.ibs.loanmgmt.bean.LoanMaster;
import com.cg.ibs.loanmgmt.bean.LoanStatus;
import com.cg.ibs.loanmgmt.bean.LoanTypeBean;
import com.cg.ibs.loanmgmt.service.CustomerServiceImpl;

public class CustomerServiceImplTest {
	private static CustomerServiceImpl customerService = new CustomerServiceImpl();
	LoanMaster loanMaster1 = new LoanMaster();
	CustomerBean customer = new CustomerBean();

	@Test
	public void customerLoginTest() {
		boolean actual = customerService.verifyCustomerLogin("ykalra", "lol123");
		assertTrue(actual);
	}

	@Test
	public void customerFailureTest() {
		boolean actual = customerService.verifyCustomerLogin("ykalra", "lol228");
		assertFalse(actual);
	}

	@Test
	public void loanAmountTest() {
		boolean actual = customerService.verifyLoanAmount(new BigDecimal("100000"), new BigDecimal("10000000"),
				new BigDecimal("12000"));
		assertTrue(actual);
	}

	@Test
	public void falseLoanAmountTest() {
		boolean actual = customerService.verifyLoanAmount(new BigDecimal("1236"), new BigDecimal("100000"),
				new BigDecimal("2000"));
		assertFalse(actual);
	}

	@Test
	public void loanTenureTest() {
		boolean actual = customerService.verifyLoanTenure(12);
		assertTrue(actual);
	}

	@Test
	public void falseLoanTenureTest() {
		boolean actual = customerService.verifyLoanTenure(13);
		assertFalse(actual);
	}

	@Test
	public void calculateEMITest() {
		loanMaster1.setTypeId(1);
		loanMaster1.setLoanAmount(new BigDecimal("120000"));
		loanMaster1.setLoanTenure(12);
		LoanMaster actual = customerService.calculateEmi(loanMaster1);
		assertEquals(new BigDecimal("10466.43"), actual.getEmiAmount());
	}

	@Test
	public void falseCalculateEMITest() {
		loanMaster1.setTypeId(1);
		loanMaster1.setLoanAmount(new BigDecimal("120000"));
		loanMaster1.setLoanTenure(12);
		LoanMaster actual = customerService.calculateEmi(loanMaster1);
		assertNotEquals(new BigDecimal("10000.43"), actual.getEmiAmount());
	}

	@Test
	public void notNullFetchCustomerTest() {
		CustomerBean actual = customerService.getCustomer("ykalra");
		assertNotNull(actual);
	}

	@Test
	public void getLoanByUciTest() {
		customer.setUci(new BigInteger("5555111151511001"));
		List<LoanMaster> actual = customerService.getLoanListByUci(customer);
		assertNotNull(actual);
	}

	@Test
	public void getLoanTest() {
		LoanTypeBean actual = customerService.getLoanTypeByTypeId(1);
		assertNotNull(actual);
	}

	@Test
	public void calculateBalTest() {
		loanMaster1.setTypeId(1);
		loanMaster1.setLoanAmount(new BigDecimal("120000"));
		loanMaster1.setLoanTenure(12);
		loanMaster1.setBalance(loanMaster1.getLoanAmount());
		loanMaster1.setEmiAmount(new BigDecimal("10466.43"));
		BigDecimal actual = customerService.calculateBalance(loanMaster1);
		assertNotNull(actual);
	}

	@Test
	public void verifyLoanTest() {
		loanMaster1.setLoanAccountNumber(new BigInteger("12345"));
		assertThrows(NoResultException.class, () -> {
			boolean actual = customerService.verifyLoanExist(loanMaster1.getLoanAccountNumber());
		});
	}

	@Test
	public void verifyPreClosureTest() {
		loanMaster1.setLoanAccountNumber(new BigInteger("12345"));
		assertThrows(NoResultException.class, () -> {
			boolean actual = customerService.verifyPreClosureApplicable(loanMaster1.getLoanAccountNumber());
		});
	}

	@Test
	public void getLoanDetailsTest() {
		loanMaster1.setLoanAccountNumber(new BigInteger("12345"));
		assertThrows(NoResultException.class, () -> {
			customerService.getLoanDetails(loanMaster1.getLoanAccountNumber());
		});
	}
}