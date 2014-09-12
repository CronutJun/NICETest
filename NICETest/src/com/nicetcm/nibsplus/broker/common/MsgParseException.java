package com.nicetcm.nibsplus.broker.common;

public class MsgParseException extends Exception {
    
    static final long serialVersionUID = 5085676343992110205L;
    
    private String reason;
    
    public MsgParseException(String reason) {
        this.reason = reason;
    }
    
    @Override
    public String getMessage() {
        return reason;
    }
}
