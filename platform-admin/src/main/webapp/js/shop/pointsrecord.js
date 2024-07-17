$(function () {
    $("#jqGrid").Grid({
        url: '../pointsRecord/list',
        colModel: [
            {label: 'id', name: 'id', index: 'id', key: true, hidden: true},
            {label: '会员', name: 'userName', index: 'user_name', width: 80},
            {label: '变动积分', name: 'points', index: 'points', width: 80},
            {label: '来源', name: 'source', index: 'source', width: 80,
                formatter: function (value) {
                    return transIsSource(value);
                }},
            {label: '类型', name: 'type', index: 'type', width: 80,
                formatter: function (value) {
                    return transIsPointsRecord(value);
                }},
            {label: '剩余积分', name: 'totalPoints', index: 'total_points', width: 80},
            {label: '记录时间', name: 'createTime', index: 'create_time', width: 80},
            {label: '备注', name: 'desc', index: 'desc', width: 80}
        ]
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        q: {
            userName: ''
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },

        del: function (event) {
            var ids = getSelectedRows("#jqGrid");
            if (ids == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                Ajax.request({
                    type: "POST",
                    dataType: 'json',
                    url: "../pointsRecord/delete",
                    contentType: "application/json",
                    params: JSON.stringify(ids),
                    successCallback: function (r) {
                        alert('操作成功', function (index) {
                            vm.reload();
                        });
                    }
                });
            });
        },

        reload: function (event) {
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {'name': vm.q.userName},
                page: page
            }).trigger("reloadGrid");
        }
    }
});