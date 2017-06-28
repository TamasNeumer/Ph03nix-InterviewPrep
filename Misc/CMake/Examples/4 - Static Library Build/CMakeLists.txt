cmake_minimum_required(VERSION 2.8.9)
project(directory_test)
set(CMAKE_BUILD_TYPE Release)
 
include_directories(include)
 
file(GLOB SOURCES "src/*.cpp")
 
add_library(testStudent STATIC ${SOURCES})
 
install(TARGETS testStudent DESTINATION /usr/lib)