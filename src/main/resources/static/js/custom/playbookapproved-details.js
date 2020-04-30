//
function openFile(event) {
    var displayUpload = $("#FileUpload").get(0);
    var displayfiles = displayUpload.files;
    for (var i = 0; i < displayfiles.length; i++) {
        var reader = new FileReader();
        reader.self = this;
        reader.name = displayfiles[i].name;
        reader.type = displayfiles[i].type;
        reader.onload = function (e) {
            $('#getContenOfFile').html(this.result);
            $('.displayitem').html('');
            $('.displayitem').append('<span>' + this.name + '</span>');
        };
        reader.readAsText(displayfiles[i]);
    }
}

genNavigation("playbookapproved");

$(document).ready(function () {
    $('#job-planning-menu-left').removeClass('kt-menu__item--open');
    $('#my-job-menu-left').removeClass('kt-menu__item--open');
    $('#job-management-menu-left').removeClass('kt-menu__item--open');
    //close another menu
    $('#system-menu-left').removeClass('kt-menu__item--open');
    $('#dashboard-menu-left').removeClass('kt-menu__item--open');
    $('#workflow-menu-left').removeClass('kt-menu__item--open');

    loadApprovedPlaybookDetail();

    function loadApprovedPlaybookDetail() {
        let is = $('#getPlaybookid').val();
        if (is > 0) {
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
                    if (sta == "NEW") {
                        $('.staNew').removeClass('hideclass');
                    }
                    if (sta == "DRAFT") {
                        $('.staDra').removeClass('hideclass');
                    }
                    if (sta == "APPROVED") {
                        $('.staApp').removeClass('hideclass');
                    }
                    if (sta == "REJECTED") {
                        $('.staRej').removeClass('hideclass');
                    }

                    $('#pldNotes').val(data.note);

                    if (data.playbookInput.length > 0) {
                        $('#tblPlaybookInput').removeClass('hideclass');
                        let get = data.playbookInput;
                        for (let i = 0; i < get.length; i++) {
                            let col1;
                            let col2;
                            let col3;
                            col1 = get[i].type.toLowerCase();
                            col2 = get[i].variable;
                            col3 = get[i].value;
                            appendNewRow(col1, col2, col3);
                        }
                    }
                    $('#tblPlaybookOutput').unbind().removeClass('hideclass');
                    if (data.playbookOutput.length > 0) {
                        let outputList = data.playbookOutput;
                        for (let i = 0; i < outputList.length; i++) {
                            let variable = outputList[i].variable;
                            let value = outputList[i].value;
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

    function appendNewRow(col1, col2, col3) {
        $('#tblPlaybookInput > tbody:last').append('<tr class="textcen">'
            + '<td style="text-transform: capitalize;">' + col1 + '</td>'
            + '<td style="text-align:center; max-width:10em; word-wrap: break-word">' + col2 + '</td>'
            + '<td style="text-align:center; max-width:10em; word-wrap: break-word">' + col3 + '</td>'
            + '</tr>');
    }

    function appendOutputRow(variable, value) {
        $('#tblPlaybookOutput').removeClass('hideclass');
        $('#tblPlaybookOutput > tbody:last').append('<tr class="notstandard">'
            + '<td style="text-align:center; max-width:11em; word-wrap: break-word">' + variable + '</td>'
            + '<td style="text-align:center; max-width:11em; word-wrap: break-word">' + value + '</td>'
            + '</tr>');
    }

    function readcontentoffile(sourceUrl) {

        if (sourceUrl == "") {
            return false;
        }

        $("#getContenOfFile").load(sourceUrl, function (responseTxt, txtStatus, jqXHR) {
            if (txtStatus == "success") {
                console.log("Success: " + jqXHR.status + ", " + jqXHR.statusText);
                $('.displayitem').append('<span>' + sourceUrl.split('/').pop() + '</span>');
            }
            if (txtStatus == "error") {
                console.log("Error: " + jqXHR.status + ", " + jqXHR.statusText);
            }
        });

    }

    $('#btnViewHistory').on('click', function () {
        window.location.href = '/menu/masterdata/playbook/history/' + $('#getPlaybookid').val();
    });

    // Approved / Rejected
    $('#btnApproved').on('click', function () {
        $('#kt_modal_App').modal('show');
    });

    $('#btnYesApp').on('click', function () {
        var st = "APPROVED";
        var rm = $('#appRemarks').val();
        callApprovedRejectedApi(st, rm);
    });

    $('#btnReject').on('click', function () {
        $('#kt_modal_Rej').modal('show');
    });

    $('#btnYesRej').on('click', function () {
        var st = "REJECTED";
        var rm = $('#rejRemarks').val();
        callApprovedRejectedApi(st, rm);
    });

    function callApprovedRejectedApi(st, rm) {
        $("#save-success").css("display", "none");
        var jasondata = {
            id: $('#getPlaybookid').val(),
            remark: rm,
            status: st,
        };
        var arr = [];
        arr.push(jasondata);

        $.ajax({
            type: 'POST',
            url: '/nera/playbook/api/approved-or-rejected-playbook',
            //data: arr,
            data: JSON.stringify(arr),
            contentType: "application/json",
            beforeSend: function () {
                //
            },
            success: function (data) {
                countPlaybookApproved();
                if (st === "APPROVED") {
                    $("#save-success").append("Approved success");
                } else {
                    $("#save-success").append("Rejected success");
                }

                $("#save-success").css("display", "block");
                $('#save-success').alert();
                $('#save-success').delay(4000).hide(200);
                $('#kt_modal_Rej').modal('hide');
                $("#kt_modal_App").modal('hide');

                setTimeout(function () {
                    window.location.href = '../../playbook-approved'; //will redirect to your blog page (an ex: blog.html)
                }, 2000); //will call the function after 2 secs.
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
                $('.setPlaybookApprovedCount').text('Playbook Approved (' + data + ')');
            },
            error: function (err) {
                //
            },
        });

    }

    // Back to list screen
    $('#btnBackToApp').on('click', function () {
        window.location.href = '../../playbook-approved';
    });

});
