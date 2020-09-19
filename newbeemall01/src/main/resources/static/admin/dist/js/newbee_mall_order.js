$(function () {
    $("#jqGrid").jqGrid({
        url: '/admin/orders/list',
        datatype: 'json',
        colModel: [
            {label: 'id', name: 'orderId',index: 'orderId', width: 50, key: true, hidden: true},
            {label: '订单号',name: 'orderNo',index: 'orderNo', width: 120},
            {label: '订单总价',name: 'totalPrice',index: 'totalPrice', width: 60},
            {label: '订单状态',name: 'orderStatus',index: 'orderStatus', width: 60, formatter: orderStatusFormatter},
            {label: '支付方式',name: 'payType',index: 'payType', width: 70,formatter: payTypeFormatter},
            {label: '收件人地址', name: 'userAddress',index: 'userAddress', width: 10, hidden: true},
            {label: '创建时间',name: 'createTime',index: 'createTime', width: 120},
            {label: '操作', name: 'createTime',index: 'createTime', width: 120, formatter: operateFormatter}
        ],
        height: 760,
        rowNum: 20,
        rowList: [20,50,80],
        styleUI: 'Bootstrap',
        loadtext: '信息读取中',
        rownumbers: false,
        rownumWidth:20,
        autowidth: true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "data.list",
            page: 'data.currPage',
            total: 'data.toalPage',
            records: 'data.totalCount'
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });
    
    $(window).resize(function () {
        $("#jqGrid").setGridWidth($(".card-body").width());
    });


    function orderStatusFormatter(cellvalue) {
        //订单状态:0.待支付 1.已支付 2.配货完成 3:出库成功 4.交易成功 -1.手动关闭 -2.超时关闭 -3.商家关闭
        if (cellvalue == 0){
            return "待支付";
        }
        if (cellvalue == 1){
            return "已支付";
        }
        if (cellvalue == 2){
            return "配货完成";
        }
        if (cellvalue == 3){
            return "出库成功";
        }
        if (cellvalue == 4){
            return "交易成功";
        }
        if (cellvalue == -1){
            return "手动关闭";
        }
        if (cellvalue == -2){
            return "超时关闭";
        }
        if (cellvalue == -3){
            return "商家关闭";
        }
    }

    function payTypeFormatter(cellvalue) {
        //支付类型:0.无 1.支付宝支付 2.微信支付
        if (cellvalue == 0) {
            return "无";
        }
        if (cellvalue == 1) {
            return "支付宝支付";
        }
        if (cellvalue == 2) {
            return "微信支付";
        }
    }


    function operateFormatter(cellvalue,rowobject) {
        console.log(cellvalue);
        return "<a href=\'##\' onclick=openOrderItems(" + rowobject.rowId + ")>查看订单信息</a>" +
            "<br/>" +
            "<a href=\'##\' onclick=openExpressInfo(" + rowobject.rowId + ")>查看收件人信息</a>";
    }
});

//jqGrid重新加载
function reload() {
    // initFlatPickr();
    var page = $("#jqGrid").jqGrid('getGridParam','page');
    $("#jqGrid").jqGrid('setGridParam',{
        page: page
    }).trigger("reloadGrid");
}


/**
 * 查看订单项信息
 * @param orderId
 */
function openOrderItems(orderId) {
    $(".modal-title").html('订单详情');
    $.ajax({
        type: 'GET',
        url: '/admin/order-items/'+orderId,
        contentType: 'application/json',
        success:function (r) {
            $("#orderItemModal").modal('show');
            if (r.code == 200){
                var itemString = '';
                for (i = 0; i < r.data.length; i++){
                    itemString += r.data[i].goodsName + ' x ' + r.data[i].goodsCount + ' 商品编号 ' + r.data[i].goodsId + ';<br/>';
                }
                $("#orderItemString").html(itemString);
            }else {
                swal(result.message, {
                    icon: "error"
                });
            }
        },
        error: function () {
            swal("操作失败", {
                icon: "error"
            });
        }
    });
}

/**
 * 查看收件人信息
 * @param orderId
 */
function openExpressInfo(orderId) {
    var rowData = $("#jqGrid").jqGrid('getRowData',orderId);
    $(".modal-title").html('收件人信息');
    $("#expressInfoModal").modal('show');
    $("#userAddressInfo").html(rowData.userAddress);
}

//修改订单
function orderEdit() {
    var id = getSelectRow();
    if (id == null){
        return;
    }
    var rowData = $("#jqGrid").jqGrid('getRowData',id);
    $(".modal-title").html('订单编辑');
    $("#orderId").val(id);
    $("#totalPrice").val(rowData.totalPrice);
    $("#userAddress").val(rowData.userAddress);
    $("#orderInfoModal").modal('show');
}

//模态确认按钮
$("#saveButton").click(function () {
    var totalPrice = $("#totalPrice").val();
    var userAddress = $("#userAddress").val();
    var userName = $("#userName").val();
    var userPhone = $("#userPhone").val();

    var orderId = getSelectedRowWithoutAlert();
    var url = '/admin/orders/update';

    var data = {
        'orderId':orderId,
        'totalPrice':totalPrice,
        'userAddress':userAddress,
        'userName':userName,
        'userPhone':userPhone
    };

    $.ajax({
        type: 'POST',
        url: url,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (result) {
            $('#orderInfoModal').modal('hide');
            if (result.code == 200) {
                swal("保存成功", {
                    icon: "success"
                });
                reload();
            } else {
                swal(result.message, {
                    icon: "error"
                });
            }
            ;
        },
        error: function () {
            swal("操作失败", {
                icon: "error"
            });
        }
    })
});

//配货完成
function orderCheckDone() {
    var ids = getSelectRows();
    if (ids == null){
        return;
    };

    var orderNos = '';
    for (var i =0; i<ids.length; i++){
        var rowData = $("#jqGrid").jqGrid('getRowData',ids[i]);
        if (rowData.orderStatus != '已支付'){
            orderNos += rowData.orderNo + " ";
        }
    }

    if (orderNos.length >= 100){
        swal("你选择了太多不是支付成功状态的订单，无法执行配货完成操作", {
            icon: "error"
        });
        return;
    }
    
    if (orderNos.length >0 && orderNos.length <100){
        swal("以下订单不是支付成功状态，无法执行配货完成操作： " + orderNos, {
            icon: "error"
        });
        return;
    }

    swal({
        title: '确认弹框',
        text: '确认要执行出库完成操作吗?',
        icon: 'warning',
        buttons: true,
        dangerMode: true
    }).then((flag) => {
        if (flag){
            $.ajax({
                type: 'POST',
                url: '/admin/orders/checkDone',
                contentType: 'application/json',
                data: JSON.stringify(ids),
                success: function (r) {
                    if (r.code == 200) {
                        swal("配货完成", {
                            icon: "success",
                        });
                        $("#jqGrid").trigger("reloadGrid");
                    } else {
                        swal(r.message, {
                            icon: "error",
                        });
                    }
                },
                error: function () {
                    swal("操作失败", {
                        icon: "error"
                    });
                }
            })
        }
    }
    );
}

//出库
function orderCheckOut() {
    var ids = getSelectRows();
    if (ids == null){
        return;
    };
    var orderNos = '';
    for (var i =0; i<ids.length; i++){
        var rowData = $("#jqGrid").jqGrid('getRowData',ids[i]);
        if (rowData.orderStatus != '已支付' && rowData.orderStatus != '配货完成'){
            orderNos += rowData.orderNo + " ";
        }
    }

    if (orderNos.length >= 100){
        swal("你选择了太多不是支付成功和配货完成状态的订单，无法执行出库操作", {
            icon: "error"
        });
        return;
    }

    if (orderNos.length >0 && orderNos.length <100){
        swal("以下订单不是支付成功和配货完成状态，无法执行出库操作： " + orderNos, {
            icon: "error"
        });
        return;
    }

    swal({
        title: '确认弹框',
        text: '确认要执行出库操作吗?',
        icon: 'warning',
        buttons: true,
        dangerMode: true
    }).then((flag) => {
        if (flag){
            $.ajax({
                type: 'POST',
                url: '/admin/orders/checkOut',
                contentType: 'application/json',
                data: JSON.stringify(ids),
                success: function (r) {
                    if (r.code == 200) {
                        swal("出库完成", {
                            icon: "success",
                        });
                        $("#jqGrid").trigger("reloadGrid");
                    } else {
                        swal(r.message, {
                            icon: "error",
                        });
                    }
                },
                error: function () {
                    swal("操作失败", {
                        icon: "error"
                    });
                }
            })
        }
    }
);
}

//关闭订单
function closeOrder() {
    var ids = getSelectRows();
    if (ids == null){
        return;
    };

    swal({
        title: '确认弹框',
        text: '确认要执行关闭订单操作吗?',
        icon: 'warning',
        buttons: true,
        dangerMode: true
    }).then((flag) => {
        if (flag){
            $.ajax({
                type: 'POST',
                url: '/admin/orders/close',
                contentType: 'application/json',
                data: JSON.stringify(ids),
                success: function (r) {
                    if (r.code == 200) {
                        swal("关闭订单完成", {
                            icon: "success",
                        });
                        $("#jqGrid").trigger("reloadGrid");
                    } else {
                        swal(r.message, {
                            icon: "error",
                        });
                    }
                },
                error: function () {
                    swal("操作失败", {
                        icon: "error"
                    });
                }
            })
        }
    })
}