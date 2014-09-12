package com.nicetcm.nibsplus.broker.common;

import java.nio.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ThrData {
    
    public int                   pos;
    public boolean              isLive;
    public int                   messageLength;
    public ByteBuffer            msg;
    public Map<String, MsgData>  msgDatMap; 
    
    public ThrData() {
        pos = 0;
        isLive = false;
        messageLength = 0;
        msg = null;
        msgDatMap = new LinkedHashMap<String, MsgData>();
    }
    
    public ThrData(ByteBuffer msg) {
        pos = 0;
        isLive = false;
        messageLength = 0;
        this.msg = msg;
        this.msg.position(0);
        msgDatMap = new LinkedHashMap<String, MsgData>();
        msgDatMap.clear();
    }
}
