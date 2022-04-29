# AppRobotController

## Application project to remotely control the "Robotshop rover v2"

![Robotshop rover v2](https://www.robotshop.com/media/catalog/product/cache/image/1350x/9df78eab33525d08d6e5fb8d27136e95/d/f/dfrobotshop-rover-tracked-robot-basic-kit.jpg)

### Dependencies :
1. [FFmpeg-kit](https://github.com/tanersener/ffmpeg-kit)
2. [lib-VLC](https://code.videolan.org/videolan/vlc-android/-/tree/master/libvlc)

_These libs are already compiled and implemented in the project._

### Bluetooth Control :
The bluetooth communication is made with __[Bluetooth V3](https://www.gotronic.fr/art-module-bluetooth-v3-tel0026-19389.htm).__

__⚠️Warning⚠️ :__ if you intend to use another module, you should know that the _UUID_ is important to be set with the right value, otherwise your device will appear to be paired but it won't connect.

### Camera/GoPro Control :
The camera communication is made on the client side with HTTP request and the camera returns a UDP streams that can be displayed with FFmpeg.

__⚠️Warning⚠️ :__ 
* Use a _Thread_ that will send UDP packet to 10.5.5.9:8554 to keep alive the stream, otherwise it will stop itself (could also send the HTTP request for restarting the live, but less practical to do that).
	*  _You can find the HTTP request over [there](https://github.com/KonradIT/goprowifihack/blob/master/HERO5/README.md)._ 
* You have to use the lib VLC because Android classic widgets can't display a stream from UDP packets.

:no_entry: __This project is not completed__
 the display part for the Livestream still has some bug, still trying to correct them