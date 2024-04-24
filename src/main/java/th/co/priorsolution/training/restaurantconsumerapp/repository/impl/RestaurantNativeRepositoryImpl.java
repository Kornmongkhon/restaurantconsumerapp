package th.co.priorsolution.training.restaurantconsumerapp.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import th.co.priorsolution.training.restaurantconsumerapp.model.*;
import th.co.priorsolution.training.restaurantconsumerapp.repository.RestaurantNativeRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RestaurantNativeRepositoryImpl implements RestaurantNativeRepository {
    private JdbcTemplate jdbcTemplate;

    public RestaurantNativeRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertOrder(RestaurantOrderModel restaurantOrderModel) {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO orders (ord_date, ord_status)");
        sql.append(" VALUES (DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 7 HOUR),?)");
//        this.jdbcTemplate.update(sql.toString(),paramList.toArray());
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
                statement.setString(1,restaurantOrderModel.getOrdStatus());
                return statement;
            }
        },holder);
        int ordId = holder.getKey().intValue();
        return ordId;
    }

    @Override
    public void insertOrderList(int ordId, List<RestaurantOrderListModel> restaurantOrderListModels) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO order_list (ord_id, food_id, food_name, food_type, food_quantity, food_status, food_bychef) ");
        sql.append("SELECT ?, ?, m.menu_name, m.menu_type, ?, ?, c.chef_id FROM menu_item m INNER JOIN chefs c ON m.menu_type = c.chef_role WHERE m.menu_id = ?");

        try {
            jdbcTemplate.batchUpdate(sql.toString(), restaurantOrderListModels, restaurantOrderListModels.size(),
                    (ps, model) -> {
                        // Set parameters for the PreparedStatement
                        ps.setInt(1, ordId);
                        ps.setInt(2, model.getFoodId());
                        ps.setInt(3, model.getFoodQuantity());
                        ps.setString(4, model.getFoodStatus());
                        ps.setInt(5, model.getFoodId());
                    });
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to insert order list: " + e.getMessage());
        }
    }

    @Override
    public List<RestaurantOrderListModel> findOrderListsByOrder(RestaurantOrderListRequestModel restaurantOrderListRequestModel) {
        List<Object> paramList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ordlist_id, ord_id, food_id, food_name, food_type, food_status, food_bychef");
        sql.append(" FROM order_list");
        sql.append(" WHERE 1=1 AND food_status <> 'Done'");
        if(restaurantOrderListRequestModel.getOrdlistId() > 0){
            sql.append(" AND ordlist_id = ?");
            paramList.add(restaurantOrderListRequestModel.getOrdlistId());
        }
        if(restaurantOrderListRequestModel.getOrdId() > 0){
            sql.append(" AND ord_id = ?");
            paramList.add(restaurantOrderListRequestModel.getOrdId());
        }
        if(StringUtils.isNotEmpty(restaurantOrderListRequestModel.getFoodName())){
            sql.append(" AND food_name = ?");
            paramList.add(restaurantOrderListRequestModel.getFoodName());
        }
        if(StringUtils.isNotEmpty(restaurantOrderListRequestModel.getFoodType())){
            sql.append(" AND food_type = ?");
            paramList.add(restaurantOrderListRequestModel.getFoodType());
        }
        if(StringUtils.isNotEmpty(restaurantOrderListRequestModel.getFoodStatus())){
            sql.append(" AND food_status = ?");
            paramList.add(restaurantOrderListRequestModel.getFoodStatus());
        }
        List<RestaurantOrderListModel> result = this.jdbcTemplate.query(sql.toString(), new RowMapper<RestaurantOrderListModel>() {
            @Override
            public RestaurantOrderListModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                int cols = 1;
                RestaurantOrderListModel restaurantOrderListModel = new RestaurantOrderListModel();
                restaurantOrderListModel.setOrdlistId(rs.getInt(cols++));
                restaurantOrderListModel.setOrdId(rs.getInt(cols++));
                restaurantOrderListModel.setFoodId(rs.getInt(cols++));
                restaurantOrderListModel.setFoodName(rs.getString(cols++));
                restaurantOrderListModel.setFoodType(rs.getString(cols++));
                restaurantOrderListModel.setFoodStatus(rs.getString(cols++));
                restaurantOrderListModel.setFoodBychef(rs.getInt(cols++));
                return restaurantOrderListModel;
            }
        },paramList.toArray());

        return result;
    }

    @Override
    public int updateOrder(RestaurantOrderListModel restaurantOrderListModel) {
        List<Object> paramList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE order_list ol");
        sql.append(" LEFT JOIN orders o ON ol.ord_id = o.ord_id");
        sql.append(" LEFT JOIN chefs c ON ol.food_bychef = c.chef_id");
        if(StringUtils.isNotEmpty(restaurantOrderListModel.getFoodStatus()) && restaurantOrderListModel.getFoodBychef() > 0 && restaurantOrderListModel.getOrdlistId() > 0){
            sql.append(" SET ol.food_status = ?");
            sql.append(" WHERE ol.ordlist_id = ?");
            sql.append(" AND ol.food_bychef = ?");
            sql.append(" AND ol.ord_id IN ( SELECT temp.ord_id FROM ( SELECT ord_id FROM order_list WHERE ordlist_id = ?) as temp)");
            paramList.add(restaurantOrderListModel.getFoodStatus());
            paramList.add(restaurantOrderListModel.getOrdlistId());
            paramList.add(restaurantOrderListModel.getFoodBychef());
            paramList.add(restaurantOrderListModel.getOrdlistId());
        }else {
            throw new IllegalArgumentException("Invalid parameters provided.");
        }
        int updateRow = 0;
        if (paramList.size() > 0) {
            updateRow = this.jdbcTemplate.update(sql.toString(), paramList.toArray());
            if (updateRow == 0) {
                throw new RuntimeException("Some information not found in database.");
            }
        }
        return updateRow;
    }

    @Override
    public Map<String, Object> getTotalOrderInfo(int ordId, String foodStatus) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CONVERT(COUNT(*),SIGNED) AS totalOrders,");
        sql.append(" CONVERT(SUM(CASE WHEN food_status = ? THEN 1 ELSE 0 END),SIGNED) AS totalOrdersWithStatus");
        sql.append(" FROM order_list WHERE ord_id IN ( SELECT temp.ord_id FROM ( SELECT ord_id FROM order_list WHERE ordlist_id = ?) as temp)");
        try {
            List<Object> paramList = new ArrayList<>();
            paramList.add(foodStatus);
            paramList.add(ordId);
//            return this.jdbcTemplate.queryForMap(sql.toString(),foodStatus,ordId);
            return this.jdbcTemplate.queryForObject(sql.toString(), paramList.toArray(), new RowMapper<Map<String, Object>>() {
                @Override
                public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Map<String, Object> result = new HashMap<>();
                    result.put("totalOrders",rs.getInt("totalOrders"));
                    result.put("totalOrdersWithStatus",rs.getInt("totalOrdersWithStatus"));
                    return result;
                }
            });
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public void updateStatus(int ordlistId, String foodStatus) {
        StringBuilder sql = new StringBuilder();
        List<Object> paramList = new ArrayList<>();
        sql.append("UPDATE orders SET ord_status = ? WHERE ord_id IN ( SELECT temp.ord_id FROM ( SELECT ord_id FROM order_list WHERE ordlist_id = ?) as temp)");
        paramList.add(foodStatus);
        paramList.add(ordlistId);
        this.jdbcTemplate.update(sql.toString(),paramList.toArray());
    }

    @Override
    public List<RestaurantChefModel> findChefByChef(RestaurantChefCriteriaModel restaurantChefModel) {
        List<Object> paramList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT chef_id, chef_name, chef_role, hire_date");
        sql.append(" FROM chefs");
        sql.append(" WHERE 1=1");
        if(StringUtils.isNotEmpty(restaurantChefModel.getChefName())){
            sql.append(" AND chef_name = ?");
            paramList.add(restaurantChefModel.getChefName());
        }
        if(StringUtils.isNotEmpty(restaurantChefModel.getChefRole())){
            sql.append(" AND chef_role = ?");
            paramList.add(restaurantChefModel.getChefRole());
        }
        if(restaurantChefModel.getHireDate() != null && restaurantChefModel.getHireDate2() == null){
            sql.append(" AND hire_date = ?");
            paramList.add(restaurantChefModel.getHireDate());
        }
        if(restaurantChefModel.getHireDate() != null && restaurantChefModel.getHireDate2() != null){
            sql.append(" AND hire_date between ? AND ?");
            paramList.add(restaurantChefModel.getHireDate());
            paramList.add(restaurantChefModel.getHireDate2());
        }
        List<RestaurantChefModel> result =this.jdbcTemplate.query(sql.toString(), new RowMapper<RestaurantChefModel>() {
            @Override
            public RestaurantChefModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                RestaurantChefModel x =new RestaurantChefModel();
                int col = 1;
                x.setChefId(rs.getInt(col++));
                x.setChefName(rs.getString(col++));
                x.setChefRole(rs.getString(col++));
                x.setHireDate(rs.getDate(col++).toLocalDate());
                return x;
            }
        },paramList.toArray());
        return result;
    }

    @Override
    public int servedOrderStatus(RestaurantOrderModel restaurantOrderModel) {
        List<Object> paramList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE orders");
        sql.append(" SET ord_status = ?");
        sql.append(" WHERE ord_id = ? AND ord_status = 'Done'");
        paramList.add(restaurantOrderModel.getOrdStatus());
        paramList.add(restaurantOrderModel.getOrdId());
        int updateRow = 0;
        if(paramList.size() > 0){
            updateRow = this.jdbcTemplate.update(sql.toString(),paramList.toArray());
            if(updateRow <= 0){
                throw new RuntimeException("Order Status not equal to 'Done' or missing order id");
            }
        }
        return updateRow;
    }
}
