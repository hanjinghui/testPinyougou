package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;
import javax.annotation.Resource;
import java.util.List;

@Resource
public interface CartService {

    /**
     * 商品添加到购物车
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num );


    /**
     * 根据用户名在redis中查询购物车的集合
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);


    /**
     * 将购物车信息添加到redis中
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
   public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);

}
