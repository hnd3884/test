package com.octo.captcha.text;

import com.octo.captcha.Captcha;

public abstract class TextCaptcha implements Captcha
{
    private Boolean hasChallengeBeenCalled;
    protected String question;
    protected String challenge;
    
    protected TextCaptcha(final String question, final String challenge) {
        this.hasChallengeBeenCalled = Boolean.FALSE;
        this.challenge = challenge;
        this.question = question;
    }
    
    public String getQuestion() {
        return this.question;
    }
    
    public Object getChallenge() {
        return this.getTextChallenge();
    }
    
    public String getTextChallenge() {
        this.hasChallengeBeenCalled = Boolean.TRUE;
        return this.challenge;
    }
    
    public void disposeChallenge() {
        this.challenge = null;
    }
    
    public Boolean hasGetChalengeBeenCalled() {
        return this.hasChallengeBeenCalled;
    }
}
