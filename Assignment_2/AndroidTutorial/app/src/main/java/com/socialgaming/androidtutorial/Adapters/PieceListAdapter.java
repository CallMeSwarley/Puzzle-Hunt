package com.socialgaming.androidtutorial.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.socialgaming.androidtutorial.DealerActivity;
import com.socialgaming.androidtutorial.Interfaces.ILoadMore;
import com.socialgaming.androidtutorial.Models.PieceViewItem;
import com.socialgaming.androidtutorial.R;
import com.socialgaming.androidtutorial.TradeActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

class PieceItemViewHolder extends RecyclerView.ViewHolder{

    public ImageView image;
    public TextView amount;

    public PieceItemViewHolder(@NonNull @NotNull View itemView, boolean isPopUpRecView, Activity activity) {
        super(itemView);

        image = itemView.findViewById(R.id.pieces_imageView);
        amount = itemView.findViewById(R.id.amount_textView);

        if (activity instanceof TradeActivity) {

            // For the pop up recyclerview
            if (isPopUpRecView) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TradeActivity) activity).addPieceToTradeView(getBindingAdapterPosition());
                    }
                });
            }
            else {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TradeActivity) activity).removePieceFromTradeView(getBindingAdapterPosition());
                    }
                });
            }
        }else if(activity instanceof DealerActivity){
            // For the pop up recyclerview
            if (isPopUpRecView) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((DealerActivity) activity).addPieceToTradeView(getBindingAdapterPosition());
                    }
                });
            }
            else {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((DealerActivity) activity).removePieceFromTradeView(getBindingAdapterPosition());
                    }
                });
            }
        }
    }
}

public class PieceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0,  VIEW_TYPE_LOADING = 1;
    ILoadMore loadMore;
    boolean isLoading;
    boolean createOnClickEvent;
    Activity activity;
    List<PieceViewItem> items;
    int visibleThreshold = 5;
    int lastVisibleItem, totalItemCount;

    public PieceListAdapter(RecyclerView recyclerView, Activity activity, List<PieceViewItem> items, boolean createOnClickEvent) {
        this.activity = activity;
        this.items = items;
        this.createOnClickEvent = createOnClickEvent;

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

    private boolean getStuff(){
        return true;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(activity).inflate(R.layout.piece_card_field, parent, false);
            return new PieceItemViewHolder(view, createOnClickEvent, activity);
        }
        else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.piece_card_field, parent, false);
            return new PieceItemViewHolder(view, createOnClickEvent, activity);
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
            viewHolder.image.setImageBitmap(item.getImage());
            viewHolder.amount.setText(item.getAmount() < 0 ? "" : Integer.toString(item.getAmount()));
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
