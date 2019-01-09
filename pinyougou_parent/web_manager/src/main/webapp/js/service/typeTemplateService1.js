app.service('typeTemplateService',function ($http) {
    //分页查询
    this.search=function (page, rows, searchEntity) {
        return $http.post('../typeTemplate/search.do?page='+page+'&rows='+rows,searchEntity);
    }

    //查询实体
    this.findOne=function(id){
        return $http.get('../typeTemplate/findOne.do?id='+id);
    }
    //增加
    this.add=function(entity){
        return  $http.post('../typeTemplate/add.do',entity );
    }
    //修改
    this.update=function(entity){
        return  $http.post('../typeTemplate/update.do',entity );
    }
    //删除
    this.dele=function(ids){
        return $http.get('../typeTemplate/delete.do?ids='+ids);
    }

})