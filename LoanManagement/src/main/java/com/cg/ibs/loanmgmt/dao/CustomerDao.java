package com.cg.ibs.loanmgmt.dao;

import java.math.BigInteger;
import java.util.List;

import com.cg.ibs.loanmgmt.bean.CustomerBean;
import com.cg.ibs.loanmgmt.bean.LoanMaster;

public interface CustomerDao {
	CustomerBean getCustomerByUserId(String userId);
	
	public CustomerBean getCustomerDetailsByUci(BigInteger uci);

	List<LoanMaster> getLoanListByUci(CustomerBean customer);

}
