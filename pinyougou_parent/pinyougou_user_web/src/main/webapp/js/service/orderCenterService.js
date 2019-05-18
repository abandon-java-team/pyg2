//用户订单查询服务层
app.service("orderCenterService",function ($http) {
   this.findOrderByStatus=function (status) {
     return $http.get("../orderCenter/orderInfo.do?status="+status)
   }
});