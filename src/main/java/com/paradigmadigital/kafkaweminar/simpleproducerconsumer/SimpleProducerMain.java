package com.paradigmadigital.kafkaweminar.simpleproducerconsumer;

import com.paradigmadigital.kafkaweminar.AppConfiguration;
import com.paradigmadigital.kafkaweminar.kafkaclient.Producer;
import com.paradigmadigital.kafkaweminar.twitterclient.RandomListener;
import com.paradigmadigital.kafkaweminar.twitterclient.StreamingClient;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Properties;

public class SimpleProducerMain extends AppConfiguration {

    static public void main(String[] args) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        Configuration twitterClientConf = configurationBuilder.setOAuthConsumerKey(getTwitterConsumerKey())
                .setOAuthConsumerSecret(getTwitterConsumerSecret())
                .setOAuthAccessToken(getTwitterAccessToken())
                .setOAuthAccessTokenSecret(getTwitterAccessTokenSecret())
                .setJSONStoreEnabled(true).build();

        Properties kafkaProperties = getKafkaProducerProperties();

        Producer<String, String> kafkaProducer = new Producer<>(kafkaProperties, getTwitterTopic());
        RandomListener randomListener = new RandomListener(kafkaProducer);
        StreamingClient twitterStream = StreamingClient.getInstance(twitterClientConf, randomListener);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                twitterStream.close();
                kafkaProducer.close();
            }
        });
    }
}
