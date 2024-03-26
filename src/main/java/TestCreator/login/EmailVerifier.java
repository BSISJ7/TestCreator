package TestCreator.login;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.ListIdentitiesRequest;
import software.amazon.awssdk.services.ses.model.ListIdentitiesResponse;
import software.amazon.awssdk.services.ses.model.SesException;
import software.amazon.awssdk.services.ses.model.VerifyEmailIdentityRequest;

public class EmailVerifier {

    public static final Region EMAIL_REGION = Region.US_EAST_2;

    public static void verifyEmail(String email) throws RuntimeException{
        SesClient client = SesClient.builder().region(EMAIL_REGION).build();

        try {
            VerifyEmailIdentityRequest request = VerifyEmailIdentityRequest.builder()
                    .emailAddress(email.toLowerCase()).build();
            client.verifyEmailIdentity(request);
        } catch (SesException e) {
            throw new RuntimeException(e);
        }
        client.close();
    }

    public static boolean isEmailVerified(String email){
        try (SesClient client = SesClient.builder()
                .region(EMAIL_REGION)
                .build()) {
            ListIdentitiesRequest request = ListIdentitiesRequest.builder().build();
            ListIdentitiesResponse response = client.listIdentities(request);
            return response.identities().contains(email.toLowerCase());
        } catch (SesException e) {
            System.out.println(STR."Email verification failed: \{e.getMessage()}");
        }
        return false;
    }
}
