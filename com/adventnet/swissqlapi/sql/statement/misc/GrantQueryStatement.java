package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import java.util.Vector;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class GrantQueryStatement implements SwisSQLStatement
{
    private String grant;
    private String allPrivileges;
    private Vector grantStatementVector;
    private String toStr;
    private Vector securityAccountVector;
    private Vector permissionVector;
    private Vector columnNamesVector;
    private String onString;
    private TableObject tableOrView;
    private String withGrantOption;
    private String asString;
    private String groupOrRole;
    private String columnOpenBraces;
    private String columnClosedBraces;
    private CommentClass commentObject;
    private UserObjectContext context;
    private Vector systemPrivilegeVector;
    private Vector newGrantStmtsVector;
    
    public GrantQueryStatement() {
        this.context = null;
        this.newGrantStmtsVector = new Vector();
    }
    
    public void setGrant(final String grant) {
        this.grant = grant;
    }
    
    public void setAllPrivileges(final String allPrivileges) {
        this.allPrivileges = allPrivileges;
    }
    
    public void setGrantStatementVector(final Vector grantStatementVector) {
        this.grantStatementVector = grantStatementVector;
    }
    
    public void setTo(final String toStr) {
        this.toStr = toStr;
    }
    
    public void setSecurityAccountVector(final Vector securityAccountVector) {
        this.securityAccountVector = securityAccountVector;
    }
    
    public void setPermissionVector(final Vector permissionVector) {
        this.permissionVector = permissionVector;
    }
    
    public void setColumnNamesVector(final Vector columnNamesVector) {
        this.columnNamesVector = columnNamesVector;
    }
    
    @Override
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    public void setOn(final String onString) {
        this.onString = onString;
    }
    
    public void setTableOrView(final TableObject tableOrView) {
        this.tableOrView = tableOrView;
    }
    
    public void setWithGrantOption(final String withGrantOption) {
        this.withGrantOption = withGrantOption;
    }
    
    public void setAs(final String asString) {
        this.asString = asString;
    }
    
    public void setGroupOrRole(final String groupOrRole) {
        this.groupOrRole = groupOrRole;
    }
    
    public void setColumnOpenBraces(final String columnOpenBraces) {
        this.columnOpenBraces = columnOpenBraces;
    }
    
    public void setColumnClosedBraces(final String columnClosedBraces) {
        this.columnClosedBraces = columnClosedBraces;
    }
    
    public void setCommentClause(final CommentClass commentObject) {
        this.commentObject = commentObject;
    }
    
    public void setSystemPrivilegeVector(final Vector systemPrivVector) {
        this.systemPrivilegeVector = systemPrivVector;
    }
    
    public String getGrant() {
        return this.grant;
    }
    
    public String getAllPrivileges() {
        return this.allPrivileges;
    }
    
    public Vector getGrantStatementVector() {
        return this.grantStatementVector;
    }
    
    public String getTo() {
        return this.toStr;
    }
    
    public Vector getSecurityAccountVector() {
        return this.securityAccountVector;
    }
    
    public Vector getPermissionVector() {
        return this.permissionVector;
    }
    
    public Vector getColumnNamesVector() {
        return this.columnNamesVector;
    }
    
    public String getOn() {
        return this.onString;
    }
    
    public TableObject getTableOrView() {
        return this.tableOrView;
    }
    
    public String getWithGrantOption() {
        return this.withGrantOption;
    }
    
    public String getAs() {
        return this.asString;
    }
    
    public String getGroupOrRole() {
        return this.groupOrRole;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return null;
    }
    
    @Override
    public UserObjectContext getObjectContext() {
        return null;
    }
    
    public Vector getSystemPrivilegeVector() {
        return this.systemPrivilegeVector;
    }
    
    public Vector getNewGrantStmtsVector() {
        return this.newGrantStmtsVector;
    }
    
    @Override
    public String removeIndent(String str) {
        str = str.replace('\n', ' ');
        str = str.replace('\t', ' ');
        return str;
    }
    
    @Override
    public void setCommentClass(final CommentClass commentClass) {
    }
    
    public GrantQueryStatement toANSIGrant() throws ConvertException {
        final GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
        if (this.commentObject != null) {
            grantQueryStmt.setCommentClause(this.commentObject);
        }
        if (this.grant != null) {
            grantQueryStmt.setGrant(this.grant);
        }
        if (this.allPrivileges != null) {
            grantQueryStmt.setAllPrivileges(this.allPrivileges);
        }
        if (this.grantStatementVector != null) {
            grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
        }
        if (this.toStr != null) {
            grantQueryStmt.setTo(this.toStr);
        }
        if (this.securityAccountVector != null) {
            final Vector securityVector = new Vector();
            for (int i = 0; i < this.securityAccountVector.size(); ++i) {
                String securityString = this.securityAccountVector.get(i);
                if (securityString.startsWith("[")) {
                    securityString = securityString.substring(1, securityString.length() - 1);
                }
                securityVector.addElement(securityString);
            }
            grantQueryStmt.setSecurityAccountVector(securityVector);
        }
        if (this.permissionVector != null) {
            grantQueryStmt.setPermissionVector(this.permissionVector);
        }
        if (this.columnNamesVector != null) {
            grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
        }
        if (this.onString != null) {
            grantQueryStmt.setOn(this.onString);
        }
        if (this.tableOrView != null) {
            this.tableOrView.toANSISQL();
            grantQueryStmt.setTableOrView(this.tableOrView);
        }
        if (this.withGrantOption != null) {
            grantQueryStmt.setWithGrantOption(this.withGrantOption);
        }
        grantQueryStmt.setAs(null);
        grantQueryStmt.setGroupOrRole(null);
        return grantQueryStmt;
    }
    
    public GrantQueryStatement toTeradataGrant() throws ConvertException {
        final GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
        if (this.commentObject != null) {
            grantQueryStmt.setCommentClause(this.commentObject);
        }
        if (this.grant != null) {
            grantQueryStmt.setGrant(this.grant);
        }
        if (this.allPrivileges != null) {
            grantQueryStmt.setAllPrivileges(this.allPrivileges);
        }
        if (this.grantStatementVector != null) {
            grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
        }
        if (this.toStr != null) {
            grantQueryStmt.setTo(this.toStr);
        }
        if (this.securityAccountVector != null) {
            final Vector securityVector = new Vector();
            for (int i = 0; i < this.securityAccountVector.size(); ++i) {
                String securityString = this.securityAccountVector.get(i);
                if (securityString.startsWith("[")) {
                    securityString = securityString.substring(1, securityString.length() - 1);
                }
                securityVector.addElement(securityString);
            }
            grantQueryStmt.setSecurityAccountVector(securityVector);
        }
        if (this.permissionVector != null) {
            grantQueryStmt.setPermissionVector(this.permissionVector);
        }
        if (this.columnNamesVector != null) {
            grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
        }
        if (this.onString != null) {
            grantQueryStmt.setOn(this.onString);
        }
        if (this.tableOrView != null) {
            this.tableOrView.toTeradata();
            grantQueryStmt.setTableOrView(this.tableOrView);
        }
        if (this.withGrantOption != null) {
            grantQueryStmt.setWithGrantOption(this.withGrantOption);
        }
        grantQueryStmt.setAs(null);
        grantQueryStmt.setGroupOrRole(null);
        return grantQueryStmt;
    }
    
    public GrantQueryStatement toDB2Grant() throws ConvertException {
        final GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
        if (this.commentObject != null) {
            grantQueryStmt.setCommentClause(this.commentObject);
        }
        if (this.grant != null) {
            grantQueryStmt.setGrant(this.grant);
        }
        if (this.allPrivileges != null) {
            grantQueryStmt.setAllPrivileges(this.allPrivileges);
        }
        if (this.grantStatementVector != null) {
            grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
        }
        if (this.toStr != null) {
            grantQueryStmt.setTo(this.toStr);
        }
        if (this.securityAccountVector != null) {
            final Vector securityVector = new Vector();
            for (int i = 0; i < this.securityAccountVector.size(); ++i) {
                String securityString = this.securityAccountVector.get(i);
                if (securityString.startsWith("[")) {
                    securityString = securityString.substring(1, securityString.length() - 1);
                }
                securityVector.addElement(securityString);
            }
            grantQueryStmt.setSecurityAccountVector(securityVector);
        }
        if (this.permissionVector != null) {
            grantQueryStmt.setPermissionVector(this.permissionVector);
        }
        if (this.columnNamesVector != null) {
            grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
        }
        if (this.onString != null) {
            grantQueryStmt.setOn(this.onString);
        }
        if (this.tableOrView != null) {
            this.tableOrView.toDB2();
            grantQueryStmt.setTableOrView(this.tableOrView);
        }
        if (this.withGrantOption != null) {
            grantQueryStmt.setWithGrantOption(this.withGrantOption);
        }
        grantQueryStmt.setAs(null);
        grantQueryStmt.setGroupOrRole(null);
        return grantQueryStmt;
    }
    
    public GrantQueryStatement toInformixGrant() throws ConvertException {
        final GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
        if (this.commentObject != null) {
            grantQueryStmt.setCommentClause(this.commentObject);
        }
        if (this.grant != null) {
            grantQueryStmt.setGrant(this.grant);
        }
        if (this.allPrivileges != null) {
            grantQueryStmt.setAllPrivileges(this.allPrivileges);
        }
        if (this.grantStatementVector != null) {
            grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
        }
        if (this.toStr != null) {
            grantQueryStmt.setTo(this.toStr);
        }
        if (this.securityAccountVector != null) {
            final Vector securityVector = new Vector();
            for (int i = 0; i < this.securityAccountVector.size(); ++i) {
                String securityString = this.securityAccountVector.get(i);
                if (securityString.startsWith("[")) {
                    securityString = securityString.substring(1, securityString.length() - 1);
                }
                securityVector.addElement(securityString);
            }
            grantQueryStmt.setSecurityAccountVector(securityVector);
        }
        if (this.permissionVector != null) {
            grantQueryStmt.setPermissionVector(this.permissionVector);
        }
        if (this.columnNamesVector != null) {
            grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
        }
        if (this.onString != null) {
            grantQueryStmt.setOn(this.onString);
        }
        if (this.tableOrView != null) {
            this.tableOrView.toInformix();
            grantQueryStmt.setTableOrView(this.tableOrView);
        }
        if (this.withGrantOption != null) {
            grantQueryStmt.setWithGrantOption(this.withGrantOption);
        }
        grantQueryStmt.setAs(null);
        grantQueryStmt.setGroupOrRole(null);
        return grantQueryStmt;
    }
    
    public GrantQueryStatement toMSSQLServerGrant() throws ConvertException {
        final GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
        if (this.commentObject != null) {
            grantQueryStmt.setCommentClause(this.commentObject);
        }
        if (this.grant != null) {
            grantQueryStmt.setGrant(this.grant);
        }
        if (this.allPrivileges != null) {
            grantQueryStmt.setAllPrivileges(this.allPrivileges);
        }
        if (this.grantStatementVector != null) {
            grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
        }
        if (this.toStr != null) {
            grantQueryStmt.setTo(this.toStr);
        }
        if (this.securityAccountVector != null) {
            final Vector securityVector = new Vector();
            for (int i = 0; i < this.securityAccountVector.size(); ++i) {
                String securityString = this.securityAccountVector.get(i);
                if (securityString.startsWith("[")) {
                    securityString = securityString.substring(1, securityString.length() - 1);
                }
                securityVector.addElement(securityString);
            }
            grantQueryStmt.setSecurityAccountVector(securityVector);
        }
        if (this.permissionVector != null) {
            grantQueryStmt.setPermissionVector(this.permissionVector);
        }
        if (this.columnNamesVector != null) {
            grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
        }
        if (this.onString != null) {
            grantQueryStmt.setOn(this.onString);
        }
        if (this.tableOrView != null) {
            this.tableOrView.toMSSQLServer();
            grantQueryStmt.setTableOrView(this.tableOrView);
        }
        if (this.withGrantOption != null) {
            grantQueryStmt.setWithGrantOption(this.withGrantOption);
        }
        grantQueryStmt.setAs(null);
        grantQueryStmt.setGroupOrRole(null);
        return grantQueryStmt;
    }
    
    public GrantQueryStatement toMySQLGrant() throws ConvertException {
        final GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
        if (this.commentObject != null) {
            grantQueryStmt.setCommentClause(this.commentObject);
        }
        if (this.grant != null) {
            grantQueryStmt.setGrant(this.grant);
        }
        if (this.allPrivileges != null) {
            grantQueryStmt.setAllPrivileges(this.allPrivileges);
        }
        if (this.grantStatementVector != null) {
            grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
        }
        if (this.toStr != null) {
            grantQueryStmt.setTo(this.toStr);
        }
        if (this.securityAccountVector != null) {
            final Vector securityVector = new Vector();
            for (int i = 0; i < this.securityAccountVector.size(); ++i) {
                String securityString = this.securityAccountVector.get(i);
                if (securityString.startsWith("[")) {
                    securityString = securityString.substring(1, securityString.length() - 1);
                }
                securityVector.addElement(securityString);
            }
            grantQueryStmt.setSecurityAccountVector(securityVector);
        }
        if (this.permissionVector != null) {
            grantQueryStmt.setPermissionVector(this.permissionVector);
        }
        if (this.columnNamesVector != null) {
            grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
        }
        if (this.onString != null) {
            grantQueryStmt.setOn(this.onString);
        }
        if (this.tableOrView != null) {
            this.tableOrView.toMySQL();
            grantQueryStmt.setTableOrView(this.tableOrView);
        }
        if (this.withGrantOption != null) {
            grantQueryStmt.setWithGrantOption(this.withGrantOption);
        }
        grantQueryStmt.setAs(null);
        grantQueryStmt.setGroupOrRole(null);
        return grantQueryStmt;
    }
    
    public GrantQueryStatement toOracleGrant() throws ConvertException {
        final GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
        if (this.commentObject != null) {
            grantQueryStmt.setCommentClause(this.commentObject);
        }
        if (this.grant != null) {
            grantQueryStmt.setGrant(this.grant);
        }
        if (this.allPrivileges != null) {
            grantQueryStmt.setAllPrivileges(this.allPrivileges);
        }
        if (this.grantStatementVector != null) {
            grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
        }
        if (this.toStr != null) {
            grantQueryStmt.setTo(this.toStr);
        }
        if (this.securityAccountVector != null) {
            final Vector securityVector = new Vector();
            for (int i = 0; i < this.securityAccountVector.size(); ++i) {
                String securityString = this.securityAccountVector.get(i);
                if (securityString.startsWith("[")) {
                    securityString = securityString.substring(1, securityString.length() - 1);
                }
                securityVector.addElement(securityString);
            }
            grantQueryStmt.setSecurityAccountVector(securityVector);
        }
        if (this.permissionVector != null) {
            grantQueryStmt.setPermissionVector(this.permissionVector);
        }
        if (this.columnNamesVector != null) {
            grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
        }
        if (this.onString != null) {
            grantQueryStmt.setOn(this.onString);
        }
        if (this.tableOrView != null) {
            this.tableOrView.toOracle();
            grantQueryStmt.setTableOrView(this.tableOrView);
        }
        if (this.withGrantOption != null) {
            grantQueryStmt.setWithGrantOption(this.withGrantOption);
        }
        grantQueryStmt.setAs(null);
        grantQueryStmt.setGroupOrRole(null);
        return grantQueryStmt;
    }
    
    public GrantQueryStatement toPostgreSQLGrant() throws ConvertException {
        final GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
        if (this.commentObject != null) {
            grantQueryStmt.setCommentClause(this.commentObject);
        }
        if (this.grant != null) {
            grantQueryStmt.setGrant(this.grant);
        }
        if (this.allPrivileges != null) {
            grantQueryStmt.setAllPrivileges(this.allPrivileges);
        }
        if (this.grantStatementVector != null) {
            grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
        }
        if (this.toStr != null) {
            grantQueryStmt.setTo(this.toStr);
        }
        if (this.securityAccountVector != null) {
            final Vector securityVector = new Vector();
            for (int i = 0; i < this.securityAccountVector.size(); ++i) {
                String securityString = this.securityAccountVector.get(i);
                if (securityString.startsWith("[")) {
                    securityString = securityString.substring(1, securityString.length() - 1);
                }
                securityVector.addElement(securityString);
            }
            grantQueryStmt.setSecurityAccountVector(securityVector);
        }
        if (this.permissionVector != null) {
            grantQueryStmt.setPermissionVector(this.permissionVector);
        }
        if (this.columnNamesVector != null) {
            grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
        }
        if (this.onString != null) {
            grantQueryStmt.setOn(this.onString);
        }
        if (this.tableOrView != null) {
            this.tableOrView.toPostgreSQL();
            grantQueryStmt.setTableOrView(this.tableOrView);
        }
        if (this.withGrantOption != null) {
            grantQueryStmt.setWithGrantOption(this.withGrantOption);
        }
        grantQueryStmt.setAs(null);
        grantQueryStmt.setGroupOrRole(null);
        return grantQueryStmt;
    }
    
    public GrantQueryStatement toSybaseGrant() throws ConvertException {
        final GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
        grantQueryStmt.setObjectContext(this.context);
        if (this.commentObject != null) {
            grantQueryStmt.setCommentClause(this.commentObject);
        }
        if (this.grant != null) {
            grantQueryStmt.setGrant(this.grant);
        }
        if (this.allPrivileges != null) {
            grantQueryStmt.setAllPrivileges(this.allPrivileges);
        }
        if (this.grantStatementVector != null) {
            grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
        }
        if (this.toStr != null) {
            grantQueryStmt.setTo(this.toStr);
        }
        if (this.securityAccountVector != null) {
            final Vector securityVector = new Vector();
            for (int i = 0; i < this.securityAccountVector.size(); ++i) {
                String securityString = this.securityAccountVector.get(i);
                if (securityString.startsWith("[")) {
                    securityString = securityString.substring(1, securityString.length() - 1);
                }
                securityVector.addElement(securityString);
            }
            grantQueryStmt.setSecurityAccountVector(securityVector);
        }
        if (this.permissionVector != null) {
            grantQueryStmt.setPermissionVector(this.permissionVector);
        }
        if (this.columnNamesVector != null) {
            grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
        }
        if (this.onString != null) {
            grantQueryStmt.setOn(this.onString);
        }
        if (this.tableOrView != null) {
            this.tableOrView.toSybase();
            grantQueryStmt.setTableOrView(this.tableOrView);
        }
        if (this.withGrantOption != null) {
            grantQueryStmt.setWithGrantOption(this.withGrantOption);
        }
        grantQueryStmt.setAs(null);
        grantQueryStmt.setGroupOrRole(null);
        return grantQueryStmt;
    }
    
    public GrantQueryStatement toTimesTenGrant() throws ConvertException {
        final GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
        grantQueryStmt.setObjectContext(this.context);
        if (this.commentObject != null) {
            grantQueryStmt.setCommentClause(this.commentObject);
        }
        if (this.grant != null) {
            grantQueryStmt.setGrant(this.grant);
        }
        if (this.allPrivileges != null) {
            grantQueryStmt.setAllPrivileges(this.allPrivileges);
        }
        if (this.grantStatementVector != null) {
            grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
        }
        if (this.toStr != null) {
            grantQueryStmt.setTo(this.toStr);
        }
        if (this.securityAccountVector != null) {
            final Vector securityVector = new Vector();
            for (int i = 0; i < this.securityAccountVector.size(); ++i) {
                String securityString = this.securityAccountVector.get(i);
                if (securityString.startsWith("[")) {
                    securityString = securityString.substring(1, securityString.length() - 1);
                }
                securityVector.addElement(securityString);
            }
            grantQueryStmt.setSecurityAccountVector(securityVector);
        }
        if (this.permissionVector != null) {
            grantQueryStmt.setPermissionVector(this.permissionVector);
        }
        if (this.columnNamesVector != null) {
            grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
        }
        if (this.onString != null) {
            grantQueryStmt.setOn(null);
        }
        if (this.tableOrView != null) {
            grantQueryStmt.setTableOrView(null);
        }
        if (this.withGrantOption != null) {
            grantQueryStmt.setWithGrantOption(this.withGrantOption);
        }
        grantQueryStmt.setAs(null);
        grantQueryStmt.setGroupOrRole(null);
        return grantQueryStmt;
    }
    
    public GrantQueryStatement toNetezzaGrant() throws ConvertException {
        final GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
        if (this.commentObject != null) {
            grantQueryStmt.setCommentClause(this.commentObject);
        }
        if (this.grant != null) {
            grantQueryStmt.setGrant(this.grant);
        }
        if (this.allPrivileges != null) {
            grantQueryStmt.setAllPrivileges(this.allPrivileges);
        }
        if (this.grantStatementVector != null) {
            grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
        }
        if (this.toStr != null) {
            grantQueryStmt.setTo(this.toStr);
        }
        if (this.securityAccountVector != null) {
            final Vector securityVector = new Vector();
            for (int i = 0; i < this.securityAccountVector.size(); ++i) {
                String securityString = this.securityAccountVector.get(i);
                if (securityString.startsWith("[")) {
                    securityString = securityString.substring(1, securityString.length() - 1);
                }
                securityVector.addElement(securityString);
            }
            grantQueryStmt.setSecurityAccountVector(securityVector);
        }
        if (this.systemPrivilegeVector != null) {
            final Vector newSystemPrivilegeVector = new Vector();
            final Vector objectPrivVector = new Vector();
            for (int systemPrivilegeVectorSize = this.systemPrivilegeVector.size(), j = 0; j < systemPrivilegeVectorSize; ++j) {
                final String obj = this.systemPrivilegeVector.get(j).toString();
                if (obj.toUpperCase().indexOf("TABLE") != -1 || obj.toUpperCase().indexOf("VIEW") != -1 || obj.toUpperCase().indexOf("SEQUENCE") != -1 || obj.toUpperCase().indexOf("SYNONYM") != -1 || obj.toUpperCase().indexOf("USER") != -1) {
                    if (obj.toUpperCase().startsWith("CREATE")) {
                        final String[] objArr = obj.split(":swissql:");
                        if (objArr.length == 3) {
                            newSystemPrivilegeVector.add(objArr[0] + " " + objArr[2]);
                        }
                        else {
                            newSystemPrivilegeVector.add(objArr[0] + " " + objArr[1]);
                        }
                    }
                    else {
                        objectPrivVector.add(this.systemPrivilegeVector.get(j));
                    }
                }
            }
            if (newSystemPrivilegeVector.size() >= 1) {
                for (int objectPrivVectorSize = newSystemPrivilegeVector.size(), k = 0; k < objectPrivVectorSize; ++k) {
                    final GrantQueryStatement newObjPrivStmt = new GrantQueryStatement();
                    newObjPrivStmt.setGrant(this.grant);
                    final Vector objPrivPermissionVector = new Vector();
                    final String systemPriv = newSystemPrivilegeVector.get(k).toString();
                    objPrivPermissionVector.add(systemPriv);
                    newObjPrivStmt.setPermissionVector(objPrivPermissionVector);
                    newObjPrivStmt.setTo(this.toStr);
                    newObjPrivStmt.setSecurityAccountVector(this.securityAccountVector);
                    newObjPrivStmt.setWithGrantOption(this.withGrantOption);
                    grantQueryStmt.getNewGrantStmtsVector().add(newObjPrivStmt.toNetezzaGrant());
                }
            }
            if (objectPrivVector.size() >= 1) {
                for (int objectPrivVectorSize = objectPrivVector.size(), k = 0; k < objectPrivVectorSize; ++k) {
                    final GrantQueryStatement newObjPrivStmt = new GrantQueryStatement();
                    newObjPrivStmt.setGrant(this.grant);
                    final Vector objPrivPermissionVector = new Vector();
                    final String systemPriv = objectPrivVector.get(k).toString();
                    final String[] systemPrivArr = systemPriv.split(":swissql:");
                    objPrivPermissionVector.add(systemPrivArr[0]);
                    newObjPrivStmt.setPermissionVector(objPrivPermissionVector);
                    final TableObject tableObj = new TableObject();
                    if (systemPrivArr.length == 3) {
                        tableObj.setTableName(systemPrivArr[2]);
                    }
                    else {
                        tableObj.setTableName(systemPrivArr[1]);
                    }
                    newObjPrivStmt.setTableOrView(tableObj);
                    newObjPrivStmt.setOn("ON");
                    newObjPrivStmt.setTo(this.toStr);
                    newObjPrivStmt.setSecurityAccountVector(this.securityAccountVector);
                    newObjPrivStmt.setWithGrantOption(this.withGrantOption);
                    grantQueryStmt.getNewGrantStmtsVector().add(newObjPrivStmt.toNetezzaGrant());
                }
            }
        }
        if (this.permissionVector != null) {
            if (this.permissionVector.size() == 1) {
                grantQueryStmt.setPermissionVector(this.permissionVector);
            }
            else {
                final Vector newPermissionVector = new Vector();
                for (int permissionVectorSize = this.permissionVector.size(), l = 0; l < permissionVectorSize; ++l) {
                    final String obj2 = this.permissionVector.get(l).toString();
                    if (obj2.equalsIgnoreCase("DELETE") || obj2.equalsIgnoreCase("INSERT") || obj2.equalsIgnoreCase("SELECT") || obj2.equalsIgnoreCase("TRUNCATE") || obj2.equalsIgnoreCase("UPDATE") || obj2.equalsIgnoreCase("DROP") || obj2.equalsIgnoreCase("ALTER")) {
                        newPermissionVector.add(this.permissionVector.get(l));
                    }
                }
                grantQueryStmt.setPermissionVector(newPermissionVector);
            }
        }
        if (this.columnNamesVector != null) {
            grantQueryStmt.setColumnNamesVector(null);
        }
        if (this.onString != null) {
            grantQueryStmt.setOn(this.onString);
        }
        if (this.tableOrView != null) {
            final TableObject grantTableObject = this.tableOrView;
            String ownerName = grantTableObject.getOwner();
            String userName = grantTableObject.getUser();
            String tableName = grantTableObject.getTableName();
            if (ownerName != null && ((ownerName.startsWith("[") && ownerName.endsWith("]")) || (ownerName.startsWith("`") && ownerName.endsWith("`")))) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
                if (ownerName.indexOf(32) != -1) {
                    ownerName = "\"" + ownerName + "\"";
                }
            }
            if (userName != null && ((userName.startsWith("[") && userName.endsWith("]")) || (userName.startsWith("`") && userName.endsWith("`")))) {
                userName = userName.substring(1, userName.length() - 1);
                if (userName.indexOf(32) != -1) {
                    userName = "\"" + userName + "\"";
                }
            }
            if (tableName != null && ((tableName.startsWith("[") && tableName.endsWith("]")) || (tableName.startsWith("`") && tableName.endsWith("`")))) {
                tableName = tableName.substring(1, tableName.length() - 1);
                if (tableName.indexOf(32) != -1) {
                    tableName = "\"" + tableName + "\"";
                }
            }
            tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords(11), null, 11);
            grantTableObject.setOwner(ownerName);
            grantTableObject.setUser(userName);
            grantTableObject.setTableName(tableName);
            grantTableObject.toNetezza();
            grantQueryStmt.setTableOrView(grantTableObject);
        }
        if (this.withGrantOption != null) {
            grantQueryStmt.setWithGrantOption(this.withGrantOption = this.withGrantOption.replaceFirst("ADMIN", "GRANT"));
        }
        grantQueryStmt.setAs(null);
        grantQueryStmt.setGroupOrRole(null);
        return grantQueryStmt;
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        return this.toANSIGrant().toString();
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        return this.toTeradataGrant().toString();
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        return this.toDB2Grant().toString();
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        return this.toInformixGrant().toString();
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        return this.toMSSQLServerGrant().toString();
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        return this.toMySQLGrant().toString();
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        return this.toOracleGrant().toString();
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        return this.toPostgreSQLGrant().toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        return this.toSybaseGrant().toString();
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        return this.toTimesTenGrant().toString();
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        return this.toNetezzaGrant().toString();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String indentString = "\n";
        if (this.commentObject != null) {
            sb.append(this.commentObject.toString());
        }
        if (this.grant != null) {
            sb.append(indentString + this.grant.toUpperCase() + " ");
        }
        if (this.allPrivileges != null) {
            sb.append(" " + this.allPrivileges.toUpperCase());
        }
        if (this.grantStatementVector != null) {
            for (int i = 0; i < this.grantStatementVector.size(); ++i) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(this.grantStatementVector.get(i));
            }
        }
        if (this.permissionVector != null) {
            for (int i = 0; i < this.permissionVector.size(); ++i) {
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(this.permissionVector.get(i));
                if ((this.permissionVector.get(i).toString().equalsIgnoreCase("insert") || this.permissionVector.get(i).toString().equalsIgnoreCase("update") || this.permissionVector.get(i).toString().equalsIgnoreCase("delete") || this.permissionVector.get(i).toString().equalsIgnoreCase("references")) && this.columnNamesVector != null && this.columnNamesVector.elementAt(i) != null) {
                    sb.append("(");
                    sb.append(this.columnNamesVector.get(i));
                    sb.append(")");
                }
            }
        }
        if (this.onString != null) {
            sb.append(indentString + this.onString.toUpperCase());
        }
        if (this.tableOrView != null) {
            this.tableOrView.setObjectContext(this.context);
            sb.append(" " + this.tableOrView);
        }
        if (this.toStr != null) {
            sb.append(indentString + this.toStr.toUpperCase() + " ");
        }
        if (this.securityAccountVector != null) {
            for (int i = 0; i < this.securityAccountVector.size(); ++i) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(" " + this.securityAccountVector.get(i));
            }
        }
        if (this.withGrantOption != null) {
            sb.append(" " + this.withGrantOption.toUpperCase());
        }
        if (this.asString != null) {
            sb.append(" " + this.asString.toUpperCase());
        }
        if (this.groupOrRole != null) {
            sb.append(" " + this.groupOrRole);
        }
        if (this.newGrantStmtsVector != null && this.newGrantStmtsVector.size() >= 1) {
            final StringBuffer newSb = new StringBuffer();
            for (int newGrantStmtsVectorSize = this.newGrantStmtsVector.size(), j = 0; j < newGrantStmtsVectorSize; ++j) {
                newSb.append(this.newGrantStmtsVector.get(j).toString());
                if (j != newGrantStmtsVectorSize - 1) {
                    newSb.append(";");
                }
            }
            return newSb.toString();
        }
        return sb.toString();
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
