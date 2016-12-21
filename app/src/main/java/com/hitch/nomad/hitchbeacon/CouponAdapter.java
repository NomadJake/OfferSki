package com.hitch.nomad.hitchbeacon;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.OfferVH> {
    Context context;
    List<Note> notes;
    private ImageLoader imageLoader2;

    OnItemClickListener clickListener;

    public CouponAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;

    }


    @Override
    public OfferVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        OfferVH viewHolder = new OfferVH(view);
        imageLoader2 = Hitchbeacon.getInstance().getImageLoader();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OfferVH holder, int position) {

        holder.title.setText(notes.get(position).getTitle());
        holder.note.setText(notes.get(position).getNote());
        holder.offerImage.setImageUrl(notes.get(position).getShopURI(),imageLoader2);
        int rem = position%4;
        switch (rem){
            case 0 : holder.lc.setBackgroundColor(Color.parseColor("#F44336"));
                break;
            case 1 : holder.lc.setBackgroundColor(Color.parseColor("#1976D2"));
                break;
            case 2 : holder.lc.setBackgroundColor(Color.parseColor("#F57C00"));
                break;
            case 3 : holder.lc.setBackgroundColor(Color.parseColor("#CDDC39"));
                break;
            default:holder.lc.setBackgroundColor(Color.parseColor("#E040FB"));
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class OfferVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, note;
        private FeedImageView offerImage;
        View lc;
        public OfferVH(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.note_item_title);
            note = (TextView) itemView.findViewById(R.id.note_item_desc);
            offerImage = (FeedImageView) itemView.findViewById(R.id.imageViewfav);
            lc = (View) itemView.findViewById(R.id.line_color);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

}
