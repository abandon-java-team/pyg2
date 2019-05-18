//购物车服务层
app.service('cartService', function ($http) {
    //购物车列表
    this.findCartList = function () {
        return $http.get('cart/findCartList.do');
    };

    //添加商品到购物车
    this.addGoodsToCartList = function (itemId, num) {
        return $http.get('cart/addGoodsToCartList.do?itemId=' + itemId + '&num=' + num);
    };

    //从收藏夹添加商品到购物车
    this.addGoodsToCartListFromCollect = function (itemId) {
        return $http.get('collect/addGoodsToCartListFromCollect.do?itemId=' + itemId)
    };

    //获取地址列表
    this.findAddressList = function () {
        return $http.get('address/findListByLoginUser.do');
    };

    this.submitOrder = function (order) {
        return $http.post("order/add.do", order);
    };

    this.findCollect = function () {
        return $http.get('collect/findCollect.do');
    };

    this.addGoodsToCollect = function (itemId) {
        return $http.get('collect/addGoodsToCollect.do?itemId='+itemId);
    };

    this.findByItemId = function (itemId) {
        return $http.get('collect/findByItemId.do?itemId=' + itemId);
    }
});
