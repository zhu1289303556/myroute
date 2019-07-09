package com.ayl.myroute;

import com.ayl.route.bean.Route;
import com.ayl.util.HeaderUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

import java.net.InetSocketAddress;

/**
 * 模拟客户端请求真实服务器
 */
public class ProxyServer {

    private final String HOST;
    private final int PORT;
    private final Route route;
    private final Object msg;
    private final Channel channel;

    public ProxyServer(Route route, Object msg, Channel channel) {
        this.route = route;
        this.HOST = route.getRouteHost().getHost();
        this.PORT = route.getRouteHost().getPort();
        this.msg = msg;
        this.channel = channel;
    }


    public void start() {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new HttpClientCodec());
                        socketChannel.pipeline().addLast(new HttpObjectAggregator(6553600));
                        socketChannel.pipeline().addLast(new ProxyServerHandler(channel, route));
                    }
                })
                .connect(new InetSocketAddress(HOST, PORT))
                .addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            HeaderUtil.addHeaders(future, msg);
                        } else {
                            future.channel().close();
                        }
                    }
                });
    }


}