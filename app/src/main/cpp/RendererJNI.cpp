
#include <jni.h>
#include "RendererJNI.h"
#include <stdlib.h>
#include <stdio.h>
#include <errno.h>
#include <GLES3/gl3.h>
#include <android/asset_manager_jni.h>
#include <android/log.h>

#define LOG_TAG "ndk-build"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
GLint g_programObject;
jint g_width;
jint g_height;

AAssetManager *g_pAssetManager = NULL;

char *readShaderSrcFile(char *shaderFile, AAssetManager *pAssetManager) {
    AAsset *pAsset = NULL;
    char *pBuffer = NULL;
    off_t size = -1;
    int numByte = -1;

    if (NULL == pAssetManager) {
        LOGE("pAssetManager is null!");
        return NULL;
    }
    pAsset = AAssetManager_open(pAssetManager, shaderFile, AASSET_MODE_UNKNOWN);
    //LOGI("after AAssetManager_open");

    size = AAsset_getLength(pAsset);
    LOGI("after AAssetManager_open");
    pBuffer = (char *) malloc(size + 1);
    pBuffer[size] = '\0';

    numByte = AAsset_read(pAsset, pBuffer, size);
    LOGI("%s : [%s]", shaderFile, pBuffer);
    AAsset_close(pAsset);

    return pBuffer;
}


GLuint LoadShader(GLenum type, const char *shaderSrc) {
    GLuint shader;
    GLint compiled;

    shader = glCreateShader(type);

    if (shader == 0) {
        return 0;
    }

 
    glShaderSource(shader, 1, &shaderSrc, NULL);


    glCompileShader(shader);


    glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);

    if (!compiled) {
        GLint infoLen = 0;

        glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);

        if (infoLen > 1) {
            char *infoLog = (char *) malloc(sizeof(char) * infoLen);

            glGetShaderInfoLog(shader, infoLen, NULL, infoLog);
            LOGE("Error compiling shader:[%s]", infoLog);

            free(infoLog);
        }

        glDeleteShader(shader);
        return 0;
    }

    return shader;

}

extern "C" JNIEXPORT void JNICALL Java_com_milk_demo4_RendererJNI_glesInit
        (JNIEnv *pEnv, jobject obj) {
    char vShaderStr[] =
            "#version 300 es                          \n"
            "layout(location = 0) in vec4 vPosition;  \n"
            "void main()                              \n"
            "{                                        \n"
            "   gl_Position = vPosition;              \n"
            "}                                        \n";

    char fShaderStr[] =
            "#version 300 es                              \n"
            "precision mediump float;                     \n"
            "out vec4 fragColor;                          \n"
            "void main()                                  \n"
            "{                                            \n"
            "   fragColor = vec4 ( 105, 105,105,0);        \n"
            "}                                            \n";

    //char *pVertexShader = readShaderSrcFile("shader/vs.glsl", g_pAssetManager);
    //char *pFragmentShader = readShaderSrcFile("shader/fs.glsl", g_pAssetManager);

    GLuint vertexShader;
    GLuint fragmentShader;
    GLuint programObject;
    GLint linked;

    // 加载顶点/碎片着色器
    vertexShader = LoadShader(GL_VERTEX_SHADER, vShaderStr);
    fragmentShader = LoadShader(GL_FRAGMENT_SHADER, fShaderStr);
    //vertexShader = LoadShader(GL_VERTEX_SHADER, pVertexShader);
    //fragmentShader = LoadShader(GL_FRAGMENT_SHADER, pFragmentShader);

  
    programObject = glCreateProgram();

    if (programObject == 0) {
        return;
    }

    glAttachShader(programObject, vertexShader);
    glAttachShader(programObject, fragmentShader);

    // 链接程序
    glLinkProgram(programObject);

    // 检查链路状态
    glGetProgramiv(programObject, GL_LINK_STATUS, &linked);

    if (!linked) {
        GLint infoLen = 0;
        glGetProgramiv(programObject, GL_INFO_LOG_LENGTH, &infoLen);
        if (infoLen > 1) {
            char *infoLog = (char *) malloc(sizeof(char) * infoLen);
            glGetProgramInfoLog(programObject, infoLen, NULL, infoLog);
            LOGE("Error linking program:[%s]", infoLog);

            free(infoLog);
        }

        glDeleteProgram(programObject);
        return;
    }

    // 存储程序对象
    g_programObject = programObject;
    glClearColor(0, 0, 0, 0);

}

/*
 * Class:     opengl_panjq_com_opengl_demo_RendererJNI
 * Method:    glesRender
 * Signature: ()V
 */
extern "C" JNIEXPORT void JNICALL Java_com_milk_demo4_RendererJNI_glesRender
        (JNIEnv *pEnv, jobject obj) {
        
    //绘制一个三角形
    GLfloat vVertices[] = {0.0f, 0.5f, 0.0f,
                           -0.5f, -0.5f, 0.0f,
                           0.5f, -0.5f, 0.0f
    };
    //绘制矩形
    GLfloat vertices[] = {
            // 第一个三角形
            0.5f, 0.5f, 0.0f,   // 右上角
            0.5f, -0.5f, 0.0f,  // 右下角
            -0.5f, 0.5f, 0.0f,  // 左上角
            // 第二个三角形
            0.5f, -0.5f, 0.0f,  // 右下角
            -0.5f, -0.5f, 0.0f, // 左下角
            -0.5f, 0.5f, 0.0f   // 左上角
    };
    
    // 设置窗口
    glViewport(0, 0, g_width, g_height);

    // 清除颜色缓冲
    glClear(GL_COLOR_BUFFER_BIT);

    // 使用程序对象
    glUseProgram(g_programObject);

    // 加载顶点数据
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, vertices);

    glEnableVertexAttribArray(0);

    glDrawArrays(GL_TRIANGLES, 0, 6);


}

extern "C" JNIEXPORT void JNICALL Java_com_milk_demo4_RendererJNI_readShaderFile
        (JNIEnv *env, jobject self, jobject assetManager) {
    if (assetManager && env) {
        //LOGI("before AAssetManager_fromJava");
        g_pAssetManager = AAssetManager_fromJava(env, assetManager);
        //LOGI("after AAssetManager_fromJava");
        if (NULL == g_pAssetManager) {
            LOGE("AAssetManager_fromJava() return null !");
        }
    } else {
        LOGE("assetManager is null !");
    }
}


/*
 * Class:     opengl_panjq_com_opengl_demo_RendererJNI
 * Method:    glesResize
 * Signature: (II)V
 */
extern "C" JNIEXPORT void JNICALL Java_com_milk_demo4_RendererJNI_glesResize
        (JNIEnv *pEnv, jobject obj, jint width, jint height) {
    g_width = width;
    g_height = height;

}
