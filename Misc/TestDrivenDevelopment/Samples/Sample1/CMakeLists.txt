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
