package com.example.unforgettable.Bmob;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static cn.bmob.v3.Bmob.getApplicationContext;

public class Bmobhelper {

    public void upload() {
        String databasePath = "data/data/com.example.unforgettable/databases/MemoryCards.db";
        final BmobFile bmobFile = new BmobFile(new File(databasePath));
        // 上传文件
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "上传文件成功", Toast.LENGTH_LONG).show();
                    Log.v("Bmob","上传文件成功:" + bmobFile.getFileUrl());  //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    // 更新至用户表内
                    MyUser newUser = new MyUser();
                    newUser.setDatabase(bmobFile);
                    MyUser myUser = MyUser.getCurrentUser(MyUser.class);
                    newUser.update(myUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_LONG).show();
                            } else {
                                Log.e("Bmob", "更新失败" + e.toString());
                                Toast.makeText(getApplicationContext(), "更新失败", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "上传文件失败", Toast.LENGTH_LONG).show();
                    Log.v("Bmob","上传文件失败：" + e.getMessage());
                }
            }
            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });

    }

    public void download() {
        BmobQuery<MyUser> bmobQuery = new BmobQuery<>();
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        bmobQuery.addWhereEqualTo("objectId", myUser.getObjectId());
        bmobQuery.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> object, BmobException e) {
                if(e == null){
                    BmobFile dataBase = object.get(0).getDatabase();
                    if (dataBase != null)
                        downloadDataBase(dataBase);     // 调用bmobfile.download方法
                    else
                        Toast.makeText(getApplicationContext(), "云端无数据", Toast.LENGTH_SHORT).show();
//                    for (MyUser user: object) {
//                        if (user.getObjectId().equals(myUser.getObjectId())) {
//                            BmobFile dataBase = user.getDatabase();
//                            if (dataBase != null) {
//                                downloadDataBase(dataBase);     //调用bmobfile.download方法
//                                return;
//                            }
//                            else
//                                Toast.makeText(getApplicationContext(), "云端无数据", Toast.LENGTH_SHORT).show();
//                        }
//                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
                    Log.e("Bmob", "查询失败："+e.getMessage());
                }
            }
        });
    }

    // 下载云端数据库
    private void downloadDataBase(BmobFile file){
        // 允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        // File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
        String databasePath = "data/data/com.example.unforgettable/databases";
        File saveFile = new File(databasePath, file.getFilename());
        file.download(saveFile, new DownloadFileListener() {
            @Override
            public void onStart() {
                Toast.makeText(getApplicationContext(), "开始下载...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void done(String savePath,BmobException e) {
                if(e == null){
                    Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_LONG).show();
                    Log.v("Bmob","下载成功,保存路径:"+savePath);
                }else{
                    Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                    Log.e("BMOB", "下载失败："+e.getErrorCode()+","+e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                Log.i("Bmob","下载进度：" + value + "," + newworkSpeed);
            }
        });
    }

}
