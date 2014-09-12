package com.nicetcm.nibsplus.broker.common;

import java.util.*;

public class MsgFmtRec {
    public    String                       name;
    public    String                       type;
    public    String                       ref_iteration;
    public    String                       ref_size;
    public    int                          iteration;
    public    int                          length;
    public    String                       encCharset;
    public    String                       decCharset;
    public    Map<String, MsgFmtRec>       schema;

    public MsgFmtRec(){

        this.name          = "";
        this.type          = "";
        this.ref_iteration = "";
        this.ref_size      = "";
        this.iteration     = 0;
        this.length        = 0;
        this.schema        = null;

    }
}
