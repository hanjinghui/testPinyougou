package com.pinyougou.sellergoods.service;

import entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {

   /*查询品牌列表*/
   public List<TbBrand> findAll();

   /*查找分页列表*/
   public PageResult findPage(int pageNum,int pageSize);

   /*修改品牌信息*/
   void updateBrand(TbBrand tbBrand);
   /*id查找  回显数据*/
   TbBrand findOne(long id);

   /*添加品牌*/
   void save(TbBrand tbBrand);

   /*删除数据*/
   void delect(long[] id);

   /*条件查询*/
   PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);

   /*select2 下拉列表的数据*/
   List<Map> selectOptionList();
}
