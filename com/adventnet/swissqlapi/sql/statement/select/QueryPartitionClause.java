package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.ArrayList;

public class QueryPartitionClause
{
    ArrayList selectColumnList;
    String partitionBy;
    String openBrace;
    String closeBrace;
    
    public QueryPartitionClause() {
        this.selectColumnList = new ArrayList();
        this.partitionBy = null;
        this.openBrace = null;
        this.closeBrace = null;
    }
    
    public void setSelectColumnList(final ArrayList selColList) {
        this.selectColumnList = selColList;
    }
    
    public void setPartitionBy(final String partitionBy) {
        this.partitionBy = partitionBy;
    }
    
    public void setOpenBrace(final String openBrace) {
        this.openBrace = openBrace;
    }
    
    public void setCloseBrace(final String closeBrace) {
        this.closeBrace = closeBrace;
    }
    
    public ArrayList getSelectColumnList() {
        return this.selectColumnList;
    }
    
    public String getPartitionBy() {
        return this.partitionBy;
    }
    
    public String getOpenBrace() {
        return this.openBrace;
    }
    
    public String getCloseBrace() {
        return this.closeBrace;
    }
    
    public QueryPartitionClause toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final QueryPartitionClause queryPartCl = new QueryPartitionClause();
        queryPartCl.setPartitionBy(this.getPartitionBy());
        final ArrayList newSelectColList = new ArrayList();
        for (int selColListSize = this.selectColumnList.size(), i = 0; i < selColListSize; ++i) {
            final Object obj = this.selectColumnList.get(i);
            if (obj instanceof SelectColumn) {
                final SelectColumn selCol = (SelectColumn)obj;
                newSelectColList.add(selCol.toANSISelect(to_sqs, from_sqs));
            }
            else {
                newSelectColList.add(obj);
            }
        }
        queryPartCl.setSelectColumnList(newSelectColList);
        return queryPartCl;
    }
    
    public QueryPartitionClause toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final QueryPartitionClause queryPartCl = new QueryPartitionClause();
        queryPartCl.setPartitionBy(this.getPartitionBy());
        queryPartCl.setOpenBrace(this.openBrace);
        queryPartCl.setCloseBrace(this.closeBrace);
        final ArrayList newSelectColList = new ArrayList();
        for (int selColListSize = this.selectColumnList.size(), i = 0; i < selColListSize; ++i) {
            final Object obj = this.selectColumnList.get(i);
            if (obj instanceof SelectColumn) {
                final SelectColumn selCol = (SelectColumn)obj;
                newSelectColList.add(selCol.toTeradataSelect(to_sqs, from_sqs));
            }
            else {
                newSelectColList.add(obj);
            }
        }
        queryPartCl.setSelectColumnList(newSelectColList);
        return queryPartCl;
    }
    
    public QueryPartitionClause toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final QueryPartitionClause queryPartCl = new QueryPartitionClause();
        queryPartCl.setPartitionBy(this.getPartitionBy());
        final ArrayList newSelectColList = new ArrayList();
        for (int selColListSize = this.selectColumnList.size(), i = 0; i < selColListSize; ++i) {
            final Object obj = this.selectColumnList.get(i);
            if (obj instanceof SelectColumn) {
                final SelectColumn selCol = (SelectColumn)obj;
                newSelectColList.add(selCol.toDB2Select(to_sqs, from_sqs));
            }
            else {
                newSelectColList.add(obj);
            }
        }
        queryPartCl.setSelectColumnList(newSelectColList);
        return queryPartCl;
    }
    
    public QueryPartitionClause toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final QueryPartitionClause queryPartCl = new QueryPartitionClause();
        queryPartCl.setPartitionBy(this.getPartitionBy());
        final ArrayList newSelectColList = new ArrayList();
        for (int selColListSize = this.selectColumnList.size(), i = 0; i < selColListSize; ++i) {
            final Object obj = this.selectColumnList.get(i);
            if (obj instanceof SelectColumn) {
                final SelectColumn selCol = (SelectColumn)obj;
                newSelectColList.add(selCol.toInformixSelect(to_sqs, from_sqs));
            }
            else {
                newSelectColList.add(obj);
            }
        }
        queryPartCl.setSelectColumnList(newSelectColList);
        return queryPartCl;
    }
    
    public QueryPartitionClause toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final QueryPartitionClause queryPartCl = new QueryPartitionClause();
        queryPartCl.setPartitionBy(this.getPartitionBy());
        final ArrayList newSelectColList = new ArrayList();
        for (int selColListSize = this.selectColumnList.size(), i = 0; i < selColListSize; ++i) {
            final Object obj = this.selectColumnList.get(i);
            if (obj instanceof SelectColumn) {
                final SelectColumn selCol = (SelectColumn)obj;
                newSelectColList.add(selCol.toMSSQLServerSelect(to_sqs, from_sqs));
            }
            else {
                newSelectColList.add(obj);
            }
        }
        queryPartCl.setSelectColumnList(newSelectColList);
        return queryPartCl;
    }
    
    public QueryPartitionClause toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final QueryPartitionClause queryPartCl = new QueryPartitionClause();
        queryPartCl.setPartitionBy(this.getPartitionBy());
        final ArrayList newSelectColList = new ArrayList();
        for (int selColListSize = this.selectColumnList.size(), i = 0; i < selColListSize; ++i) {
            final Object obj = this.selectColumnList.get(i);
            if (obj instanceof SelectColumn) {
                final SelectColumn selCol = (SelectColumn)obj;
                newSelectColList.add(selCol.toMySQLSelect(to_sqs, from_sqs));
            }
            else {
                newSelectColList.add(obj);
            }
        }
        queryPartCl.setSelectColumnList(newSelectColList);
        return queryPartCl;
    }
    
    public QueryPartitionClause toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final QueryPartitionClause queryPartCl = new QueryPartitionClause();
        queryPartCl.setPartitionBy(this.getPartitionBy());
        final ArrayList newSelectColList = new ArrayList();
        for (int selColListSize = this.selectColumnList.size(), i = 0; i < selColListSize; ++i) {
            final Object obj = this.selectColumnList.get(i);
            if (obj instanceof SelectColumn) {
                final SelectColumn selCol = (SelectColumn)obj;
                newSelectColList.add(selCol.toNetezzaSelect(to_sqs, from_sqs));
            }
            else {
                newSelectColList.add(obj);
            }
        }
        queryPartCl.setSelectColumnList(newSelectColList);
        return queryPartCl;
    }
    
    public QueryPartitionClause toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final QueryPartitionClause queryPartCl = new QueryPartitionClause();
        queryPartCl.setPartitionBy(this.getPartitionBy());
        final ArrayList newSelectColList = new ArrayList();
        for (int selColListSize = this.selectColumnList.size(), i = 0; i < selColListSize; ++i) {
            final Object obj = this.selectColumnList.get(i);
            if (obj instanceof SelectColumn) {
                final SelectColumn selCol = (SelectColumn)obj;
                newSelectColList.add(selCol.toOracleSelect(to_sqs, from_sqs));
            }
            else {
                newSelectColList.add(obj);
            }
        }
        queryPartCl.setSelectColumnList(newSelectColList);
        return queryPartCl;
    }
    
    public QueryPartitionClause toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final QueryPartitionClause queryPartCl = new QueryPartitionClause();
        queryPartCl.setPartitionBy(this.getPartitionBy());
        final ArrayList newSelectColList = new ArrayList();
        for (int selColListSize = this.selectColumnList.size(), i = 0; i < selColListSize; ++i) {
            final Object obj = this.selectColumnList.get(i);
            if (obj instanceof SelectColumn) {
                final SelectColumn selCol = (SelectColumn)obj;
                newSelectColList.add(selCol.toPostgreSQLSelect(to_sqs, from_sqs));
            }
            else {
                newSelectColList.add(obj);
            }
        }
        queryPartCl.setSelectColumnList(newSelectColList);
        return queryPartCl;
    }
    
    public QueryPartitionClause toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final QueryPartitionClause queryPartCl = new QueryPartitionClause();
        queryPartCl.setPartitionBy(this.getPartitionBy());
        final ArrayList newSelectColList = new ArrayList();
        for (int selColListSize = this.selectColumnList.size(), i = 0; i < selColListSize; ++i) {
            final Object obj = this.selectColumnList.get(i);
            if (obj instanceof SelectColumn) {
                final SelectColumn selCol = (SelectColumn)obj;
                newSelectColList.add(selCol.toSybaseSelect(to_sqs, from_sqs));
            }
            else {
                newSelectColList.add(obj);
            }
        }
        queryPartCl.setSelectColumnList(newSelectColList);
        return queryPartCl;
    }
    
    public QueryPartitionClause toTimesTenSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final QueryPartitionClause queryPartCl = new QueryPartitionClause();
        queryPartCl.setPartitionBy(this.getPartitionBy());
        final ArrayList newSelectColList = new ArrayList();
        for (int selColListSize = this.selectColumnList.size(), i = 0; i < selColListSize; ++i) {
            final Object obj = this.selectColumnList.get(i);
            if (obj instanceof SelectColumn) {
                final SelectColumn selCol = (SelectColumn)obj;
                newSelectColList.add(selCol.toTimesTenSelect(to_sqs, from_sqs));
            }
            else {
                newSelectColList.add(obj);
            }
        }
        queryPartCl.setSelectColumnList(newSelectColList);
        return queryPartCl;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.partitionBy != null) {
            sb.append(this.partitionBy + " ");
        }
        if (this.openBrace != null) {
            sb.append(this.openBrace);
        }
        if (this.selectColumnList.size() > 0) {
            for (int size = this.selectColumnList.size(), i = 0; i < size; ++i) {
                sb.append(this.selectColumnList.get(i).toString() + " ");
            }
        }
        if (this.closeBrace != null) {
            sb.append(this.closeBrace + " ");
        }
        return sb.toString();
    }
    
    public QueryPartitionClause toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final QueryPartitionClause queryPartCl = new QueryPartitionClause();
        queryPartCl.setPartitionBy(this.getPartitionBy());
        final ArrayList newSelectColList = new ArrayList();
        for (int selColListSize = this.selectColumnList.size(), i = 0; i < selColListSize; ++i) {
            final Object obj = this.selectColumnList.get(i);
            if (obj instanceof SelectColumn) {
                final SelectColumn selCol = (SelectColumn)obj;
                newSelectColList.add(selCol.toVectorWiseSelect(to_sqs, from_sqs));
            }
            else {
                newSelectColList.add(obj);
            }
        }
        queryPartCl.setSelectColumnList(newSelectColList);
        return queryPartCl;
    }
}
