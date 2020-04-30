"use strict";

let datatable;
let jobInfo;
let dataSet;
let checkUpdateOrCreate;

$(document).ready(function () {
    datatable = $('#kt_datatable').KTDatatable({
        // datasource definition
        data: {
            type: 'remote',
            source: {
                read: {
                    method: 'GET',
                    url: '/nera/job-planning/api/listing-and-filter',
                    map: function (raw) {
                        dataSet = raw;
                        if (typeof raw.data !== 'undefined') {
                            dataSet = raw.data;
                            let total = raw.meta.total;
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

        // columns definition
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
                sortable: true,
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
                marginLeft: '20%',
                template: function (row) {
                    let html1 = '';
                    let html2 = '';
                    if (hasAssignJobPermission) {
                        let id = row.id;
                        html1 = '<a href="#"  class="btn btn-hover-brand btn-icon btn-pill editJob" data-id="' + id + '"><i class="la la-edit"></i></a>';
                        html2 = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill deleteJob" data-id="' + id + '"><i class="la la-trash"></i></a>';
                    }
                    return html1 + html2;
                },
            }],
    });
    loadStatus();
    $('#btnReset').unbind().click(function () {
        reset();
    });
    $('#loader').hide();
    loaddatetimepicker();

    function loaddatetimepicker() {
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

    let timePicker = new TimePicker(['startTime', 'endTime'], {
        lang: 'en',
    });
    timePicker.on('change', function (evt) {
        evt.element.value = (evt.hour || '00') + ':' + (evt.minute || '00');
        if (evt.element.id === 'startTime') {
            let end;
            if (parseInt(evt.hour) * 60 + parseInt(evt.minute || 0) + 120 > 1440) {
                end = '24:00';
            } else {
                end = (parseInt(evt.hour) + 2 || '00') + ":" + (evt.minute || '00');
            }
            $('#validateData').addClass('hide-class');
            $('#endTime').val(end);
        } else {
            let minute = (evt.minute || '00');
            if((getHour($('#startTime').val()) === parseInt(evt.hour) && getMinute($('#startTime').val()) >= parseInt(minute)) || getHour($('#startTime').val()) > parseInt(evt.hour)) {
                $('#lblError').text('The end time must be greater than the start time.');
                $('#validateData').removeClass('hide-class');
            } else {
                $('#validateData').addClass('hide-class');
            }
        }
    });
    $('._jw-tpk-container').css('height', 'auto');
    $('#btnSearch').on('click', function () {
        doSearch();
    });

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
                $('#dateRangeError').css('display', 'block');
                return;
            } else {
                $('#dateRangeError').css('display', 'none');
            }
        }
        datatable.setDataSourceParam('startDate', $('#DateFrom').val());
        datatable.setDataSourceParam('endDate', $('#DateTo').val());
        datatable.load();
    }

    $('#plannerSelect').change(function () {
        $("#plannerSelect option:selected").val();
    });
    $('#selectJobId').change(function () {
        let val = $("#selectJobId option:selected").val();
        getJobById(val);
    });

    $('#btnYesDelete').on('click', function () {
        let id = $('#deleteId').val();
        callDeleteApi(id);
    });
    $('#btnDetailDelete').on('click', function () {
        $('#kt_modal_edit').modal('toggle');
        $('#kt_modal_Delete').modal('show');
        $('#deleteId').val(jobInfo.id);
    });
    $('#kt_datatable').on('click', '.deleteJob', function () {
        $('#kt_modal_Delete').modal('show');
        let id = $(this).attr('data-id');
        $('#deleteId').val(id);
    });

    $('#btnConfirm').on('click', function () {
        if (checkUpdateOrCreate) {
            updateJobPlanning(jobInfo.id);
        } else {
            createJobPlanning();
        }
    });
    $('#kt_datatable').on('click', '.editJob', function () {
        showUpdatePopup($(this).attr('data-id'));
    });
    datatable.on('kt-datatable--on-init', function () {
        fixedTableHeader();
    });
});

function reset() {
    $('#dateRangeError').css('display', 'none');
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

function callDeleteApi(id) {
    $.ajax({
        type: 'POST',
        url: '/nera/job-planning/api/delete-job-by-id',
        data: {id: id},
        beforeSend: function () {
            //
        },
        success: function () {
            $('#kt_modal_Delete').modal('hide');
            Swal.fire({
                text:  "Successfully",
                type:  'success',
                timer: 2000,
                showConfirmButton: false
            });
            datatable.load();
        },
        error: function () {
            Swal.fire({
                text:  "Failed",
                type:  'error',
                timer: 2000,
                showConfirmButton: false
            });
        },
    });
}

function showUpdatePopup(id) {
    $('#validateData').addClass('hide-class');
    checkUpdateOrCreate = true;
    jobInfo = grep(id)[0];
    if (jobInfo.status !== 'Rejected') {
        $('#job_description').text(jobInfo.jobDescription);
        $('#workflow_name').text(jobInfo.workflowName);
        $('#job_assign_status').text(jobInfo.status);
        $('#date_exec').text(jobInfo.executionDate);
        $('#time_exec').text(jobInfo.startTime + ' - ' + jobInfo.endTime);
        $('#planner_name').text(jobInfo.plannerName);
        $('#assignee_name').text(jobInfo.assigneeName);
        $('#job_name').text(jobInfo.jobName);
        getImageWorkflowJob(jobInfo.workflowName);
        $('#kt_modal_view').modal('show');
    } else {
        $('#jobName').val(jobInfo.jobName);
        $('#descriptionJob').text(jobInfo.jobDescription);
        $('#workflowName').text(jobInfo.workflowName);
        $('#jobAssignStatus').text(jobInfo.status);
        $('#DateExec').val(jobInfo.executionDate);
        $('#startTime').val(jobInfo.startTime);
        $('#endTime').val(jobInfo.endTime);
        getImageWorkflowJob(jobInfo.workflowName);
        loadPlanner();
        loadAssignee();
        loadJob();
        $('#kt_modal_edit').modal('show');
    }
}

function getImageWorkflowJob(nameWorkflow) {
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

function grep(id) {
    return jQuery.grep(dataSet, function (obj, index) {
        return obj.id == id;
    });
}

function autoCreateDateJob() {
    let date = new Date();
    date.setDate(new Date().getDate() + 3);
    return moment(date).format("DD/MM/YYYY")
}
function  validateExecutionDate(date) {
    let dateE = stringToDate(date,"dd/MM/yyyy","/");
    let dateNow = new Date();
    dateNow.setDate(dateNow.getDate() + 2);
    return dateE >= dateNow;
}
function stringToDate(_date,_format,_delimiter)
{
    let formatLowerCase=_format.toLowerCase();
    let formatItems=formatLowerCase.split(_delimiter);
    let dateItems=_date.split(_delimiter);
    let monthIndex=formatItems.indexOf("mm");
    let dayIndex=formatItems.indexOf("dd");
    let yearIndex=formatItems.indexOf("yyyy");
    let month=parseInt(dateItems[monthIndex]);
    month-=1;
    return new Date(dateItems[yearIndex], month, dateItems[dayIndex]);
}
function showAddPopup() {
    $('#validateData').addClass('hide-class');
    $('#DateExec').val(autoCreateDateJob());
    $('#startTime').val('8:00');
    $('#endTime').val('10:00');
    checkUpdateOrCreate = false;
    $('#descriptionJob').text('');
    $('#workflowName').text('');
    $('#jobAssignStatus').text('');
    $('.contentWorkflowDetail').empty();
    loadPlanner();
    loadAssignee();
    loadJob();
    $('#kt_modal_edit').modal('show');
}

function loadPlannerDefault() {
    $.ajax({
        type: 'GET',
        url: '/nera/my-job/api/get-user-default',
        success: function (data) {
            $("#plannerSelect").val(data.id).trigger('change');
        },
    });
}

function loadPlanner() {
    $('#plannerSelect')
        .find('option')
        .remove()
        .end();
    $.ajax({
        type: 'GET',
        url: '/nera/job-planning/api/get-planner',
        success: function (data) {
            let re = data;
            for (let i = 0; i < re.length; i++) {
                let op = new Option(re[i].fullName, re[i].id);
                $("#plannerSelect").append(op);
            }
            if (checkUpdateOrCreate) {
                $("#plannerSelect").val(jobInfo.plannerId).trigger('change');
            } else {
                loadPlannerDefault();
            }
            $('#plannerSelect').select2({
                placeholder: "Select Planner",
                allowClear: false,
                width: 'resolve',
                dropdownAutoWidth: true
            });
        },
    });
}

function loadAssignee() {
    $('#assigneeSelect')
        .find('option')
        .remove()
        .end();
    $.ajax({
        type: 'GET',
        url: '/nera/job-planning/api/get-assignee',
        success: function (data) {
            let re = data;
            for (let i = 0; i < re.length; i++) {
                let op = new Option(re[i].fullName, re[i].id);
                $("#assigneeSelect").append(op);
            }
            if (checkUpdateOrCreate) {
                $("#assigneeSelect").val(jobInfo.assigneeId).trigger('change');
            } else {
                $("#assigneeSelect").val(null).trigger('change');
            }
            $('#assigneeSelect').select2({
                placeholder: "Select Assignee",
                allowClear: false,
                width: 'resolve',
                dropdownAutoWidth: true
            });
        },
    });
}

function getJobById(id) {
    $.ajax({
        type: 'GET',
        url: '/nera/job-planning/api/get-job-view-by-id?id=' + id,
        success: function (data) {
            if (data.id !== 0) {
                if (data.workflowName === null) {
                    $('#lblError').text('Job must be at least 1 Workflow, please choose another Job.');
                    $('#validateData').removeClass('hide-class');
                    $('#descriptionJob').text('');
                    $('#workflowName').text('');
                } else {
                    $('#validateData').addClass('hide-class');
                    $('#descriptionJob').text(data.description);
                    $('#workflowName').text(data.workflowName);
                    getImageWorkflowJob(data.workflowName);
                }
            } else {
                $('#descriptionJob').text('');
                $('#workflowName').text('');
            }

        },
    });
}

function CreateOrUpdate() {
    if ($("#selectJobId option:selected").val() === null || $("#workflowName").text() === ''
        || $("#assigneeSelect option:selected").val() == null || $("#plannerSelect option:selected").val() === null
        || $('#DateExec').val() === '' || $('#startTime').val() === '' || $('#endTime').val() === '') {
        $('#lblError').text('Field contain (*) cannot empty, please type again');
        $('#validateData').removeClass('hide-class');
    }
    // else if(!validateExecutionDate($('#DateExec').val())){
    //     $('#lblError').text('The earliest execution date must be 3 days from today');
    //     $('#validateData').removeClass('hide-class');
    // }
    else if(getHour($('#startTime').val()) === getHour($('#endTime').val()) && getMinute($('#startTime').val()) >= getMinute($('#endTime').val())
                || getHour($('#startTime').val()) > getHour($('#endTime').val())) {
        $('#lblError').text('The end time must be greater than the start time.');
        $('#validateData').removeClass('hide-class');
    } else {
        $('#validateData').addClass('hide-class');
        showConfirmPopup();
    }
}

function showConfirmPopup() {
    if (checkUpdateOrCreate) {
        $('#txtConfirm').text('Are you sure you want to update a job assignment?');
    } else {
        $('#txtConfirm').text('Are you sure you want to create a new job assignment?');
    }
    $('#kt_modal_confirm').modal('show');
}

function updateJobPlanning(id) {
    let bodyUpdate = {
        id: id,
        plannerId: $("#plannerSelect option:selected").val(),
        assigneeId: $("#assigneeSelect option:selected").val(),
        jobId: $("#selectJobId option:selected").val(),
        jobDescription: $('#descriptionJob').text(),
        startTime: validateHour($('#startTime').val()),
        endTime: validateHour($('#endTime').val()),
        executionDate: $('#DateExec').val(),
    };
    $.ajax({
        type: 'POST',
        url: '/nera/job-planning/api/update-job-planning',
        data: JSON.stringify(bodyUpdate),
        contentType: "application/json",
        beforeSend: function () {
            $('#kt_modal_confirm').modal('toggle');
            $('#loader').show();
        },
        complete: function () {
            $('#loader').hide();
        },
        success: function (data) {
            if (data.code === 200) {
                $('#kt_modal_edit').modal('toggle');
                Swal.fire({
                    text:  "Successfully",
                    type:  'success',
                    timer: 2000,
                    showConfirmButton: false
                });
                datatable.reload();
            } else {
                $('#kt_modal_edit').modal('toggle');
                Swal.fire({
                    text:  "Failed",
                    type:  'error',
                    timer: 2000,
                    showConfirmButton: false
                });
            }
        },
    });
}
function createJobPlanning() {
    let body = {
        plannerId: $("#plannerSelect option:selected").val(),
        assigneeId: $("#assigneeSelect option:selected").val(),
        jobId: $("#selectJobId option:selected").val(),
        jobDescription: $('#descriptionJob').text(),
        startTime: validateHour($('#startTime').val()),
        endTime: validateHour($('#endTime').val()),
        executionDate: $('#DateExec').val(),
    };

    $.ajax({
        type: 'POST',
        url: '/nera/job-planning/api/create-job-planning',
        data: JSON.stringify(body),
        contentType: "application/json",
        beforeSend: function () {
            $('#kt_modal_confirm').modal('toggle');
            $('#loader').show();
        },
        complete: function () {
            $('#loader').hide();
        },
        success: function (data) {
            if (data.code === 200) {
                $('#kt_modal_edit').modal('toggle');
                Swal.fire({
                    text:  "Successfully",
                    type:  'success',
                    timer: 2000,
                    showConfirmButton: false
                });
                datatable.reload();
            } else {
                $('#kt_modal_edit').modal('toggle');
                Swal.fire({
                    text:  "Failed",
                    type:  'error',
                    timer: 2000,
                    showConfirmButton: false
                });
            }
        },
    });
}

function loadJob() {
    $('#selectJobId')
        .find('option')
        .remove()
        .end();
    $.ajax({
        type: 'GET',
        url: '/nera/job-planning/api/get-job-management',
        success: function (data) {
            let re = data;
            for (let i = 0; i < re.length; i++) {
                let op = new Option(re[i].name, re[i].id);
                $("#selectJobId").append(op);
            }
            if (checkUpdateOrCreate) {
                $("#selectJobId").val(jobInfo.jobId).trigger('change');

            } else {
                $("#selectJobId").val(null).trigger('change');
            }
            $('#selectJobId').select2({
                placeholder: "Select Job",
                allowClear: false,
                width: 'resolve',
                dropdownAutoWidth: true
            });
        },
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
            let re = data;
            for (let i = 0; i < re.length; i++) {
                let op = new Option(re[i], re[i]);
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

function getHour(time) {
    return parseInt(time.split(":")[0]);
}

function getMinute(time) {
    return parseInt(time.split(":")[1]);
}
function validateHour(time) {
    if(getLengthHour(time) < 2){
        return "0" + time;
    }
    return time;
}
function getLengthHour(time) {
    return time.split(":")[0].length;
}