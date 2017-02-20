package com.pingcap.tools.cdb.binlog.listener.mysql;

import com.pingcap.tools.cdb.binlog.common.AbstractCDBLifeCycle;
import com.pingcap.tools.cdb.binlog.listener.CDBEventListener;
import com.qcloud.dts.context.SubscribeContext;
import com.qcloud.dts.message.ClusterMessage;
import com.qcloud.dts.message.DataMessage;
import com.qcloud.dts.subscribe.ClusterListener;
import com.qcloud.dts.subscribe.DefaultSubscribeClient;
import com.qcloud.dts.subscribe.SubscribeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

/**
 * Created by iamxy on 2017/2/20.
 */
public class CDBMySQLEventListener extends AbstractCDBLifeCycle implements CDBEventListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String destination;

    private String serviceIp;
    private int servicePort;
    private String secretId;
    private String secretKey;
    private String guid;
    /*private int last;
    private long startTime = System.currentTimeMillis();
    private int count = 0;*/

    private SubscribeClient client;

    @Override
    public void start() {
        SubscribeContext context = new SubscribeContext();
        context.setSecretId(secretId);
        context.setSecretKey(secretKey);
        context.setServiceIp(serviceIp);
        context.setServicePort(servicePort);

        try {
            this.client = new DefaultSubscribeClient(context);
        } catch (Exception e) {
            logger.error("# create subscribe client failed", e);
        }

        ClusterListener listener = new ClusterListener() {
            @Override
            public void notify(List<ClusterMessage> messages) throws Exception {
                for (ClusterMessage m:messages) {
                    DataMessage.Record record = m.getRecord();
                    try {
                        handleMessage(record);
                        m.ackAsConsumed();
                    } catch (Exception e) {
                        logger.error("# handle message process failed", e);
                    }
                }
            }

            public void onException(Exception e) {
                logger.error("# exception: ", e);
            }
        };

        try {
            client.addClusterListener(listener);
            client.askForGUID(guid);
            client.start();
        } catch(Exception e) {
            logger.error("# start listener failed", e);
        }
    }

    @Override
    public void stop() {
        try{
            client.stop();
        } catch (Exception e) {
            logger.error("# stop listener failed", e);
        }
    }

    public void handleMessage(DataMessage.Record record) {
        // TODO: handle message process
        // System.out.printf("# DataMessage.Record: %s\n", record.getCheckpoint());
   /*     String[] ch = record.getCheckpoint().split("@");
        int chInt = Integer.parseInt(ch[2]);
        if(chInt < last) {
            System.out.println("Error offset is small than last");
        } else {
            last = chInt;
        }
        count++;
        if (count >= 800000) {
            long endTime = System.currentTimeMillis();
            System.out.println(800000/((endTime-startTime)/1000));
            System.out.println(startTime);
            System.out.println(endTime);
            System.exit(0);
        }*/

        /*try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
