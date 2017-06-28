cmake_minimum_required(VERSION 2.8.9)
project (TestLibrary)

#For the shared library:
set ( PROJECT_LINK_LIBS libtestStudent.so )
link_directories(libs)

#For the static library:
#set ( PROJECT_LINK_LIBS libtestStudent.a )
#link_directories(libs)

include_directories(libinclude)

add_executable(libtest libtest.cpp)
target_link_libraries(libtest ${PROJECT_LINK_LIBS} )
