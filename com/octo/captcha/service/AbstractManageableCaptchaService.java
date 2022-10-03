package com.octo.captcha.service;

import com.octo.captcha.Captcha;
import java.util.Locale;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.captchastore.CaptchaStore;
import org.apache.commons.collections.FastHashMap;

public abstract class AbstractManageableCaptchaService extends AbstractCaptchaService implements AbstractManageableCaptchaServiceMBean, CaptchaService
{
    private int minGuarantedStorageDelayInSeconds;
    private int captchaStoreMaxSize;
    private int captchaStoreSizeBeforeGarbageCollection;
    private int numberOfGeneratedCaptchas;
    private int numberOfCorrectResponse;
    private int numberOfUncorrectResponse;
    private int numberOfGarbageCollectedCaptcha;
    private FastHashMap times;
    private long oldestCaptcha;
    
    protected AbstractManageableCaptchaService(final CaptchaStore captchaStore, final CaptchaEngine captchaEngine, final int minGuarantedStorageDelayInSeconds, final int captchaStoreMaxSize) {
        super(captchaStore, captchaEngine);
        this.numberOfGeneratedCaptchas = 0;
        this.numberOfCorrectResponse = 0;
        this.numberOfUncorrectResponse = 0;
        this.numberOfGarbageCollectedCaptcha = 0;
        this.oldestCaptcha = 0L;
        this.setCaptchaStoreMaxSize(captchaStoreMaxSize);
        this.setMinGuarantedStorageDelayInSeconds(minGuarantedStorageDelayInSeconds);
        this.setCaptchaStoreSizeBeforeGarbageCollection((int)Math.round(0.8 * captchaStoreMaxSize));
        this.times = new FastHashMap();
    }
    
    protected AbstractManageableCaptchaService(final CaptchaStore captchaStore, final CaptchaEngine captchaEngine, final int n, final int n2, final int captchaStoreSizeBeforeGarbageCollection) {
        this(captchaStore, captchaEngine, n, n2);
        if (n2 < captchaStoreSizeBeforeGarbageCollection) {
            throw new IllegalArgumentException("the max store size can't be less than garbage collection size. if you want to disable garbage collection (this is not recommended) you may set them equals (max=garbage)");
        }
        this.setCaptchaStoreSizeBeforeGarbageCollection(captchaStoreSizeBeforeGarbageCollection);
    }
    
    public String getCaptchaEngineClass() {
        return this.engine.getClass().getName();
    }
    
    public void setCaptchaEngineClass(final String s) throws IllegalArgumentException {
        try {
            final Object instance = Class.forName(s).newInstance();
            if (!(instance instanceof CaptchaEngine)) {
                throw new IllegalArgumentException("Class is not instance of CaptchaEngine! " + s);
            }
            this.engine = (CaptchaEngine)instance;
        }
        catch (final InstantiationException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
        catch (final IllegalAccessException ex2) {
            throw new IllegalArgumentException(ex2.getMessage());
        }
        catch (final ClassNotFoundException ex3) {
            throw new IllegalArgumentException(ex3.getMessage());
        }
        catch (final RuntimeException ex4) {
            throw new IllegalArgumentException(ex4.getMessage());
        }
    }
    
    public CaptchaEngine getEngine() {
        return this.engine;
    }
    
    public void setCaptchaEngine(final CaptchaEngine engine) {
        this.engine = engine;
    }
    
    public int getMinGuarantedStorageDelayInSeconds() {
        return this.minGuarantedStorageDelayInSeconds;
    }
    
    public void setMinGuarantedStorageDelayInSeconds(final int minGuarantedStorageDelayInSeconds) {
        this.minGuarantedStorageDelayInSeconds = minGuarantedStorageDelayInSeconds;
    }
    
    public long getNumberOfGeneratedCaptchas() {
        return this.numberOfGeneratedCaptchas;
    }
    
    public long getNumberOfCorrectResponses() {
        return this.numberOfCorrectResponse;
    }
    
    public long getNumberOfUncorrectResponses() {
        return this.numberOfUncorrectResponse;
    }
    
    public int getCaptchaStoreSize() {
        return this.store.getSize();
    }
    
    public int getNumberOfGarbageCollectableCaptchas() {
        return this.getGarbageCollectableCaptchaIds(System.currentTimeMillis()).size();
    }
    
    public long getNumberOfGarbageCollectedCaptcha() {
        return this.numberOfGarbageCollectedCaptcha;
    }
    
    public int getCaptchaStoreSizeBeforeGarbageCollection() {
        return this.captchaStoreSizeBeforeGarbageCollection;
    }
    
    public void setCaptchaStoreSizeBeforeGarbageCollection(final int captchaStoreSizeBeforeGarbageCollection) {
        if (this.captchaStoreMaxSize < captchaStoreSizeBeforeGarbageCollection) {
            throw new IllegalArgumentException("the max store size can't be less than garbage collection size. if you want to disable garbage collection (this is not recommended) you may set them equals (max=garbage)");
        }
        this.captchaStoreSizeBeforeGarbageCollection = captchaStoreSizeBeforeGarbageCollection;
    }
    
    public void setCaptchaStoreMaxSize(final int captchaStoreMaxSize) {
        if (captchaStoreMaxSize < this.captchaStoreSizeBeforeGarbageCollection) {
            throw new IllegalArgumentException("the max store size can't be less than garbage collection size. if you want to disable garbage collection (this is not recommended) you may set them equals (max=garbage)");
        }
        this.captchaStoreMaxSize = captchaStoreMaxSize;
    }
    
    public int getCaptchaStoreMaxSize() {
        return this.captchaStoreMaxSize;
    }
    
    protected void garbageCollectCaptchaStore(final Iterator iterator) {
        final long n = System.currentTimeMillis() - 1000 * this.minGuarantedStorageDelayInSeconds;
        while (iterator.hasNext()) {
            final String string = iterator.next().toString();
            if ((long)this.times.get((Object)string) < n) {
                this.times.remove((Object)string);
                this.store.removeCaptcha(string);
                ++this.numberOfGarbageCollectedCaptcha;
            }
        }
    }
    
    public void garbageCollectCaptchaStore() {
        this.garbageCollectCaptchaStore(this.getGarbageCollectableCaptchaIds(System.currentTimeMillis()).iterator());
    }
    
    public void emptyCaptchaStore() {
        this.store.empty();
        this.times = new FastHashMap();
    }
    
    private Collection getGarbageCollectableCaptchaIds(final long n) {
        final HashSet set = new HashSet();
        final long n2 = n - 1000 * this.getMinGuarantedStorageDelayInSeconds();
        if (n2 > this.oldestCaptcha) {
            for (final String s : new HashSet(this.times.keySet())) {
                final long longValue = (long)this.times.get((Object)s);
                this.oldestCaptcha = Math.min(longValue, (this.oldestCaptcha == 0L) ? longValue : this.oldestCaptcha);
                if (longValue < n2) {
                    set.add(s);
                }
            }
        }
        return set;
    }
    
    @Override
    protected Captcha generateAndStoreCaptcha(final Locale locale, final String s) {
        if (!this.isCaptchaStoreFull()) {
            if (this.isCaptchaStoreQuotaReached()) {
                this.garbageCollectCaptchaStore();
            }
            return this.generateCountTimeStampAndStoreCaptcha(s, locale);
        }
        final Collection garbageCollectableCaptchaIds = this.getGarbageCollectableCaptchaIds(System.currentTimeMillis());
        if (garbageCollectableCaptchaIds.size() > 0) {
            this.garbageCollectCaptchaStore(garbageCollectableCaptchaIds.iterator());
            return this.generateAndStoreCaptcha(locale, s);
        }
        throw new CaptchaServiceException("Store is full, try to increase CaptchaStore Size orto decrease time out, or to decrease CaptchaStoreSizeBeforeGrbageCollection");
    }
    
    private Captcha generateCountTimeStampAndStoreCaptcha(final String s, final Locale locale) {
        ++this.numberOfGeneratedCaptchas;
        this.times.put((Object)s, (Object)new Long(System.currentTimeMillis()));
        return super.generateAndStoreCaptcha(locale, s);
    }
    
    protected boolean isCaptchaStoreFull() {
        return this.getCaptchaStoreMaxSize() != 0 && this.getCaptchaStoreSize() >= this.getCaptchaStoreMaxSize();
    }
    
    protected boolean isCaptchaStoreQuotaReached() {
        return this.getCaptchaStoreSize() >= this.getCaptchaStoreSizeBeforeGarbageCollection();
    }
    
    @Override
    public Boolean validateResponseForID(final String s, final Object o) throws CaptchaServiceException {
        final Boolean validateResponseForID = super.validateResponseForID(s, o);
        this.times.remove((Object)s);
        if (validateResponseForID) {
            ++this.numberOfCorrectResponse;
        }
        else {
            ++this.numberOfUncorrectResponse;
        }
        return validateResponseForID;
    }
}
