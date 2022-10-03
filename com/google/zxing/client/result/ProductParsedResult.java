package com.google.zxing.client.result;

public final class ProductParsedResult extends ParsedResult
{
    private final String productID;
    private final String normalizedProductID;
    
    ProductParsedResult(final String productID) {
        this(productID, productID);
    }
    
    ProductParsedResult(final String productID, final String normalizedProductID) {
        super(ParsedResultType.PRODUCT);
        this.productID = productID;
        this.normalizedProductID = normalizedProductID;
    }
    
    public String getProductID() {
        return this.productID;
    }
    
    public String getNormalizedProductID() {
        return this.normalizedProductID;
    }
    
    @Override
    public String getDisplayResult() {
        return this.productID;
    }
}
