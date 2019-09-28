 //控制层 
app.controller('userController' ,function($scope,$controller,userService) {

	$controller('baseController', {$scope: $scope});//继承

	//用户注册
	$scope.add=function () {

		if ($scope.entity.password!=$scope.password){
			alert("密码输入不一致，请重新输入。。")
			return;
		}
		userService.add($scope.entity,$scope.smsCode).success(
			function (response) {
				if (response.success){
					alert(response.message)
				}
			}
		)
	}

	//生成验证码

	$scope.createSmsCode=function () {
		userService.createSmsCode($scope.entity.phone).success(
		)
	}
})