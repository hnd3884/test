package java.awt.image;

import java.util.Hashtable;

public class FilteredImageSource implements ImageProducer
{
    ImageProducer src;
    ImageFilter filter;
    private Hashtable proxies;
    
    public FilteredImageSource(final ImageProducer src, final ImageFilter filter) {
        this.src = src;
        this.filter = filter;
    }
    
    @Override
    public synchronized void addConsumer(final ImageConsumer imageConsumer) {
        if (this.proxies == null) {
            this.proxies = new Hashtable();
        }
        if (!this.proxies.containsKey(imageConsumer)) {
            final ImageFilter filterInstance = this.filter.getFilterInstance(imageConsumer);
            this.proxies.put(imageConsumer, filterInstance);
            this.src.addConsumer(filterInstance);
        }
    }
    
    @Override
    public synchronized boolean isConsumer(final ImageConsumer imageConsumer) {
        return this.proxies != null && this.proxies.containsKey(imageConsumer);
    }
    
    @Override
    public synchronized void removeConsumer(final ImageConsumer imageConsumer) {
        if (this.proxies != null) {
            final ImageFilter imageFilter = this.proxies.get(imageConsumer);
            if (imageFilter != null) {
                this.src.removeConsumer(imageFilter);
                this.proxies.remove(imageConsumer);
                if (this.proxies.isEmpty()) {
                    this.proxies = null;
                }
            }
        }
    }
    
    @Override
    public synchronized void startProduction(final ImageConsumer imageConsumer) {
        if (this.proxies == null) {
            this.proxies = new Hashtable();
        }
        ImageFilter filterInstance = this.proxies.get(imageConsumer);
        if (filterInstance == null) {
            filterInstance = this.filter.getFilterInstance(imageConsumer);
            this.proxies.put(imageConsumer, filterInstance);
        }
        this.src.startProduction(filterInstance);
    }
    
    @Override
    public synchronized void requestTopDownLeftRightResend(final ImageConsumer imageConsumer) {
        if (this.proxies != null) {
            final ImageFilter imageFilter = this.proxies.get(imageConsumer);
            if (imageFilter != null) {
                imageFilter.resendTopDownLeftRight(this.src);
            }
        }
    }
}
