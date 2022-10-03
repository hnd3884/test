package com.me.devicemanagement.onpremise.server.silentupdate.ondemand;

import java.util.logging.Level;
import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

public class InstrumentationFactory
{
    private static Logger logger;
    private static String sourceClass;
    private static Instrumentation instrumentation;
    private static InstrumentationFactory instrumentationFactory;
    
    private InstrumentationFactory() {
    }
    
    public static void premain(final String args, final Instrumentation inst) {
        getInstance().setInstrumentation(inst);
        InstrumentationFactory.logger.log(Level.INFO, "premain method called !");
        InstrumentationFactory.logger.log(Level.INFO, "args : " + args);
        InstrumentationFactory.logger.log(Level.INFO, "inst : " + inst);
    }
    
    public static InstrumentationFactory getInstance() {
        if (InstrumentationFactory.instrumentationFactory == null) {
            InstrumentationFactory.instrumentationFactory = new InstrumentationFactory();
            InstrumentationFactory.logger.log(Level.INFO, "New InstrumentationFactory instance created : " + InstrumentationFactory.instrumentationFactory);
        }
        return InstrumentationFactory.instrumentationFactory;
    }
    
    private void setInstrumentation(final Instrumentation inst) {
        InstrumentationFactory.instrumentation = inst;
    }
    
    public Instrumentation getInstrumentation() {
        return InstrumentationFactory.instrumentation;
    }
    
    public boolean isInstrumentationLoaded() throws Exception {
        return getInstance().getInstrumentation() != null;
    }
    
    static {
        InstrumentationFactory.logger = Logger.getLogger("SilentUpdate");
        InstrumentationFactory.sourceClass = "InstrumentationFactory";
        InstrumentationFactory.instrumentation = null;
        InstrumentationFactory.instrumentationFactory = null;
    }
}
