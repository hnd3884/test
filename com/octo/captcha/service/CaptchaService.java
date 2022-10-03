package com.octo.captcha.service;

import java.util.Locale;

public interface CaptchaService
{
    Object getChallengeForID(final String p0) throws CaptchaServiceException;
    
    Object getChallengeForID(final String p0, final Locale p1) throws CaptchaServiceException;
    
    String getQuestionForID(final String p0) throws CaptchaServiceException;
    
    String getQuestionForID(final String p0, final Locale p1) throws CaptchaServiceException;
    
    Boolean validateResponseForID(final String p0, final Object p1) throws CaptchaServiceException;
}
