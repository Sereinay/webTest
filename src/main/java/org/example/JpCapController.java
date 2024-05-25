package org.example;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class JpCapController implements Runnable {
    private JpCapFrame frame;
    private JpcapCaptor jpcap = null;
    private static Thread thread = null;
    private static boolean pause = true;
    private NetworkInterface[] devices;
    private int deviceIndex = 0; // 默认设备索引

    public JpCapController() {
        // 创建界面
        frame = new JpCapFrame();
        frame.setVisible(true);

        // 绑定网络设备
        devices = JpcapCaptor.getDeviceList();

        try {
            updateDevice();
        } catch (IOException e) {
            System.out.println("IO异常！");
        }

        setupListeners();
    }

    private void setupListeners() {
        frame.getCheckBtn().addActionListener(e -> showDeviceInfo());
        frame.getStartBtn().addActionListener(e -> toggleCapture());
        frame.getClearBtn().addActionListener(e -> clearDisplay());
        frame.getExitBtn().addActionListener(e -> System.exit(0));
        frame.getNetworkCardField().addActionListener(e -> changeDevice());
    }

    private void showDeviceInfo() {
        frame.getShowArea().append("当前设备全部网络设备信息为： \n");
        int i = 0;
        for (NetworkInterface n : devices) {
            frame.getShowArea().append("序号: " + i + " " + n.name + " | " + n.description + "\n");
            i++;
        }
        frame.getShowArea().append(printSeparator(110, 1));
        frame.getShowArea().append("当前使用网卡信息： " + devices[deviceIndex].name + " | " + devices[deviceIndex].description + "\n");
        frame.getShowArea().append(printSeparator(110, 1));
    }

    public String printSeparator(int separator, int line) {
        StringBuilder l = new StringBuilder();
        String s = "-".repeat(separator);
        l.append("\n".repeat(line));
        return s + l;
    }

    private void toggleCapture() {
        if (pause) {
            if (thread == null) {
                frame.getShowArea().append("开始抓包,抓取范围为：" + JpCapFrame.getFilterField().getText() + ".......\n");
                thread = new Thread(this);
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.start();
                pause = false;
                frame.getStartBtn().setText("暂停");
            } else {
                frame.getStartBtn().setText("暂停");
                pause = false;
                frame.getShowArea().append("继续抓包,抓取范围为：" + JpCapFrame.getFilterField().getText() + " .......\n");
                synchronized (thread) {
                    thread.notify();
                }
            }
        } else {
            pause = true;
            frame.getStartBtn().setText("开始");
            frame.getShowArea().append("暂停抓包\n");
        }
    }

    private void clearDisplay() {
        frame.getShowArea().setText("");
        JpCapFrame.getModel().setRowCount(0);
    }

    private void changeDevice() {
        String input = frame.getNetworkCardField().getText();
        try {
            int index = Integer.parseInt(input);
            if (index >= 0 && index < devices.length) {
                deviceIndex = index;
                updateDevice();
                frame.getShowArea().append("更新到设备: " + devices[deviceIndex].name + " | " + devices[deviceIndex].description + "\n");
            } else {
                frame.getShowArea().append("非法输入: 请输入0到" + (devices.length - 1) + "之间的数字\n");
            }
        } catch (NumberFormatException | IOException ex) {
            frame.getShowArea().append("非法输入: 请输入一个有效的数字\n");
        }
    }

    private void updateDevice() throws IOException {
        if (jpcap != null) {
            jpcap.close();
        }
        jpcap = JpcapCaptor.openDevice(devices[deviceIndex], 1512, true, 50);
    }

    public static void main(String[] args) {
        new JpCapController();
    }

    @Override
    public void run() {
        try {
            capturePackets();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void capturePackets() throws InterruptedException {
        int i = 0;
        while (true) {
            synchronized (thread) {
                if (pause) {
                    thread.wait();
                }
            }
            Packet packet = jpcap.getPacket();
            if (packet instanceof IPPacket ip && ip.version == 4) {
                i++;
                displayPacketInfo(ip);

                String protocol = getProtocol(ip);

                String filterInput = JpCapFrame.getFilterField().getText();
                if (filterInput.equals(ip.src_ip.getHostAddress()) ||
                        filterInput.equals(ip.dst_ip.getHostAddress()) ||
                        filterInput.equals(protocol) ||
                        filterInput.isEmpty()) {
                    ArrayList<String> dataList = new ArrayList<>();
                    Timestamp timestamp = new Timestamp((packet.sec * 1000) + (packet.usec / 1000));

                    dataList.add(i + "");
                    dataList.add(timestamp.toString());
                    dataList.add(ip.src_ip.getHostAddress());
                    dataList.add(ip.dst_ip.getHostAddress());
                    dataList.add(protocol);
                    dataList.add(String.valueOf(packet.data.length));

                    StringBuilder strTemp = new StringBuilder();
                    for (byte b : packet.data) {
                        strTemp.append(b);
                    }
                    dataList.add(strTemp.toString());
                    JpCapFrame.getModel().addRow(dataList.toArray());
                }
            }
        }
    }

    private void displayPacketInfo(IPPacket ip) {
        System.out.println("版本：IPv4");
        System.out.println("优先权：" + ip.priority);
        System.out.println("区分服务：最大的吞吐量：" + ip.t_flag);
        System.out.println("区分服务：最高的可靠性：" + ip.r_flag);
        System.out.println("长度：" + ip.length);
        System.out.println("标识：" + ip.ident);
        System.out.println("DF:Don't Fragment: " + ip.dont_frag);
        System.out.println("NF:Nore Fragment: " + ip.more_frag);
        System.out.println("片偏移：" + ip.offset);
        System.out.println("生存时间：" + ip.hop_limit);
        System.out.println("协议：" + getProtocol(ip));
        System.out.println("源IP " + ip.src_ip.getHostAddress());
        System.out.println("目的IP " + ip.dst_ip.getHostAddress());
        System.out.println("源主机名： " + ip.src_ip);
        System.out.println("目的主机名： " + ip.dst_ip);
        System.out.println("----------------------------------------------");
    }

    private String getProtocol(IPPacket ip) {
        return switch (ip.protocol) {
            case 1 -> "ICMP";
            case 2 -> "IGMP";
            case 6 -> "TCP";
            case 8 -> "EGP";
            case 9 -> "IGP";
            case 17 -> "UDP";
            case 41 -> "IPv6";
            case 89 -> "OSPF";
            default -> "未知";
        };
    }

    public static boolean isPause() {
        return pause;
    }

    public static Thread getThread() {
        return thread;
    }
}
