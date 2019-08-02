# PauseRecord
PauseRecord

![Example](https://img-blog.csdn.net/20160812155648241?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast  "Example")

## 对类的说明：

AudioRecorder：封装了录音的方法：创建录音对象、开始、暂停、停止、取消，使用静态枚举类Status来记录录音的状态。

FileUtils：文件工具类，用于文件路径的获取

PcmToWav：封装了将.pcm文件转化.wav文件的方法

WaveHeader： wav文件头

RecordStreamListener：监听录音音频流，用于拓展业务的处理

--------------------- 
作者：imhxl 
来源：CSDN 
原文：https://blog.csdn.net/imhxl/article/details/52190451 
版权声明：本文为博主原创文章，转载请附上博文链接！
