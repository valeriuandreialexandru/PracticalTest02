package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_load_url = findViewById(R.id.btn_load_url);
        Button btn_connect_srvr = findViewById(R.id.bt_connect);

        final EditText et_pokemon = findViewById(R.id.et_pokemon);
        final EditText et_srvr_port = findViewById(R.id.et_port);

        final TextView tv_abilities = findViewById(R.id.tv_abilities);
        final TextView tv_types = findViewById(R.id.tv_types);

        final ImageView iv_pokemon = findViewById(R.id.imageView);

        btn_connect_srvr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverPort = et_srvr_port.getText().toString();
                if (serverPort == null || serverPort.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                serverThread = new ServerThread(Integer.parseInt(serverPort));
                if (serverThread.getServerSocket() == null) {
                    Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                    return;
                }
                serverThread.start();
            }
        });

        btn_load_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clientAddress = "127.0.0.1";
                String clientPort = et_srvr_port.getText().toString();
                if (clientAddress == null || clientAddress.isEmpty()
                        || clientPort == null || clientPort.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (serverThread == null || !serverThread.isAlive()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String requestedPokemon = et_pokemon.getText().toString();

                if (requestedPokemon == null || requestedPokemon.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] No pokemon name", Toast.LENGTH_SHORT).show();
                    return;
                }

                clientThread = new ClientThread(
                        clientAddress, Integer.parseInt(clientPort), requestedPokemon, tv_abilities, tv_types, iv_pokemon
                );
                clientThread.start();
            }
        });

    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}