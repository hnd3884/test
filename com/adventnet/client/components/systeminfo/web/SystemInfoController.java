package com.adventnet.client.components.systeminfo.web;

import java.net.InetAddress;
import java.io.File;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.property.web.PropertyController;

public class SystemInfoController extends PropertyController
{
    @Override
    public Object getPropertyValue(final ViewContext viewCtx, final String propertyName) throws Exception {
        if (propertyName.equals("totalmemory")) {
            return String.valueOf(Runtime.getRuntime().totalMemory() / 1048576L);
        }
        if (propertyName.equals("freememory")) {
            return String.valueOf(Runtime.getRuntime().freeMemory() / 1048576L);
        }
        if (propertyName.equals("usedmemory")) {
            final long free = Runtime.getRuntime().freeMemory() / 1048576L;
            final long total = Runtime.getRuntime().totalMemory() / 1048576L;
            final long used = total - free;
            return String.valueOf(used);
        }
        if (propertyName.equals("osname")) {
            return System.getProperty("os.name");
        }
        if (propertyName.equals("osversion")) {
            return System.getProperty("os.version");
        }
        if (propertyName.equals("osarch")) {
            return System.getProperty("os.arch");
        }
        if (propertyName.equals("systemtime")) {
            return new Long(System.currentTimeMillis());
        }
        if (propertyName.equals("javaversion")) {
            return System.getProperty("java.vm.version");
        }
        if (propertyName.equals("serverport")) {
            return String.valueOf(viewCtx.getRequest().getServerPort());
        }
        if (propertyName.equals("workingdir")) {
            final File f = new File(".");
            final String path = f.getCanonicalPath();
            final File f2 = new File(path);
            return f2.getParent();
        }
        if (propertyName.equals("hostname")) {
            try {
                final InetAddress add = InetAddress.getLocalHost();
                return add.getHostName();
            }
            catch (final Exception e) {
                return "[Unable to resolve]";
            }
        }
        if (propertyName.equals("serverstarttime")) {
            final String time = System.getProperty("serverstarttime");
            return time;
        }
        return null;
    }
}
