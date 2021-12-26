package cc.minetale.commonlib.pigeon.converters;

import cc.minetale.commonlib.api.Grant;
import cc.minetale.commonlib.api.Punishment;
import cc.minetale.commonlib.api.Profile;
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
            var options = new Profile.Options();

            options.setReceivingPartyRequests(optionsObj.get("receivingPartyRequests").getAsBoolean());
            options.setReceivingFriendRequests(optionsObj.get("receivingFriendRequests").getAsBoolean());
            options.setReceivingPublicChat(optionsObj.get("receivingPublicChat").getAsBoolean());
            options.setReceivingConversations(optionsObj.get("receivingConversations").getAsBoolean());
            options.setReceivingMessageSounds(optionsObj.get("receivingMessageSounds").getAsBoolean());

            var staffObj = data.getAsJsonObject("staffProfile");
            var staff = new Profile.Staff();

            staff.setTwoFactorKey(staffObj.get("twoFactorKey") != null ? staffObj.get("twoFactorKey").getAsString() : null);
            staff.setReceivingStaffMessages(staffObj.get("receivingStaffMessages").getAsBoolean());
            staff.setLocked(staffObj.get("locked").getAsBoolean());

            var profile = new Profile();

            profile.setId(UUID.fromString(data.get("id").getAsString()));
            profile.setName(data.get("name").getAsString());
            profile.setGrant(GrantConverter.Utils.convertToValue(data.get("grant")));
            profile.setFirstSeen(data.get("firstSeen").getAsLong());
            profile.setLastSeen(data.get("lastSeen").getAsLong());
            profile.setCurrentAddress(data.get("currentAddress").getAsString());
            profile.setOptionsProfile(options);
            profile.setStaffProfile(staff);
            profile.setDiscord(data.get("discord") != null ? data.get("discord").getAsString() : null);
            profile.setGold(data.get("gold").getAsInt());
            profile.setExperience(data.get("experience").getAsLong());
            profile.setIgnored(ListConverter.Utils.convertToValue(data.get("ignored"), String.class));
            profile.setFriends(ListConverter.Utils.convertToValue(data.get("friends"), String.class));
            profile.setPunishments(ListConverter.Utils.convertToValue(data.get("punishments"), String.class));
            profile.setGrants(ListConverter.Utils.convertToValue(data.get("grants"), String.class));
            profile.setCachedPunishments(ListConverter.Utils.convertToValue(data.get("cachedPunishments"), Punishment.class));
            profile.setCachedGrants(ListConverter.Utils.convertToValue(data.get("cachedGrants"), Grant.class));

            return profile;
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
