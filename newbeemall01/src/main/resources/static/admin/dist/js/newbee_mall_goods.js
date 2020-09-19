$(function () {
    $("#jqGrid").jqGrid({
        url: '/admin/goods/list',
        datatype: "json",
        colModel: [
            {label: '商品编号', name: 'goodsId', index: 'goodsId', width: 60, key: true},
            {label: '商品名', name: 'goodsName', index: 'goodsName', width: 120},
            {label: '商品简介', name: 'goodsIntro', index: 'goodsIntro', width: 120},
            {label: '商品图片', name: 'goodsCoverImg', index: 'goodsCoverImg', width: 120, formatter: coverImageFormatter},
            {label: '商品库存', name: 'stockNum', index: 'stockNum', width: 60},
            {label: '商品售价', name: 'sellingPrice', index: 'sellingPrice', width: 60},
            {
                label: '上架状态',
                name: 'goodsSellStatus',
                index: 'goodsSellStatus',
                width: 80,
                formatter: goodsSellStatusFormatter
            },
            {label: '创建时间', name: 'createTime', index: 'createTime', width: 60}
        ],

        //label: 如果colNames为空则用此值来作为列的显示名称，如果都没有设置则使用name 值
        height:760,
        rowNum: 20,
        rowList: [20, 50, 80],
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

/*
formatter主要是设置格式化类型，三个参数
    cellvalue - 当前cell的值
    options - 该cell的options设置，包括{rowId, colModel,pos,gid}
    rowObject - 当前cell所在row的值，是一个对象
* */
    function coverImageFormatter(cellvalue) {
        return "<img src='"+cellvalue+"' height=\"80\" width=\"80\" alt='商品主图'/>";
        //返回字符串，\为发斜杠符号 ？？
    }

    function goodsSellStatusFormatter(cellvalue) {
        //商品上架状态 0-上架 1-下架
        if (cellvalue == 0) {
            return "<button type=\"button\" class=\"btn btn-block btn-success btn-sm\" style=\"width: 80%;\">销售中</button>";
        }
        if (cellvalue == 1) {
            return "<button type=\"button\" class=\"btn btn-block btn-secondary btn-sm\" style=\"width: 80%;\">已下架</button>";
        }
    }

    function coverImageFormatter(cellvalue) {
        return "<img src='" + cellvalue + "' height=\"80\" width=\"80\" alt='商品主图'/>";
    }
});


/**
 * jqGrid重新加载
 */
function reload() {
    initFlatPickr();
    var page = $("#jqGrid").jqGrid('getGridParam', 'page');
    $("#jqGrid").jqGrid('setGridParam', {
        page: page
    }).trigger("reloadGrid");
}

//新增
function addGoods() {
    window.location.href='/admin/goods/edit';
}

//更改
function editGoods() {
    var id = getSelectRow();
    if ( id == null){
        return;
    }
    window.location.href="/admin/goods/edit/"+id;
}

function putUpGoods() {
    var ids = getSelectRows();
    if ( ids == null){
        return;
    }
    swal({
        title: "确认弹框",
        text: "确定要执行上架操作吗？",
        icon:"warning",
        buttons: true,
        dangerMode: true
    }).then((flag) => {
        if (flag){
            console.log("flag:"+flag);
            $.ajax({
                url: '/admin/goods/status/0',
                type : 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(ids),
                success: function (result) {
                    if (result.code == 200){
                        swal("上架成功",{
                            icon: "success"
                        });
                        $("#jqGrid").trigger("reloadGrid");
                    }else {
                        swal(result.message,{
                            icon: "error"
                        });
                    }
                }
            })
        }
    }
);
}

function putDownGoods() {
    var ids = getSelectRows();
    if ( ids == null){
        return;
    }
    swal({
        title: "确认弹框",
        text: "确定要执行下架操作吗？",
        icon:"warning",
        buttons: true,
        dangerMode: true
    }).then((flag) => {
        if(flag) {
            $.ajax({
                url: '/admin/goods/status/1',
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(ids),
                success: function (result) {
                    if (result.code == 200) {
                        swal("下架成功", {
                            icon: "success"
                        });
                        $("#jqGrid").trigger("reloadGrid");
                    } else {
                        swal(result.message, {
                            icon: "error"
                        });
                    }
                }
            })
        }
    }
    );
}