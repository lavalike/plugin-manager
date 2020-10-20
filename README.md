# plugin-manager
> 动态apk插件化框架（Android 5.0+）

[![Platform](https://img.shields.io/badge/Platform-Android-00CC00.svg?style=flat)](https://www.android.com)
[![](https://jitpack.io/v/lavalike/plugin-manager.svg)](https://jitpack.io/#lavalike/plugin-manager)

### 依赖导入

项目根目录

``` gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

模块目录

``` gradle
dependencies {
	implementation 'com.github.lavalike:plugin-manager:0.0.8.1'
}
```

## 宿主

### 接口说明

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

### 代码示例

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

## 插件

### 注意事项

1. 插件Activity需继承 **PluginBaseActivity**
2. 暂不支持插件Application的初始化，请正确处理相关逻辑

### 基类源码

``` java
public class PluginBaseActivity extends FragmentActivity {
    @Override
    public Resources getResources() {
        Resources resources = PluginManager.getInstance().getPluginResources();
        return resources != null ? resources : super.getResources();
    }

    @Override
    public AssetManager getAssets() {
        AssetManager assets = PluginManager.getInstance().getAssets();
        return assets != null ? assets : super.getAssets();
    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = PluginManager.getInstance().getTheme();
        return theme != null ? theme : super.getTheme();
    }
}
```

### 代码示例

```java
public class BaseActivity extends PluginBaseActivity {
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }
}
```