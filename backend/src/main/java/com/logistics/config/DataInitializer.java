package com.logistics.config;

import com.logistics.entity.Branch;
import com.logistics.entity.Courier;
import com.logistics.enums.CourierStatusEnum;
import com.logistics.service.BranchService;
import com.logistics.service.CourierService;
import com.logistics.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CourierService courierService;

    @Autowired
    private BranchService branchService;

    @Override
    public void run(String... args) {
        if (DataStore.getAllCouriers().isEmpty()) {
            initBranches();
            initCouriers();
        }
    }

    private void initBranches() {
        createBranch("朝阳区网点", "朝阳区,东城区,西城区");
        createBranch("海淀区网点", "海淀区,丰台区,石景山区");
        createBranch("通州区网点", "通州区,大兴区,亦庄开发区");
    }

    private void initCouriers() {
        createCourier("张三", "朝阳区", 5, CourierStatusEnum.IDLE.getCode());
        createCourier("李四", "朝阳区", 5, CourierStatusEnum.IDLE.getCode());
        createCourier("王五", "海淀区", 4, CourierStatusEnum.IDLE.getCode());
        createCourier("赵六", "海淀区", 6, CourierStatusEnum.IDLE.getCode());
        createCourier("钱七", "通州区", 5, CourierStatusEnum.IDLE.getCode());
    }

    private void createBranch(String name, String coverageRegions) {
        Branch branch = new Branch();
        branch.setName(name);
        branch.setCoverageRegions(coverageRegions);
        branch.setStatus("active");
        branchService.create(branch);
    }

    private void createCourier(String name, String region, Integer maxTaskCount, String status) {
        Courier courier = new Courier();
        courier.setName(name);
        courier.setRegion(region);
        courier.setMaxTaskCount(maxTaskCount);
        courier.setStatus(status);
        courierService.create(courier);
    }
}
