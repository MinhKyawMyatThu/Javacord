package org.javacord.util.handler.message.reaction;

import com.fasterxml.jackson.databind.JsonNode;
import org.javacord.DiscordApi;
import org.javacord.entity.channel.ServerTextChannel;
import org.javacord.entity.message.Message;
import org.javacord.entity.message.impl.ImplMessage;
import org.javacord.event.message.reaction.ReactionRemoveAllEvent;
import org.javacord.listener.message.reaction.ReactionRemoveAllListener;
import org.javacord.util.gateway.PacketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles the message reaction remove all packet.
 */
public class MessageReactionRemoveAllHandler extends PacketHandler {

    /**
     * Creates a new instance of this class.
     *
     * @param api The api.
     */
    public MessageReactionRemoveAllHandler(DiscordApi api) {
        super(api, true, "MESSAGE_REACTION_REMOVE_ALL");
    }

    @Override
    public void handle(JsonNode packet) {
        api.getTextChannelById(packet.get("channel_id").asText()).ifPresent(channel -> {
            long messageId = packet.get("message_id").asLong();
            Optional<Message> message = api.getCachedMessageById(messageId);

            message.ifPresent(msg -> ((ImplMessage) msg).removeAllReactionsFromCache());

            ReactionRemoveAllEvent event = new ReactionRemoveAllEvent(api, messageId, channel);

            List<ReactionRemoveAllListener> listeners = new ArrayList<>();
            listeners.addAll(Message.getReactionRemoveAllListeners(api, messageId));
            listeners.addAll(channel.getReactionRemoveAllListeners());
            if (channel instanceof ServerTextChannel) {
                listeners.addAll(((ServerTextChannel) channel).getServer().getReactionRemoveAllListeners());
            }
            listeners.addAll(api.getReactionRemoveAllListeners());

            if (channel instanceof ServerTextChannel) {
                api.getEventDispatcher().dispatchEvent(((ServerTextChannel) channel).getServer(),
                        listeners, listener -> listener.onReactionRemoveAll(event));
            } else {
                api.getEventDispatcher().dispatchEvent(api, listeners, listener -> listener.onReactionRemoveAll(event));
            }
        });
    }

}