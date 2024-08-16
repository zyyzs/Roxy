package net.minecraft.client;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.impl.misc.ForgeSpoof;

import java.util.List;

public class ClientBrandRetriever {
    public static String getClientModName() {
        ForgeSpoof fakeForge = ModuleManager.getModule(ForgeSpoof.class);
        return fakeForge.isState() ? ClientBrandRetriever.getModName() : "vanilla";
    }

    private static String getModName() {
        List<String> modNames = Lists.newArrayListWithExpectedSize(3);
        modNames.add("fml");
        modNames.add("forge");
        return Joiner.on(',').join(modNames);
    }
}
