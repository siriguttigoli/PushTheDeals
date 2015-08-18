package com.virtualkarma.pushthedeals;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.virtualkarma.pushthedeals.dao.DealSiteDao;
import com.virtualkarma.pushthedeals.domain.DealSite;
import com.virtualkarma.pushthedeals.util.DividerItemDecoration;
import com.virtualkarma.pushthedeals.util.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyListFragment extends Fragment implements FloatingActionButton.OnClickListener {

    private static final String LOG_TAG = MyListFragment.class.getSimpleName();

    private FloatingActionButton floatingActionButton;
    private MyListRecyclerViewAdapter myListRecyclerViewAdapter;
    private RecyclerView favRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MyListAsyncTask myListAsyncTask;
    private BackgroundContainer backgroundContainer;

    private OnAddClickedListener callBack;

    private ItemTouchHelper itemTouchHelper;

    public MyListFragment() {
        // Required empty public constructor
    }

    public interface OnAddClickedListener {
        void onAddClicked();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_mylist, container, false);

        backgroundContainer = (BackgroundContainer) rootview.findViewById(R.id.listViewBackground);
        favRecyclerView = (RecyclerView) rootview.findViewById(R.id.fav_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        favRecyclerView.setLayoutManager(layoutManager);
        myListRecyclerViewAdapter = new MyListRecyclerViewAdapter(new ArrayList<DealSite>(), getActivity());
        favRecyclerView.setAdapter(myListRecyclerViewAdapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL);
        favRecyclerView.addItemDecoration(itemDecoration);

        floatingActionButton = (FloatingActionButton) rootview.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback
                (myListRecyclerViewAdapter, backgroundContainer);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(favRecyclerView);

        return rootview;


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callBack = (OnAddClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_fragment_favorites);
        myListAsyncTask = new MyListAsyncTask(getActivity());
        Log.d(LOG_TAG, "start fetch task");
        myListAsyncTask.execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (myListAsyncTask != null && myListAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            Log.d(LOG_TAG, "myListAsyncTask cancelled");
            myListAsyncTask.cancel(true);
        }
    }

    @Override
    public void onClick(View v) {

        callBack.onAddClicked();

    }


    public class MyListAsyncTask extends AsyncTask<Void, Void, List<DealSite>> {

        private final String LOG_TAG = MyListAsyncTask.class.getSimpleName();
        Context context;

        public MyListAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<DealSite> doInBackground(Void... params) {
            List<DealSite> myList = new ArrayList<>();

            if (!isCancelled()) {
                DealSiteDao dealSiteDao = new DealSiteDao(context);
                try {
                    myList = dealSiteDao.lookup();
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
            return myList;
        }

        @Override
        protected void onPostExecute(List<DealSite> dealSites) {
            super.onPostExecute(dealSites);
            if (!isCancelled()) {
                Log.d(LOG_TAG, "My List - " + dealSites);
                myListRecyclerViewAdapter.getMyDealSitesList().clear();
                myListRecyclerViewAdapter.getMyDealSitesList().addAll(dealSites);
                myListRecyclerViewAdapter.notifyDataSetChanged();
            }

        }
    }


}
