app.controller("indexController", function ($scope, loginService,orderCenterService) {

    //初始化数据
    $scope.initData = function () {
        loginService.loginName().success(function (response) {
            $scope.loginName = response.loginName;
        })
    };
    //查询订单详情
    $scope.findOrderByStatus=function (status) {
      orderCenterService.findOrderByStatus(status).success(function (response) {
          $scope.orderList=response;
      })
    }

    //订单状态
    //状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
    $scope.status = ['','未付款', '已付款', '未发货', '已发货','交易成功','交易关闭','待评价'];
});