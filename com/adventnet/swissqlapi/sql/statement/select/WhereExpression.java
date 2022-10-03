package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.functions.misc.decode;
import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Iterator;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.Collection;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;

public class WhereExpression
{
    private String openBraces;
    private String closeBraces;
    private Vector whereItems;
    private RownumClause rownumClause;
    private Vector operators;
    private boolean topLevel;
    private String beginOperator;
    private boolean checkWhere;
    private String concatenation;
    private UserObjectContext context;
    private boolean isToOracle;
    private ArrayList fromTableList;
    private CommentClass commentObj;
    private String stmtTableName;
    private Vector functionColumnVector;
    private Vector removedFromItemsList;
    private boolean selectStmtInJoin;
    private boolean fromDeleteQueryStatement;
    private boolean thetaJoinPresent;
    private boolean is_Case_Expression;
    private int targetDatabase;
    
    public WhereExpression() {
        this.topLevel = false;
        this.context = null;
        this.isToOracle = false;
        this.fromTableList = null;
        this.functionColumnVector = new Vector();
        this.removedFromItemsList = null;
        this.selectStmtInJoin = false;
        this.fromDeleteQueryStatement = false;
        this.thetaJoinPresent = false;
        this.is_Case_Expression = false;
        this.openBraces = new String("");
        this.closeBraces = new String("");
        this.whereItems = new Vector();
        this.operators = new Vector();
    }
    
    public Vector getFunctionColumnVector() {
        return this.functionColumnVector;
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void addWhereItem(final WhereItem wi) {
        wi.setObjectContext(this.context);
        this.whereItems.addElement(wi);
    }
    
    public void setFromTableList(final ArrayList fromTableList) {
        this.fromTableList = fromTableList;
    }
    
    public void setStmtTableName(final String stmtTableName) {
        this.stmtTableName = stmtTableName;
    }
    
    public void addWhereExpression(final WhereExpression we) {
        this.whereItems.addElement(we);
    }
    
    public void addOperator(final String opr) {
        this.operators.addElement(opr);
    }
    
    public void addOpenBrace(final String s) {
        this.openBraces += s;
    }
    
    public void addCloseBrace(final String s) {
        this.closeBraces += s;
    }
    
    public void removeBrace() {
        if (this.openBraces != null && this.openBraces.length() > 0) {
            this.openBraces = this.openBraces.substring(1);
            if (this.closeBraces != null && this.closeBraces.length() > 0) {
                this.closeBraces = this.closeBraces.substring(1);
            }
        }
    }
    
    public boolean removeOperator() {
        if (this.operators.size() != 0) {
            this.operators.removeElementAt(this.operators.size() - 1);
            return true;
        }
        return false;
    }
    
    public void setTopLevel(final boolean b) {
        this.topLevel = b;
    }
    
    public void setBeginOperator(final String begin) {
        this.beginOperator = begin;
    }
    
    public void setWhereItem(final Vector wi) {
        if (wi != null) {
            for (int i = 0; i < wi.size(); ++i) {
                if (wi.get(i) instanceof WhereItem) {
                    wi.get(i).setObjectContext(this.context);
                }
            }
        }
        this.whereItems = wi;
    }
    
    public void setOperator(final Vector opr) {
        this.operators = opr;
    }
    
    public void setOpenBrace(final String s_ob) {
        this.openBraces = s_ob;
    }
    
    public void setCloseBrace(final String s_cb) {
        this.closeBraces = s_cb;
    }
    
    public void setCheckWhere(final boolean b_cw) {
        this.checkWhere = b_cw;
    }
    
    public void setConcatenation(final String concatenation) {
        this.concatenation = concatenation;
    }
    
    public void setRownumClause(final RownumClause rc) {
        this.rownumClause = rc;
    }
    
    public void setToOracle(final boolean isToOracle) {
        this.isToOracle = isToOracle;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public void setCaseExpressionBool(final boolean boolVal) {
        this.is_Case_Expression = boolVal;
    }
    
    public void setTargetDatabase(final int targetDatabase) {
        this.targetDatabase = targetDatabase;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public String getConcatenation() {
        return this.concatenation;
    }
    
    public RownumClause getRownumClause() {
        return this.rownumClause;
    }
    
    public String getBeginOperator() {
        return this.beginOperator;
    }
    
    public Vector getWhereItems() {
        return this.whereItems;
    }
    
    public Vector getWhereItem() {
        return this.whereItems;
    }
    
    public Vector getOperator() {
        return this.operators;
    }
    
    public String getOpenBrace() {
        return this.openBraces;
    }
    
    public String getCloseBrace() {
        return this.closeBraces;
    }
    
    public boolean getCheckWhere() {
        return this.checkWhere;
    }
    
    public boolean getTopLevel() {
        return this.topLevel;
    }
    
    public void setFromDeleteQueryStatement(final boolean b) {
        this.fromDeleteQueryStatement = b;
    }
    
    public boolean isThetaJoinPresent() {
        return this.thetaJoinPresent;
    }
    
    public void setThetaJoinPresent(final boolean val) {
        this.thetaJoinPresent = true;
    }
    
    public boolean isCaseExpression() {
        return this.is_Case_Expression;
    }
    
    public int getTargetDatabase() {
        return this.targetDatabase;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.beginOperator != null) {
            sb.append(this.beginOperator + " ");
        }
        if (this.openBraces != null && !this.openBraces.equals("")) {
            sb.append(this.openBraces);
        }
        for (int i = 0; i < this.whereItems.size(); ++i) {
            String whereItemString = null;
            if (this.whereItems.elementAt(i) != null) {
                if (this.whereItems.elementAt(i) instanceof WhereItem) {
                    this.whereItems.elementAt(i).setObjectContext(this.context);
                }
                else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
                    this.whereItems.elementAt(i).setObjectContext(this.context);
                }
                whereItemString = this.whereItems.elementAt(i).toString();
                if (whereItemString != null) {
                    sb.append(whereItemString);
                }
            }
            if (i == this.operators.size() && this.whereItems.elementAt(i) instanceof WhereExpression && whereItemString == null) {
                String s = sb.toString().trim();
                if (s.endsWith("OR")) {
                    s = s.substring(0, s.length() - 2);
                }
                else if (s.endsWith("AND")) {
                    s = s.substring(0, s.length() - 3);
                }
                sb = new StringBuffer(s);
            }
            if (i >= this.operators.size() || !this.operators.elementAt(i).toString().equalsIgnoreCase("&AND")) {
                if (i < this.operators.size() && !this.operators.elementAt(i).toString().equalsIgnoreCase("ANSIAND") && !this.operators.elementAt(i).toString().equalsIgnoreCase("ANSIOR")) {
                    if (!(this.whereItems.elementAt(i) instanceof WhereExpression) || whereItemString != null) {
                        sb.append("\n");
                        for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                            sb.append("\t");
                        }
                        sb.append(" " + this.operators.elementAt(i).toString().toUpperCase() + "\t");
                    }
                    else if (whereItemString == null && !this.isToOracle) {
                        sb.append("\n");
                        for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                            sb.append("\t");
                        }
                        sb.append(" " + this.operators.elementAt(i).toString().toUpperCase() + "\t");
                    }
                }
            }
        }
        if (this.closeBraces != null && !this.closeBraces.equals("")) {
            sb.append(this.closeBraces);
        }
        final String returnString = sb.toString();
        if (returnString.equals("()")) {
            return null;
        }
        return returnString;
    }
    
    public void removeJoinConditionsFromWhereExpression(final WhereExpression whereExp, final SelectQueryStatement from_sqs) {
        if (!this.fromDeleteQueryStatement) {
            final Vector whereItemsList = whereExp.getWhereItems();
            final Vector tempOperators = whereExp.getOperator();
            for (int i = 0; i < whereItemsList.size(); ++i) {
                final Object obj = whereItemsList.get(i);
                if (obj != null && obj instanceof WhereItem) {
                    final WhereItem wi = (WhereItem)obj;
                    if (wi.getOperator() != null && wi.getOperator().trim().equals("=")) {
                        FromTable ft1 = new FromTable();
                        FromTable ft2 = new FromTable();
                        final WhereColumn wcl = wi.getLeftWhereExp();
                        final WhereColumn wcr = wi.getRightWhereExp();
                        final Vector leftColumnExp = wcl.getColumnExpression();
                        final Vector rightColumnExp = wcr.getColumnExpression();
                        if (leftColumnExp != null && rightColumnExp != null && leftColumnExp.size() == 1 && rightColumnExp.size() == 1 && leftColumnExp.get(0) instanceof TableColumn && rightColumnExp.get(0) instanceof TableColumn) {
                            final TableColumn tc1 = leftColumnExp.get(0);
                            final TableColumn tc2 = rightColumnExp.get(0);
                            ft1 = MetadataInfoUtil.getTableOfColumn(from_sqs, tc1);
                            ft2 = MetadataInfoUtil.getTableOfColumn(from_sqs, tc2);
                            final String s1 = tc1.getColumnName().trim();
                            final String s2 = tc2.getColumnName().trim();
                            if (!s1.startsWith("'") && !s2.startsWith("'") && !s1.startsWith("\"") && !s2.startsWith("\"") && ft1 != null && ft2 != null) {
                                whereItemsList.set(i, null);
                                if (i != 0) {
                                    final String op = whereExp.getOperator().get(i - 1);
                                    if (!op.equals("&AND")) {
                                        whereExp.getOperator().setElementAt("&AND", i - 1);
                                    }
                                    else if (whereExp.getOperator().size() > i) {
                                        whereExp.getOperator().setElementAt("&AND", i);
                                    }
                                }
                                else if (whereExp.getOperator().size() > i) {
                                    whereExp.getOperator().setElementAt("&AND", i);
                                }
                            }
                        }
                    }
                }
            }
            for (int k = 0; k < whereItemsList.size(); ++k) {
                if (whereItemsList.indexOf(null) != -1) {
                    whereItemsList.remove(null);
                    if (k >= whereItemsList.size() - 1) {
                        k = --k;
                    }
                }
            }
            for (int l = 0; l < tempOperators.size(); ++l) {
                final String s3 = tempOperators.get(l);
                if (s3.trim().equalsIgnoreCase("&AND")) {
                    tempOperators.remove(l);
                    l = --l;
                }
            }
            if (whereItemsList.size() == 1) {
                tempOperators.removeAllElements();
            }
        }
    }
    
    public Vector getJoinConditionsFromWhereExpression(final WhereExpression whereExp, final SelectQueryStatement from_sqs) {
        final Vector joinConditions = new Vector();
        if (!this.fromDeleteQueryStatement) {
            final Vector nonNullwhereItemsList = new Vector();
            for (int wi = 0; wi < whereExp.getWhereItems().size(); ++wi) {
                final Object obj = whereExp.getWhereItems().get(wi);
                if (obj instanceof WhereItem) {
                    if (obj != null) {
                        nonNullwhereItemsList.add(obj);
                    }
                }
                else if (obj instanceof WhereExpression) {
                    if (obj.toString() != null) {
                        if (((WhereExpression)obj).getWhereItem() != null && ((WhereExpression)obj).getWhereItem().size() > 0) {
                            boolean nonNullWhereItemPresent = false;
                            final WhereExpression wexpObj = (WhereExpression)obj;
                            for (int wwi = 0; wwi < wexpObj.getWhereItem().size(); ++wwi) {
                                if (wexpObj.getWhereItem().get(wwi) != null) {
                                    nonNullWhereItemPresent = true;
                                }
                            }
                            if (nonNullWhereItemPresent) {
                                nonNullwhereItemsList.add(obj);
                            }
                        }
                    }
                }
            }
            final Vector nonNullOperators = new Vector();
            for (int oi = 0; oi < whereExp.getOperator().size(); ++oi) {
                final String obj2 = whereExp.getOperator().get(oi).toString();
                if (!obj2.equalsIgnoreCase("&AND")) {
                    nonNullOperators.add(obj2);
                }
            }
            for (int i = 0; i < nonNullwhereItemsList.size(); ++i) {
                final Object obj3 = nonNullwhereItemsList.get(i);
                if (obj3 != null && obj3 instanceof WhereItem) {
                    final WhereItem wi2 = (WhereItem)obj3;
                    if (wi2.getOperator() != null && wi2.getOperator().trim().equals("=")) {
                        String sa = this.getTableAliasWhereL(wi2);
                        String sb = this.getTableAliasWhereR(wi2);
                        if (sa == null || sb == null) {
                            final WhereColumn wcl = wi2.getLeftWhereExp();
                            final WhereColumn wcr = wi2.getRightWhereExp();
                            FromTable ft1 = new FromTable();
                            FromTable ft2 = new FromTable();
                            final Vector leftColumnExp = wcl.getColumnExpression();
                            final Vector rightColumnExp = wcr.getColumnExpression();
                            if (leftColumnExp != null && rightColumnExp != null && leftColumnExp.size() == 1 && rightColumnExp.size() == 1 && leftColumnExp.get(0) instanceof TableColumn && rightColumnExp.get(0) instanceof TableColumn) {
                                final TableColumn tc1 = leftColumnExp.get(0);
                                final TableColumn tc2 = rightColumnExp.get(0);
                                ft1 = MetadataInfoUtil.getTableOfColumn(from_sqs, tc1);
                                ft2 = MetadataInfoUtil.getTableOfColumn(from_sqs, tc2);
                                final String s1 = tc1.getColumnName().trim();
                                final String s2 = tc2.getColumnName().trim();
                                if (!s1.startsWith("'") && !s2.startsWith("'") && ((ft1 != null && ft2 != null) || (tc1.getTableName() != null && tc2.getTableName() != null))) {
                                    sa = tc1.getTableName();
                                    sb = tc2.getTableName();
                                }
                            }
                        }
                        if (sa != null && sb != null && !sb.equalsIgnoreCase("") && !sa.equalsIgnoreCase(sb)) {
                            String opOuter = "";
                            if (i != 0) {
                                opOuter = whereExp.getOperator().get(i - 1);
                            }
                            if (!opOuter.equalsIgnoreCase("OR")) {
                                joinConditions.add(obj3);
                                nonNullwhereItemsList.set(i, null);
                                if (i != 0) {
                                    final String op = nonNullOperators.get(i - 1);
                                    if (!op.equalsIgnoreCase("OR")) {
                                        if (!op.equals("&AND")) {
                                            nonNullOperators.setElementAt("&AND", i - 1);
                                        }
                                        else if (nonNullOperators.size() > i) {
                                            nonNullOperators.setElementAt("&AND", i - 1);
                                        }
                                    }
                                }
                                else if (nonNullOperators.size() > i) {
                                    nonNullOperators.setElementAt("&AND", i);
                                }
                            }
                        }
                    }
                }
                else if (obj3 != null && obj3 instanceof WhereExpression) {
                    final WhereExpression we = (WhereExpression)obj3;
                    if (we.getOperator().size() == 0) {
                        final Vector localWI = this.getJoinConditionsFromWhereExpression(we, from_sqs);
                        if (localWI.size() > 0) {
                            if (i != 0) {
                                final String op2 = nonNullOperators.get(i - 1);
                                if (!op2.equalsIgnoreCase("OR")) {
                                    if (!op2.equals("&AND")) {
                                        nonNullOperators.setElementAt("&AND", i - 1);
                                    }
                                    else if (nonNullOperators.size() > i) {
                                        nonNullOperators.setElementAt("&AND", i - 1);
                                    }
                                }
                            }
                            else if (nonNullOperators.size() > i) {
                                nonNullOperators.setElementAt("&AND", i);
                            }
                        }
                        joinConditions.addAll(localWI);
                    }
                    else if (!we.getOperator().contains("OR") && !we.getOperator().contains("or")) {
                        joinConditions.addAll(this.getJoinConditionsFromGroupedWhereExpression(we, from_sqs));
                    }
                }
            }
            final Vector newWhereItemsList = new Vector();
            for (int k = 0; k < nonNullwhereItemsList.size(); ++k) {
                final Object obj4 = nonNullwhereItemsList.get(k);
                if (obj4 instanceof WhereItem) {
                    if (obj4 != null) {
                        newWhereItemsList.add(nonNullwhereItemsList.get(k));
                    }
                }
                else if (obj4 instanceof WhereExpression && ((WhereExpression)obj4).getWhereItem() != null && ((WhereExpression)obj4).getWhereItem().size() > 0) {
                    boolean nonNullWhereItemPresent2 = false;
                    final WhereExpression wexpObj2 = (WhereExpression)obj4;
                    for (int wi3 = 0; wi3 < wexpObj2.getWhereItem().size(); ++wi3) {
                        if (wexpObj2.getWhereItem().get(wi3) != null) {
                            nonNullWhereItemPresent2 = true;
                        }
                    }
                    if (nonNullWhereItemPresent2) {
                        newWhereItemsList.add(nonNullwhereItemsList.get(k));
                    }
                }
            }
            final Vector newOperatorsList = new Vector();
            for (int l = 0; l < nonNullOperators.size(); ++l) {
                final String s3 = nonNullOperators.get(l);
                if (!s3.trim().equalsIgnoreCase("&AND")) {
                    newOperatorsList.add(s3);
                }
            }
            final Vector finalOperatorsList = new Vector();
            if (newOperatorsList.size() > 0) {
                for (int fi = 0; fi < newWhereItemsList.size() - 1; ++fi) {
                    finalOperatorsList.add(newOperatorsList.get(fi));
                }
            }
            if (newWhereItemsList.size() == 1) {
                finalOperatorsList.removeAllElements();
            }
            whereExp.setWhereItem(newWhereItemsList);
            whereExp.setOperator(finalOperatorsList);
        }
        return joinConditions;
    }
    
    public Vector getJoinConditionsFromGroupedWhereExpression(final WhereExpression whereExp, final SelectQueryStatement from_sqs) {
        final Vector joinConditions = new Vector();
        if (!this.fromDeleteQueryStatement) {
            final Vector nonNullwhereItemsList = new Vector();
            for (int wi = 0; wi < whereExp.getWhereItems().size(); ++wi) {
                final Object obj = whereExp.getWhereItems().get(wi);
                if (obj instanceof WhereItem) {
                    if (obj != null) {
                        nonNullwhereItemsList.add(obj);
                    }
                }
                else if (obj instanceof WhereExpression) {
                    if (obj.toString() != null) {
                        if (((WhereExpression)obj).getWhereItem() != null && ((WhereExpression)obj).getWhereItem().size() > 0) {
                            boolean nonNullWhereItemPresent = false;
                            final WhereExpression wexpObj = (WhereExpression)obj;
                            for (int wwi = 0; wwi < wexpObj.getWhereItem().size(); ++wwi) {
                                if (wexpObj.getWhereItem().get(wwi) != null) {
                                    nonNullWhereItemPresent = true;
                                }
                            }
                            if (nonNullWhereItemPresent) {
                                nonNullwhereItemsList.add(obj);
                            }
                        }
                    }
                }
            }
            final Vector nonNullOperators = new Vector();
            for (int oi = 0; oi < whereExp.getOperator().size(); ++oi) {
                final String obj2 = whereExp.getOperator().get(oi).toString();
                if (!obj2.equalsIgnoreCase("&AND")) {
                    nonNullOperators.add(obj2);
                }
            }
            for (int i = 0; i < nonNullwhereItemsList.size(); ++i) {
                final Object obj3 = nonNullwhereItemsList.get(i);
                if (obj3 != null && obj3 instanceof WhereItem) {
                    final WhereItem wi2 = (WhereItem)obj3;
                    if (wi2.getOperator() != null && wi2.getOperator().trim().equals("=")) {
                        String sa = this.getTableAliasWhereL(wi2);
                        String sb = this.getTableAliasWhereR(wi2);
                        if (sa == null || sb == null) {
                            final WhereColumn wcl = wi2.getLeftWhereExp();
                            final WhereColumn wcr = wi2.getRightWhereExp();
                            FromTable ft1 = new FromTable();
                            FromTable ft2 = new FromTable();
                            final Vector leftColumnExp = wcl.getColumnExpression();
                            final Vector rightColumnExp = wcr.getColumnExpression();
                            if (leftColumnExp != null && rightColumnExp != null && leftColumnExp.size() == 1 && rightColumnExp.size() == 1 && leftColumnExp.get(0) instanceof TableColumn && rightColumnExp.get(0) instanceof TableColumn) {
                                final TableColumn tc1 = leftColumnExp.get(0);
                                final TableColumn tc2 = rightColumnExp.get(0);
                                ft1 = MetadataInfoUtil.getTableOfColumn(from_sqs, tc1);
                                ft2 = MetadataInfoUtil.getTableOfColumn(from_sqs, tc2);
                                final String s1 = tc1.getColumnName().trim();
                                final String s2 = tc2.getColumnName().trim();
                                if (!s1.startsWith("'") && !s2.startsWith("'") && ((ft1 != null && ft2 != null) || (tc1.getTableName() != null && tc2.getTableName() != null))) {
                                    sa = tc1.getTableName();
                                    sb = tc2.getTableName();
                                }
                            }
                        }
                        if (sa != null && sb != null && !sb.equalsIgnoreCase("") && !sa.equalsIgnoreCase(sb)) {
                            String opOuter = "";
                            if (i != 0) {
                                opOuter = nonNullOperators.get(i - 1);
                            }
                            if (!opOuter.equalsIgnoreCase("OR")) {
                                joinConditions.add(obj3);
                                nonNullwhereItemsList.set(i, null);
                                if (i != 0) {
                                    final String op = nonNullOperators.get(i - 1);
                                    if (!op.equalsIgnoreCase("OR")) {
                                        if (!op.equals("&AND")) {
                                            nonNullOperators.setElementAt("&AND", i - 1);
                                        }
                                        else if (nonNullOperators.size() > i) {
                                            nonNullOperators.setElementAt("&AND", i - 1);
                                        }
                                    }
                                }
                                else if (nonNullOperators.size() > i) {
                                    nonNullOperators.setElementAt("&AND", i);
                                }
                            }
                        }
                    }
                }
                else if (obj3 != null && obj3 instanceof WhereExpression) {
                    final WhereExpression we = (WhereExpression)obj3;
                    final Vector localWI = this.getJoinConditionsFromWhereExpression(we, from_sqs);
                    if (localWI.size() > 0 && !we.hasNonNullWhereItem()) {
                        if (i != 0) {
                            final String op2 = nonNullOperators.get(i - 1);
                            if (!op2.equalsIgnoreCase("OR")) {
                                if (!op2.equals("&AND")) {
                                    nonNullOperators.setElementAt("&AND", i - 1);
                                }
                                else if (nonNullOperators.size() > i) {
                                    nonNullOperators.setElementAt("&AND", i - 1);
                                }
                            }
                        }
                        else if (nonNullOperators.size() > i) {
                            nonNullOperators.setElementAt("&AND", i);
                        }
                    }
                    joinConditions.addAll(localWI);
                }
            }
            final Vector newWhereItemsList = new Vector();
            for (int k = 0; k < nonNullwhereItemsList.size(); ++k) {
                final Object obj4 = nonNullwhereItemsList.get(k);
                if (obj4 instanceof WhereItem) {
                    if (obj4 != null) {
                        newWhereItemsList.add(nonNullwhereItemsList.get(k));
                    }
                }
                else if (obj4 instanceof WhereExpression && ((WhereExpression)obj4).getWhereItem() != null && ((WhereExpression)obj4).getWhereItem().size() > 0) {
                    boolean nonNullWhereItemPresent2 = false;
                    final WhereExpression wexpObj2 = (WhereExpression)obj4;
                    for (int wi3 = 0; wi3 < wexpObj2.getWhereItem().size(); ++wi3) {
                        if (wexpObj2.getWhereItem().get(wi3) != null) {
                            nonNullWhereItemPresent2 = true;
                        }
                    }
                    if (nonNullWhereItemPresent2) {
                        newWhereItemsList.add(nonNullwhereItemsList.get(k));
                    }
                }
            }
            final Vector newOperatorsList = new Vector();
            for (int l = 0; l < nonNullOperators.size(); ++l) {
                final String s3 = nonNullOperators.get(l);
                if (!s3.trim().equalsIgnoreCase("&AND")) {
                    newOperatorsList.add(s3);
                }
            }
            final Vector finalOperatorsList = new Vector();
            if (newOperatorsList.size() > 0) {
                for (int fi = 0; fi < newWhereItemsList.size() - 1; ++fi) {
                    finalOperatorsList.add(newOperatorsList.get(fi));
                }
            }
            if (newWhereItemsList.size() == 1) {
                finalOperatorsList.removeAllElements();
            }
            whereExp.setWhereItem(newWhereItemsList);
            whereExp.setOperator(finalOperatorsList);
        }
        return joinConditions;
    }
    
    public boolean hasNonNullWhereItem() {
        boolean nonNullWhereItem = false;
        final Vector nonNullwhereItemsList = this.getWhereItems();
        for (int k = 0; k < nonNullwhereItemsList.size(); ++k) {
            final Object obj = nonNullwhereItemsList.get(k);
            if (obj instanceof WhereItem) {
                if (obj != null) {
                    nonNullWhereItem = true;
                    break;
                }
            }
            else if (obj instanceof WhereExpression && ((WhereExpression)obj).getWhereItem() != null && ((WhereExpression)obj).getWhereItem().size() > 0) {
                final WhereExpression wexpObj = (WhereExpression)obj;
                for (int wi = 0; wi < wexpObj.getWhereItem().size(); ++wi) {
                    if (wexpObj.getWhereItem().get(wi) != null) {
                        nonNullWhereItem = true;
                        break;
                    }
                }
            }
        }
        return nonNullWhereItem;
    }
    
    public WhereExpression toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.rownumClause != null && to_sqs != null) {
            this.addLimitClause(to_sqs, from_sqs);
        }
        final WhereExpression we = new WhereExpression();
        we.setOpenBrace(this.openBraces);
        we.setCloseBrace(this.closeBraces);
        this.setTargetDatabase(5);
        if (this.beginOperator != null) {
            we.setBeginOperator(this.beginOperator);
        }
        if (this.operators != null) {
            final Vector newOperators = new Vector();
            for (int i = 0; i < this.operators.size(); ++i) {
                final String op = this.operators.elementAt(i);
                newOperators.addElement(op);
            }
            we.setOperator(newOperators);
        }
        final Vector whereItemList = new Vector();
        for (int i = 0; i < this.whereItems.size(); ++i) {
            if (this.whereItems.elementAt(i) instanceof WhereItem) {
                if (this.whereItems.elementAt(i) == null) {
                    whereItemList.addElement(null);
                }
                final WhereItem wi = this.whereItems.elementAt(i);
                if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
                    if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                        final Vector whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();
                        for (int j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                            whereItemList.addElement(whereItemsListReplacingEqualsClause.get(j).toMySQLSelect(to_sqs, from_sqs));
                            if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                                if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                                    we.getOperator().add("AND");
                                }
                                else {
                                    we.getOperator().add("OR");
                                }
                            }
                        }
                    }
                    else {
                        whereItemList.addElement(wi.toMySQLSelect(to_sqs, from_sqs));
                    }
                    if (wi.getRownumClause() != null) {
                        if (i != 0) {
                            final String op2 = we.getOperator().get(i - 1);
                            if (!op2.equals("&AND")) {
                                we.getOperator().setElementAt("&AND", i - 1);
                            }
                            else if (we.getOperator().size() > i) {
                                we.getOperator().setElementAt("&AND", i);
                            }
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                }
                else {
                    whereItemList.addElement(null);
                    if (i != 0) {
                        final String op2 = we.getOperator().get(i - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", i - 1);
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                    else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                    }
                }
            }
            else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
                final WhereExpression internalWE = this.whereItems.elementAt(i).toMySQLSelect(to_sqs, from_sqs);
                whereItemList.addElement(internalWE);
                if (internalWE.toString() == null) {
                    if (i != 0) {
                        final String op2 = we.getOperator().get(i - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", i - 1);
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                    else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                    }
                }
            }
        }
        we.setWhereItem(whereItemList);
        we.setCheckWhere(this.checkWhere);
        we.setRownumClause(this.rownumClause);
        if (this.topLevel) {
            this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 5, we);
        }
        return we;
    }
    
    public WhereExpression toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.rownumClause != null) {
            this.addFetchClause(to_sqs, from_sqs);
        }
        final WhereExpression we = new WhereExpression();
        we.setOpenBrace(this.openBraces);
        we.setCloseBrace(this.closeBraces);
        this.setTargetDatabase(3);
        if (this.beginOperator != null) {
            we.setBeginOperator(this.beginOperator);
        }
        if (this.operators != null) {
            final Vector newOperators = new Vector();
            for (int i = 0; i < this.operators.size(); ++i) {
                final String op = this.operators.elementAt(i);
                newOperators.addElement(op);
            }
            we.setOperator(newOperators);
        }
        final Vector whereItemList = new Vector();
        for (int i = 0; i < this.whereItems.size(); ++i) {
            if (this.whereItems.elementAt(i) == null) {
                whereItemList.addElement(null);
            }
            if (this.whereItems.elementAt(i) instanceof WhereItem) {
                final WhereItem wi = this.whereItems.elementAt(i);
                wi.setFromTableList(this.fromTableList);
                wi.setStmtTableName(this.stmtTableName);
                if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
                    if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                        final Vector whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();
                        for (int j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                            whereItemList.addElement(whereItemsListReplacingEqualsClause.get(j).toDB2Select(to_sqs, from_sqs));
                            if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                                if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                                    we.getOperator().add("AND");
                                }
                                else {
                                    we.getOperator().add("OR");
                                }
                            }
                        }
                    }
                    else {
                        whereItemList.addElement(wi.toDB2Select(to_sqs, from_sqs));
                    }
                    if (wi.getRownumClause() != null) {
                        if (i != 0) {
                            final String op2 = we.getOperator().get(i - 1);
                            if (!op2.equals("&AND")) {
                                we.getOperator().setElementAt("&AND", i - 1);
                            }
                            else if (we.getOperator().size() > i) {
                                we.getOperator().setElementAt("&AND", i);
                            }
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                }
                else {
                    whereItemList.addElement(null);
                    if (i != 0) {
                        final String op2 = we.getOperator().get(i - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", i - 1);
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                    else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                    }
                }
            }
            else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
                final WhereExpression internalWE = this.whereItems.elementAt(i).toDB2Select(to_sqs, from_sqs);
                whereItemList.addElement(internalWE);
                if (internalWE.toString() == null) {
                    if (i != 0) {
                        final String op2 = we.getOperator().get(i - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", i - 1);
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                    else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                    }
                }
            }
        }
        we.setWhereItem(whereItemList);
        we.setCheckWhere(this.checkWhere);
        we.setRownumClause(this.rownumClause);
        if (this.topLevel) {
            this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 3, we);
        }
        return we;
    }
    
    public WhereExpression toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.rownumClause != null && to_sqs != null) {
            this.addLimitClause(to_sqs, from_sqs);
        }
        final WhereExpression we = new WhereExpression();
        we.setOpenBrace(this.openBraces);
        we.setCloseBrace(this.closeBraces);
        this.setTargetDatabase(4);
        if (this.beginOperator != null) {
            we.setBeginOperator(this.beginOperator);
        }
        if (this.operators != null) {
            final Vector newOperators = new Vector();
            for (int i = 0; i < this.operators.size(); ++i) {
                final String op = this.operators.elementAt(i);
                newOperators.addElement(op);
            }
            we.setOperator(newOperators);
        }
        final Vector whereItemList = new Vector();
        for (int i = 0; i < this.whereItems.size(); ++i) {
            if (this.whereItems.elementAt(i) == null) {
                whereItemList.addElement(null);
            }
            if (this.whereItems.elementAt(i) instanceof WhereItem) {
                final WhereItem wi = this.whereItems.elementAt(i);
                if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
                    if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
                        final Vector whereItemsListReplacingInClause = wi.getWhereItemsReplacingInClause();
                        for (int j = 0; j < whereItemsListReplacingInClause.size(); ++j) {
                            whereItemList.addElement(whereItemsListReplacingInClause.get(j).toPostgreSQLSelect(to_sqs, from_sqs));
                            if (j != whereItemsListReplacingInClause.size() - 1) {
                                we.getOperator().add("AND");
                            }
                        }
                    }
                    else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                        final Vector whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();
                        for (int j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                            whereItemList.addElement(whereItemsListReplacingEqualsClause.get(j).toPostgreSQLSelect(to_sqs, from_sqs));
                            if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                                if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                                    we.getOperator().add("AND");
                                }
                                else {
                                    we.getOperator().add("OR");
                                }
                            }
                        }
                    }
                    else {
                        final WhereItem wiNew = wi.toPostgreSQLSelect(to_sqs, from_sqs);
                        if (wiNew.isNullSafeEqualsOperator() && from_sqs != null && from_sqs.isAmazonRedShift()) {
                            final WhereExpression we2 = new WhereExpression();
                            we2.setOpenBrace("(");
                            we2.setCloseBrace(")");
                            final WhereItem wi2 = new WhereItem();
                            wi2.setLeftWhereExp(wiNew.getLeftWhereExp());
                            wi2.setOperator("IS NULL");
                            final WhereItem wi3 = new WhereItem();
                            wi3.setLeftWhereExp(wiNew.getRightWhereExp());
                            wi3.setOperator("IS NULL");
                            final WhereExpression we3 = new WhereExpression();
                            we3.setOpenBrace("(");
                            we3.setCloseBrace(")");
                            final Vector wiV = new Vector();
                            wiV.add(wi2);
                            wiV.add(wi3);
                            final Vector opV = new Vector();
                            opV.add("AND");
                            we3.setWhereItem(wiV);
                            we3.setOperator(opV);
                            final Vector wiV2 = new Vector();
                            wiV2.add(wiNew);
                            wiV2.add(we3);
                            final Vector opV2 = new Vector();
                            opV2.add("OR");
                            we2.setWhereItem(wiV2);
                            we2.setOperator(opV2);
                            whereItemList.addElement(we2);
                        }
                        else {
                            whereItemList.addElement(wiNew);
                        }
                    }
                    if (wi.getRownumClause() != null) {
                        if (i != 0) {
                            final String op2 = we.getOperator().get(i - 1);
                            if (!op2.equals("&AND")) {
                                we.getOperator().setElementAt("&AND", i - 1);
                            }
                            else if (we.getOperator().size() > i) {
                                we.getOperator().setElementAt("&AND", i);
                            }
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                }
                else {
                    whereItemList.addElement(null);
                    if (i != 0) {
                        final String op2 = we.getOperator().get(i - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", i - 1);
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                    else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                    }
                }
            }
            else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
                final WhereExpression internalWE = this.whereItems.elementAt(i).toPostgreSQLSelect(to_sqs, from_sqs);
                whereItemList.addElement(internalWE);
                if (internalWE.toString() == null) {
                    if (i != 0) {
                        final String op2 = we.getOperator().get(i - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", i - 1);
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                    else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                    }
                }
            }
        }
        we.setWhereItem(whereItemList);
        we.setCheckWhere(this.checkWhere);
        we.setRownumClause(this.rownumClause);
        if (this.topLevel) {
            this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 4, we);
        }
        return we;
    }
    
    public WhereExpression toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.setTargetDatabase(2);
        if (this.rownumClause != null && to_sqs != null) {
            String rownumValue = "0";
            if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
                throw new ConvertException();
            }
            if (this.rownumClause.getRownumValue() instanceof SelectQueryStatement) {
                throw new ConvertException("Conversion failure.. Subquery can't be converted");
            }
            if (this.rownumClause.getRownumValue() instanceof SelectColumn) {
                final SelectColumn sc = (SelectColumn)this.rownumClause.getRownumValue();
                final Vector colExp = sc.getColumnExpression();
                for (int i = 0; i < colExp.size(); ++i) {
                    if (colExp.elementAt(i) instanceof FunctionCalls) {
                        throw new ConvertException("Conversion failure.. Function calls can't be converted");
                    }
                    if (colExp.elementAt(i) instanceof TableColumn) {
                        if (this.rownumClause.getOperator().equals("<=") || this.rownumClause.getOperator().equals("=")) {
                            to_sqs.getSelectStatement().setSelectRowCountVariable(colExp.elementAt(i).toString());
                        }
                        else {
                            to_sqs.getSelectStatement().setSelectRowCountVariable(colExp.elementAt(i).toString() + " - 1");
                        }
                    }
                    else {
                        if (!(colExp.elementAt(i) instanceof String) || colExp.size() != 1) {
                            throw new ConvertException("Conversion failure.. Expression can't be converted");
                        }
                        rownumValue = colExp.elementAt(i);
                    }
                }
            }
            to_sqs.getSelectStatement().setSelectRowSpecifier("TOP");
            if (this.rownumClause.getOperator().equals("<=") || this.rownumClause.getOperator().equals("=")) {
                to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue));
            }
            else {
                to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue) - 1);
            }
        }
        final WhereExpression we = new WhereExpression();
        we.setOpenBrace(this.openBraces);
        we.setCloseBrace(this.closeBraces);
        we.setConcatenation(this.concatenation);
        we.setCommentClass(this.commentObj);
        if (this.beginOperator != null) {
            we.setBeginOperator(this.beginOperator);
        }
        if (this.operators != null) {
            final Vector newOperators = new Vector();
            for (int j = 0; j < this.operators.size(); ++j) {
                final String op = this.operators.elementAt(j);
                newOperators.addElement(op);
            }
            we.setOperator(newOperators);
        }
        final Vector whereItemList = new Vector();
        for (int j = 0; j < this.whereItems.size(); ++j) {
            if (this.whereItems.elementAt(j) == null) {
                whereItemList.addElement(null);
            }
            if (this.whereItems.elementAt(j) instanceof WhereItem) {
                final WhereItem wi = this.whereItems.elementAt(j);
                if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
                    if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
                        final Vector whereItemsListReplacingInClause = wi.getWhereItemsReplacingInClause();
                        for (int k = 0; k < whereItemsListReplacingInClause.size(); ++k) {
                            whereItemList.addElement(whereItemsListReplacingInClause.get(k).toMSSQLServerSelect(to_sqs, from_sqs));
                            if (k != whereItemsListReplacingInClause.size() - 1) {
                                we.getOperator().add("AND");
                            }
                        }
                    }
                    else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                        final Vector whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();
                        for (int k = 0; k < whereItemsListReplacingEqualsClause.size(); ++k) {
                            whereItemList.addElement(whereItemsListReplacingEqualsClause.get(k).toMSSQLServerSelect(to_sqs, from_sqs));
                            if (k != whereItemsListReplacingEqualsClause.size() - 1) {
                                if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                                    we.getOperator().add("AND");
                                }
                                else {
                                    we.getOperator().add("OR");
                                }
                            }
                        }
                    }
                    else {
                        final WhereColumn l_wc_check = wi.getLeftWhereExp();
                        if (wi.getOperator() != null && wi.getOperator().equalsIgnoreCase("IN") && wi.getRightWhereSubQuery() != null && l_wc_check != null && l_wc_check.getColumnExpression() != null && l_wc_check.getColumnExpression().size() > 1 && l_wc_check.getColumnExpression().contains(",")) {
                            if (SwisSQLOptions.splitMulitpleColumnsOfINClause) {
                                final Vector left_col_list_v = new Vector();
                                final WhereColumn l_wc = wi.getLeftWhereExp();
                                if (l_wc.getColumnExpression() != null && l_wc.getColumnExpression().size() != 1 && l_wc.getColumnExpression().contains(",")) {
                                    final Vector col_exp_v = l_wc.getColumnExpression();
                                    for (int l = 0; l < col_exp_v.size(); ++l) {
                                        if (!col_exp_v.elementAt(l).toString().equalsIgnoreCase(",")) {
                                            left_col_list_v.addElement(col_exp_v.elementAt(l));
                                        }
                                    }
                                }
                                final SelectQueryStatement current_rw_subquery = wi.getRightWhereSubQuery();
                                final Vector rw_subquery_selectlist = current_rw_subquery.getSelectStatement().getSelectItemList();
                                for (int m = 0; m < left_col_list_v.size(); ++m) {
                                    final WhereItem wi2 = new WhereItem();
                                    wi2.setLeftWhereExp(left_col_list_v.elementAt(m));
                                    wi2.setOperator("IN");
                                    final SelectQueryStatement sqs1 = new SelectQueryStatement();
                                    final SelectStatement ss1 = new SelectStatement();
                                    final Object only_select_item_list = rw_subquery_selectlist.elementAt(m);
                                    if (only_select_item_list instanceof SelectColumn) {
                                        ((SelectColumn)only_select_item_list).setEndsWith(null);
                                    }
                                    final Vector v = new Vector();
                                    v.addElement(only_select_item_list);
                                    ss1.setSelectClause(current_rw_subquery.getSelectStatement().getSelectClause());
                                    ss1.setSelectItemList(v);
                                    sqs1.setSelectStatement(ss1);
                                    sqs1.setFromClause(current_rw_subquery.getFromClause());
                                    WhereExpression we2 = null;
                                    if (current_rw_subquery.getWhereExpression() != null) {
                                        we2 = current_rw_subquery.getWhereExpression().toMSSQLServerSelect(current_rw_subquery, sqs1);
                                    }
                                    sqs1.setWhereExpression(we2);
                                    if (only_select_item_list instanceof SelectColumn && SwisSQLUtils.isAggregateFunction((SelectColumn)only_select_item_list)) {
                                        sqs1.setGroupByStatement(current_rw_subquery.getGroupByStatement());
                                    }
                                    wi2.setRightWhereSubQuery(sqs1);
                                    whereItemList.addElement(wi2.toMSSQLServerSelect(to_sqs, from_sqs));
                                    if (m < left_col_list_v.size() - 1) {
                                        we.addOperator("AND");
                                    }
                                }
                            }
                            else {
                                whereItemList.addElement(wi.toMSSQLServerSelect(to_sqs, from_sqs));
                            }
                        }
                        else if (wi.getOperator() != null && (wi.getOperator().equalsIgnoreCase("IN") || wi.getOperator().equalsIgnoreCase("NOT IN")) && wi.getRightWhereSubQuery() == null && wi.getRightWhereExp() != null) {
                            final WhereItem mssqlWi = wi.toMSSQLServerSelect(to_sqs, from_sqs);
                            final Vector v2 = mssqlWi.getRightWhereExp().getColumnExpression();
                            if (v2 != null) {
                                final int count = v2.size();
                                WhereExpression inWhereExp = null;
                                for (int j2 = 0; j2 < count; ++j2) {
                                    final Object val = v2.get(j2);
                                    if (val instanceof WhereColumn) {
                                        final String ss2 = ((WhereColumn)val).toString().trim();
                                        if (ss2.equalsIgnoreCase("null")) {
                                            final String op2 = mssqlWi.getOperator();
                                            inWhereExp = new WhereExpression();
                                            inWhereExp.addWhereItem(mssqlWi);
                                            inWhereExp.setOpenBrace("(");
                                            inWhereExp.setCloseBrace(")");
                                            final WhereItem wet = new WhereItem();
                                            final String newOp = op2.equalsIgnoreCase("IN") ? "IS" : "IS NOT";
                                            wet.setOperator(newOp);
                                            wet.setLeftWhereExp(mssqlWi.getLeftWhereExp());
                                            final Vector vv = new Vector();
                                            vv.add("NULL");
                                            final WhereColumn wcc = new WhereColumn();
                                            wcc.setColumnExpression(vv);
                                            wet.setRightWhereExp(wcc);
                                            final String andOrOp = op2.equalsIgnoreCase("IN") ? "OR" : "AND";
                                            inWhereExp.addOperator(andOrOp);
                                            inWhereExp.addWhereItem(wet);
                                            if (j2 + 1 < count) {
                                                final Object kk = v2.get(j2 + 1);
                                                if (kk != null && kk.toString().trim().equals(",")) {
                                                    v2.remove(j2 + 1);
                                                    v2.remove(j2);
                                                    break;
                                                }
                                            }
                                            v2.remove(j2);
                                            if (j2 == v2.size() - 1 && v2.size() - 2 >= 0 && v2.get(v2.size() - 2).toString().trim().equals(",")) {
                                                v2.remove(v2.size() - 2);
                                                break;
                                            }
                                            break;
                                        }
                                    }
                                }
                                final int newCount = v2.size();
                                if (count == newCount) {
                                    whereItemList.addElement(mssqlWi);
                                }
                                else {
                                    whereItemList.addElement(inWhereExp);
                                }
                            }
                            else {
                                whereItemList.addElement(wi.toMSSQLServerSelect(to_sqs, from_sqs));
                            }
                        }
                        else {
                            final WhereItem wiNew = wi.toMSSQLServerSelect(to_sqs, from_sqs);
                            if (wiNew.isNullSafeEqualsOperator()) {
                                final WhereExpression we3 = new WhereExpression();
                                we3.setOpenBrace("(");
                                we3.setCloseBrace(")");
                                final WhereItem wi3 = new WhereItem();
                                wi3.setLeftWhereExp(wiNew.getLeftWhereExp());
                                wi3.setOperator("IS NULL");
                                final WhereItem wi4 = new WhereItem();
                                wi4.setLeftWhereExp(wiNew.getRightWhereExp());
                                wi4.setOperator("IS NULL");
                                final WhereExpression we4 = new WhereExpression();
                                we4.setOpenBrace("(");
                                we4.setCloseBrace(")");
                                final Vector wiV = new Vector();
                                wiV.add(wi3);
                                wiV.add(wi4);
                                final Vector opV = new Vector();
                                opV.add("AND");
                                we4.setWhereItem(wiV);
                                we4.setOperator(opV);
                                final Vector wiV2 = new Vector();
                                wiV2.add(wiNew);
                                wiV2.add(we4);
                                final Vector opV2 = new Vector();
                                opV2.add("OR");
                                we3.setWhereItem(wiV2);
                                we3.setOperator(opV2);
                                whereItemList.addElement(we3);
                            }
                            else {
                                whereItemList.addElement(wiNew);
                            }
                        }
                    }
                    if (wi.getRownumClause() != null) {
                        if (j != 0) {
                            final String op3 = we.getOperator().get(j - 1);
                            if (!op3.equals("&AND")) {
                                we.getOperator().setElementAt("&AND", j - 1);
                            }
                            else if (we.getOperator().size() > j) {
                                we.getOperator().setElementAt("&AND", j);
                            }
                        }
                        else if (we.getOperator().size() > 0) {
                            we.getOperator().setElementAt("&AND", 0);
                        }
                    }
                }
                else if (!SwisSQLAPI.MSSQLSERVER_THETA) {
                    whereItemList.addElement(null);
                    if (j != 0) {
                        final String op3 = we.getOperator().get(j - 1);
                        if (!op3.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", j - 1);
                        }
                        else if (we.getOperator().size() > j) {
                            we.getOperator().setElementAt("&AND", j);
                        }
                    }
                    else if (we.getOperator().size() > j) {
                        we.getOperator().setElementAt("&AND", j);
                    }
                }
                else {
                    whereItemList.addElement(this.whereItems.elementAt(j).toMSSQLServerSelect(to_sqs, from_sqs));
                }
            }
            else if (this.whereItems.elementAt(j) instanceof WhereExpression) {
                final WhereExpression internalWE = this.whereItems.elementAt(j).toMSSQLServerSelect(to_sqs, from_sqs);
                whereItemList.addElement(internalWE);
                if (internalWE.toString() == null) {
                    if (j != 0) {
                        final String op3 = we.getOperator().get(j - 1);
                        if (!op3.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", j - 1);
                        }
                        else if (we.getOperator().size() > j) {
                            we.getOperator().setElementAt("&AND", j);
                        }
                    }
                    else if (we.getOperator().size() > j) {
                        we.getOperator().setElementAt("&AND", j);
                    }
                }
            }
        }
        we.setWhereItem(whereItemList);
        we.setCheckWhere(this.checkWhere);
        we.setRownumClause(this.rownumClause);
        if (!SwisSQLAPI.MSSQLSERVER_THETA && this.topLevel) {
            this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 2, we);
        }
        return we;
    }
    
    public WhereExpression toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.setTargetDatabase(7);
        if (this.rownumClause != null && to_sqs != null) {
            String rownumValue = "0";
            if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
                throw new ConvertException();
            }
            if (this.rownumClause.getRownumValue() instanceof SelectQueryStatement) {
                throw new ConvertException("Conversion failure.. Subquery can't be converted");
            }
            if (this.rownumClause.getRownumValue() instanceof SelectColumn) {
                final SelectColumn sc = (SelectColumn)this.rownumClause.getRownumValue();
                final Vector colExp = sc.getColumnExpression();
                for (int i = 0; i < colExp.size(); ++i) {
                    if (colExp.elementAt(i) instanceof FunctionCalls) {
                        throw new ConvertException("Conversion failure.. Function calls can't be converted");
                    }
                    if (colExp.elementAt(i) instanceof TableColumn) {
                        throw new ConvertException("Conversion failure.. Identifier can't be converted");
                    }
                    if (!(colExp.elementAt(i) instanceof String) || colExp.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expression can't be converted");
                    }
                    rownumValue = colExp.elementAt(i);
                }
            }
            if (this.rownumClause.getOperator().equals("<=") || this.rownumClause.getOperator().equals("=")) {
                to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue));
            }
            else {
                to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue) - 1);
            }
        }
        final WhereExpression we = new WhereExpression();
        we.setOpenBrace(this.openBraces);
        we.setCloseBrace(this.closeBraces);
        we.setObjectContext(this.context);
        if (this.beginOperator != null) {
            we.setBeginOperator(this.beginOperator);
        }
        if (this.operators != null) {
            final Vector newOperators = new Vector();
            for (int j = 0; j < this.operators.size(); ++j) {
                final String op = this.operators.elementAt(j);
                newOperators.addElement(op);
            }
            we.setOperator(newOperators);
        }
        final Vector whereItemList = new Vector();
        for (int j = 0; j < this.whereItems.size(); ++j) {
            if (this.whereItems.elementAt(j) == null) {
                whereItemList.addElement(null);
            }
            if (this.whereItems.elementAt(j) instanceof WhereItem) {
                final WhereItem wi = this.whereItems.elementAt(j);
                wi.setObjectContext(this.context);
                if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
                    if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
                        final Vector whereItemsListReplacingInClause = wi.getWhereItemsReplacingInClause();
                        for (int k = 0; k < whereItemsListReplacingInClause.size(); ++k) {
                            whereItemsListReplacingInClause.get(k).setObjectContext(this.context);
                            whereItemList.addElement(whereItemsListReplacingInClause.get(k).toSybaseSelect(to_sqs, from_sqs));
                            if (k != whereItemsListReplacingInClause.size() - 1) {
                                we.getOperator().add("AND");
                            }
                        }
                    }
                    else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                        final Vector whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();
                        for (int k = 0; k < whereItemsListReplacingEqualsClause.size(); ++k) {
                            whereItemsListReplacingEqualsClause.get(k).setObjectContext(this.context);
                            whereItemList.addElement(whereItemsListReplacingEqualsClause.get(k).toSybaseSelect(to_sqs, from_sqs));
                            if (k != whereItemsListReplacingEqualsClause.size() - 1) {
                                if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                                    we.getOperator().add("AND");
                                }
                                else {
                                    we.getOperator().add("OR");
                                }
                            }
                        }
                    }
                    else {
                        whereItemList.addElement(wi.toSybaseSelect(to_sqs, from_sqs));
                    }
                    if (wi.getRownumClause() != null) {
                        if (j != 0) {
                            final String op2 = we.getOperator().get(j - 1);
                            if (!op2.equals("&AND")) {
                                we.getOperator().setElementAt("&AND", j - 1);
                            }
                            else if (we.getOperator().size() > j) {
                                we.getOperator().setElementAt("&AND", j);
                            }
                        }
                        else if (we.getOperator().size() > 0) {
                            we.getOperator().setElementAt("&AND", 0);
                        }
                    }
                }
                else if (!SwisSQLAPI.MSSQLSERVER_THETA) {
                    whereItemList.addElement(null);
                    if (j != 0) {
                        final String op2 = we.getOperator().get(j - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", j - 1);
                        }
                        else if (we.getOperator().size() > j) {
                            we.getOperator().setElementAt("&AND", j);
                        }
                    }
                    else if (we.getOperator().size() > j) {
                        we.getOperator().setElementAt("&AND", j);
                    }
                }
                else {
                    whereItemList.addElement(this.whereItems.elementAt(j).toSybaseSelect(to_sqs, from_sqs));
                }
            }
            else if (this.whereItems.elementAt(j) instanceof WhereExpression) {
                this.whereItems.elementAt(j).setObjectContext(this.context);
                final WhereExpression internalWE = this.whereItems.elementAt(j).toSybaseSelect(to_sqs, from_sqs);
                whereItemList.addElement(internalWE);
                if (internalWE.toString() == null) {
                    if (j != 0) {
                        final String op2 = we.getOperator().get(j - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", j - 1);
                        }
                        else if (we.getOperator().size() > j) {
                            we.getOperator().setElementAt("&AND", j);
                        }
                    }
                    else if (we.getOperator().size() > j) {
                        we.getOperator().setElementAt("&AND", j);
                    }
                }
            }
        }
        we.setWhereItem(whereItemList);
        we.setCheckWhere(this.checkWhere);
        we.setRownumClause(this.rownumClause);
        if (!SwisSQLAPI.MSSQLSERVER_THETA && this.topLevel) {
            this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 7, we);
        }
        return we;
    }
    
    public WhereExpression toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereExpression we = new WhereExpression();
        this.setTargetDatabase(1);
        if (SwisSQLAPI.convert_OracleThetaJOIN_To_ANSIJOIN) {
            we.setOpenBrace(this.openBraces);
            we.setCloseBrace(this.closeBraces);
            if (this.beginOperator != null) {
                we.setBeginOperator(this.beginOperator);
            }
            if (this.operators != null) {
                final Vector newOperators = new Vector();
                for (int i = 0; i < this.operators.size(); ++i) {
                    final String op = this.operators.elementAt(i);
                    newOperators.addElement(op);
                }
                we.setOperator(newOperators);
            }
            final Vector whereItemList = new Vector();
            for (int i = 0; i < this.whereItems.size(); ++i) {
                if (this.whereItems.elementAt(i) == null) {
                    whereItemList.addElement(null);
                }
                if (this.whereItems.elementAt(i) instanceof WhereItem) {
                    final WhereItem wi = this.whereItems.elementAt(i);
                    if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
                        whereItemList.addElement(wi.toOracleSelect(to_sqs, from_sqs));
                        if (wi.getRownumClause() != null) {
                            if (i != 0) {
                                final String op2 = we.getOperator().get(i - 1);
                                if (!op2.equals("&AND")) {
                                    we.getOperator().setElementAt("&AND", i - 1);
                                }
                                else if (we.getOperator().size() > i) {
                                    we.getOperator().setElementAt("&AND", i);
                                }
                            }
                            else if (we.getOperator().size() > i) {
                                we.getOperator().setElementAt("&AND", i);
                            }
                        }
                    }
                    else {
                        whereItemList.addElement(null);
                        if (i != 0) {
                            final String op2 = we.getOperator().get(i - 1);
                            if (!op2.equals("&AND")) {
                                we.getOperator().setElementAt("&AND", i - 1);
                            }
                            else if (we.getOperator().size() > i) {
                                we.getOperator().setElementAt("&AND", i);
                            }
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                }
                else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
                    final WhereExpression internalWE = this.whereItems.elementAt(i).toOracleSelect(to_sqs, from_sqs);
                    whereItemList.addElement(internalWE);
                    if (internalWE.toString() == null) {
                        if (i != 0) {
                            final String op2 = we.getOperator().get(i - 1);
                            if (!op2.equals("&AND")) {
                                we.getOperator().setElementAt("&AND", i - 1);
                            }
                            else if (we.getOperator().size() > i) {
                                we.getOperator().setElementAt("&AND", i);
                            }
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                }
            }
            we.setWhereItem(whereItemList);
            we.setCheckWhere(this.checkWhere);
            we.setRownumClause(this.rownumClause);
            if (this.topLevel) {
                this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 1, we);
            }
        }
        else {
            we.setOpenBrace(this.openBraces);
            we.setCloseBrace(this.closeBraces);
            we.setCommentClass(this.commentObj);
            if (this.beginOperator != null) {
                we.setBeginOperator(this.beginOperator);
            }
            if (this.operators != null) {
                final Vector newOperators = new Vector();
                for (int i = 0; i < this.operators.size(); ++i) {
                    final String op = this.operators.elementAt(i);
                    if (op != null && op.equalsIgnoreCase("ANSIAND")) {
                        newOperators.addElement("AND");
                    }
                    else if (op != null && op.equalsIgnoreCase("ANSIOR")) {
                        newOperators.addElement("OR");
                    }
                    else {
                        newOperators.addElement(op);
                    }
                }
                we.setOperator(newOperators);
            }
            final Vector whereItemList = new Vector();
            for (int i = 0; i < this.whereItems.size(); ++i) {
                final String singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
                if (this.whereItems.elementAt(i) == null) {
                    whereItemList.addElement(null);
                }
                else if (this.whereItems.elementAt(i) instanceof WhereItem) {
                    final WhereItem wi2 = this.whereItems.elementAt(i).toOracleSelect(to_sqs, from_sqs);
                    whereItemList.addElement(wi2);
                    if (wi2 != null) {
                        final String operator = wi2.getOperator();
                        if (operator != null && (operator.equalsIgnoreCase("like") || operator.equalsIgnoreCase("not like"))) {
                            this.handleRegularExpression(whereItemList, i);
                        }
                    }
                }
                else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
                    whereItemList.addElement(this.whereItems.elementAt(i).toOracleSelect(to_sqs, from_sqs));
                }
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = singleQueryConvertedToMultipleQueryList;
            }
            we.setWhereItem(whereItemList);
            we.setCheckWhere(this.checkWhere);
            we.setRownumClause(this.rownumClause);
        }
        return we;
    }
    
    public WhereExpression toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.setTargetDatabase(6);
        if (this.rownumClause != null && to_sqs != null) {
            String rownumValue = "0";
            if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
                throw new ConvertException();
            }
            if (this.rownumClause.getRownumValue() instanceof SelectQueryStatement) {
                throw new ConvertException("Conversion failure.. Subquery can't be converted");
            }
            if (this.rownumClause.getRownumValue() instanceof SelectColumn) {
                final SelectColumn sc = (SelectColumn)this.rownumClause.getRownumValue();
                final Vector colExp = sc.getColumnExpression();
                for (int i = 0; i < colExp.size(); ++i) {
                    if (colExp.elementAt(i) instanceof FunctionCalls) {
                        throw new ConvertException("Conversion failure.. Function calls can't be converted");
                    }
                    if (colExp.elementAt(i) instanceof TableColumn) {
                        throw new ConvertException("Conversion failure.. Identifier can't be converted");
                    }
                    if (!(colExp.elementAt(i) instanceof String) || colExp.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expression can't be converted");
                    }
                    rownumValue = colExp.elementAt(i);
                }
            }
            to_sqs.getSelectStatement().setInformixRowSpecifier("FIRST");
            if (this.rownumClause.getOperator().equals("<=")) {
                to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue));
            }
            else {
                to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue) - 1);
            }
        }
        final WhereExpression we = new WhereExpression();
        we.setOpenBrace(this.openBraces);
        we.setCloseBrace(this.closeBraces);
        if (this.beginOperator != null) {
            we.setBeginOperator(this.beginOperator);
        }
        if (this.operators != null) {
            final Vector newOperators = new Vector();
            for (int j = 0; j < this.operators.size(); ++j) {
                final String op = this.operators.elementAt(j);
                if (op != null && op.equalsIgnoreCase("ANSIAND")) {
                    newOperators.addElement("AND");
                }
                else if (op != null && op.equalsIgnoreCase("ANSIOR")) {
                    newOperators.addElement("OR");
                }
                else {
                    newOperators.addElement(op);
                }
            }
            we.setOperator(newOperators);
        }
        final Vector whereItemList = new Vector();
        for (int j = 0; j < this.whereItems.size(); ++j) {
            if (this.whereItems.elementAt(j) == null) {
                whereItemList.addElement(null);
            }
            if (this.whereItems.elementAt(j) instanceof WhereItem) {
                final WhereItem wi = this.whereItems.elementAt(j);
                if (this.isWhereItemInClauseIsMultipleAndNotSubquery(wi)) {
                    final Vector whereItemsListReplacingInClause = wi.getWhereItemsReplacingInClause();
                    for (int k = 0; k < whereItemsListReplacingInClause.size(); ++k) {
                        whereItemList.addElement(whereItemsListReplacingInClause.get(k).toInformixSelect(to_sqs, from_sqs));
                        if (k != whereItemsListReplacingInClause.size() - 1) {
                            we.getOperator().add("AND");
                        }
                    }
                }
                else if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                    final Vector whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();
                    for (int k = 0; k < whereItemsListReplacingEqualsClause.size(); ++k) {
                        whereItemList.addElement(whereItemsListReplacingEqualsClause.get(k).toInformixSelect(to_sqs, from_sqs));
                        if (k != whereItemsListReplacingEqualsClause.size() - 1) {
                            if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                                we.getOperator().add("AND");
                            }
                            else {
                                we.getOperator().add("OR");
                            }
                        }
                    }
                }
                else {
                    whereItemList.addElement(wi.toInformixSelect(to_sqs, from_sqs));
                }
                if (wi.getRownumClause() != null) {
                    if (j != 0) {
                        final String op2 = we.getOperator().get(j - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", j - 1);
                        }
                        else if (we.getOperator().size() > j) {
                            we.getOperator().setElementAt("&AND", j);
                        }
                    }
                    else if (we.getOperator().size() > 0) {
                        we.getOperator().setElementAt("&AND", 0);
                    }
                }
            }
            else if (this.whereItems.elementAt(j) instanceof WhereExpression) {
                final WhereExpression internalWE = this.whereItems.elementAt(j).toInformixSelect(to_sqs, from_sqs);
                whereItemList.addElement(internalWE);
                if (internalWE.toString() == null) {
                    if (j != 0) {
                        final String op2 = we.getOperator().get(j - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", j - 1);
                        }
                        else if (we.getOperator().size() > j) {
                            we.getOperator().setElementAt("&AND", j);
                        }
                    }
                    else if (we.getOperator().size() > j) {
                        we.getOperator().setElementAt("&AND", j);
                    }
                }
            }
        }
        we.setWhereItem(whereItemList);
        we.setCheckWhere(this.checkWhere);
        we.setRownumClause(this.rownumClause);
        if (this.topLevel) {
            this.setNewFromClauseWithInformixJoin(to_sqs, from_sqs, 6);
        }
        return we;
    }
    
    public WhereExpression toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereExpression we = new WhereExpression();
        we.setOpenBrace(this.openBraces);
        we.setCloseBrace(this.closeBraces);
        if (this.beginOperator != null) {
            we.setBeginOperator(this.beginOperator);
        }
        if (this.operators != null) {
            final Vector newOperators = new Vector();
            for (int i = 0; i < this.operators.size(); ++i) {
                final String op = this.operators.elementAt(i);
                newOperators.addElement(op);
            }
            we.setOperator(newOperators);
        }
        final Vector whereItemList = new Vector();
        for (int i = 0; i < this.whereItems.size(); ++i) {
            if (this.whereItems.elementAt(i) == null) {
                whereItemList.addElement(null);
            }
            if (this.whereItems.elementAt(i) instanceof WhereItem) {
                final WhereItem wi = this.whereItems.elementAt(i);
                if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
                    whereItemList.addElement(wi.toANSISelect(to_sqs, from_sqs));
                    if (wi.getRownumClause() != null) {
                        if (i != 0) {
                            final String op2 = we.getOperator().get(i - 1);
                            if (!op2.equals("&AND")) {
                                we.getOperator().setElementAt("&AND", i - 1);
                            }
                            else if (we.getOperator().size() > i) {
                                we.getOperator().setElementAt("&AND", i);
                            }
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                }
                else {
                    whereItemList.addElement(null);
                    if (i != 0) {
                        final String op2 = we.getOperator().get(i - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", i - 1);
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                    else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                    }
                }
            }
            else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
                final WhereExpression internalWE = this.whereItems.elementAt(i).toANSISelect(to_sqs, from_sqs);
                whereItemList.addElement(internalWE);
                if (internalWE.toString() == null) {
                    if (i != 0) {
                        final String op2 = we.getOperator().get(i - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", i - 1);
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                    else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                    }
                }
            }
        }
        we.setWhereItem(whereItemList);
        we.setCheckWhere(this.checkWhere);
        we.setRownumClause(this.rownumClause);
        if (this.topLevel) {
            this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 8, we);
        }
        return we;
    }
    
    public WhereExpression toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereExpression we = new WhereExpression();
        we.setOpenBrace(this.openBraces);
        we.setCloseBrace(this.closeBraces);
        this.setTargetDatabase(12);
        this.thetaJoinPresent = false;
        if (this.beginOperator != null) {
            we.setBeginOperator(this.beginOperator);
        }
        if (this.operators != null) {
            final Vector newOperators = new Vector();
            for (int i = 0; i < this.operators.size(); ++i) {
                final String op = this.operators.elementAt(i);
                newOperators.addElement(op);
            }
            we.setOperator(newOperators);
        }
        final Vector whereItemList = new Vector();
        for (int i = 0; i < this.whereItems.size(); ++i) {
            if (this.whereItems.elementAt(i) == null) {
                whereItemList.addElement(null);
            }
            if (this.whereItems.elementAt(i) instanceof WhereItem) {
                final WhereItem wi = this.whereItems.elementAt(i);
                wi.setCaseExpressionBool(this.isCaseExpression());
                if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
                    final WhereItem teradataWi = wi.toTeradataSelect(to_sqs, from_sqs);
                    whereItemList.addElement(teradataWi);
                    if (teradataWi != null && teradataWi.getTeradataSysCalendarWhereItem() != null) {
                        whereItemList.addElement(teradataWi.getTeradataSysCalendarWhereItem());
                        we.getOperator().addElement("AND");
                    }
                    if (wi.getRownumClause() != null && !this.isCaseExpression()) {
                        if (i != 0) {
                            final String op2 = we.getOperator().get(i - 1);
                            if (!op2.equals("&AND")) {
                                we.getOperator().setElementAt("&AND", i - 1);
                            }
                            else if (we.getOperator().size() > i) {
                                we.getOperator().setElementAt("&AND", i);
                            }
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                }
                else {
                    if (!this.thetaJoinPresent) {
                        this.thetaJoinPresent = true;
                    }
                    whereItemList.addElement(null);
                    if (i != 0) {
                        final String op3 = we.getOperator().get(i - 1);
                        if (!op3.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", i - 1);
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                    else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                    }
                }
            }
            else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
                this.whereItems.elementAt(i).setCaseExpressionBool(this.isCaseExpression());
                final WhereExpression internalWE = this.whereItems.elementAt(i).toTeradataSelect(to_sqs, from_sqs);
                if (!this.thetaJoinPresent) {
                    this.thetaJoinPresent = internalWE.isThetaJoinPresent();
                }
                whereItemList.addElement(internalWE);
                if (internalWE.toString() == null) {
                    if (i != 0) {
                        final String op3 = we.getOperator().get(i - 1);
                        if (!op3.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", i - 1);
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                    else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                    }
                }
            }
        }
        we.setWhereItem(whereItemList);
        we.setCheckWhere(this.checkWhere);
        we.setRownumClause(this.rownumClause);
        if (this.thetaJoinPresent) {
            we.setThetaJoinPresent(true);
        }
        if (this.topLevel && this.thetaJoinPresent) {
            this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 12, we);
        }
        return we;
    }
    
    public WhereExpression toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        this.setTargetDatabase(10);
        if (this.rownumClause != null && to_sqs != null) {
            String rownumValue = "0";
            if (to_sqs.getSelectStatement().getSelectRowSpecifier() != null) {
                throw new ConvertException();
            }
            if (this.rownumClause.getRownumValue() instanceof SelectQueryStatement) {
                throw new ConvertException("Conversion failure.. Subquery can't be converted");
            }
            if (this.rownumClause.getRownumValue() instanceof SelectColumn) {
                final SelectColumn sc = (SelectColumn)this.rownumClause.getRownumValue();
                final Vector colExp = sc.getColumnExpression();
                for (int i = 0; i < colExp.size(); ++i) {
                    if (colExp.elementAt(i) instanceof FunctionCalls) {
                        throw new ConvertException("Conversion failure.. Function calls can't be converted");
                    }
                    if (colExp.elementAt(i) instanceof TableColumn) {
                        if (this.rownumClause.getOperator().equals("<=") || this.rownumClause.getOperator().equals("=")) {
                            to_sqs.getSelectStatement().setSelectRowCountVariable(colExp.elementAt(i).toString());
                        }
                        else {
                            to_sqs.getSelectStatement().setSelectRowCountVariable(colExp.elementAt(i).toString() + " - 1");
                        }
                    }
                    else {
                        if (!(colExp.elementAt(i) instanceof String) || colExp.size() != 1) {
                            throw new ConvertException("Conversion failure.. Expression can't be converted");
                        }
                        rownumValue = colExp.elementAt(i);
                    }
                }
            }
            to_sqs.getSelectStatement().setSelectRowSpecifier("FIRST");
            if (this.rownumClause.getOperator().equals("<=") || this.rownumClause.getOperator().equals("=")) {
                to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue));
            }
            else {
                to_sqs.getSelectStatement().setSelectRowCount(Integer.parseInt(rownumValue) - 1);
            }
        }
        final WhereExpression we = new WhereExpression();
        we.setOpenBrace(this.openBraces);
        we.setCloseBrace(this.closeBraces);
        if (this.beginOperator != null) {
            we.setBeginOperator(this.beginOperator);
        }
        if (this.operators != null) {
            final Vector newOperators = new Vector();
            for (int j = 0; j < this.operators.size(); ++j) {
                final String op = this.operators.elementAt(j);
                if (op != null && op.equalsIgnoreCase("ANSIAND")) {
                    newOperators.addElement("AND");
                }
                else if (op != null && op.equalsIgnoreCase("ANSIOR")) {
                    newOperators.addElement("OR");
                }
                else {
                    newOperators.addElement(op);
                }
            }
            we.setOperator(newOperators);
        }
        final Vector whereItemList = new Vector();
        for (int j = 0; j < this.whereItems.size(); ++j) {
            final String singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
            if (this.whereItems.elementAt(j) == null) {
                whereItemList.addElement(null);
            }
            else if (this.whereItems.elementAt(j) instanceof WhereItem) {
                whereItemList.addElement(this.whereItems.elementAt(j).toTimesTenSelect(to_sqs, from_sqs));
            }
            else if (this.whereItems.elementAt(j) instanceof WhereExpression) {
                whereItemList.addElement(this.whereItems.elementAt(j).toTimesTenSelect(to_sqs, from_sqs));
            }
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = singleQueryConvertedToMultipleQueryList;
        }
        we.setWhereItem(whereItemList);
        we.setCheckWhere(this.checkWhere);
        we.setRownumClause(this.rownumClause);
        return we;
    }
    
    public WhereExpression toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final WhereExpression we = new WhereExpression();
        we.setOpenBrace(this.openBraces);
        we.setCloseBrace(this.closeBraces);
        this.setTargetDatabase(11);
        if (this.rownumClause != null && to_sqs != null) {
            this.addLimitClause(to_sqs, from_sqs);
        }
        if (this.beginOperator != null) {
            we.setBeginOperator(this.beginOperator);
        }
        if (this.operators != null) {
            final Vector newOperators = new Vector();
            for (int i = 0; i < this.operators.size(); ++i) {
                final String op = this.operators.elementAt(i);
                newOperators.addElement(op);
            }
            we.setOperator(newOperators);
        }
        final Vector whereItemList = new Vector();
        for (int i = 0; i < this.whereItems.size(); ++i) {
            if (this.whereItems.elementAt(i) == null) {
                whereItemList.addElement(null);
            }
            if (this.whereItems.elementAt(i) instanceof WhereItem) {
                final WhereItem wi = this.whereItems.elementAt(i);
                if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
                    whereItemList.addElement(wi.toNetezzaSelect(to_sqs, from_sqs));
                    if (wi.getRownumClause() != null) {
                        if (i != 0) {
                            final String op2 = we.getOperator().get(i - 1);
                            if (!op2.equals("&AND")) {
                                we.getOperator().setElementAt("&AND", i - 1);
                            }
                            else if (we.getOperator().size() > i) {
                                we.getOperator().setElementAt("&AND", i);
                            }
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                }
                else {
                    whereItemList.addElement(null);
                    if (i != 0) {
                        final String op2 = we.getOperator().get(i - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", i - 1);
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                    else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                    }
                }
            }
            else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
                final WhereExpression internalWE = this.whereItems.elementAt(i).toNetezzaSelect(to_sqs, from_sqs);
                whereItemList.addElement(internalWE);
                if (internalWE.toString() == null) {
                    if (i != 0) {
                        final String op2 = we.getOperator().get(i - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", i - 1);
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                    else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                    }
                }
            }
        }
        we.setWhereItem(whereItemList);
        we.setCheckWhere(this.checkWhere);
        we.setRownumClause(this.rownumClause);
        if (this.topLevel) {
            this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 11, we);
        }
        return we;
    }
    
    private void handleRegularExpression(final Vector whereItemsList, final int i) {
        final WhereItem wi = whereItemsList.get(i);
        final WhereColumn rwc = wi.getRightWhereExp();
        final WhereColumn lwc = wi.getLeftWhereExp();
        final String operator = wi.getOperator();
        boolean isNotLike = false;
        if (operator.equalsIgnoreCase("not like")) {
            isNotLike = true;
        }
        if (rwc != null) {
            final Vector colExp = rwc.getColumnExpression();
            for (int j = 0; j < colExp.size(); ++j) {
                final Object obj = colExp.get(j);
                if (obj instanceof String && ((String)obj).startsWith("'")) {
                    final String literal = (String)obj;
                    final int index1 = literal.indexOf("[");
                    final int index2 = literal.indexOf("]");
                    if (index1 != -1 && index2 != -1 && index2 != index1 + 1) {
                        String subRegExp = literal.substring(index1 + 1, index2);
                        final String replaceStr = "[" + subRegExp + "]";
                        if (subRegExp.startsWith("(") && subRegExp.endsWith(")") && subRegExp.length() > 2) {
                            subRegExp = subRegExp.substring(1, subRegExp.length() - 1);
                        }
                        final WhereExpression we = new WhereExpression();
                        we.setOpenBrace("(");
                        we.setCloseBrace(")");
                        if (subRegExp.startsWith("^")) {
                            subRegExp = subRegExp.substring(1);
                            if (subRegExp.startsWith("(") && subRegExp.endsWith(")") && subRegExp.length() > 2) {
                                subRegExp = subRegExp.substring(1, subRegExp.length() - 1);
                            }
                            final ArrayList rightSideItems = new ArrayList();
                            if (subRegExp.length() == 3 && subRegExp.indexOf("-") == 1) {
                                final int startInt = subRegExp.charAt(0);
                                for (int endInt = subRegExp.charAt(2), s = startInt; s <= endInt; ++s) {
                                    rightSideItems.add("'" + (char)s + "'");
                                }
                            }
                            else {
                                for (int k = 0; k < subRegExp.length(); ++k) {
                                    rightSideItems.add("'" + subRegExp.charAt(k) + "'");
                                }
                            }
                            if (literal.startsWith("'%")) {
                                final String newLiteral = StringFunctions.replaceFirst("_", replaceStr, literal);
                                colExp.setElementAt(newLiteral, j);
                                String replaceFnLiteral = literal;
                                replaceFnLiteral = "'" + replaceFnLiteral.substring(2);
                                if (replaceFnLiteral.endsWith("%'")) {
                                    replaceFnLiteral = replaceFnLiteral.substring(0, replaceFnLiteral.length() - 2) + "'";
                                }
                                final WhereExpression we2 = new WhereExpression();
                                we2.setOpenBrace("(");
                                we2.setCloseBrace(")");
                                for (int l = 0; l < rightSideItems.size(); ++l) {
                                    String character = rightSideItems.get(l).toString();
                                    character = character.substring(1, 2);
                                    final WhereItem newWI = new WhereItem();
                                    final WhereColumn newLWC = new WhereColumn();
                                    final Vector newLWCExp = new Vector();
                                    newLWC.setColumnExpression(newLWCExp);
                                    final FunctionCalls fc = new FunctionCalls();
                                    final TableColumn tcfn = new TableColumn();
                                    tcfn.setColumnName("REPLACE");
                                    fc.setFunctionName(tcfn);
                                    final Vector fnArgs = new Vector();
                                    fc.setFunctionArguments(fnArgs);
                                    final SelectColumn sc = new SelectColumn();
                                    sc.setColumnExpression(lwc.getColumnExpression());
                                    fnArgs.add(sc);
                                    final String secondArg = StringFunctions.replaceFirst(character, replaceStr, replaceFnLiteral);
                                    fnArgs.add(secondArg);
                                    fnArgs.add("'x'");
                                    newLWCExp.add(fc);
                                    newWI.setLeftWhereExp(newLWC);
                                    newWI.setOperator("LIKE");
                                    newWI.setRightWhereExp(rwc);
                                    if (isNotLike) {
                                        we2.addWhereItem(newWI);
                                    }
                                    else {
                                        we.addWhereItem(newWI);
                                    }
                                    if (l + 1 < rightSideItems.size()) {
                                        if (isNotLike) {
                                            we2.addOperator("AND");
                                        }
                                        else {
                                            we.addOperator("AND");
                                        }
                                    }
                                }
                                if (isNotLike) {
                                    we.setOpenBrace(null);
                                    we.setCloseBrace(null);
                                    final WhereItem newWI2 = new WhereItem();
                                    newWI2.setLeftWhereExp(null);
                                    newWI2.setOperator("NOT");
                                    final WhereColumn newRWC = new WhereColumn();
                                    final Vector newRWCExp = new Vector();
                                    newRWCExp.add(we2);
                                    newRWC.setColumnExpression(newRWCExp);
                                    newWI2.setRightWhereExp(newRWC);
                                    we.addWhereItem(newWI2);
                                }
                            }
                            else {
                                final WhereItem wi2 = new WhereItem();
                                wi2.setLeftWhereExp(lwc);
                                wi2.setOperator("LIKE");
                                final String newLiteral2 = StringFunctions.replaceFirst("_", replaceStr, literal);
                                colExp.setElementAt(newLiteral2, j);
                                wi2.setRightWhereExp(rwc);
                                final WhereItem wi3 = new WhereItem();
                                final WhereColumn lwc2 = new WhereColumn();
                                final Vector lwcExp = new Vector();
                                final FunctionCalls fc2 = new FunctionCalls();
                                final Vector fnArgs2 = new Vector();
                                fnArgs2.add(lwc);
                                fnArgs2.add(index1 + "");
                                fnArgs2.add("1");
                                final TableColumn tcfn2 = new TableColumn();
                                tcfn2.setColumnName("SUBSTR");
                                fc2.setFunctionName(tcfn2);
                                fc2.setFunctionArguments(fnArgs2);
                                lwcExp.add(fc2);
                                lwc2.setColumnExpression(lwcExp);
                                wi3.setLeftWhereExp(lwc2);
                                if (isNotLike) {
                                    wi3.setOperator("IN");
                                }
                                else {
                                    wi3.setOperator("NOT IN");
                                }
                                final WhereColumn rwc2 = new WhereColumn();
                                final Vector rwcExp = new Vector();
                                rwcExp.add("(");
                                for (int m = 0; m < rightSideItems.size(); ++m) {
                                    rwcExp.add(rightSideItems.get(m));
                                    if (m + 1 < rightSideItems.size()) {
                                        rwcExp.add(",");
                                    }
                                }
                                rwcExp.add(")");
                                rwc2.setColumnExpression(rwcExp);
                                wi3.setRightWhereExp(rwc2);
                                if (isNotLike) {
                                    final WhereExpression we3 = new WhereExpression();
                                    we3.setOpenBrace("(");
                                    we3.setCloseBrace(")");
                                    we3.addWhereItem(wi2);
                                    we3.addOperator("AND");
                                    we3.addWhereItem(wi3);
                                    we.addWhereExpression(we3);
                                    we.addOperator("OR");
                                    final WhereItem wi4 = new WhereItem();
                                    wi4.setLeftWhereExp(lwc);
                                    wi4.setOperator("NOT LIKE");
                                    wi4.setRightWhereExp(rwc);
                                    we.addWhereItem(wi4);
                                }
                                else {
                                    we.addWhereItem(wi2);
                                    we.addOperator("AND");
                                    we.addWhereItem(wi3);
                                }
                            }
                            whereItemsList.setElementAt(we, i);
                            break;
                        }
                        if (subRegExp.length() != 3 || subRegExp.indexOf("-") != 1) {
                            final String first = literal.substring(0, index1);
                            final String last = literal.substring(index2 + 1);
                            for (int k2 = 0; k2 < subRegExp.length(); ++k2) {
                                String newLiteral3 = null;
                                boolean oracleWildCards = false;
                                if (subRegExp.length() == 1 && (subRegExp.charAt(0) == '%' || subRegExp.charAt(0) == '_')) {
                                    newLiteral3 = first + "\\" + subRegExp.charAt(0) + last;
                                    oracleWildCards = true;
                                }
                                else {
                                    newLiteral3 = first + subRegExp.charAt(k2) + last;
                                }
                                colExp.setElementAt(newLiteral3, j);
                                final Vector newColExp = new Vector();
                                for (int n = 0; n < colExp.size(); ++n) {
                                    newColExp.add(colExp.get(n));
                                }
                                if (oracleWildCards) {
                                    newColExp.add("ESCAPE");
                                    newColExp.add("'\\'");
                                }
                                final WhereItem newWI = new WhereItem();
                                newWI.setLeftWhereExp(lwc);
                                newWI.setOperator(operator);
                                final WhereColumn newRWC2 = new WhereColumn();
                                newRWC2.setColumnExpression(newColExp);
                                newRWC2.setOpenBrace(rwc.getOpenBrace());
                                newRWC2.setCloseBrace(rwc.getCloseBrace());
                                newWI.setRightWhereExp(newRWC2);
                                we.addWhereItem(newWI);
                                if (k2 + 1 < subRegExp.length()) {
                                    if (isNotLike) {
                                        we.addOperator("AND");
                                    }
                                    else {
                                        we.addOperator("OR");
                                    }
                                }
                            }
                            whereItemsList.setElementAt(we, i);
                            break;
                        }
                        if (literal.startsWith("'%")) {
                            final int startInt2 = subRegExp.charAt(0);
                            for (int endInt2 = subRegExp.charAt(2), s2 = startInt2; s2 <= endInt2; ++s2) {
                                final WhereItem newWI3 = new WhereItem();
                                newWI3.setLeftWhereExp(lwc);
                                newWI3.setOperator(wi.getOperator());
                                final String newLiteral4 = StringFunctions.replaceFirst((char)s2 + "", replaceStr, literal);
                                colExp.setElementAt(newLiteral4, j);
                                final Vector newColExp = new Vector();
                                for (int n = 0; n < colExp.size(); ++n) {
                                    newColExp.add(colExp.get(n));
                                }
                                final WhereColumn newRWC3 = new WhereColumn();
                                newRWC3.setColumnExpression(newColExp);
                                newRWC3.setOpenBrace(rwc.getOpenBrace());
                                newRWC3.setCloseBrace(rwc.getCloseBrace());
                                newWI3.setRightWhereExp(newRWC3);
                                we.addWhereItem(newWI3);
                                if (s2 + 1 <= endInt2) {
                                    if (isNotLike) {
                                        we.addOperator("AND");
                                    }
                                    else {
                                        we.addOperator("OR");
                                    }
                                }
                            }
                            whereItemsList.setElementAt(we, i);
                            break;
                        }
                        final String startChar = "'" + subRegExp.charAt(0) + "'";
                        final String endChar = "'" + subRegExp.charAt(2) + "'";
                        final WhereItem wi5 = new WhereItem();
                        wi5.setLeftWhereExp(lwc);
                        wi5.setOperator("LIKE");
                        String newLiteral3 = StringFunctions.replaceFirst("_", replaceStr, literal);
                        colExp.setElementAt(newLiteral3, j);
                        wi5.setRightWhereExp(rwc);
                        final WhereItem wi6 = new WhereItem();
                        final WhereColumn lwc3 = new WhereColumn();
                        final Vector lwcExp2 = new Vector();
                        final FunctionCalls fc3 = new FunctionCalls();
                        final Vector fnArgs3 = new Vector();
                        fnArgs3.add(lwc);
                        fnArgs3.add(index1 + "");
                        fnArgs3.add("1");
                        final TableColumn tcfn3 = new TableColumn();
                        tcfn3.setColumnName("SUBSTR");
                        fc3.setFunctionName(tcfn3);
                        fc3.setFunctionArguments(fnArgs3);
                        lwcExp2.add(fc3);
                        lwc3.setColumnExpression(lwcExp2);
                        wi6.setLeftWhereExp(lwc3);
                        if (isNotLike) {
                            wi6.setOperator("NOT BETWEEN");
                        }
                        else {
                            wi6.setOperator("BETWEEN");
                        }
                        final WhereColumn rwc3 = new WhereColumn();
                        final Vector rwcExp2 = new Vector();
                        rwcExp2.add(startChar);
                        rwcExp2.add("AND");
                        rwcExp2.add(endChar);
                        rwc3.setColumnExpression(rwcExp2);
                        wi6.setRightWhereExp(rwc3);
                        if (isNotLike) {
                            final WhereExpression we4 = new WhereExpression();
                            we4.setOpenBrace("(");
                            we4.setCloseBrace(")");
                            we4.addWhereItem(wi5);
                            we4.addOperator("AND");
                            we4.addWhereItem(wi6);
                            we.addWhereExpression(we4);
                            we.addOperator("OR");
                            final WhereItem wi7 = new WhereItem();
                            wi7.setLeftWhereExp(lwc);
                            wi7.setOperator("NOT LIKE");
                            wi7.setRightWhereExp(rwc);
                            we.addWhereItem(wi7);
                        }
                        else {
                            we.addWhereItem(wi5);
                            we.addOperator("AND");
                            we.addWhereItem(wi6);
                        }
                        whereItemsList.setElementAt(we, i);
                        break;
                    }
                }
            }
        }
    }
    
    private String getPartialMatchKey(final Iterator it, final String key) {
        while (it.hasNext()) {
            final String existingKey = it.next().toString();
            if (key.indexOf(existingKey) != -1) {
                return existingKey;
            }
            if (existingKey.indexOf(key) != -1) {
                return existingKey;
            }
            if (key.indexOf("-&&") != -1 && existingKey.indexOf(key.substring(0, key.indexOf("-&&"))) != -1) {
                return existingKey;
            }
        }
        return null;
    }
    
    private FromClause handleInnerJoin(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final int database, final WhereExpression whereExpression, final Vector remainingFromItemList, final FromClause convertedFromClause) throws ConvertException {
        try {
            final Vector fromItemList = convertedFromClause.getFromItemList();
            final int fromItemSize = fromItemList.size();
            int fis = 0;
            int baseTableIndex = -1;
            final LinkedHashMap joinedTables = new LinkedHashMap();
            final LinkedHashMap newInnerJoinedTables = new LinkedHashMap();
            StringBuffer joinTable = new StringBuffer();
            boolean joinPresent = false;
            while (fis < fromItemSize) {
                final FromTable ft = fromItemList.get(fis);
                if (ft != null && ft.getJoinClause() == null) {
                    if (joinTable.length() > 0) {
                        final FromClause newFromClause = new FromClause();
                        final Vector newFromItems = new Vector();
                        newFromItems.addAll(fromItemList.subList(baseTableIndex, fis));
                        newFromClause.setFromItemList(newFromItems);
                        newFromClause.setOpenBraces("(");
                        newFromClause.setClosedBraces(")");
                        joinedTables.put(joinTable.toString(), newFromClause);
                        joinTable = new StringBuffer();
                    }
                    baseTableIndex = fis;
                    if (!remainingFromItemList.contains(ft)) {
                        if (ft.getAliasName() != null) {
                            joinTable.append("&" + ft.getAliasName().trim() + "&");
                        }
                        else {
                            joinTable.append("&" + ft.getTableName().toString().trim() + "&");
                        }
                        if (fis == fromItemSize - 1) {
                            final FromClause newFromClause = new FromClause();
                            final Vector newFromItems = new Vector();
                            newFromItems.add(fromItemList.get(fis));
                            newFromClause.setFromItemList(newFromItems);
                            newFromClause.setOpenBraces("(");
                            newFromClause.setClosedBraces(")");
                            joinedTables.put(joinTable.toString(), newFromClause);
                        }
                    }
                }
                else if (ft != null && ft.getJoinClause() != null) {
                    joinPresent = true;
                    if (ft.getAliasName() != null) {
                        joinTable.append("-&" + ft.getAliasName().trim() + "&");
                    }
                    else {
                        joinTable.append("-&" + ft.getTableName().toString().trim() + "&");
                    }
                }
                else if (ft == null) {
                    baseTableIndex = fis;
                }
                ++fis;
            }
            if (joinPresent) {
                final Vector randomInnerJoinCondition = this.getJoinConditionsFromWhereExpression(whereExpression, from_sqs);
                Vector innerJoinCondition = null;
                if (fromItemList.indexOf(null) == -1) {
                    innerJoinCondition = this.arrangeTheWhereItemListAccordingToTheOrderInFromItemList(randomInnerJoinCondition, fromItemList);
                }
                else {
                    innerJoinCondition = randomInnerJoinCondition;
                }
                final int innerJoinConditionSize = innerJoinCondition.size();
                int ijc = 0;
                final LinkedHashMap innerJoinTables = new LinkedHashMap();
                while (ijc < innerJoinConditionSize) {
                    WhereItem wi = null;
                    if (innerJoinCondition.get(ijc) instanceof WhereItem) {
                        wi = innerJoinCondition.get(ijc);
                    }
                    final ArrayList leftExpAliases = this.getTableAliasesWhereL(wi);
                    final ArrayList rightExpAliases = this.getTableAliasesWhereR(wi);
                    final StringBuffer hashBuf = new StringBuffer();
                    for (int k = 0; k < leftExpAliases.size(); ++k) {
                        if (k == leftExpAliases.size() - 1) {
                            hashBuf.append("&" + leftExpAliases.get(k).toString().trim() + "&-");
                        }
                        else {
                            hashBuf.append("&" + leftExpAliases.get(k).toString().trim() + "&-");
                        }
                    }
                    for (int j = 0; j < rightExpAliases.size(); ++j) {
                        if (j == rightExpAliases.size() - 1) {
                            hashBuf.append("&" + rightExpAliases.get(j).toString().trim() + "&-");
                        }
                        else {
                            hashBuf.append("&" + rightExpAliases.get(j).toString().trim() + "&-");
                        }
                    }
                    final String hashKey = hashBuf.substring(0, hashBuf.lastIndexOf("-")).toString();
                    String existingKey = null;
                    if (innerJoinTables.containsKey(hashKey)) {
                        existingKey = hashKey;
                    }
                    else if (hashKey.indexOf("-") == hashKey.lastIndexOf("-")) {
                        existingKey = this.getPartialMatchKey(innerJoinTables.keySet().iterator(), hashKey);
                    }
                    if (existingKey != null) {
                        final Object oldObj = innerJoinTables.get(existingKey);
                        if (oldObj instanceof WhereExpression) {
                            ((WhereExpression)oldObj).addWhereItem(wi);
                            ((WhereExpression)oldObj).addOperator("AND");
                        }
                        else if (oldObj instanceof WhereItem) {
                            final WhereExpression newWhereExp = new WhereExpression();
                            final WhereItem oldItem = (WhereItem)oldObj;
                            newWhereExp.addWhereItem(oldItem);
                            newWhereExp.addOperator("AND");
                            newWhereExp.addWhereItem(wi);
                            innerJoinTables.put(hashKey, newWhereExp);
                        }
                    }
                    else {
                        final WhereExpression newWhereExp2 = new WhereExpression();
                        newWhereExp2.addWhereItem(wi);
                        innerJoinTables.put(hashKey, newWhereExp2);
                    }
                    ++ijc;
                }
                final ArrayList enOut = new ArrayList(innerJoinTables.keySet());
                final String keyOut = null;
                final Vector reorderedRemainingFromItems = new Vector();
                final Vector reorderedFromItems = new Vector(fromItemList.size());
                for (int ni = 0; ni < fromItemList.size(); ++ni) {
                    reorderedFromItems.add(null);
                }
                final int remaininingFromItemsSize = remainingFromItemList.size();
                final Vector fromItemListCopy = new Vector(fromItemList.size());
                fromItemListCopy.addAll(fromItemList);
                final Vector prevTables = new Vector();
                for (int rf = 0; rf < fromItemListCopy.size(); ++rf) {
                    final FromTable ft2 = fromItemListCopy.get(rf);
                    if (ft2 != null) {
                        String alias = ft2.getAliasName();
                        if (alias == null) {
                            alias = ft2.getTableName().toString().trim();
                        }
                        alias = "&" + alias + "&";
                        final String key = this.getPartialMatchKey(innerJoinTables.keySet().iterator(), alias);
                        if (key != null) {
                            final String leftExpAlias = key.substring(1, key.indexOf("-") - 1);
                            final String rightExpAlias = key.substring(key.indexOf("-") + 2, key.length() - 1);
                            final String joinKey = this.getPartialMatchKey(joinedTables.keySet().iterator(), "&" + leftExpAlias + "&");
                            if (key.startsWith(alias)) {
                                int insertIndex = rf;
                                FromTable ft3 = null;
                                for (int i = rf; i < fromItemListCopy.size(); ++i) {
                                    ft3 = fromItemListCopy.get(i);
                                    if (ft3 != null) {
                                        String ft1Alias = ft3.getAliasName();
                                        if (ft1Alias == null) {
                                            ft1Alias = ft3.getTableName().toString();
                                        }
                                        if (ft1Alias.equalsIgnoreCase(rightExpAlias)) {
                                            insertIndex = i;
                                            break;
                                        }
                                    }
                                }
                                if (insertIndex != rf) {
                                    if (ft2.getJoinClause() != null) {
                                        if (reorderedFromItems.indexOf(null) < insertIndex) {
                                            reorderedFromItems.add(reorderedFromItems.indexOf(null), ft2);
                                            reorderedFromItems.add(reorderedFromItems.indexOf(null), ft3);
                                        }
                                        else {
                                            reorderedFromItems.add(insertIndex, ft3);
                                            reorderedFromItems.add(insertIndex, ft2);
                                        }
                                    }
                                    else if (ft3.getJoinClause() != null) {
                                        reorderedFromItems.add(reorderedFromItems.indexOf(null), ft2);
                                        reorderedFromItems.add(reorderedFromItems.indexOf(null), ft3);
                                    }
                                    else {
                                        reorderedFromItems.add(reorderedFromItems.indexOf(null), ft3);
                                        reorderedFromItems.add(reorderedFromItems.indexOf(null), ft2);
                                    }
                                    fromItemListCopy.setElementAt(null, rf);
                                    fromItemListCopy.setElementAt(null, insertIndex);
                                }
                                else {
                                    reorderedFromItems.add(reorderedFromItems.indexOf(null), ft2);
                                    fromItemListCopy.setElementAt(null, rf);
                                }
                            }
                            else if (key.endsWith(alias)) {
                                int insertIndex = rf;
                                final int loopSize = fromItemListCopy.size();
                                FromTable ft4 = null;
                                for (int l = 0; l < loopSize; ++l) {
                                    ft4 = fromItemListCopy.get(l);
                                    if (ft4 != null) {
                                        String ft1Alias2 = ft4.getAliasName();
                                        if (ft1Alias2 == null) {
                                            ft1Alias2 = ft4.getTableName().toString();
                                        }
                                        if (ft1Alias2.equalsIgnoreCase(leftExpAlias)) {
                                            insertIndex = l;
                                            break;
                                        }
                                    }
                                }
                                if (insertIndex != rf) {
                                    if (ft2.getJoinClause() != null) {
                                        reorderedFromItems.add(rf, ft4);
                                        reorderedFromItems.add(rf, ft2);
                                    }
                                    else {
                                        reorderedFromItems.add(rf, ft2);
                                        reorderedFromItems.insertElementAt(ft4, rf);
                                    }
                                    fromItemListCopy.setElementAt(null, rf);
                                    fromItemListCopy.setElementAt(null, insertIndex);
                                }
                                else {
                                    reorderedFromItems.add(reorderedFromItems.indexOf(null), ft2);
                                    fromItemListCopy.setElementAt(null, rf);
                                }
                            }
                            else {
                                reorderedFromItems.add(reorderedFromItems.indexOf(null), ft2);
                                fromItemListCopy.setElementAt(null, rf);
                            }
                        }
                        else {
                            if (reorderedFromItems.elementAt(rf) == null) {
                                if (ft2.getJoinClause() != null) {
                                    reorderedFromItems.add(reorderedFromItems.indexOf(null), ft2);
                                }
                                else if (rf == 0) {
                                    reorderedFromItems.insertElementAt(ft2, rf);
                                }
                                else {
                                    reorderedFromItems.add(ft2);
                                }
                            }
                            else if (ft2.getJoinClause() != null) {
                                reorderedFromItems.add(reorderedFromItems.indexOf(null), ft2);
                            }
                            else {
                                reorderedFromItems.insertElementAt(ft2, rf);
                            }
                            fromItemListCopy.setElementAt(null, rf);
                        }
                    }
                }
                for (int rei = 0; rei < fromItemList.size(); ++rei) {
                    final FromTable ft2 = reorderedFromItems.get(rei);
                    if (ft2 != null) {
                        String ftName = ft2.getAliasName();
                        if (ftName == null) {
                            ftName = ft2.getTableName().toString();
                        }
                        for (int rrei = 0; rrei < remainingFromItemList.size(); ++rrei) {
                            final FromTable ft5 = remainingFromItemList.get(rrei);
                            String ft1Name = ft5.getAliasName();
                            if (ft1Name == null) {
                                ft1Name = ft5.getTableName().toString();
                            }
                            if (ftName.equalsIgnoreCase(ft1Name)) {
                                reorderedRemainingFromItems.add(ft2);
                            }
                        }
                    }
                }
                remainingFromItemList.removeAll(reorderedRemainingFromItems);
                reorderedRemainingFromItems.addAll(remainingFromItemList);
                final Vector tablesWithoutJoins = new Vector();
                final FromClause fromClauseWithInnerJoin = new FromClause();
                fromClauseWithInnerJoin.setFromClause("FROM");
                final Vector fromClauseWithInnerJoinItems = new Vector();
                if (innerJoinTables.isEmpty()) {
                    fromClauseWithInnerJoinItems.addAll(joinedTables.values());
                    fromClauseWithInnerJoinItems.addAll(reorderedRemainingFromItems);
                }
                else {
                    int swapPosition = 1;
                    for (int rfy = 0; rfy < reorderedFromItems.size(); ++rfy) {
                        final FromTable ft6 = reorderedFromItems.get(rfy);
                        if (ft6 != null) {
                            String alias2 = ft6.getAliasName();
                            if (alias2 == null) {
                                alias2 = ft6.getTableName().toString().trim();
                            }
                            alias2 = "&" + alias2 + "&";
                            boolean keyAdded = false;
                            String key2 = null;
                            if (fromClauseWithInnerJoinItems.size() == 0) {
                                fromClauseWithInnerJoinItems.add(ft6);
                                prevTables.add(alias2);
                            }
                            else {
                                if (ft6.getJoinClause() != null) {
                                    continue;
                                }
                                if (!innerJoinTables.isEmpty()) {
                                    final Iterator len = innerJoinTables.keySet().iterator();
                                    while (len.hasNext()) {
                                        key2 = len.next().toString();
                                        final int ind = key2.toLowerCase().indexOf(alias2.toLowerCase());
                                        if (ind != -1) {
                                            final FromTable newFt = new FromTable();
                                            newFt.setJoinClause("INNER JOIN");
                                            newFt.setOnOrUsingJoin("ON");
                                            final Vector we = new Vector();
                                            key2 = this.getValidKeyforInnerJoinCondition(innerJoinTables, prevTables, alias2);
                                            if (!key2.equalsIgnoreCase("")) {
                                                swapPosition = 1;
                                                we.add(innerJoinTables.get(key2));
                                                innerJoinTables.remove(key2);
                                                final FromClause newFtFC = new FromClause();
                                                final Vector newFtFCItems = new Vector();
                                                newFtFCItems.add(ft6);
                                                newFtFC.setFromItemList(newFtFCItems);
                                                newFt.setTableName(newFtFC);
                                                newFt.setJoinExpression(we);
                                                fromClauseWithInnerJoinItems.add(newFt);
                                                keyAdded = true;
                                                prevTables.add(alias2);
                                                break;
                                            }
                                            if (fromItemListCopy.size() > rfy + swapPosition) {
                                                reorderedFromItems.add(rfy + swapPosition, reorderedFromItems.remove(rfy));
                                                reorderedFromItems.add(rfy, reorderedFromItems.remove(rfy + swapPosition - 1));
                                                --rfy;
                                                ++swapPosition;
                                                keyAdded = true;
                                                break;
                                            }
                                            break;
                                        }
                                    }
                                    if (!keyAdded) {
                                        swapPosition = 1;
                                        fromClauseWithInnerJoinItems.add(ft6);
                                        prevTables.add(alias2);
                                    }
                                }
                                else {
                                    swapPosition = 1;
                                    fromClauseWithInnerJoinItems.add(ft6);
                                }
                            }
                            for (FromTable joinFromTable = this.getJoinTableForTheFromItem(ft6, joinedTables, reorderedFromItems); joinFromTable != null; joinFromTable = this.getJoinTableForTheFromItem(ft6, joinedTables, reorderedFromItems)) {
                                fromClauseWithInnerJoinItems.add(joinFromTable);
                                String jAlias = joinFromTable.getAliasName();
                                if (jAlias == null) {
                                    jAlias = joinFromTable.getTableName().toString();
                                }
                                jAlias = "&" + jAlias + "&";
                                prevTables.add(jAlias);
                            }
                        }
                    }
                }
                if (!innerJoinTables.isEmpty() && !newInnerJoinedTables.isEmpty()) {
                    boolean fromClauseAdded = false;
                    final Iterator en = innerJoinTables.keySet().iterator();
                    String key3 = null;
                    while (en.hasNext()) {
                        key3 = en.next().toString();
                        final String joinedTableName1 = key3.substring(0, key3.indexOf("-")).toLowerCase();
                        final String joinedTableName2 = key3.substring(key3.indexOf("-") + 1).toLowerCase();
                        final Iterator en2 = newInnerJoinedTables.keySet().iterator();
                        String en2Key = null;
                        while (en2.hasNext()) {
                            en2Key = en2.next().toString();
                            if (en2Key.toLowerCase().indexOf(joinedTableName1) != -1 || en2Key.toLowerCase().indexOf(joinedTableName2) != -1) {
                                final FromClause newInnerJoinTable = newInnerJoinedTables.get(en2Key);
                                newInnerJoinTable.getFirstElement().getJoinExpression().lastElement().getOperator().add("AND");
                                newInnerJoinTable.getFirstElement().getJoinExpression().lastElement().getWhereItems().add(innerJoinTables.get(key3));
                                fromClauseAdded = true;
                                break;
                            }
                        }
                        if (fromClauseAdded) {
                            newInnerJoinedTables.remove(en2Key);
                        }
                    }
                }
                else if (!innerJoinTables.isEmpty()) {
                    final Iterator inIt = innerJoinTables.values().iterator();
                    while (inIt.hasNext()) {
                        if (!whereExpression.getWhereItems().isEmpty()) {
                            whereExpression.getOperator().add("AND");
                        }
                        whereExpression.getWhereItems().add(inIt.next());
                    }
                }
                fromClauseWithInnerJoinItems.addAll(tablesWithoutJoins);
                fromClauseWithInnerJoin.setFromItemList(fromClauseWithInnerJoinItems);
                return fromClauseWithInnerJoin;
            }
        }
        catch (final ArrayIndexOutOfBoundsException ae) {
            ae.printStackTrace();
            throw new ConvertException("Conversion failed.");
        }
        return null;
    }
    
    private FromTable getJoinTableForTheFromItem(final FromTable ft, final LinkedHashMap joinMap, final Vector fromItems) {
        FromTable joinFt = null;
        String alias = ft.getAliasName();
        if (ft.getAliasName() == null) {
            alias = ft.getTableName().toString();
        }
        alias = "&" + alias + "&";
        final String jKey = this.getPartialMatchKey(joinMap.keySet().iterator(), alias);
        if (jKey != null) {
            for (int i = 0, size = fromItems.size(); i < size; ++i) {
                if (fromItems.get(i) instanceof FromTable) {
                    joinFt = fromItems.get(i);
                    String joinAlias = joinFt.getAliasName();
                    if (joinAlias == null) {
                        joinAlias = joinFt.getTableName().toString();
                    }
                    joinAlias = "&" + joinAlias + "&";
                    if (jKey.indexOf(joinAlias) != -1 && !alias.equalsIgnoreCase(joinAlias)) {
                        final String[] jKeyTables = jKey.split("-");
                        if (jKeyTables.length > 2) {
                            String newJoinKey = "";
                            for (int s = 0, len = jKeyTables.length; s < len; ++s) {
                                if (!jKeyTables[s].equalsIgnoreCase(joinAlias)) {
                                    if (s != 0) {
                                        newJoinKey += "-";
                                    }
                                    newJoinKey += jKeyTables[s];
                                }
                            }
                            joinMap.put(newJoinKey, joinMap.get(jKey));
                        }
                        joinMap.remove(jKey);
                        break;
                    }
                }
                joinFt = null;
            }
        }
        return joinFt;
    }
    
    private String getValidKeyforInnerJoinCondition(final LinkedHashMap joinMap, final Vector prevTables, final String currentTableAlias) {
        String key = "";
        String key2;
        for (Iterator it = joinMap.keySet().iterator(); it.hasNext() && key.equalsIgnoreCase(""); key = key2) {
            key2 = it.next().toString().toLowerCase();
            String[] keyTables = null;
            keyTables = key2.split("-");
            for (int i = prevTables.size(); i > 0; --i) {
                final String prevTableAlias = prevTables.get(i - 1).toString();
                if (key2.indexOf("-") == key2.lastIndexOf("-")) {
                    if (key2.indexOf(prevTableAlias) != -1 && key2.indexOf(currentTableAlias) != -1) {
                        break;
                    }
                }
                else {
                    boolean canJoin = true;
                    for (int kt = 0, size = keyTables.length; kt < size; ++kt) {
                        if (!keyTables[kt].equalsIgnoreCase(currentTableAlias) && !prevTables.contains(keyTables[kt])) {
                            canJoin = false;
                            break;
                        }
                    }
                    if (canJoin) {
                        break;
                    }
                }
            }
        }
        return key;
    }
    
    private void setNewFromClauseWithANSIJoin(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final int database, final WhereExpression whereExpression) throws ConvertException {
        boolean isInformixOuterJoin = false;
        if (from_sqs == null) {
            return;
        }
        final FromClause fromClause = to_sqs.getFromClause();
        this.removedFromItemsList = new Vector();
        if (fromClause != null) {
            FromClause newFromClause = new FromClause();
            final Vector newFromItemList = new Vector();
            newFromClause.setFromClause("FROM");
            newFromClause.setFromItemList(newFromItemList);
            newFromClause.setObjectContext(this.context);
            final Vector fromItemList = fromClause.getFromItemList();
            final Vector CheckOuterFromList = fromClause.changeOrderForOuter(fromItemList);
            for (int i = 0; i < fromItemList.size(); ++i) {
                if (fromItemList.get(i) instanceof FromTable) {
                    final FromTable newFromTable = fromItemList.get(i);
                    if (newFromTable.getOuter() != null) {
                        isInformixOuterJoin = true;
                    }
                }
            }
            Vector whereItemList = new Vector();
            final Vector operatorList = new Vector();
            this.loadWhereItemsOperators(whereItemList, operatorList);
            int size = fromItemList.size();
            if (size < whereItemList.size()) {
                size = whereItemList.size();
            }
            final Vector[] joinExpression = new Vector[size];
            boolean fromAndWhereChanged = false;
            if (!isInformixOuterJoin) {
                if (database == 1) {
                    whereItemList = this.arrangeTheWhereItemListAccordingToTheOrderInFromItemList(whereItemList, fromItemList);
                }
                this.groupWhereItems(whereItemList, joinExpression, to_sqs, from_sqs, database);
                this.orderWhereItems(whereItemList, joinExpression);
                int index = 0;
                boolean isRemainingFromListItemsAdded = false;
                final WhereExpression we = new WhereExpression();
                we.setObjectContext(this.context);
                for (int j = 0; j < whereItemList.size(); ++j) {
                    final WhereItem wit = whereItemList.get(j);
                    fromAndWhereChanged = true;
                    final String tableAliasL = this.getTableAliasWhereL(wit);
                    if (j == 0) {
                        if (tableAliasL != null) {
                            newFromItemList.add(index, this.getFromItemAfterConversion(fromItemList, tableAliasL, database));
                        }
                        else {
                            FromTable ft1 = null;
                            ft1 = this.getFromTableForNoAlias(wit, false, from_sqs, fromItemList, database);
                            newFromItemList.add(index, ft1);
                        }
                        final FromTable removedFromTbl = newFromItemList.get(index);
                        if (removedFromTbl != null && removedFromTbl.getAliasName() == null) {
                            this.removedFromItemsList.add(removedFromTbl);
                        }
                        ++index;
                        final String tableAliasR = this.getTableAliasWhereR(wit);
                        FromTable ft2 = null;
                        if (tableAliasR != null) {
                            ft2 = this.getFromItemAfterConversion(fromItemList, tableAliasR, database);
                        }
                        else {
                            ft2 = this.getFromTableForNoAlias(wit, true, from_sqs, fromItemList, database);
                        }
                        if (ft2 != null && ft2.getAliasName() == null) {
                            this.removedFromItemsList.add(ft2);
                        }
                        final String str = this.getJoinType(wit, ft2);
                        if (str == null) {
                            if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                                we.addOperator("AND");
                            }
                            if (joinExpression != null && joinExpression[j] != null && !joinExpression[j].isEmpty() && joinExpression[j].get(0) instanceof WhereExpression) {
                                we.addWhereExpression(joinExpression[j].get(0));
                            }
                            else {
                                we.addWhereItem(wit);
                                wit.setLeftJoin(null);
                                wit.setRightJoin(null);
                            }
                        }
                        else {
                            ft2.setJoinClause(str);
                            ft2.setOnOrUsingJoin(" ON ");
                            if (this.selectStmtInJoin) {
                                final WhereExpression we2 = joinExpression[j].get(0);
                                final WhereItem wi1 = we2.getWhereItems().get(0);
                                wi1.setRightWhereExp(wit.getRightWhereExp());
                                joinExpression[j].remove(0);
                                joinExpression[j].add(0, we2);
                                this.selectStmtInJoin = false;
                            }
                            ft2.setJoinExpression(joinExpression[j]);
                            newFromItemList.add(index, ft2);
                            ++index;
                        }
                    }
                    else {
                        FromTable ft3 = this.getFromItemAfterConversion(fromItemList, tableAliasL, database);
                        if (ft3 == null) {
                            final String tableAliasR = this.getTableAliasWhereR(wit);
                            if (tableAliasR != null) {
                                ft3 = this.getFromItemAfterConversion(fromItemList, tableAliasR, database);
                            }
                            else {
                                ft3 = this.getFromTableForNoAlias(wit, true, from_sqs, fromItemList, database);
                            }
                        }
                        if (ft3 != null && ft3.getAliasName() == null) {
                            this.removedFromItemsList.add(ft3);
                        }
                        String str2 = this.getJoinType(ft3, whereItemList, j);
                        if (str2 == null) {
                            if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                                we.addOperator("AND");
                            }
                            wit.setLeftJoin(null);
                            wit.setRightJoin(null);
                            if (joinExpression != null && joinExpression[j] != null && !joinExpression[j].isEmpty() && joinExpression[j].get(0) instanceof WhereExpression) {
                                we.addWhereExpression(joinExpression[j].get(0));
                            }
                            else {
                                we.addWhereItem(wit);
                            }
                        }
                        else {
                            ft3.setJoinClause(str2);
                            ft3.setOnOrUsingJoin(" ON ");
                            ft3.setJoinExpression(joinExpression[j]);
                            final String tableAliasRight = this.getTableAliasWhereR(wit);
                            final String tableAliasLeft = this.getTableAliasWhereL(wit);
                            if (ft3.getAliasName() == null || !ft3.getAliasName().equalsIgnoreCase(tableAliasRight)) {
                                if (ft3.getAliasName() != null || !ft3.getTableName().toString().equalsIgnoreCase(tableAliasRight)) {
                                    if ((ft3.getAliasName() != null && ft3.getAliasName().equalsIgnoreCase(tableAliasLeft)) || (ft3.getAliasName() == null && ft3.getTableName().toString().equalsIgnoreCase(tableAliasLeft))) {
                                        if (fromItemList.size() > 1) {
                                            final int fromItemListSize = fromItemList.size();
                                            final String tableAliasR2 = this.getTableAliasWhereR(wit);
                                            FromTable remainingFromItems = this.getFromItemAfterConversion(fromItemList, tableAliasR2, database);
                                            if (remainingFromItems != null && remainingFromItems.getAliasName() == null) {
                                                this.removedFromItemsList.add(remainingFromItems);
                                            }
                                            str2 = this.getJoinType(wit, remainingFromItems);
                                            if (remainingFromItems != null) {
                                                remainingFromItems.setJoinClause(str2);
                                                remainingFromItems.setOnOrUsingJoin(" ON ");
                                                remainingFromItems.setJoinExpression(ft3.getJoinExpression());
                                                ft3.setJoinClause(null);
                                                ft3.setJoinExpression(null);
                                                ft3.setOnOrUsingJoin(null);
                                                final FromTable tempFT = ft3;
                                                ft3 = remainingFromItems;
                                                remainingFromItems = tempFT;
                                            }
                                            if (remainingFromItems != null) {
                                                if (j > fromItemList.size()) {
                                                    newFromItemList.add(index, remainingFromItems);
                                                    ++index;
                                                }
                                                else {
                                                    newFromItemList.add(index, remainingFromItems);
                                                    ++index;
                                                }
                                            }
                                            isRemainingFromListItemsAdded = true;
                                        }
                                        else if (fromItemList.size() == 1) {
                                            final String tableAliasR3 = this.getTableAliasWhereR(wit);
                                            FromTable remainingFromItems2 = this.getFromItemAfterConversion(fromItemList, tableAliasR3, database);
                                            if (remainingFromItems2 != null && remainingFromItems2.getAliasName() == null) {
                                                this.removedFromItemsList.add(remainingFromItems2);
                                            }
                                            str2 = this.getJoinType(wit, remainingFromItems2);
                                            if (remainingFromItems2 != null) {
                                                remainingFromItems2.setJoinClause(str2);
                                                remainingFromItems2.setOnOrUsingJoin(" ON ");
                                                remainingFromItems2.setJoinExpression(ft3.getJoinExpression());
                                                ft3.setJoinClause(null);
                                                ft3.setJoinExpression(null);
                                                ft3.setOnOrUsingJoin(null);
                                                final FromTable tempFT2 = ft3;
                                                ft3 = remainingFromItems2;
                                                remainingFromItems2 = tempFT2;
                                            }
                                            if (remainingFromItems2 != null) {
                                                newFromItemList.add(index, remainingFromItems2);
                                                ++index;
                                                isRemainingFromListItemsAdded = true;
                                            }
                                        }
                                    }
                                    else {
                                        System.out.println("WhereExpression: It should never come here");
                                        if (fromItemList.size() > 1) {
                                            final int fromItemListSize = fromItemList.size();
                                            if (j > fromItemList.size()) {
                                                FromTable remainingFromItems2 = null;
                                                if (fromItemList.get(0) instanceof FromTable) {
                                                    remainingFromItems2 = fromItemList.get(0);
                                                }
                                                if (remainingFromItems2 != null) {
                                                    remainingFromItems2.setJoinClause(str2);
                                                    remainingFromItems2.setOnOrUsingJoin(" ON ");
                                                    remainingFromItems2.setJoinExpression(ft3.getJoinExpression());
                                                    ft3.setJoinClause(null);
                                                    ft3.setJoinExpression(null);
                                                    ft3.setOnOrUsingJoin(null);
                                                    final FromTable tempFT2 = ft3;
                                                    ft3 = remainingFromItems2;
                                                    remainingFromItems2 = tempFT2;
                                                }
                                                newFromItemList.add(index, remainingFromItems2);
                                                ++index;
                                            }
                                            else {
                                                FromTable remainingFromItems2 = null;
                                                if (fromItemList.get(j - 1) instanceof FromTable) {
                                                    remainingFromItems2 = fromItemList.get(j - 1);
                                                }
                                                if (remainingFromItems2 != null) {
                                                    remainingFromItems2.setJoinClause(str2);
                                                    remainingFromItems2.setOnOrUsingJoin(" ON ");
                                                    remainingFromItems2.setJoinExpression(ft3.getJoinExpression());
                                                    ft3.setJoinClause(null);
                                                    ft3.setJoinExpression(null);
                                                    ft3.setOnOrUsingJoin(null);
                                                    final FromTable tempFT2 = ft3;
                                                    ft3 = remainingFromItems2;
                                                    remainingFromItems2 = tempFT2;
                                                }
                                                newFromItemList.add(index, remainingFromItems2);
                                                ++index;
                                            }
                                            isRemainingFromListItemsAdded = true;
                                        }
                                        else if (fromItemList.size() == 1 && ft3.getAliasName() != null) {
                                            FromTable remainingFromItems3 = null;
                                            if (fromItemList.get(0) instanceof FromTable) {
                                                remainingFromItems3 = fromItemList.get(0);
                                            }
                                            if (remainingFromItems3 != null) {
                                                remainingFromItems3.setJoinClause(str2);
                                                remainingFromItems3.setOnOrUsingJoin(" ON ");
                                                remainingFromItems3.setJoinExpression(ft3.getJoinExpression());
                                                ft3.setJoinClause(null);
                                                ft3.setJoinExpression(null);
                                                ft3.setOnOrUsingJoin(null);
                                                final FromTable tempFT3 = ft3;
                                                ft3 = remainingFromItems3;
                                                remainingFromItems3 = tempFT3;
                                            }
                                            newFromItemList.add(index, remainingFromItems3);
                                            ++index;
                                            isRemainingFromListItemsAdded = true;
                                        }
                                    }
                                }
                            }
                            newFromItemList.add(index, ft3);
                            ++index;
                        }
                    }
                }
                if (we.getWhereItems() != null && !we.getWhereItems().isEmpty()) {
                    for (int wei = 0; wei < we.getWhereItems().size(); ++wei) {
                        WhereItem weWhereItem = null;
                        String weWhereItemLeftAlias = null;
                        final String weWhereItemRightAlias = null;
                        if (we.getWhereItems().get(wei) instanceof WhereItem) {
                            weWhereItem = we.getWhereItems().get(wei);
                        }
                        else if (we.getWhereItems().get(wei) instanceof WhereExpression && we.getWhereItems().get(wei).getWhereItems().lastElement() instanceof WhereItem) {
                            weWhereItem = we.getWhereItems().get(wei).getWhereItems().lastElement();
                        }
                        if (weWhereItem != null) {
                            weWhereItemLeftAlias = weWhereItem.getLeftWhereExp().getTableAlias();
                        }
                        boolean added = false;
                        for (int k = newFromItemList.size() - 1; k >= 0; --k) {
                            if (newFromItemList.get(k) instanceof FromTable) {
                                final FromTable ft2 = newFromItemList.get(k);
                                if (ft2.getJoinExpression() != null && !ft2.getJoinExpression().isEmpty()) {
                                    final Vector joinExp = ft2.getJoinExpression();
                                    for (int l = joinExp.size() - 1; l >= 0; --l) {
                                        if (joinExp.get(l) instanceof WhereExpression) {
                                            final WhereExpression joinWhereExp = joinExp.get(l);
                                            if (joinWhereExp.getWhereItems().lastElement() instanceof WhereItem) {
                                                final WhereItem joinWhereItem = joinWhereExp.getWhereItems().lastElement();
                                                final String joinWhereItemLeftAlias = joinWhereItem.getLeftWhereExp().getTableAlias();
                                                final String joinWhereItemRightAlias = joinWhereItem.getRightWhereExp().getTableAlias();
                                                if (weWhereItemLeftAlias != null && ((joinWhereItemLeftAlias != null && weWhereItemLeftAlias.equalsIgnoreCase(joinWhereItemLeftAlias)) || (joinWhereItemRightAlias != null && weWhereItemLeftAlias.equalsIgnoreCase(joinWhereItemRightAlias)))) {
                                                    joinWhereExp.addWhereExpression(we);
                                                    joinWhereExp.addOperator("AND");
                                                    added = true;
                                                    break;
                                                }
                                                if (weWhereItemLeftAlias == null) {
                                                    joinWhereExp.addWhereExpression(we);
                                                    joinWhereExp.addOperator("AND");
                                                    added = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (added) {
                                        break;
                                    }
                                }
                            }
                        }
                        if (!added) {
                            if (whereExpression.getOperator().contains("AND") || whereExpression.getOperator().contains("and")) {
                                whereExpression.getOperator().add("AND");
                            }
                            else if (whereExpression.getWhereItems().size() > 0 && whereExpression.getWhereItems().lastElement() != null) {
                                whereExpression.getOperator().add("AND");
                            }
                            whereExpression.getWhereItems().add(we.getWhereItems().get(wei));
                        }
                    }
                }
                if (fromItemList.size() > 0) {
                    for (int m = 0; m < fromItemList.size(); ++m) {
                        FromTable remainingFromItems4 = null;
                        if (fromItemList.get(m) instanceof FromTable) {
                            remainingFromItems4 = fromItemList.get(m);
                        }
                        newFromItemList.add(remainingFromItems4);
                    }
                    if (database == 12 || database == 8) {
                        final FromClause tempFromClause = this.handleInnerJoin(to_sqs, from_sqs, database, whereExpression, fromItemList, newFromClause);
                        if (tempFromClause != null) {
                            newFromClause = tempFromClause;
                        }
                    }
                }
            }
            else {
                final Vector outerTableNames = fromClause.getOuterFromTableNames(fromItemList);
                final Hashtable whereItemsKeptInWhereClause = new Hashtable();
                final Vector newWhereClause = new Vector();
                boolean isOuter = false;
                this.moveOuterWhereItemsAsANSIJoins(whereItemsKeptInWhereClause, whereItemList, outerTableNames);
                if (whereItemList.size() > 0) {
                    final Vector Operators = whereExpression.getOperator();
                    final boolean removeOpFollowingFirstExpression = false;
                    final int getsize = Operators.size();
                    int count = 0;
                    for (int i2 = 0; i2 <= getsize; ++i2) {
                        final String key = "" + i2;
                        if (!whereItemsKeptInWhereClause.containsKey(key)) {
                            if (i2 - count != 0) {
                                if (Operators.size() > 0) {
                                    Operators.removeElementAt(i2 - 1 - count);
                                    ++count;
                                }
                            }
                            else if (Operators.size() > 0) {
                                Operators.removeElementAt(i2 - count);
                                ++count;
                            }
                        }
                    }
                    whereExpression.setOperator(Operators);
                }
                for (int i3 = 0; i3 < whereItemsKeptInWhereClause.size() + whereItemList.size(); ++i3) {
                    final String key2 = "" + i3;
                    if (whereItemsKeptInWhereClause.get(key2) != null) {
                        Object obj = whereItemsKeptInWhereClause.get(key2);
                        if (obj instanceof WhereItem) {
                            if (database == 1) {
                                obj = ((WhereItem)obj).toOracleSelect(to_sqs, from_sqs);
                            }
                            else if (database == 2) {
                                obj = ((WhereItem)obj).toMSSQLServerSelect(to_sqs, from_sqs);
                            }
                            else if (database == 7) {
                                obj = ((WhereItem)obj).toSybaseSelect(to_sqs, from_sqs);
                            }
                            else if (database == 3) {
                                obj = ((WhereItem)obj).toDB2Select(to_sqs, from_sqs);
                            }
                            else if (database == 5) {
                                obj = ((WhereItem)obj).toMySQLSelect(to_sqs, from_sqs);
                            }
                            else if (database == 8) {
                                obj = ((WhereItem)obj).toANSISelect(to_sqs, from_sqs);
                            }
                            else if (database == 4) {
                                obj = ((WhereItem)obj).toPostgreSQLSelect(to_sqs, from_sqs);
                            }
                            else if (database == 6) {
                                obj = ((WhereItem)obj).toInformixSelect(to_sqs, from_sqs);
                            }
                            else if (database == 10) {
                                obj = ((WhereItem)obj).toTimesTenSelect(to_sqs, from_sqs);
                            }
                            else if (database == 11) {
                                obj = ((WhereItem)obj).toNetezzaSelect(to_sqs, from_sqs);
                            }
                            else if (database == 13) {
                                obj = ((WhereItem)obj).toVectorWiseSelect(to_sqs, from_sqs);
                            }
                        }
                        newWhereClause.add(obj);
                    }
                }
                whereExpression.setWhereItem(newWhereClause);
                final Vector tableOrAliasNameBeforeOuter = new Vector();
                final Vector WhereItemsInOuter = new Vector();
                boolean setAllTablesBeforeOuterasCrossJoin = false;
                for (int i4 = 0; i4 < CheckOuterFromList.size(); ++i4) {
                    String innerJoinOnCondition = new String();
                    final FromTable existingFromTable = CheckOuterFromList.get(i4);
                    String tableOrAliasName = new String();
                    if (existingFromTable.getAliasName() != null) {
                        tableOrAliasName = existingFromTable.getAliasName();
                        tableOrAliasNameBeforeOuter.add(tableOrAliasName);
                    }
                    else {
                        tableOrAliasName = existingFromTable.getTableName().toString();
                        tableOrAliasNameBeforeOuter.add(tableOrAliasName);
                    }
                    final Vector addJoinExpression = new Vector();
                    if (existingFromTable.getOuter() != null || isOuter) {
                        fromAndWhereChanged = true;
                        if (!setAllTablesBeforeOuterasCrossJoin && (database == 2 || database == 4)) {
                            for (int j2 = 1; j2 < i4; ++j2) {
                                final FromTable previousFromTablesBeforeOuterTable = CheckOuterFromList.get(j2);
                                previousFromTablesBeforeOuterTable.setJoinClause("CROSS JOIN");
                            }
                            setAllTablesBeforeOuterasCrossJoin = true;
                        }
                        else if (!setAllTablesBeforeOuterasCrossJoin) {
                            for (int j2 = 1; j2 < i4; ++j2) {
                                final FromTable previousFromTablesBeforeOuterTable = CheckOuterFromList.get(j2);
                                previousFromTablesBeforeOuterTable.setJoinClause("INNER JOIN");
                                previousFromTablesBeforeOuterTable.setOnOrUsingJoin("ON");
                                final Vector newWhereItems = new Vector();
                                final WhereItem newWhereItem = new WhereItem();
                                final WhereColumn newWhereColumn = new WhereColumn();
                                final Vector whereColumnItems = new Vector();
                                whereColumnItems.add("1");
                                newWhereColumn.setColumnExpression(whereColumnItems);
                                newWhereItem.setLeftWhereExp(newWhereColumn);
                                newWhereItem.setRightWhereExp(newWhereColumn);
                                newWhereItem.setOperator("=");
                                newWhereItems.add(newWhereItem);
                                previousFromTablesBeforeOuterTable.setJoinExpression(newWhereItems);
                            }
                            setAllTablesBeforeOuterasCrossJoin = true;
                        }
                        if (!isOuter) {
                            existingFromTable.setJoinClause("LEFT OUTER JOIN");
                        }
                        else if (database == 2 || database == 4) {
                            existingFromTable.setJoinClause("CROSS JOIN");
                        }
                        else {
                            existingFromTable.setJoinClause("INNER JOIN");
                            innerJoinOnCondition = "ON 1 = 1 ";
                        }
                        existingFromTable.setOuter(null);
                        if (existingFromTable.getOuterOpenBrace() != null) {
                            existingFromTable.setOuterOpenBrace(null);
                            isOuter = true;
                        }
                        if (whereItemList != null) {
                            for (int j2 = 0; j2 < whereItemList.size(); ++j2) {
                                final WhereItem whereItem = whereItemList.get(j2);
                                final WhereColumn leftWhereExp = whereItem.getLeftWhereExp();
                                final WhereColumn rightWhereExp = whereItem.getRightWhereExp();
                                String tableOrAliasNameForLeftExp = new String();
                                String tableOrAliasNameForRightExp = new String();
                                if (leftWhereExp != null && leftWhereExp.getColumnExpression().get(0) instanceof TableColumn) {
                                    TableColumn tableColumn = new TableColumn();
                                    tableColumn = leftWhereExp.getColumnExpression().get(0);
                                    if (tableColumn.getOwnerName() != null) {
                                        tableOrAliasNameForLeftExp = tableColumn.getOwnerName() + "." + tableColumn.getTableName();
                                    }
                                    else {
                                        tableOrAliasNameForLeftExp = tableColumn.getTableName();
                                    }
                                }
                                if (leftWhereExp != null && leftWhereExp.getColumnExpression().get(0) instanceof String) {
                                    final String tableAliaswhere = leftWhereExp.getColumnExpression().get(0);
                                    if (tableAliaswhere.indexOf(".") != -1) {
                                        tableOrAliasNameForLeftExp = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                                    }
                                }
                                if (rightWhereExp != null && rightWhereExp.getColumnExpression().get(0) instanceof TableColumn) {
                                    TableColumn tableColumn = new TableColumn();
                                    tableColumn = rightWhereExp.getColumnExpression().get(0);
                                    if (tableColumn.getOwnerName() != null) {
                                        tableOrAliasNameForRightExp = tableColumn.getOwnerName() + "." + tableColumn.getTableName();
                                    }
                                    else {
                                        tableOrAliasNameForRightExp = tableColumn.getTableName();
                                    }
                                }
                                if (rightWhereExp != null && rightWhereExp.getColumnExpression().get(0) instanceof String) {
                                    final String tableAliaswhere = rightWhereExp.getColumnExpression().get(0);
                                    if (tableAliaswhere.indexOf(".") != -1) {
                                        tableOrAliasNameForRightExp = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                                    }
                                }
                                if ((tableOrAliasName.equalsIgnoreCase(tableOrAliasNameForRightExp) && tableOrAliasNameBeforeOuter.contains(tableOrAliasNameForLeftExp)) || (tableOrAliasName.equalsIgnoreCase(tableOrAliasNameForLeftExp) && tableOrAliasNameBeforeOuter.contains(tableOrAliasNameForRightExp)) || (tableOrAliasName.equalsIgnoreCase(tableOrAliasNameForLeftExp) && rightWhereExp != null && rightWhereExp.getColumnExpression().get(0) instanceof String)) {
                                    if (!isOuter) {
                                        if (WhereItemsInOuter.size() == 0) {
                                            addJoinExpression.add(whereItem);
                                            existingFromTable.setOnOrUsingJoin("ON");
                                        }
                                        if (j2 > 0) {
                                            whereItem.setOperator1("AND");
                                        }
                                    }
                                    else {
                                        WhereItemsInOuter.add(whereItem);
                                        if (WhereItemsInOuter.size() > 1) {
                                            whereItem.setOperator1("AND");
                                        }
                                    }
                                }
                            }
                            existingFromTable.setJoinExpression(addJoinExpression);
                        }
                        if (existingFromTable.getOuterClosedBrace() != null) {
                            existingFromTable.setOuterClosedBrace(null);
                            existingFromTable.setOnOrUsingJoin(innerJoinOnCondition + "ON");
                            isOuter = false;
                            for (int k2 = 0; k2 < WhereItemsInOuter.size(); ++k2) {
                                addJoinExpression.add(WhereItemsInOuter.get(k2));
                            }
                        }
                    }
                    if (existingFromTable.getOuterClosedBrace() != null) {
                        existingFromTable.setOuterClosedBrace(null);
                        existingFromTable.setOnOrUsingJoin(innerJoinOnCondition + "ON");
                        for (int k2 = 0; k2 < WhereItemsInOuter.size(); ++k2) {
                            addJoinExpression.add(WhereItemsInOuter.get(k2));
                        }
                    }
                    newFromClause.setFromItemList(CheckOuterFromList);
                }
            }
            if (fromAndWhereChanged) {
                to_sqs.setFromClause(newFromClause);
            }
        }
    }
    
    private FromTable getFromItem(final Vector fromItemList, final String tableAliasWhere) {
        for (int i = 0; i < fromItemList.size(); ++i) {
            final FromTable ft = fromItemList.get(i);
            if (ft.getAliasName() != null && CustomizeUtil.compareQuotedIdentifiers(ft.getAliasName(), tableAliasWhere, false)) {
                fromItemList.remove(i);
                return ft;
            }
            if (CustomizeUtil.compareQuotedIdentifiers(ft.getTableName().toString(), tableAliasWhere, false)) {
                fromItemList.remove(i);
                return ft;
            }
        }
        return null;
    }
    
    private FromTable getFromItemAfterConversion(final Vector fromItemList, final String tableAliasWhere, final int database) throws ConvertException {
        if (this.removedFromItemsList != null) {
            for (int j = 0; j < this.removedFromItemsList.size(); ++j) {
                final FromTable removedFromTable = this.removedFromItemsList.get(j);
                if (removedFromTable.getAliasName() != null && CustomizeUtil.compareQuotedIdentifiers(removedFromTable.getAliasName(), tableAliasWhere, true)) {
                    return null;
                }
                if (tableAliasWhere != null && CustomizeUtil.compareQuotedIdentifiers(tableAliasWhere, removedFromTable.getTableName().toString(), true)) {
                    return null;
                }
            }
        }
        for (int i = 0; i < fromItemList.size(); ++i) {
            final FromTable ft = fromItemList.get(i);
            if (ft.getAliasName() != null && CustomizeUtil.compareQuotedIdentifiers(ft.getAliasName(), tableAliasWhere, true)) {
                fromItemList.remove(i);
                return ft.convert(null, null, database);
            }
            if (tableAliasWhere != null && CustomizeUtil.compareQuotedIdentifiers(tableAliasWhere, ft.getTableName().toString(), true)) {
                fromItemList.remove(i);
                return ft.convert(null, null, database);
            }
        }
        return null;
    }
    
    private String getTableAliasWhereL(final WhereItem wit) {
        final WhereColumn wcl = wit.getLeftWhereExp();
        if (wcl != null && wcl.getColumnExpression() != null && wcl.getColumnExpression().size() > 0) {
            int i = 0;
            while (i < wcl.getColumnExpression().size()) {
                if (wcl.getColumnExpression().get(i) instanceof TableColumn) {
                    final TableColumn tc = wcl.getColumnExpression().get(i);
                    if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier && SwisSQLOptions.fullyQualifiedWithDatabaseName && !tc.getOwnerName().equalsIgnoreCase("dbo") && this.targetDatabase == 7) {
                        return tc.getOwnerName() + "." + "dbo." + tc.getTableName();
                    }
                    if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                        return tc.getOwnerName() + "." + tc.getTableName();
                    }
                    return tc.getTableName();
                }
                else {
                    if (wcl.getColumnExpression().get(i) instanceof FunctionCalls) {
                        final String tableName = this.getTableAliasName(wcl.getColumnExpression().get(i));
                        if (tableName != null && !tableName.equals("")) {
                            return tableName;
                        }
                    }
                    else if (wcl.getColumnExpression().get(i) instanceof CaseStatement) {
                        final CaseStatement cs = wcl.getColumnExpression().get(i);
                        final String tableName2 = this.getTableAliasName(cs);
                        if (tableName2 != null && !tableName2.equals("")) {
                            return tableName2;
                        }
                    }
                    else if (wcl.getColumnExpression().get(i) instanceof SelectColumn) {
                        final String tableName = this.getTableAliasWhereR(wcl.getColumnExpression().get(i));
                        if (tableName != null && !tableName.equals("")) {
                            return tableName;
                        }
                    }
                    else if (wcl.getColumnExpression().get(i) instanceof String) {
                        final String tableAliaswhere = wcl.getColumnExpression().get(i);
                        if (tableAliaswhere.indexOf(".") != -1) {
                            if (!tableAliaswhere.startsWith("/*") || !tableAliaswhere.endsWith("*/")) {
                                return tableAliaswhere.substring(i, tableAliaswhere.indexOf("."));
                            }
                        }
                    }
                    ++i;
                }
            }
        }
        return null;
    }
    
    private String getTableAliasWhereR(final WhereItem wit) {
        final WhereColumn wcr = wit.getRightWhereExp();
        if (wcr != null && wcr.getColumnExpression() != null && wcr.getColumnExpression().size() > 0) {
            int i = 0;
            while (i < wcr.getColumnExpression().size()) {
                if (wcr.getColumnExpression().get(i) instanceof TableColumn) {
                    final TableColumn tc = wcr.getColumnExpression().get(i);
                    if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier && SwisSQLOptions.fullyQualifiedWithDatabaseName && !tc.getOwnerName().equalsIgnoreCase("dbo") && this.targetDatabase == 7) {
                        return tc.getOwnerName() + "." + "dbo." + tc.getTableName();
                    }
                    if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                        return tc.getOwnerName() + "." + tc.getTableName();
                    }
                    return tc.getTableName();
                }
                else {
                    if (wcr.getColumnExpression().get(i) instanceof FunctionCalls) {
                        final String tableName = this.getTableAliasName(wcr.getColumnExpression().get(i));
                        if (tableName != null && !tableName.equals("")) {
                            return tableName;
                        }
                    }
                    else if (wcr.getColumnExpression().get(i) instanceof CaseStatement) {
                        final CaseStatement cs = wcr.getColumnExpression().get(i);
                        final String tableName2 = this.getTableAliasName(cs);
                        if (tableName2 != null && !tableName2.equals("")) {
                            return tableName2;
                        }
                    }
                    else if (wcr.getColumnExpression().get(i) instanceof SelectColumn) {
                        final String tableName = this.getTableAliasWhereR(wcr.getColumnExpression().get(i));
                        if (tableName != null && !tableName.equals("")) {
                            return tableName;
                        }
                    }
                    else if (wcr.getColumnExpression().get(i) instanceof String) {
                        final String tableAliaswhere = wcr.getColumnExpression().get(i);
                        if (tableAliaswhere.startsWith("'")) {
                            return "";
                        }
                        if (tableAliaswhere.indexOf(".") != -1) {
                            if (!tableAliaswhere.startsWith("/*") || !tableAliaswhere.endsWith("*/")) {
                                if (tableAliaswhere.indexOf(".") == tableAliaswhere.lastIndexOf(".")) {
                                    return tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                                }
                                if (SwisSQLOptions.removeDBSchemaQualifier) {
                                    return tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1, tableAliaswhere.lastIndexOf("."));
                                }
                                return tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                            }
                        }
                    }
                    ++i;
                }
            }
        }
        return null;
    }
    
    private ArrayList getTableAliasesWhereL(final WhereItem wit) {
        final ArrayList list = new ArrayList();
        final WhereColumn wcl = wit.getLeftWhereExp();
        if (wcl != null && wcl.getColumnExpression() != null && wcl.getColumnExpression().size() > 0) {
            for (int i = 0; i < wcl.getColumnExpression().size(); ++i) {
                if (wcl.getColumnExpression().get(i) instanceof TableColumn) {
                    final TableColumn tc = wcl.getColumnExpression().get(i);
                    if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                        list.add(tc.getOwnerName() + "." + tc.getTableName());
                    }
                    else {
                        list.add(tc.getTableName());
                    }
                }
                else if (wcl.getColumnExpression().get(i) instanceof FunctionCalls) {
                    final FunctionCalls fcl = wcl.getColumnExpression().get(i);
                    final String tableName = this.getTableAliasName(fcl);
                    if (tableName != null && !tableName.equals("")) {
                        list.add(tableName);
                    }
                    if (fcl.getFunctionName() != null && fcl.getFunctionName().getColumnName().equalsIgnoreCase("COALESCE")) {
                        final String tableName2 = this.getTableAliasWhereR(fcl.getFunctionArguments().get(1));
                        if (tableName2 != null && !tableName2.equals("")) {
                            list.add(tableName2);
                        }
                    }
                }
                else if (wcl.getColumnExpression().get(i) instanceof CaseStatement) {
                    final CaseStatement cs = wcl.getColumnExpression().get(i);
                    final String tableName = this.getTableAliasName(cs);
                    if (tableName != null && !tableName.equals("")) {
                        list.add(tableName);
                    }
                }
                else if (wcl.getColumnExpression().get(i) instanceof SelectColumn) {
                    final String tableName3 = this.getTableAliasWhereR(wcl.getColumnExpression().get(i));
                    if (tableName3 != null && !tableName3.equals("")) {
                        list.add(tableName3);
                    }
                }
                else if (wcl.getColumnExpression().get(i) instanceof String) {
                    String tableAliaswhere = wcl.getColumnExpression().get(i);
                    if (tableAliaswhere.indexOf(".") != -1) {
                        if (!tableAliaswhere.startsWith("/*") || !tableAliaswhere.endsWith("*/")) {
                            if (tableAliaswhere.indexOf(".") != tableAliaswhere.lastIndexOf(".")) {
                                if (SwisSQLOptions.removeDBSchemaQualifier) {
                                    tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                                }
                                list.add(tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf(".")));
                            }
                            list.add(tableAliaswhere.substring(0, tableAliaswhere.indexOf(".")));
                        }
                    }
                }
            }
        }
        return list;
    }
    
    private ArrayList getTableAliasesWhereR(final WhereItem wit) {
        final ArrayList list = new ArrayList();
        final WhereColumn wcr = wit.getRightWhereExp();
        if (wcr != null && wcr.getColumnExpression() != null && wcr.getColumnExpression().size() > 0) {
            for (int i = 0; i < wcr.getColumnExpression().size(); ++i) {
                if (wcr.getColumnExpression().get(i) instanceof TableColumn) {
                    final TableColumn tc = wcr.getColumnExpression().get(i);
                    if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                        list.add(tc.getOwnerName() + "." + tc.getTableName());
                    }
                    else {
                        list.add(tc.getTableName());
                    }
                }
                else if (wcr.getColumnExpression().get(i) instanceof FunctionCalls) {
                    final String tableName = this.getTableAliasName(wcr.getColumnExpression().get(i));
                    if (tableName != null && !tableName.equals("")) {
                        list.add(tableName);
                    }
                }
                else if (wcr.getColumnExpression().get(i) instanceof CaseStatement) {
                    final CaseStatement cs = wcr.getColumnExpression().get(i);
                    final String tableName2 = this.getTableAliasName(cs);
                    if (tableName2 != null && !tableName2.equals("")) {
                        list.add(tableName2);
                    }
                }
                else if (wcr.getColumnExpression().get(i) instanceof SelectColumn) {
                    final String tableName = this.getTableAliasWhereR(wcr.getColumnExpression().get(i));
                    if (tableName != null && !tableName.equals("")) {
                        list.add(tableName);
                    }
                }
                else if (wcr.getColumnExpression().get(i) instanceof String) {
                    String tableAliaswhere = wcr.getColumnExpression().get(i);
                    if (tableAliaswhere.startsWith("'")) {
                        list.add("");
                    }
                    if (tableAliaswhere.indexOf(".") != -1) {
                        if (!tableAliaswhere.startsWith("/*") || !tableAliaswhere.endsWith("*/")) {
                            if (tableAliaswhere.indexOf(".") != tableAliaswhere.lastIndexOf(".")) {
                                if (SwisSQLOptions.removeDBSchemaQualifier) {
                                    tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                                }
                                list.add(tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf(".")));
                            }
                            list.add(tableAliaswhere.substring(0, tableAliaswhere.indexOf(".")));
                        }
                    }
                }
            }
        }
        return list;
    }
    
    private String getTableAliasWhereR(final SelectColumn sc) {
        if (sc != null && sc.getColumnExpression() != null && sc.getColumnExpression().size() > 0) {
            int i = 0;
            while (i < sc.getColumnExpression().size()) {
                if (sc.getColumnExpression().get(i) instanceof TableColumn) {
                    final TableColumn tc = sc.getColumnExpression().get(i);
                    if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                        return tc.getOwnerName() + "." + tc.getTableName();
                    }
                    return tc.getTableName();
                }
                else {
                    if (sc.getColumnExpression().get(i) instanceof FunctionCalls) {
                        final FunctionCalls fc = sc.getColumnExpression().get(i);
                        final String tableName = this.getTableAliasName(fc);
                        if (tableName != null && !tableName.equals("")) {
                            return tableName;
                        }
                    }
                    else if (sc.getColumnExpression().get(i) instanceof CaseStatement) {
                        final CaseStatement cs = sc.getColumnExpression().get(i);
                        final String tableName = this.getTableAliasName(cs);
                        if (tableName != null && !tableName.equals("")) {
                            return tableName;
                        }
                    }
                    else if (sc.getColumnExpression().get(i) instanceof SelectColumn) {
                        final String tableName2 = this.getTableAliasWhereR(sc.getColumnExpression().get(i));
                        if (tableName2 != null && !tableName2.equals("")) {
                            return tableName2;
                        }
                    }
                    else if (sc.getColumnExpression().get(i) instanceof String) {
                        final String tableAliaswhere = sc.getColumnExpression().get(i);
                        if (tableAliaswhere.startsWith("'")) {
                            return "";
                        }
                        if (tableAliaswhere.indexOf(".") != -1) {
                            if (!tableAliaswhere.startsWith("/*") || !tableAliaswhere.endsWith("*/")) {
                                if (tableAliaswhere.indexOf(".") == tableAliaswhere.lastIndexOf(".")) {
                                    return tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                                }
                                if (SwisSQLOptions.removeDBSchemaQualifier) {
                                    return tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1, tableAliaswhere.lastIndexOf("."));
                                }
                                return tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                            }
                        }
                    }
                    ++i;
                }
            }
        }
        return null;
    }
    
    private FromTable getFromTableForNoAlias(final WhereItem wit, final boolean right, final SelectQueryStatement from_sqs, final Vector fromItemList, final int database) throws ConvertException {
        WhereColumn whc = null;
        FromTable ft = null;
        if (right) {
            whc = wit.getRightWhereExp();
        }
        else {
            whc = wit.getLeftWhereExp();
        }
        if (whc != null) {
            final Vector colExpr = whc.getColumnExpression();
            if (colExpr != null && colExpr.size() > 0) {
                for (int k = 0; k < colExpr.size(); ++k) {
                    if (colExpr.get(k) instanceof TableColumn) {
                        ft = this.getFromTableForTableColumnOrString(colExpr.get(k), ft, from_sqs, fromItemList, database, whc);
                    }
                    else if (colExpr.get(k) instanceof FunctionCalls) {
                        final FunctionCalls fc = colExpr.get(k);
                        final Vector fnArgs = fc.getFunctionArguments();
                        if (fnArgs != null) {
                            for (int l = 0; l < fnArgs.size(); ++l) {
                                if (fnArgs.get(l) instanceof TableColumn) {
                                    ft = this.getFromTableForTableColumnOrString(fnArgs.get(l), ft, from_sqs, fromItemList, database, whc);
                                }
                                if (fnArgs.get(l) instanceof SelectColumn) {
                                    final SelectColumn sc = fnArgs.get(l);
                                    final Vector scColExpr = sc.getColumnExpression();
                                    if (scColExpr != null) {
                                        for (int m = 0; m < scColExpr.size(); ++m) {
                                            if (scColExpr.get(m) instanceof TableColumn) {
                                                ft = this.getFromTableForTableColumnOrString(scColExpr.get(m), ft, from_sqs, fromItemList, database, whc);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (colExpr.get(k) instanceof SelectQueryStatement) {
                            final SelectQueryStatement sqs = colExpr.get(k);
                            final Vector selectItemList = sqs.getSelectStatement().getSelectItemList();
                            final SelectColumn sc2 = selectItemList.get(0);
                            final FromTable newFromTable = new FromTable();
                            sqs.setOpenBrace("(");
                            sqs.setCloseBrace(")");
                            if (selectItemList.size() == 1) {
                                sc2.setAliasName("ALIAS_1");
                            }
                            newFromTable.setTableName(sqs);
                            newFromTable.setAliasName("TABLE_ALIAS1");
                            ft = newFromTable;
                            final WhereColumn wc = new WhereColumn();
                            final Vector newRightExpression = new Vector();
                            final TableColumn tc1 = new TableColumn();
                            tc1.setTableName(newFromTable.getAliasName());
                            tc1.setColumnName(sc2.getAliasName());
                            tc1.setDot(".");
                            newRightExpression.add(tc1);
                            wc.setColumnExpression(newRightExpression);
                            wit.setRightWhereExp(wc);
                            this.selectStmtInJoin = true;
                            return ft;
                        }
                        if (colExpr.get(k) instanceof CaseStatement) {
                            final CaseStatement cs = colExpr.get(k);
                            WhereItem cswi = null;
                            if (cs.getCaseCondition() != null) {
                                cswi = cs.getCaseCondition().getWhereItem().get(0);
                            }
                            else if (cs.getWhenClauseList().get(0).getWhenCondition() != null) {
                                final Object wiObj = cs.getWhenClauseList().get(0).getWhenCondition().getWhereItem().get(0);
                                if (wiObj instanceof WhereItem) {
                                    cswi = (WhereItem)wiObj;
                                }
                                else if (wiObj instanceof WhereExpression) {
                                    cswi = ((WhereExpression)wiObj).getWhereItems().get(0);
                                }
                            }
                            final Object cssc = cswi.getLeftWhereExp().getColumnExpression().get(0);
                            if (cssc instanceof SelectColumn) {
                                final Vector scColExpr2 = ((SelectColumn)cssc).getColumnExpression();
                                if (scColExpr2 != null) {
                                    for (int i = 0; i < scColExpr2.size(); ++i) {
                                        if (scColExpr2.get(i) instanceof TableColumn) {
                                            ft = this.getFromTableForTableColumnOrString(scColExpr2.get(i), ft, from_sqs, fromItemList, database, whc);
                                        }
                                    }
                                }
                            }
                            else if (cssc instanceof TableColumn) {
                                ft = this.getFromTableForTableColumnOrString(cssc, ft, from_sqs, fromItemList, database, whc);
                            }
                            else if (cssc instanceof String) {
                                ft = this.getFromTableForTableColumnOrString(cssc, ft, from_sqs, fromItemList, database, whc);
                            }
                        }
                        else if (colExpr.get(k) instanceof String) {
                            ft = this.getFromTableForTableColumnOrString(colExpr.get(k), ft, from_sqs, fromItemList, database, whc);
                        }
                    }
                }
            }
        }
        return ft;
    }
    
    private FromTable getFromTableForTableColumnOrString(final Object object, FromTable ft, final SelectQueryStatement from_sqs, final Vector fromItemList, final int database, final WhereColumn whc) throws ConvertException {
        if (object instanceof TableColumn) {
            final TableColumn tc1 = (TableColumn)object;
            ft = MetadataInfoUtil.getTableOfColumn(from_sqs, tc1);
        }
        else if (object instanceof String) {
            ft = MetadataInfoUtil.getTableOfColumn(from_sqs, object.toString());
        }
        if (ft != null && ft.getTableName() != null) {
            String tableAlias = ft.getTableName().toString();
            final String alias = ft.getAliasName();
            if (alias != null) {
                tableAlias = ft.getAliasName();
            }
            ft = this.getFromItemAfterConversion(fromItemList, tableAlias, database);
            for (int n = 0; n < whc.getColumnExpression().size(); ++n) {
                Object obj = whc.getColumnExpression().get(n);
                if (obj != null) {
                    if (obj instanceof TableColumn) {
                        final TableColumn tc2 = (TableColumn)obj;
                        tc2.setTableName(tableAlias);
                    }
                    else if (obj instanceof FunctionCalls) {
                        final FunctionCalls fc = (FunctionCalls)obj;
                        final Vector fnArgs = fc.getFunctionArguments();
                        if (fnArgs != null) {
                            for (int k = 0; k < fnArgs.size(); ++k) {
                                if (fnArgs.get(k) instanceof SelectColumn) {
                                    final SelectColumn sc = fnArgs.get(k);
                                    final Vector colExpression = sc.getColumnExpression();
                                    if (colExpression != null) {
                                        for (int m = 0; m < colExpression.size(); ++m) {
                                            if (colExpression.get(m) instanceof TableColumn) {
                                                final TableColumn tc3 = colExpression.get(m);
                                                tc3.setTableName(tableAlias);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (obj instanceof String) {
                        final Object oldObj = obj;
                        obj = tableAlias + "." + obj.toString();
                        final Vector colWCExpr = whc.getColumnExpression();
                        if (colWCExpr != null) {
                            for (int s = 0; s < colWCExpr.size(); ++s) {
                                if (colWCExpr.get(s) instanceof String && colWCExpr.get(s).toString().equalsIgnoreCase(oldObj.toString())) {
                                    colWCExpr.setElementAt(obj, s);
                                }
                            }
                        }
                    }
                }
            }
            return ft;
        }
        return ft;
    }
    
    private String getJoinType(final WhereItem wit) {
        if (wit.getLeftJoin() != null) {
            return new String(" LEFT OUTER JOIN ");
        }
        if (wit.getRightJoin() != null) {
            return new String(" RIGHT OUTER JOIN ");
        }
        return new String(" INNER JOIN ");
    }
    
    private String getJoinType(final FromTable ft, final Vector whereItemList, final int position_of_curr_wit) {
        boolean flg = false;
        final WhereItem curr_wit = whereItemList.get(position_of_curr_wit);
        final WhereColumn curr_rwc = curr_wit.getRightWhereExp();
        for (int k = 0; k < position_of_curr_wit; ++k) {
            final WhereItem wit_in_check = whereItemList.get(k);
            if (wit_in_check != null && curr_wit != null) {
                final WhereColumn prev_rwc = wit_in_check.getRightWhereExp();
                if (curr_rwc != null && prev_rwc != null) {
                    final String curr_alias = curr_rwc.getTableAlias();
                    final String prev_alias = prev_rwc.getTableAlias();
                    if (curr_alias != null && prev_alias != null && curr_rwc.getTableAlias().equals(prev_rwc.getTableAlias())) {
                        flg = true;
                        break;
                    }
                }
            }
        }
        if (flg) {
            final WhereColumn curr_lwc = curr_wit.getLeftWhereExp();
            curr_wit.setRightWhereExp(curr_lwc);
            curr_wit.setLeftWhereExp(curr_rwc);
            final String ljoin = curr_wit.getLeftJoin();
            final String rjoin = curr_wit.getRightJoin();
            if (ljoin != null) {
                curr_wit.setRightJoin(ljoin);
                curr_wit.setLeftJoin(null);
            }
            if (rjoin != null) {
                curr_wit.setLeftJoin(rjoin);
                curr_wit.setRightJoin(null);
            }
        }
        return this.getJoinType(curr_wit, ft);
    }
    
    private String getJoinType(final WhereItem wit, final FromTable ft) {
        String currentTableName = null;
        if (ft != null) {
            if (ft.getAliasName() != null) {
                currentTableName = ft.getAliasName();
            }
            else {
                currentTableName = ft.getTableName().toString();
            }
            String leftTable = this.getTableAliasWhereL(wit);
            String rightTable = this.getTableAliasWhereR(wit);
            if (wit.getLeftJoin() != null) {
                if (ft != null && ft.getTableName() instanceof SelectQueryStatement) {
                    leftTable = ft.getAliasName();
                }
                if (rightTable != null && CustomizeUtil.compareQuotedIdentifiers(rightTable, currentTableName, true)) {
                    return new String(" LEFT OUTER JOIN ");
                }
                if (leftTable != null && CustomizeUtil.compareQuotedIdentifiers(leftTable, currentTableName, true)) {
                    return new String(" RIGHT OUTER JOIN ");
                }
            }
            else {
                if (wit.getRightJoin() == null) {
                    return new String(" INNER JOIN ");
                }
                if (ft != null && ft.getTableName() instanceof SelectQueryStatement) {
                    rightTable = ft.getAliasName();
                }
                if (leftTable != null && CustomizeUtil.compareQuotedIdentifiers(leftTable, currentTableName, true)) {
                    return new String(" LEFT OUTER JOIN ");
                }
                if (rightTable != null && CustomizeUtil.compareQuotedIdentifiers(rightTable, currentTableName, true)) {
                    return new String(" RIGHT OUTER JOIN ");
                }
            }
        }
        return null;
    }
    
    public void loadWhereItemsOperators(final Vector whereItemList, final Vector operatorList) {
        for (int i = 0; i < this.whereItems.size(); ++i) {
            final Object obj = this.whereItems.get(i);
            if (obj instanceof WhereItem) {
                whereItemList.add(obj);
                operatorList.add(this.operators);
            }
            else if (obj instanceof WhereExpression) {
                final WhereExpression we = (WhereExpression)obj;
                we.loadWhereItemsOperators(whereItemList, operatorList);
            }
        }
    }
    
    private void groupWhereItems(final Vector whereItemList, final Vector[] joinExpression, final WhereExpression newWhereExpression) throws ConvertException {
        final String[] tableAliasNameL = new String[whereItemList.size()];
        final String[] tableAliasNameR = new String[whereItemList.size()];
        final Vector wiTables = new Vector();
        final Vector whereExpressions = new Vector();
        final Vector operators = new Vector();
        int operator = -1;
        for (int i = 0; i < whereItemList.size(); ++i) {
            TableColumn tc = new TableColumn();
            final WhereItem wit = whereItemList.get(i);
            final WhereColumn wcl = wit.getLeftWhereExp();
            final WhereColumn wcr = wit.getRightWhereExp();
            if (wit.getLeftJoin() == null && wit.getRightJoin() == null) {
                whereExpressions.add(wit);
                whereItemList.remove(i);
                --i;
                ++operator;
            }
            else {
                if (wcl.getColumnExpression() != null) {
                    if (wcl.getColumnExpression().get(0) instanceof TableColumn) {
                        tc = wcl.getColumnExpression().get(0);
                        if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                            tableAliasNameL[i] = tc.getOwnerName() + "." + tc.getTableName();
                        }
                        else {
                            tableAliasNameL[i] = tc.getTableName();
                        }
                    }
                    if (wcl.getColumnExpression().get(0) instanceof String) {
                        String tableAliaswhere = wcl.getColumnExpression().get(0);
                        if (tableAliaswhere.indexOf(46) != -1) {
                            if (tableAliaswhere.indexOf(".") != tableAliaswhere.lastIndexOf(".")) {
                                if (SwisSQLOptions.removeDBSchemaQualifier) {
                                    tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                                }
                                tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                            }
                            else {
                                tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                            }
                        }
                    }
                }
                if (wcr.getColumnExpression() != null) {
                    if (wcr.getColumnExpression().get(0) instanceof TableColumn) {
                        tc = wcr.getColumnExpression().get(0);
                        if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                            tableAliasNameR[i] = tc.getOwnerName() + "." + tc.getTableName();
                        }
                        else {
                            tableAliasNameR[i] = tc.getTableName();
                        }
                    }
                    if (wcr.getColumnExpression().get(0) instanceof String) {
                        String tableAliaswhere = wcr.getColumnExpression().get(0);
                        if (tableAliaswhere.indexOf(46) != -1) {
                            if (tableAliaswhere.indexOf(".") != tableAliaswhere.lastIndexOf(".")) {
                                if (SwisSQLOptions.removeDBSchemaQualifier) {
                                    tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                                }
                                tableAliasNameR[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                            }
                            else {
                                tableAliasNameR[i] = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                            }
                        }
                    }
                }
                boolean added = false;
                for (int ii = 0; ii < wiTables.size(); ++ii) {
                    final String LR = wiTables.get(ii);
                    if (LR.equals(tableAliasNameL[i] + tableAliasNameR[i])) {
                        wiTables.add(tableAliasNameL[i] + tableAliasNameR[i]);
                        added = true;
                        break;
                    }
                    if (LR.equals(tableAliasNameR[i] + tableAliasNameL[i])) {
                        wiTables.add(tableAliasNameR[i] + tableAliasNameL[i]);
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    wiTables.add(tableAliasNameL[i] + tableAliasNameR[i]);
                }
            }
        }
        for (int i = 0; i < operator; ++i) {
            operators.add(" AND ");
        }
        newWhereExpression.setOperator(operators);
        newWhereExpression.setWhereItem(whereExpressions);
        boolean isSame = false;
        for (int loopCount = wiTables.size(), j = 0; j < loopCount; ++j) {
            joinExpression[j] = new Vector();
            final WhereItem joinWhereItem = whereItemList.get(j);
            joinExpression[j].add(joinWhereItem);
            final int[] indexArray = new int[whereItemList.size()];
            int index = 0;
            int k;
            for (k = j + 1; k < loopCount; ++k) {
                if (wiTables.get(j).equals(wiTables.get(k))) {
                    indexArray[index++] = k;
                    isSame = true;
                }
            }
            if (isSame) {
                for (int in = 0; in < indexArray.length && indexArray[in] != 0; ++in) {
                    joinExpression[j].add(new String("AND"));
                    final WhereItem wi = whereItemList.remove(indexArray[in]);
                    wi.setMovedToFromClause(true);
                    wiTables.remove(indexArray[in]);
                    this.decrement(indexArray);
                    wi.setLeftJoin(null);
                    wi.setRightJoin(null);
                    joinExpression[j].add(wi);
                    --loopCount;
                    --k;
                }
                isSame = false;
            }
        }
    }
    
    private void groupWhereItems(final Vector whereItemList, final Vector[] joinExpression, final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final int database) throws ConvertException {
        final String[] tableAliasNameL = new String[whereItemList.size()];
        final String[] tableAliasNameR = new String[whereItemList.size()];
        final Vector wiTables = new Vector();
        WhereExpression whereExpressions = null;
        final Vector operators = new Vector();
        int operator = -1;
        for (int h = 0; h < whereItemList.size(); ++h) {
            final WhereItem witem = whereItemList.get(h);
            if (witem != null && witem.getRightJoin() != null) {
                final WhereColumn lwhcol = witem.getLeftWhereExp();
                final WhereColumn rwhcol = witem.getRightWhereExp();
                if (lwhcol != null) {
                    if (rwhcol != null) {
                        final String lalias = this.getTableAliasName(lwhcol);
                        final int rightnum = -1;
                        boolean isRightNum = false;
                        try {
                            final String rightStr = rwhcol.toString().trim();
                            final Vector rwhcolExp = rwhcol.getColumnExpression();
                            for (int rwhi = 0; rwhi < rwhcolExp.size(); ++rwhi) {
                                try {
                                    String objStr = rwhcolExp.get(rwhi).toString();
                                    if (objStr.startsWith("(") && objStr.endsWith(")")) {
                                        objStr = StringFunctions.replaceAll("", "(", objStr);
                                        objStr = StringFunctions.replaceAll("", ")", objStr);
                                    }
                                    if (!objStr.equalsIgnoreCase("-") && !objStr.equalsIgnoreCase("+") && !objStr.equalsIgnoreCase("*") && !objStr.equalsIgnoreCase("/") && !objStr.equalsIgnoreCase("(") && !objStr.equalsIgnoreCase(")")) {
                                        Double.parseDouble(objStr.trim());
                                        isRightNum = true;
                                    }
                                }
                                catch (final NumberFormatException nfe) {
                                    break;
                                }
                            }
                        }
                        catch (final Exception ex) {}
                        if (lalias != null && !lalias.equals("") && (rwhcol.toString().trim().startsWith("'") || rwhcol.toString().trim().startsWith("-") || isRightNum)) {
                            for (int k = h + 1; k < whereItemList.size(); ++k) {
                                final WhereItem witem2 = whereItemList.get(k);
                                if (witem2.getRightJoin() != null) {
                                    final WhereColumn lwhcol2 = witem2.getLeftWhereExp();
                                    final WhereColumn rwhcol2 = witem2.getRightWhereExp();
                                    final String lalias2 = this.getTableAliasName(lwhcol2);
                                    final String ralias1 = this.getTableAliasName(rwhcol2);
                                    if (lalias2 != null && !lalias2.equals("") && ralias1 != null && !ralias1.equals("") && lalias2.equals(lalias)) {
                                        final WhereItem wi = whereItemList.remove(h);
                                        if (k + 1 > whereItemList.size()) {
                                            whereItemList.add(wi);
                                        }
                                        else {
                                            whereItemList.add(k + 1, wi);
                                        }
                                        --h;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < whereItemList.size(); ++i) {
            final WhereItem wit = whereItemList.get(i);
            final WhereColumn wcl = wit.getLeftWhereExp();
            final WhereColumn wcr = wit.getRightWhereExp();
            TableColumn tcLeft = new TableColumn();
            TableColumn tcRight = new TableColumn();
            FromTable ft1 = new FromTable();
            FromTable ft2 = new FromTable();
            if (wcl != null && wcl.getColumnExpression() != null && wcl.getColumnExpression().get(0) instanceof TableColumn) {
                tcLeft = wcl.getColumnExpression().get(0);
                ft1 = MetadataInfoUtil.getTableOfColumn(from_sqs, tcLeft);
            }
            if (wcr != null && wcr.getColumnExpression() != null && wcr.getColumnExpression().get(0) instanceof TableColumn) {
                tcRight = wcr.getColumnExpression().get(0);
                ft2 = MetadataInfoUtil.getTableOfColumn(from_sqs, tcRight);
            }
            if (wit.getLeftJoin() == null && wit.getRightJoin() == null) {
                whereItemList.remove(i);
                --i;
                ++operator;
            }
            else {
                TableColumn tc = new TableColumn();
                if (wcl != null && wcl.getColumnExpression() != null) {
                    if (wcl.getColumnExpression().get(0) instanceof TableColumn) {
                        tc = wcl.getColumnExpression().get(0);
                        if (tc.getTableName() == null || tc.getColumnName().trim().equals("?")) {
                            final String tableAliaswhere = tc.getColumnName();
                            String fromTableAlias = null;
                            final FromTable fromTableOfWhereColumn = MetadataInfoUtil.getTableOfColumn(from_sqs, tc);
                            if (fromTableOfWhereColumn != null) {
                                fromTableAlias = fromTableOfWhereColumn.getAliasName();
                                if (fromTableAlias != null) {
                                    tableAliasNameL[i] = fromTableAlias;
                                }
                                else {
                                    tableAliasNameL[i] = fromTableOfWhereColumn.getTableName().toString();
                                }
                            }
                            else {
                                tableAliasNameL[i] = "";
                            }
                        }
                        else if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                            tableAliasNameL[i] = tc.getOwnerName() + "." + tc.getTableName();
                        }
                        else {
                            tableAliasNameL[i] = tc.getTableName();
                        }
                    }
                    if (wcl.getColumnExpression().get(0) instanceof FunctionCalls) {
                        final FunctionCalls fc = wcl.getColumnExpression().get(0);
                        final Vector functionObjects = this.putObjectsInOneList(fc.getFunctionArguments());
                        if (functionObjects.size() > 0) {
                            if (functionObjects.get(0) instanceof TableColumn) {
                                tc = functionObjects.get(0);
                                if (tc.getTableName() == null || tc.getColumnName().trim().equals("?")) {
                                    final String tableAliaswhere2 = tc.getColumnName();
                                    tableAliasNameL[i] = "";
                                }
                                else if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                                    tableAliasNameL[i] = tc.getOwnerName() + "." + tc.getTableName();
                                }
                                else {
                                    tableAliasNameL[i] = tc.getTableName();
                                }
                            }
                            else if (functionObjects.get(0) instanceof SelectColumn) {
                                final SelectColumn sc = functionObjects.get(0);
                                if (sc.getColumnExpression() != null) {
                                    for (int index = 0; index < sc.getColumnExpression().size(); ++index) {
                                        if (sc.getColumnExpression().get(index) instanceof TableColumn) {
                                            tc = sc.getColumnExpression().get(index);
                                            if (tc.getTableName() == null || tc.getColumnName().trim().equals("?")) {
                                                final String tableAliaswhere3 = tc.getColumnName();
                                                tableAliasNameL[i] = "";
                                            }
                                            else if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                                                tableAliasNameL[i] = tc.getOwnerName() + "." + tc.getTableName();
                                            }
                                            else {
                                                tableAliasNameL[i] = tc.getTableName();
                                            }
                                        }
                                    }
                                }
                            }
                            else if (functionObjects.get(0) instanceof String) {
                                String tableAliaswhere2 = functionObjects.get(0);
                                if (tableAliaswhere2.indexOf(46) != -1) {
                                    if (tableAliaswhere2.indexOf(46) != tableAliaswhere2.lastIndexOf(46)) {
                                        if (SwisSQLOptions.removeDBSchemaQualifier) {
                                            tableAliaswhere2 = tableAliaswhere2.substring(tableAliaswhere2.indexOf(".") + 1);
                                        }
                                        tableAliasNameL[i] = tableAliaswhere2.substring(0, tableAliaswhere2.lastIndexOf("."));
                                    }
                                    else {
                                        tableAliasNameL[i] = tableAliaswhere2.substring(0, tableAliaswhere2.indexOf("."));
                                    }
                                }
                                else {
                                    tableAliasNameL[i] = "";
                                }
                            }
                            else if (functionObjects.get(0) instanceof NumericClass) {
                                if (functionObjects.size() == 2) {
                                    if (functionObjects.get(1) instanceof TableColumn) {
                                        final TableColumn tc2 = functionObjects.get(1);
                                        tableAliasNameL[i] = tc2.getTableName();
                                    }
                                    else if (functionObjects.get(1) instanceof String) {
                                        String tableAliaswhere2 = functionObjects.get(1);
                                        if (tableAliaswhere2.indexOf(46) != -1) {
                                            if (tableAliaswhere2.indexOf(46) != tableAliaswhere2.lastIndexOf(46)) {
                                                if (SwisSQLOptions.removeDBSchemaQualifier) {
                                                    tableAliaswhere2 = tableAliaswhere2.substring(tableAliaswhere2.indexOf(".") + 1);
                                                }
                                                tableAliasNameL[i] = tableAliaswhere2.substring(0, tableAliaswhere2.lastIndexOf("."));
                                            }
                                            else {
                                                tableAliasNameL[i] = tableAliaswhere2.substring(0, tableAliaswhere2.indexOf("."));
                                            }
                                        }
                                        else {
                                            tableAliasNameL[i] = "";
                                        }
                                    }
                                    else {
                                        tableAliasNameL[i] = "";
                                    }
                                }
                                else {
                                    tableAliasNameL[i] = "";
                                }
                            }
                        }
                        else if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                            tableAliasNameL[i] = tc.getOwnerName() + "." + tc.getTableName();
                        }
                        else {
                            tableAliasNameL[i] = tc.getTableName();
                        }
                    }
                    if (wcl.getColumnExpression().get(0) instanceof SelectColumn) {
                        final Vector colExpr = wcl.getColumnExpression().get(0).getColumnExpression();
                        int j = 0;
                        while (j < colExpr.size()) {
                            if (colExpr.get(j) instanceof TableColumn) {
                                tc = colExpr.get(j);
                                if (tc.getTableName() == null || tc.getColumnName().trim().equals("?")) {
                                    final String tableAliaswhere2 = tc.getColumnName();
                                    tableAliasNameL[i] = "";
                                    break;
                                }
                                if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                                    tableAliasNameL[i] = tc.getOwnerName() + "." + tc.getTableName();
                                    break;
                                }
                                tableAliasNameL[i] = tc.getTableName();
                                break;
                            }
                            else {
                                if (colExpr.get(j) instanceof String) {
                                    String tableAliaswhere2 = colExpr.get(j);
                                    if (tableAliaswhere2.indexOf(46) != -1) {
                                        if (tableAliaswhere2.indexOf(46) != tableAliaswhere2.lastIndexOf(46)) {
                                            if (SwisSQLOptions.removeDBSchemaQualifier) {
                                                tableAliaswhere2 = tableAliaswhere2.substring(tableAliaswhere2.indexOf(".") + 1);
                                            }
                                            tableAliasNameL[i] = tableAliaswhere2.substring(0, tableAliaswhere2.lastIndexOf("."));
                                        }
                                        else {
                                            tableAliasNameL[i] = tableAliaswhere2.substring(0, tableAliaswhere2.indexOf("."));
                                        }
                                    }
                                    else {
                                        tableAliasNameL[i] = "";
                                    }
                                }
                                if (colExpr.get(j) instanceof CaseStatement) {
                                    final CaseStatement casestmt = colExpr.get(j);
                                    tableAliasNameL[i] = this.getTableAliasName(casestmt);
                                }
                                ++j;
                            }
                        }
                    }
                    if (wcl.getColumnExpression().get(0) instanceof CaseStatement) {
                        final CaseStatement casestmt2 = wcl.getColumnExpression().get(0);
                        tableAliasNameL[i] = this.getTableAliasName(casestmt2);
                    }
                    if (wcl.getColumnExpression().get(0) instanceof String) {
                        String tableAliaswhere = wcl.getColumnExpression().get(0);
                        if (tableAliaswhere.indexOf(46) != -1) {
                            if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46)) {
                                if (SwisSQLOptions.removeDBSchemaQualifier) {
                                    tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                                }
                                tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                            }
                            else {
                                tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                            }
                        }
                        else {
                            tableAliasNameL[i] = "";
                        }
                    }
                }
                else {
                    tableAliasNameL[i] = "";
                }
                if (wcr != null && wcr.getColumnExpression() != null) {
                    if (wcr.getColumnExpression().get(0) instanceof SelectColumn) {
                        final Vector colExpr = wcr.getColumnExpression().get(0).getColumnExpression();
                        int j = 0;
                        while (j < colExpr.size()) {
                            if (colExpr.get(j) instanceof TableColumn) {
                                tc = colExpr.get(j);
                                if (tc.getTableName() == null || tc.getColumnName().trim().equals("?")) {
                                    final String tableAliaswhere2 = tc.getColumnName();
                                    tableAliasNameR[i] = "";
                                    break;
                                }
                                if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                                    tableAliasNameR[i] = tc.getOwnerName() + "." + tc.getTableName();
                                    break;
                                }
                                tableAliasNameR[i] = tc.getTableName();
                                break;
                            }
                            else {
                                if (colExpr.get(j) instanceof String) {
                                    String tableAliaswhere2 = colExpr.get(j);
                                    if (tableAliaswhere2.indexOf(46) != -1) {
                                        if (tableAliaswhere2.indexOf(46) != tableAliaswhere2.lastIndexOf(46)) {
                                            if (SwisSQLOptions.removeDBSchemaQualifier) {
                                                tableAliaswhere2 = tableAliaswhere2.substring(tableAliaswhere2.indexOf(".") + 1);
                                            }
                                            tableAliasNameR[i] = tableAliaswhere2.substring(0, tableAliaswhere2.lastIndexOf("."));
                                        }
                                        else {
                                            tableAliasNameR[i] = tableAliaswhere2.substring(0, tableAliaswhere2.indexOf("."));
                                        }
                                    }
                                    else {
                                        tableAliasNameR[i] = "";
                                    }
                                }
                                if (colExpr.get(j) instanceof CaseStatement) {
                                    final CaseStatement casestmt = colExpr.get(j);
                                    tableAliasNameR[i] = this.getTableAliasName(casestmt);
                                }
                                ++j;
                            }
                        }
                    }
                    if (wcr.getColumnExpression().get(0) instanceof TableColumn) {
                        tc = wcr.getColumnExpression().get(0);
                        if (tc.getTableName() == null || tc.getColumnName().trim().equals("?")) {
                            final String tableAliaswhere = tc.getColumnName();
                            String fromTableAlias = null;
                            final FromTable fromTableOfWhereColumn = MetadataInfoUtil.getTableOfColumn(from_sqs, tc);
                            if (fromTableOfWhereColumn != null) {
                                fromTableAlias = fromTableOfWhereColumn.getAliasName();
                                if (fromTableAlias != null) {
                                    tableAliasNameR[i] = fromTableAlias;
                                }
                                else {
                                    tableAliasNameR[i] = fromTableOfWhereColumn.getTableName().toString();
                                }
                            }
                            else {
                                tableAliasNameR[i] = "";
                            }
                        }
                        else if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                            tableAliasNameR[i] = tc.getOwnerName() + "." + tc.getTableName();
                        }
                        else {
                            tableAliasNameR[i] = tc.getTableName();
                        }
                    }
                    if (wcr.getColumnExpression().get(0) instanceof FunctionCalls) {
                        final FunctionCalls fc = wcr.getColumnExpression().get(0);
                        final Vector functionObjects = this.putObjectsInOneList(fc.getFunctionArguments());
                        if (functionObjects.size() > 0) {
                            if (functionObjects.get(0) instanceof TableColumn) {
                                tc = functionObjects.get(0);
                                if (tc.getTableName() == null || tc.getColumnName().trim().equals("?")) {
                                    final String tableAliaswhere2 = tc.getColumnName();
                                    tableAliasNameR[i] = "";
                                }
                                else if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                                    tableAliasNameR[i] = tc.getOwnerName() + "." + tc.getTableName();
                                }
                                else {
                                    tableAliasNameR[i] = tc.getTableName();
                                }
                            }
                            else if (functionObjects.get(0) instanceof SelectColumn) {
                                final SelectColumn sc = functionObjects.get(0);
                                if (sc.getColumnExpression() != null) {
                                    for (int index = 0; index < sc.getColumnExpression().size(); ++index) {
                                        if (sc.getColumnExpression().get(index) instanceof TableColumn) {
                                            tc = sc.getColumnExpression().get(index);
                                            if (tc.getTableName() == null && tc.getColumnName().trim().equals("?")) {
                                                final String tableAliaswhere3 = tc.getColumnName();
                                                tableAliasNameR[i] = "";
                                            }
                                            else if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                                                tableAliasNameR[i] = tc.getOwnerName() + "." + tc.getTableName();
                                            }
                                            else {
                                                tableAliasNameR[i] = tc.getTableName();
                                            }
                                        }
                                    }
                                }
                            }
                            else if (functionObjects.get(0) instanceof String) {
                                String tableAliaswhere2 = functionObjects.get(0);
                                if (tableAliaswhere2.indexOf(46) != -1) {
                                    if (tableAliaswhere2.indexOf(46) != tableAliaswhere2.lastIndexOf(46)) {
                                        if (SwisSQLOptions.removeDBSchemaQualifier) {
                                            tableAliaswhere2 = tableAliaswhere2.substring(tableAliaswhere2.indexOf(".") + 1);
                                        }
                                        tableAliasNameR[i] = tableAliaswhere2.substring(0, tableAliaswhere2.lastIndexOf("."));
                                    }
                                    else {
                                        tableAliasNameR[i] = tableAliaswhere2.substring(0, tableAliaswhere2.indexOf("."));
                                    }
                                }
                                else {
                                    tableAliasNameR[i] = "";
                                }
                            }
                        }
                        else if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                            tableAliasNameR[i] = tc.getOwnerName() + "." + tc.getTableName();
                        }
                        else {
                            tableAliasNameR[i] = tc.getTableName();
                        }
                    }
                    if (wcr.getColumnExpression().get(0) instanceof CaseStatement) {
                        final CaseStatement casestmt2 = wcr.getColumnExpression().get(0);
                        tableAliasNameR[i] = this.getTableAliasName(casestmt2);
                    }
                    if (wcr.getColumnExpression().get(0) instanceof String) {
                        String tableAliaswhere = wcr.getColumnExpression().get(0);
                        if (tableAliaswhere.indexOf(46) != -1) {
                            if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46)) {
                                if (SwisSQLOptions.removeDBSchemaQualifier) {
                                    tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                                }
                                tableAliasNameR[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                            }
                            else {
                                tableAliasNameR[i] = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                            }
                        }
                        else {
                            tableAliasNameR[i] = "";
                        }
                    }
                }
                else {
                    tableAliasNameR[i] = "";
                }
                if ((database == 12 || database == 8) && ((tableAliasNameL[i] != null && tableAliasNameL[i].equalsIgnoreCase("")) || (tableAliasNameR[i] != null && tableAliasNameR[i].equalsIgnoreCase(""))) && (wit.getLeftJoin() != null || wit.getRightJoin() != null) && this.isColumnNameExistsInWhereColumn(wit.getRightWhereExp().getColumnExpression().get(0))) {
                    String errMsg = "SwisSQL Message : Tablename is not provided for one of the Columns in the Where Condition";
                    errMsg = errMsg + " : " + wit.toString() + " \n";
                    errMsg += " Please provide the Tablename in the original query and then convert.";
                    throw new ConvertException(errMsg);
                }
                boolean added = false;
                for (int ii = 0; ii < wiTables.size(); ++ii) {
                    if (tableAliasNameL[i] != null && tableAliasNameR[i] != null) {
                        final String LR = wiTables.get(ii);
                        if (LR.equals(tableAliasNameL[i] + "-" + tableAliasNameR[i])) {
                            wiTables.add(tableAliasNameL[i] + "-" + tableAliasNameR[i]);
                            added = true;
                            break;
                        }
                        if (LR.equals(tableAliasNameR[i] + "-" + tableAliasNameL[i])) {
                            wiTables.add(tableAliasNameR[i] + "-" + tableAliasNameL[i]);
                            added = true;
                            break;
                        }
                    }
                }
                if (!added && tableAliasNameL[i] != null && tableAliasNameR[i] != null) {
                    wiTables.add(tableAliasNameL[i] + "-" + tableAliasNameR[i]);
                }
            }
        }
        for (int i = 0; i < operator; ++i) {
            operators.add(" AND ");
        }
        boolean isSame = false;
        for (int loopCount = wiTables.size(), l = 0; l < loopCount; ++l) {
            whereExpressions = new WhereExpression();
            joinExpression[l] = new Vector();
            final WhereItem joinWhereItem = whereItemList.get(l);
            whereExpressions.addWhereItem(joinWhereItem.convert(to_sqs, from_sqs, database));
            final int[] indexArray = new int[whereItemList.size()];
            int index2 = 0;
            for (int m = l + 1; m < loopCount; ++m) {
                if (wiTables.get(l).equals(wiTables.get(m))) {
                    indexArray[index2++] = m;
                    isSame = true;
                }
                else {
                    String ithElement = wiTables.get(l);
                    String jthElement = wiTables.get(m);
                    if (jthElement.endsWith("-")) {
                        jthElement = jthElement.substring(0, jthElement.length() - 1);
                    }
                    else if (jthElement.startsWith("-")) {
                        jthElement = jthElement.substring(1);
                    }
                    ithElement = "&" + ithElement.replaceAll("-", "&-&") + "&";
                    jthElement = "&" + jthElement.replaceAll("-", "&-&") + "&";
                    if (ithElement.indexOf("-" + jthElement) != -1 || ithElement.indexOf(jthElement + "-") != -1) {
                        indexArray[index2++] = m;
                        isSame = true;
                    }
                }
            }
            if (isSame) {
                for (int in = 0; in < indexArray.length && indexArray[in] != 0; ++in) {
                    whereExpressions.addOperator("AND");
                    final WhereItem wi2 = whereItemList.remove(indexArray[in] - in);
                    wiTables.remove(indexArray[in] - in);
                    whereExpressions.addWhereItem(wi2.convert(to_sqs, from_sqs, database));
                    --loopCount;
                }
                isSame = false;
            }
            joinExpression[l].add(whereExpressions);
        }
    }
    
    public void orderWhereItems(final Vector whereItemList, final Vector[] joinExpression) {
        for (int size = whereItemList.size(), i = 0; i < size; ++i) {
            final WhereItem wi = whereItemList.elementAt(i);
            final String leftTable = this.getTableAliasWhereL(wi);
            final String rightTable = this.getTableAliasWhereR(wi);
            int position = i + 1;
            for (int j = i + 1; j < size; ++j) {
                final WhereItem nextWI = whereItemList.elementAt(j);
                final String nextLeftTable = this.getTableAliasWhereL(nextWI);
                final String nextRightTable = this.getTableAliasWhereR(nextWI);
                if ((leftTable != null && nextLeftTable != null && CustomizeUtil.compareQuotedIdentifiers(leftTable, nextLeftTable, true)) || (leftTable != null && nextRightTable != null && CustomizeUtil.compareQuotedIdentifiers(leftTable, nextRightTable, true)) || (rightTable != null && nextLeftTable != null && CustomizeUtil.compareQuotedIdentifiers(rightTable, nextLeftTable, true)) || (rightTable != null && nextRightTable != null && CustomizeUtil.compareQuotedIdentifiers(rightTable, nextRightTable, true))) {
                    whereItemList.insertElementAt(whereItemList.remove(j), position);
                    Vector v = joinExpression[position];
                    for (int k = position; k < j; ++k) {
                        final Vector vNext = joinExpression[k + 1];
                        joinExpression[k + 1] = v;
                        v = vNext;
                    }
                    joinExpression[position] = v;
                    ++position;
                }
            }
        }
    }
    
    private void decrement(final int[] indexArray) {
        for (int i = 0; i < indexArray.length && indexArray[i] != 0; ++i) {
            final int n = i;
            --indexArray[n];
        }
    }
    
    private boolean isColumnNameExistsInWhereColumn(final Object obj) {
        if (obj instanceof SelectColumn) {
            final SelectColumn sc = (SelectColumn)obj;
            final Vector vc = sc.getColumnExpression();
            for (int i = 0, size = vc.size(); i < size; ++i) {
                if (this.isColumnNameExistsInWhereColumn(vc.get(i))) {
                    return true;
                }
            }
            return false;
        }
        if (obj instanceof FunctionCalls) {
            final FunctionCalls fc = (FunctionCalls)obj;
            final Vector vc = fc.getFunctionArguments();
            for (int i = 0, size = vc.size(); i < size; ++i) {
                if (this.isColumnNameExistsInWhereColumn(vc.get(i))) {
                    return true;
                }
            }
            return false;
        }
        if (obj instanceof TableColumn) {
            return this.isColumnNameExistsInWhereColumn(((TableColumn)obj).getColumnName());
        }
        if (obj instanceof String) {
            final String str = obj.toString().trim();
            return !str.startsWith("'") && !this.isNumber(str) && !this.isOperator(str) && !this.isKeyword(str, 12) && !this.isKeyword(str, 8);
        }
        return true;
    }
    
    private boolean isKeyword(final String str, final int database) {
        final String[] keyList = SwisSQLUtils.getKeywords(database);
        if (keyList == null) {
            return false;
        }
        for (int i = 0, size = keyList.length; i < size; ++i) {
            if (str.equalsIgnoreCase(keyList[i])) {
                return true;
            }
        }
        return str.equalsIgnoreCase("DUAL") || str.equalsIgnoreCase("SYS.DUAL") || str.equalsIgnoreCase("DATE") || str.equalsIgnoreCase("CURRENT DATE") || str.equalsIgnoreCase("\"rownum\"") || str.equalsIgnoreCase("rownum") || str.equalsIgnoreCase("SYSDATE") || str.equalsIgnoreCase("SYS_GUID") || str.equalsIgnoreCase("TIME") || str.equalsIgnoreCase("CURRENT TIME") || str.equalsIgnoreCase("USER") || str.equalsIgnoreCase("SYSDATE") || str.equalsIgnoreCase("TIMESTAMP") || str.equalsIgnoreCase("SYSTIMESTAMP") || str.equalsIgnoreCase("CURRENT TIMESTAMP") || str.equalsIgnoreCase("CURRENT") || str.equalsIgnoreCase("SYSTEM_USER");
    }
    
    private boolean isOperator(final String str) {
        return str.equalsIgnoreCase("+") || str.equalsIgnoreCase("-") || str.equalsIgnoreCase("*") || str.equalsIgnoreCase("/");
    }
    
    private boolean isNumber(final String str) {
        try {
            Integer.parseInt(str.substring(0, 1));
        }
        catch (final NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    public Vector arrangeTheWhereItemListAccordingToTheOrderInFromItemList(final Vector whereItemList, final Vector fromItemList) {
        final Vector arrangeWhereItemList = new Vector();
        final Vector originalWhereItemList = whereItemList;
        final Vector originalFromItemList = fromItemList;
        for (int i = 0; i < originalFromItemList.size(); ++i) {
            FromTable ft1 = null;
            if (originalFromItemList.elementAt(i) instanceof FromTable) {
                ft1 = originalFromItemList.get(i);
            }
            else if (originalFromItemList.elementAt(i) instanceof FromClause) {
                final Vector newFromItemVector = originalFromItemList.get(i).getFromItemList();
                for (int index = 0; index < newFromItemVector.size(); ++index) {
                    if (newFromItemVector.elementAt(index) instanceof FromTable) {
                        ft1 = newFromItemVector.get(index);
                    }
                    else {
                        this.arrangeTheWhereItemListAccordingToTheOrderInFromItemList(whereItemList, newFromItemVector);
                    }
                }
            }
            for (int count = i + 1; count < originalFromItemList.size(); ++count) {
                FromTable ft2 = null;
                if (originalFromItemList.elementAt(count) instanceof FromTable) {
                    ft2 = originalFromItemList.get(count);
                }
                else if (originalFromItemList.elementAt(count) instanceof FromClause) {
                    final Vector newFromItemVector2 = originalFromItemList.get(count).getFromItemList();
                    for (int index2 = 0; index2 < newFromItemVector2.size(); ++index2) {
                        if (newFromItemVector2.elementAt(index2) instanceof FromTable) {
                            ft2 = newFromItemVector2.get(index2);
                        }
                        else {
                            this.arrangeTheWhereItemListAccordingToTheOrderInFromItemList(whereItemList, newFromItemVector2);
                        }
                    }
                }
                this.orderTheWhereItemAccordingToTheFromItem(ft1, ft2, arrangeWhereItemList, originalWhereItemList);
            }
        }
        for (int i = 0; i < originalWhereItemList.size(); ++i) {
            arrangeWhereItemList.add(originalWhereItemList.get(i));
        }
        return arrangeWhereItemList;
    }
    
    public void orderTheWhereItemAccordingToTheFromItem(final FromTable ft1, final FromTable ft2, final Vector arrangeWhereItemList, final Vector originalWhereItemList) {
        final int currentArrangedSize = arrangeWhereItemList.size();
        for (int i = 0; i < originalWhereItemList.size(); ++i) {
            final WhereItem wi = originalWhereItemList.elementAt(i);
            final String leftTable = this.getTableAliasWhereL(wi);
            final String rightTable = this.getTableAliasWhereR(wi);
            String fromTableAliasName1 = ft1.getAliasName();
            String fromTableAliasName2 = ft2.getAliasName();
            if (fromTableAliasName1 == null) {
                fromTableAliasName1 = ft1.getTableName().toString();
            }
            if (fromTableAliasName2 == null) {
                fromTableAliasName2 = ft2.getTableName().toString();
            }
            if (fromTableAliasName1.equalsIgnoreCase(leftTable) && fromTableAliasName2.equalsIgnoreCase(rightTable)) {
                if (arrangeWhereItemList.size() > currentArrangedSize) {
                    arrangeWhereItemList.insertElementAt(wi, currentArrangedSize);
                }
                else {
                    arrangeWhereItemList.add(wi);
                }
                originalWhereItemList.remove(i);
                --i;
            }
            else if (fromTableAliasName1.equalsIgnoreCase(rightTable) && fromTableAliasName2.equalsIgnoreCase(leftTable)) {
                final WhereColumn newLeftWhereExp = wi.getLeftWhereExp();
                final WhereColumn newRightWhereExp = wi.getRightWhereExp();
                final String leftJoin = wi.getLeftJoin();
                final String rightJoin = wi.getRightJoin();
                wi.setLeftJoin(rightJoin);
                wi.setRightJoin(leftJoin);
                wi.setLeftWhereExp(newRightWhereExp);
                wi.setRightWhereExp(newLeftWhereExp);
                if (arrangeWhereItemList.size() > currentArrangedSize) {
                    arrangeWhereItemList.insertElementAt(wi, currentArrangedSize);
                }
                else {
                    arrangeWhereItemList.add(wi);
                }
                originalWhereItemList.remove(i);
                --i;
            }
            else if (leftTable == null && fromTableAliasName1.equalsIgnoreCase(rightTable)) {
                arrangeWhereItemList.add(wi);
                originalWhereItemList.remove(i);
                --i;
            }
            else if (leftTable != null && leftTable.trim().equals("") && fromTableAliasName1.equalsIgnoreCase(rightTable)) {
                arrangeWhereItemList.add(wi);
                originalWhereItemList.remove(i);
                --i;
            }
            else if (rightTable == null && fromTableAliasName1.equalsIgnoreCase(leftTable)) {
                arrangeWhereItemList.add(wi);
                originalWhereItemList.remove(i);
                --i;
            }
            else if (rightTable != null && rightTable.trim().equals("") && fromTableAliasName1.equalsIgnoreCase(leftTable)) {
                arrangeWhereItemList.add(wi);
                originalWhereItemList.remove(i);
                --i;
            }
        }
    }
    
    private void addFetchClause(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (to_sqs == null) {
            return;
        }
        final FetchClause fetch_clause = new FetchClause();
        String rownumValue = "0";
        if (this.rownumClause.getRownumValue() instanceof SelectQueryStatement) {
            throw new ConvertException("Conversion failure.. Subquery can't be converted");
        }
        if (this.rownumClause.getRownumValue() instanceof SelectColumn) {
            final SelectColumn sc = (SelectColumn)this.rownumClause.getRownumValue();
            final Vector colExp = sc.getColumnExpression();
            for (int i = 0; i < colExp.size(); ++i) {
                if (colExp.elementAt(i) instanceof FunctionCalls) {
                    throw new ConvertException("Conversion failure.. Function calls can't be converted");
                }
                if (colExp.elementAt(i) instanceof TableColumn) {
                    if (this.rownumClause.getOperator().equals("<=") || this.rownumClause.getOperator().equals("=")) {
                        fetch_clause.setFetchCountVariable(colExp.elementAt(i).toString());
                    }
                    else {
                        fetch_clause.setFetchCountVariable(colExp.elementAt(i).toString() + " - 1");
                    }
                }
                else {
                    if (!(colExp.elementAt(i) instanceof String) || colExp.size() != 1) {
                        throw new ConvertException("Conversion failure.. Expression can't be converted");
                    }
                    rownumValue = colExp.elementAt(i);
                }
            }
        }
        fetch_clause.setFetchFirstClause("FETCH FIRST");
        fetch_clause.setRowOnlyClause("ROWS ONLY");
        if (this.rownumClause.getOperator().equals("<=") || this.rownumClause.getOperator().equals("=")) {
            fetch_clause.setFetchCount(rownumValue);
        }
        else {
            fetch_clause.setFetchCount(Integer.parseInt(rownumValue) - 1 + "");
        }
        if (to_sqs.getFetchClause() != null) {
            throw new ConvertException();
        }
        to_sqs.setFetchClause(fetch_clause);
        final SelectStatement ss = to_sqs.getSelectStatement();
        if (ss != null) {
            final Vector scItem = ss.getSelectItemList();
            if (scItem != null) {
                for (int i = 0; i < scItem.size(); ++i) {
                    if (scItem.get(i) instanceof SelectColumn) {
                        final SelectColumn sc2 = scItem.get(i);
                        if (sc2 != null) {
                            final Vector cols = sc2.getColumnExpression();
                            if (cols != null) {
                                for (int j = 0; j < cols.size(); ++j) {
                                    if (cols.get(j) instanceof TableColumn) {
                                        final TableColumn tc = cols.get(j);
                                        if (tc != null) {
                                            final String colName = tc.getColumnName();
                                            if (colName != null && colName.toLowerCase().equals("rownum")) {
                                                tc.setColumnName("ROW_NUMBER() OVER()");
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
        to_sqs.setLimitClause(null);
    }
    
    private void addLimitClause(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final LimitClause limitClause = new LimitClause();
        String rownumValue = new String("0");
        if (this.rownumClause.getRownumValue() instanceof SelectQueryStatement) {
            throw new ConvertException(" SubQuery are not allowed in Limit clause");
        }
        if (this.rownumClause.getRownumValue() instanceof SelectColumn) {
            final SelectColumn sc = (SelectColumn)this.rownumClause.getRownumValue();
            final Vector colExp = sc.getColumnExpression();
            for (int i = 0; i < colExp.size(); ++i) {
                if (colExp.elementAt(i) instanceof FunctionCalls) {
                    throw new ConvertException(" Function calls are not allowed in Limit clause");
                }
                if (colExp.elementAt(i) instanceof TableColumn) {
                    throw new ConvertException(" Identifier is not allowed in Limit clause");
                }
                if (!(colExp.elementAt(i) instanceof String) || colExp.size() != 1) {
                    throw new ConvertException(" Expression are not allowed in Limit clause");
                }
                rownumValue = colExp.elementAt(i);
            }
        }
        limitClause.setLimitClause("LIMIT");
        if (this.rownumClause.getOperator().equals("<=")) {
            limitClause.setLimitValue(rownumValue);
        }
        else {
            limitClause.setLimitValue(Integer.parseInt(rownumValue) - 1 + "");
        }
        if (to_sqs.getLimitClause() != null) {
            throw new ConvertException();
        }
        to_sqs.setLimitClause(limitClause);
        to_sqs.setFetchClause(null);
    }
    
    private boolean isMetaDataRequired(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final WhereExpression newWhereExpression = new WhereExpression();
        final FromClause fromClause = to_sqs.getFromClause();
        if (fromClause != null) {
            final FromClause newFromClause = new FromClause();
            final Vector newFromItemList = new Vector();
            newFromClause.setFromClause("FROM");
            newFromClause.setFromItemList(newFromItemList);
            final Vector fromItemList = fromClause.getFromItemList();
            final Vector whereItemList = new Vector();
            final Vector operatorList = new Vector();
            final Vector[] joinExpression = new Vector[fromItemList.size()];
            this.loadWhereItemsOperators(whereItemList, operatorList);
            final String[] tableAliasNameL = new String[whereItemList.size()];
            final String[] tableAliasNameR = new String[whereItemList.size()];
            final Vector wiTables = new Vector();
            final Vector whereExpressions = new Vector();
            final Vector operators = new Vector();
            int operator = -1;
            for (int i = 0; i < whereItemList.size(); ++i) {
                TableColumn tc = new TableColumn();
                final WhereItem wit = whereItemList.get(i);
                final WhereColumn wcl = wit.getLeftWhereExp();
                final WhereColumn wcr = wit.getRightWhereExp();
                try {
                    if (wit.getLeftJoin() == null && wit.getRightJoin() == null) {
                        whereExpressions.add(wit);
                        whereItemList.remove(i);
                        --i;
                        ++operator;
                        continue;
                    }
                    if (wcl.getColumnExpression() != null) {
                        if (wcl.getColumnExpression().get(0) instanceof TableColumn) {
                            tc = wcl.getColumnExpression().get(0);
                            if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                                tableAliasNameL[i] = tc.getOwnerName() + "." + tc.getTableName();
                            }
                            else {
                                tableAliasNameL[i] = tc.getTableName();
                            }
                        }
                        if (wcl.getColumnExpression().get(0) instanceof String) {
                            final String tableAliaswhere = wcl.getColumnExpression().get(0);
                            if (!tableAliaswhere.startsWith("'")) {
                                try {
                                    Integer.parseInt(tableAliaswhere);
                                }
                                catch (final NumberFormatException nfe) {
                                    tableAliasNameL[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                                }
                            }
                        }
                    }
                    if (wcr.getColumnExpression() != null) {
                        if (wcr.getColumnExpression().get(0) instanceof TableColumn) {
                            tc = wcr.getColumnExpression().get(0);
                            if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                                tableAliasNameR[i] = tc.getOwnerName() + "." + tc.getTableName();
                            }
                            else {
                                tableAliasNameR[i] = tc.getTableName();
                            }
                        }
                        if (wcr.getColumnExpression().get(0) instanceof String) {
                            final String tableAliaswhere = wcr.getColumnExpression().get(0);
                            if (!tableAliaswhere.startsWith("'")) {
                                try {
                                    Integer.parseInt(tableAliaswhere);
                                }
                                catch (final NumberFormatException nfe) {
                                    tableAliasNameR[i] = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                                }
                            }
                        }
                    }
                    wiTables.add(tableAliasNameL[i] + tableAliasNameR[i]);
                }
                catch (final Exception e) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }
    
    private boolean isWhereItemInClauseIsMultipleAndNotSubquery(final WhereItem whereItem) {
        final boolean isWhereItemNormal = false;
        final String whereItemOperator = whereItem.getOperator();
        final SelectQueryStatement rightWhereSubquery = whereItem.getRightWhereSubQuery();
        final WhereColumn rightWhereExpression = whereItem.getRightWhereExp();
        if (whereItemOperator != null && (whereItemOperator.equalsIgnoreCase("IN") || whereItemOperator.equalsIgnoreCase("NOT IN")) && rightWhereExpression != null && rightWhereSubquery == null) {
            final WhereColumn leftWhereColumn = whereItem.getLeftWhereExp();
            if (leftWhereColumn.getColumnExpression() != null && leftWhereColumn.getColumnExpression().size() != 1) {
                final Vector colExpr = leftWhereColumn.getColumnExpression();
                for (int i = 0; i < colExpr.size(); ++i) {
                    if (colExpr.get(i).toString().trim().equals(",")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean isWhereItemEqualsClauseIsMultipleAndNotSubquery(final WhereItem whereItem) {
        final boolean isWhereItemNormal = false;
        final String whereItemOperator = whereItem.getOperator2();
        final SelectQueryStatement rightWhereSubquery = whereItem.getRightWhereSubQuery();
        final WhereColumn rightWhereExpression = whereItem.getRightWhereExp();
        return whereItemOperator != null && (whereItemOperator.equalsIgnoreCase("ALL") || whereItemOperator.equalsIgnoreCase("ANY") || whereItemOperator.equalsIgnoreCase("SOME")) && rightWhereExpression != null && rightWhereSubquery == null;
    }
    
    private void setNewFromClauseWithInformixJoin(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final int database) throws ConvertException {
        if (from_sqs == null) {
            return;
        }
        final FromClause fromClause = to_sqs.getFromClause();
        if (fromClause != null) {
            final FromClause newFromClause = new FromClause();
            final Vector newFromItemList = new Vector();
            newFromClause.setFromClause("FROM");
            newFromClause.setFromItemList(newFromItemList);
            final Vector fromItemList = fromClause.getFromItemList();
            final Vector whereItemList = new Vector();
            final Vector operatorList = new Vector();
            final Vector[] joinExpression = new Vector[fromItemList.size()];
            this.loadWhereItemsOperators(whereItemList, operatorList);
            if (this.isMetaDataRequired(to_sqs, from_sqs)) {
                String exceptionString = new String();
                exceptionString = "The given query involves theta join to INFORMIX join.\nIt needs metadata for conversion";
                throw new ConvertException(exceptionString);
            }
            this.groupWhereItems(whereItemList, joinExpression, to_sqs, from_sqs, database);
            final int index = 0;
            boolean fromChanged = false;
            final Vector outerJoinTableItems = new Vector();
            for (int i = 0; i < whereItemList.size(); ++i) {
                final WhereItem wit = whereItemList.get(i);
                if (wit.getLeftJoin() != null) {
                    if (!outerJoinTableItems.contains(this.getTableAliasWhereR(wit))) {
                        outerJoinTableItems.add(this.getTableAliasWhereR(wit));
                    }
                }
                else if (wit.getRightJoin() != null && !outerJoinTableItems.contains(this.getTableAliasWhereL(wit))) {
                    outerJoinTableItems.add(this.getTableAliasWhereL(wit));
                }
                this.getTableAliasWhereL(wit);
            }
            final Vector newFromItemListWithOuter = new Vector();
            final StringBuffer outerTableNames = new StringBuffer();
            boolean isOuterSet = false;
            for (int j = 0; j < fromItemList.size(); ++j) {
                FromTable fromTable = null;
                if (fromItemList.get(j) instanceof FromTable) {
                    fromTable = fromItemList.get(j);
                }
                if (outerJoinTableItems != null && fromTable != null && (outerJoinTableItems.contains(fromTable.getAliasName()) || outerJoinTableItems.contains(fromTable.getTableName().toString()))) {
                    if (!isOuterSet) {
                        outerTableNames.append("OUTER (");
                    }
                    fromChanged = true;
                    for (int k = 0; k < outerJoinTableItems.size(); ++k) {
                        if (fromTable.getAliasName() != null && fromTable.getAliasName().equalsIgnoreCase(outerJoinTableItems.get(k))) {
                            if (isOuterSet) {
                                outerTableNames.append(", ");
                            }
                            else {
                                isOuterSet = true;
                            }
                            outerTableNames.append(fromTable.getTableName().toString() + " ");
                            outerTableNames.append(fromTable.getAliasName());
                        }
                        else if (outerJoinTableItems.get(k).equalsIgnoreCase(fromTable.getTableName().toString())) {
                            if (isOuterSet) {
                                outerTableNames.append(", ");
                            }
                            else {
                                isOuterSet = true;
                            }
                            outerTableNames.append(fromTable.getTableName().toString() + " ");
                        }
                    }
                }
                else {
                    newFromItemListWithOuter.add(fromTable);
                }
            }
            if (isOuterSet) {
                outerTableNames.append(") ");
                final FromTable outerFromTable = new FromTable();
                outerFromTable.setTableName(outerTableNames.toString());
                newFromItemListWithOuter.insertElementAt(outerFromTable, 0);
            }
            newFromClause.setFromItemList(newFromItemListWithOuter);
            if (fromChanged) {
                to_sqs.setFromClause(newFromClause);
            }
        }
    }
    
    private void moveOuterWhereItemsAsANSIJoins(final Hashtable whereItemList, final Vector whereItemsToBeMovedToFromClause, final Vector outerTableNames) {
        final int getsize = whereItemsToBeMovedToFromClause.size();
        int count = 0;
        if (outerTableNames != null) {
            for (int j = 0; j < getsize; ++j) {
                final WhereItem whereItem = whereItemsToBeMovedToFromClause.get(j - count);
                final WhereColumn leftWhereExp = whereItem.getLeftWhereExp();
                final WhereColumn rightWhereExp = whereItem.getRightWhereExp();
                String tableOrAliasNameForLeftExp = new String("");
                String tableOrAliasNameForRightExp = new String("");
                if (leftWhereExp != null && leftWhereExp.getColumnExpression() != null && rightWhereExp != null && rightWhereExp.getColumnExpression() != null) {
                    if (leftWhereExp.getColumnExpression().get(0) instanceof TableColumn) {
                        TableColumn tableColumn = new TableColumn();
                        tableColumn = leftWhereExp.getColumnExpression().get(0);
                        if (tableColumn.getOwnerName() != null) {
                            tableOrAliasNameForLeftExp = tableColumn.getOwnerName() + "." + tableColumn.getTableName();
                        }
                        else {
                            tableOrAliasNameForLeftExp = tableColumn.getTableName();
                        }
                    }
                    if (leftWhereExp.getColumnExpression().get(0) instanceof String) {
                        final String tableAliaswhere = leftWhereExp.getColumnExpression().get(0);
                        if (tableAliaswhere.indexOf(".") != -1) {
                            tableOrAliasNameForLeftExp = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                        }
                    }
                    if (rightWhereExp.getColumnExpression().get(0) instanceof TableColumn) {
                        TableColumn tableColumn = new TableColumn();
                        tableColumn = rightWhereExp.getColumnExpression().get(0);
                        if (tableColumn.getOwnerName() != null) {
                            tableOrAliasNameForRightExp = tableColumn.getOwnerName() + "." + tableColumn.getTableName();
                        }
                        else {
                            tableOrAliasNameForRightExp = tableColumn.getTableName();
                        }
                    }
                    if (rightWhereExp.getColumnExpression().get(0) instanceof String) {
                        final String tableAliaswhere = rightWhereExp.getColumnExpression().get(0);
                        if (tableAliaswhere.indexOf(".") != -1) {
                            tableOrAliasNameForRightExp = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                        }
                    }
                    if (!outerTableNames.contains(tableOrAliasNameForRightExp)) {
                        if (!outerTableNames.contains(tableOrAliasNameForLeftExp)) {
                            final String obj = "" + j;
                            whereItemList.put(obj, whereItemsToBeMovedToFromClause.get(j - count));
                            whereItemsToBeMovedToFromClause.removeElementAt(j - count);
                            ++count;
                        }
                    }
                }
                else {
                    final String obj = "" + j;
                    whereItemList.put(obj, whereItemsToBeMovedToFromClause.get(j - count));
                    whereItemsToBeMovedToFromClause.removeElementAt(j - count);
                    ++count;
                }
            }
        }
        else {
            for (int i = 0; i < getsize; ++i) {
                final String obj2 = "" + i;
                whereItemList.put(obj2, whereItemsToBeMovedToFromClause.get(i - count));
                whereItemsToBeMovedToFromClause.removeElementAt(i - count);
                ++count;
            }
        }
    }
    
    private Vector putObjectsInOneList(final Vector columnList) {
        final Vector returnObjects = new Vector();
        if (columnList != null) {
            for (int i = 0; i < columnList.size(); ++i) {
                if (columnList.get(i) instanceof SelectColumn) {
                    final SelectColumn sc = columnList.get(i);
                    final Vector selectList = this.putObjectsInOneList(sc.getColumnExpression());
                    returnObjects.addAll(selectList);
                }
                else if (columnList.get(i) instanceof FunctionCalls) {
                    final FunctionCalls fc = columnList.get(i);
                    final Vector functionsList = this.putObjectsInOneList(fc.getFunctionArguments());
                    returnObjects.addAll(functionsList);
                }
                else {
                    returnObjects.add(columnList.get(i));
                }
            }
        }
        return returnObjects;
    }
    
    private String getTableAliasName(final WhereColumn wc) {
        TableColumn tc = new TableColumn();
        String aliasname = "";
        if (wc != null && wc.getColumnExpression() != null) {
            if (wc.getColumnExpression().get(0) instanceof TableColumn) {
                tc = wc.getColumnExpression().get(0);
                if (tc.getTableName() == null && tc.getColumnName().trim().equals("?")) {
                    final String tableAliaswhere = tc.getColumnName();
                    aliasname = "";
                }
                else if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                    aliasname = tc.getOwnerName() + "." + tc.getTableName();
                }
                else {
                    aliasname = tc.getTableName();
                }
            }
            if (wc.getColumnExpression().get(0) instanceof FunctionCalls) {
                final FunctionCalls fc = wc.getColumnExpression().get(0);
                final Vector functionObjects = this.putObjectsInOneList(fc.getFunctionArguments());
                if (functionObjects.size() > 0) {
                    if (functionObjects.get(0) instanceof TableColumn) {
                        tc = functionObjects.get(0);
                        if (tc.getTableName() == null || tc.getColumnName().trim().equals("?")) {
                            final String tableAliaswhere2 = tc.getColumnName();
                            aliasname = "";
                        }
                        else if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                            aliasname = tc.getOwnerName() + "." + tc.getTableName();
                        }
                        else {
                            aliasname = tc.getTableName();
                        }
                    }
                    else if (functionObjects.get(0) instanceof SelectColumn) {
                        final SelectColumn sc = functionObjects.get(0);
                        if (sc.getColumnExpression() != null) {
                            for (int index = 0; index < sc.getColumnExpression().size(); ++index) {
                                if (sc.getColumnExpression().get(index) instanceof TableColumn) {
                                    tc = sc.getColumnExpression().get(index);
                                    if (tc.getTableName() == null || tc.getColumnName().trim().equals("?")) {
                                        final String tableAliaswhere3 = tc.getColumnName();
                                        aliasname = "";
                                    }
                                    else if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                                        aliasname = tc.getOwnerName() + "." + tc.getTableName();
                                    }
                                    else {
                                        aliasname = tc.getTableName();
                                    }
                                }
                            }
                        }
                    }
                    else if (functionObjects.get(0) instanceof String) {
                        String tableAliaswhere2 = functionObjects.get(0);
                        if (tableAliaswhere2.indexOf(46) != -1) {
                            if (tableAliaswhere2.indexOf(46) != tableAliaswhere2.lastIndexOf(46)) {
                                if (SwisSQLOptions.removeDBSchemaQualifier) {
                                    tableAliaswhere2 = tableAliaswhere2.substring(tableAliaswhere2.indexOf(".") + 1);
                                }
                                aliasname = tableAliaswhere2.substring(0, tableAliaswhere2.lastIndexOf("."));
                            }
                            else {
                                aliasname = tableAliaswhere2.substring(0, tableAliaswhere2.indexOf("."));
                            }
                        }
                        else {
                            aliasname = "";
                        }
                    }
                    else if (functionObjects.get(0) instanceof NumericClass && functionObjects.size() == 2) {
                        if (functionObjects.get(1) instanceof TableColumn) {
                            final TableColumn tc2 = functionObjects.get(1);
                            aliasname = tc2.getTableName();
                        }
                        else if (functionObjects.get(1) instanceof String) {
                            String tableAliaswhere2 = functionObjects.get(1);
                            if (tableAliaswhere2.indexOf(46) != -1) {
                                if (tableAliaswhere2.indexOf(46) != tableAliaswhere2.lastIndexOf(46)) {
                                    if (SwisSQLOptions.removeDBSchemaQualifier) {
                                        tableAliaswhere2 = tableAliaswhere2.substring(tableAliaswhere2.indexOf(".") + 1);
                                    }
                                    aliasname = tableAliaswhere2.substring(0, tableAliaswhere2.lastIndexOf("."));
                                }
                                else {
                                    aliasname = tableAliaswhere2.substring(0, tableAliaswhere2.indexOf("."));
                                }
                            }
                            else {
                                aliasname = "";
                            }
                        }
                        else {
                            aliasname = "";
                        }
                    }
                }
                else if (tc.getOwnerName() != null && !SwisSQLOptions.removeDBSchemaQualifier) {
                    aliasname = tc.getOwnerName() + "." + tc.getTableName();
                }
                else {
                    aliasname = tc.getTableName();
                }
            }
            if (wc.getColumnExpression().get(0) instanceof CaseStatement) {
                final CaseStatement cs = wc.getColumnExpression().get(0);
                final String tableName = this.getTableAliasName(cs);
                if (tableName != null && !tableName.equals("")) {
                    aliasname = tableName;
                }
            }
            if (wc.getColumnExpression().get(0) instanceof String) {
                String tableAliaswhere = wc.getColumnExpression().get(0);
                if (tableAliaswhere.indexOf(46) != -1) {
                    if (tableAliaswhere.indexOf(46) != tableAliaswhere.lastIndexOf(46)) {
                        if (SwisSQLOptions.removeDBSchemaQualifier) {
                            tableAliaswhere = tableAliaswhere.substring(tableAliaswhere.indexOf(".") + 1);
                        }
                        aliasname = tableAliaswhere.substring(0, tableAliaswhere.lastIndexOf("."));
                    }
                    else {
                        aliasname = tableAliaswhere.substring(0, tableAliaswhere.indexOf("."));
                    }
                }
                else {
                    aliasname = "";
                }
            }
        }
        else {
            aliasname = "";
        }
        return aliasname;
    }
    
    private void addWhereExpToANSIJOIN(final SelectQueryStatement to_sqs, final WhereExpression we) {
        if (to_sqs != null && to_sqs.getFromClause() != null && to_sqs.getFromClause().getFromItemList() != null) {
            final Vector fromItems = to_sqs.getFromClause().getFromItemList();
            for (int i = 0; i < fromItems.size(); ++i) {
                if (fromItems.get(i) instanceof FromTable) {
                    final FromTable fromTable = fromItems.get(i);
                    if (fromTable != null && fromTable.getJoinExpression() != null) {
                        for (int j = 0; j < fromTable.getJoinExpression().size(); ++j) {
                            if (fromTable.getJoinExpression().get(j) instanceof WhereExpression) {
                                final WhereExpression whereExp = fromTable.getJoinExpression().get(j);
                                if (whereExp != null) {
                                    final Vector whereItems = whereExp.getWhereItems();
                                    for (int k = 0; k < whereItems.size(); ++k) {
                                        if (whereItems.get(k) instanceof WhereItem) {
                                            final WhereItem whereItem = whereItems.get(k);
                                            if (whereItem.getLeftWhereExp() != null && whereItem.getRightWhereExp() != null) {
                                                final WhereColumn leftColumn = whereItem.getLeftWhereExp();
                                                final WhereColumn rightColumn = whereItem.getRightWhereExp();
                                                final Vector to_sqsWhereItems = we.getWhereItems();
                                                final Vector we_Operators = we.getOperator();
                                                for (int l = 0; l < to_sqsWhereItems.size(); ++l) {
                                                    if (to_sqsWhereItems.get(l) instanceof WhereItem) {
                                                        final WhereItem to_sqsWhereItem = to_sqsWhereItems.get(l);
                                                        if (to_sqsWhereItem != null && to_sqsWhereItem.getLeftWhereExp() != null && to_sqsWhereItem.getRightWhereExp() == null) {
                                                            final WhereColumn to_sqlWhereColumn = to_sqsWhereItem.getLeftWhereExp();
                                                            if (leftColumn.toString().contentEquals(new StringBuffer(to_sqlWhereColumn.toString())) || rightColumn.toString().contentEquals(new StringBuffer(to_sqlWhereColumn.toString()))) {
                                                                if (l > 0) {
                                                                    if (we_Operators.get(l - 1).toString().equalsIgnoreCase("&AND")) {
                                                                        if (we_Operators.size() > 0) {
                                                                            fromItems.get(i).getJoinExpression().get(j).addOperator("AND");
                                                                        }
                                                                    }
                                                                    else if (we_Operators.size() > 0) {
                                                                        fromItems.get(i).getJoinExpression().get(j).addOperator(we_Operators.get(l - 1));
                                                                    }
                                                                }
                                                                else if (l == 0 && we_Operators.size() > 0) {
                                                                    fromItems.get(i).getJoinExpression().get(j).addOperator(we_Operators.get(l));
                                                                }
                                                                fromItems.get(i).getJoinExpression().get(j).addWhereItem(to_sqsWhereItem);
                                                                we.getWhereItems().removeElement(to_sqsWhereItems.get(l));
                                                                if (l == 0) {
                                                                    if (we.getOperator().size() > 0) {
                                                                        we.getOperator().removeElementAt(l);
                                                                    }
                                                                }
                                                                else if (l > 0 && we.getOperator().size() > 0) {
                                                                    we.getOperator().removeElementAt(l - 1);
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
            }
        }
    }
    
    public void replaceRownumTableColumn(final Object newColumn) throws ConvertException {
        for (int j = 0; j < this.whereItems.size(); ++j) {
            final Object wiObj = this.whereItems.get(j);
            if (wiObj != null) {
                if (wiObj instanceof WhereItem) {
                    ((WhereItem)wiObj).replaceRownumTableColumn(newColumn);
                }
                else if (wiObj instanceof WhereExpression) {
                    ((WhereExpression)wiObj).replaceRownumTableColumn(newColumn);
                }
            }
        }
    }
    
    private String getTableAliasName(final FunctionCalls fc) {
        final String aliasname = "";
        final FunctionCalls lclFnCl = fc;
        final Vector functionArguments = lclFnCl.getFunctionArguments();
        if (functionArguments != null) {
            for (int j = 0; j < functionArguments.size(); ++j) {
                if (functionArguments.get(j) instanceof SelectColumn) {
                    final String tableName = this.getTableAliasWhereR(functionArguments.get(j));
                    if (tableName != null && !tableName.equals("")) {
                        return tableName;
                    }
                }
            }
        }
        else if (functionArguments == null && lclFnCl instanceof decode) {
            final FunctionCalls fnc = fc;
            if (fnc.toString().trim().toLowerCase().startsWith("case") && fnc instanceof decode) {
                final CaseStatement cs = ((decode)fnc).getCaseStatement();
                final WhereItem cswi = cs.getCaseCondition().getWhereItem().get(0);
                final SelectColumn cssc = cswi.getLeftWhereExp().getColumnExpression().get(0);
                final String tableName2 = this.getTableAliasWhereR(cssc);
                if (tableName2 != null && !tableName2.equals("")) {
                    return tableName2;
                }
            }
        }
        return aliasname;
    }
    
    private String getTableAliasName(final CaseStatement cs) {
        final String aliasname = "";
        WhereItem cswi = null;
        if (cs.getCaseCondition() != null) {
            final Object wiObj = cs.getCaseCondition().getWhereItem().get(0);
            if (wiObj instanceof WhereItem) {
                cswi = (WhereItem)wiObj;
            }
            else if (wiObj instanceof WhereExpression) {
                cswi = ((WhereExpression)wiObj).getWhereItems().get(0);
            }
        }
        else {
            final Object wiObj = cs.getWhenClauseList().get(0).getWhenCondition().getWhereItem().get(0);
            if (wiObj instanceof WhereItem) {
                cswi = (WhereItem)wiObj;
            }
            else if (wiObj instanceof WhereExpression) {
                if (((WhereExpression)wiObj).getWhereItems().get(0) instanceof WhereItem) {
                    cswi = ((WhereExpression)wiObj).getWhereItems().get(0);
                }
                else if (((WhereExpression)wiObj).getWhereItems().get(0) instanceof WhereExpression) {
                    cswi = ((WhereExpression)wiObj).getWhereItems().get(0).getWhereItems().get(0);
                }
            }
        }
        if (cswi != null) {
            final Object cssc = cswi.getLeftWhereExp().getColumnExpression().get(0);
            if (cssc instanceof SelectColumn) {
                final String tableName = this.getTableAliasWhereR((SelectColumn)cssc);
                if (tableName != null && !tableName.equals("")) {
                    return tableName;
                }
            }
            else if (cssc instanceof TableColumn) {
                final TableColumn cscol = (TableColumn)cssc;
                if (cscol.getOwnerName() != null) {
                    return cscol.getOwnerName() + "." + cscol.getTableName();
                }
                return cscol.getTableName();
            }
            else if (cssc instanceof FunctionCalls) {
                final FunctionCalls fc = (FunctionCalls)cssc;
                final String tableName2 = this.getTableAliasName(fc);
                if (tableName2 != null && !tableName2.equals("")) {
                    return tableName2;
                }
            }
        }
        return aliasname;
    }
    
    public WhereExpression toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (this.rownumClause != null && to_sqs != null) {
            this.addLimitClause(to_sqs, from_sqs);
        }
        final WhereExpression we = new WhereExpression();
        we.setOpenBrace(this.openBraces);
        we.setCloseBrace(this.closeBraces);
        this.setTargetDatabase(13);
        if (this.beginOperator != null) {
            we.setBeginOperator(this.beginOperator);
        }
        if (this.operators != null) {
            final Vector newOperators = new Vector();
            for (int i = 0; i < this.operators.size(); ++i) {
                final String op = this.operators.elementAt(i);
                newOperators.addElement(op);
            }
            we.setOperator(newOperators);
        }
        final Vector whereItemList = new Vector();
        for (int i = 0; i < this.whereItems.size(); ++i) {
            if (this.whereItems.elementAt(i) instanceof WhereItem) {
                if (this.whereItems.elementAt(i) == null) {
                    whereItemList.addElement(null);
                }
                final WhereItem wi = this.whereItems.elementAt(i);
                if (wi.getLeftJoin() == null && wi.getRightJoin() == null) {
                    if (this.isWhereItemEqualsClauseIsMultipleAndNotSubquery(wi)) {
                        final Vector whereItemsListReplacingEqualsClause = wi.getWhereItemsReplacingEqualsClause();
                        for (int j = 0; j < whereItemsListReplacingEqualsClause.size(); ++j) {
                            whereItemList.addElement(whereItemsListReplacingEqualsClause.get(j).toVectorWiseSelect(to_sqs, from_sqs));
                            if (j != whereItemsListReplacingEqualsClause.size() - 1) {
                                if (wi.getOperator2().equalsIgnoreCase("ALL")) {
                                    we.getOperator().add("AND");
                                }
                                else {
                                    we.getOperator().add("OR");
                                }
                            }
                        }
                    }
                    else {
                        final WhereItem wiNew = wi.toVectorWiseSelect(to_sqs, from_sqs);
                        if (wiNew.isNullSafeEqualsOperator()) {
                            final WhereExpression we2 = new WhereExpression();
                            we2.setOpenBrace("(");
                            we2.setCloseBrace(")");
                            final WhereItem wi2 = new WhereItem();
                            wi2.setLeftWhereExp(wiNew.getLeftWhereExp());
                            wi2.setOperator("IS NULL");
                            final WhereItem wi3 = new WhereItem();
                            wi3.setLeftWhereExp(wiNew.getRightWhereExp());
                            wi3.setOperator("IS NULL");
                            final WhereExpression we3 = new WhereExpression();
                            we3.setOpenBrace("(");
                            we3.setCloseBrace(")");
                            final Vector wiV = new Vector();
                            wiV.add(wi2);
                            wiV.add(wi3);
                            final Vector opV = new Vector();
                            opV.add("AND");
                            we3.setWhereItem(wiV);
                            we3.setOperator(opV);
                            final Vector wiV2 = new Vector();
                            wiV2.add(wiNew);
                            wiV2.add(we3);
                            final Vector opV2 = new Vector();
                            opV2.add("OR");
                            we2.setWhereItem(wiV2);
                            we2.setOperator(opV2);
                            whereItemList.addElement(we2);
                        }
                        else {
                            whereItemList.addElement(wiNew);
                        }
                    }
                    if (wi.getRownumClause() != null) {
                        if (i != 0) {
                            final String op2 = we.getOperator().get(i - 1);
                            if (!op2.equals("&AND")) {
                                we.getOperator().setElementAt("&AND", i - 1);
                            }
                            else if (we.getOperator().size() > i) {
                                we.getOperator().setElementAt("&AND", i);
                            }
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                }
                else {
                    whereItemList.addElement(null);
                    if (i != 0) {
                        final String op2 = we.getOperator().get(i - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", i - 1);
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                    else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                    }
                }
            }
            else if (this.whereItems.elementAt(i) instanceof WhereExpression) {
                final WhereExpression internalWE = this.whereItems.elementAt(i).toVectorWiseSelect(to_sqs, from_sqs);
                whereItemList.addElement(internalWE);
                if (internalWE.toString() == null) {
                    if (i != 0) {
                        final String op2 = we.getOperator().get(i - 1);
                        if (!op2.equals("&AND")) {
                            we.getOperator().setElementAt("&AND", i - 1);
                        }
                        else if (we.getOperator().size() > i) {
                            we.getOperator().setElementAt("&AND", i);
                        }
                    }
                    else if (we.getOperator().size() > i) {
                        we.getOperator().setElementAt("&AND", i);
                    }
                }
            }
        }
        we.setWhereItem(whereItemList);
        we.setCheckWhere(this.checkWhere);
        we.setRownumClause(this.rownumClause);
        if (this.topLevel) {
            this.setNewFromClauseWithANSIJoin(to_sqs, from_sqs, 13, we);
        }
        return we;
    }
}
