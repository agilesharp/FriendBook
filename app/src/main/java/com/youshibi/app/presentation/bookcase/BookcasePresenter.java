package com.youshibi.app.presentation.bookcase;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.youshibi.app.AppRouter;
import com.youshibi.app.base.BaseListPresenter;
import com.youshibi.app.data.DBManger;
import com.youshibi.app.data.db.table.BookTb;
import com.youshibi.app.event.AddBook2BookcaseEvent;
import com.youshibi.app.rx.RxBus;
import com.youshibi.app.rx.SimpleSubscriber;
import com.youshibi.app.ui.help.CommonAdapter;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Chu on 2016/12/3.
 */

public class BookcasePresenter extends BaseListPresenter<BookcaseContract.View, BookTb> implements BookcaseContract.Presenter {

    private CommonAdapter<BookTb> mAdapter;

    @Override
    public void start() {
        super.start();
        if (isViewAttached()) {
            getView().addOnItemTouchListener(new OnItemClickListener() {
                @Override
                public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                    BookTb bookTb = (BookTb) adapter.getItem(position);
                    AppRouter.showReadActivity(view.getContext(), bookTb);
                    // AppRouter.showBookDetailActivity(view.getContext(), DataConvertUtil.bookTb2Book((BookTb) adapter.getItem(position)));
                }
            });
        }

        Subscription subscribe = RxBus
                .getDefault()
                .toObservable(AddBook2BookcaseEvent.class)
                .subscribe(new SimpleSubscriber<AddBook2BookcaseEvent>() {
                    @Override
                    public void onNext(AddBook2BookcaseEvent addBook2BookcaseEvent) {
                        mAdapter.addData(addBook2BookcaseEvent.bookTb);
                    }
                });
        addSubscription2Destroy(subscribe);
    }

    @Override
    protected Observable<List<BookTb>> doLoadData(boolean isRefresh, int page, int size) {
        return DBManger
                .getInstance()
                .loadBookTb()
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    protected Observable<List<BookTb>> doLoadMoreData(int page, int size) {
        return null;
    }


    @Override
    protected CommonAdapter<BookTb> createAdapter(List<BookTb> data) {
        mAdapter = new BookcaseAdapter(data);
        mAdapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                BookcaseAdapter bookcaseAdapter = (BookcaseAdapter) adapter;
                if(bookcaseAdapter.startEdit()){
                    getView().showEditMode();
                    return true;
                }
                return false;
            }
        });
        return mAdapter;
    }

    @Override
    protected int getPageSize() {
        return 0;
    }

    @Override
    protected long getCount() {
        return 0;
    }
}
