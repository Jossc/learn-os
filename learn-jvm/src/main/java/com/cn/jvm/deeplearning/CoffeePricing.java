package com.cn.jvm.deeplearning;

/**
 * {@code @Description: CoffeePricing}
 * {@code @Author: 一方通行 }
 * {@code @Date: 2025-03-24}
 * {@code @Version:v1.0}
 */
public class CoffeePricing {
    public static void main(String[] args) {
        double price = 20; // 当前定价（参数θ）
        double cost = 8;   // 咖啡成本（另一个参数）
        double loss;       // 经营损失（损失函数）

        for (int day = 0; day < 30; day++) {
            // 1. 计算当日经营情况
            loss = computeLoss(price, cost); // 计算损失函数

            // 2. 分析问题在哪（计算梯度）
            double[] gradients = computeGradients(price, cost);

            // 3. 调整定价策略（参数更新）
            price -= 0.1 * gradients[0]; // 学习率0.1
            cost -= 0.1 * gradients[1];

            System.out.printf("第%d天: 价格=%.1f 成本=%.1f 损失=%.1f%n",
                    day, price, cost, loss);
        }
    }
    // 计算经营损失的函数（越小越好）
    static double computeLoss(double price, double cost) {
        int customers = (int)(100 - price); // 价格越高顾客越少
        double profit = (price - cost) * customers; // 利润
        double idealProfit = 2000;           // 目标利润

        // 损失 = (实际利润 - 目标利润)^2
        return Math.pow(profit - idealProfit, 2);
    }

    // 计算如何调整参数才能减少损失
    static double[] computeGradients(double price, double cost) {
        double h = 0.001; // 微小变化量

        // 对price求偏导：保持cost不变，改变price看损失变化
        double gradPrice = (computeLoss(price + h, cost) - computeLoss(price - h, cost)) / (2*h);

        // 对cost求偏导：保持price不变，改变cost看损失变化
        double gradCost = (computeLoss(price, cost + h) - computeLoss(price, cost - h)) / (2*h);

        return new double[]{gradPrice, gradCost}; // 梯度向量
    }
}
