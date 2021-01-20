# About
一个简单版本的EventBus<br>
* 支持单独对某个事件观察者的注册和取消注册
* post的时候直接拿到观察者对象进行通知，而非反射，效率比较高
* 支持对观察者设置生命周期对象，可以自动注册和取消注册观察者
* 支持粘性事件，发送一个粘性事件，当有对应事件的观察者注册的时候，会在注册的时候立即通知此事件

# Gradle
[![](https://jitpack.io/v/zj565061763/eventbus.svg)](https://jitpack.io/#zj565061763/eventbus)

# 常规用法
```java
public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 发送事件
                FEventBus.getDefault().post(new TestEvent());
            }
        });
    }

    /**
     * 事件观察者
     */
    private final FEventObserver<TestEvent> mEventObserver = new FEventObserver<TestEvent>()
    {
        @Override
        public void onEvent(TestEvent event)
        {
            // 在主线程回调
            Log.i(TAG, String.valueOf(event));
        }
    }.register(); //注册观察者

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        // 取消注册观察者，否则会造成内存泄漏
        mEventObserver.unregister();
    }
}
```

# 绑定生命周期

设置生命周期对象(会自动注册和取消注册观察者)，支持Activity，Dialog，View<br>
底层的实现方案：View在Attached的时候注册观察者，View在Detached的时候取消注册观察者 <br>

Activity 绑定的View对象 -> Activity.getWindow().getDecorView() <br>
Dialog   绑定的View对象 -> Dialog.getWindow().getDecorView() <br>
View     绑定的View对象 -> View <br>

```java
public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //发送事件
                FEventBus.getDefault().post(new TestEvent());
            }
        });
    }

    /**
     * 事件观察者
     */
    private final FEventObserver<TestEvent> mEventObserver = new FEventObserver<TestEvent>()
    {
        @Override
        public void onEvent(TestEvent event)
        {
            // 在主线程回调
            Log.i(TAG, String.valueOf(event));
        }
    }.setLifecycle(this); //设置生命周期对象(会自动注册和取消注册观察者)，支持Activity，Dialog，View
}
```

# 调试模式
```java
// 设置调试模式
FEventBus.getDefault().setDebug(true);
```

设置调试模式后，会有类似如下日志输出，日志过滤log：FEventBus
```java
// 注册了哪个观察者，观察者对应的事件，注册后这种事件的观察者数量
register:com.sd.event.MainActivity$2@429e1a1 (com.sd.event.TestEvent 1)

// post了一个事件，此次post需要通知的观察者数量
post----->com.sd.event.TestEvent@8fafe20 1

// 通知到第几个观察者
notify 1 com.sd.event.MainActivity$2@429e1a1

// 取消注册了哪个观察者，观察者对应的事件，取消注册后这种事件的观察者数量
unregister:com.sd.event.MainActivity$2@429e1a1 (com.sd.event.TestEvent 0)
```