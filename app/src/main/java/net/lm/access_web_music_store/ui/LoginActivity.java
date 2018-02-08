package net.lm.access_web_music_store.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.lm.access_web_music_store.R;
import net.lm.access_web_music_store.app.AppConstants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/1/24.
 */

public class LoginActivity extends Activity implements AppConstants {
    /**
     * 用户名编辑框
     */
    private EditText edtUsername;
    /**
     * 密码编辑框
     */
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 利用布局资源文件设置用户界面
        setContentView(R.layout.activity_login);

        // 通过资源标识获得控件实例
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
    }

    /**
     * 登录按钮单击事件处理方法
     *
     * @param view
     */
    public void doLogin(View view) {
        // 获取用户名
        String username = edtUsername.getText().toString().trim();
        // 获取密码
        String password = edtPassword.getText().toString().trim();

        // 用户名非空校验
        if (username.equals("")) {
            Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
            edtUsername.setFocusable(true);
            edtUsername.requestFocus();
            return;
        }

        // 密码非空校验
        if (password.equals("")) {
            Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            edtPassword.setFocusable(true);
            edtPassword.requestFocus();
            return;
        }

        // 定义登录网址字符串
        String strLoginUrl = MUSIC_SERVER_URL + "/login";
        // 执行登录异步任务，传入三个参数
        new LoginTask().execute(strLoginUrl, username, password);
    }

    /**
     * 登录异步任务
     */
    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // 定义结果字符串
            String result = "";

            // 获取登录网址字符串
            String strLoginUrl = params[0];
            // 创建post请求
            HttpPost request = new HttpPost(strLoginUrl);
            // 创建名值对列表
            List<NameValuePair> list = new ArrayList<>();
            // 将用户提交的数据添加到名值对列表
            list.add(new BasicNameValuePair("username", params[1]));
            list.add(new BasicNameValuePair("password", params[2]));
            try {
                // 将名值对列表封装成url编码格式实体作为请求参数
                request.setEntity(new UrlEncodedFormEntity(list, "utf-8"));
                // 创建http客户端
                HttpClient client = new DefaultHttpClient();
                // 执行post请求，获取响应对象
                HttpResponse response = client.execute(request);
                // 如果请求成功
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    // 获取响应数据实体
                    HttpEntity entity = response.getEntity();
                    // 将响应数据实体转换成字符串保存到结果字符串里
                    result = EntityUtils.toString(entity);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // 根据响应结果执行不同操作
            if (result.trim().equals("success")) {
                // 跳转到主界面
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                // 关闭登录界面
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "用户名或密码错误，登录失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 取消按钮单击事件处理方法
     *
     * @param view
     */
    public void doCancel(View view) {
        edtUsername.setText("");
        edtPassword.setText("");
        edtUsername.requestFocus();
    }
}
