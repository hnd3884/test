package org.bouncycastle.jce.provider;

import org.bouncycastle.asn1.x509.ReasonFlags;

class ReasonsMask
{
    private int _reasons;
    static final ReasonsMask allReasons;
    
    ReasonsMask(final ReasonFlags reasonFlags) {
        this._reasons = reasonFlags.intValue();
    }
    
    private ReasonsMask(final int reasons) {
        this._reasons = reasons;
    }
    
    ReasonsMask() {
        this(0);
    }
    
    void addReasons(final ReasonsMask reasonsMask) {
        this._reasons |= reasonsMask.getReasons();
    }
    
    boolean isAllReasons() {
        return this._reasons == ReasonsMask.allReasons._reasons;
    }
    
    ReasonsMask intersect(final ReasonsMask reasonsMask) {
        final ReasonsMask reasonsMask2 = new ReasonsMask();
        reasonsMask2.addReasons(new ReasonsMask(this._reasons & reasonsMask.getReasons()));
        return reasonsMask2;
    }
    
    boolean hasNewReasons(final ReasonsMask reasonsMask) {
        return (this._reasons | (reasonsMask.getReasons() ^ this._reasons)) != 0x0;
    }
    
    int getReasons() {
        return this._reasons;
    }
    
    static {
        allReasons = new ReasonsMask(33023);
    }
}
