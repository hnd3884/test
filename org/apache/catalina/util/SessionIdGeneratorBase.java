package org.apache.catalina.util;

import org.apache.catalina.LifecycleState;
import org.apache.catalina.LifecycleException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.juli.logging.LogFactory;
import java.security.SecureRandom;
import java.util.Queue;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.SessionIdGenerator;

public abstract class SessionIdGeneratorBase extends LifecycleBase implements SessionIdGenerator
{
    private final Log log;
    private static final StringManager sm;
    private final Queue<SecureRandom> randoms;
    private String secureRandomClass;
    private String secureRandomAlgorithm;
    private String secureRandomProvider;
    private String jvmRoute;
    private int sessionIdLength;
    
    public SessionIdGeneratorBase() {
        this.log = LogFactory.getLog((Class)SessionIdGeneratorBase.class);
        this.randoms = new ConcurrentLinkedQueue<SecureRandom>();
        this.secureRandomClass = null;
        this.secureRandomAlgorithm = "SHA1PRNG";
        this.secureRandomProvider = null;
        this.jvmRoute = "";
        this.sessionIdLength = 16;
    }
    
    public String getSecureRandomClass() {
        return this.secureRandomClass;
    }
    
    public void setSecureRandomClass(final String secureRandomClass) {
        this.secureRandomClass = secureRandomClass;
    }
    
    public String getSecureRandomAlgorithm() {
        return this.secureRandomAlgorithm;
    }
    
    public void setSecureRandomAlgorithm(final String secureRandomAlgorithm) {
        this.secureRandomAlgorithm = secureRandomAlgorithm;
    }
    
    public String getSecureRandomProvider() {
        return this.secureRandomProvider;
    }
    
    public void setSecureRandomProvider(final String secureRandomProvider) {
        this.secureRandomProvider = secureRandomProvider;
    }
    
    @Override
    public String getJvmRoute() {
        return this.jvmRoute;
    }
    
    @Override
    public void setJvmRoute(final String jvmRoute) {
        this.jvmRoute = jvmRoute;
    }
    
    @Override
    public int getSessionIdLength() {
        return this.sessionIdLength;
    }
    
    @Override
    public void setSessionIdLength(final int sessionIdLength) {
        this.sessionIdLength = sessionIdLength;
    }
    
    @Override
    public String generateSessionId() {
        return this.generateSessionId(this.jvmRoute);
    }
    
    protected void getRandomBytes(final byte[] bytes) {
        SecureRandom random = this.randoms.poll();
        if (random == null) {
            random = this.createSecureRandom();
        }
        random.nextBytes(bytes);
        this.randoms.add(random);
    }
    
    private SecureRandom createSecureRandom() {
        SecureRandom result = null;
        final long t1 = System.currentTimeMillis();
        if (this.secureRandomClass != null) {
            try {
                final Class<?> clazz = Class.forName(this.secureRandomClass);
                result = (SecureRandom)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
            catch (final Exception e) {
                this.log.error((Object)SessionIdGeneratorBase.sm.getString("sessionIdGeneratorBase.random", new Object[] { this.secureRandomClass }), (Throwable)e);
            }
        }
        boolean error = false;
        if (result == null) {
            try {
                if (this.secureRandomProvider != null && this.secureRandomProvider.length() > 0) {
                    result = SecureRandom.getInstance(this.secureRandomAlgorithm, this.secureRandomProvider);
                }
                else if (this.secureRandomAlgorithm != null && this.secureRandomAlgorithm.length() > 0) {
                    result = SecureRandom.getInstance(this.secureRandomAlgorithm);
                }
            }
            catch (final NoSuchAlgorithmException e2) {
                error = true;
                this.log.error((Object)SessionIdGeneratorBase.sm.getString("sessionIdGeneratorBase.randomAlgorithm", new Object[] { this.secureRandomAlgorithm }), (Throwable)e2);
            }
            catch (final NoSuchProviderException e3) {
                error = true;
                this.log.error((Object)SessionIdGeneratorBase.sm.getString("sessionIdGeneratorBase.randomProvider", new Object[] { this.secureRandomProvider }), (Throwable)e3);
            }
        }
        if (result == null && error) {
            try {
                result = SecureRandom.getInstance("SHA1PRNG");
            }
            catch (final NoSuchAlgorithmException e2) {
                this.log.error((Object)SessionIdGeneratorBase.sm.getString("sessionIdGeneratorBase.randomAlgorithm", new Object[] { this.secureRandomAlgorithm }), (Throwable)e2);
            }
        }
        if (result == null) {
            result = new SecureRandom();
        }
        result.nextInt();
        final long t2 = System.currentTimeMillis();
        if (t2 - t1 > 100L) {
            this.log.warn((Object)SessionIdGeneratorBase.sm.getString("sessionIdGeneratorBase.createRandom", new Object[] { result.getAlgorithm(), t2 - t1 }));
        }
        return result;
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        this.generateSessionId();
        this.setState(LifecycleState.STARTING);
    }
    
    @Override
    protected void stopInternal() throws LifecycleException {
        this.setState(LifecycleState.STOPPING);
        this.randoms.clear();
    }
    
    @Override
    protected void destroyInternal() throws LifecycleException {
    }
    
    static {
        sm = StringManager.getManager("org.apache.catalina.util");
    }
}
