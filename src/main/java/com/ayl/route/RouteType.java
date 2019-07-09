package com.ayl.route;

/**
 * 定义路由策略
 */
public enum RouteType {
    TRAIN("train"),RANDOM("random"),BALANCE("balance");
    private String type;
    private RouteType(String type){
        this.type = type;
    }
    public String getType() {
        return type;
    }
}
