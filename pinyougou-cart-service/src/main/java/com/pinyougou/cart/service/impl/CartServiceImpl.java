package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加商品到购物车
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //1.根据ItemID查询商品明细SKU的对象
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        if (tbItem==null){
            throw new RuntimeException("该商品不存在");
        }

        if (!tbItem.getStatus().equals("1")){
            throw new RuntimeException("商品状态无效！");
        }

        //2.根据SKU对象得到商家ID
            String sellerId = tbItem.getSellerId();

        //3.根据商家ID在购物车列表中查询购物车对象
        Cart cart = seachCartBysellerId(cartList, sellerId);

        //4.如果购物车列表中不存在该商家的购物车

                if (cart==null){
                //4.1 创建一个新的购物车对象
                 cart = new Cart();
                //4.2 将新的购物车对象添加到购物车列表中
                cart.setSellerId(sellerId);
                cart.setSellerName(tbItem.getSeller());
                //创建订单详情
                TbOrderItem tbOrderItem = creatOrderItem(tbItem, num);

                //创建一个订单详情列表
                List orderItemList = new ArrayList<>();
                orderItemList.add(tbOrderItem);

                //将商品详情列表添加到购物车中
                cart.setOrderItemList(orderItemList);
                //将购物车添加到购物车列表中
                cartList.add(cart);
            }else{
                    //5.如果购物车列表中存在该商家的购物车，判断改购物车中是否有该商品明细
                    TbOrderItem tbOrderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
                    //5.1 如果不存在，创建新的购物车明细对象，并添加到该购物车的明细列表中

                    if (tbOrderItem==null){
                         tbOrderItem = creatOrderItem(tbItem, num);
                        cart.getOrderItemList().add(tbOrderItem);
                    }else{
                        //5.2 如果存在，在原有的数量上添加数量 ,并且更新金额
                        tbOrderItem.setNum(tbOrderItem.getNum()+num);
                        //算出购物车的中商品的小计
                        tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getNum()*tbOrderItem.getPrice().doubleValue()));

                        //操作商品详情后数量小于0.则删除购物车
                        if(tbOrderItem.getNum()<=0){
                            cart.getOrderItemList().remove(tbOrderItem);//移除商品详情
                        }

                        //如果购物车集合的商品数量小于等于零，那就删除购物车
                        if(cart.getOrderItemList().size()==0){
                            cartList.remove(cart);
                        }
                    }
            }

        return cartList;
    }

    /**
     * 将购物车添加到redis中
     * @param username
     * @param cartList
     */

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("把购物车信息添加到redia中");

        redisTemplate.boundHashOps("cartList").put(username,cartList);

    }

    /**
     * 根据用户名在redia中查询购物车列表
     * @param username
     * @return
     */

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中获取购物车的集合");
        List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps("cartList").get(username);
        if (cartList==null){
            cartList=new ArrayList<Cart>();
        }
        return cartList;
    }

    /**
     * 根据商家在购物车列表中查询相应的购物车
     * @param cartList
     * @param sellerId
     * @return
     */
    public Cart seachCartBysellerId(List<Cart> cartList,String sellerId){

        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }


    /**
     * 创建商品的订单详情对象
     * @param tbItem
     * @param num
     * @return
     */
    private TbOrderItem creatOrderItem(TbItem tbItem,Integer num){

        TbOrderItem tbOrderItem = new TbOrderItem();

        tbOrderItem.setGoodsId(tbItem.getGoodsId());
        tbOrderItem.setItemId(tbItem.getId());
        tbOrderItem.setPicPath(tbItem.getImage());
        tbOrderItem.setSellerId(tbItem.getSellerId());
        tbOrderItem.setTitle(tbItem.getTitle());
        tbOrderItem.setPrice(tbItem.getPrice());
        tbOrderItem.setNum(num);
        tbOrderItem.setTotalFee(new BigDecimal(tbItem.getPrice().doubleValue()*num));
        return tbOrderItem;
    }


    /**
     * 根据商品的id在上商品详情列表中查询是否存在该商品
     * @param tbOrderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> tbOrderItemList,Long itemId){

        for (TbOrderItem tbOrderItem : tbOrderItemList) {
            if (tbOrderItem.getItemId().longValue()==itemId){
                return tbOrderItem;
            }
        }
        return null;
    }

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {

        //合并购物车

        for (Cart cart : cartList2) {
            for(TbOrderItem orderItem:cart.getOrderItemList()){
                cartList1=
                        addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
            }
        }

        return cartList1;
    }
}
