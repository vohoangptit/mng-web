"use strict";

// Class Definition
var systemGeneral = function() {

    var handleSystemGeneralSendTestEmail = function() {
        $('#send-test-email').click(function(e) {
            e.preventDefault();

            var btn = $(this);
            var form = $(this).closest('form');

            form.validate({
                rules: {
                    email: {
                        required: true,
                        email: true
                    },
	            	smtpHost: {
			                required: true
			            },
			        smtpPort: {
			            	required: true,
			            	number: true,
			            	maxlength: 10
			            }
                }
            });

            if (!form.valid()) {
                return;
            }

            btn.addClass('kt-spinner kt-spinner--right kt-spinner--sm kt-spinner--light').attr('disabled', true);

            form.ajaxSubmit({
                url: '/nera/api/config/general',
                type: 'post',
                data: function() {
                    return $(this).serialize();
                  },
                success: function(response, status, xhr, $form) {
                	// similate 4s delay
                	btn.removeClass('kt-spinner kt-spinner--right kt-spinner--sm kt-spinner--light').attr('disabled', false); // remove
                	$('#success-action').append("Save Successfully!");
    				$('#success-action').css('display', 'block');
    				$('#success-action').alert();
    				$('#success-action').delay(4000).hide(200);
                },
            	error: function(response) {
            		btn.removeClass('kt-spinner kt-spinner--right kt-spinner--sm kt-spinner--light').attr('disabled', false); // remove
            		if(response.status === 405){
    	    			$('#error-action').append("You not have permission!");
    	    		} else if(response.status === 400){
    	    			$('#error-action').append("Invalid Input! Pleasea try again!");
    	    		} else{
    	    			$('#error-action').append("Something went wrong!");
    	    		}
            		$('#error-action').css('display', 'block');
    				$('#error-action').alert();
    				$('#error-action').delay(4000).hide(200);
            	}
            });
            $('#success-action').delay(2000).empty();
        	$('#error-action').delay(2000).empty();
        });
    }

    var handleSystemGeneralSaveForm = function() {
        $('#system-general-save').click(function(e) {
            e.preventDefault();

            var btn = $(this);
            var form = $(this).closest('form');

            form.validate({
                rules: {
                    email: {
                        required: true,
                        email: true
                    },
	            	smtpHost: {
			                required: true
			            },
			        smtpPort: {
			            	required: true,
			            	number: true,
			            	maxlength: 10
			            }
                }
            });

            if (!form.valid()) {
                return;
            }

            btn.addClass('kt-spinner kt-spinner--right kt-spinner--sm kt-spinner--light').attr('disabled', true);

            form.ajaxSubmit({
                url: '/nera/api/config/general/save',
                type: 'post',
                data: function() {
                    return $(this).serialize();
                  },
                success: function(response, status, xhr, $form) {
                    	// similate 4s delay
                  	btn.removeClass('kt-spinner kt-spinner--right kt-spinner--sm kt-spinner--light').attr('disabled', false);// remove
                    	// $('#success-action').append("Save Successfully!");
        				// $('#success-action').css('display', 'block');
        				// $('#success-action').alert();
        				// $('#success-action').delay(4000).hide(200);
                    Swal.fire({
                        text: 'Save Successfully!',
                        type:  'success',
                        timer: 2000,
                        showConfirmButton: false
                    });
                },
                error: function(response) {
                		btn.removeClass('kt-spinner kt-spinner--right kt-spinner--sm kt-spinner--light').attr('disabled', false); // remove
                		if(response.status === 405){
        	    			$('#error-action').append("You not have permission!");
        	    		} else if(response.status === 400){
        	    			$('#error-action').append("Invalid Input! Pleasea try again!");
        	    		} else{
        	    			$('#error-action').append("Something went wrong!");
        	    		}
                		$('#error-action').css('display', 'block');
        				$('#error-action').alert();
        				$('#error-action').delay(4000).hide(200);
                	}
                });
                $('#success-action').delay(2000).empty();
            	$('#error-action').delay(2000).empty();
        });
    }
    
    var isChecked = function() {
    	$('#isAuthentication').change(function() {
    		var status = $('#isAuthentication:checked').length;
    		if(status ==1) {
    			$('#authentication-info').show();
    		} else {
    			$('#authentication-info').hide();
    			$('#username').val("");
    			$('#password').val("");
    		}
    	})
    }
    // Public Functions
    return {
        // public functions
        init: function() {
        	handleSystemGeneralSendTestEmail();
        	handleSystemGeneralSaveForm();
        	isChecked();
        }
    };
}();

// Class Initialization
jQuery(document).ready(function() {
	systemGeneral.init();
});