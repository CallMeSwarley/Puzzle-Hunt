package com.socialgaming.androidtutorial.Adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.socialgaming.androidtutorial.Interfaces.ILoadMore;
import com.socialgaming.androidtutorial.Models.Puzzle;
import com.socialgaming.androidtutorial.Models.SetViewItem;
import com.socialgaming.androidtutorial.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

/*
    Adapter Klasse Vorlage
    https://www.youtube.com/watch?v=PamhELVWYY0&t=1165s
 */




class SetLoadingViewHolder extends RecyclerView.ViewHolder
{
    public ProgressBar progressBar;

    public SetLoadingViewHolder(@NonNull @NotNull View itemView, ProgressBar progressBar) {
        super(itemView);
        this.progressBar = itemView.findViewById(R.id.progressBar);
    }
}

class SetItemViewHolder extends RecyclerView.ViewHolder
{
    public TextView title, ownedPieces, maxPieces;
    public ImageView image;
    public CardView cardView;

    public SetItemViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        // Card Layout
        title = itemView.findViewById(R.id.title_textView);
        ownedPieces = itemView.findViewById(R.id.owned_textView);
        maxPieces = itemView.findViewById(R.id.max_textView);
        image = itemView.findViewById(R.id.preview_image_imageView);
        cardView = itemView.findViewById(R.id.set_CardView);
    }
}

public class SetListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final int VIEW_TYPE_ITEM = 0,  VIEW_TYPE_LOADING = 1;
    int visibleThreshold = 5;
    int lastVisibleItem, totalItemCount;
    boolean isLoading;
    ILoadMore loadMore;
    Activity activity;
    List<SetViewItem> items;
    private Function myFun = null;


    public SetListAdapter(RecyclerView recyclerView, Activity activity, List<SetViewItem> items, Function fun) {
        this.activity = activity;
        this.items = items;
        myFun = fun;
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
            View view = LayoutInflater.from(activity).inflate(R.layout.set_card_row, parent, false);
            //View view = LayoutInflater.from(activity).inflate(R.layout.set_row, parent, false);
            return new SetItemViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.set_card_loading, parent, false);
            return new SetItemViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SetItemViewHolder){
            SetViewItem item = items.get(position);

            if(item == null)
                return;

            SetItemViewHolder viewHolder = (SetItemViewHolder) holder;
            viewHolder.title.setText(item.getTitle());
            viewHolder.ownedPieces.setText(Integer.toString(item.getOwnedPieces()));
            viewHolder.maxPieces.setText(Integer.toString(item.getMaxPieces()));
            viewHolder.image.setImageResource(item.getImage());
            Puzzle puzzle = new Puzzle(item.getImage()+"",(int)Math.sqrt(item.getMaxPieces()),(int)Math.sqrt(item.getMaxPieces()),item.getPosOfOwnedPieces());

            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    myFun.apply(puzzle);

                }
            });
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
