package th.co.priorsolution.training.restaurantconsumerapp.repository;

import th.co.priorsolution.training.restaurantconsumerapp.model.*;

import java.util.List;
import java.util.Map;

public interface RestaurantNativeRepository {
    public int insertOrder(RestaurantOrderModel restaurantOrderModel);

    public void insertOrderList(int ordId, List<RestaurantOrderListModel> restaurantOrderListModels);

    public List<RestaurantOrderListModel> findOrderListsByOrder(RestaurantOrderListRequestModel restaurantOrderListRequestModel);

    int updateOrder(RestaurantOrderListModel restaurantOrderListModel);

    public Map<String,Object> getTotalOrderInfo(int ordId, String foodStatus);

    void updateStatus(int ordlistId, String foodStatus);

    List<RestaurantChefModel> findChefByChef(RestaurantChefCriteriaModel restaurantChefCriteriaModel);

    int servedOrderStatus(RestaurantOrderModel restaurantOrderModel);
}
