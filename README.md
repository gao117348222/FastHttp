#FastHttp
此项目是模仿retorfit写的网络访问的小接口
调用方式如下
###1 增加接口类
```java 
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
```
###2 产生接口
```java
        FastHttp fastHttp=new FastHttp.Builder("http://www.gx303.com/").build();
        StudentApi api =fastHttp.create(StudentApi.class);
```
###3 调用接口
```java
 Call<Student> call=api.getStduentInfo("123456");
 //第一种方式，同步访问
 Student st1 = call.execute().getBody();
 //第二种方式,异步访问
  call.enqueue(new Callback<Student>() {
    @Override
    public void onResponse(Call<Student> call, Response<Student> response) {
    }

    @Override
    public void onFailure(Call<Student> call, Throwable t) {
    }
});
```

###4 注
>* 暂时只有两种访问模式，GET和POST
>* GET只能用@path来定义占位符
>* POST只能传入一个bean，并通过requsetbody传到服务器
