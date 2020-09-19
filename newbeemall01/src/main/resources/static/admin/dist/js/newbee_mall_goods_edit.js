//KindEditor变量
var editor;
$(function () {

    //详情编辑器
    editor = KindEditor.create('textarea[id="editor"]', {
        items: ['source', '|', 'undo', 'redo', '|', 'preview', 'print', 'template', 'code', 'cut', 'copy', 'paste',
            'plainpaste', 'wordpaste', '|', 'justifyleft', 'justifycenter', 'justifyright',
            'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
            'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 'fullscreen', '/',
            'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold',
            'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'multiimage',
            'table', 'hr', 'emoticons', 'baidumap', 'pagebreak',
            'anchor', 'link', 'unlink'],
        uploadJson: '/admin/upload/file',  //指定上传文件请求的url。
        filePostName: 'file'  //指定上传文件参数名称
    });

    //异步上传图片，成功后返回 保存图片位置并通过src显示图片
    new AjaxUpload('#uploadGoodsCoverImg', {
        action: '/admin/upload/file',
        name: 'file',
        autoSubmit: true,
        responseType: "json",
        onSubmit: function (file, extension) {
            if (!(extension && /^(jpg|jpeg|png|gif)$/.test(extension.toLowerCase()))) {
                alert('只支持jpg、png、gif格式的文件！');
                return false;
            }
        },
        onComplete: function (file, response) {
            if (response != null && response.code == 200) {
                $("#goodsCoverImg").attr("src", response.data);
                $("#goodsCoverImg").attr("style", "width: 128px;height: 128px;display:block;");
                return false;
            } else {
                alert("error");
            }
        }
    });
});

/*
保存商品按钮：对商品信息进行验证，通过后显示模态框
* */
$("#confirmButton").click(function () {
    var goodsName =$("#goodsName").val();
    var goodsIntro =$("#goodsIntro").val();
    var originalPrice =$("#originalPrice").val();
    var sellingPrice =$("#sellingPrice").val();
    var stockNum =$("#stockNum").val();
    var tag =$("#tag").val();
    var goodsCategoryId = $("#levelThree option:selected").val();
    var goodsSellStatus =$("input[name='goodsSellStatus']:checked").val();
    var goodsDetailContent = editor.html();

    if (isNull(goodsName)) {
        swal("请输入商品名称",{
            icon: "error"
        });
        return;
    }
    if (!validLength(goodsName,100)) {
        swal("商品名称过长",{
            icon: "error"
        });
        return;
    }
    if (isNull(goodsIntro)) {
        swal("请输入商品简介",{
            icon: "error"
        });
        return;
    }
    if (!validLength(goodsIntro,100)) {
        swal("商品简介过长", {
            icon: "error"
        });
        return;
    }
    if (isNull(originalPrice) || originalPrice<1 ) {
        swal("请输入商品价格或者价格不能低于1元",{
            icon: "error"
        });
        return;
    }
    if (isNull(sellingPrice) || sellingPrice<1 ) {
        swal("请输入商品售卖价或者售卖价不能低于1元",{
            icon: "error"
        });
        return;
    }
    if (isNull(stockNum) || stockNum<0 ) {
        swal("请输入商品存储量或者存储量不能低小于1件",{
            icon: "error"
        });
        return;
    }
    if (isNull(tag)) {
        swal("请输入商品标签",{
            icon: "error"
        });
        return;
    }
    if (!validLength(tag,100)) {
        swal("商品标签过长", {
            icon: "error"
        });
        return;
    }
    if (isNull(goodsSellStatus)) {
        swal("请选择上架状态", {
            icon: "error"
        });
        return;
    }
    if (isNull(goodsCategoryId)) {
        swal("请选择分类", {
            icon: "error"
        });
        return;
    }
    if (isNull(goodsDetailContent)) {
        swal("请输入商品介绍", {
            icon: "error"
        });
        return;
    }
    if (!validLength(goodsDetailContent, 50000)) {
        swal("商品介绍内容过长", {
            icon: "error"
        });
        return;
    }
    $("#goodsModal").modal('show');
});

//返回商品列表 按钮
$("#cancelButton").click(function () {
    window.location.href = '/admin/goods';
});

/*
* 模态框确认按钮
* 获得商品信息，根据goodsId 是否>0，判断是保存还是更新（异步）
* */
$("#saveButton").click(function () {
    var goodsId =$("#goodsId").val();
    var goodsName =$("#goodsName").val();
    var goodsIntro =$("#goodsIntro").val();
    var originalPrice =$("#originalPrice").val();
    var sellingPrice =$("#sellingPrice").val();
    var stockNum =$("#stockNum").val();
    var tag =$("#tag").val();
    var goodsCategoryId = $("#levelThree option:selected").val();
    var goodsSellStatus =$("input[name='goodsSellStatus']:checked").val();
    var goodsDetailContent = editor.html();
    var goodsCoverImg = $("#goodsCoverImg")[0].src;
    if (isNull(goodsCoverImg) || goodsCoverImg.indexOf('img-upload') != -1){
        swal("封面图片不能为空",{
            icon: "error"
        });
        return;
    }

    var url = '/admin/goods/save';
    var selMessage = '保存成功';
    var data = {
        "goodsName": goodsName,
        "goodsIntro": goodsIntro,
        "originalPrice": originalPrice,
        "sellingPrice": sellingPrice,
        "stockNum": stockNum,
        "tag": tag,
        "goodsCategoryId": goodsCategoryId,
        "goodsSellStatus": goodsSellStatus,
        "goodsDetailContent": goodsDetailContent,
        "goodsCoverImg": goodsCoverImg,
        "goodsCarousel": goodsCoverImg
    };

    if (goodsId > 0){
        var url = '/admin/goods/update';
        var selMessage = '修改成功';
        var data = {
            "goodsId": goodsId,
            "goodsName": goodsName,
            "goodsIntro": goodsIntro,
            "originalPrice": originalPrice,
            "sellingPrice": sellingPrice,
            "stockNum": stockNum,
            "tag": tag,
            "goodsCategoryId": goodsCategoryId,
            "goodsSellStatus": goodsSellStatus,
            "goodsDetailContent": goodsDetailContent,
            "goodsCoverImg": goodsCoverImg,
            "goodsCarousel": goodsCoverImg
        }
    }
    console.log(data);
    $.ajax({
        type: 'POST',
        url: url,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (result) {
            if (result.code == 200){
                $("#goodsModal").modal('hide');
                swal({
                    title: selMessage,
                    type: 'success',
                    showCancalButton: 'false',
                    confirmButtonColor: '#1baeae',
                    confirmButtonText: '返回了商品列表',
                    confirmButtonClass: 'btn btn-success',
                    buttonStyling: false

                }).then(function () {
                    window.location.href = '/admin/goods';
                })
            }else {
                $("#goodsModal").modal('hide');
                swal(result.message,{
                    icon: "error"
                });
            }
        },
        error:function () {
            swal("操作失败",{
                icon: "error"
            });
        }
    });
});

// 当select发生改变时，异步上传id值，并返回每级分类下的所以分类id
// Map[“secondLevelCategories”:secondLevelCategories,”thirdLevelCategories”：thirdLevelCategories]
$('#levelOne').on('change', function () {
    $.ajax({
        url: '/admin/categories/categoryByselect?categoryId=' + $(this).val(),
        type: 'GET',
        success: function (result) {
            if (result.code == 200) {
                var levelTwoSelect = '';
                var secondLevelCategories = result.data.secondLevelCategories;
                var length2 = secondLevelCategories.length;
                console.log(length2);
                for (var i = 0; i < length2; i++) {
                    levelTwoSelect += '<option value=\"' + secondLevelCategories[i].categoryId + '\">' + secondLevelCategories[i].categoryName + '</option>';
                }
                $('#levelTwo').html(levelTwoSelect);
                var levelThreeSelect = '';
                var thirdLevelCategories = result.data.thirdLevelCategories;
                var length3 = thirdLevelCategories.length;
                for (var i = 0; i < length3; i++) {
                    levelThreeSelect += '<option value=\"' + thirdLevelCategories[i].categoryId + '\">' + thirdLevelCategories[i].categoryName + '</option>';
                }
                $('#levelThree').html(levelThreeSelect);
            } else {
                swal(result.message, {
                    icon: "error",
                });
            };

        },
        error: function () {
            swal("操作失败", {
                icon: "error",
            });
        }
    });
});

$('#levelTwo').on('change', function () {
    $.ajax({
        url: '/admin/categories/categoryByselect?categoryId=' + $(this).val(),
        type: 'GET',
        success: function (result) {
            if (result.code == 200) {
                var levelThreeSelect = '';
                var thirdLevelCategories = result.data.thirdLevelCategories;
                var length = thirdLevelCategories.length;
                for (var i = 0; i < length; i++) {
                    levelThreeSelect += '<option value=\"' + thirdLevelCategories[i].categoryId + '\">' + thirdLevelCategories[i].categoryName + '</option>';
                }
                $('#levelThree').html(levelThreeSelect);
            } else {
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
});




