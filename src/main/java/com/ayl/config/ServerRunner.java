package com.ayl.config;

import com.ayl.route.RouteService;
import com.ayl.route.bean.Routes;
import com.ayl.myroute.Server;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ServerRunner implements ApplicationRunner {

    private final static Log LOG = LogFactory.getLog(ServerRunner.class);

    @Value("${webserver.port}")
    private int port;

    @Getter
    @Autowired
    private Routes routes;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RouteService.getInstance().setRoutes(routes);
        new Server(port).start();
    }
}
