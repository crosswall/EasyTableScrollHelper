### 一个简单无侵入的左右&上下滑动的视图实现

>* 可用于股票看详情列表
>* 自定义表格视图

### 如何使用
```code
    //代码
    new TableScrollHelper().attachToRecyclerView(YOUR_RECYCLER_VIEW, YOUR_RECYCLER_ADAPTER, YOUR_TABLE_HEADER);

    //itemView中需要被滑动的视图设置tag
    android:tag="table_scroll_container"
```

### 如何布局
<img src=https://github.com/crosswall/EasyTableSrcollHelper/blob/master/media/table_scroll_ui.png width=60% />
### 视频演示