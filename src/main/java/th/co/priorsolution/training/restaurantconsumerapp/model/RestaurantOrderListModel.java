package th.co.priorsolution.training.restaurantconsumerapp.model;

import lombok.Data;

@Data
public class RestaurantOrderListModel {
    private int ordlistId;

    private int ordId;

    private int foodId;

    private String foodName;

    private String foodType;

    private int foodQuantity;

    private String foodStatus;

    private int foodBychef;
}
