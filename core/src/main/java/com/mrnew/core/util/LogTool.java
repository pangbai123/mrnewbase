package com.mrnew.core.util;

import android.util.Log;

/**
 * 日志二次封装
 */
public class LogTool {

    public static final String TAG = "base";
    /**
     * 在mmconfig中进行配置
     */
    public static boolean DEBUG = true;

    /**
     * Send a VERBOSE log message.
     *
     * @param msg The message you would like logged.
     */
    public static void v(String msg) {
        if (DEBUG) {
            String sb = buildMessage(msg);
            if (sb.length() > 3000) {
                Log.v(TAG, "sb.length = " + sb.length());
                int chunkCount = sb.length() / 3000;     // integer division
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 3000 * (i + 1);
                    if (max >= sb.length()) {
                        Log.v(TAG, sb.substring(3000 * i));
                    } else {
                        Log.v(TAG, sb.substring(3000 * i, max));
                    }
                }
            } else {
                Log.v(TAG, sb.toString());
            }
        }
    }

    /**
     * Send a VERBOSE log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void v(String msg, Throwable thr) {
        if (DEBUG) {
            String sb = buildMessage(msg);
            if (sb.length() > 3000) {
                Log.v(TAG, "sb.length = " + sb.length());
                int chunkCount = sb.length() / 3000;     // integer division
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 3000 * (i + 1);
                    if (max >= sb.length()) {
                        Log.v(TAG, sb.substring(3000 * i), thr);
                    } else {
                        Log.v(TAG, sb.substring(3000 * i, max));
                    }
                }
            } else {
                Log.v(TAG, sb.toString(), thr);
            }
        }
    }

    /**
     * Send a DEBUG log message.
     *
     * @param msg
     */
    public static void d(String msg) {
        if (DEBUG) {
            String sb = buildMessage(msg);
            if (sb.length() > 3000) {
                Log.v(TAG, "sb.length = " + sb.length());
                int chunkCount = sb.length() / 3000;     // integer division
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 3000 * (i + 1);
                    if (max >= sb.length()) {
                        Log.d(TAG, sb.substring(3000 * i));
                    } else {
                        Log.d(TAG, sb.substring(3000 * i, max));
                    }
                }
            } else {
                Log.d(TAG, sb.toString());
            }
        }
    }

    /**
     * Send a DEBUG log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void d(String msg, Throwable thr) {
        if (DEBUG) {
            String sb = buildMessage(msg);
            if (sb.length() > 3000) {
                Log.v(TAG, "sb.length = " + sb.length());
                int chunkCount = sb.length() / 3000;     // integer division
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 3000 * (i + 1);
                    if (max >= sb.length()) {
                        Log.d(TAG, sb.substring(3000 * i), thr);
                    } else {
                        Log.d(TAG, sb.substring(3000 * i, max));
                    }
                }
            } else {
                Log.d(TAG, sb.toString(), thr);
            }
        }
    }

    /**
     * Send an INFO log message.
     *
     * @param msg The message you would like logged.
     */
    public static void i(String msg) {
        if (DEBUG) {
            String sb = buildMessage(msg);
            if (sb.length() > 3000) {
                Log.v(TAG, "sb.length = " + sb.length());
                int chunkCount = sb.length() / 3000;     // integer division
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 3000 * (i + 1);
                    if (max >= sb.length()) {
                        Log.i(TAG, sb.substring(3000 * i));
                    } else {
                        Log.i(TAG, sb.substring(3000 * i, max));
                    }
                }
            } else {
                Log.i(TAG, sb.toString());
            }
        }
    }

    /**
     * Send a INFO log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void i(String msg, Throwable thr) {
        if (DEBUG) {
            String sb = buildMessage(msg);
            if (sb.length() > 3000) {
                Log.v(TAG, "sb.length = " + sb.length());
                int chunkCount = sb.length() / 3000;     // integer division
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 3000 * (i + 1);
                    if (max >= sb.length()) {
                        Log.i(TAG, sb.substring(3000 * i), thr);
                    } else {
                        Log.i(TAG, sb.substring(3000 * i, max));
                    }
                }
            } else {
                Log.i(TAG, sb.toString(), thr);
            }
        }
    }

    /**
     * Send an ERROR log message.
     *
     * @param msg The message you would like logged.
     */
    public static void e(String msg) {
        if (DEBUG) {
            String sb = buildMessage(msg);
            if (sb.length() > 3000) {
                Log.v(TAG, "sb.length = " + sb.length());
                int chunkCount = sb.length() / 3000;     // integer division
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 3000 * (i + 1);
                    if (max >= sb.length()) {
                        Log.e(TAG, sb.substring(3000 * i));
                    } else {
                        Log.e(TAG, sb.substring(3000 * i, max));
                    }
                }
            } else {
                Log.e(TAG, sb.toString());
            }
        }
    }

    /**
     * Send a WARN log message
     *
     * @param msg The message you would like logged.
     */
    public static void w(String msg) {
        if (DEBUG) {
            String sb = buildMessage(msg);
            if (sb.length() > 3000) {
                Log.v(TAG, "sb.length = " + sb.length());
                int chunkCount = sb.length() / 3000;     // integer division
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 3000 * (i + 1);
                    if (max >= sb.length()) {
                        Log.w(TAG, sb.substring(3000 * i));
                    } else {
                        Log.w(TAG, sb.substring(3000 * i, max));
                    }
                }
            } else {
                Log.w(TAG, sb.toString());
            }
        }
    }

    /**
     * Send a WARN log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void w(String msg, Throwable thr) {
        if (DEBUG) {
            String sb = buildMessage(msg);
            if (sb.length() > 3000) {
                Log.v(TAG, "sb.length = " + sb.length());
                int chunkCount = sb.length() / 3000;     // integer division
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 3000 * (i + 1);
                    if (max >= sb.length()) {
                        Log.w(TAG, sb.substring(3000 * i), thr);
                    } else {
                        Log.w(TAG, sb.substring(3000 * i, max));
                    }
                }
            } else {
                Log.w(TAG, sb.toString(), thr);
            }
        }
    }

    /**
     * Send an empty WARN log message and log the exception.
     *
     * @param thr An exception to log
     */
    public static void w(Throwable thr) {
        if (DEBUG)
            Log.w(TAG, buildMessage(""), thr);
    }

    /**
     * Send an ERROR log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void e(String msg, Throwable thr) {
        if (DEBUG) {
            String sb = buildMessage(msg);
            if (sb.length() > 3000) {
                Log.v(TAG, "sb.length = " + sb.length());
                int chunkCount = sb.length() / 3000;     // integer division
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 3000 * (i + 1);
                    if (max >= sb.length()) {
                        Log.e(TAG, sb.substring(3000 * i), thr);
                    } else {
                        Log.e(TAG, sb.substring(3000 * i, max));
                    }
                }
            } else {
                Log.e(TAG, sb.toString(), thr);
            }
        }
    }

    /**
     * Building Message
     *
     * @param msg The message you would like logged.
     * @return Message String
     */
    protected static String buildMessage(String msg) {
        StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];

        return caller.getClassName() + "." + caller.getMethodName() + "(): " + msg;
    }
}
