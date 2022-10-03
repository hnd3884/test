package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.statement.CommentClass;
import java.util.Vector;

public class ForUpdateStatement
{
    public String ForUpdateClause;
    public String ForUpdateQualifier;
    public Vector ForUpdateTableName;
    public String NoWaitQualifier;
    private CommentClass commentObj;
    
    public ForUpdateStatement() {
        this.ForUpdateClause = new String();
        this.ForUpdateQualifier = new String();
        this.ForUpdateTableName = new Vector();
        this.NoWaitQualifier = new String();
    }
    
    public void setForUpdateClause(final String s_fuc) {
        this.ForUpdateClause = s_fuc;
    }
    
    public void setForUpdateQualifier(final String s_fuq) {
        this.ForUpdateQualifier = s_fuq;
    }
    
    public void setForUpdateTableName(final Vector v_futn) {
        this.ForUpdateTableName = v_futn;
    }
    
    public void setNoWaitQualifier(final String s_nwq) {
        this.NoWaitQualifier = s_nwq;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public String getNoWaitQualifier() {
        return this.NoWaitQualifier;
    }
    
    public String getForUpdateQualifier() {
        return this.ForUpdateQualifier;
    }
    
    public Vector getForUpdateTableName() {
        return this.ForUpdateTableName;
    }
    
    public String getForUpdateClause() {
        return this.ForUpdateClause;
    }
    
    public ForUpdateStatement toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final ForUpdateStatement fus = new ForUpdateStatement();
        if (this.ForUpdateClause != null) {
            fus.setForUpdateClause(this.ForUpdateClause);
        }
        if (this.ForUpdateQualifier != null) {
            fus.setForUpdateQualifier(this.ForUpdateQualifier);
        }
        if (this.NoWaitQualifier != null) {
            fus.setNoWaitQualifier(this.NoWaitQualifier);
        }
        final Vector v_utn = new Vector();
        for (int i = 0; i < this.ForUpdateTableName.size(); ++i) {
            v_utn.addElement(this.ForUpdateTableName.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
        }
        fus.setForUpdateTableName(v_utn);
        return fus;
    }
    
    public ForUpdateStatement toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final ForUpdateStatement fus = new ForUpdateStatement();
        if (this.ForUpdateClause != null) {
            fus.setForUpdateClause(this.ForUpdateClause);
        }
        if (this.ForUpdateQualifier != null) {
            fus.setForUpdateQualifier(this.ForUpdateQualifier);
        }
        if (this.NoWaitQualifier != null) {
            fus.setNoWaitQualifier(this.NoWaitQualifier);
        }
        final Vector v_utn = new Vector();
        for (int i = 0; i < this.ForUpdateTableName.size(); ++i) {
            v_utn.addElement(this.ForUpdateTableName.elementAt(i).toSybaseSelect(to_sqs, from_sqs));
        }
        fus.setForUpdateTableName(v_utn);
        return fus;
    }
    
    public ForUpdateStatement toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final ForUpdateStatement fus = new ForUpdateStatement();
        fus.setCommentClass(this.commentObj);
        if (this.ForUpdateClause != null) {
            fus.setForUpdateClause(this.ForUpdateClause);
        }
        if (this.ForUpdateQualifier != null) {
            fus.setForUpdateQualifier(this.ForUpdateQualifier);
        }
        if (this.NoWaitQualifier != null) {
            fus.setNoWaitQualifier(this.NoWaitQualifier);
        }
        final Vector v_utn = new Vector();
        for (int i = 0; i < this.ForUpdateTableName.size(); ++i) {
            v_utn.addElement(this.ForUpdateTableName.elementAt(i).toOracleSelect(to_sqs, from_sqs));
        }
        fus.setForUpdateTableName(v_utn);
        return fus;
    }
    
    public ForUpdateStatement toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final ForUpdateStatement fus = new ForUpdateStatement();
        if (this.ForUpdateClause != null) {
            fus.setForUpdateClause(this.ForUpdateClause);
        }
        if (this.ForUpdateQualifier != null) {
            fus.setForUpdateQualifier(this.ForUpdateQualifier);
        }
        if (this.NoWaitQualifier != null) {
            fus.setNoWaitQualifier(this.NoWaitQualifier);
        }
        final Vector v_utn = new Vector();
        for (int i = 0; i < this.ForUpdateTableName.size(); ++i) {
            v_utn.addElement(this.ForUpdateTableName.elementAt(i).toInformixSelect(to_sqs, from_sqs));
        }
        fus.setForUpdateTableName(v_utn);
        return fus;
    }
    
    public ForUpdateStatement toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final ForUpdateStatement fus = new ForUpdateStatement();
        if (this.ForUpdateClause != null) {
            fus.setForUpdateClause(this.ForUpdateClause);
        }
        if (this.ForUpdateQualifier != null) {
            fus.setForUpdateQualifier(this.ForUpdateQualifier);
        }
        if (this.NoWaitQualifier != null) {
            fus.setNoWaitQualifier(this.NoWaitQualifier);
        }
        final Vector v_utn = new Vector();
        for (int i = 0; i < this.ForUpdateTableName.size(); ++i) {
            v_utn.addElement(this.ForUpdateTableName.elementAt(i).toDB2Select(to_sqs, from_sqs));
        }
        fus.setForUpdateTableName(v_utn);
        return fus;
    }
    
    public ForUpdateStatement toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        final ForUpdateStatement fus = new ForUpdateStatement();
        if (this.ForUpdateClause != null) {
            fus.setForUpdateClause(this.ForUpdateClause);
        }
        if (this.ForUpdateQualifier != null) {
            fus.setForUpdateQualifier(this.ForUpdateQualifier);
        }
        if (this.NoWaitQualifier != null) {
            fus.setNoWaitQualifier(this.NoWaitQualifier);
        }
        final Vector v_utn = new Vector();
        for (int i = 0; i < this.ForUpdateTableName.size(); ++i) {
            v_utn.addElement(this.ForUpdateTableName.elementAt(i).toTimesTenSelect(to_sqs, from_sqs));
        }
        fus.setForUpdateTableName(v_utn);
        return fus;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.commentObj != null) {
            sb.append(this.commentObj.toString().trim() + " ");
        }
        if (this.ForUpdateClause != null) {
            sb.append(this.ForUpdateClause.toUpperCase());
        }
        if (this.ForUpdateQualifier != null) {
            sb.append(" " + this.ForUpdateQualifier.toUpperCase() + " ");
        }
        ++SelectQueryStatement.beautyTabCount;
        for (int i = 0; i < this.ForUpdateTableName.size(); ++i) {
            if (i == this.ForUpdateTableName.size() - 1) {
                sb.append(this.ForUpdateTableName.elementAt(i).toString());
            }
            else {
                sb.append(this.ForUpdateTableName.elementAt(i).toString() + ",");
                sb.append("\n");
                for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                    sb.append("\t");
                }
            }
        }
        --SelectQueryStatement.beautyTabCount;
        if (this.NoWaitQualifier != null) {
            sb.append(" " + this.NoWaitQualifier.toUpperCase());
        }
        return sb.toString();
    }
}
