package th.co.priorsolution.training.restaurantconsumerapp.model;

import lombok.Data;

@Data
public class ResponseModel<T> {
    private int statusCode;

    private String description;

    private T data;
}
