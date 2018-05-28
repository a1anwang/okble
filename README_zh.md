# okble: 简单易用的 Android BLE library
## 简介
  BLE有Center(简单理解为客户端)和Peripheral(服务端)2个角色。我们android开发常用的就是客户端角色，使用API连接硬件外设(服务端)，然后进行相关的数据交互，如读取手环上的心率电量等数据，控制蓝牙灯的开关。
    okble功能也包含了
### Center
  Center主要可以分为 扫描->连接->通讯 三块内容. 通讯主要包含read，write，notify/indicate。read就是读取设备上的数据(如读取外设电量)，write就是发送数据(如发送关闭命令关闭蓝牙灯)，notify/indicate 字面意思就是通知/指示, 是用来接收设备主动上报的数据的(如手环可以每隔1秒就告诉APP心率值, 然后APP展现一个心率谱图)，这样就达成了双向通讯。
### Peripheral
  Peripheral主要理解为硬件外设，提供数据用的。在开发APP时很少关心这个，因为大家都是直接拿着硬件来调试的。在android5.0时，增加了Peripheral相关的API， 意味着可以让android设备模拟成外设，作为Peripheral来提供数据。这样，当我们没有硬件设备的时候，可以拿2台手机进行BLE开发，很方便。还可以扩展很多其他功能，比如使用BLE实现蓝牙聊天(google sample里面有经典蓝牙的聊天demo)，还可以把手机模拟成iBeacon等等。
### okble将会让我们轻松实现上面提到的功能
 

## 功能特点:
 - 简单明了，一个OKBLEDevice即可完成所有通讯操作
 - 顺序执行通讯操作，BLE内部是不允许同时执行多次通讯的,okble使用队列让操作排队,先进先执行,保证所有的操作都能执行
 - 动态权限集成,target sdk在23(android6.0)及以上时,扫描是需要定位权限的,okble已集成,会有回调告知是否已授权
 - 向下兼容到API 18(android4.3),在android5.0中google变更了扫描api,okble依然使用的是4.3的api,只为了兼容更多的手机
 - 双回调,处理灵活:定义了统一的listener来监听设备的状态, 也可以为每一个蓝牙通讯操作设置单独的监听,方便页面多的情况下使用
 - 自动连接:断开后可以实现自动连接,直到连接成功
 - 自动识别write type,自动识别notify or indicate
 
 ## 立即开始
 
 
 
 
