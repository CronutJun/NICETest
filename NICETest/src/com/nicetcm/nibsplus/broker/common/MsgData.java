package com.nicetcm.nibsplus.broker.common;

import java.util.*;

public class MsgData {

    public    MsgFmtRec                    refFmt;
    public    Map<String, MsgData>         refMap;
    public    String                       name;
    public    int                          pos;
    public    int                          length;
    private   byte[]                       data;
    public    int                          iteration;
    public    List<Map<String, MsgData>>   adata;

    public MsgData(){

        this.name       = "";
        this.pos        = 0;
        this.length     = 0;
        this.data       = null;
        this.iteration  = 0;
        this.adata      = new ArrayList<Map<String, MsgData>>();

    }

    public byte[] getBytes() throws Exception {
        return data;
    }

    public void setBytes(byte[] value)  throws Exception {
        this.data = value;
        if( this.refFmt.ref_size.length() > 0 ) {
            this.refMap.get(this.refFmt.ref_size).setInt(data.length);
            this.length = data.length;
        }

    }

    public String getString() throws Exception {
        if( data == null || data.length == 0 )
            setString("");

        return rtrim(new String(data));
    }

    public void setString(String value)  throws Exception {
        this.data = value.getBytes();
        if( this.refFmt.ref_size.length() > 0 ) {
            this.refMap.get(this.refFmt.ref_size).setInt(data.length);
            this.length = data.length;
        }

    }

    private String rtrim(String s) {
        int i = s.length()-1;
        while (i >= 0 && (Character.isWhitespace(s.charAt(i)) || s.charAt(i) == '\0')) {
            i--;
        }
        return s.substring(0,i+1);
    }

    public short getShort() throws Exception {
        if( data == null || data.length == 0 )
            return 0;
        else if( new String(data).trim().length() == 0 )
            return 0;
        else return Short.parseShort(new String(data).trim());
    }

    public void setShort(short value)  throws Exception {
        data = Short.toString(value).getBytes();
    }

    public int getInt() throws Exception {
        if( data == null || data.length == 0 )
            return 0;
        else if( new String(data).trim().length() == 0 )
            return 0;
        else return Integer.parseInt(new String(data).trim());
    }

    public void setInt(int value)  throws Exception {
        data = Integer.toString(value).getBytes();
    }

    public long getLong() throws Exception {
        if( data == null || data.length == 0 )
            return 0;
        else if( new String(data).trim().length() == 0 )
            return 0;
        else return Long.parseLong(new String(data).trim());
    }

    public void setLong(long value)  throws Exception {
        data = Long.toString(value).getBytes();
    }

    public float getFloat() throws Exception {
        if( data == null || data.length == 0 )
            return 0;
        else if( new String(data).trim().length() == 0 )
            return 0;
        else return Float.parseFloat(new String(data).trim());
    }

    public void setFloat(float value)  throws Exception {
        data = Float.toString(value).getBytes();
    }

    public double getDouble() throws Exception {
        if( data == null || data.length == 0 )
            return 0;
        else if( new String(data).trim().length() == 0 )
            return 0;
        else return Double.parseDouble(new String(data).trim());
    }

    public void setDouble(double value)  throws Exception {
        data = Double.toString(value).getBytes();
    }

    public LinkedHashMap<String, MsgData> addArrayData(boolean parsing) throws Exception {

        if( this.refFmt.ref_iteration.length() == 0 )
            throw new Exception("Can't be array. There's no referenced field of iteration.." + this.name);

        LinkedHashMap<String, MsgData> dMap;
        MsgData                        data;

        dMap = new LinkedHashMap<String, MsgData>();
        adata.add(dMap);

        if( this.refFmt.schema != null ) {
            Set<Map.Entry<String, MsgFmtRec>> set = this.refFmt.schema.entrySet();
            Iterator<Map.Entry<String, MsgFmtRec>>  itr = set.iterator();

            while ( itr.hasNext() ){
                Map.Entry<String, MsgFmtRec> e = (Map.Entry<String, MsgFmtRec>)itr.next();

                data = new MsgData();
                data.refFmt = e.getValue();
                data.refMap = dMap;
                data.name   = e.getKey();
                if( e.getValue().ref_size.length() == 0 )
                    data.length = e.getValue().length;

                dMap.put(e.getKey(), data);

            }
        }
        else {
            data = new MsgData();
            data.refFmt = this.refFmt;
            data.refMap = this.refMap;
            data.name   = this.name;
            if( this.refFmt.ref_size.length() == 0 )
                data.length = this.refFmt.length;

            dMap.put(this.name, data);
        }

        if( !parsing ) {
            this.iteration++;
            this.refMap.get(this.refFmt.ref_iteration).setInt(this.iteration);
        }

        return dMap;
    }

}
