package com.cg.ibs.loanmgmt.dao;

import java.math.BigInteger;
import java.util.List;

import com.cg.ibs.loanmgmt.bean.DocumentBean;

public interface DocumentDao {
	List<DocumentBean> getDocumentByApplicantNum(BigInteger applicantNum);
	DocumentBean uploadDocuments(DocumentBean document);
}
