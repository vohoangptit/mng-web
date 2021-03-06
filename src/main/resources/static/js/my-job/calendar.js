"use strict";

let idUpdate;
let eventUpdate;
let statusUpdate;
$(document).ready(function () {
    $('#calendar').fullCalendar({
        timeFormat: 'HH:mm',
        events: '/nera/my-job/api/get-job-calendar-view',
        editable: true,
        selectable: false,
        selectHelper: true,
        allDay: true,
        customButtons: {
            pending: {
                text: 'Pending for Acceptance'
            },
            accept: {
                text: 'Accepted'
            },
            reject: {
                text: 'Rejected'
            },
            executing: {
                text: 'Executing'
            },
            finishedapprove: {
                text: 'Finished(A)'
            },
            finishedreject: {
                text: 'Finished(R)'
            },
            finishFailed: {
                text: 'Finished(F)'
            },
            finishStopped: {
                text: 'Finished(S)'
            }
        },
        header: {
            left: 'prev,next today, pending, accept, reject, executing, finishedapprove, finishedreject, finishStopped, finishFailed',
            center: 'title',
            right: ''
        },
        eventClick: function (date, jsEvent) {
            openUpdateEvent(date.id);
            eventUpdate = jsEvent;
        },
    });
    renderNoteAndButtonAdd();
    $('#selectJobId').change(function () {
        let val = $("#selectJobId option:selected").val();
        getJobById(val);
    });
    loadDateTimeTicker();
    $('#btnAcceptJob').on('click', function () {
        statusUpdate = true;
        $('#pConfirm').text('Are you sure you want to accept the selected job?');
        $('#idJobConfirm').val('');
        $('#txt_view_reason').addClass('d-none');
        $('#txtReason').val('');
        $('#kt_modal_accept_reject').modal('show');
    });
    $('#btnRejectJob').on('click', function () {
        statusUpdate = false;
        $('#txt_view_reason').removeClass('d-none');
        $('#pConfirm').text('Are you sure you want to reject the selected job?');
        $('#idJobConfirm').val('');
        $('#txtReason').val('');
        $('#kt_modal_accept_reject').modal('show');
    });
    $('#btnConfirm').on('click', function () {
        let reason = $('#txtReason').val();
        if (reason === '' && !statusUpdate) {
            $('#error_reason').removeClass('d-none');
        } else {
            $('#error_reason').addClass('d-none');
            updateStatusDetailJob(idUpdate);
        }
    });
});

function renderNoteAndButtonAdd() {
    $('.fc-pending-button, .fc-accept-button, .fc-reject-button,.fc-executing-button,.fc-finishedapprove-button,.fc-finishedreject-button,.fc-finishStopped-button,.fc-finishFailed-button').css('color', '#FFFFFF');
    $('.fc-pending-button, .fc-accept-button, .fc-reject-button,.fc-executing-button,.fc-finishedapprove-button,.fc-finishedreject-button,.fc-finishStopped-button,.fc-finishFailed-button').prop('disabled', true);
    $('.fc-pending-button').css('background', '#2E64FE');
    $('.fc-accept-button').css('background', '#088A4B');
    $('.fc-reject-button').css('background', '#DF0101');
    $('.fc-executing-button').css('background', '#dfb24a');
    $('.fc-finishedapprove-button').css('background', '#4fdfc3');
    $('.fc-finishedreject-button').css('background', '#afdfd3');
    $('.fc-finishStopped-button').css('background', '#949483');
    $('.fc-finishFailed-button').css('background', '#131215');
}

function loadDateTimeTicker() {
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

function openUpdateEvent(id) {
    idUpdate = id;
    $.ajax({
        type: 'GET',
        url: '/nera/job-planning/api/get-job-by-id?id=' + id,
        success: function (data) {
            if(data.status === 'Accepted' || data.status === 'Finished (Stopped)' || data.status === 'Finished (Failed)')
            {
                $('#DateExec').text(data.executionDate);
                $('#TimeExec').text(data.startTime + ' - ' + data.endTime);
                $('#descriptionJob').text(data.jobDescription);
                $('#workflowName').text(data.workflowName);
                $('#jobAssignStatus').text(data.status);
                $('#plannerName').text(data.plannerName);
                $('#assigneeName').text(data.assigneeName);
                $('#jobNameLbl').text(data.jobName);
                $('#action_exe_job').removeClass('d-none');
                if (data.status !== 'Pending for Acceptance') {
                    $('#action_job').addClass('d-none');
                } else {
                    $('#action_job').removeClass('d-none');
                }
                $('#btnPLayJob').on('click', function () {
                    var months = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
                    var dateNow = new Date();
                    var today = dateNow.getFullYear()+'-'+months[dateNow.getMonth()]+'-'+("0" + dateNow.getDate()).slice(-2)+' '+("0" + dateNow.getHours()).slice(-2)+':'+("0" + dateNow.getMinutes()).slice(-2)+':00';
                    var dateExeFormat = data.executionDate.split('/')[2]+'-'+data.executionDate.split('/')[1]+'-'+data.executionDate.split('/')[0];

                    var dateTimeExeStart = dateExeFormat + " " + data.startTime + ":00";
                    var dateTimeExeEnd = dateExeFormat + " " + data.endTime + ":00";

                    if(today >= dateTimeExeStart && today <= dateTimeExeEnd) {
                        window.location.href = "../my-job/execution?jobid="+data.id+"&workflowid="+data.workflowId;
                    } else {
                        var textHover = 'You can only start to execute the job at ' + dateTimeExeStart + ' - ' + dateTimeExeEnd;
                        Swal.fire({
                            text:  textHover,
                            type:  'error',
                            timer: 5000,
                            showConfirmButton: false
                        });
                    }
                });
                $('#btnCancelJob').on('click', function () {
                    $('#kt_modal_edit').modal('hide');
                });
                getImageWorkflow(data.workflowName);
                $('#kt_modal_edit').modal('show');
            }
            else
            {
                $('#DateExec').text(data.executionDate);
                $('#TimeExec').text(data.startTime + ' - ' + data.endTime);
                $('#descriptionJob').text(data.jobDescription);
                $('#workflowName').text(data.workflowName);
                $('#jobAssignStatus').text(data.status);
                $('#plannerName').text(data.plannerName);
                $('#assigneeName').text(data.assigneeName);
                $('#jobNameLbl').text(data.jobName);
                if (data.status !== 'Pending for Acceptance') {
                    $('#action_job').addClass('d-none');
                } else {
                    $('#action_job').removeClass('d-none');
                }
                $('#action_exe_job').addClass('d-none');
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
                getImageWorkflow(data.workflowName);
                $('#kt_modal_edit').modal('show');
            }

        },
    });
}

function setDate(executionDate, time) {
    let date = executionDate.split("/").reverse().join("/");
    let result = new Date(date);
    result.setHours(time.split(":")[0], time.split(":")[1], 0, 0);
    return moment(result).format('YYYY/MM/DD HH:mm');
}

function updateStatusDetailJob(id) {
    let reason = $('#txtReason').val();
    $.ajax({
        type: 'POST',
        url: '/nera/my-job/api/accept-or-reject-job',
        data: {id: id, status: statusUpdate, reason: reason},
        beforeSend: function () {
        },
        success: function () {
            $('#kt_modal_accept_reject').modal('toggle');
            $('#kt_modal_edit').modal('toggle');
            $('#calendar').fullCalendar('removeEvents');
            Swal.fire({
                text: "Successfully",
                type: 'success',
                timer: 2000,
                showConfirmButton: false
            });
            $('#calendar').fullCalendar('refetchEvents');
        },
        error: function () {
            $('#kt_modal_accept_reject').modal('toggle');
            $('#kt_modal_edit').modal('toggle');
            Swal.fire({
                text: "Failed",
                type: 'error',
                timer: 2000,
                showConfirmButton: false
            });

        }
    });
}