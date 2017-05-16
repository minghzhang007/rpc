package com.lewis.zk;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Random;

import static org.apache.zookeeper.KeeperException.Code.CONNECTIONLOSS;

public class Worker implements Watcher {
    private ZooKeeper zk;
    String hostPort;
    String serverId = Integer.toHexString(new Random().nextInt());

    public Worker(String hostPort) {
        this.hostPort = hostPort;
    }

    public void startZK() throws IOException {
        this.zk = new ZooKeeper(hostPort, 15000, this);
    }

    public void process(WatchedEvent event) {
        System.out.println(event.toString() + ", " + hostPort);
    }

    public void register() {
        zk.create("/workers/worker-" + serverId, "Idle".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, createWorkerCallback, null);
    }

    private AsyncCallback.StringCallback createWorkerCallback = new AsyncCallback.StringCallback() {
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    register();
                    break;
                case OK:
                    System.out.println("Registered successfully: " + serverId);
                    break;
                case NODEEXISTS:
                    System.out.println("Already Registered: " + serverId);
                    break;
                default:
                    System.out.println("something went wrong:" + KeeperException.create(KeeperException.Code.get(rc), path));
            }
        }
    };

    public static void main(String[] args) throws IOException, InterruptedException {
        Worker worker = new Worker("localhost:2181");
        worker.startZK();
        worker.register();
        Thread.sleep(30000);
    }
}
