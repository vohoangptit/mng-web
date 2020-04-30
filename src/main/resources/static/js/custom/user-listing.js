var datatable;
var selectedUserDatatable;
var selectedCheckboxes = [];
var selectedDeleteUserId = [];					 
var userData;
var userGroups;
var filteredUserGroupIds = '';
var filteredDepartmentIds = '';
var filteredJobTitleIds = '';
var filteredFromLastLogin = '';
var filteredToLastLogin = '';
var departments;
var jobTitles;
var searchText = '';
var refreshFilters = true;
var isOpen = false;

$(document).ready(function() {
	checkSuccess();
	function checkSuccess() {
		if(hasEditUser != null && hasEditUser !== '') {
			if(hasEditUser === 'true') {
				Swal.fire({
					text: "Edit user is successful.",
					type: 'success',
					timer: 3000,
					showConfirmButton: false
				});
			} else {
				Swal.fire({
					text: "Edit user is failed.",
					type: 'error',
					timer: 3000,
					showConfirmButton: false
				});
			}
		}
		if(hasCreateUser != null && hasCreateUser !== '') {
			if(hasCreateUser === 'true') {
				Swal.fire({
					text: "Create user is successful.",
					type: 'success',
					timer: 3000,
					showConfirmButton: false
				});
			} else {
				Swal.fire({
					text: "Create user is failed.",
					type: 'error',
					timer: 3000,
					showConfirmButton: false
				});
			}
		}
	}

	loadDataTable();
	datatable.on('kt-datatable--on-init', function(){
		datatable.reload();
    	fixedTableHeader();
    });
	
	$('#groupDropdownMenuButton').on('click', function(event) {
		$('#userGroupDropdown').slideToggle();
		event.stopPropagation();
	});

	$('#userGroupDropdown').on('click', function(event) {
		event.stopPropagation();
	});

	$('#departmentDropdownMenuButton').on('click', function(event) {
		$('#departmentDropdown').slideToggle();
		event.stopPropagation();
	});

	$('#departmentDropdown').on('click', function(event) {
		event.stopPropagation();
	});

	$('#jobTitleDropdownMenuButton').on('click', function(event) {
		$('#jobTitleDropdown').slideToggle();
		event.stopPropagation();
	});

	$('#jobTitleDropdown').on('click', function(event) {
		event.stopPropagation();
	});

	$(window).on('click', function() {
		closeAllFilters();
	});
});

function loadDataTable() {
	datatable = $('#kt_datatable').KTDatatable({
		  // datasource definition
		  data: {
		    type: 'remote',
		    source: {
		      read: {
		        url: '/nera/api/user/',					        
		        map: function(raw) {
		        	userData = raw.data;
		        	userGroups = raw.userGroups;
		        	departments = raw.departments;
		        	jobTitles = raw.jobTitles;
		          // sample data mapping
		          var dataSet = raw;
		          if (typeof raw.data !== 'undefined') {
		            dataSet = raw.data;
		          }
		          return dataSet;
		        },	
		      },
		    },
		    pageSize: 10,
		    saveState: {
                cookie: false,
                webstorage: false
            },
		    serverPaging: true,
		    serverFiltering: true,
		    serverSorting: true    
		  },
		  

		  // layout definition
		  layout: {
			scroll: false,
		    footer: false,
		  },
		  selector: {
			  class: 'kt-checkbox--solid'
			},
		  	
		  // column sorting
		  sortable: true,
		  pagination: true,

		  search: {
		    input: $('#generalSearch'),
		  },

		  // datatableOptions,
		  options: {
			   extensions: {
			      checkbox: {
			         vars: {
			           selectedAllRows: 'selectedAllRows',
			           requestIds: 'requestIds',
			           rowIds: 'meta.rowIds',
			         },
			      },
			   },
		  },
		  // columns definition
		  columns: [{
			    field: 'id',
	            title: '',
	            sortable: false,
	            width: 30,
	            selector: {
	                class: 'kt-checkbox--solid'
	            },
	            textAlign: 'center',},
		    {
		      field: 'recordId',
		      title: 'S/N',
		      type: 'number',
		      selector: false,
		      textAlign: 'center',
		      sortable: false,
		    }, {
		      field: 'fullName',
		      title: 'Full Name',
		    }, {					      
		      field: 'email',
		      title: 'Email',
		    }, {					      
		      field: 'mobileNumber',
		      title: 'Mobile No',
		    }, {					      
		      field: 'groupNames',
		      title: 'User Group',
		    }, {					    	
		      field: 'departmentName',
		      title: 'Department',
		    }, {
		      field: 'jobTitleName',
		      title: 'Job Title',
		    }, {
		      field: 'status',
		      title: 'Status',
		      textAlign: 'center',
		      template: function(row) {
		    	  var active = row.active;
		    	  var htmlEdit;
			      if (active) {
			        htmlEdit = '<button style="border-radius: 5px; border: 1px solid #1eb7ae; background-color: #1eb7ae;" disabled><span style="font-family: Poppins;color:white">Active</span></button>';
			      } else{
			      	htmlEdit = '<button style="border-radius: 5px; border: 1px solid #7f7f7f; background-color: #7f7f7f;" disabled><span style="font-family: Poppins;color:white">InActive</span></button>';
			      }
			      return htmlEdit;
			      },
		    }, {
		      field: 'formattedLastLogin',
		      title: 'Last Login Timestamp',
		    }, {
		      field: 'Actions',
		      title: 'Actions',
		      sortable: false,
		      width: 130,
		      overflow: 'visible',
		      textAlign: 'center',
		      template: function(row) {
		        let id = row.id;
		        let htmlEdit = '';
		        let htmlDelete = '';
		        
		        if (hasEditPermission) {
		        	htmlEdit = '<a href="/editUser?userId=' + id + '" class="btn btn-hover-brand btn-icon btn-pill" title="Edit details"><i class="la la-edit"></i></a>'
		        }
		        if (hasDeletePermission) {
		        	htmlDelete = '<a onclick="checkDelete(' + id + ')" href="#" class="btn btn-hover-danger btn-icon btn-pill deleteUser" data-id="' + id + '" title="Delete"><i class="la la-trash"></i></a>';
		        }
		        return htmlEdit + htmlDelete;
		      },
		    }],	
		});
	datatable.on('kt-datatable--on-layout-updated', function (e) {
	   $(document).on('click', '.deleteUser' , function() {
		   checkDelete($(this).attr('data-id'));
		});
	   let html = '';
	   if (refreshFilters) {
		   for (let key in userGroups) {
			   html += '<div class="form-check">' +
						  '<input class="form-check-input" type="checkbox" name="userGroupFilter" id="' + key + '" value="' + userGroups[key] +'">' +
						    '<label for="' + key + '">' + key + '</label>'+
						'</div>';
			 }
		   html += '<hr />';
		   $('#userGroupDropdown').html(html);
		   
		   html = '';
		   for (let key in departments) {
			   html += '<div class="form-check">' +
						  '<input class="form-check-input" type="checkbox" name="departmentFilter" id="' + key + '" value="' + departments[key] +'">' +
						  '<label for="' + key + '">' + key + '</label>'+
						'</div>';
			 }
		   if(departments != null){
			   html += '<span>Empty data</span>';
		   }
		   html += '<hr />';
		   $('#departmentDropdown').html(html);
		   
		   html = '';
		   for (let key in jobTitles) {
			   html += '<div class="form-check">' +
						  '<input class="form-check-input" type="checkbox" name="jobTitleFilter" id="' + key + '" value="' + jobTitles[key] +'">' +
						  '<label for="' + key + '">' + key + '</label>'+
						'</div>';
			 }
		   html += '<hr />';
		   $('#jobTitleDropdown').html(html);
		   refreshFilters = false;
	   } 
	});
}

function closeAndUncheckAllFilters() {
	$('input[name="userGroupFilter"]').prop('checked', false);
	closeAllFilters();
}

function grep (id) {
	return jQuery.grep(userData, function(obj, index) {
	    return obj.id == id;
	});
}


function reset() {
	$('#generalSearch').val("");
	$('input[name="fromLastLogin"]').val('');
	$('input[name="toLastLogin"]').val('');
	$('input[name="userGroupFilter"]').prop('checked', false);
	$('input[name="departmentFilter"]').prop('checked', false);
	$('input[name="jobTitleFilter"]').prop('checked', false);
	
	filteredUserGroupIds = '';
	filteredDepartmentIds = '';
	filteredJobTitleIds = '';
	filteredFromLastLogin = '';
	filteredToLastLogin = '';
	
	datatable.setDataSourceParam("filteredUserGroupIds", "");
	datatable.setDataSourceParam("filteredDepartmentIds", "");
	datatable.setDataSourceParam("filteredJobTitleIds", "");
	datatable.setDataSourceParam("filteredFromLastLogin", "");
	datatable.setDataSourceParam("filteredToLastLogin", "");
	
	searchText = '';
	$('#generalSearch').val('');
	
	doSearch();
}			

function unblockUser() {
	selectedCheckboxes = [];
	let names = "";
    $('#kt_datatable').find('input[type="checkbox"]:checked')
    	.each(function () {
			selectedCheckboxes.push($(this).val());	
			let userInfo = grep($(this).val())[0];
			if (typeof userInfo != 'undefined') {
				names += userInfo.fullName + ", ";	
			}
		});
	names = names.slice(0, -2);
	names = removeRed(names);

	if (selectedCheckboxes.length > 0) {
		$('#unblockNames').html(names);
		$('#unblockModal').modal();	
	} else {
		$('#unblockSelectModal').modal();
	}
}

function confirmUnblockUser() {
	$('#unblockModal').modal('hide');
	let obj = {
			'ids': selectedCheckboxes,
     };
	let jsonObj = JSON.stringify(obj);
	$.ajax(
		{
			url: '/nera/api/unblockUser/',
			type: 'PUT',
			contentType : 'application/json',
			dataType: 'text',
	        data :  jsonObj,
		success: function(response) {
			// clearMessages();
			$('input[type="checkbox"]').prop('checked', false);
			datatable.reload();
			// $('#successMsg').text(response);
			// $('#successAlert').css('display', 'block');
			// $('#successAlert').alert();
			// $('#successAlert').delay(2000).hide(200);
			Swal.fire({
				text:  response,
				type:  'success',
				timer: 2000,
				showConfirmButton: false
			});
    	},
    	error: function(response) {
    		// clearMessages();
    		// $('#errorMsg').text(response);
			// $('#failedAlert').css('display', 'block');
			// $('#failedAlert').alert();
			// $('#failedAlert').delay(2000).hide(200);
			Swal.fire({
				text:  response,
				type:  'error',
				timer: 2000,
				showConfirmButton: false
			});
    	}
	});
}

function updateStatus() {
	let userArray = [];
	
	$('input[name="activeOrInactive"][value="true"]').prop("checked",true);
	$('#selectOneUpdateUser').css('display', 'none');
	let userMap = '[';
	selectedCheckboxes = [];
	$('.userListingDatatable input:checkbox:checked')
		.each(function () {
			let userInfo = grep($(this).val())[0];
			if (typeof userInfo != "undefined") {
				userMap = userMap + '{' + '"id":' + '"' + userInfo.id + '",' + '"fullName":' + '"' + userInfo.fullName + '"' + '},'
				userArray.push(userInfo.fullName);
			}
		});
	userMap = userMap.slice(0,-1) + "]";
	if (userArray.length > 0) {
		if (selectedUserDatatable !== undefined) {
			selectedUserDatatable.destroy();
		}
		userMap = removeRed(userMap);
		let jsonUserArray = JSON.parse(userMap);
		selectedUserDatatable = $('.selectedUserTable').KTDatatable({
			data: {
				type: 'local',
				source: jsonUserArray,
			},
			columns: [{
				    field: 'id',
		            title: '#',
		            sortable: false,
		            width: 20,
		            selector: {
		                class: 'kt-checkbox--solid kt-checkbox-brand'
		            },
		            textAlign: 'center'
	            }, {
			      field: 'fullName',
			      title: 'Full Name'
			    }]							
			});
		selectedUserDatatable.reload();
		selectedUserDatatable.on(
            'kt-datatable--on-check kt-datatable--on-uncheck',
            function(e) {
                let checkedNodes = selectedUserDatatable.rows('.kt-datatable__row--active').nodes();
                let count = checkedNodes.length;
                $('#selectedUserStatusCount').html(count);
            });
		selectedUserDatatable.on('kt-datatable--on-layout-updated', function (e) {
			selectedUserDatatable.setActiveAll(true);
		});
		
		$('#statusModal').modal();	
	} else {
		$('#statusSelectModal').modal();
	}
}

function confirmUpdateStatus() {
	let selectedUserId = [];
	// var html = selectedUserDatatable.getSelectedRecords().html();
	$.each(selectedUserDatatable.getSelectedRecords(), function(key, value) {
		let found = $(':checkbox', value);
		selectedUserId.push(found.val());
	});
	if (selectedUserId.length > 0) {
		let selectedActive = $("input[name='activeOrInactive']:checked").val();
		let obj = {
				'ids': selectedUserId,
				'activeOption': selectedActive
	     };
		let jsonObj = JSON.stringify(obj);
		$.ajax(
			{
				url: '/nera/api/updateStatus/',
				type: 'PUT',
				contentType : 'application/json',
			    dataType: 'text',
		        data :  jsonObj,
			success: function(response) {
				$('#statusModal').modal('hide');
				$('#statusModal').modal('dispose');
				// clearMessages();
				datatable.reload();
				// $('#successMsg').text(response);
				// $('#successAlert').css('display', 'block');
				// $('#successAlert').alert();
				// $('#successAlert').delay(2000).hide(200);
				Swal.fire({
					text:  response,
					type:  'success',
					timer: 2000,
					showConfirmButton: false
				});
	    	}, 
	    	error: function(response) {
	    		$('#statusModal').modal('hide');
				$('#statusModal').modal('dispose');
	    		// clearMessages();
	    		// $('#errorMsg').text(response);
				// $('#failedAlert').css('display', 'block');
				// $('#failedAlert').alert();
				// $('#failedAlert').delay(2000).hide(200);
				Swal.fire({
					text:  response,
					type:  'error',
					timer: 2000,
					showConfirmButton: false
				});
	    	}
		});
	} else {
		$('#selectOneUpdateUser').css('display', 'block');
	}				
}

function checkDelete(id) {
	let userInfo = grep(id)[0];
	if (userInfo.status === 'Active') {
		let html = userInfo.fullName;
		$('#stillActiveUsername').html(html);
		$('#stillActiveOneUserModal').modal();
	} else {
		selectedDeleteUserId = [userInfo.id];
		$('#confirmDeleteOneUserModal').modal();
	}
}

function deleteUser() {
	selectedDeleteUserId = [];
	let names = "";
	$.each(datatable.getSelectedRecords(), function(key, value) {
		let found = $(':checkbox', value);
		selectedDeleteUserId.push(found.val());
		let userInfo = grep(found.val())[0];
		names += userInfo.fullName + ", ";
	});
	
	names = names.slice(0,-2);
	names = removeRed(names);
	
	let stillActiveUsers = [];
	if (selectedDeleteUserId.length > 0) {
		for (let i  in selectedDeleteUserId) {
			let userInfo = grep(selectedDeleteUserId[i])[0];
			if (userInfo.status === 'Active') {
				stillActiveUsers.push(userInfo.fullName);
			}
		}
		
		if (stillActiveUsers.length > 0) {
			let html = "";
			for (let i in stillActiveUsers) {
				html += stillActiveUsers[i] + ', ';
			}						
			html = html.slice(0,-2);
			$('#stillActiveUsersNames').html(html);
			$('#stillActiveUsersModal').modal();
		} else {
			$('#deleteNames').html(names)
			$('#confirmDeleteModal').modal();
		}
	} else {
		$('#deleteSelectModal').modal();
	}
}
																							 
function confirmDelete() {
    let obj = {'ids': selectedDeleteUserId};
    let jsonObj = JSON.stringify(obj);
    $.ajax({
        url: '/nera/api/deleteUsers/',
        type: 'delete',
        contentType : 'application/json',
        dataType: 'text',
        data :  jsonObj,
        success: function(response) {
            $('#confirmDeleteOneUserModal').modal('hide');
            $('#confirmDeleteOneUserModal').modal('dispose');
            $('#confirmDeleteModal').modal('hide');
            $('#confirmDeleteModal').modal('dispose');
            datatable.reload();
            Swal.fire({
                text:  response,
                type:  'success',
                timer: 2000,
                showConfirmButton: false
            });
        },
        error: function(response) {
            Swal.fire({
                text:  response,
                type:  'error',
                timer: 2000,
                showConfirmButton: false
            });
        }
    });
}

function clearMessages() {
	$('#successAlert').css('display', 'none');
	$('#failedAlert').css('display', 'none');
	
}

function removeRed(textStr) {
	textStr = textStr.replace(/\<span class=\"red\">/g, "");
	return textStr.replace(/\<\/span>/g, "");
}

function filterUserGroup() {
	filteredUserGroupIds = '';
	$.each($('input[name="userGroupFilter"]:checked'), function() {
		filteredUserGroupIds += $(this).val() + ',';
	});
	filteredUserGroupIds = filteredUserGroupIds.slice(0, filteredUserGroupIds.length-1);
	datatable.setDataSourceParam("filteredUserGroupIds", filteredUserGroupIds);
	
	// doSearch();
}

function filterDepartment() {
	filteredDepartmentIds = '';
	$.each($('input[name="departmentFilter"]:checked'), function() {
		filteredDepartmentIds += $(this).val() + ',';
	});
	filteredDepartmentIds = filteredDepartmentIds.slice(0, filteredDepartmentIds.length-1);
	datatable.setDataSourceParam("filteredDepartmentIds", filteredDepartmentIds);
}

function filterJobTitle() {
	filteredJobTitleIds = '';
	$.each($('input[name="jobTitleFilter"]:checked'), function() {
		filteredJobTitleIds += $(this).val() + ',';
	});
	filteredJobTitleIds = filteredJobTitleIds.slice(0, filteredJobTitleIds.length-1);
	datatable.setDataSourceParam("filteredJobTitleIds", filteredJobTitleIds);
}


function filterFromLastLogin(){
	filteredFromLastLogin = $('input[name="fromLastLogin"]').val();	
	datatable.setDataSourceParam("filteredFromLastLogin", filteredFromLastLogin);
}
function filterToLastLogin(){
	filteredToLastLogin = $('input[name="toLastLogin"]').val();
	datatable.setDataSourceParam("filteredToLastLogin", filteredToLastLogin);
}

function doSearch() {
	filterUserGroup();
	filterDepartment();
	filterJobTitle();
	filterFromLastLogin();
	filterToLastLogin();
	
	if (filteredFromLastLogin !== '' && filteredToLastLogin !== '') {
		let fromDate = moment(filteredFromLastLogin, "DD/MM/YYYY");
		let toDate = moment(filteredToLastLogin, "DD/MM/YYYY");
		
		if (new Date(fromDate) > new Date(toDate)) {
			$('#dateRangeError').css('display', 'block');
			return;
		} else {
			$('#dateRangeError').css('display', 'none');
		}
	}
	
	searchText = $('#generalSearch').val();
	
	datatable.setDataSourceParam("query", '');
	datatable.load();
}

function closeAllFilters() {
	$('#userGroupDropdown').slideUp();
	$('#departmentDropdown').slideUp();
	$('#jobTitleDropdown').slideUp();
}