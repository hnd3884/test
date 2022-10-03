package com.adventnet.authentication;

import com.adventnet.persistence.xml.ConfigurationPopulationException;
import java.util.logging.Level;
import com.adventnet.authentication.util.AuthDBUtil;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.persistence.xml.ConfigurationPopulator;

public class AuthPopulator implements ConfigurationPopulator
{
    private static String className;
    private static Logger out;
    
    public void populate(final DataObject confData) throws ConfigurationPopulationException {
        AuthPopulator.out.entering(AuthPopulator.className, "AuthPopulatior");
        try {
            AuthDBUtil.populateAuthConf(confData);
        }
        catch (final ConfigurationPopulationException exc) {
            AuthPopulator.out.log(Level.SEVERE, "Error occured during population of conf file " + confData, (Throwable)exc);
            throw exc;
        }
        catch (final Exception exc2) {
            throw new ConfigurationPopulationException((Throwable)exc2);
        }
        finally {
            AuthPopulator.out.exiting(AuthPopulator.className, "AuthPopulator");
        }
    }
    
    public void update(final DataObject confData) throws ConfigurationPopulationException {
        AuthPopulator.out.entering(AuthPopulator.className, "AuthPopulatior");
        try {
            AuthDBUtil.updateAuthConf(confData);
        }
        catch (final ConfigurationPopulationException exc) {
            AuthPopulator.out.log(Level.SEVERE, "Error occured during population of conf file " + confData, (Throwable)exc);
            throw exc;
        }
        catch (final Exception exc2) {
            throw new ConfigurationPopulationException((Throwable)exc2);
        }
        finally {
            AuthPopulator.out.exiting(AuthPopulator.className, "AuthPopulator");
        }
    }
    
    static {
        AuthPopulator.className = AuthPopulator.class.getName();
        AuthPopulator.out = Logger.getLogger(AuthPopulator.className);
    }
}
