package com.ayl.route.strategy;

import com.ayl.route.bean.Route;
import com.ayl.route.bean.Routes;
import io.netty.handler.codec.http.HttpRequest;

public class RandomRouteStrategy implements RouteStrategy {

    @Override
    public Route routeProxyByClient(HttpRequest request, Routes routes) {
        return null;
    }
}
