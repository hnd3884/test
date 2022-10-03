package com.adventnet.client.components.form.web;

import com.adventnet.customview.ViewData;
import com.adventnet.customview.CustomViewManager;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Column;
import com.adventnet.client.cache.StaticCache;
import javax.swing.table.TableModel;
import com.adventnet.db.persistence.metadata.AllowedValues;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.List;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.ArrayList;
import java.util.HashMap;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class DiscreteFormTransformer extends DefaultTransformer
{
    @Override
    public void renderCell(final TransformerContext tableContext) throws Exception {
        final HashMap<String, Object> columnProperties = tableContext.getRenderedAttributes();
        Object data = tableContext.getPropertyValue();
        final HashMap<String, String> propHash = (HashMap<String, String>)tableContext.getRendererConfigProps().clone();
        if (propHash != null) {
            final String refTable = propHash.remove("ReferenceTable");
            final String serVal = propHash.remove("ServerValue");
            final String cliVal = propHash.remove("ClientValue");
            final List<String> serverList = new ArrayList<String>();
            final List<String> clientList = new ArrayList<String>();
            if (cliVal != null && "_ALLOWED_VALUES".equals(cliVal)) {
                final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(refTable);
                final ColumnDefinition columnDef = tableDef.getColumnDefinitionByName(serVal);
                final AllowedValues allVal = columnDef.getAllowedValues();
                final List<Object> list = allVal.getValueList();
                for (int i = 0; i < list.size(); ++i) {
                    final Object srvobj = list.get(i);
                    if (srvobj != null) {
                        serverList.add(srvobj.toString());
                        clientList.add(srvobj.toString());
                    }
                }
            }
            else {
                final TableModel refTableModel = this.getReferencedTableModel(refTable, serVal, cliVal);
                for (int size = refTableModel.getRowCount(), j = 0; j < size; ++j) {
                    if (refTableModel.getValueAt(j, 0) != null) {
                        serverList.add(refTableModel.getValueAt(j, 0).toString());
                        if (cliVal != null && refTableModel.getValueAt(j, 1) != null) {
                            clientList.add(refTableModel.getValueAt(j, 1).toString());
                            if (data != null && refTableModel.getValueAt(j, 0).toString().equals(data.toString())) {
                                data = refTableModel.getValueAt(j, 1);
                            }
                        }
                        else {
                            clientList.add(refTableModel.getValueAt(j, 0).toString());
                        }
                    }
                }
            }
            columnProperties.put("VALUE", data);
            columnProperties.put("SERVER_VALUE", serverList);
            columnProperties.put("CLIENT_VALUE", clientList);
        }
    }
    
    public TableModel getReferencedTableModel(final String tableName, final String serverColumn, final String clientColumn) throws Exception {
        TableModel tnm = (TableModel)StaticCache.getFromCache((Object)("FORM_DISCRETE_DATA:" + tableName));
        if (tnm == null) {
            final Column column = new Column(tableName, serverColumn);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table(tableName));
            query.addSelectColumn(column.distinct());
            if (clientColumn != null) {
                final Column ccolumn = new Column(tableName, clientColumn);
                query.addSelectColumn(ccolumn);
            }
            query.setRange(new Range(1, 0));
            final CustomViewRequest cvRequest = new CustomViewRequest(query);
            final CustomViewManager cvMgr = LookUpUtil.getCVManagerForTable();
            final ViewData viewData = cvMgr.getData(cvRequest);
            tnm = (TableModel)viewData.getModel();
            final ArrayList list = new ArrayList();
            list.add(tableName);
            StaticCache.addToCache((Object)("FORM_DISCRETE_DATA:" + tableName), (Object)tnm, (List)list);
        }
        return tnm;
    }
}
