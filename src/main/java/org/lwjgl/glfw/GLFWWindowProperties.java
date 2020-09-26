package org.lwjgl.glfw;

import java.util.*;

public class GLFWWindowProperties {
    public int width = GLFW.mGLFWWindowWidth;
    public int height = GLFW.mGLFWWindowHeight;
    public int x, y;
    public CharSequence title;
    public boolean shouldClose, isFramebufferSizeCalled, isWindowSizeCalled;
    public Map<Integer, Integer> inputModes = new HashMap<>();
}