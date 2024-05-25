package org.example;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

import java.io.IOException;

public class JpCapMain implements Runnable {
    JpCapFrame frame;
    JpcapCaptor jpcap = null;
    private static Thread thread = null;
    private static boolean pause = true;
    private NetworkInterface[] devices;
    private int deviceIndex = 6;  // Default device index

    public JpCapMain() {
        // 创建界面
        frame = new JpCapFrame();
        frame.setVisible(true);

        // 绑定网络设备
        devices = JpcapCaptor.getDeviceList();

        int caplen = 1512;
        boolean promiscCheck = true;

        try {
            updateDevice();
        } catch (IOException e) {
            System.out.println("IO异常！");
        }

        frame.getCheckBtn().addActionListener(e -> {
            frame.getShowArea().append("当前设备全部网络设备信息为： \n");
            int i = 0;
            for (NetworkInterface n : devices) {
                frame.getShowArea().append("序号: " + i + " " + n.name + "     |     " + n.description + "\n");
                i++;
            }
            frame.getShowArea().append(printSeparator(110, 1));
            frame.getShowArea().append("当前使用网卡信息： " + devices[deviceIndex].name + "     |     " + devices[deviceIndex].description + "\n");
            frame.getShowArea().append(printSeparator(110, 1));
        });

        frame.getStartBtn().addActionListener(e -> {
            if (pause) {
                if (thread == null) {
                    frame.getShowArea().append("   开始抓包,抓取范围为：" + JpCapFrame.getFilterField().getText() + " ……\n");
                    thread = new Thread(this);
                    thread.setPriority(Thread.MIN_PRIORITY);
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

        frame.getNetworkCardField().addActionListener(e -> {
            try {
                updateDevice();
            } catch (IOException ex) {
                frame.getShowArea().append("更新设备时发生IO异常\n");
            }
        });
    }

    private void updateDevice() throws IOException {
        String temp = frame.getNetworkCardField().getText();
        if (!temp.isEmpty() && Integer.parseInt(temp) >= 0 && Integer.parseInt(temp) < devices.length) {
            deviceIndex = Integer.parseInt(temp);
        }
        if (jpcap != null) {
            jpcap.close();
        }
        jpcap = JpcapCaptor.openDevice(devices[deviceIndex], 1512, true, 50);
        frame.getShowArea().append("更新到设备: " + devices[deviceIndex].name + " | " + devices[deviceIndex].description + "\n");
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

    public String printSeparator(int separator, int line) {
        StringBuilder l = new StringBuilder();
        String s = "-".repeat(separator);
        l.append("\n".repeat(line));
        return s + l;
    }

    public static Thread getThread() {
        return thread;
    }

    public static boolean isPause() {
        return pause;
    }
}
