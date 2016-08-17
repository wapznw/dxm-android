package com.tianku.client.mao;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class Conf {
    public static final String API_HOST = "http://192.168.1.102:88";
    //public static final String API_HOST="http:/sms.50r.cn";

    /**
     * 读取数据
     *
     * @param act
     * @param key
     * @return
     */
    static public Object get(Activity act, String key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(act);
        try {
            Object msg;
            try {
                msg = settings.getString(key, null);
            } catch (Exception e) {
                try {
                    msg = settings.getBoolean(key, false);
                } catch (Exception e1) {
                    msg = settings.getInt(key, 0);
                }
            }
            return msg;
        } catch (Exception ex) {
            Toast.makeText(act.getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /**
     * 写入数据
     *
     * @param act
     * @param name
     * @param value
     * @return
     */
    static public boolean set(Activity act, String name, String value) {
        SharedPreferences MyPreferences = PreferenceManager.getDefaultSharedPreferences(act);
        SharedPreferences.Editor editor = MyPreferences.edit();
        try {
            editor.putString(name, value);
            editor.commit();
            return true;
        } catch (Exception ex) {
            Toast.makeText(act.getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            return false;
        }

    }

    /**
     * 写入数据
     *
     * @param act
     * @param name
     * @param value
     * @return
     */
    static public boolean set(Activity act, String name, boolean value) {
        SharedPreferences MyPreferences = PreferenceManager.getDefaultSharedPreferences(act);
        SharedPreferences.Editor editor = MyPreferences.edit();
        try {
            editor.putBoolean(name, value);
            editor.commit();
            return true;
        } catch (Exception ex) {
            Toast.makeText(act.getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            return false;
        }

    }

    /**
     * 写入数据
     *
     * @param act
     * @param name
     * @param value
     * @return
     */
    static public boolean set(Activity act, String name, int value) {
        SharedPreferences MyPreferences = PreferenceManager.getDefaultSharedPreferences(act);
        SharedPreferences.Editor editor = MyPreferences.edit();
        try {
            editor.putInt(name, value);
            editor.commit();
            return true;
        } catch (Exception ex) {
            Toast.makeText(act.getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            return false;
        }

    }

    /**
     * 读取数据
     *
     * @param context
     * @param key
     * @return
     */
    static public Object get(Context context, String key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            Object msg;
            try {
                msg = settings.getString(key, null);
            } catch (Exception e) {
                try {
                    msg = settings.getBoolean(key, false);
                } catch (Exception e1) {
                    msg = settings.getInt(key, 0);
                }
            }
            return msg;
        } catch (Exception ex) {
            Toast.makeText(context.getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /**
     * 写入数据
     *
     * @param context
     * @param name
     * @param value
     * @return
     */
    static public boolean set(Context context, String name, String value) {
        SharedPreferences MyPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = MyPreferences.edit();
        try {
            editor.putString(name, value);
            editor.commit();
            return true;
        } catch (Exception ex) {
            Toast.makeText(context.getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            return false;
        }

    }

    /**
     * 写入数据
     *
     * @param context
     * @param name
     * @param value
     * @return
     */
    static public boolean set(Context context, String name, boolean value) {
        SharedPreferences MyPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = MyPreferences.edit();
        try {
            editor.putBoolean(name, value);
            editor.commit();
            return true;
        } catch (Exception ex) {
            Toast.makeText(context.getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            return false;
        }

    }


    /**
     * 写入数据
     *
     * @param context
     * @param name
     * @param value
     * @return
     */
    static public boolean set(Context context, String name, int value) {
        SharedPreferences MyPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = MyPreferences.edit();
        try {
            editor.putInt(name, value);
            editor.commit();
            return true;
        } catch (Exception ex) {
            Toast.makeText(context.getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
            return false;
        }

    }

    /**
     * 获取API主机
     *
     * @param context
     * @return
     */
    public static String Api_Host(Context context) {
        return Conf.get(context, "api_url").toString();
    }

    /**
     * 计算流量
     *
     * @param i
     * @return
     */
    public static String JsLiuLiang(int i) {
        float liuliang = (float) i;
        if (liuliang < 1024) {
            return liuliang + "Byte";
        } else if (liuliang > 1024 && liuliang < 12040) {
            return convert(liuliang / 1024) + "Kb";
        } else if (liuliang > 10240) {
            return (liuliang / 10240) + "Mb";
        } else {
            return (liuliang / 10240) + "Mb";
        }
    }

    /**
     * 取小数点后两位
     *
     * @param value
     * @return
     */
    static double convert(double value) {
        long l1 = Math.round(value * 100); // 四舍五入
        double ret = l1 / 100.0; // 注意：使用 100.0 而不是 100
        return ret;
    }
}
