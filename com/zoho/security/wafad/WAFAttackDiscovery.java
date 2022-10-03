package com.zoho.security.wafad;

import java.util.Collection;
import java.util.HashMap;
import com.zoho.security.instrumentation.datapointinfo.MethodReturnData;
import com.zoho.security.instrumentation.datapointinfo.FieldData;
import com.zoho.security.instrumentation.datapointinfo.ThrowData;
import com.zoho.security.instrumentation.datapointinfo.DirectConstantData;
import com.zoho.security.instrumentation.datapointinfo.ReturnData;
import com.zoho.security.instrumentation.datapointinfo.ArgumentData;
import com.zoho.security.instrumentation.datapointinfo.CurrentInstanceData;
import com.zoho.security.instrumentation.datapointinfo.DataFieldInfo;
import com.zoho.security.instrumentation.target.BodyTargetInfo;
import com.zoho.security.instrumentation.matcher.MethodMatcher;
import com.zoho.security.instrumentation.matcher.StaticBlockMatcher;
import com.zoho.security.instrumentation.matcher.ConstructorMatcher;
import com.zoho.security.instrumentation.WAFInstrumentUtil;
import org.json.JSONArray;
import com.zoho.security.instrumentation.target.TargetInfo;
import com.zoho.security.instrumentation.matcher.BehaviorMatcher;
import com.zoho.security.instrumentation.matcher.ClassMatcher;
import java.util.Iterator;
import org.json.JSONException;
import com.zoho.security.instrumentation.WAFInstrumentException;
import com.zoho.security.instrumentation.WAFInstrument;
import org.json.JSONObject;
import com.zoho.security.attackdiscovery.AttackDiscoveryName;
import java.util.List;
import com.zoho.security.instrumentation.WAFInstrumentInfo;
import java.util.Map;

public class WAFAttackDiscovery
{
    private static final Map<String, WAFInstrumentInfo> ID_TO_WAFINSTRUMENTINFO;
    private static final String ATTACK_DISCOVERY_INFO_FIELD = "attack-discovery-info";
    private static final String INSTRUMENT_INFO_FIELD = "instrument-info";
    private static List<AttackDiscoveryName> serviceAttackDiscoveryInfos;
    
    public static void add(final JSONObject adInstrumentInfo) throws WAFInstrumentException, JSONException, ClassNotFoundException {
        final JSONObject attackDiscoverInfoObj = adInstrumentInfo.getJSONObject("attack-discovery-info");
        final String id = attackDiscoverInfoObj.getString(ATTACK_DISCOVERY_INFO.ID.value);
        if (WAFAttackDiscovery.ID_TO_WAFINSTRUMENTINFO.containsKey(id)) {
            throw new IllegalArgumentException("Attack discovery ID \"" + id + "\" has already been mapped");
        }
        final String attackDiscoveryName = attackDiscoverInfoObj.getString(ATTACK_DISCOVERY_INFO.NAME.value);
        final AttackDiscoveryName attackDiscoveryInfo = getAttackDiscoveryInfoByName(attackDiscoveryName);
        WAFInstrumentInfo instrumentInfo = toWAFInstrumentInfo(attackDiscoveryInfo, adInstrumentInfo);
        WAFInstrument.add(instrumentInfo);
        instrumentInfo = WAFAttackDiscovery.ID_TO_WAFINSTRUMENTINFO.put(id, instrumentInfo);
    }
    
    public static void update(final JSONObject adInstrumentInfo) throws WAFInstrumentException, JSONException, ClassNotFoundException {
        final JSONObject attackDiscoverInfoObj = adInstrumentInfo.getJSONObject("attack-discovery-info");
        final String id = attackDiscoverInfoObj.getString(ATTACK_DISCOVERY_INFO.ID.value);
        final WAFInstrumentInfo oldInstrumentInfo = WAFAttackDiscovery.ID_TO_WAFINSTRUMENTINFO.get(id);
        final String attackDiscoveryName = attackDiscoverInfoObj.getString(ATTACK_DISCOVERY_INFO.NAME.value);
        final AttackDiscoveryName attackDiscoveryInfo = getAttackDiscoveryInfoByName(attackDiscoveryName);
        final WAFInstrumentInfo newInstrumentInfo = toWAFInstrumentInfo(attackDiscoveryInfo, adInstrumentInfo);
        if (oldInstrumentInfo == null) {
            WAFInstrument.add(newInstrumentInfo);
        }
        else {
            WAFInstrument.replace(oldInstrumentInfo, newInstrumentInfo);
        }
        WAFAttackDiscovery.ID_TO_WAFINSTRUMENTINFO.put(id, newInstrumentInfo);
    }
    
    public static void remove(final JSONObject adInstrumentInfo) throws WAFInstrumentException {
        final JSONObject attackDiscoverInfoObj = adInstrumentInfo.getJSONObject("attack-discovery-info");
        final String id = attackDiscoverInfoObj.getString(ATTACK_DISCOVERY_INFO.ID.value);
        final WAFInstrumentInfo instrumentInfo = WAFAttackDiscovery.ID_TO_WAFINSTRUMENTINFO.get(id);
        if (instrumentInfo == null) {
            throw new IllegalArgumentException("No mapping was found for attack discovery ID \"" + id + "\"");
        }
        WAFInstrument.delete(instrumentInfo);
        WAFAttackDiscovery.ID_TO_WAFINSTRUMENTINFO.remove(id);
    }
    
    public static JSONObject getWAFAttackDiscoveryInfosAsJSON() {
        final JSONObject wafInstrumentInfos = new JSONObject();
        for (final Map.Entry<String, WAFInstrumentInfo> idToWafInstrumentInfoEntry : WAFAttackDiscovery.ID_TO_WAFINSTRUMENTINFO.entrySet()) {
            wafInstrumentInfos.put((String)idToWafInstrumentInfoEntry.getKey(), (Object)toJSONObject(idToWafInstrumentInfoEntry.getValue()));
        }
        return wafInstrumentInfos;
    }
    
    private static WAFInstrumentInfo toWAFInstrumentInfo(final AttackDiscoveryName attackDiscoveryInfo, final JSONObject adInstrumentInfo) throws WAFInstrumentException, JSONException, ClassNotFoundException {
        final JSONObject instrumentInfoObj = adInstrumentInfo.getJSONObject("instrument-info");
        final ClassMatcher classMatcher = new ClassMatcher((Class)classForName(instrumentInfoObj.getString(INSTRUMENT_INFO.CLASS_NAME.value)));
        final BehaviorMatcher behaviorMatcher = createBehaviorMatcher(classMatcher, instrumentInfoObj);
        final WAFInstrumentInfo.InstrumentType instrumentType = WAFInstrumentInfo.InstrumentType.valueOf(instrumentInfoObj.getString(INSTRUMENT_INFO.TYPE.value));
        final TargetInfo targetPointInfo = createTargetInfo(classMatcher, behaviorMatcher, instrumentInfoObj.getJSONObject(INSTRUMENT_INFO.TARGET_INFO.value));
        final WAFInstrumentInfo wafInstrumentInfo = new WAFInstrumentInfo(attackDiscoveryInfo.getAttackDiscoveryClass(), instrumentType, targetPointInfo);
        final JSONArray dataFieldInfoArray = instrumentInfoObj.optJSONArray(INSTRUMENT_INFO.DATA_FIELD_INFO.value);
        if (dataFieldInfoArray != null) {
            for (int i = 0; i < dataFieldInfoArray.length(); ++i) {
                final JSONObject dataFieldInfoObj = dataFieldInfoArray.getJSONObject(i);
                wafInstrumentInfo.addDataFieldInfo(dataFieldInfoObj.getString(DATA_FIELD_INFO.NAME.value), createDataFieldInfo(classMatcher, dataFieldInfoObj));
            }
        }
        return wafInstrumentInfo;
    }
    
    private static BehaviorMatcher createBehaviorMatcher(final ClassMatcher classMatcher, final JSONObject instrumentInfoObj) {
        final String methodNameWithSig = instrumentInfoObj.getString(INSTRUMENT_INFO.METHOD.value);
        final String methodName = WAFInstrumentUtil.getMethodName(methodNameWithSig);
        if (methodName.isEmpty() || "<init>".equals(methodName) || classMatcher.getClazz().getSimpleName().equals(methodName)) {
            return (BehaviorMatcher)new ConstructorMatcher(WAFInstrumentUtil.getSignature(methodNameWithSig));
        }
        if ("<clinit>".equals(methodName)) {
            return (BehaviorMatcher)new StaticBlockMatcher();
        }
        return (BehaviorMatcher)new MethodMatcher(methodNameWithSig);
    }
    
    private static TargetInfo createTargetInfo(final ClassMatcher classMatcher, final BehaviorMatcher behaviorMatcher, final JSONObject targetInfoObj) {
        final TargetInfo.TargetType pointType = TargetInfo.TargetType.valueOf(targetInfoObj.getString(TARGET_INFO.TYPE.value));
        boolean after = false;
        if (targetInfoObj.has(TARGET_INFO.AFTER.value)) {
            after = targetInfoObj.getBoolean(TARGET_INFO.AFTER.value);
        }
        switch (pointType) {
            case METHOD_BODY: {
                return (TargetInfo)new BodyTargetInfo(classMatcher, behaviorMatcher, after);
            }
            default: {
                return null;
            }
        }
    }
    
    private static DataFieldInfo createDataFieldInfo(final ClassMatcher classMatcher, final JSONObject dataFieldInfoObj) throws JSONException, WAFInstrumentException, ClassNotFoundException {
        final DataFieldInfo.DATA_FIELD_TYPE dataPointType = DataFieldInfo.DATA_FIELD_TYPE.valueOf(dataFieldInfoObj.getString(DATA_FIELD_INFO.TYPE.value));
        Class<?> classType = null;
        if (dataFieldInfoObj.has(DATA_FIELD_INFO.CLASS_TYPE.value)) {
            classType = getSignatureType(dataFieldInfoObj.getString(DATA_FIELD_INFO.CLASS_TYPE.value));
        }
        switch (dataPointType) {
            case CURRENT_INSTANCE: {
                return (DataFieldInfo)new CurrentInstanceData();
            }
            case ARGUMENT: {
                return (DataFieldInfo)new ArgumentData((Class)classType, dataFieldInfoObj.getInt(DATA_FIELD_INFO.ARGUMENT_INDEX.value));
            }
            case RETURN: {
                return (DataFieldInfo)new ReturnData((Class)classType);
            }
            case DIRECT_CONSTANT: {
                return (DataFieldInfo)new DirectConstantData(dataFieldInfoObj.get(DATA_FIELD_INFO.CONSTANT_DATA.value), (Class)classType);
            }
            case THROW: {
                final Class<? extends Throwable> throwType = (Class<? extends Throwable>)classType;
                return (DataFieldInfo)new ThrowData((Class)throwType);
            }
            case FIELD: {
                final Class<?> ownerClass = dataFieldInfoObj.has(DATA_FIELD_INFO.OWNER_TYPE.value) ? classForName(dataFieldInfoObj.getString(DATA_FIELD_INFO.OWNER_TYPE.value)) : classMatcher.getClazz();
                final boolean isStatic = dataFieldInfoObj.has(DATA_FIELD_INFO.FIELD_STATIC.value) && dataFieldInfoObj.getBoolean(DATA_FIELD_INFO.FIELD_STATIC.value);
                return (DataFieldInfo)new FieldData((Class)ownerClass, dataFieldInfoObj.getString(DATA_FIELD_INFO.FIELD_NAME.value), isStatic, (Class)classType);
            }
            case METHOD: {
                final Class<?> ownerClass = dataFieldInfoObj.has(DATA_FIELD_INFO.OWNER_TYPE.value) ? classForName(dataFieldInfoObj.getString(DATA_FIELD_INFO.OWNER_TYPE.value)) : classMatcher.getClazz();
                if (dataFieldInfoObj.has(DATA_FIELD_INFO.METHOD_TYPE.value)) {
                    final MethodReturnData.METHOD_TYPE methodInvokeType = MethodReturnData.METHOD_TYPE.valueOf(dataFieldInfoObj.getString(DATA_FIELD_INFO.METHOD_TYPE.value));
                    return (DataFieldInfo)new MethodReturnData((Class)ownerClass, dataFieldInfoObj.getString(DATA_FIELD_INFO.METHOD_NAME.value), methodInvokeType, (Class)classType);
                }
                return (DataFieldInfo)new MethodReturnData((Class)ownerClass, dataFieldInfoObj.getString(DATA_FIELD_INFO.METHOD_NAME.value), (Class)classType);
            }
            default: {
                return null;
            }
        }
    }
    
    private static JSONObject toJSONObject(final WAFInstrumentInfo instrumentInfo) {
        final JSONObject wafInstrumentInfoObj = new JSONObject();
        final JSONObject attackDiscoverInfoObj = new JSONObject();
        wafInstrumentInfoObj.put("attack-discovery-info", (Object)attackDiscoverInfoObj);
        attackDiscoverInfoObj.put(ATTACK_DISCOVERY_INFO.NAME.value, (Object)getAttackDiscoveryInfoByClass(instrumentInfo.getWafInstrumentClassLookupName()));
        final JSONObject instrumentInfoObj = new JSONObject();
        wafInstrumentInfoObj.put("instrument-info", (Object)instrumentInfoObj);
        instrumentInfoObj.put(INSTRUMENT_INFO.TYPE.value, (Object)instrumentInfo.getInstrumentType().name());
        final TargetInfo targetPointInfo = instrumentInfo.getTargetInfo();
        instrumentInfoObj.put(INSTRUMENT_INFO.CLASS_NAME.value, (Object)targetPointInfo.getTargetClassMatcher().getClassName());
        final BehaviorMatcher bm = targetPointInfo.getBehaviorMatcher();
        if (bm instanceof MethodMatcher) {
            final MethodMatcher mm = (MethodMatcher)bm;
            instrumentInfoObj.put(INSTRUMENT_INFO.METHOD.value, (Object)(mm.getMethodName() + mm.getSignature()));
        }
        else if (bm instanceof ConstructorMatcher) {
            final ConstructorMatcher cm = (ConstructorMatcher)bm;
            instrumentInfoObj.put(INSTRUMENT_INFO.METHOD.value, (Object)(targetPointInfo.getTargetClassMatcher().getClazz().getSimpleName() + cm.getSignature()));
        }
        else if (bm instanceof StaticBlockMatcher) {
            instrumentInfoObj.put(INSTRUMENT_INFO.METHOD.value, (Object)"<clinit>()");
        }
        final JSONObject targetInfoObj = new JSONObject();
        instrumentInfoObj.put(INSTRUMENT_INFO.TARGET_INFO.value, (Object)targetInfoObj);
        targetInfoObj.put(TARGET_INFO.TYPE.value, (Object)targetPointInfo.getTargetType().name());
        targetInfoObj.put(TARGET_INFO.AFTER.value, targetPointInfo.isAfter());
        final Map<String, DataFieldInfo> dataPointInfos = instrumentInfo.getTargetDataFieldInfos();
        if (dataPointInfos != null && !dataPointInfos.isEmpty()) {
            final JSONArray dataFieldInfoArray = new JSONArray();
            instrumentInfoObj.put(INSTRUMENT_INFO.DATA_FIELD_INFO.value, (Object)dataFieldInfoArray);
            for (final Map.Entry<String, DataFieldInfo> dataPointInfoEntry : dataPointInfos.entrySet()) {
                final DataFieldInfo dataPointInfo = dataPointInfoEntry.getValue();
                final JSONObject dataFieldInfoObj = new JSONObject();
                dataFieldInfoObj.put(DATA_FIELD_INFO.NAME.value, (Object)dataPointInfoEntry.getKey());
                dataFieldInfoObj.put(DATA_FIELD_INFO.TYPE.value, (Object)dataPointInfo.getDataFieldType().name());
                dataFieldInfoObj.put(DATA_FIELD_INFO.CLASS_TYPE.value, (Object)dataPointInfo.getType().getCanonicalName());
                switch (dataPointInfo.getDataFieldType()) {
                    case ARGUMENT: {
                        final ArgumentData argumentDataPointInfo = (ArgumentData)dataPointInfo;
                        dataFieldInfoObj.put(DATA_FIELD_INFO.ARGUMENT_INDEX.value, argumentDataPointInfo.getArgumentIndex());
                        break;
                    }
                    case DIRECT_CONSTANT: {
                        final DirectConstantData directConstantData = (DirectConstantData)dataPointInfo;
                        dataFieldInfoObj.put(DATA_FIELD_INFO.CONSTANT_DATA.value, directConstantData.getData());
                        break;
                    }
                    case FIELD: {
                        final FieldData fieldDataPointInfo = (FieldData)dataPointInfo;
                        dataFieldInfoObj.put(DATA_FIELD_INFO.OWNER_TYPE.value, (Object)fieldDataPointInfo.getOwnerClass().getName());
                        dataFieldInfoObj.put(DATA_FIELD_INFO.FIELD_NAME.value, (Object)fieldDataPointInfo.getFieldName());
                        dataFieldInfoObj.put(DATA_FIELD_INFO.FIELD_STATIC.value, fieldDataPointInfo.isStatic());
                        break;
                    }
                    case METHOD: {
                        final MethodReturnData methodInvokeDataPointInfo = (MethodReturnData)dataPointInfo;
                        dataFieldInfoObj.put(DATA_FIELD_INFO.OWNER_TYPE.value, (Object)methodInvokeDataPointInfo.getOwnerClass().getName());
                        dataFieldInfoObj.put(DATA_FIELD_INFO.METHOD_NAME.value, (Object)(methodInvokeDataPointInfo.getMethod().getMethodName() + methodInvokeDataPointInfo.getMethod().getSignature()));
                        dataFieldInfoObj.put(DATA_FIELD_INFO.METHOD_TYPE.value, (Object)methodInvokeDataPointInfo.getMethodInvokeType().name());
                        break;
                    }
                }
                dataFieldInfoArray.put((Object)dataFieldInfoObj);
            }
        }
        return wafInstrumentInfoObj;
    }
    
    private static AttackDiscoveryName getAttackDiscoveryInfoByName(final String attackDiscoveryName) {
        if (WAFAttackDiscovery.serviceAttackDiscoveryInfos != null) {
            for (final AttackDiscoveryName info : WAFAttackDiscovery.serviceAttackDiscoveryInfos) {
                if (info.getName().equals(attackDiscoveryName)) {
                    return info;
                }
            }
        }
        return (AttackDiscoveryName)WAFAttackDiscoveries.valueOf(attackDiscoveryName);
    }
    
    private static String getAttackDiscoveryInfoByClass(final String name) {
        if (WAFAttackDiscovery.serviceAttackDiscoveryInfos != null) {
            for (final AttackDiscoveryName attackDiscoveryInfo : WAFAttackDiscovery.serviceAttackDiscoveryInfos) {
                if (attackDiscoveryInfo.getAttackDiscoveryClass().getName().equals(name)) {
                    return attackDiscoveryInfo.getName();
                }
            }
        }
        for (final AttackDiscoveryName attackDiscoveryInfo2 : WAFAttackDiscoveries.values()) {
            if (attackDiscoveryInfo2.getAttackDiscoveryClass().getName().equals(name)) {
                return attackDiscoveryInfo2.getName();
            }
        }
        throw new IllegalStateException("Attack discovery info not found for attack discovery class \"" + name + "\"");
    }
    
    public static void addLocalCatchWAFAttackDiscoveryInfo(final JSONObject adInstrumentInfos) throws JSONException, ClassNotFoundException, WAFInstrumentException {
        final Map<String, WAFInstrumentInfo> idToWAFInstrumentInfo = new HashMap<String, WAFInstrumentInfo>();
        for (final String id : adInstrumentInfos.keySet()) {
            final JSONObject adInstrumentInfo = adInstrumentInfos.getJSONObject(id);
            final JSONObject attackDiscoverInfoObj = adInstrumentInfo.getJSONObject("attack-discovery-info");
            final AttackDiscoveryName attackDiscoveryInfo = getAttackDiscoveryInfoByName(attackDiscoverInfoObj.getString(ATTACK_DISCOVERY_INFO.NAME.value));
            final WAFInstrumentInfo wafInstrumentInfo = toWAFInstrumentInfo(attackDiscoveryInfo, adInstrumentInfo);
            idToWAFInstrumentInfo.put(id, wafInstrumentInfo);
        }
        WAFInstrument.add((Collection)idToWAFInstrumentInfo.values());
        WAFAttackDiscovery.ID_TO_WAFINSTRUMENTINFO.putAll(idToWAFInstrumentInfo);
    }
    
    public static void setAttackDiscoveryInfos(final AttackDiscoveryProvider attackDiscoveryProvider) {
        WAFAttackDiscovery.serviceAttackDiscoveryInfos = attackDiscoveryProvider.getAllAttackDiscoveryInfos();
    }
    
    private static Class<?> classForName(final String className) throws ClassNotFoundException {
        return WAFInstrumentUtil.classForName(className, WAFAttackDiscovery.class.getClassLoader());
    }
    
    private static Class<?> getSignatureType(final String signature) throws WAFInstrumentException {
        return WAFInstrumentUtil.getDescriptor(signature, WAFAttackDiscovery.class.getClassLoader());
    }
    
    static {
        ID_TO_WAFINSTRUMENTINFO = new HashMap<String, WAFInstrumentInfo>();
        WAFAttackDiscoveryUtil.initEventXML();
    }
    
    private enum ATTACK_DISCOVERY_INFO
    {
        ID("id"), 
        NAME("name");
        
        private final String value;
        
        private ATTACK_DISCOVERY_INFO(final String value) {
            this.value = value;
        }
    }
    
    private enum INSTRUMENT_INFO
    {
        CLASS_NAME("class-name"), 
        METHOD("method"), 
        TYPE("type"), 
        TARGET_INFO("target-info"), 
        DATA_FIELD_INFO("data-field-info");
        
        private final String value;
        
        private INSTRUMENT_INFO(final String value) {
            this.value = value;
        }
    }
    
    private enum TARGET_INFO
    {
        TYPE("type"), 
        AFTER("after");
        
        private final String value;
        
        private TARGET_INFO(final String value) {
            this.value = value;
        }
    }
    
    private enum DATA_FIELD_INFO
    {
        NAME("name"), 
        TYPE("type"), 
        CLASS_TYPE("class-type"), 
        ARGUMENT_INDEX("argument-index"), 
        CONSTANT_DATA("constant-data"), 
        OWNER_TYPE("owner-type"), 
        FIELD_NAME("field-name"), 
        FIELD_STATIC("field-static"), 
        METHOD_NAME("method-name"), 
        METHOD_TYPE("method-type");
        
        private final String value;
        
        private DATA_FIELD_INFO(final String value) {
            this.value = value;
        }
    }
}
