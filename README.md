问题：开户为10000，其中张三隔一段时间会存入100，李四会隔一段时间进行取款100，王五会过一段时间取款200，存入金额不能超过20000，当取款为0时，停止，如何保证该阶段的数据一致性问题  
解决方案：将开户金额设置为atomic原子类，同时取款存款设置为synchronized即可解决该问题 

开户类Account
```java
package com.currency.currency05;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Account {
    private String name;
    private AtomicInteger amount;

    public void name() {
        System.out.println("账户为：" + name + "!");
    }

    public synchronized boolean deposit(String threadName, Integer change) {
        amount.addAndGet(change);
        if(amount.get() > 20000) {
            System.out.print("存款金额已经达到上限，存款失败");
            return false;
        }
        System.out.println("01：" + threadName+",存款金额为"+change + ",开始存款，存款后余额为" + amount);
        return true;
    }

    public synchronized  boolean withdraw(String threadName, Integer money) {
        if (amount.get() <= 0 || amount.get() < money) {
            System.out.println(threadName+",账户金额为"+amount.get()+"，你的取款金额为"+money+"，取款失败");
            return false;
        } else {
            amount.addAndGet(-money);
            System.out.println("02：" + threadName+",取款金额为"+money + ",开始取款，取款后余额为" + amount);
            return true;
        }
    }

    public synchronized void openAccount(String name, Integer money) {
        this.name = name;
        this.amount = new AtomicInteger(money);
        System.out.println("00：" + name + "开户成功,开户金额为"+money);
    }
}

```
取款类Deposit 
```java
package com.currency.currency05;

import lombok.Data;

import java.util.Random;

@Data
public class Deposit implements Runnable{
    private Account account;

    private int deposit;

    public void deposit(Account account, int deposit) {
        this.account = account;
        this.deposit = deposit;
    }


    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        while (true) {
            if (account.getAmount().get() < 20000) {
                boolean isFlag = account.deposit(threadName, deposit);
                if (!isFlag) {
                    break;
                }
                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (account.getAmount().get() >= 20000) {
                break;
            }
            if (account.getAmount().get() == 0) {
                System.out.println("余额为零，存款结束");
                break;
            }
        }
    }
}
```

存款类Withdraw 
```java
package com.currency.currency05;

import java.util.Random;

/**
 * @author huanglaoxie(微信:yfct-8888)
 * @className Withdraw
 * @description：
 * @date 2017/12/20 10:16
 */
public class Withdraw implements Runnable{

    private Account account;

    private int withdraw;

    public void withdraw(Account account, int withdraw) {
        this.account = account;
        this.withdraw = withdraw;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        while (true) {
            if (account.getAmount().get() > 0) {
                boolean isFlag = account.withdraw(threadName, withdraw);
                if (!isFlag) {
                    break;
                }

                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else {

                break;
            }
        }
    }
}
```
线程执行
```java
package com.currency.currency05;


public class Test {

    public static void main(String[] args) {
        Account account = new Account();
        account.openAccount("zhouzhou", 10000);

        Deposit p1 = new Deposit();
        p1.deposit(account, 100);
        Thread depositThread = new Thread(p1, "张三");

        Withdraw p2 = new Withdraw();
        p2.withdraw(account, 100);
        Thread withdrawThread1 = new Thread(p2, "李四");

        Withdraw p3 = new Withdraw();
        p3.withdraw(account, 200);
        Thread withdrawThread2 = new Thread(p3, "王五");
        withdrawThread1.start();
        withdrawThread2.start();
        depositThread.start();


    }

}
```
测试结果
```java
/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/bin/java -Dvisualvm.id=220271645666 -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=49342:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8 -classpath /Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/cldrdata.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/dnsns.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/jaccess.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/legacy8ujsse.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/localedata.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/nashorn.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/openjsse.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/sunec.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/sunjce_provider.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/sunpkcs11.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/zipfs.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/management-agent.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/lib/dt.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/lib/jconsole.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/lib/tools.jar:/Users/suyuzhou/Downloads/currency05/target/classes:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/springframework/boot/spring-boot-starter/2.5.5/spring-boot-starter-2.5.5.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/springframework/boot/spring-boot/2.5.5/spring-boot-2.5.5.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/springframework/spring-context/5.3.10/spring-context-5.3.10.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/springframework/spring-aop/5.3.10/spring-aop-5.3.10.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/springframework/spring-beans/5.3.10/spring-beans-5.3.10.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/springframework/spring-expression/5.3.10/spring-expression-5.3.10.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/springframework/boot/spring-boot-autoconfigure/2.5.5/spring-boot-autoconfigure-2.5.5.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/springframework/boot/spring-boot-starter-logging/2.5.5/spring-boot-starter-logging-2.5.5.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/ch/qos/logback/logback-classic/1.2.6/logback-classic-1.2.6.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/ch/qos/logback/logback-core/1.2.6/logback-core-1.2.6.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/apache/logging/log4j/log4j-to-slf4j/2.14.1/log4j-to-slf4j-2.14.1.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/apache/logging/log4j/log4j-api/2.14.1/log4j-api-2.14.1.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/slf4j/jul-to-slf4j/1.7.32/jul-to-slf4j-1.7.32.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/jakarta/annotation/jakarta.annotation-api/1.3.5/jakarta.annotation-api-1.3.5.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/springframework/spring-core/5.3.10/spring-core-5.3.10.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/springframework/spring-jcl/5.3.10/spring-jcl-5.3.10.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/yaml/snakeyaml/1.28/snakeyaml-1.28.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/projectlombok/lombok/1.18.20/lombok-1.18.20.jar:/Users/suyuzhou/coding/maven/apache-maven-3.8.1/localRepository/org/slf4j/slf4j-api/1.7.32/slf4j-api-1.7.32.jar com.currency.currency05.Test
00：zhouzhou开户成功,开户金额为10000
02：李四,取款金额为100,开始取款，取款后余额为9900
01：张三,存款金额为100,开始存款，存款后余额为10000
02：王五,取款金额为200,开始取款，取款后余额为9800
02：王五,取款金额为200,开始取款，取款后余额为9600
01：张三,存款金额为100,开始存款，存款后余额为9700
02：李四,取款金额为100,开始取款，取款后余额为9600
02：王五,取款金额为200,开始取款，取款后余额为9400
01：张三,存款金额为100,开始存款，存款后余额为9500
01：张三,存款金额为100,开始存款，存款后余额为9600
02：王五,取款金额为200,开始取款，取款后余额为9400
02：王五,取款金额为200,开始取款，取款后余额为9200
02：李四,取款金额为100,开始取款，取款后余额为9100
02：王五,取款金额为200,开始取款，取款后余额为8900
01：张三,存款金额为100,开始存款，存款后余额为9000
02：李四,取款金额为100,开始取款，取款后余额为8900
02：王五,取款金额为200,开始取款，取款后余额为8700
02：李四,取款金额为100,开始取款，取款后余额为8600
02：李四,取款金额为100,开始取款，取款后余额为8500
02：王五,取款金额为200,开始取款，取款后余额为8300
01：张三,存款金额为100,开始存款，存款后余额为8400
02：李四,取款金额为100,开始取款，取款后余额为8300
02：李四,取款金额为100,开始取款，取款后余额为8200
02：王五,取款金额为200,开始取款，取款后余额为8000
01：张三,存款金额为100,开始存款，存款后余额为8100
02：李四,取款金额为100,开始取款，取款后余额为8000
01：张三,存款金额为100,开始存款，存款后余额为8100
02：王五,取款金额为200,开始取款，取款后余额为7900
02：李四,取款金额为100,开始取款，取款后余额为7800
01：张三,存款金额为100,开始存款，存款后余额为7900
02：王五,取款金额为200,开始取款，取款后余额为7700
01：张三,存款金额为100,开始存款，存款后余额为7800
02：李四,取款金额为100,开始取款，取款后余额为7700
01：张三,存款金额为100,开始存款，存款后余额为7800
02：李四,取款金额为100,开始取款，取款后余额为7700
02：李四,取款金额为100,开始取款，取款后余额为7600
01：张三,存款金额为100,开始存款，存款后余额为7700
02：王五,取款金额为200,开始取款，取款后余额为7500
02：李四,取款金额为100,开始取款，取款后余额为7400
02：李四,取款金额为100,开始取款，取款后余额为7300
01：张三,存款金额为100,开始存款，存款后余额为7400
01：张三,存款金额为100,开始存款，存款后余额为7500
02：李四,取款金额为100,开始取款，取款后余额为7400
02：王五,取款金额为200,开始取款，取款后余额为7200
02：王五,取款金额为200,开始取款，取款后余额为7000
02：王五,取款金额为200,开始取款，取款后余额为6800
01：张三,存款金额为100,开始存款，存款后余额为6900
02：李四,取款金额为100,开始取款，取款后余额为6800
02：王五,取款金额为200,开始取款，取款后余额为6600
01：张三,存款金额为100,开始存款，存款后余额为6700
02：王五,取款金额为200,开始取款，取款后余额为6500
02：李四,取款金额为100,开始取款，取款后余额为6400
01：张三,存款金额为100,开始存款，存款后余额为6500
01：张三,存款金额为100,开始存款，存款后余额为6600
02：李四,取款金额为100,开始取款，取款后余额为6500
01：张三,存款金额为100,开始存款，存款后余额为6600
02：王五,取款金额为200,开始取款，取款后余额为6400
02：李四,取款金额为100,开始取款，取款后余额为6300
01：张三,存款金额为100,开始存款，存款后余额为6400
01：张三,存款金额为100,开始存款，存款后余额为6500
02：李四,取款金额为100,开始取款，取款后余额为6400
02：王五,取款金额为200,开始取款，取款后余额为6200
02：李四,取款金额为100,开始取款，取款后余额为6100
02：李四,取款金额为100,开始取款，取款后余额为6000
01：张三,存款金额为100,开始存款，存款后余额为6100
02：王五,取款金额为200,开始取款，取款后余额为5900
02：王五,取款金额为200,开始取款，取款后余额为5700
02：李四,取款金额为100,开始取款，取款后余额为5600
01：张三,存款金额为100,开始存款，存款后余额为5700
02：李四,取款金额为100,开始取款，取款后余额为5600
02：王五,取款金额为200,开始取款，取款后余额为5400
01：张三,存款金额为100,开始存款，存款后余额为5500
01：张三,存款金额为100,开始存款，存款后余额为5600
02：王五,取款金额为200,开始取款，取款后余额为5400
02：王五,取款金额为200,开始取款，取款后余额为5200
02：李四,取款金额为100,开始取款，取款后余额为5100
02：李四,取款金额为100,开始取款，取款后余额为5000
01：张三,存款金额为100,开始存款，存款后余额为5100
02：王五,取款金额为200,开始取款，取款后余额为4900
02：李四,取款金额为100,开始取款，取款后余额为4800
02：王五,取款金额为200,开始取款，取款后余额为4600
01：张三,存款金额为100,开始存款，存款后余额为4700
02：李四,取款金额为100,开始取款，取款后余额为4600
02：王五,取款金额为200,开始取款，取款后余额为4400
02：李四,取款金额为100,开始取款，取款后余额为4300
01：张三,存款金额为100,开始存款，存款后余额为4400
01：张三,存款金额为100,开始存款，存款后余额为4500
02：王五,取款金额为200,开始取款，取款后余额为4300
02：李四,取款金额为100,开始取款，取款后余额为4200
02：王五,取款金额为200,开始取款，取款后余额为4000
02：李四,取款金额为100,开始取款，取款后余额为3900
02：李四,取款金额为100,开始取款，取款后余额为3800
01：张三,存款金额为100,开始存款，存款后余额为3900
01：张三,存款金额为100,开始存款，存款后余额为4000
02：王五,取款金额为200,开始取款，取款后余额为3800
01：张三,存款金额为100,开始存款，存款后余额为3900
02：李四,取款金额为100,开始取款，取款后余额为3800
02：王五,取款金额为200,开始取款，取款后余额为3600
02：李四,取款金额为100,开始取款，取款后余额为3500
02：王五,取款金额为200,开始取款，取款后余额为3300
02：王五,取款金额为200,开始取款，取款后余额为3100
02：李四,取款金额为100,开始取款，取款后余额为3000
01：张三,存款金额为100,开始存款，存款后余额为3100
02：李四,取款金额为100,开始取款，取款后余额为3000
02：王五,取款金额为200,开始取款，取款后余额为2800
01：张三,存款金额为100,开始存款，存款后余额为2900
01：张三,存款金额为100,开始存款，存款后余额为3000
01：张三,存款金额为100,开始存款，存款后余额为3100
02：王五,取款金额为200,开始取款，取款后余额为2900
02：李四,取款金额为100,开始取款，取款后余额为2800
02：王五,取款金额为200,开始取款，取款后余额为2600
01：张三,存款金额为100,开始存款，存款后余额为2700
01：张三,存款金额为100,开始存款，存款后余额为2800
01：张三,存款金额为100,开始存款，存款后余额为2900
02：李四,取款金额为100,开始取款，取款后余额为2800
01：张三,存款金额为100,开始存款，存款后余额为2900
02：王五,取款金额为200,开始取款，取款后余额为2700
01：张三,存款金额为100,开始存款，存款后余额为2800
02：李四,取款金额为100,开始取款，取款后余额为2700
02：王五,取款金额为200,开始取款，取款后余额为2500
01：张三,存款金额为100,开始存款，存款后余额为2600
02：王五,取款金额为200,开始取款，取款后余额为2400
02：王五,取款金额为200,开始取款，取款后余额为2200
02：李四,取款金额为100,开始取款，取款后余额为2100
02：李四,取款金额为100,开始取款，取款后余额为2000
02：王五,取款金额为200,开始取款，取款后余额为1800
01：张三,存款金额为100,开始存款，存款后余额为1900
02：李四,取款金额为100,开始取款，取款后余额为1800
02：王五,取款金额为200,开始取款，取款后余额为1600
01：张三,存款金额为100,开始存款，存款后余额为1700
01：张三,存款金额为100,开始存款，存款后余额为1800
02：王五,取款金额为200,开始取款，取款后余额为1600
02：李四,取款金额为100,开始取款，取款后余额为1500
01：张三,存款金额为100,开始存款，存款后余额为1600
02：王五,取款金额为200,开始取款，取款后余额为1400
01：张三,存款金额为100,开始存款，存款后余额为1500
02：王五,取款金额为200,开始取款，取款后余额为1300
02：王五,取款金额为200,开始取款，取款后余额为1100
01：张三,存款金额为100,开始存款，存款后余额为1200
02：李四,取款金额为100,开始取款，取款后余额为1100
02：王五,取款金额为200,开始取款，取款后余额为900
02：王五,取款金额为200,开始取款，取款后余额为700
01：张三,存款金额为100,开始存款，存款后余额为800
02：李四,取款金额为100,开始取款，取款后余额为700
02：王五,取款金额为200,开始取款，取款后余额为500
01：张三,存款金额为100,开始存款，存款后余额为600
02：李四,取款金额为100,开始取款，取款后余额为500
02：王五,取款金额为200,开始取款，取款后余额为300
02：李四,取款金额为100,开始取款，取款后余额为200
01：张三,存款金额为100,开始存款，存款后余额为300
02：李四,取款金额为100,开始取款，取款后余额为200
02：王五,取款金额为200,开始取款，取款后余额为0
余额为零，存款结束

Process finished with exit code 0

```