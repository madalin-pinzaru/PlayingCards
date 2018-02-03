package com.mp.playcards.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mp.playcards.Interface.ItemClickListener;
import com.mp.playcards.R;

/**
 * Created by Madalin on 23-Ian-18.
 */

public class DeckViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView deck_name;
    public ImageView deck_image;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public DeckViewHolder(View itemView) {
        super(itemView);

        deck_name = (TextView)itemView.findViewById(R.id.deck_name);
        deck_image =(ImageView)itemView.findViewById(R.id.deck_image);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
