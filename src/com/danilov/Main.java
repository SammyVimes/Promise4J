package com.danilov;

import com.danilov.promise.Promise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    private static Promise<String> stringPromise = null;
    private static Promise<String> finalPromise = new Promise<String>();


    public static void main(String[] args) {
//        final Promise<Integer> p1 = new Promise<Integer>();
//        Thread t1 = new Thread() {
//            @Override
//            public void run() {
//                p1.finish(21, true);
//            }
//        };
//        final Promise<Integer> p2 = new Promise<Integer>();
//        Thread t2 = new Thread() {
//            @Override
//            public void run() {
//                p2.finish(21, true);
//            }
//        };
//
//        Promise.all(p1, p2).then(new Promise.Action<Object[], Object>() {
//            @Override
//            public Object action(final Object[] data) {
//                Integer a = (Integer) data[0];
//                Integer b = (Integer) data[1];
//                System.out.println(a + b);
//                return null;
//            }
//        });
//
//        t1.start();
//        t2.start();
//        testMThreads();

        List<Promise<Boolean>> promises = new ArrayList<Promise<Boolean>>();
        int partSize = 5000000;
        final int arraySize = 10000000;
        final Integer[] array = new Integer[arraySize];
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        int parts = arraySize / partSize;
        final long _startTime = System.nanoTime();
        for (int i  = 0; i < parts; i++) {
            final int partStart = i * partSize;
            final int partEnd = (i + 1) * partSize;
            final Promise<Boolean> promise = new Promise<Boolean>();
            promises.add(promise);
            new Thread() {
                public void run() {
                    Integer prev = null;
                    for (int j = partStart; j < partEnd; j++) {
                        if (prev != null && prev > array[j]) {
                            promise.finish(false, true);
                            return;
                        }
                        prev = array[j];
                    }
                    promise.finish(true, true);
                }
            }.start();
        }
        Promise.all(promises).then(new Promise.Action<Object[], Void>() {
            @Override
            public Void action(final Object[] data, final boolean success) {
                for (int i = 0; i < data.length; i++) {
                    Boolean b = (Boolean) data[i];
                    System.out.println(b);
                    if (!b) {
                        System.out.println("Плохо");
                        return null;
                    }
                }
                System.out.println("Хорошо");
                return null;
            }
        }).then(new Promise.Action<Void, Object>() {
            @Override
            public Object action(final Void data, final boolean success) {
                System.out.println("Ended in seconds: " + ((double)(System.nanoTime() - _startTime) / 1000000000));
                return null;
            }
        }).then(new Promise.Action<Object, Object>() {
            @Override
            public Object action(final Object data, final boolean success) {
                long startTime = System.nanoTime();
                Integer prev = null;
                for (int i = 0; i < array.length; i++) {
                    if (prev != null && prev > array[i]) {
                        System.out.println("Плохо");
                        System.out.println("Ended in seconds: " + ((double)(System.nanoTime() - startTime) / 1000000000));
                        return null;
                    }
                    prev = array[i];
                }
                System.out.println("Хорошо");
                System.out.println("Ended in seconds: " + ((double)(System.nanoTime() - startTime) / 1000000000));
                return null;
            }
        });
    }
    
//    private static void testMThreads() {
//
//        final Promise<Integer> integerPromise = new Promise<Integer>();
//
//        Thread t1 = new Thread() {
//            @Override
//            public void run() {
//                Integer a = 25;
//                Integer b = 40;
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("THREAD1");
//                integerPromise.finish(a + b, true);
//            }
//        };
//        final Promise<Double> doublePromise = integerPromise.then(new Promise.Action<Integer, Double>() {
//            @Override
//            public Double action(final Integer data) {
//                return Double.valueOf("" + data) + 0.25d;
//            }
//        });
//        Thread t2 = new Thread() {
//            @Override
//            public void run() {
//                stringPromise = doublePromise.then(new Promise.Action<Double, String>() {
//                    @Override
//                    public String action(final Double data) {
//                        return "" + data;
//                    }
//                });
//                System.out.println("THREAD2");
//                stringPromise.then(new Promise.Action<String, Void>() {
//                    @Override
//                    public Void action(final String data) {
//                        finalPromise.finish(data, true);
//                        return null;
//                    }
//                });
//            }
//        };
//        t2.start();
//        t1.start();
//        finalPromise.then(new Promise.Action<String, Object>() {
//            @Override
//            public Object action(final String data) {
//                System.out.println(data);
//                return null;
//            }
//        });
//
//    }
    
}
