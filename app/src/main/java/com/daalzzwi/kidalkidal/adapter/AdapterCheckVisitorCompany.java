package com.daalzzwi.kidalkidal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daalzzwi.kidalkidal.R;
import com.daalzzwi.kidalkidal.model.ModelCompany;

import java.util.ArrayList;

public class AdapterCheckVisitorCompany extends RecyclerView.Adapter< AdapterCheckVisitorCompany.ViewHolder > {

    private ArrayList<ModelCompany> dataChatCompany = new ArrayList<>();
    private OnItemClickListener dataListener = null;

    public interface OnItemClickListener {

        void onItemClick(ArrayList<ModelCompany> dataRoom , View v , int pos );
    }

    public void setOnItemClickListener( OnItemClickListener listener ) {

        this.dataListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView chatRoom;

        public ViewHolder( @NonNull View itemView ) {

            super( itemView );

            chatRoom = itemView.findViewById( R.id.tvRecyclerViewItem );

            itemView.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int pos = getAdapterPosition();

                    if( pos != RecyclerView.NO_POSITION ) {

                        dataListener.onItemClick( dataChatCompany , view , pos );
                    }
                }
            } );
        }

        void onBind( ModelCompany data ) {

            chatRoom.setText( data.getCompanyName() );
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent , int viewType ) {

        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.activity_rcv_item_dialog , parent ,false );
        return new ViewHolder( view );

    }

    @Override
    public void onBindViewHolder( @NonNull ViewHolder holder, int position ) {

        holder.onBind( dataChatCompany.get( position ) );
    }

    @Override
    public int getItemCount() {

        return dataChatCompany.size();
    }

    public void addItem( ModelCompany data ) {

        dataChatCompany.add( data );
    }
}

