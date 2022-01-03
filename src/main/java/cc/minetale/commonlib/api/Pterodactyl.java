package cc.minetale.commonlib.api;

import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import com.mattmalec.pterodactyl4j.application.entities.PteroApplication;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Pterodactyl {

    @Getter private static PteroApplication pteroApplication;

    public static void init() {
        pteroApplication = PteroBuilder.createApplication("http://nyagang.gay/", "4wzMvqFLHsHJCfwS2OfPsHC6yJxpKLa2OaHmkyhIO1UxWudg");
    }

   public static CompletableFuture<List<ApplicationServer>> getServers() {
        return new CompletableFuture<List<ApplicationServer>>()
                .completeAsync(() -> {
                    var servers = pteroApplication.retrieveServers().execute();
                    var gameServers = new ArrayList<ApplicationServer>();

                    for(var server : servers) {
                        var id = server.getExternalId();

                        if (id != null && id.contains("atom")) {
                            gameServers.add(server);
                        }
                    }

                    return gameServers;
                });
   }

}
