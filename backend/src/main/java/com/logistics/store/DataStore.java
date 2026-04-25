package com.logistics.store;

import com.logistics.entity.Branch;
import com.logistics.entity.Courier;
import com.logistics.entity.DispatchLog;
import com.logistics.entity.Order;
import com.logistics.entity.StatusLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class DataStore {
    private static final AtomicLong COURIER_ID_GENERATOR = new AtomicLong(1);
    private static final AtomicLong ORDER_ID_GENERATOR = new AtomicLong(1);
    private static final AtomicLong BRANCH_ID_GENERATOR = new AtomicLong(1);
    private static final AtomicLong DISPATCH_LOG_ID_GENERATOR = new AtomicLong(1);
    private static final AtomicLong STATUS_LOG_ID_GENERATOR = new AtomicLong(1);

    private static final ConcurrentHashMap<Long, Courier> courierMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Order> orderMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Branch> branchMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, DispatchLog> dispatchLogMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, StatusLog> statusLogMap = new ConcurrentHashMap<>();

    public static Long nextCourierId() {
        return COURIER_ID_GENERATOR.getAndIncrement();
    }

    public static Long nextOrderId() {
        return ORDER_ID_GENERATOR.getAndIncrement();
    }

    public static Long nextBranchId() {
        return BRANCH_ID_GENERATOR.getAndIncrement();
    }

    public static Long nextDispatchLogId() {
        return DISPATCH_LOG_ID_GENERATOR.getAndIncrement();
    }

    public static Long nextStatusLogId() {
        return STATUS_LOG_ID_GENERATOR.getAndIncrement();
    }

    public static ConcurrentHashMap<Long, Courier> getCourierMap() {
        return courierMap;
    }

    public static ConcurrentHashMap<Long, Order> getOrderMap() {
        return orderMap;
    }

    public static ConcurrentHashMap<Long, Branch> getBranchMap() {
        return branchMap;
    }

    public static ConcurrentHashMap<Long, DispatchLog> getDispatchLogMap() {
        return dispatchLogMap;
    }

    public static ConcurrentHashMap<Long, StatusLog> getStatusLogMap() {
        return statusLogMap;
    }

    public static List<Courier> getAllCouriers() {
        return new ArrayList<>(courierMap.values());
    }

    public static List<Order> getAllOrders() {
        return new ArrayList<>(orderMap.values());
    }

    public static List<Branch> getAllBranches() {
        return new ArrayList<>(branchMap.values());
    }

    public static List<DispatchLog> getAllDispatchLogs() {
        return new ArrayList<>(dispatchLogMap.values());
    }

    public static List<StatusLog> getAllStatusLogs() {
        return new ArrayList<>(statusLogMap.values());
    }
}
