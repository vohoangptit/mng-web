var selectedGroupsIds = [];
var selectedGroupsNames = [];
genNavigation("user");

$(document).ready(function () {
	//close another menu
	$('#master-data-menu-left').removeClass('kt-menu__item--open');
	$('#dashboard-menu-left').removeClass('kt-menu__item--open');
	$('#workflow-menu-left').removeClass('kt-menu__item--open');
	$('#job-management-menu-left').removeClass('kt-menu__item--open');
	$('#job-planning-menu-left').removeClass('kt-menu__item--open');
	$('#my-job-menu-left').removeClass('kt-menu__item--open');

	$("#btnAddNewUser").on('click', function(){
		$('.selecteddisplay').html('');
		selectedGroupsIds = [];
		selectedGroupsNames = [];
		
		//var list = $('.kt-checkbox-list');
		$.each($('input[name="groupCheckboxes"]:checked'), function() {
			$(".selecteddisplay").append('<span data-text="" data-value="' + $(this).val()
					+ '" class="selectchoice-tag">' + $(this).data('name') + 
                    '<button class= "selectchoice-button" type = "button" >x</button>'+
                '</div >');
			selectedGroupsIds.push($(this).val());
			selectedGroupsNames.push($(this).data('name'));
		});
		
		$('input[name="selectedGroupsIds[]"').val(selectedGroupsIds);
		
		removeDisplayItem();
	});

	removeDisplayItem();
	function removeDisplayItem(){
		var id; 
		$(".selectchoice-button").click(function(){
			this.closest('.selectchoice-tag').remove();
			id = $(this).parent().data('value');
			$('input[name="groupCheckboxes"][value=' + id +']').click();
		});
 	}

	$('.kt-checkbox').click(function(event){
		event.stopPropagation();
	})
	
	setInputFilter(document.getElementById("mobileNo"), function(value) {
		  return /^\d*$/.test(value); });
	
});



var loadFile = function(event) {
    var reader = new FileReader();

    const file = event.target.files[0];
    var fileType = file["type"];
    
    var validImageTypes = ["image/jpg", "image/jpeg", "image/png"];
    if ($.inArray(fileType, validImageTypes) < 0) {
    	$('#invalidFileModal').modal();
    } else {
    
	    reader.onload = function(){
	      var output = document.getElementById('imagePreview');
	      output.src = reader.result;
	      output.style.display="block";
	    };
	    reader.readAsDataURL(event.target.files[0]);
	    $('#imagePreview').css('display', 'block');
    }
};

function prepareConfirmation(e) {
	var email = $('#email').val().trim();
	var fullName = $('#fullName').val().trim();
	
	var isError = false;
	if (email.length == 0) {
		$('#emailError').css('display', 'block');
		isError = true;
	}
	if (fullName.length == 0) {
		$('#fullNameError').css('display', 'block');
		isError = true;
	}
	if (selectedGroupsNames.length == 0) {
		$('#groupsError').css('display', 'block');
		isError = true;
	}
	
	if (!isError) {
	
		var userGroupStr = "";
		for (let i in selectedGroupsNames) {
			userGroupStr += selectedGroupsNames[i] + ", ";
		}
		userGroupStr = userGroupStr.substring(0, userGroupStr.length - 2);
		
		var activeInactive = "Active";
		if ($('input[name="status"]:checked').val() === "N") {
			activeInactive = "Inactive";
		}
		
		var departmentName = $('#department').find(':selected').data('name');
		var jobTitle = $('#jobTitle').val() == 0 ? "" : $('#jobTitle').find(':selected').data('name');
		
		$('#emailConfirm').text($('#email').val());
		$('#fullNameConfirm').text($('#fullName').val());
		$('#userGroupConfirm').text(userGroupStr);
		$('#departmentConfirm').text(departmentName);
		$('#jobTitleConfirm').text(jobTitle);
		$('#mobileNoConfirm').text($('#mobileNo').val());
		$('#statusConfirm').text(activeInactive);
		
		$('#kt_modal_1').modal();
		
		$('input[name="selectedGroupsNames"]').val(userGroupStr);
		$('input[name="selectedDepartmentName"]').val(departmentName);
		$('input[name="selectedJobTitleName"]').val(jobTitle);

	}
}

function hideEmailError() {
	$('#emailError').css('display', 'none');
	$('#emailExistError').css('display', 'none');
	$('#emailFormatError').css('display', 'none');
}

function hideFullNameError() {
	$('#fullNameError').css('display', 'none');
}

function hideGroupsError() {
	$('#groupsError').css('display', 'none');
}

function checkExistingEmail(email) {
	var emailEntered = $(email).val();
	var userId = $('#userId').val();
	$.get(
		'/nera/api/checkEmail',
		{
			'emailEntered' : emailEntered,
			'isEdit': isEdit,
			'userId': userId
		},
		function (data) {
			if (data == 'true') {
				$('#emailExistError').css('display', 'block');
				$('#saveBtn').attr('disabled', true);
			} else {
				$('#saveBtn').removeAttr('disabled');
			}
		});
}

function validateEmail(email) {
	var emailText = $(email).val();
	var regex = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	var result = regex.test(String(emailText).toLowerCase());
	if (!result) {
		$('#emailFormatError').css('display', 'block');
	}
}

function searchGroups(searchBox) {
	var searchText = $(searchBox).val();
	
	if (searchText.length != 0) {
		$('input[name="groupCheckboxes"]:not([data-name*="' + searchText + '" i] )').parent().css('display', 'none');
	} else {
		$('input[name="groupCheckboxes"]').parent().css('display', 'block');
	}
}

function setInputFilter(textbox, inputFilter) {
	  ["input", "keydown", "keyup", "mousedown", "mouseup", "select", "contextmenu", "drop"].forEach(function(event) {
	    textbox.addEventListener(event, function() {
	      if (inputFilter(this.value)) {
	        this.oldValue = this.value;
	        this.oldSelectionStart = this.selectionStart;
	        this.oldSelectionEnd = this.selectionEnd;
	      } else if (this.hasOwnProperty("oldValue")) {
	        this.value = this.oldValue;
	        this.setSelectionRange(this.oldSelectionStart, this.oldSelectionEnd);
	      }
	    });
	  });
	}

function checkMobileNo(mobileNo) {
	if ($(mobileNo).val() !==  "" && $(mobileNo).val().length < 8) {
			$('#mobileLengthError').css('display', 'block');
			$('#saveBtn').attr('disabled', true);
	} else {
		$('#mobileLengthError').css('display', 'none');
		$('#saveBtn').removeAttr('disabled');
	}
}