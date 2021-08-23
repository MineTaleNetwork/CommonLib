package cc.minetale.commonlib.modules.balance.transactions;

import cc.minetale.commonlib.modules.balance.Currencies;
import cc.minetale.commonlib.modules.network.Gamemode;
import cc.minetale.commonlib.modules.profile.Profile;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter @Setter
public abstract class AbstractTransactionContext {

    private final UUID playerUUID;
    private final Gamemode gamemode;
    private final List<TransactionEntry> entries;
    private final Map<String, String> data;
    private long timestamp;

    public AbstractTransactionContext(UUID playerUUID, Gamemode gamemode, List<TransactionEntry> entries) {
        this.playerUUID = playerUUID;
        this.gamemode = gamemode;
        this.entries = entries;

        this.data = new HashMap<>();
    }

    public Transaction generateTransaction() {
        return new Transaction(null, playerUUID, entries, timestamp, data);
    }

    public Map<Currencies.CurrencyType, Integer> getTotals() {
        Map<Currencies.CurrencyType, Integer> totals = new HashMap<>();
        for (TransactionEntry entry : entries) {
            for (Map.Entry<Currencies.CurrencyType, Integer> amountsEnt : entry.getAmounts().entrySet()) {
                totals.put(amountsEnt.getKey(), totals.getOrDefault(amountsEnt.getKey(), 0) + amountsEnt.getValue());
            }
        }
        return totals;
    }

    public CompletableFuture<Boolean> process(boolean needsEnoughBalance, boolean logTransaction) {
        var future = new CompletableFuture<Boolean>();

        Profile.getProfile(playerUUID).thenAccept(profile -> {
            Map<Currencies.CurrencyType, Integer> totals = getTotals();
            if (needsEnoughBalance) {
                for (Map.Entry<Currencies.CurrencyType, Integer> ent : totals.entrySet()) {
                    if (!ent.getKey().canAfford(profile, this.gamemode, ent.getValue())) {
                        future.complete(false);
                    }
                }
            }

            processPayment(profile);
            if (logTransaction) {
                generateTransaction().save();
            }

            future.complete(true);
        });

        return future;
    }

    private void processPayment(Profile profile) {
        for (TransactionEntry entry : entries) {
            for (Map.Entry<Currencies.CurrencyType, Integer> amountEnt : entry.getAmounts().entrySet()) {
                amountEnt.getKey().add(profile, gamemode, amountEnt.getValue());
            }
        }
    }

}
