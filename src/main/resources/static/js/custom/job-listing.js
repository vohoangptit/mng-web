var datatable;
$(document).ready(function() {
    datatable = $('#kt_datatable').KTDatatable({
        // datasource definition
        data: {
            type: 'remote',
            source: {
                read: {
                    method: 'GET',
                    url: '/nera/job/api/listing-and-filter',
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
                field: 'name',
                title: 'Job Name',
                textAlign: 'left'
            },
            {
                field: 'workflowName',
                title: 'Workflow Name',
                textAlign: 'left'
            },
            {
                field: 'description',
                title: 'Job Description',
                textAlign: 'left'
            },
            {
                field: 'createdBy',
                title: 'Created By'
            },
            {
                field: 'createdDate',
                title: 'Created At'
            },
            {
                field: 'host.isActive',
                title: 'Dependency',
                template: function(row) {
                    var htmlDependency;
                    if (!row.notifyJob) {
                        htmlDependency = '<button style="border-radius: 5px; border: 1px solid #1eb7ae; background-color: #1eb7ae;" disabled><span style="font-family: Poppins;color:var(--white)">None</span></button>';
                    } else {
                        htmlDependency = '<button style="border-radius: 5px; border: 1px solid #7f7f7f; background-color: #7f7f7f;" disabled><span style="font-family: Poppins;color:var(--white)">InActive</span></button>';
                    }
                    return htmlDependency;
                },
            },
            {
                field: 'isActive',
                title: 'Status',
                width: 70,
                sortable: true,
                template: function(row) {
                    var status = row.active;
                    var htmlEdit;
                    if (status) {
                        htmlEdit = '<button style="border-radius: 5px; border: 1px solid #1eb7ae; background-color: #1eb7ae;" disabled><span style="font-family: Poppins;color:var(--white)">Active</span></button>';
                    } else {
                        htmlEdit = '<button style="border-radius: 5px; border: 1px solid #7f7f7f; background-color: #7f7f7f;" disabled><span style="font-family: Poppins;color:var(--white)">InActive</span></button>';
                    }
                    return htmlEdit;
                },
            },
            {
                field: 'Actions',
                title: 'Actions',
                sortable: false,
                overflow: 'visible',
                textAlign: 'center',
                template: function(row, index, datatable) {
                    var id = row.id;
                    var htmlUpdate  = '';
                    var htmlDelete = '';

                    if(hasUpdatePermission) {
                        htmlUpdate = '<a href="/nera/api/job-detail/' + id + '"  class="btn btn-hover-brand btn-icon btn-pill" data-id="' + id + '"><i class="la la-edit"></i></a>'
                    }

                    if(hasDeletePermission){
                        htmlDelete = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill deleteJob" data-id="' + id + '"><i class="la la-trash"></i></a>';
                    }
                    return htmlUpdate + htmlDelete;
                },
            }],
        // rows: {
        //     afterTemplate: function (row, data, index) {
        //         if(data.notifyJob) {
        //             row[0].className = row[0].className + ' background-row-table';
        //         }
        //     }
        // }
    });

    $('#btnReset').unbind().click(function(){
        reset();
    });

    function reset() {
        $('#generalSearch').val('');
        $('#DateFrom').val('');
        $('#DateTo').val('');
        $('#createdBySelected').val('');
        $('#dependencySelected').val('');
        $('#statusSelected').val('');
        $('.select2-selection__rendered').html('');

        datatable.setDataSourceParam('createdBy', null);
        datatable.setDataSourceParam('startDate', '');
        datatable.setDataSourceParam('endDate', '');
        datatable.setDataSourceParam('status', '');
        datatable.search('');
    }

    loadDateTimePicker();
    function loadDateTimePicker()
    {
        $('.m_datepicker').datepicker({
            format: 'mm/dd/yyyy',
            todayBtn: 'linked',
            clearBtn: true,
            todayHighlight: true,
            templates: {
                leftArrow: '<i class="la la-angle-left"></i>',
                rightArrow: '<i class="la la-angle-right"></i>'
            }
        });
    }

    $('#btnSearch').on('click', function(){
        var from = $('#DateFrom').val();
        var to = $('#DateTo').val();
        if(from !== '' && to !== '' && from > to) {
            Swal.fire({
                text:  'To (Created At) must be greater than or equal to From (Created At)',
                type:  'error',
                timer: 2000,
                showConfirmButton: false
            });
            return;
        }
        doSearch();
    });


    function doSearch() {
        datatable.setDataSourceParam('SearchString', $('#generalSearch').val());
        datatable.setDataSourceParam('createdBy', $('#createdBySelected').val());
        datatable.setDataSourceParam('dependency', $('#dependencySelected').val());
        datatable.setDataSourceParam('startDate', $('#DateFrom').val());
        datatable.setDataSourceParam('endDate', $('#DateTo').val());
        datatable.setDataSourceParam('status', $('#statusSelected').val());
        datatable.load();

    }

    $('#kt_datatable').on('click', '.deleteJob', function(){
        $('#kt_modal_Delete').modal('show');
        var id = $(this).attr('data-id');
        $('#deleteId').val(id);
    });

    $('#btnYesDelete').on('click', function(){
        var id = $('#deleteId').val();
        callDeleteApi(id);
    });

    function callDeleteApi(id)
    {
        $.ajax({
            type: 'POST',
            url: '/nera/job/api/delete-job',
            data: {jobId: id},
            success: function (data) {
                if(data) {
                    Swal.fire({
                        title: 'Delete Job(s)',
                        text:  'Delete Job(s) Successful',
                        type:  'success',
                        timer: 2000,
                        showConfirmButton: false
                    });
                    datatable.load();
                    loadCreatedByAll();
                } else {
                    Swal.fire({
                        text:  'Cannot delete job which is planned for execution',
                        type:  'error',
                        timer: 2000,
                        showConfirmButton: false
                    });
                }
                $('#kt_modal_Delete').modal('hide');
            },
            error: function (err) {
                //
            },
        });
    }
    loadCreatedByAll();
    function loadCreatedByAll()
    {
        $("#createdBySelected").empty();
        $.ajax({
            type: 'GET',
            url: '/nera/job/api/created-person',
            success: function (data) {
                var re = data;
                $("#createdBySelected").append(new Option('Created By (All)', '', true));
                for(var i = 0; i < re.length; i++)
                {
                    var op = new Option(re[i], re[i]);
                    $("#createdBySelected").append(op);
                }
            },
        });
    }

    $('#kt_datatable').on('click', '.deleteWorkflow', function(){
        $('#kt_modal_Delete').modal('show');
        var id = $(this).attr('data-id');
        $('#deleteId').val(id);
    });

    if (typeof datatable !== 'undefined') {
        datatable.on('kt-datatable--on-init', function(){
            fixedTableHeaders();
        });
    }

    var $fixedHeader = $("#header-fixed");

    function fixedTableHeaders(){
        if(!$fixedHeader.length){
            $fixedHeader = $('#header-fixed');
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

    $(window).bind('scroll', function() {
        if (typeof $(".kt-datatable__table").offset() !== 'undefined') {
            var offset = $(this).scrollTop() + 65;
            var tableOffset = $('.kt-datatable__table').offset().top;

            if(!$fixedHeader.length){
                $fixedHeader = $('#header-fixed');
            }

            if (offset >= tableOffset && $fixedHeader.is(':hidden')) {
                $fixedHeader.show();
            }
            else if (offset < tableOffset) {
                $fixedHeader.hide();
            }
        }
    });
});