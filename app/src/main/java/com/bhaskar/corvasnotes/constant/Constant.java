package com.bhaskar.corvasnotes.constant;

import java.io.Serializable;

public class Constant implements Serializable {
    private static final long serialVersionUID = 1L;

    public static String company = "Corvas";
    public static String email = "info@corvasnotes.com";
    public static String website = "https://www.corvas.com/";
    public static String contact = "+91 6290594650";
    public static String privacyPolicy = "http://example.com/";

//    Sketch
    public static final int PAINT_STYLE_STROKE = 1;
    public static final int PAINT_STYLE_FILL = 2;
    public static final int OPERATION_NO_OPERATION = -1;
    public static final int OPERATION_DRAW_PENCIL = 0;
    public static final int OPERATION_ERASE = 1;
    public static final int OPERATION_CHOOSE_COLOR = 2;
    public static final int OPERATION_CLEAR_CANVAS = 3;
//    public static final int OPERATION_INSERT_TEXT = 4;
    public static final int OPERATION_UNDO = 4;
    public static final int OPERATION_REDO = 5;
//    public static final int OPERATION_DRAW_CIRCLE = 7;
//    public static final int OPERATION_DRAW_RECTANGLE = 8;
//    public static final int OPERATION_DRAW_OVAL = 9;
//    public static final int OPERATION_SET_BACKGROUND = 6;
    public static final int OPERATION_SAVE_IMAGE = 6;
    public static final int OPERATION_SAVE_IMAGE_TO_SERVER = 11;
    public static final int OPERATION_MOVE_VIEW = 20;
    public static final int OPERATION_FILL_VIEW = 21;
    public static final String UPLOAD_URL = "http://google.com";
    public static final int RESULT_LOAD_IMAGE = 100;
}