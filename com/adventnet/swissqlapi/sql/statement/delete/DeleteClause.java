package com.adventnet.swissqlapi.sql.statement.delete;

import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.update.HintClause;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;

public class DeleteClause
{
    private String delete_;
    private OptionalSpecifier optionalSpecifier;
    private HintClause hint;
    private CommentClass commentObj;
    
    public DeleteClause() {
        this.delete_ = new String();
        this.optionalSpecifier = null;
        this.hint = new HintClause();
    }
    
    public void setDelete(final String s) {
        this.delete_ = s;
    }
    
    public String getDelete() {
        return this.delete_;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public void setOptionalSpecifier(final OptionalSpecifier s) {
        this.optionalSpecifier = s;
    }
    
    public OptionalSpecifier getOptionalSpecifier() {
        return this.optionalSpecifier;
    }
    
    public void setHintClause(final HintClause hintclause) {
        this.hint = hintclause;
    }
    
    public HintClause getHintClause() {
        return this.hint;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(this.delete_.toUpperCase() + " ");
        if (this.commentObj != null) {
            stringbuffer.append(this.commentObj.toString().trim() + " ");
        }
        if (this.optionalSpecifier != null) {
            stringbuffer.append(this.optionalSpecifier.toString() + " ");
        }
        if (this.hint != null && this.hint.toString() != null) {
            stringbuffer.append(this.hint.toString() + " ");
        }
        return stringbuffer.toString();
    }
    
    private void toGeneric() {
        this.optionalSpecifier = null;
    }
    
    public void toDB2() {
        this.setCommentClass(null);
        (this.optionalSpecifier = new OptionalSpecifier()).setFrom("FROM");
    }
    
    public void toOracle() {
        if (this.optionalSpecifier != null) {
            if (this.optionalSpecifier.getFrom() != null) {
                (this.optionalSpecifier = new OptionalSpecifier()).setFrom("FROM");
            }
            else {
                this.optionalSpecifier = null;
            }
        }
    }
    
    public void toANSISQL() {
        this.setCommentClass(null);
        if (this.optionalSpecifier != null) {
            if (this.optionalSpecifier.getFrom() != null) {
                (this.optionalSpecifier = new OptionalSpecifier()).setFrom("FROM");
            }
            else {
                this.optionalSpecifier = null;
            }
        }
        this.hint = null;
    }
    
    public void toSQLServer() {
        this.setCommentClass(null);
        if (this.optionalSpecifier != null) {
            if (this.optionalSpecifier.getFrom() != null) {
                (this.optionalSpecifier = new OptionalSpecifier()).setFrom("FROM");
            }
            else {
                this.optionalSpecifier = null;
            }
        }
    }
    
    public void toSybase() {
        this.setCommentClass(null);
        if (this.optionalSpecifier != null) {
            if (this.optionalSpecifier.getFrom() != null) {
                (this.optionalSpecifier = new OptionalSpecifier()).setFrom("FROM");
            }
            else {
                this.optionalSpecifier = null;
            }
        }
    }
    
    public void toMySQL() {
        this.setCommentClass(null);
        if (this.optionalSpecifier != null) {
            this.optionalSpecifier.toMySQL();
        }
        this.optionalSpecifier.setFrom("FROM");
        this.hint = null;
    }
    
    public void toPostgreSQL() {
        this.setCommentClass(null);
        if (this.optionalSpecifier != null) {
            this.optionalSpecifier.toPostgreSQL();
            if (this.optionalSpecifier.getFrom() == null) {
                this.optionalSpecifier.setFrom("FROM");
            }
        }
        else {
            (this.optionalSpecifier = new OptionalSpecifier()).setFrom("FROM");
        }
        this.hint = null;
    }
    
    public void toInformix() {
        this.setCommentClass(null);
        (this.optionalSpecifier = new OptionalSpecifier()).setFrom("FROM");
    }
    
    public void toTimesTen() {
        this.setCommentClass(null);
        (this.optionalSpecifier = new OptionalSpecifier()).setFrom("FROM");
    }
    
    public void toNetezza() {
        this.setCommentClass(null);
        if (this.optionalSpecifier != null) {
            if (this.optionalSpecifier.getFrom() != null) {
                (this.optionalSpecifier = new OptionalSpecifier()).setFrom("FROM");
            }
            else {
                this.optionalSpecifier = null;
            }
        }
        this.hint = null;
    }
    
    public void toTeradata() {
        this.setCommentClass(null);
        if (this.optionalSpecifier != null) {
            if (this.optionalSpecifier.getFrom() != null) {
                (this.optionalSpecifier = new OptionalSpecifier()).setFrom("FROM");
            }
            else {
                this.optionalSpecifier = null;
            }
        }
        this.hint = null;
    }
}
