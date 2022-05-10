//package com.phoenix.paper.bean;
//
///**
// * 服务熔断（限流）
// *
// * @author miracle
// * @since 2021/11/22 10:03
// */
//public interface ServerFuse {
//
//    /**
//     * 是否启用熔断
//     *
//     * @return boolean
//     */
//    boolean fuse();
//
//    /**
//     * 并发达到峰值是否允许继续执行
//     *
//     * @return boolean
//     */
//    boolean process();
//
//    /**
//     * 拦截请求
//     *
//     * @param uri uri
//     * @return 是否拦截请求
//     */
//    default boolean interceptRequest(String uri) {
//        return fuse() ? incrementAndGetRequestUrlConcurrency(uri) > getRequestUriConcurrencyConfig(uri) : Boolean.FALSE;
//    }
//
//    /**
//     * 自增并获取请求URL的并发数
//     *
//     * @param uri 请求URI
//     * @return concurrency
//     */
//    int incrementAndGetRequestUrlConcurrency(String uri);
//
//    /**
//     * 自增并获取请求URL的并发数
//     *
//     * @param uri 请求URI
//     */
//    void decrementAndGetRequestUrlConcurrency(String uri);
//
//    /**
//     * 获取请求的URL并发数配置
//     *
//     * <p>注意：若无配置，返回{@link Integer#MAX_VALUE}</p>
//     *
//     * @param uri 请求URI
//     * @return 并发数配置
//     */
//    int getRequestUriConcurrencyConfig(String uri);
//
//}