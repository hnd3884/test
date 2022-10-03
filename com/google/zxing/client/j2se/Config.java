package com.google.zxing.client.j2se;

import com.google.zxing.DecodeHintType;
import java.util.Map;

final class Config
{
    private Map<DecodeHintType, ?> hints;
    private boolean tryHarder;
    private boolean pureBarcode;
    private boolean productsOnly;
    private boolean dumpResults;
    private boolean dumpBlackPoint;
    private boolean multi;
    private boolean brief;
    private boolean recursive;
    private int[] crop;
    private int threads;
    
    Config() {
        this.threads = 1;
    }
    
    Map<DecodeHintType, ?> getHints() {
        return this.hints;
    }
    
    void setHints(final Map<DecodeHintType, ?> hints) {
        this.hints = hints;
    }
    
    boolean isTryHarder() {
        return this.tryHarder;
    }
    
    void setTryHarder(final boolean tryHarder) {
        this.tryHarder = tryHarder;
    }
    
    boolean isPureBarcode() {
        return this.pureBarcode;
    }
    
    void setPureBarcode(final boolean pureBarcode) {
        this.pureBarcode = pureBarcode;
    }
    
    boolean isProductsOnly() {
        return this.productsOnly;
    }
    
    void setProductsOnly(final boolean productsOnly) {
        this.productsOnly = productsOnly;
    }
    
    boolean isDumpResults() {
        return this.dumpResults;
    }
    
    void setDumpResults(final boolean dumpResults) {
        this.dumpResults = dumpResults;
    }
    
    boolean isDumpBlackPoint() {
        return this.dumpBlackPoint;
    }
    
    void setDumpBlackPoint(final boolean dumpBlackPoint) {
        this.dumpBlackPoint = dumpBlackPoint;
    }
    
    boolean isMulti() {
        return this.multi;
    }
    
    void setMulti(final boolean multi) {
        this.multi = multi;
    }
    
    boolean isBrief() {
        return this.brief;
    }
    
    void setBrief(final boolean brief) {
        this.brief = brief;
    }
    
    boolean isRecursive() {
        return this.recursive;
    }
    
    void setRecursive(final boolean recursive) {
        this.recursive = recursive;
    }
    
    int[] getCrop() {
        return this.crop;
    }
    
    void setCrop(final int[] crop) {
        this.crop = crop;
    }
    
    int getThreads() {
        return this.threads;
    }
    
    void setThreads(final int threads) {
        this.threads = threads;
    }
}
