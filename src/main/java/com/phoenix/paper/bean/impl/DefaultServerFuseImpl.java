//package com.phoenix.paper.bean.impl;
//
//import com.phoenix.paper.bean.ServerFuse;
//import com.phoenix.paper.config.ServerFuseConfig;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * 默认服务熔断实现类
// *
// * @author miracle
// * @since 2021/11/22 11:05
// */
//@Component
//public class DefaultServerFuseImpl implements ServerFuse {
//
//    @Resource
//    private ServerFuseConfig serverFuseConfig;
//    /**
//     * 请求容器：定义成对象、方便扩展
//     */
//    public record RequestComponent(AtomicInteger numbers) {
//
//        /**
//         * 构造器注入
//         *
//         * @param numbers 并发数量
//         */
//        public RequestComponent {
//        }
//
//        public AtomicInteger getNumbers() {
//            return numbers;
//        }
//    }
//
//    private final Map<String, RequestComponent> SYNC_COMPONENT = new HashMap<>(16);
//
//
//    /* ------------ Override ServerFuse Method ------------*/
//
//    @Override
//    public boolean fuse() {
//        return serverFuseConfig.getFuse();
//    }
//
//    @Override
//    public boolean process() {
//        return serverFuseConfig.getProcess();
//    }
//
//    @Override
//    public int incrementAndGetRequestUrlConcurrency(String uri) {
//        if (!SYNC_COMPONENT.containsKey(uri)) {
//            synchronized (SYNC_COMPONENT) {
//                //防止重复设置数据，再做一次判断
//                if (!SYNC_COMPONENT.containsKey(uri)) {
//                    SYNC_COMPONENT.put(uri, new RequestComponent(new AtomicInteger(0)));
//                }
//            }
//        }
//        return SYNC_COMPONENT.get(uri).getNumbers().incrementAndGet();
//    }
//
//    @Override
//    public void decrementAndGetRequestUrlConcurrency(String uri) {
//        if (!fuse()) {
//            //未启用熔断，则清空同步容器
//            if (MapUtils.isNotEmpty(SYNC_COMPONENT)) {
//                synchronized (SYNC_COMPONENT) {
//                    SYNC_COMPONENT.clear();
//                }
//            }
//            return;
//        }
//        RequestComponent component = SYNC_COMPONENT.get(uri);
//        if (!Objects.isNull(component)) {
//            component.getNumbers().updateAndGet(prev -> prev <= 0 ? 0 : prev - 1);
//        }
//    }
//
//    @Override
//    public int getRequestUriConcurrencyConfig(String uri) {
//        if (!fuse()) {
//            //未启用熔断，则返回Integer.MAX_VALUE
//            return Integer.MAX_VALUE;
//        }
//        //自定义URL并发数
//        if (serverFuseConfig.getCustomConcurrencyMap().containsKey(uri)) {
//            return Optional.ofNullable(serverFuseConfig.getCustomConcurrencyMap().get(uri))
//                    .orElse(serverFuseConfig.getConcurrency());
//        }
//        //常用服务并发数配置
//        return Math.max(serverFuseConfig.getConcurrency(), 0);
//    }
//}
