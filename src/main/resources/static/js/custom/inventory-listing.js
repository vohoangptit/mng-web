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
		        url: '/nera/inventory/api/listing-and-filter',					        
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
//	    	{
//		      field: '',
//		      title: '<input class="caseall" type="checkbox" name="" value="">',
//		      sortable: false,
//		      width: 30,
//		      template: function(row) {					        
//		        var html = '<input class="case" type="checkbox" name="" value="' + row.id + '">';
//		        return html;
//		      },
//		    },
			{
			    field: 'no',
	            title: 'S/N',
	            sortable: false,
	            width: 30,
			},
		    {
		      field: 'name',
		      title: 'Inventory Name',
		      textAlign: 'left',
		    }, 
		    {
		      field: 'createdBy',
		      title: 'Created By',
		    },
		    {
		      field: 'createdDate',
		      title: 'Created Date',
		    },
		    {					      
		      field: 'status',
		      title: 'Status',
		      width: 70,
		      sortable: true,
		      template: function(row) {	
		    	var st = row.isActive == true ? "Active" : "Inactive";
		        var html = '<span>' + st + '</span>';
		        return html;
		      },
		    },
	    	{
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
		        	htmlEdit = '<a href="/menu/masterdata/inventory/detail/' + '" class="btn btn-hover-brand btn-icon btn-pill" data-id="' + id + '"><i class="la la-edit"></i></a>';
		        }
                if (hasDeletePermission) {
                	htmlDelete = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill deleteinventory" data-id="' + id + '"><i class="la la-trash"></i></a>';
                }
                return htmlEdit + htmlDelete;
		      },
		    }],	
		});
	
	loadCreatedByAll();
	function loadCreatedByAll()
	{
	    $.ajax({
	        type: 'GET',
	        url: '/nera/inventory/api/created-person',
	        success: function (data) {
	        	var re = data;
	        	for(var i = 0; i < re.length; i++)
        		{
		        	var op = new Option(re[i], re[i]);
		        	$("#createdBySelectd").append(op);
        		}
	        	$('#createdBySelectd').select2({ placeholder: "All", allowClear: false, width: 'resolve', dropdownAutoWidth: true });
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
	
	// For delete functions
	$('#btnDelete').on('click', function () {
	    var arr = getidlist();
	    if (arr.length > 0) {
	        $('#kt_modal_Delete').modal('show');
	    }
	    else {
	        return false;
	    }
	});

	$('#kt_datatable').on('click', '.deleteinventory', function(){
		var id = $(this).attr('data-id');
		$('#deleteInId').val(id);
		$('#kt_modal_Delete').modal('show');
	});
	
	function getidlist() {
	    var array = [];
	    $('#kt_datatable .case:checked').each(function () {
            var id = $(this).val();
            array.push(id);
	    });
	    return array;
	}

	$('#btnYesDelete').on('click', function(){
		var arr = getidlist();
		if(arr.length > 0){
			callDeleteApi(arr);
		}
		else{
			arr.push($('#deleteInId').val());
			callDeleteApi(arr);
		}
	});
	
	var callDeleteApi = function(arr) {

	    $.ajax({
	        type: 'POST',
	        url: '/nera/inventory/api/delete-list-by-id',
	        data: JSON.stringify(arr),
	        contentType: "application/json",
	        beforeSend: function () {
	            //
	        },
	        success: function (data) {
	            window.location.href = './inventory';
	        },
	        error: function (err) {
	            //
	        },
	    });

	};
	
	fixedHeaderDatatable();
});

