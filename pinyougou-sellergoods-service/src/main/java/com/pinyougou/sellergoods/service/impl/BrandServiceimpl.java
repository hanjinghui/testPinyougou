package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbBrandExample;
import entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceimpl implements BrandService {

    /*
    * 查询所有品牌
    * */
    @Autowired
    private TbBrandMapper tbBrandMapper;
    @Override
    public List<TbBrand> findAll() {

        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(null);

        return tbBrands;
    }


    /*
    * 查询分页列表
    * */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        if(pageNum<=0 & pageSize<=0){
            pageNum=1;
            pageSize=10;
        }
        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(null);

           return new PageResult(page.getTotal(),page.getResult());
    }



    /*
    * 修改品牌信息
    * */

    @Override
    public TbBrand findOne(long id) {

        TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(id);
        return tbBrand;
    }

    @Override
    public void updateBrand(TbBrand tbBrand ) {
        tbBrandMapper.updateByPrimaryKey(tbBrand);
    }



    /*
    *  添加品牌
    * */
    @Override
    public void save(TbBrand tbBrand) {
        tbBrandMapper.insert(tbBrand);
    }


    /*
    * 删除品牌
    * */
    @Override
    public void delect(long[] ids) {
        if (ids!=null&& ids.length>0){
            for (long id : ids) {
                tbBrandMapper.deleteByPrimaryKey(id);
            }
        }
    }

    /*
    * 分页条件查询
    * */
    @Override
    public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum,pageSize);  //在方法执行之前要执行的方法

        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();

        if(tbBrand!=null){
            if (tbBrand.getName()!=null && tbBrand.getName().length()>0){
                criteria.andNameLike("%"+tbBrand.getName()+"%");
            }
            if (tbBrand.getFirstChar()!= null && tbBrand.getFirstChar().length()>0){
                criteria.andFirstCharLike("%"+tbBrand.getFirstChar()+"%");
            }
        }

        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(example);

        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    /*
    * select2 shuju
    * */
    @Override
    public List<Map> selectOptionList() {

        List<Map> maps = tbBrandMapper.selectOptionList();
        return maps;
    }
}
