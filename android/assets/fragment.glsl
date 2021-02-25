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
    if (texColor.a == 1.0)
        texColor = vec4(0,0,0,0);
    else
        texColor = vec4(0.34 + texColor.a * 0.12, 0.33 - texColor.a * 0.05, 0.29 - texColor.a * 0.05, 1);

    gl_FragColor = v_color * texColor;
}