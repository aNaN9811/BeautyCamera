cmake_minimum_required(VERSION 3.22.1)

project("beautycamera")

include_directories(
        include
        FaceAlignment/include
)

add_subdirectory(FaceAlignment)

add_library(
        ${CMAKE_PROJECT_NAME}
        SHARED
        native-lib.cpp
        FaceTrack.cpp
)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/../jniLibs/${CMAKE_ANDROID_ARCH_ABI}")

target_link_libraries(
        ${CMAKE_PROJECT_NAME}
        android
        log
        opencv_java4
        seeta_fa_lib
)