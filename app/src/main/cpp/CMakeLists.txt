cmake_minimum_required(VERSION 3.22.1)
project(powerkey LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

add_library(native-lib SHARED
        powerkey.cpp
)

find_library(log-lib log)

target_compile_options(native-lib PRIVATE
        -fvisibility=hidden
        -Wall -Wextra -Wpedantic
)

target_include_directories(native-lib PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})

target_link_libraries(native-lib
        ${log-lib}
)
