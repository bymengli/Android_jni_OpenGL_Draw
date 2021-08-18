cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("demo4")


include_directories(${CMAKE_SOURCE_DIR} src/main/cpp)

add_library( # Sets the name of the library.
        gltest-lib
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        src/main/cpp/RendererJNI.cpp)
add_library( # Sets the name of the library.
        native-lib
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        src/main/cpp/native-lib.cpp )

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )


target_link_libraries( # Specifies the target library.
        gltest-lib
        android # 错误：Error: undefined reference to 'AAssetManager_fromJava'
        GLESv3  # 把opengl库文件添加进来，GLESv3
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )
target_link_libraries( # Specifies the target library.
        native-lib

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )
