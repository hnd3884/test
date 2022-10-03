package com.adventnet.iam.security.antivirus.clamav;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.zoho.instrument.common.ClamAVCall;
import com.zoho.instrument.common.ClamAVConnectionCall;

public class ClamAvInstrumentation
{
    private ClamAVConnectionCall clamavconnectioncall;
    private ClamAVCall clamavcall;
    private static final Logger LOGGER;
    
    public ClamAvInstrumentation(final String destIP) {
        this.clamavconnectioncall = ClamAVConnectionCall.getInstance(destIP);
        this.clamavcall = ClamAVCall.newInstance(new String[] { destIP });
        this.startClamAVConnectionCall();
        this.startClamAVCall();
    }
    
    protected void startClamAVConnectionCall() {
        try {
            this.clamavconnectioncall.start();
        }
        catch (final Exception e) {
            ClamAvInstrumentation.LOGGER.log(Level.SEVERE, "Exception in starting ClamAv connection call:{0}", new Object[] { e });
        }
    }
    
    protected void completeClamAVConnectionCallExp(final Exception excp) {
        try {
            this.clamavconnectioncall.complete((Throwable)excp);
        }
        catch (final Exception e) {
            ClamAvInstrumentation.LOGGER.log(Level.SEVERE, "Exception in completing ClamAv connection Exception call:{0}", new Object[] { e });
        }
    }
    
    protected void completeClamAVConnectionCall() {
        try {
            this.clamavconnectioncall.complete();
        }
        catch (final Exception e) {
            ClamAvInstrumentation.LOGGER.log(Level.SEVERE, "Exception in completing ClamAv connection call:{0}", new Object[] { e });
        }
    }
    
    protected void startClamAVCall() {
        try {
            this.clamavcall.start();
        }
        catch (final Exception e) {
            ClamAvInstrumentation.LOGGER.log(Level.SEVERE, "Exception in starting ClamAv call:{0}", new Object[] { e });
        }
    }
    
    protected void completeClamAVCall(final boolean virusFound) {
        try {
            this.clamavcall.complete();
            if (virusFound) {
                this.clamavcall.setISFailed(1);
            }
        }
        catch (final Exception e) {
            ClamAvInstrumentation.LOGGER.log(Level.SEVERE, "Exception in completing ClamAv call:{0}", new Object[] { e });
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ClamAvInstrumentation.class.getName());
    }
}
