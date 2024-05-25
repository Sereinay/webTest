package org.example;

import jpcap.JpcapCaptor;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;

import java.sql.Timestamp;
import java.util.ArrayList;

public class JpCapPacket {
    private JpcapCaptor jpcap;

    public JpCapPacket(JpcapCaptor jpcap) {
        this.jpcap = jpcap;
    }

    public void capture() throws InterruptedException {
        int i = 0;
        while (true) {
            synchronized (JpCapController.getThread()) {
                if (JpCapController.isPause()) {
                    JpCapController.getThread().wait();
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
}
