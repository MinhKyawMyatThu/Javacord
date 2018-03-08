package org.javacord.entity.message;

import org.javacord.AccountType;
import org.javacord.DiscordApi;
import org.javacord.entity.DiscordEntity;
import org.javacord.entity.Icon;
import org.javacord.entity.channel.Categorizable;
import org.javacord.entity.server.Server;
import org.javacord.entity.user.User;
import org.javacord.entity.webhook.Webhook;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * This class represents either a user or a webhook.
 */
public interface MessageAuthor extends DiscordEntity {

    /**
     * Gets the message.
     *
     * @return The message.
     */
    Message getMessage();

    /**
     * Gets the name of the author.
     *
     * @return The name of the author.
     */
    String getName();

    /**
     * Gets the display name of the author.
     *
     * @return The display name of the author.
     */
    default String getDisplayName() {
        Optional<Server> server = getMessage().getServer();
        Optional<User> user = asUser();
        if (user.isPresent()) {
            return server.map(s -> user.get().getDisplayName(s)).orElseGet(() -> user.get().getName());
        }
        return getName();
    }

    /**
     * If the author is a user, gets the discriminated name of the user, e. g. {@code Bastian#8222},
     * otherwise just gets the name of the author.
     *
     * @return The discriminated name of the user or the name of the author.
     */
    default String getDiscriminatedName() {
        return getDiscriminator().map(discriminator -> getName() + "#" + discriminator).orElseGet(this::getName);
    }

    /**
     * Gets the discriminator of the author if the author is a user.
     *
     * @return The discriminator of the author if the author is a user.
     */
    Optional<String> getDiscriminator();

    /**
     * Gets the avatar of the author.
     *
     * @return The avatar of the author.
     */
    Icon getAvatar();

    /**
     * Checks if the author of the message is a user.
     *
     * @return Whether the author is a user or not.
     */
    boolean isUser();

    /**
     * Checks if the author is the owner of the current account.
     * Always returns <code>false</code> if logged in to a user account.
     *
     * @return Whether the author is the owner of the current account.
     */
    default boolean isBotOwner() {
        return getApi().getAccountType() == AccountType.BOT && isUser() && getApi().getOwnerId() == getId();
    }

    /**
     * Checks if the author can create new channels on the server where the message was sent.
     * Always returns {@code false} if the author is not a user or the message was not sent on a server.
     *
     * @return Whether the author can create channels on the server or not.
     */
    default boolean canCreateChannelsOnServer() {
        return getMessage()
                .getServer()
                .flatMap(server -> asUser().map(server::canCreateChannels))
                .orElse(false);
    }

    /**
     * Checks if the author can view the audit log of the server where the message was sent.
     * Always returns {@code false} if the author is not a user or the message was not sent on a server.
     *
     * @return Whether the author can view the audit log of the server or not.
     */
    default boolean canViewAuditLogOfServer() {
        return getMessage()
                .getServer()
                .flatMap(server -> asUser().map(server::canViewAuditLog))
                .orElse(false);
    }

    /**
     * Checks if the author can change its own nickname in the server where the message was sent.
     * Always returns {@code false} if the author is not a user or the message was not sent on a server.
     *
     * @return Whether the author can change its own nickname in the server or not.
     */
    default boolean canChangeOwnNicknameOnServer() {
        return getMessage()
                .getServer()
                .flatMap(server -> asUser().map(server::canChangeOwnNickname))
                .orElse(false);
    }

    /**
     * Checks if the author can manage nicknames on the server where the message was sent.
     * Always returns {@code false} if the author is not a user or the message was not sent on a server.
     *
     * @return Whether the author can manage nicknames on the server or not.
     */
    default boolean canManageNicknamesOnServer() {
        return getMessage()
                .getServer()
                .flatMap(server -> asUser().map(server::canManageNicknames))
                .orElse(false);
    }

    /**
     * Checks if the author can manage emojis on the server where the message was sent.
     * Always returns {@code false} if the author is not a user or the message was not sent on a server.
     *
     * @return Whether the author can manage emojis on the server or not.
     */
    default boolean canManageEmojisOnServer() {
        return getMessage()
                .getServer()
                .flatMap(server -> asUser().map(server::canManageEmojis))
                .orElse(false);
    }

    /**
     * Checks if the author can manage the server where the message was sent.
     * Always returns {@code false} if the author is not a user or the message was not sent on a server.
     *
     * @return Whether the author can manage the server or not.
     */
    default boolean canManageServer() {
        return getMessage()
                .getServer()
                .flatMap(server -> asUser().map(server::canManage))
                .orElse(false);
    }

    /**
     * Checks if the author can kick users from the server where the message was sent.
     * Always returns {@code false} if the author is not a user or the message was not sent on a server.
     *
     * @return Whether the author can kick users from the server or not.
     */
    default boolean canKickUsersFromServer() {
        return getMessage()
                .getServer()
                .flatMap(server -> asUser().map(server::canKickUsers))
                .orElse(false);
    }

    /**
     * Checks if the author can kick the user from the server where the message was sent.
     * This methods also considers the position of the user roles.
     * Always returns {@code false} if the author is not a user or the message was not sent on a server.
     *
     * @param userToKick The user which should be kicked.
     * @return Whether the author can kick the user from the server or not.
     */
    default boolean canKickUserFromServer(User userToKick) {
        return getMessage()
                .getServer()
                .flatMap(server -> asUser().map(user -> server.canKickUser(user, userToKick)))
                .orElse(false);
    }

    /**
     * Checks if the author can ban users from the server where the message was sent.
     * Always returns {@code false} if the author is not a user or the message was not sent on a server.
     *
     * @return Whether the author can ban users from the server or not.
     */
    default boolean canBanUsersFromServer() {
        return getMessage()
                .getServer()
                .flatMap(server -> asUser().map(server::canBanUsers))
                .orElse(false);
    }

    /**
     * Checks if the author can ban the user from the server where the message was sent.
     * This methods also considers the position of the user roles.
     * Always returns {@code false} if the author is not a user or the message was not sent on a server.
     *
     * @param userToBan The user which should be banned.
     * @return Whether the author can ban the user from the server or not.
     */
    default boolean canBanUserFromServer(User userToBan) {
        return getMessage()
                .getServer()
                .flatMap(server -> asUser().map(user -> server.canBanUser(user, userToBan)))
                .orElse(false);
    }

    /**
     * Checks if the author can see the channel where the message was sent.
     * In private chats (private channel or group channel) this always returns {@code true} if the user is
     * part of the chat.
     * Always returns {@code false} if the author is not a user or the message was not sent on a server.
     *
     * @return Whether the author can see the channel or not.
     */
    default boolean canSeeChannel() {
        return asUser()
                .map(getMessage().getChannel()::canSee)
                .orElse(false);
    }

    /**
     * Checks if the author can see all channels in the category of the channel where the message was sent.
     * Always returns {@code false} if the author is not a user.
     * Always returns {@code true} if the channel is not categorizable or has no category.
     *
     * @return Whether the user can see all channels in this category or not.
     */
    default boolean canSeeAllChannelsInCategory() {
        return getMessage()
                .getChannel()
                .asCategorizable()
                .flatMap(Categorizable::getCategory)
                .map(channelCategory -> asUser().map(channelCategory::canSeeAll).orElse(false))
                .orElse(true);
    }

    /**
     * Checks if the author can create an instant invite to the channel where the message was sent.
     * Always returns {@code false} if the author is not a user or the message was not sent on a server.
     *
     * @return Whether the author can create an instant invite to the channel or not.
     */
    default boolean canCreateInstantInviteToTextChannel() {
        return getMessage()
                .getChannel()
                .asServerChannel()
                .flatMap(serverChannel -> asUser().map(serverChannel::canCreateInstantInvite))
                .orElse(false);
    }

    /**
     * Checks if the author can send messages in the channel where the message was sent.
     * In private chats (private channel or group channel) this always returns {@code true} if the user is
     * part of the chat.
     * Please notice, this does not check if a user has blocked private messages!
     * Always returns {@code false} if the author is not a user.
     *
     * @return Whether the author can write messages in the channel or not.
     */
    default boolean canWriteInTextChannel() {
        return getMessage()
                .getChannel()
                .asTextChannel()
                .map(textChannel -> asUser().map(textChannel::canWrite).orElse(false))
                .orElseThrow(AssertionError::new);
    }

    /**
     * Checks if the author can use external emojis in the channel where the message was sent.
     * In private chats (private channel or group channel) this always returns {@code true} if the author is
     * part of the chat.
     * Please notice, this does not check if a user has blocked private messages!
     * It also doesn't check if the user is even able to send any external emojis (twitch subscription or nitro).
     * Always returns {@code false} if the author is not a user.
     *
     * @return Whether the author can use external emojis in the channel or not.
     */
    default boolean canUseExternalEmojisInTextChannel() {
        return getMessage()
                .getChannel()
                .asTextChannel()
                .map(textChannel -> asUser().map(textChannel::canUseExternalEmojis).orElse(false))
                .orElseThrow(AssertionError::new);
    }

    /**
     * Checks if the author can use embed links in the channel where the message was sent.
     * In private chats (private channel or group channel) this always returns {@code true} if the author is
     * part of the chat.
     * Please notice, this does not check if a user has blocked private messages!
     * Always returns {@code false} if the author is not a user.
     *
     * @return Whether the author can embed links in the channel or not.
     */
    default boolean canEmbedLinksInTextChannel() {
        return getMessage()
                .getChannel()
                .asTextChannel()
                .map(textChannel -> asUser().map(textChannel::canEmbedLinks).orElse(false))
                .orElseThrow(AssertionError::new);
    }

    /**
     * Checks if the author can read the message history of the channel where the message was sent.
     * In private chats (private channel or group channel) this always returns {@code true} if the user is
     * part of the chat.
     * Always returns {@code false} if the author is not a user.
     *
     * @return Whether the author can read the message history of the channel or not.
     */
    default boolean canReadMessageHistoryOfTextChannel() {
        return getMessage()
                .getChannel()
                .asTextChannel()
                .map(textChannel -> asUser().map(textChannel::canReadMessageHistory).orElse(false))
                .orElseThrow(AssertionError::new);
    }

    /**
     * Checks if the author can use tts (text to speech) in the channel where the message was sent.
     * In private chats (private channel or group channel) this always returns {@code true} if the user is
     * part of the chat.
     * Please notice, this does not check if a user has blocked private messages!
     * Always returns {@code false} if the author is not a user.
     *
     * @return Whether the author can use tts in the channel or not.
     */
    default boolean canUseTtsInTextChannel() {
        return getMessage()
                .getChannel()
                .asTextChannel()
                .map(textChannel -> asUser().map(textChannel::canUseTts).orElse(false))
                .orElseThrow(AssertionError::new);
    }

    /**
     * Checks if the author can attach files in the channel where the message was sent.
     * Always returns {@code false} if the author is not a user.
     *
     * @return Whether the author can attach files in the channel or not.
     */
    default boolean canAttachFilesToTextChannel() {
        return getMessage()
                .getChannel()
                .asTextChannel()
                .map(textChannel -> asUser().map(textChannel::canAttachFiles).orElse(false))
                .orElseThrow(AssertionError::new);
    }

    /**
     * Checks if the author is allowed to add <b>new</b> reactions to messages in the channel where the message was sent.
     * Always returns {@code false} if the author is not a user.
     *
     * @return Whether the author is allowed to add <b>new</b> reactions to messages in the channel or not.
     */
    default boolean canAddNewReactionsInTextChannel() {
        return getMessage()
                .getChannel()
                .asTextChannel()
                .map(textChannel -> asUser().map(textChannel::canAddNewReactions).orElse(false))
                .orElseThrow(AssertionError::new);
    }

    /**
     * Checks if the author can manage messages (delete or pin them or remove reactions of others) in the channel where the message was sent.
     * In private chats (private channel or group channel) this always returns {@code true} if the user is
     * part of the chat.
     * Always returns {@code false} if the author is not a user.
     *
     * @return Whether the author can manage messages in the channel or not.
     */
    default boolean canManageMessagesInTextChannel() {
        return getMessage()
                .getChannel()
                .asTextChannel()
                .map(textChannel -> asUser().map(textChannel::canManageMessages).orElse(false))
                .orElseThrow(AssertionError::new);
    }

    /**
     * Checks if the author can remove reactions of other users in the channel where the message was sent.
     * In private chats (private channel or group channel) this always returns {@code true} if the user is
     * part of the chat.
     * Always returns {@code false} if the author is not a user.
     *
     * @return Whether the author can remove reactions of others in the channel or not.
     */
    default boolean canRemoveReactionsOfOthersInTextChannel() {
        return getMessage()
                .getChannel()
                .asTextChannel()
                .map(textChannel -> asUser().map(textChannel::canRemoveReactionsOfOthers).orElse(false))
                .orElseThrow(AssertionError::new);
    }

    /**
     * Checks if the author can mention everyone (@everyone) in the channel where the message was sent.
     * In private chats (private channel or group channel) this always returns {@code true} if the user is
     * part of the chat.
     * Always returns {@code false} if the author is not a user.
     *
     * @return Whether the given user can mention everyone (@everyone) or not.
     */
    default boolean canMentionEveryoneInTextChannel() {
        return getMessage()
                .getChannel()
                .asTextChannel()
                .map(textChannel -> asUser().map(textChannel::canMentionEveryone).orElse(false))
                .orElseThrow(AssertionError::new);
    }

    /**
     * Checks if the author can connect to the voice channel where the message was sent.
     * In private chats (private channel or group channel) this always returns {@code true} if the user is
     * part of the chat.
     * Always returns {@code false} if the author is not a user or if the channel is not a voice channel.
     *
     * @return Whether the author can connect to the voice channel or not.
     */
    default boolean canConnectToVoiceChannel() {
        return getMessage()
                .getChannel()
                .asVoiceChannel()
                .flatMap(voiceChannel -> asUser().map(voiceChannel::canConnect))
                .orElse(false);
    }

    /**
     * Checks if the author can mute other users in the voice channel where the message was sent.
     * In private chats (private channel or group channel) this always returns @{code false}.
     * Always returns {@code false} if the author is not a user or if the channel is not a voice channel.
     *
     * @return Whether the author can mute other users in the voice channel or not.
     */
    default boolean canMuteUsersInVoiceChannel() {
        return getMessage()
                .getChannel()
                .asVoiceChannel()
                .flatMap(voiceChannel -> asUser().map(voiceChannel::canMuteUsers))
                .orElse(false);
    }

    /**
     * Checks if the author is allowed to add <b>new</b> reactions to the message.
     * Always returns {@code false} if the author is not a user.
     *
     * @return Whether the author is allowed to add <b>new</b> reactions to the message or not.
     */
    default boolean canAddNewReactionsToMessage() {
        return asUser()
                .map(getMessage()::canAddNewReactions)
                .orElse(false);
    }

    /**
     * Checks if the author can delete the message.
     * Always returns {@code false} if the author is not a user.
     *
     * @return Whether the author can delete the message or not.
     */
    default boolean canDeleteMessage() {
        return asUser()
                .map(getMessage()::canDelete)
                .orElse(false);
    }

    /**
     * Checks if the author is an administrator of the server where the message was sent.
     * Always returns {@code false} if the author is not a user or the message was not sent on a server.
     *
     * @return Whether the author is an administrator of the server or not.
     */
    default boolean isServerAdmin() {
        return getMessage()
                .getServer()
                .flatMap(server -> asUser().map(server::isAdmin))
                .orElse(false);
    }

    /**
     * Gets the author as user.
     *
     * @return The author as user.
     */
    default Optional<User> asUser() {
        if (isUser()) {
            return getApi().getCachedUserById(getId());
        }
        return Optional.empty();
    }

    /**
     * Checks if the author is a webhook.
     *
     * @return Whether the author is a webhook or not.
     */
    boolean isWebhook();

    /**
     * Gets the author as a webhook.
     *
     * @return The author as a webhook.
     */
    default Optional<CompletableFuture<Webhook>> asWebhook() {
        if (isWebhook()) {
            return Optional.of(getApi().getWebhookById(getId()));
        }
        return Optional.empty();
    }

    /**
     * Gets if this author is the user of the connected account.
     *
     * @return Whether this author is the user of the connected account or not.
     * @see DiscordApi#getYourself()
     */
    default boolean isYourself() {
        return asUser().map(User::isYourself).orElse(false);
    }

}