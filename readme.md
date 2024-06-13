# 抓包程序使用指北
## 运行环境配置 
  1. 首先得有java编译环境，安装并配置好jdk；

  2. 需要安装Winpcap，Winpcap是windows平台下的一个免费的，公共的网络访问系统（Linux系统是Libpcap）；

  3. 还需要下载Jpcap，Jpcap就是调用Winpcap给java提供一个公共的接口，从而实现平台无关性， 并能够捕获发送数据包。Jpcap包括Jpcap.jar和Jpcap.dll，两者需要版本一致，并且区分32位和64位。
  4. 将Jpcap.jar导入你的idea项目，（我已经做了 你可以忽略这一步）
  5.  重要！！<b>并且把Jpcap.dll复制到java的jdk的bin目录下.
## 使用
  1. 直接启动JpcapMain的Main方法即可 会弹出来图形化界面以供操作。
     ![start.png](src%2Fmain%2Fresources%2Fstart.png)
  2. 首先点击查看设备信息查看该设备的所有可用网卡。
    ![choose.png](src%2Fmain%2Fresources%2Fchoose.png)
  3. 接着在请输入网卡编号位置输入选择的网卡 默认为0 请根据自己设备选择 （**请输入上一步输入的显示设备的序号，之后敲一次回车** 非法输入会在右侧打印提示） 
    ![chosen.png](src%2Fmain%2Fresources%2Fchosen.png)
  4. 接下来点击开始即可 
     ![ing.png](src%2Fmain%2Fresources%2Fing.png)
  5. 开始之后可以随时点击暂停运行，再次点击继续抓包。 
  6. 清空屏幕会清除所有抓包信息，退出则会直接退出程序。
  7. 抓包范围可以输入源ip/目的ip/TCP/UDP 如果输入非法 则不会有抓包显示。 
