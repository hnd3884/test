package com.octo.captcha.service;

import com.octo.captcha.engine.CaptchaEngine;

public interface ManageableCaptchaService extends CaptchaService
{
    String getCaptchaEngineClass();
    
    void setCaptchaEngineClass(final String p0) throws IllegalArgumentException;
    
    CaptchaEngine getEngine();
    
    void setCaptchaEngine(final CaptchaEngine p0);
    
    int getMinGuarantedStorageDelayInSeconds();
    
    void setMinGuarantedStorageDelayInSeconds(final int p0);
    
    long getNumberOfGeneratedCaptchas();
    
    long getNumberOfCorrectResponses();
    
    long getNumberOfUncorrectResponses();
    
    int getCaptchaStoreSize();
    
    int getNumberOfGarbageCollectableCaptchas();
    
    long getNumberOfGarbageCollectedCaptcha();
    
    void setCaptchaStoreMaxSize(final int p0);
    
    int getCaptchaStoreMaxSize();
    
    void garbageCollectCaptchaStore();
    
    void emptyCaptchaStore();
    
    int getCaptchaStoreSizeBeforeGarbageCollection();
    
    void setCaptchaStoreSizeBeforeGarbageCollection(final int p0);
}
