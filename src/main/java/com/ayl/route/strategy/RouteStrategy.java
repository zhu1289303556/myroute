package com.ayl.route.strategy;

import com.ayl.route.bean.Route;
import com.ayl.route.bean.Routes;
import io.netty.handler.codec.http.HttpRequest;

public interface RouteStrategy {

    Route routeProxyByClient(HttpRequest request, Routes routes);
}
