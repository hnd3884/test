package com.sun.xml.internal.ws.api;

import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import java.lang.annotation.Annotation;

public class WebServiceFeatureFactory
{
    public static WSFeatureList getWSFeatureList(final Iterable<Annotation> ann) {
        final WebServiceFeatureList list = new WebServiceFeatureList();
        list.parseAnnotations(ann);
        return list;
    }
    
    public static WebServiceFeature getWebServiceFeature(final Annotation ann) {
        return WebServiceFeatureList.getFeature(ann);
    }
}
