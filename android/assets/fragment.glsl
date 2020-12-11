#ifdef GL_ES
    precision mediump float;
#endif

uniform sampler2D u_texture;
uniform sampler2D u_mask;

varying vec4 v_color;
varying vec2 v_texCoord0;


void main()
{
    vec4 texColor = texture2D(u_texture, v_texCoord0);

    texColor = vec4(1.0-texColor.a*0.7,0.3+texColor.a*0.01,0.2,sign(texColor.a));

    gl_FragColor = v_color * texColor;
    
}