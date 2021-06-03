package ro.pub.cs.systems.eim.practicaltest02;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import org.json.*;

public class ClientThread extends Thread {

    private String address;
    private int port;

    private String pokemonName;
    private TextView tv_abilities;
    private TextView tv_types;
    private ImageView iv_pokemon;
    private Socket socket;

    public ClientThread(String address, int port, String pokemonName, TextView tv_abilities, TextView tv_types, ImageView iv_pokemon) {
        this.address = address;
        this.port = port;
        this.pokemonName = pokemonName;
        this.tv_abilities = tv_abilities;
        this.tv_types = tv_types;
        this.iv_pokemon = iv_pokemon;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            printWriter.println(pokemonName);
            printWriter.flush();

            String result = "";

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            if(result != null) {


                String jsonString = result ; //assign your JSON String here
                JSONObject obj = new JSONObject(jsonString);
              //  String pageName = obj.getJSONObject("pageInfo").getString("pageName");

                JSONArray arr = obj.getJSONArray("abilities" );
                String ability_name = "";
                for (int i = 0; i < arr.length(); i++)
                {
                    ability_name += arr.getJSONObject(i).getJSONObject("ability").getString("name") + ", ";

                }

                JSONArray arrr = obj.getJSONArray("types" );
                String types_name = "";
                for (int i = 0; i < arrr.length(); i++)
                {
                    types_name += arrr.getJSONObject(i).getJSONObject("type").getString("name") + ", ";

                }
                Bitmap bmImg = null;

                String iamge_name =obj.getJSONObject("sprites").getString("front_default");

                URL myfileurl =null;

                try
                {
                    myfileurl= new URL(iamge_name);

                }
                catch (MalformedURLException e)
                {

                    e.printStackTrace();
                }

                try
                {
                    HttpURLConnection conn= (HttpURLConnection)myfileurl.openConnection();

                    conn.setDoInput(true);
                    conn.connect();
                    int length = conn.getContentLength();
                    int[] bitmapData =new int[length];
                    byte[] bitmapData2 =new byte[length];
                    InputStream is = conn.getInputStream();
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    bmImg = BitmapFactory.decodeStream(is,null,options);



                    //dialog.dismiss();
                }
                catch(IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
//          Toast.makeText(PhotoRating.this, "Connection Problem. Try Again.", Toast.LENGTH_SHORT).show();
                }




                final String finalizedtypes= types_name;
                final String finalizedResult = ability_name;
                final Bitmap final_image= bmImg;
                this.tv_abilities.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_abilities.setText(finalizedResult);
                        tv_types.setText(finalizedtypes);
                        iv_pokemon.setImageBitmap(final_image);
                        Log.d(Constants.TAG, "I just got the content:" + finalizedResult);
                    }
                });



            }

        } catch (IOException | JSONException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
