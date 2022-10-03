package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.ArrayList;

public class PartitionListAttributes
{
    private String partition;
    private String partitionName;
    private String partitionString;
    private ArrayList valuesList;
    private ArrayList physicalAttributesPartitionArrayList;
    
    public void setPartition(final String partition) {
        this.partition = partition;
    }
    
    public void setPartitionName(final String partitionName) {
        this.partitionName = partitionName;
    }
    
    public void setPartitionTableString(final String partitionString) {
        this.partitionString = partitionString;
    }
    
    public void setValuesList(final ArrayList valuesList) {
        this.valuesList = valuesList;
    }
    
    public void setPartitionPhysicalAttributes(final ArrayList physicalAttributesPartitionArrayList) {
        this.physicalAttributesPartitionArrayList = physicalAttributesPartitionArrayList;
    }
    
    public String getPartition() {
        return this.partition;
    }
    
    public String getPartitionName() {
        return this.partitionName;
    }
    
    public String getPartitionTableString() {
        return this.partitionString;
    }
    
    public ArrayList getValuesList() {
        return this.valuesList;
    }
    
    public ArrayList getPartitionPhysicalAttributes() {
        return this.physicalAttributesPartitionArrayList;
    }
    
    public PartitionListAttributes toANSI() throws ConvertException {
        final PartitionListAttributes part = this.copyObjectValues();
        part.setPartition(null);
        part.setPartitionName(null);
        part.setPartitionTableString(null);
        part.setValuesList(null);
        part.setPartitionPhysicalAttributes(null);
        return part;
    }
    
    public PartitionListAttributes toDB2() throws ConvertException {
        final PartitionListAttributes part = this.copyObjectValues();
        part.setPartition(null);
        part.setPartitionName(null);
        part.setPartitionTableString(null);
        part.setValuesList(null);
        part.setPartitionPhysicalAttributes(null);
        return part;
    }
    
    public PartitionListAttributes toInformix() throws ConvertException {
        final PartitionListAttributes part = this.copyObjectValues();
        part.setPartition(null);
        part.setPartitionName(null);
        part.setPartitionTableString(null);
        part.setValuesList(null);
        part.setPartitionPhysicalAttributes(null);
        return part;
    }
    
    public PartitionListAttributes toMSSQLServer() throws ConvertException {
        final PartitionListAttributes part = this.copyObjectValues();
        part.setPartition(null);
        part.setPartitionName(null);
        part.setPartitionTableString(null);
        part.setValuesList(null);
        part.setPartitionPhysicalAttributes(null);
        return part;
    }
    
    public PartitionListAttributes toSybase() throws ConvertException {
        final PartitionListAttributes part = this.copyObjectValues();
        part.setPartition(null);
        part.setPartitionName(null);
        part.setPartitionTableString(null);
        part.setValuesList(null);
        part.setPartitionPhysicalAttributes(null);
        return part;
    }
    
    public PartitionListAttributes toMySQL() throws ConvertException {
        final PartitionListAttributes part = this.copyObjectValues();
        part.setPartition(null);
        part.setPartitionName(null);
        part.setPartitionTableString(null);
        part.setValuesList(null);
        part.setPartitionPhysicalAttributes(null);
        return part;
    }
    
    public PartitionListAttributes toOracle() throws ConvertException {
        final PartitionListAttributes part = this.copyObjectValues();
        if (part.getPartition() != null) {
            part.getPartition();
        }
        if (part.getPartitionName() != null) {
            part.getPartitionName();
        }
        if (part.getPartitionTableString() != null) {
            part.getPartitionTableString();
        }
        if (part.getValuesList() != null) {
            final ArrayList oracleValuesList = new ArrayList();
            final ArrayList tempValuesList = part.getValuesList();
            for (int i = 0; i < tempValuesList.size(); ++i) {
                final SelectColumn selectColumn = tempValuesList.get(i);
                final SelectColumn tempSelectColumn = selectColumn.toOracleSelect(null, null);
                oracleValuesList.add(tempSelectColumn);
            }
            part.setValuesList(oracleValuesList);
        }
        if (part.getPartitionPhysicalAttributes() != null) {
            final ArrayList tempPhysicalAttributesPartitionArrayList = part.getPartitionPhysicalAttributes();
            final ArrayList oraclePhysicalAttributesArrayList = new ArrayList();
            for (int i = 0; i < tempPhysicalAttributesPartitionArrayList.size(); ++i) {
                final PhysicalAttributesClause physicalAttributesClause = tempPhysicalAttributesPartitionArrayList.get(i);
                final PhysicalAttributesClause oraclePhysicalAttributesClause = physicalAttributesClause.toOracle();
                oraclePhysicalAttributesArrayList.add(oraclePhysicalAttributesClause);
            }
            part.setPartitionPhysicalAttributes(oraclePhysicalAttributesArrayList);
        }
        return part;
    }
    
    public PartitionListAttributes toPostgreSQL() throws ConvertException {
        final PartitionListAttributes part = this.copyObjectValues();
        part.setPartition(null);
        part.setPartitionName(null);
        part.setPartitionTableString(null);
        part.setValuesList(null);
        part.setPartitionPhysicalAttributes(null);
        return part;
    }
    
    public String removeIndent(String str) {
        str = str.replace('\n', ' ');
        str = str.replace('\t', ' ');
        return str;
    }
    
    public PartitionListAttributes copyObjectValues() {
        final PartitionListAttributes dupPartitionListAttributes = new PartitionListAttributes();
        dupPartitionListAttributes.setPartition(this.partition);
        dupPartitionListAttributes.setPartitionName(this.partitionName);
        dupPartitionListAttributes.setPartitionTableString(this.partitionString);
        dupPartitionListAttributes.setValuesList(this.valuesList);
        dupPartitionListAttributes.setPartitionPhysicalAttributes(this.physicalAttributesPartitionArrayList);
        if (this.partitionString != null) {
            dupPartitionListAttributes.getPartitionTableString();
        }
        if (this.physicalAttributesPartitionArrayList != null) {
            dupPartitionListAttributes.getPartitionPhysicalAttributes();
        }
        return dupPartitionListAttributes;
    }
    
    public PartitionListAttributes toNetezza() throws ConvertException {
        final PartitionListAttributes part = this.copyObjectValues();
        part.setPartition(null);
        part.setPartitionName(null);
        part.setPartitionTableString(null);
        part.setValuesList(null);
        part.setPartitionPhysicalAttributes(null);
        return part;
    }
    
    public PartitionListAttributes toTeradata() throws ConvertException {
        final PartitionListAttributes part = this.copyObjectValues();
        part.setPartition(null);
        part.setPartitionName(null);
        part.setPartitionTableString(null);
        part.setValuesList(null);
        part.setPartitionPhysicalAttributes(null);
        return part;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.partition != null) {
            sb.append(this.partition.toUpperCase() + " ");
        }
        if (this.partitionName != null) {
            sb.append(this.partitionName + " ");
        }
        if (this.partitionString != null) {
            sb.append(this.partitionString.toUpperCase() + " ");
        }
        if (this.valuesList != null) {
            final int size = this.valuesList.size();
            for (int i = 0; i < this.valuesList.size(); ++i) {
                final SelectColumn selectColumn = this.valuesList.get(i);
                sb.append(selectColumn);
                if (i < size - 1) {
                    sb.append(" ,");
                }
            }
            sb.append(")");
        }
        if (this.physicalAttributesPartitionArrayList != null) {
            sb.append(" ");
            for (int j = 0; j < this.physicalAttributesPartitionArrayList.size(); ++j) {
                final PhysicalAttributesClause physicalAttributesClause = this.physicalAttributesPartitionArrayList.get(j);
                sb.append(physicalAttributesClause.toString() + " ");
            }
        }
        return sb.toString();
    }
}
