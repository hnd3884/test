package com.me.devicemanagement.onpremise.start;

import java.util.Hashtable;
import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.logging.Logger;

public class ProductStarter
{
    protected static TrayIconInfoParser trayIconInfoParser;
    private boolean previousProcessEnded;
    private static final Logger LOGGER;
    HashMap invokeClassMap;
    
    public ProductStarter(final String processInfoFileName) throws Exception {
        this.invokeClassMap = new HashMap();
        ProductStarter.trayIconInfoParser = new TrayIconInfoParser(processInfoFileName);
    }
    
    protected void executeProcess(final String processName) {
        final ArrayList commands = ProductStarter.trayIconInfoParser.getProcessInfo(processName);
        ProductStarter.LOGGER.log(Level.INFO, "ENTERING THE PRODUCT STARTER CLASS::::" + processName);
        final Iterator ite = commands.iterator();
        while (ite.hasNext()) {
            ProductStarter.LOGGER.log(Level.INFO, "ENTERING THE PRODUCT STARTER CLASS:::INTO THE ITERATOR");
            this.previousProcessEnded = false;
            final Properties commandAttrs = ite.next();
            try {
                final String command = commandAttrs.getProperty("OriginalCommand");
                final Properties additionalParams = ((Hashtable<K, Properties>)commandAttrs).get("AdditionalParams");
                final String type = commandAttrs.getProperty("Type");
                final String opProc = commandAttrs.getProperty("OutputProcesser");
                ProductStarter.LOGGER.log(Level.INFO, "CHECKING FOR OUTPUT PROCESSOR, PROCESS IS " + opProc + " COMMAND IS :::" + command);
                OutputProcesser userOutProc = null;
                if (opProc != null && !opProc.equalsIgnoreCase("")) {
                    ProductStarter.LOGGER.log(Level.INFO, "OUTPUR PROCESSOR EXISTS");
                    userOutProc = (OutputProcesser)Class.forName(opProc).newInstance();
                    final boolean canIStart = !userOutProc.hasProcessStarted(additionalParams);
                    if (!canIStart) {
                        continue;
                    }
                }
                if (type == null || type.equals("RunProcess")) {
                    final ArrayList envVarVect = ((Hashtable<K, ArrayList>)commandAttrs).get("EnvironmentVariables");
                    ProductStarter.LOGGER.log(Level.INFO, "Executing command \n " + command + " \n with the environment variables " + envVarVect);
                    final int envVarSize = envVarVect.size();
                    final Process process = null;
                    if (envVarSize > 0) {
                        final String[] envVars = new String[envVarSize];
                        int i = 0;
                        final Iterator enu1 = envVarVect.iterator();
                        while (enu1.hasNext()) {
                            envVars[i++] = enu1.next().toString();
                        }
                    }
                    if (opProc == null) {
                        continue;
                    }
                    final StreamReader insr = new StreamReader(process, true);
                    final StreamReader errsr = new StreamReader(process, false);
                    final OutputProcesser op = new OutputProcesser() {
                        @Override
                        public boolean processOutput(final String op) {
                            return false;
                        }
                        
                        @Override
                        public boolean processError(final String op) {
                            return false;
                        }
                        
                        @Override
                        public void terminated() {
                            ProductStarter.this.previousProcessEnded = true;
                        }
                        
                        @Override
                        public void endStringReached() {
                            ProductStarter.this.previousProcessEnded = true;
                        }
                        
                        @Override
                        public boolean hasProcessStarted(final Properties additionalParams) {
                            return true;
                        }
                    };
                    insr.addOutputProcesser(userOutProc);
                    errsr.addOutputProcesser(userOutProc);
                    insr.addOutputProcesser(op);
                    errsr.addOutputProcesser(op);
                    insr.startReading();
                    errsr.startReading();
                    while (!this.previousProcessEnded) {
                        try {
                            Thread.sleep(100L);
                            continue;
                        }
                        catch (final InterruptedException iex) {}
                        break;
                    }
                    insr.removeOutputProcesser(op);
                    errsr.removeOutputProcesser(op);
                    insr.removeOutputProcesser(userOutProc);
                    errsr.removeOutputProcesser(userOutProc);
                }
                else {
                    if (!type.equals("InvokeClass")) {
                        continue;
                    }
                    this.executeClass(command.trim(), additionalParams);
                }
            }
            catch (final Exception e) {
                ProductStarter.LOGGER.log(Level.SEVERE, e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public void executeClass(final String invokeClassStr, final Properties additionalParams) {
        try {
            InvokeClass invokeClass = this.invokeClassMap.get(invokeClassStr);
            if (invokeClass == null) {
                invokeClass = (InvokeClass)Class.forName(invokeClassStr).newInstance();
                this.invokeClassMap.put(invokeClassStr, invokeClass);
            }
            invokeClass.executeProgram(additionalParams, new String[0]);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(final String[] args) throws Exception {
        StartupUtil.getInstance().initSystemStreams(".." + File.separator + "logs");
        if (args.length < 2) {
            ProductStarter.LOGGER.log(Level.INFO, "Useage : java ProductStarter <Process name> <relative path of conf file from 'product.home' logger.log(Level.INFO,tem property>");
            System.exit(0);
        }
        if (args.length > 1) {
            final String productHome = System.getProperty("product.home");
            if (productHome == null) {
                ProductStarter.LOGGER.log(Level.INFO, "Set 'product.home' system property.");
                System.exit(0);
            }
            String processInfoFileName = productHome + File.separator + args[0];
            if (!new File(processInfoFileName).exists()) {
                ProductStarter.LOGGER.log(Level.INFO, "Wrong ProcessInfo conf File. Using default ProcessInfo File " + processInfoFileName);
                processInfoFileName = productHome + File.separator + "conf/TrayIconInfo.xml";
            }
            ProductStarter.LOGGER.log(Level.INFO, "TrayIconInfo Location : " + processInfoFileName);
            ProductStarter.LOGGER.log(Level.INFO, "Execute Process : " + args[1]);
            final ProductStarter emst = new ProductStarter(processInfoFileName);
            emst.executeProcess(args[1]);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ProductStarter.class.getName());
    }
}
