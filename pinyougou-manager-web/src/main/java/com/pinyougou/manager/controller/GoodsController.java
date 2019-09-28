package com.pinyougou.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;

import com.pinyougou.pojo.GoodsGroup;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;

import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Arrays;
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

	@Autowired
	private Destination queueSolrDestination;

	@Autowired
	private Destination queueSolrDeleteDestination;

	@Autowired
	private Destination topicPageDestination;

	@Autowired
	private JmsTemplate jmsTemplate;

	//@Reference
	//private ItemSearchService itemSearchService;
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
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);

			//itemSearchService.deleteByGoodsIds(Arrays.asList(ids));

			System.out.println("发送消息。。。");
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
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
		PageResult page1 = goodsService.findPage(tbGoods, page, rows);
		return  page1;
	}

	@Reference
	//private ItemPageService itemPageService;
	/*
	* 修改状态
	* */
	@RequestMapping("/updateStatus.do")
	public Result updateStatus( Long[] ids,String status){

		try {
            goodsService.updateStatus(ids,status);

				if ("1".equals(status)){

					//静态页面的生成
					for (final Long goodsId : ids) {
						jmsTemplate.send(topicPageDestination, new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								return session.createTextMessage(goodsId+"");
							}
						});
					}


					List<TbItem> list = goodsService.findItemListByGoodsIdandStatus(ids, status);
					if (list.size()>0) {
						//itemSearchService.importList(list);
						//将集合转换成json字符串
						final String jsonString = JSON.toJSONString(list);
						jmsTemplate.send(queueSolrDestination, new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								return session.createTextMessage(jsonString);
							}
						});
					}

				}

				return new Result(true, "通过审核");
			} catch (Exception e) {
				e.printStackTrace();
				return new Result(false, "审核失败");
			}
	}

	@RequestMapping("/html.do")
	public  void genItemHtml(Long goodsId){

		//itemPageService.genItemHtml(goodsId);

	}

}
