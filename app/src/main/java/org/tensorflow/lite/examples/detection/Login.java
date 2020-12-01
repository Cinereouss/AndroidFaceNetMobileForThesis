package org.tensorflow.lite.examples.detection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.tensorflow.lite.examples.detection.response.LoginResponse;
import org.tensorflow.lite.examples.detection.tflite.SaveDataSet;

import java.util.Objects;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Login extends AppCompatActivity {
    Button btnLogin;
    EditText inputEmail, inputPassword;
    String loginFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = Login.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(Login.this, R.color.login));

        btnLogin = findViewById(R.id.btnLogin);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        btnLogin.setOnClickListener(view -> {
            login();
        });

        Intent intent = getIntent();
        loginFrom = intent.getStringExtra("login-from");
    }

    private void login() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (email.equals("") || password.equals("")) {
            Toasty.error(this, "Email hoặc mật khẩu còn trống", Toast.LENGTH_SHORT, true).show();
        } else {
            MyCustomDialog loadingSpinner = new MyCustomDialog(Login.this, "Đang xác thực tài khoản...");
            loadingSpinner.startLoadingDialog();

            Retrofit retrofit = APIClient.getClient();
            APIService callAPI = retrofit.create(APIService.class);
            Call<LoginResponse> call = callAPI.login(email, password);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    loadingSpinner.dismissDialog();
                    if(response.isSuccessful()) {
                        String token = response.body().getToken();
                        String teacherName = response.body().getData().getUser().getName();
                        String role = response.body().getData().getUser().getRole();
                        String beLongTo = response.body().getData().getUser().getBeLongTo();

                        if ((role.equals("admin") && loginFrom.equals("teacher")) || (role.equals("staff") && loginFrom.equals("teacher")) || (role.equals("teacher") && loginFrom.equals("admin"))) {
                            Toasty.error(Login.this, "Tài khoản này không có quyền hạn", Toast.LENGTH_SHORT, true).show();
                        } else {
                            if (loginFrom.equals("teacher")) {
                                SaveDataSet.saveToken(Login.this, token, teacherName, beLongTo);
                                Intent intent = new Intent(Login.this, Profile.class);
                                startActivity(intent);
                            } else {
                                SaveDataSet.saveTokenAdmin(Login.this, token, teacherName, beLongTo);
                                Intent intent = new Intent(Login.this, QRResult.class);
                                startActivity(intent);
                            }
                            Toasty.success(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT, true).show();
                            finish();
                        }

                    } else {
                        Toasty.error(Login.this, "Tài khoản không hợp lệ", Toast.LENGTH_LONG, true).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    loadingSpinner.dismissDialog();
                    Toasty.error(Login.this, "Lỗi ứng dụng, thử lại", Toast.LENGTH_SHORT, true).show();
                }
            });
        }
    }
}