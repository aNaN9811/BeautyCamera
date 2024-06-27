# BeautyCamera

通过 Camera1 配合内置 OpenGL 的 GLSurfaceView 实现相机页面的预览绑定。

OpenGL 自定义 GLSurfaceView 的 Renderer 实现 Filter 程序与 GL 线程绑定，

通过 FBO 缓存与顶点着色器与片元着色器对于预览画面的修改（例如：反色画面效果，黑白画面效果）。

其中还通过引入 OpenCV 4.1.1 内置的人脸定位模型实现人脸识别及提取，

[SeetaFace](https://github.com/seetaface/SeetaFaceEngine) 进行人脸特征点定位并最终完成双眼放大的效果。

同时使用 MediaCodec 的 InputSurface 结合 MediaMuxer 并与自定义 EGL 绑定实现最终效果的录制存储。
