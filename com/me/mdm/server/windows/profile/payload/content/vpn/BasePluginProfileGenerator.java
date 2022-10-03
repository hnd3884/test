package com.me.mdm.server.windows.profile.payload.content.vpn;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import org.apache.axiom.om.OMElement;

public class BasePluginProfileGenerator implements PluginProfileGenerator
{
    OMElement root;
    String appIdentifier;
    
    public BasePluginProfileGenerator(final String identifer) {
        this.root = null;
        this.appIdentifier = null;
        this.appIdentifier = identifer;
    }
    
    @Override
    public HashMap createPluginProfileData(final DataObject dataObject, final Long configDataItemID) {
        final HashMap hashMap = new HashMap();
        try {
            final Row sslrow = dataObject.getRow("VpnCustomSSL", new Criteria(Column.getColumn("VpnCustomSSL", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0));
            final String server = (String)sslrow.get("SERVER_NAME");
            if (server.matches(".*:[0-9]*")) {
                final String[] split = server.split(":");
                final String port = split[split.length - 1];
                hashMap.put("port", port);
            }
            this.appIdentifier = (String)sslrow.get("IDENTIFIER");
            final Iterator iterator = dataObject.getRows("VpnCustomData", new Criteria(Column.getColumn("VpnCustomData", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0));
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String key = (String)row.get("KEY");
                final String value = (String)row.get("VALUE");
                hashMap.put(key, value);
            }
        }
        catch (final DataAccessException e) {
            e.printStackTrace();
        }
        return hashMap;
    }
    
    @Override
    public void generatePluginXML(final HashMap hashMap) {
        final Iterator iterator = hashMap.keySet().iterator();
        if (this.root != null) {
            while (iterator.hasNext()) {
                final String key = iterator.next();
                final String value = hashMap.get(key);
                final OMElement curElement = OMAbstractFactory.getOMFactory().createOMElement(key, (OMNamespace)null);
                curElement.setText(value);
                this.root.addChild((OMNode)curElement);
            }
        }
    }
    
    @Override
    public void createRootElement() {
        this.root = null;
    }
    
    @Override
    public String toString() {
        String xml = null;
        if (this.root != null) {
            xml = this.root.toString();
        }
        return xml;
    }
}
