package com.kyuhlee.hookmethod;

/**
 * Created by kyuhlee on 2018-02-19.
 */
import java.lang.reflect.Method;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookMethod implements IXposedHookLoadPackage {
    private void hookAllMethod(Class<?> c) throws Throwable {
        //XposedBridge.log("KYU: HookClass: " + c.getName());
        for (Method method : c.getMethods()) {
            //XposedBridge.log("DeclardMethod: " + method.getName());
            //if(method.getName().equals("loadUrl"))
            if(!method.getName().equals("equals")
                    && !method.getName().equals("wait")
                    && !method.getName().equals("getClass")
                    && !method.getName().equals("onDraw")
                    && !method.getName().equals("computeScroll")
                    && !method.getName().equals("hashCode")

                    )
                hookMethod(method);
        }
    }

    private void hookMethod(final Method m) throws Throwable {
        //XposedBridge.log("KYU: try hook:" + m.getName());
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            /*@Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("KYU: Before " + m.getName());
            }*/

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String s;
                s = "KYU: " + m.getName() + "(";
                if(param.args != null) {
                    for (Object obj : param.args) {
                        if (obj != null) s += obj.toString() + ", ";
                        else s += "NULL, ";
                    }
                }
                s += ")";
                if(param.getResult() != null) s += ", ret:" + param.getResult().toString();
                else s += "ret: NULL";
                XposedBridge.log(s);
                //Log.i("KYU", s);
            }
        });
    }

    private void hookMethodByName(Class<?> c, final String methodName) throws Throwable {
        Method m = XposedHelpers.findMethodBestMatch(c, methodName, String.class);
        hookMethod(m);
    }

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        //XposedBridge.log("Loaded app: " + lpparam.packageName);
        if (!lpparam.packageName.equals("com.android.webview")) return;

        XposedBridge.log("KYU: we are in System Webview!");
        Class<?> c = XposedHelpers.findClass("org.chromium.android_webview.AwContents", lpparam.classLoader);
        //hookMethodByName(c, "loadUrl");
        hookAllMethod(c);
    }
}
