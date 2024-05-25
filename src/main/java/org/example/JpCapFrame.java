package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class JpCapFrame extends JFrame {
    private static DefaultTableModel model;
    private static JTextField filterField;
    private static JTextField networkCardField;
    private JTextArea showArea;
    private JButton startBtn;
    private JButton checkBtn;
    private JButton exitBtn;
    private JButton clearBtn;

    public JpCapFrame() {
        super();
        initGUI();
    }

    public static DefaultTableModel getModel() {
        return model;
    }

    public JTextArea getShowArea() {
        return showArea;
    }

    public JButton getStartBtn() {
        return startBtn;
    }

    public JButton getCheckBtn() {
        return checkBtn;
    }

    public JButton getExitBtn() {
        return exitBtn;
    }

    public JButton getClearBtn() {
        return clearBtn;
    }

    public static JTextField getFilterField() {
        return filterField;
    }

    public JTextField getNetworkCardField() {
        return networkCardField;
    }

    private void initGUI() {
        Font font1 = new Font("宋体", Font.BOLD, 15);
        Font font4 = new Font("宋体", Font.BOLD, 14);
        Font font2 = new Font("宋体", Font.PLAIN, 16);
        Font font3 = new Font("微软雅黑", Font.PLAIN, 16);

        setSize(1550, 1000);
        setTitle("Captor");
        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        checkBtn = new JButton("查看网卡信息");
        checkBtn.setFont(font4);
        topPanel.add(checkBtn);

        startBtn = new JButton("开始");
        startBtn.setFont(font4);
        topPanel.add(startBtn);

        clearBtn = new JButton("清空");
        clearBtn.setFont(font4);
        topPanel.add(clearBtn);

        exitBtn = new JButton("退出");
        exitBtn.setFont(font4);
        topPanel.add(exitBtn);

        // 网络卡选择和过滤器输入面板
        JPanel networkPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        JLabel selectNetworkCard = new JLabel("选择网卡:");
        selectNetworkCard.setFont(font1);
        networkPanel.add(selectNetworkCard);

        networkCardField = new JTextField(10);
        networkPanel.add(networkCardField);

        JLabel filterLabel = new JLabel("过滤器:");
        filterLabel.setFont(font1);
        networkPanel.add(filterLabel);

        filterField = new JTextField(50);
        networkPanel.add(filterField);

        // 将两个顶部面板合并
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(topPanel, BorderLayout.WEST);
        topContainer.add(networkPanel, BorderLayout.EAST);

        // 表格
        String[] columnNames = {"序号", "时间", "源IP", "目的IP", "协议", "包长度", "内容"};
        model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(font1);
        table.setFont(font2);
        table.setRowHeight(20);
        table.setEnabled(false);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(1550, 600));

        // 日志显示区
        JPanel bottomPanel = new JPanel(new BorderLayout());
        showArea = new JTextArea();
        showArea.setEditable(false);
        showArea.setLineWrap(false);
        showArea.setFont(font3);
        JScrollPane logScrollPane = new JScrollPane(showArea);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        bottomPanel.add(logScrollPane, BorderLayout.CENTER);
        bottomPanel.setPreferredSize(new Dimension(1550, 300));

        // 添加组件到容器
        container.add(topContainer, BorderLayout.NORTH);
        container.add(tableScrollPane, BorderLayout.CENTER);
        container.add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
