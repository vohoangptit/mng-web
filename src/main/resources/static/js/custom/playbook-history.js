var datatable;
var isSendApproval;
$(document).ready(function() {
    var playbookId = $('#playbookId').val();
    $('#btnBackToCurrent').on('click', function () {
        if(isSendApproval) {
            window.location.href = '/menu/masterdata/playbook-approved/detail/'+playbookId;
        } else {
            window.location.href = '/menu/masterdata/playbook/detail/'+playbookId;
        }
    });
    datatable = $('#kt_datatable').KTDatatable({
        // datasource definition
        data: {
            type: 'remote',
            source: {
                read: {
                    method: 'GET',
                    url: '/nera/playbook/api/history-listing',
                    data: {id:playbookId},
                    map: function(raw) {
                        $('#txtWorkflowName').text(raw.name);
                        $('#downloadFilePB').text(raw.playbookFile);
                        $('#txtVersion').text(raw.version);
                        $('#txtUpdateBy').text(raw.updateBy);
                        $('#txtUpdateAt').text(raw.updateAt);
                        isSendApproval = raw.isSendApproval;
                        return raw.listPlaybookHistory;
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
                    let htmlRestore = '';
                    let htmlDetail = '<a href="/menu/masterdata/playbook/historydetail/'+ id +'" class="btn btn-hover-brand btn-icon btn-pill" data-id="' + id + '"><i class="la la-eye"></i></a>'
                    if (hasRestorePermission) {
                        htmlRestore = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill restorePlaybook" data-id="' + id + '"><i class="la la-history"></i></a>';
                    }
                    return htmlDetail + htmlRestore;
                },
            }],
    });

    $('#kt_datatable').on('click', '.restorePlaybook', function(){
        var id = $(this).attr('data-id');
        $.ajax({
            type: 'GET',
            url: '/nera/playbook/api/check-playbook',
            data: {historyId:id},
            success: function (data) {
                if (data.indexOf("Cannot restore the playbook ") >= 0) {
                    Swal.fire({
                        title: 'Restore Failed',
                        text: data,
                        type: 'error',
                        timer: 5000,
                        showConfirmButton: false,
                        animation: false,
                        customClass: {
                            popup: 'animated heartBeat'
                        }
                    });
                } else if (data === 'APPROVED') {
                    Swal.fire({
                        title: 'Are you sure?',
                        text: 'The current version is approved. Restore the previous version can need to ask for approval again. Continue?',
                        type: 'warning',
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
                            funcRestorePlaybook(id);
                        }
                    });
                } else {
                    Swal.fire({
                        title: 'Are you sure?',
                        text: 'You want to restore to the previous version?',
                        type: 'question',
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
                            funcRestorePlaybook(id);
                        }
                    });
                }
            }
        });
    });

    function funcRestorePlaybook(historyId) {
        $.ajax({
            type: 'POST',
            url: '/nera/playbook/api/restore-playbook',
            data: {historyId: historyId},
            success: function (data) {
                if(data) {
                    Swal.fire({
                        title: 'Restore Playbook',
                        text:  'Restore Playbook Successful',
                        type:  'success',
                        timer: 2000,
                        showConfirmButton: false,
                        animation: false,
                        customClass: {
                            popup: 'animated rubberBand'
                        }
                    });
                    datatable.load();
                } else {
                    Swal.fire({
                        text:  'Cannot restore playbook',
                        type:  'error',
                        timer: 2000,
                        showConfirmButton: false,
                        animation: false,
                        customClass: {
                            popup: 'animated heartBeat'
                        }
                    });
                }
            },
        });
    }

    $('.downloadFilePB').on('click', function(){
        window.open("/nera/api/downloadFile?filename=" + $('#downloadFilePB').text());
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

