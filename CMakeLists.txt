cmake_minimum_required(VERSION 3.10)
project(TouristGuide C)

set(CMAKE_C_STANDARD 11)

# Output compiled executable directly into bin/
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/bin)

add_executable(TouristGuide src/main/c/main.c)
