package io.sidd.loany.business;

import io.sidd.loany.model.beans.Borrower;
import io.sidd.loany.model.beans.Loan;
import io.sidd.loany.model.dao.BorrowerDao;
import io.sidd.loany.model.dao.LoanDao;
import io.sidd.loany.rest.beans.LoanDataTable;
import io.sidd.loany.rest.beans.Status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	Logger log = LoggerFactory.getLogger(MainRestController.class.getName());
	
	@ResponseBody
	@RequestMapping(path = "/createBorrower", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<Status> addBorrower(@RequestBody Borrower borrower) {
		try {
			if (borrower.borrowerName.equals("") || borrower.caste.equals("")
					|| borrower.fatherName.equals("") || borrower.place.equals("")) {
				throw new Exception("Invalid input");
			}
			log.info("/createBorrower request came");
			borrowerDao.create(borrower);
			log.info("The new borrower's id is " + borrower.borrowerId);
			createEmptyLoan(borrower.borrowerId);
		} catch(Exception e) {
			return new ResponseEntity<Status>(failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Status>(success(), HttpStatus.OK);
	}
	
	private void createEmptyLoan(long borrowerId) throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		long loanId = -1;
		ResponseEntity<Status> response = saveLoan(0, "-", "-", dateFormat.format(date), "-", 
				dateFormat.format(date), 0.0, 0.0 , 0.0,  borrowerId, loanId);
		if(response.getStatusCode().is5xxServerError()) {
			throw new Exception(response.getBody().errorMessage);
		}
	}

	@ResponseBody
	@RequestMapping(path = "/saveLoan", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<Status> saveLoan(double amount, String itemName,
			String itemWeight, String loanIssueDate,
			String loanDateHindi, String loanReturnDate,
			double interestRate, double interestAmount,
			double totalAmount, long borrowerId, long loanId) {
		try {
			
			Borrower borrower = borrowerDao.get(borrowerId);
			
			Loan loan = null;
			if (loanId == -1) {
				loan = new Loan();
			} else {
				loan = loanDao.get(loanId);
			}
			
			loan.amount = amount;
			
			if (loan.borrower == null) {
				loan.borrower = borrower;
			}
			
			loan.itemName = itemName;
			loan.itemWeight = itemWeight;
			loan.loanDateHindi = loanDateHindi;
			loan.loanIssueDate = new SimpleDateFormat("MM/dd/yyyy")
				.parse(loanIssueDate);
			loan.loanReturnDate = new SimpleDateFormat("MM/dd/yyyy")
				.parse(loanReturnDate);
			
			loan.interestRate = interestRate;
			loan.interestAmount = interestAmount;
			loan.totalAmount = totalAmount;
			
			deleteEmptyLoan(loan.borrower);
			
			if (loanId == -1) {
				loanDao.create(loan);
			} else {
				loanDao.update(loan);
			}
			
		} catch(Exception e) {
			return new ResponseEntity<Status>(failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
			return new ResponseEntity<Status>(success(), HttpStatus.OK);
	}
	
	private void deleteEmptyLoan(Borrower borrower) {
		Loan loan = loanDao.getEmptyLoan(borrower);
		if (loan != null) {
			loanDao.delete(loan);
		}
	}

	@RequestMapping(value = "/getBorrower/{id}",  method=RequestMethod.GET)
	@Transactional
	public ResponseEntity<Borrower> getBorrower(@PathVariable long id) {
		try {
			Borrower borrower = borrowerDao.get(id);
			if (borrower == null) {
				throw new Exception("Invalid id");
			}
			return new ResponseEntity<Borrower>(borrower, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Borrower>(new Borrower(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/getLoan/{id}",  method=RequestMethod.GET)
	@Transactional
	public ResponseEntity<Loan> getLoan(@PathVariable long id) {
		try {
			Loan loan = loanDao.get(id);
			if (loan == null) {
				throw new Exception("Invalid id");
			}
			return new ResponseEntity<Loan>(loan, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Loan>(new Loan(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@RequestMapping(path = "/getAllLoans")
	@Transactional
	public ResponseEntity<LoanDataTable> getAllLoans() {
		try {
			List<Loan> allLoans = loanDao.getAll();
			LoanDataTable loanDataTable = new LoanDataTable();
			loanDataTable.data = allLoans;
			return new ResponseEntity<LoanDataTable>(loanDataTable, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<LoanDataTable>(new LoanDataTable(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
