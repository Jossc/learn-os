/**
 * @Description: ProducerConsumer
 * @Author: 一方通行
 * @Date: 2021-07-18
 * @Version:v1.0
 */
public class ProducerConsumer {
    static final int N = 100;
    static Producer producer = new Producer();
    static OurMonitor monitor = new OurMonitor();
    static Consumer consumer = new Consumer();

    public static void main(String[] args) {
        producer.start();
        consumer.start();
    }


    static class Producer extends Thread {

        public void run() {
            int item;
            while (true) {
                item = producerItem();
                monitor.insert(item);
            }
        }

        /**
         * 每次循环都加1
         * 这里不用考虑线程安全
         *
         * @return 返回自增1
         */
        private int producerItem() {
            int producerItem = 1;
            return producerItem + 1;
        }
    }

    static class Consumer extends Thread {
        public void run() {
            int item;
            while (true) {
                item = monitor.remove();
                consumerItem(item);
            }
        }

        private void consumerItem(int item) {
            item = item - 1;
        }
    }

    /**
     * 创建一个管程
     */
    static class OurMonitor {
        private int buffer[] = new int[N];
        //计数器和索引
        private int count, lo = 0, hi = 0;

        /**
         * 生产一个对象
         *
         * @param value
         */
        public synchronized void insert(int value) {
            String format = String.format("producer insert value %s", value);
            System.out.println(format);
            if (count == N) {
                gotoSleep();
            }
            buffer[hi] = value;
            hi = (hi + 1) % N;
            count = count + 1;
            if (count == 1) {
                notify();
            }

        }

        /**
         * 消费数据
         *
         * @return 队列长度
         */
        public synchronized int remove() {
            int val;
            if (count == 0) {
                gotoSleep();
            }
            val = buffer[lo];
            count = count - 1;
            if (count == N - 1) {
                notify();
            }
            return val;
        }

        private void gotoSleep() {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
