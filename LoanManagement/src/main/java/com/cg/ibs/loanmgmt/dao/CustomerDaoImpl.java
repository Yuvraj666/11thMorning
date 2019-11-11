package com.cg.ibs.loanmgmt.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.cg.ibs.loanmgmt.bean.CustomerBean;
import com.cg.ibs.loanmgmt.bean.LoanMaster;
import com.cg.ibs.loanmgmt.util.JpaUtil;
@Repository("CustomerDao")
public class CustomerDaoImpl implements CustomerDao {
	private static Logger LOGGER = Logger.getLogger(CustomerDaoImpl.class);
	private EntityManager entityManager;

	public CustomerDaoImpl() {
		entityManager = JpaUtil.getEntityManger();
	}

	public CustomerBean getCustomerDetailsByUci(BigInteger uci) {
		return entityManager.find(CustomerBean.class, uci);
	}

	@Override
	public CustomerBean getCustomerByUserId(String userId) {
		LOGGER.info("Fetching customer details");
		CustomerBean customer = new CustomerBean();
		try {
			TypedQuery<CustomerBean> query = entityManager.createQuery("select c from CustomerBean c where c.userId=?1",
					CustomerBean.class);
			query.setParameter(1, userId);
			customer = (CustomerBean) query.getSingleResult();
		} catch (NoResultException exp) {
			customer = null;

		}
		return customer;
	}

	public List<LoanMaster> getLoanListByUci(CustomerBean customer) {
		TypedQuery<LoanMaster> query = entityManager.createQuery("select c from LoanMaster c where c.uci=?1",
				LoanMaster.class);
		query.setParameter(1, customer.getUci());
		List<LoanMaster> listTemp = new ArrayList<LoanMaster>();
		listTemp = query.getResultList();
		return listTemp;
	}

}
