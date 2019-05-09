package com.example.unforgettable;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unforgettable.Bmob.Bmobhelper;
import com.example.unforgettable.LitepalTable.memoryCardsList;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.app.AlertDialog.*;
import static android.content.ContentValues.TAG;
import static cn.bmob.v3.Bmob.getApplicationContext;
import static org.litepal.LitePalApplication.getContext;

public class CardsRecyclerAdapter extends RecyclerView.Adapter<CardsRecyclerAdapter.ViewHolder>{
    private List<memoryCardsList> myCardsList;
    private Dbhelper dbhelper = new Dbhelper();
    private MediaPlayer mPlayer = null;

    static class ViewHolder extends RecyclerView.ViewHolder{
        private Button delButton;
        private TextView headline;
        private TextView content_text;
        private TextView detail_text;
        private ImageButton starButton;
        private RelativeLayout cardView;
        private ImageButton audioButton;
        private ImageButton fileButton;

        public ViewHolder(View view){
            super(view);
            headline = view.findViewById(R.id.headline);
            content_text = view.findViewById(R.id.content_text);
            detail_text = view.findViewById(R.id.detail_text);
            //delButton = view.findViewById(R.id.delButton);
            cardView = view.findViewById(R.id.cardView);
            starButton = view.findViewById(R.id.starButton);
            audioButton = view.findViewById(R.id.audioButton);
            fileButton = view.findViewById(R.id.fileButton);
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

        // 设置缩略内容
        String allContent = cardsList.getContent();
        String content;
        if (allContent.length()<10){
            content = allContent;
        }
        else {
            content = allContent.substring(0,10) + "...";
        }
        byte[] images = cardsList.getPicture();
        if (images != null) {
            content += "[图片]";
        }
        holder.content_text.setText(content);

        String str = "第" + cardsList.getRepeatTime() + "次重复";
        holder.detail_text.setText(str);

        // 是否为收藏
        if (cardsList.isLike()) {
            Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.ic_star_yel);
            holder.starButton.setImageDrawable(drawable);
        }

        // 音频是否存在
        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + cardsList.getHeading() + ".3gp";
        File audioFile = new File(mFileName);
        if (!audioFile.exists()) holder.audioButton.setVisibility(View.INVISIBLE);

        // 是否已归档
        if (cardsList.isFinish()) {
            Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.ic_archive_yel);//TODO:
            holder.fileButton.setImageDrawable(drawable);
        }

        //点击删除按钮
//        holder.delButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//                String heading = holder.headline.getText().toString();
//                myCardsList.remove(holder.getAdapterPosition());
//                notifyDataSetChanged();
//                // 数据库删除
//                dbhelper.deleteCard(heading);
//            }
//        });

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
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new Builder(getContext());
                builder.setTitle("确认");
                builder.setMessage("您确定要删除这条记录吗？");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Vibrator vibrator=(Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
                        vibrator.vibrate(new long[]{0,50}, -1);
                        //showPopMenu(,holder.getAdapterPosition());
                        String heading = holder.headline.getText().toString();
                        myCardsList.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                        // 数据库删除
                        dbhelper.deleteCard(heading);
                        Toast.makeText(getContext(), "删除成功", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();

                return true;
            }
        });

        // 点击收藏
        holder.starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Vibrator vibrator=(Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{0,40}, -1);

                String heading = holder.headline.getText().toString();
                boolean like = dbhelper.changeLike(heading);
                // 改按键颜色状态
                if (like) {
                    Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_star_yel);
                    holder.starButton.setImageDrawable(drawable);
                }
                else {
                    Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_star_black);
                    holder.starButton.setImageDrawable(drawable);
                }
            }
        });
        // 点击播放
        holder.audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String heading = holder.headline.getText().toString();
                Vibrator vibrator=(Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{0,40}, -1);

                // 判断播放按钮的状态，根据相应的状态处理事务
                holder.audioButton.setEnabled(false);
                Drawable.ConstantState drawableState = holder.audioButton.getDrawable().getConstantState();
                Drawable.ConstantState drawableState_yel = getContext().getResources().getDrawable(R.drawable.ic_trumpet_yel).getConstantState();
                if (drawableState.equals(drawableState_yel)) {
                    // 停止播放
                    mPlayer.release();
                    mPlayer = null;
                    Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_trumpet_black);
                    holder.audioButton.setImageDrawable(drawable);
                } else {
                    // 开始播放
                    mPlayer = new MediaPlayer();
                    String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + heading + ".3gp";

                    try {
                        mPlayer.setDataSource(mFileName);// 设置多媒体数据来源
                        mPlayer.prepare();
                        mPlayer.start();
                    } catch (IOException e) {
                        Log.e(TAG, getContext().getString(R.string.e_play));
                        Toast.makeText(getContext(), "播放失败", Toast.LENGTH_SHORT).show();
                        Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_trumpet_black);
                        holder.audioButton.setImageDrawable(drawable);
                    }
                    // 播放完成，改变按钮状态
                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            //mIsPlayState = !mIsPlayState;
                            Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_trumpet_black);
                            holder.audioButton.setImageDrawable(drawable);
                        }
                    });

                    Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_trumpet_yel);
                    holder.audioButton.setImageDrawable(drawable);
                }
                holder.audioButton.setEnabled(true);
            }
        });
        // 点击归档
        holder.fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Vibrator vibrator=(Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{0,40}, -1);

                String heading = holder.headline.getText().toString();
                memoryCardsList card = dbhelper.findCard(heading);
                boolean isfinish = card.isFinish();
                // 改按键颜色状态
                if (isfinish) {
                    Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_archive_black);
                    holder.fileButton.setImageDrawable(drawable);
                    dbhelper.restoreFinishCard(heading);
                }
                else {
                    Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_archive_yel);
                    holder.fileButton.setImageDrawable(drawable);
                    dbhelper.finishCard(heading);
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return myCardsList.size();
    }

}
