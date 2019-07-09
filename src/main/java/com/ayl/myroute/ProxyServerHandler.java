package com.ayl.myroute;

import com.ayl.route.RouteConstant;
import com.ayl.route.RouteService;
import com.ayl.route.bean.Route;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyServerHandler.class);

    private Channel channel;
    private Route route;

    public ProxyServerHandler(Channel channel, Route route) {
        this.route = route;
        this.channel = channel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.debug("代理服务器连接成功.....");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof FullHttpResponse) {
                FullHttpResponse response = (FullHttpResponse) msg;
                if (RouteService.readCookie(response,RouteConstant.CLIENT_ID) == null){
                    Cookie cookie=new DefaultCookie(RouteConstant.CLIENT_ID, route.getName());
                    response.headers().set(HttpHeaders.Names.SET_COOKIE, cookie);
                }
                channel.writeAndFlush(response);
            }
        }catch (Exception e){
            LOG.error("proxyServer channelRead error", e);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        try {
            if (channel != null) {
                channel.close();
            }
            ctx.channel().close();
        } catch (Exception e) {
            LOG.error("proxyServer channelRead error", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            if (channel != null) {
                channel.close();
            }
            ctx.channel().close();
            LOG.error("{} request timeout {}", this, ctx.pipeline().channel().toString(), cause);
        } catch (Exception e) {
            LOG.error("proxyServer request timeout ", e);
        }
    }
}