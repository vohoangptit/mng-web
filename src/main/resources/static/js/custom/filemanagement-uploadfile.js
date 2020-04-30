// Upload file listing
var displayfiles;
var files = [];
var nameList = [];
var resultNameList = [];
genNavigation("filemanagement");
function openFile(event) {

	$('#noFilesSelectedMsg').css('display', 'none');

	// var show = "";
	var displayUpload = $("#FileUpload1").get(0);
	displayfiles = Array.from(displayUpload.files);
	for (var i = 0; i < displayfiles.length; i++) {
		var reader = new FileReader();
		reader.self = this;
		reader.name = displayfiles[i].name;
		reader.type = displayfiles[i].type;
		reader.onload = function (e) {
			// var na = this.name;
//            appendUploadFile(na);
		};
		appendUploadFile(displayfiles[i].name);
		reader.readAsDataURL(displayfiles[i]);
	}
	for (let df in displayfiles) {
		files.push(displayfiles[df]);
	}
}

function appendUploadFile(na) {
	$('#tblfilelisting > tbody:last').append('<tr><td class="assignindex"></td>'
		+ '<td><span class="red">*</span> <input type="text" class="form-control getname" value = "'+na+'"/>'
		+ '<span class="red errorMsg" style="display:none">File name is already exists.</span>'
		+ '<span class="red duplicateErrMsg" style="display:none">Name is duplicated.</span>'
		+ '<span class="red blankErrMsg" style="display:none">Name is blank.</span>'
		+ '</td>'
		+ '<td> <input type="text" class="form-control getDescription"/>'
		+ '</td>'
		+ '<td><span  class="getfilename">' + na + '</span>'
		+ '<span class="red exceededErrMsg" style="display:none">File size has exceeded 2MB.</span>'
		+ '<span class="red invalidFileTypeErrMsg" style="display:none">File type is limited to .yml, .txt, .doc, .docx, .pdf only.</span>'
		+ '</td>'
		+ '<td>'

		+ '<div class="kt-radio-list"><label class="kt-radio"><input class="radioname" type="radio" name="" value="Active"> Active<span></span></label>'
		+ '                           <label class="kt-radio"><input class="radioname" type="radio" value="Inactive" checked="checked" name=""> Inactive<span></span></label></div>'

		+ '</td>'
		+ '<td>'

		+ '<a onclick="removeRecord(this)" href="#" class="btn btn-hover-danger btn-icon btn-pill"><i class="la la-trash"></i></a>'

		+ '</td>'
		+ '</tr>');

	assignindex();
	assignname();
}

function removeRecord(anchorTag) {

	var fileToRemove = $(anchorTag).closest('tr').find('.getfilename').html();
	for (var i = 0; i < displayfiles.length; i++) {
		if (displayfiles[i].name == fileToRemove) {
			displayfiles[i].isNotValid = true;
			break;
		}
	}

	for (let nl in nameList) {
		if (nameList[nl].file == fileToRemove) {
			nameList[nl].isNotValid = true;
			break;
		}
	}
	/*nameList = $.grep(nameList, function(e) {
		return e.file != fileToRemove;
	});*/

	for (let f in files) {
		if (files[f].name == fileToRemove) {
			files[f].isNotValid = true;
		}
	}

	for (let rnl in resultNameList) {
		if (resultNameList[rnl].file == fileToRemove) {
			resultNameList[rnl].isNotValid = true;
		}
	}
	/*resultNameList = $.grep(resultNameList, function(e) {
		return e.file != fileToRemove;
	});*/

	anchorTag.parentNode.parentNode.remove();

}

function assignindex() {
	var i = 1;
	$('#tblfilelisting tbody tr .assignindex').each(function () {
		$(this).text(i++);
	});
}

function assignname() {
	var i = 1;
	$('#tblfilelisting tbody tr .kt-radio-list').each(function () {
		$(this).find('.radioname').attr('name', i++);
	});
}


// Modal for confirmation
$('#btnCheckValidation').on('click', function () {
	checkValidateName();
});

function saveResults() {
	$('#uploadFileConfirmation').modal('hide');
	loadRequestedListing();
}

function checkValidateName() {
	nameList = [];
	$('.duplicateErrMsg').css('display', 'none');
	$('.blankErrMsg').css('display', 'none');
	$('.errorMsg').css('display', 'none');
	$('.exceededErrMsg').css('display', 'none');
	$('.invalidFileTypeErrMsg').css('display', 'none');
	$('#saveResultsBtn').removeAttr('disabled');
	$("#tblResultResponse").find("tr:gt(0)").remove();


	//var names = [];
	$('#tblfilelisting tbody tr').each(function () {
		var namerecord = {
			no: $(this).find('.assignindex').text().trim(),
			description: $(this).find('.getDescription').val(),
			name: $(this).find('.getname').val(),
			file: $(this).find('.getfilename').text().trim(),
			status: $(this).find('.radioname:checked').val()
		};
		// isDuplicated: false,
		$(this).find('.duplicateErrMsg').css('display', 'none')
		nameList.push(namerecord);
		//names.push($(this).find('.getname').val());
	});


	getListNameFile();
	function getListNameFile() {
		resultNameList = [];
		Object.keys(nameList).map(function (key) {
			return resultNameList.push(nameList[key]);
		});
	}

	if (resultNameList.length == 0) {
		$('#noFilesSelectedMsg').css('display', 'block');
		return;
	}

	var hasDuplicate = false;
	var hasBlank = false;
	for (let i = 0; i < resultNameList.length; i++) {
		if (resultNameList[i].name == '') {
			resultNameList[i].isBlank = true;
			hasBlank = true;
		}
		for (var j = i + 1; j < resultNameList.length; j++) {
			if (!resultNameList[i].isNotValid) {
				if (resultNameList[i].name == resultNameList[j].name) {
					resultNameList[i].isDuplicated = true;
					resultNameList[j].isDuplicated = true;
					hasDuplicate = true;
				}
			}
		}
	}

	var exceededFileSize = false;
	var invalidFileType = false;
	//var fileUpload = $("#FileUpload1").get(0);

	for (let i = 0; i < files.length; i++) {
		if (!files[i].isNotValid) {
			if (files[i].size > 2097152) {
				try {
					resultNameList[i].exceededFileSize = true;
					exceededFileSize = true;
				}catch(err){}
			}

			var ext = files[i].name.split('.').pop();
			if (ext != 'yml' && ext != 'txt' && ext != 'doc' && ext != 'docx' && ext != 'pdf' && ext != 'py' && ext != 'csv'
				&& ext != 'log' && ext != 'yaml' && ext != 'xml' && ext != 'j2' && ext != 'json' && ext != 'gz' && ext != 'zip'
				&& ext != 'tgz' && ext != 'tar' && ext != 'bin' && ext != 'iso' && ext != 'out' && ext != 'tar.gz') {
				resultNameList[i].invalidFileType = true;
				invalidFileType = true;
			}
		}
	}

	var index = 1;
	for (let i = 0; i < resultNameList.length; i++) {
		if (!resultNameList[i].isNotValid) {
			var result = "Success";
			if (resultNameList[i].isBlank == true) {
				result = "Failed";
				$('#tblfilelisting tr:nth-child(' + index + ')').find('.blankErrMsg').css('display', 'block');
			} else if (resultNameList[i].isDuplicated == true) {
				result = "Failed";
				$('#tblfilelisting tr:nth-child(' + index + ')').find('.duplicateErrMsg').css('display', 'block');
			}
			if (resultNameList[i].exceededFileSize == true) {
				result = "Failed";
				$('#tblfilelisting tr:nth-child(' + index + ')').find('.exceededErrMsg').css('display', 'block');
			}
			if (resultNameList[i].invalidFileType == true) {
				result = "Failed";
				$('#tblfilelisting tr:nth-child(' + index + ')').find('.invalidFileTypeErrMsg').css('display', 'block');
			}

			appendResultResponse(resultNameList[i].no, resultNameList[i].name, resultNameList[i].file, resultNameList[i].status, result);
			index++;
		}
	}

	if (hasDuplicate || hasBlank || exceededFileSize || invalidFileType) {
		$('#saveResultsBtn').prop('disabled', true);
		$('#uploadFileConfirmation').modal('show');

		return;
	}

	//var fileList = {};
	var fileList = [];
	//fileList.fileManagement = resultNameList;
	for (let nl in resultNameList) {
		if (!resultNameList[nl].isNotValid) {
			fileList.push(resultNameList[nl]);
		}
	}
	var upload = {};
	upload.fileManagement = fileList;

	$('#tblResultResponse').find("tr:gt(0)").remove();

	$.ajax({
		type: 'POST',
		url: '/nera/api/CheckValidateBeforeSave',
		data: JSON.stringify(upload),
		contentType: 'application/json',
		processData: false,
		beforeSend: function () {
			//
		},
		success: function (data) {
			var get = data.fileManagement;
			for (var i = 0; i < get.length; i++) {
				appendResultResponse(get[i].no, get[i].name, get[i].description, get[i].file, get[i].status, get[i].result);
				appendErrorMess(get[i].no, get[i].result);
			}
			$('#uploadFileConfirmation').modal('show');
		},
		error: function (err) {
			//
		},
	});

}

function appendResultResponse(no, name, description, file, status, result) {
	$('#tblResultResponse > tbody:last').append('<tr><td>' + no + '</td>'
		+ '<td>' + name + '</td>'
		+ '<td>' + description + '</td>'
		+ '<td>' + file + '</td>'
		+ '<td>' + status + '</td>'
		+ '<td>' + result + '</td>'
		+ '</tr>');
}

function appendErrorMess(no, result) {
	$('#tblfilelisting tbody tr .assignindex').each(function () {
		if ($(this).text().trim() == no) {
			if (result == 'Fail') {
				var row = $(this).closest('tr');
				row.find('span').closest('.duplicateErrMsg').css('display', 'block');
			}
		}
	});
}

function loadRequestedListing() {

	//var fileUpload = $("#FileUpload1").get(0);
	//var files = fileUpload.files;
	//var files = displayfiles;

	var uploadList = [];
	$('#tblfilelisting tbody tr').each(function () {
		var filerecord = {
			name: $(this).find('.getname').val(),
			description: $(this).find('.getDescription').val(),
			file: $(this).find('.getfilename').text().trim(),
			status: $(this).find('.radioname:checked').val()
		};
		uploadList.push(filerecord);
	});

	var fileData = new FormData();
	for (let i = 0; i < files.length; i++) {
		if (!files[i].isNotValid) {
			fileData.append('files', files[i]);
		}
	}
	fileData.append('fileManagement', JSON.stringify(uploadList));
	$.ajax({
		type: 'POST',
		url: '/nera/api/saveUploadFileListing',
		data: fileData,
		contentType: false,
		processData: false,
		beforeSend: function () {
			//
		},
		success: function (data) {

			var isSuccess = true;
			for (let i = 0; i < data.length; i++) {
				if (data[i] != 'success') {
					isSuccess = false;
					break;
				}
			}

			if (!isSuccess) {
				$('#uploadErrorMsg').css('display', 'block');
			} else {
				window.location.href = '/menu/masterdata/file-listing';
			}
		},
		error: function (err) {
			//
		},
	});

}
$( document ).ready(function() {
	//close another menu
	$('#system-menu-left').removeClass('kt-menu__item--open');
	$('#dashboard-menu-left').removeClass('kt-menu__item--open');
	$('#workflow-menu-left').removeClass('kt-menu__item--open');
});