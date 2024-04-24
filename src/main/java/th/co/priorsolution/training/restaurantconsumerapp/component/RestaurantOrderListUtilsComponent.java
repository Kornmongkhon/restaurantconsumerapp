package th.co.priorsolution.training.restaurantconsumerapp.component;

import org.springframework.stereotype.Component;
import th.co.priorsolution.training.restaurantconsumerapp.model.RestaurantOrderListModel;
import th.co.priorsolution.training.restaurantconsumerapp.model.RestaurantOrderListResponseModel;


import java.util.ArrayList;
import java.util.List;

@Component
public class RestaurantOrderListUtilsComponent {
    public List<RestaurantOrderListResponseModel> transformQueryResultToResponseModel(List<RestaurantOrderListModel> queryResult){
        List<RestaurantOrderListResponseModel> restaurantOrderListResponseModels = new ArrayList<>();
        for (RestaurantOrderListModel x : queryResult) {
            RestaurantOrderListResponseModel y = new RestaurantOrderListResponseModel();
            y.setOrdlistId(x.getOrdlistId());
            y.setOrdId(x.getOrdId());
            y.setFoodName(x.getFoodName());
            y.setFoodType(x.getFoodType());
            y.setFoodStatus(x.getFoodStatus());
            y.setFoodBychef(x.getFoodBychef());
            restaurantOrderListResponseModels.add(y);
        }
        return  restaurantOrderListResponseModels;
    }
}
