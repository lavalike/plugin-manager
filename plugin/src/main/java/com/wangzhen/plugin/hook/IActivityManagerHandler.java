package com.wangzhen.plugin.hook;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.util.Pair;

import com.wangzhen.plugin.PluginManager;
import com.wangzhen.plugin.provider.ContextProvider;
import com.wangzhen.plugin.proxy.ProxyService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.wangzhen.plugin.hook.HookHelper.STUB_CLASS;

/**
 * invocation handler of IActivityManager
 * Created by wangzhen on 2020/4/18.
 */
class IActivityManagerHandler implements InvocationHandler {
    private static final String PACKAGE_MEIZU_PICKER = "com.meizu.picker";
    private Object mRaw;

    public IActivityManagerHandler(Object raw) {
        mRaw = raw;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.e("TAG", "-> invoke : " + method.getName());
        if ("startService".equals(method.getName())) {
            compat(args);
            Pair<Integer, Intent> integerIntentPair = findFirstIntentOfArgs(args);
            Intent newIntent = new Intent();
            // replace target service with local ProxyService to handle all lifecycle callbacks
            newIntent.setComponent(new ComponentName(ContextProvider.sContext.getPackageName(), ProxyService.class.getName()));
            // save target service info to intent extra
            newIntent.putExtra(HookHelper.EXTRA_TARGET_INTENT, compatIntent(integerIntentPair.second));
            // replace the intent to cheat AMS
            args[integerIntentPair.first] = newIntent;
            return method.invoke(mRaw, args);

        }
        if ("stopService".equals(method.getName())) {
            Intent raw = compatIntent(findFirstIntentOfArgs(args).second);
            return ServiceManager.getInstance().stopService(raw);
        }

        if ("startActivity".equals(method.getName())) {
            Log.e("TAG", "hook -> startActivity");
            Pair<Integer, Intent> integerIntentPair = findFirstIntentOfArgs(args);
            ComponentName component = integerIntentPair.second.getComponent();
            if (component != null && !component.getClassName().equals(STUB_CLASS)) {
                Intent newIntent = new Intent();
                // replace target service with local StubActivity
                newIntent.setComponent(new ComponentName(ContextProvider.sContext, STUB_CLASS));
                // save target service info to intent extra
                newIntent.putExtra(HookHelper.EXTRA_TARGET_INTENT, integerIntentPair.second);
                // replace the intent to cheat AMS
                args[integerIntentPair.first] = newIntent;
            }
        }

        return method.invoke(mRaw, args);
    }

    private void compat(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Intent) {
                Intent intent = (Intent) arg;
                if (PACKAGE_MEIZU_PICKER.equals(intent.getPackage())) {
                    /**
                     * long click events on EditText on meizu 16th, will start a service which cause RemoteServiceException, throw an exception and catch it.
                     *
                     * android.app.RemoteServiceException: Context.startForegroundService() did not then call Service.startForeground()
                     *         at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1926)
                     *         at com.wangzhen.plugin.hook.ActivityThreadHandlerCallback.handleMessage(ActivityThreadHandlerCallback.java:33)
                     *         at android.os.Handler.dispatchMessage(Handler.java:102)
                     *         at android.os.Looper.loop(Looper.java:192)
                     *         at android.app.ActivityThread.main(ActivityThread.java:6842)
                     *         at java.lang.reflect.Method.invoke(Native Method)
                     *         at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:438)
                     *         at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:886)
                     *
                     * data scheme:
                     * intent:#Intent;action=com.meizu.picker.action.service.HANDLE_CONTENT;launchFlags=0x20;package=com.meizu.picker;l.pick_fw_version=191212;S.package_name=com.wangzhen.plugin.host;i.intent_count=1;i.intent_index=0;i.perform_long_click_time=500;i.touch_x=554;i.touch_y=1921;S.activity_name=com.wangzhen.plugin.two.MainActivity;i.webview_hit_type=0;S.view_class_name=android.widget.EditText;i.pid=22315;i.uid=10373;i.decor_height=2160;i.decor_width=1080;l.intent_version=22275847880707;S.top_most_view_class_name=android.widget.EditText;S.view_self_class_name=android.widget.EditText;end
                     */
                    throw new NullPointerException("RemoteServiceException compat on Meizu 16th, abort current service");
                }
            }
        }
    }

    /**
     * due to plug-in loading mechanism, the raw package name of service plugin is the same as host`s, compat here
     *
     * @param intent intent
     * @return intent
     */
    private Intent compatIntent(Intent intent) {
        PackageInfo pluginPackageInfo = PluginManager.getInstance().getPluginPackageInfo();
        if (pluginPackageInfo != null && intent.getComponent() != null) {
            String packageName = pluginPackageInfo.packageName;
            intent.setComponent(new ComponentName(packageName, intent.getComponent().getClassName()));
        }
        return intent;
    }

    /**
     * find the first intent of args
     *
     * @param args args
     * @return intent
     */
    private Pair<Integer, Intent> findFirstIntentOfArgs(Object... args) {
        int index = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Intent) {
                index = i;
                break;
            }
        }
        return Pair.create(index, (Intent) args[index]);
    }
}
