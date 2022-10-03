package com.azul.crs.client;

import com.azul.crs.com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Enumeration;
import java.net.Inet4Address;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.net.SocketException;
import java.util.Comparator;
import java.util.function.Function;
import com.azul.crs.shared.Utils;
import java.util.Collections;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIgnore;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import com.azul.crs.util.logging.Logger;

public class Inventory
{
    public static final String INSTANCE_TAGS_PROPERTY = "com.azul.crs.instance.tags";
    public static final String HOST_NAME_KEY = "hostName";
    public static final String NETWORKS_KEY = "networks";
    public static final String SYSTEM_PROPS_KEY = "systemProperties";
    public static final String JVM_ARGS_KEY = "jvmArgs";
    public static final String MAIN_METHOD = "mainMethod";
    public static final String ENVIRONMENT_KEY = "osEnvironment";
    private Logger logger;
    private Map<String, Object> map;
    
    public Inventory() {
        this.logger = Logger.getLogger(Inventory.class);
        this.map = new LinkedHashMap<String, Object>();
    }
    
    public Inventory populate() {
        this.map.put("hostName", this.hostName());
        this.map.put("systemProperties", this.systemProperties());
        this.map.put("jvmArgs", jvmArgs());
        this.map.put("osEnvironment", this.osEnvironment());
        return this;
    }
    
    public Inventory networkInformation() {
        this.map.put("networks", this.networks());
        return this;
    }
    
    public Inventory mainMethod(final String mainMethod) {
        this.map.put("mainMethod", mainMethod);
        return this;
    }
    
    public String hostName() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch (final UnknownHostException uhe) {
            this.logger.warning("cannot get host name %s", uhe.toString());
            String name = this.getHostNameViaReflection();
            if (name == null) {
                name = this.getHostNameFromNetworkInterface();
            }
            if (name == null) {
                name = "<UNKNOWN>";
            }
            return name;
        }
    }
    
    @JsonIgnore
    public static String instanceTags() {
        return System.getProperties().getProperty("com.azul.crs.instance.tags");
    }
    
    public Map systemProperties() {
        return System.getProperties();
    }
    
    public Map<String, String> osEnvironment() {
        return System.getenv();
    }
    
    public static List<String> jvmArgs() {
        final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getInputArguments();
    }
    
    private List<Network> networks() {
        try {
            final List<Network> result = new ArrayList<Network>();
            for (final NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                try {
                    if (!ni.isUp() || ni.isLoopback() || ni.getName().startsWith("docker")) {
                        continue;
                    }
                    final List<Address> addrList = new ArrayList<Address>();
                    for (final InetAddress addr : Collections.list(ni.getInetAddresses())) {
                        if (Client.isVMShutdownInitiated() && Utils.currentTimeCount() >= Client.getVMShutdownDeadline()) {
                            return Collections.emptyList();
                        }
                        addrList.add(new Address(addr.getCanonicalHostName(), this.getTrueIpAddress(addr)));
                    }
                    addrList.sort(Comparator.comparing((Function<? super Address, ? extends Comparable>)new Function<Address, String>() {
                        @Override
                        public String apply(final Address a) {
                            return a.hostname;
                        }
                    }));
                    result.add(new Network(ni.getName(), addrList));
                }
                catch (final SocketException e) {
                    this.logger.warning("cannot get network info %s", e.toString());
                }
            }
            result.sort(Comparator.comparing((Function<? super Network, ? extends Comparable>)new Function<Network, String>() {
                @Override
                public String apply(final Network ni) {
                    return ni.interfaceName;
                }
            }));
            return result;
        }
        catch (final SocketException e2) {
            this.logger.warning("cannot get network info %s", e2.toString());
            return Collections.emptyList();
        }
    }
    
    private String getTrueIpAddress(final InetAddress addr) {
        final String text = addr.getHostAddress();
        final int pos = text.indexOf(37);
        return (pos < 0) ? text : text.substring(0, pos);
    }
    
    Map<String, Object> toMap() {
        return this.map;
    }
    
    private String getHostNameViaReflection() {
        try {
            final Class clazz = Class.forName("java.net.Inet4AddressImpl");
            final Method method = clazz.getDeclaredMethod("getLocalHostName", (Class[])new Class[0]);
            method.setAccessible(true);
            final Constructor[] declaredConstructors = clazz.getDeclaredConstructors();
            final int length = declaredConstructors.length;
            int i = 0;
            while (i < length) {
                final Constructor ctor = declaredConstructors[i];
                if (ctor.getParameterCount() == 0) {
                    ctor.setAccessible(true);
                    final Object instance = ctor.newInstance(new Object[0]);
                    final Object result = method.invoke(instance, new Object[0]);
                    if (result instanceof String) {
                        return (String)result;
                    }
                    this.logger.warning("cannot get host name. internal error %s", (result == null) ? null : result.getClass());
                    return null;
                }
                else {
                    ++i;
                }
            }
        }
        catch (final ReflectiveOperationException | SecurityException e) {
            this.logger.warning("cannot get host name %s", e.toString());
        }
        return null;
    }
    
    private String getHostNameFromNetworkInterface() {
        try {
            String candidateName = null;
            final Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                final NetworkInterface ni = nis.nextElement();
                if (ni.isUp() && !ni.isLoopback()) {
                    final Enumeration<InetAddress> isa = ni.getInetAddresses();
                    while (isa.hasMoreElements()) {
                        final InetAddress ia = isa.nextElement();
                        if (ia instanceof Inet4Address) {
                            return ia.getCanonicalHostName();
                        }
                        candidateName = ia.getCanonicalHostName();
                    }
                }
            }
            return candidateName;
        }
        catch (final SocketException e) {
            this.logger.warning("cannot get host name for iface %s", e.toString());
            return null;
        }
    }
    
    private static class Network
    {
        @JsonProperty("interface")
        public final String interfaceName;
        @JsonProperty("addresses")
        public final List<Address> addresses;
        
        public Network(final String interfaceName, final List<Address> addresses) {
            this.interfaceName = interfaceName;
            this.addresses = addresses;
        }
    }
    
    private static class Address
    {
        public final String hostname;
        public final String address;
        
        public Address(final String hostname, final String address) {
            this.hostname = hostname;
            this.address = address;
        }
    }
}
