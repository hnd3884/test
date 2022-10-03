package com.zoho.mickey.api;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javax.xml.transform.TransformerFactory;

public class TransformerFactoryUtil
{
    public static TransformerFactory newInstance() {
        return TransformerFactory.newInstance(TransformerFactoryImpl.class.getName(), Thread.currentThread().getContextClassLoader());
    }
}
