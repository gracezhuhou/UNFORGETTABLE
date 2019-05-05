package com.example.unforgettable;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.unforgettable.LitepalTable.memoryCardsList;

import java.util.List;

public class CardsRecyclerAdapter extends RecyclerView.Adapter<CardsRecyclerAdapter.ViewHolder>{
    private List<memoryCardsList> myCardsList;
    private Dbhelper dbhelper = new Dbhelper();

    static class ViewHolder extends RecyclerView.ViewHolder{
        private Button delButton;
        private TextView headline;
        private TextView content_text;
        private TextView detail_text;
        private Button starButton;
        private LinearLayout cardView;

        public ViewHolder(View view){
            super(view);
            headline = view.findViewById(R.id.headline);
            content_text = view.findViewById(R.id.content_text);
            detail_text = view.findViewById(R.id.detail_text);
            delButton = view.findViewById(R.id.delButton);
            cardView = view.findViewById(R.id.cardView);
            starButton = view.findViewById(R.id.starButton);
        }
    }

    public CardsRecyclerAdapter(List<memoryCardsList> cardsList){
        myCardsList = cardsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        memoryCardsList cardsList = myCardsList.get(holder.getAdapterPosition());
        holder.headline.setText(cardsList.getHeading());
        holder.content_text.setText(cardsList.getContent());
        String str = "第" + cardsList.getRepeatTime() + "次重复";
        holder.detail_text.setText(str);

        //点击删除按钮
        holder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String heading = holder.headline.getText().toString();
                myCardsList.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
                // 数据库删除
                dbhelper.deleteCard(heading);
            }
        });

        // 点击编辑
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String heading = holder.headline.getText().toString();
                Intent intent = new Intent(v.getContext(), EditCardActivity.class);
                intent.putExtra("heading_extra", heading);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount(){
        return myCardsList.size();
    }
}
