package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ProductsListResponse extends GenericJson
{
    @Key
    private PageInfo pageInfo;
    @Key
    private List<Product> product;
    @Key
    private TokenPagination tokenPagination;
    
    public PageInfo getPageInfo() {
        return this.pageInfo;
    }
    
    public ProductsListResponse setPageInfo(final PageInfo pageInfo) {
        this.pageInfo = pageInfo;
        return this;
    }
    
    public List<Product> getProduct() {
        return this.product;
    }
    
    public ProductsListResponse setProduct(final List<Product> product) {
        this.product = product;
        return this;
    }
    
    public TokenPagination getTokenPagination() {
        return this.tokenPagination;
    }
    
    public ProductsListResponse setTokenPagination(final TokenPagination tokenPagination) {
        this.tokenPagination = tokenPagination;
        return this;
    }
    
    public ProductsListResponse set(final String fieldName, final Object value) {
        return (ProductsListResponse)super.set(fieldName, value);
    }
    
    public ProductsListResponse clone() {
        return (ProductsListResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)Product.class);
    }
}
