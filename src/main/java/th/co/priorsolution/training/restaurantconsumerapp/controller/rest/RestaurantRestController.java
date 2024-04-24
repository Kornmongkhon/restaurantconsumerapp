package th.co.priorsolution.training.restaurantconsumerapp.controller.rest;

import org.springframework.web.bind.annotation.*;
import th.co.priorsolution.training.restaurantconsumerapp.model.*;
import th.co.priorsolution.training.restaurantconsumerapp.service.RestaurantService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RestaurantRestController {
    private RestaurantService restaurantService;

    public RestaurantRestController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }
    @GetMapping("/v1/restaurant/chef/getChefs")
    public ResponseModel<List<RestaurantChefModel>> getChef(@RequestBody RestaurantChefCriteriaModel restaurantChefCriteriaModel){
        return this.restaurantService.getChefByChef(restaurantChefCriteriaModel);
    }

    @GetMapping("/v1/restaurant/chef/getOrder")
    public ResponseModel<List<RestaurantOrderListResponseModel>> getInquiryOrder(@RequestBody RestaurantOrderListRequestModel restaurantOrderListRequestModel){
        return this.restaurantService.getOrder(restaurantOrderListRequestModel);
    }

    @PatchMapping("/v1/restaurant/chef/updateOrder")
    public ResponseModel<Integer> updateOrder(@RequestBody RestaurantOrderListModel restaurantOrderListModel){
        return this.restaurantService.updateOrderList(restaurantOrderListModel);
    }
}
