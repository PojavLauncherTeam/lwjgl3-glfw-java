package org.lwjgl.glfw;
import java.io.*;
import java.util.*;

public class CallbackBridge {
    public static final int JRE_TYPE_CURSOR_POS = 0;
    public static final int JRE_TYPE_CURSOR_BUTTON = 1;
    public static final int JRE_TYPE_KEYCODE_CONTROL = 2;
    public static final int JRE_TYPE_KEYCODE_CHAR = 3;
    public static final int JRE_TYPE_MOUSE_KEYCODE_CONTROL = 4;
    public static final int JRE_TYPE_WINDOW_SIZE = 5;
    public static final int JRE_TYPE_GRAB_INITIAL_POS_UNSET = 6;
    
    public static final int ANDROID_TYPE_GRAB_STATE = 0;

    // Should pending events be limited?
    volatile public static List<String> PENDING_EVENT_LIST = new ArrayList<>(20);
    volatile public static boolean PENDING_EVENT_READY = false;
    
    public static final boolean INPUT_DEBUG_ENABLED;
    
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
    
    public static void sendGrabbing(boolean grab) {
        // sendData(ANDROID_TYPE_GRAB_STATE, Boolean.toString(grab));
        
        GLFW.mGLFWIsGrabbing = grab;
        nativeSetGrabbing(grab);
    }
    
	// Called from Android side
	public static void receiveCallback(int type, String data) {
/*
        if (INPUT_DEBUG_ENABLED) {
            System.out.println("LWJGL GLFW Callback received type=" + Integer.toString(type) + ", data=" + data);
        }
*/
        
        if (PENDING_EVENT_READY) {
            if (type == JRE_TYPE_CURSOR_POS) {
                String[] dataArr = data.split(":");
                GLFW.mGLFWCursorX = Double.parseDouble(dataArr[0]);
                GLFW.mGLFWCursorY = Double.parseDouble(dataArr[1]);
                
                // System.out.println("Receive callback: x=" + GLFW.mGLFWCursorX + ", y=" + GLFW.mGLFWCursorY);
            } else {
                PENDING_EVENT_LIST.add(type + ":" + data);
            }
        } // else System.out.println("Event input is not ready yet!");
	}
    
    public static void sendData(int type, String data) {
        nativeSendData(false, type, data);
    }
    
    public static native void nativeSendData(boolean isAndroid, int type, String data);
    private static native void nativeSetGrabbing(boolean grab);
}

