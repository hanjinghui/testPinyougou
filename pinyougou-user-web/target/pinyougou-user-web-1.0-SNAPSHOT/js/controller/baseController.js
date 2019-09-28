
app.controller("baseController",function ($scope) {

    $scope.reloadList=function(){
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);

    }

    $scope.paginationConf={
        currentPage: 1,//当前页数
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    }

    //获取id选中的数组
    $scope.selectIds=[];//定义一个存放id的数组

    $scope.updateSelection=function($event,id){

        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            var index = $scope.selectIds.indexOf(id);//获取数组中id的坐标
            $scope.selectIds.splice(index,1);//根据坐标删除id
        }
    }

    /*
    * json 数据转换成字符串
    * */
    $scope.jsonToString=function(jsonString,key){
        var json=JSON.parse(jsonString);//将 json 字符串转换为 json 对象
        var value="";
        for(var i=0;i<json.length;i++){
            if(i>0){
                value+=","
            }
            value+=json[i][key];
        }
        return value;
    }

    //从集合中按照 key 查询对象
    $scope.searchObjectByKey=function(list,key,keyValue){
        for(var i=0;i<list.length;i++){
            if(list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }
})