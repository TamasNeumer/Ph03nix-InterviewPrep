cmake_minimum_required(VERSION 2.8.9)
set(PROJECT_NAME_STR bitCounter)
project(${PROJECT_NAME_STR})

find_package(Threads REQUIRED)

if(CMAKE_COMPILER_IS_GNUCXX)
    add_definitions(-std=c++14 -Wall -ansi -Wno-deprecated -pthread)
endif()

#-------------------
# Test
#-------------------
# Download and Build GTest
add_subdirectory(${PROJECT_SOURCE_DIR}/ext/gtest)

enable_testing()
set(PROJECT_TEST_NAME ${PROJECT_NAME_STR})

include_directories(${GTEST_INCLUDE_DIRS}
                    ${PROJECT_SOURCE_DIR})


file(GLOB TEST_SRC_FILES ${PROJECT_SOURCE_DIR}/*.cpp)

add_executable(${PROJECT_TEST_NAME} ${TEST_SRC_FILES})
add_dependencies(${PROJECT_TEST_NAME} googletest)

target_link_libraries(${PROJECT_TEST_NAME}
        ${GTEST_LIBS_DIR}/libgtest.a
        ${GTEST_LIBS_DIR}/libgtest_main.a
        )

target_link_libraries(${PROJECT_TEST_NAME} ${CMAKE_THREAD_LIBS_INIT})

add_test(test1 ${PROJECT_TEST_NAME})
