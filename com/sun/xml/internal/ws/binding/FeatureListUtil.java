package com.sun.xml.internal.ws.binding;

import javax.xml.ws.WebServiceException;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import javax.xml.ws.WebServiceFeature;

public class FeatureListUtil
{
    @NotNull
    public static WebServiceFeatureList mergeList(final WebServiceFeatureList... lists) {
        final WebServiceFeatureList result = new WebServiceFeatureList();
        for (final WebServiceFeatureList list : lists) {
            result.addAll(list);
        }
        return result;
    }
    
    @Nullable
    public static <F extends WebServiceFeature> F mergeFeature(@NotNull final Class<F> featureType, @Nullable final WebServiceFeatureList list1, @Nullable final WebServiceFeatureList list2) throws WebServiceException {
        final F feature1 = (F)((list1 != null) ? list1.get(featureType) : null);
        final F feature2 = (F)((list2 != null) ? list2.get(featureType) : null);
        if (feature1 == null) {
            return feature2;
        }
        if (feature2 == null) {
            return feature1;
        }
        if (feature1.equals(feature2)) {
            return feature1;
        }
        throw new WebServiceException(feature1 + ", " + feature2);
    }
    
    public static boolean isFeatureEnabled(@NotNull final Class<? extends WebServiceFeature> featureType, @Nullable final WebServiceFeatureList list1, @Nullable final WebServiceFeatureList list2) throws WebServiceException {
        final WebServiceFeature mergedFeature = mergeFeature(featureType, list1, list2);
        return mergedFeature != null && mergedFeature.isEnabled();
    }
}
