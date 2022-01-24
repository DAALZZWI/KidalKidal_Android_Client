package com.daalzzwi.kidalkidal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daalzzwi.kidalkidal.R;
import com.daalzzwi.kidalkidal.model.ModelChat;

import java.util.ArrayList;

public class AdapterChat extends RecyclerView.Adapter< RecyclerView.ViewHolder > {

    private ArrayList<ModelChat> dataChat;
    private OnItemUpdateListener dataListener = null;

    public AdapterChat() {

        this( new ArrayList<ModelChat>() );
    }

    AdapterChat(ArrayList<ModelChat> data ) {

        this.dataChat = data;
    }

    public interface OnItemUpdateListener {

        void onItemUpdate(RecyclerView.ViewHolder holder , ArrayList<ModelChat> dataChat , int position );
    }

    public void setOnItemUpdateListener( OnItemUpdateListener listener ) {

        this.dataListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {

        View view;

        if( viewType == 0 ) {

            view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.activity_rcv_item_chat_center , parent ,false );
            return new viewHolderCenter( view );
        } else if( viewType == 1 ) {

            view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.activity_rcv_item_chat_left , parent ,false );
            return new viewHolderLeft( view );
        } else {

            view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.activity_rcv_item_chat_right , parent ,false );
            return new viewHolderRight( view );
        }
    }

    @Override
    public void onBindViewHolder( @NonNull RecyclerView.ViewHolder holder, int position ) {

        dataListener.onItemUpdate( holder , dataChat , position );
    }

    @Override
    public int getItemCount() {

        return dataChat.size();
    }

    @Override
    public int getItemViewType( int position ) {

        return dataChat.get( position ).getChatViewType();
    }

    public void addItem( ModelChat modelChat ) {

        dataChat.add( modelChat );
    }

    public class viewHolderCenter extends RecyclerView.ViewHolder {

        public TextView chatMessage;

        public viewHolderCenter( @NonNull View itemView ) {

            super( itemView );

            chatMessage = itemView.findViewById( R.id.tvChatMessage );
        }
    }

    public class viewHolderLeft extends RecyclerView.ViewHolder {

        public TextView chatName;
        public TextView chatDate;
        public TextView chatMessage;
        public ImageView chatImage;

        public viewHolderLeft( @NonNull View itemView ) {

            super( itemView );

            chatName = itemView.findViewById( R.id.tvChatName );
            chatDate = itemView.findViewById( R.id.tvChatDate );
            chatMessage = itemView.findViewById( R.id.tvChatMessage );
            chatImage = itemView.findViewById( R.id.ivChatImage );
        }
    }

    public class viewHolderRight extends RecyclerView.ViewHolder {

        public TextView chatName;
        public TextView chatDate;
        public TextView chatMessage;
        public ImageView chatImage;

        public viewHolderRight( @NonNull View itemView ) {

            super( itemView );

            chatName = itemView.findViewById( R.id.tvChatName );
            chatDate = itemView.findViewById( R.id.tvChatDate );
            chatMessage = itemView.findViewById( R.id.tvChatMessage );
            chatImage = itemView.findViewById( R.id.ivChatImage );
        }
    }
}