package com.pinyougou.sellergoods.service.impl;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbSellerMapper sellerMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(GoodsGroup goodsGroup) {
		//添加商品的spu
		goodsGroup.getTbGoods().setAuditStatus("0");
		goodsMapper.insert(goodsGroup.getTbGoods());

		//添加商品的扩展属性
		goodsGroup.getTbGoodsDesc().setGoodsId(goodsGroup.getTbGoods().getId());
		goodsDescMapper.insert(goodsGroup.getTbGoodsDesc());

		saveItemList(goodsGroup);

	}

	private void saveItemList(GoodsGroup goodsGroup) {
		if ("1".equals(goodsGroup.getTbGoods().getIsEnableSpec())) {
			for (TbItem item : goodsGroup.getItemList()) {
				//标题
				String title = goodsGroup.getTbGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());

				Set<String> keySet = specMap.keySet();
				for (String key : keySet) {
					title += " " + specMap.get(key);
				}
				item.setTitle(title);
				setItemValues(goodsGroup, item);
				itemMapper.insert(item);
			}
		} else {
			TbItem item = new TbItem();
			item.setTitle(goodsGroup.getTbGoods().getGoodsName());
			item.setPrice(goodsGroup.getTbGoods().getPrice());
			item.setStatus("1");
			item.setIsDefault("1");
			item.setNum(9999);
			item.setSpec("{}");
			setItemValues(goodsGroup, item);
			itemMapper.insert(item);
		}
	}

	private void setItemValues( GoodsGroup goodsGroup,TbItem item) {
		//商品分类
		item.setCategoryid(goodsGroup.getTbGoods().getCategory3Id());//三级分类ID
		item.setCreateTime(new Date());//创建日期
		item.setUpdateTime(new Date());//更新日期

		item.setGoodsId(goodsGroup.getTbGoods().getId());//商品ID
		item.setSellerId(goodsGroup.getTbGoods().getSellerId());//商家ID

		//分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goodsGroup.getTbGoods().getCategory3Id());
		item.setCategory(itemCat.getName());
		//品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goodsGroup.getTbGoods().getBrandId());
		item.setBrand(brand.getName());
		//商家名称(店铺名称)
		TbSeller seller = sellerMapper.selectByPrimaryKey(goodsGroup.getTbGoods().getSellerId());
		item.setSeller(seller.getNickName());

		//图片
		List<Map> imageList = JSON.parseArray(goodsGroup.getTbGoodsDesc().getItemImages(), Map.class);
		if (imageList.size() > 0) {
			item.setImage((String) imageList.get(0).get("url"));
		}

	}

	/**
	 * 修改
	 */
	@Override
	public void update(GoodsGroup goodsGroup) {
		goodsGroup.getTbGoods().setAuditStatus("0");//设置未申请状态:如果是经过修改的品，需要重新设置状态
		goodsMapper.updateByPrimaryKey(goodsGroup.getTbGoods());//保存商品表
		goodsDescMapper.updateByPrimaryKey(goodsGroup.getTbGoodsDesc());//保存商品扩展表

		//删除原有的 sku 列表数据
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goodsGroup.getTbGoods().getId());
		itemMapper.deleteByExample(example);
		//添加新的 sku 列表数据
		saveItemList(goodsGroup);//插入商品 SKU 列表数据
	}

	/**
	 * 根据ID获取实体
	 *
	 * @param id
	 * @return
	 */
	@Override
	public GoodsGroup findOne(Long id) {
		//创建组合类
		GoodsGroup goodsGroup = new GoodsGroup();

		//得到查询到的tbGoods
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goodsGroup.setTbGoods(tbGoods);

		//得到查询到的tbGoodsDesc
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goodsGroup.setTbGoodsDesc(tbGoodsDesc);

		//查询 SKU 商品列表
		TbItemExample example = new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria =
				example.createCriteria();
		criteria.andGoodsIdEqualTo(id);//查询条件：商品 ID
		List<TbItem> itemList = itemMapper.selectByExample(example);
		goodsGroup.setItemList(itemList);

		return goodsGroup;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}


	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		goods.setIsDelete("0");
		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		if (goods != null) {
			if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
				criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
			}
			if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
				criteria.andAuditStatusEqualTo(goods.getAuditStatus());
			}
			if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
				criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
			}
			if (goods.getCaption() != null && goods.getCaption().length() > 0) {
				criteria.andCaptionLike("%" + goods.getCaption() + "%");
			}
			if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
				criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
			}
			if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
				criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
			}
			if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
				criteria.andIsDeleteEqualTo("0");
			}

		}

		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
		PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
		return pageResult;
	}

	/*
	* 根据id查询商品分类名称
	* */
	public List<TbItemCat> findName(Long categoryId){
		TbItemCatExample example = new TbItemCatExample();
		TbItemCatExample.Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(categoryId);

		List<TbItemCat> itemCatList = itemCatMapper.selectByExample(example);
		return itemCatList;
	}

	/*
	* 修改状态
	* */
	@Override
	public void updateStatus(Long[] ids, String status) {
		for (long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
        }
	}

	//查询审核之后的的集合
	@Override
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] ids, String status) {

		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(status);
		criteria.andGoodsIdIn(Arrays.asList(ids));

		List<TbItem> list = itemMapper.selectByExample(example);

		return list;
	}

}