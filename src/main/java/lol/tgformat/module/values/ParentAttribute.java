package lol.tgformat.module.values;

import lol.tgformat.module.values.impl.BooleanSetting;
import lombok.Getter;

import java.util.function.Predicate;

public class ParentAttribute<T extends Setting> {

    public final static Predicate<BooleanSetting> BOOLEAN_CONDITION = BooleanSetting::isEnabled;

    @Getter
    private final T parent;
    private final Predicate<T> condition;

    public ParentAttribute(T parent, Predicate<T> condition) {
        this.parent = parent;
        this.condition = condition;
    }

    public boolean isValid() {
        return condition.test(parent) && parent.getParents().stream().allMatch(ParentAttribute::isValid);
    }

}
