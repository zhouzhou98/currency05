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
