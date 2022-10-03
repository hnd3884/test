package com.octo.captcha;

import java.io.Serializable;

public interface Captcha extends Serializable
{
    String getQuestion();
    
    Object getChallenge();
    
    Boolean validateResponse(final Object p0);
    
    void disposeChallenge();
    
    Boolean hasGetChalengeBeenCalled();
}
