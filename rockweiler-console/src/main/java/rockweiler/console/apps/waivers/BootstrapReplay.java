/**
 * Copyright Vast 2014. All Rights Reserved.
 *
 * http://www.vast.com
 */
package rockweiler.console.apps.waivers;

import rockweiler.console.core.MessageListener;
import rockweiler.player.jackson.Schema;
import rockweiler.repository.JacksonPlayerRepository;
import rockweiler.repository.PlayerRepository;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class BootstrapReplay {
    private final Requests.AddPlayer addRequest = new Requests.AddPlayer();

    private final MessageListener<? super Requests.AddPlayer> listener;

    public BootstrapReplay(MessageListener<? super Requests.AddPlayer> listener) {
        this.listener = listener;
    }

    public void replay() {
        PlayerRepository<Schema.Player> repository = JacksonPlayerRepository.create("/master.player.json");

        replay(repository);

        onFinish();
    }

    public void replay(PlayerRepository<Schema.Player> repository) {
        for(Schema.Player player : repository.getPlayers()) {
            addRequest.addPlayer = player;
            listener.onMessage(addRequest);
        }
    }

    void onFinish() {

    }
}
