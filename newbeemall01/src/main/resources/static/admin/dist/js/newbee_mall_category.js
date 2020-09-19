$(function () {
    var parentId = $("#parentId").val();
    var categoryLevel = $("#categoryLevel").val();

    $("#jqGrid").jqGrid({
        url: '/admin/categories/list?categoryLevel='+categoryLevel+'&parentId='+parentId,
        datatype: "json",
        colModel: [
            {label: 'id', name: 'categoryId', index: 'categoryId', width: 50, key: true, hidden: true },
            {label: '分类名称', name: 'categoryName', index: 'categoryName', width: 240},
            {label: '排序值', name: 'categoryRank', index: 'categoryRank', width: 120},
            {label: '添加时间', name: 'createTime', index: 'createTime', width: 120}
        ],

        //label: 如果colNames为空则用此值来作为列的显示名称，如果都没有设置则使用name 值
        height: 560,
        rowNum: 10,
        rowList: [10, 20, 50],
        styleUI: 'Bootstrap', //使用bootstrap样式主题
        loadtext: '信息读取中...',
        rownumbers: false,  //是否显示行号
        rownumWidth: 20,    //行号所在列的宽度
        autowidth: true,    //Grid的宽度会根据父容器的宽度自动重算
        multiselect: true,
        pager: "#jqGridPager",  //定义翻页用的导航栏，必须是有效的html元素。翻页工具可以放在页面的任意位置。指定了jqGrid页脚显示位置


        jsonReader: {                   //jsonReader用于设置如何解析从Server端发回来的json数据
            root: "data.list",          // Json数据
            page: "data.currPage",      // json中 当前页
            total: "data.totalPage",    // json中 总页数
            records: "data.totalCount"  // json中 总记录数
        },

        prmNames: {  //设置jqGrid将要向Server传递的参数名称
            page: "page",  // 表示请求页码的参数名称
            rows: "limit",  // 表示请求行数的参数名称
            order: "order" // 表示采用的排序方式的参数名称
        },

        gridComplete: function(){
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x":"hidden"});
        }
    });

    $("window").resize(function () {  //监听窗口大小缩放，而后执行动作
        $("#jqGrid").setGridWidth($(".card-body").width());
    });
    
});

//jqGrid重新加载
function reload() {
    var page = $("#jqGrid").jqGrid('getGridParam','page');
    $("#jqGrid").jqGrid('setGridParam',{
        page: page  //设置翻到第几页
    }).trigger("reloadGrid");

}

//新增分类
function categoryAdd() {
    reset();
    $(".modal-title").html("分类添加");
    $("#categoryModal").modal('show');
}

//新增时重置
function reset() {
    $("#categoryName").val('');
    $("#categoryRank").val(0);
    $('#edit-error-msg').css("display","none");
}

/*
* 点击保存按钮
* 当不选择选项 或选择多个选项时，保存
* 当选择一个选项时，更新
* */
$('#saveButton').click(function () {
    var categoryName = $("#categoryName").val();
    var categoryRank = $("#categoryRank").val();
    var categoryLevel = $("#categoryLevel").val();
    var parentId = $("#parentId").val();

    if (!validCN_ENString2_18(categoryName)){
        $("#edit-error-msg").css("display", "block");
        $("#edit-error-msg").html("请输入符合规范的分类名称！");

    }else {
        var data = {
            categoryName: categoryName,
            categoryRank: categoryRank,
            categoryLevel: categoryLevel,
            parentId: parentId
        };
        var url = '/admin/categories/save';

        var id = getSelectedRowWithoutAlert();
        if (id != null){ //选择一个选项时，执行更新
            var data = {
                categoryName: categoryName,
                categoryRank: categoryRank,
                categoryLevel: categoryLevel,
                parentId: parentId,
                categoryId: id  //多了id
            };
             url =  '/admin/categories/update';
        }
        //当没有选择或者选择一条以上时，id为null，执行添加操作
        $.ajax({
            type: 'POST',
            url: url,
            contentType: 'application/json',
            data: JSON.stringify(data),
            //将json对象转化成字符串，且需要添加 contentType:”json/application”，此时不可用get
            success:function (result) {
               if (result.Code == 200){
                   $('#categoryModal').modal('hide');
                   swal("保存成功", {
                       // swal("title","text","success"); 第三个参数是提醒类型（success,error,warning,input
                        icon: "success"
                   });

               }else {
                   $('#categoryModal').modal('hide');
                   swal(result.message, { //得到失败的信息
                       icon: "success"
                   });
               }
            },
            error:function () {
                swal("操作失败", {
                    icon: "error"
                });
            }
        });
    }
});

//修改
function categoryEdit() {
    reset();
    var id = getSelectRow();
    if (id == null){
        return;
    }

    var rowData = $("#jqGrid").jqGrid("getRowData",id);
    //根据行id获取表格单行数据
    $(".modal-title").html('分类编辑');
    $("#categoryModal").modal('show');
    $("#categoryId").val(id);
    $("#categoryName").val(rowData.categoryName);
    $("#categoryRank").val(rowData.categoryRank);
}

//下级分类管理
function categoryManager() {
    var categoryLevel = parseInt($("#categoryLevel").val());
    var parentId = $("#parentId").val();
    var id =getSelectRow();
    if (id == undefined || id < 0){
        return false;
    }
    if (categoryLevel == 1 || categoryLevel == 2){
        categoryLevel = categoryLevel+1;
        window.location.href = '/admin/categories?categoryLevel='+categoryLevel+'&parentId='+id+'&backParentId='+parentId;
    } else {
        swal('无下一级分类', {
            icon: 'warning'
        });
    }
}

//返回上一级分类
function categoryBack() {
    var categoryLevel = parseInt($("#categoryLevel").val());
    var backParentId = $("#backParentId").val();
    if (categoryLevel ==2 || categoryLevel||3){
        categoryLevel = categoryLevel-1;
        window.location.href = '/admin/categories?categoryLevel='+categoryLevel+'&parentId='+backParentId+'&backParentId='+0;
    } else {
        swal('无上一级分类', {
            icon: 'warning'
        });
    }
}
