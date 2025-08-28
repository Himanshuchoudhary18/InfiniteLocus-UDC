package utilsDatabase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;

public class ConnectionManagerFirebase {

    public static Firestore initializeFirestore() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("src/test/resources/firestore.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("<firebaseDatabaseURL>")
                .build();

        FirebaseApp.initializeApp(options);
        return FirestoreClient.getFirestore();
    }

    public static void main(String[] args) {
        try {
            Firestore db = initializeFirestore();
            System.out.println("Connected to Firestore successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}