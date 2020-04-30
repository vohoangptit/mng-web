
var datatable;
$(document).ready(function() {
    $('#job-management-menu-left').removeClass('kt-menu__item--open');
    var idWorkflow = $('#idWorkflow').val();
    $('#btnBackToCurrent').on('click', function () {
        window.location.href = '/drawing/'+idWorkflow;
    });
    datatable = $('#kt_datatable').KTDatatable({
        // datasource definition
        data: {
            type: 'remote',
            source: {
                read: {
                    method: 'GET',
                    url: '/nera/api/workflow/history-listing',
                    data: {id:idWorkflow},
                    map: function(raw) {
                        $('#txtWorkflowName').text(raw.name);
                        $('#txtDescription').text(raw.description);
                        $('#txtVersion').text(raw.version);
                        $('#txtUpdateBy').text(raw.updateBy);
                        $('#txtUpdateAt').text(raw.updateAt);
                        return raw.listWorkflow;
                    },
                },
            },
            pageSize: 10,
            saveState: {
                cookie: false,
                webstorage: false
            },
            serverPaging: false,
            serverFiltering: false,
            serverSorting: false
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
        sortable: false,
        pagination: false,
        noRecords: 'No records found',
        search: {
            input: $('#generalSearch')
        },

        // columns definition
        columns: [
            {
                field: 'no',
                title: 'S/N',
                width: 30,
                textAlign: 'left'
            },
            {
                field: 'version',
                title: 'Version',
                template: function(row) {
                    return "Version " + row.version;
                }
            },
            {
                field: 'updateAt',
                title: 'Updated at'
            },
            {
                field: 'updateBy',
                title: 'Update by'
            },
            {
                field: 'Actions',
                title: 'Actions',
                sortable: false,
                overflow: 'visible',
                textAlign: 'center',
                template: function(row) {
                    var id = row.id;
                    let htmlViewDetail = '<a href="/history/workflow/'+id+'" class="btn btn-hover-brand btn-icon btn-pill"><i class="la la-eye"></i></a>'
                    let htmlRestore = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill restoreWorkflow" data-id="' + id + '"><i class="la la-history"></i></a>';
                    return htmlViewDetail + htmlRestore;
                },
            }],
    });

    $('#kt_datatable').on('click', '.restoreWorkflow', function(){
        let id = $(this).attr('data-id');
        // $('#historyId').val(id);
        $.ajax({
            type: 'GET',
            url: '/nera/api/workflow/check-workflow',
            data: {historyId: id},
            success: function (data) {
                if(data !== '') {
                    Swal.fire({
                        text:  data,
                        type:  'error',
                        timer: 5000,
                        showConfirmButton: false
                    });
                } else {
                    Swal.fire({
                        title: 'Are you sure?',
                        text: 'You want to restore to the previous version?',
                        type: 'info',
                        showCancelButton: true,
                        confirmButtonColor: '#5d78ff',
                        cancelButtonColor: '#ea6861',
                        confirmButtonText: 'Yes, restore it!',
                        animation: false,
                        customClass: {
                            popup: 'animated rubberBand'
                        }
                    }).then(function(result) {
                        if (result.value) {
                            execute(id);
                    }
                });
                }
            }
        });
    });

    




    if (typeof datatable !== 'undefined') {
        datatable.on('kt-datatable--on-init', function(){
            fixedTableHeaders();
        });
    }
    var $fixedHeader = $("#header-fixed");
    function fixedTableHeaders(){
    	if(!$fixedHeader.length){
        	$fixedHeader = $("#header-fixed");
        }
    	
    	$fixedHeader.empty();
        var $header = $(".kt-datatable__table > thead").clone();
        $(".kt-datatable__table > thead th").each(function(index){
        	var width = $(this).outerWidth();
        	$header.find('th').eq(index).css('width', width);
        });
        $fixedHeader.append($header).css('width', $(".kt-datatable__table > thead").outerWidth());
    }
    $(window).resize(function(){
    	fixedTableHeaders();
    });
    $(window).bind("scroll", function() {
        if (typeof $(".kt-datatable__table").offset() !== 'undefined') {
            var offset = $(this).scrollTop() + 65;
            var tableOffset = $(".kt-datatable__table").offset().top;

            if(!$fixedHeader.length){
                $fixedHeader = $("#header-fixed");
            }

            if (offset >= tableOffset && $fixedHeader.is(":hidden")) {
                $fixedHeader.show();
            }
            else if (offset < tableOffset) {
                $fixedHeader.hide();
            }
        }
    });
});

function execute(id) {
    $.ajax({
        type: 'POST',
        url: '/nera/api/workflow/restore-workflow',
        data: {historyId: id},
        success: function (data) {
            if(data) {
                Swal.fire({
                    title: 'Restore workflow',
                    text:  'Restore workflow successful',
                    type:  'success',
                    timer: 2000,
                    showConfirmButton: false
                });
                datatable.load();
            } else {
                Swal.fire({
                    text:  'Cannot restore workflow',
                    type:  'error',
                    timer: 2000,
                    showConfirmButton: false
                });
            }
        },
    });
}