package local;

import local.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderReceived_CannotOrderCencel(@Payload OrderReceived orderReceived){

        if(orderReceived.isMe()){
            System.out.println("##### listener CannotOrderCencel : " + orderReceived.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderRejected_CannotOrderCencel(@Payload OrderRejected orderRejected){

        if(orderRejected.isMe()){
            System.out.println("##### listener CannotOrderCencel : " + orderRejected.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderRejected_PayBack(@Payload OrderRejected orderRejected){

        if(orderRejected.isMe()){
            System.out.println("##### listener PayBack : " + orderRejected.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderRejected_OrderCancelFromDelivery(@Payload OrderRejected orderRejected){

        if(orderRejected.isMe()){
            System.out.println("##### listener OrderCancelFromDelivery : " + orderRejected.toJson());
        }
    }

}
