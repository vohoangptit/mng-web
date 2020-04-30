var idUpdate;
function loadEmailTemplate() {
	$.ajax({
		type : 'GET',
		url : '/nera/email-template/find-all',
		beforeSend : function() {
			//
		},
		success : function(data) {
			var re = data;
			$('#tblListing > tbody').html('');
			if(hasEditPermission){
				for (let i = 0; i < re.length; i++) {
					let no = i + 1;
					let id = re[i].id;
					let name = re[i].templateName;
					let status;
					if (re[i].active) {
						status = '<button style="border-radius: 5px; border: 1px solid #1eb7ae; background-color: #1eb7ae;" disabled><span style="font-family: Poppins;color:white">Active</span></button>';
				      } else{
				    	status = '<button style="border-radius: 5px; border: 1px solid #7f7f7f; background-color: #7f7f7f;" disabled><span style="font-family: Poppins;color:white">InActive</span></button>';
				      }
					generateEmailListingTable(no, id, name, status);
				}
			} else{
				for (let i = 0; i < re.length; i++) {
					let no = i + 1;
					let id = re[i].id;
					let name = re[i].templateName;
					let status;
					if (re[i].active) {
						status = '<button style="border-radius: 5px; border: 1px solid #1eb7ae; background-color: #1eb7ae;" disabled><span style="font-family: Poppins;color:white">Active</span></button>';
				      } else{
				    	status = '<button style="border-radius: 5px; border: 1px solid #7f7f7f; background-color: #7f7f7f;" disabled><span style="font-family: Poppins;color:white">InActive</span></button>';
				      }
					generateEmailListingTableNoEdit(no, id, name, status);
				}
			}
			
		},
		error : function(err) {
			//
		},
	});
}
function generateEmailListingTable(no, id, name, status) {
	$('#tblListing > tbody:last').append('<tr class="">'
	        + '<td class="assignindex">' + no + '</td>'
	        + '<td class="tdHostName" data-id="' + id + '">' + name + '</td>'
	        + '<td class="tdStatus">' + status + '</td>'
	        + '<td>'
	        + '<span><a data-id="' + id + '" href="#" onclick="return detailEmailTemplate('+ id +');" class="btn btn-hover-brand btn-icon btn-pill hostEditRow"><i class="la la-pencil"></i></a></span>'
	        + '</td>'
	        + '</tr>');
}
function generateEmailListingTableNoEdit(no, id, name, status) {
	$('#tblListing > tbody:last').append('<tr class="">'
	        + '<td class="assignindex">' + no + '</td>'
	        + '<td class="tdHostName" data-id="' + id + '">' + name + '</td>'
	        + '<td class="tdStatus">' + status + '</td>'
	        + '<td>'
	        + '</td>'
	        + '</tr>');
}
function detailEmailTemplate(id) {
	idUpdate = id;
	$.ajax({
		type : 'GET',
		url : '/nera/email-template/find-by-id?id='+id,
		beforeSend : function() {
			//
		},
		success : function(data) {
			$('#pldEmailTemplateName').val(data.templateName);
			$('#pldEmailSubject').val(data.subject);
			
			$('#tblEmailVariable > tbody').html('');
			for (var i = 0; i < data.emailTemplateVariable.length; i++) {
				var name = data.emailTemplateVariable[i].name;
				var description = data.emailTemplateVariable[i].description;
				appendVariableEmailTemplate(name, description);
			}
			CKEDITOR.instances['txtContent'].setData(data.templateContent);
		},
		error : function(err) {
			//
		},
	});
	$('#listingEmailTab').addClass('hideclass');
	$('#detailEmailTab').removeClass('hideclass');
}
function appendVariableEmailTemplate(name, description) {
	$('#tblEmailVariable > tbody:last').append('<tr class="notstandard">'
	        + '<td class="outPutResize">' + name + '</td>'
	        + '<td class="outPutResize">' + description + '</td>'
	        + '</tr>');
}

$( document ).ready(function() {
	$('#emailKeySearch').keyup(function(){
		var search = $('#emailKeySearch').val();
		searchEmailTemplate(search);
		
	});
	$('#btnBackListingEmail').on('click', function(){
		loadEmailTemplate();
		$('#detailEmailTab').addClass('hideclass');
		$('#listingEmailTab').removeClass('hideclass');
	});
	$('#btnUpdateEmailTemplate').unbind().on('click', function(){
		var templateContent = CKEDITOR.instances["txtContent"].getData();
		var templateName = $('#pldEmailTemplateName').val();
		var subject = $('#pldEmailSubject').val();
		var up = {
				id: idUpdate, 
				templateContent: templateContent,
				templateName: templateName,
				subject: subject
		}
		$.ajax({
			type : 'POST',
			url : '/nera/email-template/update',
			data: JSON.stringify(up),
			contentType: "application/json",
			beforeSend : function() {
				//
			},
			success : function(data) {
				loadEmailTemplate();
				$('#detailEmailTab').addClass('hideclass');
				$('#listingEmailTab').removeClass('hideclass');
			},
			error : function(err) {
				//
			},
		});
	});
});

function searchEmailTemplate(search) {
	var jasonData = {
			searchString: search,
		};
	$.ajax({
		type : 'GET',
		url : '/nera/email-template/find-all',
		data: jasonData,
		beforeSend : function() {
			//
		},
		success : function(data) {
			var re = data;
			$('#tblListing > tbody').html('');
			for (var i = 0; i < re.length; i++) {
				var no = i + 1;
				var id = re[i].id;
				var name = re[i].templateName;
				var status = re[i].active == false ? "Inactive" : "Active";
				generateEmailListingTable(no, id, name, status);
			}
		},
		error : function(err) {
			//
		},
	});
}

//validated name
function checkExistingTemplateName(name) {
	var templateName = $(name).val().trim();
	$.get(
		'/nera/email-template/validatedName',
		{
			'templateName' : templateName,
			'id' : idUpdate,
		},
		function (data) {
			if (data == true) {
				$('#existNameError').css('display', 'block');
				$('#btnUpdateEmailTemplate').attr('disabled', true);
			} else {
				$('#btnUpdateEmailTemplate').removeAttr('disabled');
			}
		});
}

function checkNameEmpty(value) {
	var text = $(value).val().trim();
	if(text == ""){
		$('#emptyNameError').css('display', 'block');
	}
}

function hideNameError() {
	$('#emptyNameError').css('display', 'none');
	$('#existNameError').css('display', 'none');
}

function hideSubjectError() {
	$('#emptySubjectError').css('display', 'none');
}

function checkSubjectEmpty(value) {
	var text = $(value).val().trim();
	if(text == ""){
		$('#emptySubjectError').css('display', 'block');
	}
}
function reset() {
	$('#emailKeySearch').val("");
	loadEmailTemplate();
}