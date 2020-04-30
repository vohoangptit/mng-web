"use strict";

var datatable;				 
$(document).ready(function() {
	datatable = $('#kt_datatable').KTDatatable({
		  // datasource definition
		  data: {
		    type: 'remote',
		    source: {
		      read: {
		    	method: 'GET',
		        url: '/nera/playbook/api/listing-approved',					        
		        map: function(raw) {
		          var dataSet = raw;
		          if (typeof raw.data !== 'undefined') {
		            dataSet = raw.data;
		            var total = raw.meta.total;
		            $('.numberofrecords').text(total);
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
		  
		  // columns definition
		  columns: [
	    	{
	    		field: 'id',
	            title: '',
	            sortable: false,
	            width: 30,
	            selector: {
	                class: 'kt-checkbox--solid'
	            },
	            textAlign: 'center',
	    	}
			,{
			    field: 'no',
	            title: 'S/N',
	            sortable: false,
	            width: 30,
			},
		    {
		      field: 'name',
		      title: 'Playbook Name',
		      textAlign: 'left',
		    }, 
		    {
		      field: 'createdBy',
		      title: 'Creator',
		    },
		    {
		      field: 'createdDate',
		      title: 'Created Date',
		    },
/*		    {					      
		    	field: 'status',
			      title: 'Status',
			      template: function(row, index, datatable) {
			    	  var htmlEdit = '<button style="border-radius: 5px; border: 1px solid #d9a747; background-color: #d9a747;" disabled><span style="font-family: Poppins;color:var(--white)">'+row.status+'</span></button>';
			    	  return htmlEdit;
			      },	    
			},*/
	    	{
		      field: 'Actions',
		      title: 'Actions',
		      sortable: false,
		      overflow: 'visible',
		      textAlign: 'center',
		      template: function(row) {
		        var id = row.id;
		        var html = '<a href="/menu/masterdata/playbook-approved/detail/' + id + '" class="btn btn-hover-brand btn-icon btn-pill" data-id="' + id + '"><i class="la la-edit"></i></a>';
		        var html1 = "";
		        var html2 = "";
		        if(hasApproveAndReject){
		        	html1 = '<a href="#" class="btn btn-icon btn-approval btn-pill approvedAction" data-id="' + id + '" data-name="' + row.name + '" data-creator="' + row.createdBy + '">'
		        			+'<i class="la la-check-circle"></i></a>';
		        	html2 = '<a href="#" class="btn btn-icon btn-reject btn-pill rejectedAction" data-id="' + id + '" data-name="' + row.name + '" data-creator="' + row.createdBy + '">'
		        			+'<i class="la la-minus-circle"></i></a>';
		        }
		        return html+html1+html2;
		      },
		    }],	
		});
	
	loadCreatedByAll();
	function loadCreatedByAll()
	{
	    $.ajax({
	        type: 'GET',
	        url: '/nera/playbook/api/created-person',
	        success: function (data) {
	        	var re = data;
	        	for(var i = 0; i < re.length; i++)
        		{
		        	var op = new Option(re[i], re[i]);
		        	$("#createdBySelectd").append(op);
        		}
	        	$('#createdBySelectd').select2({ placeholder: "Created By (All)", allowClear: false, width: 'resolve', dropdownAutoWidth: true });
	        },
	    });
	}
	
	$('#btnReset').on('click', function(){
		reset();
	});
	
	function reset() {
		$('#generalSearch').val("");
		$('#createdBySelectd').val('');
		$('.select2-selection__rendered').html('');
		
	    datatable.setDataSourceParam('SearchString', "");
    	datatable.setDataSourceParam('createdBy', null);
    	datatable.search('');
	}

	$('#btnSearch').on('click', function(){
		dosearch();
	});
	
	function dosearch() {

	    datatable.setDataSourceParam('SearchString', $('#generalSearch').val());
	    if($('#createdBySelectd').val().length == 1 && $('#createdBySelectd').val()[0] == "")
    	{
	    	datatable.setDataSourceParam('createdBy', null);
    	}
	    else{
	    	datatable.setDataSourceParam('createdBy', $('#createdBySelectd').val());
	    }
	    datatable.load();

	}
	
	//
	$('.caseall').click(function (event) {
	    if (this.checked) {
	        $('.case').prop('checked', true);
	    }
	    else {
	        $('.case').prop('checked', false);
	    }
	});

	$('.case').click(function (e) {
	    if ($('.case').length == $('.case:checked').length) {
	        $('.caseall').prop('checked', true);
	    }
	    else {
	        $('.caseall').prop('checked', false);
	    }
	});
	
	$('#btnApprove').on('click', function () {
	    changelayout("A");
	    var arr = getidlist();
	    if (arr.length > 0) {
	    	var textAppend;
	        $('#tblMassApproval > tbody').html('');
	        for (var i = 0; i < arr.length; i++) {
	            appendAppRejToConfirm(arr[i].id, arr[i].name, arr[i].createdby);
	        }
	        if(arr.length > 1){
	        	textAppend = 'By clicking on Approve button, you are confirming the change of status of these selected Playbooks from New to Approved (ready to be used in the workflow). '
	        			+ 'After approval, these selected playbooks will not appear in the listing, you can view these in "Playbook Listing".';
	        } else{
	        	textAppend = 'By clicking on Approve button, you are confirming the change of status of this selected Playbook from New to Approved (ready to be used in the workflow). '
	        		+ 'After approval, this selected playbook will not appear in the listing, you can view these in "Playbook Listing".';
	        }
	        $('#textApprovedReject').text(textAppend);
	        $('#massApproval').modal('show');
	    }
	    else {
	        return false;
	    }
	});

	$('#btnReject').on('click', function () {
	    changelayout("R");
	    var arr = getidlist();
	    if (arr.length > 0) {
	    	var textAppend;
	        $('#tblMassApproval > tbody').html('');
	        for (var i = 0; i < arr.length; i++) {
	            appendAppRejToConfirm(arr[i].id, arr[i].name, arr[i].createdby);
	        }
	        if(arr.length > 1){
	        	textAppend = 'By clicking on Reject button, you are confirming the change of status of these selected Playbooks from New to Reject (cannot use in the workflow). ' 
	        		+ 'After rejected, these selected playbooks will not appear in the listing, you can view these in "Playbook Listing".';
	        } else{
	        	textAppend = 'By clicking on Reject button, you are confirming the change of status of this selected Playbook from New to Reject (cannot use in the workflow). '
	        		+ 'After rejected, this selected playbook will not appear in the listing, you can view these in "Playbook Listing".';
	        }
	        $('#textApprovedReject').text(textAppend);
	        $('#massApproval').modal('show');
	    }
	    else {
	        return false;
	    }
	});

	function changelayout(t) {
	    if (t == "A") {
	        $('#btnContinueApprove').removeClass('hideclass');
	        $('#btnContinueReject').addClass('hideclass');
	    }
	    else {
	        $('#btnContinueApprove').addClass('hideclass');
	        $('#btnContinueReject').removeClass('hideclass');
	    }
	}

	function getidlist() {
	    var array = [];
	    $('.userGroupListingDatatable > table > tbody input:checkbox:checked').each(function () {
	        var ja = {
	            id: $(this).val(),
	            name: $(this).closest('tr').find('[data-field="name"]').text().trim(),
	            createdby: $(this).closest('tr').find('[data-field="createdBy"]').text().trim(),
	        };
	        array.push(ja);
	    });
	    return array;
	}

	function appendAppRejToConfirm(id, name, createdby) {
	    $('#tblMassApproval > tbody:last').append('<tr>'
	        + '<td data-id="' + id + '" class="getidlist">' + name + '</td>'
	        + '<td>' + createdby + '</td>'
	        + '<td><textarea class="form-control" rows="3" id="getRemarks"></textarea></td>'
	        + '</tr>');
	}

	$('#btnContinueApprove').on('click', function () {
	    var st = "APPROVED";
	    callApprovedRejectedApi(st);
	});

	$('#btnContinueReject').on('click', function () {
	    var st = "REJECTED";
	    callApprovedRejectedApi(st);
	});

	function callApprovedRejectedApi(st) {

	    var array = [];
	    $('#tblMassApproval tbody tr').each(function () {
	        var ja = {
	            id: $(this).find('.getidlist').attr('data-id'),
	            remark: $('#getRemarks').val(),
	            status: st,
	        };
	        array.push(ja);
	    });

	    $.ajax({
	        type: 'POST',
	        url: '/nera/playbook/api/approved-or-rejected-playbook',
	        data: JSON.stringify(array),
	        contentType: "application/json",
	        success: function (data) {
				countPlaybookApproved();
				Swal.fire({
					text:  'Update Success',
					type:  'success',
					timer: 4000,
					showConfirmButton: false
				});
				datatable.load();
				$("#massApproval").modal('hide');
	        },
	    });
	}
	
	function countPlaybookApproved() {
	    $.ajax({
	        type: 'GET',
	        url: '/nera/playbook/api/count-approved-playbook',
	        beforeSend: function () {
	            //
	        },
	        success: function (data) {
	        	$('.setPlaybookApprovedCount').text('Playbook Approved ('+ data +')');
	        },
	        error: function (err) {
	            //
	        },
	    });

	}
	
	datatable.on('kt-datatable--on-init', function(){
    	fixedTableHeader();
    });
	
	$('#kt_datatable').on('click', '.approvedAction', function(){
		changelayout("A");
		$('#kt_modal_Approval').modal('show');
		var id = $(this).attr('data-id');
		var name = $(this).attr('data-name');
		var createdBy = $(this).attr('data-creator');
		$('#tblMassApproval > tbody').html('');
		appendAppRejToConfirm(id,name,createdBy);
		var textAppend = 'By clicking on Approve button, you are confirming the change of status of this selected Playbook from New to Approved (ready to be used in the workflow). '
    		+ 'After approval, this selected playbook will not appear in the listing, you can view these in "Playbook Listing".';
		$('#textApprovedReject').text(textAppend);
        $('#massApproval').modal('show');
	});
	$('#kt_datatable').on('click', '.rejectedAction', function(){
		changelayout("R");
		$('#kt_modal_Approval').modal('show');
		var id = $(this).attr('data-id');
		var name = $(this).attr('data-name');
		var createdBy = $(this).attr('data-creator');
		$('#tblMassApproval > tbody').html('');
		appendAppRejToConfirm(id,name,createdBy);
		var textAppend = 'By clicking on Reject button, you are confirming the change of status of this selected Playbook from New to Rejected (cannot to be used in the workflow). '
    		+ 'After rejected, this selected playbook will not appear in the listing, you can view these in "Playbook Listing".';
		$('#textApprovedReject').text(textAppend);
        $('#massApproval').modal('show');
	});
});

