package comp5216.sydney.edu.au.afinal.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageTask extends AsyncTask<String,Void, Bitmap> {
    private CallBack callBack;
    private String imgUrl;
    public ImageTask(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        imgUrl = strings[0];
        return getImageFromServer(strings[0]);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if(callBack!=null)
            callBack.getResults(result,imgUrl);
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
    private Bitmap getImageFromServer(String path){
        try {
            URL url = new URL(path);
            HttpURLConnection connection =(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET"); //默认是get请求，当写POST时便是post请求
            connection.setConnectTimeout(5000); //设置访问超时的时间。
            if(200 == connection.getResponseCode()){  //获取响应码
                String type = connection.getContentType();
                int length = connection.getContentLength();
                InputStream is = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public interface CallBack{
        void getResults(Bitmap result,String url);
    }


}
