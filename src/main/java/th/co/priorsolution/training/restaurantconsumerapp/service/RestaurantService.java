package th.co.priorsolution.training.restaurantconsumerapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import th.co.priorsolution.training.restaurantconsumerapp.component.RestaurantOrderListUtilsComponent;
import th.co.priorsolution.training.restaurantconsumerapp.model.*;
import th.co.priorsolution.training.restaurantconsumerapp.repository.RestaurantNativeRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RestaurantService {
    private RestaurantNativeRepository restaurantNativeRepository;

    private RestaurantOrderListUtilsComponent restaurantOrderListUtilsComponent;

    public RestaurantService(RestaurantNativeRepository restaurantNativeRepository, RestaurantOrderListUtilsComponent restaurantOrderListUtilsComponent) {
        this.restaurantNativeRepository = restaurantNativeRepository;
        this.restaurantOrderListUtilsComponent = restaurantOrderListUtilsComponent;
    }

    public void insertOrder(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            RestaurantOrderModel restaurantOrderModel = mapper.readValue(message, RestaurantOrderModel.class);
            int orderId = this.restaurantNativeRepository.insertOrder(restaurantOrderModel);
            this.restaurantNativeRepository.insertOrderList(orderId, restaurantOrderModel.getOrderList());
            log.info("Insert order success : ordId {}",orderId);
            log.info("{}",restaurantOrderModel.getOrderList());
        }catch (Exception e){
            e.printStackTrace();
            log.info("Insert order failed : {}",e.getMessage());
        }
    }

    public void updateServedOrderStatus(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            RestaurantOrderModel restaurantOrderModel = mapper.readValue(message, RestaurantOrderModel.class);
            int updateRow = this.restaurantNativeRepository.servedOrderStatus(restaurantOrderModel);
        }catch (Exception e){
            e.printStackTrace();
            log.info("Update order status failed : {}",e.getMessage());
        }
    }

    public ResponseModel<List<RestaurantChefModel>> getChefByChef(RestaurantChefCriteriaModel restaurantChefCriteriaModel) {
        ResponseModel<List<RestaurantChefModel>> result = new ResponseModel<>();
        result.setStatusCode(200);
        result.setDescription("OK");
        try{
            List<RestaurantChefModel> transformData = this.restaurantNativeRepository.findChefByChef(restaurantChefCriteriaModel);
//            result.setOrdId(restaurantChefCriteriaModel.getCId());
            result.setData(transformData);
        }catch (Exception e){
            result.setStatusCode(400);
            result.setDescription(e.getMessage());
        }
        return result;
    }

    public ResponseModel<List<RestaurantOrderListResponseModel>> getOrder(RestaurantOrderListRequestModel restaurantOrderListRequestModel){
        ResponseModel<List<RestaurantOrderListResponseModel>> result = new ResponseModel<>();
        result.setStatusCode(200);
        result.setDescription("OK");
        try{
            List<RestaurantOrderListModel> queryResult = this.restaurantNativeRepository.findOrderListsByOrder(restaurantOrderListRequestModel);

            List<RestaurantOrderListResponseModel>dataList = this.restaurantOrderListUtilsComponent.transformQueryResultToResponseModel(queryResult);
            result.setData(dataList);
        }catch (Exception e){
            result.setStatusCode(400);
            result.setDescription(e.getMessage());
        }
        return result;
    }

    public ResponseModel<Integer> updateOrderList(RestaurantOrderListModel restaurantOrderListModel) {
        ResponseModel<Integer> result = new ResponseModel<>();
        result.setStatusCode(200);
        result.setDescription("OK");
        try {
            int updateRow = this.restaurantNativeRepository.updateOrder(restaurantOrderListModel);
            result.setData(updateRow);
            Map<String, Object> totalOrdersInfo = this.restaurantNativeRepository.getTotalOrderInfo(restaurantOrderListModel.getOrdlistId(), restaurantOrderListModel.getFoodStatus());
            if(totalOrdersInfo==null){
                throw new RuntimeException("Failed to retrieve total orders information from the database.");
            }else{
                int totalOrders = (int) totalOrdersInfo.get("totalOrders");
                int totalOrdersWithStatus = (int) totalOrdersInfo.get("totalOrdersWithStatus");
                if (totalOrders == totalOrdersWithStatus) {
                    this.restaurantNativeRepository.updateStatus(restaurantOrderListModel.getOrdlistId(), restaurantOrderListModel.getFoodStatus());
                }
            }
        }catch (Exception e){
            result.setStatusCode(400);
            result.setDescription(e.getMessage());
        }
        return result;
    }
}
