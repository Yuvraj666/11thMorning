package com.cg.ibs.loanmgmt.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaUtil {
	private static EntityManagerFactory entityManagerFactory;
	private static EntityManager entityManager;

	static {
		entityManagerFactory = Persistence.createEntityManagerFactory("LoanMgmt");
	}

	public static EntityManager getEntityManger() {

		if (null == entityManager || (!entityManager.isOpen())) {
			entityManager = entityManagerFactory.createEntityManager();
		}
		return entityManager;
	}

	public static EntityTransaction getTransaction() {
		return getEntityManger().getTransaction();
	}
}
