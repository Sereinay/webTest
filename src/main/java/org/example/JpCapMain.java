package org.example;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

import java.io.IOException;

public class JpCapMain implements Runnable {
    JpCapFrame frame;
    JpcapCaptor jpcap = null;
    private static Thread thread = null;
    private static boolean pause = true;

    public JpCapMain() {
        //创建界面
        frame = new JpCapFrame();
        frame.setVisible(true);

        //绑定网络设备
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();

        int caplen = 1512;
        boolean promiscCheck = true;


        frame.getCheckBtn().addActionListener(e -> {
            frame.getShowArea().append("当前设备全部网络设备信息为： \n");
            int i = 0;
            for (NetworkInterface n : devices) {
                System.out.println("序号: " + i + " " + n.name + "     |     " + n.description);
                frame.getShowArea().append("序号: " + i + " " + n.name + "     |     " + n.description + "\n");
                i++;
            }

        });

        frame.getStartBtn().addActionListener(e -> {
            /*
        default为0 可以根据自己的需求来更改成不同的
        */
            int device = 0;
            String temp = JpCapFrame.getFilterField().getText();
            if (!temp.isEmpty() && Integer.parseInt(temp) >= 0 && Integer.parseInt(temp) < devices.length) {
                device = Integer.parseInt(JpCapFrame.getFilterField().getText());
            }
            try {
                jpcap = JpcapCaptor.openDevice(devices[device], caplen, promiscCheck, 50);
            } catch (IOException e1) {
                System.out.println("IO异常！");
            }
            frame.getShowArea().append(printSeparator(110, 1));
            frame.getShowArea().append("当前使用网卡信息： " + devices[device].name + "     |     " + devices[device].description + "\n");
            frame.getShowArea().append(printSeparator(110, 1));

            if (pause) {
                if (thread == null) {
                    frame.getShowArea().append("   开始抓包,抓取范围为：" + JpCapFrame.getFilterField().getText() + " ……\n");
                    thread = new Thread(this);
                    thread.setPriority(Thread.MIN_PRIORITY);
                    //thread.sleep(100);zh
                    thread.start();
                    pause = false;
                    frame.getStartBtn().setText("暂停");
                } else {
                    frame.getStartBtn().setText("暂停");
                    pause = false;
                    frame.getShowArea().append("   继续抓包,抓取范围为：" + JpCapFrame.getFilterField().getText() + " ……\n");
                    synchronized (thread) {
                        thread.notify();
                    }
                }
            } else {
                pause = true;
                frame.getStartBtn().setText("开始");
                frame.getShowArea().append(" 暂停抓包\n");
            }
        });

        frame.getClearBtn().addActionListener(e -> {
            frame.getShowArea().setText("");
            JpCapFrame.getModel().setRowCount(0);
        });

        frame.getExitBtn().addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        new JpCapMain();
    }

    @Override
    public void run() {
        try {
            new JpCapPacket(jpcap).capture();
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param separator "-"的数量
     * @param line      "\n"的数量
     * @return 打印字符串
     */
    public String printSeparator(int separator, int line) {
        StringBuilder l = new StringBuilder();

        String s = "-".repeat(Math.max(0, separator));

        l.append("\n".repeat(Math.max(0, line)));
        return s + l;
    }

    public static Thread getThread() {
        return thread;
    }

    public static boolean isPause() {
        return pause;
    }
}
