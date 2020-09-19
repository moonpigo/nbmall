
//判空
function isNull(obj) {
    if (obj == null || obj == undefined || obj.trim() == ""){
        return true;
    }
    return false;
}

//验证是用户名是否是由4-16位的数字，字母，下划线，减号 组成
// pattern.test(str)方法:测试正则表达式与指定字符串是否匹配
function validUserName(userName) {
    var pattern = /^[a-zA-Z0-9_-]{4,16}$/;
    if ( pattern.test(userName.trim())){
        return (true);
    }else {
        return (false);
    }
}

//验证用户密码是否是由6-20位的数字，字母组成
function validPassword(password) {
    var pattern = /^[a-zA-Z0-9]{6,20}$/;
    if ( pattern.test(password.trim())){
        return (true);
    }else {
        return (false);
    }
}

//验证昵称是否是由2—18位的中英文字符串
function validCN_ENString2_18(str) {
    var pattern = /^[a-zA-Z0-9-\u4E00-\u9FA5_,， ]{2,18}$/;
    if ( pattern.test(str.trim())){
        return (true);
    }else {
        return (false);
    }
}

//获取jqGrid选中的一条记录(不出现弹框)
function getSelectedRowWithoutAlert() {
    var jqGrid = $("#jqGrid");
    var rowKey = jqGrid.getGridParam("selrow");
    //获取选中的单行记录的id,也可写成$("#jqGrid").jqGrid("getGridParam", rowid)？？;
    if (!rowKey){ //一条都没选
        return;
    }
    //获取选中的多行记录的所以id,
    var selectedIDs = jqGrid.getGridParam("selarrrow");
    if (selectedIDs.length >1) { //选了2条以上
        return;
    }
    return selectedIDs[0];
}

//获取jqGrid选中的一条记录
function getSelectRow(){
    var jqGrid =  $("#jqGrid");
    var rowkey = jqGrid.getGridParam("selrow");
    if (!rowkey){
        swal("请选择一条记录",{
            icon: "warning"
        });
        return;
    }
    var selectedIDs = jqGrid.getGridParam("selarrrow");
    if (selectedIDs.length > 1){
        swal("只能选择一条记录",{
            icon: "warning"
        });
        return;
    }
    return selectedIDs[0];
}


/**
 * 获取jqGrid选中的多条记录
 */
function getSelectRows() {
    var jqGrid =  $("#jqGrid");
    var rowkey = jqGrid.getGridParam("selrow");
    if (!rowkey){
        swal("请选择一条记录",{
            icon: "warning"
        });
        return;
    }
    var ids = jqGrid.getGridParam("selarrrow");
    return ids;
}

function validLength(obj,length) {
    if (obj.trim().length <length){
        return true;
    }
    return false;
}

/*
手机号正则验证
* */
function validPhoneNumbers(phone) {
    if ((/^1(3|4|5|6|7|8|9)\d{9}$/.test(phone))) {
        return true;
    }
    return false;
}
