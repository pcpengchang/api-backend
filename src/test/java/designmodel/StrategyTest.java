package designmodel;


import java.util.HashMap;
import java.util.Map;

/**
 * 策略模式，声明策略接口，具体的策略类去实现接口，
 *
 * @author pengchang
 */
// 策略接口
interface Strategy  {
    void issue(Object ... params);
}

class StrategyContext {
    private static final Map<String, Strategy> registerMap = new HashMap<>();

    // 注册策略
    public static void registerStrategy(String rewardType, Strategy strategy) {
        registerMap.putIfAbsent(rewardType, strategy);
    }
    // 获取策略
    public static Strategy getStrategy(String rewardType) {
        return registerMap.get(rewardType);
    }
}

// 抽象策略类
abstract class AbstractStrategy implements Strategy {
    // 类注册方法
    public void register() {
        StrategyContext.registerStrategy(getClass().getSimpleName(), this);
    }
}

//
class HotelService {
    public void sendPrize(Object... params) {
        System.out.println("Hotel...");
    }
}

//
class FoodService {
    public void payCoupon(Object... params) {
        System.out.println("Food...");
    }
}

// 酒旅策略
class Hotel extends AbstractStrategy implements Strategy {
    private HotelService hotelService = new HotelService();

    private static final Hotel instance = new Hotel();

    private Hotel() {
        register();
    }
    public static Hotel getInstance() {
        return instance;
    }

    @Override
    public void issue(Object... params) {
        hotelService.sendPrize();
    }
}
// 美食策略
class Food extends AbstractStrategy implements Strategy {
    private FoodService foodService = new FoodService();

    private static final Food instance = new Food();

    private Food() {
        register();
    }
    public static Food getInstance() {
        return instance;
    }

    @Override
    public void issue(Object... params) {
        foodService.payCoupon();
    }
}

public class StrategyTest {
    public static void main(String[] args) {
        Strategy strategy = StrategyContext.getStrategy("Hotel");
        strategy.issue();
    }
}