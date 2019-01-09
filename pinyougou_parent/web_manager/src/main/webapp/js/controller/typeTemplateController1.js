app.controller('typeTemplateController',function ($scope,$controller,brandService,specificationService,typeTemplateService){
    //继承父controller
    $controller('baseController',{$scope:$scope});

    $scope.searchEntity={};//定义搜索对象
    //分页查询  搜索
    $scope.search=function (page, rows) {
        typeTemplateService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    //定义品牌回显对象
    $scope.brandList={date:[]};
    //查询关联品牌信息
    $scope.findBrandList = function(){
        brandService.selectOptionList().success(function(response){
            $scope.brandList = {data:response};
        });
    }

    //定义规格回显对象
    $scope.specList={date:[]};
    //查询规格信息
    $scope.findSpecList = function(){
        specificationService.selectOptionList().success(function(response){
            $scope.specList = {data:response};
        });
    }

    //查询实体
    $scope.findOne=function(id){
        typeTemplateService.findOne(id).success(
            function(response){
                $scope.entity= response;
                // eval()   JSON.parse();
                $scope.entity.brandIds = JSON.parse($scope.entity.brandIds);

                $scope.entity.specIds = JSON.parse($scope.entity.specIds);

                $scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);
            }
        );
    }

    //批量删除
    $scope.dele=function(){
        //获取选中的复选框
        typeTemplateService.dele( $scope.selectIds ).success(
            function(response){
                if(response.success){
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    //保存
    $scope.save=function(){
        var serviceObject;//服务层对象
        if($scope.entity.id!=null){//如果有ID
            serviceObject=typeTemplateService.update( $scope.entity ); //修改
        }else{
            serviceObject=typeTemplateService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                    $scope.reloadList();//重新加载
                }else{
                    alert(response.message);
                }
            }
        );
    }


    //给扩展属性添加行
    $scope.entity={customAttributeItems:[]};
    $scope.addTableRow = function(){
        $scope.entity.customAttributeItems.push({});
    }

    $scope.deleteTableRow = function(index){
        $scope.entity.customAttributeItems.splice(index,1);
    }

})