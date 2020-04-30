var playPie;
var jobPie;
$(document).ready(function () {
    $('input:radio[name="playbookChecked"]').change(function() {
        loadPieChartPlaybook();
    });
    $('input:radio[name="jobChecked"]').change(function() {
        loadPieChartJob();
    });
    statisticPlaybookByStatus();
    statisticJobByStatus();
  //  getWorkflowStatistic();

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
    loadPieChartPlaybook();
    loadPieChartJob();

    $('#tbl_workflow_static').on('click', '#workflowId', function () {
        var id = $(this).attr("data-id");
        if (id === '') {
            Swal.fire({
                text:  "Require to select the workflow before viewing in detail",
                type:  'error',
                timer: 2000,
                showConfirmButton: false
            });
            return;
        }
        $('.contentWorkflowDetail').empty();
        $.ajax({
            type: 'GET',
            url: '/nera/api/get-image-workflow',
            data: {id: id},
            success: function (data) {
                if(data != '') {
                    $('.contentWorkflowDetail').append('');
                    $('.contentWorkflowDetail').append('<img src="data:image/png;base64, '+data+'">');
                } else {
                    Swal.fire({
                        title: 'Workflow Detail',
                        text:  "Load detail failed",
                        type:  'error',
                        timer: 3000,
                        showConfirmButton: false
                    });
                }
                $('#viewDetailImageWorkflow').modal('show');
            },
            error: function (data) {
                Swal.fire({
                    title: 'Workflow Detail',
                    text:  "Load detail failed",
                    type:  'error',
                    timer: 3000,
                    showConfirmButton: false
                });
            }
        });
    });
});
function loadPieChartPlaybook() {
    let type =  $("input[name='playbookChecked']:checked").val();
    $.ajax({
        type: 'GET',
        url: '/nera/dashboard/api/statistic-playbook-by-status-pie-chart?type='+type,
        success: function (data) {
            playPie = data;
            google.charts.setOnLoadCallback(drawChart);
        }
    });
}
function loadPieChartJob() {
    let type =  $("input[name='jobChecked']:checked").val();
    $.ajax({
        type: 'GET',
        url: '/nera/dashboard/api/statistic-job-by-status-pie-chart?type='+type,
        success: function (data) {
            jobPie = data;
            google.charts.setOnLoadCallback(drawChart1);
        }
    })
}
function appendWorkflow(name, usage, timeView){
    $('#up_coming_task_tbl > tbody:last').append('<tr class="text-wf">'
        + '<td>' + name + '</td>'
        + '<td>' + usage + '</td>'
        + '<td>' + timeView + '</td>'
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

function getWorkflowStatistic() {
    $.ajax({
        type: 'GET',
        url: '/nera/dashboard/api/statistic-workflow',
        success: function (data) {
            let raw = data;
            for (let i = 0; i < raw.length; i++) {
                appendWorkflow(raw[i].name, raw[i].usage, raw[i].timView);
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

google.charts.load('current', {'packages':['corechart']});

function drawChart() {
    var data = google.visualization.arrayToDataTable([
        ['Task', 'Hours per Day'],
        ['New', playPie[1]],
        ['Pending Approval', playPie[4]],
        ['Approved', playPie[2]],
        ['Rejected', playPie[3]]
    ], false);

    var options = {
        sliceVisibilityThreshold:0,
        width: '100%',
        responsive: true,
        chartArea: {
            // leave room for y-axis labels
            width: 500,
            height: 500,
            top:20,
            left:10,
            bottom:10
        },
        legend: {
            alignment: 'center'
        },
    };

    var chart = new google.visualization.PieChart(document.getElementById('piechart'));

    chart.draw(data, options);
}
function drawChart1() {
    var data = google.visualization.arrayToDataTable([
        ['Task', 'Hours per Day'],
        ['Pending Acceptance', jobPie["Pending for Acceptance"]],
        ['Accepted', jobPie["Accepted"]],
        ['Rejected', jobPie["Rejected"]],
        ['Executing', jobPie["Executing"]],
        ['Completed', jobPie["Finished (Approved)"] + jobPie["Finished (Rejected)"]],
        ['Stopped', jobPie["Finished (Stopped)"]],
        ['Failed', jobPie["Finished (Failed)"]]
    ], false);
    var options = {
        sliceVisibilityThreshold:0,
        width: '100%',
        responsive: true,
        chartArea: {
            // leave room for y-axis labels
            width: 500,
            height: 500,
            top:20,
            bottom:10
        },
        legend: {
            alignment: 'center'
        },
    };

    var chart = new google.visualization.PieChart(document.getElementById('piechart1'));

    chart.draw(data, options);
}