$(function () {
    let goodsId = getQueryString("goodsId");
    let url = '../product/list';
    if (goodsId) {
        url += '?goodsId=' + goodsId;
    }
    $("#jqGrid").Grid({
        url: url,
        colModel: [
            {label: 'id', name: 'id', index: 'id', key: true, hidden: true},
            {label: '商品', name: 'goodsName', index: 'goods_id', width: 120},
            {label: '商品规格', name: 'specificationValue', index: 'goods_specification_ids', width: 100},
            {label: '商品序列号', name: 'goodsSn', index: 'goods_sn', width: 80},
            {label: '商品库存', name: 'goodsNumber', index: 'goods_number', width: 80},
            {label: '零售价格(元)', name: 'retailPrice', index: 'retail_price', width: 80},
            {label: '市场价格(元)', name: 'marketPrice', index: 'market_price', width: 80}]
    });
});

let vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        product: {},
        ruleValidate: {
            // goodsId: [
            //     {required: true, message: '商品不能为空', trigger: 'blur'}
            // ],
            // goodsSpecificationIds: [
            //     {required: true, message: '商品规格不能为空', trigger: 'blur'}
            // ],
            // goodsNumber: [
            //     {required: true, message: '商品库存不能为空', trigger: 'blur'}
            // ],
            // retailPrice: [
            //     {required: true, message: '零售价格不能为空', trigger: 'blur'}
            // ],
            // marketPrice: [
            //     {required: true, message: '市场价格不能为空', trigger: 'blur'}
            // ]
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
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.product = {goodsId: null, specificationValue: null, goodsNumber: 99999, retailPrice: 0, marketPrice: 0};
            vm.getGoodss();
            vm.type = 'add';
        },
        update: function (event) {
            let id = getSelectedRow("#jqGrid");
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";
            vm.type = 'update';

            vm.getInfo(id)
        },
        changeGoods: function (opt) {
            let goodsId = opt.value;
            if(!goodsId)return;

            Ajax.request({
                url: "../goods/info/" + goodsId,
                async: true,
                successCallback: function (r) {
                    if (vm.type == 'add') {
                        vm.product.goodsSn = r.goods.goodsSn;
                        vm.product.goodsNumber = r.goods.goodsNumber;
                        vm.product.retailPrice = r.goods.retailPrice;
                        vm.product.marketPrice = r.goods.marketPrice;
                    }
                    Ajax.request({
                        url: "../goodsspecification/queryAll?goodsId=" + goodsId + "&specificationId=1&goodsSpecificationId="+vm.product.goodsSpecificationIds,
                        async: true,
                        successCallback: function (r) {
                            vm.guiges = r.list;
                        }
                    });
                }
            });
        },
        saveOrUpdate: function (event) {
            //校验必填字段非空
            if (vm.product.goodsId == null || vm.product.goodsId == '') {
                alert('商品不能为空');
                return;
            }
            if (vm.product.goodsSpecificationIds == null || vm.product.goodsSpecificationIds == '') {
                alert('商品规格不能为空');
                return;
            }
            if (vm.product.goodsNumber == null || vm.product.goodsNumber == '') {
                alert('商品库存不能为空');
                return;
            }
            if (vm.product.retailPrice == null || vm.product.retailPrice == '') {
                alert('零售价格不能为空');
                return;
            }
            if (vm.product.marketPrice == null || vm.product.marketPrice == '') {
                alert('市场价格不能为空');
                return;
            }


            let url = vm.product.id == null ? "../product/save" : "../product/update";
            Ajax.request({
                type: "POST",
                url: url,
                contentType: "application/json",
                params: JSON.stringify(vm.product),
                successCallback: function (r) {
                    alert('操作成功', function (index) {
                        vm.reload();
                    });
                }
            });


        },
        del: function (event) {
            let ids = getSelectedRows("#jqGrid");
            if (ids == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                Ajax.request({
                    type: "POST",
                    url: "../product/delete",
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
        getInfo: function (id) {
            vm.attribute = [];
            Ajax.request({
                url: "../product/info/" + id,
                async: true,
                successCallback: function (r) {
                    vm.product = r.product;
                    vm.getGoodss();
                }
            });
        },
        reload: function (event) {
            vm.showList = true;
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