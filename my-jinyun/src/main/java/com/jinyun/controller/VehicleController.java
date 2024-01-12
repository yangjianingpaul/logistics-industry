package com.jinyun.controller;

import com.jinyun.pojo.Vehicle;
import com.jinyun.service.VehicleService;
import com.jinyun.vo.VehicleQueryVo;
import com.jinyun.vo.VehicleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 车辆管理相关业务功能
 */
@RequestMapping("vehicle")
@RestController
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    /**
     * 查询车辆列表
     *
     * @param vehicleQueryVo
     * @return
     */
    @GetMapping
    public Object queryVehicleList(VehicleQueryVo vehicleQueryVo) {
        return this.vehicleService.queryVehicleList(vehicleQueryVo);
    }

    /**
     * 根据id查询车辆数据
     *
     * @param id
     * @return
     */
    @GetMapping("details/{id}")
    public Object queryVehicleById(@PathVariable("id")String id) {
        return this.vehicleService.queryVehicleById(id);
    }

    /**
     * 根据id删除车辆数据
     *
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public Object deleteVehicleById(@PathVariable("id") String id) {
        return this.vehicleService.deleteVehicleById(id);
    }

    /**
     * 新增车辆
     *
     * @param vehicle
     * @return
     */
    @PostMapping
    public Object createVehicle (@RequestBody Vehicle vehicle) {
        return this.vehicleService.createVehicle(vehicle);
    }

    /**
     * 修改车辆
     *
     * @param id
     * @param vehicle
     * @return
     */
    @PutMapping("{id}")
    public Object updateVehicle (@PathVariable("id") String id,
                               @RequestBody Vehicle vehicle) {
        return this.vehicleService.updateVehicle(id, vehicle);
    }
}
