package com.cg.ibs.loanmgmt.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.cg.ibs.loanmgmt.bean.DocumentBean;
import com.cg.ibs.loanmgmt.util.JpaUtil;

@Repository("DocumentDao")
public class DocumentDaoImpl implements DocumentDao {
	private EntityManager entityManager;
	private static Logger LOGGER = Logger.getLogger(DocumentDao.class);
	DocumentBean document = new DocumentBean();

	public DocumentDaoImpl() {
		entityManager = JpaUtil.getEntityManger();
	}

	@Override
	public DocumentBean uploadDocuments(DocumentBean document) {
		LOGGER.info("Documents are being saved");
		entityManager.persist(document);
		return document;
	}

	@Override
	public List<DocumentBean> getDocumentByApplicantNum(BigInteger applicantNum) {
		LOGGER.info("Applicant number is input to obtain documents.");
		List<DocumentBean> documents = new ArrayList<DocumentBean>();
		TypedQuery<DocumentBean> query = entityManager.createQuery("Select l from DocumentBean l where applicationNumber=?1",
				DocumentBean.class);
		query.setParameter(1, applicantNum);
		documents = query.getResultList();
		return documents;
	}
}
