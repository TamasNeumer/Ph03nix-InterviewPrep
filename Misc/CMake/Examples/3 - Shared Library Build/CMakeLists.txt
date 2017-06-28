cmake_minimum_required(VERSION 2.8.9)
project(sharedlib_test)
set(CMAKE_BUILD_TYPE Release)


include_directories(include)

file(GLOB SOURCES "src/*.cpp")

#Generate the shared library from the sources
add_library(testStudent SHARED ${SOURCES})

#Set the location for library installation -- i.e., /usr/lib in this case
# not really necessary in this example. Use "sudo make install" to apply
install(TARGETS testStudent DESTINATION /usr/lib)
