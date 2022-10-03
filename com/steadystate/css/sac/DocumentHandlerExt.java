package com.steadystate.css.sac;

import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.DocumentHandler;

public interface DocumentHandlerExt extends DocumentHandler
{
    void charset(final String p0, final Locator p1) throws CSSException;
    
    void importStyle(final String p0, final SACMediaList p1, final String p2, final Locator p3) throws CSSException;
    
    void ignorableAtRule(final String p0, final Locator p1) throws CSSException;
    
    void startFontFace(final Locator p0) throws CSSException;
    
    void startPage(final String p0, final String p1, final Locator p2) throws CSSException;
    
    void startMedia(final SACMediaList p0, final Locator p1) throws CSSException;
    
    void startSelector(final SelectorList p0, final Locator p1) throws CSSException;
    
    void property(final String p0, final LexicalUnit p1, final boolean p2, final Locator p3);
}
