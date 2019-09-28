app.service('loginService',function($http) {

    //获取用户登陆的用户名
    this.showName=function () {
        return $http("../login/name.do")
    }

})