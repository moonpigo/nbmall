/*
$(function () {
    $("#keyword").keypress(function (e){
        var key = e.which(); //e.which是按键的键码
        if ( key == 13){  //Enter键
            var keyword = this.val();
            if (keyword && keyword != '') {
                // alert("nice1")
                window.location.href = '/search?keyword=' + keyword;
            }
        }
    });
});

function search() {
    var keyword = $("#keyword").val();
    if (keyword && keyword != ''){
        // alert("nice2")
        window.location.href = "/search?keyword="+keyword;
    }
}*/
$(function () {
    $('#keyword').keypress(function (e) {
        var key = e.which; //e.which是按键的值
        if (key == 13) {
            var q = $(this).val();
            if (q && q != '') {
                window.location.href = '/search?keyword=' + q;
            }
        }
    });
});

function search() {
    var q = $('#keyword').val();
    if (q && q != '') {
        window.location.href = '/search?keyword=' + q;
    }
}