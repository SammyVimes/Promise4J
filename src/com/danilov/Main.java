package com.danilov;

import com.danilov.promise.Promise;

public class Main {

    private static Promise<String> stringPromise = null;
    private static Promise<String> finalPromise = new Promise<String>();


    public static void main(String[] args) {
        final Promise<Integer> p1 = new Promise<Integer>();
        Thread t1 = new Thread() {
            @Override
            public void run() {
                p1.finish(21, true);
            }
        };
        final Promise<Integer> p2 = new Promise<Integer>();
        Thread t2 = new Thread() {
            @Override
            public void run() {
                p2.finish(21, true);
            }
        };

        Promise.all(p1, p2).then(new Promise.Action<Object[], Object>() {
            @Override
            public Object action(final Object[] data) {
                Integer a = (Integer) data[0];
                Integer b = (Integer) data[1];
                System.out.println(a + b);
                return null;
            }
        });

        t1.start();
        t2.start();
        testMThreads();
    }
    
    private static void testMThreads() {

        final Promise<Integer> integerPromise = new Promise<Integer>();

        Thread t1 = new Thread() {
            @Override
            public void run() {
                Integer a = 25;
                Integer b = 40;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("THREAD1");
                integerPromise.finish(a + b, true);
            }
        };
        final Promise<Double> doublePromise = integerPromise.then(new Promise.Action<Integer, Double>() {
            @Override
            public Double action(final Integer data) {
                return Double.valueOf("" + data) + 0.25d;
            }
        });
        Thread t2 = new Thread() {
            @Override
            public void run() {
                stringPromise = doublePromise.then(new Promise.Action<Double, String>() {
                    @Override
                    public String action(final Double data) {
                        return "" + data;
                    }
                });
                System.out.println("THREAD2");
                stringPromise.then(new Promise.Action<String, Void>() {
                    @Override
                    public Void action(final String data) {
                        finalPromise.finish(data, true);
                        return null;
                    }
                });
            }
        };
        t2.start();
        t1.start();
        finalPromise.then(new Promise.Action<String, Object>() {
            @Override
            public Object action(final String data) {
                System.out.println(data);
                return null;
            }
        });
        
    }
    
}
