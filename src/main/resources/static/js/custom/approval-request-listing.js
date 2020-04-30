var datatable;
checkPermission();
function checkPermission() {
    $('#job-planning-menu-left').removeClass('kt-menu__item--open');
    if(!hasViewPermission) {
        window.location.href = '../../403';
    }
}

$(document).ready(function() {
    datatable = $('#kt_datatable').KTDatatable({
        // datasource definition
        data: {
            type: 'remote',
            source: {
                read: {
                    method: 'GET',
                    url: '/nera/my-job-approval-request/api',
                    map: function(raw) {
                        var dataSet = raw;
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
        noRecords: 'No records found',
        search: {
            input: $('#generalSearch')
        },

        // columns definition
        columns: [
            {
                field: 'no',
                title: 'S/N',
                sortable: false,
                width: 30,
                textAlign: 'left'
            },
            {
                field: 'jobName',
                title: 'Job Name',
                textAlign: 'left'
            },
            {
                field: 'workflowName',
                title: 'Workflow Name',
                textAlign: 'left'
            },
            {
                field: 'jobDescription',
                title: 'Job Description',
                textAlign: 'left'
            },
            {
                field: 'plannerName',
                title: 'Planner',
                textAlign: 'left'
            },
            {
                field: 'assigneeName',
                title: 'Assignee',
                textAlign: 'left'
            },
            {
                field: 'requestAt',
                title: 'Request at',
                width: 70,
                sortable: true
            },
            {
                field: 'Actions',
                title: 'Actions',
                sortable: false,
                overflow: 'visible',
                textAlign: 'center',
                template: function(row) {
                    var id = row.id;
                    var htmlUpdate  = '';
                    var htmlDelete = '';

                    if(hasApproveAndRejectPermission) {
                        htmlUpdate = '<a href="#"  class="btn btn-hover-brand btn-icon btn-pill approve-job" data-id="' + id + '"><i class="la la-check"></i></a>'

                        htmlDelete = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill reject-job" data-id="' + id + '"><i class="la la-close"></i></a>';
                    }
                    return htmlUpdate + htmlDelete;
                }
            }]
    });

    $(document).on('click', '.reject-job', function () {
        var id = $(this).attr("data-id");
        $('#job-approval-id').val(id);
        $('#action-type').val('Reject');
        $('.confirm-message').text('Do you want to Reject the job ?');
        $('#kt_modal').modal('show');
    });

    $(document).on('click', '.approve-job', function () {
        var id = $(this).attr("data-id");
        $('#job-approval-id').val(id);
        $('#action-type').val('Approve');
        $('.confirm-message').text('Do you want to Approve the job ?');
        $('#kt_modal').modal('show');
    });

    $(document).on('click', '#btnYes', function () {
        var id = $('#job-approval-id').val();
        var actionType = $('#action-type').val();
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
                    datatable.load();
                } else {
                    Swal.fire({
                        text:  'Cannot ' + actionType +' Job',
                        type:  'error',
                        timer: 2000,
                        showConfirmButton: false
                    });
                }
                $('#kt_modal').modal('hide');
            },
            error: function (err) {
                //
            },
        });
    });

});