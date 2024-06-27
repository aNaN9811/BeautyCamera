// 大眼 片元着色器 【大眼特效的算法处理，针对纹理数据进行处理，局部放大算法】
precision mediump float;

varying vec2 aCoord;
uniform sampler2D vTexture;

uniform vec2 left_eye;// 左眼x/y
uniform vec2 right_eye;// 右眼x/y
//uniform vec2 right_eye1;// 右眼x/y
//uniform vec2 right_eye2;// 右眼x/y
//uniform vec2 right_eye3;// 右眼x/y
//uniform vec2 right_eye4;// 右眼x/y

// r: 原来的点 距离眼睛中心点距离（半径）
// rmax: 局部放大 最大作用半径
// a：系数，系数如果等于0，那么就等于当前的r
float fs(float r, float rmax) {
    float a = 0.9;// 放大系数
    return (1.0 - pow(r / rmax - 1.0, 2.0) * a);
}

/* 放大区域外面的rgba，需要去寻找里面的rgba，来替代外面的rgba
   根据需要采集的点aCoord， 来计算新的点
     oldCoord：原始的点，原来的点
     eye：     眼睛的坐标
     rmax：    (rmax两眼间距) 注意是放大后的间距
   return newCoord：左眼/右眼 放大位置的采用点
*/
vec2 calcNewCoord(vec2 oldCoord, vec2 eye, float rmax){
    vec2 newCoord = oldCoord;
    float r = distance(oldCoord, eye);
    if (r > 0.0f && r < rmax) {
        float fsr = fs(r, rmax);
        //    新点 - 眼睛     /  老点 - 眼睛   = 新距离;
        // (newCoord - eye) / (coord - eye) = fsr;
        // 之前遗留参考如下：
        // (newCoord - eye) / (coord - eye) = fsr / r; // 上面没有*，这里就不需要除
        // newCoord - eye = fsr / r * (coord - eye)
        // newCoord = fsr / r * (coord - eye) + eye
        // newCoord新点 = 新距离 * （老点 - 眼睛） + 眼睛
        newCoord = fsr * (oldCoord - eye) + eye;
    }
    return newCoord;
}

void main(){
        // 最大两眼间距的一半
        float rmax = distance(left_eye, right_eye) / 2.0;

        // aCoord是整副图像
        vec2 newCoord = calcNewCoord(aCoord, left_eye, rmax); // 求左眼放大位置的采样点
        newCoord = calcNewCoord(newCoord, right_eye, rmax); // 求右眼放大位置的采样点
        // 此newCoord就是大眼像素坐标值
        gl_FragColor = texture2D(vTexture, newCoord);

//    float distToLeftEye = distance(aCoord, left_eye);
//    float distToRightEye = distance(aCoord, right_eye);
//    float distToRightEye1 = distance(aCoord, right_eye1);
//    float distToRightEye2 = distance(aCoord, right_eye2);
//    float distToRightEye3 = distance(aCoord, right_eye3);
//    float distToRightEye4 = distance(aCoord, right_eye4);
//    // 设置一个阈值，决定多大范围内的像素会被渲染为红色
//    float threshold = 0.01;// 你可以调整这个值
//
//    if (distToLeftEye < threshold || distToRightEye < threshold || distToRightEye1 < threshold || distToRightEye2 < threshold || distToRightEye3 < threshold || distToRightEye4 < threshold) {
//        // 如果像素靠近左眼或右眼，渲染为红色
//        gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);// 红色
//    } else {
//        // 否则，使用原来的颜色
//        gl_FragColor = texture2D(vTexture, aCoord);
//    }
}