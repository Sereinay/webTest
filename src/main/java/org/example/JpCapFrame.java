package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class JpCapFrame extends JFrame {
    private static DefaultTableModel model;
    private static JTextField filterField;
    private JTextField networkCardField;
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
        Font font2 = new Font("宋体", Font.PLAIN, 16);
        Font font3 = new Font("微软雅黑", Font.PLAIN, 16);

        setSize(1550, 1000);
        setTitle("Captor");
        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());

        //
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout());
        JLabel filterLabel = new JLabel("请输入抓包范围:");
        filterField = new JTextField(15);
        filterField.setFont(font3);
        filterPanel.add(filterLabel);
        filterPanel.add(filterField);

        JLabel networkCardLabel = new JLabel("请输入网卡编号:");
        networkCardField = new JTextField(15);
        networkCardField.setFont(font3);
        filterPanel.add(networkCardLabel);
        filterPanel.add(networkCardField);

        container.add(filterPanel, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        startBtn = new JButton("开始");
        checkBtn = new JButton("查看设备信息");
        clearBtn = new JButton("清空屏幕");
        exitBtn = new JButton("退出");

        buttonPanel.add(startBtn);
        buttonPanel.add(checkBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(exitBtn);

        container.add(buttonPanel, BorderLayout.SOUTH);

        // display
        JPanel displayPanel = new JPanel(new BorderLayout());
        showArea = new JTextArea();
        showArea.setLineWrap(true);
        showArea.setFont(font2);
        JScrollPane scrollPane = new JScrollPane(showArea);
        displayPanel.add(scrollPane, BorderLayout.CENTER);
        displayPanel.setPreferredSize(new Dimension(600, 800)); // 调整日志输出区域的宽度

        container.add(displayPanel, BorderLayout.EAST);

        // Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        model = new DefaultTableModel(new String[]{"编号", "时间戳", "源IP", "目的IP", "协议", "长度", "数据"}, 0);
        JTable table = new JTable(model);
        table.setFont(font1);
        table.setRowHeight(30);
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(font2);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        container.add(tablePanel, BorderLayout.CENTER);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}