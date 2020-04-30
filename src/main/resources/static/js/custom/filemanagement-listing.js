let datatable;
let fileData;
let selectedDeleteFileId;
let isAttached = false;
$(document).ready(function () {

    var fileIdFromHistory = Cookies.get("fileIdFromHistory");
    if (fileIdFromHistory) {
        $.ajax({
            type: "POST",
            url: "/nera/api/get-file-by-id/",
            data: {
                fileId: fileIdFromHistory
            },
            success: function(data){
                displayFileDetailModal(data);
                $('.getfileid').val(fileIdFromHistory);
                $('#editedFileId').val(fileIdFromHistory);
                Cookies.set("fileIdFromHistory", "");
            },
            error: function (err) {
                Cookies.set("fileIdFromHistory", "");
            }
        });
    }

    datatable = $('#kt_datatable').KTDatatable({
        // datasource definition						  
        data: {
            type: 'remote',
            source: {
                read: {
                    url: '/nera/api/GetAllFileManagement/',
                    map: function (raw) {
                        var dataSet = raw;
                        if (typeof raw.data !== 'undefined') {
                            dataSet = raw.data;
                            fileData = raw.data;
                            var total = raw.meta.total
                            $('.totalnum').text(total);
                        }
                        return dataSet;
                    },
                },
            },
            pageSize: 10,
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

        //datatableOptions,
        options: {
            extensions: {
                checkbox: {
                    vars: {
                        selectedAllRows: 'selectedAllRows',
                        requestIds: 'requestIds',
                        rowIds: 'meta.rowIds',
                    },
                },
            },
        },
        // columns definition
        columns: [
        {
            field: 'no',
            title: 'S/N',
            width: 30,
            type: 'number',
            selector: false,
            textAlign: 'center',
            sortable: false,
        }, {
            field: 'name',
            title: 'Name',
        },
        {
            field: 'filename',
            title: 'File',
            template: function(row) {
                return '<a href="#"' + row.filename + '" class="downloadFile1" data-filename="' + row.filename + '">' + row.filename + '</a>';
            }
        },
        {
            field: 'description',
            title: 'Description	',
        },
        {
            field: 'version',
            title: 'Version',
            template: function(row) {
            	return 'Version ' + row.version;
            }
        }, {
            field: 'createdBy',
            title: 'Created By',
        }, {
            field: 'createdByDate',
            title: 'Created Date',
        }, {
            field: 'status',
            title: 'Status',
            template: function(row) {
		    	  var active = row.active;
		    	  var htmlEdit;
			      if (active) {
			        htmlEdit = '<button style="border-radius: 5px; border: 1px solid #1eb7ae; background-color: #1eb7ae;" disabled><span style="font-family: Poppins;color:white">Active</span></button>';
			      } else{
			      	htmlEdit = '<button style="border-radius: 5px; border: 1px solid #7f7f7f; background-color: #7f7f7f;" disabled><span style="font-family: Poppins;color:white">InActive</span></button>';
			      }
			      return htmlEdit;
			      },
        }, {
            field: 'action',
            title: 'Action',
            sortable: false,
            width: 130,
            overflow: 'visible',
            textAlign: 'center',
            template: function (row) {
                var id = row.id;
                var filename = row.filename;
                var htmlEditFile = '';
		        var htmlDeleteFile = '';
		        
		        if (hasEditPermission) {
		        	htmlEditFile = '<a href="#" class="btn btn-hover-brand btn-icon btn-pill editFile" data-id="' + id + '"><i class="la la-edit"></i></a>'
		        }
                var htmlDownloadFile =  '<a href="#" class="btn btn-hover-brand btn-icon btn-pill downloadFile1" data-filename="' + filename + '"><i class="la la-download"></i></a>'
                if (hasDeletePermission) {
                	htmlDeleteFile = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill deleteFile" data-id="' + id + '"><i class="la la-trash"></i></a>';	
                }
                
                return htmlEditFile + htmlDownloadFile + htmlDeleteFile;
            },
        }],
    });

    datatable.on('kt-datatable--on-layout-updated', function (e) {
        $(document).on('click', '.deleteFile', function () {
            checkDelete($(this).attr('data-id'));
        });
        $(document).on('click', '.editFile', function () {
        	editFile($(this).attr('data-id'));
        });
        if (!isAttached) {
	        $(document).on('click', '.downloadFile1', function () {
	        	downloadFile($(this).attr('data-filename'));
	        });
	        isAttached = true;
        }
    });
    datatable.on('kt-datatable--on-init', function(){
    	fixedTableHeader();
    });

});

function redirectHistoryView() {
    var fileId = $('#editedFileId').val();
    window.location.href = '/file-history/'+ fileId;
}

function reset() {
    $('#generalSearch').val("");
    datatable.search("", "generalSearch");
}

function grep (id) {
	return jQuery.grep(fileData, function(obj, index) {
	    return obj.id == id;
	});
}

function checkDeleteGetId() {
	var id = $('#editedFileId').val();
	checkDeleteWhenEdit(id);
}

function checkDeleteWhenEdit(id) {
	var fileInfo = grep(id)[0];
	
	if (fileInfo.status === 'Active' && $('.radioI').prop('checked') == true) {
		$('#clickOnSaveMesg').css('display', 'block');
	}
	
	if (fileInfo.status === 'Active') {
		$('#stillActiveFileModal').modal();
	} else {
		selectedDeleteFileId = fileInfo.id;
		$('#confirmDeleteFileModal').modal();
	}
}

function checkDelete(id) {
	var fileInfo = grep(id)[0];
	if (fileInfo.status === 'Active') {
		$('#stillActiveFileModal').modal();
	} else {
		selectedDeleteFileId = fileInfo.id;
		$('#confirmDeleteFileModal').modal();
	}
}

function showDelete() {
	selectedDeleteFileId = $('.getfileid').val();
	$('#confirmDeleteFileModal').modal();
}

function confirmDelete() {
	clearMesg();
    $.ajax({
        type: "POST",
        url: "/nera/playbook-input/api/count-by-file-id",
        data: {
            fileId: selectedDeleteFileId
        },
        success: function(data){
            $('#confirmDeleteFileModal').modal('hide');
            if (data > 0) {
                Swal.fire({
                    text: 'The current file is used by the playbooks. Cannot delete',
                    type: 'error',
                    timer: 3000,
                    showConfirmButton: false
                });
            } else {
                deleteFile();
            }
        }
    });

}

function deleteFile() {
    $.ajax({
        url: '/nera/api/deleteFile/',
        type: 'delete',
        dataType: 'text',
        data :  {
            id: selectedDeleteFileId
        },
        success: function(response) {
            datatable.reload();
            Swal.fire({
                text:  'Delete is successful',
                type:  'success',
                timer: 2000,
                showConfirmButton: false
            });
        },
        error: function(response) {
            Swal.fire({
                text:  'Delete is fail',
                type:  'error',
                timer: 2000,
                showConfirmButton: false
            });
        }
    });
}

function openFile(event) {
    $(".displayitems").html('');
    $('.getuploadfileid').val(0);
    $(".displayitems").html('<span>' + $("#FileUpload1")[0].files[0].name + '</span>');
}

function editFile(id) {
    //
    var fileInfo = grep(id)[0];
    //
    $('.getfileid').val(id);
    $('#editedFileId').val(id);
    displayFileDetailModal(fileInfo);
}

function displayFileDetailModal(data) {
    //$('.getuploadfileid').val(fileInfo.file);
    $('#editSeclectedRecord .getdataforname').val(data.name);
    $('#editSeclectedRecord .getDescriptionVal').val(data.description);
    $('#editSeclectedRecord .displayitems').text(data.filename);
    $('#editSeclectedRecord .version').text('Version ' + data.version);
    if (data.status == 'Active') {
        $('.radioA').prop('checked', true);
    }
    else {
        $('.radioI').prop('checked', true);
    }
    $('#editSeclectedRecord').modal('show');
}

function saveFileDetail() {

	clearMesg();
	
	var name = $('.getdataforname').val();
	if (name == '') {
		$('#blankNameMesg').css('display', 'block');
		return;
	}
    var fileUpload = $("#FileUpload1").get(0);
    var files = fileUpload.files;
    var fileDatas = new FormData();
    for (var i = 0; i < files.length; i++) {
        fileDatas.append('file', files[i]);
    }
    fileDatas.append('fileid', $('.getfileid').val());
    fileDatas.append('name', $('.getdataforname').val());
    fileDatas.append('description', $('.getDescriptionVal').val());
    fileDatas.append('status', $('.radioname:checked').val());

    $.ajax({
        type: 'POST',
        url: '/nera/api/updateFileDetail',
        data: fileDatas,
        contentType: false,
        processData: false,
        "headers": {
            "Cache-Control": "no-cache"
        },
        success: function (data) {
        	if (data == 'true') {
        		$('#editSeclectedRecord').modal('hide');
                Swal.fire({
                    text:  'Update is successful',
                    type:  'success',
                    timer: 2000,
                    showConfirmButton: false
                });
	            reset();
        	} else if (data == 'false1') {
        		$('#duplicateFilenameMesg').css('display', 'block');
        	} else if (data == 'false2') {
        		$('#incorrectFileExtMesg').css('display', 'block');
        	} else if (data == 'false3') {
        		$('#incorrectNameMesg').css('display', 'block');
        	}
        },
        error: function (err) {
            Swal.fire({
                text:  'Update is fail',
                type:  'error',
                timer: 2000,
                showConfirmButton: false
            });
        },
    });
}

function clearMesg() {
	$('#blankNameMesg').css('display', 'none');
	$('#clickOnSaveMesg').css('display', 'none');
	$('#duplicateFilenameMesg').css('display', 'none');
	$('#incorrectFileExtMesg').css('display', 'none');
	$('#incorrectNameMesg').css('display', 'none');
}

function downloadFile(filename) {
	window.open("/nera/api/downloadFile?filename=" + filename);
}

