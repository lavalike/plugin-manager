## plugin-manager
动态apk插件化框架

#### 宿主
支持方法
```java
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
```java
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

#### 插件
❗️️插件Activity需继承**com.wangzhen.plugin.base.PluginBaseActivity**  
> 为防止出现异常，获取Context请使用PluginBaseActivity#getActivity()

示例
```java
public class BaseActivity extends PluginBaseActivity {
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        DarkStatusBar.get().fitDark(getActivity());
    }

}
```