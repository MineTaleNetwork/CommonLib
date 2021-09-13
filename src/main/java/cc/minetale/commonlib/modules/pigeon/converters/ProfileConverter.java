package cc.minetale.commonlib.modules.pigeon.converters;

import cc.minetale.commonlib.modules.grant.Grant;
import cc.minetale.commonlib.modules.network.Gamemode;
import cc.minetale.commonlib.modules.profile.GamemodeStorage;
import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.modules.punishment.Punishment;
import cc.minetale.pigeon.Converter;
import cc.minetale.pigeon.converters.ListConverter;
import cc.minetale.pigeon.converters.MapConverter;
import cc.minetale.pigeon.converters.StringConverter;
import cc.minetale.pigeon.converters.UUIDConverter;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

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

            var profile = new Profile();

            profile.setId(UUIDConverter.Utils.convertToValue(data.get("id")));
            profile.setPunishments(ListConverter.Utils.convertToValue(data.get("punishments"), String.class));
            profile.setCachedPunishments(ListConverter.Utils.convertToValue(data.get("cachedPunishments"), Punishment.class));
            profile.setGrants(ListConverter.Utils.convertToValue(data.get("grants"), String.class));
            profile.setCachedGrants(ListConverter.Utils.convertToValue(data.get("cachedGrants"), Grant.class));
            profile.setIgnored(ListConverter.Utils.convertToValue(data.get("ignored"), String.class));
            profile.setFriends(ListConverter.Utils.convertToValue(data.get("friends"), String.class));

            var optionsObj = data.getAsJsonObject("optionsProfile");
            var options = new Profile.Options();
            options.setReceivingPartyRequests(optionsObj.get("receivingPartyRequests").getAsBoolean());
            options.setReceivingFriendRequests(optionsObj.get("receivingFriendRequests").getAsBoolean());
            options.setReceivingPublicChat(optionsObj.get("receivingPublicChat").getAsBoolean());
            options.setReceivingConversations(optionsObj.get("receivingConversations").getAsBoolean());
            options.setReceivingMessageSounds(optionsObj.get("receivingMessageSounds").getAsBoolean());
            options.setVisibilityIndex(optionsObj.get("visibilityIndex").getAsInt());

            profile.setOptionsProfile(options);

            var staffObj = data.getAsJsonObject("staffProfile");
            var staff = new Profile.Staff();
            staff.setTwoFactorKey(StringConverter.Utils.convertToValue(staffObj.get("twoFactorKey")));
            staff.setReceivingStaffMessages(staffObj.get("receivingStaffMessages").getAsBoolean());
            staff.setTwoFactor(staffObj.get("twoFactor").getAsBoolean());
            staff.setLocked(staffObj.get("locked").getAsBoolean());
            staff.setOperator(staffObj.get("operator").getAsBoolean());

            profile.setStaffProfile(staff);

            profile.setName(StringConverter.Utils.convertToValue(data.get("name")));
            profile.setSearchableName(StringConverter.Utils.convertToValue(data.get("searchableName")));
            var currentAddress = data.get("currentAddress");
            if(currentAddress != null)
                profile.setCurrentAddress(StringConverter.Utils.convertToValue(currentAddress));

            var discordId = data.get("discordId");
            if(discordId != null)
                profile.setDiscord(StringConverter.Utils.convertToValue(discordId));

            profile.setGrant(GrantConverter.Utils.convertToValue(data.get("grant")));

            profile.setGold(data.get("gold").getAsInt());
            var firstSeen = data.get("firstSeen");
            if(firstSeen != null)
                profile.setFirstSeen(firstSeen.getAsLong());

            var lastSeen = data.get("lastSeen");
            if(lastSeen != null)
                profile.setFirstSeen(lastSeen.getAsLong());

            profile.setExperience(data.get("experience").getAsLong());

            profile.setGamemodeStorages(MapConverter.Utils.convertToValue(data.get("gamemodeStorages"), Gamemode.class, GamemodeStorage.class));

            return profile;
        }

        public static JsonElement convertToSimple(Profile value) {
            var data = new JsonObject();

            data.add("id", UUIDConverter.Utils.convertToSimple(value.getId()));
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
            optionsObj.add("visibilityIndex", new JsonPrimitive(options.getVisibilityIndex()));

            data.add("optionsProfile", optionsObj);

            var staffObj = new JsonObject();
            var staff = value.getStaffProfile();
            staffObj.add("twoFactorKey", StringConverter.Utils.convertToSimple(staff.getTwoFactorKey()));
            staffObj.add("receivingStaffMessages", new JsonPrimitive(staff.isReceivingStaffMessages()));
            staffObj.add("twoFactor", new JsonPrimitive(staff.isTwoFactor()));
            staffObj.add("locked", new JsonPrimitive(staff.isLocked()));
            staffObj.add("operator", new JsonPrimitive(staff.isOperator()));

            data.add("staffProfile", staffObj);

            data.add("name", StringConverter.Utils.convertToSimple(value.getName()));
            data.add("searchableName", StringConverter.Utils.convertToSimple(value.getSearchableName()));

            var currentAddress = value.getCurrentAddress();
            if(currentAddress != null) {
                data.add("currentAddress", StringConverter.Utils.convertToSimple(value.getCurrentAddress()));
            } else {
                data.add("currentAddress", JsonNull.INSTANCE);
            }

            var discord = value.getDiscord();
            if(discord != null) {
                data.add("discord", StringConverter.Utils.convertToSimple(value.getDiscord()));
            } else {
                data.add("discord", JsonNull.INSTANCE);
            }

            var grant = value.getGrant();
            data.add("grant", GrantConverter.Utils.convertToSimple(grant));

            data.add("gold", new JsonPrimitive(value.getGold()));

            data.add("firstSeen", new JsonPrimitive(value.getFirstSeen()));
            data.add("lastSeen", new JsonPrimitive(value.getLastSeen()));

            data.add("experience", new JsonPrimitive(value.getExperience()));

            data.add("gamemodeStorages", MapConverter.Utils.convertToSimple(value.getGamemodeStorages(), Gamemode.class, GamemodeStorage.class));

            return data;
        }
    }

}
