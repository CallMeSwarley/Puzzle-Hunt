package com.socialgaming.androidtutorial.Adapters;

import android.app.Activity;
import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.socialgaming.androidtutorial.Interfaces.ILoadMore;
import com.socialgaming.androidtutorial.Models.SetViewItem;
import com.socialgaming.androidtutorial.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/*
    Adapter Klasse Vorlage
    https://www.youtube.com/watch?v=PamhELVWYY0&t=1165s
 */


class LoadingViewHolder extends RecyclerView.ViewHolder
{
    public ProgressBar progressBar;

    public LoadingViewHolder(@NonNull @NotNull View itemView, ProgressBar progressBar) {
        super(itemView);
        this.progressBar = itemView.findViewById(R.id.progressBar);
    }
}

class ItemViewHolder extends RecyclerView.ViewHolder
{
    public TextView title, ownedPieces, maxPieces;
    public View image;

    public ItemViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        // Card Layout
        title = itemView.findViewById(R.id.title_textView);
        ownedPieces = itemView.findViewById(R.id.owned_textView);
        maxPieces = itemView.findViewById(R.id.max_textView);
        image = itemView.findViewById(R.id.preview_image_imageView);

        // Altes Layout
//        title = itemView.findViewById(R.id.name_textView);
//        ownedPieces = itemView.findViewById(R.id.owned_pieces_textView);
//        maxPieces = itemView.findViewById(R.id.max_pieces_textView);
//        image = itemView.findViewById(R.id.image_preview_imageView);
    }
}

public class SetListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0,  VIEW_TYPE_LOADING = 1;
    ILoadMore loadMore;
    boolean isLoading;
    Activity activity;
    List<SetViewItem> items;
    int visibleThreshold = 5;
    int lastVisibleItem, totalItemCount;

    public SetListAdapter(RecyclerView recyclerView, Activity activity, List<SetViewItem> items) {
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
            View view = LayoutInflater.from(activity).inflate(R.layout.set_card_row, parent, false);
            //View view = LayoutInflater.from(activity).inflate(R.layout.set_row, parent, false);
            return new ItemViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.set_card_loading, parent, false);
            return new ItemViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            SetViewItem item = items.get(position);

            if(item == null)
                return;

            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.title.setText(item.getName());
            viewHolder.ownedPieces.setText(Integer.toString(item.getOwnedPieces()));
            viewHolder.maxPieces.setText(Integer.toString(item.getMaxPieces()));

            // TODO hier sollte die image preview geladen werden
            //viewHolder.image.
        }
        else if(holder instanceof  LoadingViewHolder){

           LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
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

    /*
    Context context;
    String[] data;
    int[] images;

    public RVAdapter(Context context, String[] s, int[] images){
        this.context = context;
        this.data = s;
        this.images = images;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.set_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull RVAdapter.MyViewHolder holder, int position) {
        holder.text.setText(data[position]);
        holder.image.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        ImageView image;

        public MyViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            //text = itemView.findViewById(R.id.amt_pieces_textView);
            image = itemView.findViewById(R.id.image_preview_imageView);
        }
    }

 */
}
