let jobExecuteId;
let type;
$(document).ready(function () {
     $('#kt_dttb_assignment').KTDatatable({
        data: {
            type: 'remote',
            source: {
                read: {
                    method: 'GET',
                    url: '/nera/dashboard/api/list-job-planner',
                    map: function(raw) {
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
                webStorage: false
            },
            serverPaging: false,
            serverFiltering: false,
            serverSorting: false
        },

        // layout definition
        layout: {
            scroll: true,
            height: 382,
            footer: false
        },

        selector: {
            class: 'kt-checkbox--solid'
        },

        // column sorting
        sortable: true,
        pagination: false,

        // columns definition
        columns: [
            {
                field: 'jobName',
                title: 'Job Name',
                width: 80
            },
            {
                field: 'workflowName',
                title: 'Workflow Name',
            },
            {
                field: 'executionTime',
                title: 'Execution Time',
                width: 120
            },
            {
                field: 'assignTime',
                title: 'Job Assignment Date',
                width: 110
            },
            {
                field: 'status',
                title: 'Status',
                textAlign: 'left',
            }],
    });

    statisticPlaybookByStatus();
    statisticJobByStatus();
    getJobExecToDo(1);
    //getJobByPlanner();
    $('#up_coming_task_tbl').on('click', '.exec-job', function () {
        let jobId = $(this).attr('data-id');
        let workflowId = $(this).attr('exec-id');
        window.location.href = "../menu/my-job/execution?jobid="+jobId+"&workflowid="+workflowId;
    });

    let $fixedHeader = $("#header-fixed");

    fixedTableHeader();

    function fixedTableHeader(){
        $fixedHeader.empty();
        let $header = $("#job_assign_tbl > thead").clone();
        $("#job_assign_tbl > thead th").each(function(index){
            let width = $(this).outerWidth();
            $header.find('th').eq(index).css('width', width);
        });

        $fixedHeader.append($header).css('width', $("#job_assign_tbl > thead").outerWidth());
    }

    $(window).resize(function(){
        fixedTableHeader();
    });

    $('#up_coming_task_tbl').on('click', '.approve-job', function () {
        jobExecuteId = $(this).attr("data-id");
        type = 'Approve';
        $('.confirm-message').text('Do you want to Approve the job?');
        $('#kt_approve_reject_job').modal('show');
    });
    $('#up_coming_task_tbl').on('click', '.reject-job', function () {
        jobExecuteId = $(this).attr("data-id");
        type = 'Reject';
        $('.confirm-message').text('Do you want to Reject the job?');
        $('#kt_approve_reject_job').modal('show');
    });
    $('#btnYes').on('click', function(){
        let id = jobExecuteId;
        let actionType = type;
        $.ajax({
            type: 'POST',
            url: '/nera/my-job-approval-request/api/reject-approve-job',
            data: {
                id: id,
                actionType: actionType
            },
            success: function (data) {
                if(data) {
                    Swal.fire({
                        title: actionType + ' Job',
                        text:  actionType + " Job Successful",
                        type:  'success',
                        timer: 2000,
                        showConfirmButton: false
                    });
                    $('[data-id='+id+']').parents('tr').remove();
                } else {
                    Swal.fire({
                        text:  'Cannot ' + actionType +' Job',
                        type:  'error',
                        timer: 2000,
                        showConfirmButton: false
                    });
                }
                $('#kt_approve_reject_job').modal('hide');
            },
            error: function (err) {
                //
            },
        });
    });
});

function appendJobComing(datetime, operation, jobName, id, workflowId){
    let execAction = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill exec-job" data-id="' + id + '" exec-id="' + workflowId + '"><i class="la la-2x la-play"></i></a>';
    let htmlUpdate = '<a href="#"  class="btn btn-hover-brand btn-icon btn-pill approve-job" data-id="' + id + '"><i class="la la-check"></i></a>';
    let htmlDelete = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill reject-job" data-id="' + id + '"><i class="la la-close"></i></a>';
    let approveAction = htmlUpdate + htmlDelete;
    let action = (operation === "Execute Job" ? execAction: approveAction);
    $('#up_coming_task_tbl > tbody:last').append('<tr>'
        + '<td>' + datetime + '</td>'
        + '<td>' + jobName + '</td>'
        + '<td>' + operation + '</td>'
        + '<td>' + action + '</td>'
        + '</tr>');
}
function appendJobByPlanner(a, b, c, d, e){
    $('#job_assign_tbl > tbody:last').append('<tr>'
        + '<td>' + a + '</td>'
        + '<td>' + b + '</td>'
        + '<td>' + c + '</td>'
        + '<td>' + d + '</td>'
        + '<td>' + e + '</td>'
        + '</tr>');
}
function statisticJobByStatus() {
    $.ajax({
        type: 'GET',
        url: '/nera/dashboard/api/statistic-job-by-status',
        success: function (data) {
            $('#job_pending_status').text(data["Pending for Acceptance"]);
            $('#job_accept_status').text(data["Accepted"]);
            $('#job_reject_status').text(data["Rejected"]);
            $('#job_exec_status').text(data["Executing"]);
            $('#job_complete_status').text(data["Finished (Approved)"] + data["Finished (Rejected)"]);
            $('#job_stop_status').text(data["Finished (Stopped)"]);
            $('#job_fail_status').text(data["Finished (Failed)"]);
        }
    })
}

function statisticPlaybookByStatus() {
    $.ajax({
        type: 'GET',
        url: '/nera/dashboard/api/statistic-playbook-by-status',
        success: function (data) {
            $('#new_status').text(data[1]);
            $('#pending_status').text(data[4]);
            $('#approved_status').text(data[2]);
            $('#reject_status').text(data[3]);
        }
    })
}

function getJobExecToDo(types) {
    $.ajax({
        type: 'GET',
        url: '/nera/dashboard/api/get-job-exec-to-do?type='+types,
        success: function (data) {
            let raw = data;
            for (let i = 0; i < raw.length; i++) {
                appendJobComing(raw[i].dateExec, raw[i].operation, raw[i].jobName, raw[i].id, raw[i].workflowId);
            }
        }
    })
}

function getJobByPlanner() {
    $.ajax({
        type: 'GET',
        url: '/nera/dashboard/api/list-job-planner',
        success: function (data) {
            let raw = data;
            for (let i = 0; i < raw.length; i++) {
                appendJobByPlanner(raw[i].jobName, raw[i].workflowName, raw[i].executionTime, raw[i].assignTime, raw[i].status);
            }
        }
    })
}

function getJobExecToDoToday() {
    $('#up_coming_task_tbl tbody tr').remove();
    getJobExecToDo(1);
}
function getJobExecToDoOnWeek() {
    $('#up_coming_task_tbl tbody tr').remove();
    getJobExecToDo(2);
}
function getJobExecToDoOnMonth() {
    $('#up_coming_task_tbl tbody tr').remove();
    getJobExecToDo(3);
}

