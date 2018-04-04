package chat.nupasd_ufpi.chat;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    Button b_IP, b_Mensagem;
    EditText et_IP, et_Mensagem;
    TextView t_Mensagem;
    String IP, mensg, txt;
    Handler hand = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23 && (ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_WIFI_STATE,
                    android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    android.Manifest.permission.WAKE_LOCK
            }, 0);
        }

        b_IP = (Button) findViewById(R.id.btn_IP);
        b_Mensagem = (Button) findViewById(R.id.btn_Mensagem);
        et_IP = (EditText) findViewById(R.id.edt_IP);
        et_Mensagem = (EditText) findViewById(R.id.edt_Mensagem);
        t_Mensagem = (TextView) findViewById(R.id.txt_ExibirMensagem);

        b_IP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IP = et_IP.getText().toString();

                Toast.makeText(MainActivity.this, "IP salvo: "+IP, Toast.LENGTH_SHORT).show();
            }
        });


        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    ServerSocket ss = new ServerSocket(9000);

                    while (true) {
                        //Server is waiting for client here, if needed
                        Socket s = ss.accept();
                        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));

                        mensg = input.readLine();

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        updateUI(mensg);

                        s.close();
                    }
                    //ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        thread.start();




        b_Mensagem.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                //final Handler handler = new Handler();
                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        enviaMensagem();
                    }
                });



            }
        });


    }

    public void updateUI(final String str){
        hand.post(new Runnable() {
            @Override
            public void run() {

                String s = t_Mensagem.getText().toString();
                if (IP.trim().length() != 0)
                    t_Mensagem.setText(s + "\n" + "Enviado por Client: " +str);
            }
        });
    }


    public void enviaMensagem(){


                    try {

                        //Adicionando o IP configurável aqui

                        Socket s = new Socket(IP, 9002);

                        OutputStream out = s.getOutputStream();

                        PrintWriter output = new PrintWriter(out);

                        output.println(et_Mensagem.getText().toString());
                        output.flush();

                        output.close();
                        out.close();
                        s.close();
                    } catch (IOException ver) {
                        Toast.makeText(MainActivity.this, "Erro no envio da mensagem", Toast.LENGTH_SHORT).show();
                    }


    }
}
