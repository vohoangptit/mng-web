"use strict";

var datatable;
var selectedUserDatatable;
var selectedCheckboxes = [];
var selectedDeleteUserId;					 
var userData;
var userInfo;
$(document).ready(function() {
	datatable = $('#kt_datatable').KTDatatable({
		  // datasource definition
		  data: {
		    type: 'remote',
		    source: {
		      read: {
		    	params: {
		    		page: 0,
		    		size:10//,
		    		/*sort:'id'+','+'desc'*/
		    	},
		    	method: 'GET',
		        url: '/nera/user-group/api/getAll',					        
		        map: function(raw) {
		        	userData = raw.data;
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
		  // columns definition
		  columns: [{
			    field: 'no',
	            title: 'S/N',
	            width: 30,
	            sortable: false,
	            textAlign: 'left',},
		    {
		      field: 'groupName',
		      title: 'User Group Name',
		      sortable: true,
		      textAlign: 'left',
		    }, {
		      field: 'description',
		      title: 'Description',
		      sortable: true,
		    }, {					      
		      field: 'active',
		      title: 'Status',
		      sortable: true,
		      template: function(row) {
		    	  var active = row.active;
		    	  var htmlEdit;
			      if (active) {
			        htmlEdit = '<button style="border-radius: 5px;border: 1px solid #1eb7ae;background-color: #1eb7ae;" disabled><span style="font-family: Poppins;color:white">Active</span></button>';
			      } else{
			      	htmlEdit = '<button style="border-radius: 5px;border: 1px solid #7f7f7f;background-color: #7f7f7f;" disabled><span style="font-family: Poppins;color:white">InActive</span></button>';
			      }
			      return htmlEdit;
			      },
		    }, {
		      field: 'Actions',
		      title: 'Actions',
		      sortable: false,
		      overflow: 'visible',
		      textAlign: 'center',
		      template: function(row) {
		        var id = row.id;
		        
		        var htmlEdit = '';
		        var htmlDelete = '';
		        if (hasEditPermission) {
		        	htmlEdit = '<a onclick="showUpdatePopup(' + id + ')" href="#"  class="btn btn-hover-brand btn-icon btn-pill updateUser" data-id="' + id + '" title="Edit details"><i class="la la-edit"></i></a>'; 		 
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
		   $(document).on('click', '.updateUser' , function() {
			   showUpdatePopup($(this).attr('data-id'));
			});
	});
	datatable.on('kt-datatable--on-init', function(){
    	fixedTableHeader();
    });
});
function showUpdatePopup(id) {
	userInfo = grep(id)[0];
	$('#groupName').val(userInfo.groupName);
	$('#description').val(userInfo.description);
	if(userInfo.active===true){
		$('#group-radio-active').prop('checked', true);
	} else{
		$('#group-radio-inactive').prop('checked', true);
	}
	$( "#title-detail h5").empty();
	$( "#title-detail h5").append("Update User Group");
	$('#kt_modal_create_update_group').modal();
}



function showCreatePopup() {
	userInfo = {};
	$( "#title-detail h5").empty();
	$( "#title-detail h5").append("Add User Group");
	$('#frm_create_update_group').validate().resetForm();
	$('#kt_modal_create_update_group').modal();
}
function grep (id) {
	return jQuery.grep(userData, function(obj, index) {
	    return obj.id == id;
	});
}

function closeCreateOrUpdatePopup() {
	$('#kt_modal_create_update_group').modal('toggle');
	$('#frm_create_update_group').validate().resetForm();
}
jQuery(document).ready(function () {
		//var type = "";
		$('#kt_add_group_submit').click(function(e) {
	        e.preventDefault();
	        //var btn = $(this);
	        let form = $(this).closest('form');
	        form.validate({
	        	rules: {
	            	groupName: {
	                    required: true
	                }
	            },
	        });
	        var json = {
	        		"groupName": $('#groupName').val(),
					"description": $('#description').val(),
					"active": $("input[name='radio']:checked").val(), 
	        }
	        if(typeof userInfo !== "undefined"){
	        	json['id'] = userInfo.id;
	        }
	        if (form.valid()) {
	        	$.ajax(
	        			{
	        				url: '/nera/user-group/api/add-or-update',
	        				type: 'post',
	        				contentType: 'application/json; charset=utf-8',
	        				data: JSON.stringify(json),
	        				headers: {          
	        				    Accept: "application/json; charset=utf-8"         
	        				  },
	        			success: function(response) {
	        				if(response.status == 200){
	        					closeCreateOrUpdatePopup();
		        				datatable.reload();
								Swal.fire({
									text:  response.message,
									type:  'success',
									timer: 2000,
									showConfirmButton: false
								});
	        				} else{
	        					closeCreateOrUpdatePopup();
								Swal.fire({
									text:  response.message,
									type:  'error',
									timer: 2000,
									showConfirmButton: false
								});
	        				}
	        	    	},
	        	    	error:function (XMLHttpRequest, textStatus){
	        	    		closeCreateOrUpdatePopup();
	        	    		if(XMLHttpRequest.status === 405){
								Swal.fire({
									text:  'You not have permission!',
									type:  'error',
									timer: 2000,
									showConfirmButton: false
								});
	        	    		} else{
								Swal.fire({
									text:  textStatus,
									type:  'error',
									timer: 2000,
									showConfirmButton: false
								});
	        	    		}
	        	    	}
	        		});
	        }
	    });
});


function reset() {
	$('#generalSearch').val("");
	datatable.search("", "generalSearch");
}			

function checkDelete(id) {
	userInfo = grep(id)[0];
	if (userInfo.active ==true) {
		var html = userInfo.groupName;
		$('#stillActiveGroupName').html("User Group: " + "<span style='font-weight:bold'>"+ html +"</span>" +" still active!");
		$('#stillActiveGroupModal').modal();
	} else {
		selectedDeleteUserId = userInfo.id;
		$.get({
			url: '/nera/user-group/api/checkUserGroup?groupId=' + userInfo.id,		
			success: function(response) {
				if (response == 0) {
					$('#groupNameDelete').html("Are you sure you want to delete User Group " + "<span style='font-weight:bold'>"+ userInfo.groupName +"</span>");
					$('#confirmDeleteGroupModal').modal();
				} else {
					$('#groupNameDeleteWithActiveUsersText').html(userInfo.groupName + " group currently has " + response + " user(s).");
					$('#groupNameDeleteWithActiveUsers').modal();
				}
			}
		});
	}
}

function confirmDelete() {
	$('#groupNameDeleteWithActiveUsers').modal('hide');
	$.ajax(
			{
				url: '/nera/user-group/api/delete-by-id?id='+selectedDeleteUserId,
				type: 'put',
				contentType : 'application/json',
				headers: {          
				    Accept: "application/json; charset=utf-8"         
				},
			success: function(response) {
				$('#confirmDeleteGroupModal').modal('hide');
				$('#confirmDeleteGroupModal').modal('dispose');
				datatable.reload();
				Swal.fire({
					text:  'Delete Successfully',
					type:  'success',
					timer: 2000,
					showConfirmButton: false
				});
	    	},
	    	error:function (XMLHttpRequest, textStatus, errorThrown){
	    		$('#confirmDeleteGroupModal').modal('hide');
				$('#confirmDeleteGroupModal').modal('dispose');
				Swal.fire({
					text:  'An error has occurred',
					type:  'error',
					timer: 2000,
					showConfirmButton: false
				});
	    	} 
		});
}