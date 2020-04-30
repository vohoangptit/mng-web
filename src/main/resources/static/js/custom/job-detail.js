var datatableJobInput;
function selectAllHostModal(nameUser) {
    if(nameUser.checked) {
        $('.hostJob').each(function(){
            this.checked = true;
        });
    } else {
        $('.hostJob').each(function(){
            this.checked = false;
        });
    }
}

function selectAllHostMain(checkBoxAllMain) {
    if(checkBoxAllMain.checked) {
        $('.hostJobMain').each(function(){
            this.checked = true;
        });
    } else {
        $('.hostJobMain').each(function(){
            this.checked = false;
        });
    }
}

var fileListing = [];
function getFileDownload($i) {
    var get = $i;
    var name = "";
    for(var i = 0; i < fileListing.length; i++){
        if(fileListing[i].id === get){
            name = fileListing[i].filename;
            downloadFileCSV(name);
            reDownload();
            return false;
        }
    }
}

var listFile = [];
loadFileAll();

function loadFileAll()
{
    $.ajax({
        type: 'GET',
        url: '/nera/file-management/api/get-all',
        success: function (data) {
            var re = data;
            fileListing = re;
            for(var i = 0; i < re.length; i++)
            {
                // var op = new Option(re[i].name, re[i].id);
                // $("#selectedFile").append(op);
                var file = {
                    nameFile: re[i].name,
                    idFile: re[i].id
                };
                listFile.push(file);
                // var te = '<div>'+ re[i].name + '-' + '<a href="#" data-id="'+ re[i].id +'" class="redownload" style="color: blue;">' + re[i].filename + '</a></div>';
                // datatest.push({id: re[i].id, text: te});
            }
            // appendFileRow(datatest);
        },
    });
}



function removeTitle(){
    $('.select2-selection__rendered').hover(function () {
        $(this).removeAttr('title');
    });
}


function checkFieldName() {
    if($('#jobName').val() === '') {
        $('#validateJobName').text('The job name is mandatory');
        $('#validateJobName').removeClass('hideclass');
    } else {
        $('#validateJobName').text('');
        $('#validateJobName').addClass('hideclass');
    }
}

$(document).ready(function() {
    $('#job-management-menu-left').addClass('kt-menu__item--open');
    var idJob = $('#idJob').val();
    var datatable;
    var hostDatatable;
    checkIdJob();
    function checkIdJob() {
        getDetailJob();
        if(idJob != '') {
            $('#tabInventoryWorkflow').removeClass('hideclass');
            $('#btnDeleteJob').removeClass('hideclass');
        } else {
            workflowDetailJob(0);
        }
    }
    $('#workflowSelected').change(function(){
        if (typeof datatableJobInput !== 'undefined') {
            datatableJobInput.destroy();
        }
        getDetailJobInput($(this).find(':selected').attr('value'));
    });


    $('#tab-group').on('click', function(){
        if (typeof datatableJobInput !== 'undefined') {
            datatableJobInput.destroy();
        }
        getDetailJobInput($('#workflowSelected').find(':selected').attr('value'));
    });

    function getDetailJobInput(idWorkflow) {
        datatableJobInput = $('#kt_datatable_job_input').KTDatatable({
            // datasource definition
            data: {
                type: 'remote',
                source: {
                    read: {
                        method: 'GET',
                        url: '/nera/job/api/get-job-input-by-id',
                        data: {
                            id: idWorkflow,
                            idJob: idJob,
                        },
                        map: function(raw) {
                            if (typeof raw !== 'undefined') {
                                var dataSet = raw;
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
                height: 400,
                footer: false,
            },

            selector: {
                class: 'kt-checkbox--solid'
            },

            // column sorting
            sortable: false,
            pagination: false,

            search: {
                input: $('#hostSearchKey')
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
                    field: 'variable',
                    title: 'Variable',
                },
                {
                    field: 'type',
                    title: 'Input Type',
                    template: function(row) {
                        var nameType  = '';
                        if(row.type === 1) {
                            nameType = 'Text';
                        } else {
                            nameType = 'File';
                        }
                        return nameType;
                    },
                },
                {
                    field: 'value',
                    title: 'Value',
                    template: function(row) {
                        var typeField  = '';
                        var valueInput = row.value != null ? row.value : '';
                        if(row.type === 1) {
                            typeField = '<input type="text" name="testField" data-id="'+row.id+'" value="'+valueInput+'" style="width: 100%">';
                        } else {
                            // typeField = '<select class="form-control hideclass" id="selectedFile"></select><select class="form-control" id="selectedFileTest" onchange="getFileDownload(value)" style="width:100%; height:38px;"></select>';
                            typeField = '<select class="form-control" data-id="'+row.id+'" style="height:38px;">';
                            for(var i=0; i<listFile.length; i++) {
                                typeField += '<option value="'+ listFile[i].idFile +'"';
                                if(valueInput === listFile[i].idFile) {
                                    typeField += ' selected ';
                                }
                                typeField +=  '>'+listFile[i].nameFile+'</option>';
                            }
                            typeField += '</select>';
                        }
                        return typeField;
                    },
                }],
        });
        datatableJobInput.on('kt-datatable--on-ajax-done', function (e) {
            loadFileAll();
        });
    }

    function getDetailJob() {
        var dataSearch = $('#hostSearchKey').val();
        datatable = $('#kt_datatable_host').KTDatatable({
            // datasource definition
            data: {
                type: 'remote',
                source: {
                    read: {
                        method: 'GET',
                        url: '/nera/job/api/get-by-id',
                        data: {id: idJob,
                            searchString: dataSearch},
                        map: function(raw) {
                            if (typeof raw !== 'undefined') {
                                var dataSet = raw.listHost;
                                if (typeof raw.name !== 'undefined') {
                                    $('#jobName').val(raw.name);
                                    $('#jobDescription').text(raw.description);
                                    workflowDetailJob(raw.workflowId);
                                    if(raw.active) {
                                        $("#radioA").attr('checked', 'checked');
                                    } else {
                                        $("#radioI").attr('checked', 'checked');
                                    }
                                    $('.totalNum').text(raw.total);
                                }
                            }
                            return dataSet;
                        }
                    }
                },
                pageSize: 10,
                saveState: {
                    cookie: false,
                    webstorage: false
                },
                serverPaging: false,
                serverFiltering: true,
                serverSorting: false
            },

            // layout definition
            layout: {
                scroll: true,
                height: 400,
                footer: false,
            },

            selector: {
                class: 'kt-checkbox--solid'
            },

            // column sorting
            sortable: false,
            pagination: false,
            noRecords: 'No records found',
            // search: {
            //     input: $('#hostSearchKey')
            // },

            // columns definition
            columns: [
                {
                    field: 'checkBox',
                    title: '<input class="caseall" type="checkbox" name="checkAllData" value="" onClick="selectAllHostMain(this)">',
                    textAlign: 'center',
                    width: 20,
                    template: function(row) {
                        var idHost = row.id;
                        var htmlCheck = '<input class="hostJobMain" type="checkbox" name="" value="'+idHost+'">';
                        return htmlCheck;
                    },
                },
                {
                    field: 'no',
                    title: 'S/N',
                    sortable: false,
                    width: 30,
                    textAlign: 'left'
                },
                {
                    field: 'name',
                    title: 'Host Name',
                    textAlign: 'left'
                },
                {
                    field: 'ipAddress',
                    title: 'IP Address'
                },
                {
                    field: 'port',
                    title: 'Port',
                    width: 80
                },
                {
                    field: 'groupName',
                    title: 'Group Name'
                },
                {
                    field: 'active',
                    title: 'Status',
                    template: function(row) {
                        var htmlEdit;
                        if (row.active) {
                            htmlEdit = '<button style="border-radius: 5px; border: 1px solid #1eb7ae; background-color: #1eb7ae;" disabled><span style="font-family: Poppins;color:var(--white)">Active</span></button>';
                        } else {
                            htmlEdit = '<button style="border-radius: 5px; border: 1px solid #7f7f7f; background-color: #7f7f7f;" disabled><span style="font-family: Poppins;color:var(--white)">InActive</span></button>';
                        }
                        return htmlEdit;
                    }
                },
                {
                    field: 'Actions',
                    title: 'Actions',
                    overflow: 'visible',
                    textAlign: 'center',
                    template: function(row) {
                        var id = row.id;
                        var htmlDelete = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill deleteHost" data-id="' + id + '" title="Delete host"><i class="la la-trash"></i></a>';
                        return htmlDelete;
                    },
                }],
        });
        datatable.on('kt-datatable--on-layout-updated', function (e) {
            $(document).on('click', '.deleteHost' , function() {
                listHostDelete.push($(this).attr('data-id'));
                $('#modHostDeleteConfirmation').modal('show');
            });
        });
    }

    $('#hostSearchKey').keyup(function() {
        if (typeof datatable !== 'undefined') {
            datatable.destroy();
            getDetailJob();
        }

    });

    function valueJobInput(nameField, arrJobInput) {
        var checkField = true;
        $('#kt_datatable_job_input tbody tr td').find(nameField).each(function(e){
            var valueJob = $(this).val();
            if(valueJob === '') {
                checkField = false;
                return;
            }
            var jobInputID = $(this).attr('data-id');
            var objectJobInputText = {
                value: valueJob,
                jobInputId: jobInputID,
                jobId: idJob
            };
            arrJobInput.push(objectJobInputText);
        });
        return checkField;
    }
    $('#btnClearJobInput').on('click', function () {
        $.ajax({
            type: 'GET',
            url: '/nera/job/api/get-workflow-by-id',
            data: {jobId: idJob},
            success: function (data) {
                if(data.workflowId > 0) {
                    $('#workflowSelected').val(data.workflowId);
                    reloadTableWorkflow();
                } else {
                    $('#workflowSelected').val('');
                    reloadTableWorkflow();
                }
            },
        });
    });

    function reloadTableWorkflow() {
        if (typeof datatableJobInput !== 'undefined') {
            datatableJobInput.destroy();
        }
        getDetailJobInput($('#workflowSelected').find(':selected').attr('value'));
    }

    $('#btnAddJobInput').on('click', function(){
        var arrJobInput = [];
        var jobWorkflowObject = {};
        jobWorkflowObject['jobId'] = idJob;
        jobWorkflowObject['workflowId'] = $('#workflowSelected').val();
        jobWorkflowObject['workflowName'] = $('#workflowSelected option:selected').text();
        if(!valueJobInput('input', arrJobInput)) {
            Swal.fire({
                text:  'The value of the global variables are mandatory',
                type:  'error',
                timer: 2000,
                showConfirmButton: false
            });
            return;
        }
        valueJobInput('select', arrJobInput);
        jobWorkflowObject['jobPayload'] = arrJobInput;
        $.ajax({
            type: 'POST',
            url: '/nera/job/api/add-job-input',
            data: JSON.stringify(jobWorkflowObject),
            contentType: "application/json",
            success: function(data) {
                if(data.code === 200) {
                    Swal.fire({
                        text: 'Update Job Success',
                        type:  'success',
                        timer: 2000,
                        showConfirmButton: false
                    });
                } else {
                    Swal.fire({
                        text:  'Update Job Fail',
                        type:  'error',
                        timer: 2000,
                        showConfirmButton: false
                    });
                }
            }
        });
    });

    function workflowDetailJob(workflowId) {
        $('#workflowSelected').empty();
        $.ajax({
            type: 'GET',
            url: '/nera/api/workflow/listing',
            success: function (data) {
                var re = data;
                $('#workflowSelected').append(new Option('Select Workflow', '', true));
                for(var i = 0; i < re.length; i++)
                {
                    var op = '';
                    if(workflowId > 0 && workflowId === re[i].id) {
                        op = new Option(re[i].name, re[i].id, true, true);
                    } else {
                        op = new Option(re[i].name, re[i].id);
                    }
                    $("#workflowSelected").append(op);
                }
            }
        })
    }


    var listHostDelete = [];
    $('#btnDeleteHost').on('click', function () {
        $('#kt_datatable_host tbody tr').each(function(e){
            if($(this).find('td').eq(0).find('input').is(':checked')){
                var hostData = $(this).find('td').eq(0).find('input').val();
                listHostDelete.push(hostData);
            }
        });
        if(listHostDelete.length === 0) {
            Swal.fire({
                text:  "Require to select the host before to delete.",
                type:  'error',
                timer: 2000,
                showConfirmButton: false
            });
        } else {
            $('#modHostDeleteConfirmation').modal('show');
        }
    });

    $('#btnYesDeleteHost').on('click', function(){
        var jsonHost = {
            id: idJob,
            hostsId: listHostDelete,
        };
        listHostDelete = [];
        $.ajax({
            type: 'POST',
            url: '/nera/job/api/remove-host-job',
            data: JSON.stringify(jsonHost),
            contentType: "application/json",
            success: function (data) {
                if (data.code != 200) {
                    return false;
                }
                Swal.fire({
                    title: "Remove Host",
                    text:  "Remove Host Successful",
                    type:  'success',
                    timer: 2000,
                    showConfirmButton: false
                });
                if (typeof datatable !== 'undefined') {
                    datatable.load();
                }
                $('#modHostDeleteConfirmation').modal('hide');
            },
        });

    });
    // loadGroupAll();
    // function loadGroupAll()
    // {
    //     $("#groupSelected").empty();
    //     $.ajax({
    //         type: 'GET',
    //         url: '/nera/inventory/api/search-group-host',
    //         data: {searchString: ''},
    //         success: function (data) {
    //             var re = data;
    //             $("#groupSelected").append(new Option('Select Group (All)', '', true));
    //             for(var i = 0; i < re.length; i++)
    //             {
    //                 var op = new Option(re[i].name, re[i].id);
    //                 $("#groupSelected").append(op);
    //             }
    //         },
    //     });
    // };

    // $('#groupSelected').change(function(){
    //     loadHostAll($('#groupSelected').val());
    // });
    // loadHostAll('');
    // function loadHostAll(groupId)
    // {
    //     $("#hostSelected").empty();
    //     $.ajax({
    //         type: 'GET',
    //         url: '/nera/job/api/get-host-by-job',
    //         data: {id: 0,
    //             groupSelected: groupId},
    //         success: function (data) {
    //             var re = data.listInventory;
    //             $("#hostSelected").append(new Option('Select Host (All)', '', true));
    //             for(var i = 0; i < re.length; i++)
    //             {
    //                 var op = new Option(re[i].name, re[i].id);
    //                 $("#hostSelected").append(op);
    //             }
    //         },
    //     });
    // };


    function getListHost() {
        hostDatatable = $('#kt_datatable_add_Host').KTDatatable({
            // datasource definition
            data: {
                type: 'remote',
                source: {
                    read: {
                        method: 'GET',
                        url: '/nera/job/api/get-host-by-name',
                        // data: {id: idJob,
                        //     hostSelected: hostId,
                        //     groupSelected: groupId},
                        map: function(raw) {
                            if (typeof raw !== 'undefined') {
                                var dataSet = raw.listInventory;
                                // if(raw.listHostId.length > 0) {
                                //     listIdNotFound = raw.listHostId;
                                // }
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
                serverFiltering: true,
                serverSorting: false
            },

            // layout definition
            layout: {
                scroll: true,
                height: 500,
                footer: false,
            },

            selector: {
                class: 'kt-checkbox--solid'
            },
            // column sorting
            sortable: false,
            pagination: false,
            noRecords: 'No records found',
            search: {
                input: $('#generalSearch')
            },

            // columns definition
            columns: [
                {
                    field: 'checkBox',
                    title: '<input class="caseall" type="checkbox" name="checkAllData" value="" onClick="selectAllHostModal(this)">',
                    textAlign: 'center',
                    width: 20,
                    template: function(row) {
                        var idHost = row.id;
                        var htmlCheck = '<input class="caseall hostJob" type="checkbox" name="" ';
                        if(row.active) {
                            htmlCheck += 'checked ';
                        }
                        htmlCheck += 'value="'+idHost+'">';
                        return htmlCheck;
                    },
                },
                {
                    field: 'no',
                    title: 'S/N',
                    sortable: false,
                    width: 30,
                    textAlign: 'center'
                },
                {
                    field: 'name',
                    title: 'Host Name',
                    textAlign: 'left'
                },
                {
                    field: 'ipAddress',
                    title: 'IP Address'
                },
                {
                    field: 'port',
                    title: 'Port'
                },
                {
                    field: 'groupName',
                    title: 'Group Name'
                }],
        });
        hostDatatable.on('kt-datatable--on-ajax-done', function (e) {
            $('#modalAddHost').modal('show');
        });
    }

    $('#btnPrintWorkflow').on('click', function(){
        var nameWorkflow = $('#workflowSelected option:selected').text();
        window.open("/nera/api/download/imageWorkflow?fileName=" + nameWorkflow);
        // $.ajax({
        //     type: 'GET',
        //     url: '/nera/api/download/imageWorkflow',
        //     data: {fileName: nameWorkflow},
        //     success: function (data) {
        //
        //     },
        // });
    });

    $('#viewDetailWorkflow').on('click', function(){
        var id = $('#workflowSelected').val();
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
                        title: 'Delete Job(s)',
                        text:  "Delete Job(s) Fail",
                        type:  'error',
                        timer: 2000,
                        showConfirmButton: false
                    });
                }
                $('#viewDetailImageWorkflow').modal('show');
            },
        });
    });

    $('#btnSearch').on('click', function(){
        if (typeof hostDatatable !== 'undefined') {
            hostDatatable.destroy();
        }
        // getListHost($('#generalSearch').val(),$('#hostSelected').val());
        // doSearch();
    });

    // function doSearch() {
    //     hostDatatable.setDataSourceParam('hostSelected', $('#hostSelected').val());
    //     hostDatatable.setDataSourceParam('groupSelected', $('#groupSelected').val());
    //     hostDatatable.load();
    // };

    $(document).on('click', '#btnAddHost' , function() {
        // $('#kt_datatable_add_Host tbody tr').each(function(e){
        //     if($(this).find('td').eq(0).find('input').is(':checked')){
        //         var hostData = $(this).find('td').eq(0).find('input').val();
        //         listHost.push(hostData);
        //     }
        // })
        if (typeof hostDatatable !== 'undefined') {
            hostDatatable.destroy();
        }
        $('#generalSearch').val('');
        getListHost();

        // if($('#kt_datatable_add_Host').text() === '') {
        //     getListHost();
        // } else {
        //     $('#modalAddHost').modal('show');
        // }
        // hostDatatable.reload();
        // $('#modalAddHost').modal('show');
    });

    $('#btnCancelJob').on('click', function() {
        window.location.href = '/menu/job-listing';
    });

    $('#btnDeleteJob').on('click', function() {
        $('#kt_modal_Delete').modal('show');
    });

    $('#btnYesDeleteJob').on('click', function() {
        $.ajax({
            type: 'POST',
            url: '/nera/job/api/delete-job',
            data: {jobId: idJob},
            success: function (data) {
                if(data) {
                    window.location.href = '/menu/job-listing';
                } else {
                    Swal.fire({
                        text:  "Cannot delete job which is planned for execution",
                        type:  'error',
                        timer: 2000,
                        showConfirmButton: false
                    });
                    $('#kt_modal_Delete').modal('hide');
                }
            },
        });
    });

    $('#btnHostSave').on('click', function(){
        var listHost = [];
        $('#kt_datatable_add_Host tbody tr').each(function(e){
            if($(this).find('td').eq(0).find('input').is(':checked')){
                var hostData = $(this).find('td').eq(0).find('input').val();
                listHost.push(hostData);
            }
        });
        // if(listIdNotFound.length > 0) {
        //     for(var n=0; n < listIdNotFound.length; n++) {
        //         listHost.push(listIdNotFound[n]);
        //     }
        // }
        var jasonData = {
            id: idJob,
            hostsId: listHost
        };
        $.ajax({
            type: 'POST',
            url: '/nera/job/api/add-host-job',
            data: JSON.stringify(jasonData),
            contentType: "application/json",
            success: function (data) {
                if (data.code != 200) {
                    return false;
                }
                if (typeof datatable !== 'undefined') {
                    datatable.destroy();
                    getDetailJob();
                }
                Swal.fire({
                    title: "Add Host",
                    text:  "Add Host Successful",
                    type:  'success',
                    timer: 2000,
                    showConfirmButton: false
                });
                $('#modalAddHost').modal('hide');
            },
        });
    });

    $('#btnHostCancel').on('click', function () {
        $('#modalAddHost').modal('hide');
    });

    $('#btnAddJob').on('click', function(){
        if(idJob > 0){
            $('.contentConfirmJob').text('Are you sure you want to update job?');
        }
        else{
            $('.contentConfirmJob').text('Are you sure you want to create new job?');
        }
        $('#modalAddJob').modal('show');
    });


    $('#btnYesAddJob').on('click', function(){
        var jobName = $('#jobName').val();
        if(jobName === '') {
            checkFieldName();
            $('#modalAddJob').modal('hide');
            return;
        }
        var description = $('#jobDescription').val();
        var status = $('input[name=rdStatus]:checked').val();
        var titleMessage = 'Create Job Successful';
        var jasonData = {
            id: idJob,
            name: jobName,
            description: description,
            active: status
        };
        var url = '';
        if(idJob > 0){
            url = '/nera/job/api/update-job';
            titleMessage = 'Update Job Successful';
        }
        else{
            url = '/nera/job/api/add-job';
        }
        $.ajax({
            type: 'POST',
            url: url,
            data: JSON.stringify(jasonData),
            contentType: "application/json",
            success: function (data) {
                if (data.code === 200) {
                    Swal.fire({
                        title: titleMessage,
                        text:  data.mess,
                        type:  'success',
                        timer: 2000,
                        showConfirmButton: false
                    });
                    if (typeof datatable !== 'undefined') {
                        datatable.load();
                    }
                    if(idJob < 0 || idJob === '') {
                        $('#idJob').val(data.detail);
                        idJob = $('#idJob').val();
                    }
                    $('#btnDeleteJob').removeClass('hideclass');
                    $('#tabInventoryWorkflow').removeClass('hideclass');
                    $('#validateJobName').addClass('hideclass');
                    $('#validateJobName').text('');
                } else {
                    $('#validateJobName').text('The job name has been used');
                    $('#validateJobName').removeClass('hideclass');
                }
                $('#modalAddJob').modal('hide');
            },
        });
    });
});