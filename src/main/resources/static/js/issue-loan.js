$(document).ready(function(){
	var borrowerId = getParameterByName("borrowerId");
	var loanId = getParameterByName("loanId");
	var url = "";
	if (loanId == null) {
		url = "/getBorrower/" + borrowerId;
	} else {
		url = "/getLoan/" + loanId; 
	}
	
	principle = 0;
	rate = 0;
	startDate = '01/01/1970';
	endDate = '01/01/1970';
	
	$.ajax({
	     url : url,
	     type : "GET",
	    contentType: "application/json",
	    success: function(data){
	    	
	    	if (loanId == null) {
		    	$('#borrowerName').text('Name: ' + data['borrowerName']);
		    	$('#fatherName').text('Father\'s Name: ' + data['fatherName']);
		    	$('#place').text('Place: ' + data['place']);
		    	$('#caste').text('Caste: ' + data['caste']);
		    	loanId = "-1";
	    	} else {
		    	$('#borrowerName').text('Name: ' + data['borrower']['borrowerName']);
		    	$('#fatherName').text('Father\'s Name: ' + data['borrower']['fatherName']);
		    	$('#place').text('Place: ' + data['borrower']['place']);
		    	$('#caste').text('Caste: ' + data['borrower']['caste']);
		    	$('#amount').val(data['amount']);
		    	$('#itemName').val(data['itemName']);
		    	$('#itemWeight').val(data['itemWeight']);
		    	$('#loanIssueDate').val(getDate(data['loanIssueDate']));
		    	$('#loanDateHindi').val(data['loanDateHindi']);
		    	$('#loanReturnDate').val(getDate(data['loanReturnDate']));
		    	$('#interestRate').val(data['interestRate']);
		    	$('#interestAmount').val(data['interestAmount']);
		    	$('#totalAmount').val(data['totalAmount']);
		    	
		    	principle = data['amount'];
		    	startDate = getDate(data['loanIssueDate']);
		    	endDate = getDate(data['loanReturnDate']);
		    	
		    	if (principle >= 10000) {
		    		$('#interestRate').val('2.0');
		    		rate = 2.0;
		    	} else {
		    		$('#interestRate').val('2.5');
		    		rate = 2.5;
		    	}
	    	}

	    },
	    error: function(data) {
	    	$('#issue-loan-div').hide();
	    	$('#failure-message').show();
	    }
	    });
	
	$("#issueLoanBtn").click(function(e){
	    $.ajax({
	     url : "/saveLoan",
	     type : "POST",
	     data : 
	    {
	        "amount" : $('#amount').val(),
	        "itemName" : $('#itemName').val(),
	        "itemWeight" : $('#itemWeight').val(),
	        "loanIssueDate" : $('#loanIssueDate').val(),
	        "loanDateHindi" : $('#loanDateHindi').val(),
	        "loanReturnDate" : sanitizeDate($('#loanReturnDate').val()),
	        "interestRate" : sanitizeAmount($('#interestRate').val()),
	        "interestAmount" : sanitizeAmount($('#interestAmount').val()),
	        "totalAmount" : sanitizeAmount($('#totalAmount').val()),
	        "borrowerId" : borrowerId,
	        "loanId" : loanId
	    },
	    success: function(data){
	    	$('#success-message').hide();
	    	$('#failure-message').hide();
	    	$('#success-message').show();
	    },
	    error: function(data) {
	    	$('#success-message').hide();
	    	$('#failure-message').hide();
	    	$('#failure-message').show();
	    }
	    });
	    e.preventDefault();
	});
	
	$("#calculateInterestBtn").click(function(e){

		principle = $('#amount').val();
    	startDate = getDate($('#loanIssueDate').val());
    	endDate = getDate($('#loanReturnDate').val());
    	rate = $('#interestRate').val();
		
		var amount = getAmount(principle, rate, startDate, endDate);
		var interest = parseFloat(amount) - parseFloat(principle);
		$('#interestAmount').val(interest);
		$('#totalAmount').val(amount);
	});
	
});

function sanitizeDate(someDate) {
	if (!someDate) {
		return '01/01/1970';
	} else {
		return someDate;
	}
}

function sanitizeAmount(amount) {
	if (!amount) {
		return '0';
	} else {
		return amount;
	}
}

function getDate(millisec) {
	
	var date = new Date(millisec);
	
	var day = date.getDate();
	var month = date.getMonth() + 1;
	var year = date.getFullYear();
	
	return month + "/" + day + "/" + year;
}

function isNumeric(n) {
	  return !isNaN(parseFloat(n)) && isFinite(n);
}

function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function getAmount(principle, rate, startDate, endDate) {
	
	var months = getMonths(startDate, endDate);
	var days = getDays(startDate, endDate);
	
	alert('Calculating interest for ' + months + ' months and ' + days + ' days');
	
		while(months >= 12)
		{
			principle = parseFloat((principle * rate * 12)/100) + parseFloat(principle);
			months = months - 12;
		}
		
		interest = (( parseFloat(principle * months ) + parseFloat(principle * days/30)) * rate)/100;
		return parseFloat(interest) + parseFloat(principle);
}

function getMonths(a,b) {
	
	if (a == '' || b == '') {
		alert("Seems the issue date or return date are not proper");
		return 0;
	}
	
	var date1 = new Date(a);
	var date2 = new Date(b);
	var timeDiff = Math.abs(date2.getTime() - date1.getTime());
	var diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24)); 
	return parseInt(diffDays/30);
}

function getDays(a, b) {
	
	if (a == '' || b == '') {
		alert("Seems the issue date or return date are not proper");
		return 0;
	}
	
	var date1 = new Date(a);
	var date2 = new Date(b);
	var timeDiff = Math.abs(date2.getTime() - date1.getTime());
	var diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24)); 
	return parseInt(diffDays % 30);
}