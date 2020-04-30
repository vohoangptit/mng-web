
function showUpdatePopup(idPlayBook) {
    var json = {id: idPlayBook};
    $.ajax({
        type: 'GET',
        url: '/nera/playbook/api/get-by-id',
        data: json,
        contentType: "application/json",
        success: function (data) {
            $('#playBookName').val(data.name);
            readContentFile(data.sourceUrl);
            $('#tblPlaybookInput').removeClass('hideclass');
            if(data.playbookInput.length > 0){
                $('#tblPlaybookInput > tbody').empty();
                var get = data.playbookInput;
                for(let i = 0; i < get.length; i++){
                    let col1 = get[i].type;
                    let col2 = get[i].variable;
                    let col3 = get[i].value;
                    appendInputRow(col1, col2, col3);
                }
            }
            if(data.playbookOutput.length > 0){
                $('#tblPlaybookOutput > tbody').empty();
                var outputList = data.playbookOutput;
                for(let i = 0; i < outputList.length; i++){
                    let variable = outputList[i].variable;
                    let value = outputList[i].value;
                    appendOutputRow(variable, value);
                }
            }
            $('#modalDetailPlaybook').modal();
        }
    });

    function appendInputRow(col1, col2, col3) {
        $('#tblPlaybookInput').removeClass('hideclass');
        $('#tblPlaybookInput > tbody:last').append('<tr class="standardInput">'
            + '<td style="text-align:center; max-width:11em; word-wrap: break-word" class="outputVariable">' + col1 + '</td>'
            + '<td style="text-align:center; max-width:11em; word-wrap: break-word" class="outputValue">' + col2 + '</td>'
            + '<td style="text-align:center; max-width:11em; word-wrap: break-word" class="outputValue">' + col3 + '</td>'
            + '</tr>');
    }

    function appendOutputRow(variable, value) {
        $('#tblPlaybookOutput').removeClass('hideclass');
        $('#tblPlaybookOutput > tbody:last').append('<tr class="standardOutput">'
            + '<td style="text-align:center; max-width:11em; word-wrap: break-word" class="outputVariable">' + variable + '</td>'
            + '<td style="text-align:center; max-width:11em; word-wrap: break-word" class="outputValue">' + value + '</td>'
            + '</tr>');
    }

    function readContentFile(sourceUrl){
        if(sourceUrl == ""){
            return false;
        }
        $("#getContentOfFile").load(sourceUrl, function(responseTxt, txtStatus, jqXHR){
            $('.displayitem').empty();
            if(txtStatus == "success"){
                $('.displayitem').append('<span><a target="_blank" rel="noopener noreferrer" href="' + sourceUrl + '">' + sourceUrl.split('/').pop() + '</a></span>');
            }
            if(txtStatus == "error"){
                console.log("Error: " + jqXHR.status + ", " + jqXHR.statusText);
            }
        });

    }
}

var datatable;
$(document).ready(function() {
    $('#job-management-menu-left').removeClass('kt-menu__item--open');
    datatable = $('#kt_datatable').KTDatatable({
        // datasource definition
        data: {
            type: 'remote',
            source: {
                read: {
                    method: 'GET',
                    url: '/nera/api/workflow/listing-and-filter',
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
                title: 'Workflow Name',
            },
            {
                field: 'description',
                title: 'Description'
            },
            {
                field: 'version',
                title: 'Version',
                template: function(row) {
                    return "Version " + row.version;
                }
            },
            {
                field: 'createdDate',
                title: 'Created Date'
            },
            {
                field: 'createdBy',
                title: 'Creator'
            },
            {
                field: 'Actions',
                title: 'Actions',
                sortable: false,
                overflow: 'visible',
                textAlign: 'center',
                template: function(row) {
                    var id = row.id;
                    let htmlUpdate  = '';
                    let htmlDelete = '';
                    // let htmlClone = '<span><a href="#" class="btn btn-hover-brand btn-icon btn-pill cloneWorkflow" data-id="' + id + '"><i class="la la-clone"></i></a></span>';
                    let htmlClone = '<a href="/clone/drawing/' + id + '"  class="btn btn-hover-brand btn-icon btn-pill" data-id="' + id + '"><i class="la la-clone"></i></a>';
                    if(hasUpdatePermission) {
                        htmlUpdate = '<a href="/drawing/' + id + '"  class="btn btn-hover-brand btn-icon btn-pill" data-id="' + id + '"><i class="la la-edit"></i></a>';
                    }

                    if(hasDeletePermission){
                        htmlDelete = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill deleteWorkflow" data-id="' + id + '"><i class="la la-trash"></i></a>';
                    }
                    return htmlClone + htmlUpdate + htmlDelete;
                },
            }],
    });

    // $(document).on('click', '.cloneWorkflow' , function() {
    //     cloneWorkflow($(this).attr('data-id'));
    // });
    //
    // function cloneWorkflow(id){
    //     $('#cloneId').val(id);
    //     $('#kt_modal_clone').modal('show');
    // };
    //
    // $('#btnYesClone').on('click', function(){
    //     var workflowId = $('#cloneId').val();
    //     $.ajax({
    //         type: 'POST',
    //         url: '/nera/api/clone-workflow',
    //         data: {id:workflowId},
    //         success: function (data) {
    //             $('#kt_modal_clone').modal('hide');
    //             if (data) {
    //                 Swal.fire({
    //                     text:  'Clone Workflow Successful',
    //                     type:  'success',
    //                     timer: 2000,
    //                     showConfirmButton: false
    //                 });
    //                 datatable.load();
    //             } else {
    //                 Swal.fire({
    //                     text:  'Clone Workflow Fail',
    //                     type:  'error',
    //                     timer: 2000,
    //                     showConfirmButton: false
    //                 });
    //             }
    //         },
    //     });
    // });


    $('#btnReset').unbind().click(function(){
        reset();
    });

    function reset() {
        $('#generalSearch').val('');
        $('#DateFrom').val('');
        $('#DateTo').val('');
        $('#createdBySelected').val('');
        $('.select2-selection__rendered').html('');

        datatable.setDataSourceParam('createdBy', null);
        datatable.setDataSourceParam('startDate', "");
        datatable.setDataSourceParam('endDate', "");
        datatable.search('');
    }

    loaddatetimepicker();
    function loaddatetimepicker()
    {
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
        if($('#createdBySelected').val().length == 1 && $('#createdBySelected').val()[0] == "")
        {
            datatable.setDataSourceParam('createdBy', null);
        }
        else{
            datatable.setDataSourceParam('createdBy', $('#createdBySelected').val());
        }
        datatable.setDataSourceParam('startDate', $('#DateFrom').val());
        datatable.setDataSourceParam('endDate', $('#DateTo').val());
        datatable.load();

    }

    $('#kt_datatable').on('click', '.deleteWorkflow', function(){
        $('#kt_modal_Delete').modal('show');
        var id = $(this).attr('data-id');
        $('#deleteId').val(id);
    });

    $('#btnYesDelete').on('click', function(){
        var id = $('#deleteId').val();
        $('#kt_modal_Delete').modal('hide');
        $.ajax({
            type: 'POST',
            url: '/nera/job/api/check-used-by-workflow',
            data: {workflowId: id},
            success: function (data) {
                if (data) {
                    Swal.fire({
                        text:  'The current workflow is used by the outstanding job. Cannot delete',
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

    function callDeleteApi(id)
    {
        $.ajax({
            type: 'POST',
            url: '/nera/api/workflow/delete-workflow',
            data: {id: id},
            success: function (data) {
                Swal.fire({
                    text:  'Delete Workflow Successful',
                    type:  'success',
                    timer: 2000,
                    showConfirmButton: false
                });
                datatable.load();
                loadCreatedByAll();
            },
        });
    }
    loadCreatedByAll();
    function loadCreatedByAll()
    {
        $("#createdBySelected").empty();
        $.ajax({
            type: 'GET',
            url: '/nera/api/workflow/created-person',
            success: function (data) {
                var re = data;
                for(var i = 0; i < re.length; i++)
                {
                    var op = new Option(re[i], re[i]);
                    $("#createdBySelected").append(op);
                }
                $('#createdBySelected').select2({ placeholder: "Created By (All)", allowClear: false, width: 'resolve', dropdownAutoWidth: true });
            },
        });
    }

    showPopupWorkflow();
    function showPopupWorkflow() {
        var nameWorkflow = Cookies.get('workflowName');
        Cookies.set('workflowName', '');
        if(nameWorkflow != null && nameWorkflow != ''){
            Swal.fire({
                title: 'Save Successfully',
                text:  'Workflow '+nameWorkflow+' has been saved successfully',
                type:  'success',
                timer: 3000,
                showConfirmButton: false
            })
        }
    }
    if (typeof datatable !== 'undefined') {
        datatable.on('kt-datatable--on-init', function(){
            fixedTableHeaders();
        });
    }
    
    var $fixedHeader = $("#header-fixed");
    
    function fixedTableHeaders(){
    	if(!$fixedHeader.length){
        	$fixedHeader = $("#header-fixed");
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
    
    $(window).bind("scroll", function() {
        if (typeof $(".kt-datatable__table").offset() !== 'undefined') {
            var offset = $(this).scrollTop() + 65;
            var tableOffset = $(".kt-datatable__table").offset().top;

            if(!$fixedHeader.length){
                $fixedHeader = $("#header-fixed");
            }

            if (offset >= tableOffset && $fixedHeader.is(":hidden")) {
                $fixedHeader.show();
            }
            else if (offset < tableOffset) {
                $fixedHeader.hide();
            }
        }
    });
});

