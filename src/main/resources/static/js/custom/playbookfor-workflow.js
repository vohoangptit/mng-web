"use strict";

var fileListing = [];

function getFileDownload($i) {
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

loadFileAll();

function loadFileAll() {
    $.ajax({
        type: 'GET',
        url: '/nera/file-management/api/get-all',
        success: function (data) {
            var re = data;
            var datatest = [];
            fileListing = re;
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
        containerCssClass: 'select-box-fixsize',
        templateResult: function (d) {
            return $(d.text);
        },
        templateSelection: function (d) {
            return $(d.text);
        },
        selectionTitleAttribute: false,
    });
    removeTitle();
}

function removeTitle() {
    $('.select2-selection__rendered').hover(function () {
        $(this).removeAttr('title');
    });
}


function changeFieldFunc() {
    var ck = $('#selectedType').val();
    if (ck == 1) {
        $('#changeofinput').removeClass('hideclass');
        $('#changeoftype').addClass('hideclass');
    } else {
        $('#changeofinput').addClass('hideclass');
        $('#changeoftype').removeClass('hideclass');
    }
}

function loadDataAllPlaybook(name, id) {
    $.ajax({
        type: 'POST',
        url: '/nera/playbook/api/listing-playbook-approved',
        data: {searchString: name},
        success: function (data) {
            $('table#playbookTable tbody').empty();
            var table = $('table#playbookTable tbody');
            var htmlct = "";
            for (var i = 0; i < data.length; i++) {
                htmlct += '<tr class="table-row row-table-data';
                if (id == data[i]["id"]) {
                    htmlct += ' active';
                }
                htmlct += '">';
                htmlct += '<td><span style="display:none" id="dataid">' + data[i]["id"] + '</span><span>' + data[i]["name"] + '</span></td>';
                if (data[i]["approvedDate"] != null) {
                    htmlct += '<td><span>' + data[i]["approvedDate"] + '</span></td>';
                } else {
                    htmlct += '<td><span></span></td>';
                }
                htmlct += '<td><span>' + data[i]["approvedBy"] + '</span></td>';
                htmlct += "</tr>";
            }
            table.append(htmlct);
        }
    });
}

$(document).ajaxComplete(function () {
    $('table#playbookTable tbody tr').unbind().on('click', function (e) {
        $('.active').removeClass('active');
        $(this).addClass("active");
    });
    $('#btnCancel').unbind().on('click', function (e) {
        $('#playbookworkflow').modal('hide');
    });
    $('.btn-link').unbind().on('click', function (e) {
        $('#playbookSearch').val('');
        var idPlaybook = $('tbody tr.active').find('td').eq(0).find('#dataid').text();
        loadDataAllPlaybook('', idPlaybook);
        $('#btnCancel').removeClass('hideclass');
        $("#playbookworkflow").modal('show');
    });
});



