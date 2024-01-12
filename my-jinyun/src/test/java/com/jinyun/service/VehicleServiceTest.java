package com.jinyun.service;

import com.jinyun.vo.LocationNamePoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VehicleServiceTest {

    @Autowired
    private VehicleService vehicleService;

    @Test
    public void updateLocation() {
        String id = "65407f440e54457d6b9295f9";
        LocationNamePoint locationNamePoint = new LocationNamePoint();
        locationNamePoint.setLng("116.396939");
        locationNamePoint.setLat("39.923457");
        locationNamePoint.setName("天安门");

        Boolean result = this.vehicleService.updateLocation(id, locationNamePoint);
        System.out.println(result);
    }
}