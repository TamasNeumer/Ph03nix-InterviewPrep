# Setting up the frameworks

## Google Test
### Getting the Framework (Linux)
```bash
git clone https://github.com/google/googletest
mkdir build
cd build
cmake -Dgtest_build_samples=ON ..
sudo cp -r ../googletest/include/gtest /usr/local/include
sudo cp googlemock/lib*.a /usr/local/lib
```
### Adding it as include (Linux - CMAKE)
Add the following lines to your CMakeLists:
- ```find_package(GTest REQUIRED)```
- Add it to the list of include directories: ```include_directories(${GTEST_INCLUDE_DIR})```
- Include GTest to your file where you want to use it.
  - ```#include "gtest/gtest.h"})```

### Setting up on Windows for VS
- Tools --> Nuget Package Manager --> Package Manager Console
- ```Install-Package googletest.v140.windesktop.static.rt-dyn -Version 1.7.0.1```
- (The official Nuget Package downloaded by the Nuget Package Manager didn't work out...)
- Now you can add the includes to your file.
- On visual studio you need to reference your "main" project to this TestProject. (References->Add Reference).
- Then you can include your main project's files by "../MainProjName/Class.h"

## Setting up main()
```cpp
int main(int argc, char* argv[]) {
  testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
```
## CI and Testing using GoogleTest + Circle CI
circle.yml:
```yml
dependencies:
  pre:
    - mkdir build; cd build; cmake ..; make;
test:
  override:
    - cd build; ./sampleProject_test;
```
# A Complete Build Example Using CMake and gtest
The main idea is the following:
- Create (main) project and add it's dependencies.
- (If we are building execuable) add executable.
- Build a library from main project. (It will be linked to the unit test project later.)
- Download and build GoogleTest.
- Create a new (test) project.
- Link GTest libraries and the (main) library to the test project.
- Create executable for test project, build and run.

```cmake
cmake_minimum_required(VERSION 2.8.9)
set(PROJECT_NAME_STR sample1)
project(${PROJECT_NAME_STR})

find_package(Threads REQUIRED)

if(CMAKE_COMPILER_IS_GNUCXX)
    add_definitions(-std=c++11 -Wall -ansi -Wno-deprecated -pthread)
endif()

#-------------------
# Module source --> Build executable and lib!
#-------------------
include_directories(${PROJECT_SOURCE_DIR}/Sample1Class)
set(SOURCE
    ${CMAKE_CURRENT_SOURCE_DIR}/Sample1Class/sample1.cc
  )
add_library(sample1 STATIC ${SOURCE})

set(SOURCE
    ${CMAKE_CURRENT_SOURCE_DIR}/main.cc
    ${CMAKE_CURRENT_SOURCE_DIR}/Sample1Class/sample1.cc
  )
add_executable(sample1Production ${SOURCE})

#-------------------
# Tests
# --> Download + Build GTest
# --> Link GTest + libsample1.a to TestProject
#-------------------

#Download and Build GTest --> adds "googletest" as external project
add_subdirectory(${PROJECT_SOURCE_DIR}/ext/gtest)

set(PROJECT_TEST_NAME ${PROJECT_NAME_STR}_test)

include_directories(${GTEST_INCLUDE_DIRS}
                    ${PROJECT_SOURCE_DIR}/Sample1Class)

set(TEST_SRC_FILES
    ${PROJECT_SOURCE_DIR}/UnitTests/sample1_unittest.cc
  )

# Create Test Executable
add_executable(${PROJECT_TEST_NAME} ${TEST_SRC_FILES})

# Add Dependency --> make sure that gtest + our projects are already built
add_dependencies(${PROJECT_TEST_NAME} googletest ${PROJECT_NAME_STR})

# Link the gtest libs to our test project
target_link_libraries(${PROJECT_TEST_NAME}
        ${GTEST_LIBS_DIR}/libgtest.a
        ${GTEST_LIBS_DIR}/libgtest_main.a
        ${PROJECT_BINARY_DIR}/libsample1.a
        )
target_link_libraries(${PROJECT_TEST_NAME} ${CMAKE_THREAD_LIBS_INIT})
```
