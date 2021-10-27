package cc.minetale.commonlib.balance;

import cc.minetale.commonlib.network.Gamemode;
import cc.minetale.commonlib.profile.GamemodeStorage;
import cc.minetale.commonlib.profile.Profile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Balance {

    public static boolean canAffordWithGold(Profile profile, int cost) {
        return profile.getGold() > cost;
    }

    public static void addGold(Profile profile, int amount) {
        profile.setGold(profile.getGold() + amount);
    }

    // TODO: New Components
//    public static TextComponent getCannotAffordGoldMessage() {
//        return ChatUtil.builderToComponent(
//                new ComponentBuilder("You don't have enough gold to purchase this item.").color(ChatColor.RED)
//        );
//    }

//    public static TextComponent getRequiredGoldMessage(int required) {
//        return ChatUtil.builderToComponent(
//                new ComponentBuilder("This item costs ").color(ChatColor.RED)
//                        .append(Currencies.CurrencyType.GOLD.getFormat().apply(new TextComponent(required + Currencies.CurrencyType.GOLD.getSuffix())))
//                        .append(".").reset().color(ChatColor.RED)
//        );
//    }

    public static int getGamemodeGold(Profile profile, @Nullable Gamemode gamemode) {
        var gamemodeGold = 0;
        if(profile.api().hasGamemodeStorage(gamemode)) {
            gamemodeGold = profile.api().getGamemodeStorage(gamemode).get("gamemodeGold").asInt();
        }
        return gamemodeGold;
    }

    public static boolean canAffordWithGamemodeGold(Profile profile, @Nullable Gamemode gamemode, int cost) {
        return getGamemodeGold(profile, gamemode) > cost;
    }

    public static void addGamemodeGold(Profile profile, @NotNull Gamemode gamemode, int amount) {
        GamemodeStorage storage;
        if(profile.api().hasGamemodeStorage(gamemode)) {
            storage = profile.api().getGamemodeStorage(gamemode);
        } else {
            storage = new GamemodeStorage(gamemode);
            profile.api().addGamemodeStorage(storage);
        }

        GamemodeStorage.StorageValue value;
        if(storage.has("gamemodeGold")) {
            value = storage.get("gamemodeGold");
            value.setValue(value.asInt() + amount);
        } else {
            value = new GamemodeStorage.StorageValue("gamemodeGold", amount, true);
            storage.set(value);
        }
    }

//    public static TextComponent getCannotAffordGameModeGoldMessage() {
//        return ChatUtil.builderToComponent(
//                new ComponentBuilder("You don't have enough gamemode gold to purchase this item.").color(ChatColor.RED)
//        );
//    }

//    public static TextComponent getRequiredGameModeGoldMessage(int required) {
//        return ChatUtil.builderToComponent(
//                new ComponentBuilder("This item costs ").color(ChatColor.RED)
//                        .append(Currencies.CurrencyType.GMGOLD.getFormat().apply(new TextComponent(required + Currencies.CurrencyType.GMGOLD.getSuffix())))
//        );
//    }

    public static boolean hasEnoughExperience(Profile profile, long experienceRequired) {
        return profile.getExperience() > experienceRequired;
    }

//    public static TextComponent getNotEnoughExperienceMessage() {
//        return ChatUtil.builderToComponent(
//                new ComponentBuilder("You don't have enough experience to purchase this item.").color(ChatColor.RED)
//        );
//    }

//    public static TextComponent getNotEnoughLevelsMessage() {
//        return ChatUtil.builderToComponent(
//                new ComponentBuilder("Your level is too low to purchase this item.").color(ChatColor.RED)
//        );
//    }
//
//    public static TextComponent getRequiredExperienceMessage(long required) {
//        return ChatUtil.builderToComponent(
//                new ComponentBuilder("This item requires at least ").color(ChatColor.RED)
//                        .append(Levels.EXP_FORMAT.apply(new TextComponent(required + Levels.EXP_SUFFIX)))
//        );
//    }
//
//    public static TextComponent getRequiredLevelMessage(int required) {
//        return ChatUtil.builderToComponent(
//                new ComponentBuilder("You need at least ").color(ChatColor.RED)
//                        .append(Levels.EXP_FORMAT.apply(new TextComponent(required + " " + Levels.getLevelAffix(required))))
//                        .append(" to be able to purchase this item").reset().color(ChatColor.RED)
//        );
//    }

//    public static TextComponent getCannotAffordMultiMessage() {
//        return ChatUtil.builderToComponent(
//                new ComponentBuilder("You don't have enough gold or gamemode gold to purchase this item.").color(ChatColor.RED)
//        );
//    }
//
//    public static TextComponent getRequiredMultiMessage(int requiredGold, int requiredGmGold) {
//        return ChatUtil.builderToComponent(
//                new ComponentBuilder("This item costs ").color(ChatColor.RED)
//                        .append(Currencies.CurrencyType.GOLD.getFormat().apply(new TextComponent(requiredGold + Currencies.CurrencyType.GOLD.getSuffix())))
//                        .append(" or ").reset().color(ChatColor.RED)
//                        .append(Currencies.CurrencyType.GMGOLD.getFormat().apply(new TextComponent(requiredGmGold + Currencies.CurrencyType.GMGOLD.getSuffix())))
//        );
//    }

}