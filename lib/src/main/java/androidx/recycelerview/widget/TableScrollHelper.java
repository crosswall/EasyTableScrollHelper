package androidx.recycelerview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;

public class TableScrollHelper {

    public static final String SCROLL_ROOT_CONTAINER = "table_scroll_container";
    private static final String TAG = "TableScrollHelper";

    public TableScrollHelper() {
    }

    public void attachToRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter, ViewGroup headerScrollVG) {
        attachToRecyclerView(recyclerView, adapter, headerScrollVG, true);
    }

    public void attachToRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter, ViewGroup headerScrollVG, boolean asyncScrollable) {
        if (recyclerView == null) {
            Log.d(TAG, "recyclerView not be null");
            return;
        }
        if (adapter == null) {
            Log.d(TAG, "adapter not be null");
            return;
        }

        if (headerScrollVG == null) {
            Log.d(TAG, "headerScrollView no be null");
        }

        // add header scrollView
        ViewGroup parent = (ViewGroup) headerScrollVG.getParent();
        TableHorizonScrollView scrollView = new TableHorizonScrollView(recyclerView);
        if (parent != null) {
            ViewGroup.LayoutParams lp = headerScrollVG.getLayoutParams();
            parent.removeView(headerScrollVG);
            scrollView.addView(headerScrollVG, lp);
            parent.addView(scrollView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        // init TableAdapterWrapper
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        TableAdapterWrapper wrapperAdapter = new TableAdapterWrapper(recyclerView, adapter, scrollView, asyncScrollable);
        recyclerView.setAdapter(wrapperAdapter);
    }

    static class TableAdapterWrapper extends RecyclerView.Adapter {

        private RecyclerView recyclerView;
        private RecyclerView.Adapter adapter;
        private TableHorizonScrollView tableHeaderView;
        private int currentScrollX;
        private boolean asyncScrollable;

        public TableAdapterWrapper(RecyclerView recyclerView, RecyclerView.Adapter adapter, TableHorizonScrollView tableHeaderView, boolean asyncScrollable) {
            this.recyclerView = recyclerView;
            this.adapter = adapter;
            this.tableHeaderView = tableHeaderView;
            this.asyncScrollable = asyncScrollable;
            tableHeaderView.setOverScrollMode(View.OVER_SCROLL_NEVER);

            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    notifyDataSetChanged();
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                    notifyItemMoved(fromPosition, toPosition);
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    super.onItemRangeChanged(positionStart, itemCount);
                    notifyItemRangeChanged(positionStart, itemCount);
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    notifyItemRangeInserted(positionStart, itemCount);
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    notifyItemRangeRemoved(positionStart, itemCount);
                }
            });

            // recyclerView scroll callback
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!asyncScrollable) {
                        tableHeaderView.abortScroll();
                    }
                    //tableHeaderView.scrollTo(currentScrollX, 0);
                    int childCount = recyclerView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View view = recyclerView.getChildAt(i);
                        TableHorizonScrollView scrollView = view.findViewWithTag(TableHorizonScrollView.VIEW_TAG);
                        if (scrollView != null) {
                            if (!asyncScrollable) {
                                scrollView.abortScroll();
                            }
                            //scrollView.scrollTo(currentScrollX, 0);
                        }
                    }
                }
            });

            // header scrollView callback
            tableHeaderView.setOnScrollXCallback(new OnScrollXCallback() {
                @Override
                public void scrollX(int currentScrollX) {
                }

                @Override
                public void abort() {
                    if (!asyncScrollable) {
                        recyclerView.stopScroll();
                    }
                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = adapter.onCreateViewHolder(parent, viewType);
            View targetView = holder.itemView.findViewWithTag(SCROLL_ROOT_CONTAINER);
            if (targetView != null) {
                ViewGroup targetParent = (ViewGroup) targetView.getParent();
                ViewGroup.LayoutParams lp = targetView.getLayoutParams();
                TableHorizonScrollView scrollView = new TableHorizonScrollView(recyclerView);
                targetParent.removeView(targetView);
                scrollView.addView(targetView, lp);
                scrollView.setOnScrollXCallback(new OnScrollXCallback() {
                    @Override
                    public void scrollX(int scrollX) {
                        currentScrollX = scrollX;
                        tableHeaderView.scrollTo(scrollX, 0);
                        //Log.d(TAG, "OnScrollXCallback  ...scrollX:" + scrollX);
                    }

                    @Override
                    public void abort() {
                        if (!asyncScrollable) {
                            recyclerView.stopScroll();
                        }
                        tableHeaderView.abortScroll();
                    }
                });


                targetParent.addView(scrollView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            adapter.onBindViewHolder(holder, position);
            try {
                TableHorizonScrollView scrollView = (TableHorizonScrollView) holder.itemView.findViewWithTag(TableHorizonScrollView.VIEW_TAG);
                if (scrollView != null) {
                    syncScrollX(scrollView, 0);
                }
            } catch (Exception e) {
                Log.e(TAG, "onBindViewHolder:" + e.getMessage());
            }
        }

        @Override
        public int getItemCount() {
            return adapter.getItemCount();
        }

        private void syncScrollX(TableHorizonScrollView scrollView, long delayMills) {
            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(currentScrollX, 0);
                    scrollView.postInvalidate();
                    if (scrollView.getScrollX() != currentScrollX) {
                        syncScrollX(scrollView, 30);
                    }
                    //Log.w(TAG, "currentScrollX:" + currentScrollX + " , scrollView.getScrollX():" + scrollView.getScrollX());
                }
            }, delayMills);
        }
    }

    static class TableHorizonScrollView extends HorizontalScrollView {

        public static final String VIEW_TAG = "TableHorizonScrollView";
        private RecyclerView recyclerView;
        private boolean moveFlag = false;
        private Field scrollerField;
        private int overScrollX;
        private OnScrollXCallback callback;


        public TableHorizonScrollView(RecyclerView recyclerView) {
            this(recyclerView.getContext());
            this.recyclerView = recyclerView;
        }

        public TableHorizonScrollView(Context context) {
            this(context, null);
        }

        public TableHorizonScrollView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public TableHorizonScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initView();
        }

        private void initView() {
            setTag(VIEW_TAG);
            setOverScrollMode(OVER_SCROLL_NEVER);
            try {
                scrollerField = getClass().getSuperclass().getDeclaredField("mScroller");
                scrollerField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

//        @Override
//        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
//            Log.i(TAG, "TableHorizonScrollView overScrollBy  scrollX:" + scrollX + " , maxOverScrollX: " + maxOverScrollX + ", scrollRangeX:" + scrollRangeX + " , isTouchEvent: " + isTouchEvent + " , deltaX: " + deltaX);
//            if (callback != null) callback.scrollX(scrollX);
//            return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
//        }

        @Override
        protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);

            if (callback != null) callback.scrollX(scrollX);

            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = recyclerView.getChildAt(i);
                TableHorizonScrollView scrollView = view.findViewWithTag(VIEW_TAG);
                if (scrollView != null /*&& scrollView != this && moveFlag*/) {
                    scrollView.scrollTo(scrollX, 0);
                }
            }

            //Log.i(TAG, "TableHorizonScrollView onScrollChanged  scrollX:" + scrollX + " , oldScrollX: " + oldScrollX + " , currentScrollX: " + scrollX + " , overScrollX: " + overScrollX);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            int masked = ev.getActionMasked();
            switch (masked) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    moveFlag = true;
                    break;
                default:
                    moveFlag = false;
                    break;
            }
            return super.dispatchTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
                int childCount = recyclerView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View view = recyclerView.getChildAt(i);
                    TableHorizonScrollView scrollView = view.findViewWithTag(VIEW_TAG);
                    scrollView.abortScroll();
                }
                if (callback != null) callback.abort();
            }
            return super.onTouchEvent(ev);
        }

        @Override
        public void fling(int velocityX) {
            //super.fling(velocityX);
            int childCount = recyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = recyclerView.getChildAt(i);
                TableHorizonScrollView scrollView = view.findViewWithTag(VIEW_TAG);
                if (scrollView != null) {
                    int width = scrollView.getWidth() - scrollView.getPaddingRight() - scrollView.getPaddingLeft();
                    int right = scrollView.getChildAt(0).getWidth();
                    OverScroller scroller = null;
                    try {
                        scroller = (OverScroller) scrollerField.get(scrollView);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (scroller != null) {
                        scroller.fling(scrollView.getScrollX(), scrollView.getScrollY(), velocityX,
                                0, 0, Math.max(0, right - width), 0, 0,
                                width / 2, 0);
                        scrollView.postInvalidateOnAnimation();
                    }
                }
            }
        }

        public void setOnScrollXCallback(OnScrollXCallback callback) {
            this.callback = callback;
        }

        public void abortScroll() {
            try {
                OverScroller scroller = (OverScroller) scrollerField.get(this);
                if (scroller != null && !scroller.isFinished()) {
                    scroller.abortAnimation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    interface OnScrollXCallback {
        void scrollX(int currentScrollX);

        void abort();
    }

}
