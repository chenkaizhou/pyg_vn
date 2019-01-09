package com.xyxy.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xyxy.core.dao.good.BrandDao;
import com.xyxy.core.dao.good.GoodsDao;
import com.xyxy.core.dao.good.GoodsDescDao;
import com.xyxy.core.dao.item.ItemCatDao;
import com.xyxy.core.dao.item.ItemDao;
import com.xyxy.core.dao.seller.SellerDao;
import com.xyxy.core.pojo.entity.GoodsEntity;
import com.xyxy.core.pojo.entity.PageResult;
import com.xyxy.core.pojo.good.Brand;
import com.xyxy.core.pojo.good.Goods;
import com.xyxy.core.pojo.good.GoodsDesc;
import com.xyxy.core.pojo.good.GoodsQuery;
import com.xyxy.core.pojo.item.Item;
import com.xyxy.core.pojo.item.ItemCat;
import com.xyxy.core.pojo.item.ItemQuery;
import com.xyxy.core.pojo.seller.Seller;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.*;
import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private JmsTemplate jmsTemplate;

    //商品上架使用
    @Autowired
    private ActiveMQTopic topicPageAndSolrDestination;

    //商品下架使用
    @Autowired
    private ActiveMQQueue queueSolrDeleteDestination;

    @Override
    //查询所有商品   分页  搜索
    public PageResult findPage( Goods goods,Integer page,Integer rows) {
        PageHelper.startPage(page,rows);
        GoodsQuery query = new GoodsQuery();
        GoodsQuery.Criteria criteria = query.createCriteria();
        criteria.andIsDeleteIsNull();
        if(goods!=null){
            if(goods.getGoodsName()!=null && !"".equals(goods.getGoodsName())){
                criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
            }
            if (goods.getAuditStatus()!=null && !"".equals(goods.getAuditStatus())){
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getSellerId()!=null && !"admin".equals(goods.getSellerId())){
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
        }
        Page<Goods> goodsList = (Page<Goods>)goodsDao.selectByExample(query);
        return new PageResult(goodsList.getTotal(),goodsList.getResult());
    }

    @Override
    public void add(GoodsEntity goodsEntity) {
        //1.保存商品对象
        //刚添加的商品状态默认是0未审核
        goodsEntity.getGoods().setAuditStatus("0");
        goodsDao.insertSelective(goodsEntity.getGoods());

        //2.保存商品详情    商品详情表实际上就是商品表切割出来的一部分
        //商品主键最为商品详情的外键
        Long id = goodsEntity.getGoods().getId();//获取商品的主键
        goodsEntity.getGoodsDesc().setGoodsId(id);
        goodsDescDao.insertSelective(goodsEntity.getGoodsDesc());

        //3.保存库存信息  外键关联  保存前需要对一些数据进行初始化
        insertItem(goodsEntity);

    }

    @Override
    //根据id查询商品以及所对的所有信息
    public GoodsEntity findOne(Long  id) {
        GoodsEntity goodsEntity = new GoodsEntity();
        //获取商品基本信息
        Goods goods = goodsDao.selectByPrimaryKey(id);
        //获取商品详情
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        //获取商品的库存
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(query);
        //封装到实体对象中返回
        goodsEntity.setGoods(goods);
        goodsEntity.setGoodsDesc(goodsDesc);
        goodsEntity.setItemList(itemList);

        //获取分类的名称
        Map<String, String> itemCatName = new HashMap<>();
        ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
        ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
        ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());
        itemCatName.put("itemCat1",itemCat1.getName());
        itemCatName.put("itemCat2",itemCat2.getName());
        itemCatName.put("itemCat3",itemCat3.getName());
        goodsEntity.setItemCatMap(itemCatName);

        //获取品牌名称
        Brand brand = brandDao.selectByPrimaryKey(goods.getBrandId());
        goodsEntity.setBrandName(brand.getName());

        return goodsEntity;
    }

    @Override
    //商品修改
    public void update(GoodsEntity goodsEntity) {
        //修改商品对象
        goodsDao.updateByPrimaryKeySelective(goodsEntity.getGoods());
        //修改商品的详情对象
        goodsDescDao.updateByPrimaryKeySelective(goodsEntity.getGoodsDesc());
        //根据商品主键id删除之前所有的库存   重新添加数据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsEntity.getGoods().getId());
        itemDao.deleteByExample(query);
        //添加库存集合数据
        insertItem(goodsEntity);
    }

    @Override
    //删除商品  不是正真的删除数据  只是修改状态
    public void delete(final Long id) {
        /*
        * 1.到数据库中对商品进行逻辑删除
        * */
        Goods goods = new Goods();
        goods.setIsDelete("1");
        goods.setId(id);
        goodsDao.updateByPrimaryKeySelective(goods);

        /*
        * 2.将商品id作为消息发送给消息服务器
        * */
        jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                return textMessage;
            }
        });

    }

    @Override
    public void commit(Long[] ids) {
        if(ids!=null){
            for (Long id : ids) {
                Goods goods = new Goods();
                goods.setAuditStatus("4");
                goods.setId(id);
                goodsDao.updateByPrimaryKeySelective(goods);
            }
        }
    }

    @Override
    //修改商品状态  是否上架
    public void updateStatus(final Long id, String status) {
        /*
        * 1.根据商品id导数据库中将商品的上架状态改变
        * */

        //根据商品id修改商品对象状态码
        Goods goods = new Goods();
        goods.setId(id);
        goods.setAuditStatus(status);
        goodsDao.updateByPrimaryKeySelective(goods);

        //根据商品id修改库存集合对象的状态码
        Item item = new Item();
        item.setStatus(status);
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        itemDao.updateByExampleSelective(item,query);

        /*
        * 2.将 商品的id作为消息发送给消息服务器
        * */
        try {
            if ("1".equals(status)) {
                jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                        return textMessage;
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //保存库存数据  定义一个方法
    public void insertItem(GoodsEntity goodsEntity){
        //判断是否勾选了库存复选框  也就是规格
        if("1".equals(goodsEntity.getGoods().getIsEnableSpec())){
            //勾选了规格复选框，有库存数据
            //要获取到一条条的库存信息  需要遍历  保存库存信息
            for (Item item : goodsEntity.getItemList()){
                if(goodsEntity.getItemList()!=null){
                    //初始化标题  由商品名+规格组成
                    String title = goodsEntity.getGoods().getGoodsName();
                    //从库存对象中取出前端传入的json规格字符串
                    String specJsonStr = item.getSpec();
                    //将json格式的字符串转换成对象   保存在map集合中
                    Map specMap = JSON.parseObject(specJsonStr,Map.class);
                    //获取map中的值
                    Collection<String> values = specMap.values();
                    for (String value : values) {
                        title += ""+value;
                    }
                    //初始化标题
                    item.setTitle(title);

                    //调用方法来初始化一些前台没有编写的数据
                    setItemValue(goodsEntity, item);

                    //插入到数据库中
                    itemDao.insertSelective(item);
                }
            }
        }else {
            //没有勾选复选框，没有库存信息数据  但是我们需要初始化一条，不然前端有可能报错
            Item item = new Item();
            //价格
            item.setPrice(new BigDecimal("999999"));
            //库存量
            item.setNum(0);
            //初始化规格
            item.setSpec("{}");
            //标题
            item.setTitle(goodsEntity.getGoods().getGoodsName());

            //调用方法，设置库存信息对象属性值
            setItemValue(goodsEntity,item);

            //保存库存信息
            itemDao.insertSelective(item);
        }


    }






    // 使用goodsEntity实体类中的数据初始化, item库存对象中的属性值
    private Item setItemValue(GoodsEntity goodsEntity,Item item){
        //商品id   与商品关联  外键
        item.setGoodsId(goodsEntity.getGoods().getId());
        //创建时间
        item.setCreateTime(new Date());
        //更新时间
        item.setUpdateTime(new Date());
        //库存状态 默认为0  未审核
        item.setStatus("0");
        //分类id 库存使用商品的第三级分类作为库存分类
        item.setCategoryid(goodsEntity.getGoods().getCategory3Id());
        //分类名称(第三级分类名称)   获取第三级分类id所对应的值  分类表中获取
        ItemCat itemCat = itemCatDao.selectByPrimaryKey(goodsEntity.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //品牌名称   前台是传进的是品牌主键id（商品里）  根据id找到品牌名
        Brand brand = brandDao.selectByPrimaryKey(goodsEntity.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //卖家名称  前台登录后后台获取到卖家主键id封装到了Goods里
        Seller seller = sellerDao.selectByPrimaryKey(goodsEntity.getGoods().getSellerId());
        item.setSeller(seller.getName());
        //卖家id
        item.setSellerId(goodsEntity.getGoods().getSellerId());
        //示例图片    商品描述表是商品表切割出来的一部分   ItemImages  图片存储路径(键值对  数组形式)
        String itemImg = goodsEntity.getGoodsDesc().getItemImages();
        List<Map> maps = JSON.parseArray(itemImg,Map.class);
        if(maps!=null && maps.size()>0){
            //获取图片路径 获取第一张图
            String url = String.valueOf(maps.get(0).get("url"));
            item.setImage(url);
        }
        return item;
    }

}
