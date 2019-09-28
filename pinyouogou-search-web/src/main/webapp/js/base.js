// 定义模块:
var app = angular.module("pinyougou",[]);

//定义高亮显示
app.filter('trustHtml',['$sce',function($sce){
    return function(data){
        return $sce.trustAsHtml(data);
    }
}]);