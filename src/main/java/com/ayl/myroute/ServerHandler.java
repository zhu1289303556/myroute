package com.ayl.myroute;

import com.ayl.route.RouteService;
import com.ayl.route.bean.Route;
import com.ayl.util.ProtoUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

//线程间共享,但必须要保证此类线程安全
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private final static Log LOG = LogFactory.getLog(ServerHandler.class);

    //保证线程安全
    private ThreadLocal<ChannelFuture> futureThreadLocal = new ThreadLocal<>();
    private final AtomicInteger PORT = new AtomicInteger(0);
    private final AtomicReference<String> HOST = new AtomicReference<String>("0.0.0.0");


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.debug("服务器连接成功......");
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        //http
        try {
            if (msg instanceof FullHttpRequest) {
                FullHttpRequest request = (FullHttpRequest) msg;
    //            String name = request.method().name();
                ProtoUtil.RequestProto protoUtil = ProtoUtil.getRequestProto(request);
                String host = protoUtil.getHost();
                LOG.debug(host);
                int port = protoUtil.getPort();
                PORT.set(port);
                HOST.set(host);
                //开启代理服务器
                Route route = RouteService.getInstance().route(request);
                LOG.debug(route.getName());
                new ProxyServer(route, msg, ctx.channel()).start();
            } else {
                LOG.debug("http request fail");
            }
        } catch (Exception e) {
            LOG.error("proxy server error", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            ctx.channel().close();
            LOG.error("Server request timeout ");
        } catch (Exception e) {
            LOG.error("Server request timeout ", e);
        }
    }
}