package com.ayl.route.strategy;

import com.ayl.route.RouteConstant;
import com.ayl.route.RouteService;
import com.ayl.route.bean.Route;
import com.ayl.route.bean.Routes;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TrainRouteStrategy implements RouteStrategy {

    private final static Log LOG = LogFactory.getLog(TrainRouteStrategy.class);

    private AtomicInteger position = new AtomicInteger(0);

    public Route routeProxyByClient(HttpRequest request, Routes routes){
        //判断session
        LOG.debug(RouteService.route_map);
        String client_id = RouteService.readCookie(request, RouteConstant.CLIENT_ID);
        LOG.debug("RouteService"+client_id);
        if (client_id != null && RouteService.route_map.get(client_id) != null) {
            return RouteService.route_map.get(client_id);
        }

        List<Route> list = routes.getRoutes();
        Route route = list.get(position.get());

        position.incrementAndGet();
        if (list.size() <= position.get()){
            position.set(0);
        }
        return route;
    }

    private TrainRouteStrategy(){}

    private static TrainRouteStrategy instance = new TrainRouteStrategy();

    public static TrainRouteStrategy getInstance(){
        return instance;
    }
}
