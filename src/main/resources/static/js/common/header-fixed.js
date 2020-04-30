"use strict";

$ = jQuery;

//header fixed
var $fixedHeader = $("#header-fixed");

function fixedTableHeader() {
    if (!$fixedHeader.length) {
        $fixedHeader = $("#header-fixed");
    }
    $fixedHeader.empty();
    var $header = $(".kt-datatable__table > thead").clone();
    $(".kt-datatable__table > thead th").each(function (index) {
        var width = $(this).outerWidth();
        $header.find('th').eq(index).css('width', width);
    });

    $fixedHeader.append($header).css('width', $(".kt-datatable__table > thead").outerWidth());
}

$(window).resize(function () {
    fixedTableHeader();
});

$(window).bind("scroll", function () {
    if ($(".kt-datatable__table").length) {


        var offset = $(this).scrollTop() + 65;
        var tableOffset = $(".kt-datatable__table").offset().top;

        if (!$fixedHeader.length) {
            $fixedHeader = $("#header-fixed");
        }

        if (offset >= tableOffset && $fixedHeader.is(":hidden")) {
            $fixedHeader.show();
        } else if (offset < tableOffset) {
            $fixedHeader.hide();
        }
    }
});

$(document).ready(function () {
    $(document).on('show.bs.modal', '.modal', function () {
        var zIndex = 1040 + (10 * $('.modal:visible').not(this).length);
        $(this).css('z-index', zIndex);
        setTimeout(function () {
            $('.modal-backdrop').not('.modal-stack').css('z-index', zIndex - 1).addClass('modal-stack');
        }, 0);
    });
    $(document).on('hidden.bs.modal', '.modal', function () {
        $('.modal:visible').length && $(document.body).addClass('modal-open');
    });
});