package ar.com.fernandospr.wns.model.builders;

import ar.com.fernandospr.wns.model.WnsImage;
import ar.com.fernandospr.wns.model.WnsText;
import java.util.ArrayList;
import ar.com.fernandospr.wns.model.WnsBinding;
import ar.com.fernandospr.wns.model.WnsVisual;

public abstract class WnsAbstractBuilder<T extends WnsAbstractBuilder<T>>
{
    protected abstract T getThis();
    
    protected abstract WnsVisual getVisual();
    
    protected abstract WnsBinding getBinding();
    
    public T visualVersion(final Integer version) {
        this.getVisual().version = version;
        return this.getThis();
    }
    
    public T visualLang(final String lang) {
        this.getVisual().lang = lang;
        return this.getThis();
    }
    
    public T visualBaseUri(final String baseUri) {
        this.getVisual().baseUri = baseUri;
        return this.getThis();
    }
    
    public T visualBranding(final String branding) {
        this.getVisual().branding = branding;
        return this.getThis();
    }
    
    public T visualAddImageQuery(final Boolean addImageQuery) {
        this.getVisual().addImageQuery = addImageQuery;
        return this.getThis();
    }
    
    public T bindingFallback(final String fallback) {
        this.getBinding().fallback = fallback;
        return this.getThis();
    }
    
    public T bindingLang(final String lang) {
        this.getBinding().lang = lang;
        return this.getThis();
    }
    
    public T bindingBaseUri(final String baseUri) {
        this.getBinding().baseUri = baseUri;
        return this.getThis();
    }
    
    public T bindingBranding(final String branding) {
        this.getBinding().branding = branding;
        return this.getThis();
    }
    
    public T bindingAddImageQuery(final Boolean addImageQuery) {
        this.getBinding().addImageQuery = addImageQuery;
        return this.getThis();
    }
    
    protected T bindingTemplate(final String template) {
        this.getBinding().template = template;
        this.getBinding().texts = null;
        this.getBinding().images = null;
        return this.getThis();
    }
    
    protected T setBindingTextFields(final String... textFields) {
        this.getBinding().texts = new ArrayList<WnsText>();
        for (int i = 0; i < textFields.length; ++i) {
            final WnsText txt = new WnsText();
            txt.id = i + 1;
            txt.value = ((textFields[i] != null) ? textFields[i] : "");
            this.getBinding().texts.add(txt);
        }
        return this.getThis();
    }
    
    protected T setBindingImages(final String... imgSrcs) {
        this.getBinding().images = new ArrayList<WnsImage>();
        for (int i = 0; i < imgSrcs.length; ++i) {
            final WnsImage img = new WnsImage();
            img.id = i + 1;
            img.src = ((imgSrcs[i] != null) ? imgSrcs[i] : "");
            this.getBinding().images.add(img);
        }
        return this.getThis();
    }
}
