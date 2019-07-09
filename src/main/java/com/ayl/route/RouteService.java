package com.ayl.route;

import com.ayl.route.bean.Route;
import com.ayl.route.bean.Routes;
import com.ayl.route.strategy.TrainRouteStrategy;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RouteService {

    private final static Log LOG = LogFactory.getLog(RouteService.class);

    private Routes routes;

    public static Map<String, Route> route_map = new HashMap();

    public void setRoutes(Routes routes) {
        this.routes = routes;

        List<Route> list = routes.getRoutes();
        int size = list.size();
        for (int i = 0; i < size; i++){
            Route route = list.get(i);
            route_map.put(route.getName(),route);
        }
    }

    public Route route(HttpRequest request) throws Exception {

        switch (routes.getRouteName()){
            //轮训
            case TRAIN:{
                return TrainRouteStrategy.getInstance().routeProxyByClient(request, routes);
            }
            //随机
            case RANDOM:{
                break;
            }
            //权重
            case BALANCE:{
                break;
            }
            default:{

            }
        }
        return null;
    }

    public static String readCookie(HttpMessage request, String cookie_name){
        if (request.headers().get(HttpHeaders.Names.COOKIE) == null){
            return null;
        }
        String [] strings = request.headers().get(HttpHeaders.Names.COOKIE).split(";");
        String cookie_value = "";
        for (String str : strings){
            if (str.contains(cookie_name)){
                cookie_value = str.split("=")[1];
            }
        }
        return cookie_value;
    }

    private RouteService(){}

    private static RouteService instance = new RouteService();

    public static RouteService getInstance(){
        return instance;
    }
}
