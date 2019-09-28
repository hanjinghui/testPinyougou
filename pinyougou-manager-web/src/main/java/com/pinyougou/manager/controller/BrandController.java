package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /*
    * 查询所有品牌
    * */
    @RequestMapping("/findAll.do")
    @ResponseBody
    public List<TbBrand> findAll(){

        List<TbBrand> list = brandService.findAll();

        return list;
    }

    /*
    * 分页查询
    * */
    @RequestMapping("/findPage.do")
    @ResponseBody
    public PageResult findPage(int pageNum,int pageSize){

        PageResult pageResult = brandService.findPage(pageNum, pageSize);
        return pageResult;
    }

    /*
    *添加的品牌
    * */
    @RequestMapping("/save")
    @ResponseBody
    public Result save(@RequestBody TbBrand tbBrand){
           Result result = null;
        try {
            brandService.save(tbBrand);
             result = new Result(true, "添加成功");

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result =  new Result(false,"添加失败");
             return result;
        }
    }

    /*
    * 修改品牌信息
    * */
    @RequestMapping("/findOne")
    @ResponseBody
    public TbBrand findOne(long id){
      TbBrand tbBrand = brandService.findOne(id);
      return tbBrand;
    }

    @RequestMapping("/updateBrand.do")
    @ResponseBody
    public Result update( @RequestBody TbBrand tbBrand){

        Result result = null;
        try {
            brandService.updateBrand(tbBrand);
            result = new Result(true, "添加成功");

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result =  new Result(false,"添加失败");
            return result;
        }
    }

    /*
    * 删除数据
    * */
    @RequestMapping("/delect.do")
    @ResponseBody
    public Result delect(long[] ids){

        try {
            if (ids!=null&& ids.length>0){
                brandService.delect(ids);
            }
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }

    }

    /*
    * 分页条件查询
    * */
    @RequestMapping("/search.do")
    @ResponseBody
    public PageResult findAllpage(@RequestBody TbBrand tbBrand, int pageNum, int pageSize){

            PageResult pageResult = brandService.findPage(tbBrand, pageNum, pageSize);
            return pageResult;

    }


    /*
    * select2 下拉列表数据查询
    * */
    @RequestMapping("/selectOptionList.do")
    @ResponseBody
    public List<Map> selcetOptionList(){
        List<Map> maps = brandService.selectOptionList();
        return maps;
    }

}
