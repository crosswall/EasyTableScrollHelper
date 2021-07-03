### EasyTableScrollHelper 

一种基于recyclerView来实现的简单、高效、快速的上下左右列表滑动方案，仅使用一个类搞定一个复杂逻辑，基本对业务代码0干扰、0侵入！

#### GIF演示
<img src=https://github.com/crosswall/EasyTableScrollHelper/blob/master/media/7m470-ziz2l.gif width=40% />

#### 它能干什么
>* 股票自选详情列表
>* 汽车、游戏装备属性展示
>* 类似自定义excel表格

#### 如何使用
```code
    //代码
    new TableScrollHelper().attachToRecyclerView(YOUR_RECYCLER_VIEW, YOUR_RECYCLER_ADAPTER, YOUR_TABLE_HEADER);

    //itemView中需要被滑动的视图设置tag
    android:tag="table_scroll_container"
```

#### 如何布局
<img src=https://github.com/crosswall/EasyTableScrollHelper/blob/master/media/table_scroll_ui.png width=40% />


