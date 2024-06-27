attribute vec4 vPosition; // 顶点坐标，相当于：相机的四个点位置排版

attribute vec2 vCoord; // 纹理坐标，用来图形上色的

varying vec2 aCoord; // 把这个最终的计算成果，给片元着色器 【不需要Java传递，他是计算出来的】

void main() {
    gl_Position = vPosition; // 确定好位置排版   gl_Position OpenGL着色器语言内置的变量

    aCoord = vCoord;
}
