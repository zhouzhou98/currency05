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
