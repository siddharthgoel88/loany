package io.sidd.loany.model.beans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "loan")
public class Loan {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "loan_id", nullable = false)
	public long loanId;
	
	@Column(name = "amount", nullable = false)
	public double amount;
	
	@Column(name = "item_name", nullable = false)
	public String itemName;
	
	@Column(name = "item_weight", nullable = false)
	public String itemWeight;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "loan_issue_date", nullable = false)
	public Date loanIssueDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "loan_return_date")
	public Date loanReturnDate;
	
	@Column(name = "loan_issue_date_hindi")
	public String loanDateHindi;
	
	@Column(name = "interest_rate")
	public double interestRate;
	
	@Column(name = "interest_amount")
	public double interestAmount;
	
	@Column(name = "total_amount")
	public double totalAmount;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "borrower_id", referencedColumnName = "borrower_id")
	public Borrower borrower;
	
}
