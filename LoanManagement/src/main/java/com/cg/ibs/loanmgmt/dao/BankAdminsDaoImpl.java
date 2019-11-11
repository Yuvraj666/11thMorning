package com.cg.ibs.loanmgmt.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.cg.ibs.loanmgmt.bean.BankAdmins;
import com.cg.ibs.loanmgmt.util.JpaUtil;
@Repository("BankAdminsDao")
public class BankAdminsDaoImpl implements BankAdminsDao {
	private static Logger LOGGER = Logger.getLogger(BankAdminsDaoImpl.class);
	private EntityManager entityManager;

	public BankAdminsDaoImpl() {
		entityManager = JpaUtil.getEntityManger();
	}

	@Override
	public BankAdmins getAdminByUserId(String adminId) {
		LOGGER.info("Fetching customer details");
		BankAdmins bankAdmins = new BankAdmins();
		try {
			TypedQuery<BankAdmins> query = entityManager.createQuery("select b from BankAdmins b where b.adminId=?1",
					BankAdmins.class);
			query.setParameter(1, adminId);
			bankAdmins = (BankAdmins) query.getSingleResult();
		} catch (NoResultException exp) {
			bankAdmins = null;
		}
		return bankAdmins;
	}

}
