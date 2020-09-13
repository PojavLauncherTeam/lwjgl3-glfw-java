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
    
	// Called from Android side
	public static void receiveCallback(int type, String data) {
/*
        if (INPUT_DEBUG_ENABLED) {
            System.out.println("LWJGL GLFW Callback received type=" + Integer.toString(type) + ", data=" + data);
        }
*/
        
        if (PENDING_EVENT_READY) {
            if (type == TYPE_CURSOR_POS) {
                String[] dataArr = data.split(":");
                GLFW.mGLFWCursorPos[0] = Double.parseDouble(dataArr[0]);
                GLFW.mGLFWCursorPos[1] = Double.parseDouble(dataArr[1]);
            } else {
                PENDING_EVENT_LIST.add(type + ":" + data);
            }
        }
	}
}

