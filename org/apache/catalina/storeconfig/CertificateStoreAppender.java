package org.apache.catalina.storeconfig;

import org.apache.tomcat.util.IntrospectionUtils;
import java.beans.PropertyDescriptor;

public class CertificateStoreAppender extends StoreAppender
{
    @Override
    protected Object checkAttribute(final StoreDescription desc, final PropertyDescriptor descriptor, final String attributeName, final Object bean, final Object bean2) {
        if (attributeName.equals("type")) {
            return IntrospectionUtils.getProperty(bean, descriptor.getName());
        }
        return super.checkAttribute(desc, descriptor, attributeName, bean, bean2);
    }
}
