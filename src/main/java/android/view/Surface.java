/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.view;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import android.graphics.*;

/**
 * Handle onto a raw buffer that is being managed by the screen compositor.
 */
public class Surface {
    private static final String TAG = "Surface";

    private static native long nativeCreateFromSurfaceTexture(SurfaceTexture surfaceTexture)
            throws OutOfResourcesException;
    private static native long nativeCreateFromSurfaceControl(long surfaceControlNativeObject);

    private static native void nativeRelease(long nativeObject);
    private static native boolean nativeIsValid(long nativeObject);
    private static native boolean nativeIsConsumerRunningBehind(long nativeObject);
	
    private static native void nativeAllocateBuffers(long nativeObject);

    // Guarded state.
    final Object mLock = new Object(); // protects the native state
    private String mName;
    long mNativeObject; // package scope only for SurfaceControl access
    private long mLockedObject;
    private int mGenerationId; // incremented each time mNativeObject changes
    
    @Retention(RetentionPolicy.SOURCE)
    public @interface Rotation {}

    /**
     * Rotation constant: 0 degree rotation (natural orientation)
     */
    public static final int ROTATION_0 = 0;

    /**
     * Rotation constant: 90 degree rotation.
     */
    public static final int ROTATION_90 = 1;

    /**
     * Rotation constant: 180 degree rotation.
     */
    public static final int ROTATION_180 = 2;

    /**
     * Rotation constant: 270 degree rotation.
     */
    public static final int ROTATION_270 = 3;

    /**
     * Create an empty surface, which will later be filled in by readFromParcel().
     * @hide
     */
    public Surface() {
    }

    /**
     * Create Surface from a {@link SurfaceTexture}.
     *
     * Images drawn to the Surface will be made available to the {@link
     * SurfaceTexture}, which can attach them to an OpenGL ES texture via {@link
     * SurfaceTexture#updateTexImage}.
     *
     * @param surfaceTexture The {@link SurfaceTexture} that is updated by this
     * Surface.
     * @throws OutOfResourcesException if the surface could not be created.
     */
    public Surface(SurfaceTexture surfaceTexture) {
        if (surfaceTexture == null) {
            throw new IllegalArgumentException("surfaceTexture must not be null");
        }

        synchronized (mLock) {
            mName = surfaceTexture.toString();
            setNativeObjectLocked(nativeCreateFromSurfaceTexture(surfaceTexture));
        }
    }

    /* called from android_view_Surface_createFromIGraphicBufferProducer() */
    private Surface(long nativeObject) {
        synchronized (mLock) {
            setNativeObjectLocked(nativeObject);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            release();
        } finally {
            super.finalize();
        }
    }

    /**
     * Release the local reference to the server-side surface.
     * Always call release() when you're done with a Surface.
     * This will make the surface invalid.
     */
    public void release() {
        synchronized (mLock) {
            if (mNativeObject != 0) {
                nativeRelease(mNativeObject);
                setNativeObjectLocked(0);
            }
        }
    }

    /**
     * Free all server-side state associated with this surface and
     * release this object's reference.  This method can only be
     * called from the process that created the service.
     * @hide
     */
    public void destroy() {
        release();
    }

    /**
     * Returns true if this object holds a valid surface.
     *
     * @return True if it holds a physical surface, so lockCanvas() will succeed.
     * Otherwise returns false.
     */
    public boolean isValid() {
        synchronized (mLock) {
            if (mNativeObject == 0) return false;
            return nativeIsValid(mNativeObject);
        }
    }

    /**
     * Gets the generation number of this surface, incremented each time
     * the native surface contained within this object changes.
     *
     * @return The current generation number.
     * @hide
     */
    public int getGenerationId() {
        synchronized (mLock) {
            return mGenerationId;
        }
    }

    /**
     * Returns true if the consumer of this Surface is running behind the producer.
     *
     * @return True if the consumer is more than one buffer ahead of the producer.
     * @hide
     */
    public boolean isConsumerRunningBehind() {
        synchronized (mLock) {
            checkNotReleasedLocked();
            return nativeIsConsumerRunningBehind(mNativeObject);
        }
    }

    /**
     * This is intended to be used by {@link SurfaceView#updateWindow} only.
     * @param other access is not thread safe
     * @hide
     * @deprecated
     */
    @Deprecated
    public void transferFrom(Surface other) {
        if (other == null) {
            throw new IllegalArgumentException("other must not be null");
        }
        if (other != this) {
            final long newPtr;
            synchronized (other.mLock) {
                newPtr = other.mNativeObject;
                other.setNativeObjectLocked(0);
            }

            synchronized (mLock) {
                if (mNativeObject != 0) {
                    nativeRelease(mNativeObject);
                }
                setNativeObjectLocked(newPtr);
            }
        }
    }

    // @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        synchronized (mLock) {
            return "Surface(name=" + mName + ")/@0x" +
                    Integer.toHexString(System.identityHashCode(this));
        }
    }

    private void setNativeObjectLocked(long ptr) {
        if (mNativeObject != ptr) {
			/*
            if (mNativeObject == 0 && ptr != 0) {
                mCloseGuard.open("release");
            } else if (mNativeObject != 0 && ptr == 0) {
                mCloseGuard.close();
            }
			*/
            mNativeObject = ptr;
            mGenerationId += 1;
        }
    }

    private void checkNotReleasedLocked() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Surface has already been released.");
        }
    }

    /**
     * Allocate buffers ahead of time to avoid allocation delays during rendering
     * @hide
     */
    public void allocateBuffers() {
        synchronized (mLock) {
            checkNotReleasedLocked();
            nativeAllocateBuffers(mNativeObject);
        }
    }

    /**
     * Exception thrown when a Canvas couldn't be locked with {@link Surface#lockCanvas}, or
     * when a SurfaceTexture could not successfully be allocated.
     */
    @SuppressWarnings("serial")
    public static class OutOfResourcesException extends RuntimeException {
        public OutOfResourcesException() {
        }
        public OutOfResourcesException(String name) {
            super(name);
        }
    }

    /**
     * Returns a human readable representation of a rotation.
     *
     * @param rotation The rotation.
     * @return The rotation symbolic name.
     *
     * @hide
     */
    public static String rotationToString(int rotation) {
        switch (rotation) {
            case Surface.ROTATION_0: {
                return "ROTATION_0";
            }
            case Surface.ROTATION_90: {
                return "ROATATION_90";
            }
            case Surface.ROTATION_180: {
                return "ROATATION_180";
            }
            case Surface.ROTATION_270: {
                return "ROATATION_270";
            }
            default: {
                throw new IllegalArgumentException("Invalid rotation: " + rotation);
            }
        }
    }
}
