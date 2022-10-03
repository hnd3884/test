package org.apache.catalina.startup;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

public final class HomesUserDatabase implements UserDatabase
{
    private final Hashtable<String, String> homes;
    private UserConfig userConfig;
    
    public HomesUserDatabase() {
        this.homes = new Hashtable<String, String>();
        this.userConfig = null;
    }
    
    @Override
    public UserConfig getUserConfig() {
        return this.userConfig;
    }
    
    @Override
    public void setUserConfig(final UserConfig userConfig) {
        this.userConfig = userConfig;
        this.init();
    }
    
    @Override
    public String getHome(final String user) {
        return this.homes.get(user);
    }
    
    @Override
    public Enumeration<String> getUsers() {
        return this.homes.keys();
    }
    
    private void init() {
        final String homeBase = this.userConfig.getHomeBase();
        final File homeBaseDir = new File(homeBase);
        if (!homeBaseDir.exists() || !homeBaseDir.isDirectory()) {
            return;
        }
        final String[] homeBaseFiles = homeBaseDir.list();
        if (homeBaseFiles == null) {
            return;
        }
        for (final String homeBaseFile : homeBaseFiles) {
            final File homeDir = new File(homeBaseDir, homeBaseFile);
            if (homeDir.isDirectory()) {
                if (homeDir.canRead()) {
                    this.homes.put(homeBaseFile, homeDir.toString());
                }
            }
        }
    }
}
