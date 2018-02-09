## Gradle
[![](https://jitpack.io/v/zj565061763/eventbus.svg)](https://jitpack.io/#zj565061763/eventbus)

## 使用
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
     * TestEvent事件观察者
     * 默认对象一创建就会注册到FEventBus
     */
    private FEventObserver<TestEvent> mEventObserver = new FEventObserver<TestEvent>()
    {
        @Override
        public void onEvent(TestEvent event)
        {
            //收到post的事件
            Log.i(TAG, String.valueOf(event));
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        /**
         * 取消注册后将不再接收事件
         * FEventBus内部是用弱引用，所以不取消注册也不会内存泄漏，但是建议显式取消注册，有利于提高事件分发效率
         */
        mEventObserver.unregister();

        /**
         * 如果当前Activity有多个观察者，可以调用此方法批量取消注册
         */
        FEventObserver.unregisterAll(this);
    }
}
```