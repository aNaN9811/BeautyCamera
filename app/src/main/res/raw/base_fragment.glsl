// float 数据的精度 （precision lowp = 低精度） （precision mediump = 中精度） （precision highp = 高精度）
precision mediump float;

uniform sampler2D vTexture;

varying vec2 aCoord;

void main() {
    gl_FragColor = texture2D(vTexture, aCoord);
}

