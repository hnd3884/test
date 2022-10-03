package com.me.devicemanagement.framework.webclient.quicklink;

import com.adventnet.persistence.xml.ConfigurationPopulationException;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import com.adventnet.ds.query.Join;
import java.util.Map;
import java.util.HashMap;
import com.adventnet.persistence.ActionInfo;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.WritableDataObject;
import java.util.Collection;
import java.util.logging.Level;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.persistence.xml.ConfigurationPopulator;

public class QuickLinkPopulator implements ConfigurationPopulator
{
    private static Logger out;
    
    private static Object getDefaultRoleFromDO(final DataObject data) throws DataAccessException {
        Object obj = null;
        final Column col = new Column("ArticleToRoleMapping", "ARTICLE_ID");
        final Criteria crit = new Criteria(col, (Object)(-1L), 0);
        final Iterator itr = data.getRows("ArticleToRoleMapping", crit);
        while (itr.hasNext()) {
            final Row row = itr.next();
            obj = row.get("ROLE_ID");
            data.deleteRow(row);
        }
        return obj;
    }
    
    private static ArrayList getArticleIDList(final DataObject data) throws DataAccessException {
        final ArrayList articleIDList = new ArrayList();
        try {
            final Iterator iter = data.getRows("DCQuickLink");
            while (iter.hasNext()) {
                final Row row = iter.next();
                articleIDList.add(row.get("ARTICLE_ID"));
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
        return articleIDList;
    }
    
    public static void updateDefaultRoleIDForArticles(final DataObject existDO, final DataObject diffDO) throws DataAccessException {
        Object defaultRoleFromExistDO = null;
        final ArrayList articleIDList = getArticleIDList(existDO);
        QuickLinkPopulator.out.log(Level.FINE, "  Article ID list from existing DO : " + articleIDList);
        defaultRoleFromExistDO = getDefaultRoleFromDO(existDO);
        QuickLinkPopulator.out.log(Level.FINE, "  Default Role from existing DO  " + defaultRoleFromExistDO);
        final ArrayList artileIDListFromDiffDO = getArticleIDList(diffDO);
        QuickLinkPopulator.out.log(Level.FINE, "  Article ID list from Diff DO :  " + artileIDListFromDiffDO);
        final Object defaultRoleFromDiffDO = getDefaultRoleFromDO(diffDO);
        QuickLinkPopulator.out.log(Level.FINE, "  Default Role from Diff DO  " + defaultRoleFromDiffDO);
        final ArrayList roleMappedIDListFromDiffDO = getRoleMappedArticleIDListFromDO(diffDO);
        ArrayList roleMappedIDList = new ArrayList();
        Object defaultRole = null;
        if (defaultRoleFromDiffDO != null && defaultRoleFromExistDO != null && !defaultRoleFromExistDO.equals(defaultRoleFromDiffDO)) {
            roleMappedIDList = getUserMappedRoleVSArticleIDListFromDB(defaultRoleFromExistDO);
            defaultRole = defaultRoleFromDiffDO;
        }
        else {
            roleMappedIDList = getAllRoleMappedArticleIDListFromDB();
            defaultRole = defaultRoleFromExistDO;
            if (defaultRole == null) {
                defaultRole = defaultRoleFromDiffDO;
            }
        }
        QuickLinkPopulator.out.log(Level.FINE, "  Role Mapped Article ID list : " + roleMappedIDList);
        QuickLinkPopulator.out.log(Level.FINE, "  defaultRole  : " + defaultRole);
        if (artileIDListFromDiffDO.size() > 0) {
            if (articleIDList != null && articleIDList.size() > 0) {
                articleIDList.removeAll(artileIDListFromDiffDO);
            }
            articleIDList.addAll(artileIDListFromDiffDO);
        }
        if (roleMappedIDListFromDiffDO.size() > 0) {
            if (roleMappedIDList != null && roleMappedIDList.size() > 0) {
                roleMappedIDList.removeAll(roleMappedIDListFromDiffDO);
            }
            roleMappedIDList.addAll(roleMappedIDListFromDiffDO);
        }
        if (articleIDList.size() > 0 && roleMappedIDList.size() > 0) {
            articleIDList.removeAll(roleMappedIDList);
        }
        QuickLinkPopulator.out.log(Level.FINE, " ArticleIDList to be mapped Default Role :  " + articleIDList);
        final DataObject updateDOObj = (DataObject)new WritableDataObject();
        if (articleIDList.size() > 0) {
            for (final Object obj : articleIDList) {
                final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("ArticleToRoleMapping"));
                sq.addSelectColumn(Column.getColumn("ArticleToRoleMapping", "ARTICLE_ID"));
                sq.addSelectColumn(Column.getColumn("ArticleToRoleMapping", "ROLE_ID"));
                Criteria crit = new Criteria(Column.getColumn("ArticleToRoleMapping", "ARTICLE_ID"), obj, 0);
                if (defaultRoleFromExistDO != null) {
                    crit = crit.and(new Criteria(Column.getColumn("ArticleToRoleMapping", "ROLE_ID"), defaultRoleFromExistDO, 0));
                }
                sq.setCriteria(crit);
                final DataObject doObj = DataAccess.get((SelectQuery)sq);
                if (doObj.isEmpty()) {
                    final Row articleRoleRow = new Row("ArticleToRoleMapping");
                    articleRoleRow.set("ARTICLE_ID", obj);
                    articleRoleRow.set("ROLE_ID", defaultRole);
                    diffDO.addRow(articleRoleRow);
                }
                else {
                    final Row articleRoleRow = new Row("ArticleToRoleMapping");
                    articleRoleRow.set("ARTICLE_ID", obj);
                    articleRoleRow.set("ROLE_ID", defaultRoleFromExistDO);
                    updateDOObj.addRow(articleRoleRow);
                }
            }
        }
        if (!updateDOObj.isEmpty()) {
            ((WritableDataObject)updateDOObj).clearOperations();
            final Iterator iter = updateDOObj.getRows("ArticleToRoleMapping");
            while (iter.hasNext()) {
                final Row updateRow = iter.next();
                updateRow.set("ROLE_ID", defaultRole);
                updateDOObj.updateRow(updateRow);
            }
            diffDO.merge(updateDOObj);
        }
        final ArrayList articleToRoleMappingInsertIDs = getModifiedArticleToRoleMappingIDs(diffDO, "insert");
        final ArrayList articleToRoleMappingDeleteIDs = getModifiedArticleToRoleMappingIDs(diffDO, "delete");
        DataAccess.update(diffDO);
        if (defaultRoleFromDiffDO == null) {
            deleteExistingDefaultRoleMappedEntries(roleMappedIDList, defaultRoleFromExistDO);
        }
        if (defaultRoleFromExistDO != null && articleToRoleMappingInsertIDs != null && articleToRoleMappingInsertIDs.size() > 0) {
            deleteExistingDefaultRoleEntriesForNewlyRoleMappedArticleIDs(articleToRoleMappingInsertIDs, defaultRoleFromExistDO);
        }
        if (defaultRoleFromDiffDO != null && articleToRoleMappingDeleteIDs != null && articleToRoleMappingDeleteIDs.size() > 0) {
            updateDefaultRoleEntriesForRemovedRoleMappedArticleID(articleToRoleMappingDeleteIDs, defaultRoleFromDiffDO);
        }
    }
    
    public static ArrayList getAllRoleMappedArticleIDListFromDB() throws DataAccessException {
        final ArrayList roleMappedArticleIDList = new ArrayList();
        try {
            final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("ArticleToRoleMapping"));
            sq.addSelectColumn(Column.getColumn("ArticleToRoleMapping", "ARTICLE_ID"));
            sq.addSelectColumn(Column.getColumn("ArticleToRoleMapping", "ROLE_ID"));
            final DataObject doObj = DataAccess.get((SelectQuery)sq);
            final Iterator iter = doObj.getRows("ArticleToRoleMapping");
            while (iter.hasNext()) {
                final Row row = iter.next();
                roleMappedArticleIDList.add(row.get("ARTICLE_ID"));
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
        return roleMappedArticleIDList;
    }
    
    public static ArrayList getUserMappedRoleVSArticleIDListFromDB(final Object defaultRoleID) throws DataAccessException {
        final ArrayList roleMappedArticleIDList = new ArrayList();
        try {
            final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("ArticleToRoleMapping"));
            sq.addSelectColumn(Column.getColumn("ArticleToRoleMapping", "ARTICLE_ID"));
            sq.addSelectColumn(Column.getColumn("ArticleToRoleMapping", "ROLE_ID"));
            sq.setCriteria(new Criteria(Column.getColumn("ArticleToRoleMapping", "ROLE_ID"), defaultRoleID, 1));
            final DataObject doObj = DataAccess.get((SelectQuery)sq);
            final Iterator iter = doObj.getRows("ArticleToRoleMapping");
            while (iter.hasNext()) {
                final Row row = iter.next();
                roleMappedArticleIDList.add(row.get("ARTICLE_ID"));
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
        return roleMappedArticleIDList;
    }
    
    private static ArrayList getRoleMappedArticleIDListFromDO(final DataObject data) throws DataAccessException {
        final ArrayList roleMappedArticleIDList = new ArrayList();
        try {
            final Iterator iter = data.getRows("ArticleToRoleMapping");
            while (iter.hasNext()) {
                final Row row = iter.next();
                roleMappedArticleIDList.add(row.get("ARTICLE_ID"));
            }
        }
        catch (final Exception ex) {
            throw ex;
        }
        return roleMappedArticleIDList;
    }
    
    private static ArrayList getModifiedArticleToRoleMappingIDs(final DataObject data, final String action) {
        final Map actionRows = ((WritableDataObject)data).getActionsFor(action);
        final ArrayList modifiedArticleToRoleMappingIDs = new ArrayList();
        if (actionRows != null && actionRows.size() > 0) {
            final List<ActionInfo> infos = actionRows.get("ArticleToRoleMapping");
            final Map<String, Integer> hm = new HashMap<String, Integer>();
            if (infos != null) {
                for (int i = 0; i < infos.size(); ++i) {
                    final ActionInfo actionInfo = infos.get(i);
                    if (actionInfo != null) {
                        final Row row = actionInfo.getValue();
                        final String tablename;
                        if (row != null && (tablename = row.getTableName()) != null && tablename.equalsIgnoreCase("ArticleToRoleMapping")) {
                            modifiedArticleToRoleMappingIDs.add(row.get("ARTICLE_ID"));
                        }
                    }
                }
            }
            if (action.equals("delete")) {
                final ArrayList deletedQuickLinkArticleIDs = new ArrayList();
                final List<ActionInfo> deleteInfos = actionRows.get("DCQuickLink");
                if (deleteInfos != null) {
                    for (int j = 0; j < deleteInfos.size(); ++j) {
                        final ActionInfo actionInfo2 = deleteInfos.get(j);
                        if (actionInfo2 != null) {
                            final Row row2 = actionInfo2.getValue();
                            final String tablename2;
                            if (row2 != null && (tablename2 = row2.getTableName()) != null && tablename2.equalsIgnoreCase("DCQuickLink")) {
                                deletedQuickLinkArticleIDs.add(row2.get("ARTICLE_ID"));
                            }
                        }
                    }
                }
                if (deletedQuickLinkArticleIDs != null && deletedQuickLinkArticleIDs.size() > 0) {
                    modifiedArticleToRoleMappingIDs.removeAll(deletedQuickLinkArticleIDs);
                }
            }
        }
        return modifiedArticleToRoleMappingIDs;
    }
    
    private static void deleteExistingDefaultRoleMappedEntries(final ArrayList roleMappedIDListFromDiffDO, final Object defaultRole) throws DataAccessException {
        QuickLinkPopulator.out.log(Level.FINE, " Artcile ID List to be Removed Old Default Entries  " + roleMappedIDListFromDiffDO);
        if (roleMappedIDListFromDiffDO != null && roleMappedIDListFromDiffDO.size() > 0 && roleMappedIDListFromDiffDO != null && roleMappedIDListFromDiffDO.size() > 0) {
            final Object[] articleArray = roleMappedIDListFromDiffDO.toArray();
            Criteria crit = new Criteria(Column.getColumn("ArticleToRoleMapping", "ARTICLE_ID"), (Object)articleArray, 8);
            crit = crit.and(new Criteria(Column.getColumn("ArticleToRoleMapping", "ROLE_ID"), defaultRole, 0));
            DataAccess.delete("ArticleToRoleMapping", crit);
        }
    }
    
    private static void deleteExistingDefaultRoleEntriesForNewlyRoleMappedArticleIDs(final ArrayList roleMappedIDListFromDiffDO, final Object defaultRoleFromDiffDO) throws DataAccessException {
        deleteExistingDefaultRoleMappedEntries(roleMappedIDListFromDiffDO, defaultRoleFromDiffDO);
    }
    
    private static void updateDefaultRoleEntriesForRemovedRoleMappedArticleID(final ArrayList roleMappedIDListFromDiffDO, final Object defaultRole) throws DataAccessException {
        final ArrayList defaultRoleMappedIDList = new ArrayList();
        defaultRoleMappedIDList.add(-1L);
        if (roleMappedIDListFromDiffDO != null && roleMappedIDListFromDiffDO.size() > 0) {
            final DataObject updateDO = (DataObject)new WritableDataObject();
            for (final Object obj : roleMappedIDListFromDiffDO) {
                if (!defaultRoleMappedIDList.contains(obj)) {
                    final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("ArticleToRoleMapping"));
                    sq.addSelectColumn(Column.getColumn("ArticleToRoleMapping", "ARTICLE_ID"));
                    sq.addSelectColumn(Column.getColumn("ArticleToRoleMapping", "ROLE_ID"));
                    sq.addSelectColumn(Column.getColumn("DCQuickLink", "ARTICLE_ID"));
                    sq.addJoin(new Join("ArticleToRoleMapping", "DCQuickLink", new String[] { "ARTICLE_ID" }, new String[] { "ARTICLE_ID" }, 2));
                    Criteria crit = new Criteria(Column.getColumn("ArticleToRoleMapping", "ARTICLE_ID"), obj, 0);
                    final Criteria productCodeCriteria = EMSProductUtil.constructProductCodeCriteria("DCQuickLink", "PRODUCT_CODE");
                    crit = crit.and(productCodeCriteria);
                    sq.setCriteria(crit);
                    final DataObject doObj = DataAccess.get((SelectQuery)sq);
                    if (doObj.isEmpty()) {
                        final Row articleRoleRow = new Row("ArticleToRoleMapping");
                        articleRoleRow.set("ARTICLE_ID", obj);
                        articleRoleRow.set("ROLE_ID", defaultRole);
                        updateDO.addRow(articleRoleRow);
                    }
                }
                defaultRoleMappedIDList.add(obj);
            }
            if (!updateDO.isEmpty()) {
                DataAccess.update(updateDO);
            }
        }
    }
    
    public void populate(final DataObject data) throws ConfigurationPopulationException {
        this.populateDefaultRoleForArticles(data);
    }
    
    public void update(final DataObject data) throws ConfigurationPopulationException {
    }
    
    private void populateDefaultRoleForArticles(DataObject data) {
        try {
            data = this.generateDefaultRoleIDForArticles(data);
            DataAccess.add(data);
        }
        catch (final Exception ex) {
            QuickLinkPopulator.out.log(Level.WARNING, "Exception while populationg Default roles for article ID's. Exception : ", ex);
        }
    }
    
    private DataObject generateDefaultRoleIDForArticles(final DataObject data) throws DataAccessException {
        final ArrayList articleIDList = getArticleIDList(data);
        if (articleIDList != null && articleIDList.size() > 0) {
            final ArrayList roleMappedarticleIDList = new ArrayList();
            Object defaultRole = null;
            final Iterator itr = data.getRows("ArticleToRoleMapping");
            while (itr.hasNext()) {
                final Row r = itr.next();
                roleMappedarticleIDList.add(r.get("ARTICLE_ID"));
            }
            if (roleMappedarticleIDList != null && roleMappedarticleIDList.size() > 0) {
                articleIDList.removeAll(roleMappedarticleIDList);
            }
            if (articleIDList != null && articleIDList.size() > 0) {
                if (roleMappedarticleIDList.contains(-1L)) {
                    defaultRole = getDefaultRoleFromDO(data);
                }
                final Iterator iterator = articleIDList.iterator();
                while (iterator.hasNext()) {
                    final Row articleRoleRow = new Row("ArticleToRoleMapping");
                    articleRoleRow.set("ARTICLE_ID", iterator.next());
                    articleRoleRow.set("ROLE_ID", defaultRole);
                    data.addRow(articleRoleRow);
                }
            }
        }
        return data;
    }
    
    static {
        QuickLinkPopulator.out = Logger.getLogger(QuickLinkPopulator.class.getName());
    }
}
