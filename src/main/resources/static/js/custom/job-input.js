//
	function openFile(event) {
		let fileData = new FormData();
	    // var show = "";
		let displayUpload = $("#FileUpload").get(0);
		let displayfiles = displayUpload.files;
	    for (let i = 0; i < displayfiles.length; i++) {
	    	fileData.append('file', displayfiles[i]);
	        var reader = new FileReader();
	        reader.self = this;
	        reader.name = displayfiles[i].name;
	        reader.type = displayfiles[i].type;
	        reader.onload = function (e) {
	        	var ex = getextensionfile(this.name);
	        	if(ex){
		        	$('#getContenOfFile').html(this.result);
		        	$('.displayitem').html('');
		        	$('.displayitem').append('<span>' + this.name + '</span>');
	        	}
	        	else{
	        		$('.displayitem').html('');
	        		$('#getContenOfFile').html('');
	        		return false;
	        	}
	        };
	        reader.readAsText(displayfiles[i]);
	    }
	    $.ajax({
	        type: 'POST',
	        url: '/nera/upload/playbook/get-output',
	        data: fileData,
	        contentType: false,
	        processData: false,
	        beforeSend: function () {
	            //
	        },
	        success: function (data) {
	        	$('.notstandard').remove();
	        	for (let i = 0; i < data.length; ++i) {
	        		readOutputRow(data[i].variable, data[i].value);
	        	}
	        },
	        error: function (err) {
	            //
	        },
	    });
	}
	
	function readOutputRow(variable, value) {
		$('#tblPlaybookOutput').removeClass('hideclass');
	    $('#tblPlaybookOutput > tbody:last').append('<tr class="notstandard">'
	        + '<td style="text-align:center" class="outputVariable outPutResize">' + variable + '</td>'
	        + '<td style="text-align:center" class="outputValue outPutResize">' + value + '</td>'
	        + '</tr>');
	}
	
	function getextensionfile(a){
		var extension = "";
		var re = false;
		if(a != ""){
			extension = a.replace(/^.*\./, '');
			if(extension == "yml"){
				re = true;
			}
		}
		return re;
	}
	
	var fileListing = [];
    function changeFunc($i) {
    	var get = $i;
    	$('#selectedFile').val(get);
    	var name = "";
    	for(var i = 0; i < fileListing.length; i++){
    		if(fileListing[i].id == get){
    			name = fileListing[i].filename;
    			downloadFileCSV(name);
    			reDownload();
    			return false;
    		}
    	}
    }
    
    function downloadFileCSV(name){
    	$.ajax({
			url: '/nera/upload/downloadFile/'+name,
			type: 'GET',
			xhrFields: {
	            responseType: 'blob'
	        },
	        success: function(data) {
	        	
	            var csvData = new Blob([data], { type: '.yml' });
	            if (window.navigator && window.navigator.msSaveOrOpenBlob) { 
	                window.navigator.msSaveOrOpenBlob(csvData, name);
	            } else { 
		            var a = document.createElement('a');
		            var url = window.URL.createObjectURL(data);
		            a.href = url;
		            a.download = name;
		            a.click();
		            window.URL.revokeObjectURL(url);
	            }

	    	}, 
	    	error: function(response) {
	    		console.log(response);
	    	}
		});
    }
	
	function reDownload(){
		$('.redownload').on('click', function(){
	    	var get = $(this).attr('data-id');
	    	$('#selectedFile').val(get);
	    	var name = "";
	    	for(var i = 0; i < fileListing.length; i++){
	    		if(fileListing[i].id == get){
	    			name = fileListing[i].filename;
	    			downloadFileCSV(name);
	    			return false;
	    		}
	    	}
		});
	}
    
	// function changeoftype()
	// {
	// 	var ck = $('#selectedType').val();
	// 	if(ck == 1){
	// 		$('#changeofinput').removeClass('hideclass');
	// 		$('#changeoftype').addClass('hideclass');
	// 	}
	// 	else{
	// 		$('#changeofinput').addClass('hideclass');
	// 		$('#changeoftype').removeClass('hideclass');
	// 	}
	// };
	
	genNavigation("playbook");

$( document ).ready(function() {
		        	
	$("#kt_header_menu ul li span").text("Master Data/Playbook Listing/Detail");
	// layout
	initLayout();
	function initLayout()
	{
		var ck = $('#getPlaybookid').val();
		if(ck > 0){
			$('.layCreate').removeClass('hideclass');			
		}
		$('#selectedType').select2({ width: 'resolve', dropdownAutoWidth: true });
	}

	function appendOutputRow(variable, value) {
		$('#tblPlaybookOutput').removeClass('hideclass');
	    $('#tblPlaybookOutput > tbody:last').append('<tr class="notstandard">'
	        + '<td style="text-align:center" class="outputVariable">' + variable + '</td>'
	        + '<td style="text-align:center" class="outputValue">' + value + '</td>'
	        + '</tr>');
	}
	/*function deleteARow(){
		$('.pldDeleteRow').on('click', function(){
			$(this).closest('tr').addClass('hideclass');
			$(this).closest('tr').find('td').eq(3).text('1');
		});
	}*/

	$('#btnCancelJobInput').on('click', function(e){
		$('#jobinputworkflow').modal('hide');
	});

	$('#btnSaveJob').unbind().on("click", function(e){
		$('#jobinputworkflow').modal('hide');
	});

	/*function setTextEdit(variable, inputTypeValue){
		$('#selectedType').val(inputTypeValue).trigger('change');
		$('#pldVariable').val(variable);
	}*/

	createanewplaybook();
	function createanewplaybook(event){
		$('#btnCreateNewPlaybook').on('click', function(){
			var name = $('#pldGetPlaybookName').val();
		    if(name == ""){
		    	$('.nameRequired').removeClass('hideclass');
		    	return false;
		    }
		    else{
		    	$('.nameRequired').addClass('hideclass');
		    }
			
			var ckf = false;
			if($('.displayitem').text().trim() != ""){
				ckf = true;
				$('.uploadRequired').addClass('hideclass');
			}
			else{
				$('.uploadRequired').removeClass('hideclass');
				event.stopImmediatePropagation();
			}
			
		    var fileUpload = $("#FileUpload").get(0);
		    var files = fileUpload.files;

		    var playbookinput = [];
		    $('#tblPlaybookInput tbody .notstandard').each(function () {
		        var filerecord = {
	        		type: $(this).find('.gettypevalue').text().trim(),
		            variable: $(this).find('.getvariable').text().trim(),
		            value: $(this).find('.getfilevalue').text().trim(),
		            mandatory: $(this).find('.mandatorychecked').is(":checked"),
		            fileManagementId: $(this).find('.getfilevalue').attr('data-id'),
		        };
		        playbookinput.push(filerecord);
		    });
		    
		    var playbookinputconfirm = [];
		    $('#tblPlaybookInput tbody .notstandard').each(function () {
		        var filerecord = {
		            inputtype: $(this).find('.gettypevalue').text().trim(),
		            variable: $(this).find('.getvariable').text().trim(),
		            value: $(this).find('.getfilevalue').text().trim(),
		            mandatory: $(this).find('.mandatorychecked').is(":checked"),
		        };
		        playbookinputconfirm.push(filerecord);
		    });
		    
		    var playbookoutput = [];
		    $('#tblPlaybookOutput tbody .notstandard').each(function () {
		        var filerecord = {
		        	variable: $(this).find('.outputVariable').text().trim(),
			        value: $(this).find('.outputValue').text().trim(),
		        };
		        playbookoutput.push(filerecord);
		    });
		    
		    var playbookOutputConfirm = [];
		    $('#tblPlaybookOutput tbody .notstandard').each(function () {
		        var filerecord = {
		            variable: $(this).find('.outputVariable').text().trim(),
		            value: $(this).find('.outputValue').text().trim(),
		        };
		        playbookOutputConfirm.push(filerecord);
		    });

		    var fileData = new FormData();
		    if(ckf){
			    for (var i = 0; i < files.length; i++) {
			    	fileData.append('file', files[i]);
			    }
		    }
		    
		    var jasonData = {
	    		name: $('#pldGetPlaybookName').val(),
	    		playbookInput: playbookinput,
	    		playbookOutput: playbookoutput,
	    		note: $('#pldNotes').val(),
	    		//status: "NEW",
		    };
		    
		    if($('#getPlaybookid').val() > 0){
		    	jasonData["status"] = $('.forstatus').attr('data-value');
		    	jasonData["active"] = $('input[name=radio2]:checked').val();
		    }
		    else{
		    	jasonData["status"] = "NEW";
		    }

		    addconfirm(jasonData, playbookinputconfirm, playbookOutputConfirm);
		    var count = 0;
		    if(ckf){
		    	count = files.length;
		    }
		    let up = fileData;
			let cre = jasonData;
		    confirmtocontinue(count, up, cre);
		});
	}
	
	function addconfirm(jasonData, playbookinputconfirm, playbookOutputConfirm){
		
		if(jasonData.name == "")
		{
			return false;
		}
		$('#reName').text(jasonData.name);
		
    	if($('#getPlaybookid').val() > 0)
		{
        	$('#reFile').html($('.displayitem').text().trim());
        	$('#reFileContent').html($('#getContenOfFile').text().trim());
		}
    	else{
			if($('.displayitem').text().trim() != ""){
	    	    var displayUpload = $("#FileUpload").get(0);
	    	    var displayfiles = displayUpload.files;
	    	    if(displayfiles.length > 0){
	    	        var reader = new FileReader();
	    	        reader.self = this;
	    	        reader.name = displayfiles[0].name;
	    	        reader.onload = function (e) {
	    	        	$('#reFile').html(this.name);
	    	        	$('#reFileContent').html(this.result);
	    	        };
	    	        reader.readAsText(displayfiles[0]);
	    	    }
			}
    	}
		
		var loop = playbookinputconfirm;
		$('#tblResultResponse > tbody').html('');
        for (let i = 0; i < loop.length; i++) {
        	appendTableToConfirm(loop[i].inputtype, loop[i].variable, loop[i].value, loop[i].mandatory);
        }
        $('#tblResultOuput > tbody').html('');
        for (let i = 0; i < playbookOutputConfirm.length; i++) {
        	appendOutputToConfirm(playbookOutputConfirm[i].variable, playbookOutputConfirm[i].value);
        }
		$('#addPlaybookConfirmation').modal('show');
	}
	
	function confirmtocontinue(count, up, cre){
		$('#btnResultResponse').unbind().click( function(){
			console.log('len of create obj : __________'+cre.length);
			if($('#getPlaybookid').val() > 0){
				cre["id"] = $('#getPlaybookid').val();
				if(count > 0){
					if($('.displayitem').text().trim() != ""){
						callUploadFileApi(up, cre);
					}
				}
				else{
		        	cre["fileName"] = $('.displayitem').text().trim();
		        	cre["sourceUrl"] = $('.displayitem').attr('data-value');
					callUpdatePlaybookApi(cre);
				}
			}
			else{
				if(count > 0){
					callUploadFileApi(up, cre);
				}
				else{
					callCreateNewPlaybookApi(cre);
				}
			}
			
		});
	}
	
	function callUploadFileApi(up, cre){
	    $.ajax({
	        type: 'POST',
	        url: '/nera/upload/playbook',
	        data: up,
	        contentType: false,
	        processData: false,
	        beforeSend: function () {
	            //
	        },
	        success: function (data) {
	        	cre["fileName"] = data.fileInfo.fileName;
	        	cre["sourceUrl"] = data.fileInfo.fileDownloadUri;
	        	if($('#getPlaybookid').val() > 0){
	        		callUpdatePlaybookApi(cre);
	        	}
	        	else{
	        		callCreateNewPlaybookApi(cre);
	        	}
	        },
	        error: function (err) {
	            //
	        },
	    });
	}
	
	function callCreateNewPlaybookApi(cre){
		$('#save-failed').css('display', 'none');
		$("#save-failed").empty();
	    $.ajax({
	        type: 'POST',
	        url: '/nera/playbook/api/save',
	        data: JSON.stringify(cre),
	        contentType: "application/json",
	        beforeSend: function () {
	            //
	        },
	        success: function (data) {
	        	countPlaybookApproved()
	            window.location.href = '../playbook';
	        },
	        error: function (err) {
	            $('#save-failed').empty();
	        	if(err.status === 400 || err.status === 500){
	    			$("#save-failed").append(err.responseJSON.detail);
	    		} else{
	    			$("#save-failed").append(err.statusText);
	    		}
				$('#save-failed').css('display', 'block');
				$('#save-failed').alert();
				$('#save-failed').delay(4000).hide(200);
	        },
	    });
	}
	
	
	
	function appendTableToConfirm(input, variable, value, mandatory) {
		if(mandatory== true){
			mandatory = '<td style="vertical-align: middle;text-align: center;"><input class="mandatorycheckbox" disabled="disabled" type="checkbox" checked="true"></td>'
		} else {
			mandatory = '<td class="resizeCol" style="vertical-align: middle;text-align: center;"><input disabled="disabled" type="checkbox"></td>'
		}
	    $('#tblResultResponse > tbody:last').append('<tr><td class="textcen">' + input + '</td>'
	        + '<td class="textcen" style="word-wrap: break-word; max-width: 200px;">' + variable + '</td>'
	        + '<td class="textcen" style="word-wrap: break-word; max-width: 205px;">' + value + '</td>'
	        + mandatory
	        + '</tr>');
	}
	
	function appendOutputToConfirm(variable, value) {
	    $('#tblResultOuput > tbody:last').append('<tr>'
	        + '<td class="textcen outPutConfirmResize">' + variable + '</td>'
	        + '<td class="textcen outPutConfirmResize">' + value + '</td>'
	        + '</tr>');
	}
	
	
	// Update a playbook area
	isUpdateorCreate();
	function isUpdateorCreate()
	{
		var is = $('#getPlaybookid').val();
		if(is > 0)
		{
			$('#btnDeletePlaybook').removeClass('hideclass');
			$('#pldNotes').attr('readonly');
			
			// load detail data for id
			var json = {id: is};
		    $.ajax({
		        type: 'GET',
		        url: '/nera/playbook/api/get-by-id',
		        data: json,
		        contentType: "application/json",
		        success: function (data) {
		        	//
		        	$('#pldGetPlaybookName').val(data.name);
		        	readcontentoffile(data.sourceUrl);
		        	$('.displayitem').attr('data-value', data.sourceUrl);
		        	
		        	var sta = data.status;
		        	$('.forstatus').attr('data-value', sta);
		        	if(sta == "NEW"){
		        		$('.staNew').removeClass('hideclass');
		        	}
		        	if(sta == "DRAFT"){
		        		$('.staDra').removeClass('hideclass');
		        	}
		        	if(sta == "APPROVED"){
		        		$('.staApp').removeClass('hideclass');
		        		$('.layValiRemark').removeClass('hideclass');
		        		$('#pldRemark').attr('readonly', '');
		        		$('#btnDeletePlaybook').removeClass('hideclass');
		        		$('#btnCreateNewPlaybook').removeClass('hideclass');
		        	}
		        	if(sta == "REJECTED"){
		        		$('.staRej').removeClass('hideclass');
		        		$('.layValiRemark').removeClass('hideclass');
		        		//$('#btnDeletePlaybook').addClass('hideclass');
		        		$('#btnCreateNewPlaybook').addClass('hideclass');
		        	}

		        	$('#pldNotes').val(data.note);
		        	$('#pldRemark').val(data.remark);
		        	if(data.active == true){
		        		$('#radioA').prop('checked', true);
		        	}
		        	else{
		        		$('#radioI').prop('checked', true);
		        	}
		        	
		        	$('#tblPlaybookInput').removeClass('hideclass');
		        	if(data.playbookInput.length > 0){
		        		var get = data.playbookInput;
		        		for(let i = 0; i < get.length; i++){
							let col1 = get[i].type;
							let col11 = 1;
							let col2 = get[i].variable;
							let col3 = get[i].value;
							let checked = get[i].mandatory;
							let col31;
		        			if(get[i].type == "Text"){
								col31 = "";
		        			}
		        			else{
		        				col31 = get[i].fileManagementId == null ? "" : get[i].fileManagementId;
		        			}
							appendNewRow(col1, col11, col2, col3, col31, checked);
		        		}
		        	}
		        	$('#tblPlaybookOutput').unbind().removeClass('hideclass');
		        	if(data.playbookOutput.length > 0){
		        		var outputList = data.playbookOutput;
		        		for(let i = 0; i < outputList.length; i++){
	        				var variable = outputList[i].variable;
	        				var value = outputList[i].value;
	        				appendOutputRow(variable, value);
		        		}
		        	}
		        },
		        error: function (err) {
		            //
		        },
		    });
		}
	}
	
	function readcontentoffile(sourceUrl){
		
		if(sourceUrl == ""){
			return false;
		}
		
		$("#getContenOfFile").load(sourceUrl, function(responseTxt, txtStatus, jqXHR){
			if(txtStatus == "success"){
	            $('.displayitem').append('<span>' + sourceUrl.split('/').pop() + '</span>');
	        }
	        if(txtStatus == "error"){
	            console.log("Error: " + jqXHR.status + ", " + jqXHR.statusText);
	        }
		});
		
	}
	
	// Back to list screen
	$('#btnBackToPlay').on('click', function(){
		if($('#getPlaybookid').val() > 0){
			window.location.href = '../../playbook';
		}
		else{
			window.location.href = '../playbook';
		}
	});
	
	// Delete by id
	$('#btnDeletePlaybook').on('click', function(){
		$('#kt_modal_Delete').modal('show');
	});
	
	$('#btnYesDelete').on('click', function(){
		var id = $('#getPlaybookid').val();
		callDeleteApi(id);
	});
	
	function callDeleteApi(id)
	{
	    $.ajax({
	        type: 'POST',
	        url: '/nera/playbook/api/delete-by-id',
	        data: {id: id},
	        beforeSend: function () {
	            //
	        },
	        success: function (data) {
	        	countPlaybookApproved();
	        	$('.displaymessage').removeClass('hideclass');
	        	$('.deletemess').removeClass('hideclass');
	        	$('#kt_modal_Delete').modal('hide');
	            setTimeout(function(){
	            	window.location.href = '../../playbook';
	            }, 2000);
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
	        	$('.setPlaybookApprovedCount').text('Playbook Approved ('+ data +')');
	        },
	        error: function (err) {
	            //
	        },
	    });

	}
});
