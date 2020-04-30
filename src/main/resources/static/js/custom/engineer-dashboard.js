var dataJobAssignment;
var dataJobComingToday;
var dataJobComingWeek;
var dataJobComingMonth;
var typeConfirm;

$(document).ready(function() {
    dataJobAssignment = $('#kt_datatable_Assignment').KTDatatable({
        // datasource definition
        data: {
            type: 'remote',
            source: {
                read: {
                    method: 'GET',
                    url: '/nera/my-job/api/get-engineer-job',
                    map: function(raw) {
                        if (typeof raw !== 'undefined') {
                            $('#lbAccepted').text(raw.totalAccepted);
                            $('#lbRejected').text(raw.totalRejected);
                            $('#lbCompleted').text(raw.totalCompleted);
                            $('#lbFailed').text(raw.totalFailed);
                            var dataSet = raw.myJobs;
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
                width: 130,
                sortable: false
            },
            {
                field: 'executionDate',
                title: 'Execution At',
                width: 100,
                sortable: true,
                template: function(row) {
                    return row.executionDate + ' '+ row.startTime;
                }
            },
            {
                field: 'jobAssignmentDate',
                title: 'Job Assignment Date',
                sortable: true,
                width: 130
            },
            {
                field: 'Actions',
                title: 'Actions',
                overflow: 'visible',
                textAlign: 'center',
                width: 90,
                sortable: false,
                template: function(row) {
                    var id = row.id;
                    var htmlAccept = '<a href="#' + id + '"  class="btn btn-hover-success btn-icon btn-pill acceptJob" data-id="' + id + '"><i class="la la-check"></i></a>'
                    var htmlReject = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill rejectJob" data-id="' + id + '"><i class="la la-times"></i></a>';
                    return htmlAccept + htmlReject;
                },
            }],
    });

    $(document).on('click', '.acceptJob' , function() {
        if(hasAcceptRejectJobPermission) {
            $('#idJobConfirm').val($(this).attr('data-id'));
            typeConfirm = true;
            $('#pConfirm').text('Are you sure you want to accept the selected job?');
            $('#txt_view_reason').addClass('d-none');
            $('#txtReason').val('');
            $('#kt_modal_accept_reject').modal('show');
        } else {
            Swal.fire({
                text:  'You have no permission to perform this action. Please contact administrator for help.',
                type:  'error',
                timer: 3000,
                showConfirmButton: false
            });
        }
    });

    $(document).on('click', '.rejectJob' , function() {
        if(hasAcceptRejectJobPermission) {
            $('#error_reason').addClass('d-none');
            $('#txt_view_reason').removeClass('d-none');
            $('#idJobConfirm').val($(this).attr('data-id'));
            typeConfirm = false;
            $('#pConfirm').text('Are you sure you want to reject the selected job?');
            $('#txtReason').val('');
            $('#kt_modal_accept_reject').modal('show');
        } else {
            Swal.fire({
                text:  'You have no permission to perform this action. Please contact administrator for help.',
                type:  'error',
                timer: 3000,
                showConfirmButton: false
            });
        }
    });

    $('#btnConfirm').on('click', function () {
        let id = $('#idJobConfirm').val();
        let reason = $('#txtReason').val();
        if (reason === '' && !typeConfirm) {
            $('#error_reason').removeClass('d-none');
        } else {
            $('#error_reason').addClass('d-none');
            acceptRejectJob(id);
        }
    });

    function acceptRejectJob(id) {
        let reason = $('#txtReason').val();
        $.ajax({
            type: 'POST',
            url: '/nera/my-job/api/accept-or-reject-job',
            data: {id: id, status: typeConfirm, reason: reason},
            // beforeSend: function () {
            //     $('#loader').show();
            //     $('#kt_modal_accept_reject').modal('toggle');
            // },
            // complete: function () {
            //     $('#loader').hide();
            // },
            success: function (data) {
                // if ($('#idJobConfirm').val() == '') {
                //     $('#kt_modal_edit').modal('toggle');
                // }
                let message = "Reject Successful";
                if(typeConfirm) {
                    getDataToDay();
                    getDataThisWeek();
                    getDataThisMonth();
                    message = "Accept Successful";
                }
                $('#kt_modal_accept_reject').modal('hide');
                Swal.fire({
                    text: message,
                    type: 'success',
                    timer: 2000,
                    showConfirmButton: false
                });
                dataJobAssignment.reload();
            },
            error: function () {
                // if ($('#idJobConfirm').val() == '') {
                //     $('#kt_modal_edit').modal('toggle');
                // }
                Swal.fire({
                    text: "Failed",
                    type: 'error',
                    timer: 2000,
                    showConfirmButton: false
                });
            }
        });
    }
    getDataToDay();

    $('#tab-today').on('click', function () {
        getDataToDay();
    });

    $('#tab-week').on('click', function () {
        getDataThisWeek();
    });

    $('#tab-month').on('click', function(){
        getDataThisMonth();
    });

    function getDataToDay() {
        if (typeof dataJobComingToday !== 'undefined') {
            dataJobComingToday.destroy();
        }
        dataJobComingToday = $('#kt_datatable_coming_today').KTDatatable({
            // datasource definition
            data: {
                type: 'remote',
                source: {
                    read: {
                        method: 'GET',
                        url: '/nera/dashboard/api/get-job-accepted',
                        data: {type: 1},
                        map: function(raw) {
                            return raw;
                        }
                    }
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
                scroll: true,
                height: 500,
                footer: false
            },

            selector: {
                class: 'kt-checkbox--solid'
            },

            // column sorting
            sortable: false,
            pagination: false,

            // columns definition
            columns: [
                {
                    field: 'dateExec',
                    title: 'Date Time'
                },
                {
                    field: 'operation',
                    title: 'Operation'
                },
                {
                    field: 'jobName',
                    title: 'Job Name'
                },
                {
                    field: 'Actions',
                    title: 'Actions',
                    overflow: 'visible',
                    width: 110,
                    template: function(row) {
                        var id = row.id;
                        return '<a href="/menu/my-job/execution?jobid=' + id + '&workflowid=' + row.workflowId + '"  class="btn btn-hover-brand btn-icon btn-pill" data-id="' + id + '"><i class="la la-play"></i></a>'
                    }
                }]
        });
    }

    function getDataThisMonth() {
        if (typeof dataJobComingMonth !== 'undefined') {
            dataJobComingMonth.destroy();
        }
        dataJobComingMonth = $('#kt_datatable_coming_month').KTDatatable({
            // datasource definition
            data: {
                type: 'remote',
                source: {
                    read: {
                        method: 'GET',
                        url: '/nera/dashboard/api/get-job-accepted',
                        data: {type: 3},
                        map: function(raw) {
                            return raw;
                        }
                    }
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
                scroll: true,
                height: 500,
                footer: false
            },

            selector: {
                class: 'kt-checkbox--solid'
            },

            // column sorting
            sortable: false,
            pagination: false,

            // columns definition
            columns: [
                {
                    field: 'dateExec',
                    title: 'Date Time'
                },
                {
                    field: 'operation',
                    title: 'Operation'
                },
                {
                    field: 'jobName',
                    title: 'Job Name'
                },
                {
                    field: 'Actions',
                    title: 'Actions',
                    overflow: 'visible',
                    width: 110,
                    template: function(row) {
                        var id = row.id;
                        return '<a href="/menu/my-job/execution?jobid=' + id + '&workflowid=' + row.workflowId + '"  class="btn btn-hover-brand btn-icon btn-pill" data-id="' + id + '"><i class="la la-play"></i></a>'
                    }
                }]
        });
    }

    function getDataThisWeek() {
        if (typeof dataJobComingWeek !== 'undefined') {
            dataJobComingWeek.destroy();
        }
        dataJobComingWeek = $('#kt_datatable_coming_week').KTDatatable({
            // datasource definition
            data: {
                type: 'remote',
                source: {
                    read: {
                        method: 'GET',
                        url: '/nera/dashboard/api/get-job-accepted',
                        data: {type: 2},
                        map: function(raw) {
                            return raw;
                        }
                    }
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
                scroll: true,
                height: 500,
                footer: false
            },

            selector: {
                class: 'kt-checkbox--solid'
            },

            // column sorting
            sortable: false,
            pagination: false,

            // columns definition
            columns: [
                {
                    field: 'dateExec',
                    title: 'Date Time'
                },
                {
                    field: 'operation',
                    title: 'Operation'
                },
                {
                    field: 'jobName',
                    title: 'Job Name'
                },
                {
                    field: 'Actions',
                    title: 'Actions',
                    overflow: 'visible',
                    width: 110,
                    template: function(row) {
                        var id = row.id;
                        return '<a href="/menu/my-job/execution?jobid=' + id + '&workflowid=' + row.workflowId + '"  class="btn btn-hover-brand btn-icon btn-pill" data-id="' + id + '"><i class="la la-play"></i></a>'
                    }
                }]
        });
    }
});