var hostData;
checkPermission();
function checkPermission() {
	if(!hasViewHostPermission && !hasViewGroupHostPermission) {
		window.location.href = '../../403';
	}
}

$( document ).ready(function() {
	var datatable;
	var reportTable;
	if (hasViewHostPermission) {
		datatable = $('#kt_datatable_host').KTDatatable({
			// datasource definition
			data: {
				type: 'remote',
				source: {
					read: {
						method: 'GET',
						url: '/nera/inventory/api/get-by-id',
						map: function(raw) {
							var dataSet = raw;
							hostData = raw.data;
							if (typeof raw.data !== 'undefined') {
								dataSet = raw.data;
								var total = raw.meta.total;
								$('.totalNum').text(total);
							}
							return dataSet;
						},
					},
				},
				pageSize: 10,
				saveState: {
					cookie: false,
					webstorage: false
				},
				serverPaging: true,
				serverFiltering: true,
				serverSorting: true
			},

			// layout definition
			layout: {
				scroll: false,
				footer: false,
			},

			selector: {
				class: 'kt-checkbox--solid'
			},

			// column sorting
			sortable: true,
			pagination: true,

			search: {
				input: $('#hostSearchKey')
			},

			// columns definition
			columns: [
				{
					field: 'no',
					title: 'S/N',
					sortable: false,
					width: 30,
					textAlign: 'left'
				},
				{
					field: 'name',
					title: 'Host Name',
					textAlign: 'left'
				},
				{
					field: 'description',
					title: 'Description'
				},
				{
					field: 'ipAddress',
					title: 'IP Address'
				},
				{
					field: 'port',
					title: 'Port'
				},
				{
					field: 'isActive',
					title: 'Status',
					template: function(row) {
						var status = row.active;
						var htmlEdit;
						if (status) {
							htmlEdit = '<button style="border-radius: 5px; border: 1px solid #1eb7ae; background-color: #1eb7ae;" disabled><span style="font-family: Poppins;color:var(--white)">Active</span></button>';
						} else{
							htmlEdit = '<button style="border-radius: 5px; border: 1px solid #7f7f7f; background-color: #7f7f7f;" disabled><span style="font-family: Poppins;color:var(--white)">InActive</span></button>';
						}
						return htmlEdit;
					},
				},
				{
					field: 'Actions',
					title: 'Actions',
					sortable: false,
					overflow: 'visible',
					textAlign: 'center',
					template: function(row) {
						var id = row.id;
						var htmlClone = '';
						var htmlEdit = '';
						var htmlDelete = '';
						if (hasEditHostPermission) {
							htmlEdit   = '<a href="#"  class="btn btn-hover-brand btn-icon btn-pill updateHost" data-id="' + id + '" title="Edit details"><i class="la la-edit"></i></a>';
						}
						if (hasCloneHostPermission) {
							htmlClone  = '<a href="#"  class="btn btn-hover-brand btn-icon btn-pill cloneHost" data-id="' + id + '" title="Clone host"><i class="la la-clone"></i></a>';
						}
						if(hasDeleteHostPermission) {
							htmlDelete = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill deleteHost" data-id="' + id + '" title="Delete host"><i class="la la-trash"></i></a>';
						}
						return htmlClone + htmlEdit + htmlDelete;
					},
				}],
		});
		$(document).on('click', '.deleteHost' , function() {
			deleteAHost($(this).attr('data-id'));
		});
		// $('#kt_datatable_host').DataTable( {
		// 	fixedHeader: true
		// } );
		$(document).on('click', '.updateHost' , function() {
			getHostById($(this).attr('data-id'), true);
		});
		$(document).on('click', '.cloneHost' , function() {
			getHostById($(this).attr('data-id'), false);
		})
	} else {
		$('#kt_datatable_host').empty();
	}

	function grep (id) {
		return jQuery.grep(hostData, function(obj, index) {
			return obj.id == id;
		});
	}

	/*function showUpdatePopup(id) {
		let hostDetail = grep(id)[0];
		$('#hostId').val(hostDetail.id);
		$('#hostName').val(hostDetail.name);
		$('#hostDescription').val(hostDetail.description);
		$('#hostAddress').val(hostDetail.ipAddress);
		$('#hostPort').val(hostDetail.port);
		$('#username').val(hostDetail.username);
		$('#password').val(hostDetail.password);
		if(hostDetail.active == true){
			$('#radioA').prop('checked', true);
		}
		else{
			$('#radioI').prop('checked', true);
		}
		$('#modHostConfirmation').modal();
	}*/

	/*function showClonePopup(id) {
		let hostDetail = grep(id)[0];
		$('#hostId').val('');
		var hostName = hostDetail.name.trim() + '_copy';
		$('#hostName').val(hostName);
		$('#hostDescription').val(hostDetail.description);
		$('#hostAddress').val(hostDetail.ipAddress);
		$('#hostPort').val(hostDetail.port);
		if(hostDetail.active == true){
			$('#radioA').prop('checked', true);
		}
		else{
			$('#radioI').prop('checked', true);
		}
		$('#modHostConfirmation').modal();
	}*/

	var datatableGroup;
	if (hasViewGroupHostPermission) {
		$('#tab-group').on('click', function(){
			// datatableGroup.reload();
			datatableGroup = $('#kt_datatable_group').KTDatatable({
				// datasource definition
				data: {
					type: 'remote',
					source: {
						read: {
							method: 'GET',
							url: '/nera/inventory/api/get-list-group',
							map: function(raw) {
								var dataSet = raw;
								if (typeof raw.data !== 'undefined') {
									dataSet = raw.data;
									var total = raw.meta.total;
									$('.totalNum').text(total);
								}
								return dataSet;
							},
						},
					},
					pageSize: 10,
					saveState: {
						cookie: false,
						webstorage: false
					},
					serverPaging: true,
					serverFiltering: true,
					serverSorting: true
				},

				// layout definition
				layout: {
					scroll: false,
					footer: false
				},

				selector: {
					class: 'kt-checkbox--solid'
				},

				// column sorting
				sortable: true,
				pagination: true,

				search: {
					input: $('#groupSearchKey'),
				},

				// columns definition
				columns: [
					{
						field: 'no',
						title: 'S/N',
						sortable: false,
						textAlign: 'left',
						width: 150
					},
					{
						field: 'name',
						title: 'Group Name',
						textAlign: 'left',
					},
					{
						field: 'numberHost',
						title: 'Number of Host',
						textAlign: 'center'
					},
					{
						field: 'isActive',
						title: 'Status',
						template: function(row) {
							var status = row.active;
							var htmlEdit;
							if (status) {
								htmlEdit = '<button style="border-radius: 5px; border: 1px solid #1eb7ae; background-color: #1eb7ae;" disabled><span style="font-family: Poppins;color:var(--white)">Active</span></button>';
							} else{
								htmlEdit = '<button style="border-radius: 5px; border: 1px solid #7f7f7f; background-color: #7f7f7f;" disabled><span style="font-family: Poppins;color:var(--white)">InActive</span></button>';
							}
							return htmlEdit;
						},
					},
					{
						field: 'Actions',
						title: 'Actions',
						sortable: false,
						textAlign: 'center',
						template: function(row) {
							var id = row.id;
							var htmlEdit = '';
							var htmlDelete = '';

							if (hasEditGroupHostPermission) {
								htmlEdit   = '<a href="#"  class="btn btn-hover-brand btn-icon btn-pill updateGroup" data-id="' + id + '" title="Edit details"><i class="la la-edit"></i></a>';
							}
							if (hasDeleteGroupHostPermission) {
								htmlDelete = '<a href="#" class="btn btn-hover-danger btn-icon btn-pill deleteGroup" data-id="' + id + '" title="Delete host"><i class="la la-trash"></i></a>';
							}
							return htmlEdit + htmlDelete;
						},
					}],
			});
		});
	} else {
		$('#kt_datatable_group').empty();
	}

	$(document).on('click', '.deleteGroup' , function() {
		deleteAGroup($(this).attr('data-id'));
	});
	$(document).on('click', '.updateGroup' , function() {
		updateAGroup($(this).attr('data-id'));
	});


	$("#kt_header_menu ul li span").text("Master Data/Inventory Management");
	//// Generate the layout
	settinglayout();
	function settinglayout()
	{
		$('.btnline1').addClass('hideclass');
		$('.btnline2').removeClass('hideclass');
		$('.inventorybody').removeClass('hideclass');
		loadInventoryDetail();
	}
	
	// load inventory information
	function loadInventoryDetail(){
		// var jasonData = {id: ck};
	    $.ajax({
	        type: 'GET',
	        url: '/nera/inventory/api/get-by-id',
	        // data: jasonData,
	        beforeSend: function () {
	            //
	        },
	        success: function (data) {
	            //
	            // $('#inInventoryId').val(data.id);
	            $('#inName').val(data.name);
	            if(data.isActive == 1){
	            	$('#inRadioA').prop('checked', true);
	            }
	            else{
	            	$('#inRadioI').prop('checked', true);
	            }
	            // 
	            var search = "";
	            loadhostlisting(search);
	            loadgrouplisting(search);
	            //
	            loadIpAddressAll();
	        },
	        error: function (err) {
	            //
	        },
	    });
	}
	
	
	
    //// Create a new inventory area
	$('#btnCreateInventory').on('click', function(){
		callCreateInventory();
	});

	function callCreateInventory()
	{
		var ckname = $('#inName').val();
		if(ckname == ""){
			$('.valiinname').removeClass('hideclass');
			return false;
		}
		else{
			$('.valiinname').addClass('hideclass');
		}
		var jasonData = {
			name: ckname,
			active: $('input[name=radio4]:checked').val(),
		};
		
	    $.ajax({
	        type: 'POST',
	        url: '/nera/inventory/api/save',
	        data: JSON.stringify(jasonData),
	        contentType: "application/json",
	        beforeSend: function () {
	            //
	        },
	        success: function (data) {
	            $('#inInventoryId').val(data.id);
	            $('.btnline1').addClass('hideclass');
	            $('.btnline2').removeClass('hideclass');
	            $('.inventorybody').removeClass('hideclass');
	            $('#btnBackToIn').removeClass('hideclass');
	            $('#btnBackToInList').addClass('hideclass');
	            window.location.href = '../inventory/detail/' + data.id;
	        },
	        error: function (err) {
	        	$('#save-failed').empty();
	        	if(err.status === 400){
	    			$("#save-failed").append("InventoryName has exist!");
	    		} else{
	    			$("#save-failed").append(err.statusText);
	    		}
				$('#save-failed').css('display', 'block');
				$('#save-failed').alert();
				$('#save-failed').delay(4000).hide(100);
	        },
	    });
	}
	
	// Update an inventory
	$('#btnUpdateInventory').on('click', function(){
		callUpdateInventory();
	});
	
	function callUpdateInventory()
	{
		var ckname = $('#inName').val();
		if(ckname == ""){
			$('.valiinname').removeClass('hideclass');
			return false;
		}
		else{
			$('.valiinname').addClass('hideclass');
		}
		var inventoryid = $('#inInventoryId').val();
		var jasonData = {
			id: inventoryid,
			name: ckname,
			isActive: $('input[name=radio4]:checked').val(),
		};
		
	    $.ajax({
	        type: 'POST',
	        url: '/nera/inventory/api/update',
	        data: JSON.stringify(jasonData),
	        contentType: "application/json",
	        beforeSend: function () {
	            //
	        },
	        success: function (data) {
	            if (data.code != '200') {
	                return false;
	            }
	            //
	            $('.btnline1').addClass('hideclass');
	            $('.btnline2').removeClass('hideclass');
    			window.location.href = '../../inventory';
	        },
	        error: function (err) {
	            //
	        },
	    });
	}
	
	// Delete an inventory
	$('#btnDeleteInventory').on('click', function(){
		$('#modInDeleteConfirmation').modal('show');
	});
	
	$('#btnYesDelete').on('click', function(){
		var inventoryid = $('#inInventoryId').val();
		if(inventoryid > 0){
			callDeleteInventory(inventoryid);
		}
	});

	$('#btnAddHost').on('click', function(){
		addHideClassHost();
    	$('#hostId').val('');
    	$('#hostName').val('');
    	$('#hostDescription').val('');
    	$('#hostAddress').val('');
    	$('#hostPort').val('');
    	$('#username').val('');
    	$('#password').val('');
		$('#radioI').prop('checked', true);
    	$('#btnHostDelete').addClass('hideclass');
		$('#modHostConfirmation').modal('show');
	});

	$('#btnHostSave').on('click', function(){
		var hostname = $('#hostName').val();
		if(hostname == ""){
			$('.valihostname').removeClass('hideclass');
			return false;
		}
		else{
			$('.valihostname').addClass('hideclass');
		}
		
		var hostaddress = $('#hostAddress').val();
		if(hostaddress == ""){
			$('.valiipaddress').removeClass('hideclass');
			return false;
		}
		else{
			$('.valiipaddress').addClass('hideclass');
			var ckip = ValidateIPaddress(hostaddress);
			if(ckip){
				$('.valiipaddressvalid').addClass('hideclass');
			}
			else{
				$('.valiipaddressvalid').removeClass('hideclass');
				return false;
			}
		}
		
		var port = $('#hostPort').val();
		if(hostaddress == ""){
			$('.valiportvalid').removeClass('hideclass');
			return false;
		}
		else{
			var ck = validatePort(port);
			if(ck){
				$('.valiportvalid').addClass('hideclass');
			}
			else{
				$('.valiportvalid').removeClass('hideclass');
				return false;
			}
		}

		var username = $('#username').val();
		if(username) {
			$('.validateusername').addClass('hideclass');
		}else {
			$('.validateusername').removeClass('hideclass');
			return false;
		}

		var password = $('#password').val();
		if(password) {
			$('.validatepassword').addClass('hideclass');
		}else {
			$('.validatepassword').removeClass('hideclass');
			return false;
		}

		callCreateUpdateHost();
	});
	
	function ValidateIPaddress(ipaddress) {  
		var pattern = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
		if (pattern.test(ipaddress)) {  
			return (true);
		}  
		return (false);
	}
	
	function validatePort(port) {  
		var pattern = /^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$/;
		if (pattern.test(port)) {  
			return (true);
		}  
		return (false);
	}
	
	function callCreateUpdateHost()
	{
		var hostid = $('#hostId').val();
		var hostname = $('#hostName').val();
		var des = $('#hostDescription').val();
		var ip = $('#hostAddress').val();
		var port = parseInt($('#hostPort').val());
		var status = $('input[name=hostradio4]:checked').val();
		var username = $('#username').val();
		var password = $('#password').val();

		var jsonData = {
			id: hostid,
			name: hostname,
			description: des,
			ipAddress: ip,
			port: port,
			active: status,
			username: username,
			password: password
		};
		var url = '';
		if(hostid > 0){
			url = '/nera/inventory/api/update-host';
			$.ajax({
				type: 'POST',
				url: '/nera/inventory/api/check-host-job',
				data: JSON.stringify(jsonData),
				contentType: 'application/json',
				success: function (data) {
					if(data) {
						Swal.fire({
							title: 'Are you sure?',
							text: 'The selected host is used by the jobs. So deactivate this host will impact to those jobs too. Continue?',
							type: 'warning',
							showCancelButton: true,
							confirmButtonColor: '#5d78ff',
							cancelButtonColor: '#ea6861',
							confirmButtonText: 'Yes, Update it!'
						}).then(function (result) {
							if (result.value) {
								addHost(jsonData, url);
							}
						});
					} else {
						addHost(jsonData, url);
					}
				}
			});
		}
		else{
			url = '/nera/inventory/api/add-host';
			addHost(jsonData, url);
		}
	}

	function addHost(jsonData, url) {
		$.ajax({
			type: 'POST',
			url: url,
			data: JSON.stringify(jsonData),
			contentType: "application/json",
			success: function (data) {
				if (data.code != '200') {
					return false;
				}
				var search = "";
				loadhostlisting(search);
				loadIpAddressAll();
				datatable.load();
				$('#modHostConfirmation').modal('hide');
				Swal.fire({
					text:  data.mess,
					type:  'success',
					timer: 2000,
					showConfirmButton: false
				});
			},
			error: function (err) {
				let message = '';
				if(err.status === 400 || err.status === 500){
					message = err.responseJSON.detail;
				} else if(err.status === 405){
					message = 'Port must be numeric';
				} else{
					message = 'Internal Server Error';
				}
				Swal.fire({
					text:  message,
					type:  'error',
					timer: 2000,
					showConfirmButton: false
				});
			},
		});
	}
	
	// Load host listing
	function loadhostlisting(search){
		var jasonData = {
			searchString: search,
		};
		
	    $.ajax({
	        type: 'GET',
	        url: '/nera/inventory/api/search-host',
	        data: jasonData,
	        beforeSend: function () {
	            //
	        },
	        success: function (data) {
	        	var re = data;
	        	$('#tblHostListing > tbody').html('');
	        	for(var i = 0; i < re.length; i++)
	    		{
	        		var no = i + 1;
		        	var id = re[i].id;
		        	var name = re[i].name;
		        	var des = re[i].description;
		        	var ip = re[i].ipAddress;
		        	var port = re[i].port;
		        	var status = re[i].active == false ? "Inactive" : "Active";
		        	generateHostTable(no, id, name, des, ip, port, status);
	    		}
	        	var textHost = 'Host';
	        	if (hasViewHostPermission) {
	        		textHost = textHost + ' ('+ re.length +')';
				}
	        	$('.setNumHost').text(textHost);
	        },
	        error: function (err) {
	            //
	        },
	    });
	}
	
	// Common generate host table
	function generateHostTable(no, id, name, des, ip, port, status, username, password) {
		var rowTable = '<tr class="">'
			+ '<td class="assignindex">' + no + '</td>'
			+ '<td class="tdHostName" data-id="' + id + '">' + name + '</td>'
			+ '<td class="tdDescript">' + des + '</td>'
			+ '<td class="tdIpAdd">' + ip + '</td>'
			+ '<td class="tdPort">' + port + '</td>'
			+ '<td class="tdUsername">' + username + '</td>'
			+ '<td class="tdPassword">' + password + '</td>'
			+ '<td class="tdStatus">' + status + '</td>'
			+ '<td>'
			+ '<span><a href="#" class="btn btn-hover-brand btn-icon btn-pill hostCloneRow"><i class="la la-clone"></i></a></span>';
		if (hasEditHostPermission) {
			rowTable = rowTable + '<span><a data-id="' + id + '" href="#" classostEditRow="btn btn-hover-brand btn-icon btn-pill hostEditRow"><i class="la la-pencil"></i></a></span>';
		}
		if (hasDeleteHostPermission) {
			rowTable = rowTable + '<span><a data-id="' + id + '" href="#" class="btn btn-hover-danger btn-icon btn-pill hostDeleteRow"><i class="la la-trash"></i></a></span>';
		}
	    $('#tblHostListing > tbody:last').append(rowTable + '</td>' + '</tr>');
	    
	    cloneANewHost();
	    loadHostDetail();
	    // deleteAHost();
	}
	
	// Clone a new host
	function cloneANewHost(){
		$('#kt_tabs_host .hostCloneRow').on('click', function(e){
			e.stopPropagation();
			var infor = $(this).closest('tr');
			var hostid = "";
			var hostname = infor.find('.tdHostName').text().trim() + "_copy";
			var des = infor.find('.tdDescript').text().trim();
			//let ip = infor.find('.tdIpAdd').text().trim();
			var port = infor.find('.tdPort').text().trim();
			var status = infor.find('.tdStatus').text().trim();
			
			// fill informations
        	$('#hostId').val(hostid);
        	$('#hostName').val(hostname);
        	$('#hostDescription').val(des);
        	$('#hostAddress').val('');
        	$('#hostPort').val(port);
        	if(status == "Active"){
        		$('#radioA').prop('checked', true);
        	}
        	else{
        		$('#radioI').prop('checked', true);
        	}
        	
        	$('#selectedHostName').text(infor.find('.tdHostName').text().trim());
        	$('#modHostConfirmation').modal('show');
		});
	}
	
//	$('#btnContinueToClone').on('click', function(){
//		$('#modHostCloneConfirmation').modal('hide');
//		$('#modHostConfirmation').modal('show');
//	});
	
	// Loading a host detail
	function loadHostDetail(){
		$('.hostEditRow').on('click', function(){
			var hostid = $(this).attr('data-id');
			addHideClassHost();
			var jasonData = {
				hostId: hostid,
			};
			
		    $.ajax({
		        type: 'GET',
		        url: '/nera/inventory/api/get-host-by-id',
		        data: jasonData,
		        beforeSend: function () {
		            //
		        },
		        success: function (data) {
		        	var re = data;
		        	$('#hostId').val(re.id);
		        	$('#hostName').val(re.name);
		        	$('#hostDescription').val(re.description);
		        	$('#hostAddress').val(re.ipAddress);
		        	$('#hostPort').val(re.port);
		        	$('#username').val(re.username);
		        	$('#password').val(re.password);
		        	if(data.active == true){
		        		$('#radioA').prop('checked', true);
		        	}
		        	else{
		        		$('#radioI').prop('checked', true);
		        	}
		        	$('#btnHostDelete').removeClass('hideclass');
		        	$('#modHostConfirmation').modal('show');
		        },
		        error: function (err) {
		            //
		        },
		    });
		});
	}

	function getHostById(id, statusCheck) {
		var jasonData = {
			hostId: id
		};
		var check = statusCheck;
		$.ajax({
			type: 'GET',
			url: '/nera/inventory/api/get-host-by-id',
			data: jasonData,

			success: function (data) {
				var re = data;
				if(check) {
					$('#hostId').val(re.id);
					$('#hostName').val(re.name);
				} else {
					$('#hostId').val('');
					$('#hostName').val(re.name+'_copy');
				}
				$('#hostDescription').val(re.description);
				$('#hostAddress').val(re.ipAddress);
				$('#hostPort').val(re.port);
				$('#username').val(re.username);
				$('#password').val(re.password);
				if(data.active == true){
					$('#radioA').prop('checked', true);
				}
				else{
					$('#radioI').prop('checked', true);
				}
				$('#btnHostDelete').removeClass('hideclass');
				$('#modHostConfirmation').modal('show');
			}
		});
	}
	
	// Delete a host
	function deleteAHost(id){
		let hostDetail = grep(id)[0];
		if(hostDetail.active === true) {
			Swal.fire({
				text:  'This is an active host. Only inactive host can be deleted.',
				type:  'error',
				timer: 2000,
				showConfirmButton: false
			});
		} else {
			$('#hostDeleteId').val(id);
			$('#modHostDeleteConfirmation').modal('show');
		}
	}
	
	$('#btnYesDeleteHost').on('click', function(){
		var hostid = $('#hostDeleteId').val();
		callDeleteAHostApi(hostid);
		$('#modHostDeleteConfirmation').modal('hide');
	});
    $('#dropdownMenuButton').on('click', function(){
        $('#modHostList').modal('show');
    });
    $('#btnCancelModalGroup').on('click', function(){
        $('#modHostList').modal('hide');
    });
    $('#btnHostDelete').on('click', function(){
		var hostid = $('#hostId').val();
		callDeleteAHostApi(hostid);
	});
	
	// Executing deletion api
	function callDeleteAHostApi(hostid)
	{
		var jasonData = {
			hostId: hostid
		};
		
	    $.ajax({
	        type: 'POST',
	        url: '/nera/inventory/api/delete-host',
	        data: jasonData,
	        success: function (data) {
	            var search = "";
	            loadhostlisting(search);
	            loadgrouplisting(search);
				loadIpAddressAll();
	            $('#modHostConfirmation').modal('hide');
	            if (data) {
					Swal.fire({
						text:  'Delete Host Successful',
						type:  'success',
						timer: 2000,
						showConfirmButton: false
					});
					datatable.load();
				} else {
					Swal.fire({
						text:  'Delete Host Fail',
						type:  'error',
						timer: 2000,
						showConfirmButton: false
					});
				}

	        },
	        error: function (err) {
	            //
	        },
	    });
	}
	
	// Search host listing
	$('#hostSearchKey').keyup(function(){
		var search = $('#hostSearchKey').val();
		loadhostlisting(search);
	});
	
	
	
    //// Group area
	// Load group listing
	function loadgrouplisting(search){
		var jasonData = {
			// inventoryId: $('#inInventoryId').val(),
			searchString: search,
		};
		
	    $.ajax({
	        type: 'GET',
	        url: '/nera/inventory/api/search-group-host',
	        data: jasonData,
	        beforeSend: function () {
	            //
	        },
	        success: function (data) {
	        	var re = data;
	        	$('#tblGroupListing > tbody').html('');
	        	for(var i = 0; i < re.length; i++)
	    		{
	        		var no = i + 1;
		        	var id = re[i].id;
		        	var name = re[i].name;
		        	var numberofhost = re[i].hosts.length;
		        	var status = re[i].active == true ? "Active" : "Inactive";
		        	generateGroupTable(no, id, name, numberofhost, status);
	    		}
	        	var textGroup = 'Group';
	        	if(hasViewGroupHostPermission) {
					textGroup = textGroup + ' ('+ re.length +')';
				}
	        	$('.setNumGroup').text(textGroup);
	        },
	        error: function (err) {
	            //
	        },
	    });
	}
	
	// Common generate host table
	function generateGroupTable(no, id, name, numberofhost, status) {
		var rowGroup = '<tr class="">'
			+ '<td class="assignindex">' + no + '</td>'
			+ '<td class="tdGroupName" data-id="' + id + '">' + name + '</td>'
			+ '<td class="tdNumberOfHost">' + numberofhost + '</td>'
			+ '<td class="tdStatus">' + status + '</td>'
			+ '<td>';
		if (hasEditGroupHostPermission) {
			rowGroup = rowGroup + '<span><a data-id="' + id + '" href="#" class="btn btn-hover-brand btn-icon btn-pill groupEditRow"><i class="la la-pencil"></i></a></span>';
		}
		if (hasDeleteGroupHostPermission) {
			rowGroup = rowGroup + '<span><a data-id="' + id + '" href="#" class="btn btn-hover-danger btn-icon btn-pill groupDeleteRow"><i class="la la-trash"></i></a></span>';
		}
		$('#tblGroupListing > tbody:last').append(rowGroup + '</td>' + '</tr>');
	    
	    loadGroupDetail();
	}
	
	// Create a new group
	$('#btnAddGroup').on('click', function(){
		$('#groupId').val("");
		$('#groupName').val("");
		addHideClassGroup();
		loadIpAddressAll();
		//loadIpAddressAll();
		$('#tblTempSelectedIp > tbody').html('');
		$('#modGroupConfirmation').modal('show');
	});
	
	// Common load all ip address
	function loadIpAddressAll()
	{
		var jasonData = {
			// inventoryId: $('#inInventoryId').val(),
			searchString: "",
		};
		
	    $.ajax({
	        type: 'GET',
	        url: '/nera/inventory/api/search-host',
	        data: jasonData,
	        success: function (data) {
	        	$('#tblIpAddress > tbody').html('');
	        	var re = data;
	        	for(var i = 0; i < re.length; i++)
	    		{
	        		generateIpAddressTable(re[i].id, re[i].name, re[i].ipAddress);
	    		}
	        },
	    });
	}
	
	function generateIpAddressTable(id, name, ip) {
	    $('#tblIpAddress > tbody:last').append('<tr class="">'
	        + '<td><input class="case" type="checkbox" name="" value="' + id + '"></td>'
	        + '<td><span class="tdSpName">' + name + '</span>' + ' - ' + '<span class="tdSpIp">' + ip + '</span></td>'
	        + '</tr>');
	}
	
	// Add selected ip
	$('#btnAddSelectedIp').on('click', function(){
		var list = getselectediplist();
		addHideClassGroup();
		if(list.length > 0){
			$('.tblTemp').removeClass('hideclass');
			$('#tblTempSelectedIp > tbody').html('');
	        for (var i = 0; i < list.length; i++) {
	        	var no = i +1;
	        	displayselectedip(no, list[i].id, list[i].name, list[i].ip);
	        }
		}
		else{
			$('#tblTempSelectedIp > tbody').html('');
		}
        $('#modHostList').modal('hide');
	});

	function addHideClassGroup() {
		$('.valigroupname').addClass('hideclass');
		$('.valihostgroup').addClass('hideclass');
	}

	function addHideClassHost() {
		$('.valiportvalid').addClass('hideclass');
		$('.valiipaddressvalid').addClass('hideclass');
		$('.valiipaddress').addClass('hideclass');
		$('.valihostname').addClass('hideclass');
		$('.validateusername').addClass('hideclass');
		$('.validatepassword').addClass('hideclass');
	}

	// Get selected ip list
	function getselectediplist() {
	    var array = [];
	    $('#tblIpAddress .case:checked').each(function () {
	        var ja = {
	            id: $(this).val(),
	            name: $(this).closest('tr').find('.tdSpName').text().trim(),
	            ip: $(this).closest('tr').find('.tdSpIp').text().trim(),
	        };
	        array.push(ja);
	    });
	    return array;
	}
	
	// Display selected ip table
	function displayselectedip(no, id, name, ip){
	    $('#tblTempSelectedIp > tbody:last').append('<tr>'
	    		+ '<td>' + no + '</td>'
		        + '<td class="selectedip" data-id="' + id + '">' + name + '</td>'
		        + '<td>' + ip + '</td>'
		        + '</tr>');
	}
	
	// Loading a group detail in the table row
	function loadGroupDetail(){
		$('#tblGroupListing .groupEditRow').on('click', function(){
			var groupid = $(this).attr('data-id');
			addHideClassGroup();
			var jasonData = {
				//inventoryid: $('#inInventoryId').val(),
				groupHostId: groupid,
			};
			
		    $.ajax({
		        type: 'GET',
		        url: '/nera/inventory/api/get-group-host-by-id',
		        data: jasonData,
		        beforeSend: function () {
		            //
		        },
		        success: function (data) {
		        	var re = data;
		        	$('#groupId').val(re.id);
		        	$('#groupName').val(re.name);
		        	
		        	// loadIpAddressAll();
                    $('.tblTemp').removeClass('hideclass');
                    $('#tblTempSelectedIp > tbody').html('');
					if(re.hosts.length > 0){

		        		var checkedlist = [];
		    	        for (var i = 0; i < re.hosts.length; i++) {
		    	        	var no = i +1;
		    	        	checkedlist.push(re.hosts[i].id);
		    	        	displayselectedip(no, re.hosts[i].id, re.hosts[i].name, re.hosts[i].ipAddress);
		    	        }
		    	        
		    		    $('#tblIpAddress .case').each(function () {
		    		    	var ck = $(this).val();

		    		        if ($.inArray(parseInt(ck), checkedlist) + 1 == 0) {
		    		            $(this).prop('checked', false);
		    		        }
		    		        else{
								$(this).prop('checked', true);
		    		        }
		    		    });
		        	} else {
						loadIpAddressAll();
					}

		        	if(data.active == true){
		        		$('#groupRadioA').prop('checked', true);
		        	}
		        	else{
		        		$('#groupRadioI').prop('checked', true);
		        	}
		        	$('#modGroupConfirmation').modal('show');
		        },
		        error: function (err) {
		            //
		        },
		    });
		});
	}

	function updateAGroup(groupId) {
		addHideClassGroup();
		var jasonData = {
			groupHostId: groupId
		};
		$.ajax({
			type: 'GET',
			url: '/nera/inventory/api/get-group-host-by-id',
			data: jasonData,
			beforeSend: function () {
				//
			},
			success: function (data) {
				var re = data;
				$('#groupId').val(re.id);
				$('#groupName').val(re.name);

				// loadIpAddressAll();
				$('.tblTemp').removeClass('hideclass');
				$('#tblTempSelectedIp > tbody').html('');
				if(re.hosts.length > 0){

					var checkedlist = [];
					for (var i = 0; i < re.hosts.length; i++) {
						var no = i +1;
						checkedlist.push(re.hosts[i].id);
						displayselectedip(no, re.hosts[i].id, re.hosts[i].name, re.hosts[i].ipAddress);
					}

					$('#tblIpAddress .case').each(function () {
						var ck = $(this).val();

						if ($.inArray(parseInt(ck), checkedlist) + 1 == 0) {
							$(this).prop('checked', false);
						}
						else{
							$(this).prop('checked', true);
						}
					});
				} else {
					loadIpAddressAll();
				}

				if(data.active == true){
					$('#groupRadioA').prop('checked', true);
				}
				else{
					$('#groupRadioI').prop('checked', true);
				}
				$('#modGroupConfirmation').modal('show');
			},
			error: function (err) {
				//
			},
		});
	}
	
	// Delete a group in the table row
	function deleteAGroup(groupId){
		$('#groupDeleteId').val(groupId);
		$('#modGroupDeleteConfirmation').modal('show');
	}
	
	$('#btnYesDeleteGroup').on('click', function(){
		var groupid = $('#groupDeleteId').val();
		callDeleteAGroupApi(groupid);
		$('#modGroupDeleteConfirmation').modal('hide');
	});
	
	function callDeleteAGroupApi(groupid){
		var jasonData = {
			groupHostId: groupid
		};
			
	    $.ajax({
	        type: 'POST',
	        url: '/nera/inventory/api/delete-group-host',
	        data: jasonData,
	        beforeSend: function () {
	            //
	        },
	        success: function (data) {
	            var search = "";
	            loadgrouplisting(search);
				datatableGroup.load();
				Swal.fire({
					text:  'Delete Group Host Successful',
					type:  'success',
					timer: 2000,
					showConfirmButton: false
				});
	        },
	        error: function (err) {
	            //
	        },
	    });
	}
	
	//
	$('.caseall').click(function (event) {
	    if (this.checked) {
	        $('.case').prop('checked', true);
	    }
	    else {
	        $('.case').prop('checked', false);
	    }
	});

	$('.case').click(function (e) {
	    if ($('.case').length == $('.case:checked').length) {
	        $('.caseall').prop('checked', true);
	    }
	    else {
	        $('.caseall').prop('checked', false);
	    }
	});

	// Click Create or Update button
	$('#btnGroupSave').on('click', function(){
		var groupname = $('#groupName').val();
		if(groupname == ""){
			$('.valigroupname').removeClass('hideclass');
			return false;
		}
		else{
			$('.valigroupname').addClass('hideclass');
		}
		
	    var array = [];
	    $('#tblTempSelectedIp .selectedip').each(function () {
	        var id = $(this).attr('data-id');
	        array.push(id);
	    });
	    
		if(array.length < 1){
			$('.valihostgroup').removeClass('hideclass');
			return false;
		}
		else{
			$('.valihostgroup').addClass('hideclass');
		}
		
		callCreateUpdateGroup(array);
	});
	
	function callCreateUpdateGroup(array)
	{
		var grouid = $('#groupId').val();
		var groupname = $('#groupName').val();
		var status = $('input[name=groupradio]:checked').val();

		var jasonData = {
			id: grouid,
			name: groupname,
			active: status,
			hostsId: array
		};
		
		var url = '';
		if(grouid > 0){
			url = '/nera/inventory/api/update-group-host';
		}
		else{
			url = '/nera/inventory/api/add-group-host';
		}
		
	    $.ajax({
	        type: 'POST',
	        url: url,
	        data: JSON.stringify(jasonData),
	        contentType: "application/json",
	        success: function (data) {
				if (data.code != '200') {
					return false;
				}
	            var search = "";
	            loadgrouplisting(search);
	            $('#modGroupConfirmation').modal('hide');
				Swal.fire({
					text:  data.mess,
					type:  'success',
					timer: 2000,
					showConfirmButton: false
				});
				datatableGroup.load();
	        },
	        error: function (err) {
	            let messageError = '';
	        	if(err.status === 400 || err.status === 500){
					messageError = err.responseJSON.detail;
	    		} else{
					messageError = 'Internal Server Error';
	    		}
				Swal.fire({
					text:  messageError,
					type:  'error',
					timer: 2000,
					showConfirmButton: false
				});
	        }
	    });
	}
	
	// Search group listing
	$('#groupSearchKey').keyup(function(){
		var search = $('#groupSearchKey').val();
		loadgrouplisting(search);
	});

	// Import file
	$("#ImportFile").change(function (event) {
		event.preventDefault();
		if ($(this)[0].files.length == 0 && $(this)[0].files[0].size <= 1048576) return;
		var files = $(this)[0].files;

		if (checkExtensionFile(files[0].name)) {
			$("#ImportFile").val("");
			return;
		}
		var fileData = new FormData();
		for (let i = 0; i < files.length; i++) {
			fileData.append('file', files[i]);
		}
		$.ajax({
			type:'POST',
			enctype: 'multipart/form-data',
			url:'/nera/inventory/api/upload-file',
			data: fileData,
			processData: false,
			contentType: false,
			cache: false,
			timeout: 600000,
			success:function(jsonData)
			{
				if (!Array.isArray(jsonData.listReport)) {
					return false;
				}
				loadhostlisting("");
				loadgrouplisting("");
                // jsonDataReport = jsonData;
				generateTableReport(jsonData.listReport);
				$('#modImportReport').modal('show');
				$('#modImportReport').modal('handleUpdate');
				var success = jsonData.totalSuccess;
				var fail = jsonData.totalFail;
				var total = jsonData.listReport.length;

				datatable.load();
				if (typeof datatableGroup !== 'undefined') {
					datatableGroup.load();
				}
				// load all host and group
				loadhostlisting("");
				loadgrouplisting("");
				// load all ip address
				loadIpAddressAll();
				Swal.fire({
					text:  'Import Successful',
					type:  'success',
					timer: 1000,
					showConfirmButton: false
				});
				$("#importReportTotal").empty();
				$("#importReportTotal").append('Success: ' + success +' /Fail: '+fail+' /Total: '+total);
				$("#ImportFile").val('');
			},
            error: function (err) {
				let messageError = '';
                $('#error-action-import').empty();
				if(err.status === 500){
					messageError = 'Import Error';
				} else if(err.status === 405){
					messageError = 'Import File Wrong Format';
				}
				Swal.fire({
					text:  messageError,
					type:  'error',
					timer: 2000,
					showConfirmButton: false
				});
            }
		});
	});
	function generateTableReport(jsonData) {
		reportTable = $('#kt_datatable_report').KTDatatable({
			// datasource definition
			data: {
				type: 'local',
				source: {
					read: {
						map: function(raw) {
							return jsonData;
						}
					}
				},
				pageSize: 10,
				saveState: {
					cookie: false,
					webstorage: false
				},
				serverPaging: false,
				serverFiltering: false,
				serverSorting: false
			},

			// layout definition
			layout: {
				scroll: false,
				footer: false
			},

			selector: {
				class: 'kt-checkbox--solid'
			},

			// column sorting
			sortable: true,
			pagination: true,

			search: {
				input: $('#hostSearchKey')
			},

			// columns definition
			columns: [
				{
					field: 'hostName',
					title: 'Host Name',
					textAlign: 'left'
				},
				{
					field: 'groupName',
					title: 'Group Name',
					width: 100
				},
				{
					field: 'ipAddress',
					title: 'IP Address',
					width: 100
				},
				{
					field: 'port',
					title: 'Port',
					width: 50,
					textAlign: 'center'
				},
				{
					field: 'importStatus',
					title: 'Import Status'
				}]
		});
	}

	$('#btnDismissReport').on('click', function(){
		reportTable.destroy();
		$('#modImportReport').modal('hide');
	});

	function checkExtensionFile(fileName) {
		if (fileName.substr(fileName.lastIndexOf('.') +1).toLowerCase() !== 'csv') {
			Swal.fire({
				text:  'Wrong type file. Please import file csv.',
				type:  'error',
				timer: 2200,
				showConfirmButton: false
			});
			return true;
		}
		return false;
	}

	checkPermission();
	function checkPermission()
	{
		if(!hasViewHostPermission) {
			$("#tblHostListing").empty();
		}
		if(!hasViewGroupHostPermission) {
			$("#tblGroupListing").empty();
		}
	}
});
