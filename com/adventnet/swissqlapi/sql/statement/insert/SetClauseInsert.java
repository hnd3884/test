package com.adventnet.swissqlapi.sql.statement.insert;

import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.ArrayList;

public class SetClauseInsert
{
    private String set;
    private ArrayList setList;
    private UserObjectContext context;
    
    public SetClauseInsert() {
        this.context = null;
        this.setList = null;
        this.set = null;
    }
    
    public void setSet(final String s) {
        this.set = s;
    }
    
    public void setSetList(final ArrayList v) {
        this.setList = v;
    }
    
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    public String getSet() {
        return this.set;
    }
    
    public ArrayList getSetList() {
        return this.setList;
    }
    
    public void toGeneric(final InsertQueryStatement q) {
        final ValuesClause valuesClause = new ValuesClause();
        valuesClause.setValues("VALUES");
        final ArrayList columnList = new ArrayList();
        final ArrayList valuesList = new ArrayList();
        final int size = this.setList.size();
        columnList.add("(");
        for (int i = 0; i < size; ++i) {
            final SelectColumn sc = new SelectColumn();
            final Vector v_nce = new Vector();
            if (!(this.setList.get(i) instanceof String) || !this.setList.get(i).equals(",")) {
                if (this.setList.get(i) instanceof SelectColumn) {
                    this.setList.get(i).setObjectContext(this.context);
                    final Vector v_ce = this.setList.get(i).getColumnExpression();
                    for (int j = 0; j < v_ce.size() && (!(v_ce.elementAt(j) instanceof String) || !v_ce.elementAt(j).equals("=")); ++j) {
                        v_nce.addElement(v_ce.elementAt(j));
                    }
                    sc.setColumnExpression(v_nce);
                    columnList.add(sc);
                    columnList.add(",");
                }
                else {
                    columnList.add(this.setList.get(i));
                    columnList.add(",");
                }
            }
        }
        columnList.set(columnList.lastIndexOf(","), ")");
        final InsertClause insertClause = q.getInsertClause();
        insertClause.setColumnList(columnList);
        valuesList.add("(");
        for (int k = 0; k < size; ++k) {
            final SelectColumn sc2 = new SelectColumn();
            final Vector v_nce2 = new Vector();
            if (!(this.setList.get(k) instanceof String) || !this.setList.get(k).equals(",")) {
                if (this.setList.get(k) instanceof SelectColumn) {
                    final Vector v_ce2 = this.setList.get(k).getColumnExpression();
                    boolean b_after_equals = false;
                    for (int l = 0; l < v_ce2.size(); ++l) {
                        if (b_after_equals) {
                            v_nce2.addElement(v_ce2.elementAt(l));
                        }
                        if (v_ce2.elementAt(l) instanceof String && v_ce2.elementAt(l).equals("=")) {
                            b_after_equals = true;
                        }
                    }
                    sc2.setColumnExpression(v_nce2);
                    valuesList.add(sc2);
                    valuesList.add(",");
                }
                else {
                    valuesList.add(this.setList.get(k));
                    valuesList.add(",");
                }
            }
        }
        valuesList.set(valuesList.lastIndexOf(","), ")");
        valuesClause.setValuesList(valuesList);
        q.setValuesClause(valuesClause);
        q.setInsertClause(insertClause);
        q.setInsertClause(insertClause);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.set != null) {
            sb.append(this.set.toUpperCase() + " ");
        }
        if (this.setList != null) {
            for (int size = this.setList.size(), i = 0; i < size; ++i) {
                if (this.setList.get(i) instanceof String) {
                    if (this.context != null) {
                        final String temp = this.context.getEquivalent(this.setList.get(i)).toString();
                        sb.append(temp + " ");
                    }
                    else {
                        sb.append(this.setList.get(i) + " ");
                    }
                }
                else {
                    sb.append(this.setList.get(i) + " ");
                }
            }
        }
        return sb.toString();
    }
}
