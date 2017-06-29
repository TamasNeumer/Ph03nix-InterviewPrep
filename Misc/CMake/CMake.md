# CMake

## What is CMake?
**CMake is an extensible, open-source system that manages the build process in an operating system and in a compiler-independent manner.**  
Simple configuration files placed in each source directory (called CMakeLists.txt files) are used to generate standard build files (e.g., makefiles on Unix and projects/workspaces in Windows MSVC) which are used in the usual way.

## Install
- Ubuntu
  - ```sudo apt-get install cmake```

## Common Commands in CMake
- Specify the minimum version for CMake
  - ```make_minimum_required(VERSION 2.8)```
- Project's name (in this case hello)
  - ```project(hello)```
- Set the output directory. For this some global variables:
  - ```CMAKE_BINARY_DIR``` if you are building in-source, this is the same as ```CMAKE_SOURCE_DIR```, otherwise this is the top level directory of your build tree.
  - ```CMAKE_SOURCE_DIR``` this is the directory, from which cmake was started, i.e. the top level source directory
  - ```EXECUTABLE_OUTPUT_PATH``` set this variable to specify a common place where CMake should put all executable files (instead of ```CMAKE_CURRENT_BINARY_DIR```)
  - ```LIBRARY_OUTPUT_PATH``` set this variable to specify a common place where CMake should put all libraries (instead of ```CMAKE_CURRENT_BINARY_DIR```)
- ```add_executable(hello ${PROJECT_SOURCE_DIR}/test.cpp)```
- ```add_library(testStudent SHARED ${SOURCES})``` The library is built as a shared library using the SHARED flag (other options are: STATIC or MODULE) , and the testStudent name is used as the name of the shared library.
- ```install(TARGETS testStudent DESTINATION /usr/lib)``` define an installation location for the library (in this case it is /usr/lib). Deployment is invoked using a call to ```sudo make install``` in this case.
- ```set(property value)``` sets the property to the given value. e.g.: ```#set(SOURCES src/mainapp.cpp src/Student.cpp)```
- ```include_directories(dirName)``` - bring the header files into the build environment
- ```file(...)```used for file manipulation. A practicular example is the following: ```file(GLOB SOURCES "src/*.cpp")``` GLOB will generate a list of all files that match the globbing expressions and store it into the variable. Thus with this command you find all the .cpp files in the source directory and append it to SOURCES.
- ```link_directories(directory1 directory2 ...)``` Specify directories in which the linker will look for libraries.
	- Note that this command is rarely necessary. Library locations returned by find_package() and find_library() are absolute paths. Pass these absolute library file paths directly to the target_link_libraries() command. CMake will ensure the linker finds them
- `target_link_libraries` Link a target to given libraries.
	- The named `<target>` must have been created in the current directory by a command such as `add_executable()` or `add_library()`.
	
	
## Examples

### 1 Example with a single file

Our CMake file looks as follows:
```cmake
# Specify the minimum version for CMake
cmake_minimum_required(VERSION 2.8)

# Project's name
project(hello)

# Set the output folder where your program will be created
set(CMAKE_BINARY_DIR ${CMAKE_SOURCE_DIR}/bin)
set(EXECUTABLE_OUTPUT_PATH ${CMAKE_BINARY_DIR})
set(LIBRARY_OUTPUT_PATH ${CMAKE_BINARY_DIR}/lib)


# The following folder will be included
include_directories("${PROJECT_SOURCE_DIR}")

# Add executable hello compiled from test
add_executable(hello test.cpp)

```

Execute it with ```cmake .``` and then call ```make```. If make doesn't work for some reason make sure you have installed the following: ```sudo apt-get install gcc-multilib```

### 2 Example with multiple directories
```cmake
cmake_minimum_required(VERSION 2.8.9)
project(directory_test)

#Bring the headers, such as Student.h into the project
include_directories(include)

#Can manually add the sources using the set command as follows:
#set(SOURCES src/mainapp.cpp src/Student.cpp)

#However, the file(GLOB...) allows for wildcard additions:
file(GLOB SOURCES "src/*.cpp")

add_executable(testStudent ${SOURCES})
```

Then we execute the program with the following (slightly different) steps:
- ```mkdir build```
- ```cd build```
- ```cmake ..```
- ```make```
- ```./testStudent```

### 3 Example for shared libraries
```cmake
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
```

The CMakeLists.txt file also includes a deployment step, which allows you to install the library in a suitable accessible location. Shared library locations can be added to the path, or if you wish to make them available system wide you can add them to the /usr/lib directory. For example, the libtestStudent.so library can be installed system wide using:
- ```sudo make install```

### 4 Example for static library
Almost the same, except now we use ```add_library(testStudent STATIC ${SOURCES})```

### 5 Example for using a static / shared library
```
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
```  

### 6 Cpp11 configuration with CMake
```
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
```

TODO:
- https://cognitivewaves.wordpress.com/cmake-and-visual-studio/
- https://cmake.org/cmake-tutorial/

Sources:  
- https://tuannguyen68.gitbooks.io/learning-cmake-a-beginner-s-guide/content/chap1/chap1.html
- http://derekmolloy.ie/hello-world-introductions-to-cmake/
