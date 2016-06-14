$(document).ready(function(){
	$("#add-borrower-btn").click(function(e){
	    $.ajax({
	     url : "/createBorrower",
	     type : "POST",
	     data : JSON.stringify(
	    {
	        borrowerName: $('#borrowerName').val(),
	        fatherName: $('#fatherName').val(),
	        caste : $('#caste').val(),
	        place : $('#place').val()
	    }),
	    dataType: "json",
	    contentType: "application/json",
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
});
