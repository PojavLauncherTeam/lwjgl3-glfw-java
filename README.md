**Warning**: this repository is outdated, the latest source code is currently available at [PojavLauncher repo, jre_lwjgl3glfw directory](https://github.com/PojavLauncherTeam/PojavLauncher/tree/v3_openjdk/jre_lwjgl3glfw).

# lwjgl3-glfw-java
LWJGL3 GLFW stub java code for Android

## About this project
- This project stub and implement GLFW commands to enough run Minecraft 1.13+

## Some required properties
- `glfwstub.windowWidth`: set width for window.
- `glfwstub.windowHeight`: set height for window.
- `glfwstub.initEgl`: Will initialize EGL or not, that get following properties:
 + `glfwstub.eglContext`
 + `glfwstub.eglDisplay`
 + `glfwstub.eglSurfaceRead`
 + `glfwstub.eglSurfaceDraw`

## Optional properties
- `glfwstub.debugInput`: enable or disable input logging.


