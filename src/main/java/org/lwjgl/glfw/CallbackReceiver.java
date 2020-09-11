package org.lwjgl.glfw;
import java.io.*;
import java.util.*;

public class CallbackReceiver {
    public static final int TYPE_CURSOR_POS = 0;
    public static final int TYPE_CURSOR_BUTTON = 1;
    public static final int TYPE_KEYCODE_CONTROL = 2;
    public static final int TYPE_KEYCODE_CHAR = 3;
    public static final int TYPE_MOUSE_KEYCODE_CONTROL = 4;
    public static final int TYPE_WINDOW_SIZE = 5;

    // Should pending events be limited?
    // volatile public static List<String> PENDING_EVENT_LIST = new ArrayList<>();
    volatile public static boolean PENDING_EVENT_READY = false;
    
    public static final boolean INPUT_DEBUG_ENABLED;
    
    private static boolean isCursorEntered;
    
    // TODO send grab state event to Androud
    
    static {
        INPUT_DEBUG_ENABLED = Boolean.parseBoolean(System.getProperty("glfwstub.debugInput", "false"));

        
/*
        if (isDebugEnabled) {
            //try {
                //debugEventStream = new PrintStream(new File(System.getProperty("user.dir"), "glfwstub_inputeventlog.txt"));
		    debugEventStream = System.out;
            //} catch (FileNotFoundException e) {
            //    e.printStackTrace();
            //}
        }
	
	    //Quick and dirty: debul all key inputs to System.out
*/
    }
    
	// Called from Android side
	public static void receiveCallback(int type, String data) {
/*
        if (INPUT_DEBUG_ENABLED) {
            System.out.println("LWJGL GLFW Callback received type=" + Integer.toString(type) + ", data=" + data);
        }
*/
        
        if (PENDING_EVENT_READY) {
            // PENDING_EVENT_LIST.add(type + ":" + data);
            
            // Direct event
            executeEvent(type, data);
        }
	}
    
    public static void executeEvent(int type, String data) {
        String[] dataArr = data.split(":");
        switch (type) {
            case CallbackReceiver.TYPE_CURSOR_POS:
                if (GLFW.mGLFWCursorEnterCallback != null && !isCursorEntered) {
                    isCursorEntered = true;
                    GLFW.mGLFWCursorEnterCallback.invoke(1l, true);
                }
                if (GLFW.mGLFWCursorPosCallback != null)
                    GLFW.mGLFWCursorPos[0] = Double.parseDouble(dataArr[0]);
                GLFW.mGLFWCursorPos[1] = Double.parseDouble(dataArr[1]);
                GLFW.mGLFWCursorPosCallback.invoke(1l, GLFW.mGLFWCursorPos[0], GLFW.mGLFWCursorPos[1]);
                break;
            case CallbackReceiver.TYPE_KEYCODE_CONTROL:
                // TODO add scancode, mods impl
                if (GLFW.mGLFWKeyCallback != null)
                    GLFW.mGLFWKeyCallback.invoke(1l, Integer.parseInt(dataArr[0]), 0, Boolean.parseBoolean(dataArr[1]) ? 1 : 0, 0);
                break;
            case CallbackReceiver.TYPE_MOUSE_KEYCODE_CONTROL:
                // TODO add mods impl
                if (GLFW.mGLFWMouseButtonCallback != null)
                    GLFW.mGLFWMouseButtonCallback.invoke(1l, Integer.parseInt(dataArr[0]), Boolean.parseBoolean(dataArr[1]) ? 1 : 0, 0);
                break;
            case CallbackReceiver.TYPE_WINDOW_SIZE:
                if (GLFW.mGLFWWindowSizeCallback != null)
                    GLFW.mGLFWWindowSizeCallback.invoke(1l, Integer.parseInt(dataArr[0]), Integer.parseInt(dataArr[1]));
                break;
            default:
                System.err.println("GLFWEvent: unknown callback type " + type);
                break;
        }
    }
}

