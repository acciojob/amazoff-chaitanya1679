package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {

    HashMap<String, Order> orderHashMap = new HashMap<>();
    HashMap<String, DeliveryPartner> deliveryPartnerHashMap = new HashMap<>();

    HashMap<String, List<Order>> partnerOrderHashMap = new HashMap<>();

    HashMap<String, List<Order>> deliveredOrdersByPartner = new HashMap<>();


    public void addOrder(Order order) {
        orderHashMap.put(order.getId(), order);
    }

    public void addPartner(String partnerId) {
        DeliveryPartner dp = new DeliveryPartner(partnerId);
        deliveryPartnerHashMap.put(partnerId,dp);
    }


    public void addOrderPartnerPair(String orderId, String partnerId) {
        Order order = orderHashMap.get(orderId);
        DeliveryPartner deliveryPartner = deliveryPartnerHashMap.get(partnerId);

        if (order == null || deliveryPartner == null) return;

        if (partnerOrderHashMap.containsKey(deliveryPartner.getId())) {
            partnerOrderHashMap.get(deliveryPartner.getId()).add(order);
        }

        else {
            List<Order> list = new ArrayList<>();
            list.add(order);
            partnerOrderHashMap.put(deliveryPartner.getId(), list);
        }

        deliveryPartner.setNumberOfOrders(deliveryPartner.getNumberOfOrders()+1);
    }


    public Order getOrderById(String orderId) {
        return orderHashMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return deliveryPartnerHashMap.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        if (!partnerOrderHashMap.containsKey(partnerId)) return 0;
        List<Order> list = partnerOrderHashMap.get(partnerId);
        return list.size();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {

        List<Order> list = partnerOrderHashMap.get(partnerId);
        List<String> orders = new ArrayList<>();
        for (Order o: list) {
            orders.add(o.getId());
        }
        return orders;
    }

    public List<String> getAllOrders() {
        List<String> orders = new ArrayList<>();
        for (String s: orderHashMap.keySet()) {
            orders.add(s);
        }

        return orders;
    }

    public Integer getCountOfUnassignedOrders() {
        Integer totalOrders = getAllOrders().size();

        Integer countOfAssignedOrders = 0;
        Integer ans = 0;

        for (String dp: partnerOrderHashMap.keySet()) {
            countOfAssignedOrders += getOrderCountByPartnerId(dp);
        }

        ans = totalOrders - countOfAssignedOrders;
        return ans;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        String hh = time.substring(0,2);
        String mm = time.substring(3);
        int h = Integer.parseInt(hh) * 60;
        int m = Integer.parseInt(mm);
        int givenTime = h+m;

        if (deliveredOrdersByPartner.containsKey(partnerId)) {
            deliveredOrdersByPartner.remove(partnerId);
        }


        Integer countOfAllOrders = getOrderCountByPartnerId(partnerId);

        List<String> ordersOfPartner = getOrdersByPartnerId(partnerId);

        List<Order> deliveredByPartner = new ArrayList<>();

        for (String s: ordersOfPartner) {
            Order order = orderHashMap.get(s);
            if(givenTime - order.getDeliveryTime() >= 0) {
                deliveredByPartner.add(order);
            }
        }

        deliveredOrdersByPartner.put(partnerId,deliveredByPartner);

        Integer countOfDeliveredOrders = deliveredByPartner.size();

        return countOfAllOrders - countOfDeliveredOrders;

    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        List<Order> deliveredOrders = deliveredOrdersByPartner.get(partnerId);
        Integer n = deliveredOrders.size();

        Order lastDelivered = deliveredOrders.get(n-1);
        int time = lastDelivered.getDeliveryTime();

        int h = time/60;
        int m = time%60;

        String hh = String.valueOf(h);
        String mm = "";
        if (m >= 0 && m <=9) {
            mm = "0" + String.valueOf(m);
        }
        else {
            mm = String.valueOf(m);
        }

        return hh + ":" + mm;
    }

    public void deletePartnerById(String partnerId) {
        deliveryPartnerHashMap.remove(partnerId);
        partnerOrderHashMap.remove(partnerId);
    }

    public void deleteOrderById(String orderId) {
        orderHashMap.remove(orderId);
        for (String dp: partnerOrderHashMap.keySet()) {
            List<Order> orders = partnerOrderHashMap.get(dp);
            for (Order order: orders) {
                if (order.getId() == orderId) {
                    partnerOrderHashMap.remove(dp);
                    orders.remove(order);
                    partnerOrderHashMap.put(dp,orders);
                }
            }
        }
    }
}