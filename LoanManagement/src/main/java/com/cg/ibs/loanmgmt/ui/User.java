package com.cg.ibs.loanmgmt.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cg.ibs.loanmgmt.IBSexception.ExceptionMessages;
import com.cg.ibs.loanmgmt.IBSexception.IBSException;
import com.cg.ibs.loanmgmt.bean.AccountHolding;
import com.cg.ibs.loanmgmt.bean.BankAdmins;
import com.cg.ibs.loanmgmt.bean.CustomerBean;
import com.cg.ibs.loanmgmt.bean.LoanMaster;
import com.cg.ibs.loanmgmt.bean.LoanStatus;
import com.cg.ibs.loanmgmt.bean.TopUp;
import com.cg.ibs.loanmgmt.bean.TransactionBean;
import com.cg.ibs.loanmgmt.service.BankService;
import com.cg.ibs.loanmgmt.service.CustomerService;
import com.cg.ibs.loanmgmt.util.ComparatorUtil;
import com.itextpdf.text.DocumentException;

@Component("UI")
public class User implements ExceptionMessages {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private BankService bankService;
	private static Logger LOGGER = Logger.getLogger(User.class);
	private static Scanner read = new Scanner(System.in);

	public void userLogin() throws IBSException {
		UserOptions choice = null;
		CustomerBean client = null;

		while (choice != UserOptions.EXIT) {
			System.out.println("\n\t\t******Welcome To IBS Loan Portal******");
			System.out.println("------------------------");
			for (UserOptions menu : UserOptions.values()) {
				System.out.println((menu.ordinal() + 1) + "\t" + menu);
			}
			String userLoginInput = read.next();
			Pattern pattern = Pattern.compile("[0-9]{1}");
			Matcher matcher = pattern.matcher(userLoginInput);
			if (matcher.matches()) {
				int ordinal = Integer.parseInt(userLoginInput);
				if (ordinal >= 1 && ordinal < (UserOptions.values().length) + 1) {
					choice = UserOptions.values()[ordinal - 1];
					switch (choice) {
					case VISITOR:
						selectLoanType(client);
						break;
					case EXISTING_CUSTOMER:
						init(customerLogin());
						break;
					case BANK_ADMIN:
						adminInit(bankAdminLogin());
						break;
					case EXIT:
						System.out.println("\nThank You For Visiting. \nHave a nice day!");
						break;
					}
				} else {
					choice = null;
					try {
						if (choice == null) {
							throw new IBSException(ExceptionMessages.MESSAGEFORWRONGINPUT);
						}
					} catch (IBSException exp) {
						LOGGER.error(exp.getMessage(), exp);
						System.out.println(exp.getMessage());

					}

				}
			} else {
				try {
					throw new IBSException(ExceptionMessages.MESSAGEFORINPUTMISMATCH);
				} catch (IBSException exp) {
					LOGGER.error(exp.getMessage(), exp);
					System.out.println(exp.getMessage());
				}
			}
		}
	}

	private LoanMaster selectLoanType(CustomerBean customer) throws IBSException {
		LOGGER.info("Loan type selection!!!!");
		LoanTypes choice = null;
		LoanMaster loanMaster = null;
		while (choice != LoanTypes.GO_BACK) {
			System.out.println("Menu");
			System.out.println("--------------------");
			System.out.println("Choice");
			System.out.println("--------------------");
			for (LoanTypes menu : LoanTypes.values()) {
				System.out.println((menu.ordinal() + 1) + "\t" + menu);
			}
			System.out.println("Choice");
			String customerLoginInput = read.next();
			Pattern pattern = Pattern.compile("[0-9]{1}");
			Matcher matcher = pattern.matcher(customerLoginInput);
			if (matcher.matches()) {
				int ordinal = Integer.parseInt(customerLoginInput);
				if (ordinal >= 1 && ordinal < (LoanTypes.values().length) + 1) {
					choice = LoanTypes.values()[ordinal - 1];
					switch (choice) {
					case HOME_LOAN:
						loanMaster = calculateEMI(1);
						applyLoan(customer, loanMaster);
						break;
					case EDUCATION_LOAN:
						loanMaster = calculateEMI(2);
						applyLoan(customer, loanMaster);
						break;
					case PERSONAL_LOAN:
						loanMaster = calculateEMI(3);
						applyLoan(customer, loanMaster);
						break;
					case VEHICLE_LOAN:
						loanMaster = calculateEMI(4);
						applyLoan(customer, loanMaster);
						break;
					case GO_BACK:
						System.out.println("Thank You!");
						break;
					}
				} else {
					choice = null;
					try {
						if (choice == null) {
							throw new IBSException(ExceptionMessages.MESSAGEFORWRONGINPUT);
						}
					} catch (IBSException exp) {
						System.out.println(exp.getMessage());

					}
				}

			} else {
				try {
					throw new IBSException(ExceptionMessages.MESSAGEFORINPUTMISMATCH);
				} catch (IBSException exp) {
					System.out.println(exp.getMessage());
				}
			}

		}
		return loanMaster;
	}

	public CustomerBean init(CustomerBean customer) throws IBSException {
		LOGGER.info("Logged in as an existing customer");
		CustomerOptions customerChoice = null;
		 
			
		while (customerChoice != CustomerOptions.LOG_OUT) {
			System.out.println("-----------------------------------------------------------------------------------");
			List<LoanMaster> approvedLoansEmi = new ArrayList<>();
			List<LoanMaster> pendingEmi = new ArrayList<>();
			approvedLoansEmi = customerService.getApprovedLoanListByUci(customer);
			if (!approvedLoansEmi.isEmpty()) {
				for (LoanMaster loanMaster : approvedLoansEmi) {
					if(loanMaster.getNextEmiDate().minusDays(3).isBefore(LocalDate.now())){
						pendingEmi.add(loanMaster);
					}
				}
				if(!pendingEmi.isEmpty()) {
					System.out.println("\t\t\t\t****** NOTIFICATION ******");
					System.out.println(
							"\n-------------------------------------------------------------------------------------------------------------------------------------------");
					System.out.printf(" %25s %25s %25s %25s ", "LOAN NUMBER", "NUMBER OF EMI's LEFT", "EMI AMOUNT",
							"LAST DATE FOR NEXT EMI");
					System.out.println();
					System.out.println(
							"--------------------------------------------------------------------------------------------------------------------------------------------");
					for (LoanMaster loanMaster : pendingEmi) {
						System.out.format(" %25s %25s %25f %25tD ", loanMaster.getLoanAccountNumber(),
								(loanMaster.getTotalNumOfEmis() - loanMaster.getNumOfEmisPaid()),
								loanMaster.getEmiAmount().setScale(2), loanMaster.getNextEmiDate());
						System.out.println();
					} 
					System.out.println(
							"------------------------------------------------------------------------------------------------------------------------------------------");
					System.out.println("\n\t\t\t\t\t**********\n");
				}
			}
			System.out.println("--------------------");
			System.out.println("Please select one of the following to proceed further : ");
			System.out.println("--------------------");
			for (CustomerOptions menu : CustomerOptions.values()) {
				System.out.println((menu.ordinal() + 1) + ".\t" + menu);
			}
			System.out.println("Choice");
			String customerLoginInput = read.next();
			Pattern pattern = Pattern.compile("[0-9]{1}");
			Matcher matcher = pattern.matcher(customerLoginInput);
			if (matcher.matches()) {
				int ordinal = Integer.parseInt(customerLoginInput);
				if (ordinal >= 1 && ordinal < (CustomerOptions.values().length) + 1) {
					customerChoice = CustomerOptions.values()[ordinal - 1];
					switch (customerChoice) {
					case APPLY_LOAN:
						selectLoanType(customer);
						break;
					case PAY_EMI:
						payEmi(customer);
						break;
					case APPLY_PRECLOSURE:
						applyPreClosure(customer);
						break;
					case VIEW_HISTORY:
						viewHistory(customer);
						break;
					case LOAN_TOPUP:
						loanTopUp(customer);
						break;
					case LOG_OUT:
						System.out.println("Thank You! Come Again.");
						userLogin();
					}

				} else {
					customerChoice = null;
					try {
						if (customerChoice == null)

							throw new IBSException(ExceptionMessages.MESSAGEFORWRONGINPUT);
					} catch (IBSException exp) {
						LOGGER.error(exp.getMessage(), exp);
						System.out.println(exp.getMessage());
					}
				}
			} else {
				try {
					throw new IBSException(ExceptionMessages.MESSAGEFORINPUTMISMATCH);
				} catch (IBSException exp) {
					LOGGER.error(exp.getMessage(), exp);
					System.out.println(exp.getMessage());
				}
			}
		}
		return customer;
	}
	

	private void applyPreClosure(CustomerBean customer) {
		List<LoanMaster> listTemp = new ArrayList<>();
		listTemp = customerService.getApprovedLoanListByUci(customer);
		if (listTemp.isEmpty()) {
			System.out.println("No Active Loans");
		} else {
			List<LoanMaster> preClosureList = new ArrayList<>();
			for (LoanMaster loanMaster : listTemp) {
				if (loanMaster.getStatus().equals(LoanStatus.APPROVED)) {
					if (customerService.verifyPreClosureApplicable(loanMaster.getLoanAccountNumber())) {
						preClosureList.add(loanMaster);
					}
				}
			}
			if (preClosureList.isEmpty()) {
				System.out.println("No Loan affiliated to your UserID is applicable for PreClosure!");
			} else {
				System.out.printf("%25s %20s %20s %20s %20s %20s %25s %25s %20s","LOAN ACCOUNT NO.", "APPLIED DATE", "LOAN AMOUNT", "LOAN TYPE", "LOAN TENURE",
						 "NUMBER OF EMI's PAID", "TOTAL NUMBER OF EMI's", "LOAN STATUS", "BALANCE");
				System.out.println();
				System.out.println(
						"-------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
				for (LoanMaster loanMaster : preClosureList) {
					System.out.format("%25d %20tD %20f %20s %25s %25d %25d %20s %20f", loanMaster.getLoanAccountNumber(), loanMaster.getAppliedDate(),
							loanMaster.getLoanAmount().setScale(2),
							customerService.getLoanTypeByTypeId(loanMaster.getTypeId()).getLoanType(),loanMaster.getLoanTenure(),
							loanMaster.getNumOfEmisPaid(),
							loanMaster.getTotalNumOfEmis(), loanMaster.getStatus(), loanMaster.getBalance());
					System.out.println();
				}
				System.out.println(
						"-------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
				System.out.println("\n\t\t\t**********\n");
				System.out.println("\nEnter the Loan Account Number:");
				BigInteger loanAccNum1 = read.nextBigInteger();
				LoanMaster loanMaster = customerService.getLoanDetails(loanAccNum1);
				System.out.println("\nPreClosure details: ");
				System.out.println("Number of EMIs left to be paid:\t"
						+ (loanMaster.getTotalNumOfEmis() - loanMaster.getNumOfEmisPaid()));
				System.out.println("Remaining balance:\t" + loanMaster.getBalance());
				System.out.println("\n\t\t\t********\n");
				System.out.println("\nDo you want to close the loan by paying the pending balance ?\n1. Yes\n2. No");
				String preClosurePayChoiceTemp = read.next();
				Pattern pattern = Pattern.compile("[0-9]{1}");
				Matcher matcher = pattern.matcher(preClosurePayChoiceTemp);
				if (matcher.matches()) {
					Integer preClosurePaymentChoice = Integer.valueOf(preClosurePayChoiceTemp);
					switch (preClosurePaymentChoice) {
					case 1:
						customerService.updatePreClosure(loanMaster);
						System.out.println("Loan against Loan Number " + loanMaster.getLoanAccountNumber()
								+ " sent for PreClosure verification.");
						break;
					case 2:
						System.out.println("Have a nice day !");
						break;
					}
				} else {
					try {
						throw new IBSException(ExceptionMessages.MESSAGEFORWRONGINPUT);
					} catch (IBSException exp) {
						LOGGER.error(exp.getMessage(), exp);
						System.out.println(exp.getMessage());
					}
				}

			}
		}
	}

	private void viewHistory(CustomerBean customer) {
//		SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
		Integer viewHistoryInput;
		String na = "Not Applicable";
		System.out.println("\t\t\t**********\n");
		List<LoanMaster> listTemp = new ArrayList<>();
		listTemp = customerService.getLoanListByUci(customer);
		System.out.printf("%20s %20s %20s %20s %20s %20s %20s %20s %25s %25s %20s %20s %25s", "LOAN ACCOUNT NO.", "APPLIED DATE", "APPROVED DATE", "CLOSED DATE", "LOAN AMOUNT","LOAN TENURE", "EMI AMOUNT", "LOAN TYPE",
				 "NUMBER OF EMI's PAID","NEXT EMI DATE", "LOAN STATUS", "BALANCE", "SAVINGS ACCOUNT NO.");
		System.out.println();
		System.out.println(
				"---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		for (LoanMaster loanMaster : listTemp) {
			System.out.format("%20s %20tD %20tD %20s %20f %20d %20f %20s %25s %25tD %20s %20f %25d",(null == loanMaster.getLoanAccountNumber()) ? na : loanMaster.getLoanAccountNumber().toString(),loanMaster.getAppliedDate(),
					loanMaster.getApprovedDate(),(null == loanMaster.getLoanClosededDate()) ? na : loanMaster.getLoanClosededDate().toString(),
					loanMaster.getLoanAmount().setScale(2), loanMaster.getLoanTenure(), loanMaster.getEmiAmount(),
					customerService.getLoanTypeByTypeId(loanMaster.getTypeId()).getLoanType(),
					
					(null == loanMaster.getNumOfEmisPaid()) ? na : loanMaster.getNumOfEmisPaid().toString(),
					loanMaster.getNextEmiDate(),loanMaster.getStatus(), loanMaster.getBalance(), loanMaster.getSavingsAccount().getAccNo() );
			System.out.println();
		}
		System.out.println(
				"-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.println("\n\t\t\t**********\n");
		do {
			if (listTemp.size() < 2) {
				System.out.println("No loans to sort!\n");
				break;
			}
			System.out.println(
					"\nSort the loan details on basis of:\n1. Applied Date\n2. Loan Amount\n3. Type of Loan\n4. Loan Tenure\n5. Loan Number\n6. Go Back");
			viewHistoryInput = 0;
			String[] fields = { "AppliedDate", "LoanAmount", "TypeId", "LoanTenure", "LoanAccountNumber" };
			String viewHistoryInputTemp = read.next();
			Pattern pattern = Pattern.compile("[0-9]{1}");
			Matcher matcher = pattern.matcher(viewHistoryInputTemp);
			if (matcher.matches()) {
				viewHistoryInput = Integer.valueOf(viewHistoryInputTemp);
				if (viewHistoryInput >= 1 && viewHistoryInput < 5) {
					Collections.sort(listTemp, ComparatorUtil.<LoanMaster>getComparatorOnField(LoanMaster.class,
							fields[viewHistoryInput - 1]));
					System.out.println("\nSorting on the basis of: " + fields[viewHistoryInput - 1]);
					System.out.printf("%20s %20s %20s %20s %25s %20s %20s", "APPLIED DATE", "LOAN AMOUNT", "LOAN TYPE",
							"LOAN NUMBER", "NUMBER OF EMI's PAID", "LOAN STATUS", "BALANCE");
					System.out.println();
					System.out.println(
							"------------------------------------------------------------------------------------------------------------------------------------------------------");
					for (LoanMaster loanMaster : listTemp) {
						System.out.format("%20tD %20f %20s %20s %25s %20s %20f", loanMaster.getAppliedDate(),
								loanMaster.getLoanAmount(),
								customerService.getLoanTypeByTypeId(loanMaster.getTypeId()).getLoanType(),
								(null == loanMaster.getLoanAccountNumber()) ? na
										: loanMaster.getLoanAccountNumber().toString(),
								(null == loanMaster.getNumOfEmisPaid()) ? na : loanMaster.getNumOfEmisPaid().toString(),
								loanMaster.getStatus(), loanMaster.getBalance());
						System.out.println();
					}
					System.out.println(
							"------------------------------------------------------------------------------------------------------------------------------------------------------");
				} else if (viewHistoryInput == 5) {
					List<LoanMaster> nullList = new ArrayList<>();
					for (LoanMaster loanMaster : listTemp) {
						if (loanMaster.getLoanAccountNumber() == null) {
							nullList.add(loanMaster);
						}
					}
					listTemp.removeAll(nullList);
					Collections.sort(listTemp, ComparatorUtil.<LoanMaster>getComparatorOnField(LoanMaster.class,
							fields[viewHistoryInput - 1]));
					System.out.println("\nSorting on the basis of: " + fields[viewHistoryInput - 1]);
					System.out.printf("%20s %20s %20s %20s %25s %20s %20s", "APPLIED DATE", "LOAN AMOUNT", "LOAN TYPE",
							"LOAN NUMBER", "NUMBER OF EMI's PAID", "LOAN STATUS", "BALANCE");
					System.out.println();
					System.out.println(
							"------------------------------------------------------------------------------------------------------------------------------------------------------");

					for (LoanMaster loanMaster : listTemp) {
						System.out.format("%20tB %20f %20s %20s %25s %20s %20f", loanMaster.getAppliedDate(),
								loanMaster.getLoanAmount(),
								customerService.getLoanTypeByTypeId(loanMaster.getTypeId()).getLoanType(),
								(null == loanMaster.getLoanAccountNumber()) ? na
										: loanMaster.getLoanAccountNumber().toString(),
								(null == loanMaster.getNumOfEmisPaid()) ? na : loanMaster.getNumOfEmisPaid().toString(),
								loanMaster.getStatus(), loanMaster.getBalance());
						System.out.println();
					}
					for (LoanMaster loanMaster : nullList) {
						System.out.format("%20tB %20f %20s %20s %25s %20s %20f", loanMaster.getAppliedDate(),
								loanMaster.getLoanAmount(),
								customerService.getLoanTypeByTypeId(loanMaster.getTypeId()).getLoanType(),
								(null == loanMaster.getLoanAccountNumber()) ? na
										: loanMaster.getLoanAccountNumber().toString(),
								(null == loanMaster.getNumOfEmisPaid()) ? na : loanMaster.getNumOfEmisPaid().toString(),
								loanMaster.getStatus(), loanMaster.getBalance());
						System.out.println();
					}
					listTemp.addAll(nullList);
					System.out.println(
							"------------------------------------------------------------------------------------------------------------------------------------------------------");
				} else if (viewHistoryInput != 6) {
					System.out.println("Choose an appropriate option.");
				} else {
					System.out.println("Thank You!");
				}
			} else {
				try {
					throw new IBSException(ExceptionMessages.MESSAGEFORWRONGINPUT);
				} catch (IBSException exp) {
					LOGGER.error(exp.getMessage(), exp);
					System.out.println(exp.getMessage());
				}
			}
		} while (viewHistoryInput != 6);
	}

	public BankAdmins adminInit(BankAdmins bankAdmin) throws IBSException {
		AdminOptions adminChoice = null;
		while (adminChoice != AdminOptions.LOG_OUT) {
			System.out.println("Menu");
			System.out.println("--------------------");
			System.out.println("Choice");
			System.out.println("--------------------");
			for (AdminOptions menu : AdminOptions.values()) {
				System.out.println((menu.ordinal() + 1) + "\t" + menu);
			}
			System.out.println("Choice");
			int ordinal = read.nextInt();
			if (ordinal >= 1 && ordinal < (AdminOptions.values().length) + 1) {
				adminChoice = AdminOptions.values()[ordinal - 1];
				switch (adminChoice) {
				case VERIFY_LOAN:
					verifyLoan(bankAdmin);
					break;
				case VERIFY_PRECLOSURE:
					verifyPreClosure();
					break;
				case LOG_OUT:
					userLogin();
				}

			} else {
				adminChoice = null;
				try {
					if (adminChoice == null) {
						throw new IBSException(ExceptionMessages.MESSAGEFORWRONGINPUT);
					}
				} catch (IBSException exp) {
					LOGGER.error(exp.getMessage(), exp);
					System.out.println(exp.getMessage());

				}

			}
		}
		return bankAdmin;
	}

	private void payEmi(CustomerBean customer) {
		List<LoanMaster> approvedLoan = new ArrayList<>();
		approvedLoan = customerService.getApprovedLoanListByUci(customer);
		if (approvedLoan.isEmpty()) {
			System.out.println("No Active Loans");
		} else {
			System.out.printf(" %25s %25s %25s %25s ", "LOAN NUMBER", "NUMBER OF EMI's LEFT", "EMI AMOUNT",
					"LAST DATE FOR NEXT EMI");
			System.out.println();
			System.out.println(
					"--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			for (LoanMaster loanMaster : approvedLoan) {
				System.out.format(" %25s %25s %25f %25tD ", loanMaster.getLoanAccountNumber(),
						(loanMaster.getTotalNumOfEmis() - loanMaster.getNumOfEmisPaid()),
						loanMaster.getEmiAmount().setScale(2), loanMaster.getNextEmiDate());
				System.out.println();
			}
			System.out.println(
					"--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println("\n\t\t\t**********\n");
			LoanMaster loanMasterTemp = null;
			while (loanMasterTemp == null) {
				System.out.println("Enter the loan number of the loan you want to pay EMI for: ");
				String loanNumInputTemp = read.next();
				Pattern pattern = Pattern.compile("[1-9]{1}[0-9]{1,12}");
				Matcher matcher = pattern.matcher(loanNumInputTemp);
				if (matcher.matches()) {
					BigInteger loanNumInput = new BigInteger(loanNumInputTemp);
					System.out.println("\n\t\t\t********\n");
					for (LoanMaster loanMaster : approvedLoan) {
						if (loanMaster.getLoanAccountNumber().equals(loanNumInput)) {
							loanMasterTemp = loanMaster;
						}
					}
					if (loanMasterTemp == null) {
						System.out.println("Invalid Loan Number");
					}
				} else {
					try {
						throw new IBSException(ExceptionMessages.MESSAGEFORINVALIDLOANNUMBER);
					} catch (IBSException exp) {
						LOGGER.error(exp.getMessage(), exp);
						System.out.println(exp.getMessage());

					}
				}
			}
			System.out.println("Loan Number:\t" + loanMasterTemp.getLoanAccountNumber());
			System.out.println(
					"Loan Type:\t" + customerService.getLoanTypeByTypeId(loanMasterTemp.getTypeId()).getLoanType());
			System.out.println("Total Number of EMI's to be paid:\t" + loanMasterTemp.getTotalNumOfEmis());
			System.out.println("Number of EMI's already paid:\t" + loanMasterTemp.getNumOfEmisPaid());
			System.out.println("Balance amount in your savings account " + loanMasterTemp.getSavingsAccount().getAccNo()
					+ ":\tINR " + loanMasterTemp.getSavingsAccount().getBalance() + " /-");
			System.out.println("Outstanding loan balance:\tINR " + loanMasterTemp.getBalance() + " /-");
			System.out.println("EMI amount to be paid:\tINR " + loanMasterTemp.getEmiAmount() + " /-");
			System.out.println("\n\t\t\t********\n");
			System.out.println("Do you want to pay EMI:\n1. Yes\n2. No");
			switch (read.nextInt()) {
			case 1:
				customerService.updateEMI(loanMasterTemp);
				System.out.println("Transaction Successful!");
				createDebitTransaction(loanMasterTemp);
				System.out.println("Balance has been updated.\nNew balance:\tINR " + loanMasterTemp.getBalance());
				System.out.println("Next EMI Date:\t" + loanMasterTemp.getNextEmiDate());
				System.out.println("Number of EMIs left:\t"
						+ (loanMasterTemp.getTotalNumOfEmis() - loanMasterTemp.getNumOfEmisPaid()));
				createCreditTransaction(loanMasterTemp);
			case 2:
				System.out.println("Thankyou!");
				break;
			}

		}

	}

	private void loanTopUp(CustomerBean customer) {
		TopUp topUp = new TopUp();
		List<LoanMaster> listTemp = new ArrayList<>();
		listTemp = customerService.getApprovedLoanListByUci(customer);
		if (listTemp.isEmpty()) {
			System.out.println("No Active Loans");
		} else {
			System.out.printf("%20s %20s %20s %20s %25s %25s %20s %20s %20s", "APPLIED DATE", "LOAN AMOUNT",
					"LOAN TYPE", "LOAN NUMBER", "NUMBER OF EMI's PAID", "TOTAL NUMBER OF EMI's", "LOAN STATUS",
					"BALANCE", "EMI AMOUNT");
			System.out.println();
			System.out.println(
					"--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			for (LoanMaster loanMaster : listTemp) {
				System.out.format("%20tD %20f %20s %20s %25s %25s %20s %20f %20f", loanMaster.getAppliedDate(),
						loanMaster.getLoanAmount().setScale(2),
						customerService.getLoanTypeByTypeId(loanMaster.getTypeId()).getLoanType(),
						loanMaster.getLoanAccountNumber(), loanMaster.getNumOfEmisPaid(),
						loanMaster.getTotalNumOfEmis(), loanMaster.getStatus(), loanMaster.getBalance(),
						loanMaster.getEmiAmount().setScale(2));
				System.out.println();
			}
			System.out.println(
					"--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println("\n\t\t\t**********\n");

			LoanMaster loanMasterTemp = null;
			boolean checkLoanNumber = false;
			while (loanMasterTemp == null) {
				System.out.println("Enter the loan number of the loan you want to top up: ");
				BigInteger loanNumInputTemp = read.nextBigInteger();
				Pattern pattern = Pattern.compile("[1-9]{1}[0-9]{1,12}");
				Matcher matcher = pattern.matcher(loanNumInputTemp.toString());
				for (LoanMaster loanMaster : listTemp) {
					if (loanMaster.getLoanAccountNumber().equals(loanNumInputTemp)) {
						checkLoanNumber = true;
						break;
					}
				}
				if (matcher.matches() && checkLoanNumber) {
					System.out.println("\n\t\t\t********\n");
					for (LoanMaster loanMaster : listTemp) {
						if (loanMaster.getLoanAccountNumber().equals(loanNumInputTemp)) {
							loanMasterTemp = loanMaster;
						}
					}
				} else {
					try {
						throw new IBSException(ExceptionMessages.MESSAGEFORINVALIDLOANNUMBER);
					} catch (IBSException exp) {
						LOGGER.error(exp.getMessage(), exp);
						System.out.println(exp.getMessage());

					}
				}
			}
			System.out.println("Loan Number:\t" + loanMasterTemp.getLoanAccountNumber());
			System.out.println(
					"Loan Type:\t" + customerService.getLoanTypeByTypeId(loanMasterTemp.getTypeId()).getLoanType());
			System.out.println("Total Number of EMI's to be paid:\t" + loanMasterTemp.getTotalNumOfEmis());
			System.out.println("Number of EMI's already paid:\t" + loanMasterTemp.getNumOfEmisPaid());
			System.out.println("Balance amount in your savings account " + loanMasterTemp.getSavingsAccount().getAccNo()
					+ ":\tINR " + loanMasterTemp.getSavingsAccount().getBalance() + " /-");
			System.out.println("Outstanding loan balance:\tINR " + loanMasterTemp.getBalance() + " /-");
			System.out.println("EMI amount to be paid:\tINR " + loanMasterTemp.getEmiAmount() + " /-");
			System.out.println("\n\t\t\t********\n");
			boolean amountVerify = false;
			boolean tenureVerify = false;
			BigDecimal topUpAmount = null;
			Integer topUpTenure = null;
			while (!amountVerify) {
				System.out.println("Enter the amount you want to top up with: ");
				System.out.println("**Top Up Amount should be less than :\tINR " + loanMasterTemp.getLoanAmount());
				topUp.setTopUpAmount(read.nextBigDecimal());
				amountVerify = customerService.verifyTopupAmount(loanMasterTemp, topUp.getTopUpAmount());
				if (!amountVerify) {
					System.out.println("Not a Valid Amount!");
				}
			}
			while (!tenureVerify) {
				System.out.println("Enter the tenure: ");
				System.out.println("**Top Up Tenure should be less than :\tINR " + loanMasterTemp.getLoanTenure());
				topUp.setTopUpTenure(read.nextInt());
				tenureVerify = customerService.verifyTopUpTenure(loanMasterTemp,topUp.getTopUpTenure());
				if (!tenureVerify) {
					System.out.println("Not a Valid Top Up Tenure!");
				}
			}
			BigDecimal updatedTopUpBalance = loanMasterTemp.getBalance().add(topUpAmount);
			LoanMaster calculateEmiAfterTopUp=new LoanMaster();
			calculateEmiAfterTopUp.setBalance(updatedTopUpBalance);
			System.out.println(
					"Total Number of EMI's to be paid after Top Up:\t" + (loanMasterTemp.getTotalNumOfEmis() + topUp.getTopUpTenure()));
			System.out.println("Number of EMI's already paid:\t" + loanMasterTemp.getNumOfEmisPaid());
			System.out.println("Balance Amount to be paid after Top Up:\t" + updatedTopUpBalance);
			System.out.println("EMI to be paid:\t" + customerService.calculateEmi(calculateEmiAfterTopUp));
			System.out.println("\n\t\t\t********\n");
			System.out.println("Do you want to apply top-up:\n1. Yes\n2. No");
			switch (read.nextInt()) {
			case 1:
				
				topUp.setTopUpAmount(topUpAmount);
				topUp.setTopUpTenure(topUpTenure);
				LOGGER.info("Your loan application has been sent for verification");
				try {
					System.out.println("Loan with Application Number "
							+ customerService.applyTopUp(customer, loanMasterTemp, topUp).getApplicationNumber()
							+ " has been sent for verification!");
				} catch (IOException exp) {
					try {
						throw new IBSException(MESSAGEFORIOEXCEPTION);
					} catch (IBSException exp1) {
						LOGGER.error(exp1.getMessage(), exp1);
						System.out.println(exp1.getMessage());
					}
				}
			case 2:
				System.out.println("Thankyou!");
				break;
			}
		}
	}

	private void createBankTransaction(LoanMaster loanMaster) {
		TransactionBean transaction = new TransactionBean();
		transaction = bankService.createLoanDebitTransaction(loanMaster);
		bankService.createLoanCreditTransaction(loanMaster);
		System.out.println("\n\t\t\t********\n");
		System.out.println("Transaction Id:\t" + transaction.getTransactionId());
		System.out.println("Transaction Account Number:\t" + transaction.getAccountNumber());
		System.out.println("Transaction Amount:\t" + transaction.getTransactionAmount());
		System.out.println("Transaction Date :\t" + transaction.getTransactionDate());
		System.out.println("\n\t\t\t********\n");
		try {
			if (customerService.debitReceiptGenerator(loanMaster, transaction)) {
				System.out.println("Receipt has been generated.\nSave the receipt for future references!");
				System.out.println("\n\t\t\t********\n");
			}
		} catch (FileNotFoundException | DocumentException exp) {
			LOGGER.error(exp.getMessage(), exp);
			try {
				throw new IBSException(ExceptionMessages.MESSAGEFORFILENOTFOUND);
			} catch (IBSException exp1) {
				LOGGER.error(exp1.getMessage(), exp1);
				System.out.println(exp1.getMessage());
			}
		}
	}

	private void createDebitTransaction(LoanMaster loanMaster) {
		TransactionBean transaction = new TransactionBean();
		transaction = customerService.createDebitTransaction(loanMaster);
		System.out.println("\n\t\t\t********\n");
		System.out.println("Transaction Id:\t" + transaction.getTransactionId());
		System.out.println("Transaction Account Number:\t" + transaction.getAccountNumber());
		System.out.println("Transaction Amount:\t" + transaction.getTransactionAmount());
		System.out.println("Transaction Date :\t" + transaction.getTransactionDate());
		System.out.println("\n\t\t\t********\n");
		try {
			if (customerService.debitReceiptGenerator(loanMaster, transaction)) {
				System.out.println("Receipt has been generated.\nSave the receipt for future references!");
				System.out.println("\n\t\t\t********\n");
			}
		} catch (FileNotFoundException | DocumentException exp) {
			LOGGER.error(exp.getMessage(), exp);
			try {
				throw new IBSException(ExceptionMessages.MESSAGEFORFILENOTFOUND);
			} catch (IBSException exp1) {
				LOGGER.error(exp1.getMessage(), exp1);
				System.out.println(exp1.getMessage());
			}
		}
	}

	private void createCreditTransaction(LoanMaster loanMaster) {
		TransactionBean transaction = new TransactionBean();
		transaction = customerService.createCreditTransaction(loanMaster);
		System.out.println("\n\t\t\t********\n");
		System.out.println("Transaction Id:\t" + transaction.getTransactionId());
		System.out.println("Transaction Account Number:\t" + transaction.getAccountNumber());
		System.out.println("Transaction Amount:\t" + transaction.getTransactionAmount());
		System.out.println("Transaction Date :\t" + transaction.getTransactionDate());
		System.out.println("\n\t\t\t********\n");
		try {
			if (customerService.creditReceiptGenerator(loanMaster, transaction)) {
				System.out.println("Receipt has been generated.\nSave the receipt for future references!");
				System.out.println("\n\t\t\t********\n");
			}
		} catch (FileNotFoundException | DocumentException exp) {
			LOGGER.error(exp.getMessage(), exp);
			try {
				throw new IBSException(ExceptionMessages.MESSAGEFORFILENOTFOUND);
			} catch (IBSException exp1) {
				LOGGER.error(exp1.getMessage(), exp1);
				System.out.println(exp1.getMessage());
			}
		}
	}

	private void verifyLoan(BankAdmins bankAdmin) {
		List<LoanMaster> pendingLoans = new ArrayList<LoanMaster>();
		pendingLoans = bankService.getSentForVerificationLoans();
		if (pendingLoans.isEmpty()) {
			System.out.println("No Pending Loans!");
		} else {
			System.out.println("Pending Loans for Verification:\n");
			System.out.println("\n\t\t\t********\n");
//			System.out.println("Application Number\tCustomer Name\tLoan Type");
//			System.out.println("------------------------------------------------------------------------------");
//			for (LoanMaster loanMaster : pendingLoans) {
//				System.out.println(loanMaster.getApplicationNumber() + "\t\t\t"
//						+ bankService.getCustomerDetailsByUci(loanMaster.getUci()).getFirstName() + " "
//						+ bankService.getCustomerDetailsByUci(loanMaster.getUci()).getLastName() + "\t"
//						+ bankService.getLoanTypeByTypeID(loanMaster.getTypeId()).getLoanType());
			System.out.printf("%25s %20s %5s  %20s %20s %20s %20s %20s","APPLICATION NUMBER", "CUSTOMER", "NAME", "APPLIED DATE", "LOAN AMOUNT", "LOAN TYPE", "LOAN TENURE", "BALANCE");
			System.out.println();
			System.out.println(
					"------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			for (LoanMaster loanMaster : pendingLoans) {
				System.out.format("%25d %20s %5s %20tD %20f %20s %20d  %20f",loanMaster.getApplicationNumber(), bankService.getCustomerDetailsByUci(loanMaster.getUci()).getFirstName(), 
						bankService.getCustomerDetailsByUci(loanMaster.getUci()).getLastName(), loanMaster.getAppliedDate(),
						loanMaster.getLoanAmount().setScale(2),
						customerService.getLoanTypeByTypeId(loanMaster.getTypeId()).getLoanType(),loanMaster.getLoanTenure(),
					 loanMaster.getBalance());
				System.out.println();
			}
			System.out.println(
					"------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println("\n\t\t\t**********\n");
			}
			boolean appNumCheck = true;
			while (appNumCheck) {
				System.out.println("\nEnter the applicant number of the loan you want to verify: ");
				String appNumInputTemp = read.next();
				Pattern pattern = Pattern.compile("[0-9]{1,9}");
				Matcher matcher = pattern.matcher(appNumInputTemp);
				BigInteger appNumInput = new BigInteger(appNumInputTemp);
				boolean check = false;
				for (LoanMaster loanMaster : pendingLoans) {
					if (loanMaster.getApplicationNumber().equals(appNumInput)) {
						check = true;
						break;
					}
				}
				if (matcher.matches() && check) {
					appNumCheck = false;
					LoanMaster loanMasterTemp = new LoanMaster();
					for (LoanMaster loanMaster : pendingLoans) {
						if (loanMaster.getApplicationNumber().equals(appNumInput)) {
							loanMasterTemp = loanMaster;
							break;
						}
					}
					System.out.println("\t******");
					System.out.println("\nApplication No.:\t" + loanMasterTemp.getApplicationNumber()
							+ "\nName of customer:\t"
							+ bankService.getCustomerDetailsByUci(loanMasterTemp.getUci()).getFirstName() + " "
							+ bankService.getCustomerDetailsByUci(loanMasterTemp.getUci()).getLastName()
							+ "\nLoan Amount:\t\t" + loanMasterTemp.getLoanAmount() + "\nLoanTenure:\t\t"
							+ loanMasterTemp.getLoanTenure() + "\nEMI Amount:\t\tINR " + loanMasterTemp.getEmiAmount()
							+ "\nSavings Account Number:\t\t" + loanMasterTemp.getSavingsAccount().getAccNo());
					bankService.downloadDocument(loanMasterTemp);
					System.out.println("Document for Application Number " + loanMasterTemp.getApplicationNumber()
							+ " has been downloaded in the 'Downloads Folder'.");
					System.out.println("\n\t******");
					boolean loanApprovalChoiceCheck = true;
					while (loanApprovalChoiceCheck) {
						System.out.println("Do you want to approve the loan? \n1. Yes\n2. No\n3. Exit");
						String loanApprovalInputString = read.next();
						Pattern pattern1 = Pattern.compile("[1-3]{1}");
						Matcher matcher1 = pattern1.matcher(loanApprovalInputString);
						if (matcher1.matches()) {
							Integer loanApprovalInput = Integer.valueOf(loanApprovalInputString);
							loanApprovalChoiceCheck = false;
							switch (loanApprovalInput) {
							case 1:
								LoanMaster loanMasterTempo = bankService.updateLoanApproval(loanMasterTemp);
								System.out.println("Loan Approved!\n");
								createBankTransaction(loanMasterTempo);
								System.out.println(
										"Loan number for the loan: " + loanMasterTempo.getLoanAccountNumber() + "\n");
								break;
							case 2:
								bankService.updateLoanDenial(loanMasterTemp);
								System.out.println("Loan Declined!");
								break;
							case 3:
								try {
									adminInit(bankAdmin);
								} catch (IBSException exp1) {
									LOGGER.error(exp1.getMessage(), exp1);
									System.out.println(exp1.getMessage());
								}
							}
						} else {
							System.out.println("Please enter an appropriate choice.");
						}
					}
				} else {
					try {
						throw new IBSException(ExceptionMessages.MESSAGEFORINVALIDAPPLICATIONNUMBER);
					} catch (IBSException exp1) {
						LOGGER.error(exp1.getMessage(), exp1);
						System.out.println(exp1.getMessage());
					}
				}
			}
		}
	

	private void verifyPreClosure() {
		LOGGER.info("PreClosure is being verified");
		List<LoanMaster> pendingPreClosures = new ArrayList<LoanMaster>();
		pendingPreClosures = bankService.getPendingPreClosures();
		if (pendingPreClosures.isEmpty()) {
			System.out.println("No Pending PreClosures!");
		} else {
			System.out.println("Pending Pre-Closure Verification: \n ");
			LOGGER.debug("Listing pending pre-closures.");
			System.out.printf("%25s %25s %20s %20s %20s %20s %25s %25s %20s","APPLICATION NUMBER", "LOAN ACCOUNT NO.", "APPLIED DATE", "LOAN AMOUNT", "LOAN TYPE", "LOAN TENURE",
					 "NUMBER OF EMI's PAID", "TOTAL NUMBER OF EMI's", "BALANCE");
			System.out.println();
			System.out.println(
					"------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			for (LoanMaster loanMaster : pendingPreClosures) {
				System.out.format("%25d %25d %20tD %20f %20s %20d %25d %25d %20f",loanMaster.getApplicationNumber(), loanMaster.getLoanAccountNumber(), loanMaster.getAppliedDate(),
						loanMaster.getLoanAmount().setScale(2),
						customerService.getLoanTypeByTypeId(loanMaster.getTypeId()).getLoanType(),loanMaster.getLoanTenure(),
						loanMaster.getNumOfEmisPaid(),
						loanMaster.getTotalNumOfEmis(), loanMaster.getBalance());
				System.out.println();
			}
			System.out.println(
					"------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println("\n\t\t\t**********\n");
			}
			System.out.println("Enter the applicant number of the loan for which you want to verify the Pre-Closure: ");
			BigInteger appNumInput = read.nextBigInteger();
			LoanMaster loanMasterTemp = new LoanMaster();
			for (LoanMaster loanMaster : pendingPreClosures) {
				if (loanMaster.getApplicationNumber().equals(appNumInput)) {
					loanMasterTemp = loanMaster;
					break;
				}
			}
			System.out.println("\t********");
			System.out.println("\n\nApplication No.: " + loanMasterTemp.getApplicationNumber() + "\nName of customer: "
					+ bankService.getCustomerDetailsByUci(loanMasterTemp.getUci()).getFirstName() + " "
					+ bankService.getCustomerDetailsByUci(loanMasterTemp.getUci()).getLastName() + "\nLoan Amount: "
					+ loanMasterTemp.getLoanAmount() + "\nLoanTenure: " + loanMasterTemp.getLoanTenure()
					+ "\nEMI Amount: INR " + loanMasterTemp.getEmiAmount());
			System.out.println("\t********");
			System.out.println("\nDo you want to approve the Pre-Closure? \n1. Yes\n2. No");
			Integer preClosureApprovalInput = read.nextInt();
			switch (preClosureApprovalInput) {
			case 1:
				System.out.println(
						"Loan with loan number " + loanMasterTemp.getLoanAccountNumber() + " has been closed.");
				LoanMaster loanMasterTempo = bankService.updatePreClosureApproval(loanMasterTemp);
				System.out.println("Updated loan details:\nBalance: " + loanMasterTempo.getBalance()
						+ "\nNumber of EMIs paid: " + loanMasterTempo.getNumOfEmisPaid());
				break;
			case 2:
				System.out.println("Pre-Closure for loan with loan number " + loanMasterTemp.getLoanAccountNumber()
						+ " has been declined.");
			}
		}
	

	// customer Login
	private CustomerBean customerLogin() {
		LOGGER.info("Login portal");
		CustomerBean customerLoggedIn = new CustomerBean();
		boolean shallContinue = true;
		String userId = "";
		String password = "";
		System.out.println("\t\t******Login Portal******\n");
		LOGGER.debug("Check valid customer credentials");
		while (shallContinue) {
			System.out.println("Login with your Registered Customer UserID!\n");
			System.out.println("UserID: ");

			userId = read.next();

			System.out.println("Password: ");

			password = read.next();

			shallContinue = !customerService.verifyCustomerLogin(userId, password);
			if (shallContinue) {
				System.out.println("*Incorrect Credentials*");
			}
		}
		if (!shallContinue) {
			customerLoggedIn = customerService.getCustomer(userId);
			System.out.println("-----------------------------------------------------------------------------------");
			if (LocalTime.now().isAfter(LocalTime.NOON)) {
				System.out.println("Good Afternoon!");
			} else {
				System.out.println("Good Morning!");
			}
			System.out.println("Welcome " + customerLoggedIn.getFirstName() + " " + customerLoggedIn.getLastName());
			System.out.println("\t\t\t\tRegistered UCI :\t"+customerLoggedIn.getUci());
			System.out.println("\t\t\t\tDate Of Birth :\t\t"+ customerLoggedIn.getDateOfBirth());
			System.out.println("\t\t\t\tRegistered UserID:\t"+customerLoggedIn.getUserId());
		}
		return customerLoggedIn;
	}

	private BankAdmins bankAdminLogin() {
		LOGGER.info("Login portal");
		BankAdmins bankAdminLoggedIn = new BankAdmins();
		boolean shallContinue = true;
		String userId = "";
		System.out.println("\n\t\t******Admin Login Portal******\n");
		LOGGER.debug("Check valid admin credentials");
		while (shallContinue) {
			System.out.println("Login with your Registered Admin UserID");
			System.out.println("UserID: ");
			userId = read.next();
			System.out.println("Password: ");
			String password = read.next();
			shallContinue = !bankService.verifyBankLogin(userId, password);
			if (shallContinue) {
				System.out.println("**##Not a Registered Bank Admin##**");
			}
		}
		if (!shallContinue) {
			bankAdminLoggedIn = bankService.getBankAdmin(userId);
		}

		return bankAdminLoggedIn;
	}

	private void applyLoan(CustomerBean customer, LoanMaster loanMaster) {
		LOGGER.info("A new loan is being applied by the user");
		if (null == customer) {
			customer = callingCustomerLogin();
		}
		if (null == customer) {
			System.out.println("Thank you for visiting");
		} else {
			try {
				selectSavingsAccount(customer, loanMaster);
				if (loanMaster.getSavingsAccount() == null) {
					System.out.println("\t\t********");
					System.out.println("No Savings Account Exists for customer ID linked to " + customer.getFirstName()
							+ " " + customer.getLastName() + "\nSavings Account is pre-requisite for Loan Account!\n");
					System.out.println("\t\t********");
				} else {
					loanMaster.setApplicationNumber(
							customerService.applyLoan(customer, loanMaster).getApplicationNumber());
					documentUpload(loanMaster);
					LOGGER.info("Your loan application has been sent for verification");
					System.out.println("Loan with Application Number "
							+ customerService.applyingLoan(loanMaster).getApplicationNumber()
							+ " has been sent for verification!");
				}
			} catch (IOException exp) {
				try {
					throw new IBSException(MESSAGEFORIOEXCEPTION);
				} catch (IBSException exp1) {
					LOGGER.error(exp1.getMessage(), exp1);
					System.out.println(exp1.getMessage());
				}
			}
		}
	}

	private void documentUpload(LoanMaster loanMaster) {
		boolean docUploadCheck = true;
		Integer docId;
		System.out.println("\n\t\t******Document Upload******\n");
		while (docUploadCheck) {
			docId = 0;
			System.out.println("Please upload your Aadhar Card in pdf format");
			System.out.println("\nEnter the file path: ");
			String path = read.next();
			try {
				customerService.uploadDocument(loanMaster, path, docId);
				docUploadCheck = false;
				if (!docUploadCheck) {
					System.out.println("Aadhar Card uploaded Successfully");
				}
			} catch (IOException exp) {
				try {
					throw new IBSException(MESSAGEFORIOEXCEPTION);
				} catch (IBSException exp1) {
					System.out.println(exp1.getMessage());
				}
			}
		}
		if (loanMaster.getTypeId().equals(1)) {
			docUploadCheck = true;
			while (docUploadCheck) {
				docId = 1;
				System.out.println("Please upload your Property Document for Collateral in pdf format");
				System.out.println("\nEnter the file path: ");
				String path = read.next();
				try {
					customerService.uploadDocument(loanMaster, path, docId);
					docUploadCheck = false;
					if (!docUploadCheck) {
						System.out.println("Document for Property Collateral uploaded Successfully");
					}
				} catch (IOException exp) {
					try {
						throw new IBSException(MESSAGEFORIOEXCEPTION);
					} catch (IBSException exp1) {
						System.out.println(exp1.getMessage());
					}
				}
			}
		}
		if (loanMaster.getTypeId().equals(2)) {
			docUploadCheck = true;
			while (docUploadCheck) {
				docId = 2;
				System.out.println("Please upload your Admission Letter Document for Collateral in pdf format");
				System.out.println("\nEnter the file path: ");
				String path = read.next();
				try {
					customerService.uploadDocument(loanMaster, path, docId);
					docUploadCheck = false;
					if (!docUploadCheck) {
						System.out.println("Document for Property Collateral uploaded Successfully");
					}
				} catch (IOException exp) {
					try {
						throw new IBSException(MESSAGEFORIOEXCEPTION);
					} catch (IBSException exp1) {
						System.out.println(exp1.getMessage());
					}
				}
			}
		}
		if (loanMaster.getTypeId().equals(3)) {
			docUploadCheck = true;
			while (docUploadCheck) {
				docId = 3;
				System.out.println("Please upload your PAN Card Document in pdf format");
				System.out.println("\nEnter the file path: ");
				String path = read.next();
				try {
					customerService.uploadDocument(loanMaster, path, docId);
					docUploadCheck = false;
					if (!docUploadCheck) {
						System.out.println("Document for PAN Card uploaded Successfully");
					}
				} catch (IOException exp) {
					try {
						throw new IBSException(MESSAGEFORIOEXCEPTION);
					} catch (IBSException exp1) {
						System.out.println(exp1.getMessage());
					}
				}
			}
		}
		if (loanMaster.getTypeId().equals(4)) {
			docUploadCheck = true;
			while (docUploadCheck) {
				docId = 4;
				System.out.println("Please upload your Vehicle Collateral in pdf format");
				System.out.println("\nEnter the file path: ");
				String path = read.next();
				try {
					customerService.uploadDocument(loanMaster, path, docId);
					docUploadCheck = false;
					if (!docUploadCheck) {
						System.out.println("Document for Vehicle Collateral uploaded Successfully");
					}
				} catch (IOException exp) {
					try {
						throw new IBSException(MESSAGEFORIOEXCEPTION);
					} catch (IBSException exp1) {
						System.out.println(exp1.getMessage());
					}
				}
			}
		}
	}

	// customer Login for Customer
	private CustomerBean callingCustomerLogin() {
		LOGGER.info("Login portal for visitor");
		CustomerBean customer = null;
		System.out.println("Do you want to apply for this loan");
		System.out.println("1. Yes\n2. No");
		String inputTemp = read.next();
		Pattern pattern = Pattern.compile("[1-9]{1}");
		Matcher matcher = pattern.matcher(inputTemp);
		if (matcher.matches()) {
			Integer input = Integer.valueOf(inputTemp);
			switch (input) {
			case 1:
				customer = customerLogin();
				break;
			case 2:
				customer = null;
				break;
			}
		} else {
			try {
				throw new IBSException(ExceptionMessages.MESSAGEFORINPUTMISMATCH);
			} catch (IBSException exp) {
				LOGGER.error(exp.getMessage(), exp);
				System.out.println(exp.getMessage());
			}
		}
		return customer;

	}

	// EMI Calculation for Visitor/Customer
	private LoanMaster calculateEMI(Integer typeId) {
		LOGGER.info("EMI calculation");
		LoanMaster loanMaster = new LoanMaster();
		loanMaster.setTypeId(typeId);
		System.out.println("-----------------------------------------\n");
		System.out
				.println("Loan Type:\t\t" + customerService.getLoanTypeByTypeId(loanMaster.getTypeId()).getLoanType());
		System.out.println(
				"Interest Rate:\t\t" + customerService.getLoanTypeByTypeId(loanMaster.getTypeId()).getInterestRate());
		System.out.println("Maximum Loan Amount:\t"
				+ customerService.getLoanTypeByTypeId(loanMaster.getTypeId()).getMaximumLimit());
		System.out.println("Minimum Loan Amount:\t"
				+ customerService.getLoanTypeByTypeId(loanMaster.getTypeId()).getMinimumLimit());
		boolean shallContinue = true;
		LOGGER.debug("Check loan amount");
		while (shallContinue) {
			System.out.println("\nEnter Loan Amount: ");
			String loanAmountInput = read.next();
			Pattern pattern = Pattern.compile("[0-9]{1,50}");
			Matcher matcher = pattern.matcher(loanAmountInput);
			if (matcher.matches()) {
				BigDecimal loanAmount = new BigDecimal(loanAmountInput);
				loanMaster.setLoanAmount(loanAmount);
				shallContinue = !customerService.verifyLoanAmount(loanMaster.getLoanAmount(),
						customerService.getLoanTypeByTypeId(loanMaster.getTypeId()).getMaximumLimit(),
						customerService.getLoanTypeByTypeId(loanMaster.getTypeId()).getMinimumLimit());
				if (shallContinue) {
					System.out.println("**##Please Adhere to the Loan Limits Specified!##**");
				}
			} else {
				System.out.println("**Please enter an appropriate numeric value!**");
				shallContinue = true;
			}
		}
		shallContinue = true;
		LOGGER.debug("Checking loan tenure");
		while (shallContinue) {
			System.out.println("Enter Loan Tenure (Months): ");
			String loanTenureInput = read.next();
			Pattern pattern = Pattern.compile("[0-9]{1,50}");
			Matcher matcher = pattern.matcher(loanTenureInput);
			if (matcher.matches()) {
				loanMaster.setLoanTenure(Integer.valueOf(loanTenureInput));
				shallContinue = !customerService.verifyLoanTenure(loanMaster.getLoanTenure());
				if (shallContinue) {
					System.out.println("**##Please Adhere to the Loan Tenure limits Specified!##**");
				}
			} else {
				System.out.println("**Please enter an appropriate numeric value!**");
				shallContinue = true;
			}
		}
		customerService.calculateEmi(loanMaster);
		System.out.println("\nMonthly EMI: INR " + loanMaster.getEmiAmount().toPlainString());
		return loanMaster;
	}

	private LoanMaster selectSavingsAccount(CustomerBean customer, LoanMaster loanMaster) {
		List<AccountHolding> savingAccountList = new ArrayList<>();
		savingAccountList = customerService.getSavingAccountListByUci(customer);
		if (savingAccountList.isEmpty()) {
			loanMaster.setSavingsAccount(null);
		} else {
			System.out.println("Select the Savings Account you would like to link to your Loan Account:");
			System.out.println("\t\t\t**********\n");
			System.out.printf("%20s %20s", "ACCOUNT NUMBER", "ACCOUNT TYPE");
			System.out.println();
			System.out.println(
					"------------------------------------------------------------------------------------------------------------------------------------------------------");
			for (AccountHolding accountHolding : savingAccountList) {
				System.out.format("%20d %20s ", accountHolding.getAccount().getAccNo(), accountHolding.getType());
				System.out.println();
			}
			System.out.println(
					"------------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println("\n\t\t\t**********\n");
			System.out.println();
			boolean shallContinue = true;
			LOGGER.debug("Checking loan tenure");
			BigInteger accountNumber = null;
			BigInteger accountNumberVerify = null;
			while (shallContinue) {
				boolean verify = true;
				System.out.println("\n\t\t\t**********\n");
				System.out.println("Enter the Saving Account Number to be linked with you Loan Account: ");
				accountNumber = read.nextBigInteger();
				System.out.println("Enter the Saving Account Number again for verification: ");
				accountNumberVerify = read.nextBigInteger();
				for (AccountHolding accountHolding : savingAccountList) {
					if (accountNumber.equals(accountNumberVerify)) {
						verify = false;
						if (accountHolding.getAccount().getAccNo().equals(accountNumber)) {
							shallContinue = false;
							break;
						}
					}
				}
				if (verify) {
					System.out.println("The two account numbers entered  do not match");
				}
				if (shallContinue && !verify) {
					System.out.println(accountNumber + " is not a valid Savings Account Number!\n");
				}
			}
			loanMaster.setSavingsAccount(customerService.getAccount(accountNumber));
		}
		return loanMaster;
	}

}
