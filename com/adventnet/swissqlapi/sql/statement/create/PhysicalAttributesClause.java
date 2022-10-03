package com.adventnet.swissqlapi.sql.statement.create;

import java.util.Iterator;
import java.util.Set;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.HashMap;

public class PhysicalAttributesClause
{
    private String pctfree;
    private String pctfreeValue;
    private String pctused;
    private String pctUsedValue;
    private String initrans;
    private String maxtrans;
    private String storageClause;
    private String online;
    private String loggingOrNoLogging;
    private String compute;
    private String tableSpaceOrDefault;
    private String tableSpaceName;
    private String compressOrNoCompress;
    private String noSortOrReverse;
    private String padIndex;
    private String fillfactor;
    private String fillfactorValue;
    private String dropExisting;
    private String statisticsNoreCompute;
    private String sortInTempDb;
    private int fillFactorValueOfPctused;
    private String noCache;
    private String with;
    private HashMap diskAttr;
    private String overflow;
    
    public void setPctFree(final String pctfree) {
        this.pctfree = pctfree;
    }
    
    public void setPctFreeValue(final String pctfreeValue) {
        this.pctfreeValue = pctfreeValue;
    }
    
    public void setPctUsed(final String pctused) {
        this.pctused = pctused;
    }
    
    public void setPctUsedValue(final String pctUsedValue) {
        this.pctUsedValue = pctUsedValue;
    }
    
    public void setIniTrans(final String initrans) {
        this.initrans = initrans;
    }
    
    public void setMaxTrans(final String maxtrans) {
        this.maxtrans = maxtrans;
    }
    
    public void setStorageClause(final String storageClause) {
        this.storageClause = storageClause;
    }
    
    public void setLoggingOrNoLogging(final String loggingOrNoLogging) {
        this.loggingOrNoLogging = loggingOrNoLogging;
    }
    
    public void setOnline(final String online) {
        this.online = online;
    }
    
    public void setCompute(final String compute) {
        this.compute = compute;
    }
    
    public void setTableSpaceOrDefault(final String tableSpaceOrDefault) {
        this.tableSpaceOrDefault = tableSpaceOrDefault;
    }
    
    public void setTableSpaceName(final String tableSpaceName) {
        this.tableSpaceName = tableSpaceName;
    }
    
    public void setCompressOrNoCompress(final String compressOrNoCompress) {
        this.compressOrNoCompress = compressOrNoCompress;
    }
    
    public void setNoSortOrReverse(final String noSortOrReverse) {
        this.noSortOrReverse = noSortOrReverse;
    }
    
    public void setPadIndex(final String padIndex) {
        this.padIndex = padIndex;
    }
    
    public void setFillFactor(final String fillfactor) {
        this.fillfactor = fillfactor;
    }
    
    public void setFillFactorValue(final String fillfactorValue) {
        this.fillfactorValue = fillfactorValue;
    }
    
    public void setDropExisting(final String dropExisting) {
        this.dropExisting = dropExisting;
    }
    
    public void setStatisticsNoreCompute(final String statisticsNoreCompute) {
        this.statisticsNoreCompute = statisticsNoreCompute;
    }
    
    public void setSortInTempDb(final String sortInTempDb) {
        this.sortInTempDb = sortInTempDb;
    }
    
    public void setNoCache(final String noCache) {
        this.noCache = noCache;
    }
    
    public void setWith(final String with) {
        this.with = with;
    }
    
    public void setDiskAttr(final HashMap diskAttr) {
        this.diskAttr = diskAttr;
    }
    
    public void setOverflow(final String overflow) {
        this.overflow = overflow;
    }
    
    public String getPctFree() {
        return this.pctfree;
    }
    
    public String getPctFreeValue() {
        return this.pctfreeValue;
    }
    
    public String getPctUsed() {
        return this.pctused;
    }
    
    public String getPctUsedValue() {
        return this.pctUsedValue;
    }
    
    public String getIniTrans() {
        return this.initrans;
    }
    
    public String getMaxTrans() {
        return this.maxtrans;
    }
    
    public String getStorageClause() {
        return this.storageClause;
    }
    
    public String getLoggingOrNoLogging() {
        return this.loggingOrNoLogging;
    }
    
    public String getOnline() {
        return this.online;
    }
    
    public String getCompute() {
        return this.compute;
    }
    
    public String getTableSpaceOrDefault() {
        return this.tableSpaceOrDefault;
    }
    
    public String getTableSpaceName() {
        return this.tableSpaceName;
    }
    
    public String getCompressOrNoCompress() {
        return this.compressOrNoCompress;
    }
    
    public String getNoSortOrReverse() {
        return this.noSortOrReverse;
    }
    
    public String getPadIndex() {
        return this.padIndex;
    }
    
    public String getFillFactor() {
        return this.fillfactor;
    }
    
    public String getFillFactorValue() {
        return this.fillfactorValue;
    }
    
    public String getDropExisting() {
        return this.dropExisting;
    }
    
    public String getStatisticsNoreCompute() {
        return this.statisticsNoreCompute;
    }
    
    public String getSortInTempDb() {
        return this.sortInTempDb;
    }
    
    public String getNoCache() {
        return this.noCache;
    }
    
    public String getWith() {
        return this.with;
    }
    
    public HashMap getDiskAttr() {
        return this.diskAttr;
    }
    
    public String getOverflow() {
        return this.overflow;
    }
    
    public PhysicalAttributesClause toANSI() throws ConvertException {
        final PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
        physicalAttributes.setPctFree(null);
        physicalAttributes.setPctFreeValue(null);
        physicalAttributes.setPctUsed(null);
        physicalAttributes.setPctUsedValue(null);
        physicalAttributes.setIniTrans(null);
        physicalAttributes.setMaxTrans(null);
        physicalAttributes.setStorageClause(null);
        physicalAttributes.setNoCache(null);
        physicalAttributes.setOnline(null);
        physicalAttributes.setLoggingOrNoLogging(null);
        physicalAttributes.setCompute(null);
        physicalAttributes.setTableSpaceOrDefault(null);
        physicalAttributes.setTableSpaceName(null);
        physicalAttributes.setCompressOrNoCompress(null);
        physicalAttributes.setNoSortOrReverse(null);
        physicalAttributes.setPadIndex(null);
        physicalAttributes.setFillFactor(null);
        physicalAttributes.setFillFactorValue(null);
        physicalAttributes.setDropExisting(null);
        physicalAttributes.setStatisticsNoreCompute(null);
        physicalAttributes.setSortInTempDb(null);
        physicalAttributes.setWith(null);
        physicalAttributes.setDiskAttr(null);
        return physicalAttributes;
    }
    
    public PhysicalAttributesClause toTeradata() throws ConvertException {
        final PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
        physicalAttributes.setPctFree(null);
        physicalAttributes.setPctFreeValue(null);
        physicalAttributes.setPctUsed(null);
        physicalAttributes.setPctUsedValue(null);
        physicalAttributes.setIniTrans(null);
        physicalAttributes.setMaxTrans(null);
        physicalAttributes.setStorageClause(null);
        physicalAttributes.setNoCache(null);
        physicalAttributes.setOnline(null);
        physicalAttributes.setLoggingOrNoLogging(null);
        physicalAttributes.setCompute(null);
        physicalAttributes.setTableSpaceOrDefault(null);
        physicalAttributes.setTableSpaceName(null);
        physicalAttributes.setCompressOrNoCompress(null);
        physicalAttributes.setNoSortOrReverse(null);
        physicalAttributes.setPadIndex(null);
        physicalAttributes.setFillFactor(null);
        physicalAttributes.setFillFactorValue(null);
        physicalAttributes.setDropExisting(null);
        physicalAttributes.setStatisticsNoreCompute(null);
        physicalAttributes.setSortInTempDb(null);
        physicalAttributes.setWith(null);
        physicalAttributes.setDiskAttr(null);
        return physicalAttributes;
    }
    
    public PhysicalAttributesClause toDB2() throws ConvertException {
        final PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
        physicalAttributes.setPctFree(null);
        physicalAttributes.setPctFreeValue(null);
        physicalAttributes.setPctUsed(null);
        physicalAttributes.setPctUsedValue(null);
        physicalAttributes.setIniTrans(null);
        physicalAttributes.setMaxTrans(null);
        physicalAttributes.setStorageClause(null);
        physicalAttributes.setNoCache(null);
        physicalAttributes.setOnline(null);
        physicalAttributes.setLoggingOrNoLogging(null);
        physicalAttributes.setCompute(null);
        physicalAttributes.setTableSpaceOrDefault(null);
        physicalAttributes.setTableSpaceName(null);
        physicalAttributes.setCompressOrNoCompress(null);
        physicalAttributes.setNoSortOrReverse(null);
        physicalAttributes.setPadIndex(null);
        physicalAttributes.setFillFactor(null);
        physicalAttributes.setFillFactorValue(null);
        physicalAttributes.setDropExisting(null);
        physicalAttributes.setWith(null);
        physicalAttributes.setDiskAttr(null);
        physicalAttributes.setStatisticsNoreCompute(null);
        physicalAttributes.setSortInTempDb(null);
        return physicalAttributes;
    }
    
    public PhysicalAttributesClause toInformix() throws ConvertException {
        final PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
        physicalAttributes.setPctFree(null);
        physicalAttributes.setPctFreeValue(null);
        physicalAttributes.setPctUsed(null);
        physicalAttributes.setPctUsedValue(null);
        physicalAttributes.setIniTrans(null);
        physicalAttributes.setMaxTrans(null);
        physicalAttributes.setStorageClause(null);
        physicalAttributes.setNoCache(null);
        physicalAttributes.setOnline(null);
        physicalAttributes.setLoggingOrNoLogging(null);
        physicalAttributes.setCompute(null);
        physicalAttributes.setTableSpaceOrDefault(null);
        physicalAttributes.setTableSpaceName(null);
        physicalAttributes.setCompressOrNoCompress(null);
        physicalAttributes.setNoSortOrReverse(null);
        physicalAttributes.setPadIndex(null);
        physicalAttributes.setFillFactor(null);
        physicalAttributes.setFillFactorValue(null);
        physicalAttributes.setDropExisting(null);
        physicalAttributes.setWith(null);
        physicalAttributes.setDiskAttr(null);
        physicalAttributes.setStatisticsNoreCompute(null);
        physicalAttributes.setSortInTempDb(null);
        return physicalAttributes;
    }
    
    public PhysicalAttributesClause toMSSQLServer() throws ConvertException {
        final PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
        if (physicalAttributes.getPctFree() != null) {
            final String tempPctfree = physicalAttributes.getPctFree();
            physicalAttributes.setFillFactor("FILLFACTOR = ");
            physicalAttributes.setPctFree(null);
        }
        if (physicalAttributes.getPctFreeValue() != null) {
            final String tempPctfreeValue = physicalAttributes.getPctFreeValue();
            physicalAttributes.setFillFactorValue(tempPctfreeValue);
            physicalAttributes.setPctFreeValue(null);
        }
        if (physicalAttributes.getPctUsed() != null) {
            final String tempPctfreeValue = physicalAttributes.getPctFreeValue();
            physicalAttributes.setFillFactor("FILLFACTOR = ");
            physicalAttributes.setPctUsed(null);
        }
        if (physicalAttributes.getPctUsedValue() != null) {
            String tempPctUsedValue = physicalAttributes.getPctUsedValue();
            final int fillIntValue = Integer.parseInt(tempPctUsedValue);
            this.fillFactorValueOfPctused = 100 - fillIntValue;
            tempPctUsedValue = "" + this.fillFactorValueOfPctused;
            physicalAttributes.setFillFactorValue(tempPctUsedValue);
            physicalAttributes.setPctUsedValue(null);
        }
        physicalAttributes.setIniTrans(null);
        physicalAttributes.setMaxTrans(null);
        physicalAttributes.setStorageClause(null);
        physicalAttributes.setNoCache(null);
        physicalAttributes.setOnline(null);
        physicalAttributes.setLoggingOrNoLogging(null);
        physicalAttributes.setCompute(null);
        physicalAttributes.setTableSpaceOrDefault(null);
        physicalAttributes.setTableSpaceName(null);
        physicalAttributes.setCompressOrNoCompress(null);
        physicalAttributes.setNoSortOrReverse(null);
        if (physicalAttributes.getPadIndex() != null) {
            physicalAttributes.getPadIndex();
        }
        if (physicalAttributes.getFillFactor() != null) {
            physicalAttributes.getFillFactor();
        }
        if (physicalAttributes.getFillFactorValue() != null) {
            physicalAttributes.getFillFactorValue();
        }
        if (physicalAttributes.getDropExisting() != null) {
            physicalAttributes.getDropExisting();
        }
        if (physicalAttributes.getStatisticsNoreCompute() != null) {
            physicalAttributes.getStatisticsNoreCompute();
        }
        if (physicalAttributes.getSortInTempDb() != null) {
            physicalAttributes.getSortInTempDb();
        }
        if (physicalAttributes.getDiskAttr() != null) {
            final HashMap diskAttr = physicalAttributes.getDiskAttr();
            final String ignore_row = "IGNORE_DUP_ROW";
            if (diskAttr.containsKey(ignore_row)) {
                diskAttr.remove(ignore_row);
                diskAttr.put("IGNORE_DUP_KEY", "");
            }
            else if (diskAttr.containsKey(ignore_row.toLowerCase())) {
                diskAttr.remove(ignore_row.toLowerCase());
                diskAttr.put("IGNORE_DUP_KEY", "");
            }
        }
        return physicalAttributes;
    }
    
    public PhysicalAttributesClause toSybase() throws ConvertException {
        final PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
        if (physicalAttributes.getPctFree() != null) {
            final String tempPctfree = physicalAttributes.getPctFree();
            physicalAttributes.setFillFactor("FILLFACTOR = ");
            physicalAttributes.setPctFree(null);
        }
        if (physicalAttributes.getPctFreeValue() != null) {
            final String tempPctfreeValue = physicalAttributes.getPctFreeValue();
            physicalAttributes.setFillFactorValue(tempPctfreeValue);
            physicalAttributes.setPctFreeValue(null);
        }
        if (physicalAttributes.getPctUsed() != null) {
            final String tempPctfreeValue = physicalAttributes.getPctFreeValue();
            physicalAttributes.setFillFactor("FILLFACTOR = ");
            physicalAttributes.setPctUsed(null);
        }
        if (physicalAttributes.getPctUsedValue() != null) {
            String tempPctUsedValue = physicalAttributes.getPctUsedValue();
            final int fillIntValue = Integer.parseInt(tempPctUsedValue);
            this.fillFactorValueOfPctused = 100 - fillIntValue;
            tempPctUsedValue = "" + this.fillFactorValueOfPctused;
            physicalAttributes.setFillFactorValue(tempPctUsedValue);
            physicalAttributes.setPctUsedValue(null);
        }
        physicalAttributes.setIniTrans(null);
        physicalAttributes.setMaxTrans(null);
        physicalAttributes.setStorageClause(null);
        physicalAttributes.setNoCache(null);
        physicalAttributes.setOnline(null);
        physicalAttributes.setLoggingOrNoLogging(null);
        physicalAttributes.setCompute(null);
        physicalAttributes.setTableSpaceOrDefault(null);
        physicalAttributes.setTableSpaceName(null);
        physicalAttributes.setCompressOrNoCompress(null);
        physicalAttributes.setNoSortOrReverse(null);
        if (physicalAttributes.getPadIndex() != null) {
            physicalAttributes.getPadIndex();
        }
        if (physicalAttributes.getFillFactor() != null) {
            physicalAttributes.getFillFactor();
        }
        if (physicalAttributes.getFillFactorValue() != null) {
            physicalAttributes.getFillFactorValue();
        }
        if (physicalAttributes.getDropExisting() != null) {
            physicalAttributes.getDropExisting();
        }
        if (physicalAttributes.getStatisticsNoreCompute() != null) {
            physicalAttributes.getStatisticsNoreCompute();
        }
        if (physicalAttributes.getSortInTempDb() != null) {
            physicalAttributes.getSortInTempDb();
        }
        return physicalAttributes;
    }
    
    public PhysicalAttributesClause toMySQL() throws ConvertException {
        final PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
        physicalAttributes.setPctFree(null);
        physicalAttributes.setPctFreeValue(null);
        physicalAttributes.setPctUsed(null);
        physicalAttributes.setPctUsedValue(null);
        physicalAttributes.setIniTrans(null);
        physicalAttributes.setMaxTrans(null);
        physicalAttributes.setStorageClause(null);
        physicalAttributes.setNoCache(null);
        physicalAttributes.setOnline(null);
        physicalAttributes.setLoggingOrNoLogging(null);
        physicalAttributes.setCompute(null);
        physicalAttributes.setTableSpaceOrDefault(null);
        physicalAttributes.setTableSpaceName(null);
        physicalAttributes.setCompressOrNoCompress(null);
        physicalAttributes.setNoSortOrReverse(null);
        physicalAttributes.setPadIndex(null);
        physicalAttributes.setFillFactor(null);
        physicalAttributes.setFillFactorValue(null);
        physicalAttributes.setDropExisting(null);
        physicalAttributes.setWith(null);
        physicalAttributes.setDiskAttr(null);
        physicalAttributes.setStatisticsNoreCompute(null);
        physicalAttributes.setSortInTempDb(null);
        return physicalAttributes;
    }
    
    public PhysicalAttributesClause toOracle() throws ConvertException {
        final PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
        if (physicalAttributes.getPctFree() != null) {
            physicalAttributes.getPctFree();
        }
        if (physicalAttributes.getPctFreeValue() != null) {
            physicalAttributes.getPctFreeValue();
        }
        if (physicalAttributes.getPctUsed() != null) {
            physicalAttributes.getPctUsed();
        }
        if (physicalAttributes.getIniTrans() != null) {
            physicalAttributes.getIniTrans();
        }
        if (physicalAttributes.getMaxTrans() != null) {
            physicalAttributes.getMaxTrans();
        }
        if (physicalAttributes.getStorageClause() != null) {
            physicalAttributes.getStorageClause();
        }
        if (physicalAttributes.getOnline() != null) {
            physicalAttributes.getOnline();
        }
        if (physicalAttributes.getLoggingOrNoLogging() != null) {
            physicalAttributes.getLoggingOrNoLogging();
        }
        if (physicalAttributes.getCompute() != null) {
            physicalAttributes.getCompute();
        }
        if (physicalAttributes.getTableSpaceOrDefault() != null) {
            physicalAttributes.getTableSpaceOrDefault();
        }
        if (physicalAttributes.getTableSpaceName() != null) {
            physicalAttributes.getTableSpaceName();
        }
        if (physicalAttributes.getCompressOrNoCompress() != null) {
            physicalAttributes.getCompressOrNoCompress();
        }
        if (physicalAttributes.getNoSortOrReverse() != null) {
            physicalAttributes.getNoSortOrReverse();
        }
        if (physicalAttributes.getFillFactor() != null) {
            physicalAttributes.setFillFactor("PCTFREE ");
        }
        if (physicalAttributes.getFillFactorValue() != null) {
            physicalAttributes.setFillFactorValue(this.fillfactorValue);
        }
        if (physicalAttributes.getTableSpaceName() != null) {
            physicalAttributes.setTableSpaceOrDefault("TABLESPACE");
        }
        physicalAttributes.setPadIndex(null);
        physicalAttributes.setDropExisting(null);
        physicalAttributes.setWith(null);
        physicalAttributes.setDiskAttr(null);
        physicalAttributes.setStatisticsNoreCompute(null);
        physicalAttributes.setSortInTempDb(null);
        return physicalAttributes;
    }
    
    public PhysicalAttributesClause toPostgreSQL() throws ConvertException {
        final PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
        physicalAttributes.setPctFree(null);
        physicalAttributes.setPctFreeValue(null);
        physicalAttributes.setPctUsed(null);
        physicalAttributes.setPctUsedValue(null);
        physicalAttributes.setIniTrans(null);
        physicalAttributes.setMaxTrans(null);
        physicalAttributes.setStorageClause(null);
        physicalAttributes.setNoCache(null);
        physicalAttributes.setOnline(null);
        physicalAttributes.setLoggingOrNoLogging(null);
        physicalAttributes.setCompute(null);
        physicalAttributes.setTableSpaceOrDefault(null);
        physicalAttributes.setTableSpaceName(null);
        physicalAttributes.setCompressOrNoCompress(null);
        physicalAttributes.setNoSortOrReverse(null);
        physicalAttributes.setPadIndex(null);
        physicalAttributes.setFillFactor(null);
        physicalAttributes.setFillFactorValue(null);
        physicalAttributes.setDropExisting(null);
        physicalAttributes.setWith(null);
        physicalAttributes.setDiskAttr(null);
        physicalAttributes.setStatisticsNoreCompute(null);
        physicalAttributes.setSortInTempDb(null);
        return physicalAttributes;
    }
    
    public PhysicalAttributesClause toTimesTen() throws ConvertException {
        final PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
        physicalAttributes.setPctFree(null);
        physicalAttributes.setPctFreeValue(null);
        physicalAttributes.setPctUsed(null);
        physicalAttributes.setPctUsedValue(null);
        physicalAttributes.setIniTrans(null);
        physicalAttributes.setMaxTrans(null);
        physicalAttributes.setStorageClause(null);
        physicalAttributes.setNoCache(null);
        physicalAttributes.setOnline(null);
        physicalAttributes.setLoggingOrNoLogging(null);
        physicalAttributes.setCompute(null);
        physicalAttributes.setTableSpaceOrDefault(null);
        physicalAttributes.setTableSpaceName(null);
        physicalAttributes.setCompressOrNoCompress(null);
        physicalAttributes.setNoSortOrReverse(null);
        physicalAttributes.setPadIndex(null);
        physicalAttributes.setFillFactor(null);
        physicalAttributes.setFillFactorValue(null);
        physicalAttributes.setDropExisting(null);
        physicalAttributes.setStatisticsNoreCompute(null);
        physicalAttributes.setSortInTempDb(null);
        physicalAttributes.setWith(null);
        physicalAttributes.setDiskAttr(null);
        return physicalAttributes;
    }
    
    public PhysicalAttributesClause toNetezza() throws ConvertException {
        final PhysicalAttributesClause physicalAttributes = this.copyObjectValues();
        physicalAttributes.setPctFree(null);
        physicalAttributes.setPctFreeValue(null);
        physicalAttributes.setPctUsed(null);
        physicalAttributes.setPctUsedValue(null);
        physicalAttributes.setIniTrans(null);
        physicalAttributes.setMaxTrans(null);
        physicalAttributes.setStorageClause(null);
        physicalAttributes.setNoCache(null);
        physicalAttributes.setOnline(null);
        physicalAttributes.setLoggingOrNoLogging(null);
        physicalAttributes.setCompute(null);
        physicalAttributes.setTableSpaceOrDefault(null);
        physicalAttributes.setTableSpaceName(null);
        physicalAttributes.setCompressOrNoCompress(null);
        physicalAttributes.setNoSortOrReverse(null);
        physicalAttributes.setPadIndex(null);
        physicalAttributes.setFillFactor(null);
        physicalAttributes.setFillFactorValue(null);
        physicalAttributes.setDropExisting(null);
        physicalAttributes.setStatisticsNoreCompute(null);
        physicalAttributes.setSortInTempDb(null);
        physicalAttributes.setWith(null);
        physicalAttributes.setDiskAttr(null);
        return physicalAttributes;
    }
    
    public String removeIndent(String str) {
        str = str.replace('\n', ' ');
        str = str.replace('\t', ' ');
        return str;
    }
    
    public PhysicalAttributesClause copyObjectValues() {
        final PhysicalAttributesClause dupPhysicalAttributesClause = new PhysicalAttributesClause();
        dupPhysicalAttributesClause.setPctFree(this.pctfree);
        dupPhysicalAttributesClause.setPctFreeValue(this.pctfreeValue);
        dupPhysicalAttributesClause.setPctUsed(this.pctused);
        dupPhysicalAttributesClause.setPctUsedValue(this.pctUsedValue);
        dupPhysicalAttributesClause.setIniTrans(this.initrans);
        dupPhysicalAttributesClause.setMaxTrans(this.maxtrans);
        dupPhysicalAttributesClause.setStorageClause(this.storageClause);
        dupPhysicalAttributesClause.setNoCache(this.noCache);
        dupPhysicalAttributesClause.setLoggingOrNoLogging(this.loggingOrNoLogging);
        dupPhysicalAttributesClause.setOnline(this.online);
        dupPhysicalAttributesClause.setCompute(this.compute);
        dupPhysicalAttributesClause.setTableSpaceOrDefault(this.tableSpaceOrDefault);
        dupPhysicalAttributesClause.setTableSpaceName(this.tableSpaceName);
        dupPhysicalAttributesClause.setCompressOrNoCompress(this.compressOrNoCompress);
        dupPhysicalAttributesClause.setNoSortOrReverse(this.noSortOrReverse);
        dupPhysicalAttributesClause.setPadIndex(this.padIndex);
        dupPhysicalAttributesClause.setFillFactor(this.fillfactor);
        dupPhysicalAttributesClause.setFillFactorValue(this.fillfactorValue);
        dupPhysicalAttributesClause.setStatisticsNoreCompute(this.statisticsNoreCompute);
        dupPhysicalAttributesClause.setSortInTempDb(this.sortInTempDb);
        dupPhysicalAttributesClause.setDropExisting(this.dropExisting);
        dupPhysicalAttributesClause.setWith(this.with);
        dupPhysicalAttributesClause.setDiskAttr(this.diskAttr);
        return dupPhysicalAttributesClause;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.pctfree != null) {
            sb.append(this.pctfree.toUpperCase() + " ");
        }
        if (this.pctfreeValue != null) {
            sb.append(this.pctfreeValue.toUpperCase() + " ");
        }
        if (this.pctused != null) {
            sb.append(this.pctused.toUpperCase() + " ");
        }
        if (this.pctUsedValue != null) {
            sb.append(this.pctUsedValue + " ");
        }
        if (this.initrans != null) {
            sb.append(this.initrans.toUpperCase() + " ");
        }
        if (this.maxtrans != null) {
            sb.append(this.maxtrans.toUpperCase() + " ");
        }
        if (this.storageClause != null) {
            sb.append(this.storageClause.toUpperCase() + " ");
        }
        if (this.noCache != null) {
            sb.append(this.noCache.toUpperCase() + " ");
        }
        if (this.online != null) {
            sb.append(this.online.toUpperCase() + " ");
        }
        if (this.loggingOrNoLogging != null) {
            sb.append(this.loggingOrNoLogging.toUpperCase() + " ");
        }
        if (this.compute != null) {
            sb.append(this.compute.toUpperCase() + " ");
        }
        if (this.tableSpaceOrDefault != null) {
            sb.append(this.tableSpaceOrDefault.toUpperCase() + " ");
        }
        if (this.tableSpaceName != null) {
            sb.append(this.tableSpaceName + " ");
        }
        if (this.noSortOrReverse != null) {
            sb.append(this.noSortOrReverse.toUpperCase() + " ");
        }
        if (this.compressOrNoCompress != null) {
            sb.append(this.compressOrNoCompress.toUpperCase() + " ");
        }
        if (this.padIndex != null) {
            sb.append(this.padIndex.toUpperCase() + " ");
        }
        if (this.fillfactor != null) {
            sb.append(this.fillfactor.toUpperCase() + "  ");
        }
        if (this.fillfactorValue != null) {
            sb.append(this.fillfactorValue.toUpperCase() + " ");
        }
        if (this.dropExisting != null) {
            sb.append(this.dropExisting.toUpperCase() + " ");
        }
        if (this.statisticsNoreCompute != null) {
            sb.append(this.statisticsNoreCompute.toUpperCase() + " ");
        }
        if (this.sortInTempDb != null) {
            sb.append(this.sortInTempDb.toUpperCase() + " ");
        }
        if (this.with != null) {
            sb.append(this.with.toUpperCase() + " ");
        }
        if (this.diskAttr != null && this.diskAttr.size() > 0) {
            final Set keys = this.diskAttr.keySet();
            final Iterator it = keys.iterator();
            boolean start = true;
            while (it.hasNext()) {
                if (!start) {
                    sb.append(", ");
                }
                final Object obj = it.next();
                if (this.diskAttr.get(obj).equals("")) {
                    sb.append(obj.toString().toUpperCase());
                }
                else {
                    sb.append(obj.toString().toUpperCase() + " = " + this.diskAttr.get(obj));
                }
                start = false;
            }
        }
        return sb.toString();
    }
}
