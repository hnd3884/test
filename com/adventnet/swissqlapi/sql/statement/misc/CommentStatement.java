package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;

public class CommentStatement implements SwisSQLStatement
{
    private UserObjectContext objectContext;
    private CommentClass commentClass;
    private String comment;
    private String on;
    private String commentType;
    private CommentTableObject commentObject;
    private String is;
    private String commentString;
    private String extendedProperty;
    
    public CommentStatement() {
        this.comment = null;
        this.on = null;
        this.commentType = null;
        this.commentObject = null;
        this.is = null;
        this.commentString = null;
        this.extendedProperty = null;
    }
    
    @Override
    public String toOracleString() throws ConvertException {
        final CommentStatement to_cs = new CommentStatement();
        if (this.objectContext != null) {
            to_cs.setObjectContext(this.objectContext);
        }
        if (this.commentClass != null) {
            to_cs.setCommentClass(this.commentClass);
        }
        if (this.comment != null) {
            to_cs.setComment(this.comment);
        }
        if (this.on != null) {
            to_cs.setOn(this.on);
        }
        if (this.commentType != null) {
            to_cs.setCommentType(this.commentType);
        }
        if (this.commentObject != null) {
            final CommentTableObject to_cto = this.commentObject.toOracleCommentObject();
            to_cs.setCommentObject(to_cto);
        }
        if (this.is != null) {
            to_cs.setIs(this.is);
        }
        if (this.commentString != null) {
            to_cs.setCommentString(this.commentString);
        }
        return to_cs.toString();
    }
    
    @Override
    public String toMSSQLServerString() throws ConvertException {
        final CommentStatement to_cs = new CommentStatement();
        if (this.comment != null) {
            to_cs.setComment("EXEC SP_ADDEXTENDEDPROPERTY");
        }
        if (this.commentObject != null) {
            final CommentTableObject commentObj = this.commentObject.toMSSQLServerCommentObject(this.commentType);
            if (this.commentString != null) {
                commentObj.addLevelTypeAndName("@VALUE", this.commentString);
                this.commentString = null;
            }
            to_cs.setCommentObject(commentObj);
        }
        return to_cs.toString();
    }
    
    @Override
    public String toSybaseString() throws ConvertException {
        throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
    }
    
    @Override
    public String toDB2String() throws ConvertException {
        throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
    }
    
    @Override
    public String toPostgreSQLString() throws ConvertException {
        throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
    }
    
    @Override
    public String toMySQLString() throws ConvertException {
        throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
    }
    
    @Override
    public String toANSIString() throws ConvertException {
        throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
    }
    
    @Override
    public String toInformixString() throws ConvertException {
        throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
    }
    
    @Override
    public String toTimesTenString() throws ConvertException {
        throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
    }
    
    @Override
    public String toNetezzaString() throws ConvertException {
        throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
    }
    
    @Override
    public String toTeradataString() throws ConvertException {
        throw new UnsupportedOperationException("Conversion Not Yet supported for this database dialect.");
    }
    
    @Override
    public String toString() {
        final StringBuffer commentStr = new StringBuffer("");
        if (this.getComment() != null) {
            commentStr.append(this.getComment());
        }
        if (this.getOn() != null) {
            commentStr.append(" ");
            commentStr.append(this.getOn());
        }
        if (this.getCommentType() != null) {
            commentStr.append(" ");
            commentStr.append(this.getCommentType());
        }
        if (this.getCommentObject() != null) {
            final String tabObject = this.getCommentObject().toString();
            commentStr.append(" ");
            commentStr.append(tabObject.trim());
        }
        if (this.getIs() != null) {
            commentStr.append(" ");
            commentStr.append(this.getIs());
        }
        if (this.getCommentString() != null) {
            commentStr.append(" ");
            commentStr.append(this.getCommentString().trim());
        }
        return commentStr.toString();
    }
    
    @Override
    public String removeIndent(final String formattedSqlString) {
        return formattedSqlString;
    }
    
    @Override
    public UserObjectContext getObjectContext() {
        return this.objectContext;
    }
    
    @Override
    public void setObjectContext(final UserObjectContext objectContext) {
        this.objectContext = objectContext;
    }
    
    @Override
    public CommentClass getCommentClass() {
        return this.commentClass;
    }
    
    @Override
    public void setCommentClass(final CommentClass commentClass) {
        this.commentClass = commentClass;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(final String comment) {
        this.comment = comment;
    }
    
    public String getOn() {
        return this.on;
    }
    
    public void setOn(final String on) {
        this.on = on;
    }
    
    public String getCommentType() {
        return this.commentType;
    }
    
    public void setCommentType(final String commentType) {
        this.commentType = commentType;
    }
    
    public CommentTableObject getCommentObject() {
        return this.commentObject;
    }
    
    public void setCommentObject(final CommentTableObject commentObject) {
        this.commentObject = commentObject;
    }
    
    public String getIs() {
        return this.is;
    }
    
    public void setIs(final String is) {
        this.is = is;
    }
    
    public String getCommentString() {
        return this.commentString;
    }
    
    public void setCommentString(final String commentString) {
        this.commentString = commentString;
    }
    
    public String getExtendedProperty() {
        return this.extendedProperty;
    }
    
    public void setExtendedProperty(final String extendedProperty) {
        this.extendedProperty = extendedProperty;
    }
    
    @Override
    public String toVectorWiseString() throws ConvertException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
