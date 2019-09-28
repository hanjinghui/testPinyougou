package com.pinyougou.search;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

   public  Map<String,Object> search(Map searchMap);

   /*
   * 更新索引库
   * */
   void importList(List list);

   /*
   * 删除时更新索引库
   * */
   public void deleteByGoodsIds(List goodsIdList);
}
