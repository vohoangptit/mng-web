package com.nera.nms;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class InClient implements AutoCloseable {
    
// ####  Edit command here
    public static final String COMMAND = "/usr/bin/ansible-playbook -i /home/phuongnguyen/playbook/inventories/ /home/phuongnguyen/playbook/juniper.yml";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Connection connection;
    private Channel channel;
    private String requestQueueName;

    public InClient(String rabbitHost,String rabbitUsername,String rabbitPassword,int rabbitPort,String requestQueueName) throws IOException, TimeoutException {
        this.requestQueueName = StringUtils.isNotBlank(requestQueueName) ? requestQueueName : "rpc_queue";
        /*
         * Create connection
         */
        ConnectionFactory factory = new ConnectionFactory();
        //ip point to rabbitmq server
        factory.setHost(rabbitHost);
        factory.setPort(rabbitPort);
        if(StringUtils.isNotBlank(rabbitUsername) && StringUtils.isNotBlank(rabbitPassword)) {
            factory.setUsername(rabbitUsername);
            factory.setPassword(rabbitPassword);
            logger.info("Set userName and password.");
        } else {
            logger.info("Username or password rabbit empty.");
        }
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    /**
     *Method send msg to server
     * @param message data send to server
     * @return response is received from server
     * @throws IOException
     * @throws InterruptedException
     */
    public String call(String message) throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();
        if(channel == null || StringUtils.isBlank(requestQueueName)) {
            logger.info("Channel is null. Connect rabbit failed.");
            return "Connect Rabbit Failed";
        }
        //create channel  to receive  response
        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        //Send request to server
        channel.basicPublish("", requestQueueName, props, message.getBytes(StandardCharsets.UTF_8));

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
        //Get response
        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                boolean check = response.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
                if(check) {
                    logger.info("Connect Success");
                }
            }
        }, consumerTag -> {
        });

        String result = response.take();
        channel.basicCancel(ctag);
        return result;
    }

    public void close() throws IOException {
        if(connection != null) {
            connection.close();
        }
    }
}
