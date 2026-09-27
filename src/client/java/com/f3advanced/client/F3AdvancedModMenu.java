package com.f3advanced.client;

import com.f3advanced.client.screen.F3AdvancedConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public final class F3AdvancedModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return F3AdvancedConfigScreen::create;
    }
}
