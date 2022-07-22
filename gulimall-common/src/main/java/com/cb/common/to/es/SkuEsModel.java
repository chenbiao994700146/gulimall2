package com.cb.common.to.es;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * "properties": {
 * <p>
 * <p>
 * "saleCount": {
 * "type": "long"
 * },
 * "hasStock": {
 * "type": "boolean"
 * },
 * "hotScore": {
 * "type": "long"
 * },
 * "brandId": {
 * "type": "long"
 * },
 * "catalogId": {
 * "type": "long"
 * },
 * "brandName": {
 * "type": "keyword",
 * "index": false,
 * "doc_values": false
 * },
 * "brandImg": {
 * "type": "keyword",
 * "index": false,
 * "doc_values": false
 * },
 * "catalogName": {
 * "type": "keyword",
 * "index": false,
 * "doc_values": false
 * },
 * "attrs": {
 * "type": "nested",
 * "properties": {
 * "attrId": {
 * "type": "long"
 * },
 * "attrName": {
 * "type": "keyword",
 * "index": false,
 * "doc_values": false
 * },
 * "attrValue": {
 * "type": "keyword"
 * }
 * }
 * }
 */

@ToString
@Data
public class SkuEsModel {

    private long skuId;
    private long spuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;
    private Long hotScore;
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImg;
    private String catalogName;

    private List<Attrs> attrs;

    @Data
    public static class Attrs {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
