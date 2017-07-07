# Setting up the frameworks

## Google Test
### Getting the Framework (Linux)
- ```git clone https://github.com/google/googletest```
- ```mkdir build```
- ```cd build```
- `cmake -Dgtest_build_samples=ON ..`
- `sudo cp -r ../googletest/include/gtest /usr/local/include`
- `sudo cp googlemock/lib*.a /usr/local/lib`
### Adding it as include (Linux - CMAKE)
Add the following lines to your CMakeLists:
- ```find_package(GTest REQUIRED)```
- Add it to the list of include directories: ```include_directories(${GTEST_INCLUDE_DIR})```
- Include GTest to your file where you want to use it })```#include "gtest/gtest.h"})```

### Setting up on Windows for VS
- Tools --> Nuget Package Manager --> Package Manager Console
- ```Install-Package googletest.v140.windesktop.static.rt-dyn -Version 1.7.0.1```
- (The official Nuget Package downloaded by the Nuget Package Manager didn't work out...)
