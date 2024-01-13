# Build Project Environment

## Project front-end: wechat mini program
- project-jywl-admin-vue-ts

## Project back end: docker container
- docker installs mongoDB

~~~shell
#Pull image
docker pull mongo:4.0.3

#Create a container
docker create --name mongodb-server -p 27018:27017 -v mongodb-server-data:/data/db mongo:4.0.3 --auth

#Starter container
docker start mongodb-server

#Enter the container
docker exec -it mongodb-server /bin/bash

#Access the admin database
mongo
use admin

#Add an administrator who has permission to manage users and roles
db.createUser({ user: 'root', pwd: 'root', roles: [ { role: "root", db: "admin" } ] })
#Authentication is performed after the exit

#perform the authentication
mongo -u "root" -p "root" --authenticationDatabase "admin"

#Add a common user as admin
use admin
db.createUser({ user: 'jinyun', pwd: 'oudqBFGmGY8pU6WS', roles: [ { role: "readWrite", db: "jinyun" } ] });

#Test with tanhua user login
mongo -u "jinyun" -p "oudqBFGmGY8pU6WS" --authenticationDatabase "admin"

#You can log in to the console to perform operations
~~~

## 数据库插入订单数据

~~~java
@Test
    public void testProvince(){
        //Insert province data
        this.mongoTemplate.save(Province.builder().name("北京").lng(116.404188).lat(39.913581).build());
        this.mongoTemplate.save(Province.builder().name("上海").lng(121.464442).lat(31.223577).build());
        this.mongoTemplate.save(Province.builder().name("江苏").lng(118.797248).lat(32.069058).build());
        this.mongoTemplate.save(Province.builder().name("湖北").lng(114.294976).lat(30.599855).build());
    }

    @Test
    public void testCity(){
        //Insert city data
        this.mongoTemplate.save(City.builder().name("北京").lng(116.404188).lat(39.913581).build());
        this.mongoTemplate.save(City.builder().name("上海").lng(121.464442).lat(31.223577).build());
        this.mongoTemplate.save(City.builder().name("南京").lng(118.797248).lat(32.069058).build());
        this.mongoTemplate.save(City.builder().name("武汉").lng(114.294976).lat(30.599855).build());
    }

    @Test
    public void testCounty(){
        //Insert area data
        this.mongoTemplate.save(County.builder().name("昌平区").lng(116.232442).lat(40.225972).build());
        this.mongoTemplate.save(County.builder().name("海淀区").lng(116.301989).lat(39.963718).build());
        this.mongoTemplate.save(County.builder().name("西城区").lng(116.364079).lat(39.91902).build());
        this.mongoTemplate.save(County.builder().name("徐汇区").lng(121.442245).lat(31.194804).build());
        this.mongoTemplate.save(County.builder().name("浦东新区").lng(121.663713).lat(31.140847).build());
        this.mongoTemplate.save(County.builder().name("秦淮区").lng(118.804147).lat(32.042618).build());
        this.mongoTemplate.save(County.builder().name("江宁区").lng(118.849565).lat(31.954916).build());
        this.mongoTemplate.save(County.builder().name("长江新城区").lng(114.388491).lat(30.688678).build());
        this.mongoTemplate.save(County.builder().name("汉阳区").lng(114.228223).lat(30.559665).build());
    }

    @Test
    public void testCreateOrder(){
        //Insert test data for the order
        Order order = new Order();
        order.setOrderTime(DateUtil.formatDateTime(new Date()));
        order.setOrderNumber("O202108161001");
        order.setAmount("20");
        order.setDeliverytype("2");
        order.setEstimatedArrivalTime("2021-08-20");
        order.setPaymentStatus("2");
        order.setPayType("1");
        order.setReceiverAdress("航头镇航都路18号");
        order.setReceiverProvince(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("上海")), Province.class));
        order.setReceiverCity(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("上海")), City.class));
        order.setReceiverCounty(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("浦东新区")), County.class));
        order.setReceiverName("张三");
        order.setReceiverPhone("13888888888");
        order.setSenderAddress("建材城西路金燕龙办公楼");
        order.setSenderName("李四");
        order.setSenderPhone("13999999999");
        order.setSenderProvince(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("北京")), Province.class));
        order.setSenderCity(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("北京")), City.class));
        order.setSenderCounty(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("昌平区")), County.class));
        order.setWaybillNumber("Y202108161001");
        order.setStatus("1");
        this.mongoTemplate.save(order);

        order = new Order();
        order.setOrderTime(DateUtil.formatDateTime(new Date()));
        order.setOrderNumber("O202108161002");
        order.setAmount("30");
        order.setDeliverytype("2");
        order.setEstimatedArrivalTime("2021-08-20");
        order.setPaymentStatus("2");
        order.setPayType("1");
        order.setReceiverAdress("青龙路传智教育科创园");
        order.setReceiverProvince(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("湖北")), Province.class));
        order.setReceiverCity(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("武汉")), City.class));
        order.setReceiverCounty(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("长江新城区")), County.class));
        order.setReceiverName("王五");
        order.setReceiverPhone("13666666666");
        order.setSenderAddress("建材城西路金燕龙办公楼");
        order.setSenderName("李四");
        order.setSenderPhone("13999999999");
        order.setSenderProvince(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("北京")), Province.class));
        order.setSenderCity(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("北京")), City.class));
        order.setSenderCounty(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("昌平区")), County.class));
        order.setWaybillNumber("Y202108161002");
        order.setStatus("1");
        this.mongoTemplate.save(order);

        order = new Order();
        order.setOrderTime(DateUtil.formatDateTime(new Date()));
        order.setOrderNumber("O202108161003");
        order.setAmount("20");
        order.setDeliverytype("2");
        order.setEstimatedArrivalTime("2021-08-20");
        order.setPaymentStatus("2");
        order.setPayType("1");
        order.setReceiverAdress("青龙路传智教育科创园");
        order.setReceiverProvince(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("湖北")), Province.class));
        order.setReceiverCity(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("武汉")), City.class));
        order.setReceiverCounty(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("长江新城区")), County.class));
        order.setReceiverName("王五");
        order.setReceiverPhone("13666666666");
        order.setSenderAddress("航头镇航都路18号");
        order.setSenderName("张三");
        order.setSenderPhone("1311111111");
        order.setSenderProvince(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("上海")), Province.class));
        order.setSenderCity(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("上海")), City.class));
        order.setSenderCounty(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("浦东新区")), County.class));
        order.setWaybillNumber("Y202108161003");
        order.setStatus("1");
        this.mongoTemplate.save(order);
    }
~~~

## Database inserts waybill data
~~~java
@Test
    public void testCreateOrder() {
        //Insert test data for the waybill
        WayBill wayBill = new WayBill();
        wayBill.setOrderTime(DateUtil.formatDateTime(new Date()));
        wayBill.setOrderNumber("O202108161001");
        wayBill.setDeliverytype("2");
        wayBill.setEstimatedArrivalTime("2021-08-20");
        wayBill.setPaymentStatus("2");
        wayBill.setPayType("1");
        wayBill.setReceiverAdress("航头镇航都路18号");
        wayBill.setReceiverProvince(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("上海")), Province.class));
        wayBill.setReceiverCity(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("上海")), City.class));
        wayBill.setReceiverCounty(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("浦东新区")), County.class));
        wayBill.setReceiverName("张三");
        wayBill.setReceiverPhone("13888888888");
        wayBill.setSenderAddress("建材城西路金燕龙办公楼");
        wayBill.setSenderName("李四");
        wayBill.setSenderPhone("13999999999");
        wayBill.setSenderProvince(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("北京")), Province.class));
        wayBill.setSenderCity(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("北京")), City.class));
        wayBill.setSenderCounty(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("昌平区")), County.class));
        wayBill.setWaybillNumber("Y202108161001");
        wayBill.setStatus("1");
        this.mongoTemplate.save(wayBill);

        wayBill = new WayBill();
        wayBill.setOrderTime(DateUtil.formatDateTime(new Date()));
        wayBill.setOrderNumber("O202108161002");
        wayBill.setDeliverytype("2");
        wayBill.setEstimatedArrivalTime("2021-08-20");
        wayBill.setPaymentStatus("2");
        wayBill.setPayType("1");
        wayBill.setReceiverAdress("青龙路传智教育科创园");
        wayBill.setReceiverProvince(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("湖北")), Province.class));
        wayBill.setReceiverCity(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("武汉")), City.class));
        wayBill.setReceiverCounty(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("长江新城区")), County.class));
        wayBill.setReceiverName("王五");
        wayBill.setReceiverPhone("13666666666");
        wayBill.setSenderAddress("建材城西路金燕龙办公楼");
        wayBill.setSenderName("李四");
        wayBill.setSenderPhone("13999999999");
        wayBill.setSenderProvince(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("北京")), Province.class));
        wayBill.setSenderCity(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("北京")), City.class));
        wayBill.setSenderCounty(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("昌平区")), County.class));
        wayBill.setWaybillNumber("Y202108161002");
        wayBill.setStatus("1");
        this.mongoTemplate.save(wayBill);

        wayBill = new WayBill();
        wayBill.setOrderTime(DateUtil.formatDateTime(new Date()));
        wayBill.setOrderNumber("O202108161003");
        wayBill.setDeliverytype("2");
        wayBill.setEstimatedArrivalTime("2021-08-20");
        wayBill.setPaymentStatus("2");
        wayBill.setPayType("1");
        wayBill.setReceiverAdress("青龙路传智教育科创园");
        wayBill.setReceiverProvince(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("湖北")), Province.class));
        wayBill.setReceiverCity(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("武汉")), City.class));
        wayBill.setReceiverCounty(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("长江新城区")), County.class));
        wayBill.setReceiverName("王五");
        wayBill.setReceiverPhone("13666666666");
        wayBill.setSenderAddress("航头镇航都路18号");
        wayBill.setSenderName("张三");
        wayBill.setSenderPhone("1311111111");
        wayBill.setSenderProvince(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("上海")), Province.class));
        wayBill.setSenderCity(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("上海")), City.class));
        wayBill.setSenderCounty(this.mongoTemplate.findOne(Query.query(Criteria.where("name").is("浦东新区")), County.class));
        wayBill.setWaybillNumber("Y202108161003");
        wayBill.setStatus("1");
        this.mongoTemplate.save(wayBill);
    }
~~~