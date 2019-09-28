//服务层
app.service('userService',function($http){

    //用户注册
    this.add=function (entity,smsCode) {
        return $http.post("../user/add.do?smsCode="+smsCode,entity);
    }

    //生成验证码
    this.createSmsCode=function(phone) {
        return $http.get("../user/getCode.do?phone="+phone)
    }

});
