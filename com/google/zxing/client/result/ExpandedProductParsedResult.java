package com.google.zxing.client.result;

import java.util.Map;

public final class ExpandedProductParsedResult extends ParsedResult
{
    public static final String KILOGRAM = "KG";
    public static final String POUND = "LB";
    private final String productID;
    private final String sscc;
    private final String lotNumber;
    private final String productionDate;
    private final String packagingDate;
    private final String bestBeforeDate;
    private final String expirationDate;
    private final String weight;
    private final String weightType;
    private final String weightIncrement;
    private final String price;
    private final String priceIncrement;
    private final String priceCurrency;
    private final Map<String, String> uncommonAIs;
    
    public ExpandedProductParsedResult(final String productID, final String sscc, final String lotNumber, final String productionDate, final String packagingDate, final String bestBeforeDate, final String expirationDate, final String weight, final String weightType, final String weightIncrement, final String price, final String priceIncrement, final String priceCurrency, final Map<String, String> uncommonAIs) {
        super(ParsedResultType.PRODUCT);
        this.productID = productID;
        this.sscc = sscc;
        this.lotNumber = lotNumber;
        this.productionDate = productionDate;
        this.packagingDate = packagingDate;
        this.bestBeforeDate = bestBeforeDate;
        this.expirationDate = expirationDate;
        this.weight = weight;
        this.weightType = weightType;
        this.weightIncrement = weightIncrement;
        this.price = price;
        this.priceIncrement = priceIncrement;
        this.priceCurrency = priceCurrency;
        this.uncommonAIs = uncommonAIs;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ExpandedProductParsedResult)) {
            return false;
        }
        final ExpandedProductParsedResult other = (ExpandedProductParsedResult)o;
        return equalsOrNull(this.productID, other.productID) && equalsOrNull(this.sscc, other.sscc) && equalsOrNull(this.lotNumber, other.lotNumber) && equalsOrNull(this.productionDate, other.productionDate) && equalsOrNull(this.bestBeforeDate, other.bestBeforeDate) && equalsOrNull(this.expirationDate, other.expirationDate) && equalsOrNull(this.weight, other.weight) && equalsOrNull(this.weightType, other.weightType) && equalsOrNull(this.weightIncrement, other.weightIncrement) && equalsOrNull(this.price, other.price) && equalsOrNull(this.priceIncrement, other.priceIncrement) && equalsOrNull(this.priceCurrency, other.priceCurrency) && equalsOrNull(this.uncommonAIs, other.uncommonAIs);
    }
    
    private static boolean equalsOrNull(final Object o1, final Object o2) {
        return (o1 == null) ? (o2 == null) : o1.equals(o2);
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash ^= hashNotNull(this.productID);
        hash ^= hashNotNull(this.sscc);
        hash ^= hashNotNull(this.lotNumber);
        hash ^= hashNotNull(this.productionDate);
        hash ^= hashNotNull(this.bestBeforeDate);
        hash ^= hashNotNull(this.expirationDate);
        hash ^= hashNotNull(this.weight);
        hash ^= hashNotNull(this.weightType);
        hash ^= hashNotNull(this.weightIncrement);
        hash ^= hashNotNull(this.price);
        hash ^= hashNotNull(this.priceIncrement);
        hash ^= hashNotNull(this.priceCurrency);
        hash ^= hashNotNull(this.uncommonAIs);
        return hash;
    }
    
    private static int hashNotNull(final Object o) {
        return (o == null) ? 0 : o.hashCode();
    }
    
    public String getProductID() {
        return this.productID;
    }
    
    public String getSscc() {
        return this.sscc;
    }
    
    public String getLotNumber() {
        return this.lotNumber;
    }
    
    public String getProductionDate() {
        return this.productionDate;
    }
    
    public String getPackagingDate() {
        return this.packagingDate;
    }
    
    public String getBestBeforeDate() {
        return this.bestBeforeDate;
    }
    
    public String getExpirationDate() {
        return this.expirationDate;
    }
    
    public String getWeight() {
        return this.weight;
    }
    
    public String getWeightType() {
        return this.weightType;
    }
    
    public String getWeightIncrement() {
        return this.weightIncrement;
    }
    
    public String getPrice() {
        return this.price;
    }
    
    public String getPriceIncrement() {
        return this.priceIncrement;
    }
    
    public String getPriceCurrency() {
        return this.priceCurrency;
    }
    
    public Map<String, String> getUncommonAIs() {
        return this.uncommonAIs;
    }
    
    @Override
    public String getDisplayResult() {
        return String.valueOf(this.productID);
    }
}
