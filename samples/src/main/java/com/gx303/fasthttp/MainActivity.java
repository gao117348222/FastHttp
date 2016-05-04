package com.gx303.fasthttp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gx303.Call;
import com.gx303.Callback;
import com.gx303.FastHttp;
import com.gx303.Response;
import com.gx303.http.GET;
import com.gx303.http.POST;
import com.gx303.http.Path;

import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




    }
    public void ui_test1(View v)
    {
        FastHttp fastHttp=new FastHttp.Builder("http://www.gx303.com/").build();
        StudentApi api =fastHttp.create(StudentApi.class);
        final Call<Student> call=api.getStduentInfo("123456");
        final Call<Integer> call1=api.getStduentXh("gx303");
        final Call<ArrayList<String>> call2=api.getStduentNames("A");

        Student student=new Student();
        student.setSex("男");
        student.setName("gx303");
        student.setXh("123454");
        final Call<Student> call3=api.postStduent(student);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    Student st1 = call.execute().getBody();
                   Log.e("Unity",st1.getXh()+st1.getName()+st1.getSex());
                    int xh=call1.execute().getBody();
                    Log.e("Unity","学号:"+xh);
                    ArrayList<String> names=call2.execute().getBody();
                    for(String s1:names)
                    {
                        Log.e("Unity","姓名:"+s1);
                    }
                    Student st2 = call3.execute().getBody();
                    Log.e("Unity","学生信息"+st2.getXh()+st2.getName()+st2.getSex());
                }
                catch (Exception e)
                {
                    Log.e("Unity",e.toString());
                }
            }
        }).start();

    }
    public void ui_test2(View v)
    {
        FastHttp fastHttp=new FastHttp.Builder("http://www.gx303.com/").build();
        StudentApi api =fastHttp.create(StudentApi.class);
        final Call<Student> call=api.getStduentInfo("123456");
//        final Call<Integer> call1=api.getStduentXh("gx303");
//        final Call<ArrayList<String>> call2=api.getStduentNames("A");
//
//        Student student=new Student();
//        student.setSex("男");
//        student.setName("gx303");
//        student.setXh("123454");
//        final Call<Student> call3=api.postStduent(student);

        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                Student st1 =response.getBody();
                Log.e("Unity","学生信息:"+st1.getXh()+st1.getName()+st1.getSex());
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                Log.e("Unity",t.toString());
            }
        });
    }
    interface StudentApi
    {
        @GET("getStduentInfo.php?xh={xh}")
        Call<Student> getStduentInfo(@Path("xh") String xh);//通过学号获取学生信息
        @GET("getStduentXh.php?name={name}")
        Call<Integer> getStduentXh(@Path("name") String name);//通过姓名获取学号
        @GET("getStduentNames.php?classname={classname}")
        Call<ArrayList<String>> getStduentNames(@Path("classname")String classname);//通过班级名称获取这个班级的学生名
        @POST("postTest.php")
        Call<Student> postStduent(Student student);//传入学生返回相同的学生，测试接口
    }
    class Student extends Man
    {
        private String xh;

        public String getXh() {
            return xh;
        }

        public void setXh(String xh) {
            this.xh = xh;
        }
    }
    class Man
    {
        private String name;
        private String sex;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }
    }
}
