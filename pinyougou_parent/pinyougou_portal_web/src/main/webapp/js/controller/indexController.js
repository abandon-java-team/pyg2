app.controller("indexController", function ($scope, contentService) {

    //声明广告数据:[分类id:[数据列表]]
    $scope.contentList = [];

    //加载所有广告
    $scope.findAllContent = function () {
        //加载轮播图广告
        contentService.findByCategoryId(1).success(function (response) {
            $scope.contentList[1] = response;
        })
    }

    $scope.keywords = "";

    $scope.search = function () {
        if ($scope.keywords == "") {
            alert("请先输入搜索关键字");
            return;
        }
        window.location.href = "http://localhost:8084/search.html#?keywords=" + $scope.keywords;
    }
});