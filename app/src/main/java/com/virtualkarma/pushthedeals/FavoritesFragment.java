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
import android.widget.Toast;

import com.virtualkarma.pushthedeals.dao.DealSiteDao;
import com.virtualkarma.pushthedeals.domain.DealSite;
import com.virtualkarma.pushthedeals.util.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment implements FloatingActionButton.OnClickListener{

    private static final String LOG_TAG = FavoritesFragment.class.getSimpleName();

    private FloatingActionButton floatingActionButton;
    private FavRecyclerViewAdapter favRecyclerViewAdapter;
    private RecyclerView favRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FavoritesAsyncTask favoritesAsyncTask;

    private OnAddClickedListener callBack;

    private ItemTouchHelper itemTouchHelper;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public interface OnAddClickedListener{
        void onAddClicked();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_favorites, container, false);

        favRecyclerView = (RecyclerView)rootview.findViewById(R.id.fav_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        favRecyclerView.setLayoutManager(layoutManager);
        favRecyclerViewAdapter = new FavRecyclerViewAdapter(new ArrayList<DealSite>(), getActivity());
        favRecyclerView.setAdapter(favRecyclerViewAdapter);

        floatingActionButton = (FloatingActionButton)rootview.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(favRecyclerViewAdapter);
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
        getActivity().setTitle("My Deals Sites");
        favoritesAsyncTask = new FavoritesAsyncTask(getActivity());
        Log.d(LOG_TAG, "start fetch task");
        favoritesAsyncTask.execute();
    }



    @Override
    public void onClick(View v) {

        Toast.makeText(getActivity(),"Clicked on fab", Toast.LENGTH_SHORT).show();
        callBack.onAddClicked();

    }

    private List<DealSite> getDataSet() {
        List<DealSite> results = new ArrayList();
        for (int index = 0; index < 20; index++) {
            DealSite obj = new DealSite();
            obj.setName("Some name " + index);
            results.add(index, obj);
        }
        return results;
    }

    public class FavoritesAsyncTask extends AsyncTask<Void,Void,List<DealSite>>{

        private final String LOG_TAG = FavoritesAsyncTask.class.getSimpleName();
        Context context;
        public FavoritesAsyncTask(Context context){
            this.context = context;
        }
        @Override
        protected List<DealSite> doInBackground(Void... params) {
            List<DealSite> favList = new ArrayList<>();
            DealSiteDao dealSiteDao = new DealSiteDao(context);
            try {
                favList = dealSiteDao.lookup();
            }catch (Exception e){
                Log.e(LOG_TAG, e.getMessage());
            }
            return favList;
        }

        @Override
        protected void onPostExecute(List<DealSite> dealSites) {
            super.onPostExecute(dealSites);
            Log.d(LOG_TAG, "Favorites - " + dealSites);
            favRecyclerViewAdapter.getFavoritesList().clear();
            favRecyclerViewAdapter.getFavoritesList().addAll(dealSites);
            favRecyclerViewAdapter.notifyDataSetChanged();

        }
    }


}