package com.cg.ibs.loanmgmt.dao;

import java.util.List;

import com.cg.ibs.loanmgmt.bean.AccountHolding;
import com.cg.ibs.loanmgmt.bean.CustomerBean;

public interface AccountHoldingDao {
	public List<AccountHolding> getSavingAccountListByUci(CustomerBean customer);
}
