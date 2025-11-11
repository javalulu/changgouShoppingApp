package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.ESManagerMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.ESManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ESManagerServiceImpl implements ESManagerService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private ESManagerMapper esManagerMapper;


    // 创建索引库的结构
    @Override
    public void createMappingAndIndex() {
        // 创建索引
        elasticsearchTemplate.createIndex(SkuInfo.class);

        // 创建映射
        elasticsearchTemplate.putMapping(SkuInfo.class);
    }

    // 导入全部数据进入es
    @Override
    public void importAll() {
        // 查询skuList
        List<Sku> skuList = skuFeign.findSkuListBySpuId("all");
        if (skuList == null || skuList.size() <= 0) {
            throw new RuntimeException("当前没有数据被查询到, 无法导入索引库");
        }

        // 将skuList转换成JSON
        String jsonSkuList = JSON.toJSONString(skuList);
        // 将json转为skuInfo
        List<SkuInfo> skuInfos = JSON.parseArray(jsonSkuList, SkuInfo.class);

        for (SkuInfo skuInfo : skuInfos) {
            // 将规格信息转换为map
            Map specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(specMap);
        }

        // 导入索引库
        esManagerMapper.saveAll(skuInfos);
    }

    // 根据spuId查询skuList, 再导入索引库
    @Override
    public void importDataBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);

        if (skuList == null || skuList.size() <= 0) {
            throw new RuntimeException("当前没有数据被查询到, 无法导入索引库");
        }

        // 将集合转为JSON
        String jsonSkuList = JSON.toJSONString(skuList);
        List<SkuInfo> skuInfos = JSON.parseArray(jsonSkuList, SkuInfo.class);

        for (SkuInfo skuInfo : skuInfos) {
            // 将规格信息转换为map
            Map specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(specMap);
        }

        // 导入索引库
        esManagerMapper.saveAll(skuInfos);
    }
}
