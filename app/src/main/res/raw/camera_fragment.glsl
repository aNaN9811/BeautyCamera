// 导入 samplerExternalOES
#extension GL_OES_EGL_image_external : require

// float 数据的精度 （precision lowp = 低精度） （precision mediump = 中精度） （precision highp = 高精度）
precision mediump float;

uniform samplerExternalOES vTexture;// samplerExternalOES才能采样相机的数据

varying vec2 aCoord;// 把这个最终的计算成果，给片元着色器，拿到最终的成果，我才能上色

void main() {
    vec4 rgba =texture2D(vTexture, aCoord);
    // 305911公式：黑白效果，其实原理就是提取出Y分量
//    float gray = (0.30 * rgba.r + 0.59 * rgba.g + 0.11 * rgba.b);
//    gl_FragColor = vec4(gray, gray, gray, 1.0);
    gl_FragColor = rgba;

    //    底片效果
    //    gl_FragColor = vec4(1.-rgba.r, 1.-rgba.g, 1.-rgba.b, rgba.a);
}

