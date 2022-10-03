package com.zoho.mickey.ha;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import com.adventnet.persistence.ConfigurationParser;
import java.util.List;
import com.zoho.framework.utils.crypto.EnDecryptUtil;
import java.io.File;
import java.util.ArrayList;
import com.zoho.mickey.ha.filereplication.FileReplicationHandler;
import java.nio.file.Paths;
import com.zoho.conf.Configuration;

public class HASyncUtil
{
    public static void syncConfigFiles(final HAConfig config) {
        try {
            final File customerConfFile = Paths.get(Configuration.getString("server.home"), "conf", "customer-config.xml").toFile();
            final String oldCryptKey = getCryptKey(customerConfFile);
            final FileReplicationHandler replHandler = (FileReplicationHandler)Class.forName(config.ReplicationHandler()).newInstance();
            final List<String> files = new ArrayList<String>();
            files.add("conf" + File.separator + "database_params.conf|customer-config.xml");
            replHandler.replicateFiles(config, files);
            final String newCryptKey = getCryptKey(customerConfFile);
            if (!newCryptKey.equals(oldCryptKey)) {
                EnDecryptUtil.setCryptTag(newCryptKey);
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            throw new RuntimeException("ERROR_IN_SYNCING_CONFIG_FILES" + exp.getMessage());
        }
    }
    
    private static String getCryptKey(final File customerConfFile) throws IOException, Exception {
        final ConfigurationParser parser = new ConfigurationParser(customerConfFile.getCanonicalPath());
        final HashMap<String, String> keyVsValue = new HashMap<String, String>();
        keyVsValue.putAll(parser.getConfigurationValues());
        final String cryptTag = keyVsValue.get("CryptTag");
        if (cryptTag == null) {
            throw new Exception("crypt tag is empty");
        }
        return cryptTag;
    }
    
    public static Map<Integer, List<String>> getIndexVsExcludeDirMap(final String prefix, final Map<Integer, String> dirMap, final String prop) throws HAException {
        final Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
        try {
            if (prop.trim().length() != 0) {
                final String[] split;
                final String[] parts_outer = split = prop.split(";");
                for (final String entry : split) {
                    if (entry.trim().length() > 2) {
                        final String[] parts_inner = entry.split(":");
                        if (parts_inner.length > 1) {
                            final Integer index = Integer.parseInt(parts_inner[0].trim());
                            map.put(index, HAUtil.getList(prefix + dirMap.get(index) + "\\", parts_inner[1]));
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new HAException(HAErrorCode.ERROR_MISC, "Error in getting property " + prop + ". Excepted format 1:prop,prop; 2:prop; 3:prop,prop");
        }
        return map;
    }
    
    public static Map<Integer, String> getIndexVsDirMap(final String prop) throws HAException {
        final Map<Integer, String> map = new HashMap<Integer, String>();
        try {
            if (prop.trim().length() != 0) {
                final String[] split;
                final String[] parts_outer = split = prop.split(",");
                for (final String entry : split) {
                    if (entry.trim().length() > 2) {
                        final String[] parts_inner = entry.split(":");
                        if (parts_inner.length > 1) {
                            map.put(Integer.parseInt(parts_inner[0].trim()), parts_inner[1].trim());
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new HAException(HAErrorCode.ERROR_MISC, "Error in getting property " + prop + ". Excepted format 1:prop,2:prop,3:prop");
        }
        return map;
    }
}
