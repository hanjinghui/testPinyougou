 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,uploadService,itemCatService,$location,typeTemplateService) {

	$controller('baseController', {$scope: $scope});//继承

	//读取列表数据绑定到表单中
	$scope.findAll = function () {
		goodsService.findAll().success(
			function (response) {
				$scope.list = response;
			}
		);
	}

	//分页
	$scope.findPage = function (page, rows) {
		goodsService.findPage(page, rows).success(
			function (response) {
				$scope.list = response.rows;
				$scope.paginationConf.totalItems = response.total;//更新总记录数
			}
		);
	}

	//查询实体 
	$scope.findOne = function () {

	 var id = $location.search()['id'];

	 	if (id==null){
	 		return;
		}

		goodsService.findOne(id).success(
			function (response) {
				$scope.entity = response;
				//向富文本编辑器添加商品介绍
				editor.html($scope.entity.tbGoodsDesc.introduction);

				//显示图片列表
				$scope.entity.tbGoodsDesc.itemImages=
					JSON.parse($scope.entity.tbGoodsDesc.itemImages);

				//显示扩展属性
				$scope.entity.tbGoodsDesc.customAttributeItems=
					JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems);

				//显示规格属性
				$scope.entity.tbGoodsDesc.specificationItems=JSON.parse($scope.entity.tbGoodsDesc.specificationItems);

				//SKU 列表规格列转换
				for( var i=0;i<$scope.entity.itemList.length;i++ ){
					$scope.entity.itemList[i].spec =
						JSON.parse( $scope.entity.itemList[i].spec);
				}
			}
		);
	}

	//保存
	$scope.entity = {tbgoods: {}, tbGoodsDesc: {itemImages: []}};//定义页面实体结构
	$scope.save = function () {
		$scope.entity.tbGoodsDesc.introduction = editor.html()
		var serviceObject;//服务层对象
		var  a= $scope.entity.tbGoods;
		if ($scope.entity.tbGoods.id) {//如果有ID
			serviceObject = goodsService.update($scope.entity); //修改
		} else {
			serviceObject = goodsService.add($scope.entity);//增加
		}
		serviceObject.success(
			function (response) {
				if (response.success) {
					alert("保存成功")
					$scope.entity = {tbgoods: {}, tbGoodsDesc: {itemImages: []}};
					editor.html('');//清空富文本编辑器
					//重新查询 
					location.href="goods.html";//跳转到商品列表页
				} else {
					alert("保存失败！");
				}
			}
		);
	}


	//批量删除 
	$scope.dele = function () {
		//获取选中的复选框			
		goodsService.dele($scope.selectIds).success(
			function (response) {
				if (response.success) {
					$scope.reloadList();//刷新列表
					$scope.selectIds = [];
				}
			}
		);
	}

	$scope.searchEntity = {};//定义搜索对象

	//搜索
	$scope.search = function (page, rows) {
		goodsService.search(page, rows, $scope.searchEntity).success(
			function (response) {
				$scope.list = response.rows;
				$scope.paginationConf.totalItems = response.total;//更新总记录数
			}
		);
	}


	/*$scope.entity = {tbgoods: {}, tbGoodsDesc: {itemImages: []}};//定义页面实体结构
	//添加商品

	$scope.save = function () {

		$scope.entity.tbGoodsDesc.introduction = editor.html()

		goodsService.add($scope.entity).success(
			function (respones) {
				if (respones.success) {
					alert("保存成功")
					$scope.entity = {tbgoods: {}, tbGoodsDesc: {itemImages: []}};
					editor.html('');//清空富文本编辑器
				} else {
					alert("保存失败！")
				}
			}
		)
	}*/

	/**
	 * 上传图片
	 */
	$scope.uploadFile = function () {
		uploadService.uploadFile().success(
			function (response) {
				if (response.success) {//如果上传成功，取出 url
					$scope.image_entity.url = response.message;//设置文件地址
				} else {
					alert(response.message);
				}
			}).error(function () {

			alert("上传发生错误");
		});
	};


	//添加图片列表
	$scope.add_image_entity = function () {

		$scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity);
	}

	//列表中移除图片
	$scope.remove_image_entity = function (index) {
		$scope.entity.tbGoodsDesc.itemImages.splice(index, 1);
	}

	//查询显示一级属性列表
	$scope.selectByParentId = function () {
		itemCatService.findByParentId(0).success(
			function (respones) {
				$scope.itemsCatList = respones
			}
		)
	}

	//查询显示二级属性列表
	$scope.$watch("entity.tbGoods.category1Id", function (newValue, oldValue) {
		if ( newValue == undefined){
			return;
		}
		itemCatService.findByParentId(newValue).success(
			function (respones) {
				$scope.itemsCatList2 = respones;
			}
		)
	})

	//查询三级属性的列表
	$scope.$watch("entity.tbGoods.category2Id", function (newValue, oldValue) {
		if ( newValue == undefined){
			return;
		}
		itemCatService.findByParentId(newValue).success(
			function (respones) {
				$scope.itemsCatList3 = respones;
			}
		)
	})

	//查询模板的id
	$scope.$watch("entity.tbGoods.category3Id", function (newValue, oldValue) {

		if ( newValue == undefined){
			return;
		}
		itemCatService.findOne(newValue).success(
			function (respones) {
				$scope.entity.tbGoods.typeTemplateId = respones.typeId;
			}
		)
	})

	//添加品牌名称
	$scope.$watch("entity.tbGoods.typeTemplateId", function (newValue, oldValue) {
		if ( newValue == undefined){
			return;
		}
		typeTemplateService.findOne(newValue).success(
			function (response) {
				$scope.typeTemplate=response//根据id查询出来的typeTemplate对象

				$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);//品牌列表
				 if($location.search()['id']==null){
					$scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems)//获得扩展属性的jeson数据
				}

			}

		)

		//查询specIds 的集合
		typeTemplateService.findSpecList(newValue).success(
			function (response) {
				$scope.specList=response;
			}
		)
	})


	/*  错误区*/
	$scope.entity={ tbGoodsDesc:{itemImages:[],specificationItems:[]}  };

	$scope.updateSpecAttribute=function($event,name,value){
		var object= $scope.searchObjectByKey(
			$scope.entity.tbGoodsDesc.specificationItems ,'attributeName', name);
		if(object!=null){
			if($event.target.checked ){
				object.attributeValue.push(value);
			}else{//取消勾选
				object.attributeValue.splice( object.attributeValue.indexOf(value ) ,1);//移除选项
				//如果选项都取消了，将此条记录移除
				if(object.attributeValue.length==0){
					$scope.entity.tbGoodsDesc.specificationItems.splice(
						$scope.entity.tbGoodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else{
			$scope.entity.tbGoodsDesc.specificationItems.push(
				{"attributeName":name,"attributeValue":[value]});
		}
	}
	/*  错误区*/

	//创建 SKU 列表
	$scope.createItemList=function() {
		$scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];
		//初始
		var items = $scope.entity.tbGoodsDesc.specificationItems;

		for (var i = 0; i < items.length; i++) {
			$scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, 				items[i].attributeValue);
		}


	};
	//添加列值
	addColumn = function (list, columnName, conlumnValues) {
		var newList = [];//新的集合
		for (var i = 0; i < list.length; i++) {
			var oldRow = list[i];
			for (var j = 0; j < conlumnValues.length; j++) {
				var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
				newRow.spec[columnName] = conlumnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}


	//查询所有的商品分类
	$scope.itemCatList = [];
	$scope.findItemCatList=function () {
		itemCatService.findAll().success(
			function (response) {

				for (var i=0; i<response.length; i++){
					var a = response[i].id;
					$scope.itemCatList[response[i].id]=response[i].name;
					var b = response[i].name;
				}
			}
		)
	}


	//根据规格名称和选项名称返回是否被勾选
	$scope.checkAttributeValue=function(specName,optionName){
		var items= $scope.entity.tbGoodsDesc.specificationItems;
		var object= $scope.searchObjectByKey(items,'attributeName',specName);
		if(object==null){
			return false;
		}else{
			if(object.attributeValue.indexOf(optionName)>=0){
				return true;
			}else{
				return false;
			}
		}
	}
})