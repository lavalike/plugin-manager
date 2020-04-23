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

/**
 * invocation handler of IActivityManager
 * Created by wangzhen on 2020/4/18.
 */
class IActivityManagerHandler implements InvocationHandler {
    private Object mRaw;

    public IActivityManagerHandler(Object raw) {
        mRaw = raw;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("startService".equals(method.getName())) {
            Pair<Integer, Intent> integerIntentPair = findFirstIntentOfArgs(args);
            Intent newIntent = new Intent();
            // replace target service with local ProxyService to handle all lifecycle callbacks
            newIntent.setComponent(new ComponentName(ContextProvider.sContext.getPackageName(), ProxyService.class.getName()));
            // save target service info to intent extra
            newIntent.putExtra(AMSHookHelper.EXTRA_TARGET_INTENT, compatIntent(integerIntentPair.second));
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
        }

        return method.invoke(mRaw, args);
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
