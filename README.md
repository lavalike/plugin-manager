# plugin-manager
> 动态apk插件化框架
> 支持范围：Android 5.0 +

[![Platform](https://img.shields.io/badge/Platform-Android-00CC00.svg?style=flat)](https://www.android.com)
[![](https://jitpack.io/v/lavalike/plugin-manager.svg)](https://jitpack.io/#lavalike/plugin-manager)

### 宿主
支持方法

``` java
public interface Plugin {
    void loadAsset(String path);
    void loadAsset(String path, PluginLoadCallback callback);
    void load(String path);
    void load(String path, PluginLoadCallback callback);
    void startActivity(String className);
    void startActivity();
}
```

示例

``` java
PluginManager.getInstance().load(path, new PluginLoadCallback() {
    @Override
    public void onSuccess() {
        PluginManager.getInstance().startActivity();
    }

    @Override
    public void onFail(String error) {

    }
});
```

### 插件
❗️️插件Activity需继承 **PluginBaseActivity**
> 暂不支持插件Application的初始化，请正确处理相关逻辑

示例

```java
public class BaseActivity extends PluginBaseActivity {
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }
}
```