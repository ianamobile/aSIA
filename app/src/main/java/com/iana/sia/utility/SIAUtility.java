package com.iana.sia.utility;

import android.content.SharedPreferences;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.util.Patterns;

import com.google.gson.Gson;
import com.iana.sia.model.FieldInfo;
import com.iana.sia.model.InterchangeRequests;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Saumil on 5/16/2018.
 */

public class SIAUtility {

    public static final Pattern alphaNumericPattern = Pattern.compile("^[a-zA-Z0-9]*$");
    public static final Pattern alphaPattern = Pattern.compile("^[a-zA-Z]*$");
    public static final Pattern numericPattern = Pattern.compile("^[0-9]*$");

    // Method for disabling ShiftMode of BottomNavigationView
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    public static String replaceWhiteSpaces(String input) {
        return input.replaceAll("\\s+", "%20");
    }

    public static boolean isAlphaNumeric(String str) {
        if (null == str || str.trim().length() <= 0) {
            return false;
        }

        return alphaNumericPattern.matcher(str).matches();
    }

    public static boolean isAlpha(String str) {
        if (null == str || str.trim().length() <= 0) {
            return false;
        }

        return alphaPattern.matcher(str).matches();
    }

    public static boolean isNumeric(String str) {
        if (null == str || str.trim().length() <= 0) {
            return false;
        }

        return numericPattern.matcher(str).matches();
    }

    public static boolean isValidSTContNum(String str) {

        if (null == str || str.toString().trim().length() <= 0) {
            return false;
        }
        if (str.length() > 4) {
            if (!isAlpha(str.substring(0, 4)) || !isNumeric(str.substring(4))) {
                return false;
            }

        } else {
            if (!isAlpha(str)) {
                return false;
            }
        }
        return true;
    }

    public static <T> void setList(SharedPreferences.Editor editor, String key, List<T> list) {
        Gson gson = new Gson();
        editor.putString(key, gson.toJson(list));
    }

    public static <T> void setObject(SharedPreferences.Editor editor, String key, T obj) {
        Gson gson = new Gson();
        editor.putString(key, gson.toJson(obj));
    }

    public static <T> T getObjectOfModel(SharedPreferences sharedPref, String string, Class<T> obj) {
        Gson gson = new Gson();
        return (gson.fromJson(sharedPref.getString(string, ""), obj));
    }

    public static List<FieldInfo> prepareAndGetFieldInfoList(int[] categories, String[] categoriesName,
                                                             String[] labelArray, String[] valueArray) {

            List<FieldInfo> fieldInfoList = new ArrayList<>();
            int counter = 0;
            int innerLoopStart = 0;

            for(int i = 0;i<categories.length;i++)
            {
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setTitle(GlobalVariables.FIELD_INFO_EMPTY);
                fieldInfo.setValue(categoriesName[i]);
                fieldInfoList.add(fieldInfo);
                counter = counter + categories[i];
                for (int j = innerLoopStart; j < counter; j++) {
                    fieldInfo = new FieldInfo();
                    fieldInfo.setTitle(GlobalVariables.FIELD_INFO_TITLE);
                    fieldInfo.setValue(labelArray[j]);
                    fieldInfoList.add(fieldInfo);

                    fieldInfo = new FieldInfo();
                    fieldInfo.setTitle(GlobalVariables.FIELD_INFO_VALUE);
                    fieldInfo.setValue(valueArray[j]);
                    fieldInfoList.add(fieldInfo);
                }
                innerLoopStart = innerLoopStart + categories[i];

                if ((i + 1) < categories.length) {
                    fieldInfo = new FieldInfo();
                    fieldInfo.setTitle(GlobalVariables.FIELD_INFO_BLANK);
                    fieldInfoList.add(fieldInfo);
                }
            }
        return fieldInfoList;
    }

    public static boolean isValidEmail(String target) {
        return (null != target && target.trim().toString().length() > 0 && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
