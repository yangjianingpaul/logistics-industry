package com.jinyun.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jinyun.config.BaiduConfig;
import com.jinyun.pojo.ElectronicFence;
import com.jinyun.pojo.Vehicle;
import com.jinyun.vo.LocationPoint;
import com.jinyun.vo.RunParamVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BaiduService {
    @Autowired
    private BaiduApiService baiduApiService;
    @Autowired
    private BaiduConfig baiduConfig;

    /**
     * 根据中文的地址查询对应的经纬度数据
     *
     * @param sendAddress
     * @return
     */
    public double[] queryGeoAddress(String sendAddress) {
        String url = baiduConfig.getWebUrl() +  "geocoding/v3/";
        Map<String, Object> param = MapUtil.builder(new HashMap<String, Object>())
                .put("address", sendAddress)
                .put("ret_coordtype", "bd09ll") //返回坐标系
                .put("output", "json").build(); //返回json格式的数据
        return this.baiduApiService.execute(url, Method.GET, param, response -> {
            if (response.isOk()){
                String jsonData = response.body();
                JSONObject jsonObject = JSONUtil.parseObj(jsonData);
                if (jsonObject.getInt("status") != 0) {
//                    出错了
                    return null;
                }

                JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
                return new double[]{location.getDouble("lng"), location.getDouble("lat")};
            }
            return null;
        });
    }

    /**
     * 查询运单轨迹数据
     *
     * @param senderLocationPoint
     * @param receiverLocationPoint
     * @param wayPoints
     * @return
     */
    public List<LocationPoint> queryTrackRoute(double[] senderLocationPoint, double[] receiverLocationPoint,
                                               String wayPoints) {
        String url = baiduConfig.getWebUrl() +  "directionlite/v1/driving";
        Map<String, Object> param = MapUtil.builder(new HashMap<String, Object>())
                .put("origin", senderLocationPoint[1] + "," + senderLocationPoint[0])
                .put("destination", receiverLocationPoint[1] + "," + receiverLocationPoint[0])
                .put("waypoints", wayPoints)
                .put("coordtype", "bd09ll")
                .put("ret_coordtype", "bd09ll").build(); //返回json格式的数据

        return this.baiduApiService.execute(url, Method.GET, param, response -> {
            if (response.isOk()){
                String jsonData = response.body();
                JSONObject jsonObject = JSONUtil.parseObj(jsonData);
                if (jsonObject.getInt("status") != 0) {
//                    出错了
                    return null;
                }
//                路线数组
                JSONArray routes = jsonObject.getJSONObject("result").getJSONArray("routes");
                if (CollUtil.isEmpty(routes)) {
                    return null;
                }
//                路线分段数据
                JSONArray steps = routes.getJSONObject(0).getJSONArray("steps");
                if (CollUtil.isEmpty(steps)) {
                    return null;
                }

                List<LocationPoint> result = new ArrayList<>();
                for (Object step : steps) {
                    JSONObject jsonObj = (JSONObject) step;
                    String path = jsonObj.getStr("path");
                    String[] arr = StrUtil.splitToArray(path, ';');
                    for (String point : arr) {
                        String[] points = StrUtil.splitToArray(point, ',');
                        LocationPoint locationPoint = new LocationPoint();
                        locationPoint.setLat(points[1]);
                        locationPoint.setLng(points[0]);
                        result.add(locationPoint);
                    }
                }
                return result;
            }
            return null;
        });
    }

    /**
     * 调用百度地图鹰眼服务中的接口创建电子围栏
     *
     * @param electronicFence
     * @return  百度地图电子围栏id
     */
    public Long createElectronicFence(ElectronicFence electronicFence) {
        String url = this.baiduConfig.getYingYanUrl() + "/fence/createpolygonfence";
//        构建多边形坐标数据
        String vertexes = CollUtil.join(electronicFence.getMutiPoints().stream()
                .map(locatiopnPoint -> locatiopnPoint.getLat() + "," + locatiopnPoint.getLng())
                .collect(Collectors.toList()), ";");

//        构造提交参数
        Map<String, Object> param = MapUtil.builder(new HashMap<String, Object>())
                .put("fence_name", electronicFence.getName())   //围栏名称
                .put("vertexes", vertexes)                          //多边形围栏形状点， 结构：纬度，经度；
                .put("coord_type", "bd09ll").build();
        return this.baiduApiService.execute(url, Method.POST, param, response -> {
            if (response.isOk()){
                String body = response.body();
                JSONObject jsonObject = JSONUtil.parseObj(body);
                if (jsonObject.getInt("status") != 0) {
                    return null;
                }
                return jsonObject.getLong("fence_id");
            }
            return null;
        });
    }

    /**
     * 更新百度地图中的电子围栏
     *
     * @param fenceId           电子围栏id
     * @param electronicFence   更新数据
     * @return
     */
    public Boolean updateElectronicFence(Long fenceId, ElectronicFence electronicFence) {
        String url = this.baiduConfig.getYingYanUrl() + "/fence/updatepolygonfence";
//        构建多边形坐标数据
        String vertexes = CollUtil.join(electronicFence.getMutiPoints().stream()
                .map(locationPoint -> locationPoint.getLat() + "," + locationPoint.getLng())
                .collect(Collectors.toList()), ";");

//        构造提交参数
        Map<String, Object> param = MapUtil.builder(new HashMap<String, Object>())
                .put("fence_name", electronicFence.getName())   //围栏名称
                .put("fence_id", fenceId)                       //围栏的唯一标识
                .put("vertexes", vertexes)                      //多边形围栏形状点， 结构：纬度，经度；
                .put("coord_type", "bd09ll").build();

        return this.baiduApiService.execute(url, Method.POST, param, response -> {
            if (response.isOk()){
                String body = response.body();
                JSONObject jsonObject = JSONUtil.parseObj(body);
                return jsonObject.getInt("status") == 0;
            }
            return null;
        });
    }

    /**
     * 删除百度地图中的电子围栏
     *
     * @param fenceId
     * @return
     */
    public Boolean deleteElectronicFence(Long fenceId) {
        String url = this.baiduConfig.getYingYanUrl() + "/fence/delete";

//        构造提交参数
        Map<String, Object> param = MapUtil.builder(new HashMap<String, Object>())
                .put("fence_ids", fenceId).build();

        return this.baiduApiService.execute(url, Method.POST, param, response -> {
            if (response.isOk()){
                String body = response.body();
                JSONObject jsonObject = JSONUtil.parseObj(body);
                return jsonObject.getInt("status") == 0;
            }
            return null;
        });
    }

    /**
     * 创建鹰眼服务中的实体
     *
     * @param licensePlate 车牌号
     * @return
     */
    public Boolean createEntity(String licensePlate) {
        String url = this.baiduConfig.getYingYanUrl() + "/entity/add";
        return this.baiduApiService.execute(url, Method.POST,
                this.createParam(licensePlate), response -> {
                    if (response.isOk()) {
                        String body = response.body();
                        JSONObject jsonObject = JSONUtil.parseObj(body);
                        return jsonObject.getInt("status") == 0;
                    }
                    return false;
                });
    }

    /**
     * 构建创建实体的参数
     *
     * @param licensePlate 车牌号
     * @return
     */
    private Map<String, Object> createParam(String licensePlate) {
        return MapUtil.builder("entity_name", this.createEntityName(licensePlate)).build();
    }

    /**
     * 创建实体名称
     *
     * @param licensePlate 车牌号
     * @return
     */
    private Object createEntityName(String licensePlate) {
        return "vehicle_" + licensePlate;
    }

    /**
     * 添加车辆到电子围栏中
     *
     * @param fenceId
     * @param licensePlate
     * @return
     */
    public Boolean addVehicleToElectronicFence(Long fenceId, String licensePlate) {
        String url = this.baiduConfig.getYingYanUrl() + "/fence/addmonitoredperson";

//        构造提交参数
        Map<String, Object> param = MapUtil.builder(new HashMap<String, Object>())
                .put("fence_id", fenceId)
                .put("monitored_person", this.createEntityName(licensePlate))
                .build();

        return this.baiduApiService.execute(url, Method.POST, param, response -> {
            if (response.isOk()){
                String body = response.body();
                JSONObject jsonObject = JSONUtil.parseObj(body);
                return jsonObject.getInt("status") == 0;
            }
            return null;
        });
    }

    /**
     * 给实体添加轨迹点
     *
     * @param licensePlate 车牌号
     * @param runParamVo   位置数据
     * @return
     */
    public Boolean uploadLocation(String licensePlate, RunParamVo runParamVo) {
        String url = this.baiduConfig.getYingYanUrl() + "/track/addpoint";
        Map<String, Object> paramMap = MapUtil.builder(new HashMap<String, Object>())
                .put("entity_name", this.createEntityName(licensePlate))
                .put("latitude", runParamVo.getLatitude()) //纬度
                .put("longitude", runParamVo.getLongitude()) //经度
                .put("loc_time", System.currentTimeMillis() / 1000) //定位时间戳，精确到秒
                .put("coord_type_input", "bd09ll") //百度坐标类型
                .put("speed", runParamVo.getSpeed()).build();//速度

        return this.baiduApiService.execute(url, Method.POST,
                paramMap, response -> {
                    if (response.isOk()) {
                        String body = response.body();
                        JSONObject jsonObject = JSONUtil.parseObj(body);
                        return jsonObject.getInt("status") == 0;
                    }
                    return false;
                });
    }

    /**
     * 校验车辆是否超出电子围栏
     *
     * @param fenceId           //围栏id
     * @param licensePlate      //车牌号
     * @return
     */
    public Boolean checkVehicleElectronicFence(Long fenceId, String licensePlate) {
        String url = this.baiduConfig.getYingYanUrl() + "/fence/querystatus";

//        构造提交参数
        Map<String, Object> param = MapUtil.builder(new HashMap<String, Object>())
                .put("fence_ids", fenceId)
                .put("monitored_person", this.createEntityName(licensePlate))
                .build();

        return this.baiduApiService.execute(url, Method.GET, param, response -> {
            if (response.isOk()){
                String body = response.body();
                JSONObject jsonObject = JSONUtil.parseObj(body);
                if (jsonObject.getInt("status") != 0) {
                    return false;
                }

                JSONArray jsonArray = jsonObject.getJSONArray("monitored_statuses");
                if (CollUtil.isEmpty(jsonArray)) {
                    return false;
                }
                JSONObject obj = (JSONObject) jsonArray.get(0);
                return StrUtil.equals("out", obj.getStr("monitored_status"));
            }
            return false;
        });
    }

    /**
     * 删除百度鹰眼服务中的实体
     *
     * @param licensePlate 车牌号
     * @return
     */
    public boolean deleteEntity(String licensePlate) {
        String url = this.baiduConfig.getYingYanUrl() + "/entity/delete";
        return this.baiduApiService.execute(url, Method.POST,
                this.createParam(licensePlate), response -> {
                    if (response.isOk()) {
                        String body = response.body();
                        JSONObject jsonObject = JSONUtil.parseObj(body);
                        return jsonObject.getInt("status") == 0;
                    }
                    return false;
                });
    }

    /**
     * 删除车牌对应的电子围栏
     *
     * @param fenceId
     * @param licensePlate
     * @return
     */
    public Boolean deleteVehicleToElectronicFence(Long fenceId, String licensePlate) {
        String url = this.baiduConfig.getYingYanUrl() + "/fence/deletemonitoredperson";

//        构造提交参数
        Map<String, Object> param = MapUtil.builder(new HashMap<String, Object>())
                .put("fence_id", fenceId)
                .put("monitored_person", this.createEntityName(licensePlate))
                .build();

        return this.baiduApiService.execute(url, Method.POST, param, response -> {
            if (response.isOk()){
                String body = response.body();
                JSONObject jsonObject = JSONUtil.parseObj(body);
                return jsonObject.getInt("status") == 0;
            }
            return null;
        });
    }
}
