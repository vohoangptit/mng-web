"use strict";

let isUpdate;
let idUpdate;
let eventUpdate;
let jobUpdate;
let plannerUpdate;
let assigneeUpdate;

$(document).ready(function() {
	$('#calendar').fullCalendar({
		timeFormat: 'HH:mm',
		events: '/nera/job-planning/api/get-source-calendar-view',
		customButtons: {
			add: {
				text: '',
			},
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
		editable:true,
		header:{
			left: 'prev,next today, pending, accept, reject, executing, finishedapprove, finishedreject, finishStopped, finishFailed',
			center:'title',
			right:'add'
		},
		selectable:true,
		selectHelper:true,
		dayClick: function() {
			openAddEvent();
		},
		eventClick: function(date, jsEvent) {
			openUpdateEvent(date.id);
			eventUpdate = jsEvent;
		},
		allDay: true,
		// editable:false,
	});
	renderNoteAndButtonAdd();
	//renderEventJob();
	$('#selectJobId').change(function() {
		let val = $("#selectJobId option:selected").val();
		if(val !== undefined) getJobById(val);
	});
	loadDateTimeTicker();
	$('#btnDetailDelete').on('click', function(){
		$('#kt_modal_edit').modal('toggle');
		$('#kt_modal_Delete').modal('show');
		$('#deleteId').val(idUpdate);
	});
	$('#btnYesDelete').on('click', function(){
		let id = $('#deleteId').val();
		callDeleteApi(id);
	});
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
			$('#endTime').val(end);
		} else {
			let minute = (evt.minute || '00');
			if((getHour($('#startTime').val()) === parseInt(evt.hour) && getMinute($('#startTime').val()) >= parseInt(minute)) || getHour($('#startTime').val()) > parseInt(evt.hour)) {
				$('#lblError').text('The end time must be greater than the start time.');
				$('#validateData').removeClass('hide-class');
			} else{
				$('#validateData').addClass('hide-class');
			}
		}
	});
	$('._jw-tpk-container').css('height', 'auto');
	$('#btnConfirm').on('click', function () {
		if (isUpdate) {
			updateJobCalendarEvent(idUpdate);
		} else {
			addJobCalendarEvent();
		}
	});
	$('#loader').hide();
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
	if(hasAssignJobPermission) {
		$('.fc-add-button').replaceWith("<button onclick='openAddEvent()' class='btn btn-add-default btn-icon-sm'><span><i class='la la-plus' style='color: white; font-weight: bolder'></i></span> <span>Assign New Job</span></button>");
	} else {
		$('.fc-add-button').replaceWith('');
	}
	$('.btn-add-default').css('height', '3.1em');
}
/*function openAddEvent(){

	isUpdate = false;
	$('#validateData').addClass('hide-class');
	$('.select2-selection__rendered').html('');
	$('#DateExec').val(autoCreateDateJob());
	$('#startTime').val('8:00');
	$('#endTime').val('10:00');
	$('#descriptionJob').text('');
	$('#workflowName').text('');
	$('#jobAssignStatus').text('');
	loadPlanner();
	loadAssignee();
	loadJob();
	$('#kt_modal_edit').modal('show');
}*/
function getHour(time) {
	return parseInt(time.split(":")[0]);
}
function getLengthHour(time) {
	return time.split(":")[0].length;
}
function getMinute(time) {
	return parseInt(time.split(":")[1]);
}
function callDeleteApi(id)
{
	$.ajax({
		type: 'POST',
		url: '/nera/job-planning/api/delete-job-by-id',
		data: {id: id},
		beforeSend: function () {
			//
		},
		success: function () {
			$('#kt_modal_Delete').modal('hide');
			$('#calendar').fullCalendar('removeEvents');
			Swal.fire({
				text:  "Successfully",
				type:  'success',
				timer: 2000,
				showConfirmButton: false
			});
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
function loadDateTimeTicker()
{
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
function getJobById(id){
	$.ajax({
		type: 'GET',
		url: '/nera/job-planning/api/get-job-view-by-id?id='+id,
		success: function (data) {
			if (data.id !== 0) {
				if(data.workflowName === null){
					$('#lblError').text('Job must be at least 1 Workflow, please choose another Job.');
					$('#validateData').removeClass('hide-class');
					$('#descriptionJob').text('');
					$('#workflowName').text('');
				} else{
					$('#validateData').addClass('hide-class');
					$('#descriptionJob').text(data.description);
					$('#workflowName').text(data.workflowName);
					getImageWorkflow(data.workflowName);
				}
			} else {
				$('#descriptionJob').text('');
				$('#workflowName').text('');
			}
		},
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
function  CreateOrUpdate() {
	if( $("#selectJobId option:selected").val() == null || $("#workflowName").text() === ''
		|| $("#assigneeSelect option:selected").val() == null || $("#plannerSelect option:selected").val() == null
		|| $('#DateExec').val() === '' || $('#startTime').val() === '' || $('#endTime').val() === '') {
		$('#lblError').text('Field contain (*) cannot empty, please type again');
		$('#validateData').removeClass('hide-class');
	}
	// else if(!validateExecutionDate($('#DateExec').val())){
	// 	$('#lblError').text('The earliest execution date must be 3 days from today');
	// 	$('#validateData').removeClass('hide-class');
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
	if (isUpdate) {
		$('#txtConfirm').text('Are you sure you want to update a job assignment?');
	} else {
		$('#txtConfirm').text('Are you sure you want to create a new job assignment?');
	}
	$('#kt_modal_confirm').modal('show');
}

function validateHour(time) {
	if(getLengthHour(time) < 2){
		return "0" + time;
	}
	return time;
}

function addJobCalendarEvent()
{
	let body ={
		plannerId: $("#plannerSelect option:selected").val(),
		assigneeId: $("#assigneeSelect option:selected").val(),
		jobId: $("#selectJobId option:selected").val(),
		startTime: validateHour($('#startTime').val()),
		endTime: validateHour($('#endTime').val()),
		executionDate: $('#DateExec').val(),
		jobDescription: $('#descriptionJob').text(),
	};
	$.ajax({
		type: 'POST',
		url: '/nera/job-planning/api/create-job-planning',
		data: JSON.stringify(body),
		contentType: "application/json",
		beforeSend: function() {
			$('#kt_modal_confirm').modal('toggle');
			$('#loader').show();
		},
		complete: function(){
			$('#loader').hide();
		},
		success: function (data) {
			let end;
			let start;
			if (data.code !== 200) {
				$('#kt_modal_edit').modal('toggle');
				Swal.fire({
					text: "Failed",
					type: 'error',
					timer: 2000,
					showConfirmButton: false
				});
			} else {
				$('#kt_modal_edit').modal('toggle');
				start = setDate($('#DateExec').val(), $('#startTime').val());
				end = setDate($('#DateExec').val(), $('#endTime').val());
				let eventObject = {
					title: data.fieldName,
					start: start,
					end: end,
					textColor: 'white',
					id: data.id,
					color: '#2E64FE'
				};
				Swal.fire({
					text: "Successfully",
					type: 'success',
					timer: 2000,
					showConfirmButton: false
				});
				$('#calendar').fullCalendar('renderEvent', eventObject, true);
			}
		},
	});
}

function updateJobCalendarEvent(id)
{
	let body ={
		id: id,
		plannerId: $("#plannerSelect option:selected").val(),
		assigneeId: $("#assigneeSelect option:selected").val(),
		jobId: $("#selectJobId option:selected").val(),
		startTime: validateHour($('#startTime').val()),
		endTime: validateHour($('#endTime').val()),
		executionDate: $('#DateExec').val(),
	};
	$.ajax({
		type: 'POST',
		url: '/nera/job-planning/api/update-job-planning',
		data: JSON.stringify(body),
		contentType: "application/json",
		beforeSend: function() {
			$('#kt_modal_confirm').modal('toggle');
			$('#loader').show();
		},
		complete: function(){
			$('#loader').hide();
		},
		success: function (data) {
			let end;
			let start;
			if (data.code !== 200) {
				$('#kt_modal_edit').modal('toggle');
				Swal.fire({
					text:  "Failed",
					type:  'error',
					timer: 2000,
					showConfirmButton: false
				});
			} else {
				$('#kt_modal_edit').modal('toggle');
				start = setDate($('#DateExec').val(), $('#startTime').val());
				end = setDate($('#DateExec').val(), $('#endTime').val());
				eventUpdate.title = $("#selectJobId option:selected").text();
				eventUpdate.start = start;
				eventUpdate.end = end;
				eventUpdate.id = idUpdate;
				Swal.fire({
					text:  "Successfully",
					type:  'success',
					timer: 2000,
					showConfirmButton: false
				});
				$('#calendar').fullCalendar('refetchEvents');

			}
		},
	});
}

function autoCreateDateJob() {
	let date = new Date();
	date.setDate( new Date().getDate()+3);
	return moment(date).format("DD/MM/YYYY")
}

function openAddEvent(){
	$('.contentWorkflowDetail').empty();
	isUpdate = false;
	if(hasAssignJobPermission) {
		$('#validateData').addClass('hide-class');
		$('.select2-selection__rendered').html('');
		$('#DateExec').val(autoCreateDateJob());
		$('#startTime').val('8:00');
		$('#endTime').val('10:00');
		$('#descriptionJob').text('');
		$('#workflowName').text('');
		$('#jobAssignStatus').text('');
		loadPlanner();
		loadAssignee();
		loadJob();
		$('#kt_modal_edit').modal('show');
	} else {
		Swal.fire({
			text:  "Don't have permission to assign job",
			type:  'error',
			timer: 2000,
			showConfirmButton: false
		});
	}
}

function openUpdateEvent(id){
	isUpdate = true;
	idUpdate = id;
	$.ajax({
		type: 'GET',
		url: '/nera/job-planning/api/get-job-by-id?id='+id,
		success: function (data) {
			plannerUpdate = data.plannerId;
			jobUpdate = data.jobId;
			assigneeUpdate = data.assigneeId;
			if(data.status === 'Accepted'){
				$('#job_description').text(data.jobDescription);
				$('#workflow_name').text(data.workflowName);
				$('#job_assign_status').text(data.status);
				$('#date_exec').text(data.executionDate);
				$('#time_exec').text(data.startTime + ' - ' + data.endTime);
				$('#planner_name').text(data.plannerName);
				$('#assignee_name').text(data.assigneeName);
				$('#job_name').text(data.jobName);
				getImageWorkflow(data.workflowName);
				$('#kt_modal_view').modal('show');
			} else {
				$('#validateData').addClass('hide-class');
				$('#DateExec').val(data.executionDate);
				$('#startTime').val(data.startTime);
				$('#endTime').val(data.endTime);
				$('#descriptionJob').text(data.jobDescription);
				$('#workflowName').text(data.workflowName);
				$('#jobAssignStatus').text(data.status);
				getImageWorkflow(data.workflowName);
				loadPlanner();
				loadAssignee();
				loadJob();
				$('#kt_modal_edit').modal('show');
			}
		},
	});
}
function loadJob()
{
	$('#selectJobId')
		.find('option')
		.remove()
		.end();
	$.ajax({
		type: 'GET',
		url: '/nera/job-planning/api/get-job-management',
		success: function (data) {
			let re = data;
			for(let i = 0; i < re.length; i++)
			{
				let op = new Option(re[i].name, re[i].id);
				$("#selectJobId").append(op);
			}
			if(isUpdate){
				$("#selectJobId").val(jobUpdate).trigger('change');

			} else {
				$("#selectJobId").val(null).trigger('change');
			}
			$('#selectJobId').select2({ placeholder: "Select Job", allowClear: false, width: 'resolve', dropdownAutoWidth: true });
		},
	});
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
function loadPlanner()
{
	$('#plannerSelect')
		.find('option')
		.remove()
		.end();
	$.ajax({
		type: 'GET',
		url: '/nera/job-planning/api/get-planner',
		success: function (data) {
			let re = data;
			for(let i = 0; i < re.length; i++)
			{
				let op = new Option(re[i].fullName, re[i].id);
				$("#plannerSelect").append(op);
			}
			if (isUpdate) {
				$("#plannerSelect").val(plannerUpdate).trigger('change');
			} else {
				loadPlannerDefault();
			}
			$('#plannerSelect').select2({ placeholder: "Select Planner", allowClear: false, width: 'resolve', dropdownAutoWidth: true });
		},
	});
}
function loadAssignee()
{
	$('#assigneeSelect')
		.find('option')
		.remove()
		.end();
	$.ajax({
		type: 'GET',
		url: '/nera/job-planning/api/get-assignee',
		success: function (data) {
			let re = data;
			for(let i = 0; i < re.length; i++)
			{
				let op = new Option(re[i].fullName, re[i].id);
				$("#assigneeSelect").append(op);
			}
			if(isUpdate){
				$("#assigneeSelect").val(assigneeUpdate).trigger('change');
			} else {
				$("#assigneeSelect").val(null).trigger('change');
			}
			$('#assigneeSelect').select2({ placeholder: "Select Assignee", allowClear: false, width: 'resolve', dropdownAutoWidth: true });
		},
	});
}
function  setDate(executionDate, time) {
	let date = executionDate.split("/").reverse().join("/");
	let result = new Date(date);
	result.setHours(time.split(":")[0],time.split(":")[1],0,0);
	return moment(result).format('YYYY/MM/DD HH:mm');
}
function validateExecutionDate(date) {
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