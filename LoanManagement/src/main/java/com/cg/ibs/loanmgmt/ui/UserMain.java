package com.cg.ibs.loanmgmt.ui;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cg.ibs.loanmgmt.IBSexception.IBSException;

public class UserMain {
	private static ApplicationContext context;

	public static void main(String[] args) throws IBSException {

		context = new ClassPathXmlApplicationContext("LoanManagement.xml");
		User user = ((User) context.getBean("UI"));
			user.userLogin();
		
	}
}
