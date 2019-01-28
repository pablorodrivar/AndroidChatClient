package com.example.pablo.androidchatclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String username, serverIP = "192.168.0.161";
    int Port = 5000;
    Socket sock;
    BufferedReader reader;
    PrintWriter writer;
    ArrayList<String> usersList = new ArrayList();
    Boolean isConnected = false;
    TextView messages_view;
    EditText editText;
    ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messages_view = findViewById(R.id.messages_view);
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);

        setEventsHandler();
    }

    private void setEventsHandler() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nothing = "";
                if ((editText.getText()).equals(nothing)) {
                    editText.setText("");
                    editText.requestFocus();
                } else {
                    try {
                       writer.println(username + ":" + editText.getText() + ":" + "Chat");
                       writer.flush(); // flushes the buffer
                    } catch (Exception ex) {
                        messages_view.append("Message was not sent. \n");
                    }
                    editText.setText("");
                    editText.requestFocus();
                }

                editText.setText("");
                editText.requestFocus();
            }
        });
    }

    public void sendText(View v){
        ChatTask chatTask = new ChatTask();
        chatTask.execute()
    }

    class ChatTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            String[] data;
            String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat";

            try {
                while ((stream = reader.readLine()) != null) {
                    data = stream.split(":");

                     if (data[2].equals(chat)) {
                        messages_view.append(data[0] + ": " + data[1] + "\n");
                        //messages_view.setCaretPosition(messages_view.getDocument().getLength());
                    } else if (data[2].equals(connect)){
                        messages_view.();
                        userAdd(data[0]);
                    } else if (data[2].equals(disconnect)) {
                        userRemove(data[0]);
                    } else if (data[2].equals(done)) {
                        onlineUsersArea.setText("");
                        writeUsers();
                        usersList.clear();
                    }
                }
           }catch(Exception ex) {
           }
            return null;
        }
    }

     public class IncomingReader extends BroadcastReceiver {

         @Override
         public void onReceive(Context context, Intent intent) {
             String[] data;
            String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat";

            try {
                while ((stream = reader.readLine()) != null) {

                    data = stream.split(":");

                     if (data[2].equals(chat)) {

                        messages_view.append(data[0] + ": " + data[1] + "\n");
                        //messages_view.setCaretPosition(chatTextArea.getDocument().getLength());

                    } else if (data[2].equals(connect)){
                        messages_view.removeAll();
                        userAdd(data[0]);
                    } else if (data[2].equals(disconnect)) {
                        userRemove(data[0]);
                    } else if (data[2].equals(done)) {
                        onlineUsersArea.setText("");
                        writeUsers();
                        usersList.clear();
                    }

                }
           }catch(Exception ex) {
           }
         }
     }

    public void userAdd(String data) {
         usersList.add(data);
     }

    public void userRemove(String data) {
         messages_view.append(data + " has disconnected.\n");
     }

    public void writeUsers() {
         String[] tempList = new String[(usersList.size())];
         usersList.toArray(tempList);
         for (String token:tempList) {
             onlineUsersArea.append(token + "\n");
         }
     }

    public void sendDisconnect() {

       String bye = (username + ": :Disconnect");
        try{
            writer.println(bye); // Sends server the disconnect signal.
            writer.flush(); // flushes the buffer
        } catch (Exception e) {
            messages_view.append("Could not send Disconnect message.\n");
        }

      }

    public void Disconnect() {

        try {
               messages_view.append("Disconnected.\n");
               sock.close();
        } catch(Exception ex) {
               messages_view.append("Failed to disconnect. \n");
        }
        isConnected = false;
        usernameField.setEditable(true);
        onlineUsersArea.setText("");
      }
}
