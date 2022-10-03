package com.me.mdm.onpremise.server.agent;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AgentSecretsHandler
{
    private static AgentSecretsHandler handler;
    public static final int FCM_SERVER_KEY_CONSTANT = 1;
    public static final int FCM_AGENT_DETAILS_CONSTANT = 2;
    private Logger logger;
    
    public AgentSecretsHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static AgentSecretsHandler getInstance() {
        return AgentSecretsHandler.handler;
    }
    
    public void addOrUpdateAgentSecret(final int secretType, final JSONObject object) {
        try {
            this.logger.log(Level.INFO, "Going to add or update the secret:{0}", new Object[] { secretType });
            final DataObject dataObject = this.getAgentSecretDO(secretType);
            Row row = null;
            if (dataObject.isEmpty()) {
                row = new Row("AgentSecrets");
                row.set("AGENT_SECRET_TYPE", (Object)secretType);
                dataObject.addRow(row);
            }
            else {
                row = dataObject.getRow("AgentSecrets");
                final Iterator valueRows = dataObject.getRows("AgentSecretsDetails");
                final List<Long> detailIds = new ArrayList<Long>();
                while (valueRows.hasNext()) {
                    final Row detailRow = valueRows.next();
                    detailIds.add((Long)detailRow.get("AGENT_SECRET_DETAILS_ID"));
                }
                dataObject.deleteRows("AgentSecretsDetails", new Criteria(new Column("AgentSecretsDetails", "AGENT_SECRET_DETAILS_ID"), (Object)detailIds.toArray(), 8));
            }
            final Object secretId = row.get("AGENT_SECRET_ID");
            final Iterator keys = object.keys();
            while (keys.hasNext()) {
                final Row details = new Row("AgentSecretsDetails");
                final String key = keys.next();
                final String value = object.getString(key);
                details.set("AGENT_SECRET_ID", secretId);
                details.set("KEY", (Object)key);
                details.set("VALUE", (Object)value);
                dataObject.addRow(details);
            }
            MDMUtil.getPersistenceLite().update(dataObject);
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in adding agent secrets", (Throwable)ex);
        }
    }
    
    public JSONObject getAgentSecret(final int secretType) {
        final JSONObject jsonObject = new JSONObject();
        try {
            final DataObject dataObject = this.getAgentSecretDO(secretType);
            if (!dataObject.isEmpty()) {
                final Iterator rows = dataObject.getRows("AgentSecretsDetails");
                while (rows.hasNext()) {
                    final Row agentRow = rows.next();
                    final String key = (String)agentRow.get("KEY");
                    final String value = (String)agentRow.get("VALUE");
                    jsonObject.put(key, (Object)value);
                }
            }
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in getting agent secret", (Throwable)ex);
        }
        return jsonObject;
    }
    
    private DataObject getAgentSecretDO(final int secretType) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AgentSecrets"));
        selectQuery.addJoin(new Join("AgentSecrets", "AgentSecretsDetails", new String[] { "AGENT_SECRET_ID" }, new String[] { "AGENT_SECRET_ID" }, 2));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final Criteria criteria = new Criteria(new Column("AgentSecrets", "AGENT_SECRET_TYPE"), (Object)secretType, 0);
        selectQuery.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        return dataObject;
    }
    
    static {
        AgentSecretsHandler.handler = new AgentSecretsHandler();
    }
}
