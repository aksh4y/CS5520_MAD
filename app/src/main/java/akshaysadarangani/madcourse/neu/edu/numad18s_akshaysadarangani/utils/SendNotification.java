package akshaysadarangani.madcourse.neu.edu.numad18s_akshaysadarangani.utils;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SendNotification {

    // Please add the server key from your firebase console in the follwoing format "key=<serverKey>"
    private static final String SERVER_KEY = "key=AAAAhgDE2QA:APA91bEvfaFwKmkcJYj6wiB-el6iWzkNUtbJlq79cGfuTBeYmL-Ios6RG3WZi6FNQDeVwAS1hoDZibKawxqrwiXqumbbcFBLR3wMTJIa9b51YP0NGfwgacjNJabuaJHSUECz0_sfqFK4_XBSIccPOBKORrZiUf1Y7A";


    public void sendMessageToGlobal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToGlobalHelper();
            }
        }).start();
    }

    public void sendMessageToDevice(final String targetToken, final String userName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToDeviceHelper(targetToken, userName);
            }
        }).start();
    }

    private void sendMessageToGlobalHelper(){
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("message", "There's A New Leader On The Charts");
            jNotification.put("body", "Play To Win Back Your Title");

            // Populate the Payload object.
            // Note that "to" is a topic, not a token representing an app instance
            jPayload.put("to", "/topics/global");
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);

            // Open the HTTP connection and send the payload
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            int status = conn.getResponseCode();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();

            final String resp = convertStreamToString(inputStream);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Pushes a notification to a given device-- in particular, this device,
     * because that's what the instanceID token is defined to be.
     */
    public void sendMessageToDeviceHelper(String targetToken, String userName) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("title", "Kudos");
            jNotification.put("body", "Congrats on your high score from " + userName + "!");

            jPayload.put("to", targetToken); // CLIENT_REGISTRATION_TOKEN);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);


            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}