package io.sidd.loany.model.beans;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "borrower")
public class Borrower {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "borrower_id")
	public long borrowerId;
	
	@Column(name = "borrower_name", nullable = false)
	public String borrowerName;
	
	@Column(name = "father_name", nullable = false)
	public String fatherName;
	
	@Column(name = "place", nullable = false)
	public String place;
	
	@Column(name = "caste", nullable = false)
	public String caste;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "borrower")
	public Set<Loan> loans;
	
}
