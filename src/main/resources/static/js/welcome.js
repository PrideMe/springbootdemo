//欢迎信息
layer.config({
    extend: ['extend/layer.ext.js', 'skin/moon/style.css'],
    skin: 'layer-ext-moon'
});

layer.ready(function () {
    var html = $('#welcome-template').html();
    $('a.viewlog').click(function () {
        logs();
        return false;
    });

    $('#pay-qrcode').on('click',function () {
        var html=$(this).html();
        parent.layer.open({
            title: false,
            type: 1,
            closeBtn:false,
            shadeClose:true,
            area: ['600px', 'auto'],
            content: html
        });
    });

    function logs() {
        parent.layer.open({
            title: '初见倾心，再见动情',
            type: 1,
            area: ['700px', 'auto'],
            content: html,
            btn: ['确定', '取消']
        });
    }

    if ($(this).width() > 768) {
        parent.layer.open({
            title: "<span class='label label-danger'>广告</span> Admui 通用管理系统快速开发框架",
            type: 1,
            area: ["700px", "auto"],
            content: '<img src="http://cdn.admui.com/site/img/screenshot-1.jpg" width="700" height="360">',
            btn: ["Admui官网", "关闭"],
            yes: function (d, c) {
                parent.window.location.href = "http://www.admui.com/"
            }
        })
    }

});