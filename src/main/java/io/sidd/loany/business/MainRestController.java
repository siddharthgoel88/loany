package io.sidd.loany.business;

import io.sidd.loany.model.beans.Borrower;
import io.sidd.loany.model.beans.Loan;
import io.sidd.loany.model.dao.BorrowerDao;
import io.sidd.loany.model.dao.LoanDao;
import io.sidd.loany.rest.beans.LoanDataTable;
import io.sidd.loany.rest.beans.Status;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainRestController {
	
	@Autowired
	public BorrowerDao borrowerDao;
	
	@Autowired
	public LoanDao loanDao;
	
	@ResponseBody
	@RequestMapping(path = "/createBorrower", method = RequestMethod.POST)
	@Transactional
	public Status addBorrower(@RequestBody Borrower borrower) {
		try {
			borrowerDao.create(borrower);
		} catch(Exception e) {
			return failure(e.getMessage());
		}
		return success();
	}
	
	@ResponseBody
	@RequestMapping(path = "/addLoan", method = RequestMethod.POST)
	@Transactional
	public Status addLoan(double amount, String itemName,
			String itemWeight, String loanIssueDate,
			String loanDateHindi, long borrowerId) {
		try {
			Borrower borrower = borrowerDao.get(borrowerId);
			Loan loan = new Loan();
			loan.amount = amount;
			loan.borrower = borrower;
			loan.itemName = itemName;
			loan.itemWeight = itemWeight;
			loan.loanDateHindi = loanDateHindi;
			loan.loanIssueDate = new SimpleDateFormat("dd/MM/yyyy")
									.parse(loanIssueDate);
			
			loanDao.create(loan);
			
		} catch(Exception e) {
			return failure(e.getMessage());
		}
		return success();
	}
	
	@RequestMapping(path = "/returnLoan", method = RequestMethod.POST)
	@Transactional
	public Status returnLoan(long loanId, long borrowerId,
			String loanReturnDate, double interestRate,
			double interestAmount, double totalAmount) {
		try {
			Loan loan = loanDao.get(loanId);
			
			if (loan == null || loan.borrower.borrowerId != borrowerId) {
				throw new Exception("Seems the form data to return loan is tampered.");
			}
			
			loan.loanReturnDate = new SimpleDateFormat("dd/MM/yyyy")
								.parse(loanReturnDate);
			loan.interestRate = interestRate;
			loan.interestAmount = interestAmount;
			loan.totalAmount = totalAmount;
			
			loanDao.update(loan);
			
		} catch(Exception e) {
			return failure(e.getMessage());
		}
		return success();
	}
	
	@RequestMapping(path = "/updateLoan", method = RequestMethod.POST)
	@Transactional
	public Status updateLoan(long loanId, long borrowerId,
			double amount, String itemName, String itemWeight,
			String loanDateHindi, String loanIssueDate,
			String loanReturnDate, double interestRate,
			double interestAmount, double totalAmount) {
		try {
			Loan loan = loanDao.get(loanId);
			
			if (loan == null || loan.borrower.borrowerId != borrowerId) {
				throw new Exception("Seems the form data to update loan is tampered.");
			}
			
			loan.amount = amount;
			loan.itemName = itemName;
			loan.itemWeight = itemWeight;
			loan.loanDateHindi = loanDateHindi;
			loan.loanIssueDate = new SimpleDateFormat("dd/MM/yyyy")
									.parse(loanIssueDate);
			loan.loanReturnDate = new SimpleDateFormat("dd/MM/yyyy")
									.parse(loanReturnDate);
			loan.interestRate = interestRate;
			loan.interestAmount = interestAmount;
			loan.totalAmount = totalAmount;
			
			loanDao.update(loan);
			
		} catch(Exception e) {
			return failure(e.getMessage());
		}
		return success();
	}
	
	@RequestMapping(path = "/getAllLoans")
	@Transactional
	public LoanDataTable getAllLoans() {
			List<Loan> allLoans = loanDao.getAll();
			LoanDataTable loanDataTable = new LoanDataTable();
			loanDataTable.data = allLoans;
			return loanDataTable;
	}
	
	private Status failure(String errorMessage) {
		Status status = new Status();
		status.status = "failure";
		status.errorMessage = errorMessage;
		return status;
	}

	private Status success() {
		Status status = new Status();
		status.status = "success";
		return status;
	}
	
}
