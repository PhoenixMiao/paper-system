package com.phoenix.paper.util;

import com.phoenix.paper.dto.SessionData;
import com.phoenix.paper.single.ShuaiExpireKey;
import com.phoenix.paper.single.ShuaiServer;

import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        //todo ShuaiServer启动
        ShuaiServer.open();
        ShuaiDatabaseUtils utils = new ShuaiDatabaseUtils();
        SessionData sessionData = new SessionData();
        sessionData.setPaperNum(3);
        sessionData.setSchool("school");
        sessionData.setName("wuming");
        utils.set("32ushfuwbbrubuhfuhs", sessionData, 4);
        SessionData data = (SessionData) utils.get("32ushfuwbbrubuhfuhs");
        System.out.println(data.getPaperNum());
        System.out.println(data.getSchool());
        System.out.println(data.getName());
        for (ShuaiExpireKey key : ShuaiServer.dbActive.getExpires())
            System.out.println(key.getDelay(TimeUnit.MILLISECONDS));
        Thread.sleep(3000);
        data = (SessionData) utils.get("32ushfuwbbrubuhfuhs");
        System.out.println(data.getPaperNum());
        System.out.println(data.getSchool());
        System.out.println(data.getName());
        for (ShuaiExpireKey key : ShuaiServer.dbActive.getExpires())
            System.out.println(key.getDelay(TimeUnit.MILLISECONDS));
        Thread.sleep(7001);
        data = (SessionData) utils.get("32ushfuwbbrubuhfuhs");
        System.out.println(data.getPaperNum());
        System.out.println(data.getSchool());
        System.out.println(data.getName());
        for (ShuaiExpireKey key : ShuaiServer.dbActive.getExpires())
            System.out.println(key.getDelay(TimeUnit.MILLISECONDS));
    }
}
