package org.apache.poi.ddf;

import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.Map;
import java.util.function.Supplier;

public enum EscherRecordTypes
{
    DGG_CONTAINER(61440, "DggContainer", (String)null, (Supplier<? extends EscherRecord>)EscherContainerRecord::new), 
    BSTORE_CONTAINER(61441, "BStoreContainer", (String)null, (Supplier<? extends EscherRecord>)EscherContainerRecord::new), 
    DG_CONTAINER(61442, "DgContainer", (String)null, (Supplier<? extends EscherRecord>)EscherContainerRecord::new), 
    SPGR_CONTAINER(61443, "SpgrContainer", (String)null, (Supplier<? extends EscherRecord>)EscherContainerRecord::new), 
    SP_CONTAINER(61444, "SpContainer", (String)null, (Supplier<? extends EscherRecord>)EscherContainerRecord::new), 
    SOLVER_CONTAINER(61445, "SolverContainer", (String)null, (Supplier<? extends EscherRecord>)EscherContainerRecord::new), 
    DGG(61446, "Dgg", "MsofbtDgg", (Supplier<? extends EscherRecord>)EscherDggRecord::new), 
    BSE(61447, "BSE", "MsofbtBSE", (Supplier<? extends EscherRecord>)EscherBSERecord::new), 
    DG(61448, "Dg", "MsofbtDg", (Supplier<? extends EscherRecord>)EscherDgRecord::new), 
    SPGR(61449, "Spgr", "MsofbtSpgr", (Supplier<? extends EscherRecord>)EscherSpgrRecord::new), 
    SP(61450, "Sp", "MsofbtSp", (Supplier<? extends EscherRecord>)EscherSpRecord::new), 
    OPT(61451, "Opt", "msofbtOPT", (Supplier<? extends EscherRecord>)EscherOptRecord::new), 
    TEXTBOX(61452, (String)null, (String)null, (Supplier<? extends EscherRecord>)EscherTextboxRecord::new), 
    CLIENT_TEXTBOX(61453, "ClientTextbox", "msofbtClientTextbox", (Supplier<? extends EscherRecord>)EscherTextboxRecord::new), 
    ANCHOR(61454, (String)null, (String)null, (Supplier<? extends EscherRecord>)null), 
    CHILD_ANCHOR(61455, "ChildAnchor", "MsofbtChildAnchor", (Supplier<? extends EscherRecord>)EscherChildAnchorRecord::new), 
    CLIENT_ANCHOR(61456, "ClientAnchor", "MsofbtClientAnchor", (Supplier<? extends EscherRecord>)EscherClientAnchorRecord::new), 
    CLIENT_DATA(61457, "ClientData", "MsofbtClientData", (Supplier<? extends EscherRecord>)EscherClientDataRecord::new), 
    CONNECTOR_RULE(61458, (String)null, (String)null, (Supplier<? extends EscherRecord>)null), 
    ALIGN_RULE(61459, (String)null, (String)null, (Supplier<? extends EscherRecord>)null), 
    ARC_RULE(61460, (String)null, (String)null, (Supplier<? extends EscherRecord>)null), 
    CLIENT_RULE(61461, (String)null, (String)null, (Supplier<? extends EscherRecord>)null), 
    CLSID(61462, (String)null, (String)null, (Supplier<? extends EscherRecord>)null), 
    CALLOUT_RULE(61463, (String)null, (String)null, (Supplier<? extends EscherRecord>)null), 
    BLIP_START(61464, "Blip", "msofbtBlip", (Supplier<? extends EscherRecord>)null), 
    BLIP_EMF(61466, "BlipEmf", (String)null, (Supplier<? extends EscherRecord>)EscherMetafileBlip::new), 
    BLIP_WMF(61467, "BlipWmf", (String)null, (Supplier<? extends EscherRecord>)EscherMetafileBlip::new), 
    BLIP_PICT(61468, "BlipPict", (String)null, (Supplier<? extends EscherRecord>)EscherMetafileBlip::new), 
    BLIP_JPEG(61469, "BlipJpeg", (String)null, (Supplier<? extends EscherRecord>)EscherBitmapBlip::new), 
    BLIP_PNG(61470, "BlipPng", (String)null, (Supplier<? extends EscherRecord>)EscherBitmapBlip::new), 
    BLIP_DIB(61471, "BlipDib", (String)null, (Supplier<? extends EscherRecord>)EscherBitmapBlip::new), 
    BLIP_END(61719, "Blip", "msofbtBlip", (Supplier<? extends EscherRecord>)null), 
    REGROUP_ITEMS(61720, (String)null, (String)null, (Supplier<? extends EscherRecord>)null), 
    SELECTION(61721, (String)null, (String)null, (Supplier<? extends EscherRecord>)null), 
    COLOR_MRU(61722, (String)null, (String)null, (Supplier<? extends EscherRecord>)null), 
    DELETED_PSPL(61725, (String)null, (String)null, (Supplier<? extends EscherRecord>)null), 
    SPLIT_MENU_COLORS(61726, "SplitMenuColors", "MsofbtSplitMenuColors", (Supplier<? extends EscherRecord>)EscherSplitMenuColorsRecord::new), 
    OLE_OBJECT(61727, (String)null, (String)null, (Supplier<? extends EscherRecord>)null), 
    COLOR_SCHEME(61728, (String)null, (String)null, (Supplier<? extends EscherRecord>)null), 
    USER_DEFINED(61730, "TertiaryOpt", (String)null, (Supplier<? extends EscherRecord>)EscherTertiaryOptRecord::new), 
    UNKNOWN(65535, "unknown", "unknown", (Supplier<? extends EscherRecord>)UnknownEscherRecord::new);
    
    public final short typeID;
    public final String recordName;
    public final String description;
    public final Supplier<? extends EscherRecord> constructor;
    private static final Map<Short, EscherRecordTypes> LOOKUP;
    
    private EscherRecordTypes(final int typeID, final String recordName, final String description, final Supplier<? extends EscherRecord> constructor) {
        this.typeID = (short)typeID;
        this.recordName = recordName;
        this.description = description;
        this.constructor = constructor;
    }
    
    private Short getTypeId() {
        return this.typeID;
    }
    
    public static EscherRecordTypes forTypeID(final int typeID) {
        final EscherRecordTypes rt = EscherRecordTypes.LOOKUP.get((short)typeID);
        return (rt != null) ? rt : EscherRecordTypes.UNKNOWN;
    }
    
    static {
        LOOKUP = Stream.of(values()).collect(Collectors.toMap((Function<? super EscherRecordTypes, ? extends Short>)EscherRecordTypes::getTypeId, (Function<? super EscherRecordTypes, ? extends EscherRecordTypes>)Function.identity()));
    }
}
