package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.GoodsGroup;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			

		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goodsGroup
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody GoodsGroup goodsGroup){

		//获取商家的id
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		goodsGroup.getTbGoods().setSellerId(sellerId);//设置商家 ID

		goodsGroup.getTbGoods().setIsDelete("0");
		try {
			goodsService.add(goodsGroup);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goodsGroup
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody GoodsGroup goodsGroup){

		//校验是否是当前商家的 id
		GoodsGroup goods2 = goodsService.findOne(goodsGroup.getTbGoods().getId());

		//获取当前登录的商家 ID
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		//如果传递过来的商家 ID 并不是当前登录的用户的 ID,则属于非法操作
		if(!goods2.getTbGoods().getSellerId().equals(sellerId)
				||  !goodsGroup.getTbGoods().getSellerId().equals(sellerId) ){
			return new Result(false, "操作非法");
		}

		try {
			goodsService.update(goodsGroup);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public GoodsGroup findOne(Long id){

		goodsService.findOne(id);

		return goodsService.findOne(id);
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods tbGoods, int page, int rows  ){

		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		tbGoods.setSellerId(sellerId);

		PageResult page1 = goodsService.findPage(tbGoods, page, rows);
		return  page1;
	}

}
