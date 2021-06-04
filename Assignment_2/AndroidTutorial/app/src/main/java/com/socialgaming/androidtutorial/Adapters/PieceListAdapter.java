package com.socialgaming.androidtutorial.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.socialgaming.androidtutorial.Interfaces.ILoadMore;
import com.socialgaming.androidtutorial.Models.PieceViewItem;
import com.socialgaming.androidtutorial.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

class PieceItemViewHolder extends RecyclerView.ViewHolder{

    public View image;

    public PieceItemViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        // TODO Bild laden
    }
}

public class PieceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0,  VIEW_TYPE_LOADING = 1;
    ILoadMore loadMore;
    boolean isLoading;
    Activity activity;
    List<PieceViewItem> items;
    int visibleThreshold = 5;
    int lastVisibleItem, totalItemCount;

    public PieceListAdapter(RecyclerView recyclerView, Activity activity, List<PieceViewItem> items) {
        this.activity = activity;
        this.items = items;

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if(!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)){
                    if(loadMore != null)
                        loadMore.onLoadMore();

                    isLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(activity).inflate(R.layout.piece_card_field, parent, false);
            return new PieceItemViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.piece_card_field, parent, false);
            return new PieceItemViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PieceItemViewHolder){
            PieceViewItem item = items.get(position);

            if(item == null)
                return;

            PieceItemViewHolder viewHolder = (PieceItemViewHolder) holder;
            // TODO hier sollte die image preview geladen werden
            //viewHolder.image.
        }
        else if(holder instanceof SetLoadingViewHolder){
            SetLoadingViewHolder loadingViewHolder = (SetLoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

}
