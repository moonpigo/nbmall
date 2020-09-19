$(function () {
   $("#jqGrid").jqGrid({
       url: '/admin/carousels/list',
       datatype: "json",
       colModel: [
           {label: 'id', name: 'carouselId', index: 'carouselId', width: 50, key: true, hidden: true},
           {label: '轮播图', name: 'carouselUrl', index: 'carouselUrl', width: 180, formatter: coverImageFormatter},
           {label: '跳转链接', name: 'redirectUrl', index: 'redirectUrl', width: 120},
           {label: '排序值', name: 'carouselRank', index: 'carouselRank', width: 120},
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

   function coverImageFormatter(cellValue) {
       return "<img src='"+cellValue+"' width=\"160\" height=\"120\" alt='coverImage'/>";
   }


    new AjaxUpload('#uploadCarouselImg',{
        action:'/admin/upload/file',
        name: 'file',
        autoSubmit: true,
        responseType: "json",
        onSubmit:function (file,extension) {
            if (!(extension && /^(jpg|jpeg|png|gif)$/.test(extension.toLowerCase()))){
                alert('只支持jpg、png、gif格式的文件！');
                return false;
            }
        },
        onComplete:function (file,response) {
            console.log("..response:"+response+"..data:"+response.data);
            if (response != null && response.code == 200){
                $("#carouselImg").attr("src",response.data);
                $("#carouselImg").attr("style","width:128px;height:128px;display:block;")
                return false;
            }else {
                alert("error");
            }
        }
    })

});

function reload() {
    var page = $("#jqGrid").jqGrid("getGridParam",'page')
    $("#jqGrid").jqGrid("setGridParam",{
        page:page
    }).trigger("reloadGrid");
}

//新增
function carouselAdd() {
    reset();
    $(".modal-title").html("轮播图添加");
    $("#carouselModal").modal('show');
}

//修改
function carouselEdit() {
    reset();
    var  id = getSelectRow();
    if (id ==null){
        return;
    }
    //请求数据
    $.get("/admin/carousels/info/" + id, function (result) {
        if (result.code == 200 && result.data != null){
            //填充数据至modal
            $("#carouselImg").attr('src',result.data.carouselUrl);
            $("#carouselImg").attr('style','height: 64px;width: 64px;diplay:block');
            $("#redirectUrl").val(result.data.redirectUrl);
            $("#carouselRank").val(result.data.carouselRank);
        }
    });
    $(".modal-title").html("轮播图编辑");
    $("#carouselModal").modal('show');
}

function reset() {
    $("#carouselImg").attr('src','/admin/dist/img/img-upload.png');
    $("#carouselImg").attr('style','height: 64px;width: 64px;');
    $("#edit-error-msg").css('display','none');
    $("#redirectUrl").val('##');
    $("#carouselRank").val(0);
}

//模态框的保存按钮
$("#saveButton").click(function () {

    var carouselUrl = $("#carouselImg")[0].src;
    var redirectUrl = $("#redirectUrl").val();
    var carouselRank = $("#carouselRank").val();
    if (isNull(carouselUrl) || isNull(redirectUrl) || isNull(carouselRank)){
        $("#edit-error-msg").css("display","block");
        $("#edit-error-msg").html("参数不能为空！");
        return;
    }
    var url= '/admin/carousels/save';
    var data = {
        "carouselUrl":carouselUrl,
        "redirectUrl": redirectUrl,
        "carouselRank": carouselRank
    };
    var  id = getSelectedRowWithoutAlert();
    if (id !=null){
       url =  '/admin/carousels/update';
        var data = {
            "carouselId": id,
            "carouselUrl":carouselUrl,
            "redirectUrl": redirectUrl,
            "carouselRank": carouselRank
        };
    }

    $.ajax({
        url: url,
        type: "POST",
        contentType: 'application/json',
        data: JSON.stringify(data),
        success:function (result) {
            $("#carouselModal").modal('hide');
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

function deletecarousel() {
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
                url:'/admin/carousels/delete' ,
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