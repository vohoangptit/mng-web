var datatable;

$(document).ready(function() {
    let fileId = $('#fileId').val();

    $.ajax({
        type: "POST",
        url: "/nera/api/get-file-by-id/",
        data: {
            fileId: fileId
        },
        success: function(data){
            $('#txtFileName').text(data.name)
            $('#txtFile').text(data.file)
            $('#txtVersion').text(data.version)
            $('#txtUpdateBy').text(data.updatedBy)
            $('#txtUpdateAt').text(data.updatedAt)
        }
    });

    datatable = $('#kt_datatable').KTDatatable({
        // datasource definition
        data: {
            type: 'remote',
            source: {
                read: {
                    method: 'POST',
                    url: '/nera/file-history/api',
                    data: {
                        fileId: fileId
                    },
                    map: function(raw) {
                        if (raw) {
                            var dataSet = raw;
                            if (typeof raw.data !== 'undefined') {
                                dataSet = raw.data;
                                var total = raw.meta.total;
                                $('.totalNum').text(total);
                            }
                            return dataSet;
                        }
                    }
                }
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
        pagination: false,
        noRecords: 'No records found',

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
                field: 'version',
                title: 'Version',
                sortable: false,
                textAlign: 'left',
                template: function (row) {
                    return "Version " +  row.version;
                }
            },
            {
                field: 'updatedAt',
                title: 'Update at',
                sortable: false,
                textAlign: 'left'
            },
            {
                field: 'updatedBy',
                title: 'Update By',
                sortable: false,
                textAlign: 'left'
            },
            {
                field: 'Actions',
                title: 'Actions',
                sortable: false,
                overflow: 'visible',
                textAlign: 'center',
                template: function(row) {
                    var id = row.id;
                    var fileManagementId = row.fileManagementId;
                    var btnViewDetail  = '';
                    var btnRestore = '';


                    btnViewDetail = '<a href="#"  class="btn btn-hover-brand btn-icon btn-pill view-history" data-id="' + id + '"><i class="la la-eye"></i></a>'
                    if(hasRestorePermission) {
                        btnRestore = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill btn-restore" data-file-id="'+ fileManagementId +'" data-id="' + id + '"><i class="la la-history"></i></a>';
                    }
                    return btnViewDetail + btnRestore;
                }
            }]
    });

    $(document).on('click', '.download-file', function () {
        var fileName = $('#txtFile').text();
        downloadFile(fileName);
    });

    $(document).on('click', '.download-file-history', function () {
        var fileName = $('.txtFileNameHistory').text();
        downloadFile(fileName);
    });

    $(document).on('click', '.view-history', function () {
        var id = $(this).attr("data-id");
        $.ajax({
            type: "POST",
            url: "/nera/file-history/api/get-by-id",
            data: {
                id: id
            },
            success: function(data){
                $('.txtName').val(data.name);
                $('#fileManagementId').val(data.fileManagementId);
                $('.txtDesc').val(data.description);
                $('.txtFileNameHistory').text(data.fileName);
                $('.fileHistoryId').val(data.id);
                $('.txtVersion').text('Version ' + data.version);


                if (data.active == true) {
                    $('.radioA').prop('checked', true);
                }
                else {
                    $('.radioI').prop('checked', true);
                }
            }
        });
        $('#viewDetailModal').modal('show');
    });

    $(document).on('click', '#btnBackToCurrentVersion', function () {
        var fileIdFromHistory = $('#fileId').val();
        Cookies.set("fileIdFromHistory", fileIdFromHistory);
        window.location.href = "/menu/masterdata/fileManagement";
    });

    $(document).on('click', '.btn-restore', function () {
        let fileIds = $(this).attr("data-file-id");
        if (!fileId) {
            fileId = $('#fileManagementId').val();
        }
        let id = $(this).attr("data-id");
        if (!id) {
            id = $('.fileHistoryId').val();
        }
        displayConfirmPopup(fileIds, id);
    });

    // $(document).on('click', '#btnYes', function () {
    //     const id = $('.fileHistoryId').val();
    //
    // });

    function downloadFile(fileName) {
        window.open("/nera/api/downloadFile?filename=" + fileName);
    }

    function displayConfirmPopup(fileIds, fileHistoryId) {
        $.ajax({
            type: "POST",
            url: "/nera/playbook-input/api/count-by-file-id",
            data: {
                fileId: fileIds
            },
            success: function(data){
                var type = '';
                var message = '';
                if (data > 0) {
                    type = 'warning';
                    message = 'The current version of the file is used by a playbook. Restoring to the previous version can cause the playbook to run wrongly. Continue?';
                } else {
                    type = 'question';
                    message = 'Are you sure you want to restore to the previous version?';
                }

                Swal.fire({
                    title: 'Are you sure?',
                    text: message,
                    type: type,
                    showCancelButton: true,
                    confirmButtonColor: '#5d78ff',
                    cancelButtonColor: '#ea6861',
                    confirmButtonText: 'Yes',
                    cancelButtonText: 'No',
                    animation: false,
                    customClass: {
                        popup: 'animated rubberBand'
                    }
                }).then(function(result) {
                    if (result.value) {
                        restore(fileHistoryId);
                    }
                });
            }
        });
    }

    function restore(fileHistoryId) {
        $.ajax({
            type: 'POST',
            url: '/nera/file-history/api/restore',
            data: {
                id: fileHistoryId
            },
            success: function (data) {
                if(data) {
                    Swal.fire({
                        title: 'Restore the file',
                        text:  'File restored successfully',
                        type:  'success',
                        timer: 2000,
                        showConfirmButton: false
                    });
                    location.reload();
                } else {
                    Swal.fire({
                        text:  'Cannot restore this file',
                        type:  'error',
                        timer: 2000,
                        showConfirmButton: false
                    });
                }
            }
        });
    }
});