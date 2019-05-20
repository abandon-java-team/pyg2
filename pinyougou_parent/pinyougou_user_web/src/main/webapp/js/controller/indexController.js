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
});