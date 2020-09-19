$(function () {

   $("#jqGrid").jqGrid({
       url: '/admin/users/list',
       datatype: "json",
       colModel: [
           {label: 'id', name: 'userId', index: 'userId', width: 50, key: true, hidden: true},
           {label: '昵称', name: 'nickName', index: 'nickName', width: 180},
           {label: '登录名', name: 'loginName', index: 'loginName', width: 120},
           {label: '身份状态', name: 'isDeleted', index: 'isDeleted', width: 60,formatter:lockedFormatter},
           {label: '是否注销', name: 'lockedFlag', index: 'lockedFlag', width: 60,formatter:deleteFormatter},
           {label: '注册时间', name: 'createTime', index: 'createTime', width: 120}
       ],

       height: 560,
       rowNum: 10,
       rowList: [10, 20, 50],
       styleUI: 'Bootstrap',
       loadtext: '信息读取中...',
       rownumbers: false,
       rownumWidth: 20,
       autowidth: true,
       multiselect: true,
       pager: "#jqGridPager",
       jsonReader: {
           root: "data.list",
           page: "data.currPage",
           total: "data.totalPage",
           records: "data.totalCount"
       },

       prmNames: {
           page: "page",
           rows:"limit",
           order:"order"
       },

       gridComplete: function(){
           //隐藏grid底部滚动条
           $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x":"hidden"});
       }
   });
   
   $("#window").resize(function () {
       $("#jqGrid").setGridWidth($(".card-body").width())
   });
   
   function lockedFormatter(cellValues) {
       if (cellValues ==0) {
           return "<button type=\"button\" class=\"btn btn-block btn-success btn-sm\" style=\"width:80%;\">正常</button>"
       }
       if (cellValues ==1) {
           return "<button type=\"button\" class=\"btn btn-block btn-secondary btn-sm\" style=\"width:80%;\">已锁定</button>"
       }
   }
    function deleteFormatter(cellValues) {
        if (cellValues ==0) {
            return "<button type=\"button\" class=\"btn btn-block btn-success btn-sm\" style=\"width:80%;\">正常</button>"
        }
        if (cellValues ==1) {
            return "<button type=\"button\" class=\"btn btn-block btn-secondary btn-sm\" style=\"width:80%;\">已注销</button>"
        }
    }
});

function reload() {
    var page = $("#jqGrid").jqGrid("getGridParam",'page')
    $("#jqGrid").jqGrid("setGridParam",{
        page:page
    }).trigger("reloadGrid");
}

function changeLockStatus(lockStatus) {
    var ids = getSelectRows();
    if (ids == null){
       return;
    }
    if(lockStatus !=0 && lockStatus!=1){
        swal('非法操作', {
            icon: "error"
        });
        return;
    }
    swal({
        title: '确认弹框',
        text: '确认要修改账户状态吗？',
        icon: 'warning',
        buttons: true,
        dangerMode: true
    }).then((flag) => {
        if(flag){
            $.ajax({
                url: "/admin/users/changeLockStatus/"+ lockStatus,
                type: 'POST',
                contentType: "application/json",
                data: JSON.stringify(ids),
                success:function (r) {
                    if (r.code == 200){
                        swal('状态修改成功',{
                            icon: 'success'
                        })
                        $("#jqGrid").trigger("reloadGrid")
                    }else {
                        swal(r.message,{
                            icon: 'error'
                        })
                    }
                },
                error:function () {
                    swal('操作失败',{
                        icon: 'error'
                    })
                }
            })
        }
    }
    );
}

