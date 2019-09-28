package com.pinyougou.pojo;

import java.io.Serializable;
import java.util.List;

/*
* 商品组合实体类
* */
public class GoodsGroup implements Serializable {

    private TbGoods tbGoods;//商品的基本信息；
    private TbGoodsDesc tbGoodsDesc;//商品的扩展属性
    private List<TbItem> itemList;//SKU列表

    public TbGoods getTbGoods() {
        return tbGoods;
    }

    public void setTbGoods(TbGoods tbGoods) {
        this.tbGoods = tbGoods;
    }

    public TbGoodsDesc getTbGoodsDesc() {
        return tbGoodsDesc;
    }

    public void setTbGoodsDesc(TbGoodsDesc tbGoodsDesc) {
        this.tbGoodsDesc = tbGoodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
