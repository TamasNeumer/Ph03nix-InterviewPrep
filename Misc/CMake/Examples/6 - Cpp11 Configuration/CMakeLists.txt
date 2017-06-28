cmake_minimum_required(VERSION 2.8.9)
project(testProject)
 
include_directories(include)
file(GLOB SOURCES "src/*.cpp")

# Compiler settings
if (WIN32)
  set(CMAKE_CXX_FLAGS "/DWIN32 /D_WINDOWS /W3 /Zm1000 /EHsc /GR")
endif (WIN32)

if(UNIX)
	list( APPEND CMAKE_CXX_FLAGS "-std=c++11 ${CMAKE_CXX_FLAGS} -g -ftest-coverage -fprofile-arcs")
endif(UNIX)

add_executable(testStudent ${SOURCES})