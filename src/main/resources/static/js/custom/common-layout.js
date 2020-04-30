//
function genNavigation(current){
	$('.kt-menu__item--submenu').addClass('kt-menu__item--open');
	var module = "";
	switch (current) {
	  case "user":
		  module = "user";
		  $('').removeClass('kt-menu__item--open');
	    break;
	  case "playbook":
		  module = "playbook";
	    break;
	  case "playbookapproved":
		  module = "playbookapproved";
	    break;
	  case "inventory":
		  module = "inventory";
	    break;
	  case "filemanagement":
		  module = "filemanagement";
	}
	var selector = ".nav_" + module;
	$(selector).addClass('kt-menu__item--active');
}
function openMenuBar(event){
	if($('#kt_aside_toggler').hasClass('kt-aside__brand-aside-toggler--active')){
		$('#kt_aside_toggler').addClass('btn-bar');
		$('#kt_aside_toggler').removeClass('btn-bar-after');
	} else{
		$('#kt_aside_toggler').removeClass('btn-bar');
		$('#kt_aside_toggler').addClass('btn-bar-after');
	}
}
