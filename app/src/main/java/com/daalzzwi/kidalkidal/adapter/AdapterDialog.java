package com.daalzzwi.kidalkidal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daalzzwi.kidalkidal.R;

import java.util.ArrayList;


public class AdapterDialog extends RecyclerView.Adapter< AdapterDialog.ViewHolder > {

    private ArrayList< String > dataDialog = new ArrayList<>();
    private OnItemClickListener dataListener = null;

    public interface OnItemClickListener {

        void onItemClick( View v , String pos );
    }

    public void setOnItemClickListener( OnItemClickListener listener ) {

        this.dataListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {

        Context context = parent.getContext();
        LayoutInflater inflater = ( LayoutInflater ) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate( R.layout.activity_rcv_item_dialog, parent , false );
        AdapterDialog.ViewHolder viewHolder = new AdapterDialog.ViewHolder( view );

        return viewHolder;
    }

    @Override
    public void onBindViewHolder( @NonNull ViewHolder holder, int position ) {

        String dataSelect = dataDialog.get( position );
        holder.textView.setText( dataSelect );
    }

    @Override
    public int getItemCount() {

        return dataDialog.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        public ViewHolder( @NonNull View itemView ) {

            super(itemView);
            textView = itemView.findViewById( R.id.tvRecyclerViewItem );

            textView.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick( View view ) {

                    String pos = dataDialog.get( getAdapterPosition() );

                    if( pos != null ) {

                        dataListener.onItemClick( view , pos );
                    }
                }
            } );
        }
    }

    public AdapterDialog(ArrayList< String > list ) {

        dataDialog = list;
    }
}
