package TestCreator.login;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.ListIdentitiesRequest;
import software.amazon.awssdk.services.ses.model.ListIdentitiesResponse;
import software.amazon.awssdk.services.ses.model.SesException;
import software.amazon.awssdk.services.ses.model.VerifyEmailIdentityRequest;

public class EmailVerifier {

    public static Region EMAIL_REGION = Region.US_EAST_2;

    public static void verifyEmail(String email) {
        System.setProperty("aws.accessKeyId", System.getenv("AWS_SES_ACCESS_KEY"));
        System.setProperty("aws.secretAccessKey", System.getenv("AWS_SES_SECRET_ACCESS_KEY"));

        SesClient client = SesClient.builder()
                .region(EMAIL_REGION)
                .build();

        try {
            VerifyEmailIdentityRequest request = VerifyEmailIdentityRequest.builder()
                    .emailAddress(email)
                    .build();
            client.verifyEmailIdentity(request);
            System.out.println("A verification email has been sent to " + email);

        } catch (
                SesException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        client.close();
    }

    public static boolean isEmailVerified(String email){
        System.setProperty("aws.accessKeyId", System.getenv("AWS_SES_ACCESS_KEY"));
        System.setProperty("aws.secretAccessKey", System.getenv("AWS_SES_SECRET_ACCESS_KEY"));

        SesClient client = SesClient.builder()
                .region(EMAIL_REGION)
                .build();
        try {
            ListIdentitiesRequest request = ListIdentitiesRequest.builder().build();
            ListIdentitiesResponse response = client.listIdentities(request);
            return response.identities().contains(email.toLowerCase());
        } catch (SesException e) {
            System.out.println("Email verification failed: "+e.getMessage());
        } finally {
            client.close();
        }
        return false;
    }
}
