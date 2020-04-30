genNavigation("user");

function loadUserGroups(userGroupsIds, userGroupNames) {
	$.each(userGroupsIds, function(index, value) {
		$(".selecteddisplay").append('<span data-name="' + userGroupNames[index] + '" data-value="' + userGroupsIds[index]
				+ '" class="selectchoice-tag">' + userGroupNames[index] + 
	            '<button class= "selectchoice-button" type = "button" >x</button>'+
	        '</div >');
		selectedGroupsIds.push(userGroupsIds[index]);
		selectedGroupsNames.push(userGroupNames[index]);
		
		removeDisplayItemUpdate();
	}); 
	
	$('input[name="selectedGroupsIds[]"').val(selectedGroupsIds);
	$('input[name="selectedGroupsNames"').val(selectedGroupsNames);
}

$(document).ready(function() {
	$('input[name="selectedDepartmentName"]').val($('#department').find(':selected').data('name')); 
	$('input[name="selectedJobTitleName"]').val($('#jobTitle').find(':selected').data('name'));
});

function removeDisplayItemUpdate(){
	$(".selectchoice-button").click(function(){
		this.closest('.selectchoice-tag').remove();
		let id = $(this).parent().data('value');
		let name = $(this).parent().data('name');
		$('input[name="groupCheckboxes"][value=' + id +']').prop('checked', false);//.click();
		
		let selectedGroupsIds = selectedGroupsIds.filter(function(value, index, arr) {
			return value != id;
		});
		let selectedGroupsNames = selectedGroupsNames.filter(function(value, index, arr) {
			return value != name;
		});
		$('input[name="selectedGroupsIds[]"').val(selectedGroupsIds);
		$('input[name="selectedGroupsNames[]"').val(selectedGroupsNames);
	});
}
function prepareUpdateConfirmation() {
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

		// let activeInactive = "Active";
		// if ($('input[name="status"]:checked').val() === "N") {
		// 	activeInactive = "Inactive";
		// }

		var departmentName = $('#department').find(':selected').data('name');
		var jobTitle = $('#jobTitle').val() == 0 ? "" : $('#jobTitle').find(':selected').data('name');
		
		
		$('input[name="selectedGroupsNames"]').val(userGroupStr);
		$('input[name="selectedDepartmentName"]').val(departmentName);
		$('input[name="selectedJobTitleName"]').val(jobTitle);

		$('#confimUpateModal').modal();
	}
	
}

function prepareResetPassword() {
	$('#resetPasswordModal').modal();
}

function prepareUnblockUser() {
	$('#unblockUserModal').modal();
}

function prepareDeleteUser() {
	if ($('input[name="status"]:checked').val() == 'Y') {
		$('#cannotDeleteModal').modal();
	} else {
		$('#deleteUserModal').modal();	
	}
	
}