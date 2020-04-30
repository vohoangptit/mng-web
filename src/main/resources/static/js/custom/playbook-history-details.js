$(document).ready(function () {
    getDetailPlaybook();

    function getDetailPlaybook() {
        var playbookHistoryId = $('#playbookHistoryId').val();
        $.ajax({
            type: 'GET',
            url: '/nera/playbook/api/history-detail',
            data: {id: playbookHistoryId},
            contentType: "application/json",
            success: function (data) {
                //
                $('#pldGetPlaybookName').val(data.name);
                $('#playbookId').val(data.playbookId);
                var versionPlaybook = data.version === 0 ? 1 : data.version;
                $('#txtVersionPlaybook').text("Version " + versionPlaybook);
                readcontentoffile(data.sourceUrl);
                $('.displayitem').attr('data-value', data.sourceUrl);
                var sta = data.status;
                $('.forstatus').attr('data-value', sta);
                if (sta == "NEW") {
                    $('.staNew').removeClass('hideclass');
                } else if (sta == "DRAFT") {
                    $('.staDra').removeClass('hideclass');
                } else if (sta == "APPROVED") {
                    $('.staApp').removeClass('hideclass');
                } else if (sta == "REJECTED") {
                    $('.staRej').removeClass('hideclass');
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
                    let col1;
                    let col11;
                    let col2;
                    let col3;
                    let col31;
                    let checked;
                    for (let i = 0; i < get.length; i++) {
                        col1 = get[i].type;
                        col11 = 1;
                        col2 = get[i].variable;
                        col3 = get[i].value;
                        checked = get[i].mandatory;
                        if (get[i].type == "Text") {
                            col31 = "";
                        } else {
                            col31 = get[i].fileManagementId == null ? "" : get[i].fileManagementId;
                        }
                        appendNewRow(col1, col11, col2, col3, col31, checked);
                    }
                }
                $('#tblPlaybookOutput').unbind().removeClass('hideclass');
                if (data.playbookOutput.length > 0) {
                    let outputList = data.playbookOutput;
                    for (let i = 0; i < outputList.length; i++) {
                        var variable = outputList[i].variable;
                        var value = outputList[i].value;
                        appendOutputRow(variable, value);
                    }
                }
            }
        });
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

    $('#btnRestore').on('click', function () {
        var id = $('#playbookHistoryId').val();
        $.ajax({
            type: 'GET',
            url: '/nera/playbook/api/check-playbook',
            data: {historyId: id},
            success: function (data) {
                if (data.indexOf("Cannot restore the playbook ") >= 0) {
                    Swal.fire({
                        title: 'Restore Failed',
                        text: data,
                        type: 'error',
                        timer: 5000,
                        showConfirmButton: false
                    });
                } else if (data === 'APPROVED') {
                    Swal.fire({
                        title: 'Are you sure?',
                        text: 'The current version is approved. Restore the previous version can need to ask for approval again. Continue?',
                        type: 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#5d78ff',
                        cancelButtonColor: '#ea6861',
                        confirmButtonText: 'Yes, restore it!'
                    }).then(function (result) {
                        if (result.value) {
                            funcRestorePlaybook(id);
                        }
                    });
                } else {
                    Swal.fire({
                        title: 'Are you sure?',
                        text: 'You want to restore to the previous version?',
                        type: 'question',
                        showCancelButton: true,
                        confirmButtonColor: '#5d78ff',
                        cancelButtonColor: '#ea6861',
                        confirmButtonText: 'Yes, restore it!'
                    }).then(function (result) {
                        if (result.value) {
                            funcRestorePlaybook(id);
                        }
                    });
                }
            }
        });
    });

    function funcRestorePlaybook(historyId) {
        $.ajax({
            type: 'POST',
            url: '/nera/playbook/api/restore-playbook',
            data: {historyId: historyId},
            success: function (data) {
                if (data) {
                    Swal.fire({
                        title: 'Restore Playbook',
                        text: 'Restore Playbook Successful',
                        type: 'success',
                        timer: 2000,
                        showConfirmButton: false
                    });
                    setTimeout(function () {
                        var playbookId = $('#playbookId').val();
                        window.location.href = '/menu/masterdata/playbook/history/' + playbookId;
                    }, 1800);
                } else {
                    Swal.fire({
                        text: 'Cannot restore playbook',
                        type: 'error',
                        timer: 2000,
                        showConfirmButton: false
                    });
                }
                $('#kt_modal_restore').modal('hide');
            },
        });
    }

    function appendNewRow(col1, col11, col2, col3, col31, checked) {
        var mandatory = "";
        if (checked == true || checked == 1) {
            mandatory = '<td class="resizeCol" style="vertical-align: middle;"><input style="transform: scale(1.5);" disabled="disabled" type="checkbox" class="mandatorychecked" data-id="' + checked + '" checked></td>'
        } else {
            mandatory = '<td class="resizeCol" style="vertical-align: middle;"><input style="transform: scale(1.5);" disabled="disabled" type="checkbox" class="mandatorychecked" data-id="' + checked + '"></td>'
        }
        $('#tblPlaybookInput > tbody:last').append('<tr class="notstandard">'
            + '<td style="text-align:center; vertical-align: middle" class="gettypevalue" data-id="' + col11 + '">' + col1 + '</td>'
            + '<td class="getvariable resizeCol" style="vertical-align: middle;">' + col2 + '</td>'
            + '<td class="getfilevalue resizeCol" style="vertical-align: middle;" data-id="' + col31 + '">' + col3 + '</td>'
            + mandatory
            + '</tr>');
        $('#pldVariable').val("");
        $('#pldValue').val("");
        $('#selectedFile').val('');
        $('#selectedFileTest').val('').trigger('change');
    }

    function appendOutputRow(variable, value) {
        $('#tblPlaybookOutput').removeClass('hideclass');
        $('#tblPlaybookOutput > tbody:last').append('<tr class="notstandard">'
            + '<td style="text-align:center; max-width:11em; word-wrap: break-word" class="outputVariable">' + variable + '</td>'
            + '<td style="text-align:center; max-width:11em; word-wrap: break-word" class="outputValue">' + value + '</td>'
            + '</tr>');
    }

    $('#btnBackToHistory').on('click', function () {
        window.location.href = '/menu/masterdata/playbook/history/' + $('#playbookId').val();
    });
});