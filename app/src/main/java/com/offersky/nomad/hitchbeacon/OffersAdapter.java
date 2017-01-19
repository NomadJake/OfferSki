package com.offersky.nomad.hitchbeacon;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;
import java.util.Random;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OfferVH> {
    Context context;
    List<Offer> notes;
    private ImageLoader imageLoader;

    OnItemClickListener clickListener;

    public OffersAdapter(Context context, List<Offer> notes) {
        this.context = context;
        this.notes = notes;
    }
    @Override
    public OfferVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new, parent, false);
        OfferVH viewHolder = new OfferVH(view);
        imageLoader = Hitchbeacon.getInstance().getImageLoader();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OfferVH holder, int position) {

        holder.title.setText(notes.get(position).getTitle());
        holder.offerImageLarge.setImageUrl(((Offer) this.notes.get(position)).getUid(), this.imageLoader);
        holder.note.setText(notes.get(position).getOffer());
        holder.offerImage.setImageUrl(notes.get(position).getLogoURI(),imageLoader);
        Random rand = new Random();
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
        private FeedImageView offerImageLarge;
        View lc;
        public OfferVH(View itemView) {
            super(itemView);
            this.offerImageLarge = (FeedImageView) itemView.findViewById(R.id.offer_image);
            this.title = (TextView) itemView.findViewById(R.id.note_item_title);
            this.note = (TextView) itemView.findViewById(R.id.note_item_desc);
            this.offerImage = (FeedImageView) itemView.findViewById(R.id.imageViewfav);
            this.lc = itemView.findViewById(R.id.line_color);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
            OffersAdapter.this.clickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

}