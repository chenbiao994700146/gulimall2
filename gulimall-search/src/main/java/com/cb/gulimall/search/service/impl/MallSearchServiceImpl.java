package com.cb.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cb.common.to.es.SkuEsModel;
import com.cb.common.utils.R;
import com.cb.gulimall.search.config.GulimallElasticSearchConfig;
import com.cb.gulimall.search.constant.EsConstant;
import com.cb.gulimall.search.feign.ProductFeignService;
import com.cb.gulimall.search.service.MallSearchService;
import com.cb.gulimall.search.service.ProductSaveService;
import com.cb.gulimall.search.vo.AttrResponseVo;
import com.cb.gulimall.search.vo.BrandVo;
import com.cb.gulimall.search.vo.SearchParam;
import com.cb.gulimall.search.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Resource
    private RestHighLevelClient client;

    @Resource
    private ProductFeignService productFeignService;

    //去es进行检索
    @Override
    public SearchResult search(SearchParam param) {
        //1.动态构建出查询需要的DSL语句
        SearchResult result=null;

        //1、准备检索请求
        SearchRequest searchRequest = bulidSearchRequest(param);





        try {

            //2、执行检索请求
            SearchResponse response = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

            //3、分析响应数据封装成我们需要的格式
            result = bulidSearchResult(response, param);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 准备结果
     * @param response
     * @return
     */
    private SearchResult bulidSearchResult(SearchResponse response,SearchParam param) {
        SearchResult result= new SearchResult();

        //1、返回的所有查询到的商品
        SearchHits hits = response.getHits();
        List<SkuEsModel> esModels=new ArrayList<>();
        if(hits.getHits()!=null&&hits.getHits().length>0){
            for (SearchHit hit : hits.getHits()) {

                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if(param.getKeyword()!=null){
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String string = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(string);
                }

                esModels.add(esModel);
            }
        }

        result.setProducts(esModels); ;
        //2、当前所有商品涉及到的所有属性信息
       List<SearchResult.AttrVo> attrVos=new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //1、得到属性的id
            long attrId = bucket.getKeyAsNumber().longValue();
            //2、得到属性的名字
            String attrName = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            //3、的到属性的所有值
            List<String> attrValues = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(item -> {
                String keyAsString = ((Terms.Bucket) item).getKeyAsString();
                return keyAsString;
            }).collect(Collectors.toList());

            attrVo.setAttrId(attrId);
            attrVo.setAttrName(attrName);
            attrVo.setAttrValue(attrValues);


            attrVos.add(attrVo);
        }


        result.setAttrs(attrVos);
        //3、当前所有商品涉及到的所有品牌信息
        List<SearchResult.BrandVo> brandVos=new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //1、得到品牌的id
            long brandId = bucket.getKeyAsNumber().longValue();

            //2、得到品牌的名字
            String brandName = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();

            //3、得到品牌的图片
            String brandImg = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();

            brandVo.setBrandId(brandId);
            brandVo.setBrandImg(brandImg);
            brandVo.setBrandName(brandName);
            brandVos.add(brandVo);
        }


        result.setBrands(brandVos);
        //4、当前所有商品涉及到的所有分类信息
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");

        List<SearchResult.CatalogVo> catalogVos=new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();

            //得到分类id
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));

            //得到分类名字
            ParsedStringTerms catalog_namg_agg = bucket.getAggregations().get("catalog_namg_agg");
            String catalog_namg = catalog_namg_agg.getBuckets().get(0).getKeyAsString();

            catalogVo.setCatalogName(catalog_namg);
            catalogVos.add(catalogVo);

        }
        result.setCatalogs(catalogVos);
        //--------以上聚合信息查询得来
        //5、分页信息-页码
        result.setPageNum(param.getPageNum());
        //5、分页信息-总记录数
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        //5、分页信息-总页码  --计算得来
        int totalPages= (int)total%EsConstant.PRODUCT_PAGESIZE==0?(int)total/EsConstant.PRODUCT_PAGESIZE:(int)(total/EsConstant.PRODUCT_PAGESIZE+1);
        result.setTotalPages(totalPages);
        List<Integer> pageNavs=new ArrayList<>();
        for(int i=1;i<=totalPages;i++){
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);


        //6、构建面包先导航数据
        if(param.getAttrs()!=null&&param.getAttrs().size()>0){
            List<SearchResult.NavVo> collect = param.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();

                String[] s=attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                result.getAttrIds().add(Long.parseLong(s[0]));
                if(r.getCode()==0){
                    AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    navVo.setNavName(data.getAttrName());
                }else{
                    navVo.setNavName(s[0]);
                }

                //2、取消了这个面包屑以后，我们要跳转到那个地方，将请求的地址url里面置空
                String replace = replaceQueryString(param, attr,"attrs");
                navVo.setLink("http://search.gulimall.com/list.html?"+replace);
                return navVo;
            }).collect(Collectors.toList());

            result.setNavs(collect);
        }

        //品牌，分类
        if(param.getBrandId()!=null&&param.getBrandId().size()>0){
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            //TODO 远程查询所有屏
            R r = productFeignService.brandsInfo(param.getBrandId());
            if(r.getCode()==0){
                List<BrandVo> brand = r.getData("brand", new TypeReference<List<BrandVo>>() {
                });
                StringBuffer buffer = new StringBuffer();
                String replace ="";
                for (BrandVo brandVo : brand) {
                  buffer.append(brandVo.getBrandName()+";");
                     replace = replaceQueryString(param, brandVo.getBrandId()+"","brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setNavName("品牌");
                navVo.setLink("http://search.gulimall.com/list.html?"+replace);
            }

            navs.add(navVo);
        }

        return  result;
    }

    private String replaceQueryString(SearchParam param, String value,String key) {
        String encode =null;
        try {
             encode = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return param.get_queryString().replace("&"+key+"=" + encode, "");
    }

    /**
     * 准备检索请求
     * @return
     */
    private SearchRequest bulidSearchRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();//构建DSL语句

        /**
         *  查询：模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存）
         */
        //1、构建bool-query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //1.1、must -模糊匹配
        if (!StringUtils.isEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }

        //1.2 bool-filter -按照三级分类id查询
        if(param.getCatalog3Id()!=null){
            boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
        }

        //1.2 bool-filter -按照品牌id查询
        if(param.getBrandId()!=null&&param.getBrandId().size()>0){
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }

        //1.2 bool-filter -按照所有指定的属性进行查询
        if(param.getAttrs()!=null&&param.getAttrs().size()>0){

            for(String attrStr :param.getAttrs()){
                //attrs=1_5寸：8寸&attrs=2_16G:8G
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                String[] s=attrStr.split("_");
                String attrId=s[0]; //检索的属性id
                String[] attrValues = s[1].split(":"); //这个属性的检索用的值
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));

                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }

        }


        //1.2 bool-filter -按照库存是否有，进行查询
        if(param.getHasStock()!=null){
            boolQuery.filter(QueryBuilders.termQuery("hasStock",param.getHasStock()==1));
        }



        //1.2 bool-filter -按照价格区间进行查询
        if(!StringUtils.isEmpty(param.getSkuPrice())){
            //1_500/_500/500_
            RangeQueryBuilder rangeQuery=QueryBuilders.rangeQuery("skuPrice");

            String[] s = param.getSkuPrice().split("_");
            if(s.length==2){
                //区间
                rangeQuery.gte(s[0]).lte(s[1]);
            }else if(s.length==1){
                if(param.getSkuPrice().startsWith("_")){
                    rangeQuery.lte(s[0]);
                }

                if(param.getSkuPrice().endsWith("_")){
                    rangeQuery.gte(s[0]);
                }
            }

            boolQuery.filter(rangeQuery);
        }

        sourceBuilder.query(boolQuery);
        /**
         * 排序，分页，高亮
         */
        //2.1、排序
        if(!StringUtils.isEmpty(param.getSort())){
            //sort=hostScore_ase/desc
            String sort = param.getSort();
            String[] s = sort.split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0], order);
        }
        //2.2 分页
        sourceBuilder.from((param.getPageNum()-1)*EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);


        //2.3、高亮
        if(!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("</b>");
            sourceBuilder.highlighter(builder);
        }
        /**
         * 聚合分析
         */
        //1.品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);

        //品牌聚合的子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));

        //TODO 聚合品牌信息
        sourceBuilder.aggregation(brand_agg);
        //2、分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_namg_agg").field("catalogName").size(1));


        //TODO 聚合分类信息
        sourceBuilder.aggregation(catalog_agg);


        //3、属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        //聚合出当前所有的attrid
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(1);

       //聚合出当前attr_id对应的名字
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        //聚合出当前attr_id对应的值
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(20));

        attr_agg.subAggregation(attr_id_agg);

        //TODO 聚合属性信息
        sourceBuilder.aggregation(attr_agg);

        String s = sourceBuilder.toString();
        System.out.println("S++++++:"+s);


        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);

        return searchRequest;
    }
}
