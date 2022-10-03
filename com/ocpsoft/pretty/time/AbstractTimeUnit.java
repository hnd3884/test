package com.ocpsoft.pretty.time;

import java.util.ResourceBundle;
import java.util.Locale;

public abstract class AbstractTimeUnit
{
    protected Locale locale;
    protected TimeFormat format;
    protected String name;
    protected String pluralName;
    protected long maxQuantity;
    protected long millisPerUnit;
    
    public AbstractTimeUnit(final Locale locale) {
        this.maxQuantity = 0L;
        this.millisPerUnit = 1L;
        this.locale = locale;
        final ResourceBundle bundle = ResourceBundle.getBundle("com.ocpsoft.pretty.time.i18n.Resources", locale);
        final String pattern = bundle.getString(String.valueOf(this.getResourceKeyPrefix()) + "Pattern");
        final String futurePrefix = bundle.getString(String.valueOf(this.getResourceKeyPrefix()) + "FuturePrefix");
        final String futureSuffix = bundle.getString(String.valueOf(this.getResourceKeyPrefix()) + "FutureSuffix");
        final String pastPrefix = bundle.getString(String.valueOf(this.getResourceKeyPrefix()) + "PastPrefix");
        final String pastSuffix = bundle.getString(String.valueOf(this.getResourceKeyPrefix()) + "PastSuffix");
        this.format = new BasicTimeFormat().setPattern(pattern).setFuturePrefix(futurePrefix).setFutureSuffix(futureSuffix).setPastPrefix(pastPrefix).setPastSuffix(pastSuffix);
        this.name = bundle.getString(String.valueOf(this.getResourceKeyPrefix()) + "Name");
        this.pluralName = bundle.getString(String.valueOf(this.getResourceKeyPrefix()) + "PluralName");
    }
    
    protected abstract String getResourceKeyPrefix();
}
