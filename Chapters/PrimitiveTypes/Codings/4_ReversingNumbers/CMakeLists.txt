cmake_minimum_required(VERSION 3.1)
set(PROJECT_NAME_STR reversingBits)
project(${PROJECT_NAME_STR})

find_package(Threads REQUIRED)

if(CMAKE_COMPILER_IS_GNUCXX)
  set(CMAKE_CXX_STANDARD 14)
  set(CMAKE_CXX_STANDARD_REQUIRED ON)
  set(CMAKE_CXX_EXTENS./rIONS OFF)
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
