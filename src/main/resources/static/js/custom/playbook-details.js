//
var checkSendToApproval;

function openFile(event) {
    let fileData = new FormData();
    //let show = "";
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
            if (ex) {
                $('#getContenOfFile').html(this.result);
                $('.displayitem').html('');
                $('.displayitem').append('<span>' + this.name + '</span>');
            } else {
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

function getextensionfile(a) {
    var extension = "";
    var re = false;
    if (a != "") {
        extension = a.replace(/^.*\./, '');
        if (extension == "yml") {
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
    for (var i = 0; i < fileListing.length; i++) {
        if (fileListing[i].id == get) {
            name = fileListing[i].filename;
            downloadFileCSV(name);
            reDownload();
            return false;
        }
    }
}

function downloadFileCSV(name) {
    $.ajax({
        url: '/nera/upload/downloadFile/' + name,
        type: 'GET',
        xhrFields: {
            responseType: 'blob'
        },
        success: function (data) {

            var csvData = new Blob([data], {type: '.yml'});
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
        error: function (response) {
            console.log(response);
        }
    });
}

function reDownload() {
    $('.redownload').on('click', function () {
        var get = $(this).attr('data-id');
        $('#selectedFile').val(get);
        var name = "";
        for (var i = 0; i < fileListing.length; i++) {
            if (fileListing[i].id == get) {
                name = fileListing[i].filename;
                downloadFileCSV(name);
                return false;
            }
        }
    });
}

function changeoftype() {
    var ck = $('#selectedType').val();
    if (ck == 1) {
        $('#changeofinput').removeClass('hideclass');
        $('#changeoftype').addClass('hideclass');
    } else {
        $('#changeofinput').addClass('hideclass');
        $('#changeoftype').removeClass('hideclass');
        $('.select2 .select2-selection--single').css("height", "38px");
    }
}

genNavigation("playbook");

function deleteARow() {
    $('.pldDeleteRow').on('click', function () {
        this.closest('tr').remove();
    });
}

function editARow() {
    $('.pldEditRow').unbind().on('click', function (e) {
        e.stopPropagation();
        var row = $(this).closest('tr');
        var drop1 = row.find('.gettypevalue').attr('data-id');
        var col2 = row.find('.getvariable').text().trim();
        var drop3 = row.find('.getfilevalue').text().trim();
        var drop33 = row.find('.getfilevalue').attr('data-id');
        var mandatory = row.find('.mandatorychecked').is(":checked");
        $('#tblAddInput').removeClass('hideclass');
        maptostandard(drop1, col2, drop3, drop33, mandatory);
        //row.remove();
        $('.notstandard').removeClass('editingrow');
        row.addClass('editingrow');
    });
}

function maptostandard(drop1, col2, drop3, drop33, mandatory) {

    //$('#selectedType').val(drop1);
    $('#selectedType').val(drop1).trigger('change');
    $('#pldVariable').val(col2);
    if (mandatory == true) {
        $('#pldMandatory').prop('checked', true);
    } else {
        $('#pldMandatory').prop('checked', false);
    }
    if (drop1 == 1) {
        $('#pldValue').val(drop3);
    } else {
        $('#selectedFile').val(drop33).trigger('change');
    }
}

$(document).ready(function () {
    $('#system-menu-left').removeClass('kt-menu__item--open');
    $('#dashboard-menu-left').removeClass('kt-menu__item--open');
    $('#workflow-menu-left').removeClass('kt-menu__item--open');
    $('#job-management-menu-left').removeClass('kt-menu__item--open');
    $('#job-planning-menu-left').removeClass('kt-menu__item--open');
    $('#my-job-menu-left').removeClass('kt-menu__item--open');
    initLayout();

    function initLayout() {
        var ck = $('#getPlaybookid').val();
        if (ck > 0) {
            $('.layCreate').removeClass('hideclass');
        }
        $('#selectedType').select2({width: 'resolve', dropdownAutoWidth: true});
    }

    // Create a new playbook area
    $('#btnAddInput').click(function () {
        $('#tblPlaybookInput').removeClass('hideclass');
        $('#tblAddInput').removeClass('hideclass');
    });

    loadFileAll();

    function loadFileAll() {
        $.ajax({
            type: 'GET',
            url: '/nera/file-management/api/get-all',
            success: function (data) {
                var re = data;
                var datatest = [];
                fileListing = re;
                var emptyRecord = new Option('', '');
                $("#selectedFile").append(emptyRecord);
                datatest.push({id: '', text: ''})

                for (var i = 0; i < re.length; i++) {
                    var op = new Option(re[i].name, re[i].id);
                    $("#selectedFile").append(op);

                    var te = '<div>' + re[i].name + '-' + '<a href="#" data-id="' + re[i].id + '" class="redownload" style="color: blue;">' + re[i].filename + '</a></div>';
                    datatest.push({id: re[i].id, text: te});
                }
                appendFileRow(datatest);
            },
        });
    }

    function appendFileRow(datatest) {
        $("#selectedFileTest").select2({
            data: datatest,
            templateResult: function (d) {
                return $(d.text);
            },
            templateSelection: function (d) {
                return $(d.text);
            },
            selectionTitleAttribute: false,
            width: 'resolve'
        });
        removeTitle();
        reDownload();
    }

    function removeTitle() {
        $('.select2-selection__rendered').hover(function () {
            $(this).removeAttr('title');
        });
    }

    $('.pldConfirm').on('click', function(){
        if($('#pldVariable').val().trim()=="") {
            $('#pldVariable').removeClass('input-wrong');
            setTimeout(function(){
                $('#pldVariable').addClass('border border-danger input-wrong');
                $('#pldValue').removeClass('border border-danger input-wrong');
//				$('.select2 .selection .select2-selection--single').removeClass('border border-danger input-wrong');
			}, 100);
			$('#spErrorInput').removeClass('hideclass');
		} else{
			$('#pldVariable').removeClass('border border-danger input-wrong');
			$('#pldValue').removeClass('border border-danger input-wrong');
//			$('.select2 .selection .select2-selection--single').removeClass('border border-danger input-wrong');
            $('#spErrorInput').addClass('hideclass');
            pldAddNewRow();
            $('.editingrow').remove();
        }
    });

    function pldAddNewRow() {
        let ck = $('#selectedType').val();
        let col1 = $('#selectedType option:selected').text().trim();
        let col11 = $('#selectedType option:selected').val();
        let col2 = $('#pldVariable').val();
        let checked = $('#pldMandatory').is(":checked");
        let col3;
        let col31;
        if (ck == 1) {
            col3 = $('#pldValue').val();
            col31 = "";
        } else {
            col3 = $('#selectedFile option:selected').text().trim();
            col31 = $('#selectedFile option:selected').val();
        }

        appendNewRow(col1, col11, col2, col3, col31, checked);
    }

    function appendNewRow(col1, col11, col2, col3, col31, checked) {
        let mandatory = "";
        if (checked == true || checked == 1) {
            mandatory = '<td class="resizeCol" style="vertical-align: middle;"><input style="transform: scale(1.5);" disabled="disabled" type="checkbox" class="mandatorychecked" data-id="' + checked + '" checked></td>'
        } else {
            mandatory = '<td class="resizeCol" style="vertical-align: middle;"><input style="transform: scale(1.5);" disabled="disabled" type="checkbox" class="mandatorychecked" data-id="' + checked + '"></td>'
        }
        $('#tblPlaybookInput > tbody:last').append('<tr class="notstandard">'
            + '<td style="text-align:center; vertical-align: middle; text-transform: capitalize;" class="gettypevalue" data-id="' + col11 + '">' + col1.toLowerCase() + '</td>'
            + '<td class="getvariable resizeCol" style="vertical-align: middle;">' + col2 + '</td>'
            + '<td class="getfilevalue resizeCol" style="vertical-align: middle;" data-id="' + col31 + '">' + col3 + '</td>'
            + mandatory
            + '<td style="padding-left:5%">'
            + '<span><a href="#" class="btn btn-hover-brand btn-icon btn-pill pldEditRow"><i class="la la-pencil"></i></a></span>'
            + '<span><a href="#" class="btn btn-hover-danger btn-icon btn-pill pldDeleteRow"><i class="la la-trash"></i></a></span>'
            + '</td>'
            + '</tr>');
        $('#pldVariable').val("");
        $('#pldValue').val("");
        $('#selectedFile').val('');
        $('#selectedFileTest').val('').trigger('change');
        deleteARow();
        editARow();
    }

    function appendOutputRow(variable, value) {
        $('#tblPlaybookOutput').removeClass('hideclass');
        $('#tblPlaybookOutput > tbody:last').append('<tr class="notstandard">'
            + '<td style="text-align:center; max-width:11em; word-wrap: break-word" class="outputVariable">' + variable + '</td>'
            + '<td style="text-align:center; max-width:11em; word-wrap: break-word" class="outputValue">' + value + '</td>'
            + '</tr>');
    }



    createanewplaybook();

    function createanewplaybook() {
        $('#btnCreateNewPlaybook').on('click', function () {
            checkSendToApproval = 0;
            var name = $('#pldGetPlaybookName').val();
            if (name == "") {
                $('.nameRequired').removeClass('hideclass');
                return false;
            } else {
                $('.nameRequired').addClass('hideclass');
            }

            let ckf = false;
            if ($('.displayitem').text().trim() !== "") {
                ckf = true;
                $('.uploadRequired').addClass('hideclass');
            } else {
                $('.uploadRequired').removeClass('hideclass');
                e.stopImmediatePropagation();
            }

            var fileUpload = $("#FileUpload").get(0);
            var files = fileUpload.files;

            var playbookinput = [];
            $('#tblPlaybookInput tbody .notstandard').each(function () {
                var filerecord = {
                    type: $(this).find('.gettypevalue').text().trim().toUpperCase(),
                    variable: $(this).find('.getvariable').text().trim(),
                    value: $(this).find('.getfilevalue').text().trim(),
                    mandatory: $(this).find('.mandatorychecked').is(":checked"),
                    fileManagementId: $(this).find('.getfilevalue').attr('data-id'),
                };
                playbookinput.push(filerecord);
            });

            let playbookoutput = [];
            $('#tblPlaybookOutput tbody .notstandard').each(function () {
                var filerecord = {
                    variable: $(this).find('.outputVariable').text().trim(),
                    value: $(this).find('.outputValue').text().trim(),
                };
                playbookoutput.push(filerecord);
            });

            let fileData = new FormData();
            if (ckf) {
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

            if ($('#getPlaybookid').val() > 0) {
                jasonData["status"] = $('.forstatus').attr('data-value');
                jasonData["active"] = $('input[name=radio2]:checked').val();
            } else {
                jasonData["status"] = "NEW";
            }

            var count = 0;
            if (ckf) {
                count = files.length;
            }
            let up = fileData;
            let cre = jasonData;
            confirmtocontinue(count, up, cre);
        });

        $('#btnCreateAndSendToApproved').on('click', function () {
            checkSendToApproval = 1;
            var name = $('#pldGetPlaybookName').val();
            if (name == "") {
                $('.nameRequired').removeClass('hideclass');
                return false;
            } else {
                $('.nameRequired').addClass('hideclass');
            }

			let ckf = false;
            if ($('.displayitem').text().trim() != "") {
                ckf = true;
                $('.uploadRequired').addClass('hideclass');
            } else {
                $('.uploadRequired').removeClass('hideclass');
                e.stopImmediatePropagation();
            }

			let fileUpload = $("#FileUpload").get(0);
			let files = fileUpload.files;

			let playbookinput = [];
            $('#tblPlaybookInput tbody .notstandard').each(function () {
				let filerecord = {
                    type: $(this).find('.gettypevalue').text().trim().toUpperCase(),
                    variable: $(this).find('.getvariable').text().trim(),
                    value: $(this).find('.getfilevalue').text().trim(),
                    mandatory: $(this).find('.mandatorychecked').is(":checked"),
                    fileManagementId: $(this).find('.getfilevalue').attr('data-id'),
                };
                playbookinput.push(filerecord);
            });

			let playbookinputconfirm = [];
            $('#tblPlaybookInput tbody .notstandard').each(function () {
				let filerecord = {
                    inputtype: $(this).find('.gettypevalue').text().trim().toUpperCase(),
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

			let playbookOutputConfirm = [];
            $('#tblPlaybookOutput tbody .notstandard').each(function () {
				let filerecord = {
                    variable: $(this).find('.outputVariable').text().trim(),
                    value: $(this).find('.outputValue').text().trim(),
                };
                playbookOutputConfirm.push(filerecord);
            });

            let fileData = new FormData();
            if (ckf) {
                for (var i = 0; i < files.length; i++) {
                    fileData.append('file', files[i]);
                }
            }

            var jasonData = {
                name: $('#pldGetPlaybookName').val(),
                playbookInput: playbookinput,
                playbookOutput: playbookoutput,
                note: $('#pldNotes').val(),
                sendToApproved: true,
                //status: "NEW",
            };

            if ($('#getPlaybookid').val() > 0) {
                jasonData["status"] = $('.forstatus').attr('data-value');
                jasonData["active"] = $('input[name=radio2]:checked').val();
            } else {
                jasonData["status"] = "NEW";
            }

            addconfirm(jasonData, playbookinputconfirm, playbookOutputConfirm);
            var count = 0;
            if (ckf) {
                count = files.length;
            }
            let up = fileData;
            let cre = jasonData;
            confirmSendToApproved(count, up, cre);
        });
    }

    function addconfirm(jasonData, playbookinputconfirm, playbookOutputConfirm) {

        if (jasonData.name == "") {
            return false;
        }
        $('#reName').text(jasonData.name);

        if ($('#getPlaybookid').val() > 0) {
            $('#reFile').html($('.displayitem').text().trim());
            $('#reFileContent').html($('#getContenOfFile').text().trim());
        } else {
            if ($('.displayitem').text().trim() != "") {
                var displayUpload = $("#FileUpload").get(0);
                var displayfiles = displayUpload.files;
                if (displayfiles.length > 0) {
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

    function confirmtocontinue(count, up, cre) {
        if ($('#getPlaybookid').val() > 0) {
            cre["id"] = $('#getPlaybookid').val();
            if (count > 0) {
                if ($('.displayitem').text().trim() != "") {
                    callUploadFileApi(up, cre);
                }
            } else {
                cre["fileName"] = $('.displayitem').text().trim();
                cre["sourceUrl"] = $('.displayitem').attr('data-value');
                callUpdatePlaybookApi(cre);
            }
        } else {
            if (count > 0) {
                callUploadFileApi(up, cre);
            } else {
                callCreateNewPlaybookApi(cre);
            }
        }
    }

    function confirmSendToApproved(count, up, cre) {
        $('#btnResultResponse').unbind().click(function () {
            console.log('len of create obj : __________' + cre.length);
            if ($('#getPlaybookid').val() > 0) {
                cre["id"] = $('#getPlaybookid').val();
                if (count > 0) {
                    if ($('.displayitem').text().trim() != "") {
                        callUploadFileApi(up, cre);
                    }
                } else {
                    cre["fileName"] = $('.displayitem').text().trim();
                    cre["sourceUrl"] = $('.displayitem').attr('data-value');
                    callUpdatePlaybookApi(cre);
                }
            } else {
                if (count > 0) {
                    callUploadFileApi(up, cre);
                } else {
                    callCreateNewPlaybookApi(cre);
                }
            }

        });
    }

    function callUploadFileApi(up, cre) {
        $.ajax({
            type: 'POST',
            url: '/nera/upload/playbook',
            data: up,
            contentType: false,
            processData: false,
            success: function (data) {
                cre["fileName"] = data.fileInfo.fileName;
                cre["sourceUrl"] = data.fileInfo.fileDownloadUri;
                if ($('#getPlaybookid').val() > 0) {
                    callUpdatePlaybookApi(cre);
                } else {
                    callCreateNewPlaybookApi(cre);
                }
            },
        });
    }

    function callCreateNewPlaybookApi(cre) {
        $('#save-failed').css('display', 'none');
        $("#save-failed").empty();
        $.ajax({
            type: 'POST',
            url: '/nera/playbook/api/save',
            data: JSON.stringify(cre),
            contentType: "application/json",
            success: function (data) {
                $('#save-success').empty();
                let message = '';
                if (checkSendToApproval == 1) {
                    $('#addPlaybookConfirmation').modal('hide');
                    message = 'Request for Playbook approval has been sent successfully';
                } else {
                    message = 'Save successfully';
                }
                Swal.fire({
                    text: message,
                    type: 'success',
                    timer: 3000,
                    showConfirmButton: false
                });
                countPlaybookApproved();
                setTimeout(function () {
                    window.location.href = '../playbook';
                }, 3000);
            },
            error: function (err) {
                $('#save-failed').empty();
                let messageError = '';
                if (err.status === 400 || err.status === 500) {
                    messageError = err.responseJSON.detail;
                } else {
                    messageError = err.statusText;
                }
                Swal.fire({
                    text: messageError,
                    type: 'error',
                    timer: 3000,
                    showConfirmButton: false
                });
            },
        });
    }

    function callUpdatePlaybookApi(cre) {
        $('#save-failed').css('display', 'none');
        $("#save-failed").empty();
        $.ajax({
            type: 'POST',
            url: '/nera/playbook/api/update',
            data: JSON.stringify(cre),
            contentType: "application/json",
            success: function (data) {
                countPlaybookApproved();
                $('#save-success').empty();
                let message = '';
                if (checkSendToApproval == 1) {
                    $('#addPlaybookConfirmation').modal('hide');
                    message = 'Request for Playbook approval has been sent successfully';
                } else {
                    message = 'Update successfully';
                }
                Swal.fire({
                    text: message,
                    type: 'success',
                    timer: 3000,
                    showConfirmButton: false
                });
                setTimeout(function () {
                    window.location.href = '../../playbook';
                }, 3000);
            },
            error: function (err) {
                $('#save-failed').empty();
                let messageError = '';
                if (err.status === 400 || err.status === 500) {
                    messageError = err.responseJSON.detail;
                } else {
                    messageError = err.statusText;
                }
                Swal.fire({
                    text: messageError,
                    type: 'error',
                    timer: 3000,
                    showConfirmButton: false
                });
            },
        });
    }

    function appendTableToConfirm(input, variable, value, mandatory) {
        if (mandatory == true) {
            mandatory = '<td style="vertical-align: middle;"><input class="mandatorycheckbox" disabled="disabled" type="checkbox" checked="true"></td>'
        } else {
            mandatory = '<td class="resizeCol" style="vertical-align: middle;text-align: center;"><input disabled="disabled" type="checkbox"></td>'
        }
        $('#tblResultResponse > tbody:last').append('<tr><td class="textcen" style="text-transform: capitalize;">' + input.toLowerCase() + '</td>'
            + '<td class="textcen" style="word-wrap: break-word; max-width: 200px;">' + variable + '</td>'
            + '<td class="textcen" style="word-wrap: break-word; max-width: 200px;">' + value + '</td>'
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

    function isUpdateorCreate() {
        let is = $('#getPlaybookid').val();
        if (is > 0) {
            $('#btnViewHistory').removeClass("hideclass");
            $('#btnDeletePlaybook').removeClass('hideclass');
            $('#pldNotes').attr('readonly');

            // load detail data for id
            let json = {id: is};
            $.ajax({
                type: 'GET',
                url: '/nera/playbook/api/get-by-id',
                data: json,
                contentType: "application/json",
                success: function (data) {
                    //
                    $('#pldGetPlaybookName').val(data.name);
                    let versionPlaybook = data.version == 0 ? 1 : data.version;
                    $('#txtVersionPlaybook').text("Version " + versionPlaybook);
                    readcontentoffile(data.sourceUrl);
                    $('.displayitem').attr('data-value', data.sourceUrl);

                    let sta = data.status;
                    $('.forstatus').attr('data-value', sta);
                    if (sta == "NEW") {
                        $('.staNew').removeClass('hideclass');
                        if (hasDeleteNewPermission) {
                            $('#btnDeletePlaybook').removeClass('hideclass');
                        } else {
                            $('#btnDeletePlaybook').addClass('hideclass');
                        }
                    }
                    if (sta == "DRAFT") {
                        $('.staDra').removeClass('hideclass');
                    }
                    if (sta == "APPROVED") {
                        $('.staApp').removeClass('hideclass');
                        $('.layValiRemark').removeClass('hideclass');
                        $('#pldRemark').attr('readonly', '');
                        $('#btnCreateNewPlaybook').removeClass('hideclass');
                        $('#btnCreateAndSendToApproved').addClass('hideclass');
                        if (hasDeletePermission) {
                            $('#btnDeletePlaybook').removeClass('hideclass');
                        } else {
                            $('#btnDeletePlaybook').addClass('hideclass');
                        }
                    }
                    if (sta == "REJECTED") {
                        $('.staRej').removeClass('hideclass');
                        $('.layValiRemark').removeClass('hideclass');
                        $('#btnCreateNewPlaybook').addClass('hideclass');
                        $('#btnCreateAndSendToApproved').addClass('hideclass');
                        if (hasDeletePermission) {
                            $('#btnDeletePlaybook').removeClass('hideclass');
                        } else {
                            $('#btnDeletePlaybook').addClass('hideclass');
                        }
                    }

                    $('#pldNotes').val(data.note);
                    $('#pldRemark').val(data.remark);
                    if (data.active == true) {
                        $('#radioA').prop('checked', true);
                    } else {
                        $('#radioI').prop('checked', true);
                    }

                    $('#tblPlaybookInput').removeClass('hideclass');
                    if (data.playbookInput.length > 0) {
                        let get = data.playbookInput;
                        for (let i = 0; i < get.length; i++) {
                            let col1 = get[i].type;
                            let col11 = 1;
                            let col2 = get[i].variable;
                            let col3 = get[i].value;
                            let checked = get[i].mandatory;
                            let col31;
                            if (get[i].type === "TEXT") {
                                col31 = "";
                            } else {
                                col31 = get[i].fileManagementId == null ? "" : get[i].fileManagementId;
                            }
                            appendNewRow(col1, col11, col2, col3, col31, checked);
                        }
                    }
                    $('#tblPlaybookOutput').unbind().removeClass('hideclass');
                    if (data.playbookOutput.length > 0) {
                        var outputList = data.playbookOutput;
                        for (let i = 0; i < outputList.length; i++) {
                            var variable = outputList[i].variable;
                            var value = outputList[i].value;
                            appendOutputRow(variable, value);
                        }
                    }
                },
            });
        }
    }

    function readcontentoffile(sourceUrl) {

        if (sourceUrl == "") {
            return false;
        }

        $("#getContenOfFile").load(sourceUrl, function (responseTxt, txtStatus, jqXHR) {
            if (txtStatus == "success") {
                $('.displayitem').append('<span>' + sourceUrl.split('/').pop() + '</span>');
            }
            if (txtStatus == "error") {
                console.log("Error: " + jqXHR.status + ", " + jqXHR.statusText);
            }
        });

    }

    // Back to list screen
    $('#btnBackToPlay').on('click', function () {
        if ($('#getPlaybookid').val() > 0) {
            window.location.href = '../../playbook';
        } else {
            window.location.href = '../playbook';
        }
    });

    // Delete by id
    $('#btnDeletePlaybook').on('click', function () {
        $('#kt_modal_Delete').modal('show');
    });

    $('#btnYesDelete').on('click', function () {
        var id = $('#getPlaybookid').val();
        $('#kt_modal_Delete').modal('hide');
        $.ajax({
            type: 'POST',
            url: '/nera/playbook/api/check-used-by-workflow',
            data: {id: id},
            success: function (data) {
                if (data) {
                    Swal.fire({
                        text: 'The current playbook is used by the workflow. Cannot delete',
                        type: 'error',
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
                    text: 'Delete successful.',
                    type: 'error',
                    timer: 3000,
                    showConfirmButton: false
                });

                setTimeout(function () {
                    window.location.href = '../../playbook';
                }, 2000);
            },
            error: function (err) {
                Swal.fire({
                    text: 'Delete failed.',
                    type: 'error',
                    timer: 3000,
                    showConfirmButton: false
                });
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

    $('#btnViewHistory').on('click', function () {
        window.location.href = '/menu/masterdata/playbook/history/' + $('#getPlaybookid').val();
    });
});


//massively input
function openFileMassively() {
    //let show = "";
    let displayUpload = $("#massivelyUpload").get(0);
    let FileSize = displayUpload.files[0].size / 1024 / (1024); // in MBlet
    if(getExtension(displayUpload.files[0].name).toLowerCase() !== 'csv'){
        alert('Invalid selected file type.' +
            ' Please try again with the csv file');
        $("#massivelyUpload").val(''); //for clearing with Jquery
    }
    else if (FileSize > 1) {
        alert('The selected file is too big.' +
            ' Please try again with the file size less than 1 MB');
        $("#massivelyUpload").val(''); //for clearing with Jquery
    } else{
        let displayFiles = displayUpload.files;
        $('.displayMassively').html('');
        $('.displayMassively').append('<span>' + displayFiles[0].name + '</span>');
        let reader = new FileReader();
        reader.readAsText(displayFiles[0]);
        reader.onload = loadData;
    }
}
function loadData(event) {
    let csv = event.target.result;
    let rowData = csv.split(/\r?\n|\r/);
    let dataCheckDup =[{}];
    let checkErr = false;
    for(let i = 1; i<rowData.length ; i++){
        if(rowData[i].length === 0){
            continue;
        }
        let row = rowData[i].split(',');
        if(row.length!==4){
            alert('Invalid importing template.' +
                ' Please try again');
            checkErr = true;
            break;
        } else {
            dataCheckDup.push({
                type: row[0],
                variable: row[1],
                value: row[2],
                mandatory: row[3]
            })
        }
    }
    dataCheckDup = dataCheckDup.slice(1);
    const setArr = dataCheckDup.map(e => e.variable)
        .map((e, i, final) => final.indexOf(e) === i && i)
        .filter(e => dataCheckDup[e]).map(e => dataCheckDup[e]);
    const success = setArr.length;
    const failed = dataCheckDup.length - setArr.length;
    if(checkErr === false){
        forceData(setArr);
        $('.displayMassively').append('<label class="ml-2">Success: '+ success + ' Failed: '+ failed + '</label>');
    } else {
        $('.displayMassively').append('<label class="ml-2">Success: '+ 0 + ' Failed: '+ rowData.length + '</label>');
    }

}
function forceData(setArr){
    for(let i = 0; i< setArr.length; i++){
        appendNewRowMassively(setArr[i].type, setArr[i].type === 'Text'? '1' : '2', setArr[i].variable,
            setArr[i].value, setArr[i].type === 'Text'? '': setArr[i].value, setArr[i].mandatory === 'Yes' ? true : false);
    }
}
function appendNewRowMassively(col1, col11, col2, col3, col31, checked) {
    let mandatory = "";
    if (checked == true || checked == 1) {
        mandatory = '<td class="resizeCol" style="vertical-align: middle;"><input style="transform: scale(1.5);" disabled="disabled" type="checkbox" class="mandatorychecked" data-id="' + checked + '" checked></td>'
    } else {
        mandatory = '<td class="resizeCol" style="vertical-align: middle;"><input style="transform: scale(1.5);" disabled="disabled" type="checkbox" class="mandatorychecked" data-id="' + checked + '"></td>'
    }
    $('#tblPlaybookInput > tbody:last').append('<tr class="notstandard">'
        + '<td style="text-align:center; vertical-align: middle; text-transform: capitalize;" class="gettypevalue" data-id="' + col11 + '">' + col1.toLowerCase() + '</td>'
        + '<td class="getvariable resizeCol" style="vertical-align: middle;">' + col2 + '</td>'
        + '<td class="getfilevalue resizeCol" style="vertical-align: middle;" data-id="' + col31 + '">' + col3 + '</td>'
        + mandatory
        + '<td style="padding-left:5%">'
        + '<span><a href="#" class="btn btn-hover-brand btn-icon btn-pill pldEditRow"><i class="la la-pencil"></i></a></span>'
        + '<span><a href="#" class="btn btn-hover-danger btn-icon btn-pill pldDeleteRow"><i class="la la-trash"></i></a></span>'
        + '</td>'
        + '</tr>');
    deleteARow();
    editARow();

}
function getExtension(filename) {
     parts = filename.split('.');
    return parts[parts.length - 1];
}