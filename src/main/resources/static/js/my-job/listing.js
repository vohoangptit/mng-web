"use strict";

let datatable;
let jobInfo;
let dataSet;
let typeConfirm;

$(document).ready(function () {
    datatable = $('#kt_datatable').KTDatatable({
        // datasource definition
        data: {
            type: 'remote',
            source: {
                read: {
                    method: 'GET',
                    url: '/nera/my-job/api/listing-and-filter',
                    map: function (raw) {
                        dataSet = raw;
                        if (typeof raw.data !== 'undefined') {
                            dataSet = raw.data;
                            var total = raw.meta.total;
                            $('.totalNum').text(total);
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
        columns: [
            {
                field: 'no',
                title: 'S/N',
                sortable: false,
                width: 30,
                textAlign: 'left',
            },
            {
                field: 'jobName',
                title: 'Job Name',
                textAlign: 'left',
            },
            {
                title: 'Workflow Name',
                field: 'workflowName',
            },
            {
                title: 'Job Description',
                field: 'jobDescription',
            },
            {
                title: 'Planner',
                field: 'plannerName',
            },
            {
                title: 'Assignee',
                field: 'assigneeName',
            },
            {
                title: 'Execution Date',
                field: 'executionDate',
            },
            {
                title: 'Status',
                field: 'status',
                template: function (row) {
                    let status = row.status;
                    let htmlEdit;
                    if (status === "Pending for Acceptance") {
                        htmlEdit = '<label class="badge pending-status">PENDING</label>';
                    } else if (status === "Accepted") {
                        htmlEdit = '<label class="badge accept-status">'+status.toUpperCase()+'</label>';
                    }else if (status === 'Executing') {
                        htmlEdit = '<label class="badge executing-status">EXECUTING</label>';
                    }else if (status === 'Finished (Rejected)') {
                        htmlEdit = '<label class="badge finished-reject-job-status">FINISHED(R)</label>';
                    }else if (status === 'Finished (Approved)') {
                        htmlEdit = '<label class="badge finished-job-status">FINISHED(A)</label>';
                    }else if (status === 'Finished (Stopped)') {
                        htmlEdit = '<label class="badge finished-stop-status">FINISHED(S)</label>';
                    }else if (status === 'Finished (Failed)') {
                        htmlEdit = '<label class="badge finished-fail-status">FINISHED(F)</label>';
                    } else {
                        htmlEdit = '<label class="badge reject-status">'+status.toUpperCase()+'</label>';
                    }
                    return htmlEdit;
                },
            },
            {
                field: 'Actions',
                title: 'Actions',
                sortable: false,
                overflow: 'visible',
                textAlign: 'left',
                template: function (row) {
                    var id = row.id;
                    let html1 = '<a href="#" class="btn btn-hover-brand btn-icon btn-pill viewJob" data-id="' + id + '"><i class="la la-eye"></i></a>';
                    let html2 = '';
                    let html3 = '';
					let html4;
                    var months = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
                    var dateNow = new Date();
                    var today = dateNow.getFullYear()+'-'+months[dateNow.getMonth()]+'-'+("0" + dateNow.getDate()).slice(-2)+' '+("0" + dateNow.getHours()).slice(-2)+':'+("0" + dateNow.getMinutes()).slice(-2)+':00';
                    var dateExeFormat = row.executionDate.split('/')[2]+'-'+row.executionDate.split('/')[1]+'-'+row.executionDate.split('/')[0];

                    var dateTimeExeStart = dateExeFormat + " " + row.startTime + ":00";
                    var dateTimeExeEnd = dateExeFormat + " " + row.endTime + ":00";

                    var textHover = 'You can only start to execute the job at ' + dateTimeExeStart;
                    if(today >= dateTimeExeStart && today <= dateTimeExeEnd)
                    {
                        html4 = '<a href="#"  class="btn btn-icon btn-hover-danger btn-pill playJob" data-id="'+id+'" data-start="'+dateTimeExeStart +'" data-end="'+dateTimeExeEnd +'"><i class="la la-play"></i></a>';
                    }
                    else
                    {
                        html4 = '<a href="#" title="'+textHover+'" class="btn btn-hover-danger btn-icon btn-pill playJob disabled" data-id="' +id+'"  data-start="'+dateTimeExeStart +'" data-end="'+dateTimeExeEnd +'"><i class="la la-play"></i></a>';
                    }

                    if (hasAcceptRejectJobPermission) {
                        html2 = '<a href="#" class="btn btn-hover-success btn-icon btn-pill acceptJob" data-id="' + id + '"><i class="la la-check"></i></a>';
                        html3 = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill rejectJob" data-id="' + id + '"><i class="la la-times"></i></a>';
                    }

                    if (row.status === 'Accepted' || row.status === 'Finished (Failed)' || row.status === 'Finished (Stopped)') {
                        return html1+html4;
                    }
                    if(row.status === 'Executing' ||
                        row.status === 'Finished (Rejected)' ||
                        row.status ==='Finished (Approved)' ||
                        row.status === 'Rejected'
                    )
                    {
                        return html1;
                    }
                    if(row.status === 'Pending for Acceptance') {
                        return html1 + html2 + html3;
                    }
                },
            }],
    });
    loadStatus();
    $('#btnReset').unbind().click(function () {
        reset();
    });

    function reset() {
        $('.select2-selection__rendered').html('');
        $('#DateFrom').val('');
        $('#DateTo').val('');
        $('#generalSearch').val('');
        datatable.setDataSourceParam('status', null);
        loadStatus();
        datatable.setDataSourceParam('startDate', "");
        datatable.setDataSourceParam('endDate', "");
        datatable.search('');
    }

    loadDateTimePicker();

    $('#btnSearch').on('click', function () {
        doSearch();
    });
    $('#loader').hide();

    function doSearch() {
        datatable.setDataSourceParam('SearchString', $('#generalSearch').val());
        if ($('#statusSearch').val() == null) {
            datatable.setDataSourceParam('status', null);
        } else {
            datatable.setDataSourceParam('status', $('#statusSearch').val());
        }
        if ($('#DateFrom').val() !== '' && $('#DateTo').val() !== '') {
            let fromDate = moment($('#DateFrom').val(), "DD/MM/YYYY");
            let toDate = moment($('#DateTo').val(), "DD/MM/YYYY");

            if (new Date(fromDate) > new Date(toDate)) {
                $('#dateRangeError').removeClass('d-none');
                return;
            } else {
                $('#dateRangeError').addClass('d-none');
            }
        }
        datatable.setDataSourceParam('startDate', $('#DateFrom').val());
        datatable.setDataSourceParam('endDate', $('#DateTo').val());
        datatable.load();
    }

    $('#btnConfirm').on('click', function () {
        let id = $('#idJobConfirm').val() == '' ? jobInfo.id : $('#idJobConfirm').val();
        let reason = $('#txtReason').val();
        if (reason === '' && !typeConfirm) {
            $('#error_reason').removeClass('d-none');
        } else {
            $('#error_reason').addClass('d-none');
            updateStatusJob(id);
        }

    });
    $('#kt_datatable').on('click', '.acceptJob', function () {
        $('#idJobConfirm').val($(this).attr('data-id'));
        typeConfirm = true;
        $('#pConfirm').text('Are you sure you want to accept the selected job?');
        $('#txt_view_reason').addClass('d-none');
        $('#txtReason').val('');
        $('#kt_modal_accept_reject').modal('show');
    });

    $('#kt_datatable').on('click', '.rejectJob', function () {
        $('#error_reason').addClass('d-none');
        $('#txt_view_reason').removeClass('d-none');
        $('#idJobConfirm').val($(this).attr('data-id'));
        typeConfirm = false;
        $('#pConfirm').text('Are you sure you want to reject the selected job?');
        $('#txtReason').val('');
        $('#kt_modal_accept_reject').modal('show');
    });

    $('#kt_datatable').on('click', '.viewJob', function () {
        showViewJobPopup($(this).attr('data-id'));
    });
	$('#kt_datatable').on('click', '.playJob', function () {
        var startDate = $(this).attr('data-start');
        var endDate = $(this).attr('data-end');
        var months = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
        var dateNow = new Date();
        var today = dateNow.getFullYear()+'-'+months[dateNow.getMonth()]+'-'+("0" + dateNow.getDate()).slice(-2)+' '+("0" + dateNow.getHours()).slice(-2)+':'+("0" + dateNow.getMinutes()).slice(-2)+':00';
        if(today >= startDate && today <= endDate) {
            showPlayJobPopup($(this).attr('data-id'));
        } else {
            var textHover = 'You can only start to execute the job at ' + startDate + ' - ' + endDate;
            Swal.fire({
                text:  textHover,
                type:  'error',
                timer: 5000,
                showConfirmButton: false
            });
        }

    });
    datatable.on('kt-datatable--on-init', function () {
        fixedTableHeader();
    });
    $('#btnAcceptJob').on('click', function () {
        typeConfirm = true;
        $('#pConfirm').text('Are you sure you want to accept the selected job?');
        $('#idJobConfirm').val('');
        $('#txt_view_reason').addClass('d-none');
        $('#txtReason').val('');
        $('#kt_modal_accept_reject').modal('show');
    });
    $('#btnRejectJob').on('click', function () {
        typeConfirm = false;
        $('#pConfirm').text('Are you sure you want to reject the selected job?');
        $('#idJobConfirm').val('');
        $('#txt_view_reason').removeClass('d-none');
        $('#error_reason').addClass('d-none');
        $('#txtReason').val('');
        $('#kt_modal_accept_reject').modal('show');
    });
	$('#btnPLayJob').on('click', function () {
        // typeConfirm = true;
        // $('#pConfirm').text('Are you sure you want to play the selected job?');
        // $('#idJobConfirm').val('');
        // $('#kt_modal_accept_reject').modal('show');
        window.location.href = "../my-job/execution?jobid="+$('#rowId').val()+"&workflowid="+$('#workflowIdforExe').val();
    });
	$('#btnCancelJob').on('click', function () {
        $('#kt_modal_edit').modal('hide');
    });
});

function loadDateTimePicker() {
    $('.m_datepicker').datepicker({
        format: "dd/mm/yyyy",
        todayBtn: "linked",
        clearBtn: true,
        todayHighlight: true,
        templates: {
            leftArrow: '<i class="la la-angle-left"></i>',
            rightArrow: '<i class="la la-angle-right"></i>'
        }
    });
}

function showLoading() {
    $('.lds-spinner').removeClass('d-none');
}

function hideLoading() {
    $('.lds-spinner').addClass('d-none');
}

function showViewJobPopup(id) {
    $('#validateData').addClass('hide-class');
    jobInfo = grep(id)[0];
    getImageWorkflow(jobInfo.workflowName);
    $('#descriptionJob').text(jobInfo.jobDescription);
    $('#workflowName').text(jobInfo.workflowName);
    $('#jobAssignStatus').text(jobInfo.status);
    $('#DateExec').text(jobInfo.executionDate);
    $('#TimeExec').text(jobInfo.startTime + ' - ' + jobInfo.endTime);
    $('#plannerName').text(jobInfo.plannerName);
    $('#assigneeName').text(jobInfo.assigneeName);
    $('#rowId').val(id);
    $('#workflowIdforExe').val(jobInfo.workflowId);
    $('#jobNameLbl').text(jobInfo.jobName);
    if (jobInfo.status === 'Pending for Acceptance') {
        $('#parent-action').removeClass('hide-class');
        $('#btnRejectJob').removeClass('hide-class');
        $('#btnAcceptJob').removeClass('hide-class');
        $('#btnPLayJob').addClass('hide-class');
    } else if(jobInfo.status === 'Accepted' || jobInfo.status === 'Finished (Stopped)' || jobInfo.status === 'Finished (Failed)') {
        $('#parent-action').removeClass('hide-class');
        $('#btnPLayJob').removeClass('hide-class');
        $('#btnRejectJob').addClass('hide-class');
        $('#btnAcceptJob').addClass('hide-class');
    } else {
        $('#parent-action').addClass('hide-class');
    }
    $('#kt_modal_edit').modal('show');
}

function getImageWorkflow(nameWorkflow) {
    $('.contentWorkflowDetail').empty();
    $.ajax({
        type: 'GET',
        url: '/nera/api/get-image-workflow-by-name',
        data: {name: nameWorkflow},
        success: function (data) {
            if(data != '') {
                $('.contentWorkflowDetail').append('');
                $('.contentWorkflowDetail').append('<img src="data:image/png;base64, '+data+'">');
            }
        },
    });
}
function showPlayJobPopup(id) {
    $('#parent-action').removeClass('hide-class');
    jobInfo = grep(id)[0];
    getImageWorkflow(jobInfo.workflowName);
    $('#descriptionJob').text(jobInfo.jobDescription);
    $('#workflowName').text(jobInfo.workflowName);
    $('#jobAssignStatus').text(jobInfo.status);
    $('#DateExec').text(jobInfo.executionDate);
    $('#TimeExec').text(jobInfo.startTime + ' - ' + jobInfo.endTime);
    $('#plannerName').text(jobInfo.plannerName);
    $('#assigneeName').text(jobInfo.assigneeName);
    $('#rowId').val(id);
    $('#workflowIdforExe').val(jobInfo.workflowId);
    $('#jobNameLbl').text(jobInfo.jobName);
    $('#btnRejectJob').hide();
    $('#btnAcceptJob').hide();
    $('#kt_modal_edit').modal('show');
}

function grep(id) {
    return jQuery.grep(dataSet, function (obj) {
        return obj.id == id;
    });
}
function updateStatusJob(id) {
    let reason = $('#txtReason').val();
    $.ajax({
        type: 'POST',
        url: '/nera/my-job/api/accept-or-reject-job',        data: {id: id, status: typeConfirm, reason: reason},
        beforeSend: function () {
            $('#loader').show();
            $('#kt_modal_accept_reject').modal('toggle');
        },
        complete: function () {
            $('#loader').hide();
        },
        success: function (data) {
            if ($('#idJobConfirm').val() == '') {
                $('#kt_modal_edit').modal('toggle');
            }
            Swal.fire({
                text: "Successfully",
                type: 'success',
                timer: 2000,
                showConfirmButton: false
            });
            datatable.reload();
        },
        error: function () {
            if ($('#idJobConfirm').val() == '') {
                $('#kt_modal_edit').modal('toggle');
            }
            Swal.fire({
                text: "Failed",
                type: 'error',
                timer: 2000,
                showConfirmButton: false
            });
        }
    });
}
function loadStatus() {
    $('#statusSearch')
        .find('option')
        .remove()
        .end();
    $.ajax({
        type: 'GET',
        url: '/nera/job-planning/api/get-job-status',
        success: function (data) {
            var re = data;
            for (var i = 0; i < re.length; i++) {
                var op = new Option(re[i], re[i]);
                $("#statusSearch").append(op);
            }
            $("#statusSearch").val(null);
            $("#statusSearch").select2({
                placeholder: "Status (All)",
                allowClear: false,
                width: 'resolve',
                dropdownAutoWidth: true
            });
        },
    });
}