ID : 5team@gkn2019hotmail.onmicrosoft.com  
PW : 6400

![image](https://user-images.githubusercontent.com/28293389/81536861-34084480-93a7-11ea-866f-c09e24ef9412.png)

# 5조 - 카페포미

- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW


# Table of contents

- [5조 - 카페포미](#---)
  - [조직](#조직)
  - [서비스 시나리오](#서비스-시나리오)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
  - [운영](#운영)
    - [CI/CD 설정](#cicd설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
  - [신규 개발 조직의 추가](#신규-개발-조직의-추가)

# 조직
- 고객 : 고객 주문 오류를 최소화 한다. ( Core )
- 매장 : 접수 된 주문에 대해 오류를 최소화 한다. ( Supporting )
- 고객관리 : 주문 상태에 대해 고객에게 정확한 알람을 제공한다. ( Supporting )

# 서비스 시나리오

- 예시) 스타벅스 Siren Order

기능적 요구사항
1. 고객이 온라인으로 주문 내역을 만든다.
1. 고객이 주문 내역으로 결제한다.
1. 주문 내역이 결제 되면 해당 내역을 매장에서 접수하거나 거절한다.
    1. 매장에서 접수하면 커피를 제작하고 고객이 주문을 취소할 수 없다.
    1. 매장에서 거절하면 주문 내역이 취소되고 결제가 환불 된다.
1. 고객이 주문 내역을 취소를 요청할 수 있다.
1. 매장에서 주문 내역 취소 요청을 받으면 취소가 가능하다면 취소한다.
1. 주문 내역이 취소되면 결제가 환불 된다.
1. 주문 진행 상태가 바뀔 때 마다 SMS로 알림을 보낸다.

비기능적 요구사항
1. 트랜잭션
    1. 주문시 결제가 성립되어야 한다.  Sync 호출 
1. 장애격리
    1. 매장 서비스가 Down 되어도 주문/취소는 가능해야 한다.  Async (event-driven), Eventual Consistency
1. 성능
    1. 고객이 주문 진행 상태를 수시로 조회할 수 있어야 한다.  CQRS
    1. 고객은 주문 진행 상태를 SMS로 확인할 수 있어야 한다.  Event driven




# 분석/설계


## AS-IS 조직 (Horizontally-Aligned)
  ![image](https://user-images.githubusercontent.com/487999/79684144-2a893200-826a-11ea-9a01-79927d3a0107.png)

## TO-BE 조직 (Vertically-Aligned)
  ![image](https://user-images.githubusercontent.com/487999/79684159-3543c700-826a-11ea-8d5f-a3fc0c4cad87.png)


## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과: 

### 완성된 1차 모형

![image](https://user-images.githubusercontent.com/63624054/81624992-9ce8ce80-9432-11ea-94ee-47a019a74bcd.png)

### 완성된 2차 모형
![image](https://user-images.githubusercontent.com/63624054/81637640-cd8c3080-9451-11ea-8446-46ec36b17108.png)


    - Order cancel 프로세스 추가 : Event, Policy 추가
  
  
  

### 2차 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증

기능적 요구사항(검증)
![image](https://user-images.githubusercontent.com/63624054/81644736-48117c00-9463-11ea-8ccc-6d3e4849bea9.png)

1. 고객이 온라인으로 주문 내역을 만든다.
1. 고객이 주문 내역으로 결제한다.
1. 주문 내역이 결제 되면 해당 내역을 매장에서 접수하거나 거절한다.
    1. 매장에서 접수하면 커피를 제작하고 고객이 주문을 취소할 수 없다.
    1. 매장에서 거절하면 주문 내역이 취소되고 결제가 환불 된다.
1. 고객이 주문 내역을 취소를 요청할 수 있다.
1. 매장에서 주문 내역 취소 요청을 받으면 취소가 가능하다면 취소한다.
1. 주문 내역이 취소되면 결제가 환불 된다.
1. 매장에서 커피가 제작 되면 고객이 주문 건을 픽업한다.
1. 주문 진행 상태가 바뀔 때 마다 SMS로 알림을 보낸다.




 비기능적 요구사항(검증)
 ![image](https://user-images.githubusercontent.com/63624054/81642399-44c7c180-945e-11ea-8348-23990ab8d40d.png)

1. 트랜잭션
    1. 주문시 결제가 성립되어야 한다.  Sync 호출 
1. 장애격리
    1. 매장 서비스가 Down 되어도 주문/취소는 가능해야 한다.  Async (event-driven), Eventual Consistency
1. 성능
    1. 고객이 주문 진행 상태를 수시로 조회할 수 있어야 한다.  CQRS
    1. 고객은 주문 진행 상태를 SMS로 확인할 수 있어야 한다.  Event driven




## 헥사고날 아키텍처 다이어그램 도출
    
![image](https://user-images.githubusercontent.com/28293389/81541063-877d9100-93ad-11ea-931a-066f21747fb5.png)


    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현:

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트와 파이선으로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
cd customer
mvn spring-boot:run

cd delivery
mvn spring-boot:run 

cd gateway
mvn spring-boot:run  
```

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: (예시는 pay 마이크로 서비스). 이때 가능한 현업에서 사용하는 언어 (유비쿼터스 랭귀지)를 그대로 사용하려고 노력했다.

```
package local;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Delivery_table")
public class Delivery {

    @Id
    private Long orderId;
    private String product;
    private Integer qty;
    private String status;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}


```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다
```
package local;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface DeliveryRepository extends PagingAndSortingRepository<Delivery, Long>{
}
```
- 적용 후 REST API 의 테스트
```
# Customer 서비스의 주문 처리
http POST customer:8080/orders product="IceAmericano" qty=1 price=2000

# Delivery 서비스의 승인/거절 처리
# 주문 승인
http PUT delivery:8080/deliveries/1 status="receive" product="IceAmericano" qty=1 price=2000
# 주문 거절
http PUT delivery:8080/deliveries/1 status="reject" product="IceAmericano" qty=1 price=2000

# 주문 상태 확인 및 View
http customer:8080/orders/1
http customer:8080/orderStatuses/1
```

## 동기식 호출 과 Fallback 처리

주문이 들어오고 결제(외부 API 호출) 처리가 동기식으로 진행되어야 하는 것을 반영하였으며 실제 결제 API를 구현/연동하기에는 무리가 있어 아래와 같이 대체하였다.

- 주문을 받은 직후(@PostPersist) 결제를 요청하도록 처리
```
# (Customer) Order.java

@PostPersist
public void eventPublish() throws JsonProcessingException {
    OrderSelected orderSelected = new OrderSelected();
    BeanUtils.copyProperties(this, orderSelected);
    orderSelected.publish();

    // 결제 시작
    if (Math.random() > 0.5){
        // 결제 성공
        PaymentCompleted paymentCompleted = new PaymentCompleted();
        BeanUtils.copyProperties(this, paymentCompleted);
        paymentCompleted.publish();

    } else {
        // 결제 실패
        orderSelected.setPaymentFail(true);
        orderSelected.publish();
    }
}
```

## 장애 격리
```
# Math.random() > 0.5 이면 결제 서비스가 정상 Response로 가정

# 주문 후 결제가 자동으로 진행
http POST customer:8080/orders product="IceAmericano" qty=1 price=2000    # Success
http POST customer:8080/orders product="IceAmericano2" qty=1 price=2000   # Fail

# 결제 결과 확인
http customer:8080/orderStatuses/1   # Status="pay_success"
http customer:8080/orderStatuses/2   # Status="pay_fail"
```


## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트


결제가 이루어진 후에 상점시스템으로 이를 알려주는 행위는 동기식이 아니라 비 동기식으로 처리하여 상점 시스템의 처리를 위하여 결제주문이 블로킹 되지 않아도록 처리한다.
 
- 이를 위하여 결제 승인이 되었다는 도메인 이벤트를 카프카로 송출한다 (Publish)
 
```
PaymentCompleted paymentCompleted = new PaymentCompleted();
BeanUtils.copyProperties(this, paymentCompleted);
paymentCompleted.publish();
```
- 상점 서비스에서는 결제승인 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:

```
@StreamListener(KafkaProcessor.INPUT)
public void wheneverPaymentCompleted_OrderFromOrder(@Payload PaymentCompleted paymentCompleted){

    if(paymentCompleted.isMe()){

        Delivery delivery = new Delivery();
        delivery.setOrderId(paymentCompleted.getOrderId());
        delivery.setProduct(paymentCompleted.getProduct());
        delivery.setQty(paymentCompleted.getQty());
        delivery.setStatus("order_get");

        deliveryRepository.save(delivery);
    }
}
```
실제 구현 시, 점주는 Delivery UI를 통해 주문에 대한 수락/거절을 한다. (Delivery에 대해 PUT)
  
```
@PostUpdate
public void onPostUpdate(){
    if ("receive".equals(this.getStatus())) {
        OrderReceived orderReceived = new OrderReceived();
        BeanUtils.copyProperties(this, orderReceived);
        orderReceived.publishAfterCommit();
    }

    if ("reject".equals(this.getStatus())) {
        OrderRejected orderRejected = new OrderRejected();
        BeanUtils.copyProperties(this, orderRejected);
        orderRejected.publishAfterCommit();
    }
}
```

상점 시스템은 주문/결제와 완전히 분리되어있으며, 이벤트 수신에 따라 처리되기 때문에, 상점시스템이 유지보수로 인해 잠시 내려간 상태라도 주문을 받는데 문제가 없다:
```
# 상점 서비스 (store) 를 잠시 내려놓음 (ctrl+c)

#주문처리
http POST customer:8080/orders product="IceAmericano" qty=1 price=2000
http POST customer:8080/orders product="IceAmericano2" qty=1 price=2000

#주문상태 확인
http customer:8080/orders     # 주문 상태 : pay_success

#상점 서비스 기동
cd 상점
mvn spring-boot:run

#상점 주인의 수락/거절 진행
http PUT delivery:8080/deliveries/1 status="receive"
http PUT delivery:8080/deliveries/2 status="reject"

#주문상태 확인
http customer:8080/orders     # 상점 주인의 선택에 따라 변동
```


# 운영

## CI/CD 설정

각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD 플랫폼은 Azure(Dev)를 사용하였으며, pipeline build script 는 각 프로젝트 폴더 이하에 azure-pipelines.yml 에 포함되었다.


### 오토스케일 아웃

- Customer/Delivery 서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다.  
  
    Customer : CPU 사용량이 50프로를 넘어서면 replica를 10개까지 늘려준다(최소 3)  
    Delivery : CPU 사용량이 50프로를 넘어서면 replica를 5개까지 늘려준다(최소 1)
```
kubectl autoscale deploy customer --min=3 --max=10 --cpu-percent=50
kubectl autoscale deploy delivery --min=1 --max=5 --cpu-percent=50
```
- 부하 테스트는 Skip


## 무정지 재배포

* readiness 설정
```
(azure-pipelines.yaml)

readinessProbe:
httpGet:
    path: /actuator/health
    port: 8080
initialDelaySeconds: 10
timeoutSeconds: 2
periodSeconds: 5
failureThreshold: 10
```

- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
siege -c100 -t120S -r10 --content-type "application/json" 'http://customer:8080/orders POST {"product": "iceAmericano", "qty":1, "price":1000}'


```

- Pipeline을 통한 새버전으로의 배포 시작 - UI
![image](https://user-images.githubusercontent.com/28293389/81762438-94ae9300-9507-11ea-8e94-8a761529ecac.png)

```
NAME                            READY   STATUS        RESTARTS   AGE
pod/customer-5d479469b9-5wlxp   0/1     Terminating   0          55m
pod/customer-85f968c94c-b254s   1/1     Running       0          87s
pod/customer-85f968c94c-k5pq4   1/1     Running       0          27s
pod/customer-85f968c94c-pgwd7   1/1     Running       0          55s
pod/delivery-78f4bb9f9b-7h42s   1/1     Running       0          54m
pod/gateway-675c9b584d-tr7cv    1/1     Running       0          17h
pod/httpie                      1/1     Running       1          18h
```


- seige 의 화면으로 넘어가서 Availability 확인
```
defaulting to time-based testing: 120 seconds
** SIEGE 3.0.8
** Preparing 100 concurrent users for battle.
The server is now under siege...
Lifting the server siege...      done.

Transactions:                  12976 hits
Availability:                 100.00 %
Elapsed time:                 119.67 secs
Data transferred:               3.45 MB
Response time:                  0.42 secs
Transaction rate:             108.43 trans/sec
Throughput:                     0.03 MB/sec
Concurrency:                   45.44
Successful transactions:       12976
Failed transactions:               0
Longest transaction:            2.29
Shortest transaction:           0.00
```

배포기간 동안 Availability 가 변화없기 때문에 무정지 재배포가 성공한 것으로 확인됨.


## 이벤트 스토밍 
    ![image](https://user-images.githubusercontent.com/487999/79685356-2b729180-8273-11ea-9361-a434065f2249.png)


## 헥사고날 아키텍처 변화 

![image](https://user-images.githubusercontent.com/487999/79685243-1d704100-8272-11ea-8ef6-f4869c509996.png)

## 구현  

기존의 마이크로 서비스에 수정을 발생시키지 않도록 Inbund 요청을 REST 가 아닌 Event 를 Subscribe 하는 방식으로 구현. 기존 마이크로 서비스에 대하여 아키텍처나 기존 마이크로 서비스들의 데이터베이스 구조와 관계없이 추가됨. 

## 운영과 Retirement

Request/Response 방식으로 구현하지 않았기 때문에 서비스가 더이상 불필요해져도 Deployment 에서 제거되면 기존 마이크로 서비스에 어떤 영향도 주지 않음.

* [비교] 결제 (pay) 마이크로서비스의 경우 API 변화나 Retire 시에 app(주문) 마이크로 서비스의 변경을 초래함:

예) API 변화시
```
# Order.java (Entity)

    @PostPersist
    public void onPostPersist(){

        fooddelivery.external.결제이력 pay = new fooddelivery.external.결제이력();
        pay.setOrderId(getOrderId());
        
        Application.applicationContext.getBean(fooddelivery.external.결제이력Service.class)
                .결제(pay);

                --> 

        Application.applicationContext.getBean(fooddelivery.external.결제이력Service.class)
                .결제2(pay);

    }
```

예) Retire 시
```
# Order.java (Entity)

    @PostPersist
    public void onPostPersist(){

        /**
        fooddelivery.external.결제이력 pay = new fooddelivery.external.결제이력();
        pay.setOrderId(getOrderId());
        
        Application.applicationContext.getBean(fooddelivery.external.결제이력Service.class)
                .결제(pay);

        **/
    }
```
