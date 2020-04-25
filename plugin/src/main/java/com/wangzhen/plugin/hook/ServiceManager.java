package com.wangzhen.plugin.hook;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;

import com.wangzhen.plugin.PluginManager;
import com.wangzhen.plugin.provider.ContextProvider;
import com.wangzhen.plugin.proxy.ProxyService;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * service manager
 * Created by wangzhen on 2020/4/18.
 */
public class ServiceManager {
    private static volatile ServiceManager sInstance;
    private Map<String, Service> mServiceMap = new HashMap<>();
    private Map<ComponentName, ServiceInfo> mServiceInfoMap = new HashMap<>();

    public synchronized static ServiceManager getInstance() {
        if (sInstance == null) {
            sInstance = new ServiceManager();
        }
        return sInstance;
    }

    /**
     * launch service plugin, if the service is not launched, create a new one
     *
     * @param proxyIntent proxyIntent
     * @param startId     startId
     */
    public void onStartCommand(Intent proxyIntent, int flags, int startId) {
        Intent targetIntent = proxyIntent.getParcelableExtra(HookHelper.EXTRA_TARGET_INTENT);
        if (targetIntent == null) {
            return;
        }
        ServiceInfo serviceInfo = matchPluginService(targetIntent);
        if (serviceInfo == null) {
            return;
        }
        try {
            if (!mServiceMap.containsKey(serviceInfo.name)) {
                proxyCreateService(serviceInfo);
            }
            Service service = mServiceMap.get(serviceInfo.name);
            if (service != null) {
                service.onCreate();
                service.onStartCommand(targetIntent, flags, startId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * stop service plugin, when all service plugins stopped, stop the ProxyService
     *
     * @param targetIntent targetIntent
     * @return code
     */
    public int stopService(Intent targetIntent) {
        ServiceInfo serviceInfo = matchPluginService(targetIntent);
        if (serviceInfo == null) {
            return 0;
        }
        Service service = mServiceMap.get(serviceInfo.name);
        if (service == null) {
            return 0;
        }
        service.onDestroy();
        mServiceMap.remove(serviceInfo.name);
        if (mServiceMap.isEmpty()) {
            Context appContext = ContextProvider.sContext;
            appContext.stopService(new Intent().setComponent(new ComponentName(appContext.getPackageName(), ProxyService.class.getName())));
        }
        return 1;
    }

    /**
     * match the plugin
     *
     * @param pluginIntent plugin intent
     * @return ServiceInfo
     */
    private ServiceInfo matchPluginService(Intent pluginIntent) {
        for (ComponentName componentName : mServiceInfoMap.keySet()) {
            if (componentName.equals(pluginIntent.getComponent())) {
                return mServiceInfoMap.get(componentName);
            }
        }
        return null;
    }

    /**
     * create a new service instance by ActivityThread#handleCreateService
     *
     * @param serviceInfo ServiceInfo
     */
    private void proxyCreateService(ServiceInfo serviceInfo) throws Exception {
        IBinder token = new Binder();

        // create an instance of CreateServiceData, as the token of handleCreateService
        Class<?> createServiceDataClass = Class.forName("android.app.ActivityThread$CreateServiceData");
        Constructor<?> constructor = createServiceDataClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object createServiceData = constructor.newInstance();

        // set the token just created, used by ActivityThread#handleCreateService as a key to store services
        Field tokenField = createServiceDataClass.getDeclaredField("token");
        tokenField.setAccessible(true);
        tokenField.set(createServiceData, token);

        // write info object
        serviceInfo.applicationInfo.packageName = ContextProvider.sContext.getPackageName();
        Field infoField = createServiceDataClass.getDeclaredField("info");
        infoField.setAccessible(true);
        infoField.set(createServiceData, serviceInfo);

        // get default compatibility config
        Class<?> compatibilityClass = Class.forName("android.content.res.CompatibilityInfo");
        Field defaultCompatibilityField = compatibilityClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
        Object defaultCompatibility = defaultCompatibilityField.get(null);
        Field compatInfoField = createServiceDataClass.getDeclaredField("compatInfo");
        compatInfoField.setAccessible(true);
        compatInfoField.set(createServiceData, defaultCompatibility);

        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
        Object currentActivityThread = currentActivityThreadMethod.invoke(null);

        Method handleCreateServiceMethod = activityThreadClass.getDeclaredMethod("handleCreateService", createServiceDataClass);
        handleCreateServiceMethod.setAccessible(true);
        handleCreateServiceMethod.invoke(currentActivityThread, createServiceData);

        // retrieve the new-created service from ActivityThread#mServices
        Field mServicesField = activityThreadClass.getDeclaredField("mServices");
        mServicesField.setAccessible(true);
        Map mServices = (Map) mServicesField.get(currentActivityThread);
        Service service = (Service) mServices.get(token);

        mServices.remove(token);

        mServiceMap.put(serviceInfo.name, service);
    }

    /**
     * parse all services in apk and store to local
     */
    public void parseServices() {
        PackageInfo packageInfo = PluginManager.getInstance().getPluginPackageInfo();
        if (packageInfo != null && packageInfo.services != null) {
            for (ServiceInfo info : packageInfo.services) {
                mServiceInfoMap.put(new ComponentName(info.packageName, info.name), info);
            }
        }
    }
}
