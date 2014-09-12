package com.nicetcm.nibsplus.broker.ams.jmeter;

import java.io.Serializable;
import java.nio.ByteBuffer;

import javax.json.Json;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

import com.nicetcm.nibsplus.broker.common.MsgCommon;
import com.nicetcm.nibsplus.broker.common.MsgParser;



public class AMSFileReq  extends AbstractJavaSamplerClient implements Serializable {

    static final long serialVersionUID = 4789424761976034786L;

    // set up default arguments for the JMeter GUI
    @Override
    public Arguments getDefaultParameters() {
        Arguments defParams = new Arguments();

        defParams.addArgument( "HostIP", "10.3.28.180");
        defParams.addArgument( "Port",   "33001" );

        return defParams;
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        String hostIp = context.getParameter( "HostIP" );
        String port   = context.getParameter( "Port"   );

        SampleResult result = new SampleResult();
        result.sampleStart(); // start stopwatch

        MsgCommon.msgProps.setProperty("schema_path", "/msg_schema/");

        try {
            MsgParser msgPsr = MsgParser.getInstance(String.format("%s%s%s.json", "/msg_schema/", "1100", "5002"));
            ByteBuffer buf =  ByteBuffer.allocateDirect( msgPsr.getSchemaLength() );
            msgPsr.newMessage(buf);
            try {
                buf.position(0);
                msgPsr.setString( "CM._AOCMsgCode",           "1100")
                      .setString( "CM._AOCServiceCode",       "5002")
                      .setString( "CM._AOCMsgSendDate",       "20140912" )
                      .setString( "CM._AOCMsgSendTime",       "123402" )
                      .setString( "CM._SSTNo",                "960202" )
                      .setString( "CM._BranchCode",           "9600" )
                      .setString( "CM._BankCode",             "096" )
                      .setLong  ( "CM._AOCMsgLen",            msgPsr.getMessageLength()  - 9 )
                      .setString( "CM._AOCTranNo",            "000001" )
                      .setString( "_APVersion",               "0.0.0.1");
                msgPsr.syncMessage();
                AMSClient client = AMSClient.getInstance(hostIp, Integer.parseInt(port));
                buf.position(0);
                ByteBuffer rslt = client.outboundCall( buf, null, 120 );
            }
            finally {
                msgPsr.clearMessage();
            }

            result.sampleEnd(); // stop stopwatch
            result.setSuccessful( true );
            result.setResponseMessage( "Successfully performed action" );
            result.setResponseCodeOK(); // 200 code
        }
        catch (Exception e) {
            result.sampleEnd(); // stop stopwatch
            result.setSuccessful( false );
            result.setResponseMessage( "Exception: " + e );

            // get stack trace as a String to return as document data
            java.io.StringWriter stringWriter = new java.io.StringWriter();
            e.printStackTrace( new java.io.PrintWriter( stringWriter ) );
            result.setDataType( org.apache.jmeter.samplers.SampleResult.TEXT );
            result.setResponseCode( "500" );
        }


        return result;
    }

    public void test() {
        try {
            MsgCommon.msgProps.setProperty("schema_path", "/msg_schema/");

            MsgParser msgPsr = MsgParser.getInstance("/msg_schema/11005002.json");

            ByteBuffer buf =  ByteBuffer.allocateDirect( msgPsr.getSchemaLength() );
            msgPsr.newMessage(buf);
            try {
                buf.position(0);
                msgPsr.setString( "CM._AOCMsgCode",           "1100")
                      .setString( "CM._AOCServiceCode",       "5002")
                      .setString( "CM._AOCMsgSendDate",       "20140912" )
                      .setString( "CM._AOCMsgSendTime",       "123402" )
                      .setString( "CM._SSTNo",                "960202" )
                      .setString( "CM._BranchCode",           "9600" )
                      .setString( "CM._BankCode",             "096" )
                      .setLong  ( "CM._AOCMsgLen",            msgPsr.getMessageLength()  - 9 )
                      .setString( "CM._AOCTranNo",            "000001" )
                      .setString( "_APVersion",               "0.0.0.1");
                msgPsr.syncMessage();
                AMSClient client = AMSClient.getInstance("10.3.28.180", 33001);
                buf.position(0);
                System.out.println("buf.limit = " + buf.limit());
                ByteBuffer rslt = client.outboundCall( buf, null, 120 );
            }
            finally {
                msgPsr.clearMessage();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new AMSFileReq().test();
    }

}
