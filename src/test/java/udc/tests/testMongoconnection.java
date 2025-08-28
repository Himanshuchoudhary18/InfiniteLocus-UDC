package udc.tests;

import org.testng.annotations.Test;
import utilsWeb.CommonFunctionsWeb;
import java.util.List;

public class testMongoconnection {
    @Test
    public void testFetchLatestOTP() {
        String latestOTP = CommonFunctionsWeb.getLatestOTP(5);
        if (latestOTP != null)
        {
            System.out.println("Fetched OTP: " + latestOTP);
        }
        else
        {
            System.out.println("Could not fetch OTP.");
        }
        List<String> collectionNames = CommonFunctionsWeb.getAllCollectionNames();
        System.out.println("\n MongoDB Collections:");

        if (!collectionNames.isEmpty())
        {
            for (int i = 0; i < collectionNames.size(); i++)
            {
                System.out.println((i + 1) + ". " + collectionNames.get(i));
            }
            System.out.println("Total collections: " + collectionNames.size());
        }
        else
        {
            System.out.println(" No collections found in the database.");
        }
        System.out.println("\n");
    }
}
