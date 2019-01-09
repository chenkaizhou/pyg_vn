//控制层
app.controller('specificationController' ,function($scope,$controller   ,specificationService){
        //继承baseController  父controller于子controller中的$scope域共享
    $controller('baseController',{$scope:$scope});

    //读取列表数据绑定到表单中
    $scope.findAll=function(){
        specificationService.findAll().success(
            function (response) {
                $scope.list=response;
            }
        );
    }

    //定义所搜对象
    $scope.searchEntity={};
    //分页查询  带条件   从父controller中获取page和rows
    $scope.search = function(page,rows){
        specificationService.search(page,rows,$scope.searchEntity).success(
          function (response) {
              $scope.list=response.rows;
              $scope.paginationConf.totalItems = response.total;
          }
        );
    }

    //保存  增加规格  修改规格
    $scope.save = function () {
        //服务层对象
        var serviceObject;
        //判断是有id属性  有的是修改  没有就是增加
        if($scope.entity.specification.id!=null){//如果有ID
            serviceObject=specificationService.update( $scope.entity ); //修改
        }else{
            serviceObject=specificationService.add( $scope.entity  );//增加
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

    //查找实体（根据id查询）   修改的时候数据需要回显
    $scope.findOne=function(id){
        specificationService.findOne(id).success(
            function(response){
                $scope.entity= response;
            }
        );
    }

    //规格删除  批量删除
    $scope.dele=function(){
        //获取选中的复选框
        specificationService.dele($scope.selectIds).success(
            function (response) {
                //重新查询加载
                if(response.success){
                    $scope.reloadList();//重新加载
                } else{
                    alert(response.message);
                }
            }
        );
    }



    //增加规则选项框
    $scope.addTableRow = function(){
        $scope.entity.specificationOptionList.push({});
    }

    //删除规则选项框
    $scope.deleteTableRow = function(index){
        $scope.entity.specificationOptionList.splice(index,1);
    }
});