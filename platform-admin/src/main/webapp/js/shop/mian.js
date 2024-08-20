$(function () {
    let goodsId = getQueryString("goodsId");
    let url = '../product/profitsList';
    if (goodsId) {
        url += '?goodsId=' + goodsId;
    }
    $("#jqGrid").Grid({
        url: url,
        colModel: [
            {label: 'id', name: 'id', index: 'id', key: true, hidden: true},
            {label: '商品', name: 'goodsName', index: 'goodsName', width: 160},
            {label: '商品规格', name: 'specificationValue', index: 'specificationValue', width: 60},
            {label: '零售价格(元)', name: 'retailPrice', index: 'retail_price', width: 40},
            {label: '加盟商利润(元)', name: 'profits', index: 'profits', width: 40}
        ]
    });
});

let vm = new Vue({
    el: '#rrapp',
    data: {
        title: null,
        product: {},
        ruleValidate: {
        },
        q: {
            goodsName: ''
        },
        goodss: [],
        attribute: [],
        colors: [],
        guiges: [],
        weights: [],
        type: ''
    },
    methods: {
        query: function () {
            vm.reload();
        },
        reload: function (event) {
            let page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {'goodsName': vm.q.goodsName},
                page: page
            }).trigger("reloadGrid");
            vm.handleReset('formValidate');
        },
        handleSubmit: function (name) {
            handleSubmitValidate(this, name, function () {
                vm.saveOrUpdate()
            });
        },
        handleReset: function (name) {
            handleResetForm(this, name);
        },
        getGoodss: function () {
            Ajax.request({
                url: "../goods/queryAll/",
                async: true,
                successCallback: function (r) {
                    vm.goodss = r.list;
                }
            });
        }
    }
});