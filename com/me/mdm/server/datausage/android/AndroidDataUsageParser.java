package com.me.mdm.server.datausage.android;

import com.me.mdm.server.datausage.data.DataEntity;
import java.util.ArrayList;
import java.util.Collection;
import com.me.mdm.server.datausage.DataUsageConstants;
import com.me.mdm.server.datausage.data.DataPeriod;
import org.json.JSONArray;
import com.me.mdm.server.datausage.data.DataUsageHistory;
import java.util.List;
import org.json.JSONObject;
import com.me.mdm.server.datausage.DataUsageParsingInterface;

public class AndroidDataUsageParser implements DataUsageParsingInterface
{
    @Override
    public void parseDataUsageSummary(final Long resourceID, final JSONObject data, final List<DataUsageHistory> dataUsageSummaries) throws Exception {
        final String msgRequest = String.valueOf(data.get("MsgRequestType"));
        if (msgRequest.equals("DataUsageMessage")) {
            final JSONArray dataUsages = new JSONArray(new JSONObject(data.get("MsgRequest").toString()).get("DataUsages").toString());
            for (int i = 0; i < dataUsages.length(); ++i) {
                final JSONObject dataUsage = dataUsages.getJSONObject(i);
                final Long usageFrom = dataUsage.getLong("UsageFrom");
                final Long usageTo = dataUsage.getLong("UsageTo");
                final Long computerTime = dataUsage.getLong("AgentComputedTime");
                final DataPeriod period = new DataPeriod();
                period.startTime = usageFrom;
                period.endTime = usageTo;
                final JSONObject deviceUsage = dataUsage.optJSONObject("FullDeviceUsage");
                dataUsageSummaries.addAll(this.getHistoryObjs(resourceID, deviceUsage, "data.device.full", period, DataUsageConstants.DataUsages.DataEntities.NO_BIAS, computerTime));
                final JSONObject managedUsage = dataUsage.optJSONObject("TotalManagedAppUsage");
                dataUsageSummaries.addAll(this.getHistoryObjs(resourceID, managedUsage, "data.device.managed", period, DataUsageConstants.DataUsages.DataEntities.NO_BIAS, computerTime));
                final JSONObject unmanagedUsage = dataUsage.optJSONObject("TotalUnManagedAppUsage");
                dataUsageSummaries.addAll(this.getHistoryObjs(resourceID, unmanagedUsage, "data.device.unmanaged", period, DataUsageConstants.DataUsages.DataEntities.NO_BIAS, computerTime));
            }
        }
    }
    
    @Override
    public void parsePerAppUsageSummary(final Long resourceID, final JSONObject data, final List<DataUsageHistory> dataUsageHistories) throws Exception {
        final String msgRequest = String.valueOf(data.get("MsgRequestType"));
        if (msgRequest.equals("DetailedDataUsageMessage")) {
            final JSONArray dataUsages = new JSONArray(new JSONObject(data.get("MsgRequest").toString()).get("DataUsages").toString());
            for (int i = 0; i < dataUsages.length(); ++i) {
                final JSONObject dataUsage = dataUsages.getJSONObject(i);
                final Long usageFrom = dataUsage.getLong("UsageFrom");
                final Long usageTo = dataUsage.getLong("UsageTo");
                final Long computerTime = dataUsage.getLong("AgentComputedTime");
                final DataPeriod period = new DataPeriod();
                period.startTime = usageFrom;
                period.endTime = usageTo;
                final JSONArray perAppUsages = dataUsage.optJSONArray("PerAppUsages");
                for (int j = 0; j < perAppUsages.length(); ++j) {
                    final JSONObject curAppUsage = perAppUsages.getJSONObject(j);
                    dataUsageHistories.addAll(this.getHistoryObjs(resourceID, curAppUsage, String.valueOf(curAppUsage.get("PackageName")), period, DataUsageConstants.DataUsages.DataEntities.APP_TYPE, computerTime));
                }
            }
        }
    }
    
    private List<DataUsageHistory> getHistoryObjs(final Long resourceID, final JSONObject dataSummary, final String identifier, final DataPeriod dataPeriod, final int entityBias, final Long agentComputedTime) throws Exception {
        final List<DataUsageHistory> dataUsageSummaries = new ArrayList<DataUsageHistory>();
        if (dataSummary != null) {
            final Double totalUsage = dataSummary.getDouble("Total");
            final Double roamingUsage = dataSummary.optDouble("Roaming");
            final Double wifi = dataSummary.optDouble("WiFi");
            final Double mobiledata = dataSummary.optDouble("Mobile");
            dataUsageSummaries.add(this.getDataUsage(resourceID, dataPeriod, this.getEntity(identifier, entityBias | DataUsageConstants.DataUsages.DataEntities.TOTAL_TYPE), totalUsage, agentComputedTime));
            if (roamingUsage != null && !roamingUsage.isNaN()) {
                dataUsageSummaries.add(this.getDataUsage(resourceID, dataPeriod, this.getEntity(identifier, entityBias | DataUsageConstants.DataUsages.DataEntities.ROAMING_TYPE), roamingUsage, agentComputedTime));
            }
            if (wifi != null && !wifi.isNaN()) {
                dataUsageSummaries.add(this.getDataUsage(resourceID, dataPeriod, this.getEntity(identifier, entityBias | DataUsageConstants.DataUsages.DataEntities.WIFI_TYPE), wifi, agentComputedTime));
            }
            if (mobiledata != null && !mobiledata.isNaN()) {
                dataUsageSummaries.add(this.getDataUsage(resourceID, dataPeriod, this.getEntity(identifier, entityBias | DataUsageConstants.DataUsages.DataEntities.MOBILE_TYPE), mobiledata, agentComputedTime));
            }
        }
        return dataUsageSummaries;
    }
    
    private DataEntity getEntity(final String identifier, final Integer type) {
        final DataEntity dataEntity = new DataEntity();
        dataEntity.identifier = identifier;
        dataEntity.type = type;
        return dataEntity;
    }
    
    private DataUsageHistory getDataUsage(final Long resourceID, final DataPeriod dataPeriod, final DataEntity dataEntity, final Double usage, final Long agentComputedTime) {
        final DataUsageHistory dataUsageHistory = new DataUsageHistory();
        dataUsageHistory.dataPeriod = dataPeriod;
        dataUsageHistory.dataEntity = dataEntity;
        dataUsageHistory.usage = usage;
        dataUsageHistory.reportedTime = System.currentTimeMillis();
        dataUsageHistory.resourceID = resourceID;
        dataUsageHistory.agentComputedTime = agentComputedTime;
        return dataUsageHistory;
    }
}
