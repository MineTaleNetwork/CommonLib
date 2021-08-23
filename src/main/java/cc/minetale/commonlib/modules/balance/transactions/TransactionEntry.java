package cc.minetale.commonlib.modules.balance.transactions;

import cc.minetale.commonlib.modules.balance.Currencies;
import cc.minetale.commonlib.util.CollectionsUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter @AllArgsConstructor
public class TransactionEntry {

    private final String context;
    private final Map<Currencies.CurrencyType, Integer> amounts;

    public static TransactionEntry fromSimple(Map<String, Object> simple) {
        return new TransactionEntry(
                String.valueOf(simple.get("context")),
                CollectionsUtil.genericMapToType((Map<?, ?>) simple.get("amounts"), Currencies.CurrencyType.class, Integer.class));
    }

    public Map<String, Object> simplify() {
        HashMap<String, Object> simple = new HashMap<>();
        simple.put("context", context);

        Map<String, String> simpleAmounts = amounts.entrySet().stream()
                .collect(Collectors.<Map.Entry<Currencies.CurrencyType, Integer>, String, String>toMap(
                        ent -> ent.getKey().toString(),
                        ent -> ent.getValue().toString()));
        simple.put("amounts", simpleAmounts);
        return simple;
    }

}