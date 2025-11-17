package com.cn.jvm.deeplearning;

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;

/**
 * {@code @Description: MnistTrain}
 * {@code @Author: 一方通行 }
 * {@code @Date: 2025-03-26}
 * {@code @Version:v1.0}
 */
public class MnistTrain {

    public void train() throws Exception {
        // 设置训练数据和测试数据迭代器
        DataSetIterator trainIter = new MnistDataSetIterator(32, true, 12345);
        DataSetIterator testIter = new MnistDataSetIterator(32, false, 12345);
        // 构建神经网络的配置
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(12345)
                .l2(0.0005)
                .updater(new Adam(0.001))
                .list()
                .layer(0, new ConvolutionLayer.Builder(5, 5)
                        .nIn(1)
                        .stride(1, 1)
                        .nOut(20)
                        .activation(Activation.IDENTITY)
                        .build())
                .layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2, 2)
                        .stride(2, 2)
                        .build())
                .layer(2, new ConvolutionLayer.Builder(5, 5)
                        .stride(1, 1)
                        .nOut(50)
                        .activation(Activation.IDENTITY)
                        .build())
                .layer(3, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2, 2)
                        .stride(2, 2)
                        .build())
                .layer(4, new DenseLayer.Builder().nOut(500).build())
                .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(10)
                        .activation(Activation.SOFTMAX)
                        .build())
                .setInputType(InputType.convolutionalFlat(28, 28, 1))
                .build();

        // 初始化模型并设置参数
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        model.setListeners(new ScoreIterationListener(10));

        // 训练模型
        for (int i = 0; i < 5; i++) {
            model.fit(trainIter);
        }

        // 在测试数据上评估模型
        Evaluation eval = model.evaluate(testIter);
        System.out.println(eval.stats());

        // 将模型保存到本地磁盘
        File locationToSave = new File("model.zip"); // 将要保存的文件位置
        boolean saveUpdater = true; // 是否保存updater（用于进行模型参数更新）
        ModelSerializer.writeModel(model, locationToSave, saveUpdater);
    }
}