package com.cg.ibs.loanmgmt.dao;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.cg.ibs.loanmgmt.bean.LoanTypeBean;
import com.cg.ibs.loanmgmt.util.JpaUtil;

@Repository("LoanTypeDao")
public class LoanTypeDaoImpl implements LoanTypeDao{
	private static Logger LOGGER = Logger.getLogger(LoanTypeDaoImpl.class);
	private EntityManager entityManager;
	
	public LoanTypeDaoImpl() {
		entityManager = JpaUtil.getEntityManger();
	}
	public LoanTypeBean getLoanTypeByTypeID(Integer typeId) {
		LOGGER.info("Fetching loan details by Loan Type ID ");
		return entityManager.find(LoanTypeBean.class, typeId);
	}

	
}