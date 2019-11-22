package com.example.rule.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * 分布式自增ID雪花算法snowflake
 **/
@Slf4j
@Component
public class SnowFlake {

    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1480166465631L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12; //序列号占用的位数
    private final static long MACHINE_BIT = 5;   //机器标识占用的位数
    private final static long DATACENTER_BIT = 5;//数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private static long datacenterId;//数据中心
    private Long machineId;      //机器标识
    private long sequence = 0L; //序列号
    private long lastStmp = -1L;//上一次时间戳

    //单例
    private static SnowFlake snowFlake = new SnowFlake(1, 1);

    @Value("${server.id}")
    public void setDatacenterId(Long datacenterId) {
        //将数据中心id截取成短id，去末尾5个bit
        SnowFlake.datacenterId = -1L >>> 59 & datacenterId;
        //生成单例
        snowFlake = new SnowFlake(datacenterId, getMACAddress());
    }

    //public static void main(String[] args) {
//        AtomicInteger integer = new AtomicInteger(0);
//        ExecutorService threadPool1 = null;
//        ExecutorService threadPool2 = null;
//        try{
//            BlockingQueue<Runnable> rQueue = new LinkedBlockingQueue<>();
//            List<Callable<Integer>> cList = new ArrayList<>();
//            Runnable callable = () -> {
//                getSnowFlakeId();
//                System.out.println(integer.addAndGet(1));
//            };
//            Callable<Integer> callable = () -> {
//                getSnowFlakeId();
//                return integer.addAndGet(1);
//            };
//
//            for (int i = 0; i < 100000; i++) {
//                rQueue.add(callable);
//                cList.add(callable);
//            }
//            threadPool1 = Executors.newCachedThreadPool();
//            List<Future<Integer>> futures =threadPool1.invokeAll(cList);
//            threadPool2 = new ThreadPoolExecutor(10, 500, 10, TimeUnit.SECONDS, rQueue);
//            System.out.println(((ThreadPoolExecutor) threadPool2).getTaskCount());
////            threadPool2.submit(callable);//这行注释掉会有奇妙的效果
//        }catch (Exception e){
//            System.out.println("报错时共生成了： "+integer);
//        }finally {
//            System.out.println("共生成了： "+integer);
//            threadPool1.shutdown();
//            threadPool2.shutdown();
//        }
//
//
//    }

    private SnowFlake() {
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static SnowFlake getInstance() {
        return snowFlake;
    }

    /**
     * 获取雪花id
     *
     * @return
     */
    public static long getSnowFlakeId() {
        return snowFlake.nextId();
    }

    private SnowFlake(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.machineId = machineId;
        log.info("生成雪花id工厂实例，datacenterId = " + datacenterId + " ,machineId = " + machineId);
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;

        return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
                | datacenterId << DATACENTER_LEFT       //数据中心部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | sequence;                             //序列号部分
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取本机mac地址
     *
     * @return
     */
    private static long getMACAddress() {
        byte[] mac = null;
        InetAddress ia = null;
        try {
            ia = InetAddress.getLocalHost();
            // 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
            mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        // 下面代码是把mac地址拼装成String
        StringBuffer sb = new StringBuffer();
        if(mac == null){
            sb.append("52-54-00-f1-c4-6f");
        }else{
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                // mac[i] & 0xFF 是为了把byte转化为正整数
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
        }
        // 把字符串所有小写字母改为大写成为正规的mac地址并返回
        Long machineId = new BigInteger(sb.toString().toUpperCase().replaceAll("-", ""), 16).longValue();
        Long shortMacId = bitOperation(machineId);
        log.info("本机mac地址： " + sb.toString() + " ,10进制macid: " + machineId + " ,短macId： " + shortMacId);
        return shortMacId;
    }

    /**
     * 截取mac地址最后5bit
     *
     * @param machineId
     * @return
     */
    private static long bitOperation(Long machineId) {
        //这种方法比较笨
//        return machineId<<59>>>59;
        //这种更加优雅
        return -1L >>> 59 & machineId;
    }

}