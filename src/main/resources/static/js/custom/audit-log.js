"use strict";

var datatable;
var filteredFromActionDate;
var filteredToActionDate;
// Class Definition
var KTAuditLog = function() {

	var dataTableInit = function() {
		datatable = $('#kt_datatable').KTDatatable({
			  // datasource definition						  
			  data: {
			    type: 'remote',
			    source: {
			      read: {
			        url: '/nera/api/list/auditlog',
			        params: {
			    		page: 0,
			    		size:10
			    	},
			        map: function(raw)  {
			        	
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
				  	scroll: true,
				    //height: 350,
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
		            sortable: false,
		            width: 30,
		            textAlign: 'left',},
			    {
			      field: 'userName',
			      title: 'User',
			      selector: false,
			      textAlign: 'left',
			    }, {
			      field: 'userGroupName',
			      title: 'User Group',
			    }, {					      
			      field: 'description',
			      title: 'Description',
			    }, {					      
			      field: 'details',
			      title: 'Details',
			    },
				  {
			      field: 'strActionDate',
			      title: 'Action Date(dd/MM/yyyy hh:mm)',
			    }],	
			});
	}

	var reset = function() {
		$('#audit-reset-search').click(function(e) {
			e.preventDefault();
			$('#generalSearch').val("");
			$('input[name="fromActionDate"]').val('');
			$('input[name="toActionDate"]').val('');
			
			datatable.setDataSourceParam("filteredFromActionDate", "");
			datatable.setDataSourceParam("filteredToActionDate", "");
			datatable.search("", "generalSearch");
		});
		
	}		

    // Public Functions
    return {
        // public functions
        init: function() {
        	dataTableInit();
        	reset();
        }
    };
}();
loaddatetimepicker();
function loaddatetimepicker()
{
    $('.m_datepicker').datepicker({
        format: "mm/dd/yyyy",
        todayBtn: "linked",
        clearBtn: true,
        todayHighlight: true,
        templates: {
            leftArrow: '<i class="la la-angle-left"></i>',
            rightArrow: '<i class="la la-angle-right"></i>'
        }
    });
}

$('#audit-export-csv').on('click', function(){
	var dateTo = $('input[name="toActionDate"]').val();
	var dateFrom = $('input[name="fromActionDate"]').val();
	var dt = new Date();
	var time = (dt.getMonth()+1)+'_'+dt.getDate()+'_'+dt.getFullYear()+'_'+ dt.getHours()+'_'+ dt.getMinutes()+'_'+ dt.getSeconds()+'_'+dt.getMilliseconds();
	var name = "audit_trail_export_csv_"+time+'.csv';
	var obj = {
			path:name,
			startDate: dateFrom,
			endDate: dateTo
     };

	
	var jsonObj = jQuery.param(obj);
	$.ajax(
			{
				url: '/nera/api/export-by-time',
				type: 'POST',
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			    dataType: 'text',
		        data :  jsonObj,
			success: function(response) {
				downloadFileCSV(name);
	    	}, 
	    	error: function(response) {
	    		console.log(response);
	    	}
		});
});
function downloadFileCSV(name){
	$.ajax(
			{
				url: '/nera/upload/downloadFile/'+name,
				type: 'GET',
				xhrFields: {
		            responseType: 'blob'
		        },
			success: function(data) {
				 var a = document.createElement('a');
		            var url = window.URL.createObjectURL(data);
		            a.href = url;
		            a.download = name;
		            a.click();
		            window.URL.revokeObjectURL(url);
	    	}, 
	    	error: function(response) {
	    		console.log(response);
	    	}
		});
}
function convertDate(datestr){
	var arr = datestr.split('/');
	return arr[1]+'-'+arr[0]+'-'+arr[2];
}

// Class Initialization
jQuery(document).ready(function() {
	KTAuditLog.init();
	
	datatable.on('kt-datatable--on-init', function(){
    	fixedTableHeader();
    });
});

function filteredFromActionDateFunc(){
	filteredFromActionDate = $('input[name="fromActionDate"]').val();	
	datatable.setDataSourceParam("filteredFromActionDate", filteredFromActionDate);
}
function filteredToActionDateFunc(){
	filteredToActionDate = $('input[name="toActionDate"]').val();
	datatable.setDataSourceParam("filteredToActionDate", filteredToActionDate);
}

function doSearch() {

	filteredFromActionDateFunc();
	filteredToActionDateFunc();
	
	if (filteredFromActionDate !== '' && filteredToActionDate !== '') {
		var fromDate = moment(filteredFromActionDate, "DD/MM/YYYY");
		var toDate = moment(filteredToActionDate, "DD/MM/YYYY");
		
		if (new Date(fromDate) > new Date(toDate)) {
			$('#dateRangeError').css('display', 'block');
			return;
		} else {
			$('#dateRangeError').css('display', 'none');
		}
	}
	
//	searchText = $('#generalSearch').val();
//	datatable.setDataSourceParam("query[generalSearch]", searchText);
	
	datatable.reload();
}