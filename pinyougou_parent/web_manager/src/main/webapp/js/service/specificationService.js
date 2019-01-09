//服务层
app.service('specificationService',function($http){
    //读取列表数据绑定到表单中
    this.findAll=function(){
        return $http.get('../specification/findAll.do');
    }

    //分页查询  带条件
    this.search=function(page,rows,searchEntity){
        return $http.post('../specification/search.do?page='+page+'&rows='+rows,searchEntity);
    }

    //修改规格信息
    this.update=function (entity) {
        return $http.post('../specification/update.do',entity);
    }

    //增加规格信息
    this.add=function (entity) {
        return $http.post('../specification/add.do',entity);
    }

    //查询实体  修改的时候要对数据回显  传入id值根据id查询数据
    this.findOne=function (id) {
        return $http.get('../specification/findOne.do?id='+id);
    }

    //批量删除
    this.dele=function (ids) {
        return $http.post('../specification/delete.do?ids='+ids);
    }

    this.selectOptionList = function(){
        return $http.get("../specification/selectOptionList.do");
    }
})
