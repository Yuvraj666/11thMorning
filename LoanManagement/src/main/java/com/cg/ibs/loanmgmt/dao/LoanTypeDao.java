package com.cg.ibs.loanmgmt.dao;

import com.cg.ibs.loanmgmt.bean.LoanTypeBean;

public interface LoanTypeDao {
	public LoanTypeBean getLoanTypeByTypeID(Integer typeId);
	
}
