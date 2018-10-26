package se.mau.ag2656.p2worldandfriends;

import android.location.Location;
import android.text.Editable;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class UserClient {
    private String username, serverIP = "195.178.227.53", id;
    private int port = 7117;
    private List<String> groupList = new ArrayList<String>();

    private Socket clientSocket;
    private DataInputStream dataInputStream;
    private InputStream inputStream;
    private DataOutputStream dataOutputStream;
    private OutputStream outputStream;

    private boolean listening = true;

    public UserClient(String username) {
        this.username = username;
        connectToServer();
        activateStreams();
        listenToServer();
    }

    public void sendToServer(final JSONObject jsonObject) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("type", "register");
//                    jsonObject.put("group", "Filips grupp");
//                    jsonObject.put("member", username);


                    dataOutputStream.writeUTF(jsonObject.toString());
                    System.out.println("### " + "Sent JSON to server: " + jsonObject.toString() + " ###");
                } catch (Exception e) {
                    System.out.println("### " + "Failed to send to server" +" ###");
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void connectToServer() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("### Trying to connect... ###");
                    clientSocket = new Socket(serverIP, port);
                    System.out.println("### Successfully connected to chat server port " + clientSocket.getPort() + " on host "
                            + clientSocket.getInetAddress() + ". ###");
//                    listenToServer();
//                    sendToServer();
                } catch (IOException e) {
                    System.out.println("### Connection to server failed ###");
                    e.printStackTrace();
                }
            }
        });
        t.start();

        try{
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void activateStreams() {
        try {
            inputStream = clientSocket.getInputStream();
            dataInputStream = new DataInputStream(inputStream);
            outputStream = clientSocket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            Log.d(TAG, "activateStreams: " + dataInputStream.toString());
            Log.d(TAG, "### Streams are activated ###");

        } catch (Exception e) {
            Log.d(TAG, "### Streams didnt activate ###");
            e.printStackTrace();
        }

    }

    private void listenToServer() {
        Thread listenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String message;
//                        System.out.println("### " + message + " ###");

                            message = dataInputStream.readUTF();
                            JSONObject json = new JSONObject(message);
                            String type = json.getString("type");
//                            System.out.println("### Recieved JSON from sever:" + json.toString() + " ###");

                            switch (type) {
                                case "register":
                                    id = json.getString("id");
                                    groupList.add(id); // varje grupp man hoppar in i har ett unikt id.
                                    System.out.println("### Joinat grupp: " + json.get("group") + " med id: " + id + " ###");
                                    break;
                                case "unregiser":
                                    System.out.println("### Leavat grupp med id: " + json.get("id")+ " ###");
                                    break;
                                case "members":

                                    break;
                                case "location":

                                    break;
                                case "locations":
                                    break;

                            }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        listenerThread.start();

        try {
//            listenerThread.join(); //funkar inte, appen blir helt blank. Ska inte användas, appen ska hela tiden lyssna från servern.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void killConnection() {
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void JSONMyLocation(Location location) {
        if (!groupList.isEmpty()) {
            for (String s : groupList) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "location");
                    jsonObject.put("id", s);
                    jsonObject.put("longitude", Double.toString(location.getLongitude()));
                    jsonObject.put("latitude", Double.toString(location.getLatitude()));
                    sendToServer(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public void JSONMyGroup(String groupName) {
        if(!groupName.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", "register");
                jsonObject.put("group", groupName);
                jsonObject.put("member", "Juan Carlos");
                sendToServer(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
