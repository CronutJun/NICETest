package com.nicetcm.nibsplus.broker.ams.jmeter;

/*
 * Copyright 2014 The NIBS Project
 *
 * AMS 기기관리시스템 - AMSBrokerClientHandler
 *
 * 접속한 클라이언트의 요청 처리 객체
 *
 * @author  K.D.J
 * @since   2014.05.23
 */


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.nio.ByteBuffer;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.buffer.ByteBuf;

public class AMSClient {

    private static final Logger logger = LoggerFactory.getLogger(AMSClient.class);

    private static final ConcurrentMap<String, AMSClient> clientPool = new ConcurrentHashMap<String, AMSClient>();

    private final String host;
    private final int    port;
    private final BlockingQueue<ByteBuf> ans;
    private ByteBuf reqBuf;

    public static AMSClient getInstance(String host, int port) {

        AMSClient client = null;

        if( clientPool.containsKey(host) ) {
            client =  clientPool.get(host);
        }
        else {
            client = new AMSClient( host, port );
            clientPool.put( host, client );
        }
        return client;

    }

    private AMSClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.ans  = new LinkedBlockingQueue<ByteBuf>();
    }

    public ByteBuffer outboundCall(ByteBuffer data, InputStream strm, int timeOut) throws Exception {
        // Configure the client.
        ByteBuf lstRslt = null, fstRslt = null;
        ByteBuffer ret = null;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .option(ChannelOption.TCP_NODELAY, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast(
                             new AMSClientHandler(ans));
                 }
             });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();
            logger.debug("channel opened = " + f.channel().isOpen());
            logger.debug("data size = " + data.limit());
            byte[] read = new byte[data.limit()];
            data.position(0);
            data.get(read);
            logger.debug(new String(read));
            //for(byte a: read)
            // logger.debug(String.format("%x", a) );

            data.position(0);
            reqBuf = f.channel().alloc().buffer(data.limit());
            reqBuf.writeBytes(data);
            logger.debug("going to send");
            f.channel().writeAndFlush(reqBuf);
            while ( strm != null && strm.available() > 0 ) {
                read = strm.available() > 262144 ? new byte[262144]
                     : new byte[strm.available()];
                strm.read(read);
                reqBuf = f.channel().alloc().buffer(read.length);
                reqBuf.writeBytes(read);
                logger.debug("Send File size = " + read.length);
                f.channel().writeAndFlush(reqBuf);
            }
            if( strm != null )
                strm.close();

            String defTimeOut = "60";
            try {
                lstRslt = ans.poll(timeOut == 0 ? Integer.parseInt(defTimeOut) : timeOut, TimeUnit.SECONDS);
                if( lstRslt == null ) {
                    throw new Exception("timeout");
                }
                if( fstRslt == null )
                    fstRslt = lstRslt;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            f.channel().close().sync();
            //f.channel().closeFuture().sync();
            logger.debug("answer count = " + ans.size());
            logger.debug("channel opened = " + f.channel().isOpen());


            // Wait until the connection is closed.
            //f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
        fstRslt.resetReaderIndex();
        ret = ByteBuffer.allocateDirect(fstRslt.readableBytes());
        fstRslt.readBytes(ret);

        return ret;
    }
 }