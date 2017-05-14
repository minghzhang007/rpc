package com.lewis.rpc.chapter1;

/**
 * Created by Administrator on 2017/5/14.
 */
public class EchoServiceImpl implements EchoService {
    public String echo(String ping) {

        return ping != null ? ping + " --> I am ok." : "I am ok.";
    }
}
