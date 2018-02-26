## Gradle
[![](https://jitpack.io/v/zj565061763/eventbus.svg)](https://jitpack.io/#zj565061763/eventbus)

## 常规用法
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
                FEventBus.getDefault().post(new TestEvent()); //发送事件
            }
        });
    }

    /**
     * 事件观察者
     */
    private FEventObserver<TestEvent> mEventObserver = new FEventObserver<TestEvent>()
    {
        @Override
        public void onEvent(TestEvent event)
        {
            Log.i(TAG, String.valueOf(event));
        }
    }.register(); //注册观察者

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        /**
         * 取消注册观察者，否则会造成内存泄漏
         */
        mEventObserver.unregister();
    }
}
```

## 绑定生命周期

设置生命周期对象，支持Activity，Dialog，View<br>
底层是通过View在Attached的时候注册观察者，View在Detached的时候取消注册观察者<br>

Activity 绑定的View对象 -> Activity.getWindow().getDecorView()
Dialog   绑定的View对象 -> Dialog.getWindow().getDecorView()
View     绑定的View对象 -> View
<br>

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
                FEventBus.getDefault().post(new TestEvent()); //发送事件
            }
        });
    }

    /**
     * 事件观察者
     */
    private FEventObserver<TestEvent> mEventObserver = new FEventObserver<TestEvent>()
    {
        @Override
        public void onEvent(TestEvent event)
        {
            Log.i(TAG, String.valueOf(event));
        }
    }.setLifecycle(this); //设置生命周期对象
}
```