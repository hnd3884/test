package com.adventnet.persistence.fos;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import java.util.logging.Level;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.logging.Logger;

public class FOSUtil
{
    private static final Logger LOG;
    
    public static String getIPAddr(final String publicIP) {
        String ipaddr = null;
        try {
            final Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            while (n.hasMoreElements()) {
                final NetworkInterface e = n.nextElement();
                final Enumeration<InetAddress> a = e.getInetAddresses();
                while (a.hasMoreElements()) {
                    final InetAddress addr = a.nextElement();
                    if (!addr.isLoopbackAddress() && addr instanceof Inet4Address && !addr.getHostAddress().equals(publicIP)) {
                        ipaddr = addr.getHostAddress();
                    }
                }
            }
        }
        catch (final Exception e2) {
            FOSUtil.LOG.log(Level.SEVERE, e2.getMessage());
            throw new RuntimeException("Error in obtaining IP address", e2);
        }
        return ipaddr;
    }
    
    public static List<String> getList(final String prefix, final String prop) {
        if (prop == null) {
            throw new IllegalArgumentException("Property to be splitted cannot be null");
        }
        final List<String> list = new ArrayList<String>();
        final String[] split;
        final String[] parts = split = prop.split(",");
        for (final String entry : split) {
            if (entry.trim().length() > 0) {
                list.add((prefix + entry).trim());
            }
        }
        return list;
    }
    
    public static String getValue(final Properties props, final String property) {
        final String val = props.getProperty(property);
        if (val == null) {
            throw new IllegalArgumentException("The Mandatory property " + property + " is not configured or null");
        }
        return val;
    }
    
    public static Map<Integer, String> getIndexVsDirMap(final String prop) throws FOSException {
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
            throw new FOSException(FOSErrorCode.ERROR_MISC, "Error in getting property " + prop + ". Excepted format 1:prop,2:prop,3:prop");
        }
        return map;
    }
    
    public static Map<Integer, List<String>> getIndexVsExcludeDirMap(final String prefix, final Map<Integer, String> dirMap, final String prop) throws FOSException {
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
                            map.put(index, getList(prefix + dirMap.get(index) + "\\", parts_inner[1]));
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new FOSException(FOSErrorCode.ERROR_MISC, "Error in getting property " + prop + ". Excepted format 1:prop,prop; 2:prop; 3:prop,prop");
        }
        return map;
    }
    
    static {
        LOG = Logger.getLogger(FOSUtil.class.getName());
    }
}
