"use strict";

var datatable;
$(document).ready(function () {
    datatable = $('#kt_datatable').KTDatatable({
        // datasource definition
        data: {
            type: 'remote',
            source: {
                read: {
                    method: 'GET',
                    url: '/nera/playbook/api/listing-and-filter',
                    map: function (raw) {
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
                field: 'name',
                title: 'Playbook Name',
                textAlign: 'left',
            },
            {
                field: 'status',
                title: 'Status',
                template: function (row) {
                    var status = row.status;
                    var htmlEdit;
                    if (status == "APPROVED") {
                        htmlEdit = '<button style="border-radius: 5px; border: 1px solid #1eb7ae; background-color: #1eb7ae;" disabled><span style="font-family: Poppins;color:white">APPROVED</span></button>';
                    } else if (status == "NEW") {
                        htmlEdit = '<button style="border-radius: 5px; border: 1px solid #d9a747; background-color: #d9a747;" disabled><span style="font-family: Poppins;color:white">NEW</span></button>';
                    } else {
                        htmlEdit = '<button style="border-radius: 5px; border: 1px solid #ea6861; background-color: #ea6861;" disabled><span style="font-family: Poppins;color:white">REJECTED</span></button>';
                    }
                    return htmlEdit;
                },
            },
            {
                field: 'version',
                title: 'Version',
                template: function(row) {
                    return "Version " + row.version;
                }
            },
            {
                field: 'createdBy',
                title: 'Creator',
            },
            {
                field: 'createdDate',
                title: 'Created Date',
            },
            {
                field: 'approvedBy',
                title: 'Approved By',
            },
            {
                field: 'approvedDate',
                title: 'Approved Date',
            },
            {
                field: 'Actions',
                title: 'Actions',
                sortable: false,
                overflow: 'visible',
                textAlign: 'left',
                marginLeft: '20%',
                template: function (row) {
                    var id = row.id;
                    var html1 = "";
                    var html2 = "";
                    var html3 = "";
                    if (hasEditPermission) {
                        html1 = '<a href="/menu/masterdata/playbook/detail/' + id + '"  class="btn btn-hover-brand btn-icon btn-pill" data-id="' + id + '"><i class="la la-edit"></i></a>'
                    }
                    if (hasDeleteNewPermission && row.status == 'NEW' || hasDeletePermission && row.status == 'APPROVED'
                        || hasDeletePermission && row.status == 'REJECTED') {
                        html2 = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill deletePlaybook" data-id="' + id + '"><i class="la la-trash"></i></a>';
                    }
                    if (hasSendToApproval && row.status == 'NEW') {
                        html3 = '<a href="#" class="btn btn-icon btn-hover-approval btn-pill sendToApproval" data-id="' + id + '"><i class="fas fa-paper-plane"></i></a>';
                    }
                    return html1 + html2 + html3;
                },
            }],
    });


    loadCreatedByAll();

    function loadCreatedByAll() {
        $.ajax({
            type: 'GET',
            url: '/nera/playbook/api/created-person',
            success: function (data) {
                var re = data;
                for (var i = 0; i < re.length; i++) {
                    var op = new Option(re[i], re[i]);
                    $("#createdBySelectd").append(op);
                }
                $('#createdBySelectd').select2({
                    placeholder: "Created By (All)",
                    allowClear: false,
                    width: 'resolve',
                    dropdownAutoWidth: true
                });
            },
        });
    }

    loadApprovedByAll();

    function loadApprovedByAll() {
        $.ajax({
            type: 'GET',
            url: '/nera/playbook/api/approved-person',
            success: function (data) {
                var re = data;
                for (var i = 0; i < re.length; i++) {
                    var op = new Option(re[i], re[i]);
                    $("#approvedBySelectd").append(op);
                }
                $('#approvedBySelectd').select2({
                    placeholder: "Approved By (All)",
                    allowClear: false,
                    width: 'resolve',
                    dropdownAutoWidth: true
                });
            },
        });
    }

    $('#btnReset').unbind().click(function () {
        reset();
    });

    function reset() {
        $('#generalSearch').val('');
        $('#DateFrom').val('');
        $('#DateTo').val('');
        $('#createdBySelectd').val('');
        $('#approvedBySelectd').val('');
        $('.select2-selection__rendered').html('');

        datatable.setDataSourceParam('createdBy', null);
        datatable.setDataSourceParam('approvedBy', null);
        datatable.setDataSourceParam('startDate', "");
        datatable.setDataSourceParam('endDate', "");
        datatable.search('');
    }

    loaddatetimepicker();

    function loaddatetimepicker() {
        $('.m_datepicker').datepicker({
            format: "mm/dd/yyyy",
            todayBtn: "linked",
            clearBtn: true,
            todayHighlight: true,
            templates: {
                leftArrow: '<i class="la la-angle-left"></i>',
                rightArrow: '<i class="la la-angle-right"></i>'
            }
        });
    }

    $('#btnSearch').on('click', function () {
        dosearch();
    });

    function dosearch() {

        datatable.setDataSourceParam('SearchString', $('#generalSearch').val());
        if ($('#createdBySelectd').val().length == 1 && $('#createdBySelectd').val()[0] == "") {
            datatable.setDataSourceParam('createdBy', null);
        } else {
            datatable.setDataSourceParam('createdBy', $('#createdBySelectd').val());
        }
        if ($('#approvedBySelectd').val().length == 1 && $('#approvedBySelectd').val()[0] == "") {
            datatable.setDataSourceParam('approvedBy', null);
        } else {
            datatable.setDataSourceParam('approvedBy', $('#approvedBySelectd').val());
        }
        datatable.setDataSourceParam('startDate', $('#DateFrom').val());
        datatable.setDataSourceParam('endDate', $('#DateTo').val());
        datatable.load();

    }

    $('#kt_datatable').on('click', '.deletePlaybook', function () {
        $('#kt_modal_Delete').modal('show');
        var id = $(this).attr('data-id');
        $('#deleteId').val(id);
    });

    $('#btnYesDelete').on('click', function () {
        var id = $('#deleteId').val();
        $('#kt_modal_Delete').modal('hide');

        $.ajax({
            type: 'POST',
            url: '/nera/playbook/api/check-used-by-workflow',
            data: {id: id},
            success: function (data) {
                if (data) {
                    Swal.fire({
                        text:  'The current playbook is used by the workflow. Cannot delete',
                        type:  'error',
                        timer: 3000,
                        showConfirmButton: false
                    });
                } else {
                    callDeleteApi(id);
                }
            }
        });
    });

    function callDeleteApi(id) {
        $.ajax({
            type: 'POST',
            url: '/nera/playbook/api/delete-by-id',
            data: {id: id},
            success: function (data) {
                countPlaybookApproved();
                Swal.fire({
                    text:  'Delete playbook successful',
                    type:  'success',
                    timer: 3000,
                    showConfirmButton: false
                });
                datatable.load();
            },
        });
    }

    $('#kt_datatable').on('click', '.sendToApproval', function () {
        $('#kt_modal_Approval').modal('show');
        var id = $(this).attr('data-id');
        $('#approvalId').val(id);
    });

    $('#btnYesApproval').on('click', function () {
        var id = $('#approvalId').val()
        callSendToApprovalApi(id);
    });

    function callSendToApprovalApi(id) {
        $.ajax({
            type: 'POST',
            url: '/nera/playbook/api/send-approval-playbook-by-id',
            data: {id: id},
            success: function (data) {
                countPlaybookApproved();
                Swal.fire({
                    text:  'Request for Playbook approval has been sent successfully',
                    type:  'success',
                    timer: 3000,
                    showConfirmButton: false
                });
                // $('.deletemessage').removeClass('hideclass');
                // setTimeout(function () {
                //     $('.deletemessage').addClass('hideclass');
                // }, 4000);
                $('#kt_modal_Approval').modal('hide');
                datatable.load();
            },
            error: function (err) {
                //
            },
        });
    }

    function countPlaybookApproved() {
        $.ajax({
            type: 'GET',
            url: '/nera/playbook/api/count-approved-playbook',
            beforeSend: function () {
                //
            },
            success: function (data) {
                $('.setPlaybookApprovedCount').text('Playbook Approved (' + data + ')');
            },
            error: function (err) {
                //
            },
        });

    }

    datatable.on('kt-datatable--on-init', function () {
        fixedTableHeader();
    });
});

