package com.hyww40k;

import com.hyww40k.network.Network;
import net.minecraftforge.fml.common.Mod;

@Mod("hyw_w40k_bridge")
public class HywW40kBridge {
    public HywW40kBridge() {
        Network.register();
    }
}
