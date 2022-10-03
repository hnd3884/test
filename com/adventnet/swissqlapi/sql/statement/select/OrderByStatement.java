package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import java.util.ArrayList;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;

public class OrderByStatement
{
    private String OrderClause;
    private Vector OrderItemList;
    private UserObjectContext context;
    private CommentClass commentObj;
    private CommentClass commentObjAfterToken;
    private String siblings;
    
    public OrderByStatement() {
        this.context = null;
        this.OrderClause = new String("");
        this.OrderItemList = new Vector();
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setOrderClause(final String s_oc) {
        this.OrderClause = s_oc;
    }
    
    public void setOrderItemList(final Vector v_oil) {
        this.OrderItemList = v_oil;
    }
    
    public void setSiblings(final String sib) {
        this.siblings = sib;
    }
    
    public String getSiblings() {
        return this.siblings;
    }
    
    public void addOrderItems(final Vector orderItems) {
        if (this.OrderItemList != null) {
            if (orderItems != null) {
                for (int i = 0; i < orderItems.size(); ++i) {
                    this.OrderItemList.add(orderItems.get(i));
                }
            }
        }
        else {
            this.OrderItemList = orderItems;
        }
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public void setCommentClassAfterToken(final CommentClass commentObj) {
        this.commentObjAfterToken = commentObj;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public Vector getOrderItemList() {
        return this.OrderItemList;
    }
    
    public OrderByStatement toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderByStatement obs = new OrderByStatement();
        OrderItem oi = new OrderItem();
        if (this.OrderClause != null) {
            obs.setOrderClause(this.OrderClause);
        }
        final Vector v_oil = new Vector();
        if (this.OrderItemList.size() == 1) {
            oi = this.OrderItemList.elementAt(0).toANSISelect(to_sqs, from_sqs);
            if (oi != null) {
                v_oil.addElement(oi);
                obs.setOrderItemList(v_oil);
            }
            else {
                obs = null;
            }
        }
        else {
            for (int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
                oi = this.OrderItemList.elementAt(i_count).toANSISelect(to_sqs, from_sqs);
                if (oi != null) {
                    v_oil.addElement(oi);
                }
            }
            obs.setOrderItemList(v_oil);
        }
        return obs;
    }
    
    public OrderByStatement toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderByStatement obs = new OrderByStatement();
        OrderItem oi = new OrderItem();
        if (this.OrderClause != null) {
            obs.setOrderClause(this.OrderClause);
        }
        final Vector v_oil = new Vector();
        for (int count = 0; count < this.OrderItemList.size(); ++count) {
            final OrderItem tempOI = this.OrderItemList.get(count);
            if (tempOI.getNullsOrder() != null) {
                final String nullsOrder = tempOI.getNullsOrder().trim().toUpperCase();
                final String orderString = tempOI.getOrder();
                if (orderString != null) {
                    if (orderString.trim().equalsIgnoreCase("ASC") || orderString.trim().equalsIgnoreCase("DESC")) {
                        if (orderString.trim().equalsIgnoreCase("ASC") && nullsOrder.indexOf("LAST") != -1) {
                            this.addCaseStatementToOrderItemsList(count, tempOI, "1", "0");
                            ++count;
                        }
                        else if (orderString.trim().equalsIgnoreCase("DESC") && nullsOrder.indexOf("FIRST") != -1) {
                            this.addCaseStatementToOrderItemsList(count, tempOI, "0", "1");
                            ++count;
                        }
                    }
                }
                else if (nullsOrder != null && nullsOrder.indexOf("LAST") != -1) {
                    this.addCaseStatementToOrderItemsList(count, tempOI, "1", "0");
                    ++count;
                }
            }
        }
        if (this.OrderItemList.size() == 1) {
            oi = this.OrderItemList.elementAt(0).toTeradataSelect(to_sqs, from_sqs);
            if (oi != null) {
                v_oil.addElement(oi);
                obs.setOrderItemList(v_oil);
            }
            else {
                obs = null;
            }
        }
        else {
            for (int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
                final Object obj = this.OrderItemList.elementAt(i_count);
                if (obj instanceof CaseStatement) {
                    final CaseStatement cs = this.OrderItemList.elementAt(i_count);
                    v_oil.addElement(cs.toTeradataSelect(to_sqs, from_sqs));
                }
                else if (obj instanceof OrderItem) {
                    oi = this.OrderItemList.elementAt(i_count).toTeradataSelect(to_sqs, from_sqs);
                    if (oi != null) {
                        v_oil.addElement(oi);
                    }
                }
                else {
                    v_oil.addElement(this.OrderItemList.elementAt(i_count));
                }
            }
            obs.setOrderItemList(v_oil);
        }
        return obs;
    }
    
    public OrderByStatement toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final OrderByStatement obs = new OrderByStatement();
        if (this.OrderClause != null) {
            obs.setOrderClause(this.OrderClause);
        }
        final Vector v_oil = new Vector();
        for (int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
            v_oil.addElement(this.OrderItemList.elementAt(i_count).toPostgreSQLSelect(to_sqs, from_sqs));
        }
        obs.setOrderItemList(v_oil);
        return obs;
    }
    
    public OrderByStatement toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderByStatement obs = new OrderByStatement();
        obs.setCommentClassAfterToken(this.commentObjAfterToken);
        OrderItem oi = new OrderItem();
        if (this.OrderClause != null) {
            obs.setOrderClause(this.OrderClause);
        }
        final Vector v_oil = new Vector();
        if (this.OrderItemList.size() == 1) {
            oi = this.OrderItemList.elementAt(0).toMySQLSelect(to_sqs, from_sqs);
            if (oi != null) {
                v_oil.addElement(oi);
                obs.setOrderItemList(v_oil);
            }
            else {
                obs = null;
            }
        }
        else {
            for (int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
                oi = this.OrderItemList.elementAt(i_count).toMySQLSelect(to_sqs, from_sqs);
                if (oi != null) {
                    v_oil.addElement(oi);
                }
            }
            obs.setOrderItemList(v_oil);
        }
        return obs;
    }
    
    public OrderByStatement toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderByStatement obs = new OrderByStatement();
        OrderItem oi = new OrderItem();
        if (this.OrderClause != null) {
            obs.setOrderClause(this.OrderClause);
        }
        final Vector v_oil = new Vector();
        if (this.OrderItemList.size() == 1) {
            oi = this.OrderItemList.elementAt(0).toDB2Select(to_sqs, from_sqs);
            if (oi != null) {
                v_oil.addElement(oi);
                obs.setOrderItemList(v_oil);
            }
            else {
                obs = null;
            }
        }
        else {
            for (int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
                oi = this.OrderItemList.elementAt(i_count).toDB2Select(to_sqs, from_sqs);
                if (oi != null) {
                    v_oil.addElement(oi);
                }
            }
            obs.setOrderItemList(v_oil);
        }
        return obs;
    }
    
    public OrderByStatement toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final SelectStatement selStmt = from_sqs.getSelectStatement();
        final String selectQualifier = selStmt.getSelectQualifier();
        boolean starIsThere = false;
        if (SwisSQLOptions.RemoveOrderByColumnWhenColumnNotInSelectList) {
            if (selectQualifier != null && selectQualifier.toLowerCase().startsWith("distinct")) {
                final Vector selectColumnList = selStmt.getSelectItemList();
                final Vector v = new Vector();
                for (int i = 0; i < this.OrderItemList.size(); ++i) {
                    final OrderItem oi = this.OrderItemList.get(i);
                    final SelectColumn oc = oi.getOrderSpecifier();
                    final int orderListExprSize = oc.getColumnExpression().size();
                    final Object o = oc.getColumnExpression().get(0);
                    if (o instanceof TableColumn) {
                        final TableColumn t1 = (TableColumn)o;
                        final String orderByListColumnName = t1.getColumnName();
                        for (int j = 0; j < selectColumnList.size(); ++j) {
                            final SelectColumn sc = selectColumnList.get(j);
                            final Object ob = sc.getColumnExpression().get(0);
                            if (ob instanceof TableColumn) {
                                final TableColumn t2 = (TableColumn)ob;
                                String s1;
                                final String selectColumnListName = s1 = t2.getColumnName();
                                String s2 = orderByListColumnName;
                                if (selectColumnListName.startsWith("'")) {
                                    s1 = selectColumnListName.replaceAll("'", "");
                                }
                                if (selectColumnListName.startsWith("\"")) {
                                    s1 = selectColumnListName.replaceAll("\"", "");
                                }
                                if (orderByListColumnName.startsWith("\"")) {
                                    s2 = orderByListColumnName.replaceAll("\"", "");
                                }
                                if (orderByListColumnName.startsWith("'")) {
                                    s2 = orderByListColumnName.replaceAll("'", "");
                                }
                                if (s1.equalsIgnoreCase(s2)) {
                                    v.add(oi);
                                    break;
                                }
                            }
                            else if (ob instanceof String) {
                                final String selectColumnListName = (String)ob;
                                if (selectColumnListName.equals("*")) {
                                    starIsThere = true;
                                }
                            }
                        }
                    }
                }
                if (!starIsThere) {
                    if (v.size() == 0) {
                        return null;
                    }
                    this.OrderItemList = v;
                }
            }
        }
        else {
            Vector selectColumnList = new Vector();
            if (selectQualifier != null && selectQualifier.toLowerCase().startsWith("distinct")) {
                selectColumnList = selStmt.getSelectItemList();
                final Vector v2 = new Vector();
                for (int k = 0; k < this.OrderItemList.size(); ++k) {
                    boolean presenceFlag = false;
                    final OrderItem oi2 = this.OrderItemList.get(k);
                    final SelectColumn oc2 = oi2.getOrderSpecifier();
                    final int orderListExprSize2 = oc2.getColumnExpression().size();
                    final Object o2 = oc2.getColumnExpression().get(0);
                    if (o2 instanceof TableColumn) {
                        final TableColumn t3 = (TableColumn)o2;
                        final String orderByListColumnName = t3.getColumnName();
                        for (int l = 0; l < selectColumnList.size(); ++l) {
                            final Object o3 = selectColumnList.get(l);
                            if (o3 instanceof SelectColumn) {
                                final SelectColumn sc2 = selectColumnList.get(l);
                                final Object ob2 = sc2.getColumnExpression().get(0);
                                if (ob2 instanceof TableColumn) {
                                    final TableColumn t4 = (TableColumn)ob2;
                                    String s3;
                                    final String selectColumnListName = s3 = t4.getColumnName();
                                    String s4 = orderByListColumnName;
                                    if (selectColumnListName.startsWith("'")) {
                                        s3 = selectColumnListName.replaceAll("'", "");
                                    }
                                    if (selectColumnListName.startsWith("\"")) {
                                        s3 = selectColumnListName.replaceAll("\"", "");
                                    }
                                    if (orderByListColumnName.startsWith("\"")) {
                                        s4 = orderByListColumnName.replaceAll("\"", "");
                                    }
                                    if (orderByListColumnName.startsWith("'")) {
                                        s4 = orderByListColumnName.replaceAll("'", "");
                                    }
                                    if (s3.equalsIgnoreCase(s4)) {
                                        presenceFlag = true;
                                        break;
                                    }
                                }
                                else if (ob2 instanceof String) {
                                    final String selectColumnListName = (String)ob2;
                                    if (selectColumnListName.equals("*")) {
                                        starIsThere = true;
                                    }
                                }
                            }
                        }
                        if (!presenceFlag && !starIsThere) {
                            selectColumnList.get(selectColumnList.size() - 1).setEndsWith(",");
                            final SelectColumn addSelColumn = new SelectColumn();
                            addSelColumn.setColumnExpression(oc2.getColumnExpression());
                            selectColumnList.add(addSelColumn);
                        }
                    }
                }
                selStmt.setSelectItemList(selectColumnList);
                to_sqs.setSelectStatement(selStmt.toMSSQLServerSelect(to_sqs, from_sqs));
            }
        }
        OrderByStatement obs = new OrderByStatement();
        OrderItem oi3 = new OrderItem();
        if (this.OrderClause != null) {
            obs.setOrderClause(this.OrderClause);
        }
        final Vector v_oil = new Vector();
        if (this.OrderItemList.size() == 1) {
            final OrderItem ordItem = this.OrderItemList.elementAt(0);
            SwisSQLUtils.checkAndReplaceGroupByItem(ordItem.getOrderSpecifier(), from_sqs);
            oi3 = ordItem.toMSSQLServerSelect(to_sqs, from_sqs);
            if (oi3 != null) {
                this.convertOrdinalNumberToColumnOrderBy(oi3, from_sqs);
                v_oil.addElement(oi3.toMSSQLServerSelect(to_sqs, from_sqs));
                obs.setOrderItemList(v_oil);
            }
            else {
                obs = null;
            }
        }
        else {
            for (int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
                final OrderItem ordItem2 = this.OrderItemList.elementAt(i_count);
                SwisSQLUtils.checkAndReplaceGroupByItem(ordItem2.getOrderSpecifier(), from_sqs);
                oi3 = ordItem2.toMSSQLServerSelect(to_sqs, from_sqs);
                if (oi3 != null) {
                    this.convertOrdinalNumberToColumnOrderBy(oi3, from_sqs);
                    v_oil.addElement(oi3.toMSSQLServerSelect(to_sqs, from_sqs));
                }
            }
            obs.setOrderItemList(v_oil);
        }
        return obs;
    }
    
    public OrderByStatement toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderByStatement obs = new OrderByStatement();
        obs.setObjectContext(this.context);
        OrderItem oi = new OrderItem();
        if (this.OrderClause != null) {
            obs.setOrderClause(this.OrderClause);
        }
        final Vector v_oil = new Vector();
        if (this.OrderItemList.size() == 1) {
            oi = this.OrderItemList.elementAt(0).toSybaseSelect(to_sqs, from_sqs);
            if (oi != null) {
                oi.setObjectContext(this.context);
                v_oil.addElement(oi);
                obs.setOrderItemList(v_oil);
            }
            else {
                obs = null;
            }
        }
        else {
            for (int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
                oi = this.OrderItemList.elementAt(i_count).toSybaseSelect(to_sqs, from_sqs);
                if (oi != null) {
                    oi.setObjectContext(this.context);
                    v_oil.addElement(oi);
                }
            }
            obs.setOrderItemList(v_oil);
        }
        return obs;
    }
    
    public OrderByStatement toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderByStatement obs = new OrderByStatement();
        obs.setCommentClass(this.commentObj);
        OrderItem oi = new OrderItem();
        if (this.OrderClause != null) {
            obs.setOrderClause(this.OrderClause);
        }
        final Vector v_oil = new Vector();
        obs.setObjectContext(this.context);
        if (this.OrderItemList.size() == 1) {
            oi = this.OrderItemList.elementAt(0).toOracleSelect(to_sqs, from_sqs);
            if (oi != null) {
                this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs);
                v_oil.addElement(oi.toOracleSelect(to_sqs, from_sqs));
                obs.setOrderItemList(v_oil);
            }
            else {
                obs = null;
            }
        }
        else {
            for (int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
                oi = this.OrderItemList.elementAt(i_count).toOracleSelect(to_sqs, from_sqs);
                if (oi != null) {
                    this.convertOrdinalNumberToColumnOrderBy(oi, from_sqs);
                    v_oil.addElement(oi.toOracleSelect(to_sqs, from_sqs));
                }
            }
            obs.setOrderItemList(v_oil);
        }
        final ArrayList numberColList = new ArrayList();
        if (!numberColList.isEmpty() && numberColList.size() == obs.getOrderItemList().size()) {
            for (int i = 0; i < obs.getOrderItemList().size(); ++i) {
                if (obs.getOrderItemList().get(i) instanceof OrderItem) {
                    final OrderItem orderItem = obs.getOrderItemList().get(i);
                    final SelectColumn sc = orderItem.getOrderSpecifier();
                    if (sc.getColumnExpression() != null) {
                        final Vector columnExpression = sc.getColumnExpression();
                        for (int j = 0; j < columnExpression.size(); ++j) {
                            if (columnExpression.get(j) instanceof TableColumn) {
                                columnExpression.set(j, numberColList.get(i));
                            }
                        }
                    }
                }
            }
        }
        return obs;
    }
    
    private void convertOrdinalNumberToColumn(final OrderByStatement obs, final SelectQueryStatement from_sqs) {
        for (int i = 0; i < obs.getOrderItemList().size(); ++i) {
            final OrderItem oi = obs.getOrderItemList().get(i);
            if (oi.getOrderSpecifier().getColumnExpression().elementAt(0) instanceof String) {
                final String ordinalNumber = oi.getOrderSpecifier().getColumnExpression().elementAt(0);
                if (ordinalNumber.matches("^[1-9][0-9]*")) {
                    final Vector tc = from_sqs.getSelectStatement().getSelectItemList().elementAt(Integer.parseInt(ordinalNumber) - 1).getColumnExpression();
                    oi.getOrderSpecifier().setColumnExpression(tc);
                }
            }
            obs.getOrderItemList().set(i, oi);
        }
    }
    
    private void convertOrdinalNumberToColumnOrderBy(final OrderItem oi, final SelectQueryStatement from_sqs) {
        if (oi.getOrderSpecifier().getColumnExpression().elementAt(0) instanceof String) {
            final String ordinalNumber = oi.getOrderSpecifier().getColumnExpression().elementAt(0);
            if (ordinalNumber.matches("^[1-9][0-9]*")) {
                final Vector tc = from_sqs.getSelectStatement().getSelectItemList().elementAt(Integer.parseInt(ordinalNumber) - 1).getColumnExpression();
                oi.getOrderSpecifier().setColumnExpression(tc);
            }
        }
    }
    
    public OrderByStatement toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderByStatement obs = new OrderByStatement();
        OrderItem oi = new OrderItem();
        if (this.OrderClause != null) {
            obs.setOrderClause(this.OrderClause);
        }
        final Vector v_oil = new Vector();
        if (this.OrderItemList.size() == 1) {
            oi = this.OrderItemList.elementAt(0).toInformixSelect(to_sqs, from_sqs);
            if (oi != null) {
                if (oi.getOrderSpecifier() != null) {
                    final Vector orderbyItems = oi.getOrderSpecifier().getColumnExpression();
                    if (orderbyItems != null) {
                        for (int j = 0; j < orderbyItems.size(); ++j) {
                            if (orderbyItems.get(j) instanceof TableColumn) {
                                final String orderbyColumnName = orderbyItems.get(j).getColumnName();
                                Vector columnNamesFromTableColumn = new Vector();
                                columnNamesFromTableColumn = this.getSelectColumnList(to_sqs);
                                if (columnNamesFromTableColumn != null) {
                                    if (!columnNamesFromTableColumn.contains(orderbyColumnName)) {
                                        final Vector columnNamesFromAggregateFunctions = new Vector();
                                        orderbyItems.get(j).setColumnName(this.checkIfSelectColumnInFunctionSelectColumnList(to_sqs, orderbyColumnName, orderbyItems.get(j)));
                                    }
                                }
                            }
                        }
                    }
                }
                v_oil.addElement(oi);
                obs.setOrderItemList(v_oil);
            }
            else {
                obs = null;
            }
        }
        else {
            for (int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
                oi = this.OrderItemList.elementAt(i_count).toInformixSelect(to_sqs, from_sqs);
                if (oi != null) {
                    if (oi.getOrderSpecifier() != null) {
                        final Vector orderbyItems2 = oi.getOrderSpecifier().getColumnExpression();
                        if (orderbyItems2 != null) {
                            for (int i = 0; i < orderbyItems2.size(); ++i) {
                                if (orderbyItems2.get(i) instanceof TableColumn) {
                                    final String orderbyColumnName2 = orderbyItems2.get(i).getColumnName();
                                    Vector columnNamesFromTableColumn2 = new Vector();
                                    columnNamesFromTableColumn2 = this.getSelectColumnList(to_sqs);
                                    if (columnNamesFromTableColumn2 != null) {
                                        if (!columnNamesFromTableColumn2.contains(orderbyColumnName2)) {
                                            final Vector columnNamesFromAggregateFunctions2 = new Vector();
                                            orderbyItems2.get(i).setColumnName(this.checkIfSelectColumnInFunctionSelectColumnList(to_sqs, orderbyColumnName2, orderbyItems2.get(i)));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    v_oil.addElement(oi);
                }
            }
            obs.setOrderItemList(v_oil);
        }
        return obs;
    }
    
    public OrderByStatement toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderByStatement obs = new OrderByStatement();
        OrderItem oi = new OrderItem();
        if (this.OrderClause != null) {
            obs.setOrderClause(this.OrderClause);
        }
        final Vector v_oil = new Vector();
        obs.setObjectContext(this.context);
        if (this.OrderItemList.size() == 1) {
            oi = this.OrderItemList.elementAt(0).toTimesTenSelect(to_sqs, from_sqs);
            if (oi != null) {
                v_oil.addElement(oi);
                obs.setOrderItemList(v_oil);
            }
            else {
                obs = null;
            }
        }
        else {
            for (int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
                oi = this.OrderItemList.elementAt(i_count).toTimesTenSelect(to_sqs, from_sqs);
                if (oi != null) {
                    v_oil.addElement(oi);
                }
            }
            obs.setOrderItemList(v_oil);
        }
        return obs;
    }
    
    public OrderByStatement toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderByStatement obs = new OrderByStatement();
        OrderItem oi = new OrderItem();
        if (this.OrderClause != null) {
            obs.setOrderClause(this.OrderClause);
        }
        final Vector v_oil = new Vector();
        if (this.OrderItemList.size() == 1) {
            oi = this.OrderItemList.elementAt(0).toNetezzaSelect(to_sqs, from_sqs);
            if (oi != null) {
                v_oil.addElement(oi);
                obs.setOrderItemList(v_oil);
            }
            else {
                obs = null;
            }
        }
        else {
            for (int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
                oi = this.OrderItemList.elementAt(i_count).toNetezzaSelect(to_sqs, from_sqs);
                if (oi != null) {
                    v_oil.addElement(oi);
                }
            }
            obs.setOrderItemList(v_oil);
        }
        return obs;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
        }
        if (this.OrderClause != null) {
            sb.append(this.OrderClause.toUpperCase());
        }
        if (this.OrderItemList != null) {
            ++SelectQueryStatement.beautyTabCount;
            try {
                for (int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
                    if (this.OrderItemList.elementAt(i_count) instanceof OrderItem) {
                        this.OrderItemList.elementAt(i_count).setObjectContext(this.context);
                    }
                    if (i_count == this.OrderItemList.size() - 1) {
                        sb.append(" " + this.OrderItemList.elementAt(i_count).toString());
                    }
                    else {
                        sb.append(" " + this.OrderItemList.elementAt(i_count).toString() + ",");
                        sb.append("\n");
                        for (int i = 0; i < SelectQueryStatement.beautyTabCount; ++i) {
                            sb.append("\t");
                        }
                    }
                }
            }
            catch (final ArrayIndexOutOfBoundsException ex) {}
            catch (final Exception ex2) {}
            --SelectQueryStatement.beautyTabCount;
        }
        if (this.commentObjAfterToken != null) {
            sb.append(" " + this.commentObjAfterToken.toString().trim());
        }
        return sb.toString();
    }
    
    public Vector getSelectColumnList(final SelectQueryStatement toSelectQueryStatement) {
        final Vector columnNamesFromTableColumn = new Vector();
        final Vector getSelectColumn = toSelectQueryStatement.getSelectStatement().getSelectItemList();
        if (getSelectColumn != null) {
            for (int i = 0; i < getSelectColumn.size(); ++i) {
                final SelectColumn getSelectColumnForOrderBy = getSelectColumn.get(i);
                final Vector getSelectItems = getSelectColumnForOrderBy.getColumnExpression();
                if (getSelectItems != null) {
                    for (int j = 0; j > getSelectItems.size(); ++j) {
                        if (getSelectItems.get(j) instanceof TableColumn) {
                            final TableColumn getTableColumn = getSelectItems.get(j);
                            final String getColumnName = getTableColumn.getColumnName();
                            columnNamesFromTableColumn.add(getColumnName);
                        }
                    }
                }
            }
        }
        return columnNamesFromTableColumn;
    }
    
    public String checkIfSelectColumnInFunctionSelectColumnList(final SelectQueryStatement toSelectQueryStatement, String orderbyColumnName, final TableColumn tableColumn) {
        final Vector getSelectColumn = toSelectQueryStatement.getSelectStatement().getSelectItemList();
        if (getSelectColumn != null) {
            for (int i = 0; i < getSelectColumn.size(); ++i) {
                final SelectColumn getSelectColumnForOrderBy = getSelectColumn.get(i);
                final Vector getSelectItems = getSelectColumnForOrderBy.getColumnExpression();
                if (getSelectItems != null) {
                    for (int j = 0; j < getSelectItems.size(); ++j) {
                        if (getSelectItems.get(j) instanceof FunctionCalls) {
                            final Vector functionArguments = getSelectItems.get(j).getFunctionArguments();
                            if (functionArguments != null) {
                                for (int k = 0; k < functionArguments.size(); ++k) {
                                    if (functionArguments.get(k) instanceof SelectColumn) {
                                        final SelectColumn selectColumn = functionArguments.get(k);
                                        final Vector functionColumns = selectColumn.getColumnExpression();
                                        if (functionColumns != null) {
                                            for (int l = 0; l < functionColumns.size(); ++l) {
                                                if (functionColumns.get(l) instanceof TableColumn) {
                                                    final TableColumn getTableColumn = functionColumns.get(l);
                                                    final String getColumnName = getTableColumn.getColumnName();
                                                    if (getColumnName.equalsIgnoreCase(orderbyColumnName)) {
                                                        orderbyColumnName = "" + (i + 1);
                                                        tableColumn.setOwnerName(null);
                                                        tableColumn.setTableName(null);
                                                        tableColumn.setDot(null);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return orderbyColumnName;
    }
    
    private ArrayList convertOrderByColumnsToNumber(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final ArrayList numberColList = new ArrayList();
        final Vector from_SQS_SelectItems = from_sqs.getSelectStatement().getSelectItemList();
        final ArrayList selectItemsTableColumn = new ArrayList();
        for (int i = 0; i < from_SQS_SelectItems.size(); ++i) {
            if (from_SQS_SelectItems.elementAt(i) instanceof SelectColumn) {
                final SelectColumn fromSQLSelectCol = from_SQS_SelectItems.elementAt(i);
                if (fromSQLSelectCol.getAliasName() != null) {
                    final TableColumn tcAlias = new TableColumn();
                    tcAlias.setColumnName(fromSQLSelectCol.getAliasName());
                    selectItemsTableColumn.add(tcAlias);
                }
                else if (fromSQLSelectCol.getColumnExpression() != null) {
                    final Vector fromSQLSelectColExp = fromSQLSelectCol.getColumnExpression();
                    for (int j = 0; j < fromSQLSelectColExp.size(); ++j) {
                        if (fromSQLSelectColExp.elementAt(j) instanceof TableColumn) {
                            final TableColumn tc = fromSQLSelectColExp.elementAt(j);
                            selectItemsTableColumn.add(tc);
                        }
                        else if (fromSQLSelectColExp.elementAt(j) instanceof String) {
                            final String s_ce = fromSQLSelectColExp.elementAt(j);
                            if (s_ce.indexOf("*") == -1) {
                                final String tableOrAlias = s_ce;
                                final FromClause fc = from_sqs.getFromClause();
                                final Vector v_fil = fc.getFromItemList();
                                if (v_fil.size() > 1) {
                                    for (int countNum = 0; countNum < v_fil.size(); ++countNum) {
                                        if (v_fil.elementAt(countNum) instanceof FromTable) {
                                            final FromTable ft = v_fil.elementAt(countNum);
                                            if (ft.getAliasName() == null) {
                                                final Object o_tn = ft.getTableName();
                                                if (!(o_tn instanceof String)) {
                                                    throw new ConvertException();
                                                }
                                                String tableName = (String)o_tn;
                                                if (tableName.toLowerCase().startsWith("dbo.")) {
                                                    tableName = tableName.substring(4);
                                                }
                                                else if (tableName.toLowerCase().startsWith("[dbo].")) {
                                                    tableName = tableName.substring(6);
                                                }
                                                if (tableOrAlias.equals(tableName)) {
                                                    final ArrayList colList = SwisSQLAPI.tableColumnListMetadata.get(tableOrAlias.trim());
                                                    if (colList != null) {
                                                        for (int colListInd = 0; colListInd < colList.size(); ++colListInd) {
                                                            final TableColumn tc2 = new TableColumn();
                                                            tc2.setColumnName(colList.get(colListInd).toString());
                                                            tc2.setTableName(tableOrAlias);
                                                            selectItemsTableColumn.add(tc2);
                                                        }
                                                    }
                                                }
                                            }
                                            else {
                                                final String aliasName = ft.getAliasName();
                                                if (tableOrAlias.equals(aliasName)) {
                                                    final ArrayList colList2 = SwisSQLAPI.tableColumnListMetadata.get(ft.getTableName().toString().trim());
                                                    if (colList2 != null) {
                                                        for (int colListInd2 = 0; colListInd2 < colList2.size(); ++colListInd2) {
                                                            final TableColumn tc3 = new TableColumn();
                                                            tc3.setColumnName(colList2.get(colListInd2).toString());
                                                            tc3.setTableName(tableOrAlias);
                                                            selectItemsTableColumn.add(tc3);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int index = 0; index < this.OrderItemList.size(); ++index) {
            if (this.OrderItemList.get(index) instanceof OrderItem) {
                final OrderItem orderItem = this.OrderItemList.get(index);
                orderItem.setObjectContext(this.context);
                final SelectColumn sc = orderItem.getOrderSpecifier();
                if (sc.getColumnExpression() != null) {
                    final Vector columnExpression = sc.getColumnExpression();
                    for (int k = 0; k < columnExpression.size(); ++k) {
                        if (columnExpression.get(k) instanceof TableColumn) {
                            final TableColumn tc4 = columnExpression.get(k);
                            for (int sel_Ind = 0; sel_Ind < selectItemsTableColumn.size(); ++sel_Ind) {
                                final TableColumn tableCol = selectItemsTableColumn.get(sel_Ind);
                                if (tc4.toString().trim().equals(tableCol.toString().trim())) {
                                    final TableColumn numberCol = new TableColumn();
                                    final int numberColVal = sel_Ind + 1;
                                    final String numberColName = "" + numberColVal;
                                    numberCol.setColumnName(numberColName.trim());
                                    numberColList.add(numberCol);
                                }
                            }
                        }
                    }
                }
            }
        }
        return numberColList;
    }
    
    private void addCaseStatementToOrderItemsList(int position, final OrderItem oi, final String thenClause, final String elseClause) {
        final OrderItem caseOrderItem = new OrderItem();
        final SelectColumn caseColumn = new SelectColumn();
        final Vector caseColumnExp = new Vector();
        final CaseStatement caseStmtforNullsClause = new CaseStatement();
        final SelectColumn scForThenCondition = new SelectColumn();
        final Vector colExpForWhenCondition = new Vector();
        final WhereExpression we = new WhereExpression();
        final Vector whereItems = new Vector();
        final WhereItem wi = new WhereItem();
        final WhereColumn wc = new WhereColumn();
        final Vector whereColumnsVector = new Vector();
        final Vector thenStmts = new Vector();
        final WhenStatement whenClause = new WhenStatement();
        final SelectColumn scForElseStmt = new SelectColumn();
        final Vector elseStmts = new Vector();
        caseStmtforNullsClause.setCaseClause("CASE");
        caseStmtforNullsClause.setElseClause("ELSE");
        caseStmtforNullsClause.setEndClause("END");
        whenClause.setWhenClause("WHEN");
        whereColumnsVector.add(oi.getOrderSpecifier());
        wc.setColumnExpression(whereColumnsVector);
        wi.setLeftWhereExp(wc);
        wi.setOperator("IS NULL");
        whereItems.add(wi);
        we.setWhereItem(whereItems);
        whenClause.setWhenCondition(we);
        whenClause.setThenClause("THEN");
        thenStmts.add(thenClause);
        scForThenCondition.setColumnExpression(thenStmts);
        whenClause.setThenStatement(scForThenCondition);
        colExpForWhenCondition.add(whenClause);
        elseStmts.add(elseClause);
        scForElseStmt.setColumnExpression(elseStmts);
        caseStmtforNullsClause.setWhenStatementList(colExpForWhenCondition);
        caseStmtforNullsClause.setElseStatement(scForElseStmt);
        caseColumnExp.add(caseStmtforNullsClause);
        caseColumn.setColumnExpression(caseColumnExp);
        caseOrderItem.setOrderSpecifier(caseColumn);
        this.OrderItemList.add(position, caseOrderItem);
        ++position;
    }
    
    public OrderByStatement toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        OrderByStatement obs = new OrderByStatement();
        OrderItem oi = new OrderItem();
        if (this.OrderClause != null) {
            obs.setOrderClause(this.OrderClause);
        }
        final Vector v_oil = new Vector();
        if (this.OrderItemList.size() == 1) {
            oi = this.OrderItemList.elementAt(0).toVectorWiseSelect(to_sqs, from_sqs);
            if (oi != null) {
                v_oil.addElement(oi);
                obs.setOrderItemList(v_oil);
            }
            else {
                obs = null;
            }
        }
        else {
            for (int i_count = 0; i_count < this.OrderItemList.size(); ++i_count) {
                oi = this.OrderItemList.elementAt(i_count).toVectorWiseSelect(to_sqs, from_sqs);
                if (oi != null) {
                    v_oil.addElement(oi);
                }
            }
            obs.setOrderItemList(v_oil);
        }
        return obs;
    }
}
