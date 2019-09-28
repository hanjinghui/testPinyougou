/*
		* 服务层
		* */
		app.service('brandService',function ($http) {
			this.findAll=function () {
				return $http.get('../brand/findAll.do')
			}

			this.search=function (pageNum, pageSize,searchEntity) {
				return $http.post('../brand/search.do?pageNum='+pageNum+'&pageSize='+pageSize,searchEntity)
			}

			this.save=function (entity) {
				return $http.post('../brand/save.do',entity)
			}

			this.updateBrand=function (entity) {
				return $http.post('../brand/updateBrand.do',entity)
			}

			this.findOne=function (id) {
				return $http.post('../brand/findOne.do?id='+id)
			}
			this.dele=function (selectIds) {
				return $http.post("../brand/delect.do?ids="+selectIds)
			}

			//select2 信息数据
			this.selectOptionList=function () {
				return $http.post("../brand/selectOptionList.do")
			}


            //获取用户名
            this.loginName=function () {
                return $http.post("../brand/name.do")
            }
		});
