package com.microsoft.sqlserver.jdbc;

class StreamDone extends StreamPacket
{
    private short status;
    private long rowCount;
    static final short CMD_SELECT = 193;
    static final short CMD_SELECTINTO = 194;
    static final short CMD_INSERT = 195;
    static final short CMD_DELETE = 196;
    static final short CMD_UPDATE = 197;
    static final short CMD_EXECUTE = 224;
    static final short CMD_BULKINSERT = 240;
    static final short CMD_MERGE = 279;
    static final short CMD_CNST_CREATE = 46;
    static final short CMD_DENY = 153;
    static final short CMD_DROPSCHEMA = 176;
    static final short CMD_FUNCCREATE = 178;
    static final short CMD_FUNCDESTROY = 179;
    static final short CMD_ASMCREATE = 181;
    static final short CMD_CMD = 182;
    static final short CMD_TABCREATE = 198;
    static final short CMD_TABDESTROY = 199;
    static final short CMD_INDCREATE = 200;
    static final short CMD_INDDESTROY = 201;
    static final short CMD_DBCREATE = 203;
    static final short CMD_DBDESTROY = 204;
    static final short CMD_GRANT = 205;
    static final short CMD_REVOKE = 206;
    static final short CMD_VIEWCREATE = 207;
    static final short CMD_VIEWDESTROY = 208;
    static final short CMD_DBEXTEND = 215;
    static final short CMD_ALTERTAB = 216;
    static final short CMD_TRIGCREATE = 221;
    static final short CMD_PROCCREATE = 222;
    static final short CMD_PROCDESTROY = 223;
    static final short CMD_TRIGDESTROY = 225;
    static final short CMD_DBCC_CMD = 230;
    static final short CMD_DEFAULTCREATE = 233;
    static final short CMD_RULECREATE = 236;
    static final short CMD_RULEDESTROY = 237;
    static final short CMD_DEFAULTDESTROY = 238;
    static final short CMD_STATSDESTROY = 256;
    static final short CMD_ASMDESTROY = 270;
    static final short CMD_ASMALTER = 271;
    static final short CMD_TYPEDESTROY = 272;
    static final short CMD_TYPECREATE = 273;
    static final short CMD_CLRPROCEDURECREATE = 274;
    static final short CMD_CLRFUNCTIONCREATE = 275;
    static final short CMD_SERVICEALTER = 276;
    static final short CMD_MSGTYPECREATE = 277;
    static final short CMD_MSGTYPEDESTROY = 278;
    static final short CMD_CONTRACTCREATE = 281;
    static final short CMD_CONTRACTDESTROY = 282;
    static final short CMD_SERVICECREATE = 283;
    static final short CMD_SERVICEDESTROY = 284;
    static final short CMD_QUEUECREATE = 285;
    static final short CMD_QUEUEDESTROY = 286;
    static final short CMD_QUEUEALTER = 287;
    static final short CMD_FTXTINDEX_CREATE = 294;
    static final short CMD_FTXTINDEX_ALTER = 295;
    static final short CMD_FTXTINDEX_DROP = 296;
    static final short CMD_PRTFUNCTIONCREATE = 297;
    static final short CMD_PRTFUNCTIONDROP = 298;
    static final short CMD_PRTSCHEMECREATE = 299;
    static final short CMD_PRTSCHEMEDROP = 300;
    static final short CMD_FTXTCATALOG_CREATE = 304;
    static final short CMD_FTXTCATALOG_ALTER = 305;
    static final short CMD_FTXTCATALOG_DROP = 306;
    static final short CMD_XMLSCHEMACREATE = 309;
    static final short CMD_XMLSCHEMAALTER = 310;
    static final short CMD_XMLSCHEMADROP = 311;
    static final short CMD_ENDPOINTCREATE = 312;
    static final short CMD_ENDPOINTALTER = 313;
    static final short CMD_ENDPOINTDROP = 314;
    static final short CMD_USERCREATE = 315;
    static final short CMD_USERALTER = 316;
    static final short CMD_USERDROP = 317;
    static final short CMD_ROLECREATE = 319;
    static final short CMD_ROLEALTER = 320;
    static final short CMD_ROLEDROP = 321;
    static final short CMD_APPROLECREATE = 322;
    static final short CMD_APPROLEALTER = 323;
    static final short CMD_APPROLEDROP = 324;
    static final short CMD_LOGINCREATE = 325;
    static final short CMD_LOGINALTER = 326;
    static final short CMD_LOGINDROP = 327;
    static final short CMD_SYNONYMCREATE = 328;
    static final short CMD_SYNONYMDROP = 329;
    static final short CMD_CREATESCHEMA = 330;
    static final short CMD_ALTERSCHEMA = 331;
    static final short CMD_AGGCREATE = 332;
    static final short CMD_AGGDESTROY = 333;
    static final short CMD_CLRTRIGGERCREATE = 334;
    static final short CMD_PRTFUNCTIONALTER = 335;
    static final short CMD_PRTSCHEMEALTER = 336;
    static final short CMD_INDALTER = 337;
    static final short CMD_ROUTECREATE = 343;
    static final short CMD_ROUTEALTER = 344;
    static final short CMD_ROUTEDESTROY = 346;
    static final short CMD_EVENTNOTIFICATIONCREATE = 352;
    static final short CMD_EVENTNOTIFICATIONDROP = 353;
    static final short CMD_XMLINDEXCREATE = 354;
    static final short CMD_BINDINGCREATE = 358;
    static final short CMD_BINDINGALTER = 359;
    static final short CMD_BINDINGDESTROY = 360;
    static final short CMD_MSGTYPEALTER = 366;
    static final short CMD_CERTCREATE = 368;
    static final short CMD_CERTDROP = 369;
    static final short CMD_CERTALTER = 370;
    static final short CMD_SECDESCCREATE = 381;
    static final short CMD_SECDESCDROP = 382;
    static final short CMD_SECDESCALTER = 383;
    static final short CMD_OBFUSKEYCREATE = 386;
    static final short CMD_OBFUSKEYALTER = 387;
    static final short CMD_OBFUSKEYDROP = 388;
    static final short CMD_ALTERAUTHORIZATION = 396;
    static final short CMD_CREDENTIALCREATE = 408;
    static final short CMD_CREDENTIALALTER = 409;
    static final short CMD_CREDENTIALDROP = 410;
    static final short CMD_MASTERKEYCREATE = 411;
    static final short CMD_MASTERKEYDROP = 412;
    static final short CMD_MASTERKEYALTER = 417;
    static final short CMD_ASYMKEYCREATE = 419;
    static final short CMD_ASYMKEYDROP = 420;
    static final short CMD_ASYMKEYALTER = 425;
    private short curCmd;
    
    @Override
    void setFromTDS(final TDSReader tdsReader) throws SQLServerException {
        this.packetType = tdsReader.readUnsignedByte();
        assert 255 == this.packetType;
        this.status = tdsReader.readShort();
        this.curCmd = tdsReader.readShort();
        this.rowCount = tdsReader.readLong();
        if (this.isAttnAck()) {
            tdsReader.getCommand().onAttentionAck();
        }
    }
    
    final short getCurCmd() {
        return this.curCmd;
    }
    
    final boolean isFinal() {
        return (this.status & 0x1) == 0x0;
    }
    
    final boolean isError() {
        return (this.status & 0x2) != 0x0;
    }
    
    final boolean updateCountIsValid() {
        return (this.status & 0x10) != 0x0;
    }
    
    final boolean isAttnAck() {
        return (this.status & 0x20) != 0x0;
    }
    
    final boolean wasRPCInBatch() {
        return (this.status & 0x80) != 0x0;
    }
    
    final long getUpdateCount() {
        assert this.cmdIsDMLOrDDL();
        switch (this.curCmd) {
            case 194:
            case 195:
            case 196:
            case 197:
            case 240:
            case 279: {
                return this.updateCountIsValid() ? this.rowCount : -1L;
            }
            default: {
                return 0L;
            }
        }
    }
    
    final boolean cmdIsDMLOrDDL() {
        switch (this.curCmd) {
            case 46:
            case 153:
            case 176:
            case 178:
            case 179:
            case 181:
            case 182:
            case 194:
            case 195:
            case 196:
            case 197:
            case 198:
            case 199:
            case 200:
            case 201:
            case 203:
            case 204:
            case 205:
            case 206:
            case 207:
            case 208:
            case 215:
            case 216:
            case 221:
            case 222:
            case 223:
            case 225:
            case 230:
            case 233:
            case 236:
            case 237:
            case 238:
            case 240:
            case 256:
            case 270:
            case 271:
            case 272:
            case 273:
            case 274:
            case 275:
            case 276:
            case 277:
            case 278:
            case 279:
            case 281:
            case 282:
            case 283:
            case 284:
            case 285:
            case 286:
            case 287:
            case 294:
            case 295:
            case 296:
            case 297:
            case 298:
            case 299:
            case 300:
            case 304:
            case 305:
            case 306:
            case 309:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 315:
            case 316:
            case 317:
            case 319:
            case 320:
            case 321:
            case 322:
            case 323:
            case 324:
            case 325:
            case 326:
            case 327:
            case 328:
            case 329:
            case 330:
            case 331:
            case 332:
            case 333:
            case 334:
            case 335:
            case 336:
            case 337:
            case 343:
            case 344:
            case 346:
            case 352:
            case 353:
            case 354:
            case 358:
            case 359:
            case 360:
            case 366:
            case 368:
            case 369:
            case 370:
            case 381:
            case 382:
            case 383:
            case 386:
            case 387:
            case 388:
            case 396:
            case 408:
            case 409:
            case 410:
            case 411:
            case 412:
            case 417:
            case 419:
            case 420:
            case 425: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
