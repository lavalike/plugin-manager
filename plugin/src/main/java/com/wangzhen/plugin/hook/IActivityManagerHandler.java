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
 * IActivityManagerHandler
 * Created by wangzhen on 2020/4/18.
 */
class IActivityManagerHandler implements InvocationHandler {

    private static final String TAG = "IActivityManagerHandler";

    Object mBase;

    public IActivityManagerHandler(Object base) {
        mBase = base;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("startService".equals(method.getName())) {
            // 只拦截这个方法
            // API 23:
            // public ComponentName startService(IApplicationThread caller, Intent service,
            //        String resolvedType, int userId) throws RemoteException

            // 找到参数里面的第一个Intent 对象
            Pair<Integer, Intent> integerIntentPair = foundFirstIntentOfArgs(args);
            Intent newIntent = new Intent();

            // 代理Service的包名, 也就是我们自己的包名
            String stubPackage = ContextProvider.sContext.getPackageName();
//            String stubPackage = "com.wangzhen.plugin.two";

            // 这里我们把启动的Service替换为ProxyService, 让ProxyService接收生命周期回调
            ComponentName componentName = new ComponentName(stubPackage, ProxyService.class.getName());
            newIntent.setComponent(componentName);

            // 把我们原始要启动的TargetService先存起来
            newIntent.putExtra(AMSHookHelper.EXTRA_TARGET_INTENT, compatIntent(integerIntentPair.second));

            // 替换掉Intent, 达到欺骗AMS的目的
            args[integerIntentPair.first] = newIntent;

            Log.e(TAG, "hook method startService success");
            return method.invoke(mBase, args);

        }

        //     public int stopService(IApplicationThread caller, Intent service,
        // String resolvedType, int userId) throws RemoteException
        if ("stopService".equals(method.getName())) {
            Log.e(TAG, "hook method stopService success");
            Intent raw = compatIntent(foundFirstIntentOfArgs(args).second);
            return ServiceManager.getInstance().stopService(raw);
        }

        return method.invoke(mBase, args);
    }

    /**
     * 修改原始Service Intent的包名为插件包名
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

    private Pair<Integer, Intent> foundFirstIntentOfArgs(Object... args) {
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
