$(function () {

    var configType = $("#configType").val();

   $("#jqGrid").jqGrid({
       url: '/admin/indexConfigs/list?configType='+configType,
       datatype: "json",
       colModel: [
           {label: 'id', name: 'configId', index: 'configId', width: 50, key: true, hidden: true},
           {label: '配置项名称', name: 'configName', index: 'configName', width: 180},
           {label: '跳转链接', name: 'redirectUrl', index: 'redirectUrl', width: 120},
           {label: '排序值', name: 'configRank', index: 'configRank', width: 120},
           {label: '商品编号', name: 'goodsId', index: 'goodsId', width: 120},
           {label: '添加时间', name: 'createTime', index: 'createTime', width: 120}
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
});

function reload() {
    var page = $("#jqGrid").jqGrid("getGridParam",'page')
    $("#jqGrid").jqGrid("setGridParam",{
        page:page
    }).trigger("reloadGrid");
}

function reset() {
    $("#configName").val('');
    $("#redirectUrl").val('##');
    $("#goodsId").val(0);
    $("#configRank").val(0);
    $("#edit-error-msg").css('display','none');
}

//新增
function configAdd() {
    reset();
    $(".modal-title").html("首页配置项添加");
    $("#indexConfigModal").modal('show');
}

//修改
function configEdit() {
    reset();
    var  id = getSelectRow();
    if (id ==null){
        return;
    }
    //通过id拿到行数据
    var rowData = $("#jqGrid").getRowData(id);
    $(".modal-title").html("首页配置项编辑");
    $("#indexConfigModal").modal('show');
    $("#configName").val(rowData.configName);
    $("#goodsId").val(rowData.goodsId);
    $("#redirectUrl").val(rowData.redirectUrl);
    $("#carouselRank").val(rowData.configRank);
}

//删除
function deleteConfig() {
    var ids = getSelectRows();
    if (ids == null){
        return;
    }
    swal({
        title: "确认弹框",
        text: "确认要删除数据吗？",
        icon: "warning",
        buttons: true,
        dangerMode: true,
    }).then((flag) => {
        if(flag) {
            $.ajax({
                url:'/admin/indexConfigs/delete' ,
                type: 'POST' ,
                contentType: 'application/json',
                data: JSON.stringify(ids),
                success:function (r) {
                    if (r.code == 200){
                        swal("删除成功",{
                            icon: "success"
                        });
                        $("#jqGrid").trigger("reloadGrid");
                    }else {
                        swal(r.message,{
                            icon: "error"
                        });
                    }
                }
            });
        }
    }
)
}



//模态框的保存按钮
$("#saveButton").click(function () {
    var configType = $("#configType").val();
    var configName = $("#configName").val();
    var redirectUrl = $("#redirectUrl").val();
    var goodsId = $("#goodsId").val();
    var configRank = $("#configRank").val();
    if (isNull(configName) || isNull(redirectUrl) || isNull(goodsId) || isNull(configRank)){
        $("#edit-error-msg").css("display","block");
        $("#edit-error-msg").html("参数不能为空！");
        return;
    }
    if (!validCN_ENString2_18(configName)){
        $("#edit-error-msg").css("display","block");
        $("#edit-error-msg").html("请输入符合规范的配置项名称！");
        return;
    }
    var url= '/admin/indexConfigs/save';
    var data = {
        "configType":configType,
        "configName":configName,
        "redirectUrl": redirectUrl,
        "goodsId":goodsId,
        "configRank": configRank
    };
    var  id = getSelectedRowWithoutAlert();
    if (id !=null){
       url =  '/admin/indexConfigs/update';
        var data = {
            "configId": id,
            "configType":configType,
            "configName":configName,
            "redirectUrl": redirectUrl,
            "goodsId":goodsId,
            "configRank": configRank
        };
    }

    $.ajax({
        url: url,
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(data),
        success:function (result) {
            $("#indexConfigModal").modal('hide');
            if (result.code == 200){
                swal("保存成功",{
                    icon: "success"
                });
                reload();
            } else {
                swal(result.message,{
                    icon: "error"
                })
            }
        },
        error:function () {
            swal("操作失败",{
                icon: "error"
            })
        }
    })
});

