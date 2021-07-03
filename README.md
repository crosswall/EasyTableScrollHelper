### EasyTableScrollHelper 

A lightweight tableView container helper based on recyclerView, Tiny and fast, Easy to use, No intrusion into business code.

#### GIF Demo
<img src=https://github.com/crosswall/EasyTableScrollHelper/blob/master/media/7m470-ziz2l.gif width=35% />

#### Business scenes
>* List of stock optional details
>* Car and game equipment attribute display
>* Custom table view

#### How to use
```code
    //step 1 Init TableScrollHelper
    new TableScrollHelper().attachToRecyclerView(YOUR_RECYCLER_VIEW, YOUR_RECYCLER_ADAPTER, YOUR_TABLE_HEADER);

    //step 2 Set tag for the childView of itemView
    android:tag="table_scroll_container"
```

#### How to layout
<img src=https://github.com/crosswall/EasyTableScrollHelper/blob/master/media/table_scroll_ui.png width=50% />


