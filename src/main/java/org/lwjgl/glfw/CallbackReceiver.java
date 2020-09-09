package org.lwjgl.glfw;

public class CallbackReceiver {
    public static final int TYPE_CURSOR_POS = 0;
    public static final int TYPE_CURSOR_BUTTON = 1;
    public static final int TYPE_KEYCODE_CONTROL = 2;
    public static final int TYPE_KEYCODE_CHAR = 3;
    public static final int TYPE_MOUSE_KEYCODE_CONTROL = 4;
    public static final int TYPE_WINDOW_SIZE = 5;
    
    public static boolean isCursorEntered = false;
    
	// Called from Android side
	public static void receiveCallback(int type, String data) {
        System.out.println("LWJFL GLFW Callback received type=" + Integer.toString(type) + ", data=" + data);
        
        String[] dataArr = data.split(":");
		switch (type) {
            case TYPE_CURSOR_POS:
                if (GLFW.mGLFWCursorEnterCallback != null && !isCursorEntered) {
                    isCursorEntered = true;
                    GLFW.mGLFWCursorEnterCallback.invoke(1l, true);
                }
                if (GLFW.mGLFWCursorPosCallback != null)
                    GLFW.mGLFWCursorPosCallback.invoke(1l, Double.parseDouble(dataArr[0]), Double.parseDouble(dataArr[1]));
                break;
            case TYPE_KEYCODE_CONTROL:
                // TODO add scancode, mods impl
                if (GLFW.mGLFWKeyCallback != null)
                    GLFW.mGLFWKeyCallback.invoke(1l, Integer.parseInt(dataArr[0]), 0, Boolean.parseBoolean(dataArr[1]) ? 1 : 0, 0);
                break;
            case TYPE_MOUSE_KEYCODE_CONTROL:
                // TODO add mods impl
                if (GLFW.mGLFWMouseButtonCallback != null)
                    GLFW.mGLFWMouseButtonCallback.invoke(1l, Integer.parseInt(dataArr[0]), Boolean.parseBoolean(dataArr[1]) ? 1 : 0, 0);
                break;
            case TYPE_WINDOW_SIZE:
                if (GLFW.mGLFWWindowSizeCallback != null)
                    GLFW.mGLFWWindowSizeCallback.invoke(1l, Integer.parseInt(dataArr[0]), Integer.parseInt(dataArr[1]));
                break;
        }
	}
}

