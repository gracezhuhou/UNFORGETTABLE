package com.example.unforgettable.Bmob;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unforgettable.Dbhelper;
import com.example.unforgettable.LitepalTable.memoryCardsList;
import com.example.unforgettable.R;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static cn.bmob.v3.Bmob.getApplicationContext;
import static org.litepal.LitePalApplication.getContext;

public class Bmobhelper {

    /*
     *数据库
     */

    // 上传数据库文件
    public void upload() {
        String databasePath = "data/data/com.example.unforgettable/databases/MemoryCards.db";
        final BmobFile bmobFile = new BmobFile(new File(databasePath));
        // 上传文件
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "上传文件成功", Toast.LENGTH_SHORT).show();
                    Log.v("Bmob","上传文件成功:" + bmobFile.getFileUrl());  //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    // 更新至用户表内
                    MyUser newUser = new MyUser();
                    newUser.setDatabase(bmobFile);
                    MyUser myUser = MyUser.getCurrentUser(MyUser.class);
                    newUser.update(myUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "云端更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("Bmob", "更新失败" + e.toString());
                                Toast.makeText(getApplicationContext(), "云端更新失败，请重新登录", Toast.LENGTH_SHORT).show();

                                reLogin();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "上传文件失败", Toast.LENGTH_SHORT).show();
                    Log.v("Bmob","上传文件失败：" + e.getMessage());
                }
            }
            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
                Toast.makeText(getApplicationContext(), "上传中："+ value + "%", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // 云端数据库
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
                    Toast.makeText(getApplicationContext(), "查询失败,请重新登录", Toast.LENGTH_SHORT).show();
                    Log.e("Bmob", "查询失败："+e.getMessage());
                    reLogin();
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
                    Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_SHORT).show();
                    Log.v("Bmob","下载成功,保存路径:"+savePath);

                    LitePal.getDatabase();
                    //动态创建数据库 避免SD卡删除数据库文件 造成的CRUD报错
                    LitePalDB litePalDB = LitePalDB.fromDefault("MemoryCards");
                    LitePal.use(litePalDB);

                }else{
                    Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                    Log.e("BMOB", "下载失败："+e.getErrorCode()+","+e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                //Toast.makeText(getApplicationContext(), "下载进度：" + value + "%," + newworkSpeed, Toast.LENGTH_SHORT).show();
                Log.i("Bmob","下载进度：" + value + "%," + newworkSpeed);
            }
        });
    }

    /*
     *
     *头像
     *
     */
    // 上传头像
    public void uploadPic() {
        // TODO: 路径
        String picPath = Environment.getExternalStorageDirectory().getPath() + "/userPic.jpg";
        Log.v("Bmob","databasePath");
        //Toast.makeText(getApplicationContext(), picPath, Toast.LENGTH_LONG).show();
        final BmobFile bmobFile = new BmobFile(new File(picPath));
        // 上传文件
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "上传头像成功", Toast.LENGTH_SHORT).show();
                    Log.v("Bmob","上传头像成功:" + bmobFile.getFileUrl());  //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    // 更新至用户表内
                    MyUser newUser = new MyUser();
                    newUser.setPicture(bmobFile);
                    MyUser myUser = MyUser.getCurrentUser(MyUser.class);
                    newUser.update(myUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "云端更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("Bmob", "云端更新失败" + e.toString());
                                Toast.makeText(getApplicationContext(), "云端更新失败,请重新登录", Toast.LENGTH_SHORT).show();
                                reLogin();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "上传头像失败", Toast.LENGTH_SHORT).show();
                    Log.v("Bmob","上传头像失败：" + e.getMessage());
                }
            }
            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
                Toast.makeText(getApplicationContext(), "上传中："+ value + "%", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // 云端
    public void downloadPic() {
        BmobQuery<MyUser> bmobQuery = new BmobQuery<>();
        MyUser myUser = MyUser.getCurrentUser(MyUser.class);
        bmobQuery.addWhereEqualTo("objectId", myUser.getObjectId());
        bmobQuery.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> object, BmobException e) {
                if(e == null){
                    BmobFile userPic = object.get(0).getPicture();
                    if (userPic != null)
                        downloadPic(userPic);     // 调用bmobfile.download方法
                    else
                        Toast.makeText(getApplicationContext(), "云端无头像", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "查询用户失败,请重新登录", Toast.LENGTH_SHORT).show();
                    Log.e("Bmob", "查询用户失败："+e.getMessage());
                    reLogin();
                }
            }
        });
    }

    // 下载云端头像
    private void downloadPic(BmobFile file){
        // 允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        // File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
        // TODO: 路径
        String picPath = Environment.getExternalStorageDirectory().getPath();
        File saveFile = new File(picPath, file.getFilename());
        file.download(saveFile, new DownloadFileListener() {
            @Override
            public void onStart() {
                Toast.makeText(getApplicationContext(), "开始下载...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void done(String savePath,BmobException e) {
                if(e == null){
                    Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_SHORT).show();
                    Log.v("Bmob","下载成功,保存路径:"+savePath);
                }else{
                    Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                    Log.e("BMOB", "下载失败："+e.getErrorCode()+","+e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                //Toast.makeText(getApplicationContext(), "下载进度：" + value + "%," + newworkSpeed, Toast.LENGTH_SHORT).show();
                Log.i("Bmob","下载进度：" + value + "%," + newworkSpeed);
            }
        });
    }

    // 登出前上传数据库
    public void logout() {
        String databasePath = "data/data/com.example.unforgettable/databases/MemoryCards.db";
        final BmobFile bmobFile = new BmobFile(new File(databasePath));
        // 上传文件
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "上传文件成功", Toast.LENGTH_SHORT).show();
                    Log.v("Bmob","上传文件成功:" + bmobFile.getFileUrl());  //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    // 更新至用户表内
                    MyUser newUser = new MyUser();
                    newUser.setDatabase(bmobFile);
                    MyUser myUser = MyUser.getCurrentUser(MyUser.class);
                    newUser.update(myUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
                                MyUser.logOut();    //清除缓存用户对象
                            } else {
                                Log.e("Bmob", "更新失败" + e.toString());
                                Toast.makeText(getApplicationContext(), "更新失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "上传文件失败", Toast.LENGTH_SHORT).show();
                    Log.v("Bmob","上传文件失败：" + e.getMessage());
                }
            }
            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
                Toast.makeText(getApplicationContext(), "上传中："+ value + "%", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void reLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View textEntryView = factory.inflate(R.layout.activity_tab_add, null);

        builder.setTitle("用户登录已失效，请输入密码重新登录");
        builder.setView(textEntryView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                Vibrator vibrator = (Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{0, 40}, -1);

                EditText passwordText =  textEntryView.findViewById(R.id.passwordText);
                //showDialog("标签 ："  + tabText.getText().toString() );
                MyUser userlogin = new MyUser();
                MyUser myUser = MyUser.getCurrentUser(MyUser.class);
                userlogin.setUsername(myUser.getEmail());
                userlogin.setPassword(passwordText.getText().toString());
                userlogin.login(new SaveListener<MyUser>() {
                    @Override
                    public void done(MyUser myUser, BmobException e) {
                        if (e == null) {
                            Log.v("Bmob", myUser.getNickname() + "重新登录成功");
                        }
                        else {
                            Log.e("Bmob", "登录失败，原因: ", e);
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                            Vibrator vibrator = (Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
                            vibrator.vibrate(new long[]{0, 100}, -1);
                        }
                    }
                });
            }

        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Vibrator vibrator = (Vibrator)getContext().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{0, 40}, -1);

                dialog.dismiss();
            }
        });
        builder.show();
    }

}
