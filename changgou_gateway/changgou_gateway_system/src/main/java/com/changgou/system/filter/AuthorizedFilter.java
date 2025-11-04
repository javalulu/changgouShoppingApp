package com.changgou.system.filter;

import com.changgou.system.util.JwtUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthorizedFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        // 2.获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        // 3.判断当前请求是否为登录请求, 如果是则放行
        if (request.getURI().getPath().contains("/admin/login")) {
            // 放行
            return chain.filter(exchange);
        }
        // 4.获取当前所有请求头信息
        HttpHeaders headers = request.getHeaders();

        // 5.获取jwt令牌
        String token = headers.getFirst("token");

        // 6.判断令牌是否为空
        if (StringUtils.isEmpty(token)) {
            // 令牌不存在
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 7.解析jwt令牌
        try {
            JwtUtil.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            // 解析失败
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
