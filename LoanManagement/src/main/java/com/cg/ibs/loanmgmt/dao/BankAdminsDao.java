package com.cg.ibs.loanmgmt.dao;

import com.cg.ibs.loanmgmt.bean.BankAdmins;

public interface BankAdminsDao {
	BankAdmins getAdminByUserId(String userId);
}
