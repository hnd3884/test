package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.util.concurrent.ConcurrentHashMap;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import java.io.IOException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import java.util.List;
import org.apache.tomcat.dbcp.dbcp2.ListException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.naming.spi.ObjectFactory;

abstract class InstanceKeyDataSourceFactory implements ObjectFactory
{
    private static final Map<String, InstanceKeyDataSource> INSTANCE_MAP;
    
    static synchronized String registerNewInstance(final InstanceKeyDataSource ds) {
        int max = 0;
        for (final String s : InstanceKeyDataSourceFactory.INSTANCE_MAP.keySet()) {
            if (s != null) {
                try {
                    max = Math.max(max, Integer.parseInt(s));
                }
                catch (final NumberFormatException ex) {}
            }
        }
        final String instanceKey = String.valueOf(max + 1);
        InstanceKeyDataSourceFactory.INSTANCE_MAP.put(instanceKey, ds);
        return instanceKey;
    }
    
    static void removeInstance(final String key) {
        if (key != null) {
            InstanceKeyDataSourceFactory.INSTANCE_MAP.remove(key);
        }
    }
    
    public static void closeAll() throws Exception {
        final List<Throwable> exceptionList = new ArrayList<Throwable>(InstanceKeyDataSourceFactory.INSTANCE_MAP.size());
        for (final Map.Entry<String, InstanceKeyDataSource> next : InstanceKeyDataSourceFactory.INSTANCE_MAP.entrySet()) {
            if (next != null) {
                final InstanceKeyDataSource value = next.getValue();
                if (value == null) {
                    continue;
                }
                try {
                    value.close();
                }
                catch (final Exception e) {
                    exceptionList.add(e);
                }
            }
        }
        InstanceKeyDataSourceFactory.INSTANCE_MAP.clear();
        if (!exceptionList.isEmpty()) {
            throw new ListException("Could not close all InstanceKeyDataSource instances.", exceptionList);
        }
    }
    
    @Override
    public Object getObjectInstance(final Object refObj, final Name name, final Context context, final Hashtable<?, ?> env) throws IOException, ClassNotFoundException {
        Object obj = null;
        if (refObj instanceof Reference) {
            final Reference ref = (Reference)refObj;
            if (this.isCorrectClass(ref.getClassName())) {
                final RefAddr refAddr = ref.get("instanceKey");
                if (refAddr != null && refAddr.getContent() != null) {
                    obj = InstanceKeyDataSourceFactory.INSTANCE_MAP.get(refAddr.getContent());
                }
                else {
                    String key = null;
                    if (name != null) {
                        key = name.toString();
                        obj = InstanceKeyDataSourceFactory.INSTANCE_MAP.get(key);
                    }
                    if (obj == null) {
                        final InstanceKeyDataSource ds = this.getNewInstance(ref);
                        this.setCommonProperties(ref, ds);
                        obj = ds;
                        if (key != null) {
                            InstanceKeyDataSourceFactory.INSTANCE_MAP.put(key, ds);
                        }
                    }
                }
            }
        }
        return obj;
    }
    
    private void setCommonProperties(final Reference ref, final InstanceKeyDataSource ikds) throws IOException, ClassNotFoundException {
        RefAddr refAddr = ref.get("dataSourceName");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDataSourceName(refAddr.getContent().toString());
        }
        refAddr = ref.get("description");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDescription(refAddr.getContent().toString());
        }
        refAddr = ref.get("jndiEnvironment");
        if (refAddr != null && refAddr.getContent() != null) {
            final byte[] serialized = (byte[])refAddr.getContent();
            ikds.setJndiEnvironment((Properties)deserialize(serialized));
        }
        refAddr = ref.get("loginTimeout");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setLoginTimeout(Integer.parseInt(refAddr.getContent().toString()));
        }
        refAddr = ref.get("blockWhenExhausted");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultBlockWhenExhausted(Boolean.parseBoolean(refAddr.getContent().toString()));
        }
        refAddr = ref.get("evictionPolicyClassName");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultEvictionPolicyClassName(refAddr.getContent().toString());
        }
        refAddr = ref.get("lifo");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultLifo(Boolean.parseBoolean(refAddr.getContent().toString()));
        }
        refAddr = ref.get("maxIdlePerKey");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultMaxIdle(Integer.parseInt(refAddr.getContent().toString()));
        }
        refAddr = ref.get("maxTotalPerKey");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultMaxTotal(Integer.parseInt(refAddr.getContent().toString()));
        }
        refAddr = ref.get("maxWaitMillis");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultMaxWaitMillis(Long.parseLong(refAddr.getContent().toString()));
        }
        refAddr = ref.get("minEvictableIdleTimeMillis");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultMinEvictableIdleTimeMillis(Long.parseLong(refAddr.getContent().toString()));
        }
        refAddr = ref.get("minIdlePerKey");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultMinIdle(Integer.parseInt(refAddr.getContent().toString()));
        }
        refAddr = ref.get("numTestsPerEvictionRun");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultNumTestsPerEvictionRun(Integer.parseInt(refAddr.getContent().toString()));
        }
        refAddr = ref.get("softMinEvictableIdleTimeMillis");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultSoftMinEvictableIdleTimeMillis(Long.parseLong(refAddr.getContent().toString()));
        }
        refAddr = ref.get("testOnCreate");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultTestOnCreate(Boolean.parseBoolean(refAddr.getContent().toString()));
        }
        refAddr = ref.get("testOnBorrow");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultTestOnBorrow(Boolean.parseBoolean(refAddr.getContent().toString()));
        }
        refAddr = ref.get("testOnReturn");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultTestOnReturn(Boolean.parseBoolean(refAddr.getContent().toString()));
        }
        refAddr = ref.get("testWhileIdle");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultTestWhileIdle(Boolean.parseBoolean(refAddr.getContent().toString()));
        }
        refAddr = ref.get("timeBetweenEvictionRunsMillis");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultTimeBetweenEvictionRunsMillis(Long.parseLong(refAddr.getContent().toString()));
        }
        refAddr = ref.get("validationQuery");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setValidationQuery(refAddr.getContent().toString());
        }
        refAddr = ref.get("validationQueryTimeout");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setValidationQueryTimeout(Integer.parseInt(refAddr.getContent().toString()));
        }
        refAddr = ref.get("rollbackAfterValidation");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setRollbackAfterValidation(Boolean.parseBoolean(refAddr.getContent().toString()));
        }
        refAddr = ref.get("maxConnLifetimeMillis");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setMaxConnLifetimeMillis(Long.parseLong(refAddr.getContent().toString()));
        }
        refAddr = ref.get("defaultAutoCommit");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultAutoCommit(Boolean.valueOf(refAddr.getContent().toString()));
        }
        refAddr = ref.get("defaultTransactionIsolation");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultTransactionIsolation(Integer.parseInt(refAddr.getContent().toString()));
        }
        refAddr = ref.get("defaultReadOnly");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDefaultReadOnly(Boolean.valueOf(refAddr.getContent().toString()));
        }
    }
    
    protected abstract boolean isCorrectClass(final String p0);
    
    protected abstract InstanceKeyDataSource getNewInstance(final Reference p0) throws IOException, ClassNotFoundException;
    
    protected static final Object deserialize(final byte[] data) throws IOException, ClassNotFoundException {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new ByteArrayInputStream(data));
            return in.readObject();
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    static {
        INSTANCE_MAP = new ConcurrentHashMap<String, InstanceKeyDataSource>();
    }
}
