package com.nera.nms.utils;

import com.nera.nms.InClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class FactorialRunPLaybook implements Callable<String> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    String command ;
    String scp;
    String rabbitHost;
    String rabbitUsername;
    String rabbitPassword;
    int rabbitPort;
    String queueName;
    public  FactorialRunPLaybook()
    {

    }
    public  FactorialRunPLaybook(String command,String scp)
    {
        this.command = command;
        this.scp = scp;
    }
    public FactorialRunPLaybook(String command, String rabbitHost, String rabbitUsername, String rabbitPassword, int rabbitPort, String queueName) {
        this.command = command;
        this.rabbitHost = rabbitHost;
        this.rabbitUsername = rabbitUsername;
        this.rabbitPassword = rabbitPassword;
        this.rabbitPort = rabbitPort;
        this.queueName = queueName;
    }
    @Override
    public  String call() throws  Exception{
        String response = "";
        InClient client = new InClient(rabbitHost, rabbitUsername, rabbitPassword, rabbitPort, queueName);
        try {
            response = client.call(command);//Command run playbook
        } catch (Exception e) {
            logger.error("Exception : FactorialRunPlaybook.call ", e);
        } finally {
            client.close();
        }
        return response;
    }
}
