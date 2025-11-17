package com.cn.jvm.deeplearning;

/**
 * {@code @Description: GradientDescentCommons}
 * {@code @Author: 一方通行 }
 * {@code @Date: 2025-03-24}
 * {@code @Version:v1.0}
 */

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class GradientDescentCommons {
    private RealMatrix X;
    private RealVector y;
    private RealVector theta;
    private double learningRate;
    private int iterations;

    public GradientDescentCommons(double[][] features, double[] labels, double alpha, int iters) {
        // 添加偏置项并标准化特征
        this.X = normalizeFeatures(features);
        this.y = new ArrayRealVector(labels);
        this.theta = new ArrayRealVector(X.getColumnDimension());
        this.learningRate = alpha;
        this.iterations = iters;
    }

    private RealMatrix normalizeFeatures(double[][] features) {
        // 标准化处理（Z-score标准化）
        RealMatrix matrix = MatrixUtils.createRealMatrix(features);
        StandardDeviation std = new StandardDeviation();

        double[] means = new double[matrix.getColumnDimension()];
        double[] stds = new double[matrix.getColumnDimension()];

        for (int col = 0; col < matrix.getColumnDimension(); col++) {
            double[] column = matrix.getColumn(col);
            means[col] = new Mean().evaluate(column);
            stds[col] = std.evaluate(column, means[col]);
            for (int row = 0; row < matrix.getRowDimension(); row++) {
                matrix.setEntry(row, col, (matrix.getEntry(row, col) - means[col]) / stds[col]);
            }
        }

        // 添加全1列作为偏置项
        return new Array2DRowRealMatrix(
                MatrixUtils.createRealMatrix(matrix.getData())
                        .scalarAdd(1.0) // 标准化后加1保持数值稳定性
                        .getData()
        );
    }

    public void train() {
        for (int i = 0; i < iterations; i++) {
            RealVector predictions = X.operate(theta);
            RealVector errors = predictions.subtract(y);

            // 计算梯度 (X^T * errors) / m
            RealVector gradient = X.transpose().operate(errors)
                    .mapMultiply(1.0 / X.getRowDimension());

            // 更新参数
            theta = theta.subtract(gradient.mapMultiply(learningRate));

            if (i % 100 == 0) {
                double cost = computeCost(errors);
                System.out.printf("Iteration %d: Cost=%.4f%n", i, cost);
            }
        }
    }

    private double computeCost(RealVector errors) {
        return errors.dotProduct(errors) / (2 * X.getRowDimension());
    }

    public double predict(double[] feature) {
        RealVector x = new ArrayRealVector(feature);
        return theta.dotProduct(x);
    }

    public static void main(String[] args) {
        // 样本数据：房间数, 面积(㎡) -> 价格(万元)
        double[][] X = {{2, 50}, {3, 80}, {3, 110}, {4, 140}, {5, 170}};
        double[] y = {320, 480, 620, 760, 900};

        GradientDescentCommons gd = new GradientDescentCommons(X, y, 0.01, 1000);
        gd.train();

        // 预测3室120㎡的价格（需要手动标准化）
        double[] sample = {
                (3 - 3.4) / 0.83666,   // 房间数标准化（原始数据均值为3.4，标准差0.5477）
                (120 - 110) / 31.623   // 面积标准化（均值110，标准差31.623）
        };
        System.out.printf("预测价格: %.2f 万元%n", gd.predict(sample));
    }
}