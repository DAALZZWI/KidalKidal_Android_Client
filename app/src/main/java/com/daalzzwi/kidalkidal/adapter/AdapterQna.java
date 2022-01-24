package com.daalzzwi.kidalkidal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daalzzwi.kidalkidal.R;
import com.daalzzwi.kidalkidal.model.ModelDesk;

import java.util.ArrayList;

public class AdapterQna extends RecyclerView.Adapter< RecyclerView.ViewHolder > {

    private ArrayList<ModelDesk> dataQna = new ArrayList<>();
    private OnItemClickListener listenerClick = null;
    private OnItemUpdateListener listenerBind = null;

    public interface OnItemClickListener {

        void onItemClick(View view , ArrayList<ModelDesk> dataQna , int position );
    }

    public void setOnItemClickListener( OnItemClickListener listenerClick ) {

        this.listenerClick = listenerClick;
    }

    public interface OnItemUpdateListener {

        void onItemUpdate(RecyclerView.ViewHolder holder , ArrayList<ModelDesk> dataQna , int position );
    }

    public void setOnItemUpdateListener( OnItemUpdateListener listenerBind ) {

        this.listenerBind = listenerBind;
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        public TextView deskId;
        public TextView deskTitle;
        public TextView deskSee;
        public ImageView deskImage;

        public viewHolder( @NonNull View itemView ) {

            super( itemView );

            deskId = itemView.findViewById( R.id.tvQnaId );
            deskTitle = itemView.findViewById( R.id.tvQnaTitle );
            deskSee = itemView.findViewById( R.id.tvQnaSee );
            deskImage = itemView.findViewById( R.id.ivQnaImage );

            itemView.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick( View view ) {

                    int position = getAdapterPosition();

                    if( position != RecyclerView.NO_POSITION ) {

                        listenerClick.onItemClick( view , dataQna , position );
                    }
                }
            } );
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( @NonNull ViewGroup parent , int viewType ) {

        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.activity_rcv_item_qna , parent ,false );
        return new viewHolder( view );
    }

    @Override
    public void onBindViewHolder( @NonNull RecyclerView.ViewHolder holder, int position ) {

        listenerBind.onItemUpdate( holder , dataQna , position );
    }

    @Override
    public int getItemCount() {

        return dataQna.size();
    }

    public void addItem( ModelDesk modelDesk ) {

        dataQna.add( modelDesk );
    }
}
