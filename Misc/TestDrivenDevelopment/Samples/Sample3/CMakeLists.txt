cmake_minimum_required(VERSION 2.8.9)
set(PROJECT_NAME_STR sample3)
project(${PROJECT_NAME_STR})

find_package(Threads REQUIRED)

if(CMAKE_COMPILER_IS_GNUCXX)
    add_definitions(-std=c++11 -Wall -ansi -Wno-deprecated -pthread)
endif()

#-------------------
# Tests
# --> Download + Build GTest
# --> Link GTest to TestProject
#-------------------

#Download and Build GTest --> adds "googletest" as external project
add_subdirectory(${PROJECT_SOURCE_DIR}/ext/gtest)

set(PROJECT_TEST_NAME ${PROJECT_NAME_STR}_test)

include_directories(${GTEST_INCLUDE_DIRS}
                    ${PROJECT_SOURCE_DIR})

set(TEST_SRC_FILES
    ${PROJECT_SOURCE_DIR}/sample3_unittest.cc
  )

# Create Test Executable
add_executable(${PROJECT_TEST_NAME} ${TEST_SRC_FILES})

# Add Dependency --> make sure that gtest + our projects are already built
add_dependencies(${PROJECT_TEST_NAME} googletest ${PROJECT_NAME_STR})

# Link the gtest libs to our test project
target_link_libraries(${PROJECT_TEST_NAME}
        ${GTEST_LIBS_DIR}/libgtest.a
        ${GTEST_LIBS_DIR}/libgtest_main.a
        )
target_link_libraries(${PROJECT_TEST_NAME} ${CMAKE_THREAD_LIBS_INIT})
