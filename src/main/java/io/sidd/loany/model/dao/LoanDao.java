package io.sidd.loany.model.dao;

import io.sidd.loany.model.beans.Borrower;
import io.sidd.loany.model.beans.Loan;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class LoanDao extends AbstractDao<Loan> {
	
	Logger log = LoggerFactory.getLogger(LoanDao.class.getName());
	
	public Loan getEmptyLoan(Borrower borrower) {
		if (borrower == null) {
			return null;
		}
		
		Query query = entityManager.createQuery("Select loan from Loan loan where "
				+ "loan.borrower = :borrower and loan.amount = 0 and "
				+ "loan.itemName = '-' and loan.itemWeight = '-' ");
		
		query.setParameter("borrower", borrower);
		
		Loan loan = null;
		
		try {
			loan = (Loan) query.getSingleResult();
		} catch (NoResultException e) {
			log.info("No empty loan for borrower id = " + borrower.borrowerId);
		}
		
		return loan;
	}
	
}
