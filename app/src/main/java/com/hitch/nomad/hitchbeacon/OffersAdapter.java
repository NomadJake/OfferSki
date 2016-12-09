package com.hitch.nomad.hitchbeacon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        OfferVH viewHolder = new OfferVH(view);
        imageLoader = Hitchbeacon.getInstance().getImageLoader();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OfferVH holder, int position) {

        holder.title.setText(notes.get(position).getTitle());
        holder.note.setText(notes.get(position).getOffer());
        holder.offerImage.setImageUrl(notes.get(position).getLogoURI(),imageLoader);

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class OfferVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, note;
        private FeedImageView offerImage;

        public OfferVH(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.note_item_title);
            note = (TextView) itemView.findViewById(R.id.note_item_desc);
            offerImage = (FeedImageView) itemView.findViewById(R.id.imageViewfav);
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
