package th.co.priorsolution.training.restaurantconsumerapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RestaurantOrderModel {
    private int ordId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ordDate;

    private String ordStatus;

    private List<RestaurantOrderListModel> orderList;

}
