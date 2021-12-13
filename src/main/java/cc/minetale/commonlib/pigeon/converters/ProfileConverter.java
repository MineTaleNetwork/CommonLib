package cc.minetale.commonlib.pigeon.converters;

import cc.minetale.commonlib.api.Grant;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.api.Punishment;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.converters.ListConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.UUID;

public class ProfileConverter extends Converter<Profile> {

    public ProfileConverter() { super(Profile.class, true, false); }

    @Override
    public Profile convertToValue(Type fieldType, JsonElement element) {
        return Utils.convertToValue(element);
    }

    @Override
    public JsonElement convertToSimple(Type fieldType, Profile value) {
        return Utils.convertToSimple(value);
    }

    public static class Utils {

        public static Profile convertToValue(JsonElement element) {
            if(!element.isJsonObject()) { return null; }
            var data = element.getAsJsonObject();

            var optionsObj = data.getAsJsonObject("optionsProfile");
            var options = Profile.Options.builder()
                    .receivingPartyRequests(optionsObj.get("receivingPartyRequests").getAsBoolean())
                    .receivingFriendRequests(optionsObj.get("receivingFriendRequests").getAsBoolean())
                    .receivingPublicChat(optionsObj.get("receivingPublicChat").getAsBoolean())
                    .receivingConversations(optionsObj.get("receivingConversations").getAsBoolean())
                    .receivingMessageSounds(optionsObj.get("receivingMessageSounds").getAsBoolean());

            var staffObj = data.getAsJsonObject("staffProfile");
            var staff = Profile.Staff.builder()
                    .twoFactorKey(staffObj.get("twoFactorKey") != null ? staffObj.get("twoFactorKey").getAsString() : null)
                    .receivingStaffMessages(staffObj.get("receivingStaffMessages").getAsBoolean())
                    .locked(staffObj.get("locked").getAsBoolean());

            var profile = Profile.builder()
                    .id(UUID.fromString(data.get("id").getAsString()))
                    .name(data.get("name").getAsString())
                    .grant(GrantConverter.Utils.convertToValue(data.get("grant")))
                    .currentAddress(data.get("currentAddress").getAsString())
                    .firstSeen(data.get("firstSeen").getAsLong())
                    .lastSeen(data.get("lastSeen").getAsLong())
                    .discord(data.get("discord") != null ? data.get("discord").getAsString() : null)
                    .gold(data.get("gold").getAsInt())
                    .experience(data.get("experience").getAsLong())
                    .ignored(ListConverter.Utils.convertToValue(data.get("ignored"), String.class))
                    .friends(ListConverter.Utils.convertToValue(data.get("friends"), String.class))
                    .punishments(ListConverter.Utils.convertToValue(data.get("punishments"), String.class))
                    .cachedPunishments(ListConverter.Utils.convertToValue(data.get("cachedPunishments"), Punishment.class))
                    .grants(ListConverter.Utils.convertToValue(data.get("grants"), String.class))
                    .cachedGrants(ListConverter.Utils.convertToValue(data.get("cachedGrants"), Grant.class))
                    .optionsProfile(options.build())
                    .staffProfile(staff.build());

            return profile.build();
        }

        public static JsonElement convertToSimple(Profile value) {
            var data = new JsonObject();

            data.add("id", new JsonPrimitive(value.getId().toString()));
            data.add("name", new JsonPrimitive(value.getName()));
            data.add("grant", GrantConverter.Utils.convertToSimple(value.getGrant()));

            data.add("currentAddress", new JsonPrimitive(value.getCurrentAddress()));
            data.add("firstSeen", new JsonPrimitive(value.getFirstSeen()));
            data.add("lastSeen", new JsonPrimitive(value.getLastSeen()));
            data.add("discord", value.getDiscord() != null ? new JsonPrimitive(value.getDiscord()) : JsonNull.INSTANCE);
            data.add("gold", new JsonPrimitive(value.getGold()));
            data.add("experience", new JsonPrimitive(value.getExperience()));

            data.add("punishments", ListConverter.Utils.convertToSimple(value.getPunishments(), String.class));
            data.add("cachedPunishments", ListConverter.Utils.convertToSimple(value.getCachedPunishments(), Punishment.class));
            data.add("grants", ListConverter.Utils.convertToSimple(value.getGrants(), String.class));
            data.add("cachedGrants", ListConverter.Utils.convertToSimple(value.getCachedGrants(), Grant.class));
            data.add("ignored", ListConverter.Utils.convertToSimple(value.getIgnored(), String.class));
            data.add("friends", ListConverter.Utils.convertToSimple(value.getFriends(), String.class));

            var optionsObj = new JsonObject();
            var options = value.getOptionsProfile();
            optionsObj.add("receivingPartyRequests", new JsonPrimitive(options.isReceivingPartyRequests()));
            optionsObj.add("receivingFriendRequests", new JsonPrimitive(options.isReceivingFriendRequests()));
            optionsObj.add("receivingPublicChat", new JsonPrimitive(options.isReceivingPublicChat()));
            optionsObj.add("receivingConversations", new JsonPrimitive(options.isReceivingConversations()));
            optionsObj.add("receivingMessageSounds", new JsonPrimitive(options.isReceivingMessageSounds()));

            data.add("optionsProfile", optionsObj);

            var staffObj = new JsonObject();
            var staff = value.getStaffProfile();
            staffObj.add("twoFactorKey", staff.getTwoFactorKey() != null ? new JsonPrimitive(staff.getTwoFactorKey()) : JsonNull.INSTANCE);
            staffObj.add("receivingStaffMessages", new JsonPrimitive(staff.isReceivingStaffMessages()));
            staffObj.add("locked", new JsonPrimitive(staff.isLocked()));

            data.add("staffProfile", staffObj);

            return data;
        }

    }

}
