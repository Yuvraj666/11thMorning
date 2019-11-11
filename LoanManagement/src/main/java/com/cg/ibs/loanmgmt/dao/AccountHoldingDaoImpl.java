package com.cg.ibs.loanmgmt.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.cg.ibs.loanmgmt.bean.AccountHolding;
import com.cg.ibs.loanmgmt.bean.CustomerBean;
import com.cg.ibs.loanmgmt.util.JpaUtil;

@Repository("Accountholding")
public class AccountHoldingDaoImpl implements AccountHoldingDao {
	private static Logger LOGGER = Logger.getLogger(AccountHoldingDaoImpl.class);
	private EntityManager entityManager;

	public AccountHoldingDaoImpl() {
		entityManager = JpaUtil.getEntityManger();
	}

	@Override
	public List<AccountHolding> getSavingAccountListByUci(CustomerBean customer) {
		TypedQuery<AccountHolding> query = entityManager.createQuery("select c from AccountHolding c where c.customer=?1",
				AccountHolding.class);
		query.setParameter(1, customer);
		List<AccountHolding> listTemp = query.getResultList();
		return listTemp;
	}

}
