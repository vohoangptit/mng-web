var checkMessageStop = false;
var startDate;
var resultPlaybook = [];
function  getPlaybookOutput(id) {
    var list = [];
    $.ajax({
        async:false,
        type: 'GET',
        url: '/nera/playbook/api/get-by-id',
        data: {'id':id},
        contentType: "application/json",
        success: function (data) {
            $.each(data.playbookOutput,function () {
                var variableName = data.name+'.'+this.variable;
                list.push(variableName);
            })
        }
    });
    return list;
}
function  submitComparePatt(conditions,playbook,mapSwitchcase,playbookOut) {
    $.each(conditions,function () {
        console.log(this);
        var item = {};
        item['id'] = this.playbook;
        item['cell'] = this.nextCell;
        var paramkey = this.paramvalue;
        function getParameters(sParam) {
            var sPageURL = window.location.search.substring(1),
                sURLVariables = sPageURL.split('&'),
                sParameterName,
                i;

            for (i = 0; i < sURLVariables.length; i++) {
                sParameterName = sURLVariables[i].split('=');

                if (sParameterName[0] === sParam) {
                    return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
                }
            }
        }
        var wfId = getParameters('workflowid');
        $.each(playbookOut,function (i,vl) {
            if(paramkey === String(vl.key))
            {
                paramkey = vl.val;
            }
        });
        if(playbook === this.playbook)
        {


            $.ajax({
                async: false,
                type: 'POST',
                url: '/nera/my-job/api/get-compare-result',
                data: {
                    'src':paramkey,
                    'target':this.value,
                    'key':this.conditionvalue,
                    'workflowId':wfId
                },
                success: function (data) {

                    item['value'] = data;
                    mapSwitchcase.push(item);

                }
            });


        }
        else
        {
            console.log(this.paramkey+" "+this.value+" "+this.conditionvalue);
            $.ajax({
                async: false,
                type: 'POST',
                url: '/nera/my-job/api/get-compare-result',
                data: {'src':paramkey,'target':this.value,'key':this.conditionvalue,'workflowId':wfId},
                success: function (data) {
                    // console.log(data);

                    item['value'] = data;
                    mapSwitchcase.push(item);

                }
            });

        }

        playbook = this.playbook;

    });
    return mapSwitchcase;

}
function downloadFileLog(name) {
    $.ajax(
        {
            url: '/nera/upload/downloadFile/' + name,
            type: 'GET',
            xhrFields: {
                responseType: 'blob'
            },
            success: function (data) {
                var a = document.createElement('a');
                var url = window.URL.createObjectURL(data);
                a.href = url;
                a.download = name;
                a.click();
                window.URL.revokeObjectURL(url);
            },
            error: function (response) {
                // console.log(response);
            }
        });
}
function getFullFlow(mappingList ,key,result)
{
    var map = getNextOps(mappingList,key);

    if(map.length >0)
    {
        $.each(map,function (run,value) {
            result.push(value);
            // recursion mapping
            getFullFlow(mappingList,value,result);

        });
        return result;

    }
    else
        return

}
function getCurrentDatetime() {
    var result =[];
    var months = ["01", "02", "03","04", "05", "06", "07", "08", "09", "10", "11", "12"];
    var current_datetime = new Date();
    var formatted_date = current_datetime.getFullYear() + "-" + months[current_datetime.getMonth()] + "-" + current_datetime.getDate();
    var formatted_time = current_datetime.getHours()+ ':'+current_datetime.getMinutes()+':'+current_datetime.getSeconds();
    result.push(formatted_date);
    result.push(formatted_time);
    return result;
}
function getCurrentDatetimeWithFormat_mm_dd_yyyy() {
    var result =[];
    var months = ["01", "02", "03","04", "05", "06", "07", "08", "09", "10", "11", "12"];
    var current_datetime = new Date();
    var formatted_date = months[current_datetime.getMonth()] + "/" +current_datetime.getDate() +  "/" + current_datetime.getFullYear();
    var formatted_time = current_datetime.getHours()+ ':'+current_datetime.getMinutes()+':'+current_datetime.getSeconds();
    result.push(formatted_date);
    result.push(formatted_time);
    return result;
}
function combineConditions(conditions) {
    var result = conditions[0];
    $.each(conditions,function (i) {
        if(i+1 < conditions.length)
        {
            result = result && conditions[i+1];
        }
    });
    return result;
}
function getfromMap(map,id) {
    var result = [];

    $.each(map,function () {


        if(this.cell !== null && mxUtils.parseXml(this.cell.contentXML).documentElement.getAttribute('id') === id)
        {
            result.push(this.value);
        }

    });
    return result;

}
function  getSwitchCase(cells,id) {
    var list = [];
    $.each( cells, function( i, cell ) {
        var doc = mxUtils.parseXml(cell.contentXML);
        new mxCodec(doc);
        if (doc.documentElement.nodeName === 'Condition' && doc.documentElement.getAttribute('id') === id) {
            var conditionId = doc.documentElement.getAttribute('id');
            var mappingFlow = getMappingFlow(cells,'connector');
            getNextOps(mappingFlow,conditionId);
            var label = '';
            console.log(doc.documentElement.attributes);
            $.each(doc.documentElement.attributes,function (i) {
                var mapping = {};

                if(typeof this.nodeName.split("value")[1] !== 'undefined')
                {
                    if(label !== this.nodeName.split("value")[1])
                    {
                        mapping['id'] = this.nodeName.split("value")[1];
                        mapping['playbook'] =  this.nodeName.split("value")[1].split('_')[0];
                        $.ajax({
                            async:false,
                            type: 'GET',
                            url: '/nera/playbook/api/get-by-id',
                            data: {'id':mxUtils.parseXml(getOpsbyId(cells,getPreviousOps(mappingFlow,id)[0]).contentXML).documentElement.getAttribute('pbid')},
                            contentType: "application/json",
                            success: function (data1) {
                                $.each(data1.playbookOutput,function () {
                                    if(doc.documentElement.attributes['paramkey'+mapping['id']].nodeValue === this.variable)
                                    {
                                        mapping['paramkey'] = this.value;
                                    }
                                })
                            }
                        });
                        mapping['nextCell'] =  getOpsbyName(cells,doc.documentElement.attributes['conditionPlaybook'+mapping['id']].nodeValue);
                        mapping['value'] = doc.documentElement.attributes['value'+mapping['id']].nodeValue;
                        mapping['paramvalue'] = doc.documentElement.attributes['paramkey'+mapping['id']].nodeValue;
                        mapping['conditionvalue'] = doc.documentElement.attributes['conditionvalue'+mapping['id']].nodeValue;

                        list.push(mapping);
                    }

                    label = this.nodeName.split("value")[1];
                }
            });
        }
    });
    return list;
}
function getNextOps(mappingList,key)
{
    var result =[];
    // console.log('_____________________________________________________________________');
    // console.log(mappingList);
    $.each( mappingList, function( run, k ) {
        if(k.from === key)
        {
            result.push(k.to);
        }
    });
    return result;
}
function getPreviousOps(mappingList,key)
{
    var result =[];
    // console.log('_____________________________________________________________________');
    // console.log(mappingList);
    $.each( mappingList, function( run, k ) {
        if(k.to === key)
        {
            result.push(k.from);
        }
    });
    return result;
}
function getMappingFlow(cells,type)
{
    var result = [];
    $.each( cells, function( i, cell ) {
        if(cell.properties === type)
        {
            var doc = mxUtils.parseXml(cell.contentXML);
            new mxCodec(doc);
            var mapping ={};
            mapping['from'] = doc.documentElement.childNodes[0].attributes['source'].nodeValue;
            mapping['to'] = doc.documentElement.childNodes[0].attributes['target'].nodeValue;
            mapping['toname'] = mxUtils.parseXml(getOpsbyId(cells,doc.documentElement.childNodes[0].attributes['target'].nodeValue).contentXML).documentElement;
            result.push(mapping);
        }
    });
    return result;
}
function getOpsbyId (cells,id){
    var result = null;
    $.each( cells, function( i, cell ) {
        var doc = mxUtils.parseXml(cell.contentXML);
        new mxCodec(doc);
        if(doc.documentElement.getAttribute('id') === id)
        {
            result = cell;
        }
    });
    return result;
}
function getOpsbyName (cells,name){
    var result = null;
    $.each( cells, function( i, cell ) {
        var doc = mxUtils.parseXml(cell.contentXML);
        new mxCodec(doc);
        if(doc.documentElement.getAttribute('label') === name)
        {
            result = cell;
        }
    });
    return result;
}
function  appendMessges(type,args) {
    if(!checkMessageStop) {
        $('#executionlog').append(getMsgTemplate(type,args));
        $('#executionlog').append('<br>');
    }
    return getMsgTemplate(type,args);
}
function getMsgTemplate(type,args){
    var result ='';
    var templates = [];
    var listType = ['Start','Playbook Execute','Playbook Receive','Switch Case','Switch Case Finish','Approver','Approver Status','End','Result Playbook', 'Connect Failed', 'Stopped', 'Syntax Error'];
    var msgList = [];
    var start = '{0}&ensp;{1}&ensp;START';
    var playbookExecute = '{0}&ensp;{1}&ensp;Execute the play book {2}';
    var playbookReceive = '{0}&ensp;{1}&ensp;Receive the execution result of  the play book{2}';
    var switchCase = '{0}&ensp;{1}&ensp;Proceed the switch case operator {2}';
    var switchCaseFinish = '{0}&ensp;{1}&ensp;Define the next operator is {2} {3}';
    var approver = '{0}&ensp;{1}&ensp;Send mail for asking approval';
    var approverStatus = '{0}&ensp;{1}&ensp;Get approve from {2}';
    var end = '{0}&ensp;{1}&ensp;END';
    var resultplaybook = '{0}&ensp;{1}&ensp;{2}';
    var connectRabbitFailed = '{0}&ensp;{1}&ensp; Connect Rabbit Or Ansible Failed';
    var stopped = '{0}&ensp;{1}&ensp; Job Execution stopped';
    var syntaxError = '{0}&ensp;{1}&ensp; Syntax Playbook Error';
    msgList.push(start);
    msgList.push(playbookExecute);
    msgList.push(playbookReceive);
    msgList.push(switchCase);
    msgList.push(switchCaseFinish);
    msgList.push(approver);
    msgList.push(approverStatus);
    msgList.push(end);
    msgList.push(resultplaybook);
    msgList.push(connectRabbitFailed);
    msgList.push(stopped);
    msgList.push(syntaxError);
    $.each( listType, function( i, item ) {
        var format = {};
        format['formatString'] = msgList[i];
        format['type'] = item;
        templates.push(format);
    });
    $.each( templates, function( run, tmp ) {
        if(tmp.type === type)
        {
            result = tmp.formatString.format(args);
        }
    })
    return result;
}
function getfileUpload(cells,mappingList,id)
{
    var result = [];
    $.each( cells, function( i, cell ) {
        var doc = mxUtils.parseXml(cell.contentXML);
        if(doc.documentElement.getAttribute('id') === id)
        {
            // console.log(doc);
            var json = {id: doc.documentElement.getAttribute('pbid')};
            $.ajax({
                async:false,
                type: 'GET',
                url: '/nera/playbook/api/get-by-id',
                data: json,
                contentType: "application/json",
                success: function (data1) {
                    $.each(data1.playbookInput,function () {
                        if(this.type === 'FILE')
                        {
                            result.push(this.fileInfo.filename);
                        }
                    })
                }
            });

        }
    });
    return result;
}
function getfileUploadXMl(cells,mappingList,id)
{
    var result = [];
    $.each( cells, function( i, cell ) {
        var doc = mxUtils.parseXml(cell.contentXML);
        if(doc.documentElement.getAttribute('id') === id)
        {
            // console.log(doc);

            var json = {id: doc.documentElement.getAttribute('pbid')};
            $.ajax({
                async:false,
                type: 'GET',
                url: '/nera/playbook/api/get-by-id',
                data: json,
                contentType: "application/json",
                success: function (data1) {
                    result.push(data1.fileName);
                }
            });

        }
    });
    return result;
}
function mappingPlaybookInput(cells,mappingList,id){
    var result = [];
    $.each( cells, function( i, cell ) {
        var doc = mxUtils.parseXml(cell.contentXML);
        new mxCodec(doc);
        if(doc.documentElement.getAttribute('id') === id)
        {
            $('#playbookname').val(cell.properties);
            var objects = doc.documentElement.getElementsByTagName('PLaybookInputElement');
            if(objects == null || objects.length == 0) {
                objects = doc.documentElement.getElementsByTagName('playbookinputelement');
            }
            $.each(objects,function (run,value) {
                // console.log(value);
                if(value.attributes['lalelvl_'+run].nodeValue === 'Default value' || value.attributes['value_'+run].nodeValue.indexOf('.') > -1)
                {
                    var variable = value.attributes['variable'].nodeValue;
                    if(value.attributes['manualinput_'+run].nodeValue === 'false') {
                        if(value.attributes['value_'+run].nodeValue.indexOf('.') > -1) {
                            var mapping = {};
                            mapping['variable'] = value.attributes['variable'].nodeValue;
                            mapping['value'] = value.attributes['value_'+run].nodeValue;
                            result.push(mapping);
                        } else {
                            var json = {id: doc.documentElement.getAttribute('pbid')};
                            $.ajax({
                                async:false,
                                type: 'GET',
                                url: '/nera/playbook/api/get-by-id',
                                data: json,
                                contentType: "application/json",
                                success: function (data1) {

                                    $('#playbookname').val(data1.name);
                                    $.each(data1.playbookInput,function (runinput,valueinput) {
                                        if(variable === valueinput.variable)
                                        {
                                            var mappingValueInput = {};
                                            mappingValueInput['variable'] = variable;
                                            mappingValueInput['value'] = valueinput.value;
                                            result.push(mappingValueInput);
                                        }
                                    });
                                }
                            });
                        }
                    } else {
                        var mappingManualInput = {};
                        mappingManualInput['variable'] = variable;
                        mappingManualInput['value'] = value.attributes['value_'+run].nodeValue;
                        result.push(mappingManualInput);
                    }
                }
                else
                {
                    var pbid = getPreviousOps(mappingList,doc.documentElement.getAttribute('id'));
                    var json = {id: mxUtils.parseXml(getOpsbyId(cells,pbid[0]).contentXML).documentElement.getAttribute('pbid')};
                    if(pbid.length>0)
                    {
                        $.ajax({
                            async:false,
                            type: 'GET',
                            url: '/nera/playbook/api/get-by-id',
                            data: json,
                            contentType: "application/json",
                            success: function (data1) {
                                $('#playbookname').val(data1.name);
                                $.each(data1.playbookOutput,function (runinput,valueoutput) {

                                    if(value.attributes['value_'+run].nodeValue.split('.')[1] === valueoutput.variable)
                                    {
                                        var mapping = {};
                                        mapping['variable'] = value.attributes['variable'].nodeValue ;
                                        mapping['value'] = valueoutput.value;
                                        result.push(mapping);
                                    }
                                });
                            }
                        });
                    }
                    else
                    {
                        var variable = value.attributes['variable'].nodeValue;
                        var mapping = {};
                        mapping['variable'] = variable;
                        mapping['value'] = value.attributes['lalelvl_'+run].nodeValue;
                        result.push(mapping);
                    }

                }
            });
        }
    });
    return result;
}
String.prototype.format = function (args) {
    var str = this;
    return str.replace(String.prototype.format.regex, function(item) {
        var intVal = parseInt(item.substring(1, item.length - 1));
        var replace;
        if (intVal >= 0) {
            replace = args[intVal];
        } else if (intVal === -1) {
            replace = "{";
        } else if (intVal === -2) {
            replace = "}";
        } else {
            replace = "";
        }
        return replace;
    });
};
String.prototype.format.regex = new RegExp("{-?[0-9]+}", "g");
$(document).ready(function () {

    var getUrlParameter = function getUrlParameter(sParam) {
        var sPageURL = window.location.search.substring(1),
            sURLVariables = sPageURL.split('&'),
            sParameterName,
            i;

        for (i = 0; i < sURLVariables.length; i++) {
            sParameterName = sURLVariables[i].split('=');

            if (sParameterName[0] === sParam) {
                return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
            }
        }
    };
    var mỵjobid = getUrlParameter('jobid');
    var workflowid = getUrlParameter('workflowid');
    $('#log-div').hide();
    $('#action-div-log').hide();

    Cookies.set('jobid',mỵjobid);

    Cookies.set('workflowid',workflowid);
    $.ajax({
        type: 'GET',
        url: '/nera/job-planning/api/get-job-by-id?id='+mỵjobid,
        success: function (data) {
            $('#jobid').val(data.jobId);
            Cookies.set('jobasign',data.id);
            $('#DateExec').text(data.executionDate);
            $('#TimeExec').text(data.startTime + ' - ' + data.endTime);
            $('#descriptionJob').text(data.jobDescription);
            $('#workflowName').text(data.workflowName);
            $('#jobAssignStatus').text(data.status);
            $('#plannerName').text(data.plannerName);
            $('#assigneeName').text(data.assigneeName);
            $('#jobNameLbl').text(data.jobName);
            $('#jobname').val(data.jobName);
            var dateExeFormat = data.executionDate.split('/')[2]+'-'+data.executionDate.split('/')[1]+'-'+data.executionDate.split('/')[0];
            $('#startExeDate').val(dateExeFormat + " " + data.startTime + ":00");
            $('#endExeDate').val(dateExeFormat + " " + data.endTime + ":00");
        },
    });
    // $('#btnCancel2Job').on('click', function() {
    //     window.location.href = '/menu/my-job/listing';
    // });
    $('#btnCancel1Job').on('click', function() {
        window.location.href = '/menu/my-job/listing';
    });
    var mapOutput = [];
    $('#btnExeJob').unbind().on('click', function(e){
        var dateNow = new Date();
        mapOutput = [];
        var months = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
        var todayExe = dateNow.getFullYear()+'-'+months[dateNow.getMonth()]+'-'+("0" + dateNow.getDate()).slice(-2)+' '+("0" + dateNow.getHours()).slice(-2)+':'+("0" + dateNow.getMinutes()).slice(-2)+':00';
        if(todayExe >= $('#endExeDate').val() || todayExe <= $('#startExeDate').val())
        {
            var textHover = 'You can only start to execute the job at ' + $('#startExeDate').val() + ' - ' + $('#endExeDate').val();
            Swal.fire({
                text:  textHover,
                type:  'error',
                timer: 5000,
                showConfirmButton: false
            });
            return;
        }
        $('#executionlog').empty();
        checkMessageStop = false;
        $.ajax({
            type:'POST',
            url:'/nera/my-job/api/delete-job-exe',
            data:{'jobId':$('#jobid').val()},
            success: function (data) {
            }
        });
        startDate = getCurrentDatetime()[0] +" "+getCurrentDatetime()[1];
        $('#log-div').show();
        $('#action-div-log').show();
        $('.btn').prop('disabled', true);
        $('#btnCancel2Job').prop('disabled', false);
        var keyMap = [];
        var dictItem1 = {};
        dictItem1['condition'] = 1;
        keyMap.push(dictItem1);
        var dictItem2 = {};
        dictItem2['playbook'] = 2;
        keyMap.push(dictItem2);
        var dictItem3 = {};
        dictItem3['approval'] = 3;
        keyMap.push(dictItem3)
        var dictItem4 = {};
        dictItem4['userobject'] = 4;
        keyMap.push((dictItem4));
        //get workflow api to get workflow info
        var json = {'id':parseInt(workflowid)};
        $.ajax({
            type: 'POST',
            url: '/nera/api/load-workflow',
            data: json,
            success: function (dataWorkflow) {

                $.ajax({
                    type:'POST',
                    async:false,
                    url:'/nera/my-job/api/waiting-approve-update',
                    data: {
                        id: Cookies.get('jobasign')},
                    success: function (result) {
                        console.log(result);
                    }
                });
                var  cells = dataWorkflow.workflowOperator;
                new mxCodec();
                var mappingFlow = getMappingFlow(cells,'connector');


                console.log(mappingFlow);
                // console.log(getOpsbyId(cells,'9'));

                // exe fullflow at once
                var ops = getFullFlow(mappingFlow,'star',['star']);

                var isRun = [];
                $.each(ops,function () {
                    isRun.push(true);
                })

                $.ajax({
                    async: false,
                    type: 'GET',
                    url: '/nera/job-planning/api/get-job-pay-load?id=1',
                    success: function (jobpayload) {

                    }
                });

                var run =0;
                var isFinished=false;
                var timer = 2000;
                var isBreak = false;
                function runningWorkFlowOps(run,isFinished,timer,isBreak,ops,isRun,cells,mappingFlow,dataWorkflow,outMap) {

                    $('#btnCancel2Job').on('click', function() {
                        // console.log('stoped');
                        // console.log('stoped'+isRun[run+1]);
                        checkMessageStop = true;
                        var saveExe = {};
                        saveExe['result']  = 'complete';
                        saveExe['executeStart'] = startDate;
                        saveExe['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                        saveExe['jobId'] = $('#jobid').val();
                        saveExe['id'] = $('#jobexeid').val();
                        saveExe['result'] = 'stopped';
                        saveExe['log'] = msg;
                        var jobOperatorExecutions = [];
                        var jobOperatorExecution = {};
                        jobOperatorExecution['result'] = 'complete';
                        jobOperatorExecution['type'] = keyMap[typeOp.toLowerCase()];
                        jobOperatorExecution['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                        jobOperatorExecution['executeStart'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                        jobOperatorExecutions.push(jobOperatorExecution);
                        saveExe['jobOperatorExecutions']  = jobOperatorExecutions;
                        var saveExeJson = JSON.stringify(saveExe);
                        var jobAssignId = getUrlParameter('jobid');
                        $.ajax({
                            type:'POST',
                            url:'/nera/my-job/api/update-status-finish-job',
                            data:{'id':jobAssignId,'status':'Finished (Stopped)'},
                            success: function (data) {
                                if($('#executionlog').text().indexOf('Job Execution stopped') < 0) {
                                    var args = getCurrentDatetime();
                                    $('#executionlog').append(getMsgTemplate('Stopped',args));
                                    $('#executionlog').append('<br>');
                                    var dt = new Date();
                                    var time = (dt.getMonth()+1)+'_'+dt.getDate()+'_'+dt.getFullYear()+'_'+ dt.getHours()+'_'+ dt.getMinutes()+'_'+ dt.getSeconds()+'_'+dt.getMilliseconds();
                                    var name = "execute_log_"+time+'.txt';
                                    var jobAssignId = getUrlParameter('jobid');
                                    $.ajax({
                                        // async: false,
                                        type: 'POST',
                                        url: '/nera/my-job/api/send-mail-log-execute',
                                        data: {'contentFile':$('#executionlog').html(),'fileName':name, 'jobAssignId':jobAssignId},
                                        success: function (data) {
                                        }
                                    });
                                }
                                isRun[run+1] = false;
                                saveData(saveExeJson,callbackAjax,ops[run],[]);
                            }
                        });

                        $('#btnCancel2Job').prop('disabled', true);
                        $('#btnExeJob').prop('disabled', false);
                        return false;
                    });
                    document.onkeydown = function()
                    {
                        if(event.keyCode==116) {
                            Swal.fire({
                                title: 'System Error',
                                text: "The system don't allow Roll back/ canceling when the job executing",
                                type: 'error',
                                timer: 2000,
                                showConfirmButton: false
                            });
                            event.keyCode=0;
                            event.returnValue = false;
                        }
                    };

                    //to avoid refresh, using context menu of the browser

                    document.oncontextmenu = function() {
                        Swal.fire({
                            title: 'System Error',
                            text: "The system don't allow Roll back/ canceling when the job executing",
                            type: 'error',
                            timer: 2000,
                            showConfirmButton: false
                        });
                        event.returnValue = false;
                    };
                    window.onbeforeunload = function(event) {
                        Swal.fire({
                            title: 'System Error',
                            text: "The system don't allow Roll back/ canceling when the job executing",
                            type: 'error',
                            timer: 2000,
                            showConfirmButton: false
                        });
                        event.cancelable = true;
                        event.returnValue = false;
                    };
                    // $.each(ops,function (opi,op) {
                    // run current op and get next op


                    if(isRun[run]&& !isBreak)
                    {
                        var opxml = mxUtils.parseXml(getOpsbyId(cells,ops[run]).contentXML).documentElement;
                        var typeOp = opxml.nodeName;
                        // save to database when each operator finished
                        console.log("_________________"+typeOp);
                        function  saveData(saveExeJson,callbackAjax,run,mapOut) {


                            $.ajax({
                                async:true,
                                type:'POST',
                                url:'/nera/my-job/api/save-job-exe',
                                contentType: "application/json",
                                data:saveExeJson,
                                success: function (data) {
                                    callbackAjax(data,run,mapOut);
                                },
                                error: function(request,error) {
                                    console.log('An error occurred attempting to get new e-number');
                                    // console.log(request, error);
                                }

                            });

                        }
                        // if(run ===0)
                        // {
                        //     saveExe['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                        //     saveExe['jobId'] = $('#jobid').val();
                        //     saveExe['result'] = 'complete';
                        //     saveExe['log'] = 'execution success';
                        //     var jobOperatorExecutions = [];
                        //
                        //     jobOperatorExecution['result'] = 'complete';
                        //     jobOperatorExecution['type'] = keyMap[typeOp.toLowerCase()];
                        //     jobOperatorExecution['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                        //     jobOperatorExecutions.push(jobOperatorExecution);
                        //     saveExe['jobOperatorExecutions']  = jobOperatorExecutions;
                        //     var saveExeJson = JSON.stringify(saveExe);
                        //     $.ajax({
                        //         async:false,
                        //         type:'POST',
                        //         url:'/nera/my-job/api/save-job-exe',
                        //         contentType: "application/json",
                        //         data:saveExeJson,
                        //         success: function (data) {
                        //             $('#jobexeid').val(data.id);
                        //         }
                        //
                        //     });
                        //
                        // }



                        if(typeOp === 'Condition')
                        {

                            var args = getCurrentDatetime();
                            args.push(opxml.getAttribute('label'));
                            appendMessges('Switch Case',args);

                            var conditions = getSwitchCase(cells,ops[run]);

                            conditions.sort(function(a, b) {
                                return parseFloat(b.playbook) - parseFloat(a.playbook);
                            });
                            var playbook = conditions[0].playbook;

                            //Phuong
                            var mapSwitchcase = [];
                            mapSwitchcase = submitComparePatt(conditions,playbook,mapSwitchcase,outMap);
                            var nextofSwitchcase = (getNextOps(mappingFlow,ops[run]));
                            console.log(nextofSwitchcase);
                            $.each(nextofSwitchcase,function (i,vl) {
                                var l = getfromMap(mapSwitchcase,vl);
                                var result = combineConditions(l);
                                // isRun[ops.indexOf(vl)] = result;
                                if(result === false)
                                {
                                    isRun.splice(ops.indexOf(vl),1);
                                    ops.splice(ops.indexOf(vl),1);
                                }


                                if(result)
                                {
                                    var args = getCurrentDatetime();
                                    var msg = args.push(mxUtils.parseXml(getOpsbyId(cells,vl).contentXML).documentElement.getAttribute('label'));
                                    args.push(" ");
                                    appendMessges('Switch Case Finish',args);
                                    var saveExe = {};
                                    saveExe['result']  = 'complete';
                                    saveExe['executeStart'] = startDate;
                                    saveExe['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                    saveExe['jobId'] = $('#jobid').val();
                                    saveExe['id'] = $('#jobexeid').val();
                                    saveExe['result'] = 'complete';
                                    saveExe['log'] = msg;
                                    var jobOperatorExecutions = [];
                                    var jobOperatorExecution = {};
                                    jobOperatorExecution['result'] = 'complete';
                                    jobOperatorExecution['type'] = keyMap[typeOp.toLowerCase()];
                                    jobOperatorExecution['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                    jobOperatorExecution['executeStart'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                    jobOperatorExecutions.push(jobOperatorExecution);
                                    saveExe['jobOperatorExecutions']  = jobOperatorExecutions;
                                    var saveExeJson = JSON.stringify(saveExe);
                                    saveData(saveExeJson,callbackAjax,ops.indexOf(vl),[]);
                                }


                            })
                        }
                        if(typeOp === 'Playbook' || typeOp === 'playbook')
                        {
                            var varObj = mappingPlaybookInput(cells,mappingFlow,ops[run]);
                            var args = getCurrentDatetime();
                            args.push($('#playbookname').val());
                            appendMessges('Playbook Execute',args);
                            var test = {};
                            test['role'] = 'juniper';
                            var listHost = [];
                            var listUSer = [];
                            var listPass = [];
                            var listPort = [];
                            var listAddr = [];
                            var listVar = [];
                            var listVal = [];
                            $.each(varObj,function () {
                                listVar.push(this.variable);
                                listVal.push(this.value);
                            });
                            var listFile = getfileUpload(cells,mappingFlow,ops[run]);
                            var listYml = getfileUploadXMl(cells,mappingFlow,ops[run]);


                            $.ajax({
                                async: false,
                                method: 'GET',
                                url: '/nera/job/api/get-by-id',
                                data: {id:  $('#jobid').val(),searchString:''},
                                success:function (dataJobMangament) {
                                    //
                                    console.log(dataJobMangament);
                                    $.each(dataJobMangament.listHost,function () {
                                        listHost.push(this.name);
                                        listUSer.push(this.username);
                                        listPass.push(this.password);
                                        listPort.push(this.port);
                                        listAddr.push(this.ipAddress);
                                    });

                                    console.log(dataJobMangament.listPayload);
                                    var dt = new Date();
                                    var time = (dt.getMonth()+1)+'_'+dt.getDate()+'_'+dt.getFullYear()+'_'+ dt.getHours()+'_'+ dt.getMinutes()+'_'+ dt.getSeconds()+'_'+dt.getMilliseconds();
                                    var foldername = "playbook"+time;
                                    test['title'] = " Get Device Facts";
                                    test['hostname'] = listHost;
                                    test['user'] = listUSer;
                                    test['pass'] = listPass;
                                    test['port'] = listPort;
                                    test['addressIps'] = listAddr;
                                    test['yml'] = listYml;
                                    test['files'] = listFile;
                                    test['variable'] = listVar;
                                    test['value'] = listVal;
                                    test['foldername'] =foldername;
                                    test['jobName'] = $('#jobNameLbl').text();
                                    test['jobPayloads'] = dataJobMangament.listPayload;
                                    if(resultPlaybook.length > 0) {
                                        test['resultPlaybooks'] = resultPlaybook;
                                    } else {
                                        test['resultPlaybooks'] = [];
                                    }

                                    var param = JSON.stringify(test);



                                    function  createPlaybook(param,runPlayBook,opxml,getPlaybookResult) {

                                        $.ajax({
                                            type:'POST',
                                            url:'/nera/my-job/api/create-playbook-exe-folder',
                                            contentType: "application/json",
                                            data:param,
                                            success:function (folderresult) {
                                                runPlayBook(folderresult,getPlaybookResult,opxml);
                                            },
                                        });
                                    }
                                    function  getPlaybookResult(msg,opxml,result) {

                                        var m ;
                                        var listOutput = getPlaybookOutput(opxml.getAttribute('pbid'));
                                        if(result !== '') {
                                            $.each(listOutput,function () {
                                                var variableOutput = this.split('.')[1];
                                                var re = new RegExp('\s*"'+variableOutput+' = ([^"]+)"', "g");

                                                m = re.exec(msg);
                                                if (m) {
                                                    var rex = {};
                                                    rex['key'] = this;
                                                    rex['val'] = m[1];
                                                    mapOutput.push(rex);
                                                }
                                            });
                                        }

                                        console.log(mapOutput);
                                        if(mapOutput.length >0)
                                        {
                                            var args = getCurrentDatetime();
                                            var messge = args.push($('#playbookname').val());
                                            var saveExe = {};
                                            saveExe['result']  = 'complete';
                                            saveExe['executeStart'] = startDate;
                                            saveExe['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                            saveExe['jobId'] = $('#jobid').val();
                                            saveExe['id'] = $('#jobexeid').val();
                                            var resultExecutePlaybook = result.replace('Result execute :','').replace('<br/>','');
                                            saveExe['result'] = resultExecutePlaybook;
                                            saveExe['log'] = msg;
                                            var jobOperatorExecutions = [];
                                            var jobOperatorExecution = {};
                                            jobOperatorExecution['result'] = 'complete';
                                            jobOperatorExecution['type'] = keyMap[typeOp.toLowerCase()];
                                            jobOperatorExecution['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                            jobOperatorExecution['executeStart'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                            jobOperatorExecutions.push(jobOperatorExecution);
                                            saveExe['jobOperatorExecutions']  = jobOperatorExecutions;
                                            var saveExeJson = JSON.stringify(saveExe);
                                            if(resultExecutePlaybook.indexOf('Ok') > 0) {
                                                appendMessges('Playbook Receive',args);
                                                saveData(saveExeJson,callbackAjax,run+1,mapOutput);
                                            } else {
                                                var jobAssignIdParam = getUrlParameter('jobid');
                                                $.ajax({
                                                    type:'POST',
                                                    url:'/nera/my-job/api/update-status-finish-job',
                                                    data:{'id':jobAssignIdParam,'status':'Finished (Failed)'},
                                                    success: function (data) {
                                                        saveData(saveExeJson,callbackAjax,-1,mapOutput);
                                                    }
                                                });
                                            }
                                        }
                                        else
                                        {
                                            var args = getCurrentDatetime();
                                            var messge = args.push($('#playbookname').val());

                                            var saveExe = {};
                                            saveExe['id'] = $('#jobexeid').val();
                                            saveExe['result']  = 'complete';
                                            saveExe['executeStart'] = startDate;
                                            saveExe['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                            saveExe['jobId'] = $('#jobid').val();
                                            if(msg.indexOf('ERROR') > -1) {
                                                messge = appendMessges('Syntax Error',args);
                                                saveExe['result']  = 'Syntax Playbook Error.';
                                            } else if(msg === '') {
                                                messge = appendMessges('Connect Failed',args);
                                                saveExe['result']  = 'Connect Rabbit Or Ansible Failed.';
                                            } else {
                                                appendMessges('Playbook Receive',args);
                                                saveExe['result']  = 'NG';
                                            }

                                            saveExe['log'] = messge;
                                            var jobOperatorExecutions = [];
                                            var jobOperatorExecution = {};
                                            jobOperatorExecution['result'] = 'complete';
                                            jobOperatorExecution['type'] = keyMap[typeOp.toLowerCase()];
                                            jobOperatorExecution['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                            jobOperatorExecution['executeStart'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                            jobOperatorExecutions.push(jobOperatorExecution);
                                            saveExe['jobOperatorExecutions']  = jobOperatorExecutions;
                                            var saveExeJson = JSON.stringify(saveExe);
                                            if(msg === '' || msg.indexOf('ERROR') > 0) {
                                                var jobAssignId = getUrlParameter('jobid');
                                                $.ajax({
                                                    type:'POST',
                                                    url:'/nera/my-job/api/update-status-finish-job',
                                                    data:{'id':jobAssignId,'status':'Finished (Failed)'},
                                                    success: function (data) {
                                                        saveData(saveExeJson,callbackAjax,-1,[]);
                                                    }
                                                });
                                            } else {
                                                saveData(saveExeJson,callbackAjax,run+1,[]);
                                            }
                                        }
                                    }
                                    function runPlayBook(folderresult,getPlaybookResult,opxml) {
                                        $.ajax({
                                            type:'POST',
                                            url: '/nera/my-job/api/runplaybook/',
                                            data:{'foldername':folderresult},
                                            success:function (msg) {
                                                resultPlaybook.push(opxml.getAttribute('label')+'-==-'+msg);
                                                var args = getCurrentDatetime();
                                                if(msg.indexOf('ERROR') > -1) {
                                                    args.push(msg);
                                                    appendMessges('Result Playbook',args);
                                                    getPlaybookResult(msg,opxml,'');
                                                } else if(msg === '') {
                                                    getPlaybookResult(msg,opxml,'');
                                                } else {
                                                    var input = msg;
                                                    var refailed = new RegExp('\s*failed=([^  ]+)','g');
                                                    var reok = new RegExp('\s*ok=([^  ]+)','g');
                                                    var m = refailed.exec(input);
                                                    var n = reok.exec(input);
                                                    var result = "";
                                                    var replayrecap = new RegExp('\s*PLAY RECAP([^*]+)','g');
                                                    var c = replayrecap.exec(input);
                                                    if(c.length > 1)
                                                    {
                                                        if(n !== null && parseInt(n[1]) > 0) {
                                                            result = "Result execute: Ok <br/>";
                                                        } else if(n !== null && parseInt(n[1]) < 0) {
                                                            result = 'Result execute: Failed <br/>';
                                                        } else if(input.indexOf('PLAY RECAP')) {
                                                            result = "Result execute: Ok <br/>";
                                                        }
                                                        args.push(result + input);
                                                        appendMessges('Result Playbook',args);
                                                        // console.log(msg);
                                                        getPlaybookResult(input,opxml,result);
                                                    }
                                                }
                                            },
                                            error: function(request,error) {
                                                getPlaybookResult('',opxml,'');
                                                // console.log('loi roi');
                                            }

                                        });

                                    }
                                    createPlaybook(param,runPlayBook,opxml,getPlaybookResult);
                                }
                            });


                        }
                        if(typeOp === 'UserObject')
                        {
                            if(opxml.getAttribute('id') === 'star')
                            {
                                var args = getCurrentDatetime();
                                args.push('START');
                                var msg =   appendMessges('Start',args);

                                var saveExe = {};
                                saveExe['executeStart'] = startDate;
                                saveExe['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                saveExe['jobId'] = $('#jobid').val();
                                saveExe['result'] = 'complete';
                                saveExe['log'] = msg;
                                var jobOperatorExecutions = [];
                                var jobOperatorExecution = {};
                                jobOperatorExecution['result'] = 'complete';
                                jobOperatorExecution['type'] = keyMap[typeOp.toLowerCase()];
                                jobOperatorExecution['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                jobOperatorExecution['executeStart'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                jobOperatorExecutions.push(jobOperatorExecution);
                                saveExe['jobOperatorExecutions']  = jobOperatorExecutions;
                                var saveExeJson = JSON.stringify(saveExe);
                                saveData(saveExeJson,callbackAjax,run+1,[]);
                            }
                            else
                            {

                                var args = getCurrentDatetime();
                                var msg = args.push('END');
                                appendMessges('End',args);
                                var saveExe = {};
                                saveExe['executeStart'] = startDate;
                                saveExe['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                saveExe['jobId'] = $('#jobid').val();
                                saveExe['result'] = 'complete';
                                saveExe['log'] = msg;
                                var jobOperatorExecutions = [];
                                var jobOperatorExecution = {};
                                jobOperatorExecution['result'] = 'complete';
                                jobOperatorExecution['type'] = keyMap[typeOp.toLowerCase()];
                                jobOperatorExecution['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                jobOperatorExecution['executeStart'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                jobOperatorExecutions.push(jobOperatorExecution);
                                saveExe['jobOperatorExecutions']  = jobOperatorExecutions;
                                var saveExeJson = JSON.stringify(saveExe);
                                var jobAssignId = getUrlParameter('jobid');
                                $.ajax({
                                    type:'POST',
                                    url:'/nera/my-job/api/update-status-finish-job',
                                    data:{'id':jobAssignId,'status':'Finished (Approved)'},
                                    success: function (data) {
                                    }
                                });
                                saveData(saveExeJson,callbackAjax,-1,[]);
                            }
                            saveExe['result'] = 'complete';
                        }
                        if(typeOp === 'Approval')
                        {
                            // console.log(typeOp);
                            // create request

                            // console.log(opxml.getAttribute("approver"));
                            // console.log(resultSaveJob);
                            var job_exe_id = $('#jobexeid').val();

                            var args = getCurrentDatetime();
                            $.ajax({
                                type:'POST',
                                url:'/nera/my-job/api/save-jobopsexeapproval',
                                data:{'jobExeId':job_exe_id,'approvedBy':opxml.getAttribute("approver")},
                                success: function (data) {
                                    $.ajax({
                                        type: 'POST',
                                        url: '/nera/my-job/api/send-request-email',
                                        data: {'jobName':$('#jobname').val(),'requestAt':args[0],'approvalUrl':window.location.origin+'/menu/approval-request','email':opxml.getAttribute("approver")},
                                        success: function (dataSendRequest) {
                                        }
                                    });

                                }
                            });
                            waitingApproval();
                            var args = getCurrentDatetime();
                            appendMessges('Approver',args);
                            var msg = args.push(opxml.getAttribute("approver").replace(","," "));



                            var myInter = setInterval( waitingApproval,6000);
                            //    set next can not running and ending job

                            function waitingApproval() {
                                $.ajax({
                                    async:false,
                                    type: 'GET',
                                    url: '/nera/job-planning/api/get-job-by-id?id='+Cookies.get('jobid'),
                                    success: function (status) {

                                        if(status.status === 'Finished (Approved)')
                                        {
                                            clearInterval(myInter);

                                            var saveExe = {};
                                            saveExe['result']  = 'complete';
                                            saveExe['executeStart'] = startDate;
                                            saveExe['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                            saveExe['jobId'] = $('#jobid').val();
                                            saveExe['id'] = $('#jobexeid').val();
                                            saveExe['result'] = 'complete';
                                            saveExe['log'] = msg;
                                            var jobOperatorExecutions = [];
                                            var jobOperatorExecution = {};
                                            jobOperatorExecution['result'] = 'complete';
                                            jobOperatorExecution['type'] = keyMap[typeOp.toLowerCase()];
                                            jobOperatorExecution['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                            jobOperatorExecution['executeStart'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                            jobOperatorExecutions.push(jobOperatorExecution);
                                            saveExe['jobOperatorExecutions']  = jobOperatorExecutions;
                                            var saveExeJson = JSON.stringify(saveExe);
                                            saveData(saveExeJson,callbackAjax,run+1,[]);
                                            // $('#btnCancel1Job').prop('disabled',false);
                                            // $('#btnExportJob').prop('disabled',false);
                                            // var args = getCurrentDatetime();
                                            // var msg = args.push('END');
                                            // appendMessges('End',args);
                                            var args = getCurrentDatetime();
                                            args.push(opxml.getAttribute("approver").replace(","," "));
                                            appendMessges('Approver Status',args);


                                        }
                                        if( status.status ==='Finished (Rejected)' )
                                        {
                                            clearInterval(myInter);

                                            var saveExe = {};
                                            saveExe['result']  = 'complete';
                                            saveExe['executeStart'] = startDate;
                                            saveExe['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                            saveExe['jobId'] = $('#jobid').val();
                                            saveExe['id'] = $('#jobexeid').val();
                                            saveExe['result'] = 'complete';
                                            saveExe['log'] = msg;
                                            var jobOperatorExecutions = [];
                                            var jobOperatorExecution = {};
                                            jobOperatorExecution['result'] = 'complete';
                                            jobOperatorExecution['type'] = keyMap[typeOp.toLowerCase()];
                                            jobOperatorExecution['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                            jobOperatorExecution['executeStart'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                                            jobOperatorExecutions.push(jobOperatorExecution);
                                            saveExe['jobOperatorExecutions']  = jobOperatorExecutions;
                                            var saveExeJson = JSON.stringify(saveExe);
                                            saveData(saveExeJson,callbackAjax,ops.indexOf('end1'),[]);

                                        }
                                        // if( status.status ==='Executing')
                                        // {
                                        //
                                        // }

                                    }
                                });
                            }

                        }
                        // if(run >0)
                        // {
                        //     $.ajax({
                        //         async:false,
                        //         type:'GET',
                        //         url:'/nera/my-job/api/get-job-exe?id='+$('#jobexeid').val(),
                        //         contentType: "application/json",
                        //         success: function (resultSaveJob) {
                        //             saveExe['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                        //
                        //             saveExe['log'] = 'execution success';
                        //             saveExe['id'] =  resultSaveJob.id;
                        //             var jobOperatorExecutions = [];
                        //
                        //             jobOperatorExecution['result'] = 'complete';
                        //             jobOperatorExecution['type'] = 1;
                        //             jobOperatorExecution['executeEnd'] = getCurrentDatetime()[0]+" "+getCurrentDatetime()[1];
                        //             $.each(resultSaveJob.jobExeList,function () {
                        //                 var jobOps = {};
                        //                 jobOps['id'] = this.id;
                        //                 jobOps['result'] = this.result;
                        //                 jobOps['type'] = this.type;
                        //                 jobOps['log'] = this.result;
                        //                 jobOps['executeEnd'] = this.executeEnd.split('+')[0].replace('T',' ');
                        //                 jobOps['executeStart'] = this.executeStart.split('+')[0].replace('T',' ');;
                        //                 jobOperatorExecutions.push(jobOps);
                        //             });
                        //             jobOperatorExecutions.push(jobOperatorExecution);
                        //             // jobOperatorExecutions.push(jobOperatorExecution);
                        //
                        //             saveExe['jobOperatorExecutions']  = jobOperatorExecutions;
                        //             var saveExeJson = JSON.stringify(saveExe);
                        //             $.ajax({
                        //                 async:false,
                        //                 type:'POST',
                        //                 url:'/nera/my-job/api/save-job-exe',
                        //                 contentType: "application/json",
                        //                 data:saveExeJson,
                        //                 success: function (data) {
                        //                     $('#jobexeid').val(data.id);
                        //                 }
                        //
                        //             });
                        //         }
                        //
                        //
                        //     });
                        //
                        // }
                        function  callbackAjax(data,run,outMap) {
                            if(run !== -1)
                            {
                                $('#jobexeid').val(data);
                                runningWorkFlowOps(run,isFinished,timer,false,ops,isRun,cells,mappingFlow,dataWorkflow,outMap);
                            }
                            else
                            {
                                var dt = new Date();
                                var time = (dt.getMonth()+1)+'_'+dt.getDate()+'_'+dt.getFullYear()+'_'+ dt.getHours()+'_'+ dt.getMinutes()+'_'+ dt.getSeconds()+'_'+dt.getMilliseconds();
                                var name = "execute_log_"+time+'.txt';
                                var jobAssignId = getUrlParameter('jobid');
                                $.ajax({
                                    // async: false,
                                    type: 'POST',
                                    url: '/nera/my-job/api/send-mail-log-execute',
                                    data: {'contentFile':$('#executionlog').html(),'fileName':name, 'jobAssignId':jobAssignId},
                                    success: function (dataLogSendMail) {
                                    }
                                });
                                $('#btnCancel1Job').prop('disabled',false);
                                $('#btnExportJob').prop('disabled',false);
                                $('#btnCancel2Job').prop('disabled',true);
                            }
                        }
                        // var nextOp = getNextOps(mappingFlow,ops[run]);
                        // if(nextOp.length >0)
                        // {
                        //     $.each(nextOp,function (i,optmp) {
                        //         var opxmltmp = mxUtils.parseXml(getOpsbyId(cells,optmp).contentXML).documentElement;
                        //         var typeOptmp = opxmltmp.nodeName;
                        //         if(typeOptmp.toLowerCase() === 'approval'&& isRun[ops.indexOf(optmp)] )
                        //         {
                        //             var job_exe_id =
                        //
                        //             var args = getCurrentDatetime();
                        //             // args.push(getOpsbyId(cells,getNextOps(mappingFlow,'star')[0]).properties);
                        //             // console.log(getNextOps(mappingFlow,'star')[0]);
                        //             // appendMessges('Playbook Execute',args);
                        //
                        //             $.ajax({
                        //                 async: false,
                        //                 type: 'POST',
                        //                 url: '/nera/my-job/api/send-request-email',
                        //                 data: {'jobName':$('#jobname').val(),'approvalName':opxml.getAttribute("approver"),'requestAt':args,'approvalUrl':'','email':opxml.getAttribute("approver")},
                        //                 success: function (data) {
                        //                     var args = getCurrentDatetime();
                        //                     appendMessges('Approver',args);
                        //                     console.log(data);
                        //                 }
                        //             });
                        //             saveExe['result'] = 'complete';
                        //             $.ajax({
                        //                 async: false,
                        //                 type: 'POST',
                        //                 url: '/nera/my-job/api/save-jobopsexeapproval',
                        //                 data: {'job_exe_id': job_exe_id,'jobid':$('#jobid').val()},
                        //                 success: function (data) {
                        //
                        //                 }
                        //             });
                        //         }
                        //
                        //     })
                        //     // console.log(nextOp);
                        // }

                    }
                    // if(isFinished)
                    // {
                    //
                    //     $('#btnCancel1Job').prop('disabled',false);
                    //     $('#btnExportJob').prop('disabled',false);
                    // }

                    $('#btnExportJob').unbind().on('click', function(e){

                        var dt = new Date();
                        var time = (dt.getMonth()+1)+'_'+dt.getDate()+'_'+dt.getFullYear()+'_'+ dt.getHours()+'_'+ dt.getMinutes()+'_'+ dt.getSeconds()+'_'+dt.getMilliseconds();
                        var name = "execute_log_"+time+'.txt';
                        $.ajax({
                            // async: false,
                            type: 'POST',
                            url: '/nera/my-job/api/export-log-execute',
                            data: {'txt':$('#executionlog').html(),'path':name},
                            success: function (data) {
                                downloadFileLog(name);
                            }
                        });
                    });

                    // })

                }
                runningWorkFlowOps(run,isFinished,timer,isBreak,ops,isRun,cells,mappingFlow,dataWorkflow,[]);


            }
        });
        // end get workflow

    });

});
