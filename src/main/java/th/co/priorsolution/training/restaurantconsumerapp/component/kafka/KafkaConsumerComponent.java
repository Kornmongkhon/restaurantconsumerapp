package th.co.priorsolution.training.restaurantconsumerapp.component.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import th.co.priorsolution.training.restaurantconsumerapp.service.RestaurantService;

@Slf4j
@Component
public class KafkaConsumerComponent {
    private RestaurantService restaurantService;

    public KafkaConsumerComponent(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @KafkaListener(topics = "${kafka.topics.restaurant.name}",groupId = "${kafka.groupid.restaurant}")
    public void consumerInsertOrderMessage(@Payload String message) throws Exception{
        log.info("factory got message got {}",message);
        this.restaurantService.insertOrder(message);
    }
    @KafkaListener(topics = "${kafka.topics.served.name}",groupId = "${kafka.groupid.served}")
    public void consumerServedOrderMessage(@Payload String message) throws Exception{
        log.info("factory got message got {}",message);
        this.restaurantService.updateServedOrderStatus(message);
    }
}
