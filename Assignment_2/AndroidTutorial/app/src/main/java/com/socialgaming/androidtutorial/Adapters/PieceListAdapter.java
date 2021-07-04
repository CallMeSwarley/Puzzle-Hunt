package com.socialgaming.androidtutorial.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    public PieceItemViewHolder(@NonNull @NotNull View itemView, Activity activity, RecyclerView recyclerView) {
        super(itemView);

        image = itemView.findViewById(R.id.pieces_imageView);
        amount = itemView.findViewById(R.id.amount_textView);


        /** OnClickEvents for recyclerViews can be added here
          * 1. Determine the Activity your RecyclerView is in
          * 2. Choose the right RecyclerView one with the id
         */
        if (activity instanceof TradeActivity) {

            if(recyclerView.getId() == R.id.player1_trade_items_recyclerView) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TradeActivity) activity).removePieceFromTradeView(getBindingAdapterPosition());
                    }
                });
            }
            else if (recyclerView.getId() == R.id.player2_trade_items_recyclerView) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TradeActivity) activity).choosePieceFromPartner(getBindingAdapterPosition());
                    }
                });
            }
            else if (recyclerView.getId() == R.id.add_pieces_recyclerView){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TradeActivity) activity).addPieceToTradeView(getBindingAdapterPosition());
                    }
                });
            }
        }
        else if(activity instanceof DealerActivity){
            // For the pop up recyclerview
            if (recyclerView.getId() == R.id.add_pieces_recyclerView) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((DealerActivity) activity).addPieceToTradeView(getBindingAdapterPosition());
                    }
                });
            }
            else if(recyclerView.getId() == R.id.recyclerView2) {
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
    RecyclerView recyclerView;
    List<PieceViewItem> items;
    int visibleThreshold = 5;
    int lastVisibleItem, totalItemCount;

    public PieceListAdapter(RecyclerView recyclerView, Activity activity, List<PieceViewItem> items) {
        this.activity = activity;
        this.items = items;
        this.createOnClickEvent = createOnClickEvent;
        this.recyclerView = recyclerView;

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
            return new PieceItemViewHolder(view, activity, recyclerView);
        }
        else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.piece_card_field, parent, false);
            return new PieceItemViewHolder(view, activity, recyclerView);
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
