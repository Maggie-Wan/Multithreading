package com.example.student.Multithreading;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView mTextField,tv2,tv3,tv4;
    Handler handler;
    //宣告AsynTask物件
    MyAsynTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextField=(TextView)findViewById(R.id.mTextField);
        tv2=(TextView)findViewById(R.id.tv2);
        tv3=(TextView)findViewById(R.id.tv3);
        tv4=(TextView)findViewById(R.id.tv4);
        handler=new Handler();
        //task=new MyAsynTask(tv4);

    }
    public void countdown(View view) {
        //每秒鐘執行一次，總共執行30秒CountDownTimer，都是在主執行緒執行
        new CountDownTimer(5000, 1000) {
            //倒數時要run的程式
            public void onTick(long millisUntilFinished) {
                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                /*try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
            //結束後要run的程式
            public void onFinish() {
                mTextField.setText("done!");
            }
        }.start();
    }

    public void runonui(View view) {
        new Thread(){
            int i = 5;
            @Override
            public void run() {
                super.run();
                do {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv2.setText(String.valueOf(i));
                        }
                    });
                    i--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (i>0);


            }
        }.start();
    }


    public void onhandler(View view) {
        new Thread(){
            int i = 5;
            @Override
            public void run() {
                super.run();
                do {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tv3.setText(String.valueOf(i));
                        }
                    });
                    i--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (i>0);
            }
        }.start();
    }

    public void asyn(View view) {
        task=new MyAsynTask(tv4);
        //執行要用execute而非doinBackground
        task.execute(5);
    }

    public void cancel(View view) {
        //呼叫cacel() method
        /*cancel
        boolean cancel (boolean mayInterruptIfRunning)
        Attempts to cancel execution of this task. This attempt will fail if the task has already completed, already been cancelled, or could not be cancelled for some other reason.
                Calling this method will result in onCancelled(Object) being invoked on the UI thread after doInBackground(Object[]) returns.
        Parameters mayInterruptIfRunning	boolean: true if the thread executing this task should be interrupted; otherwise, in-progress tasks are allowed to complete.*/

                task.cancel(false);
    }

    //建立一個內部類別繼承AsynTask
    class MyAsynTask extends AsyncTask<Integer,Integer,String>{
        //因為要使用MainActivity的tv4但是不要設定static，就利用建構式把tv4傳進來
        TextView tv;
        public MyAsynTask(TextView tv){
            this.tv=tv;
        }

        //override doInBackground方法=>在背景執行
        //這邊的參數類型會和繼承時決定的傳入型別一樣，...是參數列表與陣列類似，可以一個或多個參數
        //ex:abc(int [ ] x):void; 或 abc(int... x):void;
        @Override
        protected String doInBackground(Integer... integers) {
            int n = integers[0];
            int i;
            for(i=n;i>=0;i--)
            {
                Log.d("TASK", "i:" + i);
                //publishProgress(Progress... values): to publish updates on the UI thread while the background computation is still running
                //參數:values Progress: The progress values to update the UI with.
                publishProgress(i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Returns true if this task was cancelled before it completed normally
                if (this.isCancelled() == true)
                {
                    break;
                }
            }
            return "OK";

        }

        @Override
        //在執行之後
        //會在doInBackground執行完之後才執行，而且是在主執行緒執行，並且doInBackground return的內容會丟到這邊的參數帶入
        //所以看log這邊會顯示ok
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("TASK", s);
        }

        @Override
        //進度更新，也是在主執行緒跑
        //這邊的Integer...values是MyAsynTask<>第二個帶入的參數類型，由publishProgerss()傳值過來
        //必須在 doInBackground加上publishProgress();
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tv.setText(String.valueOf(values[0]));

        }

        @Override //當呼叫cancel method，會run onCancelled()
        /*onCancelled
        Applications should preferably override onCancelled(Object). This method is invoked by the default implementation of onCancelled(Object).
        Runs on the UI thread after cancel(boolean) is invoked and doInBackground(Object[]) has finished.*/
        protected void onCancelled() {
            super.onCancelled();

        }
    }
}
