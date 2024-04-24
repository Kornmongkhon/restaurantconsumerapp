package th.co.priorsolution.training.restaurantconsumerapp.model;

import lombok.Data;

@Data
public class RestaurantOrderListRequestModel {
    private int ordId;

    private int ordlistId;

    private int foodId;

    private String foodName;

    private String foodType;

    private String foodStatus;

    private int foodBychef;
}
