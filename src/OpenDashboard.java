import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class OpenDashboard {



    public static void launchDashboard() throws IOException {
        try {
            URI grafanaURI = new URI("http://localhost:3000/d/ads2s67/mushroom-tracker?orgId=1&from=now-6h&to=now&timezone=browser&refresh=10s");
            Desktop d = Desktop.getDesktop();
            d.browse(grafanaURI);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
