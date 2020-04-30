$(document).ready(function () {
    checkListRole();

	$('.cbx-perm[data-level=2]').click(function () {
		var dataid = $(this).attr('data-id');
		var role = $(this).attr('data-role');
        if (this.checked) {				
            $('.cbx-perm[data-level=3][data-role='+ role +'][data-parent='+ dataid +']').prop('checked', true);
        }
        else {
            $('.cbx-perm[data-level=3][data-role='+ role +'][data-parent='+ dataid +']').prop('checked', false);
        }
    });

    $('.cbx-perm[data-level=3]').click(function () {
		var parentid = $(this).attr('data-parent');
		var role = $(this).attr('data-role');
        if ($('.cbx-perm[data-level=3][data-role='+ role +'][data-parent='+ parentid +']').length == $('.cbx-perm[data-level=3][data-role='+ role +'][data-parent='+ parentid +']:checked').length) {
            $('.cbx-perm[data-level=2][data-role='+ role +'][data-id='+ parentid +']').prop('checked', true);
        }
        else {
            $('.cbx-perm[data-level=2][data-role='+ role +'][data-id='+ parentid +']').prop('checked', false);
        }
    });

    $('.collapse-button').on('click', function(e){
    	e.preventDefault();
    	var target = $(this).data('target');
    	if($(target).length){
    		$(target).toggleClass('collapsed');
    		$(this).toggleClass('collapsed');
    	}
    });
    
    var $fixedHeader = $("#header-fixed");
    
    fixedTableHeader();
    
    function fixedTableHeader(){
    	$fixedHeader.empty();
        var $header = $("#table-1 > thead").clone();
        $("#table-1 > thead th").each(function(index){
        	var width = $(this).outerWidth();
        	$header.find('th').eq(index).css('width', width);
        });
        
        $fixedHeader.append($header).css('width', $("#table-1 > thead").outerWidth());
    }
    
    $(window).resize(function(){
    	fixedTableHeader();
    });
    
    var tableOffset = $("#table-1").offset().top;
    
    $(window).bind("scroll", function() {
        var offset = $(this).scrollTop() + 65;
        
        if (offset >= tableOffset && $fixedHeader.is(":hidden")) {
            $fixedHeader.show();
        }
        else if (offset < tableOffset) {
            $fixedHeader.hide();
        }
    });
	
});
function checkListRole() {
    var number;
    if(typeof check  !== "undefined"){
        var num = 0;
        $.each(check, function(i, c) {
            num = num +1;
            var index = 0;
            $.each(roles, function(n, r) {
                number = index+num;
                index = index + 1;
                if ($('.cbx-perm[data-level=3][data-role='+ i.replace(/\s/g, '') +'][data-parent='+ number +']').length === $('.cbx-perm[data-level=3][data-role='+ i.replace(/\s/g, '') +'][data-parent='+ number +']:checked').length) {
                    $('.cbx-perm[data-level=2][data-role='+ i.replace(/\s/g, '') +'][data-id='+ number +']').prop('checked', true);
                    if(i === "System") {
                        $('.cbx-perm[data-level=2][data-role='+ i.replace(/\s/g, '') +'][data-id='+ number +']').prop('checked', true);
                        $('.cbx-perm[data-level=2][data-role='+ i.replace(/\s/g, '') +'][data-id='+ number +']').attr("disabled", true);
                        $('.cbx-perm[data-level=3][data-role='+ i.replace(/\s/g, '') +'][data-parent='+ number +']').attr("disabled", true);
                    }
                }
            });
        });
    }
}

