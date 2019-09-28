/*
		* 消费层
		* */
app.controller('brandController',function ($scope,brandService,$controller) {

    $controller("baseController",{$scope:$scope});//继承

    /*读取数据*/
    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    }


    /*
    * 分页条件查询
    * */
    $scope.searchEntity={};

    $scope.search=function (pageNum,pageSize) {
        brandService.search(pageNum,pageSize,$scope.searchEntity).success(

            function (response) {
                $scope.list=response.rows;  //查询出来的数据集合
                $scope.paginationConf.totalItems = response.total//更新总记录数

                $scope.selectIds=[];//页面刷新后清空，复选框id的集合


            }
        )
    }
    /*
    * 分页查询//已经不用了
    * */
    $scope.findPage=function (page,size) {
        $http.get('../brand/findPage.do?pageNum='+page+'&pageSize='+size).success(
            function (respones) {
                $scope.list=respones.rows; //返回当前页的记录集合

                $scope.paginationConf.totalItems=respones.total;//更新总记录数

            }
        )
    }

    /*
    * 添加品牌
    * */
    $scope.save=function(){

        var obj = null;
        if ($scope.entity.id!=null){
            obj=brandService.updateBrand($scope.entity);
        }else{
            obj=brandService.save($scope.entity);
        }
        obj.success(
            function (respones) {
                if (respones.success){
                    $scope.reloadList();//添加成功后刷新页面
                }else{
                    alert(respones.message)
                }
            }
        ).error(function(data){
            console.log(data);

        })
    }

    /*
    * 修改，byId
    * */
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (respones) {
                $scope.entity=respones;
            }
        )
    }

    /*
    * 删除的方法
    *
    * */


    //实现删除
    $scope.dele=function () {
        brandService.dele($scope.selectIds).success(
            function (respones) {
                if (respones.success){
                    $scope.reloadList();//删除成功后刷新页面
                    $scope.selectIds=[];
                } else{
                    alert($scope.message)
                }
            }
        )
    }
    //显示当前用户名
    $scope.loginName=function(){
        brandService.loginName().success(
            function(response){
                $scope.loginName=response.loginName;
            }
        );
    }

});
