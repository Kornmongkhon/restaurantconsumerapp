package th.co.priorsolution.training.restaurantconsumerapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RestaurantChefModel {
    private int chefId;

    private String chefName;

    private String chefRole;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;
}
